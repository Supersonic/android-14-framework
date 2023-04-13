package android.p008os;

import java.lang.Throwable;
/* renamed from: android.os.OutcomeReceiver */
/* loaded from: classes3.dex */
public interface OutcomeReceiver<R, E extends Throwable> {
    void onResult(R r);

    default void onError(E error) {
    }
}
