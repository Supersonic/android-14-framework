package com.android.server.companion;

import android.os.Parcel;
import android.os.ResultReceiver;
/* loaded from: classes.dex */
public final class Utils {
    public static <T extends ResultReceiver> ResultReceiver prepareForIpc(T t) {
        Parcel obtain = Parcel.obtain();
        t.writeToParcel(obtain, 0);
        obtain.setDataPosition(0);
        ResultReceiver resultReceiver = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(obtain);
        obtain.recycle();
        return resultReceiver;
    }
}
