package android.security.keymaster;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class KeymasterCertificateChain implements Parcelable {
    public static final Parcelable.Creator<KeymasterCertificateChain> CREATOR = new Parcelable.Creator<KeymasterCertificateChain>() { // from class: android.security.keymaster.KeymasterCertificateChain.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeymasterCertificateChain createFromParcel(Parcel in) {
            return new KeymasterCertificateChain(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeymasterCertificateChain[] newArray(int size) {
            return new KeymasterCertificateChain[size];
        }
    };
    private List<byte[]> mCertificates;

    public KeymasterCertificateChain() {
        this.mCertificates = null;
    }

    public KeymasterCertificateChain(List<byte[]> mCertificates) {
        this.mCertificates = mCertificates;
    }

    private KeymasterCertificateChain(Parcel in) {
        readFromParcel(in);
    }

    public void shallowCopyFrom(KeymasterCertificateChain other) {
        this.mCertificates = other.mCertificates;
    }

    public List<byte[]> getCertificates() {
        return this.mCertificates;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        List<byte[]> list = this.mCertificates;
        if (list == null) {
            out.writeInt(0);
            return;
        }
        out.writeInt(list.size());
        for (byte[] arg : this.mCertificates) {
            out.writeByteArray(arg);
        }
    }

    public void readFromParcel(Parcel in) {
        int length = in.readInt();
        this.mCertificates = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            this.mCertificates.add(in.createByteArray());
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
