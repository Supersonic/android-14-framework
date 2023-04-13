package android.service.rotationresolver;

import android.annotation.DurationMillisLong;
import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.Surface;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
@SystemApi
/* loaded from: classes3.dex */
public final class RotationResolutionRequest implements Parcelable {
    public static final Parcelable.Creator<RotationResolutionRequest> CREATOR = new Parcelable.Creator<RotationResolutionRequest>() { // from class: android.service.rotationresolver.RotationResolutionRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RotationResolutionRequest[] newArray(int size) {
            return new RotationResolutionRequest[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RotationResolutionRequest createFromParcel(Parcel in) {
            return new RotationResolutionRequest(in);
        }
    };
    private final int mCurrentRotation;
    private final String mForegroundPackageName;
    private final int mProposedRotation;
    private final boolean mShouldUseCamera;
    private final long mTimeoutMillis;

    public RotationResolutionRequest(String foregroundPackageName, int currentRotation, int proposedRotation, boolean shouldUseCamera, long timeoutMillis) {
        this.mForegroundPackageName = foregroundPackageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) foregroundPackageName);
        this.mCurrentRotation = currentRotation;
        AnnotationValidations.validate((Class<? extends Annotation>) Surface.Rotation.class, (Annotation) null, currentRotation);
        this.mProposedRotation = proposedRotation;
        AnnotationValidations.validate((Class<? extends Annotation>) Surface.Rotation.class, (Annotation) null, proposedRotation);
        this.mShouldUseCamera = shouldUseCamera;
        this.mTimeoutMillis = timeoutMillis;
        AnnotationValidations.validate(DurationMillisLong.class, (Annotation) null, timeoutMillis);
    }

    public String getForegroundPackageName() {
        return this.mForegroundPackageName;
    }

    public int getCurrentRotation() {
        return this.mCurrentRotation;
    }

    public int getProposedRotation() {
        return this.mProposedRotation;
    }

    public boolean shouldUseCamera() {
        return this.mShouldUseCamera;
    }

    public long getTimeoutMillis() {
        return this.mTimeoutMillis;
    }

    public String toString() {
        return "RotationResolutionRequest { foregroundPackageName = " + this.mForegroundPackageName + ", currentRotation = " + this.mCurrentRotation + ", proposedRotation = " + this.mProposedRotation + ", shouldUseCamera = " + this.mShouldUseCamera + ", timeoutMillis = " + this.mTimeoutMillis + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mShouldUseCamera ? (byte) (0 | 8) : (byte) 0;
        dest.writeByte(flg);
        dest.writeString(this.mForegroundPackageName);
        dest.writeInt(this.mCurrentRotation);
        dest.writeInt(this.mProposedRotation);
        dest.writeLong(this.mTimeoutMillis);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    RotationResolutionRequest(Parcel in) {
        byte flg = in.readByte();
        boolean shouldUseCamera = (flg & 8) != 0;
        String foregroundPackageName = in.readString();
        int currentRotation = in.readInt();
        int proposedRotation = in.readInt();
        long timeoutMillis = in.readLong();
        this.mForegroundPackageName = foregroundPackageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) foregroundPackageName);
        this.mCurrentRotation = currentRotation;
        AnnotationValidations.validate((Class<? extends Annotation>) Surface.Rotation.class, (Annotation) null, currentRotation);
        this.mProposedRotation = proposedRotation;
        AnnotationValidations.validate((Class<? extends Annotation>) Surface.Rotation.class, (Annotation) null, proposedRotation);
        this.mShouldUseCamera = shouldUseCamera;
        this.mTimeoutMillis = timeoutMillis;
        AnnotationValidations.validate(DurationMillisLong.class, (Annotation) null, timeoutMillis);
    }

    @Deprecated
    private void __metadata() {
    }
}
