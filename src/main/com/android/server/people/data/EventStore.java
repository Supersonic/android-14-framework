package com.android.server.people.data;

import android.net.Uri;
import android.util.ArrayMap;
import com.android.internal.annotations.GuardedBy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class EventStore {
    @GuardedBy({"this"})
    public final List<Map<String, EventHistoryImpl>> mEventHistoryMaps;
    public final List<File> mEventsCategoryDirs;
    public final ScheduledExecutorService mScheduledExecutorService;

    public EventStore(File file, ScheduledExecutorService scheduledExecutorService) {
        ArrayList arrayList = new ArrayList();
        this.mEventHistoryMaps = arrayList;
        ArrayList arrayList2 = new ArrayList();
        this.mEventsCategoryDirs = arrayList2;
        arrayList.add(0, new ArrayMap());
        arrayList.add(1, new ArrayMap());
        arrayList.add(2, new ArrayMap());
        arrayList.add(3, new ArrayMap());
        arrayList.add(4, new ArrayMap());
        File file2 = new File(file, "event");
        arrayList2.add(0, new File(file2, "shortcut"));
        arrayList2.add(1, new File(file2, "locus"));
        arrayList2.add(2, new File(file2, "call"));
        arrayList2.add(3, new File(file2, "sms"));
        arrayList2.add(4, new File(file2, "class"));
        this.mScheduledExecutorService = scheduledExecutorService;
    }

    public synchronized void loadFromDisk() {
        for (int i = 0; i < this.mEventsCategoryDirs.size(); i++) {
            this.mEventHistoryMaps.get(i).putAll(EventHistoryImpl.eventHistoriesImplFromDisk(this.mEventsCategoryDirs.get(i), this.mScheduledExecutorService));
        }
    }

    public synchronized void saveToDisk() {
        for (Map<String, EventHistoryImpl> map : this.mEventHistoryMaps) {
            for (EventHistoryImpl eventHistoryImpl : map.values()) {
                eventHistoryImpl.saveToDisk();
            }
        }
    }

    public synchronized EventHistory getEventHistory(int i, String str) {
        return this.mEventHistoryMaps.get(i).get(str);
    }

    public synchronized EventHistoryImpl getOrCreateEventHistory(final int i, final String str) {
        return this.mEventHistoryMaps.get(i).computeIfAbsent(str, new Function() { // from class: com.android.server.people.data.EventStore$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                EventHistoryImpl lambda$getOrCreateEventHistory$0;
                lambda$getOrCreateEventHistory$0 = EventStore.this.lambda$getOrCreateEventHistory$0(i, str, (String) obj);
                return lambda$getOrCreateEventHistory$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ EventHistoryImpl lambda$getOrCreateEventHistory$0(int i, String str, String str2) {
        return new EventHistoryImpl(new File(this.mEventsCategoryDirs.get(i), Uri.encode(str)), this.mScheduledExecutorService);
    }

    public synchronized void deleteEventHistory(int i, String str) {
        EventHistoryImpl remove = this.mEventHistoryMaps.get(i).remove(str);
        if (remove != null) {
            remove.onDestroy();
        }
    }

    public synchronized void deleteEventHistories(int i) {
        for (EventHistoryImpl eventHistoryImpl : this.mEventHistoryMaps.get(i).values()) {
            eventHistoryImpl.onDestroy();
        }
        this.mEventHistoryMaps.get(i).clear();
    }

    public synchronized void pruneOldEvents() {
        for (Map<String, EventHistoryImpl> map : this.mEventHistoryMaps) {
            for (EventHistoryImpl eventHistoryImpl : map.values()) {
                eventHistoryImpl.pruneOldEvents();
            }
        }
    }

    public synchronized void pruneOrphanEventHistories(int i, Predicate<String> predicate) {
        Set<String> keySet = this.mEventHistoryMaps.get(i).keySet();
        ArrayList<String> arrayList = new ArrayList();
        for (String str : keySet) {
            if (!predicate.test(str)) {
                arrayList.add(str);
            }
        }
        Map<String, EventHistoryImpl> map = this.mEventHistoryMaps.get(i);
        for (String str2 : arrayList) {
            EventHistoryImpl remove = map.remove(str2);
            if (remove != null) {
                remove.onDestroy();
            }
        }
    }

    public synchronized void onDestroy() {
        for (Map<String, EventHistoryImpl> map : this.mEventHistoryMaps) {
            for (EventHistoryImpl eventHistoryImpl : map.values()) {
                eventHistoryImpl.onDestroy();
            }
        }
    }
}
