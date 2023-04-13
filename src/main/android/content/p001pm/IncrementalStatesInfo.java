package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.content.pm.IncrementalStatesInfo */
/* loaded from: classes.dex */
public class IncrementalStatesInfo implements Parcelable {
    public static final Parcelable.Creator<IncrementalStatesInfo> CREATOR = new Parcelable.Creator<IncrementalStatesInfo>() { // from class: android.content.pm.IncrementalStatesInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IncrementalStatesInfo createFromParcel(Parcel source) {
            return new IncrementalStatesInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IncrementalStatesInfo[] newArray(int size) {
            return new IncrementalStatesInfo[size];
        }
    };
    private boolean mIsLoading;
    private float mProgress;

    public IncrementalStatesInfo(boolean isLoading, float progress) {
        this.mIsLoading = isLoading;
        this.mProgress = progress;
    }

    private IncrementalStatesInfo(Parcel source) {
        this.mIsLoading = source.readBoolean();
        this.mProgress = source.readFloat();
    }

    public boolean isLoading() {
        return this.mIsLoading;
    }

    public float getProgress() {
        return this.mProgress;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mIsLoading);
        dest.writeFloat(this.mProgress);
    }
}
