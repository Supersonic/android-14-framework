package com.android.server.credentials.metrics;

import android.util.Log;
import java.util.AbstractMap;
import java.util.Map;
/* loaded from: classes.dex */
public enum EntryEnum {
    UNKNOWN(0),
    ACTION_ENTRY(1),
    CREDENTIAL_ENTRY(2),
    REMOTE_ENTRY(3),
    AUTHENTICATION_ENTRY(4);
    
    public static final Map<String, Integer> sKeyToEntryCode;
    private final int mInnerMetricCode;

    static {
        EntryEnum entryEnum;
        EntryEnum entryEnum2;
        EntryEnum entryEnum3;
        sKeyToEntryCode = Map.ofEntries(new AbstractMap.SimpleEntry("action_key", Integer.valueOf(r0.mInnerMetricCode)), new AbstractMap.SimpleEntry("authentication_action_key", Integer.valueOf(entryEnum3.mInnerMetricCode)), new AbstractMap.SimpleEntry("remote_entry_key", Integer.valueOf(entryEnum2.mInnerMetricCode)), new AbstractMap.SimpleEntry("credential_key", Integer.valueOf(entryEnum.mInnerMetricCode)));
    }

    EntryEnum(int i) {
        this.mInnerMetricCode = i;
    }

    public int getMetricCode() {
        return this.mInnerMetricCode;
    }

    public static int getMetricCodeFromString(String str) {
        Map<String, Integer> map = sKeyToEntryCode;
        if (!map.containsKey(str)) {
            Log.w("EntryEnum", "Attempted to use an unsupported string key entry type");
            return UNKNOWN.mInnerMetricCode;
        }
        return map.get(str).intValue();
    }
}
