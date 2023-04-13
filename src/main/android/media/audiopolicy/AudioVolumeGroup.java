package android.media.audiopolicy;

import android.annotation.SystemApi;
import android.media.AudioAttributes;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SystemApi
/* loaded from: classes2.dex */
public final class AudioVolumeGroup implements Parcelable {
    public static final int DEFAULT_VOLUME_GROUP = -1;
    private static final String TAG = "AudioVolumeGroup";
    private static List<AudioVolumeGroup> sAudioVolumeGroups;
    private final AudioAttributes[] mAudioAttributes;
    private int mId;
    private int[] mLegacyStreamTypes;
    private final String mName;
    private static final Object sLock = new Object();
    public static final Parcelable.Creator<AudioVolumeGroup> CREATOR = new Parcelable.Creator<AudioVolumeGroup>() { // from class: android.media.audiopolicy.AudioVolumeGroup.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioVolumeGroup createFromParcel(Parcel in) {
            Preconditions.checkNotNull(in, "in Parcel must not be null");
            String name = in.readString();
            int id = in.readInt();
            int nbAttributes = in.readInt();
            AudioAttributes[] audioAttributes = new AudioAttributes[nbAttributes];
            for (int index = 0; index < nbAttributes; index++) {
                audioAttributes[index] = AudioAttributes.CREATOR.createFromParcel(in);
            }
            int nbStreamTypes = in.readInt();
            int[] streamTypes = new int[nbStreamTypes];
            for (int index2 = 0; index2 < nbStreamTypes; index2++) {
                streamTypes[index2] = in.readInt();
            }
            return new AudioVolumeGroup(name, id, audioAttributes, streamTypes);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioVolumeGroup[] newArray(int size) {
            return new AudioVolumeGroup[size];
        }
    };

    private static native int native_list_audio_volume_groups(ArrayList<AudioVolumeGroup> arrayList);

    public static List<AudioVolumeGroup> getAudioVolumeGroups() {
        if (sAudioVolumeGroups == null) {
            synchronized (sLock) {
                if (sAudioVolumeGroups == null) {
                    sAudioVolumeGroups = initializeAudioVolumeGroups();
                }
            }
        }
        return sAudioVolumeGroups;
    }

    private static List<AudioVolumeGroup> initializeAudioVolumeGroups() {
        ArrayList<AudioVolumeGroup> avgList = new ArrayList<>();
        int status = native_list_audio_volume_groups(avgList);
        if (status != 0) {
            Log.m104w(TAG, ": listAudioVolumeGroups failed");
        }
        return avgList;
    }

    AudioVolumeGroup(String name, int id, AudioAttributes[] audioAttributes, int[] legacyStreamTypes) {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkNotNull(audioAttributes, "audioAttributes must not be null");
        Preconditions.checkNotNull(legacyStreamTypes, "legacyStreamTypes must not be null");
        this.mName = name;
        this.mId = id;
        this.mAudioAttributes = audioAttributes;
        this.mLegacyStreamTypes = legacyStreamTypes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioVolumeGroup thatAvg = (AudioVolumeGroup) o;
        if (this.mName.equals(thatAvg.mName) && this.mId == thatAvg.mId && Arrays.equals(this.mAudioAttributes, thatAvg.mAudioAttributes)) {
            return true;
        }
        return false;
    }

    public List<AudioAttributes> getAudioAttributes() {
        return Arrays.asList(this.mAudioAttributes);
    }

    public int[] getLegacyStreamTypes() {
        return this.mLegacyStreamTypes;
    }

    public String name() {
        return this.mName;
    }

    public int getId() {
        return this.mId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        AudioAttributes[] audioAttributesArr;
        int[] iArr;
        dest.writeString(this.mName);
        dest.writeInt(this.mId);
        dest.writeInt(this.mAudioAttributes.length);
        for (AudioAttributes attributes : this.mAudioAttributes) {
            attributes.writeToParcel(dest, flags | 1);
        }
        dest.writeInt(this.mLegacyStreamTypes.length);
        for (int streamType : this.mLegacyStreamTypes) {
            dest.writeInt(streamType);
        }
    }

    public String toString() {
        AudioAttributes[] audioAttributesArr;
        int[] iArr;
        StringBuilder s = new StringBuilder();
        s.append("\n Name: ");
        s.append(this.mName);
        s.append(" Id: ");
        s.append(Integer.toString(this.mId));
        s.append("\n     Supported Audio Attributes:");
        for (AudioAttributes attribute : this.mAudioAttributes) {
            s.append("\n       -");
            s.append(attribute.toString());
        }
        s.append("\n     Supported Legacy Stream Types: { ");
        for (int legacyStreamType : this.mLegacyStreamTypes) {
            s.append(Integer.toString(legacyStreamType));
            s.append(" ");
        }
        s.append("}");
        return s.toString();
    }
}
