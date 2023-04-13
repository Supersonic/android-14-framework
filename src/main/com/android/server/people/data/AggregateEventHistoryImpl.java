package com.android.server.people.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class AggregateEventHistoryImpl implements EventHistory {
    public final List<EventHistory> mEventHistoryList = new ArrayList();

    @Override // com.android.server.people.data.EventHistory
    public EventIndex getEventIndex(int i) {
        for (EventHistory eventHistory : this.mEventHistoryList) {
            EventIndex eventIndex = eventHistory.getEventIndex(i);
            if (!eventIndex.isEmpty()) {
                return eventIndex;
            }
        }
        return EventIndex.EMPTY;
    }

    @Override // com.android.server.people.data.EventHistory
    public EventIndex getEventIndex(Set<Integer> set) {
        EventIndex eventIndex = null;
        for (EventHistory eventHistory : this.mEventHistoryList) {
            EventIndex eventIndex2 = eventHistory.getEventIndex(set);
            if (eventIndex == null) {
                eventIndex = eventIndex2;
            } else if (!eventIndex2.isEmpty()) {
                eventIndex = EventIndex.combine(eventIndex, eventIndex2);
            }
        }
        return eventIndex != null ? eventIndex : EventIndex.EMPTY;
    }

    public void addEventHistory(EventHistory eventHistory) {
        this.mEventHistoryList.add(eventHistory);
    }
}
