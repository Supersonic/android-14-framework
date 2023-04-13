package android.permission;

import com.android.internal.infra.AndroidFuture;
import java.util.List;
import java.util.function.Consumer;
/* compiled from: D8$$SyntheticClass */
/* loaded from: classes3.dex */
public final /* synthetic */ class PermissionControllerService$1$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ AndroidFuture f$0;

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.complete((List) obj);
    }
}
