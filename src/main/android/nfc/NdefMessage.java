package android.nfc;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.proto.ProtoOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
/* loaded from: classes2.dex */
public final class NdefMessage implements Parcelable {
    public static final Parcelable.Creator<NdefMessage> CREATOR = new Parcelable.Creator<NdefMessage>() { // from class: android.nfc.NdefMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NdefMessage createFromParcel(Parcel in) {
            int recordsLength = in.readInt();
            NdefRecord[] records = new NdefRecord[recordsLength];
            in.readTypedArray(records, NdefRecord.CREATOR);
            return new NdefMessage(records);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NdefMessage[] newArray(int size) {
            return new NdefMessage[size];
        }
    };
    private final NdefRecord[] mRecords;

    public NdefMessage(byte[] data) throws FormatException {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.mRecords = NdefRecord.parse(buffer, false);
        if (buffer.remaining() > 0) {
            throw new FormatException("trailing data");
        }
    }

    public NdefMessage(NdefRecord record, NdefRecord... records) {
        if (record == null) {
            throw new NullPointerException("record cannot be null");
        }
        for (NdefRecord r : records) {
            if (r == null) {
                throw new NullPointerException("record cannot be null");
            }
        }
        NdefRecord[] ndefRecordArr = new NdefRecord[records.length + 1];
        this.mRecords = ndefRecordArr;
        ndefRecordArr[0] = record;
        System.arraycopy(records, 0, ndefRecordArr, 1, records.length);
    }

    public NdefMessage(NdefRecord[] records) {
        if (records.length < 1) {
            throw new IllegalArgumentException("must have at least one record");
        }
        for (NdefRecord r : records) {
            if (r == null) {
                throw new NullPointerException("records cannot contain null");
            }
        }
        this.mRecords = records;
    }

    public NdefRecord[] getRecords() {
        return this.mRecords;
    }

    public int getByteArrayLength() {
        NdefRecord[] ndefRecordArr;
        int length = 0;
        for (NdefRecord r : this.mRecords) {
            length += r.getByteLength();
        }
        return length;
    }

    public byte[] toByteArray() {
        int length = getByteArrayLength();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        int i = 0;
        while (true) {
            NdefRecord[] ndefRecordArr = this.mRecords;
            if (i < ndefRecordArr.length) {
                boolean me = false;
                boolean mb = i == 0;
                if (i == ndefRecordArr.length - 1) {
                    me = true;
                }
                ndefRecordArr[i].writeToByteBuffer(buffer, mb, me);
                i++;
            } else {
                return buffer.array();
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRecords.length);
        dest.writeTypedArray(this.mRecords, flags);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mRecords);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NdefMessage other = (NdefMessage) obj;
        return Arrays.equals(this.mRecords, other.mRecords);
    }

    public String toString() {
        return "NdefMessage " + Arrays.toString(this.mRecords);
    }

    public void dumpDebug(ProtoOutputStream proto) {
        NdefRecord[] ndefRecordArr;
        for (NdefRecord record : this.mRecords) {
            long token = proto.start(2246267895809L);
            record.dumpDebug(proto);
            proto.end(token);
        }
    }
}
