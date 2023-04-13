package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.telephony.uicc.IccUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
@SystemApi
/* loaded from: classes3.dex */
public final class SmsCbEtwsInfo implements Parcelable {
    public static final Parcelable.Creator<SmsCbEtwsInfo> CREATOR = new Parcelable.Creator<SmsCbEtwsInfo>() { // from class: android.telephony.SmsCbEtwsInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmsCbEtwsInfo createFromParcel(Parcel in) {
            return new SmsCbEtwsInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmsCbEtwsInfo[] newArray(int size) {
            return new SmsCbEtwsInfo[size];
        }
    };
    public static final int ETWS_WARNING_TYPE_EARTHQUAKE = 0;
    public static final int ETWS_WARNING_TYPE_EARTHQUAKE_AND_TSUNAMI = 2;
    public static final int ETWS_WARNING_TYPE_OTHER_EMERGENCY = 4;
    public static final int ETWS_WARNING_TYPE_TEST_MESSAGE = 3;
    public static final int ETWS_WARNING_TYPE_TSUNAMI = 1;
    public static final int ETWS_WARNING_TYPE_UNKNOWN = -1;
    private final boolean mIsEmergencyUserAlert;
    private final boolean mIsPopupAlert;
    private final boolean mIsPrimary;
    private final byte[] mWarningSecurityInformation;
    private final int mWarningType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface WarningType {
    }

    public SmsCbEtwsInfo(int warningType, boolean isEmergencyUserAlert, boolean isPopupAlert, boolean isPrimary, byte[] warningSecurityInformation) {
        this.mWarningType = warningType;
        this.mIsEmergencyUserAlert = isEmergencyUserAlert;
        this.mIsPopupAlert = isPopupAlert;
        this.mIsPrimary = isPrimary;
        this.mWarningSecurityInformation = warningSecurityInformation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SmsCbEtwsInfo(Parcel in) {
        this.mWarningType = in.readInt();
        this.mIsEmergencyUserAlert = in.readInt() != 0;
        this.mIsPopupAlert = in.readInt() != 0;
        this.mIsPrimary = in.readInt() != 0;
        this.mWarningSecurityInformation = in.createByteArray();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mWarningType);
        dest.writeInt(this.mIsEmergencyUserAlert ? 1 : 0);
        dest.writeInt(this.mIsPopupAlert ? 1 : 0);
        dest.writeInt(this.mIsPrimary ? 1 : 0);
        dest.writeByteArray(this.mWarningSecurityInformation);
    }

    public int getWarningType() {
        return this.mWarningType;
    }

    public boolean isEmergencyUserAlert() {
        return this.mIsEmergencyUserAlert;
    }

    public boolean isPopupAlert() {
        return this.mIsPopupAlert;
    }

    public boolean isPrimary() {
        return this.mIsPrimary;
    }

    public long getPrimaryNotificationTimestamp() {
        byte[] bArr = this.mWarningSecurityInformation;
        if (bArr == null || bArr.length < 7) {
            return 0L;
        }
        int year = IccUtils.gsmBcdByteToInt(bArr[0]);
        int month = IccUtils.gsmBcdByteToInt(this.mWarningSecurityInformation[1]);
        int day = IccUtils.gsmBcdByteToInt(this.mWarningSecurityInformation[2]);
        int hour = IccUtils.gsmBcdByteToInt(this.mWarningSecurityInformation[3]);
        int minute = IccUtils.gsmBcdByteToInt(this.mWarningSecurityInformation[4]);
        int second = IccUtils.gsmBcdByteToInt(this.mWarningSecurityInformation[5]);
        byte tzByte = this.mWarningSecurityInformation[6];
        int timezoneOffset = IccUtils.gsmBcdByteToInt((byte) (tzByte & (-9)));
        int timeZoneOffsetSeconds = ((tzByte & 8) == 0 ? timezoneOffset : -timezoneOffset) * 15 * 60;
        try {
            LocalDateTime localDateTime = LocalDateTime.of(year + 2000, month, day, hour, minute, second);
            long epochSeconds = localDateTime.toEpochSecond(ZoneOffset.UTC) - timeZoneOffsetSeconds;
            return 1000 * epochSeconds;
        } catch (DateTimeException e) {
            return 0L;
        }
    }

    public byte[] getPrimaryNotificationSignature() {
        byte[] bArr = this.mWarningSecurityInformation;
        if (bArr == null || bArr.length < 50) {
            return null;
        }
        return Arrays.copyOfRange(bArr, 7, 50);
    }

    public String toString() {
        return "SmsCbEtwsInfo{warningType=" + this.mWarningType + ", emergencyUserAlert=" + this.mIsEmergencyUserAlert + ", activatePopup=" + this.mIsPopupAlert + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
