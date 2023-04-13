package android.window;

import android.app.ResourcesManager;
import android.app.WindowConfiguration;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.view.DisplayInfo;
import android.view.InsetsState;
import android.view.WindowInsets;
import android.view.WindowManagerGlobal;
import android.view.WindowMetrics;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public final class WindowMetricsController {
    private static final boolean LAZY_WINDOW_INSETS = SystemProperties.getBoolean("persist.wm.debug.win_metrics_lazy_insets", true);
    private final Context mContext;

    public WindowMetricsController(Context context) {
        this.mContext = context;
    }

    public WindowMetrics getCurrentWindowMetrics() {
        return getWindowMetricsInternal(false);
    }

    public WindowMetrics getMaximumWindowMetrics() {
        return getWindowMetricsInternal(true);
    }

    private WindowMetrics getWindowMetricsInternal(boolean isMaximum) {
        Rect bounds;
        float density;
        final boolean isScreenRound;
        final int windowingMode;
        synchronized (ResourcesManager.getInstance()) {
            Configuration config = this.mContext.getResources().getConfiguration();
            WindowConfiguration winConfig = config.windowConfiguration;
            bounds = isMaximum ? winConfig.getMaxBounds() : winConfig.getBounds();
            density = config.densityDpi * 0.00625f;
            isScreenRound = config.isScreenRound();
            windowingMode = winConfig.getWindowingMode();
        }
        final IBinder token = Context.getToken(this.mContext);
        final Rect rect = bounds;
        Supplier<WindowInsets> insetsSupplier = new Supplier() { // from class: android.window.WindowMetricsController$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                WindowInsets lambda$getWindowMetricsInternal$0;
                lambda$getWindowMetricsInternal$0 = WindowMetricsController.this.lambda$getWindowMetricsInternal$0(token, rect, isScreenRound, windowingMode);
                return lambda$getWindowMetricsInternal$0;
            }
        };
        if (LAZY_WINDOW_INSETS) {
            return new WindowMetrics(new Rect(bounds), insetsSupplier, density);
        }
        return new WindowMetrics(new Rect(bounds), insetsSupplier.get(), density);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$getWindowMetricsInternal$0(IBinder token, Rect bounds, boolean isScreenRound, int windowingMode) {
        return getWindowInsetsFromServerForDisplay(this.mContext.getDisplayId(), token, bounds, isScreenRound, windowingMode);
    }

    private static WindowInsets getWindowInsetsFromServerForDisplay(int displayId, IBinder token, Rect bounds, boolean isScreenRound, int windowingMode) {
        try {
            InsetsState insetsState = new InsetsState();
            try {
                boolean alwaysConsumeSystemBars = WindowManagerGlobal.getWindowManagerService().getWindowInsets(displayId, token, insetsState);
                float overrideInvScale = CompatibilityInfo.getOverrideInvertedScale();
                if (overrideInvScale != 1.0f) {
                    insetsState.scale(overrideInvScale);
                }
                return insetsState.calculateInsets(bounds, null, isScreenRound, alwaysConsumeSystemBars, 48, 0, 0, -1, windowingMode, null);
            } catch (RemoteException e) {
                e = e;
                throw e.rethrowFromSystemServer();
            }
        } catch (RemoteException e2) {
            e = e2;
        }
    }

    public Set<WindowMetrics> getPossibleMaximumWindowMetrics(int displayId) {
        try {
            List<DisplayInfo> possibleDisplayInfos = WindowManagerGlobal.getWindowManagerService().getPossibleDisplayInfo(displayId);
            Set<WindowMetrics> maxMetrics = new HashSet<>();
            for (int i = 0; i < possibleDisplayInfos.size(); i++) {
                DisplayInfo currentDisplayInfo = possibleDisplayInfos.get(i);
                Rect maxBounds = new Rect(0, 0, currentDisplayInfo.logicalWidth, currentDisplayInfo.logicalHeight);
                boolean isScreenRound = (currentDisplayInfo.flags & 16) != 0;
                WindowInsets windowInsets = getWindowInsetsFromServerForDisplay(currentDisplayInfo.displayId, null, new Rect(0, 0, currentDisplayInfo.getNaturalWidth(), currentDisplayInfo.getNaturalHeight()), isScreenRound, 1);
                WindowInsets windowInsets2 = new WindowInsets.Builder(windowInsets).setRoundedCorners(currentDisplayInfo.roundedCorners).setDisplayCutout(currentDisplayInfo.displayCutout).build();
                float density = currentDisplayInfo.logicalDensityDpi * 0.00625f;
                maxMetrics.add(new WindowMetrics(maxBounds, windowInsets2, density));
            }
            return maxMetrics;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
