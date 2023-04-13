package android.service.wallpapereffectsgeneration;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.wallpapereffectsgeneration.CinematicEffectRequest;
import android.app.wallpapereffectsgeneration.CinematicEffectResponse;
import android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager;
import android.content.Context;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.service.wallpapereffectsgeneration.IWallpaperEffectsGenerationService;
import android.util.Slog;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.function.BiConsumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class WallpaperEffectsGenerationService extends Service {
    private static final boolean DEBUG = false;
    public static final String SERVICE_INTERFACE = "android.service.wallpapereffectsgeneration.WallpaperEffectsGenerationService";
    private static final String TAG = "WallpaperEffectsGenerationService";
    private Handler mHandler;
    private final IWallpaperEffectsGenerationService mInterface = new IWallpaperEffectsGenerationService.Stub() { // from class: android.service.wallpapereffectsgeneration.WallpaperEffectsGenerationService.1
        @Override // android.service.wallpapereffectsgeneration.IWallpaperEffectsGenerationService
        public void onGenerateCinematicEffect(CinematicEffectRequest request) {
            WallpaperEffectsGenerationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.wallpapereffectsgeneration.WallpaperEffectsGenerationService$1$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((WallpaperEffectsGenerationService) obj).onGenerateCinematicEffect((CinematicEffectRequest) obj2);
                }
            }, WallpaperEffectsGenerationService.this, request));
        }
    };
    private IWallpaperEffectsGenerationManager mService;

    public abstract void onGenerateCinematicEffect(CinematicEffectRequest cinematicEffectRequest);

    public final void returnCinematicEffectResponse(CinematicEffectResponse response) {
        try {
            this.mService.returnCinematicEffectResponse(response);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
        IBinder b = ServiceManager.getService(Context.WALLPAPER_EFFECTS_GENERATION_SERVICE);
        this.mService = IWallpaperEffectsGenerationManager.Stub.asInterface(b);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        Slog.m90w(TAG, "Tried to bind to wrong intent (should be android.service.wallpapereffectsgeneration.WallpaperEffectsGenerationService: " + intent);
        return null;
    }
}
