package com.android.server.p006am;

import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateTimeEvents.BaseTimeEvent;
import java.util.Iterator;
import java.util.LinkedList;
/* renamed from: com.android.server.am.BaseAppStateTimeEvents */
/* loaded from: classes.dex */
public class BaseAppStateTimeEvents<T extends BaseTimeEvent> extends BaseAppStateEvents<T> {
    public BaseAppStateTimeEvents(int i, String str, int i2, String str2, BaseAppStateEvents.MaxTrackingDurationConfig maxTrackingDurationConfig) {
        super(i, str, i2, str2, maxTrackingDurationConfig);
    }

    public BaseAppStateTimeEvents(BaseAppStateTimeEvents baseAppStateTimeEvents) {
        super(baseAppStateTimeEvents);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.p006am.BaseAppStateEvents
    public LinkedList<T> add(LinkedList<T> linkedList, LinkedList<T> linkedList2) {
        if (linkedList2 == null || linkedList2.size() == 0) {
            return linkedList;
        }
        if (linkedList == null || linkedList.size() == 0) {
            return (LinkedList) linkedList2.clone();
        }
        Iterator<T> it = linkedList.iterator();
        Iterator<T> it2 = linkedList2.iterator();
        T next = it.next();
        T next2 = it2.next();
        LinkedList<T> linkedList3 = (LinkedList<T>) new LinkedList();
        long timestamp = next.getTimestamp();
        long timestamp2 = next2.getTimestamp();
        while (true) {
            if (timestamp == Long.MAX_VALUE && timestamp2 == Long.MAX_VALUE) {
                return linkedList3;
            }
            int i = (timestamp > timestamp2 ? 1 : (timestamp == timestamp2 ? 0 : -1));
            if (i == 0) {
                linkedList3.add((BaseTimeEvent) next.clone());
                if (it.hasNext()) {
                    next = it.next();
                    timestamp = next.getTimestamp();
                } else {
                    timestamp = Long.MAX_VALUE;
                }
                if (it2.hasNext()) {
                    next2 = it2.next();
                    timestamp2 = next2.getTimestamp();
                } else {
                    timestamp2 = Long.MAX_VALUE;
                }
            } else if (i < 0) {
                linkedList3.add((BaseTimeEvent) next.clone());
                if (it.hasNext()) {
                    next = it.next();
                    timestamp = next.getTimestamp();
                } else {
                    timestamp = Long.MAX_VALUE;
                }
            } else {
                linkedList3.add((BaseTimeEvent) next2.clone());
                if (it2.hasNext()) {
                    next2 = it2.next();
                    timestamp2 = next2.getTimestamp();
                } else {
                    timestamp2 = Long.MAX_VALUE;
                }
            }
        }
    }

    @Override // com.android.server.p006am.BaseAppStateEvents
    public int getTotalEventsSince(long j, long j2, int i) {
        LinkedList linkedList = this.mEvents[i];
        int i2 = 0;
        if (linkedList != null && linkedList.size() != 0) {
            Iterator it = linkedList.iterator();
            while (it.hasNext()) {
                if (((BaseTimeEvent) it.next()).getTimestamp() >= j) {
                    i2++;
                }
            }
        }
        return i2;
    }

    @Override // com.android.server.p006am.BaseAppStateEvents
    public void trimEvents(long j, int i) {
        LinkedList linkedList = this.mEvents[i];
        if (linkedList == null) {
            return;
        }
        while (linkedList.size() > 0 && ((BaseTimeEvent) linkedList.peek()).getTimestamp() < j) {
            linkedList.pop();
        }
    }

    /* renamed from: com.android.server.am.BaseAppStateTimeEvents$BaseTimeEvent */
    /* loaded from: classes.dex */
    public static class BaseTimeEvent implements Cloneable {
        public long mTimestamp;

        public BaseTimeEvent(long j) {
            this.mTimestamp = j;
        }

        public BaseTimeEvent(BaseTimeEvent baseTimeEvent) {
            this.mTimestamp = baseTimeEvent.mTimestamp;
        }

        public void trimTo(long j) {
            this.mTimestamp = j;
        }

        public long getTimestamp() {
            return this.mTimestamp;
        }

        public Object clone() {
            return new BaseTimeEvent(this);
        }

        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == BaseTimeEvent.class && ((BaseTimeEvent) obj).mTimestamp == this.mTimestamp;
        }

        public int hashCode() {
            return Long.hashCode(this.mTimestamp);
        }
    }
}
