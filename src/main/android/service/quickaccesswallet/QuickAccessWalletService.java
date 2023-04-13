package android.service.quickaccesswallet;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.service.quickaccesswallet.IQuickAccessWalletService;
import android.service.quickaccesswallet.QuickAccessWalletService;
import android.util.Log;
/* loaded from: classes3.dex */
public abstract class QuickAccessWalletService extends Service {
    public static final String ACTION_VIEW_WALLET = "android.service.quickaccesswallet.action.VIEW_WALLET";
    public static final String ACTION_VIEW_WALLET_SETTINGS = "android.service.quickaccesswallet.action.VIEW_WALLET_SETTINGS";
    public static final String SERVICE_INTERFACE = "android.service.quickaccesswallet.QuickAccessWalletService";
    public static final String SERVICE_META_DATA = "android.quickaccesswallet";
    private static final String TAG = "QAWalletService";
    public static final String TILE_SERVICE_META_DATA = "android.quickaccesswallet.tile";
    private IQuickAccessWalletServiceCallbacks mEventListener;
    private String mEventListenerId;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final IQuickAccessWalletService mInterface = new BinderC26081();

    public abstract void onWalletCardSelected(SelectWalletCardRequest selectWalletCardRequest);

    public abstract void onWalletCardsRequested(GetWalletCardsRequest getWalletCardsRequest, GetWalletCardsCallback getWalletCardsCallback);

    public abstract void onWalletDismissed();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.service.quickaccesswallet.QuickAccessWalletService$1 */
    /* loaded from: classes3.dex */
    public class BinderC26081 extends IQuickAccessWalletService.Stub {
        BinderC26081() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWalletCardsRequested$0(GetWalletCardsRequest request, IQuickAccessWalletServiceCallbacks callback) {
            QuickAccessWalletService.this.onWalletCardsRequestedInternal(request, callback);
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onWalletCardsRequested(final GetWalletCardsRequest request, final IQuickAccessWalletServiceCallbacks callback) {
            QuickAccessWalletService.this.mHandler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    QuickAccessWalletService.BinderC26081.this.lambda$onWalletCardsRequested$0(request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWalletCardSelected$1(SelectWalletCardRequest request) {
            QuickAccessWalletService.this.onWalletCardSelected(request);
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onWalletCardSelected(final SelectWalletCardRequest request) {
            QuickAccessWalletService.this.mHandler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    QuickAccessWalletService.BinderC26081.this.lambda$onWalletCardSelected$1(request);
                }
            });
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onWalletDismissed() {
            Handler handler = QuickAccessWalletService.this.mHandler;
            final QuickAccessWalletService quickAccessWalletService = QuickAccessWalletService.this;
            handler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$1$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    QuickAccessWalletService.this.onWalletDismissed();
                }
            });
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onTargetActivityIntentRequested(final IQuickAccessWalletServiceCallbacks callbacks) {
            QuickAccessWalletService.this.mHandler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    QuickAccessWalletService.BinderC26081.this.lambda$onTargetActivityIntentRequested$2(callbacks);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTargetActivityIntentRequested$2(IQuickAccessWalletServiceCallbacks callbacks) {
            QuickAccessWalletService.this.onTargetActivityIntentRequestedInternal(callbacks);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$registerWalletServiceEventListener$3(WalletServiceEventListenerRequest request, IQuickAccessWalletServiceCallbacks callback) {
            QuickAccessWalletService.this.registerDismissWalletListenerInternal(request, callback);
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void registerWalletServiceEventListener(final WalletServiceEventListenerRequest request, final IQuickAccessWalletServiceCallbacks callback) {
            QuickAccessWalletService.this.mHandler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    QuickAccessWalletService.BinderC26081.this.lambda$registerWalletServiceEventListener$3(request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$unregisterWalletServiceEventListener$4(WalletServiceEventListenerRequest request) {
            QuickAccessWalletService.this.unregisterDismissWalletListenerInternal(request);
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void unregisterWalletServiceEventListener(final WalletServiceEventListenerRequest request) {
            QuickAccessWalletService.this.mHandler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    QuickAccessWalletService.BinderC26081.this.lambda$unregisterWalletServiceEventListener$4(request);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWalletCardsRequestedInternal(GetWalletCardsRequest request, IQuickAccessWalletServiceCallbacks callback) {
        onWalletCardsRequested(request, new GetWalletCardsCallbackImpl(request, callback, this.mHandler, this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTargetActivityIntentRequestedInternal(IQuickAccessWalletServiceCallbacks callbacks) {
        try {
            callbacks.onTargetActivityPendingIntentReceived(getTargetActivityPendingIntent());
        } catch (RemoteException e) {
            Log.m103w(TAG, "Error returning wallet cards", e);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (!SERVICE_INTERFACE.equals(intent.getAction())) {
            Log.m104w(TAG, "Wrong action");
            return null;
        }
        return this.mInterface.asBinder();
    }

    public final void sendWalletServiceEvent(final WalletServiceEvent serviceEvent) {
        this.mHandler.post(new Runnable() { // from class: android.service.quickaccesswallet.QuickAccessWalletService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                QuickAccessWalletService.this.lambda$sendWalletServiceEvent$0(serviceEvent);
            }
        });
    }

    public PendingIntent getTargetActivityPendingIntent() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: sendWalletServiceEventInternal */
    public void lambda$sendWalletServiceEvent$0(WalletServiceEvent serviceEvent) {
        IQuickAccessWalletServiceCallbacks iQuickAccessWalletServiceCallbacks = this.mEventListener;
        if (iQuickAccessWalletServiceCallbacks == null) {
            Log.m108i(TAG, "No dismiss listener registered");
            return;
        }
        try {
            iQuickAccessWalletServiceCallbacks.onWalletServiceEvent(serviceEvent);
        } catch (RemoteException e) {
            Log.m103w(TAG, "onWalletServiceEvent error", e);
            this.mEventListenerId = null;
            this.mEventListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDismissWalletListenerInternal(WalletServiceEventListenerRequest request, IQuickAccessWalletServiceCallbacks callback) {
        this.mEventListenerId = request.getListenerId();
        this.mEventListener = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterDismissWalletListenerInternal(WalletServiceEventListenerRequest request) {
        String str = this.mEventListenerId;
        if (str != null && str.equals(request.getListenerId())) {
            this.mEventListenerId = null;
            this.mEventListener = null;
            return;
        }
        Log.m104w(TAG, "dismiss listener missing or replaced");
    }
}
