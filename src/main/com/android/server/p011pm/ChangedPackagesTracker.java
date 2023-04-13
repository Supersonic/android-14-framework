package com.android.server.p011pm;

import android.content.pm.ChangedPackages;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.pm.ChangedPackagesTracker */
/* loaded from: classes2.dex */
public class ChangedPackagesTracker {
    @GuardedBy({"mLock"})
    public int mChangedPackagesSequenceNumber;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<SparseArray<String>> mUserIdToSequenceToPackage = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final SparseArray<Map<String, Integer>> mChangedPackagesSequenceNumbers = new SparseArray<>();

    public ChangedPackages getChangedPackages(int i, int i2) {
        synchronized (this.mLock) {
            ChangedPackages changedPackages = null;
            if (i >= this.mChangedPackagesSequenceNumber) {
                return null;
            }
            SparseArray<String> sparseArray = this.mUserIdToSequenceToPackage.get(i2);
            if (sparseArray == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList(this.mChangedPackagesSequenceNumber - i);
            while (i < this.mChangedPackagesSequenceNumber) {
                String str = sparseArray.get(i);
                if (str != null) {
                    arrayList.add(str);
                }
                i++;
            }
            if (!arrayList.isEmpty()) {
                changedPackages = new ChangedPackages(this.mChangedPackagesSequenceNumber, arrayList);
            }
            return changedPackages;
        }
    }

    public int getSequenceNumber() {
        return this.mChangedPackagesSequenceNumber;
    }

    public void iterateAll(BiConsumer<Integer, SparseArray<SparseArray<String>>> biConsumer) {
        synchronized (this.mLock) {
            biConsumer.accept(Integer.valueOf(this.mChangedPackagesSequenceNumber), this.mUserIdToSequenceToPackage);
        }
    }

    public void updateSequenceNumber(String str, int[] iArr) {
        synchronized (this.mLock) {
            for (int length = iArr.length - 1; length >= 0; length--) {
                int i = iArr[length];
                SparseArray<String> sparseArray = this.mUserIdToSequenceToPackage.get(i);
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>();
                    this.mUserIdToSequenceToPackage.put(i, sparseArray);
                }
                Map<String, Integer> map = this.mChangedPackagesSequenceNumbers.get(i);
                if (map == null) {
                    map = new HashMap<>();
                    this.mChangedPackagesSequenceNumbers.put(i, map);
                }
                Integer num = map.get(str);
                if (num != null) {
                    sparseArray.remove(num.intValue());
                }
                sparseArray.put(this.mChangedPackagesSequenceNumber, str);
                map.put(str, Integer.valueOf(this.mChangedPackagesSequenceNumber));
            }
            this.mChangedPackagesSequenceNumber++;
        }
    }
}
