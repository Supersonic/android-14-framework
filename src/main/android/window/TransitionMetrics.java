package android.window;

import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.util.Singleton;
/* loaded from: classes4.dex */
public class TransitionMetrics {
    private static final Singleton<TransitionMetrics> sTransitionMetrics = new Singleton<TransitionMetrics>() { // from class: android.window.TransitionMetrics.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public TransitionMetrics create() {
            return new TransitionMetrics(WindowOrganizer.getTransitionMetricsReporter());
        }
    };
    private final ITransitionMetricsReporter mTransitionMetricsReporter;

    private TransitionMetrics(ITransitionMetricsReporter reporter) {
        this.mTransitionMetricsReporter = reporter;
    }

    public void reportAnimationStart(IBinder transitionToken) {
        try {
            this.mTransitionMetricsReporter.reportAnimationStart(transitionToken, SystemClock.elapsedRealtime());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static TransitionMetrics getInstance() {
        return sTransitionMetrics.get();
    }
}
