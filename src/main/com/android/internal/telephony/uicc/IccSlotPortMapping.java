package com.android.internal.telephony.uicc;
/* loaded from: classes.dex */
public class IccSlotPortMapping {
    public int mPhysicalSlotIndex = -1;
    public int mPortIndex = 0;

    public String toString() {
        return "{physicalSlotIndex=" + this.mPhysicalSlotIndex + ", portIndex=" + this.mPortIndex + "}";
    }
}
