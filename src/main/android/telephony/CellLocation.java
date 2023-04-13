package android.telephony;

import android.app.ActivityThread;
import android.content.Context;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.android.internal.telephony.ITelephony;
@Deprecated
/* loaded from: classes3.dex */
public abstract class CellLocation {
    public abstract void fillInNotifierBundle(Bundle bundle);

    public abstract boolean isEmpty();

    public abstract void setStateInvalid();

    @Deprecated
    public static void requestLocationUpdate() {
        Context appContext = ActivityThread.currentApplication();
        if (appContext == null) {
            return;
        }
        try {
            ITelephony phone = ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
            if (phone != null) {
                phone.updateServiceLocationWithPackageName(appContext.getOpPackageName());
            }
        } catch (RemoteException e) {
        }
    }

    public static CellLocation newFromBundle(Bundle bundle) {
        switch (TelephonyManager.getDefault().getCurrentPhoneType()) {
            case 1:
                return new GsmCellLocation(bundle);
            case 2:
                return new CdmaCellLocation(bundle);
            default:
                return null;
        }
    }

    public static CellLocation getEmpty() {
        switch (TelephonyManager.getDefault().getCurrentPhoneType()) {
            case 1:
                return new GsmCellLocation();
            case 2:
                return new CdmaCellLocation();
            default:
                return null;
        }
    }
}
