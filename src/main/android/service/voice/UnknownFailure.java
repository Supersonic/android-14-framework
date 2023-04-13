package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes3.dex */
public final class UnknownFailure extends DetectorFailure {
    public static final Parcelable.Creator<UnknownFailure> CREATOR = new Parcelable.Creator<UnknownFailure>() { // from class: android.service.voice.UnknownFailure.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UnknownFailure[] newArray(int size) {
            return new UnknownFailure[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UnknownFailure createFromParcel(Parcel in) {
            return (UnknownFailure) DetectorFailure.CREATOR.createFromParcel(in);
        }
    };
    public static final int ERROR_CODE_UNKNOWN = 0;

    public UnknownFailure(String errorMessage) {
        super(-1, 0, errorMessage);
    }

    @Override // android.service.voice.DetectorFailure
    public int getSuggestedAction() {
        return 0;
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
