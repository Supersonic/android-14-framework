package com.android.server.testharness;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.debug.AdbManagerInternal;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.PersistentDataBlockManagerInternal;
import com.android.server.SystemService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
/* loaded from: classes2.dex */
public class TestHarnessModeService extends SystemService {
    public static final String TAG = "TestHarnessModeService";
    public PersistentDataBlockManagerInternal mPersistentDataBlockManagerInternal;
    public final IBinder mService;

    public TestHarnessModeService(Context context) {
        super(context);
        this.mService = new Binder() { // from class: com.android.server.testharness.TestHarnessModeService.1
            public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
                new TestHarnessModeShellCommand().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
            }
        };
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("testharness", this.mService);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            setUpTestHarnessMode();
        } else if (i == 1000) {
            completeTestHarnessModeSetup();
            showNotificationIfEnabled();
        }
        super.onBootPhase(i);
    }

    public final void setUpTestHarnessMode() {
        Slog.d(TAG, "Setting up test harness mode");
        if (getTestHarnessModeData() == null) {
            return;
        }
        setDeviceProvisioned();
        disableLockScreen();
        SystemProperties.set("persist.sys.test_harness", "1");
    }

    public final void disableLockScreen() {
        new LockPatternUtils(getContext()).setLockScreenDisabled(true, getMainUserId());
    }

    public final void completeTestHarnessModeSetup() {
        Slog.d(TAG, "Completing Test Harness Mode setup.");
        byte[] testHarnessModeData = getTestHarnessModeData();
        if (testHarnessModeData == null) {
            return;
        }
        try {
            try {
                setUpAdbFiles(PersistentData.fromBytes(testHarnessModeData));
                configureSettings();
                configureUser();
            } catch (SetUpTestHarnessModeException e) {
                Slog.e(TAG, "Failed to set up Test Harness Mode. Bad data.", e);
            }
        } finally {
            getPersistentDataBlock().clearTestHarnessModeData();
        }
    }

    public final byte[] getTestHarnessModeData() {
        PersistentDataBlockManagerInternal persistentDataBlock = getPersistentDataBlock();
        if (persistentDataBlock == null) {
            Slog.e(TAG, "Failed to start Test Harness Mode; no implementation of PersistentDataBlockManagerInternal was bound!");
            return null;
        }
        byte[] testHarnessModeData = persistentDataBlock.getTestHarnessModeData();
        if (testHarnessModeData == null || testHarnessModeData.length == 0) {
            return null;
        }
        return testHarnessModeData;
    }

    public final void configureSettings() {
        ContentResolver contentResolver = getContext().getContentResolver();
        if (Settings.Global.getInt(contentResolver, "adb_enabled", 0) == 1) {
            SystemProperties.set("ctl.restart", "adbd");
            Slog.d(TAG, "Restarted adbd");
        }
        Settings.Global.putLong(contentResolver, "adb_allowed_connection_time", 0L);
        Settings.Global.putInt(contentResolver, "development_settings_enabled", 1);
        Settings.Global.putInt(contentResolver, "verifier_verify_adb_installs", 0);
        Settings.Global.putInt(contentResolver, "stay_on_while_plugged_in", 15);
        Settings.Global.putInt(contentResolver, "ota_disable_automatic_update", 1);
    }

    public final void setUpAdbFiles(PersistentData persistentData) {
        AdbManagerInternal adbManagerInternal = (AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class);
        if (adbManagerInternal.getAdbKeysFile() != null) {
            writeBytesToFile(persistentData.mAdbKeys, adbManagerInternal.getAdbKeysFile().toPath());
        }
        if (adbManagerInternal.getAdbTempKeysFile() != null) {
            writeBytesToFile(persistentData.mAdbTempKeys, adbManagerInternal.getAdbTempKeysFile().toPath());
        }
        adbManagerInternal.notifyKeyFilesUpdated();
    }

    public final void configureUser() {
        int mainUserId = getMainUserId();
        ContentResolver.setMasterSyncAutomaticallyAsUser(false, mainUserId);
        ((LocationManager) getContext().getSystemService(LocationManager.class)).setLocationEnabledForUser(true, UserHandle.of(mainUserId));
    }

    public final int getMainUserId() {
        int mainUserId = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getMainUserId();
        if (mainUserId >= 0) {
            return mainUserId;
        }
        Slog.w(TAG, "No MainUser exists; using user 0 instead");
        return 0;
    }

    public final void writeBytesToFile(byte[] bArr, Path path) {
        try {
            OutputStream newOutputStream = Files.newOutputStream(path, new OpenOption[0]);
            newOutputStream.write(bArr);
            newOutputStream.close();
            Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(path, new LinkOption[0]);
            posixFilePermissions.add(PosixFilePermission.GROUP_READ);
            Files.setPosixFilePermissions(path, posixFilePermissions);
        } catch (IOException e) {
            Slog.e(TAG, "Failed to set up adb keys", e);
        }
    }

    public final void setDeviceProvisioned() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Settings.Global.putInt(contentResolver, "device_provisioned", 1);
        Settings.Secure.putIntForUser(contentResolver, "user_setup_complete", 1, -2);
    }

    public final void showNotificationIfEnabled() {
        if (SystemProperties.getBoolean("persist.sys.test_harness", false)) {
            String string = getContext().getString(17041640);
            ((NotificationManager) getContext().getSystemService(NotificationManager.class)).notifyAsUser(null, 54, new Notification.Builder(getContext(), SystemNotificationChannels.DEVELOPER).setSmallIcon(17303596).setWhen(0L).setOngoing(true).setTicker(string).setDefaults(0).setColor(getContext().getColor(17170460)).setContentTitle(string).setContentText(getContext().getString(17041639)).setVisibility(1).build(), UserHandle.ALL);
        }
    }

    public final PersistentDataBlockManagerInternal getPersistentDataBlock() {
        if (this.mPersistentDataBlockManagerInternal == null) {
            Slog.d(TAG, "Getting PersistentDataBlockManagerInternal from LocalServices");
            this.mPersistentDataBlockManagerInternal = (PersistentDataBlockManagerInternal) LocalServices.getService(PersistentDataBlockManagerInternal.class);
        }
        return this.mPersistentDataBlockManagerInternal;
    }

    /* loaded from: classes2.dex */
    public class TestHarnessModeShellCommand extends ShellCommand {
        public TestHarnessModeShellCommand() {
        }

        public int onCommand(String str) {
            if (str == null) {
                return handleDefaultCommands(str);
            }
            if (str.equals("enable") || str.equals("restore")) {
                checkPermissions();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (isDeviceSecure()) {
                        getErrPrintWriter().println("Test Harness Mode cannot be enabled if there is a lock screen");
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return 2;
                    }
                    return handleEnable();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return handleDefaultCommands(str);
        }

        public final void checkPermissions() {
            TestHarnessModeService.this.getContext().enforceCallingPermission("android.permission.ENABLE_TEST_HARNESS_MODE", "You must hold android.permission.ENABLE_TEST_HARNESS_MODE to enable Test Harness Mode");
        }

        public final boolean isDeviceSecure() {
            return ((KeyguardManager) TestHarnessModeService.this.getContext().getSystemService(KeyguardManager.class)).isDeviceSecure(TestHarnessModeService.this.getMainUserId());
        }

        public final int handleEnable() {
            AdbManagerInternal adbManagerInternal = (AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class);
            try {
                PersistentData persistentData = new PersistentData(getBytesFromFile(adbManagerInternal.getAdbKeysFile()), getBytesFromFile(adbManagerInternal.getAdbTempKeysFile()));
                PersistentDataBlockManagerInternal persistentDataBlock = TestHarnessModeService.this.getPersistentDataBlock();
                if (persistentDataBlock == null) {
                    Slog.e("ShellCommand", "Failed to enable Test Harness Mode. No implementation of PersistentDataBlockManagerInternal was bound.");
                    getErrPrintWriter().println("Failed to enable Test Harness Mode");
                    return 1;
                }
                persistentDataBlock.setTestHarnessModeData(persistentData.toBytes());
                Intent intent = new Intent("android.intent.action.FACTORY_RESET");
                intent.setPackage(PackageManagerShellCommandDataLoader.PACKAGE);
                intent.addFlags(268435456);
                intent.putExtra("android.intent.extra.REASON", "ShellCommand");
                intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
                TestHarnessModeService.this.getContext().sendBroadcastAsUser(intent, UserHandle.SYSTEM);
                return 0;
            } catch (IOException e) {
                Slog.e("ShellCommand", "Failed to store ADB keys.", e);
                getErrPrintWriter().println("Failed to enable Test Harness Mode");
                return 1;
            }
        }

        public final byte[] getBytesFromFile(File file) throws IOException {
            if (file == null || !file.exists()) {
                return new byte[0];
            }
            Path path = file.toPath();
            InputStream newInputStream = Files.newInputStream(path, new OpenOption[0]);
            try {
                int size = (int) Files.size(path);
                byte[] bArr = new byte[size];
                if (newInputStream.read(bArr) != size) {
                    throw new IOException("Failed to read the whole file");
                }
                newInputStream.close();
                return bArr;
            } catch (Throwable th) {
                if (newInputStream != null) {
                    try {
                        newInputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("About:");
            outPrintWriter.println("  Test Harness Mode is a mode that the device can be placed in to prepare");
            outPrintWriter.println("  the device for running UI tests. The device is placed into this mode by");
            outPrintWriter.println("  first wiping all data from the device, preserving ADB keys.");
            outPrintWriter.println();
            outPrintWriter.println("  By default, the following settings are configured:");
            outPrintWriter.println("    * Package Verifier is disabled");
            outPrintWriter.println("    * Stay Awake While Charging is enabled");
            outPrintWriter.println("    * OTA Updates are disabled");
            outPrintWriter.println("    * Auto-Sync for accounts is disabled");
            outPrintWriter.println();
            outPrintWriter.println("  Other apps may configure themselves differently in Test Harness Mode by");
            outPrintWriter.println("  checking ActivityManager.isRunningInUserTestHarness()");
            outPrintWriter.println();
            outPrintWriter.println("Test Harness Mode commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Print this help text.");
            outPrintWriter.println();
            outPrintWriter.println("  enable|restore");
            outPrintWriter.println("    Erase all data from this device and enable Test Harness Mode,");
            outPrintWriter.println("    preserving the stored ADB keys currently on the device and toggling");
            outPrintWriter.println("    settings in a way that are conducive to Instrumentation testing.");
        }
    }

    /* loaded from: classes2.dex */
    public static class PersistentData {
        public final byte[] mAdbKeys;
        public final byte[] mAdbTempKeys;
        public final int mVersion;

        public PersistentData(byte[] bArr, byte[] bArr2) {
            this(2, bArr, bArr2);
        }

        public PersistentData(int i, byte[] bArr, byte[] bArr2) {
            this.mVersion = i;
            this.mAdbKeys = bArr;
            this.mAdbTempKeys = bArr2;
        }

        public static PersistentData fromBytes(byte[] bArr) throws SetUpTestHarnessModeException {
            try {
                DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
                int readInt = dataInputStream.readInt();
                if (readInt == 1) {
                    dataInputStream.readBoolean();
                }
                byte[] bArr2 = new byte[dataInputStream.readInt()];
                dataInputStream.readFully(bArr2);
                byte[] bArr3 = new byte[dataInputStream.readInt()];
                dataInputStream.readFully(bArr3);
                return new PersistentData(readInt, bArr2, bArr3);
            } catch (IOException e) {
                throw new SetUpTestHarnessModeException(e);
            }
        }

        public byte[] toBytes() {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                dataOutputStream.writeInt(2);
                dataOutputStream.writeInt(this.mAdbKeys.length);
                dataOutputStream.write(this.mAdbKeys);
                dataOutputStream.writeInt(this.mAdbTempKeys.length);
                dataOutputStream.write(this.mAdbTempKeys);
                dataOutputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class SetUpTestHarnessModeException extends Exception {
        public SetUpTestHarnessModeException(Exception exc) {
            super(exc);
        }
    }
}
