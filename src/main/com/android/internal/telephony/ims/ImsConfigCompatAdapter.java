package com.android.internal.telephony.ims;

import android.os.RemoteException;
import android.telephony.ims.stub.ImsConfigImplBase;
import android.util.Log;
import com.android.ims.internal.IImsConfig;
/* loaded from: classes.dex */
public class ImsConfigCompatAdapter extends ImsConfigImplBase {
    public static final int FAILED = 1;
    public static final int SUCCESS = 0;
    public static final int UNKNOWN = -1;
    private final IImsConfig mOldConfigInterface;

    public ImsConfigCompatAdapter(IImsConfig iImsConfig) {
        this.mOldConfigInterface = iImsConfig;
    }

    public int setConfig(int i, int i2) {
        try {
            return this.mOldConfigInterface.setProvisionedValue(i, i2) == 0 ? 0 : 1;
        } catch (RemoteException e) {
            Log.w("ImsConfigCompatAdapter", "setConfig: item=" + i + " value=" + i2 + "failed: " + e.getMessage());
            return 1;
        }
    }

    public int setConfig(int i, String str) {
        try {
            return this.mOldConfigInterface.setProvisionedStringValue(i, str) == 0 ? 0 : 1;
        } catch (RemoteException e) {
            Log.w("ImsConfigCompatAdapter", "setConfig: item=" + i + " value=" + str + "failed: " + e.getMessage());
            return 1;
        }
    }

    public int getConfigInt(int i) {
        int provisionedValue;
        try {
            provisionedValue = this.mOldConfigInterface.getProvisionedValue(i);
        } catch (RemoteException e) {
            Log.w("ImsConfigCompatAdapter", "getConfigInt: item=" + i + "failed: " + e.getMessage());
        }
        if (provisionedValue != -1) {
            return provisionedValue;
        }
        return -1;
    }

    public String getConfigString(int i) {
        try {
            return this.mOldConfigInterface.getProvisionedStringValue(i);
        } catch (RemoteException e) {
            Log.w("ImsConfigCompatAdapter", "getConfigInt: item=" + i + "failed: " + e.getMessage());
            return null;
        }
    }
}
