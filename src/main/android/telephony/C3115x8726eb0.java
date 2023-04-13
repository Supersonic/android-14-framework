package android.telephony;

import android.telephony.TelephonyManager;
import java.lang.ref.WeakReference;
import java.util.function.Supplier;
/* compiled from: D8$$SyntheticClass */
/* renamed from: android.telephony.TelephonyRegistryManager$CarrierPrivilegesCallbackWrapper$$ExternalSyntheticLambda1 */
/* loaded from: classes3.dex */
public final /* synthetic */ class C3115x8726eb0 implements Supplier {
    public final /* synthetic */ WeakReference f$0;

    public /* synthetic */ C3115x8726eb0(WeakReference weakReference) {
        this.f$0 = weakReference;
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return (TelephonyManager.CarrierPrivilegesCallback) this.f$0.get();
    }
}
