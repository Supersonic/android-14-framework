package android.hardware.broadcastradio;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Metadata implements Parcelable {
    public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator<Metadata>() { // from class: android.hardware.broadcastradio.Metadata.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Metadata createFromParcel(Parcel parcel) {
            return new Metadata(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Metadata[] newArray(int i) {
            return new Metadata[i];
        }
    };
    public int _tag;
    public Object _value;

    public final int getStability() {
        return 1;
    }

    public Metadata() {
        this._tag = 0;
        this._value = null;
    }

    public Metadata(Parcel parcel) {
        readFromParcel(parcel);
    }

    public int getTag() {
        return this._tag;
    }

    public String getRdsPs() {
        _assertTag(0);
        return (String) this._value;
    }

    public int getRdsPty() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public int getRbdsPty() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public String getRdsRt() {
        _assertTag(3);
        return (String) this._value;
    }

    public String getSongTitle() {
        _assertTag(4);
        return (String) this._value;
    }

    public String getSongArtist() {
        _assertTag(5);
        return (String) this._value;
    }

    public String getSongAlbum() {
        _assertTag(6);
        return (String) this._value;
    }

    public int getStationIcon() {
        _assertTag(7);
        return ((Integer) this._value).intValue();
    }

    public int getAlbumArt() {
        _assertTag(8);
        return ((Integer) this._value).intValue();
    }

    public String getProgramName() {
        _assertTag(9);
        return (String) this._value;
    }

    public String getDabEnsembleName() {
        _assertTag(10);
        return (String) this._value;
    }

    public String getDabEnsembleNameShort() {
        _assertTag(11);
        return (String) this._value;
    }

    public String getDabServiceName() {
        _assertTag(12);
        return (String) this._value;
    }

    public String getDabServiceNameShort() {
        _assertTag(13);
        return (String) this._value;
    }

    public String getDabComponentName() {
        _assertTag(14);
        return (String) this._value;
    }

    public String getDabComponentNameShort() {
        _assertTag(15);
        return (String) this._value;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._tag);
        switch (this._tag) {
            case 0:
                parcel.writeString(getRdsPs());
                return;
            case 1:
                parcel.writeInt(getRdsPty());
                return;
            case 2:
                parcel.writeInt(getRbdsPty());
                return;
            case 3:
                parcel.writeString(getRdsRt());
                return;
            case 4:
                parcel.writeString(getSongTitle());
                return;
            case 5:
                parcel.writeString(getSongArtist());
                return;
            case 6:
                parcel.writeString(getSongAlbum());
                return;
            case 7:
                parcel.writeInt(getStationIcon());
                return;
            case 8:
                parcel.writeInt(getAlbumArt());
                return;
            case 9:
                parcel.writeString(getProgramName());
                return;
            case 10:
                parcel.writeString(getDabEnsembleName());
                return;
            case 11:
                parcel.writeString(getDabEnsembleNameShort());
                return;
            case 12:
                parcel.writeString(getDabServiceName());
                return;
            case 13:
                parcel.writeString(getDabServiceNameShort());
                return;
            case 14:
                parcel.writeString(getDabComponentName());
                return;
            case 15:
                parcel.writeString(getDabComponentNameShort());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel parcel) {
        int readInt = parcel.readInt();
        switch (readInt) {
            case 0:
                _set(readInt, parcel.readString());
                return;
            case 1:
                _set(readInt, Integer.valueOf(parcel.readInt()));
                return;
            case 2:
                _set(readInt, Integer.valueOf(parcel.readInt()));
                return;
            case 3:
                _set(readInt, parcel.readString());
                return;
            case 4:
                _set(readInt, parcel.readString());
                return;
            case 5:
                _set(readInt, parcel.readString());
                return;
            case 6:
                _set(readInt, parcel.readString());
                return;
            case 7:
                _set(readInt, Integer.valueOf(parcel.readInt()));
                return;
            case 8:
                _set(readInt, Integer.valueOf(parcel.readInt()));
                return;
            case 9:
                _set(readInt, parcel.readString());
                return;
            case 10:
                _set(readInt, parcel.readString());
                return;
            case 11:
                _set(readInt, parcel.readString());
                return;
            case 12:
                _set(readInt, parcel.readString());
                return;
            case 13:
                _set(readInt, parcel.readString());
                return;
            case 14:
                _set(readInt, parcel.readString());
                return;
            case 15:
                _set(readInt, parcel.readString());
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + readInt);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        getTag();
        return 0;
    }

    public String toString() {
        switch (this._tag) {
            case 0:
                return "android.hardware.broadcastradio.Metadata.rdsPs(" + Objects.toString(getRdsPs()) + ")";
            case 1:
                return "android.hardware.broadcastradio.Metadata.rdsPty(" + getRdsPty() + ")";
            case 2:
                return "android.hardware.broadcastradio.Metadata.rbdsPty(" + getRbdsPty() + ")";
            case 3:
                return "android.hardware.broadcastradio.Metadata.rdsRt(" + Objects.toString(getRdsRt()) + ")";
            case 4:
                return "android.hardware.broadcastradio.Metadata.songTitle(" + Objects.toString(getSongTitle()) + ")";
            case 5:
                return "android.hardware.broadcastradio.Metadata.songArtist(" + Objects.toString(getSongArtist()) + ")";
            case 6:
                return "android.hardware.broadcastradio.Metadata.songAlbum(" + Objects.toString(getSongAlbum()) + ")";
            case 7:
                return "android.hardware.broadcastradio.Metadata.stationIcon(" + getStationIcon() + ")";
            case 8:
                return "android.hardware.broadcastradio.Metadata.albumArt(" + getAlbumArt() + ")";
            case 9:
                return "android.hardware.broadcastradio.Metadata.programName(" + Objects.toString(getProgramName()) + ")";
            case 10:
                return "android.hardware.broadcastradio.Metadata.dabEnsembleName(" + Objects.toString(getDabEnsembleName()) + ")";
            case 11:
                return "android.hardware.broadcastradio.Metadata.dabEnsembleNameShort(" + Objects.toString(getDabEnsembleNameShort()) + ")";
            case 12:
                return "android.hardware.broadcastradio.Metadata.dabServiceName(" + Objects.toString(getDabServiceName()) + ")";
            case 13:
                return "android.hardware.broadcastradio.Metadata.dabServiceNameShort(" + Objects.toString(getDabServiceNameShort()) + ")";
            case 14:
                return "android.hardware.broadcastradio.Metadata.dabComponentName(" + Objects.toString(getDabComponentName()) + ")";
            case 15:
                return "android.hardware.broadcastradio.Metadata.dabComponentNameShort(" + Objects.toString(getDabComponentNameShort()) + ")";
            default:
                throw new IllegalStateException("unknown field: " + this._tag);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof Metadata)) {
            Metadata metadata = (Metadata) obj;
            return this._tag == metadata._tag && Objects.deepEquals(this._value, metadata._value);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this._tag), this._value).toArray());
    }

    public final void _assertTag(int i) {
        if (getTag() == i) {
            return;
        }
        throw new IllegalStateException("bad access: " + _tagString(i) + ", " + _tagString(getTag()) + " is available.");
    }

    public final String _tagString(int i) {
        switch (i) {
            case 0:
                return "rdsPs";
            case 1:
                return "rdsPty";
            case 2:
                return "rbdsPty";
            case 3:
                return "rdsRt";
            case 4:
                return "songTitle";
            case 5:
                return "songArtist";
            case 6:
                return "songAlbum";
            case 7:
                return "stationIcon";
            case 8:
                return "albumArt";
            case 9:
                return "programName";
            case 10:
                return "dabEnsembleName";
            case 11:
                return "dabEnsembleNameShort";
            case 12:
                return "dabServiceName";
            case 13:
                return "dabServiceNameShort";
            case 14:
                return "dabComponentName";
            case 15:
                return "dabComponentNameShort";
            default:
                throw new IllegalStateException("unknown field: " + i);
        }
    }

    public final void _set(int i, Object obj) {
        this._tag = i;
        this._value = obj;
    }
}
