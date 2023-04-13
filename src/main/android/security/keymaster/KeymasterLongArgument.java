package android.security.keymaster;

import android.p008os.Parcel;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class KeymasterLongArgument extends KeymasterArgument {
    public final long value;

    public KeymasterLongArgument(int tag, long value) {
        super(tag);
        switch (KeymasterDefs.getTagType(tag)) {
            case -1610612736:
            case 1342177280:
                this.value = value;
                return;
            default:
                throw new IllegalArgumentException("Bad long tag " + tag);
        }
    }

    public KeymasterLongArgument(int tag, Parcel in) {
        super(tag);
        this.value = in.readLong();
    }

    @Override // android.security.keymaster.KeymasterArgument
    public void writeValue(Parcel out) {
        out.writeLong(this.value);
    }
}
