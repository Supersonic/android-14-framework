package com.android.internal.telephony.satellite;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.Rlog;
import android.telephony.satellite.SatelliteDatagram;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.telephony.Phone;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongUnaryOperator;
/* loaded from: classes.dex */
public class DatagramDispatcher extends Handler {
    private static AtomicLong mNextDatagramId = new AtomicLong(0);
    private static DatagramDispatcher sInstance;
    private final Context mContext;
    private final Object mLock;
    @GuardedBy({"mLock"})
    private final LinkedHashMap<Long, SendSatelliteDatagramArgument> mPendingEmergencyDatagramsMap;
    @GuardedBy({"mLock"})
    private final LinkedHashMap<Long, SendSatelliteDatagramArgument> mPendingNonEmergencyDatagramsMap;
    @GuardedBy({"mLock"})
    private boolean mSendingDatagramInProgress;

    public static DatagramDispatcher make(Context context, Looper looper) {
        if (sInstance == null) {
            sInstance = new DatagramDispatcher(context, looper);
        }
        return sInstance;
    }

    private DatagramDispatcher(Context context, Looper looper) {
        super(looper);
        Object obj = new Object();
        this.mLock = obj;
        this.mPendingEmergencyDatagramsMap = new LinkedHashMap<>();
        this.mPendingNonEmergencyDatagramsMap = new LinkedHashMap<>();
        this.mContext = context;
        synchronized (obj) {
            this.mSendingDatagramInProgress = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class DatagramDispatcherHandlerRequest {
        public Object argument;
        public Phone phone;
        public Object result;

        DatagramDispatcherHandlerRequest(Object obj, Phone phone) {
            this.argument = obj;
            this.phone = phone;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SendSatelliteDatagramArgument {
        public Consumer<Integer> callback;
        public SatelliteDatagram datagram;
        public long datagramId;
        public int datagramType;
        public boolean isSatelliteDemoModeEnabled;
        public boolean needFullScreenPointingUI;

        SendSatelliteDatagramArgument(long j, int i, SatelliteDatagram satelliteDatagram, boolean z, boolean z2, Consumer<Integer> consumer) {
            this.datagramId = j;
            this.datagramType = i;
            this.datagram = satelliteDatagram;
            this.needFullScreenPointingUI = z;
            this.isSatelliteDemoModeEnabled = z2;
            this.callback = consumer;
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            DatagramDispatcherHandlerRequest datagramDispatcherHandlerRequest = (DatagramDispatcherHandlerRequest) message.obj;
            SendSatelliteDatagramArgument sendSatelliteDatagramArgument = (SendSatelliteDatagramArgument) datagramDispatcherHandlerRequest.argument;
            Message obtainMessage = obtainMessage(2, datagramDispatcherHandlerRequest);
            if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
                SatelliteModemInterface.getInstance().sendSatelliteDatagram(sendSatelliteDatagramArgument.datagram, sendSatelliteDatagramArgument.datagramType == 1, sendSatelliteDatagramArgument.needFullScreenPointingUI, sendSatelliteDatagramArgument.isSatelliteDemoModeEnabled, obtainMessage);
                return;
            }
            Phone phone = datagramDispatcherHandlerRequest.phone;
            if (phone != null) {
                phone.sendSatelliteDatagram(obtainMessage, sendSatelliteDatagramArgument.datagram, sendSatelliteDatagramArgument.needFullScreenPointingUI);
                return;
            }
            loge("sendSatelliteDatagram: No phone object");
            sendSatelliteDatagramArgument.callback.accept(6);
        } else if (i == 2) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            int satelliteError = SatelliteServiceUtils.getSatelliteError(asyncResult, "sendSatelliteDatagram");
            SendSatelliteDatagramArgument sendSatelliteDatagramArgument2 = (SendSatelliteDatagramArgument) ((DatagramDispatcherHandlerRequest) asyncResult.userObj).argument;
            synchronized (this.mLock) {
                this.mSendingDatagramInProgress = false;
            }
            sendSatelliteDatagramArgument2.callback.accept(Integer.valueOf(satelliteError));
            synchronized (this.mLock) {
                if (sendSatelliteDatagramArgument2.datagramType == 1) {
                    this.mPendingEmergencyDatagramsMap.remove(Long.valueOf(sendSatelliteDatagramArgument2.datagramId));
                } else {
                    this.mPendingNonEmergencyDatagramsMap.remove(Long.valueOf(sendSatelliteDatagramArgument2.datagramId));
                }
            }
            if (satelliteError == 0) {
                sendPendingDatagrams();
                return;
            }
            sendErrorCodeAndCleanupPendingDatagrams(this.mPendingEmergencyDatagramsMap, 15);
            sendErrorCodeAndCleanupPendingDatagrams(this.mPendingNonEmergencyDatagramsMap, 15);
        } else {
            logw("DatagramDispatcherHandler: unexpected message code: " + message.what);
        }
    }

    public void sendSatelliteDatagram(int i, SatelliteDatagram satelliteDatagram, boolean z, boolean z2, Consumer<Integer> consumer) {
        Phone phone = SatelliteServiceUtils.getPhone();
        long andUpdate = mNextDatagramId.getAndUpdate(new LongUnaryOperator() { // from class: com.android.internal.telephony.satellite.DatagramDispatcher$$ExternalSyntheticLambda0
            @Override // java.util.function.LongUnaryOperator
            public final long applyAsLong(long j) {
                long lambda$sendSatelliteDatagram$0;
                lambda$sendSatelliteDatagram$0 = DatagramDispatcher.lambda$sendSatelliteDatagram$0(j);
                return lambda$sendSatelliteDatagram$0;
            }
        });
        SendSatelliteDatagramArgument sendSatelliteDatagramArgument = new SendSatelliteDatagramArgument(andUpdate, i, satelliteDatagram, z, z2, consumer);
        synchronized (this.mLock) {
            if (i == 1) {
                this.mPendingEmergencyDatagramsMap.put(Long.valueOf(andUpdate), sendSatelliteDatagramArgument);
            } else {
                this.mPendingNonEmergencyDatagramsMap.put(Long.valueOf(andUpdate), sendSatelliteDatagramArgument);
            }
            if (!this.mSendingDatagramInProgress) {
                this.mSendingDatagramInProgress = true;
                sendRequestAsync(1, sendSatelliteDatagramArgument, phone);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ long lambda$sendSatelliteDatagram$0(long j) {
        return (j + 1) % DatagramController.MAX_DATAGRAM_ID;
    }

    private void sendPendingDatagrams() {
        Set<Map.Entry<Long, SendSatelliteDatagramArgument>> entrySet;
        Phone phone = SatelliteServiceUtils.getPhone();
        synchronized (this.mLock) {
            if (!this.mSendingDatagramInProgress && !this.mPendingEmergencyDatagramsMap.isEmpty()) {
                entrySet = this.mPendingEmergencyDatagramsMap.entrySet();
            } else {
                entrySet = (this.mSendingDatagramInProgress || this.mPendingNonEmergencyDatagramsMap.isEmpty()) ? null : this.mPendingNonEmergencyDatagramsMap.entrySet();
            }
            if (entrySet != null && entrySet.iterator().hasNext()) {
                this.mSendingDatagramInProgress = true;
                sendRequestAsync(1, entrySet.iterator().next().getValue(), phone);
            }
        }
    }

    private void sendErrorCodeAndCleanupPendingDatagrams(LinkedHashMap<Long, SendSatelliteDatagramArgument> linkedHashMap, int i) {
        synchronized (this.mLock) {
            for (Map.Entry<Long, SendSatelliteDatagramArgument> entry : linkedHashMap.entrySet()) {
                entry.getValue().callback.accept(Integer.valueOf(i));
            }
            linkedHashMap.clear();
        }
    }

    private void sendRequestAsync(int i, Object obj, Phone phone) {
        obtainMessage(i, new DatagramDispatcherHandlerRequest(obj, phone)).sendToTarget();
    }

    private static void loge(String str) {
        Rlog.e("DatagramDispatcher", str);
    }

    private static void logw(String str) {
        Rlog.w("DatagramDispatcher", str);
    }
}
