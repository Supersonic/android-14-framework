package android.window;

import android.content.res.Configuration;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public final class DisplayAreaInfo implements Parcelable {
    public static final Parcelable.Creator<DisplayAreaInfo> CREATOR = new Parcelable.Creator<DisplayAreaInfo>() { // from class: android.window.DisplayAreaInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayAreaInfo createFromParcel(Parcel in) {
            return new DisplayAreaInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayAreaInfo[] newArray(int size) {
            return new DisplayAreaInfo[size];
        }
    };
    public final Configuration configuration;
    public final int displayId;
    public final int featureId;
    public int rootDisplayAreaId;
    public final WindowContainerToken token;

    public DisplayAreaInfo(WindowContainerToken token, int displayId, int featureId) {
        this.configuration = new Configuration();
        this.rootDisplayAreaId = -1;
        this.token = token;
        this.displayId = displayId;
        this.featureId = featureId;
    }

    private DisplayAreaInfo(Parcel in) {
        Configuration configuration = new Configuration();
        this.configuration = configuration;
        this.rootDisplayAreaId = -1;
        this.token = WindowContainerToken.CREATOR.createFromParcel(in);
        configuration.readFromParcel(in);
        this.displayId = in.readInt();
        this.featureId = in.readInt();
        this.rootDisplayAreaId = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.token.writeToParcel(dest, flags);
        this.configuration.writeToParcel(dest, flags);
        dest.writeInt(this.displayId);
        dest.writeInt(this.featureId);
        dest.writeInt(this.rootDisplayAreaId);
    }

    public String toString() {
        return "DisplayAreaInfo{token=" + this.token + " config=" + this.configuration + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
