package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class SoundTriggerFailure extends DetectorFailure {
    public static final Parcelable.Creator<SoundTriggerFailure> CREATOR = new Parcelable.Creator<SoundTriggerFailure>() { // from class: android.service.voice.SoundTriggerFailure.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SoundTriggerFailure[] newArray(int size) {
            return new SoundTriggerFailure[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SoundTriggerFailure createFromParcel(Parcel in) {
            return (SoundTriggerFailure) DetectorFailure.CREATOR.createFromParcel(in);
        }
    };
    public static final int ERROR_CODE_MODULE_DIED = 1;
    public static final int ERROR_CODE_RECOGNITION_RESUME_FAILED = 2;
    public static final int ERROR_CODE_UNEXPECTED_PREEMPTION = 3;
    public static final int ERROR_CODE_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SoundTriggerErrorCode {
    }

    public SoundTriggerFailure(int errorCode, String errorMessage) {
        super(1, errorCode, errorMessage);
    }

    @Override // android.service.voice.DetectorFailure
    public int getErrorCode() {
        return super.getErrorCode();
    }

    @Override // android.service.voice.DetectorFailure
    public int getSuggestedAction() {
        switch (getErrorCode()) {
            case 1:
            case 3:
                return 3;
            case 2:
                return 4;
            default:
                return 1;
        }
    }

    @Override // android.service.voice.DetectorFailure, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.service.voice.DetectorFailure, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
