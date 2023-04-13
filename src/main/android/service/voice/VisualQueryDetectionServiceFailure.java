package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class VisualQueryDetectionServiceFailure extends DetectorFailure {
    public static final Parcelable.Creator<VisualQueryDetectionServiceFailure> CREATOR = new Parcelable.Creator<VisualQueryDetectionServiceFailure>() { // from class: android.service.voice.VisualQueryDetectionServiceFailure.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VisualQueryDetectionServiceFailure[] newArray(int size) {
            return new VisualQueryDetectionServiceFailure[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VisualQueryDetectionServiceFailure createFromParcel(Parcel in) {
            DetectorFailure detectorFailure = DetectorFailure.CREATOR.createFromParcel(in);
            return (VisualQueryDetectionServiceFailure) detectorFailure;
        }
    };
    public static final int ERROR_CODE_BINDING_DIED = 2;
    public static final int ERROR_CODE_BIND_FAILURE = 1;
    public static final int ERROR_CODE_ILLEGAL_ATTENTION_STATE = 3;
    public static final int ERROR_CODE_ILLEGAL_STREAMING_STATE = 4;
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 5;
    public static final int ERROR_CODE_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VisualQueryDetectionServiceErrorCode {
    }

    public VisualQueryDetectionServiceFailure(int errorCode, String errorMessage) {
        super(2, errorCode, errorMessage);
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
            case 3:
            case 5:
                return 3;
            case 4:
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
