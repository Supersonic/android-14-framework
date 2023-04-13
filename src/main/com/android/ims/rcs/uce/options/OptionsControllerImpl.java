package com.android.ims.rcs.uce.options;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.aidl.IOptionsResponseCallback;
import android.util.Log;
import com.android.ims.RcsFeatureManager;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.ArrayList;
import java.util.Set;
/* loaded from: classes.dex */
public class OptionsControllerImpl implements OptionsController {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "OptionsController";
    private final Context mContext;
    private volatile boolean mIsDestroyedFlag;
    private volatile RcsFeatureManager mRcsFeatureManager;
    private final int mSubId;

    public OptionsControllerImpl(Context context, int subId) {
        this.mSubId = subId;
        this.mContext = context;
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onRcsConnected(RcsFeatureManager manager) {
        this.mRcsFeatureManager = manager;
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onRcsDisconnected() {
        this.mRcsFeatureManager = null;
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onDestroy() {
        this.mIsDestroyedFlag = true;
        this.mRcsFeatureManager = null;
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onCarrierConfigChanged() {
    }

    @Override // com.android.ims.rcs.uce.options.OptionsController
    public void sendCapabilitiesRequest(Uri contactUri, Set<String> deviceFeatureTags, IOptionsResponseCallback c) throws RemoteException {
        if (this.mIsDestroyedFlag) {
            throw new RemoteException("OPTIONS controller is destroyed");
        }
        RcsFeatureManager featureManager = this.mRcsFeatureManager;
        if (featureManager == null) {
            Log.w(LOG_TAG, "sendCapabilitiesRequest: Service is unavailable");
            c.onCommandError(9);
            return;
        }
        featureManager.sendOptionsCapabilityRequest(contactUri, new ArrayList(deviceFeatureTags), c);
    }
}
