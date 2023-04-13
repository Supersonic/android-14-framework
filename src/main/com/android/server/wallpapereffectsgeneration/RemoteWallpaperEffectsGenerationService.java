package com.android.server.wallpapereffectsgeneration;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.service.wallpapereffectsgeneration.IWallpaperEffectsGenerationService;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
/* loaded from: classes2.dex */
public class RemoteWallpaperEffectsGenerationService extends AbstractMultiplePendingRequestsRemoteService<RemoteWallpaperEffectsGenerationService, IWallpaperEffectsGenerationService> {
    public static final String TAG = RemoteWallpaperEffectsGenerationService.class.getSimpleName();
    public final RemoteWallpaperEffectsGenerationServiceCallback mCallback;

    /* loaded from: classes2.dex */
    public interface RemoteWallpaperEffectsGenerationServiceCallback extends AbstractRemoteService.VultureCallback<RemoteWallpaperEffectsGenerationService> {
        void onConnectedStateChanged(boolean z);
    }

    public long getRemoteRequestMillis() {
        return 2000L;
    }

    public long getTimeoutIdleBindMillis() {
        return 0L;
    }

    public RemoteWallpaperEffectsGenerationService(Context context, ComponentName componentName, int i, RemoteWallpaperEffectsGenerationServiceCallback remoteWallpaperEffectsGenerationServiceCallback, boolean z, boolean z2) {
        super(context, "android.service.wallpapereffectsgeneration.WallpaperEffectsGenerationService", componentName, i, remoteWallpaperEffectsGenerationServiceCallback, context.getMainThreadHandler(), z ? 4194304 : 0, z2, 1);
        this.mCallback = remoteWallpaperEffectsGenerationServiceCallback;
    }

    public IWallpaperEffectsGenerationService getServiceInterface(IBinder iBinder) {
        return IWallpaperEffectsGenerationService.Stub.asInterface(iBinder);
    }

    public void reconnect() {
        super.scheduleBind();
    }

    public void executeOnResolvedService(AbstractRemoteService.AsyncRequest<IWallpaperEffectsGenerationService> asyncRequest) {
        executeAsyncRequest(asyncRequest);
    }

    public void handleOnConnectedStateChanged(boolean z) {
        RemoteWallpaperEffectsGenerationServiceCallback remoteWallpaperEffectsGenerationServiceCallback = this.mCallback;
        if (remoteWallpaperEffectsGenerationServiceCallback != null) {
            remoteWallpaperEffectsGenerationServiceCallback.onConnectedStateChanged(z);
        }
    }
}
