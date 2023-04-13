package com.android.internal.telephony.satellite;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.telephony.Rlog;
import android.telephony.satellite.ISatelliteDatagramCallback;
import android.telephony.satellite.SatelliteDatagram;
import com.android.internal.telephony.Phone;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class DatagramController {
    public static final long MAX_DATAGRAM_ID = (long) Math.pow(2.0d, 16.0d);
    private static DatagramController sInstance;
    private final Context mContext;
    private final DatagramDispatcher mDatagramDispatcher;
    private final DatagramReceiver mDatagramReceiver;

    public static DatagramController getInstance() {
        if (sInstance == null) {
            loge("DatagramController was not yet initialized.");
        }
        return sInstance;
    }

    public static DatagramController make(Context context, Looper looper) {
        if (sInstance == null) {
            sInstance = new DatagramController(context, looper);
        }
        return sInstance;
    }

    private DatagramController(Context context, Looper looper) {
        this.mContext = context;
        this.mDatagramDispatcher = DatagramDispatcher.make(context, looper);
        this.mDatagramReceiver = DatagramReceiver.make(context, looper);
    }

    public int registerForSatelliteDatagram(int i, int i2, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
        return this.mDatagramReceiver.registerForSatelliteDatagram(i, i2, iSatelliteDatagramCallback);
    }

    public void unregisterForSatelliteDatagram(int i, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
        this.mDatagramReceiver.unregisterForSatelliteDatagram(i, iSatelliteDatagramCallback);
    }

    public void pollPendingSatelliteDatagrams(Message message, Phone phone) {
        this.mDatagramReceiver.pollPendingSatelliteDatagrams(message, phone);
    }

    public void sendSatelliteDatagram(int i, SatelliteDatagram satelliteDatagram, boolean z, boolean z2, Consumer<Integer> consumer) {
        this.mDatagramDispatcher.sendSatelliteDatagram(i, satelliteDatagram, z, z2, consumer);
    }

    private static void loge(String str) {
        Rlog.e("DatagramController", str);
    }
}
