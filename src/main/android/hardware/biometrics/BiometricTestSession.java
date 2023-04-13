package android.hardware.biometrics;

import android.content.Context;
import android.hardware.biometrics.ITestSessionCallback;
import android.p008os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class BiometricTestSession implements AutoCloseable {
    private static final String BASE_TAG = "BiometricTestSession";
    private final ITestSessionCallback mCallback;
    private CountDownLatch mCloseLatch;
    private final Context mContext;
    private final int mSensorId;
    private final ITestSession mTestSession;
    private final ArraySet<Integer> mTestedUsers;
    private final ArraySet<Integer> mUsersCleaningUp;

    /* loaded from: classes.dex */
    public interface TestSessionProvider {
        ITestSession createTestSession(Context context, int i, ITestSessionCallback iTestSessionCallback) throws RemoteException;
    }

    public BiometricTestSession(Context context, int sensorId, TestSessionProvider testSessionProvider) throws RemoteException {
        ITestSessionCallback.Stub stub = new ITestSessionCallback.Stub() { // from class: android.hardware.biometrics.BiometricTestSession.1
            @Override // android.hardware.biometrics.ITestSessionCallback
            public void onCleanupStarted(int userId) {
                Log.m112d(BiometricTestSession.this.getTag(), "onCleanupStarted, sensor: " + BiometricTestSession.this.mSensorId + ", userId: " + userId);
            }

            @Override // android.hardware.biometrics.ITestSessionCallback
            public void onCleanupFinished(int userId) {
                Log.m112d(BiometricTestSession.this.getTag(), "onCleanupFinished, sensor: " + BiometricTestSession.this.mSensorId + ", userId: " + userId + ", remaining users: " + BiometricTestSession.this.mUsersCleaningUp.size());
                BiometricTestSession.this.mUsersCleaningUp.remove(Integer.valueOf(userId));
                if (BiometricTestSession.this.mUsersCleaningUp.isEmpty() && BiometricTestSession.this.mCloseLatch != null) {
                    BiometricTestSession.this.mCloseLatch.countDown();
                }
            }
        };
        this.mCallback = stub;
        this.mContext = context;
        this.mSensorId = sensorId;
        this.mTestSession = testSessionProvider.createTestSession(context, sensorId, stub);
        this.mTestedUsers = new ArraySet<>();
        this.mUsersCleaningUp = new ArraySet<>();
        setTestHalEnabled(true);
    }

    private void setTestHalEnabled(boolean enabled) {
        try {
            Log.m104w(getTag(), "setTestHalEnabled, sensor: " + this.mSensorId + " enabled: " + enabled);
            this.mTestSession.setTestHalEnabled(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startEnroll(int userId) {
        try {
            this.mTestedUsers.add(Integer.valueOf(userId));
            this.mTestSession.startEnroll(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void finishEnroll(int userId) {
        try {
            this.mTestedUsers.add(Integer.valueOf(userId));
            this.mTestSession.finishEnroll(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void acceptAuthentication(int userId) {
        try {
            this.mTestSession.acceptAuthentication(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void rejectAuthentication(int userId) {
        try {
            this.mTestSession.rejectAuthentication(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyAcquired(int userId, int acquireInfo) {
        try {
            this.mTestSession.notifyAcquired(userId, acquireInfo);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyError(int userId, int errorCode) {
        try {
            this.mTestSession.notifyError(userId, errorCode);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void cleanupInternalState(int userId) {
        try {
            if (this.mUsersCleaningUp.contains(Integer.valueOf(userId))) {
                Log.m104w(getTag(), "Cleanup already in progress for user: " + userId);
            }
            this.mUsersCleaningUp.add(Integer.valueOf(userId));
            this.mTestSession.cleanupInternalState(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        Log.m112d(getTag(), "Close, mTestedUsers size; " + this.mTestedUsers.size());
        if (!this.mTestedUsers.isEmpty()) {
            this.mCloseLatch = new CountDownLatch(1);
            Iterator<Integer> it = this.mTestedUsers.iterator();
            while (it.hasNext()) {
                int user = it.next().intValue();
                cleanupInternalState(user);
            }
            try {
                Log.m112d(getTag(), "Awaiting latch...");
                this.mCloseLatch.await(3L, TimeUnit.SECONDS);
                Log.m112d(getTag(), "Finished awaiting");
            } catch (InterruptedException e) {
                Log.m109e(getTag(), "Latch interrupted", e);
            }
        }
        if (!this.mUsersCleaningUp.isEmpty()) {
            Log.m110e(getTag(), "Cleanup not finished before shutdown - pending: " + this.mUsersCleaningUp.size());
        }
        setTestHalEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getTag() {
        return "BiometricTestSession_" + this.mSensorId;
    }
}
