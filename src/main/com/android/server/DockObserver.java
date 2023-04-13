package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.server.ExtconUEventObserver;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class DockObserver extends SystemService {
    public int mActualDockState;
    public final boolean mAllowTheaterModeWakeFromDock;
    public DeviceProvisionedObserver mDeviceProvisionedObserver;
    public final List<ExtconStateConfig> mExtconStateConfigs;
    public final ExtconUEventObserver mExtconUEventObserver;
    public final Handler mHandler;
    public final boolean mKeepDreamingWhenUndocking;
    public final Object mLock;
    public final PowerManager mPowerManager;
    public int mPreviousDockState;
    public int mReportedDockState;
    public boolean mSystemReady;
    public boolean mUpdatesStopped;
    public final PowerManager.WakeLock mWakeLock;

    /* loaded from: classes.dex */
    public static final class ExtconStateProvider {
        public final Map<String, String> mState;

        public ExtconStateProvider(Map<String, String> map) {
            this.mState = map;
        }

        public String getValue(String str) {
            return this.mState.get(str);
        }

        public static ExtconStateProvider fromString(String str) {
            String[] split;
            HashMap hashMap = new HashMap();
            for (String str2 : str.split("\n")) {
                String[] split2 = str2.split("=");
                if (split2.length == 2) {
                    hashMap.put(split2[0], split2[1]);
                } else {
                    Slog.e("DockObserver", "Invalid line: " + str2);
                }
            }
            return new ExtconStateProvider(hashMap);
        }

        public static ExtconStateProvider fromFile(String str) {
            char[] cArr = new char[1024];
            try {
                FileReader fileReader = new FileReader(str);
                ExtconStateProvider fromString = fromString(new String(cArr, 0, fileReader.read(cArr, 0, 1024)).trim());
                fileReader.close();
                return fromString;
            } catch (FileNotFoundException unused) {
                Slog.w("DockObserver", "No state file found at: " + str);
                return new ExtconStateProvider(new HashMap());
            } catch (Exception e) {
                Slog.e("DockObserver", "", e);
                return new ExtconStateProvider(new HashMap());
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class ExtconStateConfig {
        public final int extraStateValue;
        public final List<Pair<String, String>> keyValuePairs = new ArrayList();

        public ExtconStateConfig(int i) {
            this.extraStateValue = i;
        }
    }

    public static List<ExtconStateConfig> loadExtconStateConfigs(Context context) {
        String[] stringArray = context.getResources().getStringArray(17236058);
        try {
            ArrayList arrayList = new ArrayList();
            for (String str : stringArray) {
                String[] split = str.split(",");
                ExtconStateConfig extconStateConfig = new ExtconStateConfig(Integer.parseInt(split[0]));
                for (int i = 1; i < split.length; i++) {
                    String[] split2 = split[i].split("=");
                    if (split2.length != 2) {
                        throw new IllegalArgumentException("Invalid key-value: " + split[i]);
                    }
                    extconStateConfig.keyValuePairs.add(Pair.create(split2[0], split2[1]));
                }
                arrayList.add(extconStateConfig);
            }
            return arrayList;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            Slog.e("DockObserver", "Could not parse extcon state config", e);
            return new ArrayList();
        }
    }

    public DockObserver(Context context) {
        super(context);
        this.mLock = new Object();
        this.mActualDockState = 0;
        this.mReportedDockState = 0;
        this.mPreviousDockState = 0;
        Handler handler = new Handler(true) { // from class: com.android.server.DockObserver.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 0) {
                    return;
                }
                DockObserver.this.handleDockStateChange();
                DockObserver.this.mWakeLock.release();
            }
        };
        this.mHandler = handler;
        ExtconUEventObserver extconUEventObserver = new ExtconUEventObserver() { // from class: com.android.server.DockObserver.2
            @Override // com.android.server.ExtconUEventObserver
            public void onUEvent(ExtconUEventObserver.ExtconInfo extconInfo, UEventObserver.UEvent uEvent) {
                synchronized (DockObserver.this.mLock) {
                    String str = uEvent.get("STATE");
                    if (str != null) {
                        DockObserver.this.setDockStateFromProviderLocked(ExtconStateProvider.fromString(str));
                    } else {
                        Slog.e("DockObserver", "Extcon event missing STATE: " + uEvent);
                    }
                }
            }
        };
        this.mExtconUEventObserver = extconUEventObserver;
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        this.mPowerManager = powerManager;
        this.mWakeLock = powerManager.newWakeLock(1, "DockObserver");
        this.mAllowTheaterModeWakeFromDock = context.getResources().getBoolean(17891354);
        this.mKeepDreamingWhenUndocking = context.getResources().getBoolean(17891714);
        this.mDeviceProvisionedObserver = new DeviceProvisionedObserver(handler);
        this.mExtconStateConfigs = loadExtconStateConfigs(context);
        List<ExtconUEventObserver.ExtconInfo> extconInfoForTypes = ExtconUEventObserver.ExtconInfo.getExtconInfoForTypes(new String[]{"DOCK"});
        if (!extconInfoForTypes.isEmpty()) {
            ExtconUEventObserver.ExtconInfo extconInfo = extconInfoForTypes.get(0);
            Slog.i("DockObserver", "Found extcon info devPath: " + extconInfo.getDevicePath() + ", statePath: " + extconInfo.getStatePath());
            setDockStateFromProviderLocked(ExtconStateProvider.fromFile(extconInfo.getStatePath()));
            this.mPreviousDockState = this.mActualDockState;
            extconUEventObserver.startObserving(extconInfo);
            return;
        }
        Slog.i("DockObserver", "No extcon dock device found in this kernel.");
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("DockObserver", new BinderService());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 550) {
            synchronized (this.mLock) {
                this.mSystemReady = true;
                this.mDeviceProvisionedObserver.onSystemReady();
                updateIfDockedLocked();
            }
        }
    }

    public final void updateIfDockedLocked() {
        if (this.mReportedDockState != 0) {
            updateLocked();
        }
    }

    public final void setActualDockStateLocked(int i) {
        this.mActualDockState = i;
        if (this.mUpdatesStopped) {
            return;
        }
        setDockStateLocked(i);
    }

    public final void setDockStateLocked(int i) {
        if (i != this.mReportedDockState) {
            this.mReportedDockState = i;
            if (this.mSystemReady) {
                if (allowWakeFromDock()) {
                    this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.server:DOCK");
                }
                updateLocked();
            }
        }
    }

    public final boolean allowWakeFromDock() {
        if (this.mKeepDreamingWhenUndocking) {
            return false;
        }
        return this.mAllowTheaterModeWakeFromDock || Settings.Global.getInt(getContext().getContentResolver(), "theater_mode_on", 0) == 0;
    }

    public final void updateLocked() {
        this.mWakeLock.acquire();
        this.mHandler.sendEmptyMessage(0);
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00a6 A[Catch: all -> 0x00e1, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x003b, B:7:0x0042, B:9:0x0044, B:13:0x0064, B:17:0x006f, B:48:0x00d6, B:49:0x00df, B:23:0x007e, B:41:0x00a6, B:43:0x00ac, B:45:0x00c3, B:47:0x00cd), top: B:54:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void handleDockStateChange() {
        String str;
        String string;
        Uri parse;
        Ringtone ringtone;
        synchronized (this.mLock) {
            Slog.i("DockObserver", "Dock state changed from " + this.mPreviousDockState + " to " + this.mReportedDockState);
            int i = this.mPreviousDockState;
            this.mPreviousDockState = this.mReportedDockState;
            ContentResolver contentResolver = getContext().getContentResolver();
            if (!this.mDeviceProvisionedObserver.isDeviceProvisioned()) {
                Slog.i("DockObserver", "Device not provisioned, skipping dock broadcast");
                return;
            }
            Intent intent = new Intent("android.intent.action.DOCK_EVENT");
            intent.addFlags(536870912);
            intent.putExtra("android.intent.extra.DOCK_STATE", this.mReportedDockState);
            boolean z = Settings.Global.getInt(contentResolver, "dock_sounds_enabled", 1) == 1;
            boolean z2 = Settings.Global.getInt(contentResolver, "dock_sounds_enabled_when_accessbility", 1) == 1;
            boolean z3 = Settings.Secure.getInt(contentResolver, "accessibility_enabled", 0) == 1;
            if (z || (z3 && z2)) {
                int i2 = this.mReportedDockState;
                if (i2 == 0) {
                    if (i != 1 && i != 3 && i != 4) {
                        if (i == 2) {
                            str = "car_undock_sound";
                            if (str != null && (string = Settings.Global.getString(contentResolver, str)) != null) {
                                parse = Uri.parse("file://" + string);
                                if (parse != null && (ringtone = RingtoneManager.getRingtone(getContext(), parse)) != null) {
                                    ringtone.setStreamType(1);
                                    ringtone.preferBuiltinDevice(true);
                                    ringtone.play();
                                }
                            }
                        }
                        str = null;
                        if (str != null) {
                            parse = Uri.parse("file://" + string);
                            if (parse != null) {
                                ringtone.setStreamType(1);
                                ringtone.preferBuiltinDevice(true);
                                ringtone.play();
                            }
                        }
                    }
                    str = "desk_undock_sound";
                    if (str != null) {
                    }
                } else {
                    if (i2 != 1 && i2 != 3 && i2 != 4) {
                        if (i2 == 2) {
                            str = "car_dock_sound";
                            if (str != null) {
                            }
                        }
                        str = null;
                        if (str != null) {
                        }
                    }
                    str = "desk_dock_sound";
                    if (str != null) {
                    }
                }
            }
            getContext().sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x003f, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int getDockedStateExtraValue(ExtconStateProvider extconStateProvider) {
        for (ExtconStateConfig extconStateConfig : this.mExtconStateConfigs) {
            boolean z = true;
            for (Pair<String, String> pair : extconStateConfig.keyValuePairs) {
                String value = extconStateProvider.getValue((String) pair.first);
                if (z && ((String) pair.second).equals(value)) {
                    z = true;
                    continue;
                } else {
                    z = false;
                    continue;
                }
                if (!z) {
                    break;
                }
            }
            if (z) {
                return extconStateConfig.extraStateValue;
            }
        }
        return 1;
    }

    @VisibleForTesting
    public void setDockStateFromProviderForTesting(ExtconStateProvider extconStateProvider) {
        synchronized (this.mLock) {
            setDockStateFromProviderLocked(extconStateProvider);
        }
    }

    public final void setDockStateFromProviderLocked(ExtconStateProvider extconStateProvider) {
        setActualDockStateLocked("1".equals(extconStateProvider.getValue("DOCK")) ? getDockedStateExtraValue(extconStateProvider) : 0);
    }

    /* loaded from: classes.dex */
    public final class BinderService extends Binder {
        public BinderService() {
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(DockObserver.this.getContext(), "DockObserver", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    synchronized (DockObserver.this.mLock) {
                        if (strArr != null && strArr.length != 0 && !"-a".equals(strArr[0])) {
                            if (strArr.length == 3 && "set".equals(strArr[0])) {
                                String str = strArr[1];
                                String str2 = strArr[2];
                                try {
                                    if ("state".equals(str)) {
                                        DockObserver.this.mUpdatesStopped = true;
                                        DockObserver.this.setDockStateLocked(Integer.parseInt(str2));
                                    } else {
                                        printWriter.println("Unknown set option: " + str);
                                    }
                                } catch (NumberFormatException unused) {
                                    printWriter.println("Bad value: " + str2);
                                }
                            } else if (strArr.length == 1 && "reset".equals(strArr[0])) {
                                DockObserver.this.mUpdatesStopped = false;
                                DockObserver dockObserver = DockObserver.this;
                                dockObserver.setDockStateLocked(dockObserver.mActualDockState);
                            } else {
                                printWriter.println("Dump current dock state, or:");
                                printWriter.println("  set state <value>");
                                printWriter.println("  reset");
                            }
                        }
                        printWriter.println("Current Dock Observer Service state:");
                        if (DockObserver.this.mUpdatesStopped) {
                            printWriter.println("  (UPDATES STOPPED -- use 'reset' to restart)");
                        }
                        printWriter.println("  reported state: " + DockObserver.this.mReportedDockState);
                        printWriter.println("  previous state: " + DockObserver.this.mPreviousDockState);
                        printWriter.println("  actual state: " + DockObserver.this.mActualDockState);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class DeviceProvisionedObserver extends ContentObserver {
        public boolean mRegistered;

        public DeviceProvisionedObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            synchronized (DockObserver.this.mLock) {
                updateRegistration();
                if (isDeviceProvisioned()) {
                    DockObserver.this.updateIfDockedLocked();
                }
            }
        }

        public void onSystemReady() {
            updateRegistration();
        }

        public final void updateRegistration() {
            boolean z = !isDeviceProvisioned();
            if (z == this.mRegistered) {
                return;
            }
            ContentResolver contentResolver = DockObserver.this.getContext().getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
            this.mRegistered = z;
        }

        public boolean isDeviceProvisioned() {
            return Settings.Global.getInt(DockObserver.this.getContext().getContentResolver(), "device_provisioned", 0) != 0;
        }
    }
}
