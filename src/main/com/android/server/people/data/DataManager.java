package com.android.server.people.data;

import android.app.NotificationChannel;
import android.app.Person;
import android.app.people.ConversationChannel;
import android.app.people.ConversationStatus;
import android.app.prediction.AppTarget;
import android.app.prediction.AppTargetEvent;
import android.app.usage.UsageEvents;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.telephony.SmsApplication;
import com.android.server.LocalServices;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.notification.NotificationManagerInternal;
import com.android.server.notification.ShortcutHelper;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.people.PeopleService;
import com.android.server.people.data.ConversationInfo;
import com.android.server.people.data.DataManager;
import com.android.server.people.data.UsageStatsQueryHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;
/* loaded from: classes2.dex */
public class DataManager {
    @VisibleForTesting
    static final int MAX_CACHED_RECENT_SHORTCUTS = 30;
    public final SparseArray<BroadcastReceiver> mBroadcastReceivers;
    public ContentObserver mCallLogContentObserver;
    public final SparseArray<ContentObserver> mContactsContentObservers;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public final List<PeopleService.ConversationsListener> mConversationsListeners;
    public final Handler mHandler;
    public final Injector mInjector;
    public final Object mLock;
    public ContentObserver mMmsSmsContentObserver;
    public final SparseArray<NotificationListener> mNotificationListeners;
    public NotificationManagerInternal mNotificationManagerInternal;
    public PackageManagerInternal mPackageManagerInternal;
    public final SparseArray<PackageMonitor> mPackageMonitors;
    public final ScheduledExecutorService mScheduledExecutor;
    public ShortcutServiceInternal mShortcutServiceInternal;
    public ConversationStatusExpirationBroadcastReceiver mStatusExpReceiver;
    public final SparseArray<ScheduledFuture<?>> mUsageStatsQueryFutures;
    public final SparseArray<UserData> mUserDataArray;
    public UserManager mUserManager;

    public DataManager(Context context) {
        this(context, new Injector(), BackgroundThread.get().getLooper());
    }

    public DataManager(Context context, Injector injector, Looper looper) {
        this.mLock = new Object();
        this.mUserDataArray = new SparseArray<>();
        this.mBroadcastReceivers = new SparseArray<>();
        this.mContactsContentObservers = new SparseArray<>();
        this.mUsageStatsQueryFutures = new SparseArray<>();
        this.mNotificationListeners = new SparseArray<>();
        this.mPackageMonitors = new SparseArray<>();
        this.mConversationsListeners = new ArrayList(1);
        this.mContext = context;
        this.mInjector = injector;
        this.mScheduledExecutor = injector.createScheduledExecutor();
        this.mHandler = new Handler(looper);
    }

    public void initialize() {
        this.mShortcutServiceInternal = (ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class);
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mNotificationManagerInternal = (NotificationManagerInternal) LocalServices.getService(NotificationManagerInternal.class);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mShortcutServiceInternal.addShortcutChangeCallback(new ShortcutServiceCallback());
        ConversationStatusExpirationBroadcastReceiver conversationStatusExpirationBroadcastReceiver = new ConversationStatusExpirationBroadcastReceiver();
        this.mStatusExpReceiver = conversationStatusExpirationBroadcastReceiver;
        this.mContext.registerReceiver(conversationStatusExpirationBroadcastReceiver, ConversationStatusExpirationBroadcastReceiver.getFilter(), 4);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
        this.mContext.registerReceiver(new ShutdownBroadcastReceiver(), intentFilter);
    }

    public void onUserUnlocked(final int i) {
        synchronized (this.mLock) {
            UserData userData = this.mUserDataArray.get(i);
            if (userData == null) {
                userData = new UserData(i, this.mScheduledExecutor);
                this.mUserDataArray.put(i, userData);
            }
            userData.setUserUnlocked();
        }
        this.mScheduledExecutor.execute(new Runnable() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DataManager.this.lambda$onUserUnlocked$0(i);
            }
        });
    }

    public void onUserStopping(final int i) {
        synchronized (this.mLock) {
            UserData userData = this.mUserDataArray.get(i);
            if (userData != null) {
                userData.setUserStopped();
            }
        }
        this.mScheduledExecutor.execute(new Runnable() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DataManager.this.lambda$onUserStopping$1(i);
            }
        });
    }

    public void forPackagesInProfile(int i, Consumer<PackageData> consumer) {
        for (UserInfo userInfo : this.mUserManager.getEnabledProfiles(i)) {
            UserData unlockedUserData = getUnlockedUserData(userInfo.id);
            if (unlockedUserData != null) {
                unlockedUserData.forAllPackages(consumer);
            }
        }
    }

    public PackageData getPackage(String str, int i) {
        UserData unlockedUserData = getUnlockedUserData(i);
        if (unlockedUserData != null) {
            return unlockedUserData.getPackageData(str);
        }
        return null;
    }

    public ShortcutInfo getShortcut(String str, int i, String str2) {
        List<ShortcutInfo> shortcuts = getShortcuts(str, i, Collections.singletonList(str2));
        if (shortcuts == null || shortcuts.isEmpty()) {
            return null;
        }
        return shortcuts.get(0);
    }

    public List<ShortcutManager.ShareShortcutInfo> getShareShortcuts(IntentFilter intentFilter, int i) {
        return this.mShortcutServiceInternal.getShareTargets(this.mContext.getPackageName(), intentFilter, i);
    }

    public ConversationChannel getConversation(String str, int i, String str2) {
        PackageData packageData;
        UserData unlockedUserData = getUnlockedUserData(i);
        if (unlockedUserData == null || (packageData = unlockedUserData.getPackageData(str)) == null) {
            return null;
        }
        return getConversationChannel(str, i, str2, packageData.getConversationInfo(str2));
    }

    public final ConversationChannel getConversationChannel(String str, int i, String str2, ConversationInfo conversationInfo) {
        return getConversationChannel(getShortcut(str, i, str2), conversationInfo, str, i, str2);
    }

    public final ConversationChannel getConversationChannel(ShortcutInfo shortcutInfo, ConversationInfo conversationInfo, final String str, final int i, final String str2) {
        if (conversationInfo == null || conversationInfo.isDemoted()) {
            return null;
        }
        if (shortcutInfo == null) {
            Slog.e("DataManager", "Shortcut no longer found");
            this.mInjector.getBackgroundExecutor().execute(new Runnable() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    DataManager.this.lambda$getConversationChannel$2(str, i, str2);
                }
            });
            return null;
        }
        int packageUid = this.mPackageManagerInternal.getPackageUid(str, 0L, i);
        NotificationChannel notificationChannel = this.mNotificationManagerInternal.getNotificationChannel(str, packageUid, conversationInfo.getNotificationChannelId());
        return new ConversationChannel(shortcutInfo, packageUid, notificationChannel, notificationChannel != null ? this.mNotificationManagerInternal.getNotificationChannelGroup(str, packageUid, notificationChannel.getId()) : null, conversationInfo.getLastEventTimestamp(), hasActiveNotifications(str, i, str2), false, getStatuses(conversationInfo));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getConversationChannel$2(String str, int i, String str2) {
        removeConversations(str, i, Set.of(str2));
    }

    public List<ConversationChannel> getRecentConversations(int i) {
        final ArrayList arrayList = new ArrayList();
        forPackagesInProfile(i, new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$getRecentConversations$4(arrayList, (PackageData) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getRecentConversations$4(final List list, final PackageData packageData) {
        packageData.forAllConversations(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$getRecentConversations$3(packageData, list, (ConversationInfo) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getRecentConversations$3(PackageData packageData, List list, ConversationInfo conversationInfo) {
        if (isCachedRecentConversation(conversationInfo)) {
            ConversationChannel conversationChannel = getConversationChannel(packageData.getPackageName(), packageData.getUserId(), conversationInfo.getShortcutId(), conversationInfo);
            if (conversationChannel == null || conversationChannel.getNotificationChannel() == null) {
                return;
            }
            list.add(conversationChannel);
        }
    }

    public void removeRecentConversation(String str, int i, String str2, int i2) {
        if (hasActiveNotifications(str, i, str2)) {
            return;
        }
        this.mShortcutServiceInternal.uncacheShortcuts(i2, this.mContext.getPackageName(), str, Collections.singletonList(str2), i, 16384);
    }

    public void removeAllRecentConversations(int i) {
        pruneOldRecentConversations(i, Long.MAX_VALUE);
    }

    public void pruneOldRecentConversations(final int i, final long j) {
        forPackagesInProfile(i, new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$pruneOldRecentConversations$6(j, i, (PackageData) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pruneOldRecentConversations$6(final long j, int i, PackageData packageData) {
        final String packageName = packageData.getPackageName();
        final int userId = packageData.getUserId();
        final ArrayList arrayList = new ArrayList();
        packageData.forAllConversations(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$pruneOldRecentConversations$5(j, packageName, userId, arrayList, (ConversationInfo) obj);
            }
        });
        if (arrayList.isEmpty()) {
            return;
        }
        this.mShortcutServiceInternal.uncacheShortcuts(i, this.mContext.getPackageName(), packageName, arrayList, userId, 16384);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pruneOldRecentConversations$5(long j, String str, int i, List list, ConversationInfo conversationInfo) {
        String shortcutId = conversationInfo.getShortcutId();
        if (!isCachedRecentConversation(conversationInfo) || j - conversationInfo.getLastEventTimestamp() <= 864000000 || hasActiveNotifications(str, i, shortcutId)) {
            return;
        }
        list.add(shortcutId);
    }

    public void pruneExpiredConversationStatuses(int i, final long j) {
        forPackagesInProfile(i, new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda11
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$pruneExpiredConversationStatuses$8(j, (PackageData) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pruneExpiredConversationStatuses$8(final long j, final PackageData packageData) {
        final ConversationStore conversationStore = packageData.getConversationStore();
        packageData.forAllConversations(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda15
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$pruneExpiredConversationStatuses$7(j, conversationStore, packageData, (ConversationInfo) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pruneExpiredConversationStatuses$7(long j, ConversationStore conversationStore, PackageData packageData, ConversationInfo conversationInfo) {
        ConversationInfo.Builder builder = new ConversationInfo.Builder(conversationInfo);
        ArrayList arrayList = new ArrayList();
        for (ConversationStatus conversationStatus : conversationInfo.getStatuses()) {
            if (conversationStatus.getEndTimeMillis() < 0 || j < conversationStatus.getEndTimeMillis()) {
                arrayList.add(conversationStatus);
            }
        }
        builder.setStatuses(arrayList);
        updateConversationStoreThenNotifyListeners(conversationStore, builder.build(), packageData.getPackageName(), packageData.getUserId());
    }

    public boolean isConversation(String str, int i, String str2) {
        ConversationChannel conversation = getConversation(str, i, str2);
        return (conversation == null || conversation.getShortcutInfo() == null || TextUtils.isEmpty(conversation.getShortcutInfo().getLabel())) ? false : true;
    }

    public long getLastInteraction(String str, int i, String str2) {
        ConversationInfo conversationInfo;
        PackageData packageData = getPackage(str, i);
        if (packageData == null || (conversationInfo = packageData.getConversationInfo(str2)) == null) {
            return 0L;
        }
        return conversationInfo.getLastEventTimestamp();
    }

    public void addOrUpdateStatus(String str, int i, String str2, ConversationStatus conversationStatus) {
        ConversationStore conversationStoreOrThrow = getConversationStoreOrThrow(str, i);
        ConversationInfo.Builder builder = new ConversationInfo.Builder(getConversationInfoOrThrow(conversationStoreOrThrow, str2));
        builder.addOrUpdateStatus(conversationStatus);
        updateConversationStoreThenNotifyListeners(conversationStoreOrThrow, builder.build(), str, i);
        if (conversationStatus.getEndTimeMillis() >= 0) {
            this.mStatusExpReceiver.scheduleExpiration(this.mContext, i, str, str2, conversationStatus);
        }
    }

    public void clearStatus(String str, int i, String str2, String str3) {
        ConversationStore conversationStoreOrThrow = getConversationStoreOrThrow(str, i);
        ConversationInfo.Builder builder = new ConversationInfo.Builder(getConversationInfoOrThrow(conversationStoreOrThrow, str2));
        builder.clearStatus(str3);
        updateConversationStoreThenNotifyListeners(conversationStoreOrThrow, builder.build(), str, i);
    }

    public void clearStatuses(String str, int i, String str2) {
        ConversationStore conversationStoreOrThrow = getConversationStoreOrThrow(str, i);
        ConversationInfo.Builder builder = new ConversationInfo.Builder(getConversationInfoOrThrow(conversationStoreOrThrow, str2));
        builder.setStatuses(null);
        updateConversationStoreThenNotifyListeners(conversationStoreOrThrow, builder.build(), str, i);
    }

    public List<ConversationStatus> getStatuses(String str, int i, String str2) {
        return getStatuses(getConversationInfoOrThrow(getConversationStoreOrThrow(str, i), str2));
    }

    public final List<ConversationStatus> getStatuses(ConversationInfo conversationInfo) {
        Collection<ConversationStatus> statuses = conversationInfo.getStatuses();
        if (statuses != null) {
            ArrayList arrayList = new ArrayList(statuses.size());
            arrayList.addAll(statuses);
            return arrayList;
        }
        return new ArrayList();
    }

    public final ConversationStore getConversationStoreOrThrow(String str, int i) {
        PackageData packageData = getPackage(str, i);
        if (packageData == null) {
            throw new IllegalArgumentException("No settings exist for package " + str);
        }
        ConversationStore conversationStore = packageData.getConversationStore();
        if (conversationStore != null) {
            return conversationStore;
        }
        throw new IllegalArgumentException("No conversations exist for package " + str);
    }

    public final ConversationInfo getConversationInfoOrThrow(ConversationStore conversationStore, String str) {
        ConversationInfo conversation = conversationStore.getConversation(str);
        if (conversation != null) {
            return conversation;
        }
        throw new IllegalArgumentException("Conversation does not exist");
    }

    public void reportShareTargetEvent(AppTargetEvent appTargetEvent, IntentFilter intentFilter) {
        UserData unlockedUserData;
        EventHistoryImpl orCreateEventHistory;
        AppTarget target = appTargetEvent.getTarget();
        if (target == null || appTargetEvent.getAction() != 1 || (unlockedUserData = getUnlockedUserData(target.getUser().getIdentifier())) == null) {
            return;
        }
        PackageData orCreatePackageData = unlockedUserData.getOrCreatePackageData(target.getPackageName());
        int mimeTypeToShareEventType = mimeTypeToShareEventType(intentFilter.getDataType(0));
        if ("direct_share".equals(appTargetEvent.getLaunchLocation())) {
            if (target.getShortcutInfo() == null) {
                return;
            }
            String id = target.getShortcutInfo().getId();
            if ("chooser_target".equals(id)) {
                return;
            }
            if (orCreatePackageData.getConversationStore().getConversation(id) == null) {
                addOrUpdateConversationInfo(target.getShortcutInfo());
            }
            orCreateEventHistory = orCreatePackageData.getEventStore().getOrCreateEventHistory(0, id);
        } else {
            orCreateEventHistory = orCreatePackageData.getEventStore().getOrCreateEventHistory(4, target.getClassName());
        }
        orCreateEventHistory.addEvent(new Event(System.currentTimeMillis(), mimeTypeToShareEventType));
    }

    public List<UsageEvents.Event> queryAppMovingToForegroundEvents(int i, long j, long j2) {
        return UsageStatsQueryHelper.queryAppMovingToForegroundEvents(i, j, j2);
    }

    public Map<String, AppUsageStatsData> queryAppUsageStats(int i, long j, long j2, Set<String> set) {
        return UsageStatsQueryHelper.queryAppUsageStats(i, j, j2, set);
    }

    public void pruneDataForUser(final int i, final CancellationSignal cancellationSignal) {
        UserData unlockedUserData = getUnlockedUserData(i);
        if (unlockedUserData == null || cancellationSignal.isCanceled()) {
            return;
        }
        pruneUninstalledPackageData(unlockedUserData);
        unlockedUserData.forAllPackages(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$pruneDataForUser$9(cancellationSignal, i, (PackageData) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pruneDataForUser$9(CancellationSignal cancellationSignal, int i, PackageData packageData) {
        if (cancellationSignal.isCanceled()) {
            return;
        }
        packageData.getEventStore().pruneOldEvents();
        if (!packageData.isDefaultDialer()) {
            packageData.getEventStore().deleteEventHistories(2);
        }
        if (!packageData.isDefaultSmsApp()) {
            packageData.getEventStore().deleteEventHistories(3);
        }
        packageData.pruneOrphanEvents();
        pruneExpiredConversationStatuses(i, System.currentTimeMillis());
        pruneOldRecentConversations(i, System.currentTimeMillis());
        cleanupCachedShortcuts(i, 30);
    }

    public byte[] getBackupPayload(int i) {
        UserData unlockedUserData = getUnlockedUserData(i);
        if (unlockedUserData == null) {
            return null;
        }
        return unlockedUserData.getBackupPayload();
    }

    public void restore(int i, byte[] bArr) {
        UserData unlockedUserData = getUnlockedUserData(i);
        if (unlockedUserData == null) {
            return;
        }
        unlockedUserData.restore(bArr);
    }

    /* renamed from: setupUser */
    public final void lambda$onUserUnlocked$0(int i) {
        synchronized (this.mLock) {
            UserData unlockedUserData = getUnlockedUserData(i);
            if (unlockedUserData == null) {
                return;
            }
            unlockedUserData.loadUserData();
            updateDefaultDialer(unlockedUserData);
            updateDefaultSmsApp(unlockedUserData);
            this.mUsageStatsQueryFutures.put(i, this.mScheduledExecutor.scheduleAtFixedRate(new UsageStatsQueryRunnable(i), 1L, 120L, TimeUnit.SECONDS));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.telecom.action.DEFAULT_DIALER_CHANGED");
            intentFilter.addAction("android.provider.action.DEFAULT_SMS_PACKAGE_CHANGED_INTERNAL");
            if (this.mBroadcastReceivers.get(i) == null) {
                PerUserBroadcastReceiver perUserBroadcastReceiver = new PerUserBroadcastReceiver(i);
                this.mBroadcastReceivers.put(i, perUserBroadcastReceiver);
                this.mContext.registerReceiverAsUser(perUserBroadcastReceiver, UserHandle.of(i), intentFilter, null, null);
            }
            ContactsContentObserver contactsContentObserver = new ContactsContentObserver(BackgroundThread.getHandler());
            this.mContactsContentObservers.put(i, contactsContentObserver);
            this.mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactsContentObserver, i);
            NotificationListener notificationListener = new NotificationListener(i);
            this.mNotificationListeners.put(i, notificationListener);
            try {
                notificationListener.registerAsSystemService(this.mContext, new ComponentName(this.mContext, getClass()), i);
            } catch (RemoteException unused) {
            }
            if (this.mPackageMonitors.get(i) == null) {
                PerUserPackageMonitor perUserPackageMonitor = new PerUserPackageMonitor();
                perUserPackageMonitor.register(this.mContext, (Looper) null, UserHandle.of(i), true);
                this.mPackageMonitors.put(i, perUserPackageMonitor);
            }
            if (i == 0) {
                this.mCallLogContentObserver = new CallLogContentObserver(BackgroundThread.getHandler());
                this.mContext.getContentResolver().registerContentObserver(CallLog.CONTENT_URI, true, this.mCallLogContentObserver, 0);
                this.mMmsSmsContentObserver = new MmsSmsContentObserver(BackgroundThread.getHandler());
                this.mContext.getContentResolver().registerContentObserver(Telephony.MmsSms.CONTENT_URI, false, this.mMmsSmsContentObserver, 0);
            }
            DataMaintenanceService.scheduleJob(this.mContext, i);
        }
    }

    /* renamed from: cleanupUser */
    public final void lambda$onUserStopping$1(int i) {
        synchronized (this.mLock) {
            UserData userData = this.mUserDataArray.get(i);
            if (userData != null && !userData.isUnlocked()) {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                if (this.mUsageStatsQueryFutures.indexOfKey(i) >= 0) {
                    this.mUsageStatsQueryFutures.get(i).cancel(true);
                }
                if (this.mBroadcastReceivers.indexOfKey(i) >= 0) {
                    this.mContext.unregisterReceiver(this.mBroadcastReceivers.get(i));
                }
                if (this.mContactsContentObservers.indexOfKey(i) >= 0) {
                    contentResolver.unregisterContentObserver(this.mContactsContentObservers.get(i));
                }
                if (this.mNotificationListeners.indexOfKey(i) >= 0) {
                    try {
                        this.mNotificationListeners.get(i).unregisterAsSystemService();
                    } catch (RemoteException unused) {
                    }
                }
                if (this.mPackageMonitors.indexOfKey(i) >= 0) {
                    this.mPackageMonitors.get(i).unregister();
                }
                if (i == 0) {
                    ContentObserver contentObserver = this.mCallLogContentObserver;
                    if (contentObserver != null) {
                        contentResolver.unregisterContentObserver(contentObserver);
                        this.mCallLogContentObserver = null;
                    }
                    ContentObserver contentObserver2 = this.mMmsSmsContentObserver;
                    if (contentObserver2 != null) {
                        contentResolver.unregisterContentObserver(contentObserver2);
                        this.mCallLogContentObserver = null;
                    }
                }
                DataMaintenanceService.cancelJob(this.mContext, i);
            }
        }
    }

    public int mimeTypeToShareEventType(String str) {
        if (str == null) {
            return 7;
        }
        if (str.startsWith("text/")) {
            return 4;
        }
        if (str.startsWith("image/")) {
            return 5;
        }
        return str.startsWith("video/") ? 6 : 7;
    }

    public final void pruneUninstalledPackageData(UserData userData) {
        final ArraySet arraySet = new ArraySet();
        this.mPackageManagerInternal.forEachInstalledPackage(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.lambda$pruneUninstalledPackageData$10(arraySet, (AndroidPackage) obj);
            }
        }, userData.getUserId());
        final ArrayList<String> arrayList = new ArrayList();
        userData.forAllPackages(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda13
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.lambda$pruneUninstalledPackageData$11(arraySet, arrayList, (PackageData) obj);
            }
        });
        for (String str : arrayList) {
            userData.deletePackageData(str);
        }
    }

    public static /* synthetic */ void lambda$pruneUninstalledPackageData$10(Set set, AndroidPackage androidPackage) {
        set.add(androidPackage.getPackageName());
    }

    public static /* synthetic */ void lambda$pruneUninstalledPackageData$11(Set set, List list, PackageData packageData) {
        if (set.contains(packageData.getPackageName())) {
            return;
        }
        list.add(packageData.getPackageName());
    }

    public final List<ShortcutInfo> getShortcuts(String str, int i, List<String> list) {
        return this.mShortcutServiceInternal.getShortcuts(0, this.mContext.getPackageName(), 0L, str, list, (List) null, (ComponentName) null, 3091, i, Process.myPid(), Process.myUid());
    }

    public final void forAllUnlockedUsers(Consumer<UserData> consumer) {
        for (int i = 0; i < this.mUserDataArray.size(); i++) {
            UserData userData = this.mUserDataArray.get(this.mUserDataArray.keyAt(i));
            if (userData.isUnlocked()) {
                consumer.accept(userData);
            }
        }
    }

    public final UserData getUnlockedUserData(int i) {
        UserData userData = this.mUserDataArray.get(i);
        if (userData == null || !userData.isUnlocked()) {
            return null;
        }
        return userData;
    }

    public final void updateDefaultDialer(UserData userData) {
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        userData.setDefaultDialer(telecomManager != null ? telecomManager.getDefaultDialerPackage(new UserHandle(userData.getUserId())) : null);
    }

    public final void updateDefaultSmsApp(UserData userData) {
        ComponentName defaultSmsApplicationAsUser = SmsApplication.getDefaultSmsApplicationAsUser(this.mContext, false, UserHandle.of(userData.getUserId()));
        userData.setDefaultSmsApp(defaultSmsApplicationAsUser != null ? defaultSmsApplicationAsUser.getPackageName() : null);
    }

    public final PackageData getPackageIfConversationExists(StatusBarNotification statusBarNotification, Consumer<ConversationInfo> consumer) {
        PackageData packageData;
        ConversationInfo conversation;
        String shortcutId = statusBarNotification.getNotification().getShortcutId();
        if (shortcutId == null || (packageData = getPackage(statusBarNotification.getPackageName(), statusBarNotification.getUser().getIdentifier())) == null || (conversation = packageData.getConversationStore().getConversation(shortcutId)) == null) {
            return null;
        }
        consumer.accept(conversation);
        return packageData;
    }

    public final boolean isCachedRecentConversation(ConversationInfo conversationInfo) {
        return isEligibleForCleanUp(conversationInfo) && conversationInfo.getLastEventTimestamp() > 0;
    }

    public final boolean isEligibleForCleanUp(ConversationInfo conversationInfo) {
        return conversationInfo.isShortcutCachedForNotification() && Objects.equals(conversationInfo.getNotificationChannelId(), conversationInfo.getParentNotificationChannelId());
    }

    public final boolean hasActiveNotifications(String str, int i, String str2) {
        NotificationListener notificationListener = this.mNotificationListeners.get(i);
        return notificationListener != null && notificationListener.hasActiveNotifications(str, str2);
    }

    public final void cleanupCachedShortcuts(int i, int i2) {
        UserData unlockedUserData = getUnlockedUserData(i);
        if (unlockedUserData == null) {
            return;
        }
        final ArrayList<Pair> arrayList = new ArrayList();
        unlockedUserData.forAllPackages(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$cleanupCachedShortcuts$13(arrayList, (PackageData) obj);
            }
        });
        if (arrayList.size() <= i2) {
            return;
        }
        int size = arrayList.size() - i2;
        PriorityQueue priorityQueue = new PriorityQueue(size + 1, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda7
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                long lambda$cleanupCachedShortcuts$14;
                lambda$cleanupCachedShortcuts$14 = DataManager.lambda$cleanupCachedShortcuts$14((Pair) obj);
                return lambda$cleanupCachedShortcuts$14;
            }
        }).reversed());
        for (Pair pair : arrayList) {
            if (!hasActiveNotifications((String) pair.first, i, ((ConversationInfo) pair.second).getShortcutId())) {
                priorityQueue.offer(pair);
                if (priorityQueue.size() > size) {
                    priorityQueue.poll();
                }
            }
        }
        while (!priorityQueue.isEmpty()) {
            Pair pair2 = (Pair) priorityQueue.poll();
            this.mShortcutServiceInternal.uncacheShortcuts(i, this.mContext.getPackageName(), (String) pair2.first, Collections.singletonList(((ConversationInfo) pair2.second).getShortcutId()), i, 16384);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupCachedShortcuts$13(final List list, final PackageData packageData) {
        packageData.forAllConversations(new Consumer() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda14
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataManager.this.lambda$cleanupCachedShortcuts$12(list, packageData, (ConversationInfo) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupCachedShortcuts$12(List list, PackageData packageData, ConversationInfo conversationInfo) {
        if (isEligibleForCleanUp(conversationInfo)) {
            list.add(Pair.create(packageData.getPackageName(), conversationInfo));
        }
    }

    public static /* synthetic */ long lambda$cleanupCachedShortcuts$14(Pair pair) {
        return Math.max(((ConversationInfo) pair.second).getLastEventTimestamp(), ((ConversationInfo) pair.second).getCreationTimestamp());
    }

    @VisibleForTesting
    public void addOrUpdateConversationInfo(ShortcutInfo shortcutInfo) {
        ConversationInfo.Builder creationTimestamp;
        UserData unlockedUserData = getUnlockedUserData(shortcutInfo.getUserId());
        if (unlockedUserData == null) {
            return;
        }
        ConversationStore conversationStore = unlockedUserData.getOrCreatePackageData(shortcutInfo.getPackage()).getConversationStore();
        ConversationInfo conversation = conversationStore.getConversation(shortcutInfo.getId());
        if (conversation != null) {
            creationTimestamp = new ConversationInfo.Builder(conversation);
        } else {
            creationTimestamp = new ConversationInfo.Builder().setCreationTimestamp(System.currentTimeMillis());
        }
        creationTimestamp.setShortcutId(shortcutInfo.getId());
        creationTimestamp.setLocusId(shortcutInfo.getLocusId());
        creationTimestamp.setShortcutFlags(shortcutInfo.getFlags());
        creationTimestamp.setContactUri(null);
        creationTimestamp.setContactPhoneNumber(null);
        creationTimestamp.setContactStarred(false);
        if (shortcutInfo.getPersons() != null && shortcutInfo.getPersons().length != 0) {
            Person person = shortcutInfo.getPersons()[0];
            creationTimestamp.setPersonImportant(person.isImportant());
            creationTimestamp.setPersonBot(person.isBot());
            String uri = person.getUri();
            if (uri != null) {
                ContactsQueryHelper createContactsQueryHelper = this.mInjector.createContactsQueryHelper(this.mContext);
                if (createContactsQueryHelper.query(uri)) {
                    creationTimestamp.setContactUri(createContactsQueryHelper.getContactUri());
                    creationTimestamp.setContactStarred(createContactsQueryHelper.isStarred());
                    creationTimestamp.setContactPhoneNumber(createContactsQueryHelper.getPhoneNumber());
                }
            }
        }
        updateConversationStoreThenNotifyListeners(conversationStore, creationTimestamp.build(), shortcutInfo);
    }

    @VisibleForTesting
    public ContentObserver getContactsContentObserverForTesting(int i) {
        return this.mContactsContentObservers.get(i);
    }

    @VisibleForTesting
    public ContentObserver getCallLogContentObserverForTesting() {
        return this.mCallLogContentObserver;
    }

    @VisibleForTesting
    public ContentObserver getMmsSmsContentObserverForTesting() {
        return this.mMmsSmsContentObserver;
    }

    @VisibleForTesting
    public NotificationListener getNotificationListenerServiceForTesting(int i) {
        return this.mNotificationListeners.get(i);
    }

    @VisibleForTesting
    public PackageMonitor getPackageMonitorForTesting(int i) {
        return this.mPackageMonitors.get(i);
    }

    @VisibleForTesting
    public UserData getUserDataForTesting(int i) {
        return this.mUserDataArray.get(i);
    }

    /* loaded from: classes2.dex */
    public class ContactsContentObserver extends ContentObserver {
        public long mLastUpdatedTimestamp;

        public ContactsContentObserver(Handler handler) {
            super(handler);
            this.mLastUpdatedTimestamp = System.currentTimeMillis();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            ContactsQueryHelper createContactsQueryHelper = DataManager.this.mInjector.createContactsQueryHelper(DataManager.this.mContext);
            if (createContactsQueryHelper.querySince(this.mLastUpdatedTimestamp)) {
                final Uri contactUri = createContactsQueryHelper.getContactUri();
                final ConversationSelector conversationSelector = new ConversationSelector();
                UserData unlockedUserData = DataManager.this.getUnlockedUserData(i);
                if (unlockedUserData == null) {
                    return;
                }
                unlockedUserData.forAllPackages(new Consumer() { // from class: com.android.server.people.data.DataManager$ContactsContentObserver$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataManager.ContactsContentObserver.lambda$onChange$0(contactUri, conversationSelector, (PackageData) obj);
                    }
                });
                if (conversationSelector.mConversationInfo == null) {
                    return;
                }
                ConversationInfo.Builder builder = new ConversationInfo.Builder(conversationSelector.mConversationInfo);
                builder.setContactStarred(createContactsQueryHelper.isStarred());
                builder.setContactPhoneNumber(createContactsQueryHelper.getPhoneNumber());
                DataManager.this.updateConversationStoreThenNotifyListeners(conversationSelector.mConversationStore, builder.build(), conversationSelector.mPackageName, i);
                this.mLastUpdatedTimestamp = createContactsQueryHelper.getLastUpdatedTimestamp();
            }
        }

        public static /* synthetic */ void lambda$onChange$0(Uri uri, ConversationSelector conversationSelector, PackageData packageData) {
            ConversationInfo conversationByContactUri = packageData.getConversationStore().getConversationByContactUri(uri);
            if (conversationByContactUri != null) {
                conversationSelector.mConversationStore = packageData.getConversationStore();
                conversationSelector.mConversationInfo = conversationByContactUri;
                conversationSelector.mPackageName = packageData.getPackageName();
            }
        }

        /* loaded from: classes2.dex */
        public class ConversationSelector {
            public ConversationInfo mConversationInfo;
            public ConversationStore mConversationStore;
            public String mPackageName;

            public ConversationSelector() {
                this.mConversationStore = null;
                this.mConversationInfo = null;
                this.mPackageName = null;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class CallLogContentObserver extends ContentObserver implements BiConsumer<String, Event> {
        public final CallLogQueryHelper mCallLogQueryHelper;
        public long mLastCallTimestamp;

        public CallLogContentObserver(Handler handler) {
            super(handler);
            this.mCallLogQueryHelper = DataManager.this.mInjector.createCallLogQueryHelper(DataManager.this.mContext, this);
            this.mLastCallTimestamp = System.currentTimeMillis() - BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (this.mCallLogQueryHelper.querySince(this.mLastCallTimestamp)) {
                this.mLastCallTimestamp = this.mCallLogQueryHelper.getLastCallTimestamp();
            }
        }

        @Override // java.util.function.BiConsumer
        public void accept(final String str, final Event event) {
            DataManager.this.forAllUnlockedUsers(new Consumer() { // from class: com.android.server.people.data.DataManager$CallLogContentObserver$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataManager.CallLogContentObserver.lambda$accept$0(str, event, (UserData) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$accept$0(String str, Event event, UserData userData) {
            PackageData defaultDialer = userData.getDefaultDialer();
            if (defaultDialer == null || defaultDialer.getConversationStore().getConversationByPhoneNumber(str) == null) {
                return;
            }
            defaultDialer.getEventStore().getOrCreateEventHistory(2, str).addEvent(event);
        }
    }

    /* loaded from: classes2.dex */
    public class MmsSmsContentObserver extends ContentObserver implements BiConsumer<String, Event> {
        public long mLastMmsTimestamp;
        public long mLastSmsTimestamp;
        public final MmsQueryHelper mMmsQueryHelper;
        public final SmsQueryHelper mSmsQueryHelper;

        public MmsSmsContentObserver(Handler handler) {
            super(handler);
            this.mMmsQueryHelper = DataManager.this.mInjector.createMmsQueryHelper(DataManager.this.mContext, this);
            this.mSmsQueryHelper = DataManager.this.mInjector.createSmsQueryHelper(DataManager.this.mContext, this);
            long currentTimeMillis = System.currentTimeMillis() - BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
            this.mLastMmsTimestamp = currentTimeMillis;
            this.mLastSmsTimestamp = currentTimeMillis;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (this.mMmsQueryHelper.querySince(this.mLastMmsTimestamp)) {
                this.mLastMmsTimestamp = this.mMmsQueryHelper.getLastMessageTimestamp();
            }
            if (this.mSmsQueryHelper.querySince(this.mLastSmsTimestamp)) {
                this.mLastSmsTimestamp = this.mSmsQueryHelper.getLastMessageTimestamp();
            }
        }

        @Override // java.util.function.BiConsumer
        public void accept(final String str, final Event event) {
            DataManager.this.forAllUnlockedUsers(new Consumer() { // from class: com.android.server.people.data.DataManager$MmsSmsContentObserver$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataManager.MmsSmsContentObserver.lambda$accept$0(str, event, (UserData) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$accept$0(String str, Event event, UserData userData) {
            PackageData defaultSmsApp = userData.getDefaultSmsApp();
            if (defaultSmsApp == null || defaultSmsApp.getConversationStore().getConversationByPhoneNumber(str) == null) {
                return;
            }
            defaultSmsApp.getEventStore().getOrCreateEventHistory(3, str).addEvent(event);
        }
    }

    /* loaded from: classes2.dex */
    public class ShortcutServiceCallback implements LauncherApps.ShortcutChangeCallback {
        public ShortcutServiceCallback() {
        }

        public void onShortcutsAddedOrUpdated(final String str, final List<ShortcutInfo> list, final UserHandle userHandle) {
            DataManager.this.mInjector.getBackgroundExecutor().execute(new Runnable() { // from class: com.android.server.people.data.DataManager$ShortcutServiceCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DataManager.ShortcutServiceCallback.this.lambda$onShortcutsAddedOrUpdated$0(str, userHandle, list);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShortcutsAddedOrUpdated$0(String str, UserHandle userHandle, List list) {
            PackageData packageData = DataManager.this.getPackage(str, userHandle.getIdentifier());
            Iterator it = list.iterator();
            boolean z = false;
            while (it.hasNext()) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) it.next();
                if (ShortcutHelper.isConversationShortcut(shortcutInfo, DataManager.this.mShortcutServiceInternal, userHandle.getIdentifier())) {
                    if (shortcutInfo.isCached()) {
                        ConversationInfo conversationInfo = packageData != null ? packageData.getConversationInfo(shortcutInfo.getId()) : null;
                        if (conversationInfo == null || !conversationInfo.isShortcutCachedForNotification()) {
                            z = true;
                        }
                    }
                    DataManager.this.addOrUpdateConversationInfo(shortcutInfo);
                }
            }
            if (z) {
                DataManager.this.cleanupCachedShortcuts(userHandle.getIdentifier(), 30);
            }
        }

        public void onShortcutsRemoved(final String str, final List<ShortcutInfo> list, final UserHandle userHandle) {
            DataManager.this.mInjector.getBackgroundExecutor().execute(new Runnable() { // from class: com.android.server.people.data.DataManager$ShortcutServiceCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DataManager.ShortcutServiceCallback.this.lambda$onShortcutsRemoved$1(list, str, userHandle);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onShortcutsRemoved$1(List list, String str, UserHandle userHandle) {
            HashSet hashSet = new HashSet();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                hashSet.add(((ShortcutInfo) it.next()).getId());
            }
            DataManager.this.removeConversations(str, userHandle.getIdentifier(), hashSet);
        }
    }

    public final void removeConversations(String str, int i, Set<String> set) {
        PackageData packageData = getPackage(str, i);
        if (packageData != null) {
            for (String str2 : set) {
                packageData.deleteDataForConversation(str2);
            }
        }
        try {
            this.mNotificationManagerInternal.onConversationRemoved(str, this.mContext.getPackageManager().getPackageUidAsUser(str, i), set);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("DataManager", "Package not found when removing conversation: " + str, e);
        }
    }

    /* loaded from: classes2.dex */
    public class NotificationListener extends NotificationListenerService {
        @GuardedBy({"this"})
        public final Map<Pair<String, String>, Set<String>> mActiveNotifKeys;
        public final int mUserId;

        public NotificationListener(int i) {
            this.mActiveNotifKeys = new ArrayMap();
            this.mUserId = i;
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationPosted(final StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
            if (statusBarNotification.getUser().getIdentifier() != this.mUserId) {
                return;
            }
            final String shortcutId = statusBarNotification.getNotification().getShortcutId();
            PackageData packageIfConversationExists = DataManager.this.getPackageIfConversationExists(statusBarNotification, new Consumer() { // from class: com.android.server.people.data.DataManager$NotificationListener$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataManager.NotificationListener.this.lambda$onNotificationPosted$1(statusBarNotification, shortcutId, (ConversationInfo) obj);
                }
            });
            if (packageIfConversationExists != null) {
                NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
                rankingMap.getRanking(statusBarNotification.getKey(), ranking);
                ConversationInfo conversationInfo = packageIfConversationExists.getConversationInfo(shortcutId);
                if (conversationInfo == null) {
                    return;
                }
                ConversationInfo.Builder notificationChannelId = new ConversationInfo.Builder(conversationInfo).setLastEventTimestamp(statusBarNotification.getPostTime()).setNotificationChannelId(ranking.getChannel().getId());
                if (!TextUtils.isEmpty(ranking.getChannel().getParentChannelId())) {
                    notificationChannelId.setParentNotificationChannelId(ranking.getChannel().getParentChannelId());
                } else {
                    notificationChannelId.setParentNotificationChannelId(statusBarNotification.getNotification().getChannelId());
                }
                packageIfConversationExists.getConversationStore().addOrUpdate(notificationChannelId.build());
                packageIfConversationExists.getEventStore().getOrCreateEventHistory(0, shortcutId).addEvent(new Event(statusBarNotification.getPostTime(), 2));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNotificationPosted$1(StatusBarNotification statusBarNotification, String str, ConversationInfo conversationInfo) {
            synchronized (this) {
                this.mActiveNotifKeys.computeIfAbsent(Pair.create(statusBarNotification.getPackageName(), str), new Function() { // from class: com.android.server.people.data.DataManager$NotificationListener$$ExternalSyntheticLambda3
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        Set lambda$onNotificationPosted$0;
                        lambda$onNotificationPosted$0 = DataManager.NotificationListener.lambda$onNotificationPosted$0((Pair) obj);
                        return lambda$onNotificationPosted$0;
                    }
                }).add(statusBarNotification.getKey());
            }
        }

        public static /* synthetic */ Set lambda$onNotificationPosted$0(Pair pair) {
            return new HashSet();
        }

        @Override // android.service.notification.NotificationListenerService
        public synchronized void onNotificationRemoved(final StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
            if (statusBarNotification.getUser().getIdentifier() != this.mUserId) {
                return;
            }
            final String shortcutId = statusBarNotification.getNotification().getShortcutId();
            PackageData packageIfConversationExists = DataManager.this.getPackageIfConversationExists(statusBarNotification, new Consumer() { // from class: com.android.server.people.data.DataManager$NotificationListener$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataManager.NotificationListener.this.lambda$onNotificationRemoved$3(statusBarNotification, shortcutId, (ConversationInfo) obj);
                }
            });
            if (i == 1 && packageIfConversationExists != null) {
                long currentTimeMillis = System.currentTimeMillis();
                ConversationInfo conversationInfo = packageIfConversationExists.getConversationInfo(shortcutId);
                if (conversationInfo == null) {
                    return;
                }
                packageIfConversationExists.getConversationStore().addOrUpdate(new ConversationInfo.Builder(conversationInfo).setLastEventTimestamp(currentTimeMillis).build());
                packageIfConversationExists.getEventStore().getOrCreateEventHistory(0, shortcutId).addEvent(new Event(currentTimeMillis, 3));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNotificationRemoved$3(StatusBarNotification statusBarNotification, String str, ConversationInfo conversationInfo) {
            Pair<String, String> create = Pair.create(statusBarNotification.getPackageName(), str);
            synchronized (this) {
                Set<String> computeIfAbsent = this.mActiveNotifKeys.computeIfAbsent(create, new Function() { // from class: com.android.server.people.data.DataManager$NotificationListener$$ExternalSyntheticLambda2
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        Set lambda$onNotificationRemoved$2;
                        lambda$onNotificationRemoved$2 = DataManager.NotificationListener.lambda$onNotificationRemoved$2((Pair) obj);
                        return lambda$onNotificationRemoved$2;
                    }
                });
                computeIfAbsent.remove(statusBarNotification.getKey());
                if (computeIfAbsent.isEmpty()) {
                    this.mActiveNotifKeys.remove(create);
                    DataManager.this.cleanupCachedShortcuts(this.mUserId, 30);
                }
            }
        }

        public static /* synthetic */ Set lambda$onNotificationRemoved$2(Pair pair) {
            return new HashSet();
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationChannelModified(String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) {
            ConversationStore conversationStore;
            ConversationInfo conversation;
            if (userHandle.getIdentifier() != this.mUserId) {
                return;
            }
            PackageData packageData = DataManager.this.getPackage(str, userHandle.getIdentifier());
            String conversationId = notificationChannel.getConversationId();
            if (packageData == null || conversationId == null || (conversation = (conversationStore = packageData.getConversationStore()).getConversation(conversationId)) == null) {
                return;
            }
            ConversationInfo.Builder builder = new ConversationInfo.Builder(conversation);
            if (i == 1 || i == 2) {
                builder.setNotificationChannelId(notificationChannel.getId());
                builder.setImportant(notificationChannel.isImportantConversation());
                builder.setDemoted(notificationChannel.isDemoted());
                builder.setNotificationSilenced(notificationChannel.getImportance() <= 2);
                builder.setBubbled(notificationChannel.canBubble());
            } else if (i == 3) {
                builder.setNotificationChannelId(null);
                builder.setImportant(false);
                builder.setDemoted(false);
                builder.setNotificationSilenced(false);
                builder.setBubbled(false);
            }
            DataManager.this.updateConversationStoreThenNotifyListeners(conversationStore, builder.build(), str, packageData.getUserId());
        }

        public synchronized boolean hasActiveNotifications(String str, String str2) {
            return this.mActiveNotifKeys.containsKey(Pair.create(str, str2));
        }
    }

    /* loaded from: classes2.dex */
    public class UsageStatsQueryRunnable implements Runnable, UsageStatsQueryHelper.EventListener {
        public long mLastEventTimestamp;
        public final UsageStatsQueryHelper mUsageStatsQueryHelper;

        public UsageStatsQueryRunnable(final int i) {
            this.mUsageStatsQueryHelper = DataManager.this.mInjector.createUsageStatsQueryHelper(i, new Function() { // from class: com.android.server.people.data.DataManager$UsageStatsQueryRunnable$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    PackageData lambda$new$0;
                    lambda$new$0 = DataManager.UsageStatsQueryRunnable.this.lambda$new$0(i, (String) obj);
                    return lambda$new$0;
                }
            }, this);
            this.mLastEventTimestamp = System.currentTimeMillis() - BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ PackageData lambda$new$0(int i, String str) {
            return DataManager.this.getPackage(str, i);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mUsageStatsQueryHelper.querySince(this.mLastEventTimestamp)) {
                this.mLastEventTimestamp = this.mUsageStatsQueryHelper.getLastEventTimestamp();
            }
        }

        @Override // com.android.server.people.data.UsageStatsQueryHelper.EventListener
        public void onEvent(PackageData packageData, ConversationInfo conversationInfo, Event event) {
            if (event.getType() == 13) {
                DataManager.this.updateConversationStoreThenNotifyListeners(packageData.getConversationStore(), new ConversationInfo.Builder(conversationInfo).setLastEventTimestamp(event.getTimestamp()).build(), packageData.getPackageName(), packageData.getUserId());
            }
        }
    }

    public void addConversationsListener(PeopleService.ConversationsListener conversationsListener) {
        synchronized (this.mConversationsListeners) {
            List<PeopleService.ConversationsListener> list = this.mConversationsListeners;
            Objects.requireNonNull(conversationsListener);
            list.add(conversationsListener);
        }
    }

    @VisibleForTesting
    public void updateConversationStoreThenNotifyListeners(ConversationStore conversationStore, ConversationInfo conversationInfo, String str, int i) {
        conversationStore.addOrUpdate(conversationInfo);
        ConversationChannel conversationChannel = getConversationChannel(str, i, conversationInfo.getShortcutId(), conversationInfo);
        if (conversationChannel != null) {
            notifyConversationsListeners(Arrays.asList(conversationChannel));
        }
    }

    public final void updateConversationStoreThenNotifyListeners(ConversationStore conversationStore, ConversationInfo conversationInfo, ShortcutInfo shortcutInfo) {
        conversationStore.addOrUpdate(conversationInfo);
        ConversationChannel conversationChannel = getConversationChannel(shortcutInfo, conversationInfo, shortcutInfo.getPackage(), shortcutInfo.getUserId(), shortcutInfo.getId());
        if (conversationChannel != null) {
            notifyConversationsListeners(Arrays.asList(conversationChannel));
        }
    }

    @VisibleForTesting
    public void notifyConversationsListeners(final List<ConversationChannel> list) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.people.data.DataManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DataManager.this.lambda$notifyConversationsListeners$15(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyConversationsListeners$15(List list) {
        ArrayList<PeopleService.ConversationsListener> arrayList;
        try {
            synchronized (this.mLock) {
                arrayList = new ArrayList(this.mConversationsListeners);
            }
            for (PeopleService.ConversationsListener conversationsListener : arrayList) {
                conversationsListener.onConversationsUpdate(list);
            }
        } catch (Exception unused) {
        }
    }

    /* loaded from: classes2.dex */
    public class PerUserBroadcastReceiver extends BroadcastReceiver {
        public final int mUserId;

        public PerUserBroadcastReceiver(int i) {
            this.mUserId = i;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            UserData unlockedUserData = DataManager.this.getUnlockedUserData(this.mUserId);
            if (unlockedUserData == null) {
                return;
            }
            if ("android.telecom.action.DEFAULT_DIALER_CHANGED".equals(intent.getAction())) {
                unlockedUserData.setDefaultDialer(intent.getStringExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME"));
            } else if ("android.provider.action.DEFAULT_SMS_PACKAGE_CHANGED_INTERNAL".equals(intent.getAction())) {
                DataManager.this.updateDefaultSmsApp(unlockedUserData);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class PerUserPackageMonitor extends PackageMonitor {
        public PerUserPackageMonitor() {
        }

        public void onPackageRemoved(String str, int i) {
            super.onPackageRemoved(str, i);
            UserData unlockedUserData = DataManager.this.getUnlockedUserData(getChangingUserId());
            if (unlockedUserData != null) {
                unlockedUserData.deletePackageData(str);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class ShutdownBroadcastReceiver extends BroadcastReceiver {
        public ShutdownBroadcastReceiver() {
        }

        public static /* synthetic */ void lambda$onReceive$0(UserData userData) {
            userData.forAllPackages(new Consumer() { // from class: com.android.server.people.data.DataManager$ShutdownBroadcastReceiver$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PackageData) obj).saveToDisk();
                }
            });
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            DataManager.this.forAllUnlockedUsers(new Consumer() { // from class: com.android.server.people.data.DataManager$ShutdownBroadcastReceiver$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataManager.ShutdownBroadcastReceiver.lambda$onReceive$0((UserData) obj);
                }
            });
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public ScheduledExecutorService createScheduledExecutor() {
            return Executors.newSingleThreadScheduledExecutor();
        }

        public Executor getBackgroundExecutor() {
            return BackgroundThread.getExecutor();
        }

        public ContactsQueryHelper createContactsQueryHelper(Context context) {
            return new ContactsQueryHelper(context);
        }

        public CallLogQueryHelper createCallLogQueryHelper(Context context, BiConsumer<String, Event> biConsumer) {
            return new CallLogQueryHelper(context, biConsumer);
        }

        public MmsQueryHelper createMmsQueryHelper(Context context, BiConsumer<String, Event> biConsumer) {
            return new MmsQueryHelper(context, biConsumer);
        }

        public SmsQueryHelper createSmsQueryHelper(Context context, BiConsumer<String, Event> biConsumer) {
            return new SmsQueryHelper(context, biConsumer);
        }

        public UsageStatsQueryHelper createUsageStatsQueryHelper(int i, Function<String, PackageData> function, UsageStatsQueryHelper.EventListener eventListener) {
            return new UsageStatsQueryHelper(i, function, eventListener);
        }
    }
}
