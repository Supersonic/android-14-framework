package android.window;

import android.app.ActivityManager;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.window.ITaskOrganizer;
import java.util.List;
/* loaded from: classes4.dex */
public interface ITaskOrganizerController extends IInterface {
    public static final String DESCRIPTOR = "android.window.ITaskOrganizerController";

    void createRootTask(int i, int i2, IBinder iBinder, boolean z) throws RemoteException;

    boolean deleteRootTask(WindowContainerToken windowContainerToken) throws RemoteException;

    List<ActivityManager.RunningTaskInfo> getChildTasks(WindowContainerToken windowContainerToken, int[] iArr) throws RemoteException;

    WindowContainerToken getImeTarget(int i) throws RemoteException;

    List<ActivityManager.RunningTaskInfo> getRootTasks(int i, int[] iArr) throws RemoteException;

    ParceledListSlice<TaskAppearedInfo> registerTaskOrganizer(ITaskOrganizer iTaskOrganizer) throws RemoteException;

    void restartTaskTopActivityProcessIfVisible(WindowContainerToken windowContainerToken) throws RemoteException;

    void setInterceptBackPressedOnTaskRoot(WindowContainerToken windowContainerToken, boolean z) throws RemoteException;

    void setIsIgnoreOrientationRequestDisabled(boolean z) throws RemoteException;

    void unregisterTaskOrganizer(ITaskOrganizer iTaskOrganizer) throws RemoteException;

    void updateCameraCompatControlState(WindowContainerToken windowContainerToken, int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITaskOrganizerController {
        @Override // android.window.ITaskOrganizerController
        public ParceledListSlice<TaskAppearedInfo> registerTaskOrganizer(ITaskOrganizer organizer) throws RemoteException {
            return null;
        }

        @Override // android.window.ITaskOrganizerController
        public void unregisterTaskOrganizer(ITaskOrganizer organizer) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizerController
        public void createRootTask(int displayId, int windowingMode, IBinder launchCookie, boolean removeWithTaskOrganizer) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizerController
        public boolean deleteRootTask(WindowContainerToken task) throws RemoteException {
            return false;
        }

        @Override // android.window.ITaskOrganizerController
        public List<ActivityManager.RunningTaskInfo> getChildTasks(WindowContainerToken parent, int[] activityTypes) throws RemoteException {
            return null;
        }

        @Override // android.window.ITaskOrganizerController
        public List<ActivityManager.RunningTaskInfo> getRootTasks(int displayId, int[] activityTypes) throws RemoteException {
            return null;
        }

        @Override // android.window.ITaskOrganizerController
        public WindowContainerToken getImeTarget(int display) throws RemoteException {
            return null;
        }

        @Override // android.window.ITaskOrganizerController
        public void setInterceptBackPressedOnTaskRoot(WindowContainerToken task, boolean interceptBackPressed) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizerController
        public void restartTaskTopActivityProcessIfVisible(WindowContainerToken task) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizerController
        public void updateCameraCompatControlState(WindowContainerToken task, int state) throws RemoteException {
        }

        @Override // android.window.ITaskOrganizerController
        public void setIsIgnoreOrientationRequestDisabled(boolean isDisabled) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITaskOrganizerController {
        static final int TRANSACTION_createRootTask = 3;
        static final int TRANSACTION_deleteRootTask = 4;
        static final int TRANSACTION_getChildTasks = 5;
        static final int TRANSACTION_getImeTarget = 7;
        static final int TRANSACTION_getRootTasks = 6;
        static final int TRANSACTION_registerTaskOrganizer = 1;
        static final int TRANSACTION_restartTaskTopActivityProcessIfVisible = 9;
        static final int TRANSACTION_setInterceptBackPressedOnTaskRoot = 8;
        static final int TRANSACTION_setIsIgnoreOrientationRequestDisabled = 11;
        static final int TRANSACTION_unregisterTaskOrganizer = 2;
        static final int TRANSACTION_updateCameraCompatControlState = 10;

        public Stub() {
            attachInterface(this, ITaskOrganizerController.DESCRIPTOR);
        }

        public static ITaskOrganizerController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITaskOrganizerController.DESCRIPTOR);
            if (iin != null && (iin instanceof ITaskOrganizerController)) {
                return (ITaskOrganizerController) iin;
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
                    return "registerTaskOrganizer";
                case 2:
                    return "unregisterTaskOrganizer";
                case 3:
                    return "createRootTask";
                case 4:
                    return "deleteRootTask";
                case 5:
                    return "getChildTasks";
                case 6:
                    return "getRootTasks";
                case 7:
                    return "getImeTarget";
                case 8:
                    return "setInterceptBackPressedOnTaskRoot";
                case 9:
                    return "restartTaskTopActivityProcessIfVisible";
                case 10:
                    return "updateCameraCompatControlState";
                case 11:
                    return "setIsIgnoreOrientationRequestDisabled";
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
                data.enforceInterface(ITaskOrganizerController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITaskOrganizerController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ITaskOrganizer _arg0 = ITaskOrganizer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ParceledListSlice<TaskAppearedInfo> _result = registerTaskOrganizer(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ITaskOrganizer _arg02 = ITaskOrganizer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterTaskOrganizer(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg1 = data.readInt();
                            IBinder _arg2 = data.readStrongBinder();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            createRootTask(_arg03, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 4:
                            WindowContainerToken _arg04 = (WindowContainerToken) data.readTypedObject(WindowContainerToken.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result2 = deleteRootTask(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            WindowContainerToken _arg05 = (WindowContainerToken) data.readTypedObject(WindowContainerToken.CREATOR);
                            int[] _arg12 = data.createIntArray();
                            data.enforceNoDataAvail();
                            List<ActivityManager.RunningTaskInfo> _result3 = getChildTasks(_arg05, _arg12);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int[] _arg13 = data.createIntArray();
                            data.enforceNoDataAvail();
                            List<ActivityManager.RunningTaskInfo> _result4 = getRootTasks(_arg06, _arg13);
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            WindowContainerToken _result5 = getImeTarget(_arg07);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 8:
                            WindowContainerToken _arg08 = (WindowContainerToken) data.readTypedObject(WindowContainerToken.CREATOR);
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setInterceptBackPressedOnTaskRoot(_arg08, _arg14);
                            reply.writeNoException();
                            break;
                        case 9:
                            WindowContainerToken _arg09 = (WindowContainerToken) data.readTypedObject(WindowContainerToken.CREATOR);
                            data.enforceNoDataAvail();
                            restartTaskTopActivityProcessIfVisible(_arg09);
                            reply.writeNoException();
                            break;
                        case 10:
                            WindowContainerToken _arg010 = (WindowContainerToken) data.readTypedObject(WindowContainerToken.CREATOR);
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            updateCameraCompatControlState(_arg010, _arg15);
                            reply.writeNoException();
                            break;
                        case 11:
                            boolean _arg011 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setIsIgnoreOrientationRequestDisabled(_arg011);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ITaskOrganizerController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITaskOrganizerController.DESCRIPTOR;
            }

            @Override // android.window.ITaskOrganizerController
            public ParceledListSlice<TaskAppearedInfo> registerTaskOrganizer(ITaskOrganizer organizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<TaskAppearedInfo> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public void unregisterTaskOrganizer(ITaskOrganizer organizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeStrongInterface(organizer);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public void createRootTask(int displayId, int windowingMode, IBinder launchCookie, boolean removeWithTaskOrganizer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(windowingMode);
                    _data.writeStrongBinder(launchCookie);
                    _data.writeBoolean(removeWithTaskOrganizer);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public boolean deleteRootTask(WindowContainerToken task) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(task, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public List<ActivityManager.RunningTaskInfo> getChildTasks(WindowContainerToken parent, int[] activityTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(parent, 0);
                    _data.writeIntArray(activityTypes);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.RunningTaskInfo> _result = _reply.createTypedArrayList(ActivityManager.RunningTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public List<ActivityManager.RunningTaskInfo> getRootTasks(int displayId, int[] activityTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeIntArray(activityTypes);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    List<ActivityManager.RunningTaskInfo> _result = _reply.createTypedArrayList(ActivityManager.RunningTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public WindowContainerToken getImeTarget(int display) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeInt(display);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    WindowContainerToken _result = (WindowContainerToken) _reply.readTypedObject(WindowContainerToken.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public void setInterceptBackPressedOnTaskRoot(WindowContainerToken task, boolean interceptBackPressed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(task, 0);
                    _data.writeBoolean(interceptBackPressed);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public void restartTaskTopActivityProcessIfVisible(WindowContainerToken task) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(task, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public void updateCameraCompatControlState(WindowContainerToken task, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeTypedObject(task, 0);
                    _data.writeInt(state);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ITaskOrganizerController
            public void setIsIgnoreOrientationRequestDisabled(boolean isDisabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITaskOrganizerController.DESCRIPTOR);
                    _data.writeBoolean(isDisabled);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
