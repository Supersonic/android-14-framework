package android.app.wallpapereffectsgeneration;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class CinematicEffectResponse implements Parcelable {
    public static final int CINEMATIC_EFFECT_STATUS_ANIMATION_FAILURE = 10;
    public static final int CINEMATIC_EFFECT_STATUS_CONTENT_TARGET_ERROR = 8;
    public static final int CINEMATIC_EFFECT_STATUS_CONTENT_TOO_FLAT = 9;
    public static final int CINEMATIC_EFFECT_STATUS_CONTENT_UNSUPPORTED = 7;
    public static final int CINEMATIC_EFFECT_STATUS_ERROR = 0;
    public static final int CINEMATIC_EFFECT_STATUS_FEATURE_DISABLED = 5;
    public static final int CINEMATIC_EFFECT_STATUS_IMAGE_FORMAT_NOT_SUITABLE = 6;
    public static final int CINEMATIC_EFFECT_STATUS_NOT_READY = 2;
    public static final int CINEMATIC_EFFECT_STATUS_OK = 1;
    public static final int CINEMATIC_EFFECT_STATUS_PENDING = 3;
    public static final int CINEMATIC_EFFECT_STATUS_TOO_MANY_REQUESTS = 4;
    public static final Parcelable.Creator<CinematicEffectResponse> CREATOR = new Parcelable.Creator<CinematicEffectResponse>() { // from class: android.app.wallpapereffectsgeneration.CinematicEffectResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CinematicEffectResponse createFromParcel(Parcel in) {
            return new CinematicEffectResponse(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CinematicEffectResponse[] newArray(int size) {
            return new CinematicEffectResponse[size];
        }
    };
    public static final int IMAGE_CONTENT_TYPE_LANDSCAPE = 2;
    public static final int IMAGE_CONTENT_TYPE_OTHER = 3;
    public static final int IMAGE_CONTENT_TYPE_PEOPLE_PORTRAIT = 1;
    public static final int IMAGE_CONTENT_TYPE_UNKNOWN = 0;
    private CameraAttributes mEndKeyFrame;
    private int mImageContentType;
    private CameraAttributes mStartKeyFrame;
    private int mStatusCode;
    private String mTaskId;
    private List<TexturedMesh> mTexturedMeshes;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CinematicEffectStatusCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ImageContentType {
    }

    private CinematicEffectResponse(Parcel in) {
        this.mStatusCode = in.readInt();
        this.mTaskId = in.readString();
        this.mImageContentType = in.readInt();
        ArrayList arrayList = new ArrayList();
        this.mTexturedMeshes = arrayList;
        in.readTypedList(arrayList, TexturedMesh.CREATOR);
        this.mStartKeyFrame = (CameraAttributes) in.readTypedObject(CameraAttributes.CREATOR);
        this.mEndKeyFrame = (CameraAttributes) in.readTypedObject(CameraAttributes.CREATOR);
    }

    private CinematicEffectResponse(int statusCode, String taskId, int imageContentType, List<TexturedMesh> texturedMeshes, CameraAttributes startKeyFrame, CameraAttributes endKeyFrame) {
        this.mStatusCode = statusCode;
        this.mTaskId = taskId;
        this.mImageContentType = imageContentType;
        this.mStartKeyFrame = startKeyFrame;
        this.mEndKeyFrame = endKeyFrame;
        this.mTexturedMeshes = texturedMeshes;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public String getTaskId() {
        return this.mTaskId;
    }

    public int getImageContentType() {
        return this.mImageContentType;
    }

    public List<TexturedMesh> getTexturedMeshes() {
        return this.mTexturedMeshes;
    }

    public CameraAttributes getStartKeyFrame() {
        return this.mStartKeyFrame;
    }

    public CameraAttributes getEndKeyFrame() {
        return this.mEndKeyFrame;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mStatusCode);
        out.writeString(this.mTaskId);
        out.writeInt(this.mImageContentType);
        out.writeTypedList(this.mTexturedMeshes, flags);
        out.writeTypedObject(this.mStartKeyFrame, flags);
        out.writeTypedObject(this.mEndKeyFrame, flags);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CinematicEffectResponse that = (CinematicEffectResponse) o;
        return this.mTaskId.equals(that.mTaskId);
    }

    public int hashCode() {
        return Objects.hash(this.mTaskId);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private CameraAttributes mEndKeyFrame;
        private int mImageContentType;
        private CameraAttributes mStartKeyFrame;
        private int mStatusCode;
        private String mTaskId;
        private List<TexturedMesh> mTexturedMeshes;

        @SystemApi
        public Builder(int statusCode, String taskId) {
            this.mStatusCode = statusCode;
            this.mTaskId = taskId;
        }

        public Builder setImageContentType(int imageContentType) {
            this.mImageContentType = imageContentType;
            return this;
        }

        public Builder setTexturedMeshes(List<TexturedMesh> texturedMeshes) {
            this.mTexturedMeshes = texturedMeshes;
            return this;
        }

        public Builder setStartKeyFrame(CameraAttributes startKeyFrame) {
            this.mStartKeyFrame = startKeyFrame;
            return this;
        }

        public Builder setEndKeyFrame(CameraAttributes endKeyFrame) {
            this.mEndKeyFrame = endKeyFrame;
            return this;
        }

        public CinematicEffectResponse build() {
            if (this.mTexturedMeshes == null) {
                this.mTexturedMeshes = new ArrayList(0);
            }
            return new CinematicEffectResponse(this.mStatusCode, this.mTaskId, this.mImageContentType, this.mTexturedMeshes, this.mStartKeyFrame, this.mEndKeyFrame);
        }
    }
}
