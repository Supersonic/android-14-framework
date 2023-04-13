package android.net;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.io.File;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ConnectivityModuleConnector {
    private static final String CONFIG_ALWAYS_RATELIMIT_NETWORKSTACK_CRASH = "always_ratelimit_networkstack_crash";
    private static final String CONFIG_MIN_CRASH_INTERVAL_MS = "min_crash_interval";
    private static final String CONFIG_MIN_UPTIME_BEFORE_CRASH_MS = "min_uptime_before_crash";
    private static final long DEFAULT_MIN_CRASH_INTERVAL_MS = 21600000;
    private static final long DEFAULT_MIN_UPTIME_BEFORE_CRASH_MS = 1800000;
    private static final String IN_PROCESS_SUFFIX = ".InProcess";
    private static final String PREFS_FILE = "ConnectivityModuleConnector.xml";
    private static final String PREF_KEY_LAST_CRASH_TIME = "lastcrash_time";
    private static final String TAG = "ConnectivityModuleConnector";
    private static ConnectivityModuleConnector sInstance;
    private Context mContext;
    private final Dependencies mDeps;
    @GuardedBy({"mHealthListeners"})
    private final ArraySet<ConnectivityModuleHealthListener> mHealthListeners;

    /* loaded from: classes.dex */
    public interface ConnectivityModuleHealthListener {
        void onNetworkStackFailure(String str);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Dependencies {
        Intent getModuleServiceIntent(PackageManager packageManager, String str, String str2, boolean z);
    }

    /* loaded from: classes.dex */
    public interface ModuleServiceCallback {
        void onModuleServiceConnected(IBinder iBinder);
    }

    private ConnectivityModuleConnector() {
        this(new DependenciesImpl());
    }

    @VisibleForTesting
    public ConnectivityModuleConnector(Dependencies dependencies) {
        this.mHealthListeners = new ArraySet<>();
        this.mDeps = dependencies;
    }

    public static synchronized ConnectivityModuleConnector getInstance() {
        ConnectivityModuleConnector connectivityModuleConnector;
        synchronized (ConnectivityModuleConnector.class) {
            if (sInstance == null) {
                sInstance = new ConnectivityModuleConnector();
            }
            connectivityModuleConnector = sInstance;
        }
        return connectivityModuleConnector;
    }

    public void init(Context context) {
        log("Network stack init");
        this.mContext = context;
    }

    /* loaded from: classes.dex */
    public static class DependenciesImpl implements Dependencies {
        public DependenciesImpl() {
        }

        @Override // android.net.ConnectivityModuleConnector.Dependencies
        public Intent getModuleServiceIntent(PackageManager packageManager, String str, String str2, boolean z) {
            if (z) {
                str = str + ConnectivityModuleConnector.IN_PROCESS_SUFFIX;
            }
            Intent intent = new Intent(str);
            ComponentName resolveSystemService = intent.resolveSystemService(packageManager, 0);
            if (resolveSystemService == null) {
                return null;
            }
            intent.setComponent(resolveSystemService);
            try {
                int packageUidAsUser = packageManager.getPackageUidAsUser(resolveSystemService.getPackageName(), 0);
                if (packageUidAsUser == (z ? 1000 : 1073)) {
                    if (!z) {
                        ConnectivityModuleConnector.checkModuleServicePermission(packageManager, resolveSystemService, str2);
                    }
                    return intent;
                }
                throw new SecurityException("Invalid network stack UID: " + packageUidAsUser);
            } catch (PackageManager.NameNotFoundException e) {
                throw new SecurityException("Could not check network stack UID; package not found.", e);
            }
        }
    }

    public void registerHealthListener(ConnectivityModuleHealthListener connectivityModuleHealthListener) {
        synchronized (this.mHealthListeners) {
            this.mHealthListeners.add(connectivityModuleHealthListener);
        }
    }

    public void startModuleService(String str, String str2, ModuleServiceCallback moduleServiceCallback) {
        log("Starting networking module " + str);
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent moduleServiceIntent = this.mDeps.getModuleServiceIntent(packageManager, str, str2, true);
        if (moduleServiceIntent == null) {
            moduleServiceIntent = this.mDeps.getModuleServiceIntent(packageManager, str, str2, false);
            log("Starting networking module in network_stack process");
        } else {
            log("Starting networking module in system_server process");
        }
        if (moduleServiceIntent == null) {
            maybeCrashWithTerribleFailure("Could not resolve the networking module", null);
            return;
        }
        String packageName = moduleServiceIntent.getComponent().getPackageName();
        if (!this.mContext.bindServiceAsUser(moduleServiceIntent, new ModuleServiceConnection(packageName, moduleServiceCallback), 65, UserHandle.SYSTEM)) {
            maybeCrashWithTerribleFailure("Could not bind to networking module in-process, or in app with " + moduleServiceIntent, packageName);
            return;
        }
        log("Networking module service start requested");
    }

    /* loaded from: classes.dex */
    public class ModuleServiceConnection implements ServiceConnection {
        public final ModuleServiceCallback mModuleServiceCallback;
        public final String mPackageName;

        public ModuleServiceConnection(String str, ModuleServiceCallback moduleServiceCallback) {
            this.mPackageName = str;
            this.mModuleServiceCallback = moduleServiceCallback;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ConnectivityModuleConnector.this.logi("Networking module service connected");
            this.mModuleServiceCallback.onModuleServiceConnected(iBinder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            ConnectivityModuleConnector.this.maybeCrashWithTerribleFailure("Lost network stack. This is not the root cause of any issue, it is a side effect of a crash that happened earlier. Earlier logs should point to the actual issue.", this.mPackageName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkModuleServicePermission(PackageManager packageManager, ComponentName componentName, String str) {
        if (packageManager.checkPermission(str, componentName.getPackageName()) == 0) {
            return;
        }
        throw new SecurityException("Networking module does not have permission " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void maybeCrashWithTerribleFailure(String str, String str2) {
        ArraySet arraySet;
        logWtf(str, null);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long currentTimeMillis = System.currentTimeMillis();
        long j = DeviceConfig.getLong("connectivity", CONFIG_MIN_CRASH_INTERVAL_MS, (long) DEFAULT_MIN_CRASH_INTERVAL_MS);
        long j2 = DeviceConfig.getLong("connectivity", CONFIG_MIN_UPTIME_BEFORE_CRASH_MS, 1800000L);
        boolean z = false;
        boolean z2 = DeviceConfig.getBoolean("connectivity", CONFIG_ALWAYS_RATELIMIT_NETWORKSTACK_CRASH, false);
        SharedPreferences sharedPreferences = getSharedPreferences();
        long tryGetLastCrashTime = tryGetLastCrashTime(sharedPreferences);
        boolean z3 = Build.IS_DEBUGGABLE && !z2;
        boolean z4 = elapsedRealtime < j2;
        if ((tryGetLastCrashTime != 0 && tryGetLastCrashTime < currentTimeMillis) && currentTimeMillis < tryGetLastCrashTime + j) {
            z = true;
        }
        if (z3 || !(z4 || z)) {
            tryWriteLastCrashTime(sharedPreferences, currentTimeMillis);
            throw new IllegalStateException(str);
        } else if (str2 != null) {
            synchronized (this.mHealthListeners) {
                arraySet = new ArraySet((ArraySet) this.mHealthListeners);
            }
            Iterator it = arraySet.iterator();
            while (it.hasNext()) {
                ((ConnectivityModuleHealthListener) it.next()).onNetworkStackFailure(str2);
            }
        }
    }

    private SharedPreferences getSharedPreferences() {
        try {
            return this.mContext.createDeviceProtectedStorageContext().getSharedPreferences(new File(Environment.getDataSystemDeDirectory(0), PREFS_FILE), 0);
        } catch (Throwable th) {
            this.logWtf("Error loading shared preferences", th);
            return null;
        }
    }

    private long tryGetLastCrashTime(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return 0L;
        }
        try {
            return sharedPreferences.getLong(PREF_KEY_LAST_CRASH_TIME, 0L);
        } catch (Throwable th) {
            this.logWtf("Error getting last crash time", th);
            return 0L;
        }
    }

    private void tryWriteLastCrashTime(SharedPreferences sharedPreferences, long j) {
        if (sharedPreferences == null) {
            return;
        }
        try {
            sharedPreferences.edit().putLong(PREF_KEY_LAST_CRASH_TIME, j).commit();
        } catch (Throwable th) {
            logWtf("Error writing last crash time", th);
        }
    }

    private void log(String str) {
        Log.d(TAG, str);
    }

    private void logWtf(String str, Throwable th) {
        String str2 = TAG;
        Slog.wtf(str2, str, th);
        Log.e(str2, str, th);
    }

    private void loge(String str, Throwable th) {
        Log.e(TAG, str, th);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logi(String str) {
        Log.i(TAG, str);
    }
}
