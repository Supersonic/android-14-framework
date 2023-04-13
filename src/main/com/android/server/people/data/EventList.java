package com.android.server.people.data;

import com.android.internal.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class EventList {
    public final List<Event> mEvents = new ArrayList();

    public void add(Event event) {
        int firstIndexOnOrAfter = firstIndexOnOrAfter(event.getTimestamp());
        if (firstIndexOnOrAfter < this.mEvents.size() && this.mEvents.get(firstIndexOnOrAfter).getTimestamp() == event.getTimestamp() && isDuplicate(event, firstIndexOnOrAfter)) {
            return;
        }
        this.mEvents.add(firstIndexOnOrAfter, event);
    }

    public void addAll(List<Event> list) {
        for (Event event : list) {
            add(event);
        }
    }

    public void clear() {
        this.mEvents.clear();
    }

    public List<Event> getAllEvents() {
        return CollectionUtils.copyOf(this.mEvents);
    }

    public void removeOldEvents(long j) {
        int firstIndexOnOrAfter = firstIndexOnOrAfter(j);
        if (firstIndexOnOrAfter == 0) {
            return;
        }
        int size = this.mEvents.size();
        if (firstIndexOnOrAfter == size) {
            this.mEvents.clear();
            return;
        }
        int i = 0;
        while (firstIndexOnOrAfter < size) {
            List<Event> list = this.mEvents;
            list.set(i, list.get(firstIndexOnOrAfter));
            i++;
            firstIndexOnOrAfter++;
        }
        if (size > i) {
            this.mEvents.subList(i, size).clear();
        }
    }

    public final int firstIndexOnOrAfter(long j) {
        int size = this.mEvents.size();
        int size2 = this.mEvents.size() - 1;
        int i = 0;
        while (i <= size2) {
            int i2 = (i + size2) >>> 1;
            if (this.mEvents.get(i2).getTimestamp() >= j) {
                size2 = i2 - 1;
                size = i2;
            } else {
                i = i2 + 1;
            }
        }
        return size;
    }

    public final boolean isDuplicate(Event event, int i) {
        int size = this.mEvents.size();
        while (i < size && this.mEvents.get(i).getTimestamp() <= event.getTimestamp()) {
            int i2 = i + 1;
            if (this.mEvents.get(i).getType() == event.getType()) {
                return true;
            }
            i = i2;
        }
        return false;
    }
}
