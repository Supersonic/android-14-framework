package com.android.server.people.data;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.LocusId;
import android.util.ArrayMap;
import com.android.server.LocalServices;
import com.android.server.people.data.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class UsageStatsQueryHelper {
    public final EventListener mEventListener;
    public long mLastEventTimestamp;
    public final Function<String, PackageData> mPackageDataGetter;
    public final int mUserId;
    public final Map<ComponentName, UsageEvents.Event> mConvoStartEvents = new ArrayMap();
    public final UsageStatsManagerInternal mUsageStatsManagerInternal = getUsageStatsManagerInternal();

    /* loaded from: classes2.dex */
    public interface EventListener {
        void onEvent(PackageData packageData, ConversationInfo conversationInfo, Event event);
    }

    public UsageStatsQueryHelper(int i, Function<String, PackageData> function, EventListener eventListener) {
        this.mUserId = i;
        this.mPackageDataGetter = function;
        this.mEventListener = eventListener;
    }

    public boolean querySince(long j) {
        UsageEvents queryEventsForUser = this.mUsageStatsManagerInternal.queryEventsForUser(this.mUserId, j, System.currentTimeMillis(), 0);
        boolean z = false;
        if (queryEventsForUser == null) {
            return false;
        }
        while (queryEventsForUser.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            queryEventsForUser.getNextEvent(event);
            this.mLastEventTimestamp = Math.max(this.mLastEventTimestamp, event.getTimeStamp());
            String packageName = event.getPackageName();
            PackageData apply = this.mPackageDataGetter.apply(packageName);
            if (apply != null) {
                int eventType = event.getEventType();
                if (eventType != 2) {
                    if (eventType == 8) {
                        addEventByShortcutId(apply, event.getShortcutId(), new Event(event.getTimeStamp(), 1));
                    } else if (eventType == 30) {
                        onInAppConversationEnded(apply, event);
                        LocusId locusId = event.getLocusId() != null ? new LocusId(event.getLocusId()) : null;
                        if (locusId != null && apply.getConversationStore().getConversationByLocusId(locusId) != null) {
                            this.mConvoStartEvents.put(new ComponentName(packageName, event.getClassName()), event);
                        }
                    } else if (eventType != 23 && eventType != 24) {
                    }
                }
                onInAppConversationEnded(apply, event);
            }
            z = true;
        }
        return z;
    }

    public long getLastEventTimestamp() {
        return this.mLastEventTimestamp;
    }

    public static List<UsageEvents.Event> queryAppMovingToForegroundEvents(int i, long j, long j2) {
        ArrayList arrayList = new ArrayList();
        UsageEvents queryEventsForUser = getUsageStatsManagerInternal().queryEventsForUser(i, j, j2, 10);
        if (queryEventsForUser == null) {
            return arrayList;
        }
        while (queryEventsForUser.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            queryEventsForUser.getNextEvent(event);
            if (event.getEventType() == 1) {
                arrayList.add(event);
            }
        }
        return arrayList;
    }

    public static Map<String, AppUsageStatsData> queryAppUsageStats(int i, long j, long j2, Set<String> set) {
        List<UsageStats> queryUsageStatsForUser = getUsageStatsManagerInternal().queryUsageStatsForUser(i, 4, j, j2, false);
        ArrayMap arrayMap = new ArrayMap();
        if (queryUsageStatsForUser == null) {
            return arrayMap;
        }
        for (UsageStats usageStats : queryUsageStatsForUser) {
            String packageName = usageStats.getPackageName();
            if (set.contains(packageName)) {
                AppUsageStatsData appUsageStatsData = (AppUsageStatsData) arrayMap.computeIfAbsent(packageName, new Function() { // from class: com.android.server.people.data.UsageStatsQueryHelper$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        AppUsageStatsData lambda$queryAppUsageStats$0;
                        lambda$queryAppUsageStats$0 = UsageStatsQueryHelper.lambda$queryAppUsageStats$0((String) obj);
                        return lambda$queryAppUsageStats$0;
                    }
                });
                appUsageStatsData.incrementChosenCountBy(sumChooserCounts(usageStats.mChooserCounts));
                appUsageStatsData.incrementLaunchCountBy(usageStats.getAppLaunchCount());
            }
        }
        return arrayMap;
    }

    public static /* synthetic */ AppUsageStatsData lambda$queryAppUsageStats$0(String str) {
        return new AppUsageStatsData();
    }

    public static int sumChooserCounts(ArrayMap<String, ArrayMap<String, Integer>> arrayMap) {
        if (arrayMap == null) {
            return 0;
        }
        int size = arrayMap.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ArrayMap<String, Integer> valueAt = arrayMap.valueAt(i2);
            if (valueAt != null) {
                int size2 = valueAt.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    i += valueAt.valueAt(i3).intValue();
                }
            }
        }
        return i;
    }

    public final void onInAppConversationEnded(PackageData packageData, UsageEvents.Event event) {
        UsageEvents.Event remove = this.mConvoStartEvents.remove(new ComponentName(event.getPackageName(), event.getClassName()));
        if (remove == null || remove.getTimeStamp() >= event.getTimeStamp()) {
            return;
        }
        addEventByLocusId(packageData, new LocusId(remove.getLocusId()), new Event.Builder(remove.getTimeStamp(), 13).setDurationSeconds((int) ((event.getTimeStamp() - remove.getTimeStamp()) / 1000)).build());
    }

    public final void addEventByShortcutId(PackageData packageData, String str, Event event) {
        ConversationInfo conversation = packageData.getConversationStore().getConversation(str);
        if (conversation == null) {
            return;
        }
        packageData.getEventStore().getOrCreateEventHistory(0, str).addEvent(event);
        this.mEventListener.onEvent(packageData, conversation, event);
    }

    public final void addEventByLocusId(PackageData packageData, LocusId locusId, Event event) {
        ConversationInfo conversationByLocusId = packageData.getConversationStore().getConversationByLocusId(locusId);
        if (conversationByLocusId == null) {
            return;
        }
        packageData.getEventStore().getOrCreateEventHistory(1, locusId.getId()).addEvent(event);
        this.mEventListener.onEvent(packageData, conversationByLocusId, event);
    }

    public static UsageStatsManagerInternal getUsageStatsManagerInternal() {
        return (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
    }
}
