package com.android.internal.telephony.gsm;

import java.util.Objects;
/* loaded from: classes3.dex */
public final class SmsBroadcastConfigInfo {
    private int mFromCodeScheme;
    private int mFromServiceId;
    private boolean mSelected;
    private int mToCodeScheme;
    private int mToServiceId;

    public SmsBroadcastConfigInfo(int fromId, int toId, int fromScheme, int toScheme, boolean selected) {
        this.mFromServiceId = fromId;
        this.mToServiceId = toId;
        this.mFromCodeScheme = fromScheme;
        this.mToCodeScheme = toScheme;
        this.mSelected = selected;
    }

    public void setFromServiceId(int fromServiceId) {
        this.mFromServiceId = fromServiceId;
    }

    public int getFromServiceId() {
        return this.mFromServiceId;
    }

    public void setToServiceId(int toServiceId) {
        this.mToServiceId = toServiceId;
    }

    public int getToServiceId() {
        return this.mToServiceId;
    }

    public void setFromCodeScheme(int fromCodeScheme) {
        this.mFromCodeScheme = fromCodeScheme;
    }

    public int getFromCodeScheme() {
        return this.mFromCodeScheme;
    }

    public void setToCodeScheme(int toCodeScheme) {
        this.mToCodeScheme = toCodeScheme;
    }

    public int getToCodeScheme() {
        return this.mToCodeScheme;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public String toString() {
        return "SmsBroadcastConfigInfo: Id [" + this.mFromServiceId + ',' + this.mToServiceId + "] Code [" + this.mFromCodeScheme + ',' + this.mToCodeScheme + "] " + (this.mSelected ? "ENABLED" : "DISABLED");
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mFromServiceId), Integer.valueOf(this.mToServiceId), Integer.valueOf(this.mFromCodeScheme), Integer.valueOf(this.mToCodeScheme), Boolean.valueOf(this.mSelected));
    }

    public boolean equals(Object obj) {
        if (obj instanceof SmsBroadcastConfigInfo) {
            SmsBroadcastConfigInfo other = (SmsBroadcastConfigInfo) obj;
            return this.mFromServiceId == other.mFromServiceId && this.mToServiceId == other.mToServiceId && this.mFromCodeScheme == other.mFromCodeScheme && this.mToCodeScheme == other.mToCodeScheme && this.mSelected == other.mSelected;
        }
        return false;
    }
}
