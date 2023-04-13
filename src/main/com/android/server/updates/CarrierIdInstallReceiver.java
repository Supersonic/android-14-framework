package com.android.server.updates;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
/* loaded from: classes2.dex */
public class CarrierIdInstallReceiver extends ConfigUpdateInstallReceiver {
    public CarrierIdInstallReceiver() {
        super("/data/misc/carrierid", "carrier_list.pb", "metadata/", "version");
    }

    @Override // com.android.server.updates.ConfigUpdateInstallReceiver
    public void postInstall(Context context, Intent intent) {
        context.getContentResolver().update(Uri.withAppendedPath(Telephony.CarrierId.All.CONTENT_URI, "update_db"), new ContentValues(), null, null);
    }
}
