package android.hardware.fingerprint;

import android.annotation.NonNull;
import android.hardware.biometrics.AuthenticateOptions;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.net.module.util.NetworkStackConstants;
import java.lang.annotation.Annotation;
import java.util.Objects;
/* loaded from: classes.dex */
public final class FingerprintAuthenticateOptions implements AuthenticateOptions, Parcelable {
    public static final Parcelable.Creator<FingerprintAuthenticateOptions> CREATOR = new Parcelable.Creator<FingerprintAuthenticateOptions>() { // from class: android.hardware.fingerprint.FingerprintAuthenticateOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FingerprintAuthenticateOptions[] newArray(int size) {
            return new FingerprintAuthenticateOptions[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FingerprintAuthenticateOptions createFromParcel(Parcel in) {
            return new FingerprintAuthenticateOptions(in);
        }
    };
    private String mAttributionTag;
    private final int mDisplayState;
    private final boolean mIgnoreEnrollmentState;
    private String mOpPackageName;
    private int mSensorId;
    private final int mUserId;

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultUserId() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultSensorId() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean defaultIgnoreEnrollmentState() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultDisplayState() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String defaultOpPackageName() {
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String defaultAttributionTag() {
        return null;
    }

    FingerprintAuthenticateOptions(int userId, int sensorId, boolean ignoreEnrollmentState, int displayState, String opPackageName, String attributionTag) {
        this.mUserId = userId;
        this.mSensorId = sensorId;
        this.mIgnoreEnrollmentState = ignoreEnrollmentState;
        this.mDisplayState = displayState;
        AnnotationValidations.validate((Class<? extends Annotation>) AuthenticateOptions.DisplayState.class, (Annotation) null, displayState);
        this.mOpPackageName = opPackageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) opPackageName);
        this.mAttributionTag = attributionTag;
    }

    @Override // android.hardware.biometrics.AuthenticateOptions
    public int getUserId() {
        return this.mUserId;
    }

    @Override // android.hardware.biometrics.AuthenticateOptions
    public int getSensorId() {
        return this.mSensorId;
    }

    public boolean isIgnoreEnrollmentState() {
        return this.mIgnoreEnrollmentState;
    }

    @Override // android.hardware.biometrics.AuthenticateOptions
    public int getDisplayState() {
        return this.mDisplayState;
    }

    @Override // android.hardware.biometrics.AuthenticateOptions
    public String getOpPackageName() {
        return this.mOpPackageName;
    }

    @Override // android.hardware.biometrics.AuthenticateOptions
    public String getAttributionTag() {
        return this.mAttributionTag;
    }

    public FingerprintAuthenticateOptions setSensorId(int value) {
        this.mSensorId = value;
        return this;
    }

    public FingerprintAuthenticateOptions setOpPackageName(String value) {
        this.mOpPackageName = value;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) value);
        return this;
    }

    public FingerprintAuthenticateOptions setAttributionTag(String value) {
        this.mAttributionTag = value;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FingerprintAuthenticateOptions that = (FingerprintAuthenticateOptions) o;
        if (this.mUserId == that.mUserId && this.mSensorId == that.mSensorId && this.mIgnoreEnrollmentState == that.mIgnoreEnrollmentState && this.mDisplayState == that.mDisplayState && Objects.equals(this.mOpPackageName, that.mOpPackageName) && Objects.equals(this.mAttributionTag, that.mAttributionTag)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mUserId;
        return (((((((((_hash * 31) + this.mSensorId) * 31) + Boolean.hashCode(this.mIgnoreEnrollmentState)) * 31) + this.mDisplayState) * 31) + Objects.hashCode(this.mOpPackageName)) * 31) + Objects.hashCode(this.mAttributionTag);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mIgnoreEnrollmentState ? (byte) (0 | 4) : (byte) 0;
        if (this.mAttributionTag != null) {
            flg = (byte) (flg | NetworkStackConstants.TCPHDR_URG);
        }
        dest.writeByte(flg);
        dest.writeInt(this.mUserId);
        dest.writeInt(this.mSensorId);
        dest.writeInt(this.mDisplayState);
        dest.writeString(this.mOpPackageName);
        String str = this.mAttributionTag;
        if (str != null) {
            dest.writeString(str);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    FingerprintAuthenticateOptions(Parcel in) {
        byte flg = in.readByte();
        boolean ignoreEnrollmentState = (flg & 4) != 0;
        int userId = in.readInt();
        int sensorId = in.readInt();
        int displayState = in.readInt();
        String opPackageName = in.readString();
        String attributionTag = (flg & NetworkStackConstants.TCPHDR_URG) == 0 ? null : in.readString();
        this.mUserId = userId;
        this.mSensorId = sensorId;
        this.mIgnoreEnrollmentState = ignoreEnrollmentState;
        this.mDisplayState = displayState;
        AnnotationValidations.validate((Class<? extends Annotation>) AuthenticateOptions.DisplayState.class, (Annotation) null, displayState);
        this.mOpPackageName = opPackageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) opPackageName);
        this.mAttributionTag = attributionTag;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private String mAttributionTag;
        private long mBuilderFieldsSet = 0;
        private int mDisplayState;
        private boolean mIgnoreEnrollmentState;
        private String mOpPackageName;
        private int mSensorId;
        private int mUserId;

        public Builder setUserId(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mUserId = value;
            return this;
        }

        public Builder setSensorId(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mSensorId = value;
            return this;
        }

        public Builder setIgnoreEnrollmentState(boolean value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mIgnoreEnrollmentState = value;
            return this;
        }

        public Builder setDisplayState(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mDisplayState = value;
            return this;
        }

        public Builder setOpPackageName(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mOpPackageName = value;
            return this;
        }

        public Builder setAttributionTag(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mAttributionTag = value;
            return this;
        }

        public FingerprintAuthenticateOptions build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 64;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mUserId = FingerprintAuthenticateOptions.defaultUserId();
            }
            if ((this.mBuilderFieldsSet & 2) == 0) {
                this.mSensorId = FingerprintAuthenticateOptions.defaultSensorId();
            }
            if ((this.mBuilderFieldsSet & 4) == 0) {
                this.mIgnoreEnrollmentState = FingerprintAuthenticateOptions.defaultIgnoreEnrollmentState();
            }
            if ((this.mBuilderFieldsSet & 8) == 0) {
                this.mDisplayState = FingerprintAuthenticateOptions.defaultDisplayState();
            }
            if ((this.mBuilderFieldsSet & 16) == 0) {
                this.mOpPackageName = FingerprintAuthenticateOptions.defaultOpPackageName();
            }
            if ((this.mBuilderFieldsSet & 32) == 0) {
                this.mAttributionTag = FingerprintAuthenticateOptions.defaultAttributionTag();
            }
            FingerprintAuthenticateOptions o = new FingerprintAuthenticateOptions(this.mUserId, this.mSensorId, this.mIgnoreEnrollmentState, this.mDisplayState, this.mOpPackageName, this.mAttributionTag);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 64) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
