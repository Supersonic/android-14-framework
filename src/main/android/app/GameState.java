package android.app;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class GameState implements Parcelable {
    public static final Parcelable.Creator<GameState> CREATOR = new Parcelable.Creator<GameState>() { // from class: android.app.GameState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameState createFromParcel(Parcel in) {
            return new GameState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameState[] newArray(int size) {
            return new GameState[size];
        }
    };
    public static final int MODE_CONTENT = 4;
    public static final int MODE_GAMEPLAY_INTERRUPTIBLE = 2;
    public static final int MODE_GAMEPLAY_UNINTERRUPTIBLE = 3;
    public static final int MODE_NONE = 1;
    public static final int MODE_UNKNOWN = 0;
    private final boolean mIsLoading;
    private final int mLabel;
    private final int mMode;
    private final int mQuality;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface GameStateMode {
    }

    public GameState(boolean isLoading, int mode) {
        this(isLoading, mode, -1, -1);
    }

    public GameState(boolean isLoading, int mode, int label, int quality) {
        this.mIsLoading = isLoading;
        this.mMode = mode;
        this.mLabel = label;
        this.mQuality = quality;
    }

    private GameState(Parcel in) {
        this.mIsLoading = in.readBoolean();
        this.mMode = in.readInt();
        this.mLabel = in.readInt();
        this.mQuality = in.readInt();
    }

    public boolean isLoading() {
        return this.mIsLoading;
    }

    public int getMode() {
        return this.mMode;
    }

    public int getLabel() {
        return this.mLabel;
    }

    public int getQuality() {
        return this.mQuality;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBoolean(this.mIsLoading);
        parcel.writeInt(this.mMode);
        parcel.writeInt(this.mLabel);
        parcel.writeInt(this.mQuality);
    }
}
