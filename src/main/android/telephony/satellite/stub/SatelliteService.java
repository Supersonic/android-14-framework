package android.telephony.satellite.stub;

import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.app.Service;
import android.content.Intent;
import android.p008os.IBinder;
import com.android.telephony.Rlog;
/* loaded from: classes3.dex */
public class SatelliteService extends Service {
    public static final String SERVICE_INTERFACE = "android.telephony.satellite.SatelliteService";
    private static final String TAG = "SatelliteService";

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            Rlog.m10d(TAG, "SatelliteService bound");
            return new SatelliteImplBase(new PendingIntent$$ExternalSyntheticLambda1()).getBinder();
        }
        return null;
    }
}
