package android.p008os;

import android.p008os.IHwBinder;
import libcore.util.NativeAllocationRegistry;
/* renamed from: android.os.HwRemoteBinder */
/* loaded from: classes3.dex */
public class HwRemoteBinder implements IHwBinder {
    private static final String TAG = "HwRemoteBinder";
    private static final NativeAllocationRegistry sNativeRegistry;
    private long mNativeContext;

    private static final native long native_init();

    private final native void native_setup_empty();

    public final native boolean equals(Object obj);

    public final native int hashCode();

    @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
    public native boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j);

    @Override // android.p008os.IHwBinder
    public final native void transact(int i, HwParcel hwParcel, HwParcel hwParcel2, int i2) throws RemoteException;

    @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
    public native boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient);

    public HwRemoteBinder() {
        native_setup_empty();
        sNativeRegistry.registerNativeAllocation(this, this.mNativeContext);
    }

    @Override // android.p008os.IHwBinder
    public IHwInterface queryLocalInterface(String descriptor) {
        return null;
    }

    static {
        long freeFunction = native_init();
        sNativeRegistry = new NativeAllocationRegistry(HwRemoteBinder.class.getClassLoader(), freeFunction, 128L);
    }

    private static final void sendDeathNotice(IHwBinder.DeathRecipient recipient, long cookie) {
        recipient.serviceDied(cookie);
    }
}
