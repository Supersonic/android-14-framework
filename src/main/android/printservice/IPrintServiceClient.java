package android.printservice;

import android.content.p001pm.ParceledListSlice;
import android.graphics.drawable.Icon;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes3.dex */
public interface IPrintServiceClient extends IInterface {
    PrintJobInfo getPrintJobInfo(PrintJobId printJobId) throws RemoteException;

    List<PrintJobInfo> getPrintJobInfos() throws RemoteException;

    void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon) throws RemoteException;

    void onPrintersAdded(ParceledListSlice parceledListSlice) throws RemoteException;

    void onPrintersRemoved(ParceledListSlice parceledListSlice) throws RemoteException;

    boolean setPrintJobState(PrintJobId printJobId, int i, String str) throws RemoteException;

    boolean setPrintJobTag(PrintJobId printJobId, String str) throws RemoteException;

    void setProgress(PrintJobId printJobId, float f) throws RemoteException;

    void setStatus(PrintJobId printJobId, CharSequence charSequence) throws RemoteException;

    void setStatusRes(PrintJobId printJobId, int i, CharSequence charSequence) throws RemoteException;

    void writePrintJobData(ParcelFileDescriptor parcelFileDescriptor, PrintJobId printJobId) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPrintServiceClient {
        @Override // android.printservice.IPrintServiceClient
        public List<PrintJobInfo> getPrintJobInfos() throws RemoteException {
            return null;
        }

        @Override // android.printservice.IPrintServiceClient
        public PrintJobInfo getPrintJobInfo(PrintJobId printJobId) throws RemoteException {
            return null;
        }

        @Override // android.printservice.IPrintServiceClient
        public boolean setPrintJobState(PrintJobId printJobId, int state, String error) throws RemoteException {
            return false;
        }

        @Override // android.printservice.IPrintServiceClient
        public boolean setPrintJobTag(PrintJobId printJobId, String tag) throws RemoteException {
            return false;
        }

        @Override // android.printservice.IPrintServiceClient
        public void writePrintJobData(ParcelFileDescriptor fd, PrintJobId printJobId) throws RemoteException {
        }

        @Override // android.printservice.IPrintServiceClient
        public void setProgress(PrintJobId printJobId, float progress) throws RemoteException {
        }

        @Override // android.printservice.IPrintServiceClient
        public void setStatus(PrintJobId printJobId, CharSequence status) throws RemoteException {
        }

        @Override // android.printservice.IPrintServiceClient
        public void setStatusRes(PrintJobId printJobId, int status, CharSequence appPackageName) throws RemoteException {
        }

        @Override // android.printservice.IPrintServiceClient
        public void onPrintersAdded(ParceledListSlice printers) throws RemoteException {
        }

        @Override // android.printservice.IPrintServiceClient
        public void onPrintersRemoved(ParceledListSlice printerIds) throws RemoteException {
        }

        @Override // android.printservice.IPrintServiceClient
        public void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPrintServiceClient {
        public static final String DESCRIPTOR = "android.printservice.IPrintServiceClient";
        static final int TRANSACTION_getPrintJobInfo = 2;
        static final int TRANSACTION_getPrintJobInfos = 1;
        static final int TRANSACTION_onCustomPrinterIconLoaded = 11;
        static final int TRANSACTION_onPrintersAdded = 9;
        static final int TRANSACTION_onPrintersRemoved = 10;
        static final int TRANSACTION_setPrintJobState = 3;
        static final int TRANSACTION_setPrintJobTag = 4;
        static final int TRANSACTION_setProgress = 6;
        static final int TRANSACTION_setStatus = 7;
        static final int TRANSACTION_setStatusRes = 8;
        static final int TRANSACTION_writePrintJobData = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintServiceClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintServiceClient)) {
                return (IPrintServiceClient) iin;
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
                    return "getPrintJobInfos";
                case 2:
                    return "getPrintJobInfo";
                case 3:
                    return "setPrintJobState";
                case 4:
                    return "setPrintJobTag";
                case 5:
                    return "writePrintJobData";
                case 6:
                    return "setProgress";
                case 7:
                    return "setStatus";
                case 8:
                    return "setStatusRes";
                case 9:
                    return "onPrintersAdded";
                case 10:
                    return "onPrintersRemoved";
                case 11:
                    return "onCustomPrinterIconLoaded";
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
                            List<PrintJobInfo> _result = getPrintJobInfos();
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            PrintJobId _arg0 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            data.enforceNoDataAvail();
                            PrintJobInfo _result2 = getPrintJobInfo(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            PrintJobId _arg02 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = setPrintJobState(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            PrintJobId _arg03 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = setPrintJobTag(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            ParcelFileDescriptor _arg04 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            PrintJobId _arg13 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            data.enforceNoDataAvail();
                            writePrintJobData(_arg04, _arg13);
                            break;
                        case 6:
                            PrintJobId _arg05 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            float _arg14 = data.readFloat();
                            data.enforceNoDataAvail();
                            setProgress(_arg05, _arg14);
                            reply.writeNoException();
                            break;
                        case 7:
                            PrintJobId _arg06 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            CharSequence _arg15 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setStatus(_arg06, _arg15);
                            reply.writeNoException();
                            break;
                        case 8:
                            PrintJobId _arg07 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg16 = data.readInt();
                            CharSequence _arg22 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setStatusRes(_arg07, _arg16, _arg22);
                            reply.writeNoException();
                            break;
                        case 9:
                            ParceledListSlice _arg08 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            data.enforceNoDataAvail();
                            onPrintersAdded(_arg08);
                            reply.writeNoException();
                            break;
                        case 10:
                            ParceledListSlice _arg09 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            data.enforceNoDataAvail();
                            onPrintersRemoved(_arg09);
                            reply.writeNoException();
                            break;
                        case 11:
                            PrinterId _arg010 = (PrinterId) data.readTypedObject(PrinterId.CREATOR);
                            Icon _arg17 = (Icon) data.readTypedObject(Icon.CREATOR);
                            data.enforceNoDataAvail();
                            onCustomPrinterIconLoaded(_arg010, _arg17);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPrintServiceClient {
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

            @Override // android.printservice.IPrintServiceClient
            public List<PrintJobInfo> getPrintJobInfos() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<PrintJobInfo> _result = _reply.createTypedArrayList(PrintJobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public PrintJobInfo getPrintJobInfo(PrintJobId printJobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    PrintJobInfo _result = (PrintJobInfo) _reply.readTypedObject(PrintJobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public boolean setPrintJobState(PrintJobId printJobId, int state, String error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeInt(state);
                    _data.writeString(error);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public boolean setPrintJobTag(PrintJobId printJobId, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeString(tag);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void writePrintJobData(ParcelFileDescriptor fd, PrintJobId printJobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    _data.writeTypedObject(printJobId, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void setProgress(PrintJobId printJobId, float progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeFloat(progress);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void setStatus(PrintJobId printJobId, CharSequence status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    if (status != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(status, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void setStatusRes(PrintJobId printJobId, int status, CharSequence appPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeInt(status);
                    if (appPackageName != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(appPackageName, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void onPrintersAdded(ParceledListSlice printers) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printers, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void onPrintersRemoved(ParceledListSlice printerIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerIds, 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.printservice.IPrintServiceClient
            public void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerId, 0);
                    _data.writeTypedObject(icon, 0);
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
