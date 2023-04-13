package com.android.server.notification;

import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class GroupHelper {
    public static final boolean DEBUG = Log.isLoggable("GroupHelper", 3);
    public final int mAutoGroupAtCount;
    public final Callback mCallback;
    public final ArrayMap<String, ArraySet<String>> mOngoingGroupCount = new ArrayMap<>();
    public Map<Integer, Map<String, LinkedHashSet<String>>> mUngroupedNotifications = new HashMap();

    /* loaded from: classes2.dex */
    public interface Callback {
        void addAutoGroup(String str);

        void addAutoGroupSummary(int i, String str, String str2, boolean z);

        void removeAutoGroup(String str);

        void removeAutoGroupSummary(int i, String str);

        void updateAutogroupSummary(int i, String str, boolean z);
    }

    public GroupHelper(int i, Callback callback) {
        this.mAutoGroupAtCount = i;
        this.mCallback = callback;
    }

    public final String generatePackageKey(int i, String str) {
        return i + "|" + str;
    }

    @VisibleForTesting
    public int getOngoingGroupCount(int i, String str) {
        return this.mOngoingGroupCount.getOrDefault(generatePackageKey(i, str), new ArraySet<>(0)).size();
    }

    public final void updateOngoingGroupCount(StatusBarNotification statusBarNotification, boolean z) {
        if (statusBarNotification.getNotification().isGroupSummary()) {
            return;
        }
        String generatePackageKey = generatePackageKey(statusBarNotification.getUserId(), statusBarNotification.getPackageName());
        ArraySet<String> orDefault = this.mOngoingGroupCount.getOrDefault(generatePackageKey, new ArraySet<>(0));
        if (z) {
            orDefault.add(statusBarNotification.getKey());
            this.mOngoingGroupCount.put(generatePackageKey, orDefault);
        } else {
            orDefault.remove(statusBarNotification.getKey());
        }
        this.mCallback.updateAutogroupSummary(statusBarNotification.getUserId(), statusBarNotification.getPackageName(), orDefault.size() > 0);
    }

    public void onNotificationUpdated(StatusBarNotification statusBarNotification) {
        updateOngoingGroupCount(statusBarNotification, statusBarNotification.isOngoing() && !statusBarNotification.isAppGroup());
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification, boolean z) {
        try {
            updateOngoingGroupCount(statusBarNotification, statusBarNotification.isOngoing() && !statusBarNotification.isAppGroup());
            List<String> arrayList = new ArrayList<>();
            if (!statusBarNotification.isAppGroup()) {
                synchronized (this.mUngroupedNotifications) {
                    Map<String, LinkedHashSet<String>> map = this.mUngroupedNotifications.get(Integer.valueOf(statusBarNotification.getUserId()));
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    this.mUngroupedNotifications.put(Integer.valueOf(statusBarNotification.getUserId()), map);
                    LinkedHashSet<String> linkedHashSet = map.get(statusBarNotification.getPackageName());
                    if (linkedHashSet == null) {
                        linkedHashSet = new LinkedHashSet<>();
                    }
                    linkedHashSet.add(statusBarNotification.getKey());
                    map.put(statusBarNotification.getPackageName(), linkedHashSet);
                    if (linkedHashSet.size() >= this.mAutoGroupAtCount || z) {
                        arrayList.addAll(linkedHashSet);
                    }
                }
                if (arrayList.size() > 0) {
                    adjustAutogroupingSummary(statusBarNotification.getUserId(), statusBarNotification.getPackageName(), arrayList.get(0), true);
                    adjustNotificationBundling(arrayList, true);
                    return;
                }
                return;
            }
            maybeUngroup(statusBarNotification, false, statusBarNotification.getUserId());
        } catch (Exception e) {
            Slog.e("GroupHelper", "Failure processing new notification", e);
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        try {
            updateOngoingGroupCount(statusBarNotification, false);
            maybeUngroup(statusBarNotification, true, statusBarNotification.getUserId());
        } catch (Exception e) {
            Slog.e("GroupHelper", "Error processing canceled notification", e);
        }
    }

    public final void maybeUngroup(StatusBarNotification statusBarNotification, boolean z, int i) {
        boolean z2;
        ArrayList arrayList = new ArrayList();
        synchronized (this.mUngroupedNotifications) {
            Map<String, LinkedHashSet<String>> map = this.mUngroupedNotifications.get(Integer.valueOf(statusBarNotification.getUserId()));
            if (map != null && map.size() != 0) {
                LinkedHashSet<String> linkedHashSet = map.get(statusBarNotification.getPackageName());
                if (linkedHashSet != null && linkedHashSet.size() != 0) {
                    if (linkedHashSet.remove(statusBarNotification.getKey()) && !z) {
                        arrayList.add(statusBarNotification.getKey());
                    }
                    if (linkedHashSet.size() == 0) {
                        map.remove(statusBarNotification.getPackageName());
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (z2) {
                        adjustAutogroupingSummary(i, statusBarNotification.getPackageName(), null, false);
                    }
                    if (arrayList.size() > 0) {
                        adjustNotificationBundling(arrayList, false);
                    }
                }
            }
        }
    }

    public final void adjustAutogroupingSummary(int i, String str, String str2, boolean z) {
        if (z) {
            this.mCallback.addAutoGroupSummary(i, str, str2, getOngoingGroupCount(i, str) > 0);
        } else {
            this.mCallback.removeAutoGroupSummary(i, str);
        }
    }

    public final void adjustNotificationBundling(List<String> list, boolean z) {
        for (String str : list) {
            if (DEBUG) {
                Log.i("GroupHelper", "Sending grouping adjustment for: " + str + " group? " + z);
            }
            if (z) {
                this.mCallback.addAutoGroup(str);
            } else {
                this.mCallback.removeAutoGroup(str);
            }
        }
    }
}
