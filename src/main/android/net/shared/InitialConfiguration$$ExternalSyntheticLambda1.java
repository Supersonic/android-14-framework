package android.net.shared;

import java.net.InetAddress;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class InitialConfiguration$$ExternalSyntheticLambda1 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return IpConfigurationParcelableUtil.parcelAddress((InetAddress) obj);
    }
}
