package com.android.internal.widget;

import android.p008os.Bundle;
import android.p008os.IBinder;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes5.dex */
public final class InlinePresentationStyleUtils {
    public static boolean bundleEquals(Bundle bundle1, Bundle bundle2) {
        boolean equal;
        if (bundle1 == bundle2) {
            return true;
        }
        if (bundle1 == null || bundle2 == null || bundle1.size() != bundle2.size()) {
            return false;
        }
        Set<String> keys = bundle1.keySet();
        for (String key : keys) {
            Object value1 = bundle1.get(key);
            Object value2 = bundle2.get(key);
            if ((value1 instanceof Bundle) && (value2 instanceof Bundle)) {
                equal = bundleEquals((Bundle) value1, (Bundle) value2);
                continue;
            } else {
                equal = Objects.equals(value1, value2);
                continue;
            }
            if (!equal) {
                return false;
            }
        }
        return true;
    }

    public static void filterContentTypes(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        for (String key : bundle.keySet()) {
            Object o = bundle.get(key);
            if (o instanceof Bundle) {
                filterContentTypes((Bundle) o);
            } else if (o instanceof IBinder) {
                bundle.remove(key);
            }
        }
    }

    private InlinePresentationStyleUtils() {
    }
}
