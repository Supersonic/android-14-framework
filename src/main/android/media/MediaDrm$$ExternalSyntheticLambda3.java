package android.media;

import android.media.MediaDrm;
import java.util.function.Consumer;
import java.util.function.Function;
/* compiled from: D8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class MediaDrm$$ExternalSyntheticLambda3 implements Function {
    public final /* synthetic */ MediaDrm f$0;

    public /* synthetic */ MediaDrm$$ExternalSyntheticLambda3(MediaDrm mediaDrm) {
        this.f$0 = mediaDrm;
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        Consumer createOnKeyStatusChangeListener;
        createOnKeyStatusChangeListener = this.f$0.createOnKeyStatusChangeListener((MediaDrm.OnKeyStatusChangeListener) obj);
        return createOnKeyStatusChangeListener;
    }
}
