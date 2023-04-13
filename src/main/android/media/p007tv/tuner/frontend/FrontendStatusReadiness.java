package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.FrontendStatusReadiness */
/* loaded from: classes2.dex */
public final class FrontendStatusReadiness {
    public static final int FRONTEND_STATUS_READINESS_STABLE = 3;
    public static final int FRONTEND_STATUS_READINESS_UNAVAILABLE = 1;
    public static final int FRONTEND_STATUS_READINESS_UNDEFINED = 0;
    public static final int FRONTEND_STATUS_READINESS_UNSTABLE = 2;
    public static final int FRONTEND_STATUS_READINESS_UNSUPPORTED = 4;
    private int mFrontendStatusType;
    private int mStatusReadiness;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatusReadiness$Readiness */
    /* loaded from: classes2.dex */
    public @interface Readiness {
    }

    private FrontendStatusReadiness(int type, int readiness) {
        this.mFrontendStatusType = type;
        this.mStatusReadiness = readiness;
    }

    public int getStatusType() {
        return this.mFrontendStatusType;
    }

    public int getStatusReadiness() {
        return this.mStatusReadiness;
    }
}
