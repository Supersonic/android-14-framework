package android.p008os;

import com.google.android.collect.Maps;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
/* renamed from: android.os.SystemService */
/* loaded from: classes3.dex */
public class SystemService {
    private static HashMap<String, State> sStates = Maps.newHashMap();
    private static Object sPropertyLock = new Object();

    static {
        SystemProperties.addChangeCallback(new Runnable() { // from class: android.os.SystemService.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (SystemService.sPropertyLock) {
                    SystemService.sPropertyLock.notifyAll();
                }
            }
        });
    }

    /* renamed from: android.os.SystemService$State */
    /* loaded from: classes3.dex */
    public enum State {
        RUNNING("running"),
        STOPPING("stopping"),
        STOPPED("stopped"),
        RESTARTING("restarting");

        State(String state) {
            SystemService.sStates.put(state, this);
        }
    }

    public static void start(String name) {
        SystemProperties.set("ctl.start", name);
    }

    public static void stop(String name) {
        SystemProperties.set("ctl.stop", name);
    }

    public static void restart(String name) {
        SystemProperties.set("ctl.restart", name);
    }

    public static State getState(String service) {
        String rawState = SystemProperties.get("init.svc." + service);
        State state = sStates.get(rawState);
        if (state != null) {
            return state;
        }
        return State.STOPPED;
    }

    public static boolean isStopped(String service) {
        return State.STOPPED.equals(getState(service));
    }

    public static boolean isRunning(String service) {
        return State.RUNNING.equals(getState(service));
    }

    public static void waitForState(String service, State state, long timeoutMillis) throws TimeoutException {
        long endMillis = SystemClock.elapsedRealtime() + timeoutMillis;
        while (true) {
            synchronized (sPropertyLock) {
                State currentState = getState(service);
                if (state.equals(currentState)) {
                    return;
                }
                if (SystemClock.elapsedRealtime() >= endMillis) {
                    throw new TimeoutException("Service " + service + " currently " + currentState + "; waited " + timeoutMillis + "ms for " + state);
                }
                try {
                    sPropertyLock.wait(timeoutMillis);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static void waitForAnyStopped(String... services) {
        while (true) {
            synchronized (sPropertyLock) {
                for (String service : services) {
                    if (State.STOPPED.equals(getState(service))) {
                        return;
                    }
                }
                try {
                    sPropertyLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
