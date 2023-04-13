package android.security.keymaster;

import android.p008os.Parcel;
import java.util.Date;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class KeymasterDateArgument extends KeymasterArgument {
    public final Date date;

    public KeymasterDateArgument(int tag, Date date) {
        super(tag);
        switch (KeymasterDefs.getTagType(tag)) {
            case 1610612736:
                this.date = date;
                return;
            default:
                throw new IllegalArgumentException("Bad date tag " + tag);
        }
    }

    public KeymasterDateArgument(int tag, Parcel in) {
        super(tag);
        this.date = new Date(in.readLong());
    }

    @Override // android.security.keymaster.KeymasterArgument
    public void writeValue(Parcel out) {
        out.writeLong(this.date.getTime());
    }
}
