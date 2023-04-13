package android.graphics;

import android.p008os.Parcel;
import android.p008os.Process;
import android.util.ArrayMap;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class LeakyTypefaceStorage {
    private static final Object sLock = new Object();
    private static final ArrayList<Typeface> sStorage = new ArrayList<>();
    private static final ArrayMap<Typeface, Integer> sTypefaceMap = new ArrayMap<>();

    public static void writeTypefaceToParcel(Typeface typeface, Parcel parcel) {
        int id;
        parcel.writeInt(Process.myPid());
        synchronized (sLock) {
            ArrayMap<Typeface, Integer> arrayMap = sTypefaceMap;
            Integer i = arrayMap.get(typeface);
            if (i != null) {
                id = i.intValue();
            } else {
                ArrayList<Typeface> arrayList = sStorage;
                int id2 = arrayList.size();
                arrayList.add(typeface);
                arrayMap.put(typeface, Integer.valueOf(id2));
                id = id2;
            }
            parcel.writeInt(id);
        }
    }

    public static Typeface readTypefaceFromParcel(Parcel parcel) {
        Typeface typeface;
        int pid = parcel.readInt();
        int typefaceId = parcel.readInt();
        if (pid != Process.myPid()) {
            return null;
        }
        synchronized (sLock) {
            typeface = sStorage.get(typefaceId);
        }
        return typeface;
    }
}
