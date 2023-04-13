package android.content.p001pm;

import android.p008os.Parcel;
import java.util.ArrayList;
import java.util.HashMap;
/* renamed from: android.content.pm.PackageParserCacheHelper */
/* loaded from: classes.dex */
public class PackageParserCacheHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "PackageParserCacheHelper";

    private PackageParserCacheHelper() {
    }

    /* renamed from: android.content.pm.PackageParserCacheHelper$ReadHelper */
    /* loaded from: classes.dex */
    public static class ReadHelper extends Parcel.ReadWriteHelper {
        private final Parcel mParcel;
        private final ArrayList<String> mStrings = new ArrayList<>();

        public ReadHelper(Parcel p) {
            this.mParcel = p;
        }

        public void startAndInstall() {
            this.mStrings.clear();
            int poolPosition = this.mParcel.readInt();
            if (poolPosition < 0) {
                throw new IllegalStateException("Invalid string pool position: " + poolPosition);
            }
            int startPosition = this.mParcel.dataPosition();
            this.mParcel.setDataPosition(poolPosition);
            this.mParcel.readStringList(this.mStrings);
            this.mParcel.setDataPosition(startPosition);
            this.mParcel.setReadWriteHelper(this);
        }

        public String readString(Parcel p) {
            return this.mStrings.get(p.readInt());
        }

        @Override // android.p008os.Parcel.ReadWriteHelper
        public String readString8(Parcel p) {
            return readString(p);
        }

        @Override // android.p008os.Parcel.ReadWriteHelper
        public String readString16(Parcel p) {
            return readString(p);
        }
    }

    /* renamed from: android.content.pm.PackageParserCacheHelper$WriteHelper */
    /* loaded from: classes.dex */
    public static class WriteHelper extends Parcel.ReadWriteHelper {
        private final Parcel mParcel;
        private final int mStartPos;
        private final ArrayList<String> mStrings = new ArrayList<>();
        private final HashMap<String, Integer> mIndexes = new HashMap<>();

        public WriteHelper(Parcel p) {
            this.mParcel = p;
            this.mStartPos = p.dataPosition();
            p.writeInt(0);
            p.setReadWriteHelper(this);
        }

        public void writeString(Parcel p, String s) {
            Integer cur = this.mIndexes.get(s);
            if (cur != null) {
                p.writeInt(cur.intValue());
                return;
            }
            int index = this.mStrings.size();
            this.mIndexes.put(s, Integer.valueOf(index));
            this.mStrings.add(s);
            p.writeInt(index);
        }

        @Override // android.p008os.Parcel.ReadWriteHelper
        public void writeString8(Parcel p, String s) {
            writeString(p, s);
        }

        @Override // android.p008os.Parcel.ReadWriteHelper
        public void writeString16(Parcel p, String s) {
            writeString(p, s);
        }

        public void finishAndUninstall() {
            this.mParcel.setReadWriteHelper(null);
            int poolPosition = this.mParcel.dataPosition();
            this.mParcel.writeStringList(this.mStrings);
            this.mParcel.setDataPosition(this.mStartPos);
            this.mParcel.writeInt(poolPosition);
            Parcel parcel = this.mParcel;
            parcel.setDataPosition(parcel.dataSize());
        }
    }
}
