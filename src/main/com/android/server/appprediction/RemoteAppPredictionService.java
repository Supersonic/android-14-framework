package com.android.server.appprediction;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.service.appprediction.IPredictionService;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
/* loaded from: classes.dex */
public class RemoteAppPredictionService extends AbstractMultiplePendingRequestsRemoteService<RemoteAppPredictionService, IPredictionService> {
    public final RemoteAppPredictionServiceCallbacks mCallback;

    /* loaded from: classes.dex */
    public interface RemoteAppPredictionServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteAppPredictionService> {
        void onConnectedStateChanged(boolean z);
    }

    public long getRemoteRequestMillis() {
        return 2000L;
    }

    public long getTimeoutIdleBindMillis() {
        return 0L;
    }

    public RemoteAppPredictionService(Context context, String str, ComponentName componentName, int i, RemoteAppPredictionServiceCallbacks remoteAppPredictionServiceCallbacks, boolean z, boolean z2) {
        super(context, str, componentName, i, remoteAppPredictionServiceCallbacks, context.getMainThreadHandler(), z ? 4194304 : 0, z2, 1);
        this.mCallback = remoteAppPredictionServiceCallbacks;
    }

    public IPredictionService getServiceInterface(IBinder iBinder) {
        return IPredictionService.Stub.asInterface(iBinder);
    }

    public void reconnect() {
        super.scheduleBind();
    }

    public void scheduleOnResolvedService(AbstractRemoteService.AsyncRequest<IPredictionService> asyncRequest) {
        scheduleAsyncRequest(asyncRequest);
    }

    public void executeOnResolvedService(AbstractRemoteService.AsyncRequest<IPredictionService> asyncRequest) {
        executeAsyncRequest(asyncRequest);
    }

    public void handleOnConnectedStateChanged(boolean z) {
        RemoteAppPredictionServiceCallbacks remoteAppPredictionServiceCallbacks = this.mCallback;
        if (remoteAppPredictionServiceCallbacks != null) {
            remoteAppPredictionServiceCallbacks.onConnectedStateChanged(z);
        }
    }
}
