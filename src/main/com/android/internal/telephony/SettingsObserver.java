package com.android.internal.telephony;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.android.telephony.Rlog;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class SettingsObserver extends ContentObserver {
    private final Context mContext;
    private final Handler mHandler;
    private final Map<Uri, Integer> mUriEventMap;

    public SettingsObserver(Context context, Handler handler) {
        super(null);
        this.mUriEventMap = new HashMap();
        this.mContext = context;
        this.mHandler = handler;
    }

    public void observe(Uri uri, int i) {
        this.mUriEventMap.put(uri, Integer.valueOf(i));
        this.mContext.getContentResolver().registerContentObserver(uri, false, this);
    }

    public void unobserve() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        Rlog.e("SettingsObserver", "Should never be reached.");
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        Integer num = this.mUriEventMap.get(uri);
        if (num != null) {
            this.mHandler.obtainMessage(num.intValue()).sendToTarget();
            return;
        }
        Rlog.e("SettingsObserver", "No matching event to send for URI=" + uri);
    }
}
