package android.security.keymaster;

import android.p008os.Parcel;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class KeymasterBooleanArgument extends KeymasterArgument {
    public final boolean value;

    public KeymasterBooleanArgument(int tag) {
        super(tag);
        this.value = true;
        switch (KeymasterDefs.getTagType(tag)) {
            case 1879048192:
                return;
            default:
                throw new IllegalArgumentException("Bad bool tag " + tag);
        }
    }

    public KeymasterBooleanArgument(int tag, Parcel in) {
        super(tag);
        this.value = true;
    }

    @Override // android.security.keymaster.KeymasterArgument
    public void writeValue(Parcel out) {
    }
}
