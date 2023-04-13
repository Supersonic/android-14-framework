package android.window;

import android.app.ActivityManager;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public interface ITaskOrganizer extends IInterface {
    public static final String DESCRIPTOR = "android.window.ITaskOrganizer";

    void addStartingWindow(StartingWindowInfo startingWindowInfo) throws RemoteException;

    void copySplashScreenView(int i) throws RemoteException;

    void onAppSplashScreenViewRemoved(int i) throws RemoteException;

    void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onImeDrawnOnTask(int i) throws RemoteException;

    void onTaskAppeared(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl surfaceControl) throws RemoteException;

    void onTaskInfoChanged(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void onTaskVanished(ActivityManager.RunningTaskInfo runningTaskInfo) throws RemoteException;

    void removeStartingWindow(StartingWindowRemovalInfo startingWindowRemovalInfo) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITaskOrganizer {
        @Override // android.window.ITaskOrganizer
        public void addStartingWindow(StartingWindowInfo info) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void removeStartingWindow(StartingWindowRemovalInfo removalInfo) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void copySplashScreenView(int taskId) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void onAppSplashScreenViewRemoved(int taskId) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void onTaskAppeared(ActivityManager.RunningTaskInfo taskInfo, SurfaceControl leash) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void onTaskVanished(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void onTaskInfoChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizer
        public void onImeDrawnOnTask(int taskId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITaskOrganizer {
        static final int TRANSACTION_addStartingWindow = 1;
        static final int TRANSACTION_copySplashScreenView = 3;
        static final int TRANSACTION_onAppSplashScreenViewRemoved = 4;
        static final int TRANSACTION_onBackPressedOnTaskRoot = 8;
        static final int TRANSACTION_onImeDrawnOnTask = 9;
        static final int TRANSACTION_onTaskAppeared = 5;
        static final int TRANSACTION_onTaskInfoChanged = 7;
        static final int TRANSACTION_onTaskVanished = 6;
        static final int TRANSACTION_removeStartingWindow = 2;

        public Stub() {
            attachInterface(this, ITaskOrganizer.DESCRIPTOR);
        }

        public static ITaskOrganizer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITaskOrganizer.DESCRIPTOR);
            if (iin != null && (iin instanceof ITaskOrganizer)) {
                return (ITaskOrganizer) iin;
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
                    return "addStartingWindow";
                case 2:
                    return "removeStartingWindow";
                case 3:
                    return "copySplashScreenView";
                case 4:
                    return "onAppSplashScreenViewRemoved";
                case 5:
                    return "onTaskAppeared";
                case 6:
                    return "onTaskVanished";
                case 7:
                    return "onTaskInfoChanged";
                case 8:
                    return "onBackPressedOnTaskRoot";
                case 9:
                    return "onImeDrawnOnTask";
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
                data.enforceInterface(ITaskOrganizer.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITaskOrganizer.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            StartingWindowInfo _arg0 = (StartingWindowInfo) data.readTypedObject(StartingWindowInfo.CREATOR);
                            data.enforceNoDataAvail();
                            addStartingWindow(_arg0);
                            break;
                        case 2:
                            StartingWindowRemovalInfo _arg02 = (StartingWindowRemovalInfo) data.readTypedObject(StartingWindowRemovalInfo.CREATOR);
                            data.enforceNoDataAvail();
                            removeStartingWindow(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            copySplashScreenView(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onAppSplashScreenViewRemoved(_arg04);
                            break;
                        case 5:
                            ActivityManager.RunningTaskInfo _arg05 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            SurfaceControl _arg1 = (SurfaceControl) data.readTypedObject(SurfaceControl.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskAppeared(_arg05, _arg1);
                            break;
                        case 6:
                            ActivityManager.RunningTaskInfo _arg06 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskVanished(_arg06);
                            break;
                        case 7:
                            ActivityManager.RunningTaskInfo _arg07 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTaskInfoChanged(_arg07);
                            break;
                        case 8:
                            ActivityManager.RunningTaskInfo _arg08 = (ActivityManager.RunningTaskInfo) data.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onBackPressedOnTaskRoot(_arg08);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            onImeDrawnOnTask(_arg09);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ITaskOrganizer {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITaskOrganizer.DESCRIPTOR;
            }

            @Override // android.window.ITaskOrganizer
            public void addStartingWindow(StartingWindowInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void removeStartingWindow(StartingWindowRemovalInfo removalInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(removalInfo, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void copySplashScreenView(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void onAppSplashScreenViewRemoved(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void onTaskAppeared(ActivityManager.RunningTaskInfo taskInfo, SurfaceControl leash) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    _data.writeTypedObject(leash, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void onTaskVanished(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void onTaskInfoChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(taskInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizer
            public void onImeDrawnOnTask(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskOrganizer.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
