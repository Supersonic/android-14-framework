package android.p008os.image;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Messenger;
import android.p008os.ParcelableException;
import android.p008os.RemoteException;
import android.util.Slog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
@SystemApi
/* renamed from: android.os.image.DynamicSystemClient */
/* loaded from: classes3.dex */
public class DynamicSystemClient {
    public static final String ACTION_NOTIFY_IF_IN_USE = "android.os.image.action.NOTIFY_IF_IN_USE";
    public static final String ACTION_START_INSTALL = "android.os.image.action.START_INSTALL";
    public static final int CAUSE_ERROR_EXCEPTION = 6;
    public static final int CAUSE_ERROR_INVALID_URL = 4;
    public static final int CAUSE_ERROR_IO = 3;
    public static final int CAUSE_ERROR_IPC = 5;
    public static final int CAUSE_INSTALL_CANCELLED = 2;
    public static final int CAUSE_INSTALL_COMPLETED = 1;
    public static final int CAUSE_NOT_SPECIFIED = 0;
    public static final String KEY_EXCEPTION_DETAIL = "KEY_EXCEPTION_DETAIL";
    public static final String KEY_INSTALLED_SIZE = "KEY_INSTALLED_SIZE";
    public static final String KEY_SYSTEM_SIZE = "KEY_SYSTEM_SIZE";
    public static final String KEY_USERDATA_SIZE = "KEY_USERDATA_SIZE";
    public static final int MSG_POST_STATUS = 3;
    public static final int MSG_REGISTER_LISTENER = 1;
    public static final int MSG_UNREGISTER_LISTENER = 2;
    public static final int STATUS_IN_PROGRESS = 2;
    public static final int STATUS_IN_USE = 4;
    public static final int STATUS_NOT_STARTED = 1;
    public static final int STATUS_READY = 3;
    public static final int STATUS_UNKNOWN = 0;
    private static final String TAG = "DynamicSystemClient";
    private boolean mBound;
    private final Context mContext;
    private Executor mExecutor;
    private OnStatusChangedListener mListener;
    private Messenger mService;
    private final DynSystemServiceConnection mConnection = new DynSystemServiceConnection();
    private final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.image.DynamicSystemClient$InstallationStatus */
    /* loaded from: classes3.dex */
    public @interface InstallationStatus {
    }

    /* renamed from: android.os.image.DynamicSystemClient$OnStatusChangedListener */
    /* loaded from: classes3.dex */
    public interface OnStatusChangedListener {
        void onStatusChanged(int i, int i2, long j, Throwable th);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.image.DynamicSystemClient$StatusChangedCause */
    /* loaded from: classes3.dex */
    public @interface StatusChangedCause {
    }

    /* renamed from: android.os.image.DynamicSystemClient$IncomingHandler */
    /* loaded from: classes3.dex */
    private static class IncomingHandler extends Handler {
        private final WeakReference<DynamicSystemClient> mWeakClient;

        IncomingHandler(DynamicSystemClient service) {
            super(Looper.getMainLooper());
            this.mWeakClient = new WeakReference<>(service);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            DynamicSystemClient service = this.mWeakClient.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

    /* renamed from: android.os.image.DynamicSystemClient$DynSystemServiceConnection */
    /* loaded from: classes3.dex */
    private class DynSystemServiceConnection implements ServiceConnection {
        private DynSystemServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            Slog.m92v(DynamicSystemClient.TAG, "onServiceConnected: " + className);
            DynamicSystemClient.this.mService = new Messenger(service);
            try {
                Message msg = Message.obtain((Handler) null, 1);
                msg.replyTo = DynamicSystemClient.this.mMessenger;
                DynamicSystemClient.this.mService.send(msg);
            } catch (RemoteException e) {
                Slog.m96e(DynamicSystemClient.TAG, "Unable to get status from installation service");
                DynamicSystemClient.this.notifyOnStatusChangedListener(0, 5, 0L, e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Slog.m92v(DynamicSystemClient.TAG, "onServiceDisconnected: " + className);
            DynamicSystemClient.this.mService = null;
        }
    }

    @SystemApi
    public DynamicSystemClient(Context context) {
        this.mContext = context;
    }

    public void setOnStatusChangedListener(Executor executor, OnStatusChangedListener listener) {
        this.mListener = listener;
        this.mExecutor = executor;
    }

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        this.mListener = listener;
        this.mExecutor = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOnStatusChangedListener(final int status, final int cause, final long progress, final Throwable detail) {
        OnStatusChangedListener onStatusChangedListener = this.mListener;
        if (onStatusChangedListener != null) {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new Runnable() { // from class: android.os.image.DynamicSystemClient$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DynamicSystemClient.this.lambda$notifyOnStatusChangedListener$0(status, cause, progress, detail);
                    }
                });
            } else {
                onStatusChangedListener.onStatusChanged(status, cause, progress, detail);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyOnStatusChangedListener$0(int status, int cause, long progress, Throwable detail) {
        this.mListener.onStatusChanged(status, cause, progress, detail);
    }

    @SystemApi
    public void bind() {
        Intent intent = new Intent();
        intent.setClassName("com.android.dynsystem", "com.android.dynsystem.DynamicSystemInstallationService");
        this.mContext.bindService(intent, this.mConnection, 1);
        this.mBound = true;
    }

    @SystemApi
    public void unbind() {
        if (!this.mBound) {
            return;
        }
        if (this.mService != null) {
            try {
                Message msg = Message.obtain((Handler) null, 2);
                msg.replyTo = this.mMessenger;
                this.mService.send(msg);
            } catch (RemoteException e) {
                Slog.m96e(TAG, "Unable to unregister from installation service");
            }
        }
        this.mContext.unbindService(this.mConnection);
        this.mBound = false;
    }

    @SystemApi
    public void start(Uri systemUrl, long systemSize) {
        start(systemUrl, systemSize, 0L);
    }

    public void start(Uri systemUrl, long systemSize, long userdataSize) {
        Intent intent = new Intent();
        intent.setClassName("com.android.dynsystem", "com.android.dynsystem.VerificationActivity");
        intent.setData(systemUrl);
        intent.setAction(ACTION_START_INSTALL);
        intent.setFlags(268435456);
        intent.putExtra(KEY_SYSTEM_SIZE, systemSize);
        intent.putExtra(KEY_USERDATA_SIZE, userdataSize);
        this.mContext.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 3:
                int status = msg.arg1;
                int cause = msg.arg2;
                Bundle bundle = (Bundle) msg.obj;
                long progress = bundle.getLong(KEY_INSTALLED_SIZE);
                ParcelableException t = (ParcelableException) bundle.getSerializable(KEY_EXCEPTION_DETAIL, ParcelableException.class);
                Throwable detail = t == null ? null : t.getCause();
                notifyOnStatusChangedListener(status, cause, progress, detail);
                return;
            default:
                return;
        }
    }
}
