package android.media;

import android.media.MediaDrm;
import java.util.function.Consumer;
import java.util.function.Function;
/* compiled from: D8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class MediaDrm$$ExternalSyntheticLambda4 implements Function {
    public final /* synthetic */ MediaDrm f$0;

    public /* synthetic */ MediaDrm$$ExternalSyntheticLambda4(MediaDrm mediaDrm) {
        this.f$0 = mediaDrm;
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        Consumer createOnSessionLostStateListener;
        createOnSessionLostStateListener = this.f$0.createOnSessionLostStateListener((MediaDrm.OnSessionLostStateListener) obj);
        return createOnSessionLostStateListener;
    }
}
