/*
 * Copyright 2015 akashche@redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.akashche;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.impl.DefaultDataConnectionConfiguration;
import org.apache.ftpserver.impl.PassivePorts;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;

/**
 * User: alexkasko
 * Date: 11/11/15
 */
public class TestFTPServer {
    public static void main(String[] args) throws Exception {
        if (1 != args.length) {
            System.err.println("Port number must be specified as an only argument");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        FtpServerFactory serverFactory = new FtpServerFactory();
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(true);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
        ListenerFactory factory = new ListenerFactory();
        String activeLocalAddress = InetAddress.getLocalHost().getHostAddress();
        DataConnectionConfiguration dcc = factory.getDataConnectionConfiguration();
        DefaultDataConnectionConfiguration ndcc = new DefaultDataConnectionConfiguration(dcc.getIdleTime(),
                dcc.getSslConfiguration(), dcc.isActiveEnabled(), dcc.isActiveIpCheck(), activeLocalAddress,
                dcc.getActiveLocalPort(), dcc.getPassiveAddress(), new PassivePorts(dcc.getPassivePorts(), false),
                dcc.getPassiveExernalAddress(), dcc.isImplicitSsl());
        factory.setDataConnectionConfiguration(ndcc);
        BaseUser user = new BaseUser();
        user.setName("anonymous");
        File dir = codeSourceDir(TestFTPServer.class);
        user.setHomeDirectory(dir.getAbsolutePath());
        serverFactory.getUserManager().save(user);
        factory.setPort(port);
        serverFactory.addListener("default", factory.createListener());
        FtpServer server = serverFactory.createServer();
        server.start();
        System.out.println("Started FTP server on port: [" + port + "]");
        for (;;) {
            Thread.sleep(30 * 1000);
            logHeartbeat();
        }
    }

    // http://en.wikipedia.org/wiki/Heart_sounds
    private static void logHeartbeat() {
        System.out.println("lub-dub");
    }

    private static File codeSourceDir(Class<?> clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            File jarOrDir = new File(uri);
            return jarOrDir.isDirectory() ? jarOrDir : jarOrDir.getParentFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
