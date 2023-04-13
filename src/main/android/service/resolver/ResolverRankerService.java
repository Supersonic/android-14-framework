package android.service.resolver;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.service.resolver.IResolverRankerService;
import android.util.Log;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public abstract class ResolverRankerService extends Service {
    public static final String BIND_PERMISSION = "android.permission.BIND_RESOLVER_RANKER_SERVICE";
    private static final boolean DEBUG = false;
    private static final String HANDLER_THREAD_NAME = "RESOLVER_RANKER_SERVICE";
    public static final String HOLD_PERMISSION = "android.permission.PROVIDE_RESOLVER_RANKER_SERVICE";
    public static final String SERVICE_INTERFACE = "android.service.resolver.ResolverRankerService";
    private static final String TAG = "ResolverRankerService";
    private volatile Handler mHandler;
    private HandlerThread mHandlerThread;
    private ResolverRankerServiceWrapper mWrapper = null;

    public void onPredictSharingProbabilities(List<ResolverTarget> targets) {
    }

    public void onTrainRankingModel(List<ResolverTarget> targets, int selectedPosition) {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            if (this.mHandlerThread == null) {
                HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
                this.mHandlerThread = handlerThread;
                handlerThread.start();
                this.mHandler = new Handler(this.mHandlerThread.getLooper());
            }
            if (this.mWrapper == null) {
                this.mWrapper = new ResolverRankerServiceWrapper();
            }
            return this.mWrapper;
        }
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandler = null;
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendResult(List<ResolverTarget> targets, IResolverRankerResult result) {
        try {
            result.sendResult(targets);
        } catch (Exception e) {
            Log.m110e(TAG, "failed to send results: " + e);
        }
    }

    /* loaded from: classes3.dex */
    private class ResolverRankerServiceWrapper extends IResolverRankerService.Stub {
        private ResolverRankerServiceWrapper() {
        }

        @Override // android.service.resolver.IResolverRankerService
        public void predict(final List<ResolverTarget> targets, final IResolverRankerResult result) throws RemoteException {
            Runnable predictRunnable = new Runnable() { // from class: android.service.resolver.ResolverRankerService.ResolverRankerServiceWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ResolverRankerService.this.onPredictSharingProbabilities(targets);
                        ResolverRankerService.sendResult(targets, result);
                    } catch (Exception e) {
                        Log.m110e(ResolverRankerService.TAG, "onPredictSharingProbabilities failed; send null results: " + e);
                        ResolverRankerService.sendResult(null, result);
                    }
                }
            };
            Handler h = ResolverRankerService.this.mHandler;
            if (h != null) {
                h.post(predictRunnable);
            }
        }

        @Override // android.service.resolver.IResolverRankerService
        public void train(final List<ResolverTarget> targets, final int selectedPosition) throws RemoteException {
            Runnable trainRunnable = new Runnable() { // from class: android.service.resolver.ResolverRankerService.ResolverRankerServiceWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ResolverRankerService.this.onTrainRankingModel(targets, selectedPosition);
                    } catch (Exception e) {
                        Log.m110e(ResolverRankerService.TAG, "onTrainRankingModel failed; skip train: " + e);
                    }
                }
            };
            Handler h = ResolverRankerService.this.mHandler;
            if (h != null) {
                h.post(trainRunnable);
            }
        }
    }
}
