package com.android.server.display.layout;

import android.util.Slog;
import android.view.DisplayAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class Layout {
    public final List<Display> mDisplays = new ArrayList(2);

    public String toString() {
        return this.mDisplays.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Layout) {
            return this.mDisplays.equals(((Layout) obj).mDisplays);
        }
        return false;
    }

    public int hashCode() {
        return this.mDisplays.hashCode();
    }

    public Display createDisplayLocked(DisplayAddress displayAddress, boolean z, boolean z2, String str, DisplayIdProducer displayIdProducer, String str2, int i) {
        return createDisplayLocked(displayAddress, z, z2, str, displayIdProducer, str2, -1, i);
    }

    public Display createDisplayLocked(DisplayAddress displayAddress, boolean z, boolean z2, String str, DisplayIdProducer displayIdProducer, String str2, int i, int i2) {
        if (contains(displayAddress)) {
            Slog.w("Layout", "Attempting to add second definition for display-device: " + displayAddress);
            return null;
        } else if (z && getById(0) != null) {
            Slog.w("Layout", "Ignoring attempt to add a second default display: " + displayAddress);
            return null;
        } else {
            String str3 = str == null ? "" : str;
            if (z && !str3.equals("")) {
                throw new IllegalArgumentException("Default display should own DEFAULT_DISPLAY_GROUP");
            }
            Display display = new Display(displayAddress, displayIdProducer.getId(z), z2, str3, str2, i, i2);
            this.mDisplays.add(display);
            return display;
        }
    }

    public void removeDisplayLocked(int i) {
        Display byId = getById(i);
        if (byId != null) {
            this.mDisplays.remove(byId);
        }
    }

    public boolean contains(DisplayAddress displayAddress) {
        int size = this.mDisplays.size();
        for (int i = 0; i < size; i++) {
            if (displayAddress.equals(this.mDisplays.get(i).getAddress())) {
                return true;
            }
        }
        return false;
    }

    public Display getById(int i) {
        for (int i2 = 0; i2 < this.mDisplays.size(); i2++) {
            Display display = this.mDisplays.get(i2);
            if (i == display.getLogicalDisplayId()) {
                return display;
            }
        }
        return null;
    }

    public Display getByAddress(DisplayAddress displayAddress) {
        for (int i = 0; i < this.mDisplays.size(); i++) {
            Display display = this.mDisplays.get(i);
            if (displayAddress.equals(display.getAddress())) {
                return display;
            }
        }
        return null;
    }

    public Display getAt(int i) {
        return this.mDisplays.get(i);
    }

    public int size() {
        return this.mDisplays.size();
    }

    /* loaded from: classes.dex */
    public static class Display {
        public final DisplayAddress mAddress;
        public final String mBrightnessThrottlingMapId;
        public final String mDisplayGroupName;
        public final boolean mIsEnabled;
        public int mLeadDisplayId;
        public final int mLogicalDisplayId;
        public int mPosition;
        public String mRefreshRateThermalThrottlingMapId;
        public String mRefreshRateZoneId;

        public Display(DisplayAddress displayAddress, int i, boolean z, String str, String str2, int i2, int i3) {
            this.mAddress = displayAddress;
            this.mLogicalDisplayId = i;
            this.mIsEnabled = z;
            this.mDisplayGroupName = str;
            this.mPosition = i2;
            this.mBrightnessThrottlingMapId = str2;
            if (i3 == i) {
                this.mLeadDisplayId = -1;
            } else {
                this.mLeadDisplayId = i3;
            }
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("{dispId: ");
            sb.append(this.mLogicalDisplayId);
            sb.append("(");
            sb.append(this.mIsEnabled ? "ON" : "OFF");
            sb.append("), displayGroupName: ");
            sb.append(this.mDisplayGroupName);
            sb.append(", addr: ");
            sb.append(this.mAddress);
            if (this.mPosition == -1) {
                str = "";
            } else {
                str = ", position: " + this.mPosition;
            }
            sb.append(str);
            sb.append(", brightnessThrottlingMapId: ");
            sb.append(this.mBrightnessThrottlingMapId);
            sb.append(", mRefreshRateZoneId: ");
            sb.append(this.mRefreshRateZoneId);
            sb.append(", mLeadDisplayId: ");
            sb.append(this.mLeadDisplayId);
            sb.append(", mRefreshRateThermalThrottlingMapId: ");
            sb.append(this.mRefreshRateThermalThrottlingMapId);
            sb.append("}");
            return sb.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof Display) {
                Display display = (Display) obj;
                return display.mIsEnabled == this.mIsEnabled && display.mPosition == this.mPosition && display.mLogicalDisplayId == this.mLogicalDisplayId && this.mDisplayGroupName.equals(display.mDisplayGroupName) && this.mAddress.equals(display.mAddress) && Objects.equals(this.mBrightnessThrottlingMapId, display.mBrightnessThrottlingMapId) && Objects.equals(display.mRefreshRateZoneId, this.mRefreshRateZoneId) && this.mLeadDisplayId == display.mLeadDisplayId && Objects.equals(this.mRefreshRateThermalThrottlingMapId, display.mRefreshRateThermalThrottlingMapId);
            }
            return false;
        }

        public int hashCode() {
            return ((((((((((((((((Boolean.hashCode(this.mIsEnabled) + 31) * 31) + this.mPosition) * 31) + this.mLogicalDisplayId) * 31) + this.mDisplayGroupName.hashCode()) * 31) + this.mAddress.hashCode()) * 31) + this.mBrightnessThrottlingMapId.hashCode()) * 31) + Objects.hashCode(this.mRefreshRateZoneId)) * 31) + this.mLeadDisplayId) * 31) + Objects.hashCode(this.mRefreshRateThermalThrottlingMapId);
        }

        public DisplayAddress getAddress() {
            return this.mAddress;
        }

        public int getLogicalDisplayId() {
            return this.mLogicalDisplayId;
        }

        public boolean isEnabled() {
            return this.mIsEnabled;
        }

        public String getDisplayGroupName() {
            return this.mDisplayGroupName;
        }

        public void setRefreshRateZoneId(String str) {
            this.mRefreshRateZoneId = str;
        }

        public String getRefreshRateZoneId() {
            return this.mRefreshRateZoneId;
        }

        public void setPosition(int i) {
            this.mPosition = i;
        }

        public String getBrightnessThrottlingMapId() {
            return this.mBrightnessThrottlingMapId;
        }

        public int getPosition() {
            return this.mPosition;
        }

        public int getLeadDisplayId() {
            return this.mLeadDisplayId;
        }

        public void setRefreshRateThermalThrottlingMapId(String str) {
            this.mRefreshRateThermalThrottlingMapId = str;
        }

        public String getRefreshRateThermalThrottlingMapId() {
            return this.mRefreshRateThermalThrottlingMapId;
        }
    }
}
