package com.android.server.usage;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class PackagesTokenData {
    public int counter = 1;
    public final SparseArray<ArrayList<String>> tokensToPackagesMap = new SparseArray<>();
    public final ArrayMap<String, ArrayMap<String, Integer>> packagesToTokensMap = new ArrayMap<>();
    public final ArrayMap<String, Long> removedPackagesMap = new ArrayMap<>();
    public final ArraySet<Integer> removedPackageTokens = new ArraySet<>();

    public int getPackageTokenOrAdd(String str, long j) {
        Long l = this.removedPackagesMap.get(str);
        if (l == null || l.longValue() <= j) {
            ArrayMap<String, Integer> arrayMap = this.packagesToTokensMap.get(str);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.packagesToTokensMap.put(str, arrayMap);
            }
            int intValue = arrayMap.getOrDefault(str, -1).intValue();
            if (intValue == -1) {
                int i = this.counter;
                this.counter = i + 1;
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(str);
                arrayMap.put(str, Integer.valueOf(i));
                this.tokensToPackagesMap.put(i, arrayList);
                return i;
            }
            return intValue;
        }
        return -1;
    }

    public int getTokenOrAdd(int i, String str, String str2) {
        if (str.equals(str2)) {
            return 0;
        }
        int intValue = this.packagesToTokensMap.get(str).getOrDefault(str2, -1).intValue();
        if (intValue == -1) {
            int size = this.tokensToPackagesMap.get(i).size();
            this.packagesToTokensMap.get(str).put(str2, Integer.valueOf(size));
            this.tokensToPackagesMap.get(i).add(str2);
            return size;
        }
        return intValue;
    }

    public String getPackageString(int i) {
        ArrayList<String> arrayList = this.tokensToPackagesMap.get(i);
        if (arrayList == null) {
            return null;
        }
        return arrayList.get(0);
    }

    public String getString(int i, int i2) {
        try {
            return this.tokensToPackagesMap.get(i).get(i2);
        } catch (IndexOutOfBoundsException unused) {
            return null;
        } catch (NullPointerException e) {
            Slog.e("PackagesTokenData", "Unable to find tokenized strings for package " + i, e);
            return null;
        }
    }

    public int removePackage(String str, long j) {
        this.removedPackagesMap.put(str, Long.valueOf(j));
        if (this.packagesToTokensMap.containsKey(str)) {
            int intValue = this.packagesToTokensMap.get(str).get(str).intValue();
            this.packagesToTokensMap.remove(str);
            this.tokensToPackagesMap.delete(intValue);
            this.removedPackageTokens.add(Integer.valueOf(intValue));
            return intValue;
        }
        return -1;
    }
}
