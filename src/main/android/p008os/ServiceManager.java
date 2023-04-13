package android.p008os;

import android.annotation.SystemApi;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.p028os.BinderInternal;
import com.android.internal.util.StatLogger;
import java.util.Map;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* renamed from: android.os.ServiceManager */
/* loaded from: classes3.dex */
public final class ServiceManager {
    private static final int SLOW_LOG_INTERVAL_MS = 5000;
    private static final int STATS_LOG_INTERVAL_MS = 5000;
    private static final String TAG = "ServiceManager";
    private static int sGetServiceAccumulatedCallCount;
    private static int sGetServiceAccumulatedUs;
    private static long sLastSlowLogActualTime;
    private static long sLastSlowLogUptime;
    private static long sLastStatsLogUptime;
    private static IServiceManager sServiceManager;
    private static final Object sLock = new Object();
    private static Map<String, IBinder> sCache = new ArrayMap();
    private static final long GET_SERVICE_SLOW_THRESHOLD_US_CORE = SystemProperties.getInt("debug.servicemanager.slow_call_core_ms", 10) * 1000;
    private static final long GET_SERVICE_SLOW_THRESHOLD_US_NON_CORE = SystemProperties.getInt("debug.servicemanager.slow_call_ms", 50) * 1000;
    private static final int GET_SERVICE_LOG_EVERY_CALLS_CORE = SystemProperties.getInt("debug.servicemanager.log_calls_core", 100);
    private static final int GET_SERVICE_LOG_EVERY_CALLS_NON_CORE = SystemProperties.getInt("debug.servicemanager.log_calls", 200);
    public static final StatLogger sStatLogger = new StatLogger(new String[]{"getService()"});

    /* renamed from: android.os.ServiceManager$Stats */
    /* loaded from: classes3.dex */
    interface Stats {
        public static final int COUNT = 1;
        public static final int GET_SERVICE = 0;
    }

    private static native IBinder waitForServiceNative(String str);

    private static IServiceManager getIServiceManager() {
        IServiceManager iServiceManager = sServiceManager;
        if (iServiceManager != null) {
            return iServiceManager;
        }
        IServiceManager asInterface = ServiceManagerNative.asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
        sServiceManager = asInterface;
        return asInterface;
    }

    public static IBinder getService(String name) {
        try {
            IBinder service = sCache.get(name);
            if (service != null) {
                return service;
            }
            return Binder.allowBlocking(rawGetService(name));
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in getService", e);
            return null;
        }
    }

    public static IBinder getServiceOrThrow(String name) throws ServiceNotFoundException {
        IBinder binder = getService(name);
        if (binder != null) {
            return binder;
        }
        throw new ServiceNotFoundException(name);
    }

    public static void addService(String name, IBinder service) {
        addService(name, service, false, 8);
    }

    public static void addService(String name, IBinder service, boolean allowIsolated) {
        addService(name, service, allowIsolated, 8);
    }

    public static void addService(String name, IBinder service, boolean allowIsolated, int dumpPriority) {
        try {
            getIServiceManager().addService(name, service, allowIsolated, dumpPriority);
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in addService", e);
        }
    }

    public static IBinder checkService(String name) {
        try {
            IBinder service = sCache.get(name);
            if (service != null) {
                return service;
            }
            return Binder.allowBlocking(getIServiceManager().checkService(name));
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in checkService", e);
            return null;
        }
    }

    public static boolean isDeclared(String name) {
        try {
            return getIServiceManager().isDeclared(name);
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in isDeclared", e);
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static String[] getDeclaredInstances(String iface) {
        try {
            return getIServiceManager().getDeclaredInstances(iface);
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in getDeclaredInstances", e);
            throw e.rethrowFromSystemServer();
        }
    }

    public static IBinder waitForService(String name) {
        return Binder.allowBlocking(waitForServiceNative(name));
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static IBinder waitForDeclaredService(String name) {
        if (isDeclared(name)) {
            return waitForService(name);
        }
        return null;
    }

    public static void registerForNotifications(String name, IServiceCallback callback) throws RemoteException {
        getIServiceManager().registerForNotifications(name, callback);
    }

    public static String[] listServices() {
        try {
            return getIServiceManager().listServices(15);
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in listServices", e);
            return null;
        }
    }

    public static ServiceDebugInfo[] getServiceDebugInfo() {
        try {
            return getIServiceManager().getServiceDebugInfo();
        } catch (RemoteException e) {
            Log.m109e(TAG, "error in getServiceDebugInfo", e);
            return null;
        }
    }

    public static void initServiceCache(Map<String, IBinder> cache) {
        if (sCache.size() != 0) {
            throw new IllegalStateException("setServiceCache may only be called once");
        }
        sCache.putAll(cache);
    }

    /* renamed from: android.os.ServiceManager$ServiceNotFoundException */
    /* loaded from: classes3.dex */
    public static class ServiceNotFoundException extends Exception {
        public ServiceNotFoundException(String name) {
            super("No service published for: " + name);
        }
    }

    private static IBinder rawGetService(String name) throws RemoteException {
        long slowThreshold;
        int logInterval;
        StatLogger statLogger = sStatLogger;
        long start = statLogger.getTime();
        IBinder binder = getIServiceManager().getService(name);
        int time = (int) statLogger.logDurationStat(0, start);
        int myUid = Process.myUid();
        boolean isCore = UserHandle.isCore(myUid);
        if (isCore) {
            slowThreshold = GET_SERVICE_SLOW_THRESHOLD_US_CORE;
        } else {
            slowThreshold = GET_SERVICE_SLOW_THRESHOLD_US_NON_CORE;
        }
        synchronized (sLock) {
            sGetServiceAccumulatedUs += time;
            sGetServiceAccumulatedCallCount++;
            long nowUptime = SystemClock.uptimeMillis();
            if (time >= slowThreshold && (nowUptime > sLastSlowLogUptime + 5000 || sLastSlowLogActualTime < time)) {
                EventLogTags.writeServiceManagerSlow(time / 1000, name);
                sLastSlowLogUptime = nowUptime;
                sLastSlowLogActualTime = time;
            }
            if (isCore) {
                logInterval = GET_SERVICE_LOG_EVERY_CALLS_CORE;
            } else {
                logInterval = GET_SERVICE_LOG_EVERY_CALLS_NON_CORE;
            }
            int i = sGetServiceAccumulatedCallCount;
            if (i >= logInterval) {
                long j = sLastStatsLogUptime;
                if (nowUptime >= j + 5000) {
                    EventLogTags.writeServiceManagerStats(i, sGetServiceAccumulatedUs / 1000, (int) (nowUptime - j));
                    sGetServiceAccumulatedCallCount = 0;
                    sGetServiceAccumulatedUs = 0;
                    sLastStatsLogUptime = nowUptime;
                }
            }
        }
        return binder;
    }
}
