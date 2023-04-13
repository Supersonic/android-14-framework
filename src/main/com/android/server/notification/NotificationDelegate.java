package com.android.server.notification;

import android.app.Notification;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.internal.statusbar.NotificationVisibility;
/* loaded from: classes2.dex */
public interface NotificationDelegate {
    void clearEffects();

    void clearInlineReplyUriPermissions(String str, int i);

    void grantInlineReplyUriPermission(String str, Uri uri, UserHandle userHandle, String str2, int i);

    void onBubbleMetadataFlagChanged(String str, int i);

    void onClearAll(int i, int i2, int i3);

    void onNotificationActionClick(int i, int i2, String str, int i3, Notification.Action action, NotificationVisibility notificationVisibility, boolean z);

    void onNotificationBubbleChanged(String str, boolean z, int i);

    void onNotificationClear(int i, int i2, String str, int i3, String str2, int i4, int i5, NotificationVisibility notificationVisibility);

    void onNotificationClick(int i, int i2, String str, NotificationVisibility notificationVisibility);

    void onNotificationDirectReplied(String str);

    void onNotificationError(int i, int i2, String str, String str2, int i3, int i4, int i5, String str3, int i6);

    void onNotificationExpansionChanged(String str, boolean z, boolean z2, int i);

    void onNotificationFeedbackReceived(String str, Bundle bundle);

    void onNotificationSettingsViewed(String str);

    void onNotificationSmartReplySent(String str, int i, CharSequence charSequence, int i2, boolean z);

    void onNotificationSmartSuggestionsAdded(String str, int i, int i2, boolean z, boolean z2);

    void onNotificationVisibilityChanged(NotificationVisibility[] notificationVisibilityArr, NotificationVisibility[] notificationVisibilityArr2);

    void onPanelHidden();

    void onPanelRevealed(boolean z, int i);

    void onSetDisabled(int i);

    void prepareForPossibleShutdown();
}
