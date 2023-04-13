package android.app.job;

import android.app.Notification;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IJobCallback extends IInterface {
    void acknowledgeGetTransferredDownloadBytesMessage(int i, int i2, long j) throws RemoteException;

    void acknowledgeGetTransferredUploadBytesMessage(int i, int i2, long j) throws RemoteException;

    void acknowledgeStartMessage(int i, boolean z) throws RemoteException;

    void acknowledgeStopMessage(int i, boolean z) throws RemoteException;

    boolean completeWork(int i, int i2) throws RemoteException;

    JobWorkItem dequeueWork(int i) throws RemoteException;

    void jobFinished(int i, boolean z) throws RemoteException;

    void setNotification(int i, int i2, Notification notification, int i3) throws RemoteException;

    void updateEstimatedNetworkBytes(int i, JobWorkItem jobWorkItem, long j, long j2) throws RemoteException;

    void updateTransferredNetworkBytes(int i, JobWorkItem jobWorkItem, long j, long j2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IJobCallback {
        @Override // android.app.job.IJobCallback
        public void acknowledgeGetTransferredDownloadBytesMessage(int jobId, int workId, long transferredBytes) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public void acknowledgeGetTransferredUploadBytesMessage(int jobId, int workId, long transferredBytes) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public void acknowledgeStartMessage(int jobId, boolean ongoing) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public void acknowledgeStopMessage(int jobId, boolean reschedule) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public JobWorkItem dequeueWork(int jobId) throws RemoteException {
            return null;
        }

        @Override // android.app.job.IJobCallback
        public boolean completeWork(int jobId, int workId) throws RemoteException {
            return false;
        }

        @Override // android.app.job.IJobCallback
        public void jobFinished(int jobId, boolean reschedule) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public void updateEstimatedNetworkBytes(int jobId, JobWorkItem item, long downloadBytes, long uploadBytes) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public void updateTransferredNetworkBytes(int jobId, JobWorkItem item, long transferredDownloadBytes, long transferredUploadBytes) throws RemoteException {
        }

        @Override // android.app.job.IJobCallback
        public void setNotification(int jobId, int notificationId, Notification notification, int jobEndNotificationPolicy) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IJobCallback {
        public static final String DESCRIPTOR = "android.app.job.IJobCallback";
        static final int TRANSACTION_acknowledgeGetTransferredDownloadBytesMessage = 1;
        static final int TRANSACTION_acknowledgeGetTransferredUploadBytesMessage = 2;
        static final int TRANSACTION_acknowledgeStartMessage = 3;
        static final int TRANSACTION_acknowledgeStopMessage = 4;
        static final int TRANSACTION_completeWork = 6;
        static final int TRANSACTION_dequeueWork = 5;
        static final int TRANSACTION_jobFinished = 7;
        static final int TRANSACTION_setNotification = 10;
        static final int TRANSACTION_updateEstimatedNetworkBytes = 8;
        static final int TRANSACTION_updateTransferredNetworkBytes = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IJobCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IJobCallback)) {
                return (IJobCallback) iin;
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
                    return "acknowledgeGetTransferredDownloadBytesMessage";
                case 2:
                    return "acknowledgeGetTransferredUploadBytesMessage";
                case 3:
                    return "acknowledgeStartMessage";
                case 4:
                    return "acknowledgeStopMessage";
                case 5:
                    return "dequeueWork";
                case 6:
                    return "completeWork";
                case 7:
                    return "jobFinished";
                case 8:
                    return "updateEstimatedNetworkBytes";
                case 9:
                    return "updateTransferredNetworkBytes";
                case 10:
                    return "setNotification";
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
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            long _arg2 = data.readLong();
                            data.enforceNoDataAvail();
                            acknowledgeGetTransferredDownloadBytesMessage(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            long _arg22 = data.readLong();
                            data.enforceNoDataAvail();
                            acknowledgeGetTransferredUploadBytesMessage(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            acknowledgeStartMessage(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            acknowledgeStopMessage(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            JobWorkItem _result = dequeueWork(_arg05);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = completeWork(_arg06, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            jobFinished(_arg07, _arg16);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            JobWorkItem _arg17 = (JobWorkItem) data.readTypedObject(JobWorkItem.CREATOR);
                            long _arg23 = data.readLong();
                            long _arg3 = data.readLong();
                            data.enforceNoDataAvail();
                            updateEstimatedNetworkBytes(_arg08, _arg17, _arg23, _arg3);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            JobWorkItem _arg18 = (JobWorkItem) data.readTypedObject(JobWorkItem.CREATOR);
                            long _arg24 = data.readLong();
                            long _arg32 = data.readLong();
                            data.enforceNoDataAvail();
                            updateTransferredNetworkBytes(_arg09, _arg18, _arg24, _arg32);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg19 = data.readInt();
                            Notification _arg25 = (Notification) data.readTypedObject(Notification.CREATOR);
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            setNotification(_arg010, _arg19, _arg25, _arg33);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IJobCallback {
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

            @Override // android.app.job.IJobCallback
            public void acknowledgeGetTransferredDownloadBytesMessage(int jobId, int workId, long transferredBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(workId);
                    _data.writeLong(transferredBytes);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void acknowledgeGetTransferredUploadBytesMessage(int jobId, int workId, long transferredBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(workId);
                    _data.writeLong(transferredBytes);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void acknowledgeStartMessage(int jobId, boolean ongoing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeBoolean(ongoing);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void acknowledgeStopMessage(int jobId, boolean reschedule) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeBoolean(reschedule);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public JobWorkItem dequeueWork(int jobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    JobWorkItem _result = (JobWorkItem) _reply.readTypedObject(JobWorkItem.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public boolean completeWork(int jobId, int workId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(workId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void jobFinished(int jobId, boolean reschedule) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeBoolean(reschedule);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void updateEstimatedNetworkBytes(int jobId, JobWorkItem item, long downloadBytes, long uploadBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeTypedObject(item, 0);
                    _data.writeLong(downloadBytes);
                    _data.writeLong(uploadBytes);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void updateTransferredNetworkBytes(int jobId, JobWorkItem item, long transferredDownloadBytes, long transferredUploadBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeTypedObject(item, 0);
                    _data.writeLong(transferredDownloadBytes);
                    _data.writeLong(transferredUploadBytes);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobCallback
            public void setNotification(int jobId, int notificationId, Notification notification, int jobEndNotificationPolicy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(jobId);
                    _data.writeInt(notificationId);
                    _data.writeTypedObject(notification, 0);
                    _data.writeInt(jobEndNotificationPolicy);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
