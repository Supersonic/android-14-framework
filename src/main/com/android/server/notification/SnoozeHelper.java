package com.android.server.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.IntArray;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.notification.ManagedServices;
import com.android.server.notification.NotificationManagerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public final class SnoozeHelper {
    public static final boolean DEBUG = Log.isLoggable("SnoozeHelper", 3);
    public static final String REPOST_ACTION = SnoozeHelper.class.getSimpleName() + ".EVALUATE";
    public AlarmManager mAm;
    public final BroadcastReceiver mBroadcastReceiver;
    public Callback mCallback;
    public final Context mContext;
    public final ManagedServices.UserProfiles mUserProfiles;
    public ArrayMap<String, NotificationRecord> mSnoozedNotifications = new ArrayMap<>();
    public final ArrayMap<String, Long> mPersistedSnoozedNotifications = new ArrayMap<>();
    public final ArrayMap<String, String> mPersistedSnoozedNotificationsWithContext = new ArrayMap<>();
    public final Object mLock = new Object();

    /* loaded from: classes2.dex */
    public interface Callback {
        void repost(int i, NotificationRecord notificationRecord, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface Inserter<T> {
        void insert(T t) throws IOException;
    }

    public SnoozeHelper(Context context, Callback callback, ManagedServices.UserProfiles userProfiles) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.notification.SnoozeHelper.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (SnoozeHelper.DEBUG) {
                    Slog.d("SnoozeHelper", "Reposting notification");
                }
                if (SnoozeHelper.REPOST_ACTION.equals(intent.getAction())) {
                    SnoozeHelper.this.repost(intent.getStringExtra("key"), intent.getIntExtra("userId", 0), false);
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter(REPOST_ACTION);
        intentFilter.addDataScheme("repost");
        context.registerReceiver(broadcastReceiver, intentFilter, 2);
        this.mAm = (AlarmManager) context.getSystemService("alarm");
        this.mCallback = callback;
        this.mUserProfiles = userProfiles;
    }

    public boolean canSnooze(int i) {
        synchronized (this.mLock) {
            return this.mSnoozedNotifications.size() + i <= 500;
        }
    }

    public Long getSnoozeTimeForUnpostedNotification(int i, String str, String str2) {
        Long l;
        synchronized (this.mLock) {
            l = this.mPersistedSnoozedNotifications.get(getTrimmedString(str2));
        }
        if (l == null) {
            return 0L;
        }
        return l;
    }

    public String getSnoozeContextForUnpostedNotification(int i, String str, String str2) {
        String str3;
        synchronized (this.mLock) {
            str3 = this.mPersistedSnoozedNotificationsWithContext.get(getTrimmedString(str2));
        }
        return str3;
    }

    public boolean isSnoozed(int i, String str, String str2) {
        boolean containsKey;
        synchronized (this.mLock) {
            containsKey = this.mSnoozedNotifications.containsKey(str2);
        }
        return containsKey;
    }

    public Collection<NotificationRecord> getSnoozed(int i, String str) {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList();
            for (NotificationRecord notificationRecord : this.mSnoozedNotifications.values()) {
                if (notificationRecord.getUserId() == i && notificationRecord.getSbn().getPackageName().equals(str)) {
                    arrayList.add(notificationRecord);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<NotificationRecord> getNotifications(String str, String str2, Integer num) {
        ArrayList<NotificationRecord> arrayList = new ArrayList<>();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mSnoozedNotifications.size(); i++) {
                NotificationRecord valueAt = this.mSnoozedNotifications.valueAt(i);
                if (valueAt.getSbn().getPackageName().equals(str) && valueAt.getUserId() == num.intValue() && Objects.equals(valueAt.getSbn().getGroup(), str2)) {
                    arrayList.add(valueAt);
                }
            }
        }
        return arrayList;
    }

    public NotificationRecord getNotification(String str) {
        NotificationRecord notificationRecord;
        synchronized (this.mLock) {
            notificationRecord = this.mSnoozedNotifications.get(str);
        }
        return notificationRecord;
    }

    public List<NotificationRecord> getSnoozed() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList();
            arrayList.addAll(this.mSnoozedNotifications.values());
        }
        return arrayList;
    }

    public void snooze(NotificationRecord notificationRecord, long j) {
        String key = notificationRecord.getKey();
        snooze(notificationRecord);
        scheduleRepost(key, j);
        Long valueOf = Long.valueOf(System.currentTimeMillis() + j);
        synchronized (this.mLock) {
            this.mPersistedSnoozedNotifications.put(getTrimmedString(key), valueOf);
        }
    }

    public void snooze(NotificationRecord notificationRecord, String str) {
        if (str != null) {
            synchronized (this.mLock) {
                this.mPersistedSnoozedNotificationsWithContext.put(getTrimmedString(notificationRecord.getKey()), getTrimmedString(str));
            }
        }
        snooze(notificationRecord);
    }

    public final void snooze(NotificationRecord notificationRecord) {
        if (DEBUG) {
            Slog.d("SnoozeHelper", "Snoozing " + notificationRecord.getKey());
        }
        synchronized (this.mLock) {
            this.mSnoozedNotifications.put(notificationRecord.getKey(), notificationRecord);
        }
    }

    public final String getTrimmedString(String str) {
        return (str == null || str.length() <= 1000) ? str : str.substring(0, 1000);
    }

    public boolean cancel(int i, String str, String str2, int i2) {
        synchronized (this.mLock) {
            for (Map.Entry<String, NotificationRecord> entry : this.mSnoozedNotifications.entrySet()) {
                StatusBarNotification sbn = entry.getValue().getSbn();
                if (sbn.getPackageName().equals(str) && sbn.getUserId() == i && Objects.equals(sbn.getTag(), str2) && sbn.getId() == i2) {
                    entry.getValue().isCanceled = true;
                    return true;
                }
            }
            return false;
        }
    }

    public void cancel(int i, boolean z) {
        synchronized (this.mLock) {
            if (this.mSnoozedNotifications.size() == 0) {
                return;
            }
            IntArray intArray = new IntArray();
            intArray.add(i);
            if (z) {
                intArray = this.mUserProfiles.getCurrentProfileIds();
            }
            for (NotificationRecord notificationRecord : this.mSnoozedNotifications.values()) {
                if (intArray.binarySearch(notificationRecord.getUserId()) >= 0) {
                    notificationRecord.isCanceled = true;
                }
            }
        }
    }

    public boolean cancel(int i, String str) {
        synchronized (this.mLock) {
            int size = this.mSnoozedNotifications.size();
            for (int i2 = 0; i2 < size; i2++) {
                NotificationRecord valueAt = this.mSnoozedNotifications.valueAt(i2);
                if (valueAt.getSbn().getPackageName().equals(str) && valueAt.getUserId() == i) {
                    valueAt.isCanceled = true;
                }
            }
        }
        return true;
    }

    public void update(int i, NotificationRecord notificationRecord) {
        synchronized (this.mLock) {
            if (this.mSnoozedNotifications.containsKey(notificationRecord.getKey())) {
                this.mSnoozedNotifications.put(notificationRecord.getKey(), notificationRecord);
            }
        }
    }

    public void repost(String str, boolean z) {
        synchronized (this.mLock) {
            NotificationRecord notificationRecord = this.mSnoozedNotifications.get(str);
            if (notificationRecord != null) {
                repost(str, notificationRecord.getUserId(), z);
            }
        }
    }

    public void repost(String str, int i, boolean z) {
        NotificationRecord remove;
        String trimmedString = getTrimmedString(str);
        synchronized (this.mLock) {
            this.mPersistedSnoozedNotifications.remove(trimmedString);
            this.mPersistedSnoozedNotificationsWithContext.remove(trimmedString);
            remove = this.mSnoozedNotifications.remove(str);
        }
        if (remove == null || remove.isCanceled) {
            return;
        }
        this.mAm.cancel(createPendingIntent(remove.getKey()));
        MetricsLogger.action(remove.getLogMaker().setCategory(831).setType(1));
        this.mCallback.repost(remove.getUserId(), remove, z);
    }

    public void repostGroupSummary(String str, int i, String str2) {
        String str3;
        final NotificationRecord remove;
        synchronized (this.mLock) {
            int size = this.mSnoozedNotifications.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    str3 = null;
                    break;
                }
                NotificationRecord valueAt = this.mSnoozedNotifications.valueAt(i2);
                if (valueAt.getSbn().getPackageName().equals(str) && valueAt.getUserId() == i && valueAt.getSbn().isGroup() && valueAt.getNotification().isGroupSummary() && str2.equals(valueAt.getGroupKey())) {
                    str3 = valueAt.getKey();
                    break;
                }
                i2++;
            }
            if (str3 != null && (remove = this.mSnoozedNotifications.remove(str3)) != null && !remove.isCanceled) {
                new Runnable() { // from class: com.android.server.notification.SnoozeHelper$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SnoozeHelper.this.lambda$repostGroupSummary$0(remove);
                    }
                }.run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$repostGroupSummary$0(NotificationRecord notificationRecord) {
        MetricsLogger.action(notificationRecord.getLogMaker().setCategory(831).setType(1));
        this.mCallback.repost(notificationRecord.getUserId(), notificationRecord, false);
    }

    public void clearData(int i, String str) {
        synchronized (this.mLock) {
            for (int size = this.mSnoozedNotifications.size() - 1; size >= 0; size--) {
                final NotificationRecord valueAt = this.mSnoozedNotifications.valueAt(size);
                if (valueAt.getUserId() == i && valueAt.getSbn().getPackageName().equals(str)) {
                    this.mSnoozedNotifications.removeAt(size);
                    String trimmedString = getTrimmedString(valueAt.getKey());
                    this.mPersistedSnoozedNotificationsWithContext.remove(trimmedString);
                    this.mPersistedSnoozedNotifications.remove(trimmedString);
                    new Runnable() { // from class: com.android.server.notification.SnoozeHelper$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            SnoozeHelper.this.lambda$clearData$1(valueAt);
                        }
                    }.run();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearData$1(NotificationRecord notificationRecord) {
        this.mAm.cancel(createPendingIntent(notificationRecord.getKey()));
        MetricsLogger.action(notificationRecord.getLogMaker().setCategory(831).setType(5));
    }

    public void clearData(int i) {
        synchronized (this.mLock) {
            for (int size = this.mSnoozedNotifications.size() - 1; size >= 0; size--) {
                final NotificationRecord valueAt = this.mSnoozedNotifications.valueAt(size);
                if (valueAt.getUserId() == i) {
                    this.mSnoozedNotifications.removeAt(size);
                    String trimmedString = getTrimmedString(valueAt.getKey());
                    this.mPersistedSnoozedNotificationsWithContext.remove(trimmedString);
                    this.mPersistedSnoozedNotifications.remove(trimmedString);
                    new Runnable() { // from class: com.android.server.notification.SnoozeHelper$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            SnoozeHelper.this.lambda$clearData$2(valueAt);
                        }
                    }.run();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearData$2(NotificationRecord notificationRecord) {
        this.mAm.cancel(createPendingIntent(notificationRecord.getKey()));
        MetricsLogger.action(notificationRecord.getLogMaker().setCategory(831).setType(5));
    }

    public final PendingIntent createPendingIntent(String str) {
        return PendingIntent.getBroadcast(this.mContext, 1, new Intent(REPOST_ACTION).setPackage(PackageManagerShellCommandDataLoader.PACKAGE).setData(new Uri.Builder().scheme("repost").appendPath(str).build()).addFlags(268435456).putExtra("key", str), 201326592);
    }

    public void scheduleRepostsForPersistedNotifications(long j) {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mPersistedSnoozedNotifications.size(); i++) {
                String keyAt = this.mPersistedSnoozedNotifications.keyAt(i);
                Long valueAt = this.mPersistedSnoozedNotifications.valueAt(i);
                if (valueAt != null && valueAt.longValue() > j) {
                    scheduleRepostAtTime(keyAt, valueAt.longValue());
                }
            }
        }
    }

    public final void scheduleRepost(String str, long j) {
        scheduleRepostAtTime(str, System.currentTimeMillis() + j);
    }

    public final void scheduleRepostAtTime(final String str, final long j) {
        new Runnable() { // from class: com.android.server.notification.SnoozeHelper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SnoozeHelper.this.lambda$scheduleRepostAtTime$3(str, j);
            }
        }.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRepostAtTime$3(String str, long j) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PendingIntent createPendingIntent = createPendingIntent(str);
            this.mAm.cancel(createPendingIntent);
            if (DEBUG) {
                Slog.d("SnoozeHelper", "Scheduling evaluate for " + new Date(j));
            }
            this.mAm.setExactAndAllowWhileIdle(0, j, createPendingIntent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dump(PrintWriter printWriter, NotificationManagerService.DumpFilter dumpFilter) {
        synchronized (this.mLock) {
            printWriter.println("\n  Snoozed notifications:");
            Iterator<String> it = this.mSnoozedNotifications.keySet().iterator();
            while (it.hasNext()) {
                printWriter.print("    ");
                printWriter.println("key: " + it.next());
            }
            printWriter.println("\n Pending snoozed notifications");
            for (String str : this.mPersistedSnoozedNotifications.keySet()) {
                printWriter.print("    ");
                printWriter.println("key: " + str + " until: " + this.mPersistedSnoozedNotifications.get(str));
            }
        }
    }

    public void writeXml(final TypedXmlSerializer typedXmlSerializer) throws IOException {
        synchronized (this.mLock) {
            final long currentTimeMillis = System.currentTimeMillis();
            typedXmlSerializer.startTag((String) null, "snoozed-notifications");
            writeXml(typedXmlSerializer, this.mPersistedSnoozedNotifications, "notification", new Inserter() { // from class: com.android.server.notification.SnoozeHelper$$ExternalSyntheticLambda4
                @Override // com.android.server.notification.SnoozeHelper.Inserter
                public final void insert(Object obj) {
                    SnoozeHelper.lambda$writeXml$4(currentTimeMillis, typedXmlSerializer, (Long) obj);
                }
            });
            writeXml(typedXmlSerializer, this.mPersistedSnoozedNotificationsWithContext, "context", new Inserter() { // from class: com.android.server.notification.SnoozeHelper$$ExternalSyntheticLambda5
                @Override // com.android.server.notification.SnoozeHelper.Inserter
                public final void insert(Object obj) {
                    typedXmlSerializer.attribute(null, "id", (String) obj);
                }
            });
            typedXmlSerializer.endTag((String) null, "snoozed-notifications");
        }
    }

    public static /* synthetic */ void lambda$writeXml$4(long j, TypedXmlSerializer typedXmlSerializer, Long l) throws IOException {
        if (l.longValue() < j) {
            return;
        }
        typedXmlSerializer.attributeLong((String) null, "time", l.longValue());
    }

    public final <T> void writeXml(TypedXmlSerializer typedXmlSerializer, ArrayMap<String, T> arrayMap, String str, Inserter<T> inserter) throws IOException {
        for (int i = 0; i < arrayMap.size(); i++) {
            T valueAt = arrayMap.valueAt(i);
            typedXmlSerializer.startTag((String) null, str);
            inserter.insert(valueAt);
            typedXmlSerializer.attributeInt((String) null, "version", 1);
            typedXmlSerializer.attribute((String) null, "key", arrayMap.keyAt(i));
            typedXmlSerializer.endTag((String) null, str);
        }
    }

    public void readXml(TypedXmlPullParser typedXmlPullParser, long j) throws XmlPullParserException, IOException {
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            String name = typedXmlPullParser.getName();
            if (next == 3 && "snoozed-notifications".equals(name)) {
                return;
            }
            if (next == 2 && ("notification".equals(name) || name.equals("context"))) {
                if (typedXmlPullParser.getAttributeInt((String) null, "version", -1) == 1) {
                    try {
                        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "key");
                        if (name.equals("notification")) {
                            Long valueOf = Long.valueOf(typedXmlPullParser.getAttributeLong((String) null, "time", 0L));
                            if (valueOf.longValue() > j) {
                                synchronized (this.mLock) {
                                    this.mPersistedSnoozedNotifications.put(attributeValue, valueOf);
                                }
                            }
                        }
                        if (name.equals("context")) {
                            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "id");
                            synchronized (this.mLock) {
                                this.mPersistedSnoozedNotificationsWithContext.put(attributeValue, attributeValue2);
                            }
                        }
                    } catch (Exception e) {
                        Slog.e("SnoozeHelper", "Exception in reading snooze data from policy xml", e);
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public void setAlarmManager(AlarmManager alarmManager) {
        this.mAm = alarmManager;
    }
}
