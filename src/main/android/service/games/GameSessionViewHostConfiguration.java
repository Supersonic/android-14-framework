package android.service.games;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class GameSessionViewHostConfiguration implements Parcelable {
    public static final Parcelable.Creator<GameSessionViewHostConfiguration> CREATOR = new Parcelable.Creator<GameSessionViewHostConfiguration>() { // from class: android.service.games.GameSessionViewHostConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameSessionViewHostConfiguration createFromParcel(Parcel source) {
            return new GameSessionViewHostConfiguration(source.readInt(), source.readInt(), source.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameSessionViewHostConfiguration[] newArray(int size) {
            return new GameSessionViewHostConfiguration[0];
        }
    };
    final int mDisplayId;
    final int mHeightPx;
    final int mWidthPx;

    public GameSessionViewHostConfiguration(int displayId, int widthPx, int heightPx) {
        this.mDisplayId = displayId;
        this.mWidthPx = widthPx;
        this.mHeightPx = heightPx;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDisplayId);
        dest.writeInt(this.mWidthPx);
        dest.writeInt(this.mHeightPx);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GameSessionViewHostConfiguration) {
            GameSessionViewHostConfiguration that = (GameSessionViewHostConfiguration) o;
            return this.mDisplayId == that.mDisplayId && this.mWidthPx == that.mWidthPx && this.mHeightPx == that.mHeightPx;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mDisplayId), Integer.valueOf(this.mWidthPx), Integer.valueOf(this.mHeightPx));
    }

    public String toString() {
        return "GameSessionViewHostConfiguration{mDisplayId=" + this.mDisplayId + ", mWidthPx=" + this.mWidthPx + ", mHeightPx=" + this.mHeightPx + '}';
    }
}
