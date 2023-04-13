package android.print;

import android.media.TtmlUtils;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.print.ILayoutResultCallback;
import android.print.IPrintDocumentAdapterObserver;
import android.print.IWriteResultCallback;
/* loaded from: classes3.dex */
public interface IPrintDocumentAdapter extends IInterface {
    void finish() throws RemoteException;

    void kill(String str) throws RemoteException;

    void layout(PrintAttributes printAttributes, PrintAttributes printAttributes2, ILayoutResultCallback iLayoutResultCallback, Bundle bundle, int i) throws RemoteException;

    void setObserver(IPrintDocumentAdapterObserver iPrintDocumentAdapterObserver) throws RemoteException;

    void start() throws RemoteException;

    void write(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, IWriteResultCallback iWriteResultCallback, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPrintDocumentAdapter {
        @Override // android.print.IPrintDocumentAdapter
        public void setObserver(IPrintDocumentAdapterObserver observer) throws RemoteException {
        }

        @Override // android.print.IPrintDocumentAdapter
        public void start() throws RemoteException {
        }

        @Override // android.print.IPrintDocumentAdapter
        public void layout(PrintAttributes oldAttributes, PrintAttributes newAttributes, ILayoutResultCallback callback, Bundle metadata, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintDocumentAdapter
        public void write(PageRange[] pages, ParcelFileDescriptor fd, IWriteResultCallback callback, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintDocumentAdapter
        public void finish() throws RemoteException {
        }

        @Override // android.print.IPrintDocumentAdapter
        public void kill(String reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPrintDocumentAdapter {
        public static final String DESCRIPTOR = "android.print.IPrintDocumentAdapter";
        static final int TRANSACTION_finish = 5;
        static final int TRANSACTION_kill = 6;
        static final int TRANSACTION_layout = 3;
        static final int TRANSACTION_setObserver = 1;
        static final int TRANSACTION_start = 2;
        static final int TRANSACTION_write = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintDocumentAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintDocumentAdapter)) {
                return (IPrintDocumentAdapter) iin;
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
                    return "setObserver";
                case 2:
                    return "start";
                case 3:
                    return TtmlUtils.TAG_LAYOUT;
                case 4:
                    return "write";
                case 5:
                    return "finish";
                case 6:
                    return "kill";
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
                            IPrintDocumentAdapterObserver _arg0 = IPrintDocumentAdapterObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setObserver(_arg0);
                            break;
                        case 2:
                            start();
                            break;
                        case 3:
                            PrintAttributes _arg02 = (PrintAttributes) data.readTypedObject(PrintAttributes.CREATOR);
                            PrintAttributes _arg1 = (PrintAttributes) data.readTypedObject(PrintAttributes.CREATOR);
                            ILayoutResultCallback _arg2 = ILayoutResultCallback.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            layout(_arg02, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 4:
                            PageRange[] _arg03 = (PageRange[]) data.createTypedArray(PageRange.CREATOR);
                            ParcelFileDescriptor _arg12 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            IWriteResultCallback _arg22 = IWriteResultCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            write(_arg03, _arg12, _arg22, _arg32);
                            break;
                        case 5:
                            finish();
                            break;
                        case 6:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            kill(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPrintDocumentAdapter {
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

            @Override // android.print.IPrintDocumentAdapter
            public void setObserver(IPrintDocumentAdapterObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintDocumentAdapter
            public void start() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintDocumentAdapter
            public void layout(PrintAttributes oldAttributes, PrintAttributes newAttributes, ILayoutResultCallback callback, Bundle metadata, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(oldAttributes, 0);
                    _data.writeTypedObject(newAttributes, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(metadata, 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintDocumentAdapter
            public void write(PageRange[] pages, ParcelFileDescriptor fd, IWriteResultCallback callback, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(pages, 0);
                    _data.writeTypedObject(fd, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(sequence);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintDocumentAdapter
            public void finish() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintDocumentAdapter
            public void kill(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
