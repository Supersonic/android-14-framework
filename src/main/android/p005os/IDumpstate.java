package android.p005os;

import android.p005os.IDumpstateListener;
import java.io.FileDescriptor;
/* renamed from: android.os.IDumpstate */
/* loaded from: classes.dex */
public interface IDumpstate extends IInterface {
    public static final int BUGREPORT_FLAG_DEFER_CONSENT = 2;
    public static final int BUGREPORT_FLAG_USE_PREDUMPED_UI_DATA = 1;
    public static final int BUGREPORT_MODE_DEFAULT = 6;
    public static final int BUGREPORT_MODE_FULL = 0;
    public static final int BUGREPORT_MODE_INTERACTIVE = 1;
    public static final int BUGREPORT_MODE_REMOTE = 2;
    public static final int BUGREPORT_MODE_TELEPHONY = 4;
    public static final int BUGREPORT_MODE_WEAR = 3;
    public static final int BUGREPORT_MODE_WIFI = 5;
    public static final String DESCRIPTOR = "android.os.IDumpstate";

    /* renamed from: android.os.IDumpstate$Default */
    /* loaded from: classes.dex */
    public static class Default implements IDumpstate {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IDumpstate
        public void cancelBugreport(int i, String str) throws RemoteException {
        }

        @Override // android.p005os.IDumpstate
        public void preDumpUiData(String str) throws RemoteException {
        }

        @Override // android.p005os.IDumpstate
        public void retrieveBugreport(int i, String str, FileDescriptor fileDescriptor, String str2, IDumpstateListener iDumpstateListener) throws RemoteException {
        }

        @Override // android.p005os.IDumpstate
        public void startBugreport(int i, String str, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, int i2, int i3, IDumpstateListener iDumpstateListener, boolean z) throws RemoteException {
        }
    }

    void cancelBugreport(int i, String str) throws RemoteException;

    void preDumpUiData(String str) throws RemoteException;

    void retrieveBugreport(int i, String str, FileDescriptor fileDescriptor, String str2, IDumpstateListener iDumpstateListener) throws RemoteException;

    void startBugreport(int i, String str, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, int i2, int i3, IDumpstateListener iDumpstateListener, boolean z) throws RemoteException;

    /* renamed from: android.os.IDumpstate$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDumpstate {
        public static final int TRANSACTION_cancelBugreport = 3;
        public static final int TRANSACTION_preDumpUiData = 1;
        public static final int TRANSACTION_retrieveBugreport = 4;
        public static final int TRANSACTION_startBugreport = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IDumpstate.DESCRIPTOR);
        }

        public static IDumpstate asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IDumpstate.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IDumpstate)) {
                return (IDumpstate) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IDumpstate.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IDumpstate.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                String readString = parcel.readString();
                parcel.enforceNoDataAvail();
                preDumpUiData(readString);
                parcel2.writeNoException();
            } else if (i == 2) {
                int readInt = parcel.readInt();
                String readString2 = parcel.readString();
                FileDescriptor readRawFileDescriptor = parcel.readRawFileDescriptor();
                FileDescriptor readRawFileDescriptor2 = parcel.readRawFileDescriptor();
                int readInt2 = parcel.readInt();
                int readInt3 = parcel.readInt();
                IDumpstateListener asInterface = IDumpstateListener.Stub.asInterface(parcel.readStrongBinder());
                boolean readBoolean = parcel.readBoolean();
                parcel.enforceNoDataAvail();
                startBugreport(readInt, readString2, readRawFileDescriptor, readRawFileDescriptor2, readInt2, readInt3, asInterface, readBoolean);
                parcel2.writeNoException();
            } else if (i == 3) {
                int readInt4 = parcel.readInt();
                String readString3 = parcel.readString();
                parcel.enforceNoDataAvail();
                cancelBugreport(readInt4, readString3);
                parcel2.writeNoException();
            } else if (i == 4) {
                int readInt5 = parcel.readInt();
                String readString4 = parcel.readString();
                FileDescriptor readRawFileDescriptor3 = parcel.readRawFileDescriptor();
                String readString5 = parcel.readString();
                IDumpstateListener asInterface2 = IDumpstateListener.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                retrieveBugreport(readInt5, readString4, readRawFileDescriptor3, readString5, asInterface2);
                parcel2.writeNoException();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        /* renamed from: android.os.IDumpstate$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IDumpstate {
            public IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IDumpstate.DESCRIPTOR;
            }

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.p005os.IDumpstate
            public void preDumpUiData(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDumpstate.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstate
            public void startBugreport(int i, String str, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, int i2, int i3, IDumpstateListener iDumpstateListener, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDumpstate.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeRawFileDescriptor(fileDescriptor);
                    obtain.writeRawFileDescriptor(fileDescriptor2);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeStrongInterface(iDumpstateListener);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstate
            public void cancelBugreport(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDumpstate.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstate
            public void retrieveBugreport(int i, String str, FileDescriptor fileDescriptor, String str2, IDumpstateListener iDumpstateListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDumpstate.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeRawFileDescriptor(fileDescriptor);
                    obtain.writeString(str2);
                    obtain.writeStrongInterface(iDumpstateListener);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
