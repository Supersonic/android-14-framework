package com.android.internal.telephony;

import android.util.StatsEvent;
import android.util.StatsLog;
/* loaded from: classes3.dex */
public final class TelephonyCommonStatsLog {
    public static final byte ANNOTATION_ID_DEFAULT_STATE = 6;
    public static final byte ANNOTATION_ID_EXCLUSIVE_STATE = 4;
    public static final byte ANNOTATION_ID_IS_UID = 1;
    public static final byte ANNOTATION_ID_PRIMARY_FIELD = 3;
    public static final byte ANNOTATION_ID_PRIMARY_FIELD_FIRST_UID = 5;
    public static final byte ANNOTATION_ID_STATE_NESTED = 8;
    public static final byte ANNOTATION_ID_TRIGGER_STATE_RESET = 7;
    public static final byte ANNOTATION_ID_TRUNCATE_TIMESTAMP = 2;
    public static final int DEVICE_IDENTIFIER_ACCESS_DENIED = 172;

    public static void write(int code, String arg1, String arg2, boolean arg3, boolean arg4) {
        StatsEvent.Builder builder = StatsEvent.newBuilder();
        builder.setAtomId(code);
        builder.writeString(arg1);
        builder.writeString(arg2);
        builder.writeBoolean(arg3);
        builder.writeBoolean(arg4);
        builder.usePooledBuffer();
        StatsLog.write(builder.build());
    }
}
