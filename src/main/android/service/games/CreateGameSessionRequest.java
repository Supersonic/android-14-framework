package android.service.games;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class CreateGameSessionRequest implements Parcelable {
    public static final Parcelable.Creator<CreateGameSessionRequest> CREATOR = new Parcelable.Creator<CreateGameSessionRequest>() { // from class: android.service.games.CreateGameSessionRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CreateGameSessionRequest createFromParcel(Parcel source) {
            return new CreateGameSessionRequest(source.readInt(), source.readString8());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CreateGameSessionRequest[] newArray(int size) {
            return new CreateGameSessionRequest[0];
        }
    };
    private final String mGamePackageName;
    private final int mTaskId;

    public CreateGameSessionRequest(int taskId, String gamePackageName) {
        this.mTaskId = taskId;
        this.mGamePackageName = gamePackageName;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTaskId);
        dest.writeString8(this.mGamePackageName);
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public String getGamePackageName() {
        return this.mGamePackageName;
    }

    public String toString() {
        return "GameSessionRequest{mTaskId=" + this.mTaskId + ", mGamePackageName='" + this.mGamePackageName + "'}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CreateGameSessionRequest) {
            CreateGameSessionRequest that = (CreateGameSessionRequest) o;
            return this.mTaskId == that.mTaskId && Objects.equals(this.mGamePackageName, that.mGamePackageName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mTaskId), this.mGamePackageName);
    }
}
