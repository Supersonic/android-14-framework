package android.hardware.display;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class WifiDisplayStatus implements Parcelable {
    public static final Parcelable.Creator<WifiDisplayStatus> CREATOR = new Parcelable.Creator<WifiDisplayStatus>() { // from class: android.hardware.display.WifiDisplayStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiDisplayStatus createFromParcel(Parcel in) {
            WifiDisplay activeDisplay;
            int featureState = in.readInt();
            int scanState = in.readInt();
            int activeDisplayState = in.readInt();
            if (in.readInt() == 0) {
                activeDisplay = null;
            } else {
                WifiDisplay activeDisplay2 = WifiDisplay.CREATOR.createFromParcel(in);
                activeDisplay = activeDisplay2;
            }
            WifiDisplay[] displays = WifiDisplay.CREATOR.newArray(in.readInt());
            for (int i = 0; i < displays.length; i++) {
                displays[i] = WifiDisplay.CREATOR.createFromParcel(in);
            }
            WifiDisplaySessionInfo sessionInfo = WifiDisplaySessionInfo.CREATOR.createFromParcel(in);
            return new WifiDisplayStatus(featureState, scanState, activeDisplayState, activeDisplay, displays, sessionInfo);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiDisplayStatus[] newArray(int size) {
            return new WifiDisplayStatus[size];
        }
    };
    public static final int DISPLAY_STATE_CONNECTED = 2;
    public static final int DISPLAY_STATE_CONNECTING = 1;
    public static final int DISPLAY_STATE_NOT_CONNECTED = 0;
    public static final int FEATURE_STATE_DISABLED = 1;
    public static final int FEATURE_STATE_OFF = 2;
    public static final int FEATURE_STATE_ON = 3;
    public static final int FEATURE_STATE_UNAVAILABLE = 0;
    public static final int SCAN_STATE_NOT_SCANNING = 0;
    public static final int SCAN_STATE_SCANNING = 1;
    private final WifiDisplay mActiveDisplay;
    private final int mActiveDisplayState;
    private final WifiDisplay[] mDisplays;
    private final int mFeatureState;
    private final int mScanState;
    private final WifiDisplaySessionInfo mSessionInfo;

    public WifiDisplayStatus() {
        this(0, 0, 0, null, WifiDisplay.EMPTY_ARRAY, null);
    }

    public WifiDisplayStatus(int featureState, int scanState, int activeDisplayState, WifiDisplay activeDisplay, WifiDisplay[] displays, WifiDisplaySessionInfo sessionInfo) {
        if (displays == null) {
            throw new IllegalArgumentException("displays must not be null");
        }
        this.mFeatureState = featureState;
        this.mScanState = scanState;
        this.mActiveDisplayState = activeDisplayState;
        this.mActiveDisplay = activeDisplay;
        this.mDisplays = displays;
        this.mSessionInfo = sessionInfo != null ? sessionInfo : new WifiDisplaySessionInfo();
    }

    public int getFeatureState() {
        return this.mFeatureState;
    }

    public int getScanState() {
        return this.mScanState;
    }

    public int getActiveDisplayState() {
        return this.mActiveDisplayState;
    }

    public WifiDisplay getActiveDisplay() {
        return this.mActiveDisplay;
    }

    public WifiDisplay[] getDisplays() {
        return this.mDisplays;
    }

    public WifiDisplaySessionInfo getSessionInfo() {
        return this.mSessionInfo;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        WifiDisplay[] wifiDisplayArr;
        dest.writeInt(this.mFeatureState);
        dest.writeInt(this.mScanState);
        dest.writeInt(this.mActiveDisplayState);
        if (this.mActiveDisplay != null) {
            dest.writeInt(1);
            this.mActiveDisplay.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.mDisplays.length);
        for (WifiDisplay display : this.mDisplays) {
            display.writeToParcel(dest, flags);
        }
        this.mSessionInfo.writeToParcel(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "WifiDisplayStatus{featureState=" + this.mFeatureState + ", scanState=" + this.mScanState + ", activeDisplayState=" + this.mActiveDisplayState + ", activeDisplay=" + this.mActiveDisplay + ", displays=" + Arrays.toString(this.mDisplays) + ", sessionInfo=" + this.mSessionInfo + "}";
    }
}
