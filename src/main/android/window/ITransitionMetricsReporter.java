package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ITransitionMetricsReporter extends IInterface {
    public static final String DESCRIPTOR = "android.window.ITransitionMetricsReporter";

    void reportAnimationStart(IBinder iBinder, long j) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITransitionMetricsReporter {
        @Override // android.window.ITransitionMetricsReporter
        public void reportAnimationStart(IBinder transitionToken, long startTime) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITransitionMetricsReporter {
        static final int TRANSACTION_reportAnimationStart = 1;

        public Stub() {
            attachInterface(this, ITransitionMetricsReporter.DESCRIPTOR);
        }

        public static ITransitionMetricsReporter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITransitionMetricsReporter.DESCRIPTOR);
            if (iin != null && (iin instanceof ITransitionMetricsReporter)) {
                return (ITransitionMetricsReporter) iin;
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
                    return "reportAnimationStart";
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
                data.enforceInterface(ITransitionMetricsReporter.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITransitionMetricsReporter.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            long _arg1 = data.readLong();
                            data.enforceNoDataAvail();
                            reportAnimationStart(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ITransitionMetricsReporter {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITransitionMetricsReporter.DESCRIPTOR;
            }

            @Override // android.window.ITransitionMetricsReporter
            public void reportAnimationStart(IBinder transitionToken, long startTime) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransitionMetricsReporter.DESCRIPTOR);
                    _data.writeStrongBinder(transitionToken);
                    _data.writeLong(startTime);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
