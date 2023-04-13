package android.app;

import android.annotation.SystemApi;
import android.app.IGameManagerService;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class GameManager {
    public static final int GAME_MODE_BATTERY = 3;
    public static final int GAME_MODE_CUSTOM = 4;
    public static final int GAME_MODE_PERFORMANCE = 2;
    public static final int GAME_MODE_STANDARD = 1;
    public static final int GAME_MODE_UNSUPPORTED = 0;
    private static final String TAG = "GameManager";
    private final Context mContext;
    private final IGameManagerService mService = IGameManagerService.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.GAME_SERVICE));

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface GameMode {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GameManager(Context context, Handler handler) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
    }

    public int getGameMode() {
        return getGameModeImpl(this.mContext.getPackageName(), this.mContext.getApplicationInfo().targetSdkVersion);
    }

    public int getGameMode(String packageName) {
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.m191of(0L));
            int targetSdkVersion = applicationInfo.targetSdkVersion;
            return getGameModeImpl(packageName, targetSdkVersion);
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private int getGameModeImpl(String packageName, int targetSdkVersion) {
        try {
            int gameMode = this.mService.getGameMode(packageName, this.mContext.getUserId());
            if (gameMode == 4 && targetSdkVersion <= 33) {
                return 1;
            }
            return gameMode;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public GameModeInfo getGameModeInfo(String packageName) {
        try {
            return this.mService.getGameModeInfo(packageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setGameMode(String packageName, int gameMode) {
        try {
            this.mService.setGameMode(packageName, gameMode, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int[] getAvailableGameModes(String packageName) {
        try {
            return this.mService.getAvailableGameModes(packageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAngleEnabled(String packageName) {
        try {
            return this.mService.isAngleEnabled(packageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyGraphicsEnvironmentSetup() {
        try {
            this.mService.notifyGraphicsEnvironmentSetup(this.mContext.getPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setGameState(GameState gameState) {
        try {
            this.mService.setGameState(this.mContext.getPackageName(), gameState, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setGameServiceProvider(String packageName) {
        try {
            this.mService.setGameServiceProvider(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void updateCustomGameModeConfiguration(String packageName, GameModeConfiguration gameModeConfig) {
        try {
            this.mService.updateCustomGameModeConfiguration(packageName, gameModeConfig, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
