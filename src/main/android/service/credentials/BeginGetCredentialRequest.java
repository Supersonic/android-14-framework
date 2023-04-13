package android.service.credentials;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class BeginGetCredentialRequest implements Parcelable {
    public static final Parcelable.Creator<BeginGetCredentialRequest> CREATOR = new Parcelable.Creator<BeginGetCredentialRequest>() { // from class: android.service.credentials.BeginGetCredentialRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginGetCredentialRequest createFromParcel(Parcel in) {
            return new BeginGetCredentialRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginGetCredentialRequest[] newArray(int size) {
            return new BeginGetCredentialRequest[size];
        }
    };
    private final List<BeginGetCredentialOption> mBeginGetCredentialOptions;
    private final CallingAppInfo mCallingAppInfo;

    private BeginGetCredentialRequest(CallingAppInfo callingAppInfo, List<BeginGetCredentialOption> getBeginCredentialOptions) {
        this.mCallingAppInfo = callingAppInfo;
        this.mBeginGetCredentialOptions = getBeginCredentialOptions;
    }

    private BeginGetCredentialRequest(Parcel in) {
        this.mCallingAppInfo = (CallingAppInfo) in.readTypedObject(CallingAppInfo.CREATOR);
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, BeginGetCredentialOption.CREATOR);
        this.mBeginGetCredentialOptions = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mCallingAppInfo, flags);
        dest.writeTypedList(this.mBeginGetCredentialOptions);
    }

    public CallingAppInfo getCallingAppInfo() {
        return this.mCallingAppInfo;
    }

    public List<BeginGetCredentialOption> getBeginGetCredentialOptions() {
        return this.mBeginGetCredentialOptions;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private CallingAppInfo mCallingAppInfo = null;
        private List<BeginGetCredentialOption> mBeginGetCredentialOptions = new ArrayList();

        public Builder setCallingAppInfo(CallingAppInfo callingAppInfo) {
            this.mCallingAppInfo = callingAppInfo;
            return this;
        }

        public Builder setBeginGetCredentialOptions(List<BeginGetCredentialOption> getBeginCredentialOptions) {
            Preconditions.checkCollectionNotEmpty(getBeginCredentialOptions, "getBeginCredentialOptions");
            Preconditions.checkCollectionElementsNotNull(getBeginCredentialOptions, "getBeginCredentialOptions");
            this.mBeginGetCredentialOptions = getBeginCredentialOptions;
            return this;
        }

        public Builder addBeginGetCredentialOption(BeginGetCredentialOption beginGetCredentialOption) {
            Objects.requireNonNull(beginGetCredentialOption, "beginGetCredentialOption must not be null");
            this.mBeginGetCredentialOptions.add(beginGetCredentialOption);
            return this;
        }

        public BeginGetCredentialRequest build() {
            Preconditions.checkCollectionNotEmpty(this.mBeginGetCredentialOptions, "beginGetCredentialOptions");
            return new BeginGetCredentialRequest(this.mCallingAppInfo, this.mBeginGetCredentialOptions);
        }
    }
}
