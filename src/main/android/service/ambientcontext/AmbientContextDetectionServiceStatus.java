package android.service.ambientcontext;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.app.ambientcontext.AmbientContextManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class AmbientContextDetectionServiceStatus implements Parcelable {
    public static final Parcelable.Creator<AmbientContextDetectionServiceStatus> CREATOR = new Parcelable.Creator<AmbientContextDetectionServiceStatus>() { // from class: android.service.ambientcontext.AmbientContextDetectionServiceStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AmbientContextDetectionServiceStatus[] newArray(int size) {
            return new AmbientContextDetectionServiceStatus[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AmbientContextDetectionServiceStatus createFromParcel(Parcel in) {
            return new AmbientContextDetectionServiceStatus(in);
        }
    };
    public static final String STATUS_RESPONSE_BUNDLE_KEY = "android.app.ambientcontext.AmbientContextServiceStatusBundleKey";
    private final String mPackageName;
    private final int mStatusCode;

    AmbientContextDetectionServiceStatus(int statusCode, String packageName) {
        this.mStatusCode = statusCode;
        AnnotationValidations.validate((Class<? extends Annotation>) AmbientContextManager.StatusCode.class, (Annotation) null, statusCode);
        this.mPackageName = packageName;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String toString() {
        return "AmbientContextDetectionServiceStatus { statusCode = " + this.mStatusCode + ", packageName = " + this.mPackageName + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) 0);
        dest.writeInt(this.mStatusCode);
        dest.writeString(this.mPackageName);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AmbientContextDetectionServiceStatus(Parcel in) {
        in.readByte();
        int statusCode = in.readInt();
        String packageName = in.readString();
        this.mStatusCode = statusCode;
        AnnotationValidations.validate((Class<? extends Annotation>) AmbientContextManager.StatusCode.class, (Annotation) null, statusCode);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private String mPackageName;
        private int mStatusCode;

        public Builder(String packageName) {
            Objects.requireNonNull(packageName);
            this.mPackageName = packageName;
        }

        public Builder setStatusCode(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mStatusCode = value;
            return this;
        }

        public AmbientContextDetectionServiceStatus build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 2;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mStatusCode = 0;
            }
            AmbientContextDetectionServiceStatus o = new AmbientContextDetectionServiceStatus(this.mStatusCode, this.mPackageName);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 2) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
