package android.media;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public class MediaSyncEvent implements Parcelable {
    public static final Parcelable.Creator<MediaSyncEvent> CREATOR = new Parcelable.Creator<MediaSyncEvent>() { // from class: android.media.MediaSyncEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaSyncEvent createFromParcel(Parcel p) {
            return new MediaSyncEvent(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaSyncEvent[] newArray(int size) {
            return new MediaSyncEvent[size];
        }
    };
    public static final int SYNC_EVENT_NONE = 0;
    public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;
    @SystemApi
    public static final int SYNC_EVENT_SHARE_AUDIO_HISTORY = 100;
    private int mAudioSession;
    private final int mType;

    public static MediaSyncEvent createEvent(int eventType) throws IllegalArgumentException {
        if (!isValidType(eventType)) {
            throw new IllegalArgumentException(eventType + "is not a valid MediaSyncEvent type.");
        }
        return new MediaSyncEvent(eventType);
    }

    private MediaSyncEvent(int eventType) {
        this.mAudioSession = 0;
        this.mType = eventType;
    }

    public MediaSyncEvent setAudioSessionId(int audioSessionId) throws IllegalArgumentException {
        if (audioSessionId > 0) {
            this.mAudioSession = audioSessionId;
            return this;
        }
        throw new IllegalArgumentException(audioSessionId + " is not a valid session ID.");
    }

    public int getType() {
        return this.mType;
    }

    public int getAudioSessionId() {
        return this.mAudioSession;
    }

    private static boolean isValidType(int type) {
        switch (type) {
            case 0:
            case 1:
            case 100:
                return true;
            default:
                return false;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Objects.requireNonNull(dest);
        dest.writeInt(this.mType);
        dest.writeInt(this.mAudioSession);
    }

    private MediaSyncEvent(Parcel in) {
        this.mAudioSession = 0;
        this.mType = in.readInt();
        this.mAudioSession = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaSyncEvent that = (MediaSyncEvent) o;
        if (this.mType == that.mType && this.mAudioSession == that.mAudioSession) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mType), Integer.valueOf(this.mAudioSession));
    }

    public String toString() {
        return new String("MediaSyncEvent: type=" + typeToString(this.mType) + " session=" + this.mAudioSession);
    }

    public static String typeToString(int type) {
        switch (type) {
            case 0:
                return "SYNC_EVENT_NONE";
            case 1:
                return "SYNC_EVENT_PRESENTATION_COMPLETE";
            case 100:
                return "SYNC_EVENT_SHARE_AUDIO_HISTORY";
            default:
                return "unknown event type " + type;
        }
    }
}
