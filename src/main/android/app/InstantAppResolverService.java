package android.app;

import android.annotation.SystemApi;
import android.app.IInstantAppResolver;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.InstantAppRequestInfo;
import android.content.p001pm.InstantAppResolveInfo;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.IRemoteCallback;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.Log;
import android.util.Slog;
import com.android.internal.p028os.SomeArgs;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public abstract class InstantAppResolverService extends Service {
    private static final boolean DEBUG_INSTANT = Build.IS_DEBUGGABLE;
    public static final String EXTRA_RESOLVE_INFO = "android.app.extra.RESOLVE_INFO";
    public static final String EXTRA_SEQUENCE = "android.app.extra.SEQUENCE";
    private static final String TAG = "PackageManager";
    Handler mHandler;

    @Deprecated
    public void onGetInstantAppResolveInfo(int[] digestPrefix, String token, InstantAppResolutionCallback callback) {
        throw new IllegalStateException("Must define onGetInstantAppResolveInfo");
    }

    @Deprecated
    public void onGetInstantAppIntentFilter(int[] digestPrefix, String token, InstantAppResolutionCallback callback) {
        throw new IllegalStateException("Must define onGetInstantAppIntentFilter");
    }

    @Deprecated
    public void onGetInstantAppResolveInfo(Intent sanitizedIntent, int[] hostDigestPrefix, String token, InstantAppResolutionCallback callback) {
        if (sanitizedIntent.isWebIntent()) {
            onGetInstantAppResolveInfo(hostDigestPrefix, token, callback);
        } else {
            callback.onInstantAppResolveInfo(Collections.emptyList());
        }
    }

    @Deprecated
    public void onGetInstantAppIntentFilter(Intent sanitizedIntent, int[] hostDigestPrefix, String token, InstantAppResolutionCallback callback) {
        Log.m110e(TAG, "New onGetInstantAppIntentFilter is not overridden");
        if (sanitizedIntent.isWebIntent()) {
            onGetInstantAppIntentFilter(hostDigestPrefix, token, callback);
        } else {
            callback.onInstantAppResolveInfo(Collections.emptyList());
        }
    }

    @Deprecated
    public void onGetInstantAppResolveInfo(Intent sanitizedIntent, int[] hostDigestPrefix, UserHandle userHandle, String token, InstantAppResolutionCallback callback) {
        onGetInstantAppResolveInfo(sanitizedIntent, hostDigestPrefix, token, callback);
    }

    @Deprecated
    public void onGetInstantAppIntentFilter(Intent sanitizedIntent, int[] hostDigestPrefix, UserHandle userHandle, String token, InstantAppResolutionCallback callback) {
        onGetInstantAppIntentFilter(sanitizedIntent, hostDigestPrefix, token, callback);
    }

    public void onGetInstantAppResolveInfo(InstantAppRequestInfo request, InstantAppResolutionCallback callback) {
        onGetInstantAppResolveInfo(request.getIntent(), request.getHostDigestPrefix(), request.getUserHandle(), request.getToken(), callback);
    }

    public void onGetInstantAppIntentFilter(InstantAppRequestInfo request, InstantAppResolutionCallback callback) {
        onGetInstantAppIntentFilter(request.getIntent(), request.getHostDigestPrefix(), request.getUserHandle(), request.getToken(), callback);
    }

    Looper getLooper() {
        return getBaseContext().getMainLooper();
    }

    @Override // android.app.Service, android.content.ContextWrapper
    public final void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.mHandler = new ServiceHandler(getLooper());
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IInstantAppResolver.Stub() { // from class: android.app.InstantAppResolverService.1
            @Override // android.app.IInstantAppResolver
            public void getInstantAppResolveInfoList(InstantAppRequestInfo request, int sequence, IRemoteCallback callback) {
                if (InstantAppResolverService.DEBUG_INSTANT) {
                    Slog.m92v(InstantAppResolverService.TAG, NavigationBarInflaterView.SIZE_MOD_START + request.getToken() + "] Phase1 called; posting");
                }
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = request;
                args.arg2 = callback;
                InstantAppResolverService.this.mHandler.obtainMessage(1, sequence, 0, args).sendToTarget();
            }

            @Override // android.app.IInstantAppResolver
            public void getInstantAppIntentFilterList(InstantAppRequestInfo request, IRemoteCallback callback) {
                if (InstantAppResolverService.DEBUG_INSTANT) {
                    Slog.m92v(InstantAppResolverService.TAG, NavigationBarInflaterView.SIZE_MOD_START + request.getToken() + "] Phase2 called; posting");
                }
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = request;
                args.arg2 = callback;
                InstantAppResolverService.this.mHandler.obtainMessage(2, args).sendToTarget();
            }
        };
    }

    /* loaded from: classes.dex */
    public static final class InstantAppResolutionCallback {
        private final IRemoteCallback mCallback;
        private final int mSequence;

        public InstantAppResolutionCallback(int sequence, IRemoteCallback callback) {
            this.mCallback = callback;
            this.mSequence = sequence;
        }

        public void onInstantAppResolveInfo(List<InstantAppResolveInfo> resolveInfo) {
            Bundle data = new Bundle();
            data.putParcelableList(InstantAppResolverService.EXTRA_RESOLVE_INFO, resolveInfo);
            data.putInt(InstantAppResolverService.EXTRA_SEQUENCE, this.mSequence);
            try {
                this.mCallback.sendResult(data);
            } catch (RemoteException e) {
            }
        }
    }

    /* loaded from: classes.dex */
    private final class ServiceHandler extends Handler {
        public static final int MSG_GET_INSTANT_APP_INTENT_FILTER = 2;
        public static final int MSG_GET_INSTANT_APP_RESOLVE_INFO = 1;

        public ServiceHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message message) {
            int action = message.what;
            switch (action) {
                case 1:
                    SomeArgs args = (SomeArgs) message.obj;
                    InstantAppRequestInfo request = (InstantAppRequestInfo) args.arg1;
                    IRemoteCallback callback = (IRemoteCallback) args.arg2;
                    args.recycle();
                    int sequence = message.arg1;
                    if (InstantAppResolverService.DEBUG_INSTANT) {
                        Slog.m98d(InstantAppResolverService.TAG, NavigationBarInflaterView.SIZE_MOD_START + request.getToken() + "] Phase1 request; prefix: " + Arrays.toString(request.getHostDigestPrefix()) + ", userId: " + request.getUserHandle().getIdentifier());
                    }
                    InstantAppResolverService.this.onGetInstantAppResolveInfo(request, new InstantAppResolutionCallback(sequence, callback));
                    return;
                case 2:
                    SomeArgs args2 = (SomeArgs) message.obj;
                    InstantAppRequestInfo request2 = (InstantAppRequestInfo) args2.arg1;
                    IRemoteCallback callback2 = (IRemoteCallback) args2.arg2;
                    args2.recycle();
                    if (InstantAppResolverService.DEBUG_INSTANT) {
                        Slog.m98d(InstantAppResolverService.TAG, NavigationBarInflaterView.SIZE_MOD_START + request2.getToken() + "] Phase2 request; prefix: " + Arrays.toString(request2.getHostDigestPrefix()) + ", userId: " + request2.getUserHandle().getIdentifier());
                    }
                    InstantAppResolverService.this.onGetInstantAppIntentFilter(request2, new InstantAppResolutionCallback(-1, callback2));
                    return;
                default:
                    throw new IllegalArgumentException("Unknown message: " + action);
            }
        }
    }
}
