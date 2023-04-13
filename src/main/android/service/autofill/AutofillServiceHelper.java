package android.service.autofill;

import android.view.autofill.AutofillId;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
final class AutofillServiceHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static AutofillId[] assertValid(AutofillId[] ids) {
        Preconditions.checkArgument(ids != null && ids.length > 0, "must have at least one id");
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == null) {
                throw new IllegalArgumentException("ids[" + i + "] must not be null");
            }
        }
        return ids;
    }

    private AutofillServiceHelper() {
        throw new UnsupportedOperationException("contains static members only");
    }
}
