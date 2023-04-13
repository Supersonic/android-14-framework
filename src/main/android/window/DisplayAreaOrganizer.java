package android.window;

import android.p008os.RemoteException;
import android.view.SurfaceControl;
import android.window.DisplayAreaOrganizer;
import android.window.IDisplayAreaOrganizer;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes4.dex */
public class DisplayAreaOrganizer extends WindowOrganizer {
    public static final int FEATURE_DEFAULT_TASK_CONTAINER = 1;
    public static final int FEATURE_FULLSCREEN_MAGNIFICATION = 5;
    public static final int FEATURE_HIDE_DISPLAY_CUTOUT = 6;
    public static final int FEATURE_IME = 8;
    public static final int FEATURE_IME_PLACEHOLDER = 7;
    public static final int FEATURE_ONE_HANDED = 3;
    public static final int FEATURE_ROOT = 0;
    public static final int FEATURE_RUNTIME_TASK_CONTAINER_FIRST = 20002;
    public static final int FEATURE_SYSTEM_FIRST = 0;
    public static final int FEATURE_SYSTEM_LAST = 10000;
    public static final int FEATURE_UNDEFINED = -1;
    public static final int FEATURE_VENDOR_FIRST = 10001;
    public static final int FEATURE_VENDOR_LAST = 20001;
    public static final int FEATURE_WINDOWED_MAGNIFICATION = 4;
    public static final int FEATURE_WINDOW_TOKENS = 2;
    public static final String KEY_ROOT_DISPLAY_AREA_ID = "root_display_area_id";
    private final Executor mExecutor;
    private final IDisplayAreaOrganizer mInterface = new BinderC39431();

    public DisplayAreaOrganizer(Executor executor) {
        this.mExecutor = executor;
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    public List<DisplayAreaAppearedInfo> registerOrganizer(int displayAreaFeature) {
        try {
            return getController().registerOrganizer(this.mInterface, displayAreaFeature).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterOrganizer() {
        try {
            getController().unregisterOrganizer(this.mInterface);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public DisplayAreaAppearedInfo createTaskDisplayArea(int displayId, int parentFeatureId, String name) {
        try {
            return getController().createTaskDisplayArea(this.mInterface, displayId, parentFeatureId, name);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void deleteTaskDisplayArea(WindowContainerToken taskDisplayArea) {
        try {
            getController().deleteTaskDisplayArea(taskDisplayArea);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo, SurfaceControl leash) {
    }

    public void onDisplayAreaVanished(DisplayAreaInfo displayAreaInfo) {
    }

    public void onDisplayAreaInfoChanged(DisplayAreaInfo displayAreaInfo) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.window.DisplayAreaOrganizer$1 */
    /* loaded from: classes4.dex */
    public class BinderC39431 extends IDisplayAreaOrganizer.Stub {
        BinderC39431() {
        }

        @Override // android.window.IDisplayAreaOrganizer
        public void onDisplayAreaAppeared(final DisplayAreaInfo displayAreaInfo, final SurfaceControl leash) {
            DisplayAreaOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.DisplayAreaOrganizer$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayAreaOrganizer.BinderC39431.this.lambda$onDisplayAreaAppeared$0(displayAreaInfo, leash);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDisplayAreaAppeared$0(DisplayAreaInfo displayAreaInfo, SurfaceControl leash) {
            DisplayAreaOrganizer.this.onDisplayAreaAppeared(displayAreaInfo, leash);
        }

        @Override // android.window.IDisplayAreaOrganizer
        public void onDisplayAreaVanished(final DisplayAreaInfo displayAreaInfo) {
            DisplayAreaOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.DisplayAreaOrganizer$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayAreaOrganizer.BinderC39431.this.lambda$onDisplayAreaVanished$1(displayAreaInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDisplayAreaVanished$1(DisplayAreaInfo displayAreaInfo) {
            DisplayAreaOrganizer.this.onDisplayAreaVanished(displayAreaInfo);
        }

        @Override // android.window.IDisplayAreaOrganizer
        public void onDisplayAreaInfoChanged(final DisplayAreaInfo displayAreaInfo) {
            DisplayAreaOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.DisplayAreaOrganizer$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayAreaOrganizer.BinderC39431.this.lambda$onDisplayAreaInfoChanged$2(displayAreaInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDisplayAreaInfoChanged$2(DisplayAreaInfo displayAreaInfo) {
            DisplayAreaOrganizer.this.onDisplayAreaInfoChanged(displayAreaInfo);
        }
    }

    private IDisplayAreaOrganizerController getController() {
        try {
            return getWindowOrganizerController().getDisplayAreaOrganizerController();
        } catch (RemoteException e) {
            return null;
        }
    }
}
