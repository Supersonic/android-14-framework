package com.android.server.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.utils.ManagedApplicationService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
/* loaded from: classes2.dex */
public class ManagedApplicationService {
    public IInterface mBoundInterface;
    public final BinderChecker mChecker;
    public final int mClientLabel;
    public final ComponentName mComponent;
    public ServiceConnection mConnection;
    public final Context mContext;
    public final EventCallback mEventCb;
    public final Handler mHandler;
    public final boolean mIsImportant;
    public long mLastRetryTimeMs;
    public PendingEvent mPendingEvent;
    public int mRetryCount;
    public final int mRetryType;
    public boolean mRetrying;
    public final String mSettingsAction;
    public final int mUserId;
    public final String TAG = getClass().getSimpleName();
    public final Runnable mRetryRunnable = new Runnable() { // from class: com.android.server.utils.ManagedApplicationService$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            ManagedApplicationService.this.doRetry();
        }
    };
    public final Object mLock = new Object();
    public long mNextRetryDurationMs = 2000;

    /* loaded from: classes2.dex */
    public interface BinderChecker {
        IInterface asInterface(IBinder iBinder);

        boolean checkType(IInterface iInterface);
    }

    /* loaded from: classes2.dex */
    public interface EventCallback {
        void onServiceEvent(LogEvent logEvent);
    }

    /* loaded from: classes2.dex */
    public interface LogFormattable {
        String toLogString(SimpleDateFormat simpleDateFormat);
    }

    /* loaded from: classes2.dex */
    public interface PendingEvent {
        void runEvent(IInterface iInterface) throws RemoteException;
    }

    /* loaded from: classes2.dex */
    public static class LogEvent implements LogFormattable {
        public final ComponentName component;
        public final int event;
        public final long timestamp;

        public static String eventToString(int i) {
            return i != 1 ? i != 2 ? i != 3 ? i != 4 ? "Unknown Event Occurred" : "Permanently Stopped" : "Binding Died For" : "Disconnected" : "Connected";
        }

        public LogEvent(long j, ComponentName componentName, int i) {
            this.timestamp = j;
            this.component = componentName;
            this.event = i;
        }

        @Override // com.android.server.utils.ManagedApplicationService.LogFormattable
        public String toLogString(SimpleDateFormat simpleDateFormat) {
            StringBuilder sb = new StringBuilder();
            sb.append(simpleDateFormat.format(new Date(this.timestamp)));
            sb.append("   ");
            sb.append(eventToString(this.event));
            sb.append(" Managed Service: ");
            ComponentName componentName = this.component;
            sb.append(componentName == null ? "None" : componentName.flattenToString());
            return sb.toString();
        }
    }

    public ManagedApplicationService(Context context, ComponentName componentName, int i, int i2, String str, BinderChecker binderChecker, boolean z, int i3, Handler handler, EventCallback eventCallback) {
        this.mContext = context;
        this.mComponent = componentName;
        this.mUserId = i;
        this.mClientLabel = i2;
        this.mSettingsAction = str;
        this.mChecker = binderChecker;
        this.mIsImportant = z;
        this.mRetryType = i3;
        this.mHandler = handler;
        this.mEventCb = eventCallback;
    }

    public static ManagedApplicationService build(Context context, ComponentName componentName, int i, int i2, String str, BinderChecker binderChecker, boolean z, int i3, Handler handler, EventCallback eventCallback) {
        return new ManagedApplicationService(context, componentName, i, i2, str, binderChecker, z, i3, handler, eventCallback);
    }

    public int getUserId() {
        return this.mUserId;
    }

    public ComponentName getComponent() {
        return this.mComponent;
    }

    public boolean disconnectIfNotMatching(ComponentName componentName, int i) {
        if (matches(componentName, i)) {
            return false;
        }
        disconnect();
        return true;
    }

    public void sendEvent(PendingEvent pendingEvent) {
        IInterface iInterface;
        synchronized (this.mLock) {
            iInterface = this.mBoundInterface;
            if (iInterface == null) {
                this.mPendingEvent = pendingEvent;
            }
        }
        if (iInterface != null) {
            try {
                pendingEvent.runEvent(iInterface);
            } catch (RemoteException | RuntimeException e) {
                Slog.e(this.TAG, "Received exception from user service: ", e);
            }
        }
    }

    public void disconnect() {
        synchronized (this.mLock) {
            ServiceConnection serviceConnection = this.mConnection;
            if (serviceConnection == null) {
                return;
            }
            this.mContext.unbindService(serviceConnection);
            this.mConnection = null;
            this.mBoundInterface = null;
        }
    }

    public void connect() {
        synchronized (this.mLock) {
            if (this.mConnection != null) {
                return;
            }
            Intent component = new Intent().setComponent(this.mComponent);
            int i = this.mClientLabel;
            if (i != 0) {
                component.putExtra("android.intent.extra.client_label", i);
            }
            if (this.mSettingsAction != null) {
                component.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent(this.mSettingsAction), 67108864));
            }
            ServiceConnectionC17531 serviceConnectionC17531 = new ServiceConnectionC17531();
            this.mConnection = serviceConnectionC17531;
            try {
                if (!this.mContext.bindServiceAsUser(component, serviceConnectionC17531, this.mIsImportant ? 67108929 : 67108865, new UserHandle(this.mUserId))) {
                    String str = this.TAG;
                    Slog.w(str, "Unable to bind service: " + component);
                    startRetriesLocked();
                }
            } catch (SecurityException e) {
                String str2 = this.TAG;
                Slog.w(str2, "Unable to bind service: " + component, e);
                startRetriesLocked();
            }
        }
    }

    /* renamed from: com.android.server.utils.ManagedApplicationService$1 */
    /* loaded from: classes2.dex */
    public class ServiceConnectionC17531 implements ServiceConnection {
        public ServiceConnectionC17531() {
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            final long currentTimeMillis = System.currentTimeMillis();
            String str = ManagedApplicationService.this.TAG;
            Slog.w(str, "Service binding died: " + componentName);
            synchronized (ManagedApplicationService.this.mLock) {
                if (ManagedApplicationService.this.mConnection != this) {
                    return;
                }
                ManagedApplicationService.this.mHandler.post(new Runnable() { // from class: com.android.server.utils.ManagedApplicationService$1$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManagedApplicationService.ServiceConnectionC17531.this.lambda$onBindingDied$0(currentTimeMillis);
                    }
                });
                ManagedApplicationService.this.mBoundInterface = null;
                ManagedApplicationService.this.startRetriesLocked();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindingDied$0(long j) {
            ManagedApplicationService.this.mEventCb.onServiceEvent(new LogEvent(j, ManagedApplicationService.this.mComponent, 3));
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PendingEvent pendingEvent;
            final long currentTimeMillis = System.currentTimeMillis();
            Slog.i(ManagedApplicationService.this.TAG, "Service connected: " + componentName);
            synchronized (ManagedApplicationService.this.mLock) {
                if (ManagedApplicationService.this.mConnection != this) {
                    return;
                }
                ManagedApplicationService.this.mHandler.post(new Runnable() { // from class: com.android.server.utils.ManagedApplicationService$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManagedApplicationService.ServiceConnectionC17531.this.lambda$onServiceConnected$1(currentTimeMillis);
                    }
                });
                ManagedApplicationService.this.stopRetriesLocked();
                IInterface iInterface = null;
                ManagedApplicationService.this.mBoundInterface = null;
                if (ManagedApplicationService.this.mChecker != null) {
                    ManagedApplicationService managedApplicationService = ManagedApplicationService.this;
                    managedApplicationService.mBoundInterface = managedApplicationService.mChecker.asInterface(iBinder);
                    if (!ManagedApplicationService.this.mChecker.checkType(ManagedApplicationService.this.mBoundInterface)) {
                        ManagedApplicationService.this.mBoundInterface = null;
                        Slog.w(ManagedApplicationService.this.TAG, "Invalid binder from " + componentName);
                        ManagedApplicationService.this.startRetriesLocked();
                        return;
                    }
                    IInterface iInterface2 = ManagedApplicationService.this.mBoundInterface;
                    pendingEvent = ManagedApplicationService.this.mPendingEvent;
                    ManagedApplicationService.this.mPendingEvent = null;
                    iInterface = iInterface2;
                } else {
                    pendingEvent = null;
                }
                if (iInterface == null || pendingEvent == null) {
                    return;
                }
                try {
                    pendingEvent.runEvent(iInterface);
                } catch (RemoteException | RuntimeException e) {
                    Slog.e(ManagedApplicationService.this.TAG, "Received exception from user service: ", e);
                    ManagedApplicationService.this.startRetriesLocked();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onServiceConnected$1(long j) {
            ManagedApplicationService.this.mEventCb.onServiceEvent(new LogEvent(j, ManagedApplicationService.this.mComponent, 1));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            final long currentTimeMillis = System.currentTimeMillis();
            String str = ManagedApplicationService.this.TAG;
            Slog.w(str, "Service disconnected: " + componentName);
            synchronized (ManagedApplicationService.this.mLock) {
                if (ManagedApplicationService.this.mConnection != this) {
                    return;
                }
                ManagedApplicationService.this.mHandler.post(new Runnable() { // from class: com.android.server.utils.ManagedApplicationService$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManagedApplicationService.ServiceConnectionC17531.this.lambda$onServiceDisconnected$2(currentTimeMillis);
                    }
                });
                ManagedApplicationService.this.mBoundInterface = null;
                ManagedApplicationService.this.startRetriesLocked();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onServiceDisconnected$2(long j) {
            ManagedApplicationService.this.mEventCb.onServiceEvent(new LogEvent(j, ManagedApplicationService.this.mComponent, 2));
        }
    }

    public final boolean matches(ComponentName componentName, int i) {
        return Objects.equals(this.mComponent, componentName) && this.mUserId == i;
    }

    public final void startRetriesLocked() {
        if (checkAndDeliverServiceDiedCbLocked()) {
            disconnect();
        } else if (this.mRetrying) {
        } else {
            this.mRetrying = true;
            queueRetryLocked();
        }
    }

    public final void stopRetriesLocked() {
        this.mRetrying = false;
        this.mHandler.removeCallbacks(this.mRetryRunnable);
    }

    public final void queueRetryLocked() {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - this.mLastRetryTimeMs > 64000) {
            this.mNextRetryDurationMs = 2000L;
            this.mRetryCount = 0;
        }
        this.mLastRetryTimeMs = uptimeMillis;
        this.mHandler.postDelayed(this.mRetryRunnable, this.mNextRetryDurationMs);
        this.mNextRetryDurationMs = Math.min(this.mNextRetryDurationMs * 2, 16000L);
        this.mRetryCount++;
    }

    public final boolean checkAndDeliverServiceDiedCbLocked() {
        int i = this.mRetryType;
        if (i == 2 || (i == 3 && this.mRetryCount >= 4)) {
            String str = this.TAG;
            Slog.e(str, "Service " + this.mComponent + " has died too much, not retrying.");
            if (this.mEventCb != null) {
                final long currentTimeMillis = System.currentTimeMillis();
                this.mHandler.post(new Runnable() { // from class: com.android.server.utils.ManagedApplicationService$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManagedApplicationService.this.lambda$checkAndDeliverServiceDiedCbLocked$0(currentTimeMillis);
                    }
                });
                return true;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAndDeliverServiceDiedCbLocked$0(long j) {
        this.mEventCb.onServiceEvent(new LogEvent(j, this.mComponent, 4));
    }

    public final void doRetry() {
        synchronized (this.mLock) {
            if (this.mConnection == null) {
                return;
            }
            if (this.mRetrying) {
                String str = this.TAG;
                Slog.i(str, "Attempting to reconnect " + this.mComponent + "...");
                disconnect();
                if (checkAndDeliverServiceDiedCbLocked()) {
                    return;
                }
                queueRetryLocked();
                connect();
            }
        }
    }
}
