package com.android.internal.p028os;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.ProcessInfo;
import android.media.MediaMetrics;
import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.p008os.Build;
import android.p008os.FactoryTest;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SystemProperties;
import android.p008os.Trace;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.net.NetworkUtilsInternal;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import dalvik.system.ZygoteHooks;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import libcore.io.IoUtils;
/* renamed from: com.android.internal.os.Zygote */
/* loaded from: classes4.dex */
public final class Zygote {
    public static final String ALLOWLISTED_DATA_INFO_MAP = "--allowlisted-data-info-map";
    private static final String ANDROID_SOCKET_PREFIX = "ANDROID_SOCKET_";
    public static final int API_ENFORCEMENT_POLICY_MASK = 12288;
    public static final String BIND_MOUNT_APP_DATA_DIRS = "--bind-mount-data-dirs";
    public static final String BIND_MOUNT_APP_STORAGE_DIRS = "--bind-mount-storage-dirs";
    public static final String CHILD_ZYGOTE_ABI_LIST_ARG = "--abi-list=";
    public static final String CHILD_ZYGOTE_SOCKET_NAME_ARG = "--zygote-socket=";
    public static final String CHILD_ZYGOTE_UID_RANGE_END = "--uid-range-end=";
    public static final String CHILD_ZYGOTE_UID_RANGE_START = "--uid-range-start=";
    public static final int DEBUG_ALWAYS_JIT = 64;
    public static final int DEBUG_ENABLE_ASSERT = 4;
    public static final int DEBUG_ENABLE_CHECKJNI = 2;
    public static final int DEBUG_ENABLE_JDWP = 1;
    public static final int DEBUG_ENABLE_JNI_LOGGING = 16;
    public static final int DEBUG_ENABLE_SAFEMODE = 8;
    public static final int DEBUG_GENERATE_DEBUG_INFO = 32;
    public static final int DEBUG_GENERATE_MINI_DEBUG_INFO = 2048;
    public static final int DEBUG_IGNORE_APP_SIGNAL_HANDLER = 131072;
    public static final int DEBUG_JAVA_DEBUGGABLE = 256;
    public static final int DEBUG_NATIVE_DEBUGGABLE = 128;
    public static final int DISABLE_TEST_API_ENFORCEMENT_POLICY = 262144;
    public static final int DISABLE_VERIFIER = 512;
    private static final long GWP_ASAN = 135634846;
    public static final int GWP_ASAN_LEVEL_ALWAYS = 4194304;
    public static final int GWP_ASAN_LEVEL_DEFAULT = 6291456;
    public static final int GWP_ASAN_LEVEL_LOTTERY = 2097152;
    public static final int GWP_ASAN_LEVEL_MASK = 6291456;
    public static final int GWP_ASAN_LEVEL_NEVER = 0;
    public static final int MEMORY_TAG_LEVEL_ASYNC = 1048576;
    public static final int MEMORY_TAG_LEVEL_MASK = 1572864;
    public static final int MEMORY_TAG_LEVEL_NONE = 0;
    public static final int MEMORY_TAG_LEVEL_SYNC = 1572864;
    public static final int MEMORY_TAG_LEVEL_TBI = 524288;
    public static final int MOUNT_EXTERNAL_ANDROID_WRITABLE = 4;
    public static final int MOUNT_EXTERNAL_DEFAULT = 1;
    public static final int MOUNT_EXTERNAL_INSTALLER = 2;
    public static final int MOUNT_EXTERNAL_NONE = 0;
    public static final int MOUNT_EXTERNAL_PASS_THROUGH = 3;
    private static final long NATIVE_HEAP_POINTER_TAGGING = 135754954;
    private static final long NATIVE_HEAP_POINTER_TAGGING_SECONDARY_ZYGOTE = 207557677;
    private static final long NATIVE_HEAP_ZERO_INIT = 178038272;
    public static final int NATIVE_HEAP_ZERO_INIT_ENABLED = 8388608;
    private static final long NATIVE_MEMTAG_ASYNC = 135772972;
    private static final long NATIVE_MEMTAG_SYNC = 177438394;
    public static final int ONLY_USE_SYSTEM_OAT_FILES = 1024;
    public static final String PKG_DATA_INFO_MAP = "--pkg-data-info-map";
    public static final String PRIMARY_SOCKET_NAME = "zygote";
    private static final int PRIORITY_MAX = -20;
    public static final int PROFILEABLE = 16777216;
    public static final int PROFILE_FROM_SHELL = 32768;
    public static final int PROFILE_SYSTEM_SERVER = 16384;
    public static final long PROPERTY_CHECK_INTERVAL = 60000;
    public static final String SECONDARY_SOCKET_NAME = "zygote_secondary";
    public static final int SOCKET_BUFFER_SIZE = 256;
    public static final String START_AS_TOP_APP_ARG = "--is-top-app";
    private static final String TAG = "Zygote";
    private static final String USAP_ERROR_PREFIX = "Invalid command to USAP: ";
    static final int USAP_MANAGEMENT_MESSAGE_BYTES = 8;
    public static final String USAP_POOL_PRIMARY_SOCKET_NAME = "usap_pool_primary";
    public static final String USAP_POOL_SECONDARY_SOCKET_NAME = "usap_pool_secondary";
    public static final int USE_APP_IMAGE_STARTUP_CACHE = 65536;
    public static final int API_ENFORCEMENT_POLICY_SHIFT = Integer.numberOfTrailingZeros(12288);
    static final int[][] INT_ARRAY_2D = (int[][]) Array.newInstance(Integer.TYPE, 0, 0);
    private static final boolean ENABLE_JDWP = SystemProperties.get("persist.debug.dalvik.vm.jdwp.enabled").equals("1");

    @CriticalNative
    private static native void nativeAddUsapTableEntry(int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public static native void nativeAllowFileAcrossFork(String str);

    private static native void nativeAllowFilesOpenedByPreload();

    private static native void nativeBlockSigTerm();

    private static native void nativeBoostUsapPriority();

    public static native int nativeCurrentTaggingLevel();

    private static native void nativeEmptyUsapPool();

    private static native int nativeForkAndSpecialize(int i, int i2, int[] iArr, int i3, int[][] iArr2, int i4, String str, String str2, int[] iArr3, int[] iArr4, boolean z, String str3, String str4, boolean z2, String[] strArr, String[] strArr2, boolean z3, boolean z4);

    private static native int nativeForkApp(int i, int i2, int[] iArr, boolean z, boolean z2);

    private static native int nativeForkSystemServer(int i, int i2, int[] iArr, int i3, int[][] iArr2, long j, long j2);

    private static native int[] nativeGetUsapPipeFDs();

    private static native int nativeGetUsapPoolCount();

    private static native int nativeGetUsapPoolEventFD();

    protected static native void nativeInitNativeState(boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public static native void nativeInstallSeccompUidGidFilter(int i, int i2);

    private static native void nativeMarkOpenedFilesBeforePreload();

    @FastNative
    public static native int nativeParseSigChld(byte[] bArr, int i, int[] iArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nativePreApplicationInit();

    @CriticalNative
    private static native boolean nativeRemoveUsapTableEntry(int i);

    private static native void nativeSpecializeAppProcess(int i, int i2, int[] iArr, int i3, int[][] iArr2, int i4, String str, String str2, boolean z, String str3, String str4, boolean z2, String[] strArr, String[] strArr2, boolean z3, boolean z4);

    public static native boolean nativeSupportsMemoryTagging();

    public static native boolean nativeSupportsTaggedPointers();

    private static native void nativeUnblockSigTerm();

    private Zygote() {
    }

    private static boolean containsInetGid(int[] gids) {
        for (int i : gids) {
            if (i == 3003) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int forkAndSpecialize(int uid, int gid, int[] gids, int runtimeFlags, int[][] rlimits, int mountExternal, String seInfo, String niceName, int[] fdsToClose, int[] fdsToIgnore, boolean startChildZygote, String instructionSet, String appDataDir, boolean isTopApp, String[] pkgDataInfoList, String[] allowlistedDataInfoList, boolean bindMountAppDataDirs, boolean bindMountAppStorageDirs) {
        ZygoteHooks.preFork();
        int pid = nativeForkAndSpecialize(uid, gid, gids, runtimeFlags, rlimits, mountExternal, seInfo, niceName, fdsToClose, fdsToIgnore, startChildZygote, instructionSet, appDataDir, isTopApp, pkgDataInfoList, allowlistedDataInfoList, bindMountAppDataDirs, bindMountAppStorageDirs);
        if (pid == 0) {
            Trace.traceBegin(64L, "PostFork");
            if (gids != null && gids.length > 0) {
                NetworkUtilsInternal.setAllowNetworkingForProcess(containsInetGid(gids));
            }
        }
        Thread.currentThread().setPriority(5);
        ZygoteHooks.postForkCommon();
        return pid;
    }

    private static void specializeAppProcess(int uid, int gid, int[] gids, int runtimeFlags, int[][] rlimits, int mountExternal, String seInfo, String niceName, boolean startChildZygote, String instructionSet, String appDataDir, boolean isTopApp, String[] pkgDataInfoList, String[] allowlistedDataInfoList, boolean bindMountAppDataDirs, boolean bindMountAppStorageDirs) {
        nativeSpecializeAppProcess(uid, gid, gids, runtimeFlags, rlimits, mountExternal, seInfo, niceName, startChildZygote, instructionSet, appDataDir, isTopApp, pkgDataInfoList, allowlistedDataInfoList, bindMountAppDataDirs, bindMountAppStorageDirs);
        Trace.traceBegin(64L, "PostFork");
        if (gids != null && gids.length > 0) {
            NetworkUtilsInternal.setAllowNetworkingForProcess(containsInetGid(gids));
        }
        Thread.currentThread().setPriority(5);
        ZygoteHooks.postForkCommon();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int forkSystemServer(int uid, int gid, int[] gids, int runtimeFlags, int[][] rlimits, long permittedCapabilities, long effectiveCapabilities) {
        ZygoteHooks.preFork();
        int pid = nativeForkSystemServer(uid, gid, gids, runtimeFlags, rlimits, permittedCapabilities, effectiveCapabilities);
        Thread.currentThread().setPriority(5);
        ZygoteHooks.postForkCommon();
        return pid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void allowAppFilesAcrossFork(ApplicationInfo appInfo) {
        String[] allApkPaths;
        for (String path : appInfo.getAllApkPaths()) {
            nativeAllowFileAcrossFork(path);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void markOpenedFilesBeforePreload() {
        nativeMarkOpenedFilesBeforePreload();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void allowFilesOpenedByPreload() {
        nativeAllowFilesOpenedByPreload();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void initNativeState(boolean isPrimary) {
        nativeInitNativeState(isPrimary);
    }

    public static String getConfigurationProperty(String propertyName, String defaultValue) {
        return SystemProperties.get(String.join(MediaMetrics.SEPARATOR, ZygoteConfig.PROPERTY_PREFIX_DEVICE_CONFIG, "runtime_native", propertyName), defaultValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void emptyUsapPool() {
        nativeEmptyUsapPool();
    }

    public static boolean getConfigurationPropertyBoolean(String propertyName, Boolean defaultValue) {
        return SystemProperties.getBoolean(String.join(MediaMetrics.SEPARATOR, ZygoteConfig.PROPERTY_PREFIX_DEVICE_CONFIG, "runtime_native", propertyName), defaultValue.booleanValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getUsapPoolCount() {
        return nativeGetUsapPoolCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FileDescriptor getUsapPoolEventFD() {
        FileDescriptor fd = new FileDescriptor();
        fd.setInt$(nativeGetUsapPoolEventFD());
        return fd;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Runnable forkUsap(LocalServerSocket usapPoolSocket, int[] sessionSocketRawFDs, boolean isPriorityFork) {
        try {
            FileDescriptor[] pipeFDs = Os.pipe2(OsConstants.O_CLOEXEC);
            FileDescriptor readFD = pipeFDs[0];
            FileDescriptor writeFD = pipeFDs[1];
            int pid = nativeForkApp(readFD.getInt$(), writeFD.getInt$(), sessionSocketRawFDs, false, isPriorityFork);
            if (pid == 0) {
                IoUtils.closeQuietly(readFD);
                return childMain(null, usapPoolSocket, writeFD);
            } else if (pid == -1) {
                return null;
            } else {
                IoUtils.closeQuietly(writeFD);
                nativeAddUsapTableEntry(pid, readFD.getInt$());
                return null;
            }
        } catch (ErrnoException errnoEx) {
            throw new IllegalStateException("Unable to create USAP pipe.", errnoEx);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Runnable forkSimpleApps(ZygoteCommandBuffer argBuffer, FileDescriptor zygoteSocket, int expectedUid, int minUid, String firstNiceName) {
        boolean in_child = argBuffer.forkRepeatedly(zygoteSocket, expectedUid, minUid, firstNiceName);
        if (!in_child) {
            return null;
        }
        return childMain(argBuffer, null, null);
    }

    private static Runnable childMain(ZygoteCommandBuffer argBuffer, LocalServerSocket usapPoolSocket, FileDescriptor writePipe) {
        ZygoteArguments args;
        int[][] rlimits;
        int pid = Process.myPid();
        DataOutputStream usapOutputStream = null;
        LocalSocket sessionSocket = null;
        if (argBuffer == null) {
            Process.setArgV0(Process.is64Bit() ? "usap64" : "usap32");
            boostUsapPriority();
            while (true) {
                ZygoteCommandBuffer tmpArgBuffer = null;
                try {
                    sessionSocket = usapPoolSocket.accept();
                    blockSigTerm();
                    usapOutputStream = new DataOutputStream(sessionSocket.getOutputStream());
                    Credentials peerCredentials = sessionSocket.getPeerCredentials();
                    tmpArgBuffer = new ZygoteCommandBuffer(sessionSocket);
                    args = ZygoteArguments.getInstance(tmpArgBuffer);
                    applyUidSecurityPolicy(args, peerCredentials);
                    validateUsapCommand(args);
                    break;
                } catch (Exception ex) {
                    Log.m110e("USAP", ex.getMessage());
                    unblockSigTerm();
                    IoUtils.closeQuietly(sessionSocket);
                    IoUtils.closeQuietly(tmpArgBuffer);
                }
            }
        } else {
            blockSigTerm();
            try {
                args = ZygoteArguments.getInstance(argBuffer);
            } catch (Exception ex2) {
                Log.m110e("AppStartup", ex2.getMessage());
                throw new AssertionError("Failed to parse application start command", ex2);
            }
        }
        if (args == null) {
            throw new AssertionError("Empty command line");
        }
        try {
            applyDebuggerSystemProperty(args);
            if (args.mRLimits == null) {
                rlimits = null;
            } else {
                try {
                    int[][] rlimits2 = (int[][]) args.mRLimits.toArray(INT_ARRAY_2D);
                    rlimits = rlimits2;
                } catch (Throwable th) {
                    th = th;
                    unblockSigTerm();
                    throw th;
                }
            }
            if (argBuffer == null) {
                try {
                    usapOutputStream.writeInt(pid);
                    try {
                        FileDescriptor fd = usapPoolSocket.getFileDescriptor();
                        usapPoolSocket.close();
                        Os.close(fd);
                    } catch (ErrnoException | IOException ex3) {
                        Log.m110e("USAP", "Failed to close USAP pool socket");
                        throw new RuntimeException(ex3);
                    }
                } catch (IOException ioEx) {
                    Log.m110e("USAP", "Failed to write response to session socket: " + ioEx.getMessage());
                    throw new RuntimeException(ioEx);
                }
            }
            if (writePipe != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream(8);
                    DataOutputStream outputStream = new DataOutputStream(buffer);
                    outputStream.writeLong(pid);
                    outputStream.flush();
                    Os.write(writePipe, buffer.toByteArray(), 0, buffer.size());
                    IoUtils.closeQuietly(writePipe);
                } catch (Exception ex4) {
                    Log.m110e("USAP", String.format("Failed to write PID (%d) to pipe (%d): %s", Integer.valueOf(pid), Integer.valueOf(writePipe.getInt$()), ex4.getMessage()));
                    throw new RuntimeException(ex4);
                }
            }
            try {
                try {
                    try {
                        specializeAppProcess(args.mUid, args.mGid, args.mGids, args.mRuntimeFlags, rlimits, args.mMountExternal, args.mSeInfo, args.mNiceName, args.mStartChildZygote, args.mInstructionSet, args.mAppDataDir, args.mIsTopApp, args.mPkgDataInfoList, args.mAllowlistedDataInfoList, args.mBindMountAppDataDirs, args.mBindMountAppStorageDirs);
                        Trace.traceEnd(64L);
                        Runnable zygoteInit = ZygoteInit.zygoteInit(args.mTargetSdkVersion, args.mDisabledCompatChanges, args.mRemainingArgs, null);
                        unblockSigTerm();
                        return zygoteInit;
                    } catch (Throwable th2) {
                        th = th2;
                        unblockSigTerm();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Throwable th5) {
            th = th5;
        }
    }

    private static void blockSigTerm() {
        nativeBlockSigTerm();
    }

    private static void unblockSigTerm() {
        nativeUnblockSigTerm();
    }

    private static void boostUsapPriority() {
        nativeBoostUsapPriority();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setAppProcessName(ZygoteArguments args, String loggingTag) {
        if (args.mNiceName != null) {
            Process.setArgV0(args.mNiceName);
        } else if (args.mPackageName != null) {
            Process.setArgV0(args.mPackageName);
        } else {
            Log.m104w(loggingTag, "Unable to set package name.");
        }
    }

    private static void validateUsapCommand(ZygoteArguments args) {
        if (args.mAbiListQuery) {
            throw new IllegalArgumentException("Invalid command to USAP: --query-abi-list");
        }
        if (args.mPidQuery) {
            throw new IllegalArgumentException("Invalid command to USAP: --get-pid");
        }
        if (args.mPreloadDefault) {
            throw new IllegalArgumentException("Invalid command to USAP: --preload-default");
        }
        if (args.mPreloadPackage != null) {
            throw new IllegalArgumentException("Invalid command to USAP: --preload-package");
        }
        if (args.mPreloadApp != null) {
            throw new IllegalArgumentException("Invalid command to USAP: --preload-app");
        }
        if (args.mStartChildZygote) {
            throw new IllegalArgumentException("Invalid command to USAP: --start-child-zygote");
        }
        if (args.mApiDenylistExemptions != null) {
            throw new IllegalArgumentException("Invalid command to USAP: --set-api-denylist-exemptions");
        }
        if (args.mHiddenApiAccessLogSampleRate != -1) {
            throw new IllegalArgumentException("Invalid command to USAP: --hidden-api-log-sampling-rate=");
        }
        if (args.mHiddenApiAccessStatslogSampleRate != -1) {
            throw new IllegalArgumentException("Invalid command to USAP: --hidden-api-statslog-sampling-rate=");
        }
        if (args.mInvokeWith != null) {
            throw new IllegalArgumentException("Invalid command to USAP: --invoke-with");
        }
        if (args.mPermittedCapabilities != 0 || args.mEffectiveCapabilities != 0) {
            throw new ZygoteSecurityException("Client may not specify capabilities: permitted=0x" + Long.toHexString(args.mPermittedCapabilities) + ", effective=0x" + Long.toHexString(args.mEffectiveCapabilities));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] getUsapPipeFDs() {
        return nativeGetUsapPipeFDs();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean removeUsapTableEntry(int usapPID) {
        return nativeRemoveUsapTableEntry(usapPID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int minChildUid(Credentials peer) {
        return (peer.getUid() == 1000 && FactoryTest.getMode() == 0) ? 1000 : 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyUidSecurityPolicy(ZygoteArguments args, Credentials peer) throws ZygoteSecurityException {
        if (args.mUidSpecified && args.mUid < minChildUid(peer)) {
            throw new ZygoteSecurityException("System UID may not launch process with UID < 1000");
        }
        if (!args.mUidSpecified) {
            args.mUid = peer.getUid();
            args.mUidSpecified = true;
        }
        if (!args.mGidSpecified) {
            args.mGid = peer.getGid();
            args.mGidSpecified = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyDebuggerSystemProperty(ZygoteArguments args) {
        if (Build.IS_ENG || ENABLE_JDWP) {
            args.mRuntimeFlags |= 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyInvokeWithSecurityPolicy(ZygoteArguments args, Credentials peer) throws ZygoteSecurityException {
        int peerUid = peer.getUid();
        if (args.mInvokeWith != null && peerUid != 0 && (args.mRuntimeFlags & 1) == 0) {
            throw new ZygoteSecurityException("Peer is permitted to specify an explicit invoke-with wrapper command only for debuggable applications.");
        }
    }

    public static String getWrapProperty(String appName) {
        String propertyValue;
        if (appName == null || appName.isEmpty() || (propertyValue = SystemProperties.get("wrap." + appName)) == null || propertyValue.isEmpty()) {
            return null;
        }
        return propertyValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyInvokeWithSystemProperty(ZygoteArguments args) {
        if (args.mInvokeWith == null) {
            args.mInvokeWith = getWrapProperty(args.mNiceName);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static LocalServerSocket createManagedSocketFromInitSocket(String socketName) {
        String fullSocketName = ANDROID_SOCKET_PREFIX + socketName;
        try {
            String env = System.getenv(fullSocketName);
            int fileDesc = Integer.parseInt(env);
            try {
                FileDescriptor fd = new FileDescriptor();
                fd.setInt$(fileDesc);
                return new LocalServerSocket(fd);
            } catch (IOException ex) {
                throw new RuntimeException("Error building socket from file descriptor: " + fileDesc, ex);
            }
        } catch (RuntimeException ex2) {
            throw new RuntimeException("Socket unset or invalid: " + fullSocketName, ex2);
        }
    }

    private static void callPostForkSystemServerHooks(int runtimeFlags) {
        ZygoteHooks.postForkSystemServer(runtimeFlags);
    }

    private static void callPostForkChildHooks(int runtimeFlags, boolean isSystemServer, boolean isZygote, String instructionSet) {
        ZygoteHooks.postForkChild(runtimeFlags, isSystemServer, isZygote, instructionSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void execShell(String command) {
        String[] args = {"/system/bin/sh", "-c", command};
        try {
            Os.execv(args[0], args);
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void appendQuotedShellArgs(StringBuilder command, String[] args) {
        for (String arg : args) {
            command.append(" '").append(arg.replace("'", "'\\''")).append("'");
        }
    }

    private static int memtagModeToZygoteMemtagLevel(int memtagMode) {
        switch (memtagMode) {
            case 1:
                return 1048576;
            case 2:
                return 1572864;
            default:
                return 0;
        }
    }

    private static boolean isCompatChangeEnabled(long change, ApplicationInfo info, IPlatformCompat platformCompat, int enabledAfter) {
        if (platformCompat != null) {
            try {
                return platformCompat.isChangeEnabled(change, info);
            } catch (RemoteException e) {
            }
        }
        return enabledAfter > 0 && info.targetSdkVersion > enabledAfter;
    }

    private static int getRequestedMemtagLevel(ApplicationInfo info, ProcessInfo processInfo, IPlatformCompat platformCompat) {
        if (processInfo == null || processInfo.memtagMode == -1) {
            if (info.getMemtagMode() != -1) {
                return memtagModeToZygoteMemtagLevel(info.getMemtagMode());
            }
            if (isCompatChangeEnabled(NATIVE_MEMTAG_SYNC, info, platformCompat, 0)) {
                return 1572864;
            }
            if (isCompatChangeEnabled(NATIVE_MEMTAG_ASYNC, info, platformCompat, 0)) {
                return 1048576;
            }
            if (info.allowsNativeHeapPointerTagging()) {
                String defaultLevel = SystemProperties.get("persist.arm64.memtag.app_default");
                if ("sync".equals(defaultLevel)) {
                    return 1572864;
                }
                if ("async".equals(defaultLevel)) {
                    return 1048576;
                }
                return isCompatChangeEnabled(NATIVE_HEAP_POINTER_TAGGING, info, platformCompat, 29) ? 524288 : 0;
            }
            return 0;
        }
        return memtagModeToZygoteMemtagLevel(processInfo.memtagMode);
    }

    private static int decideTaggingLevel(ApplicationInfo info, ProcessInfo processInfo, IPlatformCompat platformCompat) {
        int level = getRequestedMemtagLevel(info, processInfo, platformCompat);
        if (nativeSupportsMemoryTagging()) {
            if (level == 524288) {
                level = 0;
            }
        } else if (nativeSupportsTaggedPointers()) {
            if (level == 1048576 || level == 1572864) {
                level = 524288;
            }
        } else {
            level = 0;
        }
        if (level == 1048576) {
            if ((Build.IS_USERDEBUG || Build.IS_ENG) && "sync".equals(SystemProperties.get("persist.arm64.memtag.default"))) {
                return 1572864;
            }
            return level;
        }
        return level;
    }

    private static int decideGwpAsanLevel(ApplicationInfo info, ProcessInfo processInfo, IPlatformCompat platformCompat) {
        if (processInfo != null && processInfo.gwpAsanMode != -1) {
            return processInfo.gwpAsanMode == 1 ? 4194304 : 0;
        } else if (info.getGwpAsanMode() != -1) {
            return info.getGwpAsanMode() == 1 ? 4194304 : 0;
        } else if (isCompatChangeEnabled(GWP_ASAN, info, platformCompat, 0)) {
            return 4194304;
        } else {
            if ((info.flags & 1) != 0) {
                return 2097152;
            }
            return 6291456;
        }
    }

    private static boolean enableNativeHeapZeroInit(ApplicationInfo info, ProcessInfo processInfo, IPlatformCompat platformCompat) {
        return (processInfo == null || processInfo.nativeHeapZeroInitialized == -1) ? info.getNativeHeapZeroInitialized() != -1 ? info.getNativeHeapZeroInitialized() == 1 : isCompatChangeEnabled(NATIVE_HEAP_ZERO_INIT, info, platformCompat, 0) : processInfo.nativeHeapZeroInitialized == 1;
    }

    public static int getMemorySafetyRuntimeFlags(ApplicationInfo info, ProcessInfo processInfo, String instructionSet, IPlatformCompat platformCompat) {
        int runtimeFlags = decideGwpAsanLevel(info, processInfo, platformCompat);
        if (instructionSet == null || instructionSet.equals("arm64")) {
            runtimeFlags |= decideTaggingLevel(info, processInfo, platformCompat);
        }
        if (enableNativeHeapZeroInit(info, processInfo, platformCompat)) {
            return runtimeFlags | 8388608;
        }
        return runtimeFlags;
    }

    public static int getMemorySafetyRuntimeFlagsForSecondaryZygote(ApplicationInfo info, ProcessInfo processInfo) {
        IPlatformCompat platformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService(Context.PLATFORM_COMPAT_SERVICE));
        int runtimeFlags = getMemorySafetyRuntimeFlags(info, processInfo, null, platformCompat);
        if ((1572864 & runtimeFlags) == 524288 && isCompatChangeEnabled(NATIVE_HEAP_POINTER_TAGGING_SECONDARY_ZYGOTE, info, platformCompat, 31)) {
            return (runtimeFlags & (-1572865)) | 0;
        }
        return runtimeFlags;
    }
}
