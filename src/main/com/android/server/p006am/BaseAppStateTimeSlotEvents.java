package com.android.server.p006am;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p006am.BaseAppStateEvents;
import java.util.Iterator;
import java.util.LinkedList;
/* renamed from: com.android.server.am.BaseAppStateTimeSlotEvents */
/* loaded from: classes.dex */
public class BaseAppStateTimeSlotEvents extends BaseAppStateEvents<Integer> {
    public long[] mCurSlotStartTime;
    public final long mTimeSlotSize;

    @Override // com.android.server.p006am.BaseAppStateEvents
    public LinkedList<Integer> add(LinkedList<Integer> linkedList, LinkedList<Integer> linkedList2) {
        return null;
    }

    public BaseAppStateTimeSlotEvents(int i, String str, int i2, long j, String str2, BaseAppStateEvents.MaxTrackingDurationConfig maxTrackingDurationConfig) {
        super(i, str, i2, str2, maxTrackingDurationConfig);
        this.mTimeSlotSize = j;
        this.mCurSlotStartTime = new long[i2];
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.p006am.BaseAppStateEvents
    public void add(BaseAppStateEvents baseAppStateEvents) {
        int i;
        if (baseAppStateEvents == null || !(baseAppStateEvents instanceof BaseAppStateTimeSlotEvents)) {
            return;
        }
        BaseAppStateTimeSlotEvents baseAppStateTimeSlotEvents = (BaseAppStateTimeSlotEvents) baseAppStateEvents;
        if (this.mEvents.length != baseAppStateTimeSlotEvents.mEvents.length) {
            return;
        }
        int i2 = 0;
        while (i2 < this.mEvents.length) {
            LinkedList linkedList = baseAppStateTimeSlotEvents.mEvents[i2];
            if (linkedList == null || linkedList.size() == 0) {
                i = i2;
            } else {
                LinkedList linkedList2 = this.mEvents[i2];
                if (linkedList2 == null || linkedList2.size() == 0) {
                    i = i2;
                    this.mEvents[i] = new LinkedList(linkedList);
                    this.mCurSlotStartTime[i] = baseAppStateTimeSlotEvents.mCurSlotStartTime[i];
                } else {
                    LinkedList linkedList3 = new LinkedList();
                    Iterator it = linkedList2.iterator();
                    Iterator it2 = linkedList.iterator();
                    long j = this.mCurSlotStartTime[i2];
                    long j2 = baseAppStateTimeSlotEvents.mCurSlotStartTime[i2];
                    int i3 = i2;
                    long size = j - (this.mTimeSlotSize * (linkedList2.size() - 1));
                    long size2 = j2 - (this.mTimeSlotSize * (linkedList.size() - 1));
                    long max = Math.max(j, j2);
                    long min = Math.min(size, size2);
                    while (min <= max) {
                        linkedList3.add(Integer.valueOf(((min < size || min > j) ? 0 : ((Integer) it.next()).intValue()) + ((min < size2 || min > j2) ? 0 : ((Integer) it2.next()).intValue())));
                        min += this.mTimeSlotSize;
                        size = size;
                    }
                    this.mEvents[i3] = linkedList3;
                    if (j < j2) {
                        this.mCurSlotStartTime[i3] = baseAppStateTimeSlotEvents.mCurSlotStartTime[i3];
                    }
                    i = i3;
                    trimEvents(getEarliest(this.mCurSlotStartTime[i3]), i);
                }
            }
            i2 = i + 1;
        }
    }

    @Override // com.android.server.p006am.BaseAppStateEvents
    public int getTotalEventsSince(long j, long j2, int i) {
        LinkedList linkedList = this.mEvents[i];
        int i2 = 0;
        if (linkedList != null && linkedList.size() != 0) {
            long slotStartTime = getSlotStartTime(j);
            if (slotStartTime > this.mCurSlotStartTime[i]) {
                return 0;
            }
            long min = Math.min(getSlotStartTime(j2), this.mCurSlotStartTime[i]);
            Iterator descendingIterator = linkedList.descendingIterator();
            long j3 = this.mCurSlotStartTime[i];
            while (j3 >= slotStartTime && descendingIterator.hasNext()) {
                int intValue = ((Integer) descendingIterator.next()).intValue();
                if (j3 <= min) {
                    i2 += intValue;
                }
                j3 -= this.mTimeSlotSize;
            }
        }
        return i2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addEvent(long j, int i) {
        long slotStartTime = getSlotStartTime(j);
        LinkedList linkedList = this.mEvents[i];
        if (linkedList == null) {
            linkedList = new LinkedList();
            this.mEvents[i] = linkedList;
        }
        if (linkedList.size() == 0) {
            linkedList.add(1);
        } else {
            long j2 = this.mCurSlotStartTime[i];
            while (j2 < slotStartTime) {
                linkedList.add(0);
                j2 += this.mTimeSlotSize;
            }
            linkedList.offerLast(Integer.valueOf(((Integer) linkedList.pollLast()).intValue() + 1));
        }
        this.mCurSlotStartTime[i] = slotStartTime;
        trimEvents(getEarliest(j), i);
    }

    @Override // com.android.server.p006am.BaseAppStateEvents
    public void trimEvents(long j, int i) {
        LinkedList linkedList = this.mEvents[i];
        if (linkedList == null || linkedList.size() == 0) {
            return;
        }
        long slotStartTime = getSlotStartTime(j);
        long size = this.mCurSlotStartTime[i] - (this.mTimeSlotSize * (linkedList.size() - 1));
        while (size < slotStartTime && linkedList.size() > 0) {
            linkedList.pop();
            size += this.mTimeSlotSize;
        }
    }

    public long getSlotStartTime(long j) {
        return j - (j % this.mTimeSlotSize);
    }

    @VisibleForTesting
    public long getCurrentSlotStartTime(int i) {
        return this.mCurSlotStartTime[i];
    }
}
