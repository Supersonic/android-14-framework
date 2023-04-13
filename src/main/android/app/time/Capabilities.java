package android.app.time;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public final class Capabilities {
    public static final int CAPABILITY_NOT_ALLOWED = 20;
    public static final int CAPABILITY_NOT_APPLICABLE = 30;
    public static final int CAPABILITY_NOT_SUPPORTED = 10;
    public static final int CAPABILITY_POSSESSED = 40;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CapabilityState {
    }

    private Capabilities() {
    }
}
