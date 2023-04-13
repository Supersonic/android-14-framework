package com.android.internal.telephony.domainselection;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.BarringInfo;
import android.telephony.DomainSelectionService;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.TransportSelectorCallback;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.util.TelephonyUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class DomainSelectionController {
    private static final boolean DBG = TelephonyUtils.IS_DEBUGGABLE;
    protected final int[] mConnectionCounts;
    private final ArrayList<DomainSelectionConnection> mConnections;
    protected final Context mContext;
    private final DomainSelectionService mDomainSelectionService;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final LocalLog mLocalLog;
    protected final Object mLock;

    /* loaded from: classes.dex */
    private final class DomainSelectionControllerHandler extends Handler {
        DomainSelectionControllerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                DomainSelectionController.this.updateServiceState((Phone) asyncResult.userObj, (ServiceState) asyncResult.result);
            } else if (i == 2) {
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                DomainSelectionController.this.updateBarringInfo((Phone) asyncResult2.userObj, (BarringInfo) asyncResult2.result);
            } else {
                DomainSelectionController domainSelectionController = DomainSelectionController.this;
                domainSelectionController.loge("unexpected event=" + message.what);
            }
        }
    }

    public DomainSelectionController(Context context, DomainSelectionService domainSelectionService) {
        this(context, domainSelectionService, null);
    }

    @VisibleForTesting
    public DomainSelectionController(Context context, DomainSelectionService domainSelectionService, Looper looper) {
        HandlerThread handlerThread = new HandlerThread("DomainSelectionControllerHandler");
        this.mHandlerThread = handlerThread;
        this.mLocalLog = new LocalLog(30);
        this.mLock = new Object();
        this.mConnections = new ArrayList<>();
        this.mContext = context;
        this.mDomainSelectionService = domainSelectionService;
        if (looper == null) {
            handlerThread.start();
            looper = handlerThread.getLooper();
        }
        this.mHandler = new DomainSelectionControllerHandler(looper);
        int activeModemCount = TelephonyManager.getDefault().getActiveModemCount();
        this.mConnectionCounts = new int[activeModemCount];
        for (int i = 0; i < activeModemCount; i++) {
            this.mConnectionCounts[i] = 0;
        }
    }

    public DomainSelectionConnection getDomainSelectionConnection(Phone phone, int i, boolean z) {
        DomainSelectionConnection domainSelectionConnection;
        if (i == 1) {
            if (z) {
                domainSelectionConnection = new EmergencyCallDomainSelectionConnection(phone, this);
            } else {
                domainSelectionConnection = new NormalCallDomainSelectionConnection(phone, this);
            }
        } else if (i != 2) {
            domainSelectionConnection = null;
        } else if (z) {
            domainSelectionConnection = new EmergencySmsDomainSelectionConnection(phone, this);
        } else {
            domainSelectionConnection = new SmsDomainSelectionConnection(phone, this);
        }
        addConnection(domainSelectionConnection);
        return domainSelectionConnection;
    }

    private void addConnection(DomainSelectionConnection domainSelectionConnection) {
        if (domainSelectionConnection == null) {
            return;
        }
        this.mConnections.add(domainSelectionConnection);
        registerForStateChange(domainSelectionConnection);
    }

    public void removeConnection(DomainSelectionConnection domainSelectionConnection) {
        if (domainSelectionConnection == null) {
            return;
        }
        this.mConnections.remove(domainSelectionConnection);
        unregisterForStateChange(domainSelectionConnection);
    }

    public void selectDomain(final DomainSelectionService.SelectionAttributes selectionAttributes, final TransportSelectorCallback transportSelectorCallback) {
        if (selectionAttributes == null || transportSelectorCallback == null) {
            return;
        }
        if (DBG) {
            logd("selectDomain");
        }
        this.mDomainSelectionService.getCachedExecutor().execute(new Runnable() { // from class: com.android.internal.telephony.domainselection.DomainSelectionController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DomainSelectionController.this.lambda$selectDomain$0(selectionAttributes, transportSelectorCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$selectDomain$0(DomainSelectionService.SelectionAttributes selectionAttributes, TransportSelectorCallback transportSelectorCallback) {
        this.mDomainSelectionService.onDomainSelection(selectionAttributes, transportSelectorCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateServiceState(final Phone phone, final ServiceState serviceState) {
        if (phone == null || serviceState == null) {
            return;
        }
        if (DBG) {
            logd("updateServiceState phoneId=" + phone.getPhoneId());
        }
        this.mDomainSelectionService.getCachedExecutor().execute(new Runnable() { // from class: com.android.internal.telephony.domainselection.DomainSelectionController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DomainSelectionController.this.lambda$updateServiceState$1(phone, serviceState);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateServiceState$1(Phone phone, ServiceState serviceState) {
        this.mDomainSelectionService.onServiceStateUpdated(phone.getPhoneId(), phone.getSubId(), serviceState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBarringInfo(final Phone phone, final BarringInfo barringInfo) {
        if (phone == null || barringInfo == null) {
            return;
        }
        if (DBG) {
            logd("updateBarringInfo phoneId=" + phone.getPhoneId());
        }
        this.mDomainSelectionService.getCachedExecutor().execute(new Runnable() { // from class: com.android.internal.telephony.domainselection.DomainSelectionController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DomainSelectionController.this.lambda$updateBarringInfo$2(phone, barringInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateBarringInfo$2(Phone phone, BarringInfo barringInfo) {
        this.mDomainSelectionService.onBarringInfoUpdated(phone.getPhoneId(), phone.getSubId(), barringInfo);
    }

    private void registerForStateChange(DomainSelectionConnection domainSelectionConnection) {
        Phone phone = domainSelectionConnection.getPhone();
        int i = this.mConnectionCounts[phone.getPhoneId()];
        if (i < 0) {
            i = 0;
        }
        this.mConnectionCounts[phone.getPhoneId()] = i + 1;
        if (i > 0) {
            return;
        }
        phone.registerForServiceStateChanged(this.mHandler, 1, phone);
        phone.mCi.registerForBarringInfoChanged(this.mHandler, 2, phone);
        updateServiceState(phone, phone.getServiceStateTracker().getServiceState());
        updateBarringInfo(phone, phone.mCi.getLastBarringInfo());
    }

    private void unregisterForStateChange(DomainSelectionConnection domainSelectionConnection) {
        Phone phone = domainSelectionConnection.getPhone();
        int i = this.mConnectionCounts[phone.getPhoneId()];
        if (i < 1) {
            i = 1;
        }
        this.mConnectionCounts[phone.getPhoneId()] = i - 1;
        if (i > 1) {
            return;
        }
        phone.unregisterForServiceStateChanged(this.mHandler);
        phone.mCi.unregisterForBarringInfoChanged(this.mHandler);
    }

    public Executor getDomainSelectionServiceExecutor() {
        return this.mDomainSelectionService.getCachedExecutor();
    }

    public void dump(PrintWriter printWriter) {
        this.mLocalLog.dump(printWriter);
    }

    private void logd(String str) {
        Log.d("DomainSelectionController", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Log.e("DomainSelectionController", str);
        this.mLocalLog.log(str);
    }
}
