package android.service.dreams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.p008os.PowerManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SystemClock;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.util.Slog;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public final class Sandman {
    private static final String TAG = "Sandman";

    private Sandman() {
    }

    public static boolean shouldStartDockApp(Context context, Intent intent) {
        ComponentName somnambulatorComponent = ComponentName.unflattenFromString(context.getResources().getString(C4057R.string.config_somnambulatorComponent));
        ComponentName name = intent.resolveActivity(context.getPackageManager());
        return (name == null || name.equals(somnambulatorComponent)) ? false : true;
    }

    public static void startDreamByUserRequest(Context context) {
        startDream(context, false);
    }

    public static void startDreamWhenDockedIfAppropriate(Context context) {
        if (!isScreenSaverEnabled(context) || !isScreenSaverActivatedOnDock(context)) {
            Slog.m94i(TAG, "Dreams currently disabled for docks.");
        } else {
            startDream(context, true);
        }
    }

    private static void startDream(Context context, boolean docked) {
        try {
            IDreamManager dreamManagerService = IDreamManager.Stub.asInterface(ServiceManager.getService(DreamService.DREAM_SERVICE));
            if (dreamManagerService != null && !dreamManagerService.isDreaming()) {
                if (docked) {
                    Slog.m94i(TAG, "Activating dream while docked.");
                    PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
                    powerManager.wakeUp(SystemClock.uptimeMillis(), 3, "android.service.dreams:DREAM");
                } else {
                    Slog.m94i(TAG, "Activating dream by user request.");
                }
                dreamManagerService.dream();
            }
        } catch (RemoteException ex) {
            Slog.m95e(TAG, "Could not start dream when docked.", ex);
        }
    }

    private static boolean isScreenSaverEnabled(Context context) {
        int def = context.getResources().getBoolean(C4057R.bool.config_dreamsEnabledByDefault) ? 1 : 0;
        return Settings.Secure.getIntForUser(context.getContentResolver(), Settings.Secure.SCREENSAVER_ENABLED, def, -2) != 0;
    }

    private static boolean isScreenSaverActivatedOnDock(Context context) {
        int def = context.getResources().getBoolean(C4057R.bool.config_dreamsActivatedOnDockByDefault) ? 1 : 0;
        return Settings.Secure.getIntForUser(context.getContentResolver(), Settings.Secure.SCREENSAVER_ACTIVATE_ON_DOCK, def, -2) != 0;
    }
}
