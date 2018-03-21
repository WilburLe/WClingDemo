/*
 * Copyright (C) 2014 Kevin Shen
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
package com.wilbur.clingdemo;

import com.wilbur.clingdemo.dms.JettyResourceServer;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application extends android.app.Application {
    private static Application sBeyondApplication = null;
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private JettyResourceServer mJettyResourceServer;

    private static InetAddress inetAddress;

    private static String hostAddress;

    private static String hostName;

    @Override
    public void onCreate() {
        super.onCreate();

        sBeyondApplication = this;

        mJettyResourceServer = new JettyResourceServer();
        mThreadPool.execute(mJettyResourceServer);
    }

    public static void setLocalIpAddress(InetAddress inetAddr) {
        inetAddress = inetAddr;

    }

    public static InetAddress getLocalIpAddress() {
        return inetAddress;
    }

    public static String getHostAddress() {
        return hostAddress;
    }

    public static void setHostAddress(String hostAddress) {
        Application.hostAddress = hostAddress;
    }

    public static String getHostName() {
        return hostName;
    }

    public static void setHostName(String hostName) {
        Application.hostName = hostName;
    }

    synchronized public static Application getApplication() {
        return sBeyondApplication;
    }

    synchronized public void stopServer() {
        mJettyResourceServer.stopIfRunning();
    }
}
