package android.media;

import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
/* loaded from: classes2.dex */
public class MediaRouter2Utils {
    static final String SEPARATOR = ":";
    static final String TAG = "MR2Utils";

    public static String toUniqueId(String providerId, String id) {
        Preconditions.checkArgument((TextUtils.isEmpty(providerId) || TextUtils.isEmpty(id)) ? false : true);
        return providerId + ":" + id;
    }

    public static String getProviderId(String uniqueId) {
        if (TextUtils.isEmpty(uniqueId)) {
            Log.m104w(TAG, "getProviderId: uniqueId shouldn't be empty");
            return null;
        }
        int firstIndexOfSeparator = uniqueId.indexOf(":");
        if (firstIndexOfSeparator == -1) {
            return null;
        }
        String providerId = uniqueId.substring(0, firstIndexOfSeparator);
        if (TextUtils.isEmpty(providerId)) {
            return null;
        }
        return providerId;
    }

    public static String getOriginalId(String uniqueId) {
        if (TextUtils.isEmpty(uniqueId)) {
            Log.m104w(TAG, "getOriginalId: uniqueId shouldn't be empty");
            return null;
        }
        int firstIndexOfSeparator = uniqueId.indexOf(":");
        if (firstIndexOfSeparator == -1 || firstIndexOfSeparator + 1 >= uniqueId.length()) {
            return null;
        }
        String providerId = uniqueId.substring(firstIndexOfSeparator + 1);
        if (TextUtils.isEmpty(providerId)) {
            return null;
        }
        return providerId;
    }
}
