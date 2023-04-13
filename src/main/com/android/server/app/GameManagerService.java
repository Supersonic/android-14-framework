package com.android.server.app;

import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.GameManagerInternal;
import android.app.GameModeConfiguration;
import android.app.GameModeInfo;
import android.app.GameState;
import android.app.IGameManagerService;
import android.app.IGameModeListener;
import android.app.IUidObserver;
import android.app.StatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.KeyValueListParser;
import android.util.Slog;
import android.util.StatsEvent;
import android.util.Xml;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.app.GameManagerService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class GameManagerService extends IGameManagerService.Stub {
    @GuardedBy({"mDeviceConfigLock"})
    public final ArrayMap<String, GamePackageConfiguration> mConfigs;
    public final Context mContext;
    public DeviceConfigListener mDeviceConfigListener;
    public final Object mDeviceConfigLock;
    @GuardedBy({"mUidObserverLock"})
    public final Set<Integer> mForegroundGameUids;
    @VisibleForTesting
    final AtomicFile mGameModeInterventionListFile;
    public final Object mGameModeListenerLock;
    @GuardedBy({"mGameModeListenerLock"})
    public final ArrayMap<IGameModeListener, Integer> mGameModeListeners;
    public final GameServiceController mGameServiceController;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final Handler mHandler;
    public final Object mLock;
    public final PackageManager mPackageManager;
    public final PowerManagerInternal mPowerManagerInternal;
    @GuardedBy({"mLock"})
    public final ArrayMap<Integer, GameManagerSettings> mSettings;
    public final File mSystemDir;
    @VisibleForTesting
    final UidObserver mUidObserver;
    public final Object mUidObserverLock;
    public final UserManager mUserManager;

    public static int gameModeToStatsdGameMode(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return i != 4 ? 0 : 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public static int gameStateModeToStatsdGameState(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    i2 = 4;
                    if (i != 4) {
                        return 0;
                    }
                }
            }
        }
        return i2;
    }

    public static int modeToBitmask(int i) {
        return 1 << i;
    }

    private static native void nativeSetOverrideFrameRate(int i, float f);

    @RequiresPermission("android.permission.WRITE_SECURE_SETTINGS")
    public final void updateUseAngle(String str, int i) {
    }

    public GameManagerService(Context context) {
        this(context, createServiceThread().getLooper());
    }

    public GameManagerService(Context context, Looper looper) {
        this(context, looper, Environment.getDataDirectory());
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public GameManagerService(Context context, Looper looper, File file) {
        this.mLock = new Object();
        this.mDeviceConfigLock = new Object();
        this.mGameModeListenerLock = new Object();
        this.mSettings = new ArrayMap<>();
        this.mConfigs = new ArrayMap<>();
        this.mGameModeListeners = new ArrayMap<>();
        this.mUidObserverLock = new Object();
        this.mForegroundGameUids = new HashSet();
        this.mContext = context;
        this.mHandler = new SettingsHandler(looper);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        File file2 = new File(file, "system");
        this.mSystemDir = file2;
        file2.mkdirs();
        FileUtils.setPermissions(file2.toString(), 509, -1, -1);
        AtomicFile atomicFile = new AtomicFile(new File(file2, "game_mode_intervention.list"));
        this.mGameModeInterventionListFile = atomicFile;
        FileUtils.setPermissions(atomicFile.getBaseFile().getAbsolutePath(), FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_RESTARTED, -1, -1);
        if (context.getPackageManager().hasSystemFeature("android.software.game_service")) {
            this.mGameServiceController = new GameServiceController(context, BackgroundThread.getExecutor(), new GameServiceProviderSelectorImpl(context.getResources(), context.getPackageManager()), new GameServiceProviderInstanceFactoryImpl(context));
        } else {
            this.mGameServiceController = null;
        }
        UidObserver uidObserver = new UidObserver();
        this.mUidObserver = uidObserver;
        try {
            ActivityManager.getService().registerUidObserver(uidObserver, 3, -1, (String) null);
        } catch (RemoteException unused) {
            Slog.w("GameManagerService", "Could not register UidObserver");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new GameManagerShellCommand().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            printWriter.println("Permission Denial: can't dump GameManagerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission android.permission.DUMP");
        } else if (strArr == null || strArr.length == 0) {
            printWriter.println("*Dump GameManagerService*");
            dumpAllGameConfigs(printWriter);
        }
    }

    public final void dumpAllGameConfigs(PrintWriter printWriter) {
        int currentUser = ActivityManager.getCurrentUser();
        for (String str : getInstalledGamePackageNames(currentUser)) {
            printWriter.println(getInterventionList(str, currentUser));
        }
    }

    /* loaded from: classes.dex */
    public class SettingsHandler extends Handler {
        public SettingsHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            doHandleMessage(message);
        }

        public void doHandleMessage(Message message) {
            int i;
            switch (message.what) {
                case 1:
                    int intValue = ((Integer) message.obj).intValue();
                    if (intValue < 0) {
                        Slog.wtf("GameManagerService", "Attempt to write settings for invalid user: " + intValue);
                        synchronized (GameManagerService.this.mLock) {
                            removeEqualMessages(1, message.obj);
                        }
                        return;
                    }
                    Process.setThreadPriority(0);
                    synchronized (GameManagerService.this.mLock) {
                        removeEqualMessages(1, message.obj);
                        if (GameManagerService.this.mSettings.containsKey(Integer.valueOf(intValue))) {
                            ((GameManagerSettings) GameManagerService.this.mSettings.get(Integer.valueOf(intValue))).writePersistentDataLocked();
                        }
                    }
                    Process.setThreadPriority(10);
                    return;
                case 2:
                    int intValue2 = ((Integer) message.obj).intValue();
                    if (intValue2 < 0) {
                        Slog.wtf("GameManagerService", "Attempt to write settings for invalid user: " + intValue2);
                        synchronized (GameManagerService.this.mLock) {
                            removeEqualMessages(1, message.obj);
                            removeEqualMessages(2, message.obj);
                        }
                        return;
                    }
                    synchronized (GameManagerService.this.mLock) {
                        removeEqualMessages(1, message.obj);
                        removeEqualMessages(2, message.obj);
                        if (GameManagerService.this.mSettings.containsKey(Integer.valueOf(intValue2))) {
                            GameManagerService.this.mSettings.remove(Integer.valueOf(intValue2));
                            ((GameManagerSettings) GameManagerService.this.mSettings.get(Integer.valueOf(intValue2))).writePersistentDataLocked();
                        }
                    }
                    return;
                case 3:
                    removeEqualMessages(3, message.obj);
                    int intValue3 = ((Integer) message.obj).intValue();
                    GameManagerService.this.updateConfigsForUser(intValue3, false, GameManagerService.this.getInstalledGamePackageNames(intValue3));
                    return;
                case 4:
                    GameState gameState = (GameState) message.obj;
                    boolean isLoading = gameState.isLoading();
                    Bundle data = message.getData();
                    String string = data.getString("packageName");
                    int i2 = data.getInt("userId");
                    boolean z = GameManagerService.this.getGameMode(string, i2) == 2;
                    try {
                        i = GameManagerService.this.mPackageManager.getPackageUidAsUser(string, i2);
                    } catch (PackageManager.NameNotFoundException unused) {
                        Slog.v("GameManagerService", "Failed to get package metadata");
                        i = -1;
                    }
                    FrameworkStatsLog.write((int) FrameworkStatsLog.GAME_STATE_CHANGED, string, i, z, GameManagerService.gameStateModeToStatsdGameState(gameState.getMode()), isLoading, gameState.getLabel(), gameState.getQuality());
                    if (z) {
                        if (GameManagerService.this.mPowerManagerInternal == null) {
                            Slog.d("GameManagerService", "Error setting loading mode for package " + string + " and userId " + i2);
                            return;
                        }
                        if (GameManagerService.this.mHandler.hasMessages(5)) {
                            GameManagerService.this.mHandler.removeMessages(5);
                        }
                        GameManagerService.this.mPowerManagerInternal.setPowerMode(16, isLoading);
                        if (isLoading) {
                            int loadingBoostDuration = GameManagerService.this.getLoadingBoostDuration(string, i2);
                            if (loadingBoostDuration <= 0) {
                                loadingBoostDuration = 5000;
                            }
                            Handler handler = GameManagerService.this.mHandler;
                            handler.sendMessageDelayed(handler.obtainMessage(5), loadingBoostDuration);
                            return;
                        }
                        return;
                    }
                    return;
                case 5:
                    GameManagerService.this.mPowerManagerInternal.setPowerMode(16, false);
                    return;
                case 6:
                    int intValue4 = ((Integer) message.obj).intValue();
                    if (intValue4 < 0) {
                        Slog.wtf("GameManagerService", "Attempt to write setting for invalid user: " + intValue4);
                        synchronized (GameManagerService.this.mLock) {
                            removeEqualMessages(6, message.obj);
                        }
                        return;
                    }
                    Process.setThreadPriority(0);
                    removeEqualMessages(6, message.obj);
                    GameManagerService.this.writeGameModeInterventionsToFile(intValue4);
                    Process.setThreadPriority(10);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes.dex */
    public class DeviceConfigListener implements DeviceConfig.OnPropertiesChangedListener {
        public DeviceConfigListener() {
            DeviceConfig.addOnPropertiesChangedListener("game_overlay", GameManagerService.this.mContext.getMainExecutor(), this);
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            GameManagerService.this.updateConfigsForUser(ActivityManager.getCurrentUser(), true, (String[]) properties.getKeyset().toArray(new String[0]));
        }

        public void finalize() {
            DeviceConfig.removeOnPropertiesChangedListener(this);
        }
    }

    /* loaded from: classes.dex */
    public enum FrameRate {
        FPS_DEFAULT(0),
        FPS_30(30),
        FPS_40(40),
        FPS_45(45),
        FPS_60(60),
        FPS_90(90),
        FPS_120(120),
        FPS_INVALID(-1);
        
        public final int fps;

        FrameRate(int i) {
            this.fps = i;
        }
    }

    public static int getFpsInt(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 0:
                if (str.equals("")) {
                    c = 0;
                    break;
                }
                break;
            case 1629:
                if (str.equals("30")) {
                    c = 1;
                    break;
                }
                break;
            case 1660:
                if (str.equals("40")) {
                    c = 2;
                    break;
                }
                break;
            case 1665:
                if (str.equals("45")) {
                    c = 3;
                    break;
                }
                break;
            case 1722:
                if (str.equals("60")) {
                    c = 4;
                    break;
                }
                break;
            case 1815:
                if (str.equals("90")) {
                    c = 5;
                    break;
                }
                break;
            case 48687:
                if (str.equals("120")) {
                    c = 6;
                    break;
                }
                break;
            case 1671308008:
                if (str.equals("disable")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 7:
                return FrameRate.FPS_DEFAULT.fps;
            case 1:
                return FrameRate.FPS_30.fps;
            case 2:
                return FrameRate.FPS_40.fps;
            case 3:
                return FrameRate.FPS_45.fps;
            case 4:
                return FrameRate.FPS_60.fps;
            case 5:
                return FrameRate.FPS_90.fps;
            case 6:
                return FrameRate.FPS_120.fps;
            default:
                return FrameRate.FPS_INVALID.fps;
        }
    }

    public void setGameState(String str, GameState gameState, int i) {
        if (!lambda$updateConfigsForUser$0(str, i)) {
            Slog.d("GameManagerService", "No-op for attempt to set game state for non-game app: " + str);
            return;
        }
        Message obtainMessage = this.mHandler.obtainMessage(4);
        Bundle bundle = new Bundle();
        bundle.putString("packageName", str);
        bundle.putInt("userId", i);
        obtainMessage.setData(bundle);
        obtainMessage.obj = gameState;
        this.mHandler.sendMessage(obtainMessage);
    }

    /* loaded from: classes.dex */
    public static class GamePackageConfiguration {
        public boolean mAllowAngle;
        public boolean mAllowDownscale;
        public boolean mAllowFpsOverride;
        public boolean mBatteryModeOverridden;
        public final Object mModeConfigLock;
        @GuardedBy({"mModeConfigLock"})
        public final ArrayMap<Integer, GameModeConfiguration> mModeConfigs;
        public final String mPackageName;
        public boolean mPerfModeOverridden;

        public GamePackageConfiguration(String str) {
            this.mModeConfigLock = new Object();
            this.mModeConfigs = new ArrayMap<>();
            this.mPerfModeOverridden = false;
            this.mBatteryModeOverridden = false;
            this.mAllowDownscale = true;
            this.mAllowAngle = true;
            this.mAllowFpsOverride = true;
            this.mPackageName = str;
        }

        public GamePackageConfiguration(PackageManager packageManager, String str, int i) {
            String[] split;
            Bundle bundle;
            this.mModeConfigLock = new Object();
            this.mModeConfigs = new ArrayMap<>();
            this.mPerfModeOverridden = false;
            this.mBatteryModeOverridden = false;
            this.mAllowDownscale = true;
            this.mAllowAngle = true;
            this.mAllowFpsOverride = true;
            this.mPackageName = str;
            try {
                ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(str, 128, i);
                if (!parseInterventionFromXml(packageManager, applicationInfoAsUser, str) && (bundle = applicationInfoAsUser.metaData) != null) {
                    this.mPerfModeOverridden = bundle.getBoolean("com.android.app.gamemode.performance.enabled");
                    this.mBatteryModeOverridden = applicationInfoAsUser.metaData.getBoolean("com.android.app.gamemode.battery.enabled");
                    this.mAllowDownscale = applicationInfoAsUser.metaData.getBoolean("com.android.graphics.intervention.wm.allowDownscale", true);
                    this.mAllowAngle = applicationInfoAsUser.metaData.getBoolean("com.android.graphics.intervention.angle.allowAngle", true);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Slog.v("GameManagerService_GamePackageConfiguration", "Failed to get package metadata");
            }
            String property = DeviceConfig.getProperty("game_overlay", str);
            if (property != null) {
                for (String str2 : property.split(XmlUtils.STRING_ARRAY_SEPARATOR)) {
                    try {
                        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
                        keyValueListParser.setString(str2);
                        addModeConfig(new GameModeConfiguration(keyValueListParser));
                    } catch (IllegalArgumentException unused2) {
                        Slog.e("GameManagerService_GamePackageConfiguration", "Invalid config string");
                    }
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:42:0x007f A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final boolean parseInterventionFromXml(PackageManager packageManager, ApplicationInfo applicationInfo, String str) {
            boolean z;
            try {
                XmlResourceParser loadXmlMetaData = applicationInfo.loadXmlMetaData(packageManager, "android.game_mode_config");
                if (loadXmlMetaData == null) {
                    try {
                        Slog.v("GameManagerService_GamePackageConfiguration", "No android.game_mode_config meta-data found for package " + this.mPackageName);
                        z = false;
                    } catch (Throwable th) {
                        th = th;
                        if (loadXmlMetaData != null) {
                            try {
                                loadXmlMetaData.close();
                            }
                        }
                        throw th;
                    }
                } else {
                    try {
                        Resources resourcesForApplication = packageManager.getResourcesForApplication(str);
                        AttributeSet asAttributeSet = Xml.asAttributeSet(loadXmlMetaData);
                        while (true) {
                            int next = loadXmlMetaData.next();
                            if (next == 1 || next == 2) {
                                break;
                            }
                        }
                        if (!"game-mode-config".equals(loadXmlMetaData.getName())) {
                            Slog.w("GameManagerService_GamePackageConfiguration", "Meta-data does not start with game-mode-config tag");
                        } else {
                            TypedArray obtainAttributes = resourcesForApplication.obtainAttributes(asAttributeSet, R.styleable.GameModeConfig);
                            this.mPerfModeOverridden = obtainAttributes.getBoolean(1, false);
                            this.mBatteryModeOverridden = obtainAttributes.getBoolean(0, false);
                            this.mAllowDownscale = obtainAttributes.getBoolean(3, true);
                            this.mAllowAngle = obtainAttributes.getBoolean(2, true);
                            this.mAllowFpsOverride = obtainAttributes.getBoolean(4, true);
                            obtainAttributes.recycle();
                        }
                        z = true;
                    } catch (Throwable th2) {
                        th = th2;
                        if (loadXmlMetaData != null) {
                        }
                        throw th;
                    }
                }
                if (loadXmlMetaData != null) {
                    try {
                        loadXmlMetaData.close();
                    } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException unused) {
                        this.mPerfModeOverridden = false;
                        this.mBatteryModeOverridden = false;
                        this.mAllowDownscale = true;
                        this.mAllowAngle = true;
                        this.mAllowFpsOverride = true;
                        Slog.e("GameManagerService_GamePackageConfiguration", "Error while parsing XML meta-data for android.game_mode_config");
                        return z;
                    }
                }
            } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException unused2) {
                z = false;
            }
            return z;
        }

        public GameModeConfiguration getOrAddDefaultGameModeConfiguration(int i) {
            GameModeConfiguration gameModeConfiguration;
            synchronized (this.mModeConfigLock) {
                this.mModeConfigs.putIfAbsent(Integer.valueOf(i), new GameModeConfiguration(i));
                gameModeConfiguration = this.mModeConfigs.get(Integer.valueOf(i));
            }
            return gameModeConfiguration;
        }

        public boolean hasActiveGameModeConfig() {
            boolean z;
            synchronized (this.mModeConfigLock) {
                z = !this.mModeConfigs.isEmpty();
            }
            return z;
        }

        /* loaded from: classes.dex */
        public class GameModeConfiguration {
            public String mFps;
            public final int mGameMode;
            public int mLoadingBoostDuration;
            public float mScaling;
            public boolean mUseAngle;

            public GameModeConfiguration(int i) {
                this.mScaling = -1.0f;
                this.mFps = "";
                this.mGameMode = i;
                this.mUseAngle = false;
                this.mLoadingBoostDuration = -1;
            }

            public GameModeConfiguration(KeyValueListParser keyValueListParser) {
                float f = -1.0f;
                this.mScaling = -1.0f;
                String str = "";
                this.mFps = "";
                boolean z = false;
                int i = keyValueListParser.getInt("mode", 0);
                this.mGameMode = i;
                if (GamePackageConfiguration.this.mAllowDownscale && !GamePackageConfiguration.this.willGamePerformOptimizations(i)) {
                    f = keyValueListParser.getFloat("downscaleFactor", -1.0f);
                }
                this.mScaling = f;
                if (GamePackageConfiguration.this.mAllowFpsOverride && !GamePackageConfiguration.this.willGamePerformOptimizations(i)) {
                    str = keyValueListParser.getString("fps", "");
                }
                this.mFps = str;
                if (GamePackageConfiguration.this.mAllowAngle && !GamePackageConfiguration.this.willGamePerformOptimizations(i) && keyValueListParser.getBoolean("useAngle", false)) {
                    z = true;
                }
                this.mUseAngle = z;
                this.mLoadingBoostDuration = GamePackageConfiguration.this.willGamePerformOptimizations(i) ? -1 : keyValueListParser.getInt("loadingBoost", -1);
            }

            public int getGameMode() {
                return this.mGameMode;
            }

            public synchronized float getScaling() {
                return this.mScaling;
            }

            public synchronized int getFps() {
                return GameManagerService.getFpsInt(this.mFps);
            }

            public synchronized String getFpsStr() {
                return this.mFps;
            }

            public synchronized boolean getUseAngle() {
                return this.mUseAngle;
            }

            public synchronized int getLoadingBoostDuration() {
                return this.mLoadingBoostDuration;
            }

            public synchronized void setScaling(float f) {
                this.mScaling = f;
            }

            public synchronized void setFpsStr(String str) {
                this.mFps = str;
            }

            public synchronized void setUseAngle(boolean z) {
                this.mUseAngle = z;
            }

            public synchronized void setLoadingBoostDuration(int i) {
                this.mLoadingBoostDuration = i;
            }

            public boolean isActive() {
                int i = this.mGameMode;
                return (i == 1 || i == 2 || i == 3 || i == 4) && !GamePackageConfiguration.this.willGamePerformOptimizations(i);
            }

            public android.app.GameModeConfiguration toPublicGameModeConfig() {
                int fpsInt = GameManagerService.getFpsInt(this.mFps);
                if (fpsInt <= 0) {
                    fpsInt = 0;
                }
                float f = this.mScaling;
                if (f == -1.0f) {
                    f = 1.0f;
                }
                return new GameModeConfiguration.Builder().setScalingFactor(f).setFpsOverride(fpsInt).build();
            }

            public void updateFromPublicGameModeConfig(android.app.GameModeConfiguration gameModeConfiguration) {
                this.mScaling = gameModeConfiguration.getScalingFactor();
                this.mFps = String.valueOf(gameModeConfiguration.getFpsOverride());
            }

            public String toString() {
                return "[Game Mode:" + this.mGameMode + ",Scaling:" + this.mScaling + ",Use Angle:" + this.mUseAngle + ",Fps:" + this.mFps + ",Loading Boost Duration:" + this.mLoadingBoostDuration + "]";
            }
        }

        public boolean willGamePerformOptimizations(int i) {
            return (this.mBatteryModeOverridden && i == 3) || (this.mPerfModeOverridden && i == 2);
        }

        public final int getAvailableGameModesBitfield() {
            int modeToBitmask = GameManagerService.modeToBitmask(4) | GameManagerService.modeToBitmask(1);
            synchronized (this.mModeConfigLock) {
                for (Integer num : this.mModeConfigs.keySet()) {
                    modeToBitmask |= GameManagerService.modeToBitmask(num.intValue());
                }
            }
            if (this.mBatteryModeOverridden) {
                modeToBitmask |= GameManagerService.modeToBitmask(3);
            }
            return this.mPerfModeOverridden ? modeToBitmask | GameManagerService.modeToBitmask(2) : modeToBitmask;
        }

        public int[] getAvailableGameModes() {
            int availableGameModesBitfield = getAvailableGameModesBitfield();
            int[] iArr = new int[Integer.bitCount(availableGameModesBitfield)];
            int numberOfTrailingZeros = Integer.numberOfTrailingZeros(Integer.highestOneBit(availableGameModesBitfield));
            int i = 0;
            for (int i2 = 0; i2 <= numberOfTrailingZeros; i2++) {
                if (((availableGameModesBitfield >> i2) & 1) != 0) {
                    iArr[i] = i2;
                    i++;
                }
            }
            return iArr;
        }

        public int[] getOverriddenGameModes() {
            boolean z = this.mBatteryModeOverridden;
            if (z && this.mPerfModeOverridden) {
                return new int[]{3, 2};
            }
            if (z) {
                return new int[]{3};
            }
            return this.mPerfModeOverridden ? new int[]{2} : new int[0];
        }

        public GameModeConfiguration getGameModeConfiguration(int i) {
            GameModeConfiguration gameModeConfiguration;
            synchronized (this.mModeConfigLock) {
                gameModeConfiguration = this.mModeConfigs.get(Integer.valueOf(i));
            }
            return gameModeConfiguration;
        }

        public void addModeConfig(GameModeConfiguration gameModeConfiguration) {
            if (gameModeConfiguration.isActive()) {
                synchronized (this.mModeConfigLock) {
                    this.mModeConfigs.put(Integer.valueOf(gameModeConfiguration.getGameMode()), gameModeConfiguration);
                }
                return;
            }
            Slog.w("GameManagerService_GamePackageConfiguration", "Attempt to add inactive game mode config for " + this.mPackageName + XmlUtils.STRING_ARRAY_SEPARATOR + gameModeConfiguration.toString());
        }

        public void removeModeConfig(int i) {
            synchronized (this.mModeConfigLock) {
                this.mModeConfigs.remove(Integer.valueOf(i));
            }
        }

        public boolean isActive() {
            boolean z;
            synchronized (this.mModeConfigLock) {
                z = this.mModeConfigs.size() > 0 || this.mBatteryModeOverridden || this.mPerfModeOverridden;
            }
            return z;
        }

        public GamePackageConfiguration copyAndApplyOverride(GamePackageConfiguration gamePackageConfiguration) {
            GamePackageConfiguration gamePackageConfiguration2 = new GamePackageConfiguration(this.mPackageName);
            boolean z = true;
            gamePackageConfiguration2.mPerfModeOverridden = this.mPerfModeOverridden && (gamePackageConfiguration == null || gamePackageConfiguration.getGameModeConfiguration(2) == null);
            gamePackageConfiguration2.mBatteryModeOverridden = this.mBatteryModeOverridden && (gamePackageConfiguration == null || gamePackageConfiguration.getGameModeConfiguration(3) == null);
            gamePackageConfiguration2.mAllowDownscale = this.mAllowDownscale || gamePackageConfiguration != null;
            gamePackageConfiguration2.mAllowAngle = this.mAllowAngle || gamePackageConfiguration != null;
            if (!this.mAllowFpsOverride && gamePackageConfiguration == null) {
                z = false;
            }
            gamePackageConfiguration2.mAllowFpsOverride = z;
            if (gamePackageConfiguration != null) {
                synchronized (gamePackageConfiguration2.mModeConfigLock) {
                    synchronized (this.mModeConfigLock) {
                        for (Map.Entry<Integer, GameModeConfiguration> entry : this.mModeConfigs.entrySet()) {
                            gamePackageConfiguration2.mModeConfigs.put(entry.getKey(), entry.getValue());
                        }
                    }
                    synchronized (gamePackageConfiguration.mModeConfigLock) {
                        for (Map.Entry<Integer, GameModeConfiguration> entry2 : gamePackageConfiguration.mModeConfigs.entrySet()) {
                            gamePackageConfiguration2.mModeConfigs.put(entry2.getKey(), entry2.getValue());
                        }
                    }
                }
            }
            return gamePackageConfiguration2;
        }

        public String toString() {
            String str;
            synchronized (this.mModeConfigLock) {
                str = "[Name:" + this.mPackageName + " Modes: " + this.mModeConfigs.toString() + "]";
            }
            return str;
        }
    }

    /* loaded from: classes.dex */
    public final class LocalService extends GameManagerInternal {
        public LocalService() {
        }

        public float getResolutionScalingFactor(String str, int i) {
            return GameManagerService.this.getResolutionScalingFactorInternal(str, GameManagerService.this.getGameModeFromSettingsUnchecked(str, i), i);
        }
    }

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public GameManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new GameManagerService(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("game", this.mService);
            this.mService.publishLocalService();
            this.mService.registerDeviceConfigListener();
            this.mService.registerPackageReceiver();
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 1000) {
                this.mService.onBootCompleted();
                this.mService.registerStatsCallbacks();
            }
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            Slog.d("GameManagerService", "Starting user " + targetUser.getUserIdentifier());
            this.mService.onUserStarting(targetUser, Environment.getDataSystemDeDirectory(targetUser.getUserIdentifier()));
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mService.onUserUnlocking(targetUser);
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            this.mService.onUserStopping(targetUser);
        }

        @Override // com.android.server.SystemService
        public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
            this.mService.onUserSwitching(targetUser, targetUser2);
        }
    }

    public final boolean isValidPackageName(String str, int i) {
        try {
            return this.mPackageManager.getPackageUidAsUser(str, i) == Binder.getCallingUid();
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final void checkPermission(String str) throws SecurityException {
        if (this.mContext.checkCallingOrSelfPermission(str) == 0) {
            return;
        }
        throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + str);
    }

    public final int[] getAvailableGameModesUnchecked(String str, int i) {
        GamePackageConfiguration config = getConfig(str, i);
        if (config == null) {
            return new int[]{1, 4};
        }
        return config.getAvailableGameModes();
    }

    /* renamed from: isPackageGame */
    public final boolean lambda$updateConfigsForUser$0(String str, int i) {
        try {
            return this.mPackageManager.getApplicationInfoAsUser(str, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, i).category == 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public int[] getAvailableGameModes(String str, int i) throws SecurityException {
        checkPermission("android.permission.MANAGE_GAME_MODE");
        return !lambda$updateConfigsForUser$0(str, i) ? new int[0] : getAvailableGameModesUnchecked(str, i);
    }

    public final int getGameModeFromSettingsUnchecked(String str, int i) {
        synchronized (this.mLock) {
            if (!this.mSettings.containsKey(Integer.valueOf(i))) {
                Slog.d("GameManagerService", "User ID '" + i + "' does not have a Game Mode selected for package: '" + str + "'");
                return 1;
            }
            return this.mSettings.get(Integer.valueOf(i)).getGameModeLocked(str);
        }
    }

    public int getGameMode(String str, int i) throws SecurityException {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "getGameMode", "com.android.server.app.GameManagerService");
        if (lambda$updateConfigsForUser$0(str, handleIncomingUser)) {
            if (isValidPackageName(str, handleIncomingUser)) {
                return getGameModeFromSettingsUnchecked(str, handleIncomingUser);
            }
            checkPermission("android.permission.MANAGE_GAME_MODE");
            return getGameModeFromSettingsUnchecked(str, handleIncomingUser);
        }
        return 0;
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public GameModeInfo getGameModeInfo(String str, int i) {
        GamePackageConfiguration.GameModeConfiguration gameModeConfiguration;
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "getGameModeInfo", "com.android.server.app.GameManagerService");
        checkPermission("android.permission.MANAGE_GAME_MODE");
        if (lambda$updateConfigsForUser$0(str, handleIncomingUser)) {
            int gameModeFromSettingsUnchecked = getGameModeFromSettingsUnchecked(str, handleIncomingUser);
            GamePackageConfiguration config = getConfig(str, handleIncomingUser);
            if (config != null) {
                int[] overriddenGameModes = config.getOverriddenGameModes();
                int[] availableGameModes = config.getAvailableGameModes();
                GameModeInfo.Builder fpsOverrideAllowed = new GameModeInfo.Builder().setActiveGameMode(gameModeFromSettingsUnchecked).setAvailableGameModes(availableGameModes).setOverriddenGameModes(overriddenGameModes).setDownscalingAllowed(config.mAllowDownscale).setFpsOverrideAllowed(config.mAllowFpsOverride);
                for (int i2 : availableGameModes) {
                    if (!config.willGamePerformOptimizations(i2) && (gameModeConfiguration = config.getGameModeConfiguration(i2)) != null) {
                        fpsOverrideAllowed.setGameModeConfiguration(i2, gameModeConfiguration.toPublicGameModeConfig());
                    }
                }
                return fpsOverrideAllowed.build();
            }
            return new GameModeInfo.Builder().setActiveGameMode(gameModeFromSettingsUnchecked).setAvailableGameModes(getAvailableGameModesUnchecked(str, handleIncomingUser)).build();
        }
        return null;
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public void setGameMode(String str, int i, int i2) throws SecurityException {
        int i3;
        checkPermission("android.permission.MANAGE_GAME_MODE");
        if (i == 0) {
            Slog.d("GameManagerService", "No-op for attempt to set UNSUPPORTED mode for app: " + str);
        } else if (lambda$updateConfigsForUser$0(str, i2)) {
            synchronized (this.mLock) {
                int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "setGameMode", "com.android.server.app.GameManagerService");
                if (!this.mSettings.containsKey(Integer.valueOf(handleIncomingUser))) {
                    Slog.d("GameManagerService", "Failed to set game mode for package " + str + " as user " + handleIncomingUser + " is not started");
                    return;
                }
                GameManagerSettings gameManagerSettings = this.mSettings.get(Integer.valueOf(handleIncomingUser));
                int gameModeLocked = gameManagerSettings.getGameModeLocked(str);
                gameManagerSettings.setGameModeLocked(str, i);
                updateInterventions(str, i, handleIncomingUser);
                synchronized (this.mGameModeListenerLock) {
                    for (IGameModeListener iGameModeListener : this.mGameModeListeners.keySet()) {
                        Binder.allowBlocking(iGameModeListener.asBinder());
                        try {
                            iGameModeListener.onGameModeChanged(str, gameModeLocked, i, handleIncomingUser);
                        } catch (RemoteException unused) {
                            Slog.w("GameManagerService", "Cannot notify game mode change for listener added by " + this.mGameModeListeners.get(iGameModeListener));
                        }
                    }
                }
                sendUserMessage(handleIncomingUser, 1, "SET_GAME_MODE", FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                sendUserMessage(handleIncomingUser, 6, "SET_GAME_MODE", 0);
                try {
                    i3 = this.mPackageManager.getPackageUidAsUser(str, handleIncomingUser);
                } catch (PackageManager.NameNotFoundException unused2) {
                    Slog.d("GameManagerService", "Cannot find the UID for package " + str + " under user " + handleIncomingUser);
                    i3 = -1;
                }
                FrameworkStatsLog.write((int) FrameworkStatsLog.GAME_MODE_CHANGED, i3, Binder.getCallingUid(), gameModeToStatsdGameMode(gameModeLocked), gameModeToStatsdGameMode(i));
            }
        } else {
            Slog.d("GameManagerService", "No-op for attempt to set game mode for non-game app: " + str);
        }
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public boolean isAngleEnabled(String str, int i) throws SecurityException {
        int gameMode = getGameMode(str, i);
        if (gameMode == 0) {
            return false;
        }
        synchronized (this.mDeviceConfigLock) {
            GamePackageConfiguration gamePackageConfiguration = this.mConfigs.get(str);
            if (gamePackageConfiguration == null) {
                return false;
            }
            GamePackageConfiguration.GameModeConfiguration gameModeConfiguration = gamePackageConfiguration.getGameModeConfiguration(gameMode);
            if (gameModeConfiguration == null) {
                return false;
            }
            return gameModeConfiguration.getUseAngle();
        }
    }

    public int getLoadingBoostDuration(String str, int i) throws SecurityException {
        GamePackageConfiguration gamePackageConfiguration;
        GamePackageConfiguration.GameModeConfiguration gameModeConfiguration;
        int gameMode = getGameMode(str, i);
        if (gameMode == 0) {
            return -1;
        }
        synchronized (this.mDeviceConfigLock) {
            gamePackageConfiguration = this.mConfigs.get(str);
        }
        if (gamePackageConfiguration == null || (gameModeConfiguration = gamePackageConfiguration.getGameModeConfiguration(gameMode)) == null) {
            return -1;
        }
        return gameModeConfiguration.getLoadingBoostDuration();
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public void notifyGraphicsEnvironmentSetup(String str, int i) throws SecurityException {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "notifyGraphicsEnvironmentSetup", "com.android.server.app.GameManagerService");
        if (!isValidPackageName(str, handleIncomingUser)) {
            Slog.d("GameManagerService", "No-op for attempt to notify graphics env setup for different packagethan caller with uid: " + Binder.getCallingUid());
        } else if (getGameMode(str, handleIncomingUser) == 0) {
            Slog.d("GameManagerService", "No-op for attempt to notify graphics env setup for non-game app: " + str);
        } else {
            int loadingBoostDuration = getLoadingBoostDuration(str, handleIncomingUser);
            if (loadingBoostDuration != -1) {
                if (loadingBoostDuration == 0 || loadingBoostDuration > 5000) {
                    loadingBoostDuration = 5000;
                }
                if (this.mHandler.hasMessages(5)) {
                    this.mHandler.removeMessages(5);
                } else {
                    this.mPowerManagerInternal.setPowerMode(16, true);
                }
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(5), loadingBoostDuration);
            }
        }
    }

    @RequiresPermission("android.permission.SET_GAME_SERVICE")
    public void setGameServiceProvider(String str) throws SecurityException {
        checkPermission("android.permission.SET_GAME_SERVICE");
        GameServiceController gameServiceController = this.mGameServiceController;
        if (gameServiceController == null) {
            return;
        }
        gameServiceController.setGameServiceProvider(str);
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public void updateResolutionScalingFactor(String str, int i, float f, int i2) throws SecurityException, IllegalArgumentException {
        checkPermission("android.permission.MANAGE_GAME_MODE");
        synchronized (this.mLock) {
            if (!this.mSettings.containsKey(Integer.valueOf(i2))) {
                throw new IllegalArgumentException("User " + i2 + " wasn't started");
            }
        }
        setGameModeConfigOverride(str, i2, i, null, Float.toString(f));
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public float getResolutionScalingFactor(String str, int i, int i2) throws SecurityException, IllegalArgumentException {
        checkPermission("android.permission.MANAGE_GAME_MODE");
        synchronized (this.mLock) {
            if (!this.mSettings.containsKey(Integer.valueOf(i2))) {
                throw new IllegalArgumentException("User " + i2 + " wasn't started");
            }
        }
        return getResolutionScalingFactorInternal(str, i, i2);
    }

    public float getResolutionScalingFactorInternal(String str, int i, int i2) {
        GamePackageConfiguration.GameModeConfiguration gameModeConfiguration;
        GamePackageConfiguration config = getConfig(str, i2);
        if (config == null || (gameModeConfiguration = config.getGameModeConfiguration(i)) == null) {
            return -1.0f;
        }
        return gameModeConfiguration.getScaling();
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public void updateCustomGameModeConfiguration(String str, GameModeConfiguration gameModeConfiguration, int i) throws SecurityException, IllegalArgumentException {
        int i2;
        checkPermission("android.permission.MANAGE_GAME_MODE");
        if (!lambda$updateConfigsForUser$0(str, i)) {
            Slog.d("GameManagerService", "No-op for attempt to update custom game mode for non-game app: " + str);
            return;
        }
        synchronized (this.mLock) {
            if (!this.mSettings.containsKey(Integer.valueOf(i))) {
                throw new IllegalArgumentException("User " + i + " wasn't started");
            }
        }
        synchronized (this.mLock) {
            if (this.mSettings.containsKey(Integer.valueOf(i))) {
                GameManagerSettings gameManagerSettings = this.mSettings.get(Integer.valueOf(i));
                GamePackageConfiguration configOverride = gameManagerSettings.getConfigOverride(str);
                if (configOverride == null) {
                    configOverride = new GamePackageConfiguration(str);
                    gameManagerSettings.setConfigOverride(str, configOverride);
                }
                GamePackageConfiguration.GameModeConfiguration orAddDefaultGameModeConfiguration = configOverride.getOrAddDefaultGameModeConfiguration(4);
                float scaling = orAddDefaultGameModeConfiguration.getScaling();
                int fps = orAddDefaultGameModeConfiguration.getFps();
                orAddDefaultGameModeConfiguration.updateFromPublicGameModeConfig(gameModeConfiguration);
                sendUserMessage(i, 1, "UPDATE_CUSTOM_GAME_MODE_CONFIG", FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                sendUserMessage(i, 6, "UPDATE_CUSTOM_GAME_MODE_CONFIG", FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                int gameMode = getGameMode(str, i);
                if (gameMode == 4) {
                    updateInterventions(str, gameMode, i);
                }
                Slog.i("GameManagerService", "Updated custom game mode config for package: " + str + " with FPS=" + orAddDefaultGameModeConfiguration.getFps() + ";Scaling=" + orAddDefaultGameModeConfiguration.getScaling() + " under user " + i);
                try {
                    i2 = this.mPackageManager.getPackageUidAsUser(str, i);
                } catch (PackageManager.NameNotFoundException unused) {
                    Slog.d("GameManagerService", "Cannot find the UID for package " + str + " under user " + i);
                    i2 = -1;
                }
                FrameworkStatsLog.write((int) FrameworkStatsLog.GAME_MODE_CONFIGURATION_CHANGED, i2, Binder.getCallingUid(), gameModeToStatsdGameMode(4), scaling, gameModeConfiguration.getScalingFactor(), fps, gameModeConfiguration.getFpsOverride());
            }
        }
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public void addGameModeListener(final IGameModeListener iGameModeListener) {
        checkPermission("android.permission.MANAGE_GAME_MODE");
        try {
            final IBinder asBinder = iGameModeListener.asBinder();
            asBinder.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.app.GameManagerService.1
                @Override // android.os.IBinder.DeathRecipient
                public void binderDied() {
                    GameManagerService.this.removeGameModeListenerUnchecked(iGameModeListener);
                    asBinder.unlinkToDeath(this, 0);
                }
            }, 0);
            synchronized (this.mGameModeListenerLock) {
                this.mGameModeListeners.put(iGameModeListener, Integer.valueOf(Binder.getCallingUid()));
            }
        } catch (RemoteException e) {
            Slog.e("GameManagerService", "Failed to link death recipient for IGameModeListener from caller " + Binder.getCallingUid() + ", abandoned its listener registration", e);
        }
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    public void removeGameModeListener(IGameModeListener iGameModeListener) {
        checkPermission("android.permission.MANAGE_GAME_MODE");
        removeGameModeListenerUnchecked(iGameModeListener);
    }

    public final void removeGameModeListenerUnchecked(IGameModeListener iGameModeListener) {
        synchronized (this.mGameModeListenerLock) {
            this.mGameModeListeners.remove(iGameModeListener);
        }
    }

    @VisibleForTesting
    public void onBootCompleted() {
        Slog.d("GameManagerService", "onBootCompleted");
        GameServiceController gameServiceController = this.mGameServiceController;
        if (gameServiceController != null) {
            gameServiceController.onBootComplete();
        }
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.app.GameManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                    synchronized (GameManagerService.this.mLock) {
                        for (Map.Entry entry : GameManagerService.this.mSettings.entrySet()) {
                            int intValue = ((Integer) entry.getKey()).intValue();
                            GameManagerService.this.sendUserMessage(intValue, 1, "RECEIVE_SHUTDOWN_INDENT", 0);
                            GameManagerService.this.sendUserMessage(intValue, 6, "RECEIVE_SHUTDOWN_INDENT", 0);
                        }
                    }
                }
            }
        }, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
    }

    public final void sendUserMessage(int i, int i2, String str, int i3) {
        if (this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i2, Integer.valueOf(i)), i3)) {
            return;
        }
        Slog.e("GameManagerService", "Failed to send user message " + i2 + " on " + str);
    }

    public void onUserStarting(SystemService.TargetUser targetUser, File file) {
        int userIdentifier = targetUser.getUserIdentifier();
        synchronized (this.mLock) {
            if (!this.mSettings.containsKey(Integer.valueOf(userIdentifier))) {
                GameManagerSettings gameManagerSettings = new GameManagerSettings(file);
                this.mSettings.put(Integer.valueOf(userIdentifier), gameManagerSettings);
                gameManagerSettings.readPersistentDataLocked();
            }
        }
        sendUserMessage(userIdentifier, 3, "ON_USER_STARTING", 0);
        GameServiceController gameServiceController = this.mGameServiceController;
        if (gameServiceController != null) {
            gameServiceController.notifyUserStarted(targetUser);
        }
    }

    public void onUserUnlocking(SystemService.TargetUser targetUser) {
        GameServiceController gameServiceController = this.mGameServiceController;
        if (gameServiceController != null) {
            gameServiceController.notifyUserUnlocking(targetUser);
        }
    }

    public void onUserStopping(SystemService.TargetUser targetUser) {
        int userIdentifier = targetUser.getUserIdentifier();
        synchronized (this.mLock) {
            if (this.mSettings.containsKey(Integer.valueOf(userIdentifier))) {
                sendUserMessage(userIdentifier, 2, "ON_USER_STOPPING", 0);
                GameServiceController gameServiceController = this.mGameServiceController;
                if (gameServiceController != null) {
                    gameServiceController.notifyUserStopped(targetUser);
                }
            }
        }
    }

    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        sendUserMessage(targetUser2.getUserIdentifier(), 3, "ON_USER_SWITCHING", 0);
        GameServiceController gameServiceController = this.mGameServiceController;
        if (gameServiceController != null) {
            gameServiceController.notifyNewForegroundUser(targetUser2);
        }
    }

    public final void resetFps(String str, int i) {
        try {
            setOverrideFrameRate(this.mPackageManager.getPackageUidAsUser(str, i), 0.0f);
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    public final boolean bitFieldContainsModeBitmask(int i, int i2) {
        return (modeToBitmask(i2) & i) != 0;
    }

    public final void updateFps(GamePackageConfiguration gamePackageConfiguration, String str, int i, int i2) {
        GamePackageConfiguration.GameModeConfiguration gameModeConfiguration = gamePackageConfiguration.getGameModeConfiguration(i);
        if (gameModeConfiguration == null) {
            Slog.d("GameManagerService", "Game mode " + i + " not found for " + str);
            return;
        }
        try {
            setOverrideFrameRate(this.mPackageManager.getPackageUidAsUser(str, i2), gameModeConfiguration.getFps());
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    public final void updateInterventions(String str, int i, int i2) {
        GamePackageConfiguration config = getConfig(str, i2);
        if (i == 1 || i == 0 || config == null || config.willGamePerformOptimizations(i) || config.getGameModeConfiguration(i) == null) {
            resetFps(str, i2);
            if (config == null) {
                Slog.v("GameManagerService", "Package configuration not found for " + str);
                return;
            }
        } else {
            updateFps(config, str, i, i2);
        }
        updateUseAngle(str, i);
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    @VisibleForTesting
    public void setGameModeConfigOverride(String str, int i, int i2, String str2, String str3) throws SecurityException {
        int i3;
        float parseFloat;
        int parseInt;
        checkPermission("android.permission.MANAGE_GAME_MODE");
        try {
            i3 = this.mPackageManager.getPackageUidAsUser(str, i);
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.d("GameManagerService", "Cannot find the UID for package " + str + " under user " + i);
            i3 = -1;
        }
        int i4 = i3;
        GamePackageConfiguration config = getConfig(str, i);
        if (config != null && config.getGameModeConfiguration(i2) != null) {
            GamePackageConfiguration.GameModeConfiguration gameModeConfiguration = config.getGameModeConfiguration(i2);
            int callingUid = Binder.getCallingUid();
            int gameModeToStatsdGameMode = gameModeToStatsdGameMode(i2);
            float scaling = gameModeConfiguration.getScaling();
            if (str3 == null) {
                parseFloat = gameModeConfiguration.getScaling();
            } else {
                parseFloat = Float.parseFloat(str3);
            }
            int fps = gameModeConfiguration.getFps();
            if (str2 == null) {
                parseInt = gameModeConfiguration.getFps();
            } else {
                parseInt = Integer.parseInt(str2);
            }
            FrameworkStatsLog.write((int) FrameworkStatsLog.GAME_MODE_CONFIGURATION_CHANGED, i4, callingUid, gameModeToStatsdGameMode, scaling, parseFloat, fps, parseInt);
        } else {
            FrameworkStatsLog.write((int) FrameworkStatsLog.GAME_MODE_CONFIGURATION_CHANGED, i4, Binder.getCallingUid(), gameModeToStatsdGameMode(i2), -1.0f, str3 == null ? -1.0f : Float.parseFloat(str3), 0, str2 == null ? 0 : Integer.parseInt(str2));
        }
        synchronized (this.mLock) {
            if (this.mSettings.containsKey(Integer.valueOf(i))) {
                GameManagerSettings gameManagerSettings = this.mSettings.get(Integer.valueOf(i));
                GamePackageConfiguration configOverride = gameManagerSettings.getConfigOverride(str);
                if (configOverride == null) {
                    configOverride = new GamePackageConfiguration(str);
                    gameManagerSettings.setConfigOverride(str, configOverride);
                }
                GamePackageConfiguration.GameModeConfiguration orAddDefaultGameModeConfiguration = configOverride.getOrAddDefaultGameModeConfiguration(i2);
                if (str2 != null) {
                    orAddDefaultGameModeConfiguration.setFpsStr(str2);
                } else {
                    orAddDefaultGameModeConfiguration.setFpsStr("");
                }
                if (str3 != null) {
                    orAddDefaultGameModeConfiguration.setScaling(Float.parseFloat(str3));
                }
                Slog.i("GameManagerService", "Package Name: " + str + " FPS: " + String.valueOf(orAddDefaultGameModeConfiguration.getFps()) + " Scaling: " + orAddDefaultGameModeConfiguration.getScaling());
                setGameMode(str, i2, i);
            }
        }
    }

    @RequiresPermission("android.permission.MANAGE_GAME_MODE")
    @VisibleForTesting
    public void resetGameModeConfigOverride(String str, int i, int i2) throws SecurityException {
        checkPermission("android.permission.MANAGE_GAME_MODE");
        synchronized (this.mLock) {
            if (this.mSettings.containsKey(Integer.valueOf(i))) {
                GameManagerSettings gameManagerSettings = this.mSettings.get(Integer.valueOf(i));
                if (i2 != -1) {
                    GamePackageConfiguration configOverride = gameManagerSettings.getConfigOverride(str);
                    if (configOverride == null) {
                        return;
                    }
                    if (!bitFieldContainsModeBitmask(configOverride.getAvailableGameModesBitfield(), i2)) {
                        return;
                    }
                    configOverride.removeModeConfig(i2);
                    if (!configOverride.hasActiveGameModeConfig()) {
                        gameManagerSettings.removeConfigOverride(str);
                    }
                } else {
                    gameManagerSettings.removeConfigOverride(str);
                }
                int gameMode = getGameMode(str, i);
                if (gameMode != getNewGameMode(gameMode, getConfig(str, i))) {
                    setGameMode(str, 1, i);
                } else {
                    setGameMode(str, gameMode, i);
                }
            }
        }
    }

    public final int getNewGameMode(int i, GamePackageConfiguration gamePackageConfiguration) {
        if (gamePackageConfiguration != null) {
            if (!bitFieldContainsModeBitmask(gamePackageConfiguration.getAvailableGameModesBitfield() & (~modeToBitmask(0)), i)) {
                i = 1;
            }
            return i;
        }
        return 1;
    }

    @RequiresPermission("android.permission.QUERY_ALL_PACKAGES")
    public String getInterventionList(String str, int i) {
        checkPermission("android.permission.QUERY_ALL_PACKAGES");
        GamePackageConfiguration config = getConfig(str, i);
        StringBuilder sb = new StringBuilder();
        if (config == null) {
            sb.append("\n No intervention found for package ");
            sb.append(str);
            return sb.toString();
        }
        sb.append("\n");
        sb.append(config.toString());
        return sb.toString();
    }

    @VisibleForTesting
    public void updateConfigsForUser(final int i, boolean z, String... strArr) {
        GamePackageConfiguration gamePackageConfiguration;
        if (z) {
            strArr = (String[]) Arrays.stream(strArr).filter(new Predicate() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateConfigsForUser$0;
                    lambda$updateConfigsForUser$0 = GameManagerService.this.lambda$updateConfigsForUser$0(i, (String) obj);
                    return lambda$updateConfigsForUser$0;
                }
            }).toArray(new IntFunction() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda4
                @Override // java.util.function.IntFunction
                public final Object apply(int i2) {
                    String[] lambda$updateConfigsForUser$1;
                    lambda$updateConfigsForUser$1 = GameManagerService.lambda$updateConfigsForUser$1(i2);
                    return lambda$updateConfigsForUser$1;
                }
            });
        }
        try {
            synchronized (this.mDeviceConfigLock) {
                for (String str : strArr) {
                    GamePackageConfiguration gamePackageConfiguration2 = new GamePackageConfiguration(this.mPackageManager, str, i);
                    if (gamePackageConfiguration2.isActive()) {
                        this.mConfigs.put(str, gamePackageConfiguration2);
                    } else {
                        this.mConfigs.remove(str);
                    }
                }
            }
            synchronized (this.mLock) {
                if (this.mSettings.containsKey(Integer.valueOf(i))) {
                    for (String str2 : strArr) {
                        int gameMode = getGameMode(str2, i);
                        synchronized (this.mDeviceConfigLock) {
                            gamePackageConfiguration = this.mConfigs.get(str2);
                        }
                        int newGameMode = getNewGameMode(gameMode, gamePackageConfiguration);
                        if (newGameMode != gameMode) {
                            setGameMode(str2, newGameMode, i);
                        } else {
                            updateInterventions(str2, gameMode, i);
                        }
                    }
                    sendUserMessage(i, 6, "UPDATE_CONFIGS_FOR_USERS", 0);
                }
            }
        } catch (Exception e) {
            Slog.e("GameManagerService", "Failed to update configs for user " + i + ": " + e);
        }
    }

    public static /* synthetic */ String[] lambda$updateConfigsForUser$1(int i) {
        return new String[i];
    }

    public final void writeGameModeInterventionsToFile(int i) {
        int[] availableGameModes;
        int i2 = i;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.mGameModeInterventionListFile.startWrite();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, Charset.defaultCharset()));
            StringBuilder sb = new StringBuilder();
            for (String str : getInstalledGamePackageNamesByAllUsers(i)) {
                GamePackageConfiguration config = getConfig(str, i2);
                if (config != null) {
                    sb.append(str);
                    sb.append("\t");
                    sb.append(this.mPackageManager.getPackageUidAsUser(str, i2));
                    sb.append("\t");
                    sb.append(getGameMode(str, i2));
                    sb.append("\t");
                    for (int i3 : config.getAvailableGameModes()) {
                        GamePackageConfiguration.GameModeConfiguration gameModeConfiguration = config.getGameModeConfiguration(i3);
                        if (gameModeConfiguration != null) {
                            sb.append(i3);
                            sb.append("\t");
                            sb.append(TextUtils.formatSimple("angle=%d", new Object[]{Integer.valueOf(gameModeConfiguration.getUseAngle() ? 1 : 0)}));
                            sb.append(",");
                            float scaling = gameModeConfiguration.getScaling();
                            sb.append("scaling=");
                            sb.append(scaling);
                            sb.append(",");
                            sb.append(TextUtils.formatSimple("fps=%d", new Object[]{Integer.valueOf(gameModeConfiguration.getFps())}));
                            sb.append("\t");
                        }
                    }
                    sb.append("\n");
                    i2 = i;
                }
            }
            bufferedWriter.append((CharSequence) sb);
            bufferedWriter.flush();
            FileUtils.sync(fileOutputStream);
            this.mGameModeInterventionListFile.finishWrite(fileOutputStream);
        } catch (Exception e) {
            this.mGameModeInterventionListFile.failWrite(fileOutputStream);
            Slog.wtf("GameManagerService", "Failed to write game_mode_intervention.list, exception " + e);
        }
    }

    public final int[] getAllUserIds(int i) {
        List users = this.mUserManager.getUsers();
        int size = users.size();
        int[] iArr = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = ((UserInfo) users.get(i2)).id;
        }
        return i != -1 ? ArrayUtils.appendInt(iArr, i) : iArr;
    }

    public final String[] getInstalledGamePackageNames(int i) {
        return (String[]) this.mPackageManager.getInstalledPackagesAsUser(0, i).stream().filter(new Predicate() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getInstalledGamePackageNames$2;
                lambda$getInstalledGamePackageNames$2 = GameManagerService.lambda$getInstalledGamePackageNames$2((PackageInfo) obj);
                return lambda$getInstalledGamePackageNames$2;
            }
        }).map(new Function() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String str;
                str = ((PackageInfo) obj).packageName;
                return str;
            }
        }).toArray(new IntFunction() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda2
            @Override // java.util.function.IntFunction
            public final Object apply(int i2) {
                String[] lambda$getInstalledGamePackageNames$4;
                lambda$getInstalledGamePackageNames$4 = GameManagerService.lambda$getInstalledGamePackageNames$4(i2);
                return lambda$getInstalledGamePackageNames$4;
            }
        });
    }

    public static /* synthetic */ boolean lambda$getInstalledGamePackageNames$2(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        return applicationInfo != null && applicationInfo.category == 0;
    }

    public static /* synthetic */ String[] lambda$getInstalledGamePackageNames$4(int i) {
        return new String[i];
    }

    public final List<String> getInstalledGamePackageNamesByAllUsers(int i) {
        HashSet hashSet = new HashSet();
        for (int i2 : getAllUserIds(i)) {
            hashSet.addAll(Arrays.asList(getInstalledGamePackageNames(i2)));
        }
        return new ArrayList(hashSet);
    }

    public GamePackageConfiguration getConfig(String str, int i) {
        GamePackageConfiguration gamePackageConfiguration;
        GamePackageConfiguration configOverride;
        synchronized (this.mDeviceConfigLock) {
            gamePackageConfiguration = this.mConfigs.get(str);
        }
        synchronized (this.mLock) {
            configOverride = this.mSettings.containsKey(Integer.valueOf(i)) ? this.mSettings.get(Integer.valueOf(i)).getConfigOverride(str) : null;
        }
        if (configOverride == null || gamePackageConfiguration == null) {
            return configOverride == null ? gamePackageConfiguration : configOverride;
        }
        return gamePackageConfiguration.copyAndApplyOverride(configOverride);
    }

    public final void registerPackageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.server.app.GameManagerService.3
            /* JADX WARN: Removed duplicated region for block: B:23:0x0050  */
            /* JADX WARN: Removed duplicated region for block: B:45:0x00af A[Catch: NullPointerException -> 0x00b9, TRY_LEAVE, TryCatch #3 {NullPointerException -> 0x00b9, blocks: (B:3:0x0004, B:6:0x000f, B:7:0x0013, B:10:0x0024, B:25:0x0053, B:27:0x005b, B:28:0x0061, B:31:0x006c, B:32:0x0072, B:45:0x00af, B:15:0x0039, B:18:0x0043, B:33:0x0073, B:35:0x0083, B:36:0x0096, B:37:0x00a7, B:29:0x0062, B:30:0x006b), top: B:52:0x0004 }] */
            @Override // android.content.BroadcastReceiver
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onReceive(Context context, Intent intent) {
                char c;
                Uri data = intent.getData();
                try {
                    int sendingUserId = getSendingUserId();
                    if (sendingUserId != ActivityManager.getCurrentUser()) {
                        return;
                    }
                    String schemeSpecificPart = data.getSchemeSpecificPart();
                    try {
                        if (GameManagerService.this.mPackageManager.getApplicationInfoAsUser(schemeSpecificPart, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, sendingUserId).category != 0) {
                            return;
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                    String action = intent.getAction();
                    int hashCode = action.hashCode();
                    if (hashCode != 525384130) {
                        if (hashCode == 1544582882 && action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 0;
                            if (c != 0) {
                                GameManagerService.this.updateConfigsForUser(sendingUserId, true, schemeSpecificPart);
                                return;
                            } else if (c == 1 && !intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                                synchronized (GameManagerService.this.mDeviceConfigLock) {
                                    GameManagerService.this.mConfigs.remove(schemeSpecificPart);
                                }
                                synchronized (GameManagerService.this.mLock) {
                                    if (GameManagerService.this.mSettings.containsKey(Integer.valueOf(sendingUserId))) {
                                        ((GameManagerSettings) GameManagerService.this.mSettings.get(Integer.valueOf(sendingUserId))).removeGame(schemeSpecificPart);
                                    }
                                    GameManagerService.this.sendUserMessage(sendingUserId, 1, "android.intent.action.PACKAGE_REMOVED", FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                                    GameManagerService.this.sendUserMessage(sendingUserId, 6, "android.intent.action.PACKAGE_REMOVED", FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    } else {
                        if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                            c = 1;
                            if (c != 0) {
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    }
                } catch (NullPointerException unused2) {
                    Slog.e("GameManagerService", "Failed to get package name for new package");
                }
            }
        }, intentFilter, null, null);
    }

    public final void registerDeviceConfigListener() {
        this.mDeviceConfigListener = new DeviceConfigListener();
    }

    public final void publishLocalService() {
        LocalServices.addService(GameManagerInternal.class, new LocalService());
    }

    public final void registerStatsCallbacks() {
        StatsManager statsManager = (StatsManager) this.mContext.getSystemService(StatsManager.class);
        statsManager.setPullAtomCallback((int) FrameworkStatsLog.GAME_MODE_INFO, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda5
            public final int onPullAtom(int i, List list) {
                int onPullAtom;
                onPullAtom = GameManagerService.this.onPullAtom(i, list);
                return onPullAtom;
            }
        });
        statsManager.setPullAtomCallback((int) FrameworkStatsLog.GAME_MODE_CONFIGURATION, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda5
            public final int onPullAtom(int i, List list) {
                int onPullAtom;
                onPullAtom = GameManagerService.this.onPullAtom(i, list);
                return onPullAtom;
            }
        });
        statsManager.setPullAtomCallback((int) FrameworkStatsLog.GAME_MODE_LISTENER, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.app.GameManagerService$$ExternalSyntheticLambda5
            public final int onPullAtom(int i, List list) {
                int onPullAtom;
                onPullAtom = GameManagerService.this.onPullAtom(i, list);
                return onPullAtom;
            }
        });
    }

    public final int onPullAtom(int i, List<StatsEvent> list) {
        Set<String> keySet;
        int i2;
        int[] availableGameModes;
        if (i == 10165 || i == 10166) {
            int currentUser = ActivityManager.getCurrentUser();
            synchronized (this.mDeviceConfigLock) {
                keySet = this.mConfigs.keySet();
            }
            for (String str : keySet) {
                GamePackageConfiguration config = getConfig(str, currentUser);
                if (config != null) {
                    try {
                        i2 = this.mPackageManager.getPackageUidAsUser(str, currentUser);
                    } catch (PackageManager.NameNotFoundException unused) {
                        Slog.d("GameManagerService", "Cannot find UID for package " + str + " under user handle id " + currentUser);
                        i2 = -1;
                    }
                    if (i == 10165) {
                        list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.GAME_MODE_INFO, i2, gameModesToStatsdGameModes(config.getOverriddenGameModes()), gameModesToStatsdGameModes(config.getAvailableGameModes())));
                    } else if (i == 10166) {
                        for (int i3 : config.getAvailableGameModes()) {
                            GamePackageConfiguration.GameModeConfiguration gameModeConfiguration = config.getGameModeConfiguration(i3);
                            if (gameModeConfiguration != null) {
                                list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.GAME_MODE_CONFIGURATION, i2, gameModeToStatsdGameMode(i3), gameModeConfiguration.getFps(), gameModeConfiguration.getScaling()));
                            }
                        }
                    }
                }
            }
        } else if (i == 10167) {
            synchronized (this.mGameModeListenerLock) {
                list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.GAME_MODE_LISTENER, this.mGameModeListeners.size()));
            }
        }
        return 0;
    }

    public static int[] gameModesToStatsdGameModes(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        int[] iArr2 = new int[iArr.length];
        int length = iArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            iArr2[i2] = gameModeToStatsdGameMode(iArr[i]);
            i++;
            i2++;
        }
        return iArr2;
    }

    public static ServiceThread createServiceThread() {
        ServiceThread serviceThread = new ServiceThread("GameManagerService", 10, true);
        serviceThread.start();
        return serviceThread;
    }

    @VisibleForTesting
    public void setOverrideFrameRate(int i, float f) {
        nativeSetOverrideFrameRate(i, f);
    }

    /* loaded from: classes.dex */
    public final class UidObserver extends IUidObserver.Stub {
        public void onUidActive(int i) {
        }

        public void onUidCachedChanged(int i, boolean z) {
        }

        public void onUidIdle(int i, boolean z) {
        }

        public void onUidProcAdjChanged(int i) {
        }

        public UidObserver() {
        }

        public void onUidGone(int i, boolean z) {
            synchronized (GameManagerService.this.mUidObserverLock) {
                disableGameMode(i);
            }
        }

        public void onUidStateChanged(int i, int i2, long j, int i3) {
            synchronized (GameManagerService.this.mUidObserverLock) {
                if (ActivityManager.isProcStateBackground(i2)) {
                    disableGameMode(i);
                    return;
                }
                String[] packagesForUid = GameManagerService.this.mContext.getPackageManager().getPackagesForUid(i);
                if (packagesForUid != null && packagesForUid.length != 0) {
                    final int userId = GameManagerService.this.mContext.getUserId();
                    if (Arrays.stream(packagesForUid).anyMatch(new Predicate() { // from class: com.android.server.app.GameManagerService$UidObserver$$ExternalSyntheticLambda0
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$onUidStateChanged$0;
                            lambda$onUidStateChanged$0 = GameManagerService.UidObserver.this.lambda$onUidStateChanged$0(userId, (String) obj);
                            return lambda$onUidStateChanged$0;
                        }
                    })) {
                        if (GameManagerService.this.mForegroundGameUids.isEmpty()) {
                            Slog.v("GameManagerService", "Game power mode ON (process state was changed to foreground)");
                            GameManagerService.this.mPowerManagerInternal.setPowerMode(15, true);
                        }
                        GameManagerService.this.mForegroundGameUids.add(Integer.valueOf(i));
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onUidStateChanged$0(int i, String str) {
            return GameManagerService.this.lambda$updateConfigsForUser$0(str, i);
        }

        public final void disableGameMode(int i) {
            synchronized (GameManagerService.this.mUidObserverLock) {
                if (GameManagerService.this.mForegroundGameUids.contains(Integer.valueOf(i))) {
                    GameManagerService.this.mForegroundGameUids.remove(Integer.valueOf(i));
                    if (GameManagerService.this.mForegroundGameUids.isEmpty()) {
                        Slog.v("GameManagerService", "Game power mode OFF (process remomved or state changed to background)");
                        GameManagerService.this.mPowerManagerInternal.setPowerMode(15, false);
                    }
                }
            }
        }
    }
}
