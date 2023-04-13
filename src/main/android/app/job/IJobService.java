package android.app.job;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IJobService extends IInterface {
    void getTransferredDownloadBytes(JobParameters jobParameters, JobWorkItem jobWorkItem) throws RemoteException;

    void getTransferredUploadBytes(JobParameters jobParameters, JobWorkItem jobWorkItem) throws RemoteException;

    void onNetworkChanged(JobParameters jobParameters) throws RemoteException;

    void startJob(JobParameters jobParameters) throws RemoteException;

    void stopJob(JobParameters jobParameters) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IJobService {
        @Override // android.app.job.IJobService
        public void startJob(JobParameters jobParams) throws RemoteException {
        }

        @Override // android.app.job.IJobService
        public void stopJob(JobParameters jobParams) throws RemoteException {
        }

        @Override // android.app.job.IJobService
        public void onNetworkChanged(JobParameters jobParams) throws RemoteException {
        }

        @Override // android.app.job.IJobService
        public void getTransferredDownloadBytes(JobParameters jobParams, JobWorkItem jobWorkItem) throws RemoteException {
        }

        @Override // android.app.job.IJobService
        public void getTransferredUploadBytes(JobParameters jobParams, JobWorkItem jobWorkItem) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IJobService {
        public static final String DESCRIPTOR = "android.app.job.IJobService";
        static final int TRANSACTION_getTransferredDownloadBytes = 4;
        static final int TRANSACTION_getTransferredUploadBytes = 5;
        static final int TRANSACTION_onNetworkChanged = 3;
        static final int TRANSACTION_startJob = 1;
        static final int TRANSACTION_stopJob = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IJobService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IJobService)) {
                return (IJobService) iin;
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
                    return "startJob";
                case 2:
                    return "stopJob";
                case 3:
                    return "onNetworkChanged";
                case 4:
                    return "getTransferredDownloadBytes";
                case 5:
                    return "getTransferredUploadBytes";
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
                            JobParameters _arg0 = (JobParameters) data.readTypedObject(JobParameters.CREATOR);
                            data.enforceNoDataAvail();
                            startJob(_arg0);
                            break;
                        case 2:
                            JobParameters _arg02 = (JobParameters) data.readTypedObject(JobParameters.CREATOR);
                            data.enforceNoDataAvail();
                            stopJob(_arg02);
                            break;
                        case 3:
                            JobParameters _arg03 = (JobParameters) data.readTypedObject(JobParameters.CREATOR);
                            data.enforceNoDataAvail();
                            onNetworkChanged(_arg03);
                            break;
                        case 4:
                            JobParameters _arg04 = (JobParameters) data.readTypedObject(JobParameters.CREATOR);
                            JobWorkItem _arg1 = (JobWorkItem) data.readTypedObject(JobWorkItem.CREATOR);
                            data.enforceNoDataAvail();
                            getTransferredDownloadBytes(_arg04, _arg1);
                            break;
                        case 5:
                            JobParameters _arg05 = (JobParameters) data.readTypedObject(JobParameters.CREATOR);
                            JobWorkItem _arg12 = (JobWorkItem) data.readTypedObject(JobWorkItem.CREATOR);
                            data.enforceNoDataAvail();
                            getTransferredUploadBytes(_arg05, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IJobService {
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

            @Override // android.app.job.IJobService
            public void startJob(JobParameters jobParams) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(jobParams, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobService
            public void stopJob(JobParameters jobParams) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(jobParams, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobService
            public void onNetworkChanged(JobParameters jobParams) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(jobParams, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobService
            public void getTransferredDownloadBytes(JobParameters jobParams, JobWorkItem jobWorkItem) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(jobParams, 0);
                    _data.writeTypedObject(jobWorkItem, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobService
            public void getTransferredUploadBytes(JobParameters jobParams, JobWorkItem jobWorkItem) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(jobParams, 0);
                    _data.writeTypedObject(jobWorkItem, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
