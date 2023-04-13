package com.android.server.p006am;

import android.os.SystemClock;
import android.os.UserHandle;
import android.util.TimeUtils;
import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateTimeEvents;
import com.android.server.p006am.BaseAppStateTimeEvents.BaseTimeEvent;
import java.util.Iterator;
import java.util.LinkedList;
/* renamed from: com.android.server.am.BaseAppStateDurations */
/* loaded from: classes.dex */
public abstract class BaseAppStateDurations<T extends BaseAppStateTimeEvents.BaseTimeEvent> extends BaseAppStateTimeEvents<T> {
    public BaseAppStateDurations(int i, String str, int i2, String str2, BaseAppStateEvents.MaxTrackingDurationConfig maxTrackingDurationConfig) {
        super(i, str, i2, str2, maxTrackingDurationConfig);
    }

    public BaseAppStateDurations(BaseAppStateDurations baseAppStateDurations) {
        super(baseAppStateDurations);
    }

    public void addEvent(boolean z, T t, int i) {
        Object[] objArr = this.mEvents;
        if (objArr[i] == null) {
            objArr[i] = new LinkedList();
        }
        LinkedList linkedList = this.mEvents[i];
        linkedList.size();
        if (z != isActive(i)) {
            linkedList.add(t);
        }
        trimEvents(getEarliest(t.getTimestamp()), i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.p006am.BaseAppStateTimeEvents, com.android.server.p006am.BaseAppStateEvents
    public void trimEvents(long j, int i) {
        trimEvents(j, (LinkedList) this.mEvents[i]);
    }

    public void trimEvents(long j, LinkedList<T> linkedList) {
        if (linkedList == null) {
            return;
        }
        while (linkedList.size() > 1) {
            if (linkedList.peek().getTimestamp() >= j) {
                return;
            }
            if (linkedList.get(1).getTimestamp() > j) {
                linkedList.get(0).trimTo(j);
                return;
            } else {
                linkedList.pop();
                linkedList.pop();
            }
        }
        if (linkedList.size() == 1) {
            linkedList.get(0).trimTo(Math.max(j, linkedList.peek().getTimestamp()));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.p006am.BaseAppStateTimeEvents, com.android.server.p006am.BaseAppStateEvents
    public LinkedList<T> add(LinkedList<T> linkedList, LinkedList<T> linkedList2) {
        T t;
        T t2;
        long j;
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
        boolean z = false;
        boolean z2 = false;
        while (true) {
            long j2 = Long.MAX_VALUE;
            if (timestamp == Long.MAX_VALUE && timestamp2 == Long.MAX_VALUE) {
                return linkedList3;
            }
            boolean z3 = true;
            boolean z4 = z || z2;
            int i = (timestamp > timestamp2 ? 1 : (timestamp == timestamp2 ? 0 : -1));
            if (i == 0) {
                z = !z;
                z2 = !z2;
                if (it.hasNext()) {
                    t2 = it.next();
                    j = t2.getTimestamp();
                } else {
                    t2 = next;
                    j = Long.MAX_VALUE;
                }
                if (it2.hasNext()) {
                    next2 = it2.next();
                    j2 = next2.getTimestamp();
                }
            } else if (i < 0) {
                z = !z;
                if (it.hasNext()) {
                    t2 = it.next();
                    j2 = t2.getTimestamp();
                } else {
                    t2 = next;
                }
                j = j2;
                j2 = timestamp2;
            } else {
                z2 = !z2;
                if (it2.hasNext()) {
                    t = it2.next();
                    j2 = t.getTimestamp();
                } else {
                    t = next2;
                }
                long j3 = timestamp;
                t2 = next;
                next = next2;
                next2 = t;
                j = j3;
            }
            if (!z && !z2) {
                z3 = false;
            }
            if (z4 != z3) {
                linkedList3.add((BaseAppStateTimeEvents.BaseTimeEvent) next.clone());
            }
            next = t2;
            timestamp = j;
            timestamp2 = j2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void subtract(BaseAppStateDurations baseAppStateDurations, int i, int i2) {
        Object obj;
        Object obj2;
        Object[] objArr = this.mEvents;
        if (objArr.length <= i || (obj = objArr[i]) == null) {
            return;
        }
        Object[] objArr2 = baseAppStateDurations.mEvents;
        if (objArr2.length <= i2 || (obj2 = objArr2[i2]) == null) {
            return;
        }
        objArr[i] = subtract((LinkedList) obj, (LinkedList) obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void subtract(BaseAppStateDurations baseAppStateDurations, int i) {
        Object[] objArr = baseAppStateDurations.mEvents;
        if (objArr.length <= i || objArr[i] == null) {
            return;
        }
        int i2 = 0;
        while (true) {
            Object[] objArr2 = this.mEvents;
            if (i2 >= objArr2.length) {
                return;
            }
            Object obj = objArr2[i2];
            if (obj != null) {
                objArr2[i2] = subtract((LinkedList) obj, (LinkedList) baseAppStateDurations.mEvents[i]);
            }
            i2++;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public LinkedList<T> subtract(LinkedList<T> linkedList, LinkedList<T> linkedList2) {
        T t;
        T t2;
        long j;
        if (linkedList2 == null || linkedList2.size() == 0 || linkedList == null || linkedList.size() == 0) {
            return linkedList;
        }
        Iterator<T> it = linkedList.iterator();
        Iterator<T> it2 = linkedList2.iterator();
        T next = it.next();
        T next2 = it2.next();
        LinkedList<T> linkedList3 = (LinkedList<T>) new LinkedList();
        long timestamp = next.getTimestamp();
        long timestamp2 = next2.getTimestamp();
        boolean z = false;
        boolean z2 = false;
        while (true) {
            long j2 = Long.MAX_VALUE;
            if (timestamp == Long.MAX_VALUE && timestamp2 == Long.MAX_VALUE) {
                return linkedList3;
            }
            boolean z3 = true;
            boolean z4 = z && !z2;
            int i = (timestamp > timestamp2 ? 1 : (timestamp == timestamp2 ? 0 : -1));
            if (i == 0) {
                z = !z;
                z2 = !z2;
                if (it.hasNext()) {
                    t2 = it.next();
                    j = t2.getTimestamp();
                } else {
                    t2 = next;
                    j = Long.MAX_VALUE;
                }
                if (it2.hasNext()) {
                    next2 = it2.next();
                    j2 = next2.getTimestamp();
                }
            } else if (i < 0) {
                z = !z;
                if (it.hasNext()) {
                    t2 = it.next();
                    j2 = t2.getTimestamp();
                } else {
                    t2 = next;
                }
                j = j2;
                j2 = timestamp2;
            } else {
                z2 = !z2;
                if (it2.hasNext()) {
                    t = it2.next();
                    j2 = t.getTimestamp();
                } else {
                    t = next2;
                }
                long j3 = timestamp;
                t2 = next;
                next = next2;
                next2 = t;
                j = j3;
            }
            if (!z || z2) {
                z3 = false;
            }
            if (z4 != z3) {
                linkedList3.add((BaseAppStateTimeEvents.BaseTimeEvent) next.clone());
            }
            next = t2;
            timestamp = j;
            timestamp2 = j2;
        }
    }

    public long getTotalDurations(long j, int i) {
        return getTotalDurationsSince(getEarliest(0L), j, i);
    }

    public long getTotalDurationsSince(long j, long j2, int i) {
        LinkedList linkedList = this.mEvents[i];
        if (linkedList == null || linkedList.size() == 0) {
            return 0L;
        }
        Iterator it = linkedList.iterator();
        long j3 = 0;
        long j4 = 0;
        boolean z = true;
        while (it.hasNext()) {
            BaseAppStateTimeEvents.BaseTimeEvent baseTimeEvent = (BaseAppStateTimeEvents.BaseTimeEvent) it.next();
            if (baseTimeEvent.getTimestamp() < j || z) {
                j4 = baseTimeEvent.getTimestamp();
            } else {
                j3 += Math.max(0L, baseTimeEvent.getTimestamp() - Math.max(j4, j));
            }
            z = !z;
        }
        return (linkedList.size() & 1) == 1 ? j3 + Math.max(0L, j2 - Math.max(j4, j)) : j3;
    }

    public boolean isActive(int i) {
        LinkedList linkedList = this.mEvents[i];
        return linkedList != null && (linkedList.size() & 1) == 1;
    }

    @Override // com.android.server.p006am.BaseAppStateEvents
    public String formatEventSummary(long j, int i) {
        return TimeUtils.formatDuration(getTotalDurations(j, i));
    }

    @Override // com.android.server.p006am.BaseAppStateEvents
    public String toString() {
        return this.mPackageName + "/" + UserHandle.formatUid(this.mUid) + " isActive[0]=" + isActive(0) + " totalDurations[0]=" + getTotalDurations(SystemClock.elapsedRealtime(), 0);
    }
}
