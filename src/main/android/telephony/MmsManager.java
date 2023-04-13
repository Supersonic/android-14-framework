package android.telephony;

import android.app.ActivityThread;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import com.android.internal.telephony.IMms;
/* loaded from: classes3.dex */
public class MmsManager {
    private static final String TAG = "MmsManager";
    private final Context mContext;

    public MmsManager(Context context) {
        this.mContext = context;
    }

    public void sendMultimediaMessage(int subId, Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent, long messageId) {
        try {
            IMms iMms = IMms.Stub.asInterface(ServiceManager.getService("imms"));
            if (iMms == null) {
                return;
            }
            try {
                iMms.sendMessage(subId, ActivityThread.currentPackageName(), contentUri, locationUrl, configOverrides, sentIntent, messageId, this.mContext.getAttributionTag());
            } catch (RemoteException e) {
            }
        } catch (RemoteException e2) {
        }
    }

    public void downloadMultimediaMessage(int subId, String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent, long messageId) {
        try {
            IMms iMms = IMms.Stub.asInterface(ServiceManager.getService("imms"));
            if (iMms == null) {
                return;
            }
            try {
                iMms.downloadMessage(subId, ActivityThread.currentPackageName(), locationUrl, contentUri, configOverrides, downloadedIntent, messageId, this.mContext.getAttributionTag());
            } catch (RemoteException e) {
            }
        } catch (RemoteException e2) {
        }
    }
}
