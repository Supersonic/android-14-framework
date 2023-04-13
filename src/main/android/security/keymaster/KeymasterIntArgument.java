package android.security.keymaster;

import android.p008os.Parcel;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class KeymasterIntArgument extends KeymasterArgument {
    public final int value;

    public KeymasterIntArgument(int tag, int value) {
        super(tag);
        switch (KeymasterDefs.getTagType(tag)) {
            case 268435456:
            case 536870912:
            case 805306368:
            case 1073741824:
                this.value = value;
                return;
            default:
                throw new IllegalArgumentException("Bad int tag " + tag);
        }
    }

    public KeymasterIntArgument(int tag, Parcel in) {
        super(tag);
        this.value = in.readInt();
    }

    @Override // android.security.keymaster.KeymasterArgument
    public void writeValue(Parcel out) {
        out.writeInt(this.value);
    }
}
