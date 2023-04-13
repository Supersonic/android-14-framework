package com.android.net.module.util;

import android.telecom.Logging.Session;
/* loaded from: classes5.dex */
public class NetworkIdentityUtils {
    public static String scrubSubscriberId(String subscriberId) {
        if (subscriberId != null) {
            return subscriberId.substring(0, Math.min(6, subscriberId.length())) + Session.TRUNCATE_STRING;
        }
        return "null";
    }

    public static String[] scrubSubscriberIds(String[] subscriberIds) {
        if (subscriberIds == null) {
            return null;
        }
        String[] res = new String[subscriberIds.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = scrubSubscriberId(subscriberIds[i]);
        }
        return res;
    }
}
