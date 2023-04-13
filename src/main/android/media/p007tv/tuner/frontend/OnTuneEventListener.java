package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.OnTuneEventListener */
/* loaded from: classes2.dex */
public interface OnTuneEventListener {
    public static final int SIGNAL_LOCKED = 0;
    public static final int SIGNAL_LOST_LOCK = 2;
    public static final int SIGNAL_NO_SIGNAL = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.OnTuneEventListener$TuneEvent */
    /* loaded from: classes2.dex */
    public @interface TuneEvent {
    }

    void onTuneEvent(int i);
}
