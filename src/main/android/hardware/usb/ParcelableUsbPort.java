package android.hardware.usb;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class ParcelableUsbPort implements Parcelable {
    public static final Parcelable.Creator<ParcelableUsbPort> CREATOR = new Parcelable.Creator<ParcelableUsbPort>() { // from class: android.hardware.usb.ParcelableUsbPort.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableUsbPort createFromParcel(Parcel in) {
            String id = in.readString();
            int supportedModes = in.readInt();
            int supportedContaminantProtectionModes = in.readInt();
            boolean supportsEnableContaminantPresenceProtection = in.readBoolean();
            boolean supportsEnableContaminantPresenceDetection = in.readBoolean();
            boolean supportsComplianceWarnings = in.readBoolean();
            int supportedAltModesMask = in.readInt();
            return new ParcelableUsbPort(id, supportedModes, supportedContaminantProtectionModes, supportsEnableContaminantPresenceProtection, supportsEnableContaminantPresenceDetection, supportsComplianceWarnings, supportedAltModesMask);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableUsbPort[] newArray(int size) {
            return new ParcelableUsbPort[size];
        }
    };
    private final String mId;
    private final int mSupportedAltModesMask;
    private final int mSupportedContaminantProtectionModes;
    private final int mSupportedModes;
    private final boolean mSupportsComplianceWarnings;
    private final boolean mSupportsEnableContaminantPresenceDetection;
    private final boolean mSupportsEnableContaminantPresenceProtection;

    private ParcelableUsbPort(String id, int supportedModes, int supportedContaminantProtectionModes, boolean supportsEnableContaminantPresenceProtection, boolean supportsEnableContaminantPresenceDetection, boolean supportsComplianceWarnings, int supportedAltModesMask) {
        this.mId = id;
        this.mSupportedModes = supportedModes;
        this.mSupportedContaminantProtectionModes = supportedContaminantProtectionModes;
        this.mSupportsEnableContaminantPresenceProtection = supportsEnableContaminantPresenceProtection;
        this.mSupportsEnableContaminantPresenceDetection = supportsEnableContaminantPresenceDetection;
        this.mSupportsComplianceWarnings = supportsComplianceWarnings;
        this.mSupportedAltModesMask = supportedAltModesMask;
    }

    /* renamed from: of */
    public static ParcelableUsbPort m155of(UsbPort port) {
        return new ParcelableUsbPort(port.getId(), port.getSupportedModes(), port.getSupportedContaminantProtectionModes(), port.supportsEnableContaminantPresenceProtection(), port.supportsEnableContaminantPresenceDetection(), port.supportsComplianceWarnings(), port.getSupportedAltModesMask());
    }

    public UsbPort getUsbPort(UsbManager usbManager) {
        return new UsbPort(usbManager, this.mId, this.mSupportedModes, this.mSupportedContaminantProtectionModes, this.mSupportsEnableContaminantPresenceProtection, this.mSupportsEnableContaminantPresenceDetection, this.mSupportsComplianceWarnings, this.mSupportedAltModesMask);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeInt(this.mSupportedModes);
        dest.writeInt(this.mSupportedContaminantProtectionModes);
        dest.writeBoolean(this.mSupportsEnableContaminantPresenceProtection);
        dest.writeBoolean(this.mSupportsEnableContaminantPresenceDetection);
        dest.writeBoolean(this.mSupportsComplianceWarnings);
        dest.writeInt(this.mSupportedAltModesMask);
    }
}
