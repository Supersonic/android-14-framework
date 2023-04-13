package android.service.games;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class GameScreenshotResult implements Parcelable {
    public static final Parcelable.Creator<GameScreenshotResult> CREATOR = new Parcelable.Creator<GameScreenshotResult>() { // from class: android.service.games.GameScreenshotResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameScreenshotResult createFromParcel(Parcel source) {
            return new GameScreenshotResult(source.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameScreenshotResult[] newArray(int size) {
            return new GameScreenshotResult[0];
        }
    };
    public static final int GAME_SCREENSHOT_ERROR_INTERNAL_ERROR = 1;
    public static final int GAME_SCREENSHOT_SUCCESS = 0;
    private final int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GameScreenshotStatus {
    }

    public static GameScreenshotResult createSuccessResult() {
        return new GameScreenshotResult(0);
    }

    public static GameScreenshotResult createInternalErrorResult() {
        return new GameScreenshotResult(1);
    }

    private GameScreenshotResult(int status) {
        this.mStatus = status;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatus);
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String toString() {
        return "GameScreenshotResult{mStatus=" + this.mStatus + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GameScreenshotResult) {
            GameScreenshotResult that = (GameScreenshotResult) o;
            return this.mStatus == that.mStatus;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mStatus));
    }
}
