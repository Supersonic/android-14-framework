package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class HotwordDetectionServiceFailure extends DetectorFailure {
    public static final Parcelable.Creator<HotwordDetectionServiceFailure> CREATOR = new Parcelable.Creator<HotwordDetectionServiceFailure>() { // from class: android.service.voice.HotwordDetectionServiceFailure.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordDetectionServiceFailure[] newArray(int size) {
            return new HotwordDetectionServiceFailure[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordDetectionServiceFailure createFromParcel(Parcel in) {
            DetectorFailure detectorFailure = DetectorFailure.CREATOR.createFromParcel(in);
            return (HotwordDetectionServiceFailure) detectorFailure;
        }
    };
    public static final int ERROR_CODE_BINDING_DIED = 2;
    public static final int ERROR_CODE_BIND_FAILURE = 1;
    public static final int ERROR_CODE_COPY_AUDIO_DATA_FAILURE = 3;
    public static final int ERROR_CODE_DETECT_TIMEOUT = 4;
    public static final int ERROR_CODE_ON_DETECTED_SECURITY_EXCEPTION = 5;
    public static final int ERROR_CODE_ON_DETECTED_STREAM_COPY_FAILURE = 6;
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 7;
    public static final int ERROR_CODE_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface HotwordDetectionServiceErrorCode {
    }

    public HotwordDetectionServiceFailure(int errorCode, String errorMessage) {
        super(0, errorCode, errorMessage);
    }

    @Override // android.service.voice.DetectorFailure
    public int getErrorCode() {
        return super.getErrorCode();
    }

    @Override // android.service.voice.DetectorFailure
    public int getSuggestedAction() {
        switch (getErrorCode()) {
            case 1:
            case 2:
            case 7:
                return 3;
            case 3:
            default:
                return 1;
            case 4:
            case 5:
            case 6:
                return 4;
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
