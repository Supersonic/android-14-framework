package com.android.server.notification;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.metrics.LogMaker;
import android.os.Binder;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.notification.ConversationChannelWrapper;
import android.service.notification.NotificationListenerService;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.StatsEvent;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.notification.NotificationManagerService;
import com.android.server.notification.PermissionHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class PreferencesHelper implements RankingConfig {
    @VisibleForTesting
    static final int DEFAULT_BUBBLE_PREFERENCE = 0;
    @VisibleForTesting
    static final boolean DEFAULT_HIDE_SILENT_STATUS_BAR_ICONS = false;
    @VisibleForTesting
    static final int NOTIFICATION_CHANNEL_COUNT_LIMIT = 5000;
    @VisibleForTesting
    static final int NOTIFICATION_CHANNEL_GROUP_COUNT_LIMIT = 6000;
    @VisibleForTesting
    static final String TAG_RANKING = "ranking";
    @VisibleForTesting
    static final int UNKNOWN_UID = -10000;
    public final AppOpsManager mAppOps;
    public boolean mAreChannelsBypassingDnd;
    public SparseBooleanArray mBadgingEnabled;
    public SparseBooleanArray mBubblesEnabled;
    public final Context mContext;
    public SparseBooleanArray mLockScreenPrivateNotifications;
    public SparseBooleanArray mLockScreenShowNotifications;
    public final NotificationChannelLogger mNotificationChannelLogger;
    public final PermissionHelper mPermissionHelper;
    public final PackageManager mPm;
    public final RankingHandler mRankingHandler;
    public boolean mShowReviewPermissionsNotification;
    public final SysUiStatsEvent$BuilderFactory mStatsEventBuilderFactory;
    public final ZenModeHelper mZenModeHelper;
    public final ArrayMap<String, PackagePreferences> mPackagePreferences = new ArrayMap<>();
    public final ArrayMap<String, PackagePreferences> mRestoredWithoutUids = new ArrayMap<>();
    public boolean mIsMediaNotificationFilteringEnabled = true;
    public boolean mHideSilentStatusBarIcons = false;
    public boolean mAllowInvalidShortcuts = false;
    public final int XML_VERSION = 4;

    public PreferencesHelper(Context context, PackageManager packageManager, RankingHandler rankingHandler, ZenModeHelper zenModeHelper, PermissionHelper permissionHelper, NotificationChannelLogger notificationChannelLogger, AppOpsManager appOpsManager, SysUiStatsEvent$BuilderFactory sysUiStatsEvent$BuilderFactory, boolean z) {
        this.mContext = context;
        this.mZenModeHelper = zenModeHelper;
        this.mRankingHandler = rankingHandler;
        this.mPermissionHelper = permissionHelper;
        this.mPm = packageManager;
        this.mNotificationChannelLogger = notificationChannelLogger;
        this.mAppOps = appOpsManager;
        this.mStatsEventBuilderFactory = sysUiStatsEvent$BuilderFactory;
        this.mShowReviewPermissionsNotification = z;
        updateBadgingEnabled();
        updateBubblesEnabled();
        updateMediaNotificationFilteringEnabled();
        syncChannelsBypassingDnd();
    }

    public void readXml(TypedXmlPullParser typedXmlPullParser, boolean z, int i) throws XmlPullParserException, IOException {
        ArrayMap<String, PackagePreferences> arrayMap;
        if (typedXmlPullParser.getEventType() == 2 && TAG_RANKING.equals(typedXmlPullParser.getName())) {
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "version", -1);
            boolean z2 = attributeInt == 1;
            boolean z3 = attributeInt < 3;
            if (this.mShowReviewPermissionsNotification && attributeInt < 4) {
                Settings.Global.putInt(this.mContext.getContentResolver(), "review_permissions_notification_state", 0);
            }
            ArrayList<PermissionHelper.PackagePermission> arrayList = new ArrayList<>();
            ArrayMap<String, PackagePreferences> arrayMap2 = this.mPackagePreferences;
            synchronized (arrayMap2) {
                while (true) {
                    try {
                        int next = typedXmlPullParser.next();
                        if (next == 1) {
                            break;
                        }
                        String name = typedXmlPullParser.getName();
                        if (next == 3 && TAG_RANKING.equals(name)) {
                            break;
                        } else if (next == 2) {
                            if ("silent_status_icons".equals(name)) {
                                if (!z || i == 0) {
                                    this.mHideSilentStatusBarIcons = typedXmlPullParser.getAttributeBoolean((String) null, "hide_gentle", false);
                                }
                            } else if ("package".equals(name)) {
                                String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
                                if (TextUtils.isEmpty(attributeValue)) {
                                    arrayMap = arrayMap2;
                                } else {
                                    arrayMap = arrayMap2;
                                    try {
                                        restorePackage(typedXmlPullParser, z, i, attributeValue, z2, z3, arrayList);
                                    } catch (Throwable th) {
                                        th = th;
                                        throw th;
                                    }
                                }
                                arrayMap2 = arrayMap;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        arrayMap = arrayMap2;
                    }
                }
                if (z3) {
                    Iterator<PermissionHelper.PackagePermission> it = arrayList.iterator();
                    while (it.hasNext()) {
                        PermissionHelper.PackagePermission next2 = it.next();
                        try {
                            this.mPermissionHelper.setNotificationPermission(next2);
                        } catch (Exception e) {
                            Slog.e("NotificationPrefHelper", "could not migrate setting for " + next2.packageName, e);
                        }
                    }
                }
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(17:1|(2:2|3)|(17:108|109|110|111|112|6|(1:107)(1:11)|(1:13)(1:106)|14|15|16|17|(5:18|(1:1)(6:(7:51|52|(2:54|(1:(8:63|64|65|66|67|68|69|62)(6:57|58|59|60|61|62))(2:70|(1:72)))(1:96)|73|(2:94|95)(2:75|(1:(5:79|80|68|69|62)(5:78|59|60|61|62))(2:81|82))|83|(6:85|(1:91)(1:89)|90|60|61|62)(5:92|93|60|61|62))|99|93|60|61|62)|45|46|47)|25|26|27|(2:29|(4:31|(1:33)(1:38)|34|36)(1:39))(1:40))|5|6|(0)|107|(0)(0)|14|15|16|17|(3:18|(3:20|22|24)(1:101)|62)|25|26|27|(0)(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(18:1|2|3|(17:108|109|110|111|112|6|(1:107)(1:11)|(1:13)(1:106)|14|15|16|17|(5:18|(1:1)(6:(7:51|52|(2:54|(1:(8:63|64|65|66|67|68|69|62)(6:57|58|59|60|61|62))(2:70|(1:72)))(1:96)|73|(2:94|95)(2:75|(1:(5:79|80|68|69|62)(5:78|59|60|61|62))(2:81|82))|83|(6:85|(1:91)(1:89)|90|60|61|62)(5:92|93|60|61|62))|99|93|60|61|62)|45|46|47)|25|26|27|(2:29|(4:31|(1:33)(1:38)|34|36)(1:39))(1:40))|5|6|(0)|107|(0)(0)|14|15|16|17|(3:18|(3:20|22|24)(1:101)|62)|25|26|27|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x01c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01c5, code lost:
        android.util.Slog.e(r7, "deleteDefaultChannelIfNeededLocked - Exception: " + r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0201, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0202, code lost:
        r7 = r22;
     */
    /* JADX WARN: Removed duplicated region for block: B:121:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x004e A[Catch: Exception -> 0x0205, TryCatch #2 {Exception -> 0x0205, blocks: (B:3:0x0017, B:5:0x001d, B:7:0x0023, B:13:0x0032, B:20:0x0056, B:19:0x004e), top: B:100:0x0017 }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01db A[Catch: Exception -> 0x01c1, TryCatch #4 {Exception -> 0x01c1, blocks: (B:41:0x010b, B:45:0x011c, B:47:0x012e, B:49:0x013e, B:51:0x0146, B:54:0x0152, B:55:0x016a, B:57:0x0172, B:59:0x017a, B:61:0x0194, B:63:0x019a, B:65:0x01a1, B:72:0x01bd, B:80:0x01db, B:82:0x01e5, B:86:0x01f4, B:78:0x01c5), top: B:104:0x010b, inners: #1 }] */
    @GuardedBy({"mPackagePreferences"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void restorePackage(TypedXmlPullParser typedXmlPullParser, boolean z, int i, String str, boolean z2, boolean z3, ArrayList<PermissionHelper.PackagePermission> arrayList) {
        String str2;
        int attributeInt;
        int next;
        String str3;
        try {
            attributeInt = typedXmlPullParser.getAttributeInt((String) null, "uid", (int) UNKNOWN_UID);
        } catch (Exception e) {
            e = e;
            str2 = "NotificationPrefHelper";
        }
        if (z) {
            try {
                try {
                    attributeInt = this.mPm.getPackageUidAsUser(str, i);
                } catch (PackageManager.NameNotFoundException unused) {
                }
            } catch (PackageManager.NameNotFoundException unused2) {
            }
            int i2 = attributeInt;
            int attributeInt2 = !(!z2 && i2 != UNKNOWN_UID && this.mAppOps.noteOpNoThrow(24, i2, str, (String) null, "check-notif-bubble") == 0) ? 1 : typedXmlPullParser.getAttributeInt((String) null, "allow_bubble", 0);
            int attributeInt3 = typedXmlPullParser.getAttributeInt((String) null, "importance", -1000);
            String str4 = "uid";
            String str5 = null;
            String str6 = "NotificationPrefHelper";
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i, i2, attributeInt3, typedXmlPullParser.getAttributeInt((String) null, "priority", 0), typedXmlPullParser.getAttributeInt((String) null, "visibility", -1000), typedXmlPullParser.getAttributeBoolean((String) null, "show_badge", true), attributeInt2);
            orCreatePackagePreferencesLocked.priority = typedXmlPullParser.getAttributeInt((String) null, "priority", 0);
            orCreatePackagePreferencesLocked.visibility = typedXmlPullParser.getAttributeInt((String) null, "visibility", -1000);
            orCreatePackagePreferencesLocked.showBadge = typedXmlPullParser.getAttributeBoolean((String) null, "show_badge", true);
            orCreatePackagePreferencesLocked.lockedAppFields = typedXmlPullParser.getAttributeInt((String) null, "app_user_locked_fields", 0);
            orCreatePackagePreferencesLocked.hasSentInvalidMessage = typedXmlPullParser.getAttributeBoolean((String) null, "sent_invalid_msg", false);
            orCreatePackagePreferencesLocked.hasSentValidMessage = typedXmlPullParser.getAttributeBoolean((String) null, "sent_valid_msg", false);
            orCreatePackagePreferencesLocked.userDemotedMsgApp = typedXmlPullParser.getAttributeBoolean((String) null, "user_demote_msg_app", false);
            int depth = typedXmlPullParser.getDepth();
            boolean z4 = false;
            boolean z5 = false;
            while (true) {
                next = typedXmlPullParser.next();
                if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                    break;
                }
                if (next != 3 && next != 4) {
                    String name = typedXmlPullParser.getName();
                    if (!"channelGroup".equals(name)) {
                        str2 = str6;
                    } else if (orCreatePackagePreferencesLocked.groups.size() < NOTIFICATION_CHANNEL_GROUP_COUNT_LIMIT) {
                        str2 = str6;
                        String attributeValue = typedXmlPullParser.getAttributeValue(str5, "id");
                        String attributeValue2 = typedXmlPullParser.getAttributeValue(str5, "name");
                        if (!TextUtils.isEmpty(attributeValue)) {
                            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(attributeValue, attributeValue2);
                            notificationChannelGroup.populateFromXml(typedXmlPullParser);
                            orCreatePackagePreferencesLocked.groups.put(attributeValue, notificationChannelGroup);
                        }
                    } else if (z5) {
                        str2 = str6;
                        str3 = str4;
                        str4 = str3;
                        str6 = str2;
                        str5 = null;
                    } else {
                        str2 = str6;
                        try {
                            Slog.w(str2, "Skipping further groups for " + orCreatePackagePreferencesLocked.pkg);
                            z5 = true;
                            str6 = str2;
                        } catch (Exception e2) {
                            e = e2;
                        }
                    }
                    if ("channel".equals(name)) {
                        if (orCreatePackagePreferencesLocked.channels.size() < NOTIFICATION_CHANNEL_COUNT_LIMIT) {
                            restoreChannel(typedXmlPullParser, z, orCreatePackagePreferencesLocked);
                        } else if (z4) {
                            str3 = str4;
                            str4 = str3;
                            str6 = str2;
                            str5 = null;
                        } else {
                            Slog.w(str2, "Skipping further channels for " + orCreatePackagePreferencesLocked.pkg);
                            z4 = true;
                            str6 = str2;
                        }
                    }
                    if ("delegate".equals(name)) {
                        str3 = str4;
                        int attributeInt4 = typedXmlPullParser.getAttributeInt(str5, str3, (int) UNKNOWN_UID);
                        String readStringAttribute = XmlUtils.readStringAttribute(typedXmlPullParser, "name");
                        orCreatePackagePreferencesLocked.delegate = (attributeInt4 == UNKNOWN_UID || TextUtils.isEmpty(readStringAttribute)) ? null : new Delegate(readStringAttribute, attributeInt4, typedXmlPullParser.getAttributeBoolean(str5, "enabled", true), typedXmlPullParser.getAttributeBoolean(str5, "allowed", true));
                        str4 = str3;
                        str6 = str2;
                        str5 = null;
                    } else {
                        str3 = str4;
                        str4 = str3;
                        str6 = str2;
                        str5 = null;
                    }
                }
                str3 = str4;
                str2 = str6;
                str4 = str3;
                str6 = str2;
                str5 = null;
                e = e2;
                Slog.w(str2, "Failed to restore pkg", e);
                return;
            }
            str2 = str6;
            deleteDefaultChannelIfNeededLocked(orCreatePackagePreferencesLocked);
            if (z3) {
                return;
            }
            orCreatePackagePreferencesLocked.importance = attributeInt3;
            orCreatePackagePreferencesLocked.migrateToPm = true;
            int i3 = orCreatePackagePreferencesLocked.uid;
            if (i3 != UNKNOWN_UID) {
                arrayList.add(new PermissionHelper.PackagePermission(orCreatePackagePreferencesLocked.pkg, UserHandle.getUserId(i3), orCreatePackagePreferencesLocked.importance != 0, hasUserConfiguredSettings(orCreatePackagePreferencesLocked)));
                return;
            }
            return;
        }
        int i22 = attributeInt;
        if (!(!z2 && i22 != UNKNOWN_UID && this.mAppOps.noteOpNoThrow(24, i22, str, (String) null, "check-notif-bubble") == 0)) {
        }
        int attributeInt32 = typedXmlPullParser.getAttributeInt((String) null, "importance", -1000);
        String str42 = "uid";
        String str52 = null;
        String str62 = "NotificationPrefHelper";
        PackagePreferences orCreatePackagePreferencesLocked2 = getOrCreatePackagePreferencesLocked(str, i, i22, attributeInt32, typedXmlPullParser.getAttributeInt((String) null, "priority", 0), typedXmlPullParser.getAttributeInt((String) null, "visibility", -1000), typedXmlPullParser.getAttributeBoolean((String) null, "show_badge", true), attributeInt2);
        orCreatePackagePreferencesLocked2.priority = typedXmlPullParser.getAttributeInt((String) null, "priority", 0);
        orCreatePackagePreferencesLocked2.visibility = typedXmlPullParser.getAttributeInt((String) null, "visibility", -1000);
        orCreatePackagePreferencesLocked2.showBadge = typedXmlPullParser.getAttributeBoolean((String) null, "show_badge", true);
        orCreatePackagePreferencesLocked2.lockedAppFields = typedXmlPullParser.getAttributeInt((String) null, "app_user_locked_fields", 0);
        orCreatePackagePreferencesLocked2.hasSentInvalidMessage = typedXmlPullParser.getAttributeBoolean((String) null, "sent_invalid_msg", false);
        orCreatePackagePreferencesLocked2.hasSentValidMessage = typedXmlPullParser.getAttributeBoolean((String) null, "sent_valid_msg", false);
        orCreatePackagePreferencesLocked2.userDemotedMsgApp = typedXmlPullParser.getAttributeBoolean((String) null, "user_demote_msg_app", false);
        int depth2 = typedXmlPullParser.getDepth();
        boolean z42 = false;
        boolean z52 = false;
        while (true) {
            next = typedXmlPullParser.next();
            if (next == 1) {
                break;
            }
            break;
        }
        str2 = str62;
        deleteDefaultChannelIfNeededLocked(orCreatePackagePreferencesLocked2);
        if (z3) {
        }
    }

    @GuardedBy({"mPackagePreferences"})
    public final void restoreChannel(TypedXmlPullParser typedXmlPullParser, boolean z, PackagePreferences packagePreferences) {
        boolean z2;
        try {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "id");
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "name");
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "importance", -1000);
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return;
            }
            NotificationChannel notificationChannel = new NotificationChannel(attributeValue, attributeValue2, attributeInt);
            if (z) {
                notificationChannel.populateFromXmlForRestore(typedXmlPullParser, this.mContext);
            } else {
                notificationChannel.populateFromXml(typedXmlPullParser);
            }
            if (!packagePreferences.defaultAppLockedImportance && !packagePreferences.fixedImportance) {
                z2 = false;
                notificationChannel.setImportanceLockedByCriticalDeviceFunction(z2);
                if (isShortcutOk(notificationChannel) || !isDeletionOk(notificationChannel)) {
                }
                packagePreferences.channels.put(attributeValue, notificationChannel);
                return;
            }
            z2 = true;
            notificationChannel.setImportanceLockedByCriticalDeviceFunction(z2);
            if (isShortcutOk(notificationChannel)) {
            }
        } catch (Exception e) {
            Slog.w("NotificationPrefHelper", "could not restore channel for " + packagePreferences.pkg, e);
        }
    }

    @GuardedBy({"mPackagePreferences"})
    public final boolean hasUserConfiguredSettings(PackagePreferences packagePreferences) {
        boolean z;
        Iterator<NotificationChannel> it = packagePreferences.channels.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            } else if (it.next().getUserLockedFields() != 0) {
                z = true;
                break;
            }
        }
        return z || packagePreferences.importance == 0;
    }

    public final boolean isShortcutOk(NotificationChannel notificationChannel) {
        boolean z = notificationChannel.getConversationId() != null && notificationChannel.getConversationId().contains(":placeholder_id");
        boolean z2 = this.mAllowInvalidShortcuts;
        if (z2) {
            return true;
        }
        return (z2 || z) ? false : true;
    }

    public final boolean isDeletionOk(NotificationChannel notificationChannel) {
        if (notificationChannel.isDeleted()) {
            return notificationChannel.getDeletedTimeMs() > System.currentTimeMillis() - 2592000000L;
        }
        return true;
    }

    public final PackagePreferences getPackagePreferencesLocked(String str, int i) {
        return this.mPackagePreferences.get(packagePreferencesKey(str, i));
    }

    public final PackagePreferences getOrCreatePackagePreferencesLocked(String str, int i) {
        return getOrCreatePackagePreferencesLocked(str, UserHandle.getUserId(i), i, -1000, 0, -1000, true, 0);
    }

    public final PackagePreferences getOrCreatePackagePreferencesLocked(String str, int i, int i2, int i3, int i4, int i5, boolean z, int i6) {
        PackagePreferences packagePreferences;
        String packagePreferencesKey = packagePreferencesKey(str, i2);
        if (i2 == UNKNOWN_UID) {
            packagePreferences = this.mRestoredWithoutUids.get(unrestoredPackageKey(str, i));
        } else {
            packagePreferences = this.mPackagePreferences.get(packagePreferencesKey);
        }
        if (packagePreferences == null) {
            packagePreferences = new PackagePreferences();
            packagePreferences.pkg = str;
            packagePreferences.uid = i2;
            packagePreferences.importance = i3;
            packagePreferences.priority = i4;
            packagePreferences.visibility = i5;
            packagePreferences.showBadge = z;
            packagePreferences.bubblePreference = i6;
            try {
                createDefaultChannelIfNeededLocked(packagePreferences);
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e("NotificationPrefHelper", "createDefaultChannelIfNeededLocked - Exception: " + e);
            }
            if (packagePreferences.uid == UNKNOWN_UID) {
                this.mRestoredWithoutUids.put(unrestoredPackageKey(str, i), packagePreferences);
            } else {
                this.mPackagePreferences.put(packagePreferencesKey, packagePreferences);
            }
        }
        return packagePreferences;
    }

    public final boolean shouldHaveDefaultChannel(PackagePreferences packagePreferences) throws PackageManager.NameNotFoundException {
        return this.mPm.getApplicationInfoAsUser(packagePreferences.pkg, 0, UserHandle.getUserId(packagePreferences.uid)).targetSdkVersion < 26;
    }

    public final boolean deleteDefaultChannelIfNeededLocked(PackagePreferences packagePreferences) throws PackageManager.NameNotFoundException {
        if (packagePreferences.channels.containsKey("miscellaneous") && !shouldHaveDefaultChannel(packagePreferences)) {
            packagePreferences.channels.remove("miscellaneous");
            return true;
        }
        return false;
    }

    public final boolean createDefaultChannelIfNeededLocked(PackagePreferences packagePreferences) throws PackageManager.NameNotFoundException {
        if (packagePreferences.uid == UNKNOWN_UID) {
            return false;
        }
        if (packagePreferences.channels.containsKey("miscellaneous")) {
            packagePreferences.channels.get("miscellaneous").setName(this.mContext.getString(17040114));
            return false;
        } else if (shouldHaveDefaultChannel(packagePreferences)) {
            NotificationChannel notificationChannel = new NotificationChannel("miscellaneous", this.mContext.getString(17040114), packagePreferences.importance);
            notificationChannel.setBypassDnd(packagePreferences.priority == 2);
            notificationChannel.setLockscreenVisibility(packagePreferences.visibility);
            if (packagePreferences.importance != -1000) {
                notificationChannel.lockFields(4);
            }
            if (packagePreferences.priority != 0) {
                notificationChannel.lockFields(1);
            }
            if (packagePreferences.visibility != -1000) {
                notificationChannel.lockFields(2);
            }
            packagePreferences.channels.put(notificationChannel.getId(), notificationChannel);
            return true;
        } else {
            return false;
        }
    }

    public void writeXml(TypedXmlSerializer typedXmlSerializer, boolean z, int i) throws IOException {
        typedXmlSerializer.startTag((String) null, TAG_RANKING);
        typedXmlSerializer.attributeInt((String) null, "version", this.XML_VERSION);
        if (this.mHideSilentStatusBarIcons && (!z || i == 0)) {
            typedXmlSerializer.startTag((String) null, "silent_status_icons");
            typedXmlSerializer.attributeBoolean((String) null, "hide_gentle", this.mHideSilentStatusBarIcons);
            typedXmlSerializer.endTag((String) null, "silent_status_icons");
        }
        ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap = new ArrayMap<>();
        if (z) {
            arrayMap = this.mPermissionHelper.getNotificationPermissionValues(i);
        }
        synchronized (this.mPackagePreferences) {
            int size = this.mPackagePreferences.size();
            int i2 = 0;
            while (true) {
                int i3 = 3;
                if (i2 >= size) {
                    break;
                }
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i2);
                if (!z || UserHandle.getUserId(valueAt.uid) == i) {
                    typedXmlSerializer.startTag((String) null, "package");
                    typedXmlSerializer.attribute((String) null, "name", valueAt.pkg);
                    if (!arrayMap.isEmpty()) {
                        Pair pair = new Pair(Integer.valueOf(valueAt.uid), valueAt.pkg);
                        Pair<Boolean, Boolean> pair2 = arrayMap.get(pair);
                        if (pair2 == null || !((Boolean) pair2.first).booleanValue()) {
                            i3 = 0;
                        }
                        typedXmlSerializer.attributeInt((String) null, "importance", i3);
                        arrayMap.remove(pair);
                    } else {
                        int i4 = valueAt.importance;
                        if (i4 != -1000) {
                            typedXmlSerializer.attributeInt((String) null, "importance", i4);
                        }
                    }
                    int i5 = valueAt.priority;
                    if (i5 != 0) {
                        typedXmlSerializer.attributeInt((String) null, "priority", i5);
                    }
                    int i6 = valueAt.visibility;
                    if (i6 != -1000) {
                        typedXmlSerializer.attributeInt((String) null, "visibility", i6);
                    }
                    int i7 = valueAt.bubblePreference;
                    if (i7 != 0) {
                        typedXmlSerializer.attributeInt((String) null, "allow_bubble", i7);
                    }
                    typedXmlSerializer.attributeBoolean((String) null, "show_badge", valueAt.showBadge);
                    typedXmlSerializer.attributeInt((String) null, "app_user_locked_fields", valueAt.lockedAppFields);
                    typedXmlSerializer.attributeBoolean((String) null, "sent_invalid_msg", valueAt.hasSentInvalidMessage);
                    typedXmlSerializer.attributeBoolean((String) null, "sent_valid_msg", valueAt.hasSentValidMessage);
                    typedXmlSerializer.attributeBoolean((String) null, "user_demote_msg_app", valueAt.userDemotedMsgApp);
                    if (!z) {
                        typedXmlSerializer.attributeInt((String) null, "uid", valueAt.uid);
                    }
                    if (valueAt.delegate != null) {
                        typedXmlSerializer.startTag((String) null, "delegate");
                        typedXmlSerializer.attribute((String) null, "name", valueAt.delegate.mPkg);
                        typedXmlSerializer.attributeInt((String) null, "uid", valueAt.delegate.mUid);
                        boolean z2 = valueAt.delegate.mEnabled;
                        if (!z2) {
                            typedXmlSerializer.attributeBoolean((String) null, "enabled", z2);
                        }
                        boolean z3 = valueAt.delegate.mUserAllowed;
                        if (!z3) {
                            typedXmlSerializer.attributeBoolean((String) null, "allowed", z3);
                        }
                        typedXmlSerializer.endTag((String) null, "delegate");
                    }
                    for (NotificationChannelGroup notificationChannelGroup : valueAt.groups.values()) {
                        notificationChannelGroup.writeXml(typedXmlSerializer);
                    }
                    for (NotificationChannel notificationChannel : valueAt.channels.values()) {
                        if (z) {
                            if (!notificationChannel.isDeleted()) {
                                notificationChannel.writeXmlForBackup(typedXmlSerializer, this.mContext);
                            }
                        } else {
                            notificationChannel.writeXml(typedXmlSerializer);
                        }
                    }
                    typedXmlSerializer.endTag((String) null, "package");
                }
                i2++;
            }
        }
        if (!arrayMap.isEmpty()) {
            for (Pair<Integer, String> pair3 : arrayMap.keySet()) {
                typedXmlSerializer.startTag((String) null, "package");
                typedXmlSerializer.attribute((String) null, "name", (String) pair3.second);
                typedXmlSerializer.attributeInt((String) null, "importance", ((Boolean) arrayMap.get(pair3).first).booleanValue() ? 3 : 0);
                typedXmlSerializer.endTag((String) null, "package");
            }
        }
        typedXmlSerializer.endTag((String) null, TAG_RANKING);
    }

    public void setBubblesAllowed(String str, int i, int i2) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            z = orCreatePackagePreferencesLocked.bubblePreference != i2;
            orCreatePackagePreferencesLocked.bubblePreference = i2;
            orCreatePackagePreferencesLocked.lockedAppFields |= 2;
        }
        if (z) {
            updateConfig();
        }
    }

    @Override // com.android.server.notification.RankingConfig
    public int getBubblePreference(String str, int i) {
        int i2;
        synchronized (this.mPackagePreferences) {
            i2 = getOrCreatePackagePreferencesLocked(str, i).bubblePreference;
        }
        return i2;
    }

    @Override // com.android.server.notification.RankingConfig
    public boolean canShowBadge(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(str, i).showBadge;
        }
        return z;
    }

    public void setShowBadge(String str, int i, boolean z) {
        synchronized (this.mPackagePreferences) {
            getOrCreatePackagePreferencesLocked(str, i).showBadge = z;
        }
        updateConfig();
    }

    public boolean isInInvalidMsgState(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            z = orCreatePackagePreferencesLocked.hasSentInvalidMessage && !orCreatePackagePreferencesLocked.hasSentValidMessage;
        }
        return z;
    }

    public boolean hasUserDemotedInvalidMsgApp(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = isInInvalidMsgState(str, i) ? getOrCreatePackagePreferencesLocked(str, i).userDemotedMsgApp : false;
        }
        return z;
    }

    public void setInvalidMsgAppDemoted(String str, int i, boolean z) {
        synchronized (this.mPackagePreferences) {
            getOrCreatePackagePreferencesLocked(str, i).userDemotedMsgApp = z;
        }
    }

    public boolean setInvalidMessageSent(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            z = !orCreatePackagePreferencesLocked.hasSentInvalidMessage;
            orCreatePackagePreferencesLocked.hasSentInvalidMessage = true;
        }
        return z;
    }

    public boolean setValidMessageSent(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            z = !orCreatePackagePreferencesLocked.hasSentValidMessage;
            orCreatePackagePreferencesLocked.hasSentValidMessage = true;
        }
        return z;
    }

    @VisibleForTesting
    public boolean hasSentInvalidMsg(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(str, i).hasSentInvalidMessage;
        }
        return z;
    }

    @VisibleForTesting
    public boolean hasSentValidMsg(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(str, i).hasSentValidMessage;
        }
        return z;
    }

    @VisibleForTesting
    public boolean didUserEverDemoteInvalidMsgApp(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(str, i).userDemotedMsgApp;
        }
        return z;
    }

    public boolean setValidBubbleSent(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            z = !orCreatePackagePreferencesLocked.hasSentValidBubble;
            orCreatePackagePreferencesLocked.hasSentValidBubble = true;
        }
        return z;
    }

    public boolean hasSentValidBubble(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(str, i).hasSentValidBubble;
        }
        return z;
    }

    public boolean isImportanceLocked(String str, int i) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            z = orCreatePackagePreferencesLocked.fixedImportance || orCreatePackagePreferencesLocked.defaultAppLockedImportance;
        }
        return z;
    }

    public boolean isGroupBlocked(String str, int i, String str2) {
        if (str2 == null) {
            return false;
        }
        synchronized (this.mPackagePreferences) {
            NotificationChannelGroup notificationChannelGroup = getOrCreatePackagePreferencesLocked(str, i).groups.get(str2);
            if (notificationChannelGroup == null) {
                return false;
            }
            return notificationChannelGroup.isBlocked();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0076 A[Catch: all -> 0x00bd, TryCatch #0 {, blocks: (B:6:0x001c, B:8:0x0022, B:12:0x002f, B:13:0x0032, B:15:0x0041, B:17:0x004a, B:18:0x0060, B:20:0x006a, B:22:0x0070, B:24:0x0076, B:29:0x008b, B:33:0x0094, B:34:0x009d, B:35:0x00a6, B:39:0x00ad, B:40:0x00b4, B:41:0x00b5, B:42:0x00bc), top: B:48:0x001c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void createNotificationChannelGroup(String str, int i, NotificationChannelGroup notificationChannelGroup, boolean z) {
        boolean z2;
        Objects.requireNonNull(str);
        Objects.requireNonNull(notificationChannelGroup);
        Objects.requireNonNull(notificationChannelGroup.getId());
        if (TextUtils.isEmpty(notificationChannelGroup.getName())) {
            throw new IllegalArgumentException("group.getName() can't be empty");
        }
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            if (orCreatePackagePreferencesLocked == null) {
                throw new IllegalArgumentException("Invalid package");
            }
            if (orCreatePackagePreferencesLocked.groups.size() >= NOTIFICATION_CHANNEL_GROUP_COUNT_LIMIT) {
                throw new IllegalStateException("Limit exceed; cannot create more groups");
            }
            if (z) {
                notificationChannelGroup.setBlocked(false);
            }
            NotificationChannelGroup notificationChannelGroup2 = orCreatePackagePreferencesLocked.groups.get(notificationChannelGroup.getId());
            if (notificationChannelGroup2 != null) {
                notificationChannelGroup.setChannels(notificationChannelGroup2.getChannels());
                if (z) {
                    notificationChannelGroup.setBlocked(notificationChannelGroup2.isBlocked());
                    notificationChannelGroup.unlockFields(notificationChannelGroup.getUserLockedFields());
                    notificationChannelGroup.lockFields(notificationChannelGroup2.getUserLockedFields());
                } else if (notificationChannelGroup.isBlocked() != notificationChannelGroup2.isBlocked()) {
                    notificationChannelGroup.lockFields(1);
                    z2 = true;
                    if (!notificationChannelGroup.equals(notificationChannelGroup2)) {
                        MetricsLogger.action(getChannelGroupLog(notificationChannelGroup.getId(), str));
                        this.mNotificationChannelLogger.logNotificationChannelGroup(notificationChannelGroup, i, str, notificationChannelGroup2 == null, notificationChannelGroup2 != null && notificationChannelGroup2.isBlocked());
                    }
                    orCreatePackagePreferencesLocked.groups.put(notificationChannelGroup.getId(), notificationChannelGroup);
                }
            }
            z2 = false;
            if (!notificationChannelGroup.equals(notificationChannelGroup2)) {
            }
            orCreatePackagePreferencesLocked.groups.put(notificationChannelGroup.getId(), notificationChannelGroup);
        }
        if (z2) {
            updateChannelsBypassingDnd();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x01c8 A[Catch: all -> 0x0219, TryCatch #0 {, blocks: (B:10:0x003b, B:12:0x0041, B:14:0x0047, B:17:0x0054, B:18:0x005b, B:19:0x005c, B:21:0x0068, B:24:0x007a, B:26:0x0080, B:28:0x009b, B:30:0x00b2, B:31:0x00be, B:33:0x00cc, B:34:0x00d4, B:36:0x00de, B:37:0x00e6, B:39:0x00ec, B:41:0x00f2, B:42:0x00fa, B:44:0x0108, B:46:0x0112, B:47:0x011a, B:50:0x0122, B:53:0x012e, B:55:0x0135, B:61:0x0145, B:63:0x014b, B:65:0x0155, B:68:0x015c, B:105:0x01fa, B:70:0x016b, B:74:0x0179, B:78:0x0181, B:80:0x0186, B:82:0x018d, B:84:0x0193, B:85:0x0199, B:87:0x01a0, B:92:0x01a8, B:94:0x01b1, B:95:0x01b4, B:97:0x01b8, B:98:0x01bb, B:100:0x01c8, B:101:0x01d7, B:104:0x01e9, B:109:0x0201, B:110:0x0208, B:111:0x0209, B:112:0x0210, B:113:0x0211, B:114:0x0218), top: B:118:0x003b }] */
    /* JADX WARN: Removed duplicated region for block: B:103:0x01e8  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x01b1 A[Catch: all -> 0x0219, TryCatch #0 {, blocks: (B:10:0x003b, B:12:0x0041, B:14:0x0047, B:17:0x0054, B:18:0x005b, B:19:0x005c, B:21:0x0068, B:24:0x007a, B:26:0x0080, B:28:0x009b, B:30:0x00b2, B:31:0x00be, B:33:0x00cc, B:34:0x00d4, B:36:0x00de, B:37:0x00e6, B:39:0x00ec, B:41:0x00f2, B:42:0x00fa, B:44:0x0108, B:46:0x0112, B:47:0x011a, B:50:0x0122, B:53:0x012e, B:55:0x0135, B:61:0x0145, B:63:0x014b, B:65:0x0155, B:68:0x015c, B:105:0x01fa, B:70:0x016b, B:74:0x0179, B:78:0x0181, B:80:0x0186, B:82:0x018d, B:84:0x0193, B:85:0x0199, B:87:0x01a0, B:92:0x01a8, B:94:0x01b1, B:95:0x01b4, B:97:0x01b8, B:98:0x01bb, B:100:0x01c8, B:101:0x01d7, B:104:0x01e9, B:109:0x0201, B:110:0x0208, B:111:0x0209, B:112:0x0210, B:113:0x0211, B:114:0x0218), top: B:118:0x003b }] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01b8 A[Catch: all -> 0x0219, TryCatch #0 {, blocks: (B:10:0x003b, B:12:0x0041, B:14:0x0047, B:17:0x0054, B:18:0x005b, B:19:0x005c, B:21:0x0068, B:24:0x007a, B:26:0x0080, B:28:0x009b, B:30:0x00b2, B:31:0x00be, B:33:0x00cc, B:34:0x00d4, B:36:0x00de, B:37:0x00e6, B:39:0x00ec, B:41:0x00f2, B:42:0x00fa, B:44:0x0108, B:46:0x0112, B:47:0x011a, B:50:0x0122, B:53:0x012e, B:55:0x0135, B:61:0x0145, B:63:0x014b, B:65:0x0155, B:68:0x015c, B:105:0x01fa, B:70:0x016b, B:74:0x0179, B:78:0x0181, B:80:0x0186, B:82:0x018d, B:84:0x0193, B:85:0x0199, B:87:0x01a0, B:92:0x01a8, B:94:0x01b1, B:95:0x01b4, B:97:0x01b8, B:98:0x01bb, B:100:0x01c8, B:101:0x01d7, B:104:0x01e9, B:109:0x0201, B:110:0x0208, B:111:0x0209, B:112:0x0210, B:113:0x0211, B:114:0x0218), top: B:118:0x003b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean createNotificationChannel(String str, int i, NotificationChannel notificationChannel, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean canBypassDnd;
        Objects.requireNonNull(str);
        Objects.requireNonNull(notificationChannel);
        Objects.requireNonNull(notificationChannel.getId());
        boolean z8 = true;
        Preconditions.checkArgument(!TextUtils.isEmpty(notificationChannel.getName()));
        Preconditions.checkArgument(notificationChannel.getImportance() >= 0 && notificationChannel.getImportance() <= 5, "Invalid importance level");
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            if (orCreatePackagePreferencesLocked == null) {
                throw new IllegalArgumentException("Invalid package");
            }
            if (notificationChannel.getGroup() != null && !orCreatePackagePreferencesLocked.groups.containsKey(notificationChannel.getGroup())) {
                throw new IllegalArgumentException("NotificationChannelGroup doesn't exist");
            }
            if ("miscellaneous".equals(notificationChannel.getId())) {
                throw new IllegalArgumentException("Reserved id");
            }
            NotificationChannel notificationChannel2 = orCreatePackagePreferencesLocked.channels.get(notificationChannel.getId());
            if (notificationChannel2 != null && z) {
                if (notificationChannel2.isDeleted()) {
                    notificationChannel2.setDeleted(false);
                    notificationChannel2.setDeletedTimeMs(-1L);
                    MetricsLogger.action(getChannelLog(notificationChannel, str).setType(1));
                    this.mNotificationChannelLogger.logNotificationChannelCreated(notificationChannel, i, str);
                    z5 = true;
                } else {
                    z5 = false;
                }
                boolean z9 = z5;
                if (!Objects.equals(notificationChannel.getName().toString(), notificationChannel2.getName().toString())) {
                    notificationChannel2.setName(notificationChannel.getName().toString());
                    z9 = true;
                }
                if (!Objects.equals(notificationChannel.getDescription(), notificationChannel2.getDescription())) {
                    notificationChannel2.setDescription(notificationChannel.getDescription());
                    z9 = true;
                }
                if (notificationChannel.isBlockable() != notificationChannel2.isBlockable()) {
                    notificationChannel2.setBlockable(notificationChannel.isBlockable());
                    z9 = true;
                }
                if (notificationChannel.getGroup() != null && notificationChannel2.getGroup() == null) {
                    notificationChannel2.setGroup(notificationChannel.getGroup());
                    z9 = true;
                }
                int importance = notificationChannel2.getImportance();
                int loggingImportance = NotificationChannelLogger.getLoggingImportance(notificationChannel2);
                if (notificationChannel2.getUserLockedFields() == 0 && notificationChannel.getImportance() < notificationChannel2.getImportance()) {
                    notificationChannel2.setImportance(notificationChannel.getImportance());
                    z9 = true;
                }
                if (notificationChannel2.getUserLockedFields() == 0 && z2 && ((canBypassDnd = notificationChannel.canBypassDnd()) != notificationChannel2.canBypassDnd() || z5)) {
                    notificationChannel2.setBypassDnd(canBypassDnd);
                    if (canBypassDnd == this.mAreChannelsBypassingDnd && importance == notificationChannel2.getImportance()) {
                        z4 = false;
                        z6 = true;
                    }
                    z6 = true;
                    z4 = true;
                } else {
                    boolean z10 = z9;
                    z4 = false;
                    z6 = z10;
                }
                if (notificationChannel2.getOriginalImportance() == -1000) {
                    notificationChannel2.setOriginalImportance(notificationChannel.getImportance());
                    z7 = true;
                } else {
                    z7 = z6;
                }
                updateConfig();
                if (z7 && !z5) {
                    this.mNotificationChannelLogger.logNotificationChannelModified(notificationChannel2, i, str, loggingImportance, false);
                }
                z8 = z7;
            } else if (orCreatePackagePreferencesLocked.channels.size() >= NOTIFICATION_CHANNEL_COUNT_LIMIT) {
                throw new IllegalStateException("Limit exceed; cannot create more channels");
            } else {
                if (z && !z2) {
                    notificationChannel.setBypassDnd(orCreatePackagePreferencesLocked.priority == 2);
                }
                if (z) {
                    notificationChannel.setLockscreenVisibility(orCreatePackagePreferencesLocked.visibility);
                    notificationChannel.setAllowBubbles(notificationChannel2 != null ? notificationChannel2.getAllowBubbles() : -1);
                    notificationChannel.setImportantConversation(false);
                }
                clearLockedFieldsLocked(notificationChannel);
                if (!orCreatePackagePreferencesLocked.defaultAppLockedImportance && !orCreatePackagePreferencesLocked.fixedImportance) {
                    z3 = false;
                    notificationChannel.setImportanceLockedByCriticalDeviceFunction(z3);
                    if (notificationChannel.getLockscreenVisibility() == 1) {
                        notificationChannel.setLockscreenVisibility(-1000);
                    }
                    if (!orCreatePackagePreferencesLocked.showBadge) {
                        notificationChannel.setShowBadge(false);
                    }
                    notificationChannel.setOriginalImportance(notificationChannel.getImportance());
                    if (notificationChannel.getParentChannelId() != null) {
                        Preconditions.checkArgument(orCreatePackagePreferencesLocked.channels.containsKey(notificationChannel.getParentChannelId()), "Tried to create a conversation channel without a preexisting parent");
                    }
                    orCreatePackagePreferencesLocked.channels.put(notificationChannel.getId(), notificationChannel);
                    boolean z11 = notificationChannel.canBypassDnd() != this.mAreChannelsBypassingDnd;
                    MetricsLogger.action(getChannelLog(notificationChannel, str).setType(1));
                    this.mNotificationChannelLogger.logNotificationChannelCreated(notificationChannel, i, str);
                    z4 = z11;
                }
                z3 = true;
                notificationChannel.setImportanceLockedByCriticalDeviceFunction(z3);
                if (notificationChannel.getLockscreenVisibility() == 1) {
                }
                if (!orCreatePackagePreferencesLocked.showBadge) {
                }
                notificationChannel.setOriginalImportance(notificationChannel.getImportance());
                if (notificationChannel.getParentChannelId() != null) {
                }
                orCreatePackagePreferencesLocked.channels.put(notificationChannel.getId(), notificationChannel);
                if (notificationChannel.canBypassDnd() != this.mAreChannelsBypassingDnd) {
                }
                MetricsLogger.action(getChannelLog(notificationChannel, str).setType(1));
                this.mNotificationChannelLogger.logNotificationChannelCreated(notificationChannel, i, str);
                z4 = z11;
            }
        }
        if (z4) {
            updateChannelsBypassingDnd();
        }
        return z8;
    }

    public void clearLockedFieldsLocked(NotificationChannel notificationChannel) {
        notificationChannel.unlockFields(notificationChannel.getUserLockedFields());
    }

    public void unlockNotificationChannelImportance(String str, int i, String str2) {
        Objects.requireNonNull(str2);
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            if (orCreatePackagePreferencesLocked == null) {
                throw new IllegalArgumentException("Invalid package");
            }
            NotificationChannel notificationChannel = orCreatePackagePreferencesLocked.channels.get(str2);
            if (notificationChannel == null || notificationChannel.isDeleted()) {
                throw new IllegalArgumentException("Channel does not exist");
            }
            notificationChannel.unlockFields(4);
        }
    }

    public void updateNotificationChannel(String str, int i, NotificationChannel notificationChannel, boolean z) {
        boolean z2;
        Objects.requireNonNull(notificationChannel);
        Objects.requireNonNull(notificationChannel.getId());
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            if (orCreatePackagePreferencesLocked == null) {
                throw new IllegalArgumentException("Invalid package");
            }
            NotificationChannel notificationChannel2 = orCreatePackagePreferencesLocked.channels.get(notificationChannel.getId());
            if (notificationChannel2 == null || notificationChannel2.isDeleted()) {
                throw new IllegalArgumentException("Channel does not exist");
            }
            z2 = true;
            if (notificationChannel.getLockscreenVisibility() == 1) {
                notificationChannel.setLockscreenVisibility(-1000);
            }
            if (z) {
                notificationChannel.lockFields(notificationChannel2.getUserLockedFields());
                lockFieldsForUpdateLocked(notificationChannel2, notificationChannel);
            } else {
                notificationChannel.unlockFields(notificationChannel.getUserLockedFields());
            }
            if (notificationChannel2.isImportanceLockedByCriticalDeviceFunction() && !notificationChannel2.isBlockable() && notificationChannel2.getImportance() != 0) {
                notificationChannel.setImportance(notificationChannel2.getImportance());
            }
            orCreatePackagePreferencesLocked.channels.put(notificationChannel.getId(), notificationChannel);
            if (onlyHasDefaultChannel(str, i)) {
                orCreatePackagePreferencesLocked.priority = notificationChannel.canBypassDnd() ? 2 : 0;
                orCreatePackagePreferencesLocked.visibility = notificationChannel.getLockscreenVisibility();
                orCreatePackagePreferencesLocked.showBadge = notificationChannel.canShowBadge();
            }
            if (!notificationChannel2.equals(notificationChannel)) {
                MetricsLogger.action(getChannelLog(notificationChannel, str).setSubtype(z ? 1 : 0));
                this.mNotificationChannelLogger.logNotificationChannelModified(notificationChannel, i, str, NotificationChannelLogger.getLoggingImportance(notificationChannel2), z);
            }
            if (notificationChannel.canBypassDnd() == this.mAreChannelsBypassingDnd && notificationChannel2.getImportance() == notificationChannel.getImportance()) {
                z2 = false;
            }
        }
        if (z2) {
            updateChannelsBypassingDnd();
        }
        updateConfig();
    }

    public NotificationChannel getNotificationChannel(String str, int i, String str2, boolean z) {
        Objects.requireNonNull(str);
        return getConversationNotificationChannel(str, i, str2, null, true, z);
    }

    @Override // com.android.server.notification.RankingConfig
    public NotificationChannel getConversationNotificationChannel(String str, int i, String str2, String str3, boolean z, boolean z2) {
        NotificationChannel notificationChannel;
        Preconditions.checkNotNull(str);
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            if (orCreatePackagePreferencesLocked == null) {
                return null;
            }
            if (str2 == null) {
                str2 = "miscellaneous";
            }
            NotificationChannel findConversationChannel = str3 != null ? findConversationChannel(orCreatePackagePreferencesLocked, str2, str3, z2) : null;
            return (findConversationChannel != null || !z || (notificationChannel = orCreatePackagePreferencesLocked.channels.get(str2)) == null || (!z2 && notificationChannel.isDeleted())) ? findConversationChannel : notificationChannel;
        }
    }

    public final NotificationChannel findConversationChannel(PackagePreferences packagePreferences, String str, String str2, boolean z) {
        for (NotificationChannel notificationChannel : packagePreferences.channels.values()) {
            if (str2.equals(notificationChannel.getConversationId()) && str.equals(notificationChannel.getParentChannelId()) && (z || !notificationChannel.isDeleted())) {
                return notificationChannel;
            }
        }
        return null;
    }

    public boolean deleteNotificationChannel(String str, int i, String str2) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            boolean z2 = false;
            if (packagePreferencesLocked == null) {
                return false;
            }
            NotificationChannel notificationChannel = packagePreferencesLocked.channels.get(str2);
            if (notificationChannel != null) {
                z2 = notificationChannel.canBypassDnd();
                z = deleteNotificationChannelLocked(notificationChannel, str, i);
            } else {
                z = false;
            }
            if (z2) {
                updateChannelsBypassingDnd();
            }
            return z;
        }
    }

    public final boolean deleteNotificationChannelLocked(NotificationChannel notificationChannel, String str, int i) {
        if (notificationChannel.isDeleted()) {
            return false;
        }
        notificationChannel.setDeleted(true);
        notificationChannel.setDeletedTimeMs(System.currentTimeMillis());
        LogMaker channelLog = getChannelLog(notificationChannel, str);
        channelLog.setType(2);
        MetricsLogger.action(channelLog);
        this.mNotificationChannelLogger.logNotificationChannelDeleted(notificationChannel, i, str);
        return true;
    }

    @VisibleForTesting
    public void permanentlyDeleteNotificationChannel(String str, int i, String str2) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(str2);
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return;
            }
            packagePreferencesLocked.channels.remove(str2);
        }
    }

    public boolean shouldHideSilentStatusIcons() {
        return this.mHideSilentStatusBarIcons;
    }

    public void setHideSilentStatusIcons(boolean z) {
        this.mHideSilentStatusBarIcons = z;
    }

    public void updateFixedImportance(List<UserInfo> list) {
        for (UserInfo userInfo : list) {
            for (PackageInfo packageInfo : this.mPm.getInstalledPackagesAsUser(PackageManager.PackageInfoFlags.of(1048576L), userInfo.getUserHandle().getIdentifier())) {
                if (this.mPermissionHelper.isPermissionFixed(packageInfo.packageName, userInfo.getUserHandle().getIdentifier())) {
                    synchronized (this.mPackagePreferences) {
                        PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(packageInfo.packageName, packageInfo.applicationInfo.uid);
                        orCreatePackagePreferencesLocked.fixedImportance = true;
                        for (NotificationChannel notificationChannel : orCreatePackagePreferencesLocked.channels.values()) {
                            notificationChannel.setImportanceLockedByCriticalDeviceFunction(true);
                        }
                    }
                }
            }
        }
    }

    public void updateDefaultApps(int i, ArraySet<String> arraySet, ArraySet<Pair<String, Integer>> arraySet2) {
        synchronized (this.mPackagePreferences) {
            for (PackagePreferences packagePreferences : this.mPackagePreferences.values()) {
                if (i == UserHandle.getUserId(packagePreferences.uid) && arraySet != null && arraySet.contains(packagePreferences.pkg)) {
                    packagePreferences.defaultAppLockedImportance = false;
                    if (!packagePreferences.fixedImportance) {
                        for (NotificationChannel notificationChannel : packagePreferences.channels.values()) {
                            notificationChannel.setImportanceLockedByCriticalDeviceFunction(false);
                        }
                    }
                }
            }
            if (arraySet2 != null) {
                Iterator<Pair<String, Integer>> it = arraySet2.iterator();
                while (it.hasNext()) {
                    Pair<String, Integer> next = it.next();
                    PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked((String) next.first, ((Integer) next.second).intValue());
                    orCreatePackagePreferencesLocked.defaultAppLockedImportance = true;
                    for (NotificationChannel notificationChannel2 : orCreatePackagePreferencesLocked.channels.values()) {
                        notificationChannel2.setImportanceLockedByCriticalDeviceFunction(true);
                    }
                }
            }
        }
    }

    public NotificationChannelGroup getNotificationChannelGroupWithChannels(String str, int i, String str2, boolean z) {
        Objects.requireNonNull(str);
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked != null && str2 != null && packagePreferencesLocked.groups.containsKey(str2)) {
                NotificationChannelGroup clone = packagePreferencesLocked.groups.get(str2).clone();
                clone.setChannels(new ArrayList());
                int size = packagePreferencesLocked.channels.size();
                for (int i2 = 0; i2 < size; i2++) {
                    NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i2);
                    if ((z || !valueAt.isDeleted()) && str2.equals(valueAt.getGroup())) {
                        clone.addChannel(valueAt);
                    }
                }
                return clone;
            }
            return null;
        }
    }

    public NotificationChannelGroup getNotificationChannelGroup(String str, String str2, int i) {
        Objects.requireNonNull(str2);
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str2, i);
            if (packagePreferencesLocked == null) {
                return null;
            }
            return packagePreferencesLocked.groups.get(str);
        }
    }

    public ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroups(String str, int i, boolean z, boolean z2, boolean z3) {
        Objects.requireNonNull(str);
        ArrayMap arrayMap = new ArrayMap();
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return ParceledListSlice.emptyList();
            }
            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(null, null);
            int size = packagePreferencesLocked.channels.size();
            for (int i2 = 0; i2 < size; i2++) {
                NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i2);
                if (z || !valueAt.isDeleted()) {
                    if (valueAt.getGroup() != null) {
                        if (packagePreferencesLocked.groups.get(valueAt.getGroup()) != null) {
                            NotificationChannelGroup notificationChannelGroup2 = (NotificationChannelGroup) arrayMap.get(valueAt.getGroup());
                            if (notificationChannelGroup2 == null) {
                                notificationChannelGroup2 = packagePreferencesLocked.groups.get(valueAt.getGroup()).clone();
                                notificationChannelGroup2.setChannels(new ArrayList());
                                arrayMap.put(valueAt.getGroup(), notificationChannelGroup2);
                            }
                            notificationChannelGroup2.addChannel(valueAt);
                        }
                    } else {
                        notificationChannelGroup.addChannel(valueAt);
                    }
                }
            }
            if (z2 && notificationChannelGroup.getChannels().size() > 0) {
                arrayMap.put(null, notificationChannelGroup);
            }
            if (z3) {
                for (NotificationChannelGroup notificationChannelGroup3 : packagePreferencesLocked.groups.values()) {
                    if (!arrayMap.containsKey(notificationChannelGroup3.getId())) {
                        arrayMap.put(notificationChannelGroup3.getId(), notificationChannelGroup3);
                    }
                }
            }
            return new ParceledListSlice<>(new ArrayList(arrayMap.values()));
        }
    }

    public List<NotificationChannel> deleteNotificationChannelGroup(String str, int i, String str2) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked != null && !TextUtils.isEmpty(str2)) {
                NotificationChannelGroup remove = packagePreferencesLocked.groups.remove(str2);
                if (remove != null) {
                    this.mNotificationChannelLogger.logNotificationChannelGroupDeleted(remove, i, str);
                }
                int size = packagePreferencesLocked.channels.size();
                boolean z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i2);
                    if (str2.equals(valueAt.getGroup())) {
                        z |= valueAt.canBypassDnd();
                        deleteNotificationChannelLocked(valueAt, str, i);
                        arrayList.add(valueAt);
                    }
                }
                if (z) {
                    updateChannelsBypassingDnd();
                }
                return arrayList;
            }
            return arrayList;
        }
    }

    public Collection<NotificationChannelGroup> getNotificationChannelGroups(String str, int i) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return arrayList;
            }
            arrayList.addAll(packagePreferencesLocked.groups.values());
            return arrayList;
        }
    }

    public NotificationChannelGroup getGroupForChannel(String str, int i, String str2) {
        NotificationChannel notificationChannel;
        NotificationChannelGroup notificationChannelGroup;
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null || (notificationChannel = packagePreferencesLocked.channels.get(str2)) == null || notificationChannel.isDeleted() || notificationChannel.getGroup() == null || (notificationChannelGroup = packagePreferencesLocked.groups.get(notificationChannel.getGroup())) == null) {
                return null;
            }
            return notificationChannelGroup;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00a9 A[Catch: all -> 0x00b1, TryCatch #0 {, blocks: (B:4:0x0003, B:5:0x0012, B:7:0x0018, B:9:0x002a, B:11:0x0034, B:13:0x0046, B:15:0x004c, B:17:0x0052, B:20:0x005a, B:24:0x0080, B:26:0x0089, B:28:0x0097, B:34:0x00a9, B:31:0x009f, B:23:0x007c, B:35:0x00ac, B:36:0x00af), top: B:41:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00ac A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ArrayList<ConversationChannelWrapper> getConversations(IntArray intArray, boolean z) {
        ArrayList<ConversationChannelWrapper> arrayList;
        boolean z2;
        NotificationChannelGroup notificationChannelGroup;
        synchronized (this.mPackagePreferences) {
            arrayList = new ArrayList<>();
            for (PackagePreferences packagePreferences : this.mPackagePreferences.values()) {
                if (intArray.binarySearch(UserHandle.getUserId(packagePreferences.uid)) >= 0) {
                    int size = packagePreferences.channels.size();
                    for (int i = 0; i < size; i++) {
                        NotificationChannel valueAt = packagePreferences.channels.valueAt(i);
                        if (!TextUtils.isEmpty(valueAt.getConversationId()) && !valueAt.isDeleted() && !valueAt.isDemoted() && (valueAt.isImportantConversation() || !z)) {
                            ConversationChannelWrapper conversationChannelWrapper = new ConversationChannelWrapper();
                            conversationChannelWrapper.setPkg(packagePreferences.pkg);
                            conversationChannelWrapper.setUid(packagePreferences.uid);
                            conversationChannelWrapper.setNotificationChannel(valueAt);
                            NotificationChannel notificationChannel = packagePreferences.channels.get(valueAt.getParentChannelId());
                            conversationChannelWrapper.setParentChannelLabel(notificationChannel == null ? null : notificationChannel.getName());
                            if (valueAt.getGroup() != null && (notificationChannelGroup = packagePreferences.groups.get(valueAt.getGroup())) != null) {
                                if (notificationChannelGroup.isBlocked()) {
                                    z2 = true;
                                    if (z2) {
                                        arrayList.add(conversationChannelWrapper);
                                    }
                                } else {
                                    conversationChannelWrapper.setGroupLabel(notificationChannelGroup.getName());
                                }
                            }
                            z2 = false;
                            if (z2) {
                            }
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x008b A[Catch: all -> 0x0093, TryCatch #0 {, blocks: (B:4:0x0006, B:6:0x000c, B:7:0x0011, B:9:0x0013, B:11:0x0022, B:13:0x0034, B:15:0x003a, B:17:0x0040, B:19:0x006b, B:21:0x0079, B:27:0x008b, B:24:0x0081, B:28:0x008e, B:29:0x0091), top: B:34:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x008e A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ArrayList<ConversationChannelWrapper> getConversations(String str, int i) {
        boolean z;
        NotificationChannelGroup notificationChannelGroup;
        Objects.requireNonNull(str);
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return new ArrayList<>();
            }
            ArrayList<ConversationChannelWrapper> arrayList = new ArrayList<>();
            int size = packagePreferencesLocked.channels.size();
            for (int i2 = 0; i2 < size; i2++) {
                NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i2);
                if (!TextUtils.isEmpty(valueAt.getConversationId()) && !valueAt.isDeleted() && !valueAt.isDemoted()) {
                    ConversationChannelWrapper conversationChannelWrapper = new ConversationChannelWrapper();
                    conversationChannelWrapper.setPkg(packagePreferencesLocked.pkg);
                    conversationChannelWrapper.setUid(packagePreferencesLocked.uid);
                    conversationChannelWrapper.setNotificationChannel(valueAt);
                    conversationChannelWrapper.setParentChannelLabel(packagePreferencesLocked.channels.get(valueAt.getParentChannelId()).getName());
                    if (valueAt.getGroup() != null && (notificationChannelGroup = packagePreferencesLocked.groups.get(valueAt.getGroup())) != null) {
                        if (notificationChannelGroup.isBlocked()) {
                            z = true;
                            if (z) {
                                arrayList.add(conversationChannelWrapper);
                            }
                        } else {
                            conversationChannelWrapper.setGroupLabel(notificationChannelGroup.getName());
                        }
                    }
                    z = false;
                    if (z) {
                    }
                }
            }
            return arrayList;
        }
    }

    public List<String> deleteConversations(String str, int i, Set<String> set) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return arrayList;
            }
            int size = packagePreferencesLocked.channels.size();
            for (int i2 = 0; i2 < size; i2++) {
                NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i2);
                if (valueAt.getConversationId() != null && set.contains(valueAt.getConversationId())) {
                    valueAt.setDeleted(true);
                    valueAt.setDeletedTimeMs(System.currentTimeMillis());
                    LogMaker channelLog = getChannelLog(valueAt, str);
                    channelLog.setType(2);
                    MetricsLogger.action(channelLog);
                    this.mNotificationChannelLogger.logNotificationChannelDeleted(valueAt, i, str);
                    arrayList.add(valueAt.getId());
                }
            }
            if (!arrayList.isEmpty() && this.mAreChannelsBypassingDnd) {
                updateChannelsBypassingDnd();
            }
            return arrayList;
        }
    }

    public ParceledListSlice<NotificationChannel> getNotificationChannels(String str, int i, boolean z) {
        Objects.requireNonNull(str);
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return ParceledListSlice.emptyList();
            }
            int size = packagePreferencesLocked.channels.size();
            for (int i2 = 0; i2 < size; i2++) {
                NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i2);
                if (z || !valueAt.isDeleted()) {
                    arrayList.add(valueAt);
                }
            }
            return new ParceledListSlice<>(arrayList);
        }
    }

    public ParceledListSlice<NotificationChannel> getNotificationChannelsBypassingDnd(String str, int i) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferences = this.mPackagePreferences.get(packagePreferencesKey(str, i));
            if (packagePreferences != null) {
                for (NotificationChannel notificationChannel : packagePreferences.channels.values()) {
                    if (channelIsLiveLocked(packagePreferences, notificationChannel) && notificationChannel.canBypassDnd()) {
                        arrayList.add(notificationChannel);
                    }
                }
            }
        }
        return new ParceledListSlice<>(arrayList);
    }

    public boolean onlyHasDefaultChannel(String str, int i) {
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            return orCreatePackagePreferencesLocked.channels.size() == 1 && orCreatePackagePreferencesLocked.channels.containsKey("miscellaneous");
        }
    }

    public int getDeletedChannelCount(String str, int i) {
        Objects.requireNonNull(str);
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return 0;
            }
            int size = packagePreferencesLocked.channels.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                if (packagePreferencesLocked.channels.valueAt(i3).isDeleted()) {
                    i2++;
                }
            }
            return i2;
        }
    }

    public int getBlockedChannelCount(String str, int i) {
        Objects.requireNonNull(str);
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked == null) {
                return 0;
            }
            int size = packagePreferencesLocked.channels.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                NotificationChannel valueAt = packagePreferencesLocked.channels.valueAt(i3);
                if (!valueAt.isDeleted() && valueAt.getImportance() == 0) {
                    i2++;
                }
            }
            return i2;
        }
    }

    public final void syncChannelsBypassingDnd() {
        this.mAreChannelsBypassingDnd = (this.mZenModeHelper.getNotificationPolicy().state & 1) == 1;
        updateChannelsBypassingDnd();
    }

    public final void updateChannelsBypassingDnd() {
        ArraySet arraySet = new ArraySet();
        int currentUser = getCurrentUser();
        synchronized (this.mPackagePreferences) {
            int size = this.mPackagePreferences.size();
            for (int i = 0; i < size; i++) {
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i);
                if (currentUser == UserHandle.getUserId(valueAt.uid)) {
                    Iterator<NotificationChannel> it = valueAt.channels.values().iterator();
                    while (true) {
                        if (it.hasNext()) {
                            NotificationChannel next = it.next();
                            if (channelIsLiveLocked(valueAt, next) && next.canBypassDnd()) {
                                arraySet.add(new Pair(valueAt.pkg, Integer.valueOf(valueAt.uid)));
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
            if (!this.mPermissionHelper.hasPermission(((Integer) ((Pair) arraySet.valueAt(size2)).second).intValue())) {
                arraySet.removeAt(size2);
            }
        }
        boolean z = arraySet.size() > 0;
        if (this.mAreChannelsBypassingDnd != z) {
            this.mAreChannelsBypassingDnd = z;
            updateZenPolicy(z);
        }
    }

    public final int getCurrentUser() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        int currentUser = ActivityManager.getCurrentUser();
        Binder.restoreCallingIdentity(clearCallingIdentity);
        return currentUser;
    }

    public final boolean channelIsLiveLocked(PackagePreferences packagePreferences, NotificationChannel notificationChannel) {
        return (isGroupBlocked(packagePreferences.pkg, packagePreferences.uid, notificationChannel.getGroup()) || notificationChannel.isDeleted() || notificationChannel.getImportance() == 0) ? false : true;
    }

    public void updateZenPolicy(boolean z) {
        NotificationManager.Policy notificationPolicy = this.mZenModeHelper.getNotificationPolicy();
        this.mZenModeHelper.setNotificationPolicy(new NotificationManager.Policy(notificationPolicy.priorityCategories, notificationPolicy.priorityCallSenders, notificationPolicy.priorityMessageSenders, notificationPolicy.suppressedVisualEffects, z ? 1 : 0, notificationPolicy.priorityConversationSenders));
    }

    public boolean areChannelsBypassingDnd() {
        return this.mAreChannelsBypassingDnd;
    }

    public String getNotificationDelegate(String str, int i) {
        Delegate delegate;
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked != null && (delegate = packagePreferencesLocked.delegate) != null) {
                if (delegate.mUserAllowed && delegate.mEnabled) {
                    return delegate.mPkg;
                }
                return null;
            }
            return null;
        }
    }

    public void setNotificationDelegate(String str, int i, String str2, int i2) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences orCreatePackagePreferencesLocked = getOrCreatePackagePreferencesLocked(str, i);
            Delegate delegate = orCreatePackagePreferencesLocked.delegate;
            if (delegate != null && !delegate.mUserAllowed) {
                z = false;
                orCreatePackagePreferencesLocked.delegate = new Delegate(str2, i2, true, z);
            }
            z = true;
            orCreatePackagePreferencesLocked.delegate = new Delegate(str2, i2, true, z);
        }
        updateConfig();
    }

    public void revokeNotificationDelegate(String str, int i) {
        boolean z;
        Delegate delegate;
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            z = false;
            if (packagePreferencesLocked != null && (delegate = packagePreferencesLocked.delegate) != null) {
                delegate.mEnabled = false;
                z = true;
            }
        }
        if (z) {
            updateConfig();
        }
    }

    public boolean isDelegateAllowed(String str, int i, String str2, int i2) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            z = packagePreferencesLocked != null && packagePreferencesLocked.isValidDelegate(str2, i2);
        }
        return z;
    }

    @VisibleForTesting
    public void lockFieldsForUpdateLocked(NotificationChannel notificationChannel, NotificationChannel notificationChannel2) {
        if (notificationChannel.canBypassDnd() != notificationChannel2.canBypassDnd()) {
            notificationChannel2.lockFields(1);
        }
        if (notificationChannel.getLockscreenVisibility() != notificationChannel2.getLockscreenVisibility()) {
            notificationChannel2.lockFields(2);
        }
        if (notificationChannel.getImportance() != notificationChannel2.getImportance()) {
            notificationChannel2.lockFields(4);
        }
        if (notificationChannel.shouldShowLights() != notificationChannel2.shouldShowLights() || notificationChannel.getLightColor() != notificationChannel2.getLightColor()) {
            notificationChannel2.lockFields(8);
        }
        if (!Objects.equals(notificationChannel.getSound(), notificationChannel2.getSound())) {
            notificationChannel2.lockFields(32);
        }
        if (!Arrays.equals(notificationChannel.getVibrationPattern(), notificationChannel2.getVibrationPattern()) || notificationChannel.shouldVibrate() != notificationChannel2.shouldVibrate()) {
            notificationChannel2.lockFields(16);
        }
        if (notificationChannel.canShowBadge() != notificationChannel2.canShowBadge()) {
            notificationChannel2.lockFields(128);
        }
        if (notificationChannel.getAllowBubbles() != notificationChannel2.getAllowBubbles()) {
            notificationChannel2.lockFields(256);
        }
    }

    public void dump(PrintWriter printWriter, String str, NotificationManagerService.DumpFilter dumpFilter, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap) {
        printWriter.print(str);
        printWriter.println("per-package config version: " + this.XML_VERSION);
        printWriter.println("PackagePreferences:");
        synchronized (this.mPackagePreferences) {
            dumpPackagePreferencesLocked(printWriter, str, dumpFilter, this.mPackagePreferences, arrayMap);
        }
        printWriter.println("Restored without uid:");
        dumpPackagePreferencesLocked(printWriter, str, dumpFilter, this.mRestoredWithoutUids, (ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>>) null);
    }

    public void dump(ProtoOutputStream protoOutputStream, NotificationManagerService.DumpFilter dumpFilter, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap) {
        synchronized (this.mPackagePreferences) {
            dumpPackagePreferencesLocked(protoOutputStream, 2246267895810L, dumpFilter, this.mPackagePreferences, arrayMap);
        }
        dumpPackagePreferencesLocked(protoOutputStream, 2246267895811L, dumpFilter, this.mRestoredWithoutUids, (ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>>) null);
    }

    public final void dumpPackagePreferencesLocked(PrintWriter printWriter, String str, NotificationManagerService.DumpFilter dumpFilter, ArrayMap<String, PackagePreferences> arrayMap, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap2) {
        Set<Pair<Integer, String>> keySet = arrayMap2 != null ? arrayMap2.keySet() : null;
        int size = arrayMap.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            PackagePreferences valueAt = arrayMap.valueAt(i);
            if (dumpFilter.matches(valueAt.pkg)) {
                printWriter.print(str);
                printWriter.print("  AppSettings: ");
                printWriter.print(valueAt.pkg);
                printWriter.print(" (");
                int i2 = valueAt.uid;
                printWriter.print(i2 != UNKNOWN_UID ? Integer.toString(i2) : "UNKNOWN_UID");
                printWriter.print(')');
                Pair pair = new Pair(Integer.valueOf(valueAt.uid), valueAt.pkg);
                if (arrayMap2 != null && keySet.contains(pair)) {
                    printWriter.print(" importance=");
                    printWriter.print(NotificationListenerService.Ranking.importanceToString(((Boolean) arrayMap2.get(pair).first).booleanValue() ? 3 : 0));
                    printWriter.print(" userSet=");
                    printWriter.print(arrayMap2.get(pair).second);
                    keySet.remove(pair);
                }
                if (valueAt.priority != 0) {
                    printWriter.print(" priority=");
                    printWriter.print(Notification.priorityToString(valueAt.priority));
                }
                if (valueAt.visibility != -1000) {
                    printWriter.print(" visibility=");
                    printWriter.print(Notification.visibilityToString(valueAt.visibility));
                }
                if (!valueAt.showBadge) {
                    printWriter.print(" showBadge=");
                    printWriter.print(valueAt.showBadge);
                }
                if (valueAt.defaultAppLockedImportance) {
                    printWriter.print(" defaultAppLocked=");
                    printWriter.print(valueAt.defaultAppLockedImportance);
                }
                if (valueAt.fixedImportance) {
                    printWriter.print(" fixedImportance=");
                    printWriter.print(valueAt.fixedImportance);
                }
                printWriter.println();
                for (NotificationChannel notificationChannel : valueAt.channels.values()) {
                    printWriter.print(str);
                    notificationChannel.dump(printWriter, "    ", dumpFilter.redact);
                }
                for (NotificationChannelGroup notificationChannelGroup : valueAt.groups.values()) {
                    printWriter.print(str);
                    printWriter.print("  ");
                    printWriter.print("  ");
                    printWriter.println(notificationChannelGroup);
                }
            }
            i++;
        }
        if (keySet != null) {
            for (Pair<Integer, String> pair2 : keySet) {
                if (dumpFilter.matches((String) pair2.second)) {
                    printWriter.print(str);
                    printWriter.print("  AppSettings: ");
                    printWriter.print((String) pair2.second);
                    printWriter.print(" (");
                    printWriter.print(((Integer) pair2.first).intValue() == UNKNOWN_UID ? "UNKNOWN_UID" : Integer.toString(((Integer) pair2.first).intValue()));
                    printWriter.print(')');
                    printWriter.print(" importance=");
                    printWriter.print(NotificationListenerService.Ranking.importanceToString(((Boolean) arrayMap2.get(pair2).first).booleanValue() ? 3 : 0));
                    printWriter.print(" userSet=");
                    printWriter.print(arrayMap2.get(pair2).second);
                    printWriter.println();
                }
            }
        }
    }

    public final void dumpPackagePreferencesLocked(ProtoOutputStream protoOutputStream, long j, NotificationManagerService.DumpFilter dumpFilter, ArrayMap<String, PackagePreferences> arrayMap, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap2) {
        Set<Pair<Integer, String>> keySet = arrayMap2 != null ? arrayMap2.keySet() : null;
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            PackagePreferences valueAt = arrayMap.valueAt(i);
            if (dumpFilter.matches(valueAt.pkg)) {
                long start = protoOutputStream.start(j);
                protoOutputStream.write(1138166333441L, valueAt.pkg);
                protoOutputStream.write(1120986464258L, valueAt.uid);
                Pair pair = new Pair(Integer.valueOf(valueAt.uid), valueAt.pkg);
                if (arrayMap2 != null && keySet.contains(pair)) {
                    protoOutputStream.write(1172526071811L, ((Boolean) arrayMap2.get(pair).first).booleanValue() ? 3 : 0);
                    keySet.remove(pair);
                }
                protoOutputStream.write(1120986464260L, valueAt.priority);
                protoOutputStream.write(1172526071813L, valueAt.visibility);
                protoOutputStream.write(1133871366150L, valueAt.showBadge);
                for (NotificationChannel notificationChannel : valueAt.channels.values()) {
                    notificationChannel.dumpDebug(protoOutputStream, 2246267895815L);
                }
                for (NotificationChannelGroup notificationChannelGroup : valueAt.groups.values()) {
                    notificationChannelGroup.dumpDebug(protoOutputStream, 2246267895816L);
                }
                protoOutputStream.end(start);
            }
        }
        if (keySet != null) {
            for (Pair<Integer, String> pair2 : keySet) {
                if (dumpFilter.matches((String) pair2.second)) {
                    long start2 = protoOutputStream.start(j);
                    protoOutputStream.write(1138166333441L, (String) pair2.second);
                    protoOutputStream.write(1120986464258L, ((Integer) pair2.first).intValue());
                    protoOutputStream.write(1172526071811L, ((Boolean) arrayMap2.get(pair2).first).booleanValue() ? 3 : 0);
                    protoOutputStream.end(start2);
                }
            }
        }
    }

    public void pullPackagePreferencesStats(List<StatsEvent> list, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap) {
        int i;
        boolean z;
        Set<Pair<Integer, String>> keySet = arrayMap != null ? arrayMap.keySet() : null;
        synchronized (this.mPackagePreferences) {
            int i2 = 0;
            i = 0;
            while (true) {
                int i3 = 3;
                if (i2 >= this.mPackagePreferences.size() || i > 1000) {
                    break;
                }
                i++;
                SysUiStatsEvent$Builder atomId = this.mStatsEventBuilderFactory.newBuilder().setAtomId(FrameworkStatsLog.PACKAGE_NOTIFICATION_PREFERENCES);
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i2);
                atomId.writeInt(valueAt.uid);
                atomId.addBooleanAnnotation((byte) 1, true);
                Pair pair = new Pair(Integer.valueOf(valueAt.uid), valueAt.pkg);
                if (arrayMap == null || !keySet.contains(pair)) {
                    i3 = 0;
                    z = false;
                } else {
                    Pair<Boolean, Boolean> pair2 = arrayMap.get(pair);
                    if (!((Boolean) pair2.first).booleanValue()) {
                        i3 = 0;
                    }
                    z = ((Boolean) pair2.second).booleanValue();
                    keySet.remove(pair);
                }
                atomId.writeInt(i3);
                atomId.writeInt(valueAt.visibility);
                atomId.writeInt(valueAt.lockedAppFields);
                atomId.writeBoolean(z);
                list.add(atomId.build());
                i2++;
            }
        }
        if (arrayMap != null) {
            for (Pair<Integer, String> pair3 : keySet) {
                if (i > 1000) {
                    return;
                }
                i++;
                SysUiStatsEvent$Builder atomId2 = this.mStatsEventBuilderFactory.newBuilder().setAtomId(FrameworkStatsLog.PACKAGE_NOTIFICATION_PREFERENCES);
                atomId2.writeInt(((Integer) pair3.first).intValue());
                atomId2.addBooleanAnnotation((byte) 1, true);
                atomId2.writeInt(((Boolean) arrayMap.get(pair3).first).booleanValue() ? 3 : 0);
                atomId2.writeInt(-1000);
                atomId2.writeInt(0);
                atomId2.writeBoolean(((Boolean) arrayMap.get(pair3).second).booleanValue());
                list.add(atomId2.build());
            }
        }
    }

    public void pullPackageChannelPreferencesStats(List<StatsEvent> list) {
        synchronized (this.mPackagePreferences) {
            int i = 0;
            for (int i2 = 0; i2 < this.mPackagePreferences.size() && i <= 2000; i2++) {
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i2);
                for (NotificationChannel notificationChannel : valueAt.channels.values()) {
                    i++;
                    if (i > 2000) {
                        break;
                    }
                    SysUiStatsEvent$Builder atomId = this.mStatsEventBuilderFactory.newBuilder().setAtomId(FrameworkStatsLog.PACKAGE_NOTIFICATION_CHANNEL_PREFERENCES);
                    atomId.writeInt(valueAt.uid);
                    boolean z = true;
                    atomId.addBooleanAnnotation((byte) 1, true);
                    atomId.writeString(notificationChannel.getId());
                    atomId.writeString(notificationChannel.getName().toString());
                    atomId.writeString(notificationChannel.getDescription());
                    atomId.writeInt(notificationChannel.getImportance());
                    atomId.writeInt(notificationChannel.getUserLockedFields());
                    atomId.writeBoolean(notificationChannel.isDeleted());
                    if (notificationChannel.getConversationId() == null) {
                        z = false;
                    }
                    atomId.writeBoolean(z);
                    atomId.writeBoolean(notificationChannel.isDemoted());
                    atomId.writeBoolean(notificationChannel.isImportantConversation());
                    list.add(atomId.build());
                }
            }
        }
    }

    public void pullPackageChannelGroupPreferencesStats(List<StatsEvent> list) {
        synchronized (this.mPackagePreferences) {
            int i = 0;
            for (int i2 = 0; i2 < this.mPackagePreferences.size() && i <= 1000; i2++) {
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i2);
                for (NotificationChannelGroup notificationChannelGroup : valueAt.groups.values()) {
                    i++;
                    if (i > 1000) {
                        break;
                    }
                    SysUiStatsEvent$Builder atomId = this.mStatsEventBuilderFactory.newBuilder().setAtomId(FrameworkStatsLog.PACKAGE_NOTIFICATION_CHANNEL_GROUP_PREFERENCES);
                    atomId.writeInt(valueAt.uid);
                    atomId.addBooleanAnnotation((byte) 1, true);
                    atomId.writeString(notificationChannelGroup.getId());
                    atomId.writeString(notificationChannelGroup.getName().toString());
                    atomId.writeString(notificationChannelGroup.getDescription());
                    atomId.writeBoolean(notificationChannelGroup.isBlocked());
                    atomId.writeInt(notificationChannelGroup.getUserLockedFields());
                    list.add(atomId.build());
                }
            }
        }
    }

    public JSONObject dumpJson(NotificationManagerService.DumpFilter dumpFilter, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap) {
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        try {
            jSONObject.put("noUid", this.mRestoredWithoutUids.size());
        } catch (JSONException unused) {
        }
        Set<Pair<Integer, String>> keySet = arrayMap != null ? arrayMap.keySet() : null;
        synchronized (this.mPackagePreferences) {
            int size = this.mPackagePreferences.size();
            int i = 0;
            while (true) {
                int i2 = 3;
                if (i >= size) {
                    break;
                }
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i);
                if (dumpFilter == null || dumpFilter.matches(valueAt.pkg)) {
                    JSONObject jSONObject2 = new JSONObject();
                    try {
                        jSONObject2.put("userId", UserHandle.getUserId(valueAt.uid));
                        jSONObject2.put("packageName", valueAt.pkg);
                        Pair pair = new Pair(Integer.valueOf(valueAt.uid), valueAt.pkg);
                        if (arrayMap != null && keySet.contains(pair)) {
                            if (!((Boolean) arrayMap.get(pair).first).booleanValue()) {
                                i2 = 0;
                            }
                            jSONObject2.put("importance", NotificationListenerService.Ranking.importanceToString(i2));
                            keySet.remove(pair);
                        }
                        int i3 = valueAt.priority;
                        if (i3 != 0) {
                            jSONObject2.put("priority", Notification.priorityToString(i3));
                        }
                        int i4 = valueAt.visibility;
                        if (i4 != -1000) {
                            jSONObject2.put("visibility", Notification.visibilityToString(i4));
                        }
                        boolean z = valueAt.showBadge;
                        if (!z) {
                            jSONObject2.put("showBadge", Boolean.valueOf(z));
                        }
                        JSONArray jSONArray2 = new JSONArray();
                        for (NotificationChannel notificationChannel : valueAt.channels.values()) {
                            jSONArray2.put(notificationChannel.toJson());
                        }
                        jSONObject2.put("channels", jSONArray2);
                        JSONArray jSONArray3 = new JSONArray();
                        for (NotificationChannelGroup notificationChannelGroup : valueAt.groups.values()) {
                            jSONArray3.put(notificationChannelGroup.toJson());
                        }
                        jSONObject2.put("groups", jSONArray3);
                    } catch (JSONException unused2) {
                    }
                    jSONArray.put(jSONObject2);
                }
                i++;
            }
        }
        if (keySet != null) {
            for (Pair<Integer, String> pair2 : keySet) {
                if (dumpFilter == null || dumpFilter.matches((String) pair2.second)) {
                    JSONObject jSONObject3 = new JSONObject();
                    try {
                        jSONObject3.put("userId", UserHandle.getUserId(((Integer) pair2.first).intValue()));
                        jSONObject3.put("packageName", pair2.second);
                        jSONObject3.put("importance", NotificationListenerService.Ranking.importanceToString(((Boolean) arrayMap.get(pair2).first).booleanValue() ? 3 : 0));
                    } catch (JSONException unused3) {
                    }
                    jSONArray.put(jSONObject3);
                }
            }
        }
        try {
            jSONObject.put("PackagePreferencess", jSONArray);
        } catch (JSONException unused4) {
        }
        return jSONObject;
    }

    public JSONArray dumpBansJson(NotificationManagerService.DumpFilter dumpFilter, ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap) {
        JSONArray jSONArray = new JSONArray();
        for (Map.Entry<Integer, String> entry : getPermissionBasedPackageBans(arrayMap).entrySet()) {
            int userId = UserHandle.getUserId(entry.getKey().intValue());
            String value = entry.getValue();
            if (dumpFilter == null || dumpFilter.matches(value)) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("userId", userId);
                    jSONObject.put("packageName", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jSONArray.put(jSONObject);
            }
        }
        return jSONArray;
    }

    public Map<Integer, String> getPermissionBasedPackageBans(ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap) {
        ArrayMap arrayMap2 = new ArrayMap();
        if (arrayMap != null) {
            for (Pair<Integer, String> pair : arrayMap.keySet()) {
                if (!((Boolean) arrayMap.get(pair).first).booleanValue()) {
                    arrayMap2.put((Integer) pair.first, (String) pair.second);
                }
            }
        }
        return arrayMap2;
    }

    public JSONArray dumpChannelsJson(NotificationManagerService.DumpFilter dumpFilter) {
        JSONArray jSONArray = new JSONArray();
        for (Map.Entry<String, Integer> entry : getPackageChannels().entrySet()) {
            String key = entry.getKey();
            if (dumpFilter == null || dumpFilter.matches(key)) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("packageName", key);
                    jSONObject.put("channelCount", entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jSONArray.put(jSONObject);
            }
        }
        return jSONArray;
    }

    public final Map<String, Integer> getPackageChannels() {
        ArrayMap arrayMap = new ArrayMap();
        synchronized (this.mPackagePreferences) {
            for (int i = 0; i < this.mPackagePreferences.size(); i++) {
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i);
                int i2 = 0;
                for (int i3 = 0; i3 < valueAt.channels.size(); i3++) {
                    if (!valueAt.channels.valueAt(i3).isDeleted()) {
                        i2++;
                    }
                }
                arrayMap.put(valueAt.pkg, Integer.valueOf(i2));
            }
        }
        return arrayMap;
    }

    public void onUserRemoved(int i) {
        synchronized (this.mPackagePreferences) {
            for (int size = this.mPackagePreferences.size() - 1; size >= 0; size--) {
                if (UserHandle.getUserId(this.mPackagePreferences.valueAt(size).uid) == i) {
                    this.mPackagePreferences.removeAt(size);
                }
            }
        }
    }

    public void onLocaleChanged(Context context, int i) {
        synchronized (this.mPackagePreferences) {
            int size = this.mPackagePreferences.size();
            for (int i2 = 0; i2 < size; i2++) {
                PackagePreferences valueAt = this.mPackagePreferences.valueAt(i2);
                if (UserHandle.getUserId(valueAt.uid) == i && valueAt.channels.containsKey("miscellaneous")) {
                    valueAt.channels.get("miscellaneous").setName(context.getResources().getString(17040114));
                }
            }
        }
    }

    public boolean onPackagesChanged(boolean z, int i, String[] strArr, int[] iArr) {
        boolean z2;
        int i2 = 0;
        if (strArr == null || strArr.length == 0) {
            return false;
        }
        if (z) {
            int min = Math.min(strArr.length, iArr.length);
            z2 = false;
            while (i2 < min) {
                String str = strArr[i2];
                int i3 = iArr[i2];
                synchronized (this.mPackagePreferences) {
                    this.mPackagePreferences.remove(packagePreferencesKey(str, i3));
                }
                this.mRestoredWithoutUids.remove(unrestoredPackageKey(str, i));
                i2++;
                z2 = true;
            }
        } else {
            z2 = false;
            for (String str2 : strArr) {
                PackagePreferences packagePreferences = this.mRestoredWithoutUids.get(unrestoredPackageKey(str2, i));
                if (packagePreferences != null) {
                    try {
                        packagePreferences.uid = this.mPm.getPackageUidAsUser(packagePreferences.pkg, i);
                        this.mRestoredWithoutUids.remove(unrestoredPackageKey(str2, i));
                        synchronized (this.mPackagePreferences) {
                            this.mPackagePreferences.put(packagePreferencesKey(packagePreferences.pkg, packagePreferences.uid), packagePreferences);
                        }
                        if (packagePreferences.migrateToPm) {
                            try {
                                this.mPermissionHelper.setNotificationPermission(new PermissionHelper.PackagePermission(packagePreferences.pkg, UserHandle.getUserId(packagePreferences.uid), packagePreferences.importance != 0, hasUserConfiguredSettings(packagePreferences)));
                            } catch (Exception e) {
                                Slog.e("NotificationPrefHelper", "could not migrate setting for " + packagePreferences.pkg, e);
                            }
                        }
                        z2 = true;
                    } catch (Exception e2) {
                        Slog.e("NotificationPrefHelper", "could not restore " + packagePreferences.pkg, e2);
                    }
                }
                try {
                    synchronized (this.mPackagePreferences) {
                        PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str2, this.mPm.getPackageUidAsUser(str2, i));
                        if (packagePreferencesLocked != null) {
                            z2 = z2 | createDefaultChannelIfNeededLocked(packagePreferencesLocked) | deleteDefaultChannelIfNeededLocked(packagePreferencesLocked);
                        }
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        }
        if (z2) {
            updateConfig();
        }
        return z2;
    }

    public void clearData(String str, int i) {
        synchronized (this.mPackagePreferences) {
            PackagePreferences packagePreferencesLocked = getPackagePreferencesLocked(str, i);
            if (packagePreferencesLocked != null) {
                packagePreferencesLocked.channels = new ArrayMap<>();
                packagePreferencesLocked.groups = new ArrayMap();
                packagePreferencesLocked.delegate = null;
                packagePreferencesLocked.lockedAppFields = 0;
                packagePreferencesLocked.bubblePreference = 0;
                packagePreferencesLocked.importance = -1000;
                packagePreferencesLocked.priority = 0;
                packagePreferencesLocked.visibility = -1000;
                packagePreferencesLocked.showBadge = true;
            }
        }
    }

    public final LogMaker getChannelLog(NotificationChannel notificationChannel, String str) {
        return new LogMaker(856).setType(6).setPackageName(str).addTaggedData(857, notificationChannel.getId()).addTaggedData(858, Integer.valueOf(notificationChannel.getImportance()));
    }

    public final LogMaker getChannelGroupLog(String str, String str2) {
        return new LogMaker(859).setType(6).addTaggedData(860, str).setPackageName(str2);
    }

    public void updateMediaNotificationFilteringEnabled() {
        boolean z = Settings.Global.getInt(this.mContext.getContentResolver(), "qs_media_controls", 1) > 0;
        if (z != this.mIsMediaNotificationFilteringEnabled) {
            this.mIsMediaNotificationFilteringEnabled = z;
            updateConfig();
        }
    }

    @Override // com.android.server.notification.RankingConfig
    public boolean isMediaNotificationFilteringEnabled() {
        return this.mIsMediaNotificationFilteringEnabled;
    }

    public void updateBadgingEnabled() {
        if (this.mBadgingEnabled == null) {
            this.mBadgingEnabled = new SparseBooleanArray();
        }
        boolean z = false;
        for (int i = 0; i < this.mBadgingEnabled.size(); i++) {
            int keyAt = this.mBadgingEnabled.keyAt(i);
            boolean z2 = this.mBadgingEnabled.get(keyAt);
            boolean z3 = true;
            boolean z4 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_badging", 1, keyAt) != 0;
            this.mBadgingEnabled.put(keyAt, z4);
            if (z2 == z4) {
                z3 = false;
            }
            z |= z3;
        }
        if (z) {
            updateConfig();
        }
    }

    @Override // com.android.server.notification.RankingConfig
    public boolean badgingEnabled(UserHandle userHandle) {
        int identifier = userHandle.getIdentifier();
        if (identifier == -1) {
            return false;
        }
        if (this.mBadgingEnabled.indexOfKey(identifier) < 0) {
            this.mBadgingEnabled.put(identifier, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_badging", 1, identifier) != 0);
        }
        return this.mBadgingEnabled.get(identifier, true);
    }

    public void updateBubblesEnabled() {
        if (this.mBubblesEnabled == null) {
            this.mBubblesEnabled = new SparseBooleanArray();
        }
        boolean z = false;
        for (int i = 0; i < this.mBubblesEnabled.size(); i++) {
            int keyAt = this.mBubblesEnabled.keyAt(i);
            boolean z2 = this.mBubblesEnabled.get(keyAt);
            boolean z3 = true;
            boolean z4 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_bubbles", 1, keyAt) != 0;
            this.mBubblesEnabled.put(keyAt, z4);
            if (z2 == z4) {
                z3 = false;
            }
            z |= z3;
        }
        if (z) {
            updateConfig();
        }
    }

    @Override // com.android.server.notification.RankingConfig
    public boolean bubblesEnabled(UserHandle userHandle) {
        int identifier = userHandle.getIdentifier();
        if (identifier == -1) {
            return false;
        }
        if (this.mBubblesEnabled.indexOfKey(identifier) < 0) {
            this.mBubblesEnabled.put(identifier, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_bubbles", 1, identifier) != 0);
        }
        return this.mBubblesEnabled.get(identifier, true);
    }

    public void updateLockScreenPrivateNotifications() {
        if (this.mLockScreenPrivateNotifications == null) {
            this.mLockScreenPrivateNotifications = new SparseBooleanArray();
        }
        boolean z = false;
        for (int i = 0; i < this.mLockScreenPrivateNotifications.size(); i++) {
            int keyAt = this.mLockScreenPrivateNotifications.keyAt(i);
            boolean z2 = this.mLockScreenPrivateNotifications.get(keyAt);
            boolean z3 = true;
            boolean z4 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 1, keyAt) != 0;
            this.mLockScreenPrivateNotifications.put(keyAt, z4);
            if (z2 == z4) {
                z3 = false;
            }
            z |= z3;
        }
        if (z) {
            updateConfig();
        }
    }

    public void updateLockScreenShowNotifications() {
        if (this.mLockScreenShowNotifications == null) {
            this.mLockScreenShowNotifications = new SparseBooleanArray();
        }
        boolean z = false;
        for (int i = 0; i < this.mLockScreenShowNotifications.size(); i++) {
            int keyAt = this.mLockScreenShowNotifications.keyAt(i);
            boolean z2 = this.mLockScreenShowNotifications.get(keyAt);
            boolean z3 = true;
            boolean z4 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, keyAt) != 0;
            this.mLockScreenShowNotifications.put(keyAt, z4);
            if (z2 == z4) {
                z3 = false;
            }
            z |= z3;
        }
        if (z) {
            updateConfig();
        }
    }

    @Override // com.android.server.notification.RankingConfig
    public boolean canShowNotificationsOnLockscreen(int i) {
        if (this.mLockScreenShowNotifications == null) {
            this.mLockScreenShowNotifications = new SparseBooleanArray();
        }
        return this.mLockScreenShowNotifications.get(i, true);
    }

    @Override // com.android.server.notification.RankingConfig
    public boolean canShowPrivateNotificationsOnLockScreen(int i) {
        if (this.mLockScreenPrivateNotifications == null) {
            this.mLockScreenPrivateNotifications = new SparseBooleanArray();
        }
        return this.mLockScreenPrivateNotifications.get(i, true);
    }

    public void unlockAllNotificationChannels() {
        synchronized (this.mPackagePreferences) {
            int size = this.mPackagePreferences.size();
            for (int i = 0; i < size; i++) {
                for (NotificationChannel notificationChannel : this.mPackagePreferences.valueAt(i).channels.values()) {
                    notificationChannel.unlockFields(4);
                }
            }
        }
    }

    public final void updateConfig() {
        this.mRankingHandler.requestSort();
    }

    public static String packagePreferencesKey(String str, int i) {
        return str + "|" + i;
    }

    public static String unrestoredPackageKey(String str, int i) {
        return str + "|" + i;
    }

    /* loaded from: classes2.dex */
    public static class PackagePreferences {
        public int bubblePreference;
        public ArrayMap<String, NotificationChannel> channels;
        public boolean defaultAppLockedImportance;
        public Delegate delegate;
        public boolean fixedImportance;
        public Map<String, NotificationChannelGroup> groups;
        public boolean hasSentInvalidMessage;
        public boolean hasSentValidBubble;
        public boolean hasSentValidMessage;
        public int importance;
        public int lockedAppFields;
        public boolean migrateToPm;
        public String pkg;
        public int priority;
        public boolean showBadge;
        public int uid;
        public boolean userDemotedMsgApp;
        public int visibility;

        public PackagePreferences() {
            this.uid = PreferencesHelper.UNKNOWN_UID;
            this.importance = -1000;
            this.priority = 0;
            this.visibility = -1000;
            this.showBadge = true;
            this.bubblePreference = 0;
            this.lockedAppFields = 0;
            this.defaultAppLockedImportance = false;
            this.fixedImportance = false;
            this.hasSentInvalidMessage = false;
            this.hasSentValidMessage = false;
            this.userDemotedMsgApp = false;
            this.hasSentValidBubble = false;
            this.migrateToPm = false;
            this.delegate = null;
            this.channels = new ArrayMap<>();
            this.groups = new ConcurrentHashMap();
        }

        public boolean isValidDelegate(String str, int i) {
            Delegate delegate = this.delegate;
            return delegate != null && delegate.isAllowed(str, i);
        }
    }

    /* loaded from: classes2.dex */
    public static class Delegate {
        public boolean mEnabled;
        public String mPkg;
        public int mUid;
        public boolean mUserAllowed;

        public Delegate(String str, int i, boolean z, boolean z2) {
            this.mPkg = str;
            this.mUid = i;
            this.mEnabled = z;
            this.mUserAllowed = z2;
        }

        public boolean isAllowed(String str, int i) {
            return str != null && i != PreferencesHelper.UNKNOWN_UID && str.equals(this.mPkg) && i == this.mUid && this.mUserAllowed && this.mEnabled;
        }
    }
}
