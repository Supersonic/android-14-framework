package com.android.ims.rcs.uce.presence.subscribe;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.aidl.ISubscribeResponseCallback;
import android.util.Log;
import com.android.ims.RcsFeatureManager;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.List;
/* loaded from: classes.dex */
public class SubscribeControllerImpl implements SubscribeController {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "SubscribeController";
    private final Context mContext;
    private volatile boolean mIsDestroyedFlag;
    private volatile RcsFeatureManager mRcsFeatureManager;
    private final int mSubId;

    public SubscribeControllerImpl(Context context, int subId) {
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
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onCarrierConfigChanged() {
    }

    @Override // com.android.ims.rcs.uce.presence.subscribe.SubscribeController
    public void requestCapabilities(List<Uri> contactUris, ISubscribeResponseCallback c) throws RemoteException {
        if (this.mIsDestroyedFlag) {
            throw new RemoteException("Subscribe controller is destroyed");
        }
        RcsFeatureManager featureManager = this.mRcsFeatureManager;
        if (featureManager == null) {
            Log.w(LOG_TAG, "requestCapabilities: Service is unavailable");
            c.onCommandError(9);
            return;
        }
        featureManager.requestCapabilities(contactUris, c);
    }
}
