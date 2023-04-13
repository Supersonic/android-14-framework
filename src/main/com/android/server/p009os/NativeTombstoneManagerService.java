package com.android.server.p009os;

import android.content.Context;
import com.android.server.LocalServices;
import com.android.server.SystemService;
/* renamed from: com.android.server.os.NativeTombstoneManagerService */
/* loaded from: classes2.dex */
public class NativeTombstoneManagerService extends SystemService {
    public NativeTombstoneManager mManager;

    public NativeTombstoneManagerService(Context context) {
        super(context);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        NativeTombstoneManager nativeTombstoneManager = new NativeTombstoneManager(getContext());
        this.mManager = nativeTombstoneManager;
        LocalServices.addService(NativeTombstoneManager.class, nativeTombstoneManager);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 550) {
            this.mManager.onSystemReady();
        }
    }
}
