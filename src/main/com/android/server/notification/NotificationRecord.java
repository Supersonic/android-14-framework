package com.android.server.notification;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Person;
import android.content.ContentProvider;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioSystem;
import android.metrics.LogMaker;
import android.net.Uri;
import android.net.util.NetworkConstants;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.provider.Settings;
import android.service.notification.Adjustment;
import android.service.notification.NotificationListenerService;
import android.service.notification.NotificationStats;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.widget.RemoteViews;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.notification.NotificationUsageStats;
import com.android.server.uri.UriGrantsManagerInternal;
import dalvik.annotation.optimization.NeverCompile;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class NotificationRecord {
    public static final boolean DBG = Log.isLoggable("NotificationRecord", 3);
    public boolean isCanceled;
    public boolean isUpdate;
    public String mAdjustmentIssuer;
    public final List<Adjustment> mAdjustments;
    public boolean mAllowBubble;
    public boolean mAppDemotedFromConvo;
    public AudioAttributes mAttributes;
    public int mAuthoritativeRank;
    public NotificationChannel mChannel;
    public float mContactAffinity;
    public final Context mContext;
    public long mCreationTimeMs;
    public boolean mEditChoicesBeforeSending;
    public boolean mFlagBubbleRemoved;
    public String mGlobalSortKey;
    public ArraySet<Uri> mGrantableUris;
    public boolean mHasSeenSmartReplies;
    public boolean mHasSentValidMsg;
    public boolean mHidden;
    public int mImportance;
    public boolean mImportanceFixed;
    public boolean mIntercept;
    public boolean mInterceptSet;
    public long mInterruptionTimeMs;
    public boolean mIsAppImportanceLocked;
    public boolean mIsInterruptive;
    public boolean mIsNotConversationOverride;
    public long mLastAudiblyAlertedMs;
    public long mLastIntrusive;
    public Light mLight;
    public int mNumberOfSmartActionsAdded;
    public int mNumberOfSmartRepliesAdded;
    public final int mOriginalFlags;
    public int mPackagePriority;
    public int mPackageVisibility;
    public ArrayList<String> mPeopleOverride;
    public ArraySet<String> mPhoneNumbers;
    public boolean mPkgAllowedAsConvo;
    public boolean mPostSilently;
    public boolean mPreChannelsNotification;
    public boolean mRecentlyIntrusive;
    public boolean mRecordedInterruption;
    public ShortcutInfo mShortcutInfo;
    public boolean mShowBadge;
    public ArrayList<CharSequence> mSmartReplies;
    public ArrayList<SnoozeCriterion> mSnoozeCriteria;
    public Uri mSound;
    public final NotificationStats mStats;
    public boolean mSuggestionsGeneratedByAssistant;
    public ArrayList<Notification.Action> mSystemGeneratedSmartActions;
    public final int mTargetSdkVersion;
    public boolean mTextChanged;
    @VisibleForTesting
    final long mUpdateTimeMs;
    public int mUserSentiment;
    public VibrationEffect mVibration;
    public long mVisibleSinceMs;
    public IBinder permissionOwner;
    public final StatusBarNotification sbn;
    public NotificationUsageStats.SingleNotificationStats stats;
    public int mSystemImportance = -1000;
    public int mAssistantImportance = -1000;
    public float mRankingScore = 0.0f;
    public int mCriticality = 2;
    public int mImportanceExplanationCode = 0;
    public int mInitialImportanceExplanationCode = 0;
    public int mSuppressedVisualEffects = 0;
    public boolean mPendingLogUpdate = false;
    public int mProposedImportance = -1000;
    public boolean mSensitiveContent = false;
    public IActivityManager mAm = ActivityManager.getService();
    public UriGrantsManagerInternal mUgmInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
    public long mRankingTimeMs = calculateRankingTimeMs(0);

    public NotificationRecord(Context context, StatusBarNotification statusBarNotification, NotificationChannel notificationChannel) {
        this.mImportance = -1000;
        this.mPreChannelsNotification = true;
        this.sbn = statusBarNotification;
        this.mTargetSdkVersion = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageTargetSdkVersion(statusBarNotification.getPackageName());
        this.mOriginalFlags = statusBarNotification.getNotification().flags;
        long postTime = statusBarNotification.getPostTime();
        this.mCreationTimeMs = postTime;
        this.mUpdateTimeMs = postTime;
        this.mInterruptionTimeMs = postTime;
        this.mContext = context;
        this.stats = new NotificationUsageStats.SingleNotificationStats();
        this.mChannel = notificationChannel;
        this.mPreChannelsNotification = isPreChannelsNotification();
        this.mSound = calculateSound();
        this.mVibration = calculateVibration();
        this.mAttributes = calculateAttributes();
        this.mImportance = calculateInitialImportance();
        this.mLight = calculateLights();
        this.mAdjustments = new ArrayList();
        this.mStats = new NotificationStats();
        calculateUserSentiment();
        calculateGrantableUris();
    }

    public final boolean isPreChannelsNotification() {
        return "miscellaneous".equals(getChannel().getId()) && this.mTargetSdkVersion < 26;
    }

    public final Uri calculateSound() {
        Notification notification = getSbn().getNotification();
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.leanback")) {
            return null;
        }
        Uri sound = this.mChannel.getSound();
        if (this.mPreChannelsNotification && (getChannel().getUserLockedFields() & 32) == 0) {
            if ((notification.defaults & 1) != 0) {
                return Settings.System.DEFAULT_NOTIFICATION_URI;
            }
            return notification.sound;
        }
        return sound;
    }

    public final Light calculateLights() {
        int color = this.mContext.getResources().getColor(17170816);
        int integer = this.mContext.getResources().getInteger(17694798);
        int integer2 = this.mContext.getResources().getInteger(17694797);
        Light light = getChannel().shouldShowLights() ? new Light(getChannel().getLightColor() != 0 ? getChannel().getLightColor() : color, integer, integer2) : null;
        if (this.mPreChannelsNotification && (getChannel().getUserLockedFields() & 8) == 0) {
            Notification notification = getSbn().getNotification();
            if ((notification.flags & 1) != 0) {
                return (notification.defaults & 4) != 0 ? new Light(color, integer, integer2) : new Light(notification.ledARGB, notification.ledOnMS, notification.ledOffMS);
            }
            return null;
        }
        return light;
    }

    public final VibrationEffect calculateVibration() {
        VibrationEffect vibrationEffect;
        VibratorHelper vibratorHelper = new VibratorHelper(this.mContext);
        Notification notification = getSbn().getNotification();
        boolean z = (notification.flags & 4) != 0;
        VibrationEffect createDefaultVibration = vibratorHelper.createDefaultVibration(z);
        if (getChannel().shouldVibrate()) {
            vibrationEffect = getChannel().getVibrationPattern() == null ? createDefaultVibration : VibratorHelper.createWaveformVibration(getChannel().getVibrationPattern(), z);
        } else {
            vibrationEffect = null;
        }
        if (this.mPreChannelsNotification && (getChannel().getUserLockedFields() & 16) == 0) {
            return (notification.defaults & 2) != 0 ? createDefaultVibration : VibratorHelper.createWaveformVibration(notification.vibrate, z);
        }
        return vibrationEffect;
    }

    public final AudioAttributes calculateAttributes() {
        Notification notification = getSbn().getNotification();
        AudioAttributes audioAttributes = getChannel().getAudioAttributes();
        if (audioAttributes == null) {
            audioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
        }
        if (this.mPreChannelsNotification && (getChannel().getUserLockedFields() & 32) == 0) {
            AudioAttributes audioAttributes2 = notification.audioAttributes;
            if (audioAttributes2 != null) {
                return audioAttributes2;
            }
            int i = notification.audioStreamType;
            if (i >= 0 && i < AudioSystem.getNumStreamTypes()) {
                return new AudioAttributes.Builder().setInternalLegacyStreamType(notification.audioStreamType).build();
            }
            int i2 = notification.audioStreamType;
            if (i2 != -1) {
                Log.w("NotificationRecord", String.format("Invalid stream type: %d", Integer.valueOf(i2)));
                return audioAttributes;
            }
            return audioAttributes;
        }
        return audioAttributes;
    }

    public final int calculateInitialImportance() {
        Notification notification = getSbn().getNotification();
        int importance = getChannel().getImportance();
        boolean z = true;
        int i = 2;
        this.mInitialImportanceExplanationCode = getChannel().hasUserSetImportance() ? 2 : 1;
        if ((notification.flags & 128) != 0) {
            notification.priority = 2;
        }
        int clamp = NotificationManagerService.clamp(notification.priority, -2, 2);
        notification.priority = clamp;
        int i2 = 3;
        int i3 = clamp != -2 ? clamp != -1 ? (clamp == 0 || !(clamp == 1 || clamp == 2)) ? 3 : 4 : 2 : 1;
        NotificationUsageStats.SingleNotificationStats singleNotificationStats = this.stats;
        singleNotificationStats.requestedImportance = i3;
        if (this.mSound == null && this.mVibration == null) {
            z = false;
        }
        singleNotificationStats.isNoisy = z;
        if (this.mPreChannelsNotification && (importance == -1000 || !getChannel().hasUserSetImportance())) {
            boolean z2 = this.stats.isNoisy;
            if (z2 || i3 <= 2) {
                i = i3;
            }
            if (!z2 || i >= 3) {
                i2 = i;
            }
            importance = notification.fullScreenIntent != null ? 4 : i2;
            this.mInitialImportanceExplanationCode = 5;
        }
        this.stats.naturalImportance = importance;
        return importance;
    }

    public void copyRankingInformation(NotificationRecord notificationRecord) {
        this.mContactAffinity = notificationRecord.mContactAffinity;
        this.mRecentlyIntrusive = notificationRecord.mRecentlyIntrusive;
        this.mPackagePriority = notificationRecord.mPackagePriority;
        this.mPackageVisibility = notificationRecord.mPackageVisibility;
        this.mIntercept = notificationRecord.mIntercept;
        this.mHidden = notificationRecord.mHidden;
        this.mRankingTimeMs = calculateRankingTimeMs(notificationRecord.getRankingTimeMs());
        this.mCreationTimeMs = notificationRecord.mCreationTimeMs;
        this.mVisibleSinceMs = notificationRecord.mVisibleSinceMs;
        if (notificationRecord.getSbn().getOverrideGroupKey() == null || getSbn().isAppGroup()) {
            return;
        }
        getSbn().setOverrideGroupKey(notificationRecord.getSbn().getOverrideGroupKey());
    }

    public Notification getNotification() {
        return getSbn().getNotification();
    }

    public int getFlags() {
        return getSbn().getNotification().flags;
    }

    public UserHandle getUser() {
        return getSbn().getUser();
    }

    public String getKey() {
        return getSbn().getKey();
    }

    public int getUserId() {
        return getSbn().getUserId();
    }

    public int getUid() {
        return getSbn().getUid();
    }

    public void dump(ProtoOutputStream protoOutputStream, long j, boolean z, int i) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, getSbn().getKey());
        protoOutputStream.write(1159641169922L, i);
        if (getChannel() != null) {
            protoOutputStream.write(1138166333444L, getChannel().getId());
        }
        protoOutputStream.write(1133871366152L, getLight() != null);
        protoOutputStream.write(1133871366151L, getVibration() != null);
        protoOutputStream.write(1120986464259L, getSbn().getNotification().flags);
        protoOutputStream.write(1138166333449L, getGroupKey());
        protoOutputStream.write(1172526071818L, getImportance());
        if (getSound() != null) {
            protoOutputStream.write(1138166333445L, getSound().toString());
        }
        if (getAudioAttributes() != null) {
            getAudioAttributes().dumpDebug(protoOutputStream, 1146756268038L);
        }
        protoOutputStream.write(1138166333451L, getSbn().getPackageName());
        protoOutputStream.write(1138166333452L, getSbn().getOpPkg());
        protoOutputStream.end(start);
    }

    public String formatRemoteViews(RemoteViews remoteViews) {
        return remoteViews == null ? "null" : String.format("%s/0x%08x (%d bytes): %s", remoteViews.getPackage(), Integer.valueOf(remoteViews.getLayoutId()), Integer.valueOf(remoteViews.estimateMemoryUsage()), remoteViews.toString());
    }

    @NeverCompile
    public void dump(PrintWriter printWriter, String str, Context context, boolean z) {
        Notification notification = getSbn().getNotification();
        printWriter.println(str + this);
        String str2 = str + "  ";
        printWriter.println(str2 + "uid=" + getSbn().getUid() + " userId=" + getSbn().getUserId());
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("opPkg=");
        sb.append(getSbn().getOpPkg());
        printWriter.println(sb.toString());
        printWriter.println(str2 + "icon=" + notification.getSmallIcon());
        printWriter.println(str2 + "flags=0x" + Integer.toHexString(notification.flags));
        printWriter.println(str2 + "originalFlags=0x" + Integer.toHexString(this.mOriginalFlags));
        printWriter.println(str2 + "pri=" + notification.priority);
        printWriter.println(str2 + "key=" + getSbn().getKey());
        printWriter.println(str2 + "seen=" + this.mStats.hasSeen());
        printWriter.println(str2 + "groupKey=" + getGroupKey());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str2);
        sb2.append("notification=");
        printWriter.println(sb2.toString());
        dumpNotification(printWriter, str2 + str2, notification, z);
        printWriter.println(str2 + "publicNotification=");
        dumpNotification(printWriter, str2 + str2, notification.publicVersion, z);
        printWriter.println(str2 + "stats=" + this.stats.toString());
        printWriter.println(str2 + "mContactAffinity=" + this.mContactAffinity);
        printWriter.println(str2 + "mRecentlyIntrusive=" + this.mRecentlyIntrusive);
        printWriter.println(str2 + "mPackagePriority=" + this.mPackagePriority);
        printWriter.println(str2 + "mPackageVisibility=" + this.mPackageVisibility);
        printWriter.println(str2 + "mSystemImportance=" + NotificationListenerService.Ranking.importanceToString(this.mSystemImportance));
        printWriter.println(str2 + "mAsstImportance=" + NotificationListenerService.Ranking.importanceToString(this.mAssistantImportance));
        printWriter.println(str2 + "mImportance=" + NotificationListenerService.Ranking.importanceToString(this.mImportance));
        printWriter.println(str2 + "mImportanceExplanation=" + ((Object) getImportanceExplanation()));
        printWriter.println(str2 + "mProposedImportance=" + NotificationListenerService.Ranking.importanceToString(this.mProposedImportance));
        printWriter.println(str2 + "mIsAppImportanceLocked=" + this.mIsAppImportanceLocked);
        printWriter.println(str2 + "mSensitiveContent=" + this.mSensitiveContent);
        printWriter.println(str2 + "mIntercept=" + this.mIntercept);
        printWriter.println(str2 + "mHidden==" + this.mHidden);
        printWriter.println(str2 + "mGlobalSortKey=" + this.mGlobalSortKey);
        printWriter.println(str2 + "mRankingTimeMs=" + this.mRankingTimeMs);
        printWriter.println(str2 + "mCreationTimeMs=" + this.mCreationTimeMs);
        printWriter.println(str2 + "mVisibleSinceMs=" + this.mVisibleSinceMs);
        printWriter.println(str2 + "mUpdateTimeMs=" + this.mUpdateTimeMs);
        printWriter.println(str2 + "mInterruptionTimeMs=" + this.mInterruptionTimeMs);
        printWriter.println(str2 + "mSuppressedVisualEffects= " + this.mSuppressedVisualEffects);
        if (this.mPreChannelsNotification) {
            printWriter.println(str2 + String.format("defaults=0x%08x flags=0x%08x", Integer.valueOf(notification.defaults), Integer.valueOf(notification.flags)));
            printWriter.println(str2 + "n.sound=" + notification.sound);
            printWriter.println(str2 + "n.audioStreamType=" + notification.audioStreamType);
            printWriter.println(str2 + "n.audioAttributes=" + notification.audioAttributes);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str2);
            sb3.append(String.format("  led=0x%08x onMs=%d offMs=%d", Integer.valueOf(notification.ledARGB), Integer.valueOf(notification.ledOnMS), Integer.valueOf(notification.ledOffMS)));
            printWriter.println(sb3.toString());
            printWriter.println(str2 + "vibrate=" + Arrays.toString(notification.vibrate));
        }
        printWriter.println(str2 + "mSound= " + this.mSound);
        printWriter.println(str2 + "mVibration= " + this.mVibration);
        printWriter.println(str2 + "mAttributes= " + this.mAttributes);
        printWriter.println(str2 + "mLight= " + this.mLight);
        printWriter.println(str2 + "mShowBadge=" + this.mShowBadge);
        printWriter.println(str2 + "mColorized=" + notification.isColorized());
        printWriter.println(str2 + "mAllowBubble=" + this.mAllowBubble);
        printWriter.println(str2 + "isBubble=" + notification.isBubbleNotification());
        printWriter.println(str2 + "mIsInterruptive=" + this.mIsInterruptive);
        printWriter.println(str2 + "effectiveNotificationChannel=" + getChannel());
        if (getPeopleOverride() != null) {
            printWriter.println(str2 + "overridePeople= " + TextUtils.join(",", getPeopleOverride()));
        }
        if (getSnoozeCriteria() != null) {
            printWriter.println(str2 + "snoozeCriteria=" + TextUtils.join(",", getSnoozeCriteria()));
        }
        printWriter.println(str2 + "mAdjustments=" + this.mAdjustments);
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str2);
        sb4.append("shortcut=");
        sb4.append(notification.getShortcutId());
        sb4.append(" found valid? ");
        sb4.append(this.mShortcutInfo != null);
        printWriter.println(sb4.toString());
    }

    public final void dumpNotification(PrintWriter printWriter, String str, Notification notification, boolean z) {
        if (notification == null) {
            printWriter.println(str + "None");
            return;
        }
        printWriter.println(str + "fullscreenIntent=" + notification.fullScreenIntent);
        printWriter.println(str + "contentIntent=" + notification.contentIntent);
        printWriter.println(str + "deleteIntent=" + notification.deleteIntent);
        printWriter.println(str + "number=" + notification.number);
        printWriter.println(str + "groupAlertBehavior=" + notification.getGroupAlertBehavior());
        printWriter.println(str + "when=" + notification.when);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("tickerText=");
        printWriter.print(sb.toString());
        if (!TextUtils.isEmpty(notification.tickerText)) {
            String charSequence = notification.tickerText.toString();
            if (z) {
                printWriter.print(charSequence.length() > 16 ? charSequence.substring(0, 8) : "");
                printWriter.println("...");
            } else {
                printWriter.println(charSequence);
            }
        } else {
            printWriter.println("null");
        }
        printWriter.println(str + "contentView=" + formatRemoteViews(notification.contentView));
        printWriter.println(str + "bigContentView=" + formatRemoteViews(notification.bigContentView));
        printWriter.println(str + "headsUpContentView=" + formatRemoteViews(notification.headsUpContentView));
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(String.format("color=0x%08x", Integer.valueOf(notification.color)));
        printWriter.println(sb2.toString());
        printWriter.println(str + "timeout=" + TimeUtils.formatForLogging(notification.getTimeoutAfter()));
        Notification.Action[] actionArr = notification.actions;
        if (actionArr != null && actionArr.length > 0) {
            printWriter.println(str + "actions={");
            int length = notification.actions.length;
            for (int i = 0; i < length; i++) {
                Notification.Action action = notification.actions[i];
                if (action != null) {
                    Object[] objArr = new Object[4];
                    objArr[0] = str;
                    objArr[1] = Integer.valueOf(i);
                    objArr[2] = action.title;
                    PendingIntent pendingIntent = action.actionIntent;
                    objArr[3] = pendingIntent == null ? "null" : pendingIntent.toString();
                    printWriter.println(String.format("%s    [%d] \"%s\" -> %s", objArr));
                }
            }
            printWriter.println(str + "  }");
        }
        Bundle bundle = notification.extras;
        if (bundle == null || bundle.size() <= 0) {
            return;
        }
        printWriter.println(str + "extras={");
        for (String str2 : notification.extras.keySet()) {
            printWriter.print(str + "    " + str2 + "=");
            Object obj = notification.extras.get(str2);
            if (obj == null) {
                printWriter.println("null");
            } else {
                printWriter.print(obj.getClass().getSimpleName());
                if (z && (obj instanceof CharSequence) && shouldRedactStringExtra(str2)) {
                    printWriter.print(String.format(" [length=%d]", Integer.valueOf(((CharSequence) obj).length())));
                } else if (obj instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) obj;
                    printWriter.print(String.format(" (%dx%d)", Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight())));
                } else if (obj.getClass().isArray()) {
                    int length2 = Array.getLength(obj);
                    printWriter.print(" (" + length2 + ")");
                    if (!z) {
                        for (int i2 = 0; i2 < length2; i2++) {
                            printWriter.println();
                            printWriter.print(String.format("%s      [%d] %s", str, Integer.valueOf(i2), String.valueOf(Array.get(obj, i2))));
                        }
                    }
                } else {
                    printWriter.print(" (" + String.valueOf(obj) + ")");
                }
                printWriter.println();
            }
        }
        printWriter.println(str + "}");
    }

    public final boolean shouldRedactStringExtra(String str) {
        if (str == null) {
            return true;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1349298919:
                if (str.equals("android.template")) {
                    c = 0;
                    break;
                }
                break;
            case -330858995:
                if (str.equals("android.substName")) {
                    c = 1;
                    break;
                }
                break;
            case 1258919194:
                if (str.equals("android.support.v4.app.extra.COMPAT_TEMPLATE")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
                return false;
            default:
                return true;
        }
    }

    public final String toString() {
        return String.format("NotificationRecord(0x%08x: pkg=%s user=%s id=%d tag=%s importance=%d key=%s: %s)", Integer.valueOf(System.identityHashCode(this)), getSbn().getPackageName(), getSbn().getUser(), Integer.valueOf(getSbn().getId()), getSbn().getTag(), Integer.valueOf(this.mImportance), getSbn().getKey(), getSbn().getNotification());
    }

    public void addAdjustment(Adjustment adjustment) {
        synchronized (this.mAdjustments) {
            this.mAdjustments.add(adjustment);
        }
    }

    public void applyAdjustments() {
        System.currentTimeMillis();
        synchronized (this.mAdjustments) {
            for (Adjustment adjustment : this.mAdjustments) {
                Bundle signals = adjustment.getSignals();
                if (signals.containsKey("key_people")) {
                    ArrayList<String> stringArrayList = adjustment.getSignals().getStringArrayList("key_people");
                    setPeopleOverride(stringArrayList);
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_people", stringArrayList.toString());
                }
                if (signals.containsKey("key_snooze_criteria")) {
                    ArrayList parcelableArrayList = adjustment.getSignals().getParcelableArrayList("key_snooze_criteria", SnoozeCriterion.class);
                    setSnoozeCriteria(parcelableArrayList);
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_snooze_criteria", parcelableArrayList.toString());
                }
                if (signals.containsKey("key_group_key")) {
                    String string = adjustment.getSignals().getString("key_group_key");
                    setOverrideGroupKey(string);
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_group_key", string);
                }
                if (signals.containsKey("key_user_sentiment") && !this.mIsAppImportanceLocked && (getChannel().getUserLockedFields() & 4) == 0) {
                    setUserSentiment(adjustment.getSignals().getInt("key_user_sentiment", 0));
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_user_sentiment", Integer.toString(getUserSentiment()));
                }
                if (signals.containsKey("key_contextual_actions")) {
                    setSystemGeneratedSmartActions(signals.getParcelableArrayList("key_contextual_actions", Notification.Action.class));
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_contextual_actions", getSystemGeneratedSmartActions().toString());
                }
                if (signals.containsKey("key_text_replies")) {
                    setSmartReplies(signals.getCharSequenceArrayList("key_text_replies"));
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_text_replies", getSmartReplies().toString());
                }
                if (signals.containsKey("key_importance")) {
                    int min = Math.min(4, Math.max(-1000, signals.getInt("key_importance")));
                    setAssistantImportance(min);
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_importance", Integer.toString(min));
                }
                if (signals.containsKey("key_ranking_score")) {
                    this.mRankingScore = signals.getFloat("key_ranking_score");
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_ranking_score", Float.toString(this.mRankingScore));
                }
                if (signals.containsKey("key_not_conversation")) {
                    this.mIsNotConversationOverride = signals.getBoolean("key_not_conversation");
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_not_conversation", Boolean.toString(this.mIsNotConversationOverride));
                }
                if (signals.containsKey("key_importance_proposal")) {
                    this.mProposedImportance = signals.getInt("key_importance_proposal");
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_importance_proposal", Integer.toString(this.mProposedImportance));
                }
                if (signals.containsKey("key_sensitive_content")) {
                    this.mSensitiveContent = signals.getBoolean("key_sensitive_content");
                    EventLogTags.writeNotificationAdjusted(getKey(), "key_sensitive_content", Boolean.toString(this.mSensitiveContent));
                }
                if (!signals.isEmpty() && adjustment.getIssuer() != null) {
                    this.mAdjustmentIssuer = adjustment.getIssuer();
                }
            }
            this.mAdjustments.clear();
        }
    }

    public String getAdjustmentIssuer() {
        return this.mAdjustmentIssuer;
    }

    public void setIsAppImportanceLocked(boolean z) {
        this.mIsAppImportanceLocked = z;
        calculateUserSentiment();
    }

    public void setContactAffinity(float f) {
        this.mContactAffinity = f;
    }

    public float getContactAffinity() {
        return this.mContactAffinity;
    }

    public void setRecentlyIntrusive(boolean z) {
        this.mRecentlyIntrusive = z;
        if (z) {
            this.mLastIntrusive = System.currentTimeMillis();
        }
    }

    public boolean isRecentlyIntrusive() {
        return this.mRecentlyIntrusive;
    }

    public long getLastIntrusive() {
        return this.mLastIntrusive;
    }

    public void setPackagePriority(int i) {
        this.mPackagePriority = i;
    }

    public int getPackagePriority() {
        return this.mPackagePriority;
    }

    public void setPackageVisibilityOverride(int i) {
        this.mPackageVisibility = i;
    }

    public int getPackageVisibilityOverride() {
        return this.mPackageVisibility;
    }

    public void setSystemImportance(int i) {
        this.mSystemImportance = i;
        calculateImportance();
    }

    public void setAssistantImportance(int i) {
        this.mAssistantImportance = i;
    }

    public int getAssistantImportance() {
        return this.mAssistantImportance;
    }

    public void setImportanceFixed(boolean z) {
        this.mImportanceFixed = z;
    }

    public void calculateImportance() {
        int i;
        this.mImportance = calculateInitialImportance();
        this.mImportanceExplanationCode = this.mInitialImportanceExplanationCode;
        if (!getChannel().hasUserSetImportance() && (i = this.mAssistantImportance) != -1000 && !this.mImportanceFixed) {
            this.mImportance = i;
            this.mImportanceExplanationCode = 3;
        }
        int i2 = this.mSystemImportance;
        if (i2 != -1000) {
            this.mImportance = i2;
            this.mImportanceExplanationCode = 4;
        }
    }

    public int getImportance() {
        return this.mImportance;
    }

    public int getInitialImportance() {
        return this.stats.naturalImportance;
    }

    public int getProposedImportance() {
        return this.mProposedImportance;
    }

    public boolean hasSensitiveContent() {
        return this.mSensitiveContent;
    }

    public float getRankingScore() {
        return this.mRankingScore;
    }

    public int getImportanceExplanationCode() {
        return this.mImportanceExplanationCode;
    }

    public int getInitialImportanceExplanationCode() {
        return this.mInitialImportanceExplanationCode;
    }

    public CharSequence getImportanceExplanation() {
        int i = this.mImportanceExplanationCode;
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            return null;
                        }
                        return "app";
                    }
                    return "system";
                }
                return "asst";
            }
            return "user";
        }
        return "app";
    }

    public boolean setIntercepted(boolean z) {
        this.mIntercept = z;
        this.mInterceptSet = true;
        return z;
    }

    public void setCriticality(int i) {
        this.mCriticality = i;
    }

    public int getCriticality() {
        return this.mCriticality;
    }

    public boolean isIntercepted() {
        return this.mIntercept;
    }

    public boolean hasInterceptBeenSet() {
        return this.mInterceptSet;
    }

    public boolean isNewEnoughForAlerting(long j) {
        return getFreshnessMs(j) <= 2000;
    }

    public void setHidden(boolean z) {
        this.mHidden = z;
    }

    public boolean isHidden() {
        return this.mHidden;
    }

    public boolean isForegroundService() {
        return (getFlags() & 64) != 0;
    }

    public void setPostSilently(boolean z) {
        this.mPostSilently = z;
    }

    public boolean shouldPostSilently() {
        return this.mPostSilently;
    }

    public void setSuppressedVisualEffects(int i) {
        this.mSuppressedVisualEffects = i;
    }

    public int getSuppressedVisualEffects() {
        return this.mSuppressedVisualEffects;
    }

    public boolean isCategory(String str) {
        return Objects.equals(getNotification().category, str);
    }

    public boolean isAudioAttributesUsage(int i) {
        AudioAttributes audioAttributes = this.mAttributes;
        return audioAttributes != null && audioAttributes.getUsage() == i;
    }

    public long getRankingTimeMs() {
        return this.mRankingTimeMs;
    }

    public int getFreshnessMs(long j) {
        return (int) (j - this.mUpdateTimeMs);
    }

    public int getLifespanMs(long j) {
        return (int) (j - this.mCreationTimeMs);
    }

    public int getExposureMs(long j) {
        long j2 = this.mVisibleSinceMs;
        if (j2 == 0) {
            return 0;
        }
        return (int) (j - j2);
    }

    public int getInterruptionMs(long j) {
        return (int) (j - this.mInterruptionTimeMs);
    }

    public void setVisibility(boolean z, int i, int i2, NotificationRecordLogger notificationRecordLogger) {
        long currentTimeMillis = System.currentTimeMillis();
        this.mVisibleSinceMs = z ? currentTimeMillis : this.mVisibleSinceMs;
        this.stats.onVisibilityChanged(z);
        MetricsLogger.action(getLogMaker(currentTimeMillis).setCategory(128).setType(z ? 1 : 2).addTaggedData(798, Integer.valueOf(i)).addTaggedData(1395, Integer.valueOf(i2)));
        if (z) {
            setSeen();
            MetricsLogger.histogram(this.mContext, "note_freshness", getFreshnessMs(currentTimeMillis));
        }
        EventLogTags.writeNotificationVisibility(getKey(), z ? 1 : 0, getLifespanMs(currentTimeMillis), getFreshnessMs(currentTimeMillis), 0, i);
        notificationRecordLogger.logNotificationVisibility(this, z);
    }

    public final long calculateRankingTimeMs(long j) {
        Notification notification = getNotification();
        long j2 = notification.when;
        if (j2 == 0 || j2 > getSbn().getPostTime()) {
            return j > 0 ? j : getSbn().getPostTime();
        }
        return notification.when;
    }

    public void setGlobalSortKey(String str) {
        this.mGlobalSortKey = str;
    }

    public String getGlobalSortKey() {
        return this.mGlobalSortKey;
    }

    public boolean isSeen() {
        return this.mStats.hasSeen();
    }

    public void setSeen() {
        this.mStats.setSeen();
        if (this.mTextChanged) {
            setInterruptive(true);
        }
    }

    public void setAuthoritativeRank(int i) {
        this.mAuthoritativeRank = i;
    }

    public int getAuthoritativeRank() {
        return this.mAuthoritativeRank;
    }

    public String getGroupKey() {
        return getSbn().getGroupKey();
    }

    public void setOverrideGroupKey(String str) {
        getSbn().setOverrideGroupKey(str);
    }

    public NotificationChannel getChannel() {
        return this.mChannel;
    }

    public boolean getIsAppImportanceLocked() {
        return this.mIsAppImportanceLocked;
    }

    public void updateNotificationChannel(NotificationChannel notificationChannel) {
        if (notificationChannel != null) {
            this.mChannel = notificationChannel;
            calculateImportance();
            calculateUserSentiment();
        }
    }

    public void setShowBadge(boolean z) {
        this.mShowBadge = z;
    }

    public boolean canBubble() {
        return this.mAllowBubble;
    }

    public void setAllowBubble(boolean z) {
        this.mAllowBubble = z;
    }

    public boolean canShowBadge() {
        return this.mShowBadge;
    }

    public Light getLight() {
        return this.mLight;
    }

    public Uri getSound() {
        return this.mSound;
    }

    public VibrationEffect getVibration() {
        return this.mVibration;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAttributes;
    }

    public ArrayList<String> getPeopleOverride() {
        return this.mPeopleOverride;
    }

    public void setInterruptive(boolean z) {
        this.mIsInterruptive = z;
        long currentTimeMillis = System.currentTimeMillis();
        this.mInterruptionTimeMs = z ? currentTimeMillis : this.mInterruptionTimeMs;
        if (z) {
            MetricsLogger.action(getLogMaker().setCategory(1501).setType(1).addTaggedData((int) NetworkConstants.ETHER_MTU, Integer.valueOf(getInterruptionMs(currentTimeMillis))));
            MetricsLogger.histogram(this.mContext, "note_interruptive", getInterruptionMs(currentTimeMillis));
        }
    }

    public void setAudiblyAlerted(boolean z) {
        this.mLastAudiblyAlertedMs = z ? System.currentTimeMillis() : -1L;
    }

    public void setTextChanged(boolean z) {
        this.mTextChanged = z;
    }

    public void setRecordedInterruption(boolean z) {
        this.mRecordedInterruption = z;
    }

    public boolean hasRecordedInterruption() {
        return this.mRecordedInterruption;
    }

    public boolean isInterruptive() {
        return this.mIsInterruptive;
    }

    public boolean isTextChanged() {
        return this.mTextChanged;
    }

    public long getLastAudiblyAlertedMs() {
        return this.mLastAudiblyAlertedMs;
    }

    public void setPeopleOverride(ArrayList<String> arrayList) {
        this.mPeopleOverride = arrayList;
    }

    public ArrayList<SnoozeCriterion> getSnoozeCriteria() {
        return this.mSnoozeCriteria;
    }

    public void setSnoozeCriteria(ArrayList<SnoozeCriterion> arrayList) {
        this.mSnoozeCriteria = arrayList;
    }

    public final void calculateUserSentiment() {
        if ((getChannel().getUserLockedFields() & 4) != 0 || this.mIsAppImportanceLocked) {
            this.mUserSentiment = 1;
        }
    }

    public final void setUserSentiment(int i) {
        this.mUserSentiment = i;
    }

    public int getUserSentiment() {
        return this.mUserSentiment;
    }

    public NotificationStats getStats() {
        return this.mStats;
    }

    public void recordExpanded() {
        this.mStats.setExpanded();
    }

    public void recordDirectReplied() {
        this.mStats.setDirectReplied();
    }

    public void recordDismissalSurface(int i) {
        this.mStats.setDismissalSurface(i);
    }

    public void recordDismissalSentiment(int i) {
        this.mStats.setDismissalSentiment(i);
    }

    public void recordSnoozed() {
        this.mStats.setSnoozed();
    }

    public void recordViewedSettings() {
        this.mStats.setViewedSettings();
    }

    public void setNumSmartRepliesAdded(int i) {
        this.mNumberOfSmartRepliesAdded = i;
    }

    public int getNumSmartRepliesAdded() {
        return this.mNumberOfSmartRepliesAdded;
    }

    public void setNumSmartActionsAdded(int i) {
        this.mNumberOfSmartActionsAdded = i;
    }

    public int getNumSmartActionsAdded() {
        return this.mNumberOfSmartActionsAdded;
    }

    public void setSuggestionsGeneratedByAssistant(boolean z) {
        this.mSuggestionsGeneratedByAssistant = z;
    }

    public boolean getSuggestionsGeneratedByAssistant() {
        return this.mSuggestionsGeneratedByAssistant;
    }

    public boolean getEditChoicesBeforeSending() {
        return this.mEditChoicesBeforeSending;
    }

    public void setEditChoicesBeforeSending(boolean z) {
        this.mEditChoicesBeforeSending = z;
    }

    public boolean hasSeenSmartReplies() {
        return this.mHasSeenSmartReplies;
    }

    public void setSeenSmartReplies(boolean z) {
        this.mHasSeenSmartReplies = z;
    }

    public boolean hasBeenVisiblyExpanded() {
        return this.stats.hasBeenVisiblyExpanded();
    }

    public boolean isFlagBubbleRemoved() {
        return this.mFlagBubbleRemoved;
    }

    public void setFlagBubbleRemoved(boolean z) {
        this.mFlagBubbleRemoved = z;
    }

    public void setSystemGeneratedSmartActions(ArrayList<Notification.Action> arrayList) {
        this.mSystemGeneratedSmartActions = arrayList;
    }

    public ArrayList<Notification.Action> getSystemGeneratedSmartActions() {
        return this.mSystemGeneratedSmartActions;
    }

    public void setSmartReplies(ArrayList<CharSequence> arrayList) {
        this.mSmartReplies = arrayList;
    }

    public ArrayList<CharSequence> getSmartReplies() {
        return this.mSmartReplies;
    }

    public boolean isProxied() {
        return !Objects.equals(getSbn().getPackageName(), getSbn().getOpPkg());
    }

    public int getNotificationType() {
        if (isConversation()) {
            return 1;
        }
        return getImportance() >= 3 ? 2 : 4;
    }

    public ArraySet<Uri> getGrantableUris() {
        return this.mGrantableUris;
    }

    public void calculateGrantableUris() {
        NotificationChannel channel;
        Notification notification = getNotification();
        notification.visitUris(new Consumer() { // from class: com.android.server.notification.NotificationRecord$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationRecord.this.lambda$calculateGrantableUris$0((Uri) obj);
            }
        });
        if (notification.getChannelId() == null || (channel = getChannel()) == null) {
            return;
        }
        visitGrantableUri(channel.getSound(), (channel.getUserLockedFields() & 32) != 0, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$calculateGrantableUris$0(Uri uri) {
        visitGrantableUri(uri, false, false);
    }

    public final void visitGrantableUri(Uri uri, boolean z, boolean z2) {
        int uid;
        if (uri == null || !"content".equals(uri.getScheme()) || (uid = getSbn().getUid()) == 1000) {
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mUgmInternal.checkGrantUriPermission(uid, null, ContentProvider.getUriWithoutUserId(uri), 1, ContentProvider.getUserIdFromUri(uri, UserHandle.getUserId(uid)));
                if (this.mGrantableUris == null) {
                    this.mGrantableUris = new ArraySet<>();
                }
                this.mGrantableUris.add(uri);
            } catch (SecurityException e) {
                if (!z) {
                    if (z2) {
                        this.mSound = Settings.System.DEFAULT_NOTIFICATION_URI;
                        Log.w("NotificationRecord", "Replacing " + uri + " from " + uid + ": " + e.getMessage());
                    } else if (this.mTargetSdkVersion >= 28) {
                        throw e;
                    } else {
                        Log.w("NotificationRecord", "Ignoring " + uri + " from " + uid + ": " + e.getMessage());
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public LogMaker getLogMaker(long j) {
        LogMaker addTaggedData = getSbn().getLogMaker().addTaggedData(858, Integer.valueOf(this.mImportance)).addTaggedData(793, Integer.valueOf(getLifespanMs(j))).addTaggedData(795, Integer.valueOf(getFreshnessMs(j))).addTaggedData(794, Integer.valueOf(getExposureMs(j))).addTaggedData((int) NetworkConstants.ETHER_MTU, Integer.valueOf(getInterruptionMs(j)));
        int i = this.mImportanceExplanationCode;
        if (i != 0) {
            addTaggedData.addTaggedData(1688, Integer.valueOf(i));
            int i2 = this.mImportanceExplanationCode;
            if ((i2 == 3 || i2 == 4) && this.stats.naturalImportance != -1000) {
                addTaggedData.addTaggedData(1690, Integer.valueOf(this.mInitialImportanceExplanationCode));
                addTaggedData.addTaggedData(1689, Integer.valueOf(this.stats.naturalImportance));
            }
        }
        int i3 = this.mAssistantImportance;
        if (i3 != -1000) {
            addTaggedData.addTaggedData(1691, Integer.valueOf(i3));
        }
        String str = this.mAdjustmentIssuer;
        if (str != null) {
            addTaggedData.addTaggedData(1742, Integer.valueOf(str.hashCode()));
        }
        return addTaggedData;
    }

    public LogMaker getLogMaker() {
        return getLogMaker(System.currentTimeMillis());
    }

    public LogMaker getItemLogMaker() {
        return getLogMaker().setCategory(128);
    }

    public boolean hasUndecoratedRemoteView() {
        Notification notification = getNotification();
        return (notification.contentView != null || notification.bigContentView != null || notification.headsUpContentView != null) && !(notification.isStyle(Notification.DecoratedCustomViewStyle.class) || notification.isStyle(Notification.DecoratedMediaCustomViewStyle.class));
    }

    public void setShortcutInfo(ShortcutInfo shortcutInfo) {
        this.mShortcutInfo = shortcutInfo;
    }

    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }

    public void setHasSentValidMsg(boolean z) {
        this.mHasSentValidMsg = z;
    }

    public void userDemotedAppFromConvoSpace(boolean z) {
        this.mAppDemotedFromConvo = z;
    }

    public void setPkgAllowedAsConvo(boolean z) {
        this.mPkgAllowedAsConvo = z;
    }

    public boolean isConversation() {
        ShortcutInfo shortcutInfo;
        Notification notification = getNotification();
        if (this.mChannel.isDemoted() || this.mAppDemotedFromConvo || this.mIsNotConversationOverride) {
            return false;
        }
        if (!notification.isStyle(Notification.MessagingStyle.class)) {
            return this.mPkgAllowedAsConvo && this.mTargetSdkVersion < 30 && "msg".equals(getNotification().category);
        } else if (this.mTargetSdkVersion >= 30 && notification.isStyle(Notification.MessagingStyle.class) && ((shortcutInfo = this.mShortcutInfo) == null || isOnlyBots(shortcutInfo.getPersons()))) {
            return false;
        } else {
            return (this.mHasSentValidMsg && this.mShortcutInfo == null) ? false : true;
        }
    }

    public final boolean isOnlyBots(Person[] personArr) {
        if (personArr == null || personArr.length == 0) {
            return false;
        }
        for (Person person : personArr) {
            if (!person.isBot()) {
                return false;
            }
        }
        return true;
    }

    public StatusBarNotification getSbn() {
        return this.sbn;
    }

    public boolean rankingScoreMatches(float f) {
        return ((double) Math.abs(this.mRankingScore - f)) < 1.0E-4d;
    }

    public void setPendingLogUpdate(boolean z) {
        this.mPendingLogUpdate = z;
    }

    public boolean hasPendingLogUpdate() {
        return this.mPendingLogUpdate;
    }

    public void mergePhoneNumbers(ArraySet<String> arraySet) {
        if (arraySet == null || arraySet.size() == 0) {
            return;
        }
        if (this.mPhoneNumbers == null) {
            this.mPhoneNumbers = new ArraySet<>();
        }
        this.mPhoneNumbers.addAll((ArraySet<? extends String>) arraySet);
    }

    public ArraySet<String> getPhoneNumbers() {
        return this.mPhoneNumbers;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static final class Light {
        public final int color;
        public final int offMs;
        public final int onMs;

        public Light(int i, int i2, int i3) {
            this.color = i;
            this.onMs = i2;
            this.offMs = i3;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Light.class != obj.getClass()) {
                return false;
            }
            Light light = (Light) obj;
            return this.color == light.color && this.onMs == light.onMs && this.offMs == light.offMs;
        }

        public int hashCode() {
            return (((this.color * 31) + this.onMs) * 31) + this.offMs;
        }

        public String toString() {
            return "Light{color=" + this.color + ", onMs=" + this.onMs + ", offMs=" + this.offMs + '}';
        }
    }
}
