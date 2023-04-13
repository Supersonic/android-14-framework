package android.service.carrier;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.p008os.IBinder;
import android.service.carrier.IApnSourceService;
import android.util.Log;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public abstract class ApnService extends Service {
    private static final String LOG_TAG = "ApnService";
    private final IApnSourceService.Stub mBinder = new IApnSourceService.Stub() { // from class: android.service.carrier.ApnService.1
        @Override // android.service.carrier.IApnSourceService
        public ContentValues[] getApns(int subId) {
            try {
                List<ContentValues> apns = ApnService.this.onRestoreApns(subId);
                return (ContentValues[]) apns.toArray(new ContentValues[apns.size()]);
            } catch (Exception e) {
                Log.m109e(ApnService.LOG_TAG, "Error in getApns for subId=" + subId + ": " + e.getMessage(), e);
                return null;
            }
        }
    };

    public abstract List<ContentValues> onRestoreApns(int i);

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}
