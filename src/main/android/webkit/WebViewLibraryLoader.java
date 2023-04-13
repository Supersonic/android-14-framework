package android.webkit;

import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.LoadedApk;
import android.content.p001pm.PackageInfo;
import android.content.res.CompatibilityInfo;
import android.p008os.Build;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.server.LocalServices;
import dalvik.system.VMRuntime;
import java.util.Arrays;
/* loaded from: classes4.dex */
public class WebViewLibraryLoader {
    private static final String CHROMIUM_WEBVIEW_NATIVE_RELRO_32 = "/data/misc/shared_relro/libwebviewchromium32.relro";
    private static final String CHROMIUM_WEBVIEW_NATIVE_RELRO_64 = "/data/misc/shared_relro/libwebviewchromium64.relro";
    private static final boolean DEBUG = false;
    private static final String LOGTAG = WebViewLibraryLoader.class.getSimpleName();
    private static boolean sAddressSpaceReserved = false;

    static native boolean nativeCreateRelroFile(String str, String str2, ClassLoader classLoader);

    static native int nativeLoadWithRelroFile(String str, String str2, ClassLoader classLoader);

    static native boolean nativeReserveAddressSpace(long j);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class RelroFileCreator {
        private RelroFileCreator() {
        }

        public static void main(String[] args) {
            boolean is64Bit = VMRuntime.getRuntime().is64Bit();
            try {
                if (args.length == 2 && args[0] != null && args[1] != null) {
                    String packageName = args[0];
                    String libraryFileName = args[1];
                    Log.m106v(WebViewLibraryLoader.LOGTAG, "RelroFileCreator (64bit = " + is64Bit + "), package: " + packageName + " library: " + libraryFileName);
                    if (!WebViewLibraryLoader.sAddressSpaceReserved) {
                        Log.m110e(WebViewLibraryLoader.LOGTAG, "can't create relro file; address space not reserved");
                        return;
                    }
                    LoadedApk apk = ActivityThread.currentActivityThread().getPackageInfo(packageName, (CompatibilityInfo) null, 3);
                    boolean result = WebViewLibraryLoader.nativeCreateRelroFile(libraryFileName, is64Bit ? WebViewLibraryLoader.CHROMIUM_WEBVIEW_NATIVE_RELRO_64 : WebViewLibraryLoader.CHROMIUM_WEBVIEW_NATIVE_RELRO_32, apk.getClassLoader());
                    try {
                        WebViewFactory.getUpdateServiceUnchecked().notifyRelroCreationCompleted();
                    } catch (RemoteException e) {
                        Log.m109e(WebViewLibraryLoader.LOGTAG, "error notifying update service", e);
                    }
                    if (!result) {
                        Log.m110e(WebViewLibraryLoader.LOGTAG, "failed to create relro file");
                    }
                    System.exit(0);
                    return;
                }
                Log.m110e(WebViewLibraryLoader.LOGTAG, "Invalid RelroFileCreator args: " + Arrays.toString(args));
                try {
                    WebViewFactory.getUpdateServiceUnchecked().notifyRelroCreationCompleted();
                } catch (RemoteException e2) {
                    Log.m109e(WebViewLibraryLoader.LOGTAG, "error notifying update service", e2);
                }
                if (0 == 0) {
                    Log.m110e(WebViewLibraryLoader.LOGTAG, "failed to create relro file");
                }
                System.exit(0);
            } finally {
                try {
                    WebViewFactory.getUpdateServiceUnchecked().notifyRelroCreationCompleted();
                } catch (RemoteException e3) {
                    Log.m109e(WebViewLibraryLoader.LOGTAG, "error notifying update service", e3);
                }
                if (0 == 0) {
                    Log.m110e(WebViewLibraryLoader.LOGTAG, "failed to create relro file");
                }
                System.exit(0);
            }
        }
    }

    static void createRelroFile(boolean is64Bit, String packageName, String libraryFileName) {
        final String abi = is64Bit ? Build.SUPPORTED_64_BIT_ABIS[0] : Build.SUPPORTED_32_BIT_ABIS[0];
        Runnable crashHandler = new Runnable() { // from class: android.webkit.WebViewLibraryLoader.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Log.m110e(WebViewLibraryLoader.LOGTAG, "relro file creator for " + abi + " crashed. Proceeding without");
                    WebViewFactory.getUpdateService().notifyRelroCreationCompleted();
                } catch (RemoteException e) {
                    Log.m110e(WebViewLibraryLoader.LOGTAG, "Cannot reach WebViewUpdateService. " + e.getMessage());
                }
            }
        };
        try {
            boolean success = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).startIsolatedProcess(RelroFileCreator.class.getName(), new String[]{packageName, libraryFileName}, "WebViewLoader-" + abi, abi, 1037, crashHandler);
            if (!success) {
                throw new Exception("Failed to start the relro file creator process");
            }
        } catch (Throwable t) {
            Log.m109e(LOGTAG, "error starting relro file creator for abi " + abi, t);
            crashHandler.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int prepareNativeLibraries(PackageInfo webViewPackageInfo) {
        String libraryFileName = WebViewFactory.getWebViewLibrary(webViewPackageInfo.applicationInfo);
        if (libraryFileName == null) {
            return 0;
        }
        return createRelros(webViewPackageInfo.packageName, libraryFileName);
    }

    private static int createRelros(String packageName, String libraryFileName) {
        int numRelros = 0;
        if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
            createRelroFile(false, packageName, libraryFileName);
            numRelros = 0 + 1;
        }
        if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
            createRelroFile(true, packageName, libraryFileName);
            return numRelros + 1;
        }
        return numRelros;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void reserveAddressSpaceInZygote() {
        long addressSpaceToReserve;
        System.loadLibrary("webviewchromium_loader");
        if (VMRuntime.getRuntime().is64Bit()) {
            addressSpaceToReserve = 1073741824;
        } else if (VMRuntime.getRuntime().vmInstructionSet().equals("arm")) {
            addressSpaceToReserve = 136314880;
        } else {
            addressSpaceToReserve = 199229440;
        }
        boolean nativeReserveAddressSpace = nativeReserveAddressSpace(addressSpaceToReserve);
        sAddressSpaceReserved = nativeReserveAddressSpace;
        if (!nativeReserveAddressSpace) {
            Log.m110e(LOGTAG, "reserving " + addressSpaceToReserve + " bytes of address space failed");
        }
    }

    public static int loadNativeLibrary(ClassLoader clazzLoader, String libraryFileName) {
        if (!sAddressSpaceReserved) {
            Log.m110e(LOGTAG, "can't load with relro file; address space not reserved");
            return 2;
        }
        String relroPath = VMRuntime.getRuntime().is64Bit() ? CHROMIUM_WEBVIEW_NATIVE_RELRO_64 : CHROMIUM_WEBVIEW_NATIVE_RELRO_32;
        int result = nativeLoadWithRelroFile(libraryFileName, relroPath, clazzLoader);
        if (result != 0) {
            Log.m104w(LOGTAG, "failed to load with relro file, proceeding without");
        }
        return result;
    }
}
