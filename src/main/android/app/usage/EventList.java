package android.app.usage;

import android.app.usage.UsageEvents;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class EventList {
    private final ArrayList<UsageEvents.Event> mEvents = new ArrayList<>();

    public int size() {
        return this.mEvents.size();
    }

    public void clear() {
        this.mEvents.clear();
    }

    public UsageEvents.Event get(int index) {
        return this.mEvents.get(index);
    }

    public void insert(UsageEvents.Event event) {
        int size = this.mEvents.size();
        if (size == 0 || event.mTimeStamp >= this.mEvents.get(size - 1).mTimeStamp) {
            this.mEvents.add(event);
            return;
        }
        int insertIndex = firstIndexOnOrAfter(event.mTimeStamp + 1);
        this.mEvents.add(insertIndex, event);
    }

    public UsageEvents.Event remove(int index) {
        try {
            return this.mEvents.remove(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int firstIndexOnOrAfter(long timeStamp) {
        int size = this.mEvents.size();
        int result = size;
        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            long midTimeStamp = this.mEvents.get(mid).mTimeStamp;
            if (midTimeStamp >= timeStamp) {
                hi = mid - 1;
                result = mid;
            } else {
                lo = mid + 1;
            }
        }
        return result;
    }

    public void merge(EventList events) {
        int size = events.size();
        for (int i = 0; i < size; i++) {
            insert(events.get(i));
        }
    }
}
