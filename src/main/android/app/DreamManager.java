package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamManager;
import com.android.internal.C4057R;
/* loaded from: classes.dex */
public class DreamManager {
    private final Context mContext;
    private final IDreamManager mService = IDreamManager.Stub.asInterface(ServiceManager.getServiceOrThrow(DreamService.DREAM_SERVICE));

    public DreamManager(Context context) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
    }

    public boolean isScreensaverEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), Settings.Secure.SCREENSAVER_ENABLED, 0, -2) != 0;
    }

    public void setScreensaverEnabled(boolean enabled) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), Settings.Secure.SCREENSAVER_ENABLED, enabled ? 1 : 0, -2);
    }

    public boolean areDreamsSupported() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_dreamsSupported);
    }

    public void startDream() {
        try {
            this.mService.dream();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void stopDream() {
        try {
            this.mService.awaken();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setActiveDream(ComponentName dreamComponent) {
        ComponentName[] dreams = {dreamComponent};
        try {
            this.mService.setDreamComponentsForUser(this.mContext.getUserId(), dreamComponent != null ? dreams : null);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setSystemDreamComponent(ComponentName dreamComponent) {
        try {
            this.mService.setSystemDreamComponent(dreamComponent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setDreamOverlay(ComponentName dreamOverlayComponent) {
        try {
            this.mService.registerDreamOverlayService(dreamOverlayComponent);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public boolean isDreaming() {
        try {
            return this.mService.isDreaming();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }
}
