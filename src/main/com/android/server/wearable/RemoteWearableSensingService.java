package com.android.server.wearable;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteCallback;
import android.os.SharedMemory;
import android.service.wearable.IWearableSensingService;
import com.android.internal.infra.ServiceConnector;
/* loaded from: classes2.dex */
final class RemoteWearableSensingService extends ServiceConnector.Impl<IWearableSensingService> {
    public static final String TAG = RemoteWearableSensingService.class.getSimpleName();

    public long getAutoDisconnectTimeoutMs() {
        return -1L;
    }

    public RemoteWearableSensingService(Context context, ComponentName componentName, int i) {
        super(context, new Intent("android.service.wearable.WearableSensingService").setComponent(componentName), 67112960, i, new com.android.server.ambientcontext.RemoteWearableSensingService$$ExternalSyntheticLambda1());
        connect();
    }

    public void provideDataStream(final ParcelFileDescriptor parcelFileDescriptor, final RemoteCallback remoteCallback) {
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.wearable.RemoteWearableSensingService$$ExternalSyntheticLambda1
            public final void runNoResult(Object obj) {
                ((IWearableSensingService) obj).provideDataStream(parcelFileDescriptor, remoteCallback);
            }
        });
    }

    public void provideData(final PersistableBundle persistableBundle, final SharedMemory sharedMemory, final RemoteCallback remoteCallback) {
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.wearable.RemoteWearableSensingService$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                ((IWearableSensingService) obj).provideData(persistableBundle, sharedMemory, remoteCallback);
            }
        });
    }
}
