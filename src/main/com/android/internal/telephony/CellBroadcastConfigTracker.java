package com.android.internal.telephony;

import android.os.AsyncResult;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.telephony.CellBroadcastIdRange;
import android.telephony.SubscriptionManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CellBroadcastConfigTracker;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class CellBroadcastConfigTracker extends StateMachine {
    private static final boolean DBG = Build.IS_DEBUGGABLE;
    private List<CellBroadcastIdRange> mCbRanges3gpp;
    private List<CellBroadcastIdRange> mCbRanges3gpp2;
    private CdmaActivatingState mCdmaActivatingState;
    private CdmaConfiguringState mCdmaConfiguringState;
    private DefaultState mDefaultState;
    private GsmActivatingState mGsmActivatingState;
    private GsmConfiguringState mGsmConfiguringState;
    private IdleState mIdleState;
    private Phone mPhone;
    @VisibleForTesting
    public final SubscriptionManager.OnSubscriptionsChangedListener mSubChangedListener;
    @VisibleForTesting
    public int mSubId;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Request {
        Consumer<Integer> mCallback;
        private final List<CellBroadcastIdRange> mCbRangesRequest3gpp = new CopyOnWriteArrayList();
        private final List<CellBroadcastIdRange> mCbRangesRequest3gpp2 = new CopyOnWriteArrayList();

        Request(List<CellBroadcastIdRange> list, Consumer<Integer> consumer) {
            list.forEach(new Consumer() { // from class: com.android.internal.telephony.CellBroadcastConfigTracker$Request$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    CellBroadcastConfigTracker.Request.this.lambda$new$0((CellBroadcastIdRange) obj);
                }
            });
            this.mCallback = consumer;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(CellBroadcastIdRange cellBroadcastIdRange) {
            if (cellBroadcastIdRange.getType() == 1) {
                this.mCbRangesRequest3gpp.add(cellBroadcastIdRange);
            } else {
                this.mCbRangesRequest3gpp2.add(cellBroadcastIdRange);
            }
        }

        List<CellBroadcastIdRange> get3gppRanges() {
            return this.mCbRangesRequest3gpp;
        }

        List<CellBroadcastIdRange> get3gpp2Ranges() {
            return this.mCbRangesRequest3gpp2;
        }

        Consumer<Integer> getCallback() {
            return this.mCallback;
        }

        public String toString() {
            return "Request[mCbRangesRequest3gpp = " + this.mCbRangesRequest3gpp + ", mCbRangesRequest3gpp2 = " + this.mCbRangesRequest3gpp2 + ", mCallback = " + this.mCallback + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DefaultState extends State {
        private DefaultState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            CellBroadcastConfigTracker.this.mPhone.registerForRadioOffOrNotAvailable(CellBroadcastConfigTracker.this.getHandler(), 4, null);
            ((SubscriptionManager) CellBroadcastConfigTracker.this.mPhone.getContext().getSystemService(SubscriptionManager.class)).addOnSubscriptionsChangedListener(new HandlerExecutor(CellBroadcastConfigTracker.this.getHandler()), CellBroadcastConfigTracker.this.mSubChangedListener);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            CellBroadcastConfigTracker.this.mPhone.unregisterForRadioOffOrNotAvailable(CellBroadcastConfigTracker.this.getHandler());
            ((SubscriptionManager) CellBroadcastConfigTracker.this.mPhone.getContext().getSystemService(SubscriptionManager.class)).removeOnSubscriptionsChangedListener(CellBroadcastConfigTracker.this.mSubChangedListener);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker.logd("DefaultState message:" + message.what);
            }
            int i = message.what;
            if (i == 4) {
                CellBroadcastConfigTracker.this.resetConfig();
                return true;
            } else if (i != 5) {
                CellBroadcastConfigTracker.this.log("unexpected message!");
                return true;
            } else {
                int subId = CellBroadcastConfigTracker.this.mPhone.getSubId();
                CellBroadcastConfigTracker cellBroadcastConfigTracker2 = CellBroadcastConfigTracker.this;
                if (cellBroadcastConfigTracker2.mSubId != subId) {
                    cellBroadcastConfigTracker2.log("SubId changed from " + CellBroadcastConfigTracker.this.mSubId + " to " + subId);
                    CellBroadcastConfigTracker cellBroadcastConfigTracker3 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker3.mSubId = subId;
                    cellBroadcastConfigTracker3.resetConfig();
                    return true;
                }
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class IdleState extends State {
        private IdleState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker.logd("IdleState message:" + message.what);
            }
            if (message.what != 1) {
                return false;
            }
            Request request = (Request) message.obj;
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker2 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker2.logd("IdleState handle EVENT_REQUEST with request:" + request);
            }
            if (!CellBroadcastConfigTracker.this.mCbRanges3gpp.equals(request.get3gppRanges())) {
                CellBroadcastConfigTracker.this.setGsmConfig(request.get3gppRanges(), request);
                CellBroadcastConfigTracker cellBroadcastConfigTracker3 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker3.transitionTo(cellBroadcastConfigTracker3.mGsmConfiguringState);
                return true;
            } else if (CellBroadcastConfigTracker.this.mCbRanges3gpp2.equals(request.get3gpp2Ranges())) {
                CellBroadcastConfigTracker.this.logd("Do nothing as the requested ranges are same as now");
                request.getCallback().accept(0);
                return true;
            } else {
                CellBroadcastConfigTracker.this.setCdmaConfig(request.get3gpp2Ranges(), request);
                CellBroadcastConfigTracker cellBroadcastConfigTracker4 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker4.transitionTo(cellBroadcastConfigTracker4.mCdmaConfiguringState);
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GsmConfiguringState extends State {
        private GsmConfiguringState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker.logd("GsmConfiguringState message:" + message.what);
            }
            int i = message.what;
            if (i == 1) {
                CellBroadcastConfigTracker.this.deferMessage(message);
                return true;
            } else if (i != 2) {
                return false;
            } else {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Request request = (Request) asyncResult.userObj;
                if (CellBroadcastConfigTracker.DBG) {
                    CellBroadcastConfigTracker cellBroadcastConfigTracker2 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker2.logd("GsmConfiguringState handle EVENT_CONFIGURATION_DONE with request:" + request);
                }
                if (asyncResult.exception != null) {
                    CellBroadcastConfigTracker.this.logd("Failed to set gsm config");
                    request.getCallback().accept(2);
                    CellBroadcastConfigTracker cellBroadcastConfigTracker3 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker3.transitionTo(cellBroadcastConfigTracker3.mIdleState);
                    return true;
                }
                CellBroadcastConfigTracker.this.setActivation(1, !request.get3gppRanges().isEmpty(), request);
                CellBroadcastConfigTracker cellBroadcastConfigTracker4 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker4.transitionTo(cellBroadcastConfigTracker4.mGsmActivatingState);
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GsmActivatingState extends State {
        private GsmActivatingState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker.logd("GsmActivatingState message:" + message.what);
            }
            int i = message.what;
            if (i == 1) {
                CellBroadcastConfigTracker.this.deferMessage(message);
                return true;
            } else if (i != 3) {
                return false;
            } else {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Request request = (Request) asyncResult.userObj;
                if (CellBroadcastConfigTracker.DBG) {
                    CellBroadcastConfigTracker cellBroadcastConfigTracker2 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker2.logd("GsmActivatingState handle EVENT_ACTIVATION_DONE with request:" + request);
                }
                if (asyncResult.exception != null) {
                    CellBroadcastConfigTracker.this.logd("Failed to set gsm activation");
                    request.getCallback().accept(3);
                    CellBroadcastConfigTracker cellBroadcastConfigTracker3 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker3.transitionTo(cellBroadcastConfigTracker3.mIdleState);
                    return true;
                }
                CellBroadcastConfigTracker.this.mCbRanges3gpp = request.get3gppRanges();
                if (CellBroadcastConfigTracker.this.mCbRanges3gpp2.equals(request.get3gpp2Ranges())) {
                    CellBroadcastConfigTracker.this.logd("Done as no need to update ranges for 3gpp2");
                    request.getCallback().accept(0);
                    CellBroadcastConfigTracker cellBroadcastConfigTracker4 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker4.transitionTo(cellBroadcastConfigTracker4.mIdleState);
                    return true;
                }
                CellBroadcastConfigTracker.this.setCdmaConfig(request.get3gpp2Ranges(), request);
                CellBroadcastConfigTracker cellBroadcastConfigTracker5 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker5.transitionTo(cellBroadcastConfigTracker5.mCdmaConfiguringState);
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CdmaConfiguringState extends State {
        private CdmaConfiguringState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker.logd("CdmaConfiguringState message:" + message.what);
            }
            int i = message.what;
            if (i == 1) {
                CellBroadcastConfigTracker.this.deferMessage(message);
                return true;
            } else if (i != 2) {
                return false;
            } else {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Request request = (Request) asyncResult.userObj;
                if (CellBroadcastConfigTracker.DBG) {
                    CellBroadcastConfigTracker cellBroadcastConfigTracker2 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker2.logd("CdmaConfiguringState handle EVENT_ACTIVATION_DONE with request:" + request);
                }
                if (asyncResult.exception != null) {
                    CellBroadcastConfigTracker.this.logd("Failed to set cdma config");
                    request.getCallback().accept(2);
                    CellBroadcastConfigTracker cellBroadcastConfigTracker3 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker3.transitionTo(cellBroadcastConfigTracker3.mIdleState);
                    return true;
                }
                CellBroadcastConfigTracker.this.setActivation(2, !request.get3gpp2Ranges().isEmpty(), request);
                CellBroadcastConfigTracker cellBroadcastConfigTracker4 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker4.transitionTo(cellBroadcastConfigTracker4.mCdmaActivatingState);
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CdmaActivatingState extends State {
        private CdmaActivatingState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (CellBroadcastConfigTracker.DBG) {
                CellBroadcastConfigTracker cellBroadcastConfigTracker = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker.logd("CdmaActivatingState message:" + message.what);
            }
            int i = message.what;
            if (i == 1) {
                CellBroadcastConfigTracker.this.deferMessage(message);
                return true;
            } else if (i != 3) {
                return false;
            } else {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Request request = (Request) asyncResult.userObj;
                if (CellBroadcastConfigTracker.DBG) {
                    CellBroadcastConfigTracker cellBroadcastConfigTracker2 = CellBroadcastConfigTracker.this;
                    cellBroadcastConfigTracker2.logd("CdmaActivatingState handle EVENT_ACTIVATION_DONE with request:" + request);
                }
                if (asyncResult.exception != null) {
                    CellBroadcastConfigTracker.this.logd("Failed to set cdma activation");
                    request.getCallback().accept(3);
                } else {
                    CellBroadcastConfigTracker.this.mCbRanges3gpp2 = request.get3gpp2Ranges();
                    request.getCallback().accept(0);
                }
                CellBroadcastConfigTracker cellBroadcastConfigTracker3 = CellBroadcastConfigTracker.this;
                cellBroadcastConfigTracker3.transitionTo(cellBroadcastConfigTracker3.mIdleState);
                return true;
            }
        }
    }

    private CellBroadcastConfigTracker(Phone phone) {
        super("CellBroadcastConfigTracker-" + phone.getPhoneId());
        this.mCbRanges3gpp = new CopyOnWriteArrayList();
        this.mCbRanges3gpp2 = new CopyOnWriteArrayList();
        this.mSubChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.CellBroadcastConfigTracker.1
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                CellBroadcastConfigTracker.this.sendMessage(5);
            }
        };
        this.mDefaultState = new DefaultState();
        this.mIdleState = new IdleState();
        this.mGsmConfiguringState = new GsmConfiguringState();
        this.mGsmActivatingState = new GsmActivatingState();
        this.mCdmaConfiguringState = new CdmaConfiguringState();
        this.mCdmaActivatingState = new CdmaActivatingState();
        init(phone);
    }

    private CellBroadcastConfigTracker(Phone phone, Handler handler) {
        super("CellBroadcastConfigTracker-" + phone.getPhoneId(), handler);
        this.mCbRanges3gpp = new CopyOnWriteArrayList();
        this.mCbRanges3gpp2 = new CopyOnWriteArrayList();
        this.mSubChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.CellBroadcastConfigTracker.1
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                CellBroadcastConfigTracker.this.sendMessage(5);
            }
        };
        this.mDefaultState = new DefaultState();
        this.mIdleState = new IdleState();
        this.mGsmConfiguringState = new GsmConfiguringState();
        this.mGsmActivatingState = new GsmActivatingState();
        this.mCdmaConfiguringState = new CdmaConfiguringState();
        this.mCdmaActivatingState = new CdmaActivatingState();
        init(phone);
    }

    private void init(Phone phone) {
        logd("init");
        this.mPhone = phone;
        this.mSubId = phone.getSubId();
        addState(this.mDefaultState);
        addState(this.mIdleState, this.mDefaultState);
        addState(this.mGsmConfiguringState, this.mDefaultState);
        addState(this.mGsmActivatingState, this.mDefaultState);
        addState(this.mCdmaConfiguringState, this.mDefaultState);
        addState(this.mCdmaActivatingState, this.mDefaultState);
        setInitialState(this.mIdleState);
    }

    public static CellBroadcastConfigTracker make(Phone phone, Handler handler, boolean z) {
        CellBroadcastConfigTracker cellBroadcastConfigTracker;
        if (handler == null) {
            cellBroadcastConfigTracker = new CellBroadcastConfigTracker(phone);
        } else {
            cellBroadcastConfigTracker = new CellBroadcastConfigTracker(phone, handler);
        }
        if (z) {
            cellBroadcastConfigTracker.start();
        }
        return cellBroadcastConfigTracker;
    }

    public List<CellBroadcastIdRange> getCellBroadcastIdRanges() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.mCbRanges3gpp);
        arrayList.addAll(this.mCbRanges3gpp2);
        return arrayList;
    }

    public void setCellBroadcastIdRanges(List<CellBroadcastIdRange> list, Consumer<Integer> consumer) {
        if (DBG) {
            logd("setCellBroadcastIdRanges with ranges:" + list);
        }
        sendMessage(1, new Request(mergeRangesAsNeeded(list), consumer));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$mergeRangesAsNeeded$0(CellBroadcastIdRange cellBroadcastIdRange, CellBroadcastIdRange cellBroadcastIdRange2) {
        int startId;
        int startId2;
        if (cellBroadcastIdRange.getType() != cellBroadcastIdRange2.getType()) {
            startId = cellBroadcastIdRange.getType();
            startId2 = cellBroadcastIdRange2.getType();
        } else if (cellBroadcastIdRange.getStartId() == cellBroadcastIdRange2.getStartId()) {
            return cellBroadcastIdRange2.getEndId() - cellBroadcastIdRange.getEndId();
        } else {
            startId = cellBroadcastIdRange.getStartId();
            startId2 = cellBroadcastIdRange2.getStartId();
        }
        return startId - startId2;
    }

    @VisibleForTesting
    public static List<CellBroadcastIdRange> mergeRangesAsNeeded(List<CellBroadcastIdRange> list) throws IllegalArgumentException {
        list.sort(new Comparator() { // from class: com.android.internal.telephony.CellBroadcastConfigTracker$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$mergeRangesAsNeeded$0;
                lambda$mergeRangesAsNeeded$0 = CellBroadcastConfigTracker.lambda$mergeRangesAsNeeded$0((CellBroadcastIdRange) obj, (CellBroadcastIdRange) obj2);
                return lambda$mergeRangesAsNeeded$0;
            }
        });
        final ArrayList arrayList = new ArrayList();
        list.forEach(new Consumer() { // from class: com.android.internal.telephony.CellBroadcastConfigTracker$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CellBroadcastConfigTracker.lambda$mergeRangesAsNeeded$1(arrayList, (CellBroadcastIdRange) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$mergeRangesAsNeeded$1(List list, CellBroadcastIdRange cellBroadcastIdRange) {
        if (list.isEmpty() || ((CellBroadcastIdRange) list.get(list.size() - 1)).getType() != cellBroadcastIdRange.getType() || ((CellBroadcastIdRange) list.get(list.size() - 1)).getEndId() + 1 < cellBroadcastIdRange.getStartId() || (((CellBroadcastIdRange) list.get(list.size() - 1)).getEndId() + 1 == cellBroadcastIdRange.getStartId() && ((CellBroadcastIdRange) list.get(list.size() - 1)).isEnabled() != cellBroadcastIdRange.isEnabled())) {
            list.add(new CellBroadcastIdRange(cellBroadcastIdRange.getStartId(), cellBroadcastIdRange.getEndId(), cellBroadcastIdRange.getType(), cellBroadcastIdRange.isEnabled()));
        } else if (((CellBroadcastIdRange) list.get(list.size() - 1)).isEnabled() != cellBroadcastIdRange.isEnabled()) {
            throw new IllegalArgumentException("range conflict " + cellBroadcastIdRange);
        } else if (cellBroadcastIdRange.getEndId() > ((CellBroadcastIdRange) list.get(list.size() - 1)).getEndId()) {
            CellBroadcastIdRange cellBroadcastIdRange2 = (CellBroadcastIdRange) list.get(list.size() - 1);
            list.set(list.size() - 1, new CellBroadcastIdRange(cellBroadcastIdRange2.getStartId(), cellBroadcastIdRange.getEndId(), cellBroadcastIdRange2.getType(), cellBroadcastIdRange2.isEnabled()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetConfig() {
        this.mCbRanges3gpp.clear();
        this.mCbRanges3gpp2.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGsmConfig(List<CellBroadcastIdRange> list, Request request) {
        if (DBG) {
            logd("setGsmConfig with " + list);
        }
        int size = list.size();
        SmsBroadcastConfigInfo[] smsBroadcastConfigInfoArr = new SmsBroadcastConfigInfo[size];
        for (int i = 0; i < size; i++) {
            CellBroadcastIdRange cellBroadcastIdRange = list.get(i);
            smsBroadcastConfigInfoArr[i] = new SmsBroadcastConfigInfo(cellBroadcastIdRange.getStartId(), cellBroadcastIdRange.getEndId(), 0, 255, cellBroadcastIdRange.isEnabled());
        }
        this.mPhone.mCi.setGsmBroadcastConfig(smsBroadcastConfigInfoArr, obtainMessage(2, request));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCdmaConfig(List<CellBroadcastIdRange> list, Request request) {
        if (DBG) {
            logd("setCdmaConfig with " + list);
        }
        int size = list.size();
        CdmaSmsBroadcastConfigInfo[] cdmaSmsBroadcastConfigInfoArr = new CdmaSmsBroadcastConfigInfo[size];
        for (int i = 0; i < size; i++) {
            CellBroadcastIdRange cellBroadcastIdRange = list.get(i);
            cdmaSmsBroadcastConfigInfoArr[i] = new CdmaSmsBroadcastConfigInfo(cellBroadcastIdRange.getStartId(), cellBroadcastIdRange.getEndId(), 1, cellBroadcastIdRange.isEnabled());
        }
        this.mPhone.mCi.setCdmaBroadcastConfig(cdmaSmsBroadcastConfigInfoArr, obtainMessage(2, request));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setActivation(int i, boolean z, Request request) {
        if (DBG) {
            logd("setActivation(" + i + "." + z + ')');
        }
        Message obtainMessage = obtainMessage(3, request);
        if (i == 1) {
            this.mPhone.mCi.setGsmBroadcastActivation(z, obtainMessage);
        } else if (i == 2) {
            this.mPhone.mCi.setCdmaBroadcastActivation(z, obtainMessage);
        }
    }
}
