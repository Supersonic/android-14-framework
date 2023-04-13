package com.android.internal.telephony.data;

import android.net.KeepalivePacketData;
import android.net.NattKeepalivePacketData;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.TelephonyNetworkAgent;
import com.android.telephony.Rlog;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class KeepaliveTracker extends Handler {
    private final DataNetwork mDataNetwork;
    private final SparseArray<KeepaliveRecord> mKeepalives;
    private final String mLogTag;
    private final TelephonyNetworkAgent mNetworkAgent;
    private final Phone mPhone;

    private int keepaliveStatusErrorToPacketKeepaliveError(int i) {
        if (i != 0) {
            if (i != 1) {
                return i != 2 ? -31 : -32;
            }
            return -30;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class KeepaliveRecord {
        public int currentStatus;
        public int slotIndex;

        KeepaliveRecord(int i, int i2) {
            this.slotIndex = i;
            this.currentStatus = i2;
        }
    }

    public KeepaliveTracker(Phone phone, Looper looper, DataNetwork dataNetwork, TelephonyNetworkAgent telephonyNetworkAgent) {
        super(looper);
        this.mKeepalives = new SparseArray<>();
        this.mPhone = phone;
        this.mDataNetwork = dataNetwork;
        this.mNetworkAgent = telephonyNetworkAgent;
        this.mLogTag = "KT-" + telephonyNetworkAgent.getId();
        telephonyNetworkAgent.registerCallback(new TelephonyNetworkAgent.TelephonyNetworkAgentCallback(new Executor() { // from class: com.android.internal.telephony.data.KeepaliveTracker$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                KeepaliveTracker.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.KeepaliveTracker.1
            @Override // com.android.internal.telephony.data.TelephonyNetworkAgent.TelephonyNetworkAgentCallback
            public void onStartSocketKeepalive(int i, Duration duration, KeepalivePacketData keepalivePacketData) {
                KeepaliveTracker.this.onStartSocketKeepaliveRequested(i, duration, keepalivePacketData);
            }

            @Override // com.android.internal.telephony.data.TelephonyNetworkAgent.TelephonyNetworkAgentCallback
            public void onStopSocketKeepalive(int i) {
                KeepaliveTracker.this.onStopSocketKeepaliveRequested(i);
            }
        });
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        Object obj;
        int i = message.what;
        if (i == 1) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            int i2 = message.arg1;
            if (asyncResult.exception != null || (obj = asyncResult.result) == null) {
                loge("EVENT_KEEPALIVE_STARTED: error starting keepalive, e=" + asyncResult.exception);
                this.mNetworkAgent.sendSocketKeepaliveEvent(i2, -31);
                return;
            }
            onSocketKeepaliveStarted(i2, (KeepaliveStatus) obj);
        } else if (i == 2) {
            AsyncResult asyncResult2 = (AsyncResult) message.obj;
            int i3 = message.arg1;
            if (asyncResult2.exception != null) {
                loge("EVENT_KEEPALIVE_STOPPED: error stopping keepalive for handle=" + i3 + " e=" + asyncResult2.exception);
                onKeepaliveStatus(new KeepaliveStatus(3));
                return;
            }
            log("Keepalive Stop Requested for handle=" + i3);
            onKeepaliveStatus(new KeepaliveStatus(i3, 1));
        } else if (i != 3) {
            if (i == 4) {
                this.mPhone.mCi.registerForNattKeepaliveStatus(this, 3, null);
            } else if (i == 5) {
                this.mPhone.mCi.unregisterForNattKeepaliveStatus(this);
            } else {
                loge("Unexpected message " + message);
            }
        } else {
            AsyncResult asyncResult3 = (AsyncResult) message.obj;
            if (asyncResult3.exception != null) {
                loge("EVENT_KEEPALIVE_STATUS: error in keepalive, e=" + asyncResult3.exception);
                return;
            }
            Object obj2 = asyncResult3.result;
            if (obj2 != null) {
                onKeepaliveStatus((KeepaliveStatus) obj2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartSocketKeepaliveRequested(int i, Duration duration, KeepalivePacketData keepalivePacketData) {
        log("onStartSocketKeepaliveRequested: slot=" + i + ", interval=" + duration.getSeconds() + "s, packet=" + keepalivePacketData);
        if (keepalivePacketData instanceof NattKeepalivePacketData) {
            if (this.mDataNetwork.getTransport() == 1) {
                this.mPhone.mCi.startNattKeepalive(this.mDataNetwork.getId(), keepalivePacketData, (int) TimeUnit.SECONDS.toMillis(duration.getSeconds()), obtainMessage(1, i, 0, null));
                return;
            } else {
                this.mNetworkAgent.sendSocketKeepaliveEvent(i, -20);
                return;
            }
        }
        this.mNetworkAgent.sendSocketKeepaliveEvent(i, -30);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStopSocketKeepaliveRequested(int i) {
        log("onStopSocketKeepaliveRequested: slot=" + i);
        int handleForSlot = getHandleForSlot(i);
        if (handleForSlot < 0) {
            loge("No slot found for stopSocketKeepalive! " + i);
            this.mNetworkAgent.sendSocketKeepaliveEvent(i, -33);
            return;
        }
        log("Stopping keepalive with handle: " + handleForSlot);
        this.mPhone.mCi.stopNattKeepalive(handleForSlot, obtainMessage(2, handleForSlot, i, null));
    }

    private int getHandleForSlot(int i) {
        for (int i2 = 0; i2 < this.mKeepalives.size(); i2++) {
            if (this.mKeepalives.valueAt(i2).slotIndex == i) {
                return this.mKeepalives.keyAt(i2);
            }
        }
        return -1;
    }

    private void onSocketKeepaliveStarted(int i, KeepaliveStatus keepaliveStatus) {
        log("onSocketKeepaliveStarted: slot=" + i + ", keepaliveStatus=" + keepaliveStatus);
        int i2 = keepaliveStatus.statusCode;
        if (i2 == 0) {
            this.mNetworkAgent.sendSocketKeepaliveEvent(i, 0);
        } else if (i2 == 1) {
            this.mNetworkAgent.sendSocketKeepaliveEvent(i, keepaliveStatusErrorToPacketKeepaliveError(keepaliveStatus.errorCode));
            return;
        } else if (i2 != 2) {
            log("Invalid KeepaliveStatus Code: " + keepaliveStatus.statusCode);
            return;
        }
        log("Adding keepalive handle=" + keepaliveStatus.sessionHandle + " slotIndex = " + i);
        this.mKeepalives.put(keepaliveStatus.sessionHandle, new KeepaliveRecord(i, keepaliveStatus.statusCode));
    }

    private void onKeepaliveStatus(KeepaliveStatus keepaliveStatus) {
        log("onKeepaliveStatus: " + keepaliveStatus);
        KeepaliveRecord keepaliveRecord = this.mKeepalives.get(keepaliveStatus.sessionHandle);
        if (keepaliveRecord == null) {
            loge("Discarding keepalive event for different data connection:" + keepaliveStatus);
            return;
        }
        int i = keepaliveRecord.currentStatus;
        if (i == 0) {
            int i2 = keepaliveStatus.statusCode;
            if (i2 != 0) {
                if (i2 == 1) {
                    log("Keepalive received stopped status!");
                    this.mNetworkAgent.sendSocketKeepaliveEvent(keepaliveRecord.slotIndex, 0);
                    keepaliveRecord.currentStatus = 1;
                    this.mKeepalives.remove(keepaliveStatus.sessionHandle);
                    return;
                } else if (i2 != 2) {
                    loge("Invalid Keepalive Status received, " + keepaliveStatus.statusCode);
                    return;
                }
            }
            loge("Active Keepalive received invalid status!");
        } else if (i == 1) {
            log("Inactive Keepalive received status!");
            this.mNetworkAgent.sendSocketKeepaliveEvent(keepaliveRecord.slotIndex, -31);
        } else if (i == 2) {
            int i3 = keepaliveStatus.statusCode;
            if (i3 == 0) {
                log("Pending Keepalive received active status!");
                keepaliveRecord.currentStatus = 0;
                this.mNetworkAgent.sendSocketKeepaliveEvent(keepaliveRecord.slotIndex, 0);
            } else if (i3 == 1) {
                this.mNetworkAgent.sendSocketKeepaliveEvent(keepaliveRecord.slotIndex, keepaliveStatusErrorToPacketKeepaliveError(keepaliveStatus.errorCode));
                keepaliveRecord.currentStatus = 1;
                this.mKeepalives.remove(keepaliveStatus.sessionHandle);
            } else if (i3 == 2) {
                loge("Invalid unsolicited Keepalive Pending Status!");
            } else {
                loge("Invalid Keepalive Status received, " + keepaliveStatus.statusCode);
            }
        } else {
            loge("Invalid Keepalive Status received, " + keepaliveRecord.currentStatus);
        }
    }

    public void registerForKeepaliveStatus() {
        sendEmptyMessage(4);
    }

    public void unregisterForKeepaliveStatus() {
        sendEmptyMessage(5);
    }

    private void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }
}
