package android.app.wallpapereffectsgeneration;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public final class CameraAttributes implements Parcelable {
    public static final Parcelable.Creator<CameraAttributes> CREATOR = new Parcelable.Creator<CameraAttributes>() { // from class: android.app.wallpapereffectsgeneration.CameraAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraAttributes createFromParcel(Parcel in) {
            return new CameraAttributes(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraAttributes[] newArray(int size) {
            return new CameraAttributes[size];
        }
    };
    private float[] mAnchorPointInOutputUvSpace;
    private float[] mAnchorPointInWorldSpace;
    private float mCameraOrbitPitchDegrees;
    private float mCameraOrbitYawDegrees;
    private float mDollyDistanceInWorldSpace;
    private float mFrustumFarInWorldSpace;
    private float mFrustumNearInWorldSpace;
    private float mVerticalFovDegrees;

    private CameraAttributes(Parcel in) {
        this.mCameraOrbitYawDegrees = in.readFloat();
        this.mCameraOrbitPitchDegrees = in.readFloat();
        this.mDollyDistanceInWorldSpace = in.readFloat();
        this.mVerticalFovDegrees = in.readFloat();
        this.mFrustumNearInWorldSpace = in.readFloat();
        this.mFrustumFarInWorldSpace = in.readFloat();
        this.mAnchorPointInWorldSpace = in.createFloatArray();
        this.mAnchorPointInOutputUvSpace = in.createFloatArray();
    }

    private CameraAttributes(float[] anchorPointInWorldSpace, float[] anchorPointInOutputUvSpace, float cameraOrbitYawDegrees, float cameraOrbitPitchDegrees, float dollyDistanceInWorldSpace, float verticalFovDegrees, float frustumNearInWorldSpace, float frustumFarInWorldSpace) {
        this.mAnchorPointInWorldSpace = anchorPointInWorldSpace;
        this.mAnchorPointInOutputUvSpace = anchorPointInOutputUvSpace;
        this.mCameraOrbitYawDegrees = cameraOrbitYawDegrees;
        this.mCameraOrbitPitchDegrees = cameraOrbitPitchDegrees;
        this.mDollyDistanceInWorldSpace = dollyDistanceInWorldSpace;
        this.mVerticalFovDegrees = verticalFovDegrees;
        this.mFrustumNearInWorldSpace = frustumNearInWorldSpace;
        this.mFrustumFarInWorldSpace = frustumFarInWorldSpace;
    }

    public float[] getAnchorPointInWorldSpace() {
        return this.mAnchorPointInWorldSpace;
    }

    public float[] getAnchorPointInOutputUvSpace() {
        return this.mAnchorPointInOutputUvSpace;
    }

    public float getCameraOrbitYawDegrees() {
        return this.mCameraOrbitYawDegrees;
    }

    public float getCameraOrbitPitchDegrees() {
        return this.mCameraOrbitPitchDegrees;
    }

    public float getDollyDistanceInWorldSpace() {
        return this.mDollyDistanceInWorldSpace;
    }

    public float getVerticalFovDegrees() {
        return this.mVerticalFovDegrees;
    }

    public float getFrustumNearInWorldSpace() {
        return this.mFrustumNearInWorldSpace;
    }

    public float getFrustumFarInWorldSpace() {
        return this.mFrustumFarInWorldSpace;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.mCameraOrbitYawDegrees);
        out.writeFloat(this.mCameraOrbitPitchDegrees);
        out.writeFloat(this.mDollyDistanceInWorldSpace);
        out.writeFloat(this.mVerticalFovDegrees);
        out.writeFloat(this.mFrustumNearInWorldSpace);
        out.writeFloat(this.mFrustumFarInWorldSpace);
        out.writeFloatArray(this.mAnchorPointInWorldSpace);
        out.writeFloatArray(this.mAnchorPointInOutputUvSpace);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private float[] mAnchorPointInOutputUvSpace;
        private float[] mAnchorPointInWorldSpace;
        private float mCameraOrbitPitchDegrees;
        private float mCameraOrbitYawDegrees;
        private float mDollyDistanceInWorldSpace;
        private float mFrustumFarInWorldSpace;
        private float mFrustumNearInWorldSpace;
        private float mVerticalFovDegrees;

        @SystemApi
        public Builder(float[] anchorPointInWorldSpace, float[] anchorPointInOutputUvSpace) {
            this.mAnchorPointInWorldSpace = anchorPointInWorldSpace;
            this.mAnchorPointInOutputUvSpace = anchorPointInOutputUvSpace;
        }

        public Builder setCameraOrbitYawDegrees(float cameraOrbitYawDegrees) {
            this.mCameraOrbitYawDegrees = cameraOrbitYawDegrees;
            return this;
        }

        public Builder setCameraOrbitPitchDegrees(float cameraOrbitPitchDegrees) {
            this.mCameraOrbitPitchDegrees = cameraOrbitPitchDegrees;
            return this;
        }

        public Builder setDollyDistanceInWorldSpace(float dollyDistanceInWorldSpace) {
            this.mDollyDistanceInWorldSpace = dollyDistanceInWorldSpace;
            return this;
        }

        public Builder setVerticalFovDegrees(float verticalFovDegrees) {
            this.mVerticalFovDegrees = verticalFovDegrees;
            return this;
        }

        public Builder setFrustumNearInWorldSpace(float frustumNearInWorldSpace) {
            this.mFrustumNearInWorldSpace = frustumNearInWorldSpace;
            return this;
        }

        public Builder setFrustumFarInWorldSpace(float frustumFarInWorldSpace) {
            this.mFrustumFarInWorldSpace = frustumFarInWorldSpace;
            return this;
        }

        public CameraAttributes build() {
            return new CameraAttributes(this.mAnchorPointInWorldSpace, this.mAnchorPointInOutputUvSpace, this.mCameraOrbitYawDegrees, this.mCameraOrbitPitchDegrees, this.mDollyDistanceInWorldSpace, this.mVerticalFovDegrees, this.mFrustumNearInWorldSpace, this.mFrustumFarInWorldSpace);
        }
    }
}
