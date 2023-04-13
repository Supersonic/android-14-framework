package android.service.autofill;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;
/* loaded from: classes3.dex */
public interface SavedDatasetsInfoCallback {
    public static final int ERROR_NEEDS_USER_ACTION = 2;
    public static final int ERROR_OTHER = 0;
    public static final int ERROR_UNSUPPORTED = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Error {
    }

    void onError(int i);

    void onSuccess(Set<SavedDatasetsInfo> set);
}
