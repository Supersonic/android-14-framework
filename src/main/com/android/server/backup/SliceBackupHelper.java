package com.android.server.backup;

import android.app.backup.BlobBackupHelper;
import android.app.slice.ISliceManager;
import android.content.Context;
import android.p008os.ServiceManager;
import android.util.Log;
import android.util.Slog;
/* loaded from: classes5.dex */
public class SliceBackupHelper extends BlobBackupHelper {
    static final int BLOB_VERSION = 1;
    static final String KEY_SLICES = "slices";
    static final String TAG = "SliceBackupHelper";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);

    public SliceBackupHelper(Context context) {
        super(1, KEY_SLICES);
    }

    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String key) {
        if (!KEY_SLICES.equals(key)) {
            return null;
        }
        try {
            ISliceManager sm = ISliceManager.Stub.asInterface(ServiceManager.getService("slice"));
            byte[] newPayload = sm.getBackupPayload(0);
            return newPayload;
        } catch (Exception e) {
            Slog.m95e(TAG, "Couldn't communicate with slice manager", e);
            return null;
        }
    }

    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String key, byte[] payload) {
        if (DEBUG) {
            Slog.m92v(TAG, "Got restore of " + key);
        }
        if (KEY_SLICES.equals(key)) {
            try {
                ISliceManager sm = ISliceManager.Stub.asInterface(ServiceManager.getService("slice"));
                sm.applyRestore(payload, 0);
            } catch (Exception e) {
                Slog.m95e(TAG, "Couldn't communicate with slice manager", e);
            }
        }
    }
}
