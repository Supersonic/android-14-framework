package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public abstract class DetectorFailure implements Parcelable {
    public static final Parcelable.Creator<DetectorFailure> CREATOR = new Parcelable.Creator<DetectorFailure>() { // from class: android.service.voice.DetectorFailure.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DetectorFailure[] newArray(int size) {
            return new DetectorFailure[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DetectorFailure createFromParcel(Parcel in) {
            int errorSourceType = in.readInt();
            int errorCode = in.readInt();
            String errorMessage = in.readString8();
            switch (errorSourceType) {
                case 0:
                    return new HotwordDetectionServiceFailure(errorCode, errorMessage);
                case 1:
                    return new SoundTriggerFailure(errorCode, errorMessage);
                case 2:
                    return new VisualQueryDetectionServiceFailure(errorCode, errorMessage);
                default:
                    return new UnknownFailure(errorMessage);
            }
        }
    };
    public static final int ERROR_SOURCE_TYPE_HOTWORD_DETECTION = 0;
    public static final int ERROR_SOURCE_TYPE_SOUND_TRIGGER = 1;
    public static final int ERROR_SOURCE_TYPE_UNKNOWN = -1;
    public static final int ERROR_SOURCE_TYPE_VISUAL_QUERY_DETECTION = 2;
    public static final int SUGGESTED_ACTION_DISABLE_DETECTION = 2;
    public static final int SUGGESTED_ACTION_NONE = 1;
    public static final int SUGGESTED_ACTION_RECREATE_DETECTOR = 3;
    public static final int SUGGESTED_ACTION_RESTART_RECOGNITION = 4;
    public static final int SUGGESTED_ACTION_UNKNOWN = 0;
    private int mErrorCode;
    private String mErrorMessage;
    private int mErrorSourceType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SuggestedAction {
    }

    public abstract int getSuggestedAction();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DetectorFailure(int errorSourceType, int errorCode, String errorMessage) {
        this.mErrorSourceType = -1;
        this.mErrorCode = 0;
        this.mErrorMessage = "Unknown";
        Preconditions.checkArgumentInRange(errorSourceType, -1, 2, "errorSourceType");
        if (TextUtils.isEmpty(errorMessage)) {
            throw new IllegalArgumentException("errorMessage is empty or null.");
        }
        this.mErrorSourceType = errorSourceType;
        this.mErrorCode = errorCode;
        this.mErrorMessage = errorMessage;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getErrorMessage() {
        return this.mErrorMessage;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mErrorSourceType);
        dest.writeInt(this.mErrorCode);
        dest.writeString8(this.mErrorMessage);
    }
}
