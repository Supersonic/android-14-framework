package android.content.p001pm;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
@SystemApi
/* renamed from: android.content.pm.InstantAppResolveInfo */
/* loaded from: classes.dex */
public final class InstantAppResolveInfo implements Parcelable {
    private static final String SHA_ALGORITHM = "SHA-256";
    private final InstantAppDigest mDigest;
    private final Bundle mExtras;
    private final List<InstantAppIntentFilter> mFilters;
    private final String mPackageName;
    private final boolean mShouldLetInstallerDecide;
    private final long mVersionCode;
    private static final byte[] EMPTY_DIGEST = new byte[0];
    public static final Parcelable.Creator<InstantAppResolveInfo> CREATOR = new Parcelable.Creator<InstantAppResolveInfo>() { // from class: android.content.pm.InstantAppResolveInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstantAppResolveInfo createFromParcel(Parcel in) {
            return new InstantAppResolveInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstantAppResolveInfo[] newArray(int size) {
            return new InstantAppResolveInfo[size];
        }
    };

    public InstantAppResolveInfo(InstantAppDigest digest, String packageName, List<InstantAppIntentFilter> filters, int versionCode) {
        this(digest, packageName, filters, versionCode, null);
    }

    public InstantAppResolveInfo(InstantAppDigest digest, String packageName, List<InstantAppIntentFilter> filters, long versionCode, Bundle extras) {
        this(digest, packageName, filters, versionCode, extras, false);
    }

    public InstantAppResolveInfo(String hostName, String packageName, List<InstantAppIntentFilter> filters) {
        this(new InstantAppDigest(hostName), packageName, filters, -1L, null);
    }

    public InstantAppResolveInfo(Bundle extras) {
        this(InstantAppDigest.UNDEFINED, null, null, -1L, extras, true);
    }

    private InstantAppResolveInfo(InstantAppDigest digest, String packageName, List<InstantAppIntentFilter> filters, long versionCode, Bundle extras, boolean shouldLetInstallerDecide) {
        if ((packageName == null && filters != null && filters.size() != 0) || (packageName != null && (filters == null || filters.size() == 0))) {
            throw new IllegalArgumentException();
        }
        this.mDigest = digest;
        if (filters != null) {
            ArrayList arrayList = new ArrayList(filters.size());
            this.mFilters = arrayList;
            arrayList.addAll(filters);
        } else {
            this.mFilters = null;
        }
        this.mPackageName = packageName;
        this.mVersionCode = versionCode;
        this.mExtras = extras;
        this.mShouldLetInstallerDecide = shouldLetInstallerDecide;
    }

    InstantAppResolveInfo(Parcel in) {
        boolean readBoolean = in.readBoolean();
        this.mShouldLetInstallerDecide = readBoolean;
        this.mExtras = in.readBundle();
        if (!readBoolean) {
            this.mDigest = (InstantAppDigest) in.readParcelable(null, InstantAppDigest.class);
            this.mPackageName = in.readString();
            ArrayList arrayList = new ArrayList();
            this.mFilters = arrayList;
            in.readTypedList(arrayList, InstantAppIntentFilter.CREATOR);
            this.mVersionCode = in.readLong();
            return;
        }
        this.mDigest = InstantAppDigest.UNDEFINED;
        this.mPackageName = null;
        this.mFilters = Collections.emptyList();
        this.mVersionCode = -1L;
    }

    public boolean shouldLetInstallerDecide() {
        return this.mShouldLetInstallerDecide;
    }

    public byte[] getDigestBytes() {
        return this.mDigest.mDigestBytes.length > 0 ? this.mDigest.getDigestBytes()[0] : EMPTY_DIGEST;
    }

    public int getDigestPrefix() {
        return this.mDigest.getDigestPrefix()[0];
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public List<InstantAppIntentFilter> getIntentFilters() {
        return this.mFilters;
    }

    @Deprecated
    public int getVersionCode() {
        return (int) (this.mVersionCode & (-1));
    }

    public long getLongVersionCode() {
        return this.mVersionCode;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeBoolean(this.mShouldLetInstallerDecide);
        out.writeBundle(this.mExtras);
        if (this.mShouldLetInstallerDecide) {
            return;
        }
        out.writeParcelable(this.mDigest, flags);
        out.writeString(this.mPackageName);
        out.writeTypedList(this.mFilters);
        out.writeLong(this.mVersionCode);
    }

    @SystemApi
    /* renamed from: android.content.pm.InstantAppResolveInfo$InstantAppDigest */
    /* loaded from: classes.dex */
    public static final class InstantAppDigest implements Parcelable {
        public static final Parcelable.Creator<InstantAppDigest> CREATOR;
        static final int DIGEST_MASK = -4096;
        public static final InstantAppDigest UNDEFINED = new InstantAppDigest(new byte[0], new int[0]);
        private static Random sRandom;
        private final byte[][] mDigestBytes;
        private final int[] mDigestPrefix;
        private int[] mDigestPrefixSecure;

        static {
            sRandom = null;
            try {
                sRandom = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                sRandom = new Random();
            }
            CREATOR = new Parcelable.Creator<InstantAppDigest>() { // from class: android.content.pm.InstantAppResolveInfo.InstantAppDigest.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.p008os.Parcelable.Creator
                public InstantAppDigest createFromParcel(Parcel in) {
                    if (in.readBoolean()) {
                        return InstantAppDigest.UNDEFINED;
                    }
                    return new InstantAppDigest(in);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.p008os.Parcelable.Creator
                public InstantAppDigest[] newArray(int size) {
                    return new InstantAppDigest[size];
                }
            };
        }

        public InstantAppDigest(String hostName) {
            this(hostName, -1);
        }

        public InstantAppDigest(String hostName, int maxDigests) {
            if (hostName == null) {
                throw new IllegalArgumentException();
            }
            byte[][] generateDigest = generateDigest(hostName.toLowerCase(Locale.ENGLISH), maxDigests);
            this.mDigestBytes = generateDigest;
            this.mDigestPrefix = new int[generateDigest.length];
            int i = 0;
            while (true) {
                byte[][] bArr = this.mDigestBytes;
                if (i < bArr.length) {
                    int[] iArr = this.mDigestPrefix;
                    byte[] bArr2 = bArr[i];
                    iArr[i] = (((bArr2[3] & 255) << 0) | ((bArr2[0] & 255) << 24) | ((bArr2[1] & 255) << 16) | ((bArr2[2] & 255) << 8)) & DIGEST_MASK;
                    i++;
                } else {
                    return;
                }
            }
        }

        private InstantAppDigest(byte[][] digestBytes, int[] prefix) {
            this.mDigestPrefix = prefix;
            this.mDigestBytes = digestBytes;
        }

        private static byte[][] generateDigest(String hostName, int maxDigests) {
            ArrayList<byte[]> digests = new ArrayList<>();
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                if (maxDigests <= 0) {
                    byte[] hostBytes = hostName.getBytes();
                    digests.add(digest.digest(hostBytes));
                } else {
                    int prevDot = hostName.lastIndexOf(46, hostName.lastIndexOf(46) - 1);
                    if (prevDot < 0) {
                        digests.add(digest.digest(hostName.getBytes()));
                    } else {
                        byte[] hostBytes2 = hostName.substring(prevDot + 1, hostName.length()).getBytes();
                        digests.add(digest.digest(hostBytes2));
                        for (int digestCount = 1; prevDot >= 0 && digestCount < maxDigests; digestCount++) {
                            prevDot = hostName.lastIndexOf(46, prevDot - 1);
                            byte[] hostBytes3 = hostName.substring(prevDot + 1, hostName.length()).getBytes();
                            digests.add(digest.digest(hostBytes3));
                        }
                    }
                }
                return (byte[][]) digests.toArray(new byte[digests.size()]);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("could not find digest algorithm");
            }
        }

        InstantAppDigest(Parcel in) {
            int digestCount = in.readInt();
            if (digestCount == -1) {
                this.mDigestBytes = null;
            } else {
                this.mDigestBytes = new byte[digestCount];
                for (int i = 0; i < digestCount; i++) {
                    this.mDigestBytes[i] = in.createByteArray();
                }
            }
            this.mDigestPrefix = in.createIntArray();
            this.mDigestPrefixSecure = in.createIntArray();
        }

        public byte[][] getDigestBytes() {
            return this.mDigestBytes;
        }

        public int[] getDigestPrefix() {
            return this.mDigestPrefix;
        }

        public int[] getDigestPrefixSecure() {
            if (this == UNDEFINED) {
                return getDigestPrefix();
            }
            if (this.mDigestPrefixSecure == null) {
                int realSize = getDigestPrefix().length;
                int manufacturedSize = realSize + 10 + sRandom.nextInt(10);
                this.mDigestPrefixSecure = Arrays.copyOf(getDigestPrefix(), manufacturedSize);
                for (int i = realSize; i < manufacturedSize; i++) {
                    this.mDigestPrefixSecure[i] = sRandom.nextInt() & DIGEST_MASK;
                }
                Arrays.sort(this.mDigestPrefixSecure);
            }
            return this.mDigestPrefixSecure;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            boolean isUndefined = this == UNDEFINED;
            out.writeBoolean(isUndefined);
            if (isUndefined) {
                return;
            }
            byte[][] bArr = this.mDigestBytes;
            if (bArr == null) {
                out.writeInt(-1);
            } else {
                out.writeInt(bArr.length);
                int i = 0;
                while (true) {
                    byte[][] bArr2 = this.mDigestBytes;
                    if (i >= bArr2.length) {
                        break;
                    }
                    out.writeByteArray(bArr2[i]);
                    i++;
                }
            }
            out.writeIntArray(this.mDigestPrefix);
            out.writeIntArray(this.mDigestPrefixSecure);
        }
    }
}
