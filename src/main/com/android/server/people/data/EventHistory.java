package com.android.server.people.data;

import java.util.Set;
/* loaded from: classes2.dex */
public interface EventHistory {
    EventIndex getEventIndex(int i);

    EventIndex getEventIndex(Set<Integer> set);
}
