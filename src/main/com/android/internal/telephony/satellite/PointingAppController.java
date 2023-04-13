package com.android.internal.telephony.satellite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.Rlog;
import android.telephony.satellite.ISatellitePositionUpdateCallback;
import android.telephony.satellite.PointingInfo;
import android.telephony.satellite.SatelliteManager;
import android.text.TextUtils;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.satellite.PointingAppController;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class PointingAppController {
    private static PointingAppController sInstance;
    private final Context mContext;
    private final ConcurrentHashMap<Integer, SatellitePositionUpdateHandler> mSatellitePositionUpdateHandlers = new ConcurrentHashMap<>();
    private boolean mStartedSatellitePositionUpdates = false;

    public static PointingAppController getInstance() {
        if (sInstance == null) {
            loge("PointingAppController was not yet initialized.");
        }
        return sInstance;
    }

    public static PointingAppController make(Context context) {
        if (sInstance == null) {
            sInstance = new PointingAppController(context);
        }
        return sInstance;
    }

    private PointingAppController(Context context) {
        this.mContext = context;
    }

    public void setStartedSatellitePositionUpdates(boolean z) {
        this.mStartedSatellitePositionUpdates = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SatellitePositionUpdateHandler extends Handler {
        public static final int EVENT_DATAGRAM_TRANSFER_STATE_CHANGED = 2;
        public static final int EVENT_POSITION_INFO_CHANGED = 1;
        private final ConcurrentHashMap<IBinder, ISatellitePositionUpdateCallback> mListeners;

        SatellitePositionUpdateHandler(Looper looper) {
            super(looper);
            this.mListeners = new ConcurrentHashMap<>();
        }

        public void addListener(ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback) {
            this.mListeners.put(iSatellitePositionUpdateCallback.asBinder(), iSatellitePositionUpdateCallback);
        }

        public void removeListener(ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback) {
            this.mListeners.remove(iSatellitePositionUpdateCallback.asBinder());
        }

        public boolean hasListeners() {
            return !this.mListeners.isEmpty();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                final PointingInfo pointingInfo = (PointingInfo) ((AsyncResult) message.obj).result;
                this.mListeners.values().forEach(new Consumer() { // from class: com.android.internal.telephony.satellite.PointingAppController$SatellitePositionUpdateHandler$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        PointingAppController.SatellitePositionUpdateHandler.lambda$handleMessage$0(pointingInfo, (ISatellitePositionUpdateCallback) obj);
                    }
                });
            } else if (i == 2) {
                final int intValue = ((Integer) ((AsyncResult) message.obj).result).intValue();
                this.mListeners.values().forEach(new Consumer() { // from class: com.android.internal.telephony.satellite.PointingAppController$SatellitePositionUpdateHandler$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        PointingAppController.SatellitePositionUpdateHandler.lambda$handleMessage$1(intValue, (ISatellitePositionUpdateCallback) obj);
                    }
                });
            } else {
                PointingAppController.loge("SatellitePositionUpdateHandler unknown event: " + message.what);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$handleMessage$0(PointingInfo pointingInfo, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback) {
            try {
                iSatellitePositionUpdateCallback.onSatellitePositionChanged(pointingInfo);
            } catch (RemoteException e) {
                PointingAppController.logd("EVENT_POSITION_INFO_CHANGED RemoteException: " + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$handleMessage$1(int i, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback) {
            try {
                iSatellitePositionUpdateCallback.onDatagramTransferStateChanged(i, 0, 0, 0);
            } catch (RemoteException e) {
                PointingAppController.logd("EVENT_DATAGRAM_TRANSFER_STATE_CHANGED RemoteException: " + e);
            }
        }
    }

    public void registerForSatellitePositionUpdates(int i, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback, Phone phone) {
        SatellitePositionUpdateHandler satellitePositionUpdateHandler = this.mSatellitePositionUpdateHandlers.get(Integer.valueOf(i));
        if (satellitePositionUpdateHandler != null) {
            satellitePositionUpdateHandler.addListener(iSatellitePositionUpdateCallback);
            return;
        }
        SatellitePositionUpdateHandler satellitePositionUpdateHandler2 = new SatellitePositionUpdateHandler(Looper.getMainLooper());
        satellitePositionUpdateHandler2.addListener(iSatellitePositionUpdateCallback);
        this.mSatellitePositionUpdateHandlers.put(Integer.valueOf(i), satellitePositionUpdateHandler2);
        if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
            SatelliteModemInterface.getInstance().registerForSatellitePositionInfoChanged(satellitePositionUpdateHandler2, 1, null);
            SatelliteModemInterface.getInstance().registerForDatagramTransferStateChanged(satellitePositionUpdateHandler2, 2, null);
            return;
        }
        phone.registerForSatellitePositionInfoChanged(satellitePositionUpdateHandler2, 1, null);
    }

    public void unregisterForSatellitePositionUpdates(int i, Consumer<Integer> consumer, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback, Phone phone) {
        SatellitePositionUpdateHandler satellitePositionUpdateHandler = this.mSatellitePositionUpdateHandlers.get(Integer.valueOf(i));
        if (satellitePositionUpdateHandler != null) {
            satellitePositionUpdateHandler.removeListener(iSatellitePositionUpdateCallback);
            if (satellitePositionUpdateHandler.hasListeners()) {
                consumer.accept(0);
                return;
            }
            this.mSatellitePositionUpdateHandlers.remove(Integer.valueOf(i));
            if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
                SatelliteModemInterface.getInstance().unregisterForSatellitePositionInfoChanged(satellitePositionUpdateHandler);
                SatelliteModemInterface.getInstance().unregisterForDatagramTransferStateChanged(satellitePositionUpdateHandler);
            } else if (phone == null) {
                consumer.accept(6);
            } else {
                phone.unregisterForSatellitePositionInfoChanged(satellitePositionUpdateHandler);
            }
        }
    }

    public void startSatellitePositionUpdates(Message message, Phone phone) {
        if (this.mStartedSatellitePositionUpdates) {
            logd("startSatellitePositionUpdates: already started");
        } else if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
            SatelliteModemInterface.getInstance().startSendingSatellitePointingInfo(message);
            this.mStartedSatellitePositionUpdates = true;
        } else if (phone != null) {
            phone.startSatellitePositionUpdates(message);
            this.mStartedSatellitePositionUpdates = true;
        } else {
            loge("startSatellitePositionUpdates: No phone object");
            AsyncResult.forMessage(message, (Object) null, new SatelliteManager.SatelliteException(6));
            message.sendToTarget();
        }
    }

    public void stopSatellitePositionUpdates(Message message, Phone phone) {
        if (SatelliteModemInterface.getInstance().isSatelliteServiceSupported()) {
            SatelliteModemInterface.getInstance().stopSendingSatellitePointingInfo(message);
        } else if (phone != null) {
            phone.stopSatellitePositionUpdates(message);
        } else {
            loge("startSatellitePositionUpdates: No phone object");
            AsyncResult.forMessage(message, (Object) null, new SatelliteManager.SatelliteException(6));
            message.sendToTarget();
        }
    }

    public void startPointingUI(boolean z) {
        Intent launchIntentForPackage = this.mContext.getPackageManager().getLaunchIntentForPackage(TextUtils.emptyIfNull(this.mContext.getResources().getString(17039978)));
        launchIntentForPackage.putExtra("needFullScreen", z);
        this.mContext.startActivity(launchIntentForPackage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d("PointingAppController", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e("PointingAppController", str);
    }
}
