package android.app.servertransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
class ObjectPool {
    private static final int MAX_POOL_SIZE = 50;
    private static final Object sPoolSync = new Object();
    private static final Map<Class, ArrayList<? extends ObjectPoolItem>> sPoolMap = new HashMap();

    ObjectPool() {
    }

    public static <T extends ObjectPoolItem> T obtain(Class<T> itemClass) {
        synchronized (sPoolSync) {
            ArrayList<? extends ObjectPoolItem> arrayList = sPoolMap.get(itemClass);
            if (arrayList == null || arrayList.isEmpty()) {
                return null;
            }
            return (T) arrayList.remove(arrayList.size() - 1);
        }
    }

    public static <T extends ObjectPoolItem> void recycle(T item) {
        synchronized (sPoolSync) {
            Map<Class, ArrayList<? extends ObjectPoolItem>> map = sPoolMap;
            ArrayList<? extends ObjectPoolItem> arrayList = map.get(item.getClass());
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                map.put(item.getClass(), arrayList);
            }
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                if (arrayList.get(i) == item) {
                    throw new IllegalStateException("Trying to recycle already recycled item");
                }
            }
            if (size < 50) {
                arrayList.add(item);
            }
        }
    }
}
