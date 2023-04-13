package com.android.internal.p028os;

import android.app.LoadedApk;
import android.app.ZygotePreload;
import android.content.ComponentName;
import android.content.p001pm.ApplicationInfo;
import android.net.LocalSocket;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
/* renamed from: com.android.internal.os.AppZygoteInit */
/* loaded from: classes4.dex */
class AppZygoteInit {
    public static final String TAG = "AppZygoteInit";
    private static ZygoteServer sServer;

    AppZygoteInit() {
    }

    /* renamed from: com.android.internal.os.AppZygoteInit$AppZygoteServer */
    /* loaded from: classes4.dex */
    private static class AppZygoteServer extends ZygoteServer {
        private AppZygoteServer() {
        }

        @Override // com.android.internal.p028os.ZygoteServer
        protected ZygoteConnection createNewConnection(LocalSocket socket, String abiList) throws IOException {
            return new AppZygoteConnection(socket, abiList);
        }
    }

    /* renamed from: com.android.internal.os.AppZygoteInit$AppZygoteConnection */
    /* loaded from: classes4.dex */
    private static class AppZygoteConnection extends ZygoteConnection {
        AppZygoteConnection(LocalSocket socket, String abiList) throws IOException {
            super(socket, abiList);
        }

        @Override // com.android.internal.p028os.ZygoteConnection
        protected void preload() {
        }

        @Override // com.android.internal.p028os.ZygoteConnection
        protected boolean isPreloadComplete() {
            return true;
        }

        @Override // com.android.internal.p028os.ZygoteConnection
        protected boolean canPreloadApp() {
            return true;
        }

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:10:0x008a -> B:22:0x00a8). Please submit an issue!!! */
        @Override // com.android.internal.p028os.ZygoteConnection
        protected void handlePreloadApp(ApplicationInfo appInfo) {
            Log.m108i(AppZygoteInit.TAG, "Beginning application preload for " + appInfo.packageName);
            LoadedApk loadedApk = new LoadedApk(null, appInfo, null, null, false, true, false);
            ClassLoader loader = loadedApk.getClassLoader();
            Zygote.allowAppFilesAcrossFork(appInfo);
            int i = 1;
            if (appInfo.zygotePreloadName != null) {
                try {
                    ComponentName preloadName = ComponentName.createRelative(appInfo.packageName, appInfo.zygotePreloadName);
                    Class<?> cl = Class.forName(preloadName.getClassName(), true, loader);
                    if (!ZygotePreload.class.isAssignableFrom(cl)) {
                        Log.m110e(AppZygoteInit.TAG, preloadName.getClassName() + " does not implement " + ZygotePreload.class.getName());
                    } else {
                        Constructor<?> ctor = cl.getConstructor(new Class[0]);
                        ZygotePreload preloadObject = (ZygotePreload) ctor.newInstance(new Object[0]);
                        Zygote.markOpenedFilesBeforePreload();
                        preloadObject.doPreload(appInfo);
                        Zygote.allowFilesOpenedByPreload();
                    }
                } catch (ReflectiveOperationException e) {
                    Log.m109e(AppZygoteInit.TAG, "AppZygote application preload failed for " + appInfo.zygotePreloadName, e);
                }
            } else {
                Log.m108i(AppZygoteInit.TAG, "No zygotePreloadName attribute specified.");
            }
            try {
                DataOutputStream socketOut = getSocketOutputStream();
                if (loader == null) {
                    i = 0;
                }
                socketOut.writeInt(i);
                Log.m108i(AppZygoteInit.TAG, "Application preload done");
            } catch (IOException e2) {
                throw new IllegalStateException("Error writing to command socket", e2);
            }
        }
    }

    public static void main(String[] argv) {
        AppZygoteServer server = new AppZygoteServer();
        ChildZygoteInit.runZygoteServer(server, argv);
    }
}
