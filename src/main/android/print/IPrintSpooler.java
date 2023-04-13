package android.print;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.print.IPrintSpoolerCallbacks;
import android.print.IPrintSpoolerClient;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes3.dex */
public interface IPrintSpooler extends IInterface {
    void clearCustomPrinterIconCache(IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i) throws RemoteException;

    void createPrintJob(PrintJobInfo printJobInfo) throws RemoteException;

    void getCustomPrinterIcon(PrinterId printerId, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i) throws RemoteException;

    void getPrintJobInfo(PrintJobId printJobId, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i, int i2) throws RemoteException;

    void getPrintJobInfos(IPrintSpoolerCallbacks iPrintSpoolerCallbacks, ComponentName componentName, int i, int i2, int i3) throws RemoteException;

    void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i) throws RemoteException;

    void pruneApprovedPrintServices(List<ComponentName> list) throws RemoteException;

    void removeObsoletePrintJobs() throws RemoteException;

    void setClient(IPrintSpoolerClient iPrintSpoolerClient) throws RemoteException;

    void setPrintJobCancelling(PrintJobId printJobId, boolean z) throws RemoteException;

    void setPrintJobState(PrintJobId printJobId, int i, String str, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i2) throws RemoteException;

    void setPrintJobTag(PrintJobId printJobId, String str, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i) throws RemoteException;

    void setProgress(PrintJobId printJobId, float f) throws RemoteException;

    void setStatus(PrintJobId printJobId, CharSequence charSequence) throws RemoteException;

    void setStatusRes(PrintJobId printJobId, int i, CharSequence charSequence) throws RemoteException;

    void writePrintJobData(ParcelFileDescriptor parcelFileDescriptor, PrintJobId printJobId) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPrintSpooler {
        @Override // android.print.IPrintSpooler
        public void removeObsoletePrintJobs() throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void getPrintJobInfos(IPrintSpoolerCallbacks callback, ComponentName componentName, int state, int appId, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void getPrintJobInfo(PrintJobId printJobId, IPrintSpoolerCallbacks callback, int appId, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void createPrintJob(PrintJobInfo printJob) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setPrintJobState(PrintJobId printJobId, int status, String stateReason, IPrintSpoolerCallbacks callback, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setProgress(PrintJobId printJobId, float progress) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setStatus(PrintJobId printJobId, CharSequence status) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setStatusRes(PrintJobId printJobId, int status, CharSequence appPackageName) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon, IPrintSpoolerCallbacks callbacks, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void getCustomPrinterIcon(PrinterId printerId, IPrintSpoolerCallbacks callbacks, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void clearCustomPrinterIconCache(IPrintSpoolerCallbacks callbacks, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setPrintJobTag(PrintJobId printJobId, String tag, IPrintSpoolerCallbacks callback, int sequence) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void writePrintJobData(ParcelFileDescriptor fd, PrintJobId printJobId) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setClient(IPrintSpoolerClient client) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void setPrintJobCancelling(PrintJobId printJobId, boolean cancelling) throws RemoteException {
        }

        @Override // android.print.IPrintSpooler
        public void pruneApprovedPrintServices(List<ComponentName> servicesToKeep) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPrintSpooler {
        public static final String DESCRIPTOR = "android.print.IPrintSpooler";
        static final int TRANSACTION_clearCustomPrinterIconCache = 11;
        static final int TRANSACTION_createPrintJob = 4;
        static final int TRANSACTION_getCustomPrinterIcon = 10;
        static final int TRANSACTION_getPrintJobInfo = 3;
        static final int TRANSACTION_getPrintJobInfos = 2;
        static final int TRANSACTION_onCustomPrinterIconLoaded = 9;
        static final int TRANSACTION_pruneApprovedPrintServices = 16;
        static final int TRANSACTION_removeObsoletePrintJobs = 1;
        static final int TRANSACTION_setClient = 14;
        static final int TRANSACTION_setPrintJobCancelling = 15;
        static final int TRANSACTION_setPrintJobState = 5;
        static final int TRANSACTION_setPrintJobTag = 12;
        static final int TRANSACTION_setProgress = 6;
        static final int TRANSACTION_setStatus = 7;
        static final int TRANSACTION_setStatusRes = 8;
        static final int TRANSACTION_writePrintJobData = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintSpooler asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintSpooler)) {
                return (IPrintSpooler) iin;
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
                    return "removeObsoletePrintJobs";
                case 2:
                    return "getPrintJobInfos";
                case 3:
                    return "getPrintJobInfo";
                case 4:
                    return "createPrintJob";
                case 5:
                    return "setPrintJobState";
                case 6:
                    return "setProgress";
                case 7:
                    return "setStatus";
                case 8:
                    return "setStatusRes";
                case 9:
                    return "onCustomPrinterIconLoaded";
                case 10:
                    return "getCustomPrinterIcon";
                case 11:
                    return "clearCustomPrinterIconCache";
                case 12:
                    return "setPrintJobTag";
                case 13:
                    return "writePrintJobData";
                case 14:
                    return "setClient";
                case 15:
                    return "setPrintJobCancelling";
                case 16:
                    return "pruneApprovedPrintServices";
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
                            removeObsoletePrintJobs();
                            break;
                        case 2:
                            IPrintSpoolerCallbacks _arg0 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            ComponentName _arg1 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            getPrintJobInfos(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 3:
                            PrintJobId _arg02 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            IPrintSpoolerCallbacks _arg12 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            getPrintJobInfo(_arg02, _arg12, _arg22, _arg32);
                            break;
                        case 4:
                            PrintJobInfo _arg03 = (PrintJobInfo) data.readTypedObject(PrintJobInfo.CREATOR);
                            data.enforceNoDataAvail();
                            createPrintJob(_arg03);
                            break;
                        case 5:
                            PrintJobId _arg04 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg13 = data.readInt();
                            String _arg23 = data.readString();
                            IPrintSpoolerCallbacks _arg33 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            setPrintJobState(_arg04, _arg13, _arg23, _arg33, _arg42);
                            break;
                        case 6:
                            PrintJobId _arg05 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            float _arg14 = data.readFloat();
                            data.enforceNoDataAvail();
                            setProgress(_arg05, _arg14);
                            break;
                        case 7:
                            PrintJobId _arg06 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            CharSequence _arg15 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setStatus(_arg06, _arg15);
                            break;
                        case 8:
                            PrintJobId _arg07 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg16 = data.readInt();
                            CharSequence _arg24 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setStatusRes(_arg07, _arg16, _arg24);
                            break;
                        case 9:
                            PrinterId _arg08 = (PrinterId) data.readTypedObject(PrinterId.CREATOR);
                            Icon _arg17 = (Icon) data.readTypedObject(Icon.CREATOR);
                            IPrintSpoolerCallbacks _arg25 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            onCustomPrinterIconLoaded(_arg08, _arg17, _arg25, _arg34);
                            break;
                        case 10:
                            PrinterId _arg09 = (PrinterId) data.readTypedObject(PrinterId.CREATOR);
                            IPrintSpoolerCallbacks _arg18 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            getCustomPrinterIcon(_arg09, _arg18, _arg26);
                            break;
                        case 11:
                            IPrintSpoolerCallbacks _arg010 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            clearCustomPrinterIconCache(_arg010, _arg19);
                            break;
                        case 12:
                            PrintJobId _arg011 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            String _arg110 = data.readString();
                            IPrintSpoolerCallbacks _arg27 = IPrintSpoolerCallbacks.Stub.asInterface(data.readStrongBinder());
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            setPrintJobTag(_arg011, _arg110, _arg27, _arg35);
                            break;
                        case 13:
                            ParcelFileDescriptor _arg012 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            PrintJobId _arg111 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            data.enforceNoDataAvail();
                            writePrintJobData(_arg012, _arg111);
                            break;
                        case 14:
                            IPrintSpoolerClient _arg013 = IPrintSpoolerClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setClient(_arg013);
                            break;
                        case 15:
                            PrintJobId _arg014 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPrintJobCancelling(_arg014, _arg112);
                            break;
                        case 16:
                            List<ComponentName> _arg015 = data.createTypedArrayList(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            pruneApprovedPrintServices(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPrintSpooler {
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

            @Override // android.print.IPrintSpooler
            public void removeObsoletePrintJobs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void getPrintJobInfos(IPrintSpoolerCallbacks callback, ComponentName componentName, int state, int appId, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(state);
                    _data.writeInt(appId);
                    _data.writeInt(sequence);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void getPrintJobInfo(PrintJobId printJobId, IPrintSpoolerCallbacks callback, int appId, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(appId);
                    _data.writeInt(sequence);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void createPrintJob(PrintJobInfo printJob) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJob, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setPrintJobState(PrintJobId printJobId, int status, String stateReason, IPrintSpoolerCallbacks callback, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeInt(status);
                    _data.writeString(stateReason);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(sequence);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setProgress(PrintJobId printJobId, float progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeFloat(progress);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setStatus(PrintJobId printJobId, CharSequence status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    if (status != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(status, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setStatusRes(PrintJobId printJobId, int status, CharSequence appPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
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
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon, IPrintSpoolerCallbacks callbacks, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerId, 0);
                    _data.writeTypedObject(icon, 0);
                    _data.writeStrongInterface(callbacks);
                    _data.writeInt(sequence);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void getCustomPrinterIcon(PrinterId printerId, IPrintSpoolerCallbacks callbacks, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerId, 0);
                    _data.writeStrongInterface(callbacks);
                    _data.writeInt(sequence);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void clearCustomPrinterIconCache(IPrintSpoolerCallbacks callbacks, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callbacks);
                    _data.writeInt(sequence);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setPrintJobTag(PrintJobId printJobId, String tag, IPrintSpoolerCallbacks callback, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeString(tag);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(sequence);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void writePrintJobData(ParcelFileDescriptor fd, PrintJobId printJobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    _data.writeTypedObject(printJobId, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setClient(IPrintSpoolerClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setPrintJobCancelling(PrintJobId printJobId, boolean cancelling) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeBoolean(cancelling);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void pruneApprovedPrintServices(List<ComponentName> servicesToKeep) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(servicesToKeep, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 15;
        }
    }
}
