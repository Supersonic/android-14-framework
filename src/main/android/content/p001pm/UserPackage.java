package android.content.p001pm;

import android.util.SparseArrayMap;
import com.android.internal.util.ArrayUtils;
import java.util.Objects;
import java.util.Random;
import libcore.util.EmptyArray;
/* renamed from: android.content.pm.UserPackage */
/* loaded from: classes.dex */
public final class UserPackage {
    private static final boolean ENABLE_CACHING = true;
    static final int MAX_NUM_CACHED_ENTRIES_PER_USER = 1000;
    public final String packageName;
    public final int userId;
    private static final Object sCacheLock = new Object();
    private static final SparseArrayMap<String, UserPackage> sCache = new SparseArrayMap<>();
    private static int[] sUserIds = EmptyArray.INT;

    private UserPackage(int userId, String packageName) {
        this.userId = userId;
        this.packageName = packageName;
    }

    public String toString() {
        return "<" + this.userId + ">" + this.packageName;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserPackage) {
            UserPackage other = (UserPackage) obj;
            return this.userId == other.userId && Objects.equals(this.packageName, other.packageName);
        }
        return false;
    }

    public int hashCode() {
        int result = (0 * 31) + this.userId;
        return (result * 31) + this.packageName.hashCode();
    }

    /* renamed from: of */
    public static UserPackage m187of(int userId, String packageName) {
        synchronized (sCacheLock) {
            if (!ArrayUtils.contains(sUserIds, userId)) {
                return new UserPackage(userId, packageName);
            }
            SparseArrayMap<String, UserPackage> sparseArrayMap = sCache;
            UserPackage up = sparseArrayMap.get(userId, packageName);
            if (up == null) {
                maybePurgeRandomEntriesLocked(userId);
                String packageName2 = packageName.intern();
                up = new UserPackage(userId, packageName2);
                sparseArrayMap.add(userId, packageName2, up);
            }
            return up;
        }
    }

    public static void removeFromCache(int userId, String packageName) {
        synchronized (sCacheLock) {
            sCache.delete(userId, packageName);
        }
    }

    public static void setValidUserIds(int[] userIds) {
        int[] userIds2 = (int[]) userIds.clone();
        synchronized (sCacheLock) {
            sUserIds = userIds2;
            for (int u = sCache.numMaps() - 1; u >= 0; u--) {
                SparseArrayMap<String, UserPackage> sparseArrayMap = sCache;
                int userId = sparseArrayMap.keyAt(u);
                if (!ArrayUtils.contains(userIds2, userId)) {
                    sparseArrayMap.deleteAt(u);
                }
            }
        }
    }

    public static int numEntriesForUser(int userId) {
        int numElementsForKey;
        synchronized (sCacheLock) {
            numElementsForKey = sCache.numElementsForKey(userId);
        }
        return numElementsForKey;
    }

    private static void maybePurgeRandomEntriesLocked(int userId) {
        int numCached;
        SparseArrayMap<String, UserPackage> sparseArrayMap = sCache;
        int uIdx = sparseArrayMap.indexOfKey(userId);
        if (uIdx < 0 || (numCached = sparseArrayMap.numElementsForKeyAt(uIdx)) < 1000) {
            return;
        }
        Random rand = new Random();
        int numToPurge = Math.max(1, 10);
        int i = 0;
        while (i < numToPurge && numCached > 0) {
            int numCached2 = numCached - 1;
            int removeIdx = rand.nextInt(numCached);
            sCache.deleteAt(uIdx, removeIdx);
            i++;
            numCached = numCached2;
        }
    }
}
