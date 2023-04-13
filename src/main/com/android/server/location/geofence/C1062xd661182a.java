package com.android.server.location.geofence;

import android.os.IBinder;
import com.android.server.servicewatcher.ServiceWatcher;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.location.geofence.GeofenceProxy$GeofenceProxyServiceConnection$$ExternalSyntheticLambda0 */
/* loaded from: classes.dex */
public final /* synthetic */ class C1062xd661182a implements ServiceWatcher.BinderOperation {
    public final /* synthetic */ GeofenceProxy f$0;

    @Override // com.android.server.servicewatcher.ServiceWatcher.BinderOperation
    public final void run(IBinder iBinder) {
        this.f$0.updateGeofenceHardware(iBinder);
    }
}
