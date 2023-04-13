package com.android.server.p009os;

import android.content.Context;
import com.android.server.SystemService;
/* renamed from: com.android.server.os.BugreportManagerService */
/* loaded from: classes2.dex */
public class BugreportManagerService extends SystemService {
    public BugreportManagerServiceImpl mService;

    public BugreportManagerService(Context context) {
        super(context);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        BugreportManagerServiceImpl bugreportManagerServiceImpl = new BugreportManagerServiceImpl(getContext());
        this.mService = bugreportManagerServiceImpl;
        publishBinderService("bugreport", bugreportManagerServiceImpl);
    }
}
