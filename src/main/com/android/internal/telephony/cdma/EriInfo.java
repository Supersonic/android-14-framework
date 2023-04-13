package com.android.internal.telephony.cdma;
/* loaded from: classes.dex */
public final class EriInfo {
    public static final int ROAMING_ICON_MODE_FLASH = 1;
    public static final int ROAMING_ICON_MODE_NORMAL = 0;
    public static final int ROAMING_INDICATOR_FLASH = 2;
    public static final int ROAMING_INDICATOR_OFF = 1;
    public static final int ROAMING_INDICATOR_ON = 0;
    public int alertId;
    public int callPromptId;
    public String eriText;
    public int iconIndex;
    public int iconMode;
    public int roamingIndicator;

    public EriInfo(int i, int i2, int i3, String str, int i4, int i5) {
        this.roamingIndicator = i;
        this.iconIndex = i2;
        this.iconMode = i3;
        this.eriText = str;
        this.callPromptId = i4;
        this.alertId = i5;
    }
}
