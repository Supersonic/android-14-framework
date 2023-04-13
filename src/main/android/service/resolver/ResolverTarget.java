package android.service.resolver;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes3.dex */
public final class ResolverTarget implements Parcelable {
    public static final Parcelable.Creator<ResolverTarget> CREATOR = new Parcelable.Creator<ResolverTarget>() { // from class: android.service.resolver.ResolverTarget.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResolverTarget createFromParcel(Parcel source) {
            return new ResolverTarget(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResolverTarget[] newArray(int size) {
            return new ResolverTarget[size];
        }
    };
    private static final String TAG = "ResolverTarget";
    private float mChooserScore;
    private float mLaunchScore;
    private float mRecencyScore;
    private float mSelectProbability;
    private float mTimeSpentScore;

    public ResolverTarget() {
    }

    ResolverTarget(Parcel in) {
        this.mRecencyScore = in.readFloat();
        this.mTimeSpentScore = in.readFloat();
        this.mLaunchScore = in.readFloat();
        this.mChooserScore = in.readFloat();
        this.mSelectProbability = in.readFloat();
    }

    public float getRecencyScore() {
        return this.mRecencyScore;
    }

    public void setRecencyScore(float recencyScore) {
        this.mRecencyScore = recencyScore;
    }

    public float getTimeSpentScore() {
        return this.mTimeSpentScore;
    }

    public void setTimeSpentScore(float timeSpentScore) {
        this.mTimeSpentScore = timeSpentScore;
    }

    public float getLaunchScore() {
        return this.mLaunchScore;
    }

    public void setLaunchScore(float launchScore) {
        this.mLaunchScore = launchScore;
    }

    public float getChooserScore() {
        return this.mChooserScore;
    }

    public void setChooserScore(float chooserScore) {
        this.mChooserScore = chooserScore;
    }

    public float getSelectProbability() {
        return this.mSelectProbability;
    }

    public void setSelectProbability(float selectProbability) {
        this.mSelectProbability = selectProbability;
    }

    public String toString() {
        return "ResolverTarget{" + this.mRecencyScore + ", " + this.mTimeSpentScore + ", " + this.mLaunchScore + ", " + this.mChooserScore + ", " + this.mSelectProbability + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mRecencyScore);
        dest.writeFloat(this.mTimeSpentScore);
        dest.writeFloat(this.mLaunchScore);
        dest.writeFloat(this.mChooserScore);
        dest.writeFloat(this.mSelectProbability);
    }
}
