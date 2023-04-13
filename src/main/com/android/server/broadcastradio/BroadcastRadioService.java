package com.android.server.broadcastradio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.radio.IRadioService;
import com.android.server.SystemService;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class BroadcastRadioService extends SystemService {
    public final IRadioService mServiceImpl;

    public BroadcastRadioService(Context context) {
        super(context);
        ArrayList<String> servicesNames = IRadioServiceAidlImpl.getServicesNames();
        this.mServiceImpl = servicesNames.isEmpty() ? new IRadioServiceHidlImpl(this) : new IRadioServiceAidlImpl(this, servicesNames);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("broadcastradio", this.mServiceImpl.asBinder());
    }

    @SuppressLint({"AndroidFrameworkRequiresPermission"})
    public void enforcePolicyAccess() {
        if (getContext().checkCallingPermission("android.permission.ACCESS_BROADCAST_RADIO") != 0) {
            throw new SecurityException("ACCESS_BROADCAST_RADIO permission not granted");
        }
    }
}
