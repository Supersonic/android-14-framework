package android.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.service.notification.IStatusBarNotificationHolder;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes3.dex */
public interface INotificationListener extends IInterface {
    void onActionClicked(String str, Notification.Action action, int i) throws RemoteException;

    void onAllowedAdjustmentsChanged() throws RemoteException;

    void onInterruptionFilterChanged(int i) throws RemoteException;

    void onListenerConnected(NotificationRankingUpdate notificationRankingUpdate) throws RemoteException;

    void onListenerHintsChanged(int i) throws RemoteException;

    void onNotificationChannelGroupModification(String str, UserHandle userHandle, NotificationChannelGroup notificationChannelGroup, int i) throws RemoteException;

    void onNotificationChannelModification(String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) throws RemoteException;

    void onNotificationClicked(String str) throws RemoteException;

    void onNotificationDirectReply(String str) throws RemoteException;

    void onNotificationEnqueuedWithChannel(IStatusBarNotificationHolder iStatusBarNotificationHolder, NotificationChannel notificationChannel, NotificationRankingUpdate notificationRankingUpdate) throws RemoteException;

    void onNotificationExpansionChanged(String str, boolean z, boolean z2) throws RemoteException;

    void onNotificationFeedbackReceived(String str, NotificationRankingUpdate notificationRankingUpdate, Bundle bundle) throws RemoteException;

    void onNotificationPosted(IStatusBarNotificationHolder iStatusBarNotificationHolder, NotificationRankingUpdate notificationRankingUpdate) throws RemoteException;

    void onNotificationRankingUpdate(NotificationRankingUpdate notificationRankingUpdate) throws RemoteException;

    void onNotificationRemoved(IStatusBarNotificationHolder iStatusBarNotificationHolder, NotificationRankingUpdate notificationRankingUpdate, NotificationStats notificationStats, int i) throws RemoteException;

    void onNotificationSnoozedUntilContext(IStatusBarNotificationHolder iStatusBarNotificationHolder, String str) throws RemoteException;

    void onNotificationVisibilityChanged(String str, boolean z) throws RemoteException;

    void onNotificationsSeen(List<String> list) throws RemoteException;

    void onPanelHidden() throws RemoteException;

    void onPanelRevealed(int i) throws RemoteException;

    void onStatusBarIconsBehaviorChanged(boolean z) throws RemoteException;

    void onSuggestedReplySent(String str, CharSequence charSequence, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements INotificationListener {
        @Override // android.service.notification.INotificationListener
        public void onListenerConnected(NotificationRankingUpdate update) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationPosted(IStatusBarNotificationHolder notificationHolder, NotificationRankingUpdate update) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onStatusBarIconsBehaviorChanged(boolean hideSilentStatusIcons) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationRemoved(IStatusBarNotificationHolder notificationHolder, NotificationRankingUpdate update, NotificationStats stats, int reason) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationRankingUpdate(NotificationRankingUpdate update) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onListenerHintsChanged(int hints) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onInterruptionFilterChanged(int interruptionFilter) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationChannelModification(String pkgName, UserHandle user, NotificationChannel channel, int modificationType) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationChannelGroupModification(String pkgName, UserHandle user, NotificationChannelGroup group, int modificationType) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationEnqueuedWithChannel(IStatusBarNotificationHolder notificationHolder, NotificationChannel channel, NotificationRankingUpdate update) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationSnoozedUntilContext(IStatusBarNotificationHolder notificationHolder, String snoozeCriterionId) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationsSeen(List<String> keys) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onPanelRevealed(int items) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onPanelHidden() throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationVisibilityChanged(String key, boolean isVisible) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationExpansionChanged(String key, boolean userAction, boolean expanded) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationDirectReply(String key) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onSuggestedReplySent(String key, CharSequence reply, int source) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onActionClicked(String key, Notification.Action action, int source) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationClicked(String key) throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onAllowedAdjustmentsChanged() throws RemoteException {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationFeedbackReceived(String key, NotificationRankingUpdate update, Bundle feedback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements INotificationListener {
        public static final String DESCRIPTOR = "android.service.notification.INotificationListener";
        static final int TRANSACTION_onActionClicked = 19;
        static final int TRANSACTION_onAllowedAdjustmentsChanged = 21;
        static final int TRANSACTION_onInterruptionFilterChanged = 7;
        static final int TRANSACTION_onListenerConnected = 1;
        static final int TRANSACTION_onListenerHintsChanged = 6;
        static final int TRANSACTION_onNotificationChannelGroupModification = 9;
        static final int TRANSACTION_onNotificationChannelModification = 8;
        static final int TRANSACTION_onNotificationClicked = 20;
        static final int TRANSACTION_onNotificationDirectReply = 17;
        static final int TRANSACTION_onNotificationEnqueuedWithChannel = 10;
        static final int TRANSACTION_onNotificationExpansionChanged = 16;
        static final int TRANSACTION_onNotificationFeedbackReceived = 22;
        static final int TRANSACTION_onNotificationPosted = 2;
        static final int TRANSACTION_onNotificationRankingUpdate = 5;
        static final int TRANSACTION_onNotificationRemoved = 4;
        static final int TRANSACTION_onNotificationSnoozedUntilContext = 11;
        static final int TRANSACTION_onNotificationVisibilityChanged = 15;
        static final int TRANSACTION_onNotificationsSeen = 12;
        static final int TRANSACTION_onPanelHidden = 14;
        static final int TRANSACTION_onPanelRevealed = 13;
        static final int TRANSACTION_onStatusBarIconsBehaviorChanged = 3;
        static final int TRANSACTION_onSuggestedReplySent = 18;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INotificationListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INotificationListener)) {
                return (INotificationListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "onListenerConnected";
                case 2:
                    return "onNotificationPosted";
                case 3:
                    return "onStatusBarIconsBehaviorChanged";
                case 4:
                    return "onNotificationRemoved";
                case 5:
                    return "onNotificationRankingUpdate";
                case 6:
                    return "onListenerHintsChanged";
                case 7:
                    return "onInterruptionFilterChanged";
                case 8:
                    return "onNotificationChannelModification";
                case 9:
                    return "onNotificationChannelGroupModification";
                case 10:
                    return "onNotificationEnqueuedWithChannel";
                case 11:
                    return "onNotificationSnoozedUntilContext";
                case 12:
                    return "onNotificationsSeen";
                case 13:
                    return "onPanelRevealed";
                case 14:
                    return "onPanelHidden";
                case 15:
                    return "onNotificationVisibilityChanged";
                case 16:
                    return "onNotificationExpansionChanged";
                case 17:
                    return "onNotificationDirectReply";
                case 18:
                    return "onSuggestedReplySent";
                case 19:
                    return "onActionClicked";
                case 20:
                    return "onNotificationClicked";
                case 21:
                    return "onAllowedAdjustmentsChanged";
                case 22:
                    return "onNotificationFeedbackReceived";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            NotificationRankingUpdate _arg0 = (NotificationRankingUpdate) data.readTypedObject(NotificationRankingUpdate.CREATOR);
                            data.enforceNoDataAvail();
                            onListenerConnected(_arg0);
                            break;
                        case 2:
                            IStatusBarNotificationHolder _arg02 = IStatusBarNotificationHolder.Stub.asInterface(data.readStrongBinder());
                            NotificationRankingUpdate _arg1 = (NotificationRankingUpdate) data.readTypedObject(NotificationRankingUpdate.CREATOR);
                            data.enforceNoDataAvail();
                            onNotificationPosted(_arg02, _arg1);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onStatusBarIconsBehaviorChanged(_arg03);
                            break;
                        case 4:
                            IStatusBarNotificationHolder _arg04 = IStatusBarNotificationHolder.Stub.asInterface(data.readStrongBinder());
                            NotificationRankingUpdate _arg12 = (NotificationRankingUpdate) data.readTypedObject(NotificationRankingUpdate.CREATOR);
                            NotificationStats _arg2 = (NotificationStats) data.readTypedObject(NotificationStats.CREATOR);
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onNotificationRemoved(_arg04, _arg12, _arg2, _arg3);
                            break;
                        case 5:
                            NotificationRankingUpdate _arg05 = (NotificationRankingUpdate) data.readTypedObject(NotificationRankingUpdate.CREATOR);
                            data.enforceNoDataAvail();
                            onNotificationRankingUpdate(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            onListenerHintsChanged(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onInterruptionFilterChanged(_arg07);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            UserHandle _arg13 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            NotificationChannel _arg22 = (NotificationChannel) data.readTypedObject(NotificationChannel.CREATOR);
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            onNotificationChannelModification(_arg08, _arg13, _arg22, _arg32);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            UserHandle _arg14 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            NotificationChannelGroup _arg23 = (NotificationChannelGroup) data.readTypedObject(NotificationChannelGroup.CREATOR);
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            onNotificationChannelGroupModification(_arg09, _arg14, _arg23, _arg33);
                            break;
                        case 10:
                            IStatusBarNotificationHolder _arg010 = IStatusBarNotificationHolder.Stub.asInterface(data.readStrongBinder());
                            NotificationChannel _arg15 = (NotificationChannel) data.readTypedObject(NotificationChannel.CREATOR);
                            NotificationRankingUpdate _arg24 = (NotificationRankingUpdate) data.readTypedObject(NotificationRankingUpdate.CREATOR);
                            data.enforceNoDataAvail();
                            onNotificationEnqueuedWithChannel(_arg010, _arg15, _arg24);
                            break;
                        case 11:
                            IStatusBarNotificationHolder _arg011 = IStatusBarNotificationHolder.Stub.asInterface(data.readStrongBinder());
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            onNotificationSnoozedUntilContext(_arg011, _arg16);
                            break;
                        case 12:
                            List<String> _arg012 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            onNotificationsSeen(_arg012);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            onPanelRevealed(_arg013);
                            break;
                        case 14:
                            onPanelHidden();
                            break;
                        case 15:
                            String _arg014 = data.readString();
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onNotificationVisibilityChanged(_arg014, _arg17);
                            break;
                        case 16:
                            String _arg015 = data.readString();
                            boolean _arg18 = data.readBoolean();
                            boolean _arg25 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onNotificationExpansionChanged(_arg015, _arg18, _arg25);
                            break;
                        case 17:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            onNotificationDirectReply(_arg016);
                            break;
                        case 18:
                            String _arg017 = data.readString();
                            CharSequence _arg19 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            onSuggestedReplySent(_arg017, _arg19, _arg26);
                            break;
                        case 19:
                            String _arg018 = data.readString();
                            Notification.Action _arg110 = (Notification.Action) data.readTypedObject(Notification.Action.CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            onActionClicked(_arg018, _arg110, _arg27);
                            break;
                        case 20:
                            String _arg019 = data.readString();
                            data.enforceNoDataAvail();
                            onNotificationClicked(_arg019);
                            break;
                        case 21:
                            onAllowedAdjustmentsChanged();
                            break;
                        case 22:
                            String _arg020 = data.readString();
                            NotificationRankingUpdate _arg111 = (NotificationRankingUpdate) data.readTypedObject(NotificationRankingUpdate.CREATOR);
                            Bundle _arg28 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onNotificationFeedbackReceived(_arg020, _arg111, _arg28);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements INotificationListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.service.notification.INotificationListener
            public void onListenerConnected(NotificationRankingUpdate update) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(update, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationPosted(IStatusBarNotificationHolder notificationHolder, NotificationRankingUpdate update) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(notificationHolder);
                    _data.writeTypedObject(update, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onStatusBarIconsBehaviorChanged(boolean hideSilentStatusIcons) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(hideSilentStatusIcons);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationRemoved(IStatusBarNotificationHolder notificationHolder, NotificationRankingUpdate update, NotificationStats stats, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(notificationHolder);
                    _data.writeTypedObject(update, 0);
                    _data.writeTypedObject(stats, 0);
                    _data.writeInt(reason);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationRankingUpdate(NotificationRankingUpdate update) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(update, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onListenerHintsChanged(int hints) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hints);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onInterruptionFilterChanged(int interruptionFilter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(interruptionFilter);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationChannelModification(String pkgName, UserHandle user, NotificationChannel channel, int modificationType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeTypedObject(user, 0);
                    _data.writeTypedObject(channel, 0);
                    _data.writeInt(modificationType);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationChannelGroupModification(String pkgName, UserHandle user, NotificationChannelGroup group, int modificationType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeTypedObject(user, 0);
                    _data.writeTypedObject(group, 0);
                    _data.writeInt(modificationType);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationEnqueuedWithChannel(IStatusBarNotificationHolder notificationHolder, NotificationChannel channel, NotificationRankingUpdate update) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(notificationHolder);
                    _data.writeTypedObject(channel, 0);
                    _data.writeTypedObject(update, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationSnoozedUntilContext(IStatusBarNotificationHolder notificationHolder, String snoozeCriterionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(notificationHolder);
                    _data.writeString(snoozeCriterionId);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationsSeen(List<String> keys) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(keys);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onPanelRevealed(int items) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(items);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onPanelHidden() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationVisibilityChanged(String key, boolean isVisible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeBoolean(isVisible);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationExpansionChanged(String key, boolean userAction, boolean expanded) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeBoolean(userAction);
                    _data.writeBoolean(expanded);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationDirectReply(String key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onSuggestedReplySent(String key, CharSequence reply, int source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    if (reply != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(reply, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(source);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onActionClicked(String key, Notification.Action action, int source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeTypedObject(action, 0);
                    _data.writeInt(source);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationClicked(String key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onAllowedAdjustmentsChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.notification.INotificationListener
            public void onNotificationFeedbackReceived(String key, NotificationRankingUpdate update, Bundle feedback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeTypedObject(update, 0);
                    _data.writeTypedObject(feedback, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 21;
        }
    }
}
