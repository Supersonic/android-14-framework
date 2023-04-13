package com.android.server.people.data;

import android.net.Uri;
import android.os.FileUtils;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.people.data.AbstractProtoDiskReadWriter;
import com.android.server.people.data.EventHistoryImpl;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
/* loaded from: classes2.dex */
public class EventHistoryImpl implements EventHistory {
    @GuardedBy({"this"})
    public final SparseArray<EventIndex> mEventIndexArray;
    public final EventIndexesProtoDiskReadWriter mEventIndexesProtoDiskReadWriter;
    public final EventsProtoDiskReadWriter mEventsProtoDiskReadWriter;
    public final Injector mInjector;
    public long mLastPruneTime;
    @GuardedBy({"this"})
    public final EventList mRecentEvents;
    public final File mRootDir;
    public final ScheduledExecutorService mScheduledExecutorService;

    public EventHistoryImpl(File file, ScheduledExecutorService scheduledExecutorService) {
        this(new Injector(), file, scheduledExecutorService);
    }

    @VisibleForTesting
    public EventHistoryImpl(Injector injector, File file, ScheduledExecutorService scheduledExecutorService) {
        this.mEventIndexArray = new SparseArray<>();
        this.mRecentEvents = new EventList();
        this.mInjector = injector;
        this.mScheduledExecutorService = scheduledExecutorService;
        this.mLastPruneTime = injector.currentTimeMillis();
        this.mRootDir = file;
        this.mEventsProtoDiskReadWriter = new EventsProtoDiskReadWriter(new File(file, "events"), scheduledExecutorService);
        this.mEventIndexesProtoDiskReadWriter = new EventIndexesProtoDiskReadWriter(new File(file, "indexes"), scheduledExecutorService);
    }

    public static Map<String, EventHistoryImpl> eventHistoriesImplFromDisk(File file, ScheduledExecutorService scheduledExecutorService) {
        return eventHistoriesImplFromDisk(new Injector(), file, scheduledExecutorService);
    }

    @VisibleForTesting
    public static Map<String, EventHistoryImpl> eventHistoriesImplFromDisk(Injector injector, File file, ScheduledExecutorService scheduledExecutorService) {
        ArrayMap arrayMap = new ArrayMap();
        File[] listFiles = file.listFiles(new EventHistoryImpl$$ExternalSyntheticLambda1());
        if (listFiles == null) {
            return arrayMap;
        }
        for (File file2 : listFiles) {
            File[] listFiles2 = file2.listFiles(new FilenameFilter() { // from class: com.android.server.people.data.EventHistoryImpl$$ExternalSyntheticLambda2
                @Override // java.io.FilenameFilter
                public final boolean accept(File file3, String str) {
                    boolean lambda$eventHistoriesImplFromDisk$0;
                    lambda$eventHistoriesImplFromDisk$0 = EventHistoryImpl.lambda$eventHistoriesImplFromDisk$0(file3, str);
                    return lambda$eventHistoriesImplFromDisk$0;
                }
            });
            if (listFiles2 != null && listFiles2.length == 2) {
                EventHistoryImpl eventHistoryImpl = new EventHistoryImpl(injector, file2, scheduledExecutorService);
                eventHistoryImpl.loadFromDisk();
                arrayMap.put(Uri.decode(file2.getName()), eventHistoryImpl);
            }
        }
        return arrayMap;
    }

    public static /* synthetic */ boolean lambda$eventHistoriesImplFromDisk$0(File file, String str) {
        return "events".equals(str) || "indexes".equals(str);
    }

    @VisibleForTesting
    public synchronized void loadFromDisk() {
        this.mScheduledExecutorService.execute(new Runnable() { // from class: com.android.server.people.data.EventHistoryImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EventHistoryImpl.this.lambda$loadFromDisk$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFromDisk$1() {
        synchronized (this) {
            EventList loadRecentEventsFromDisk = this.mEventsProtoDiskReadWriter.loadRecentEventsFromDisk();
            if (loadRecentEventsFromDisk != null) {
                loadRecentEventsFromDisk.removeOldEvents(this.mInjector.currentTimeMillis() - BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
                this.mRecentEvents.addAll(loadRecentEventsFromDisk.getAllEvents());
            }
            SparseArray<EventIndex> loadIndexesFromDisk = this.mEventIndexesProtoDiskReadWriter.loadIndexesFromDisk();
            if (loadIndexesFromDisk != null) {
                for (int i = 0; i < loadIndexesFromDisk.size(); i++) {
                    this.mEventIndexArray.put(loadIndexesFromDisk.keyAt(i), loadIndexesFromDisk.valueAt(i));
                }
            }
        }
    }

    public synchronized void saveToDisk() {
        this.mEventsProtoDiskReadWriter.saveEventsImmediately(this.mRecentEvents);
        this.mEventIndexesProtoDiskReadWriter.saveIndexesImmediately(this.mEventIndexArray);
    }

    @Override // com.android.server.people.data.EventHistory
    public synchronized EventIndex getEventIndex(int i) {
        EventIndex eventIndex;
        eventIndex = this.mEventIndexArray.get(i);
        return eventIndex != null ? new EventIndex(eventIndex) : this.mInjector.createEventIndex();
    }

    @Override // com.android.server.people.data.EventHistory
    public synchronized EventIndex getEventIndex(Set<Integer> set) {
        EventIndex createEventIndex;
        createEventIndex = this.mInjector.createEventIndex();
        for (Integer num : set) {
            EventIndex eventIndex = this.mEventIndexArray.get(num.intValue());
            if (eventIndex != null) {
                createEventIndex = EventIndex.combine(createEventIndex, eventIndex);
            }
        }
        return createEventIndex;
    }

    public synchronized void addEvent(Event event) {
        pruneOldEvents();
        addEventInMemory(event);
        this.mEventsProtoDiskReadWriter.scheduleEventsSave(this.mRecentEvents);
        this.mEventIndexesProtoDiskReadWriter.scheduleIndexesSave(this.mEventIndexArray);
    }

    public synchronized void onDestroy() {
        this.mEventIndexArray.clear();
        this.mRecentEvents.clear();
        this.mEventsProtoDiskReadWriter.deleteRecentEventsFile();
        this.mEventIndexesProtoDiskReadWriter.deleteIndexesFile();
        FileUtils.deleteContentsAndDir(this.mRootDir);
    }

    public synchronized void pruneOldEvents() {
        long currentTimeMillis = this.mInjector.currentTimeMillis();
        if (currentTimeMillis - this.mLastPruneTime > 900000) {
            this.mRecentEvents.removeOldEvents(currentTimeMillis - BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
            this.mLastPruneTime = currentTimeMillis;
        }
    }

    public final synchronized void addEventInMemory(Event event) {
        EventIndex eventIndex = this.mEventIndexArray.get(event.getType());
        if (eventIndex == null) {
            eventIndex = this.mInjector.createEventIndex();
            this.mEventIndexArray.put(event.getType(), eventIndex);
        }
        eventIndex.addEvent(event.getTimestamp());
        this.mRecentEvents.add(event);
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public EventIndex createEventIndex() {
            return new EventIndex();
        }

        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    /* loaded from: classes2.dex */
    public static class EventsProtoDiskReadWriter extends AbstractProtoDiskReadWriter<EventList> {
        public static final String TAG = "EventsProtoDiskReadWriter";

        public EventsProtoDiskReadWriter(File file, ScheduledExecutorService scheduledExecutorService) {
            super(file, scheduledExecutorService);
            file.mkdirs();
        }

        @Override // com.android.server.people.data.AbstractProtoDiskReadWriter
        public AbstractProtoDiskReadWriter.ProtoStreamWriter<EventList> protoStreamWriter() {
            return new AbstractProtoDiskReadWriter.ProtoStreamWriter() { // from class: com.android.server.people.data.EventHistoryImpl$EventsProtoDiskReadWriter$$ExternalSyntheticLambda0
                @Override // com.android.server.people.data.AbstractProtoDiskReadWriter.ProtoStreamWriter
                public final void write(ProtoOutputStream protoOutputStream, Object obj) {
                    EventHistoryImpl.EventsProtoDiskReadWriter.lambda$protoStreamWriter$0(protoOutputStream, (EventList) obj);
                }
            };
        }

        public static /* synthetic */ void lambda$protoStreamWriter$0(ProtoOutputStream protoOutputStream, EventList eventList) {
            for (Event event : eventList.getAllEvents()) {
                long start = protoOutputStream.start(2246267895809L);
                event.writeToProto(protoOutputStream);
                protoOutputStream.end(start);
            }
        }

        @Override // com.android.server.people.data.AbstractProtoDiskReadWriter
        public AbstractProtoDiskReadWriter.ProtoStreamReader<EventList> protoStreamReader() {
            return new AbstractProtoDiskReadWriter.ProtoStreamReader() { // from class: com.android.server.people.data.EventHistoryImpl$EventsProtoDiskReadWriter$$ExternalSyntheticLambda1
                @Override // com.android.server.people.data.AbstractProtoDiskReadWriter.ProtoStreamReader
                public final Object read(ProtoInputStream protoInputStream) {
                    EventList lambda$protoStreamReader$1;
                    lambda$protoStreamReader$1 = EventHistoryImpl.EventsProtoDiskReadWriter.lambda$protoStreamReader$1(protoInputStream);
                    return lambda$protoStreamReader$1;
                }
            };
        }

        public static /* synthetic */ EventList lambda$protoStreamReader$1(ProtoInputStream protoInputStream) {
            ArrayList newArrayList = Lists.newArrayList();
            while (protoInputStream.nextField() != -1) {
                try {
                    if (protoInputStream.getFieldNumber() == 1) {
                        long start = protoInputStream.start(2246267895809L);
                        Event readFromProto = Event.readFromProto(protoInputStream);
                        protoInputStream.end(start);
                        newArrayList.add(readFromProto);
                    }
                } catch (IOException e) {
                    Slog.e(TAG, "Failed to read protobuf input stream.", e);
                }
            }
            EventList eventList = new EventList();
            eventList.addAll(newArrayList);
            return eventList;
        }

        public void scheduleEventsSave(EventList eventList) {
            scheduleSave("recent", eventList);
        }

        public void saveEventsImmediately(EventList eventList) {
            saveImmediately("recent", eventList);
        }

        public EventList loadRecentEventsFromDisk() {
            return read("recent");
        }

        public void deleteRecentEventsFile() {
            delete("recent");
        }
    }

    /* loaded from: classes2.dex */
    public static class EventIndexesProtoDiskReadWriter extends AbstractProtoDiskReadWriter<SparseArray<EventIndex>> {
        public static final String TAG = "EventIndexesProtoDiskReadWriter";

        public EventIndexesProtoDiskReadWriter(File file, ScheduledExecutorService scheduledExecutorService) {
            super(file, scheduledExecutorService);
            file.mkdirs();
        }

        @Override // com.android.server.people.data.AbstractProtoDiskReadWriter
        public AbstractProtoDiskReadWriter.ProtoStreamWriter<SparseArray<EventIndex>> protoStreamWriter() {
            return new AbstractProtoDiskReadWriter.ProtoStreamWriter() { // from class: com.android.server.people.data.EventHistoryImpl$EventIndexesProtoDiskReadWriter$$ExternalSyntheticLambda0
                @Override // com.android.server.people.data.AbstractProtoDiskReadWriter.ProtoStreamWriter
                public final void write(ProtoOutputStream protoOutputStream, Object obj) {
                    EventHistoryImpl.EventIndexesProtoDiskReadWriter.lambda$protoStreamWriter$0(protoOutputStream, (SparseArray) obj);
                }
            };
        }

        public static /* synthetic */ void lambda$protoStreamWriter$0(ProtoOutputStream protoOutputStream, SparseArray sparseArray) {
            for (int i = 0; i < sparseArray.size(); i++) {
                int keyAt = sparseArray.keyAt(i);
                long start = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, keyAt);
                long start2 = protoOutputStream.start(1146756268034L);
                ((EventIndex) sparseArray.valueAt(i)).writeToProto(protoOutputStream);
                protoOutputStream.end(start2);
                protoOutputStream.end(start);
            }
        }

        @Override // com.android.server.people.data.AbstractProtoDiskReadWriter
        public AbstractProtoDiskReadWriter.ProtoStreamReader<SparseArray<EventIndex>> protoStreamReader() {
            return new AbstractProtoDiskReadWriter.ProtoStreamReader() { // from class: com.android.server.people.data.EventHistoryImpl$EventIndexesProtoDiskReadWriter$$ExternalSyntheticLambda1
                @Override // com.android.server.people.data.AbstractProtoDiskReadWriter.ProtoStreamReader
                public final Object read(ProtoInputStream protoInputStream) {
                    SparseArray lambda$protoStreamReader$1;
                    lambda$protoStreamReader$1 = EventHistoryImpl.EventIndexesProtoDiskReadWriter.lambda$protoStreamReader$1(protoInputStream);
                    return lambda$protoStreamReader$1;
                }
            };
        }

        public static /* synthetic */ SparseArray lambda$protoStreamReader$1(ProtoInputStream protoInputStream) {
            SparseArray sparseArray = new SparseArray();
            while (protoInputStream.nextField() != -1) {
                try {
                    if (protoInputStream.getFieldNumber() == 1) {
                        long start = protoInputStream.start(2246267895809L);
                        EventIndex eventIndex = EventIndex.EMPTY;
                        int i = 0;
                        while (protoInputStream.nextField() != -1) {
                            int fieldNumber = protoInputStream.getFieldNumber();
                            if (fieldNumber == 1) {
                                i = protoInputStream.readInt(1120986464257L);
                            } else if (fieldNumber == 2) {
                                long start2 = protoInputStream.start(1146756268034L);
                                eventIndex = EventIndex.readFromProto(protoInputStream);
                                protoInputStream.end(start2);
                            } else {
                                String str = TAG;
                                Slog.w(str, "Could not read undefined field: " + protoInputStream.getFieldNumber());
                            }
                        }
                        sparseArray.append(i, eventIndex);
                        protoInputStream.end(start);
                    }
                } catch (IOException e) {
                    Slog.e(TAG, "Failed to read protobuf input stream.", e);
                }
            }
            return sparseArray;
        }

        public void scheduleIndexesSave(SparseArray<EventIndex> sparseArray) {
            scheduleSave("index", sparseArray);
        }

        public void saveIndexesImmediately(SparseArray<EventIndex> sparseArray) {
            saveImmediately("index", sparseArray);
        }

        public SparseArray<EventIndex> loadIndexesFromDisk() {
            return read("index");
        }

        public void deleteIndexesFile() {
            delete("index");
        }
    }
}
