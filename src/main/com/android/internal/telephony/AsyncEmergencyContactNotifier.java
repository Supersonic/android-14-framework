package com.android.internal.telephony;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.BlockedNumberContract;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class AsyncEmergencyContactNotifier extends AsyncTask<Void, Void, Void> {
    private final Context mContext;

    public AsyncEmergencyContactNotifier(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Void doInBackground(Void... voidArr) {
        try {
            BlockedNumberContract.SystemContract.notifyEmergencyContact(this.mContext);
            return null;
        } catch (Exception e) {
            Rlog.e("AsyncEmergencyContactNotifier", "Exception notifying emergency contact: " + e);
            return null;
        }
    }
}
