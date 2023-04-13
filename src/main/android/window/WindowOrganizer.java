package android.window;

import android.app.ActivityTaskManager;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Singleton;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public class WindowOrganizer {
    private static final Singleton<IWindowOrganizerController> IWindowOrganizerControllerSingleton = new Singleton<IWindowOrganizerController>() { // from class: android.window.WindowOrganizer.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IWindowOrganizerController create() {
            try {
                return ActivityTaskManager.getService().getWindowOrganizerController();
            } catch (RemoteException e) {
                return null;
            }
        }
    };

    public void applyTransaction(WindowContainerTransaction t) {
        try {
            if (!t.isEmpty()) {
                getWindowOrganizerController().applyTransaction(t);
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int applySyncTransaction(WindowContainerTransaction t, WindowContainerTransactionCallback callback) {
        try {
            return getWindowOrganizerController().applySyncTransaction(t, callback.mInterface);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IBinder startNewTransition(int type, WindowContainerTransaction t) {
        try {
            return getWindowOrganizerController().startNewTransition(type, t);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startTransition(IBinder transitionToken, WindowContainerTransaction t) {
        try {
            getWindowOrganizerController().startTransition(transitionToken, t);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int finishTransition(IBinder transitionToken, WindowContainerTransaction t, WindowContainerTransactionCallback callback) {
        try {
            return getWindowOrganizerController().finishTransition(transitionToken, t, callback != null ? callback.mInterface : null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int startLegacyTransition(int type, RemoteAnimationAdapter adapter, WindowContainerTransactionCallback syncCallback, WindowContainerTransaction t) {
        try {
            return getWindowOrganizerController().startLegacyTransition(type, adapter, syncCallback.mInterface, t);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerTransitionPlayer(ITransitionPlayer player) {
        try {
            getWindowOrganizerController().registerTransitionPlayer(player);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static ITransitionMetricsReporter getTransitionMetricsReporter() {
        try {
            return getWindowOrganizerController().getTransitionMetricsReporter();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean shareTransactionQueue() {
        try {
            IBinder wmApplyToken = getWindowOrganizerController().getApplyToken();
            if (wmApplyToken == null) {
                return false;
            }
            SurfaceControl.Transaction.setDefaultApplyToken(wmApplyToken);
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IWindowOrganizerController getWindowOrganizerController() {
        return IWindowOrganizerControllerSingleton.get();
    }
}
