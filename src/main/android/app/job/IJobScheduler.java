package android.app.job;

import android.Manifest;
import android.app.ActivityThread;
import android.app.job.IJobScheduler;
import android.app.job.IUserVisibleJobObserver;
import android.content.AttributionSource;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.service.notification.ZenModeConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public interface IJobScheduler extends IInterface {
    boolean canRunUserInitiatedJobs(String str) throws RemoteException;

    void cancel(String str, int i) throws RemoteException;

    void cancelAll() throws RemoteException;

    void cancelAllInNamespace(String str) throws RemoteException;

    int enqueue(String str, JobInfo jobInfo, JobWorkItem jobWorkItem) throws RemoteException;

    ParceledListSlice getAllJobSnapshots() throws RemoteException;

    Map<String, ParceledListSlice<JobInfo>> getAllPendingJobs() throws RemoteException;

    ParceledListSlice<JobInfo> getAllPendingJobsInNamespace(String str) throws RemoteException;

    JobInfo getPendingJob(String str, int i) throws RemoteException;

    int getPendingJobReason(String str, int i) throws RemoteException;

    List<JobInfo> getStartedJobs() throws RemoteException;

    boolean hasRunUserInitiatedJobsPermission(String str, int i) throws RemoteException;

    void notePendingUserRequestedAppStop(String str, int i, String str2) throws RemoteException;

    void registerUserVisibleJobObserver(IUserVisibleJobObserver iUserVisibleJobObserver) throws RemoteException;

    int schedule(String str, JobInfo jobInfo) throws RemoteException;

    int scheduleAsPackage(String str, JobInfo jobInfo, String str2, int i, String str3) throws RemoteException;

    void unregisterUserVisibleJobObserver(IUserVisibleJobObserver iUserVisibleJobObserver) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IJobScheduler {
        @Override // android.app.job.IJobScheduler
        public int schedule(String namespace, JobInfo job) throws RemoteException {
            return 0;
        }

        @Override // android.app.job.IJobScheduler
        public int enqueue(String namespace, JobInfo job, JobWorkItem work) throws RemoteException {
            return 0;
        }

        @Override // android.app.job.IJobScheduler
        public int scheduleAsPackage(String namespace, JobInfo job, String packageName, int userId, String tag) throws RemoteException {
            return 0;
        }

        @Override // android.app.job.IJobScheduler
        public void cancel(String namespace, int jobId) throws RemoteException {
        }

        @Override // android.app.job.IJobScheduler
        public void cancelAll() throws RemoteException {
        }

        @Override // android.app.job.IJobScheduler
        public void cancelAllInNamespace(String namespace) throws RemoteException {
        }

        @Override // android.app.job.IJobScheduler
        public Map<String, ParceledListSlice<JobInfo>> getAllPendingJobs() throws RemoteException {
            return null;
        }

        @Override // android.app.job.IJobScheduler
        public ParceledListSlice<JobInfo> getAllPendingJobsInNamespace(String namespace) throws RemoteException {
            return null;
        }

        @Override // android.app.job.IJobScheduler
        public JobInfo getPendingJob(String namespace, int jobId) throws RemoteException {
            return null;
        }

        @Override // android.app.job.IJobScheduler
        public int getPendingJobReason(String namespace, int jobId) throws RemoteException {
            return 0;
        }

        @Override // android.app.job.IJobScheduler
        public boolean canRunUserInitiatedJobs(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.job.IJobScheduler
        public boolean hasRunUserInitiatedJobsPermission(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.job.IJobScheduler
        public List<JobInfo> getStartedJobs() throws RemoteException {
            return null;
        }

        @Override // android.app.job.IJobScheduler
        public ParceledListSlice getAllJobSnapshots() throws RemoteException {
            return null;
        }

        @Override // android.app.job.IJobScheduler
        public void registerUserVisibleJobObserver(IUserVisibleJobObserver observer) throws RemoteException {
        }

        @Override // android.app.job.IJobScheduler
        public void unregisterUserVisibleJobObserver(IUserVisibleJobObserver observer) throws RemoteException {
        }

        @Override // android.app.job.IJobScheduler
        public void notePendingUserRequestedAppStop(String packageName, int userId, String debugReason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IJobScheduler {
        public static final String DESCRIPTOR = "android.app.job.IJobScheduler";
        static final int TRANSACTION_canRunUserInitiatedJobs = 11;
        static final int TRANSACTION_cancel = 4;
        static final int TRANSACTION_cancelAll = 5;
        static final int TRANSACTION_cancelAllInNamespace = 6;
        static final int TRANSACTION_enqueue = 2;
        static final int TRANSACTION_getAllJobSnapshots = 14;
        static final int TRANSACTION_getAllPendingJobs = 7;
        static final int TRANSACTION_getAllPendingJobsInNamespace = 8;
        static final int TRANSACTION_getPendingJob = 9;
        static final int TRANSACTION_getPendingJobReason = 10;
        static final int TRANSACTION_getStartedJobs = 13;
        static final int TRANSACTION_hasRunUserInitiatedJobsPermission = 12;
        static final int TRANSACTION_notePendingUserRequestedAppStop = 17;
        static final int TRANSACTION_registerUserVisibleJobObserver = 15;
        static final int TRANSACTION_schedule = 1;
        static final int TRANSACTION_scheduleAsPackage = 3;
        static final int TRANSACTION_unregisterUserVisibleJobObserver = 16;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IJobScheduler asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IJobScheduler)) {
                return (IJobScheduler) iin;
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
                    return ZenModeConfig.SCHEDULE_PATH;
                case 2:
                    return "enqueue";
                case 3:
                    return "scheduleAsPackage";
                case 4:
                    return "cancel";
                case 5:
                    return "cancelAll";
                case 6:
                    return "cancelAllInNamespace";
                case 7:
                    return "getAllPendingJobs";
                case 8:
                    return "getAllPendingJobsInNamespace";
                case 9:
                    return "getPendingJob";
                case 10:
                    return "getPendingJobReason";
                case 11:
                    return "canRunUserInitiatedJobs";
                case 12:
                    return "hasRunUserInitiatedJobsPermission";
                case 13:
                    return "getStartedJobs";
                case 14:
                    return "getAllJobSnapshots";
                case 15:
                    return "registerUserVisibleJobObserver";
                case 16:
                    return "unregisterUserVisibleJobObserver";
                case 17:
                    return "notePendingUserRequestedAppStop";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, final Parcel reply, int flags) throws RemoteException {
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
                            String _arg0 = data.readString();
                            JobInfo _arg1 = (JobInfo) data.readTypedObject(JobInfo.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = schedule(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            JobInfo _arg12 = (JobInfo) data.readTypedObject(JobInfo.CREATOR);
                            JobWorkItem _arg2 = (JobWorkItem) data.readTypedObject(JobWorkItem.CREATOR);
                            data.enforceNoDataAvail();
                            int _result2 = enqueue(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            JobInfo _arg13 = (JobInfo) data.readTypedObject(JobInfo.CREATOR);
                            String _arg22 = data.readString();
                            int _arg3 = data.readInt();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            int _result3 = scheduleAsPackage(_arg03, _arg13, _arg22, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            cancel(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            cancelAll();
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            cancelAllInNamespace(_arg05);
                            reply.writeNoException();
                            break;
                        case 7:
                            Map<String, ParceledListSlice<JobInfo>> _result4 = getAllPendingJobs();
                            reply.writeNoException();
                            if (_result4 == null) {
                                reply.writeInt(-1);
                                break;
                            } else {
                                reply.writeInt(_result4.size());
                                _result4.forEach(new BiConsumer() { // from class: android.app.job.IJobScheduler$Stub$$ExternalSyntheticLambda0
                                    @Override // java.util.function.BiConsumer
                                    public final void accept(Object obj, Object obj2) {
                                        IJobScheduler.Stub.lambda$onTransact$0(Parcel.this, (String) obj, (ParceledListSlice) obj2);
                                    }
                                });
                                break;
                            }
                        case 8:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice<JobInfo> _result5 = getAllPendingJobsInNamespace(_arg06);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 9:
                            String _arg07 = data.readString();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            JobInfo _result6 = getPendingJob(_arg07, _arg15);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 10:
                            String _arg08 = data.readString();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result7 = getPendingJobReason(_arg08, _arg16);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 11:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result8 = canRunUserInitiatedJobs(_arg09);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 12:
                            String _arg010 = data.readString();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = hasRunUserInitiatedJobsPermission(_arg010, _arg17);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 13:
                            List<JobInfo> _result10 = getStartedJobs();
                            reply.writeNoException();
                            reply.writeTypedList(_result10, 1);
                            break;
                        case 14:
                            ParceledListSlice _result11 = getAllJobSnapshots();
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            break;
                        case 15:
                            IUserVisibleJobObserver _arg011 = IUserVisibleJobObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerUserVisibleJobObserver(_arg011);
                            reply.writeNoException();
                            break;
                        case 16:
                            IUserVisibleJobObserver _arg012 = IUserVisibleJobObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterUserVisibleJobObserver(_arg012);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg013 = data.readString();
                            int _arg18 = data.readInt();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            notePendingUserRequestedAppStop(_arg013, _arg18, _arg23);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$0(Parcel reply, String k, ParceledListSlice v) {
            reply.writeString(k);
            reply.writeTypedObject(v, 1);
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IJobScheduler {
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

            @Override // android.app.job.IJobScheduler
            public int schedule(String namespace, JobInfo job) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    _data.writeTypedObject(job, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public int enqueue(String namespace, JobInfo job, JobWorkItem work) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    _data.writeTypedObject(job, 0);
                    _data.writeTypedObject(work, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public int scheduleAsPackage(String namespace, JobInfo job, String packageName, int userId, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    _data.writeTypedObject(job, 0);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(tag);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public void cancel(String namespace, int jobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    _data.writeInt(jobId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public void cancelAll() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public void cancelAllInNamespace(String namespace) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public Map<String, ParceledListSlice<JobInfo>> getAllPendingJobs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                final Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int N = _reply.readInt();
                    final Map<String, ParceledListSlice<JobInfo>> _result = N < 0 ? null : new HashMap<>();
                    IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.app.job.IJobScheduler$Stub$Proxy$$ExternalSyntheticLambda0
                        @Override // java.util.function.IntConsumer
                        public final void accept(int i) {
                            IJobScheduler.Stub.Proxy.lambda$getAllPendingJobs$0(Parcel.this, _result, i);
                        }
                    });
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$getAllPendingJobs$0(Parcel _reply, Map _result, int i) {
                String k = _reply.readString();
                ParceledListSlice<JobInfo> v = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                _result.put(k, v);
            }

            @Override // android.app.job.IJobScheduler
            public ParceledListSlice<JobInfo> getAllPendingJobsInNamespace(String namespace) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<JobInfo> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public JobInfo getPendingJob(String namespace, int jobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    _data.writeInt(jobId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    JobInfo _result = (JobInfo) _reply.readTypedObject(JobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public int getPendingJobReason(String namespace, int jobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(namespace);
                    _data.writeInt(jobId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public boolean canRunUserInitiatedJobs(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public boolean hasRunUserInitiatedJobsPermission(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public List<JobInfo> getStartedJobs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    List<JobInfo> _result = _reply.createTypedArrayList(JobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public ParceledListSlice getAllJobSnapshots() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public void registerUserVisibleJobObserver(IUserVisibleJobObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public void unregisterUserVisibleJobObserver(IUserVisibleJobObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.job.IJobScheduler
            public void notePendingUserRequestedAppStop(String packageName, int userId, String debugReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(debugReason);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void registerUserVisibleJobObserver_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAllOf(new String[]{Manifest.C0000permission.MANAGE_ACTIVITY_TASKS, Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL}, source);
        }

        protected void unregisterUserVisibleJobObserver_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAllOf(new String[]{Manifest.C0000permission.MANAGE_ACTIVITY_TASKS, Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL}, source);
        }

        protected void notePendingUserRequestedAppStop_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAllOf(new String[]{Manifest.C0000permission.MANAGE_ACTIVITY_TASKS, Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL}, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 16;
        }
    }
}
