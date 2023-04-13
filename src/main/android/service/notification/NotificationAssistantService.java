package android.service.notification;

import android.annotation.SystemApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import com.android.internal.p028os.SomeArgs;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public abstract class NotificationAssistantService extends NotificationListenerService {
    public static final String ACTION_NOTIFICATION_ASSISTANT_DETAIL_SETTINGS = "android.service.notification.action.NOTIFICATION_ASSISTANT_DETAIL_SETTINGS";
    public static final String FEEDBACK_RATING = "feedback.rating";
    public static final String SERVICE_INTERFACE = "android.service.notification.NotificationAssistantService";
    public static final int SOURCE_FROM_APP = 0;
    public static final int SOURCE_FROM_ASSISTANT = 1;
    private static final String TAG = "NotificationAssistants";
    protected Handler mHandler;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Source {
    }

    public abstract Adjustment onNotificationEnqueued(StatusBarNotification statusBarNotification);

    public abstract void onNotificationSnoozedUntilContext(StatusBarNotification statusBarNotification, String str);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.service.notification.NotificationListenerService, android.app.Service, android.content.ContextWrapper
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.mHandler = new MyHandler(getContext().getMainLooper());
    }

    @Override // android.service.notification.NotificationListenerService, android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mWrapper == null) {
            this.mWrapper = new NotificationAssistantServiceWrapper();
        }
        return this.mWrapper;
    }

    public Adjustment onNotificationEnqueued(StatusBarNotification sbn, NotificationChannel channel) {
        return onNotificationEnqueued(sbn);
    }

    public Adjustment onNotificationEnqueued(StatusBarNotification sbn, NotificationChannel channel, NotificationListenerService.RankingMap rankingMap) {
        return onNotificationEnqueued(sbn, channel);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap, NotificationStats stats, int reason) {
        onNotificationRemoved(sbn, rankingMap, reason);
    }

    public void onNotificationsSeen(List<String> keys) {
    }

    public void onPanelRevealed(int items) {
    }

    public void onPanelHidden() {
    }

    public void onNotificationVisibilityChanged(String key, boolean isVisible) {
    }

    public void onNotificationExpansionChanged(String key, boolean isUserAction, boolean isExpanded) {
    }

    public void onNotificationDirectReplied(String key) {
    }

    public void onSuggestedReplySent(String key, CharSequence reply, int source) {
    }

    public void onActionInvoked(String key, Notification.Action action, int source) {
    }

    public void onNotificationClicked(String key) {
    }

    @Deprecated
    public void onAllowedAdjustmentsChanged() {
    }

    public void onNotificationFeedbackReceived(String key, NotificationListenerService.RankingMap rankingMap, Bundle feedback) {
    }

    public final void adjustNotification(Adjustment adjustment) {
        if (isBound()) {
            try {
                setAdjustmentIssuer(adjustment);
                getNotificationInterface().applyEnqueuedAdjustmentFromAssistant(this.mWrapper, adjustment);
            } catch (RemoteException ex) {
                Log.m105v(TAG, "Unable to contact notification manager", ex);
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    public final void adjustNotifications(List<Adjustment> adjustments) {
        if (isBound()) {
            try {
                for (Adjustment adjustment : adjustments) {
                    setAdjustmentIssuer(adjustment);
                }
                getNotificationInterface().applyAdjustmentsFromAssistant(this.mWrapper, adjustments);
            } catch (RemoteException ex) {
                Log.m105v(TAG, "Unable to contact notification manager", ex);
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    public final void unsnoozeNotification(String key) {
        if (isBound()) {
            try {
                getNotificationInterface().unsnoozeNotificationFromAssistant(this.mWrapper, key);
            } catch (RemoteException ex) {
                Log.m105v(TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    /* loaded from: classes3.dex */
    private class NotificationAssistantServiceWrapper extends NotificationListenerService.NotificationListenerWrapper {
        private NotificationAssistantServiceWrapper() {
            super();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationEnqueuedWithChannel(IStatusBarNotificationHolder sbnHolder, NotificationChannel channel, NotificationRankingUpdate update) {
            try {
                StatusBarNotification sbn = sbnHolder.get();
                if (sbn == null) {
                    Log.m104w(NotificationAssistantService.TAG, "onNotificationEnqueuedWithChannel: Error receiving StatusBarNotification");
                    return;
                }
                NotificationAssistantService.this.applyUpdateLocked(update);
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = sbn;
                args.arg2 = channel;
                args.arg3 = NotificationAssistantService.this.getCurrentRanking();
                NotificationAssistantService.this.mHandler.obtainMessage(1, args).sendToTarget();
            } catch (RemoteException e) {
                Log.m103w(NotificationAssistantService.TAG, "onNotificationEnqueued: Error receiving StatusBarNotification", e);
            }
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationSnoozedUntilContext(IStatusBarNotificationHolder sbnHolder, String snoozeCriterionId) {
            try {
                StatusBarNotification sbn = sbnHolder.get();
                if (sbn == null) {
                    Log.m104w(NotificationAssistantService.TAG, "onNotificationSnoozed: Error receiving StatusBarNotification");
                    return;
                }
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = sbn;
                args.arg2 = snoozeCriterionId;
                NotificationAssistantService.this.mHandler.obtainMessage(2, args).sendToTarget();
            } catch (RemoteException e) {
                Log.m103w(NotificationAssistantService.TAG, "onNotificationSnoozed: Error receiving StatusBarNotification", e);
            }
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationsSeen(List<String> keys) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = keys;
            NotificationAssistantService.this.mHandler.obtainMessage(3, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onPanelRevealed(int items) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = items;
            NotificationAssistantService.this.mHandler.obtainMessage(9, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onPanelHidden() {
            SomeArgs args = SomeArgs.obtain();
            NotificationAssistantService.this.mHandler.obtainMessage(10, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationVisibilityChanged(String key, boolean isVisible) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            args.argi1 = isVisible ? 1 : 0;
            NotificationAssistantService.this.mHandler.obtainMessage(11, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationExpansionChanged(String key, boolean isUserAction, boolean isExpanded) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            args.argi1 = isUserAction ? 1 : 0;
            args.argi2 = isExpanded ? 1 : 0;
            NotificationAssistantService.this.mHandler.obtainMessage(4, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationDirectReply(String key) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            NotificationAssistantService.this.mHandler.obtainMessage(5, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onSuggestedReplySent(String key, CharSequence reply, int source) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            args.arg2 = reply;
            args.argi2 = source;
            NotificationAssistantService.this.mHandler.obtainMessage(6, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onActionClicked(String key, Notification.Action action, int source) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            args.arg2 = action;
            args.argi2 = source;
            NotificationAssistantService.this.mHandler.obtainMessage(7, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationClicked(String key) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            NotificationAssistantService.this.mHandler.obtainMessage(12, args).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onAllowedAdjustmentsChanged() {
            NotificationAssistantService.this.mHandler.obtainMessage(8).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationFeedbackReceived(String key, NotificationRankingUpdate update, Bundle feedback) {
            NotificationAssistantService.this.applyUpdateLocked(update);
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = key;
            args.arg2 = NotificationAssistantService.this.getCurrentRanking();
            args.arg3 = feedback;
            NotificationAssistantService.this.mHandler.obtainMessage(13, args).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAdjustmentIssuer(Adjustment adjustment) {
        if (adjustment != null) {
            adjustment.setIssuer(getOpPackageName() + "/" + getClass().getName());
        }
    }

    /* loaded from: classes3.dex */
    private final class MyHandler extends Handler {
        public static final int MSG_ON_ACTION_INVOKED = 7;
        public static final int MSG_ON_ALLOWED_ADJUSTMENTS_CHANGED = 8;
        public static final int MSG_ON_NOTIFICATIONS_SEEN = 3;
        public static final int MSG_ON_NOTIFICATION_CLICKED = 12;
        public static final int MSG_ON_NOTIFICATION_DIRECT_REPLY_SENT = 5;
        public static final int MSG_ON_NOTIFICATION_ENQUEUED = 1;
        public static final int MSG_ON_NOTIFICATION_EXPANSION_CHANGED = 4;
        public static final int MSG_ON_NOTIFICATION_FEEDBACK_RECEIVED = 13;
        public static final int MSG_ON_NOTIFICATION_SNOOZED = 2;
        public static final int MSG_ON_NOTIFICATION_VISIBILITY_CHANGED = 11;
        public static final int MSG_ON_PANEL_HIDDEN = 10;
        public static final int MSG_ON_PANEL_REVEALED = 9;
        public static final int MSG_ON_SUGGESTED_REPLY_SENT = 6;

        public MyHandler(Looper looper) {
            super(looper, null, false);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            boolean isVisible;
            switch (msg.what) {
                case 1:
                    SomeArgs args = (SomeArgs) msg.obj;
                    StatusBarNotification sbn = (StatusBarNotification) args.arg1;
                    NotificationChannel channel = (NotificationChannel) args.arg2;
                    NotificationListenerService.RankingMap ranking = (NotificationListenerService.RankingMap) args.arg3;
                    args.recycle();
                    Adjustment adjustment = NotificationAssistantService.this.onNotificationEnqueued(sbn, channel, ranking);
                    NotificationAssistantService.this.setAdjustmentIssuer(adjustment);
                    if (adjustment != null) {
                        if (!NotificationAssistantService.this.isBound()) {
                            Log.m104w(NotificationAssistantService.TAG, "MSG_ON_NOTIFICATION_ENQUEUED: service not bound, skip.");
                            return;
                        }
                        try {
                            NotificationAssistantService.this.getNotificationInterface().applyEnqueuedAdjustmentFromAssistant(NotificationAssistantService.this.mWrapper, adjustment);
                            return;
                        } catch (RemoteException ex) {
                            Log.m105v(NotificationAssistantService.TAG, "Unable to contact notification manager", ex);
                            throw ex.rethrowFromSystemServer();
                        } catch (SecurityException e) {
                            Log.m103w(NotificationAssistantService.TAG, "Enqueue adjustment failed; no longer connected", e);
                            return;
                        }
                    }
                    return;
                case 2:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    StatusBarNotification sbn2 = (StatusBarNotification) args2.arg1;
                    String snoozeCriterionId = (String) args2.arg2;
                    args2.recycle();
                    NotificationAssistantService.this.onNotificationSnoozedUntilContext(sbn2, snoozeCriterionId);
                    return;
                case 3:
                    SomeArgs args3 = (SomeArgs) msg.obj;
                    List<String> keys = (List) args3.arg1;
                    args3.recycle();
                    NotificationAssistantService.this.onNotificationsSeen(keys);
                    return;
                case 4:
                    SomeArgs args4 = (SomeArgs) msg.obj;
                    String key = (String) args4.arg1;
                    boolean isUserAction = args4.argi1 == 1;
                    isVisible = args4.argi2 == 1;
                    args4.recycle();
                    NotificationAssistantService.this.onNotificationExpansionChanged(key, isUserAction, isVisible);
                    return;
                case 5:
                    SomeArgs args5 = (SomeArgs) msg.obj;
                    String key2 = (String) args5.arg1;
                    args5.recycle();
                    NotificationAssistantService.this.onNotificationDirectReplied(key2);
                    return;
                case 6:
                    SomeArgs args6 = (SomeArgs) msg.obj;
                    String key3 = (String) args6.arg1;
                    CharSequence reply = (CharSequence) args6.arg2;
                    int source = args6.argi2;
                    args6.recycle();
                    NotificationAssistantService.this.onSuggestedReplySent(key3, reply, source);
                    return;
                case 7:
                    SomeArgs args7 = (SomeArgs) msg.obj;
                    String key4 = (String) args7.arg1;
                    Notification.Action action = (Notification.Action) args7.arg2;
                    int source2 = args7.argi2;
                    args7.recycle();
                    NotificationAssistantService.this.onActionInvoked(key4, action, source2);
                    return;
                case 8:
                    NotificationAssistantService.this.onAllowedAdjustmentsChanged();
                    return;
                case 9:
                    SomeArgs args8 = (SomeArgs) msg.obj;
                    int items = args8.argi1;
                    args8.recycle();
                    NotificationAssistantService.this.onPanelRevealed(items);
                    return;
                case 10:
                    NotificationAssistantService.this.onPanelHidden();
                    return;
                case 11:
                    SomeArgs args9 = (SomeArgs) msg.obj;
                    String key5 = (String) args9.arg1;
                    isVisible = args9.argi1 == 1;
                    args9.recycle();
                    NotificationAssistantService.this.onNotificationVisibilityChanged(key5, isVisible);
                    return;
                case 12:
                    SomeArgs args10 = (SomeArgs) msg.obj;
                    String key6 = (String) args10.arg1;
                    args10.recycle();
                    NotificationAssistantService.this.onNotificationClicked(key6);
                    return;
                case 13:
                    SomeArgs args11 = (SomeArgs) msg.obj;
                    String key7 = (String) args11.arg1;
                    NotificationListenerService.RankingMap ranking2 = (NotificationListenerService.RankingMap) args11.arg2;
                    Bundle feedback = (Bundle) args11.arg3;
                    args11.recycle();
                    NotificationAssistantService.this.onNotificationFeedbackReceived(key7, ranking2, feedback);
                    return;
                default:
                    return;
            }
        }
    }
}
