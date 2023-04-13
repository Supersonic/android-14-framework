package com.android.server.timezonedetector;

import android.os.SystemClock;
import android.os.TimestampedValue;
import android.util.IndentingPrintWriter;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Iterator;
/* loaded from: classes2.dex */
public final class ReferenceWithHistory<V> {
    public final int mMaxHistorySize;
    public int mSetCount;
    public ArrayDeque<TimestampedValue<V>> mValues;

    public ReferenceWithHistory(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("maxHistorySize < 1: " + i);
        }
        this.mMaxHistorySize = i;
    }

    public V get() {
        ArrayDeque<TimestampedValue<V>> arrayDeque = this.mValues;
        if (arrayDeque == null || arrayDeque.isEmpty()) {
            return null;
        }
        return (V) this.mValues.getFirst().getValue();
    }

    public V set(V v) {
        if (this.mValues == null) {
            this.mValues = new ArrayDeque<>(this.mMaxHistorySize);
        }
        if (this.mValues.size() >= this.mMaxHistorySize) {
            this.mValues.removeLast();
        }
        V v2 = get();
        this.mValues.addFirst(new TimestampedValue<>(SystemClock.elapsedRealtime(), v));
        this.mSetCount++;
        return v2;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        ArrayDeque<TimestampedValue<V>> arrayDeque = this.mValues;
        if (arrayDeque == null) {
            indentingPrintWriter.println("{Empty}");
        } else {
            int size = this.mSetCount - arrayDeque.size();
            Iterator<TimestampedValue<V>> descendingIterator = this.mValues.descendingIterator();
            while (descendingIterator.hasNext()) {
                TimestampedValue<V> next = descendingIterator.next();
                indentingPrintWriter.print(size);
                indentingPrintWriter.print("@");
                indentingPrintWriter.print(Duration.ofMillis(next.getReferenceTimeMillis()).toString());
                indentingPrintWriter.print(": ");
                indentingPrintWriter.println(next.getValue());
                size++;
            }
        }
        indentingPrintWriter.flush();
    }

    public int getHistoryCount() {
        ArrayDeque<TimestampedValue<V>> arrayDeque = this.mValues;
        if (arrayDeque == null) {
            return 0;
        }
        return arrayDeque.size();
    }

    public String toString() {
        return String.valueOf(get());
    }
}
