package android.app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.window.TaskSnapshot;
/* loaded from: classes.dex */
public interface ITaskStackListener extends IInterface {
    public static final int FORCED_RESIZEABLE_REASON_SECONDARY_DISPLAY = 2;
    public static final int FORCED_RESIZEABLE_REASON_SPLIT_SCREEN = 1;

    void onActivityDismissingDockedTask() throws RemoteException;

    void onActivityForcedResizable(String str, int i, int i2) throws RemoteException;

    void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo runningTaskInfo, int i) throws RemoteException;

    void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo runningTaskInfo, int i) throws RemoteException;

    void onActivityPinned(String str, int i, int i2, int i3) throws RemoteException;

    void onActivityRequestedOrientationChanged(int i, int i2) throws RemoteException;

    void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) throws RemoteException;

    void onActivityRotation(int i) throws RemoteException;

    void onActivityUnpinned() throws RemoteException;

    void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onLockTaskModeChanged(int i) throws RemoteException;

    void onRecentTaskListFrozenChanged(boolean z) throws RemoteException;

    void onRecentTaskListUpdated() throws RemoteException;

    void onTaskCreated(int i, ComponentName componentName) throws RemoteException;

    void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onTaskDisplayChanged(int i, int i2) throws RemoteException;

    void onTaskFocusChanged(int i, boolean z) throws RemoteException;

    void onTaskMovedToBack(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onTaskMovedToFront(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onTaskProfileLocked(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onTaskRemovalStarted(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onTaskRemoved(int i) throws RemoteException;

    void onTaskRequestedOrientationChanged(int i, int i2) throws RemoteException;

    void onTaskSnapshotChanged(int i, TaskSnapshot taskSnapshot) throws RemoteException;

    void onTaskStackChanged() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ITaskStackListener {
        @Override // android.app.ITaskStackListener
        public void onTaskStackChanged() throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityPinned(String packageName, int userId, int taskId, int stackId) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityUnpinned() throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible, boolean clearedTask, boolean wasVisible) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityForcedResizable(String packageName, int taskId, int reason) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityDismissingDockedTask() throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskRemoved(int taskId) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskProfileLocked(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskSnapshotChanged(int taskId, TaskSnapshot snapshot) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onRecentTaskListUpdated() throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onRecentTaskListFrozenChanged(boolean frozen) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskFocusChanged(int taskId, boolean focused) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onActivityRotation(int displayId) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onTaskMovedToBack(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.app.ITaskStackListener
        public void onLockTaskModeChanged(int mode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITaskStackListener {
        public static final String DESCRIPTOR = "android.app.ITaskStackListener";
        static final int TRANSACTION_onActivityDismissingDockedTask = 6;
        static final int TRANSACTION_onActivityForcedResizable = 5;
        static final int TRANSACTION_onActivityLaunchOnSecondaryDisplayFailed = 7;
        static final int TRANSACTION_onActivityLaunchOnSecondaryDisplayRerouted = 8;
        static final int TRANSACTION_onActivityPinned = 2;
        static final int TRANSACTION_onActivityRequestedOrientationChanged = 13;
        static final int TRANSACTION_onActivityRestartAttempt = 4;
        static final int TRANSACTION_onActivityRotation = 23;
        static final int TRANSACTION_onActivityUnpinned = 3;
        static final int TRANSACTION_onBackPressedOnTaskRoot = 17;
        static final int TRANSACTION_onLockTaskModeChanged = 25;
        static final int TRANSACTION_onRecentTaskListFrozenChanged = 20;
        static final int TRANSACTION_onRecentTaskListUpdated = 19;
        static final int TRANSACTION_onTaskCreated = 9;
        static final int TRANSACTION_onTaskDescriptionChanged = 12;
        static final int TRANSACTION_onTaskDisplayChanged = 18;
        static final int TRANSACTION_onTaskFocusChanged = 21;
        static final int TRANSACTION_onTaskMovedToBack = 24;
        static final int TRANSACTION_onTaskMovedToFront = 11;
        static final int TRANSACTION_onTaskProfileLocked = 15;
        static final int TRANSACTION_onTaskRemovalStarted = 14;
        static final int TRANSACTION_onTaskRemoved = 10;
        static final int TRANSACTION_onTaskRequestedOrientationChanged = 22;
        static final int TRANSACTION_onTaskSnapshotChanged = 16;
        static final int TRANSACTION_onTaskStackChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITaskStackListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITaskStackListener)) {
                return (ITaskStackListener) iin;
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
                    return "onTaskStackChanged";
                case 2:
                    return "onActivityPinned";
                case 3:
                    return "onActivityUnpinned";
                case 4:
                    return "onActivityRestartAttempt";
                case 5:
                    return "onActivityForcedResizable";
                case 6:
                    return "onActivityDismissingDockedTask";
                case 7:
                    return "onActivityLaunchOnSecondaryDisplayFailed";
                case 8:
                    return "onActivityLaunchOnSecondaryDisplayRerouted";
                case 9:
                    return "onTaskCreated";
                case 10:
                    return "onTaskRemoved";
                case 11:
                    return "onTaskMovedToFront";
                case 12:
                    return "onTaskDescriptionChanged";
                case 13:
                    return "onActivityRequestedOrientationChanged";
                case 14:
                    return "onTaskRemovalStarted";
                case 15:
                    return "onTaskProfileLocked";
                case 16:
                    return "onTaskSnapshotChanged";
                case 17:
                    return "onBackPressedOnTaskRoot";
                case 18:
                    return "onTaskDisplayChanged";
                case 19:
                    return "onRecentTaskListUpdated";
                case 20:
                    return "onRecentTaskListFrozenChanged";
                case 21:
                    return "onTaskFocusChanged";
                case 22:
                    return "onTaskRequestedOrientationChanged";
                case 23:
                    return "onActivityRotation";
                case 24:
                    return "onTaskMovedToBack";
                case 25:
                    return "onLockTaskModeChanged";
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
                            onTaskStackChanged();
                            break;
                        case 2:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onActivityPinned(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 3:
                            onActivityUnpinned();
                            break;
                        case 4:
                            ActivityManager.RunningTaskInfo _arg02 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            boolean _arg12 = data.readBoolean();
                            boolean _arg22 = data.readBoolean();
                            boolean _arg32 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onActivityRestartAttempt(_arg02, _arg12, _arg22, _arg32);
                            break;
                        case 5:
                            String _arg03 = data.readString();
                            int _arg13 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            onActivityForcedResizable(_arg03, _arg13, _arg23);
                            break;
                        case 6:
                            onActivityDismissingDockedTask();
                            break;
                        case 7:
                            ActivityManager.RunningTaskInfo _arg04 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            onActivityLaunchOnSecondaryDisplayFailed(_arg04, _arg14);
                            break;
                        case 8:
                            ActivityManager.RunningTaskInfo _arg05 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            onActivityLaunchOnSecondaryDisplayRerouted(_arg05, _arg15);
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            ComponentName _arg16 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskCreated(_arg06, _arg16);
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onTaskRemoved(_arg07);
                            break;
                        case 11:
                            ActivityManager.RunningTaskInfo _arg08 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskMovedToFront(_arg08);
                            break;
                        case 12:
                            ActivityManager.RunningTaskInfo _arg09 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskDescriptionChanged(_arg09);
                            break;
                        case 13:
                            int _arg010 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            onActivityRequestedOrientationChanged(_arg010, _arg17);
                            break;
                        case 14:
                            ActivityManager.RunningTaskInfo _arg011 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskRemovalStarted(_arg011);
                            break;
                        case 15:
                            ActivityManager.RunningTaskInfo _arg012 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskProfileLocked(_arg012);
                            break;
                        case 16:
                            int _arg013 = data.readInt();
                            TaskSnapshot _arg18 = (TaskSnapshot) data.readTypedObject(TaskSnapshot.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskSnapshotChanged(_arg013, _arg18);
                            break;
                        case 17:
                            ActivityManager.RunningTaskInfo _arg014 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onBackPressedOnTaskRoot(_arg014);
                            break;
                        case 18:
                            int _arg015 = data.readInt();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            onTaskDisplayChanged(_arg015, _arg19);
                            break;
                        case 19:
                            onRecentTaskListUpdated();
                            break;
                        case 20:
                            boolean _arg016 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onRecentTaskListFrozenChanged(_arg016);
                            break;
                        case 21:
                            int _arg017 = data.readInt();
                            boolean _arg110 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onTaskFocusChanged(_arg017, _arg110);
                            break;
                        case 22:
                            int _arg018 = data.readInt();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            onTaskRequestedOrientationChanged(_arg018, _arg111);
                            break;
                        case 23:
                            int _arg019 = data.readInt();
                            data.enforceNoDataAvail();
                            onActivityRotation(_arg019);
                            break;
                        case 24:
                            ActivityManager.RunningTaskInfo _arg020 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskMovedToBack(_arg020);
                            break;
                        case 25:
                            int _arg021 = data.readInt();
                            data.enforceNoDataAvail();
                            onLockTaskModeChanged(_arg021);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ITaskStackListener {
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

            @Override // android.app.ITaskStackListener
            public void onTaskStackChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityPinned(String packageName, int userId, int taskId, int stackId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeInt(taskId);
                    _data.writeInt(stackId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityUnpinned() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible, boolean clearedTask, boolean wasVisible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(task, 0);
                    _data.writeBoolean(homeTaskVisible);
                    _data.writeBoolean(clearedTask);
                    _data.writeBoolean(wasVisible);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityForcedResizable(String packageName, int taskId, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(taskId);
                    _data.writeInt(reason);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityDismissingDockedTask() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    _data.writeInt(requestedDisplayId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    _data.writeInt(requestedDisplayId);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(componentName, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskRemoved(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(requestedOrientation);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskProfileLocked(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskSnapshotChanged(int taskId, TaskSnapshot snapshot) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(snapshot, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(newDisplayId);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onRecentTaskListUpdated() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onRecentTaskListFrozenChanged(boolean frozen) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(frozen);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskFocusChanged(int taskId, boolean focused) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeBoolean(focused);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeInt(requestedOrientation);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onActivityRotation(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onTaskMovedToBack(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ITaskStackListener
            public void onLockTaskModeChanged(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 24;
        }
    }
}
