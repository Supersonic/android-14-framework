package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.ims.DelegateMessageCallback;
import android.telephony.ims.DelegateRequest;
import android.telephony.ims.DelegateStateCallback;
import android.telephony.ims.aidl.ISipDelegate;
import android.telephony.ims.aidl.ISipDelegateMessageCallback;
import android.telephony.ims.aidl.ISipDelegateStateCallback;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.aidl.SipDelegateAidlWrapper;
import android.telephony.ims.stub.SipTransportImplBase;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public class SipTransportImplBase {
    private static final String LOG_TAG = "SipTransportIB";
    private Executor mBinderExecutor;
    private final IBinder.DeathRecipient mDeathRecipient = new C33071();
    private final ISipTransport.Stub mSipTransportImpl = new BinderC33082();
    private final ArrayList<SipDelegateAidlWrapper> mDelegates = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.stub.SipTransportImplBase$1 */
    /* loaded from: classes3.dex */
    public class C33071 implements IBinder.DeathRecipient {
        C33071() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$0() {
            SipTransportImplBase.this.binderDiedInternal(null);
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            SipTransportImplBase.this.mBinderExecutor.execute(new Runnable() { // from class: android.telephony.ims.stub.SipTransportImplBase$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SipTransportImplBase.C33071.this.lambda$binderDied$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$1(IBinder who) {
            SipTransportImplBase.this.binderDiedInternal(who);
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied(final IBinder who) {
            SipTransportImplBase.this.mBinderExecutor.execute(new Runnable() { // from class: android.telephony.ims.stub.SipTransportImplBase$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SipTransportImplBase.C33071.this.lambda$binderDied$1(who);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.stub.SipTransportImplBase$2 */
    /* loaded from: classes3.dex */
    public class BinderC33082 extends ISipTransport.Stub {
        BinderC33082() {
        }

        @Override // android.telephony.ims.aidl.ISipTransport
        public void createSipDelegate(final int subId, final DelegateRequest request, final ISipDelegateStateCallback dc, final ISipDelegateMessageCallback mc) {
            long token = Binder.clearCallingIdentity();
            try {
                SipTransportImplBase.this.mBinderExecutor.execute(new Runnable() { // from class: android.telephony.ims.stub.SipTransportImplBase$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipTransportImplBase.BinderC33082.this.lambda$createSipDelegate$0(subId, request, dc, mc);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$createSipDelegate$0(int subId, DelegateRequest request, ISipDelegateStateCallback dc, ISipDelegateMessageCallback mc) {
            SipTransportImplBase.this.createSipDelegateInternal(subId, request, dc, mc);
        }

        @Override // android.telephony.ims.aidl.ISipTransport
        public void destroySipDelegate(final ISipDelegate delegate, final int reason) {
            long token = Binder.clearCallingIdentity();
            try {
                SipTransportImplBase.this.mBinderExecutor.execute(new Runnable() { // from class: android.telephony.ims.stub.SipTransportImplBase$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipTransportImplBase.BinderC33082.this.lambda$destroySipDelegate$1(delegate, reason);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$destroySipDelegate$1(ISipDelegate delegate, int reason) {
            SipTransportImplBase.this.destroySipDelegateInternal(delegate, reason);
        }
    }

    public SipTransportImplBase() {
    }

    public SipTransportImplBase(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        this.mBinderExecutor = executor;
    }

    public void createSipDelegate(int subscriptionId, DelegateRequest request, DelegateStateCallback dc, DelegateMessageCallback mc) {
        throw new UnsupportedOperationException("createSipDelegate not implemented!");
    }

    public void destroySipDelegate(SipDelegate delegate, int reason) {
        throw new UnsupportedOperationException("destroySipDelegate not implemented!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createSipDelegateInternal(int subId, DelegateRequest r, ISipDelegateStateCallback cb, ISipDelegateMessageCallback mc) {
        SipDelegateAidlWrapper wrapper = new SipDelegateAidlWrapper(this.mBinderExecutor, cb, mc);
        this.mDelegates.add(wrapper);
        linkDeathRecipient(wrapper);
        createSipDelegate(subId, r, wrapper, wrapper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroySipDelegateInternal(ISipDelegate d, int reason) {
        SipDelegateAidlWrapper result = null;
        Iterator<SipDelegateAidlWrapper> it = this.mDelegates.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SipDelegateAidlWrapper w = it.next();
            if (Objects.equals(d, w.getDelegateBinder())) {
                result = w;
                break;
            }
        }
        if (result != null) {
            unlinkDeathRecipient(result);
            this.mDelegates.remove(result);
            destroySipDelegate(result.getDelegate(), reason);
            return;
        }
        Log.m104w(LOG_TAG, "destroySipDelegateInternal, could not findSipDelegate corresponding to " + d);
    }

    private void linkDeathRecipient(SipDelegateAidlWrapper w) {
        try {
            w.getStateCallbackBinder().asBinder().linkToDeath(this.mDeathRecipient, 0);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "linkDeathRecipient, remote process already died, cleaning up.");
            this.mDeathRecipient.binderDied(w.getStateCallbackBinder().asBinder());
        }
    }

    private void unlinkDeathRecipient(SipDelegateAidlWrapper w) {
        try {
            w.getStateCallbackBinder().asBinder().unlinkToDeath(this.mDeathRecipient, 0);
        } catch (NoSuchElementException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:5:0x000e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void binderDiedInternal(IBinder who) {
        Iterator<SipDelegateAidlWrapper> it = this.mDelegates.iterator();
        while (it.hasNext()) {
            SipDelegateAidlWrapper w = it.next();
            if (who == null || w.getStateCallbackBinder().asBinder().equals(who)) {
                Log.m104w(LOG_TAG, "Binder death detected for " + w + ", calling destroy and removing.");
                this.mDelegates.remove(w);
                destroySipDelegate(w.getDelegate(), 1);
                return;
            }
            while (it.hasNext()) {
            }
        }
        Log.m104w(LOG_TAG, "Binder death detected for IBinder " + who + ", but couldn't find matching SipDelegate");
    }

    public ISipTransport getBinder() {
        return this.mSipTransportImpl;
    }

    public final void setDefaultExecutor(Executor executor) {
        if (this.mBinderExecutor == null) {
            this.mBinderExecutor = executor;
        }
    }
}
