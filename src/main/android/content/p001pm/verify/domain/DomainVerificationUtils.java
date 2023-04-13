package android.content.p001pm.verify.domain;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.util.ArraySet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* renamed from: android.content.pm.verify.domain.DomainVerificationUtils */
/* loaded from: classes.dex */
public class DomainVerificationUtils {
    private static final int STRINGS_TARGET_BYTE_SIZE = IBinder.getSuggestedMaxIpcSizeBytes() / 2;

    public static void writeHostMap(Parcel dest, Map<String, ?> map) {
        boolean targetSizeExceeded = false;
        int totalSize = dest.dataSize();
        Iterator<String> it = map.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String host = it.next();
            totalSize += estimatedByteSizeOf(host);
            if (totalSize > STRINGS_TARGET_BYTE_SIZE) {
                targetSizeExceeded = true;
                break;
            }
        }
        dest.writeBoolean(targetSizeExceeded);
        if (!targetSizeExceeded) {
            dest.writeMap(map);
            return;
        }
        Parcel data = Parcel.obtain();
        try {
            data.writeMap(map);
            dest.writeBlob(data.marshall());
        } finally {
            data.recycle();
        }
    }

    public static <T extends Map> T readHostMap(Parcel in, T map, ClassLoader classLoader) {
        boolean targetSizeExceeded = in.readBoolean();
        if (!targetSizeExceeded) {
            in.readMap(map, classLoader);
            return map;
        }
        Parcel data = Parcel.obtain();
        try {
            byte[] blob = in.readBlob();
            data.unmarshall(blob, 0, blob.length);
            data.setDataPosition(0);
            data.readMap(map, classLoader);
            return map;
        } finally {
            data.recycle();
        }
    }

    public static void writeHostSet(Parcel dest, Set<String> set) {
        boolean targetSizeExceeded = false;
        int totalSize = dest.dataSize();
        Iterator<String> it = set.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String host = it.next();
            totalSize += estimatedByteSizeOf(host);
            if (totalSize > STRINGS_TARGET_BYTE_SIZE) {
                targetSizeExceeded = true;
                break;
            }
        }
        dest.writeBoolean(targetSizeExceeded);
        if (!targetSizeExceeded) {
            writeSet(dest, set);
            return;
        }
        Parcel data = Parcel.obtain();
        try {
            writeSet(data, set);
            dest.writeBlob(data.marshall());
        } finally {
            data.recycle();
        }
    }

    public static Set<String> readHostSet(Parcel in) {
        boolean targetSizeExceeded = in.readBoolean();
        if (!targetSizeExceeded) {
            return readSet(in);
        }
        Parcel data = Parcel.obtain();
        try {
            byte[] blob = in.readBlob();
            data.unmarshall(blob, 0, blob.length);
            data.setDataPosition(0);
            return readSet(data);
        } finally {
            data.recycle();
        }
    }

    private static void writeSet(Parcel dest, Set<String> set) {
        if (set == null) {
            dest.writeInt(-1);
            return;
        }
        dest.writeInt(set.size());
        for (String string : set) {
            dest.writeString(string);
        }
    }

    private static Set<String> readSet(Parcel in) {
        int size = in.readInt();
        if (size == -1) {
            return Collections.emptySet();
        }
        ArraySet<String> set = new ArraySet<>(size);
        for (int count = 0; count < size; count++) {
            set.add(in.readString());
        }
        return set;
    }

    public static int estimatedByteSizeOf(String string) {
        return (string.length() * 2) + 12;
    }
}
