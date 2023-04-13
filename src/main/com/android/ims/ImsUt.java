package com.android.ims;

import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.ims.ImsCallForwardInfo;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsSsData;
import android.telephony.ims.ImsSsInfo;
import com.android.ims.ImsUt;
import com.android.ims.internal.IImsUt;
import com.android.ims.internal.IImsUtListener;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class ImsUt implements ImsUtInterface {
    public static final String CATEGORY_CB = "CB";
    public static final String CATEGORY_CDIV = "CDIV";
    public static final String CATEGORY_CONF = "CONF";
    public static final String CATEGORY_CW = "CW";
    public static final String CATEGORY_OIP = "OIP";
    public static final String CATEGORY_OIR = "OIR";
    public static final String CATEGORY_TIP = "TIP";
    public static final String CATEGORY_TIR = "TIR";
    private static final boolean DBG = true;
    public static final String KEY_ACTION = "action";
    public static final String KEY_CATEGORY = "category";
    private static final int SERVICE_CLASS_NONE = 0;
    private static final int SERVICE_CLASS_VOICE = 1;
    private static final String TAG = "ImsUt";
    private Executor mExecutor;
    private Object mLockObj = new Object();
    private HashMap<Integer, Message> mPendingCmds = new HashMap<>();
    private Registrant mSsIndicationRegistrant;
    private final IImsUt miUt;

    public ImsUt(IImsUt iUt, Executor executor) {
        this.mExecutor = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
        this.miUt = iUt;
        if (executor != null) {
            this.mExecutor = executor;
        }
        if (iUt != null) {
            try {
                iUt.setListener(new IImsUtListenerProxy());
            } catch (RemoteException e) {
            }
        }
    }

    public void close() {
        synchronized (this.mLockObj) {
            IImsUt iImsUt = this.miUt;
            if (iImsUt != null) {
                try {
                    iImsUt.close();
                } catch (RemoteException e) {
                }
            }
            if (!this.mPendingCmds.isEmpty()) {
                Map.Entry<Integer, Message>[] entries = (Map.Entry[]) this.mPendingCmds.entrySet().toArray(new Map.Entry[this.mPendingCmds.size()]);
                for (Map.Entry<Integer, Message> entry : entries) {
                    sendFailureReport(entry.getValue(), new ImsReasonInfo(802, 0));
                }
                this.mPendingCmds.clear();
            }
        }
    }

    public void registerForSuppServiceIndication(Handler h, int what, Object obj) {
        this.mSsIndicationRegistrant = new Registrant(h, what, obj);
    }

    public void unregisterForSuppServiceIndication(Handler h) {
        this.mSsIndicationRegistrant.clear();
    }

    public void queryCallBarring(int cbType, Message result) {
        queryCallBarring(cbType, result, 0);
    }

    public void queryCallBarring(int cbType, Message result, int serviceClass) {
        int id;
        log("queryCallBarring :: Ut=" + this.miUt + ", cbType=" + cbType + ", serviceClass=" + serviceClass);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCallBarringForServiceClass(cbType, serviceClass);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void queryCallForward(int condition, String number, Message result) {
        int id;
        log("queryCallForward :: Ut=" + this.miUt + ", condition=" + condition + ", number=" + Rlog.pii(TAG, number));
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCallForward(condition, number);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void queryCallWaiting(Message result) {
        int id;
        log("queryCallWaiting :: Ut=" + this.miUt);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCallWaiting();
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void queryCLIR(Message result) {
        int id;
        log("queryCLIR :: Ut=" + this.miUt);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCLIR();
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void queryCLIP(Message result) {
        int id;
        log("queryCLIP :: Ut=" + this.miUt);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCLIP();
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void queryCOLR(Message result) {
        int id;
        log("queryCOLR :: Ut=" + this.miUt);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCOLR();
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void queryCOLP(Message result) {
        int id;
        log("queryCOLP :: Ut=" + this.miUt);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.queryCOLP();
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCallBarring(int cbType, int action, Message result, String[] barrList) {
        updateCallBarring(cbType, action, result, barrList, 0);
    }

    public void updateCallBarring(int cbType, int action, Message result, String[] barrList, int serviceClass) {
        updateCallBarring(cbType, action, result, barrList, serviceClass, "");
    }

    public void updateCallBarring(int cbType, int action, Message result, String[] barrList, int serviceClass, String password) {
        int id;
        if (barrList != null) {
            String bList = "";
            for (int i = 0; i < barrList.length; i++) {
                bList = bList + barrList[i] + " ";
            }
            log("updateCallBarring :: Ut=" + this.miUt + ", cbType=" + cbType + ", action=" + action + ", serviceClass=" + serviceClass + ", barrList=" + bList);
        } else {
            log("updateCallBarring :: Ut=" + this.miUt + ", cbType=" + cbType + ", action=" + action + ", serviceClass=" + serviceClass);
        }
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCallBarringWithPassword(cbType, action, barrList, serviceClass, password);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCallForward(int action, int condition, String number, int serviceClass, int timeSeconds, Message result) {
        int id;
        log("updateCallForward :: Ut=" + this.miUt + ", action=" + action + ", condition=" + condition + ", number=" + Rlog.pii(TAG, number) + ", serviceClass=" + serviceClass + ", timeSeconds=" + timeSeconds);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCallForward(action, condition, number, serviceClass, timeSeconds);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCallWaiting(boolean enable, int serviceClass, Message result) {
        int id;
        log("updateCallWaiting :: Ut=" + this.miUt + ", enable=" + enable + ",serviceClass=" + serviceClass);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCallWaiting(enable, serviceClass);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCLIR(int clirMode, Message result) {
        int id;
        log("updateCLIR :: Ut=" + this.miUt + ", clirMode=" + clirMode);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCLIR(clirMode);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCLIP(boolean enable, Message result) {
        int id;
        log("updateCLIP :: Ut=" + this.miUt + ", enable=" + enable);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCLIP(enable);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCOLR(int presentation, Message result) {
        int id;
        log("updateCOLR :: Ut=" + this.miUt + ", presentation=" + presentation);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCOLR(presentation);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public void updateCOLP(boolean enable, Message result) {
        int id;
        log("updateCallWaiting :: Ut=" + this.miUt + ", enable=" + enable);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.updateCOLP(enable);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    public boolean isBinderAlive() {
        return this.miUt.asBinder().isBinderAlive();
    }

    public void transact(Bundle ssInfo, Message result) {
        int id;
        log("transact :: Ut=" + this.miUt + ", ssInfo=" + ssInfo);
        synchronized (this.mLockObj) {
            try {
                id = this.miUt.transact(ssInfo);
            } catch (RemoteException e) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            }
            if (id < 0) {
                sendFailureReport(result, new ImsReasonInfo(802, 0));
            } else {
                this.mPendingCmds.put(Integer.valueOf(id), result);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFailureReport(Message result, ImsReasonInfo error) {
        String errorString;
        if (result == null || error == null) {
            return;
        }
        if (error.mExtraMessage == null) {
            errorString = Resources.getSystem().getString(17040808);
        } else {
            errorString = new String(error.mExtraMessage);
        }
        AsyncResult.forMessage(result, (Object) null, new ImsException(errorString, error.mCode));
        result.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSuccessReport(Message result) {
        if (result == null) {
            return;
        }
        AsyncResult.forMessage(result, (Object) null, (Throwable) null);
        result.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSuccessReport(Message result, Object ssInfo) {
        if (result == null) {
            return;
        }
        AsyncResult.forMessage(result, ssInfo, (Throwable) null);
        result.sendToTarget();
    }

    private void log(String s) {
        Rlog.d(TAG, s);
    }

    private void loge(String s) {
        Rlog.e(TAG, s);
    }

    private void loge(String s, Throwable t) {
        Rlog.e(TAG, s, t);
    }

    /* loaded from: classes.dex */
    public class IImsUtListenerProxy extends IImsUtListener.Stub {
        public IImsUtListenerProxy() {
        }

        public void utConfigurationUpdated(IImsUt ut, final int id) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$utConfigurationUpdated$0(id);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$utConfigurationUpdated$0(int id) {
            Integer key = Integer.valueOf(id);
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendSuccessReport((Message) imsUt.mPendingCmds.get(key));
                ImsUt.this.mPendingCmds.remove(key);
            }
        }

        public void utConfigurationUpdateFailed(IImsUt ut, final int id, final ImsReasonInfo error) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$utConfigurationUpdateFailed$1(id, error);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$utConfigurationUpdateFailed$1(int id, ImsReasonInfo error) {
            Integer key = Integer.valueOf(id);
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendFailureReport((Message) imsUt.mPendingCmds.get(key), error);
                ImsUt.this.mPendingCmds.remove(key);
            }
        }

        public void utConfigurationQueried(IImsUt ut, int id, Bundle ssInfo) {
            int[] clirResponse = ssInfo.getIntArray("queryClir");
            if (clirResponse != null && clirResponse.length == 2) {
                lineIdentificationSupplementaryServiceResponse(id, new ImsSsInfo.Builder(-1).setClirOutgoingState(clirResponse[0]).setClirInterrogationStatus(clirResponse[1]).build());
                return;
            }
            ImsSsInfo info = (ImsSsInfo) ssInfo.getParcelable("imsSsInfo");
            if (info != null) {
                lineIdentificationSupplementaryServiceResponse(id, info);
            } else {
                Rlog.w(ImsUt.TAG, "Invalid utConfigurationQueried response received for Bundle " + ssInfo);
            }
        }

        public void lineIdentificationSupplementaryServiceResponse(final int id, final ImsSsInfo config) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$lineIdentificationSupplementaryServiceResponse$2(id, config);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$lineIdentificationSupplementaryServiceResponse$2(int id, ImsSsInfo config) {
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendSuccessReport((Message) imsUt.mPendingCmds.get(Integer.valueOf(id)), config);
                ImsUt.this.mPendingCmds.remove(Integer.valueOf(id));
            }
        }

        public void utConfigurationQueryFailed(IImsUt ut, final int id, final ImsReasonInfo error) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$utConfigurationQueryFailed$3(id, error);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$utConfigurationQueryFailed$3(int id, ImsReasonInfo error) {
            Integer key = Integer.valueOf(id);
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendFailureReport((Message) imsUt.mPendingCmds.get(key), error);
                ImsUt.this.mPendingCmds.remove(key);
            }
        }

        public void utConfigurationCallBarringQueried(IImsUt ut, final int id, final ImsSsInfo[] cbInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$utConfigurationCallBarringQueried$4(id, cbInfo);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$utConfigurationCallBarringQueried$4(int id, ImsSsInfo[] cbInfo) {
            Integer key = Integer.valueOf(id);
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendSuccessReport((Message) imsUt.mPendingCmds.get(key), cbInfo);
                ImsUt.this.mPendingCmds.remove(key);
            }
        }

        public void utConfigurationCallForwardQueried(IImsUt ut, final int id, final ImsCallForwardInfo[] cfInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$utConfigurationCallForwardQueried$5(id, cfInfo);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$utConfigurationCallForwardQueried$5(int id, ImsCallForwardInfo[] cfInfo) {
            Integer key = Integer.valueOf(id);
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendSuccessReport((Message) imsUt.mPendingCmds.get(key), cfInfo);
                ImsUt.this.mPendingCmds.remove(key);
            }
        }

        public void utConfigurationCallWaitingQueried(IImsUt ut, final int id, final ImsSsInfo[] cwInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$utConfigurationCallWaitingQueried$6(id, cwInfo);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$utConfigurationCallWaitingQueried$6(int id, ImsSsInfo[] cwInfo) {
            Integer key = Integer.valueOf(id);
            synchronized (ImsUt.this.mLockObj) {
                ImsUt imsUt = ImsUt.this;
                imsUt.sendSuccessReport((Message) imsUt.mPendingCmds.get(key), cwInfo);
                ImsUt.this.mPendingCmds.remove(key);
            }
        }

        public void onSupplementaryServiceIndication(final ImsSsData ssData) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.ims.ImsUt$IImsUtListenerProxy$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUt.IImsUtListenerProxy.this.lambda$onSupplementaryServiceIndication$7(ssData);
                }
            }, ImsUt.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSupplementaryServiceIndication$7(ImsSsData ssData) {
            if (ImsUt.this.mSsIndicationRegistrant != null) {
                ImsUt.this.mSsIndicationRegistrant.notifyResult(ssData);
            }
        }
    }
}
