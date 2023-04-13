package android.app.blob;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Base64;
import android.util.IndentingPrintWriter;
import com.android.internal.org.bouncycastle.cms.CMSAttributeTableGenerator;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public final class BlobHandle implements Parcelable {
    public static final String ALGO_SHA_256 = "SHA-256";
    private static final int LIMIT_BLOB_LABEL_LENGTH = 100;
    private static final int LIMIT_BLOB_TAG_LENGTH = 128;
    public final String algorithm;
    public final byte[] digest;
    public final long expiryTimeMillis;
    public final CharSequence label;
    public final String tag;
    private static final String[] SUPPORTED_ALGOS = {"SHA-256"};
    public static final Parcelable.Creator<BlobHandle> CREATOR = new Parcelable.Creator<BlobHandle>() { // from class: android.app.blob.BlobHandle.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BlobHandle createFromParcel(Parcel source) {
            return new BlobHandle(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BlobHandle[] newArray(int size) {
            return new BlobHandle[size];
        }
    };

    private BlobHandle(String algorithm, byte[] digest, CharSequence label, long expiryTimeMillis, String tag) {
        this.algorithm = algorithm;
        this.digest = digest;
        this.label = label;
        this.expiryTimeMillis = expiryTimeMillis;
        this.tag = tag;
    }

    private BlobHandle(Parcel in) {
        this.algorithm = in.readString();
        this.digest = in.createByteArray();
        this.label = in.readCharSequence();
        this.expiryTimeMillis = in.readLong();
        this.tag = in.readString();
    }

    public static BlobHandle create(String algorithm, byte[] digest, CharSequence label, long expiryTimeMillis, String tag) {
        BlobHandle handle = new BlobHandle(algorithm, digest, label, expiryTimeMillis, tag);
        handle.assertIsValid();
        return handle;
    }

    public static BlobHandle createWithSha256(byte[] digest, CharSequence label, long expiryTimeMillis, String tag) {
        return create("SHA-256", digest, label, expiryTimeMillis, tag);
    }

    public byte[] getSha256Digest() {
        return this.digest;
    }

    public CharSequence getLabel() {
        return this.label;
    }

    public long getExpiryTimeMillis() {
        return this.expiryTimeMillis;
    }

    public String getTag() {
        return this.tag;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.algorithm);
        dest.writeByteArray(this.digest);
        dest.writeCharSequence(this.label);
        dest.writeLong(this.expiryTimeMillis);
        dest.writeString(this.tag);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BlobHandle)) {
            return false;
        }
        BlobHandle other = (BlobHandle) obj;
        if (this.algorithm.equals(other.algorithm) && Arrays.equals(this.digest, other.digest) && this.label.toString().equals(other.label.toString()) && this.expiryTimeMillis == other.expiryTimeMillis && this.tag.equals(other.tag)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.algorithm, Integer.valueOf(Arrays.hashCode(this.digest)), this.label, Long.valueOf(this.expiryTimeMillis), this.tag);
    }

    public void dump(IndentingPrintWriter fout, boolean dumpFull) {
        if (dumpFull) {
            fout.println("algo: " + this.algorithm);
            StringBuilder append = new StringBuilder().append("digest: ");
            byte[] bArr = this.digest;
            fout.println(append.append(dumpFull ? encodeDigest(bArr) : safeDigest(bArr)).toString());
            fout.println("label: " + ((Object) this.label));
            fout.println("expiryMs: " + this.expiryTimeMillis);
            fout.println("tag: " + this.tag);
            return;
        }
        fout.println(toString());
    }

    public void assertIsValid() {
        Preconditions.checkArgumentIsSupported(SUPPORTED_ALGOS, this.algorithm);
        Preconditions.checkByteArrayNotEmpty(this.digest, CMSAttributeTableGenerator.DIGEST);
        Preconditions.checkStringNotEmpty(this.label, "label must not be null");
        Preconditions.checkArgument(this.label.length() <= 100, "label too long");
        Preconditions.checkArgumentNonnegative(this.expiryTimeMillis, "expiryTimeMillis must not be negative");
        Preconditions.checkStringNotEmpty(this.tag, "tag must not be null");
        Preconditions.checkArgument(this.tag.length() <= 128, "tag too long");
    }

    public String toString() {
        return "BlobHandle {algo:" + this.algorithm + ",digest:" + safeDigest(this.digest) + ",label:" + ((Object) this.label) + ",expiryMs:" + this.expiryTimeMillis + ",tag:" + this.tag + "}";
    }

    public static String safeDigest(byte[] digest) {
        String digestStr = encodeDigest(digest);
        return digestStr.substring(0, 2) + ".." + digestStr.substring(digestStr.length() - 2);
    }

    private static String encodeDigest(byte[] digest) {
        return Base64.encodeToString(digest, 2);
    }

    public boolean isExpired() {
        long j = this.expiryTimeMillis;
        return j != 0 && j < System.currentTimeMillis();
    }

    public void writeToXml(XmlSerializer out) throws IOException {
        XmlUtils.writeStringAttribute(out, XmlTags.ATTR_ALGO, this.algorithm);
        XmlUtils.writeByteArrayAttribute(out, XmlTags.ATTR_DIGEST, this.digest);
        XmlUtils.writeStringAttribute(out, XmlTags.ATTR_LABEL, this.label);
        XmlUtils.writeLongAttribute(out, XmlTags.ATTR_EXPIRY_TIME, this.expiryTimeMillis);
        XmlUtils.writeStringAttribute(out, XmlTags.ATTR_TAG, this.tag);
    }

    public static BlobHandle createFromXml(XmlPullParser in) throws IOException {
        String algo = XmlUtils.readStringAttribute(in, XmlTags.ATTR_ALGO);
        byte[] digest = XmlUtils.readByteArrayAttribute(in, XmlTags.ATTR_DIGEST);
        CharSequence label = XmlUtils.readStringAttribute(in, XmlTags.ATTR_LABEL);
        long expiryTimeMs = XmlUtils.readLongAttribute(in, XmlTags.ATTR_EXPIRY_TIME);
        String tag = XmlUtils.readStringAttribute(in, XmlTags.ATTR_TAG);
        return create(algo, digest, label, expiryTimeMs, tag);
    }
}
