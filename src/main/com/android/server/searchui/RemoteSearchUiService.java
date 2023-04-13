package com.android.server.searchui;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.service.search.ISearchUiService;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
/* loaded from: classes2.dex */
public class RemoteSearchUiService extends AbstractMultiplePendingRequestsRemoteService<RemoteSearchUiService, ISearchUiService> {
    public final RemoteSearchUiServiceCallbacks mCallback;

    /* loaded from: classes2.dex */
    public interface RemoteSearchUiServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteSearchUiService> {
        void onConnectedStateChanged(boolean z);
    }

    public long getRemoteRequestMillis() {
        return 2000L;
    }

    public long getTimeoutIdleBindMillis() {
        return 0L;
    }

    public RemoteSearchUiService(Context context, String str, ComponentName componentName, int i, RemoteSearchUiServiceCallbacks remoteSearchUiServiceCallbacks, boolean z, boolean z2) {
        super(context, str, componentName, i, remoteSearchUiServiceCallbacks, context.getMainThreadHandler(), z ? 4194304 : 0, z2, 1);
        this.mCallback = remoteSearchUiServiceCallbacks;
    }

    public ISearchUiService getServiceInterface(IBinder iBinder) {
        return ISearchUiService.Stub.asInterface(iBinder);
    }

    public void reconnect() {
        super.scheduleBind();
    }

    public void executeOnResolvedService(AbstractRemoteService.AsyncRequest<ISearchUiService> asyncRequest) {
        executeAsyncRequest(asyncRequest);
    }

    public void handleOnConnectedStateChanged(boolean z) {
        RemoteSearchUiServiceCallbacks remoteSearchUiServiceCallbacks = this.mCallback;
        if (remoteSearchUiServiceCallbacks != null) {
            remoteSearchUiServiceCallbacks.onConnectedStateChanged(z);
        }
    }
}
