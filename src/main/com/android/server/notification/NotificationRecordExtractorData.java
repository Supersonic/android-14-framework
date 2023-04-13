package com.android.server.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.service.notification.SnoozeCriterion;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class NotificationRecordExtractorData {
    public final boolean mAllowBubble;
    public final NotificationChannel mChannel;
    public final String mGroupKey;
    public final int mImportance;
    public final boolean mIsBubble;
    public final boolean mIsConversation;
    public final ArrayList<String> mOverridePeople;
    public final int mPosition;
    public final int mProposedImportance;
    public final float mRankingScore;
    public final boolean mSensitiveContent;
    public final boolean mShowBadge;
    public final ArrayList<CharSequence> mSmartReplies;
    public final ArrayList<SnoozeCriterion> mSnoozeCriteria;
    public final Integer mSuppressVisually;
    public final ArrayList<Notification.Action> mSystemSmartActions;
    public final Integer mUserSentiment;
    public final int mVisibility;

    public NotificationRecordExtractorData(int i, int i2, boolean z, boolean z2, boolean z3, NotificationChannel notificationChannel, String str, ArrayList<String> arrayList, ArrayList<SnoozeCriterion> arrayList2, Integer num, Integer num2, ArrayList<Notification.Action> arrayList3, ArrayList<CharSequence> arrayList4, int i3, float f, boolean z4, int i4, boolean z5) {
        this.mPosition = i;
        this.mVisibility = i2;
        this.mShowBadge = z;
        this.mAllowBubble = z2;
        this.mIsBubble = z3;
        this.mChannel = notificationChannel;
        this.mGroupKey = str;
        this.mOverridePeople = arrayList;
        this.mSnoozeCriteria = arrayList2;
        this.mUserSentiment = num;
        this.mSuppressVisually = num2;
        this.mSystemSmartActions = arrayList3;
        this.mSmartReplies = arrayList4;
        this.mImportance = i3;
        this.mRankingScore = f;
        this.mIsConversation = z4;
        this.mProposedImportance = i4;
        this.mSensitiveContent = z5;
    }

    public boolean hasDiffForRankingLocked(NotificationRecord notificationRecord, int i) {
        return (this.mPosition == i && this.mVisibility == notificationRecord.getPackageVisibilityOverride() && this.mShowBadge == notificationRecord.canShowBadge() && this.mAllowBubble == notificationRecord.canBubble() && this.mIsBubble == notificationRecord.getNotification().isBubbleNotification() && Objects.equals(this.mChannel, notificationRecord.getChannel()) && Objects.equals(this.mGroupKey, notificationRecord.getGroupKey()) && Objects.equals(this.mOverridePeople, notificationRecord.getPeopleOverride()) && Objects.equals(this.mSnoozeCriteria, notificationRecord.getSnoozeCriteria()) && Objects.equals(this.mUserSentiment, Integer.valueOf(notificationRecord.getUserSentiment())) && Objects.equals(this.mSuppressVisually, Integer.valueOf(notificationRecord.getSuppressedVisualEffects())) && Objects.equals(this.mSystemSmartActions, notificationRecord.getSystemGeneratedSmartActions()) && Objects.equals(this.mSmartReplies, notificationRecord.getSmartReplies()) && this.mImportance == notificationRecord.getImportance() && this.mProposedImportance == notificationRecord.getProposedImportance() && this.mSensitiveContent == notificationRecord.hasSensitiveContent()) ? false : true;
    }

    public boolean hasDiffForLoggingLocked(NotificationRecord notificationRecord, int i) {
        return (this.mPosition == i && Objects.equals(this.mChannel, notificationRecord.getChannel()) && Objects.equals(this.mGroupKey, notificationRecord.getGroupKey()) && Objects.equals(this.mOverridePeople, notificationRecord.getPeopleOverride()) && Objects.equals(this.mSnoozeCriteria, notificationRecord.getSnoozeCriteria()) && Objects.equals(this.mUserSentiment, Integer.valueOf(notificationRecord.getUserSentiment())) && Objects.equals(this.mSystemSmartActions, notificationRecord.getSystemGeneratedSmartActions()) && Objects.equals(this.mSmartReplies, notificationRecord.getSmartReplies()) && this.mImportance == notificationRecord.getImportance() && notificationRecord.rankingScoreMatches(this.mRankingScore) && this.mIsConversation == notificationRecord.isConversation() && this.mProposedImportance == notificationRecord.getProposedImportance() && this.mSensitiveContent == notificationRecord.hasSensitiveContent()) ? false : true;
    }
}
