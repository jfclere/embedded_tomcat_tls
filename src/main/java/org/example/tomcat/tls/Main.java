/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.tomcat.tls;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.LifecycleException;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws LifecycleException, IOException {

        try {
            Tomcat tomcat = new Tomcat();
            Connector connector = new Connector();

            // TLS connector.
            SSLHostConfig sslHostConfig = new SSLHostConfig();
            sslHostConfig.setCertificateKeyAlias("tomcat");
            sslHostConfig.setCertificateKeystoreFile("../tomcat.key");
            sslHostConfig.setCertificateKeystorePassword("changeit");
            sslHostConfig.setCertificateKeyPassword("changeit");
            System.out.println("SSLHostConfig: " + sslHostConfig);

            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(8443);
            AbstractHttp11Protocol handler = (AbstractHttp11Protocol) connector.getProtocolHandler();
            System.out.println("ProtocolHandler: " + handler);
            handler.setSSLEnabled(true);
            connector.addSslHostConfig(sslHostConfig);
            tomcat.getService().addConnector(connector);


            // Server
            File base = new File(System.getProperty("java.io.tmpdir"));
            Context context = tomcat.addContext("", base.getAbsolutePath());

            // Add servlet
            tomcat.addServlet(context, "MyServlet", new MyServlet());
            context.addServletMappingDecoded("/*", "MyServlet");

            // Start Tomcat
            tomcat.start();

            // Wait until we stop Tomcat
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
