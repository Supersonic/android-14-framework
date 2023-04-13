package com.android.server.timezonedetector;

import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes2.dex */
public final class ArrayMapWithHistory<K, V> {
    public ArrayMap<K, ReferenceWithHistory<V>> mMap;
    public final int mMaxHistorySize;

    public ArrayMapWithHistory(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("maxHistorySize < 1: " + i);
        }
        this.mMaxHistorySize = i;
    }

    public V put(K k, V v) {
        if (this.mMap == null) {
            this.mMap = new ArrayMap<>();
        }
        ReferenceWithHistory<V> referenceWithHistory = this.mMap.get(k);
        if (referenceWithHistory == null) {
            referenceWithHistory = new ReferenceWithHistory<>(this.mMaxHistorySize);
            this.mMap.put(k, referenceWithHistory);
        } else if (referenceWithHistory.getHistoryCount() == 0) {
            Log.w("ArrayMapWithHistory", "History for \"" + k + "\" was unexpectedly empty");
        }
        return referenceWithHistory.set(v);
    }

    public V get(Object obj) {
        ReferenceWithHistory<V> referenceWithHistory;
        ArrayMap<K, ReferenceWithHistory<V>> arrayMap = this.mMap;
        if (arrayMap == null || (referenceWithHistory = arrayMap.get(obj)) == null) {
            return null;
        }
        if (referenceWithHistory.getHistoryCount() == 0) {
            Log.w("ArrayMapWithHistory", "History for \"" + obj + "\" was unexpectedly empty");
        }
        return referenceWithHistory.get();
    }

    public int size() {
        ArrayMap<K, ReferenceWithHistory<V>> arrayMap = this.mMap;
        if (arrayMap == null) {
            return 0;
        }
        return arrayMap.size();
    }

    public K keyAt(int i) {
        ArrayMap<K, ReferenceWithHistory<V>> arrayMap = this.mMap;
        if (arrayMap == null) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        return arrayMap.keyAt(i);
    }

    public V valueAt(int i) {
        ArrayMap<K, ReferenceWithHistory<V>> arrayMap = this.mMap;
        if (arrayMap == null) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        ReferenceWithHistory<V> valueAt = arrayMap.valueAt(i);
        if (valueAt == null || valueAt.getHistoryCount() == 0) {
            Log.w("ArrayMapWithHistory", "valueAt(" + i + ") was unexpectedly null or empty");
            return null;
        }
        return valueAt.get();
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        if (this.mMap == null) {
            indentingPrintWriter.println("{Empty}");
        } else {
            for (int i = 0; i < this.mMap.size(); i++) {
                indentingPrintWriter.println("key idx: " + i + "=" + this.mMap.keyAt(i));
                ReferenceWithHistory<V> valueAt = this.mMap.valueAt(i);
                indentingPrintWriter.println("val idx: " + i + "=" + valueAt);
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("Historic values=[");
                indentingPrintWriter.increaseIndent();
                valueAt.dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("]");
                indentingPrintWriter.decreaseIndent();
            }
        }
        indentingPrintWriter.flush();
    }

    @VisibleForTesting
    public int getHistoryCountForKeyForTests(K k) {
        ReferenceWithHistory<V> referenceWithHistory;
        ArrayMap<K, ReferenceWithHistory<V>> arrayMap = this.mMap;
        if (arrayMap == null || (referenceWithHistory = arrayMap.get(k)) == null) {
            return 0;
        }
        if (referenceWithHistory.getHistoryCount() == 0) {
            Log.w("ArrayMapWithHistory", "getValuesSizeForKeyForTests(\"" + k + "\") was unexpectedly empty");
            return 0;
        }
        return referenceWithHistory.getHistoryCount();
    }

    public String toString() {
        return "ArrayMapWithHistory{mHistorySize=" + this.mMaxHistorySize + ", mMap=" + this.mMap + '}';
    }
}
