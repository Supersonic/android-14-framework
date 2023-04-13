package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public enum ComprehensionTlvTag {
    COMMAND_DETAILS(1),
    DEVICE_IDENTITIES(2),
    RESULT(3),
    DURATION(4),
    ALPHA_ID(5),
    ADDRESS(6),
    USSD_STRING(10),
    SMS_TPDU(11),
    TEXT_STRING(13),
    TONE(14),
    ITEM(15),
    ITEM_ID(16),
    RESPONSE_LENGTH(17),
    FILE_LIST(18),
    HELP_REQUEST(21),
    DEFAULT_TEXT(23),
    EVENT_LIST(25),
    ICON_ID(30),
    ITEM_ICON_ID_LIST(31),
    IMMEDIATE_RESPONSE(43),
    LANGUAGE(45),
    URL(49),
    BROWSER_TERMINATION_CAUSE(52),
    TEXT_ATTRIBUTE(80);
    
    private int mValue;

    ComprehensionTlvTag(int i) {
        this.mValue = i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int value() {
        return this.mValue;
    }

    public static ComprehensionTlvTag fromInt(int i) {
        ComprehensionTlvTag[] values;
        for (ComprehensionTlvTag comprehensionTlvTag : values()) {
            if (comprehensionTlvTag.mValue == i) {
                return comprehensionTlvTag;
            }
        }
        return null;
    }
}
