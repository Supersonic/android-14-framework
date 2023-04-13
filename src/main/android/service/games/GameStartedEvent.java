package android.service.games;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class GameStartedEvent implements Parcelable {
    public static final Parcelable.Creator<GameStartedEvent> CREATOR = new Parcelable.Creator<GameStartedEvent>() { // from class: android.service.games.GameStartedEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameStartedEvent createFromParcel(Parcel source) {
            return new GameStartedEvent(source.readInt(), source.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameStartedEvent[] newArray(int size) {
            return new GameStartedEvent[0];
        }
    };
    private final String mPackageName;
    private final int mTaskId;

    public GameStartedEvent(int taskId, String packageName) {
        this.mTaskId = taskId;
        this.mPackageName = packageName;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTaskId);
        dest.writeString(this.mPackageName);
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String toString() {
        return "GameStartedEvent{mTaskId=" + this.mTaskId + ", mPackageName='" + this.mPackageName + "'}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GameStartedEvent) {
            GameStartedEvent that = (GameStartedEvent) o;
            return this.mTaskId == that.mTaskId && Objects.equals(this.mPackageName, that.mPackageName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mTaskId), this.mPackageName);
    }
}
