package com.android.internal.telephony.satellite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Telephony;
import android.telephony.Rlog;
import android.telephony.satellite.ISatelliteDatagramCallback;
import android.telephony.satellite.SatelliteDatagram;
import android.telephony.satellite.SatelliteManager;
import android.util.Pair;
import com.android.internal.telephony.ILongConsumer;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.satellite.DatagramReceiver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongUnaryOperator;
/* loaded from: classes.dex */
public class DatagramReceiver {
    private static AtomicLong mNextDatagramId = new AtomicLong(0);
    private static DatagramReceiver sInstance;
    private final Handler mBackgroundHandler;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final ConcurrentHashMap<Integer, SatelliteDatagramListenerHandler> mSatelliteDatagramListenerHandlers = new ConcurrentHashMap<>();
    private SharedPreferences mSharedPreferences;

    public static DatagramReceiver make(Context context, Looper looper) {
        if (sInstance == null) {
            sInstance = new DatagramReceiver(context, looper);
        }
        return sInstance;
    }

    private DatagramReceiver(Context context, Looper looper) {
        this.mSharedPreferences = null;
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        new HandlerThread("DatagramReceiver").start();
        this.mBackgroundHandler = new Handler(looper);
        try {
            this.mSharedPreferences = context.getSharedPreferences("satellite_shared_pref", 0);
        } catch (Exception e) {
            loge("Cannot get default shared preferences: " + e);
        }
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences == null || sharedPreferences.contains("satellite_datagram_id_key")) {
            return;
        }
        this.mSharedPreferences.edit().putLong("satellite_datagram_id_key", mNextDatagramId.get()).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SatelliteDatagramListenerHandler extends Handler {
        public static final int EVENT_RECEIVED_ACK = 3;
        public static final int EVENT_RETRY_DELIVERING_RECEIVED_DATAGRAM = 2;
        public static final int EVENT_SATELLITE_DATAGRAM_RECEIVED = 1;
        private final ConcurrentHashMap<IBinder, ISatelliteDatagramCallback> mListeners;
        private final int mSubId;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static final class DatagramRetryArgument {
            public SatelliteDatagram datagram;
            public long datagramId;
            public ISatelliteDatagramCallback listener;
            public int pendingCount;

            DatagramRetryArgument(long j, SatelliteDatagram satelliteDatagram, int i, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
                this.datagramId = j;
                this.datagram = satelliteDatagram;
                this.pendingCount = i;
                this.listener = iSatelliteDatagramCallback;
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || DatagramRetryArgument.class != obj.getClass()) {
                    return false;
                }
                DatagramRetryArgument datagramRetryArgument = (DatagramRetryArgument) obj;
                return this.datagramId == datagramRetryArgument.datagramId && this.datagram.equals(datagramRetryArgument.datagram) && this.pendingCount == datagramRetryArgument.pendingCount && this.listener.equals(datagramRetryArgument.listener);
            }
        }

        SatelliteDatagramListenerHandler(Looper looper, int i) {
            super(looper);
            this.mSubId = i;
            this.mListeners = new ConcurrentHashMap<>();
        }

        public void addListener(ISatelliteDatagramCallback iSatelliteDatagramCallback) {
            this.mListeners.put(iSatelliteDatagramCallback.asBinder(), iSatelliteDatagramCallback);
        }

        public void removeListener(ISatelliteDatagramCallback iSatelliteDatagramCallback) {
            this.mListeners.remove(iSatelliteDatagramCallback.asBinder());
        }

        public boolean hasListeners() {
            return !this.mListeners.isEmpty();
        }

        private int getTimeoutToReceiveAck() {
            return DatagramReceiver.sInstance.mContext.getResources().getInteger(17694973);
        }

        private long getDatagramId() {
            if (DatagramReceiver.sInstance.mSharedPreferences == null) {
                try {
                    DatagramReceiver.sInstance.mSharedPreferences = DatagramReceiver.sInstance.mContext.getSharedPreferences("satellite_shared_pref", 0);
                } catch (Exception e) {
                    DatagramReceiver.loge("Cannot get default shared preferences: " + e);
                }
            }
            if (DatagramReceiver.sInstance.mSharedPreferences != null) {
                long j = (DatagramReceiver.sInstance.mSharedPreferences.getLong("satellite_datagram_id_key", DatagramReceiver.mNextDatagramId.get()) + 1) % DatagramController.MAX_DATAGRAM_ID;
                DatagramReceiver.mNextDatagramId.set(j);
                DatagramReceiver.sInstance.mSharedPreferences.edit().putLong("satellite_datagram_id_key", j).commit();
                return j;
            }
            DatagramReceiver.loge("Shared preferences is null - returning default datagramId");
            return DatagramReceiver.mNextDatagramId.getAndUpdate(new LongUnaryOperator() { // from class: com.android.internal.telephony.satellite.DatagramReceiver$SatelliteDatagramListenerHandler$$ExternalSyntheticLambda1
                @Override // java.util.function.LongUnaryOperator
                public final long applyAsLong(long j2) {
                    long lambda$getDatagramId$0;
                    lambda$getDatagramId$0 = DatagramReceiver.SatelliteDatagramListenerHandler.lambda$getDatagramId$0(j2);
                    return lambda$getDatagramId$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ long lambda$getDatagramId$0(long j) {
            return (j + 1) % DatagramController.MAX_DATAGRAM_ID;
        }

        private void insertDatagram(long j, SatelliteDatagram satelliteDatagram) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("datagram_id", Long.valueOf(j));
            contentValues.put("datagram", satelliteDatagram.getSatelliteDatagram());
            if (DatagramReceiver.sInstance.mContentResolver.insert(Telephony.SatelliteDatagrams.CONTENT_URI, contentValues) == null) {
                DatagramReceiver.loge("Cannot insert datagram with datagramId: " + j);
                return;
            }
            DatagramReceiver.logd("Inserted datagram with datagramId: " + j);
        }

        private void deleteDatagram(long j) {
            String str = "datagram_id=" + j;
            Cursor query = DatagramReceiver.sInstance.mContentResolver.query(Telephony.SatelliteDatagrams.CONTENT_URI, null, str, null, null);
            if (query != null && query.getCount() == 1) {
                if (DatagramReceiver.sInstance.mContentResolver.delete(Telephony.SatelliteDatagrams.CONTENT_URI, str, null) == 0) {
                    DatagramReceiver.loge("Cannot delete datagram with datagramId: " + j);
                    return;
                }
                DatagramReceiver.logd("Deleted datagram with datagramId: " + j);
                return;
            }
            DatagramReceiver.loge("Datagram with datagramId: " + j + " is not present in DB.");
        }

        private void onSatelliteDatagramReceived(final DatagramRetryArgument datagramRetryArgument) {
            try {
                datagramRetryArgument.listener.onSatelliteDatagramReceived(datagramRetryArgument.datagramId, datagramRetryArgument.datagram, datagramRetryArgument.pendingCount, new ILongConsumer.Stub() { // from class: com.android.internal.telephony.satellite.DatagramReceiver.SatelliteDatagramListenerHandler.1
                    public void accept(long j) {
                        DatagramReceiver.logd("acknowledgeSatelliteDatagramReceived: datagramId=" + j);
                        SatelliteDatagramListenerHandler satelliteDatagramListenerHandler = SatelliteDatagramListenerHandler.this;
                        satelliteDatagramListenerHandler.sendMessage(satelliteDatagramListenerHandler.obtainMessage(3, datagramRetryArgument));
                    }
                });
            } catch (RemoteException e) {
                DatagramReceiver.logd("EVENT_SATELLITE_DATAGRAM_RECEIVED RemoteException: " + e);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                Pair pair = (Pair) ((AsyncResult) message.obj).result;
                final SatelliteDatagram satelliteDatagram = (SatelliteDatagram) pair.first;
                final int intValue = ((Integer) pair.second).intValue();
                final long datagramId = getDatagramId();
                insertDatagram(datagramId, satelliteDatagram);
                DatagramReceiver.logd("Received EVENT_SATELLITE_DATAGRAM_RECEIVED for subId=" + this.mSubId);
                this.mListeners.values().forEach(new Consumer() { // from class: com.android.internal.telephony.satellite.DatagramReceiver$SatelliteDatagramListenerHandler$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DatagramReceiver.SatelliteDatagramListenerHandler.this.lambda$handleMessage$1(datagramId, satelliteDatagram, intValue, (ISatelliteDatagramCallback) obj);
                    }
                });
            } else if (i == 2) {
                DatagramRetryArgument datagramRetryArgument = (DatagramRetryArgument) message.obj;
                DatagramReceiver.logd("Received EVENT_RETRY_DELIVERING_RECEIVED_DATAGRAM datagramId:" + datagramRetryArgument.datagramId);
                onSatelliteDatagramReceived(datagramRetryArgument);
            } else {
                if (i == 3) {
                    DatagramRetryArgument datagramRetryArgument2 = (DatagramRetryArgument) message.obj;
                    DatagramReceiver.logd("Received EVENT_RECEIVED_ACK datagramId:" + datagramRetryArgument2.datagramId);
                    removeMessages(2, datagramRetryArgument2);
                    deleteDatagram(datagramRetryArgument2.datagramId);
                }
                DatagramReceiver.loge("SatelliteDatagramListenerHandler unknown event: " + message.what);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleMessage$1(long j, SatelliteDatagram satelliteDatagram, int i, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
            DatagramRetryArgument datagramRetryArgument = new DatagramRetryArgument(j, satelliteDatagram, i, iSatelliteDatagramCallback);
            onSatelliteDatagramReceived(datagramRetryArgument);
            sendMessageDelayed(obtainMessage(2, datagramRetryArgument), getTimeoutToReceiveAck());
        }
    }

    public int registerForSatelliteDatagram(int i, int i2, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
        if (SatelliteController.getInstance().isSatelliteSupported()) {
            int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
            SatelliteDatagramListenerHandler satelliteDatagramListenerHandler = this.mSatelliteDatagramListenerHandlers.get(Integer.valueOf(validSatelliteSubId));
            if (satelliteDatagramListenerHandler == null) {
                satelliteDatagramListenerHandler = new SatelliteDatagramListenerHandler(this.mBackgroundHandler.getLooper(), validSatelliteSubId);
                if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
                    SatelliteModemInterface.getInstance().registerForSatelliteDatagramsReceived(satelliteDatagramListenerHandler, 1, null);
                } else {
                    SatelliteServiceUtils.getPhone().registerForSatelliteDatagramsReceived(satelliteDatagramListenerHandler, 1, null);
                }
            }
            satelliteDatagramListenerHandler.addListener(iSatelliteDatagramCallback);
            this.mSatelliteDatagramListenerHandlers.put(Integer.valueOf(validSatelliteSubId), satelliteDatagramListenerHandler);
            return 0;
        }
        return 20;
    }

    public void unregisterForSatelliteDatagram(int i, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        SatelliteDatagramListenerHandler satelliteDatagramListenerHandler = this.mSatelliteDatagramListenerHandlers.get(Integer.valueOf(validSatelliteSubId));
        if (satelliteDatagramListenerHandler != null) {
            satelliteDatagramListenerHandler.removeListener(iSatelliteDatagramCallback);
            if (satelliteDatagramListenerHandler.hasListeners()) {
                return;
            }
            this.mSatelliteDatagramListenerHandlers.remove(Integer.valueOf(validSatelliteSubId));
            if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
                SatelliteModemInterface.getInstance().unregisterForSatelliteDatagramsReceived(satelliteDatagramListenerHandler);
                return;
            }
            Phone phone = SatelliteServiceUtils.getPhone();
            if (phone != null) {
                phone.unregisterForSatelliteDatagramsReceived(satelliteDatagramListenerHandler);
            }
        }
    }

    public void pollPendingSatelliteDatagrams(Message message, Phone phone) {
        if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
            SatelliteModemInterface.getInstance().pollPendingSatelliteDatagrams(message);
        } else if (phone != null) {
            phone.pollPendingSatelliteDatagrams(message);
        } else {
            loge("pollPendingSatelliteDatagrams: No phone object");
            AsyncResult.forMessage(message, (Object) null, new SatelliteManager.SatelliteException(6));
            message.sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d("DatagramReceiver", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e("DatagramReceiver", str);
    }
}
