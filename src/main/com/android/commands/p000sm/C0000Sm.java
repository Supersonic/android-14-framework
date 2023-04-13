package com.android.commands.p000sm;

import android.os.IVoldTaskListener;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.storage.DiskInfo;
import android.os.storage.IStorageManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import java.util.concurrent.CompletableFuture;
/* renamed from: com.android.commands.sm.Sm */
/* loaded from: classes.dex */
public final class C0000Sm {
    private static final String ANDROID_VOLD_APP_DATA_ISOLATION_ENABLED_PROPERTY = "persist.sys.vold_app_data_isolation_enabled";
    private static final String TAG = "Sm";
    private String[] mArgs;
    private String mCurArgData;
    private int mNextArg;
    IStorageManager mSm;

    public static void main(String[] args) {
        int i = 0;
        try {
            new C0000Sm().run(args);
            i = 1;
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                showUsage();
                System.exit(1);
            }
            Log.e(TAG, "Error", e);
            System.err.println("Error: " + e);
        }
        System.exit(i ^ 1);
    }

    public void run(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }
        IStorageManager asInterface = IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        this.mSm = asInterface;
        if (asInterface == null) {
            throw new RemoteException("Failed to find running mount service");
        }
        this.mArgs = args;
        String op = args[0];
        this.mNextArg = 1;
        if ("list-disks".equals(op)) {
            runListDisks();
        } else if ("list-volumes".equals(op)) {
            runListVolumes();
        } else if ("has-adoptable".equals(op)) {
            runHasAdoptable();
        } else if ("get-primary-storage-uuid".equals(op)) {
            runGetPrimaryStorageUuid();
        } else if ("set-force-adoptable".equals(op)) {
            runSetForceAdoptable();
        } else if ("set-sdcardfs".equals(op)) {
            runSetSdcardfs();
        } else if ("partition".equals(op)) {
            runPartition();
        } else if ("mount".equals(op)) {
            runMount();
        } else if ("unmount".equals(op)) {
            runUnmount();
        } else if ("format".equals(op)) {
            runFormat();
        } else if ("benchmark".equals(op)) {
            runBenchmark();
        } else if ("forget".equals(op)) {
            runForget();
        } else if ("get-fbe-mode".equals(op)) {
            runGetFbeMode();
        } else if ("idle-maint".equals(op)) {
            runIdleMaint();
        } else if ("fstrim".equals(op)) {
            runFstrim();
        } else if ("set-virtual-disk".equals(op)) {
            runSetVirtualDisk();
        } else if ("start-checkpoint".equals(op)) {
            runStartCheckpoint();
        } else if ("supports-checkpoint".equals(op)) {
            runSupportsCheckpoint();
        } else if ("unmount-app-data-dirs".equals(op)) {
            runDisableAppDataIsolation();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void runListDisks() throws RemoteException {
        boolean onlyAdoptable = "adoptable".equals(nextArg());
        DiskInfo[] disks = this.mSm.getDisks();
        for (DiskInfo disk : disks) {
            if (!onlyAdoptable || disk.isAdoptable()) {
                System.out.println(disk.getId());
            }
        }
    }

    public void runListVolumes() throws RemoteException {
        int filterType;
        String filter = nextArg();
        if ("public".equals(filter)) {
            filterType = 0;
        } else if ("private".equals(filter)) {
            filterType = 1;
        } else if ("emulated".equals(filter)) {
            filterType = 2;
        } else if ("stub".equals(filter)) {
            filterType = 5;
        } else {
            filterType = -1;
        }
        VolumeInfo[] vols = this.mSm.getVolumes(0);
        for (VolumeInfo vol : vols) {
            if (filterType == -1 || filterType == vol.getType()) {
                String envState = VolumeInfo.getEnvironmentForState(vol.getState());
                System.out.println(vol.getId() + " " + envState + " " + vol.getFsUuid());
            }
        }
    }

    public void runHasAdoptable() {
        System.out.println(StorageManager.hasAdoptable());
    }

    public void runGetPrimaryStorageUuid() throws RemoteException {
        System.out.println(this.mSm.getPrimaryStorageUuid());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void runSetForceAdoptable() throws RemoteException {
        char c;
        String nextArg = nextArg();
        switch (nextArg.hashCode()) {
            case 3551:
                if (nextArg.equals("on")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 109935:
                if (nextArg.equals("off")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (nextArg.equals("true")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (nextArg.equals("false")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1544803905:
                if (nextArg.equals("default")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                this.mSm.setDebugFlags(1, 3);
                return;
            case 2:
                this.mSm.setDebugFlags(2, 3);
                return;
            case 3:
            case 4:
                this.mSm.setDebugFlags(0, 3);
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void runSetSdcardfs() throws RemoteException {
        char c;
        String nextArg = nextArg();
        switch (nextArg.hashCode()) {
            case 3551:
                if (nextArg.equals("on")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 109935:
                if (nextArg.equals("off")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1544803905:
                if (nextArg.equals("default")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.mSm.setDebugFlags(4, 12);
                return;
            case 1:
                this.mSm.setDebugFlags(8, 12);
                return;
            case 2:
                this.mSm.setDebugFlags(0, 12);
                return;
            default:
                return;
        }
    }

    public void runGetFbeMode() {
        if (StorageManager.isFileEncrypted()) {
            System.out.println("native");
        } else {
            System.out.println("none");
        }
    }

    public void runPartition() throws RemoteException {
        String diskId = nextArg();
        String type = nextArg();
        if ("public".equals(type)) {
            this.mSm.partitionPublic(diskId);
        } else if ("private".equals(type)) {
            this.mSm.partitionPrivate(diskId);
        } else if ("mixed".equals(type)) {
            int ratio = Integer.parseInt(nextArg());
            this.mSm.partitionMixed(diskId, ratio);
        } else {
            throw new IllegalArgumentException("Unsupported partition type " + type);
        }
    }

    public void runMount() throws RemoteException {
        String volId = nextArg();
        this.mSm.mount(volId);
    }

    public void runUnmount() throws RemoteException {
        String volId = nextArg();
        this.mSm.unmount(volId);
    }

    public void runFormat() throws RemoteException {
        String volId = nextArg();
        this.mSm.format(volId);
    }

    public void runBenchmark() throws Exception {
        String volId = nextArg();
        final CompletableFuture<PersistableBundle> result = new CompletableFuture<>();
        this.mSm.benchmark(volId, new IVoldTaskListener.Stub() { // from class: com.android.commands.sm.Sm.1
            public void onStatus(int status, PersistableBundle extras) {
            }

            public void onFinished(int status, PersistableBundle extras) {
                extras.size();
                result.complete(extras);
            }
        });
        System.out.println(result.get());
    }

    public void runDisableAppDataIsolation() throws RemoteException {
        if (!SystemProperties.getBoolean(ANDROID_VOLD_APP_DATA_ISOLATION_ENABLED_PROPERTY, false)) {
            System.err.println("Storage app data isolation is not enabled.");
            return;
        }
        String pkgName = nextArg();
        int pid = Integer.parseInt(nextArg());
        int userId = Integer.parseInt(nextArg());
        this.mSm.disableAppDataIsolation(pkgName, pid, userId);
    }

    public void runForget() throws RemoteException {
        String fsUuid = nextArg();
        if ("all".equals(fsUuid)) {
            this.mSm.forgetAllVolumes();
        } else {
            this.mSm.forgetVolume(fsUuid);
        }
    }

    public void runFstrim() throws Exception {
        final CompletableFuture<PersistableBundle> result = new CompletableFuture<>();
        this.mSm.fstrim(0, new IVoldTaskListener.Stub() { // from class: com.android.commands.sm.Sm.2
            public void onStatus(int status, PersistableBundle extras) {
            }

            public void onFinished(int status, PersistableBundle extras) {
                extras.size();
                result.complete(extras);
            }
        });
        System.out.println(result.get());
    }

    public void runSetVirtualDisk() throws RemoteException {
        boolean virtualDisk = Boolean.parseBoolean(nextArg());
        this.mSm.setDebugFlags(virtualDisk ? 16 : 0, 16);
    }

    public void runIdleMaint() throws RemoteException {
        boolean im_run = "run".equals(nextArg());
        if (im_run) {
            this.mSm.runIdleMaintenance();
        } else {
            this.mSm.abortIdleMaintenance();
        }
    }

    private void runStartCheckpoint() throws RemoteException {
        String numRetriesString = nextArg();
        if (numRetriesString == null) {
            throw new IllegalArgumentException("Expected <num-retries>");
        }
        try {
            int numRetries = Integer.parseInt(numRetriesString);
            if (numRetries <= 0) {
                throw new IllegalArgumentException("<num-retries> must be a positive integer");
            }
            this.mSm.startCheckpoint(numRetries);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("<num-retries> must be a positive integer");
        }
    }

    private void runSupportsCheckpoint() throws RemoteException {
        System.out.println(this.mSm.supportsCheckpoint());
    }

    private String nextArg() {
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        this.mNextArg = i + 1;
        return arg;
    }

    private static int showUsage() {
        System.err.println("usage: sm list-disks [adoptable]");
        System.err.println("       sm list-volumes [public|private|emulated|stub|all]");
        System.err.println("       sm has-adoptable");
        System.err.println("       sm get-primary-storage-uuid");
        System.err.println("       sm set-force-adoptable [on|off|default]");
        System.err.println("       sm set-virtual-disk [true|false]");
        System.err.println("");
        System.err.println("       sm partition DISK [public|private|mixed] [ratio]");
        System.err.println("       sm mount VOLUME");
        System.err.println("       sm unmount VOLUME");
        System.err.println("       sm format VOLUME");
        System.err.println("       sm benchmark VOLUME");
        System.err.println("       sm idle-maint [run|abort]");
        System.err.println("       sm fstrim");
        System.err.println("");
        System.err.println("       sm forget [UUID|all]");
        System.err.println("");
        System.err.println("       sm start-checkpoint <num-retries>");
        System.err.println("");
        System.err.println("       sm supports-checkpoint");
        System.err.println("");
        System.err.println("       sm unmount-app-data-dirs PACKAGE_NAME PID USER_ID");
        System.err.println("");
        return 1;
    }
}
