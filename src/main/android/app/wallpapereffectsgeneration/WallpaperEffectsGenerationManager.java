package android.app.wallpapereffectsgeneration;

import android.annotation.SystemApi;
import android.app.wallpapereffectsgeneration.ICinematicEffectListener;
import android.app.wallpapereffectsgeneration.WallpaperEffectsGenerationManager;
import android.p008os.RemoteException;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes.dex */
public final class WallpaperEffectsGenerationManager {
    private final IWallpaperEffectsGenerationManager mService;

    /* loaded from: classes.dex */
    public interface CinematicEffectListener {
        void onCinematicEffectGenerated(CinematicEffectResponse cinematicEffectResponse);
    }

    public WallpaperEffectsGenerationManager(IWallpaperEffectsGenerationManager service) {
        this.mService = service;
    }

    @SystemApi
    public void generateCinematicEffect(CinematicEffectRequest request, Executor executor, CinematicEffectListener listener) {
        try {
            this.mService.generateCinematicEffect(request, new CinematicEffectListenerWrapper(listener, executor));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class CinematicEffectListenerWrapper extends ICinematicEffectListener.Stub {
        private final Executor mExecutor;
        private final CinematicEffectListener mListener;

        CinematicEffectListenerWrapper(CinematicEffectListener listener, Executor executor) {
            this.mListener = listener;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCinematicEffectGenerated$0(CinematicEffectResponse response) {
            this.mListener.onCinematicEffectGenerated(response);
        }

        @Override // android.app.wallpapereffectsgeneration.ICinematicEffectListener
        public void onCinematicEffectGenerated(final CinematicEffectResponse response) {
            this.mExecutor.execute(new Runnable() { // from class: android.app.wallpapereffectsgeneration.WallpaperEffectsGenerationManager$CinematicEffectListenerWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WallpaperEffectsGenerationManager.CinematicEffectListenerWrapper.this.lambda$onCinematicEffectGenerated$0(response);
                }
            });
        }
    }
}
