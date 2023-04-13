package com.android.server.people.data;

import android.text.format.DateFormat;
import android.util.Range;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class EventIndex {
    public static final String TAG = "EventIndex";
    public final long[] mEventBitmaps;
    public final Injector mInjector;
    public long mLastUpdatedTime;
    public final Object mLock;
    public static final EventIndex EMPTY = new EventIndex();
    public static final List<Function<Long, Range<Long>>> TIME_SLOT_FACTORIES = Collections.unmodifiableList(Arrays.asList(new Function() { // from class: com.android.server.people.data.EventIndex$$ExternalSyntheticLambda0
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            Range createOneDayLongTimeSlot;
            createOneDayLongTimeSlot = EventIndex.createOneDayLongTimeSlot(((Long) obj).longValue());
            return createOneDayLongTimeSlot;
        }
    }, new Function() { // from class: com.android.server.people.data.EventIndex$$ExternalSyntheticLambda1
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            Range createFourHoursLongTimeSlot;
            createFourHoursLongTimeSlot = EventIndex.createFourHoursLongTimeSlot(((Long) obj).longValue());
            return createFourHoursLongTimeSlot;
        }
    }, new Function() { // from class: com.android.server.people.data.EventIndex$$ExternalSyntheticLambda2
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            Range createOneHourLongTimeSlot;
            createOneHourLongTimeSlot = EventIndex.createOneHourLongTimeSlot(((Long) obj).longValue());
            return createOneHourLongTimeSlot;
        }
    }, new Function() { // from class: com.android.server.people.data.EventIndex$$ExternalSyntheticLambda3
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            Range createTwoMinutesLongTimeSlot;
            createTwoMinutesLongTimeSlot = EventIndex.createTwoMinutesLongTimeSlot(((Long) obj).longValue());
            return createTwoMinutesLongTimeSlot;
        }
    }));

    public static EventIndex combine(EventIndex eventIndex, EventIndex eventIndex2) {
        long j = eventIndex.mLastUpdatedTime;
        long j2 = eventIndex2.mLastUpdatedTime;
        EventIndex eventIndex3 = j < j2 ? eventIndex : eventIndex2;
        if (j < j2) {
            eventIndex = eventIndex2;
        }
        EventIndex eventIndex4 = new EventIndex(eventIndex3);
        eventIndex4.updateEventBitmaps(eventIndex.mLastUpdatedTime);
        for (int i = 0; i < 4; i++) {
            long[] jArr = eventIndex4.mEventBitmaps;
            jArr[i] = jArr[i] | eventIndex.mEventBitmaps[i];
        }
        return eventIndex4;
    }

    public EventIndex() {
        this(new Injector());
    }

    public EventIndex(EventIndex eventIndex) {
        this(eventIndex.mInjector, eventIndex.mEventBitmaps, eventIndex.mLastUpdatedTime);
    }

    @VisibleForTesting
    public EventIndex(Injector injector) {
        this(injector, new long[]{0, 0, 0, 0}, injector.currentTimeMillis());
    }

    public EventIndex(Injector injector, long[] jArr, long j) {
        this.mLock = new Object();
        this.mInjector = injector;
        this.mEventBitmaps = Arrays.copyOf(jArr, 4);
        this.mLastUpdatedTime = j;
    }

    public Range<Long> getMostRecentActiveTimeSlot() {
        synchronized (this.mLock) {
            for (int i = 3; i >= 0; i--) {
                if (this.mEventBitmaps[i] != 0) {
                    Range<Long> apply = TIME_SLOT_FACTORIES.get(i).apply(Long.valueOf(this.mLastUpdatedTime));
                    long duration = getDuration(apply) * Long.numberOfTrailingZeros(this.mEventBitmaps[i]);
                    return Range.create(Long.valueOf(apply.getLower().longValue() - duration), Long.valueOf(apply.getUpper().longValue() - duration));
                }
            }
            return null;
        }
    }

    public List<Range<Long>> getActiveTimeSlots() {
        List<Range<Long>> arrayList = new ArrayList<>();
        synchronized (this.mLock) {
            for (int i = 0; i < 4; i++) {
                arrayList = combineTimeSlotLists(arrayList, getActiveTimeSlotsForType(i));
            }
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public boolean isEmpty() {
        synchronized (this.mLock) {
            for (int i = 0; i < 4; i++) {
                if (this.mEventBitmaps[i] != 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public void addEvent(long j) {
        if (EMPTY == this) {
            throw new IllegalStateException("EMPTY instance is immutable");
        }
        synchronized (this.mLock) {
            long currentTimeMillis = this.mInjector.currentTimeMillis();
            updateEventBitmaps(currentTimeMillis);
            for (int i = 0; i < 4; i++) {
                int diffTimeSlots = diffTimeSlots(i, j, currentTimeMillis);
                if (diffTimeSlots < 64) {
                    long[] jArr = this.mEventBitmaps;
                    jArr[i] = jArr[i] | (1 << diffTimeSlots);
                }
            }
        }
    }

    public String toString() {
        return "EventIndex {perDayEventBitmap=0b" + Long.toBinaryString(this.mEventBitmaps[0]) + ", perFourHoursEventBitmap=0b" + Long.toBinaryString(this.mEventBitmaps[1]) + ", perHourEventBitmap=0b" + Long.toBinaryString(this.mEventBitmaps[2]) + ", perTwoMinutesEventBitmap=0b" + Long.toBinaryString(this.mEventBitmaps[3]) + ", lastUpdatedTime=" + DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mLastUpdatedTime) + "}";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof EventIndex) {
            EventIndex eventIndex = (EventIndex) obj;
            return this.mLastUpdatedTime == eventIndex.mLastUpdatedTime && Arrays.equals(this.mEventBitmaps, eventIndex.mEventBitmaps);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mLastUpdatedTime), Integer.valueOf(Arrays.hashCode(this.mEventBitmaps)));
    }

    public synchronized void writeToProto(ProtoOutputStream protoOutputStream) {
        for (long j : this.mEventBitmaps) {
            protoOutputStream.write(2211908157441L, j);
        }
        protoOutputStream.write(1112396529666L, this.mLastUpdatedTime);
    }

    public final void updateEventBitmaps(long j) {
        for (int i = 0; i < 4; i++) {
            int diffTimeSlots = diffTimeSlots(i, this.mLastUpdatedTime, j);
            if (diffTimeSlots < 64) {
                long[] jArr = this.mEventBitmaps;
                jArr[i] = jArr[i] << diffTimeSlots;
            } else {
                this.mEventBitmaps[i] = 0;
            }
        }
        long[] jArr2 = this.mEventBitmaps;
        long j2 = jArr2[0] << 1;
        jArr2[0] = j2;
        jArr2[0] = j2 >>> 1;
        this.mLastUpdatedTime = j;
    }

    public static EventIndex readFromProto(ProtoInputStream protoInputStream) throws IOException {
        long[] jArr = new long[4];
        int i = 0;
        long j = 0;
        while (protoInputStream.nextField() != -1) {
            int fieldNumber = protoInputStream.getFieldNumber();
            if (fieldNumber == 1) {
                jArr[i] = protoInputStream.readLong(2211908157441L);
                i++;
            } else if (fieldNumber == 2) {
                j = protoInputStream.readLong(1112396529666L);
            } else {
                String str = TAG;
                Slog.e(str, "Could not read undefined field: " + protoInputStream.getFieldNumber());
            }
        }
        return new EventIndex(new Injector(), jArr, j);
    }

    public static LocalDateTime toLocalDateTime(long j) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(j), TimeZone.getDefault().toZoneId());
    }

    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getDuration(Range<Long> range) {
        return range.getUpper().longValue() - range.getLower().longValue();
    }

    public static int diffTimeSlots(int i, long j, long j2) {
        Function<Long, Range<Long>> function = TIME_SLOT_FACTORIES.get(i);
        Range<Long> apply = function.apply(Long.valueOf(j));
        return (int) ((function.apply(Long.valueOf(j2)).getLower().longValue() - apply.getLower().longValue()) / getDuration(apply));
    }

    public final List<Range<Long>> getActiveTimeSlotsForType(int i) {
        long j = this.mEventBitmaps[i];
        Range<Long> apply = TIME_SLOT_FACTORIES.get(i).apply(Long.valueOf(this.mLastUpdatedTime));
        long longValue = apply.getLower().longValue();
        long duration = getDuration(apply);
        ArrayList arrayList = new ArrayList();
        while (j != 0) {
            int numberOfTrailingZeros = Long.numberOfTrailingZeros(j);
            if (numberOfTrailingZeros > 0) {
                longValue -= numberOfTrailingZeros * duration;
                j >>>= numberOfTrailingZeros;
            }
            if (j != 0) {
                arrayList.add(Range.create(Long.valueOf(longValue), Long.valueOf(longValue + duration)));
                longValue -= duration;
                j >>>= 1;
            }
        }
        return arrayList;
    }

    public static List<Range<Long>> combineTimeSlotLists(List<Range<Long>> list, List<Range<Long>> list2) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int i2 = 0;
        while (i < list.size() && i2 < list2.size()) {
            Range<Long> range = list.get(i);
            Range<Long> range2 = list2.get(i2);
            if (range.contains(range2)) {
                arrayList.add(range2);
                i++;
            } else if (range.getLower().longValue() < range2.getLower().longValue()) {
                arrayList.add(range2);
            } else {
                arrayList.add(range);
                i++;
            }
            i2++;
        }
        if (i < list.size()) {
            arrayList.addAll(list.subList(i, list.size()));
        } else if (i2 < list2.size()) {
            arrayList.addAll(list2.subList(i2, list2.size()));
        }
        return arrayList;
    }

    public static Range<Long> createOneDayLongTimeSlot(long j) {
        LocalDateTime truncatedTo = toLocalDateTime(j).truncatedTo(ChronoUnit.DAYS);
        return Range.create(Long.valueOf(toEpochMilli(truncatedTo)), Long.valueOf(toEpochMilli(truncatedTo.plusDays(1L))));
    }

    public static Range<Long> createFourHoursLongTimeSlot(long j) {
        LocalDateTime minusHours = toLocalDateTime(j).truncatedTo(ChronoUnit.HOURS).minusHours(toLocalDateTime(j).getHour() % 4);
        return Range.create(Long.valueOf(toEpochMilli(minusHours)), Long.valueOf(toEpochMilli(minusHours.plusHours(4L))));
    }

    public static Range<Long> createOneHourLongTimeSlot(long j) {
        LocalDateTime truncatedTo = toLocalDateTime(j).truncatedTo(ChronoUnit.HOURS);
        return Range.create(Long.valueOf(toEpochMilli(truncatedTo)), Long.valueOf(toEpochMilli(truncatedTo.plusHours(1L))));
    }

    public static Range<Long> createTwoMinutesLongTimeSlot(long j) {
        LocalDateTime minusMinutes = toLocalDateTime(j).truncatedTo(ChronoUnit.MINUTES).minusMinutes(toLocalDateTime(j).getMinute() % 2);
        return Range.create(Long.valueOf(toEpochMilli(minusMinutes)), Long.valueOf(toEpochMilli(minusMinutes.plusMinutes(2L))));
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }
}
