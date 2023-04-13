package android.print;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.print.IPrintDocumentAdapter;
import android.print.IPrintJobStateChangeListener;
import android.print.IPrintServicesChangeListener;
import android.print.IPrinterDiscoveryObserver;
import android.printservice.PrintServiceInfo;
import android.printservice.recommendation.IRecommendationsChangeListener;
import android.printservice.recommendation.RecommendationInfo;
import java.util.List;
/* loaded from: classes3.dex */
public interface IPrintManager extends IInterface {
    void addPrintJobStateChangeListener(IPrintJobStateChangeListener iPrintJobStateChangeListener, int i, int i2) throws RemoteException;

    void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener iRecommendationsChangeListener, int i) throws RemoteException;

    void addPrintServicesChangeListener(IPrintServicesChangeListener iPrintServicesChangeListener, int i) throws RemoteException;

    void cancelPrintJob(PrintJobId printJobId, int i, int i2) throws RemoteException;

    void createPrinterDiscoverySession(IPrinterDiscoveryObserver iPrinterDiscoveryObserver, int i) throws RemoteException;

    void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver iPrinterDiscoveryObserver, int i) throws RemoteException;

    boolean getBindInstantServiceAllowed(int i) throws RemoteException;

    Icon getCustomPrinterIcon(PrinterId printerId, int i) throws RemoteException;

    PrintJobInfo getPrintJobInfo(PrintJobId printJobId, int i, int i2) throws RemoteException;

    List<PrintJobInfo> getPrintJobInfos(int i, int i2) throws RemoteException;

    List<RecommendationInfo> getPrintServiceRecommendations(int i) throws RemoteException;

    List<PrintServiceInfo> getPrintServices(int i, int i2) throws RemoteException;

    boolean isPrintServiceEnabled(ComponentName componentName, int i) throws RemoteException;

    Bundle print(String str, IPrintDocumentAdapter iPrintDocumentAdapter, PrintAttributes printAttributes, String str2, int i, int i2) throws RemoteException;

    void removePrintJobStateChangeListener(IPrintJobStateChangeListener iPrintJobStateChangeListener, int i) throws RemoteException;

    void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener iRecommendationsChangeListener, int i) throws RemoteException;

    void removePrintServicesChangeListener(IPrintServicesChangeListener iPrintServicesChangeListener, int i) throws RemoteException;

    void restartPrintJob(PrintJobId printJobId, int i, int i2) throws RemoteException;

    void setBindInstantServiceAllowed(int i, boolean z) throws RemoteException;

    void setPrintServiceEnabled(ComponentName componentName, boolean z, int i) throws RemoteException;

    void startPrinterDiscovery(IPrinterDiscoveryObserver iPrinterDiscoveryObserver, List<PrinterId> list, int i) throws RemoteException;

    void startPrinterStateTracking(PrinterId printerId, int i) throws RemoteException;

    void stopPrinterDiscovery(IPrinterDiscoveryObserver iPrinterDiscoveryObserver, int i) throws RemoteException;

    void stopPrinterStateTracking(PrinterId printerId, int i) throws RemoteException;

    void validatePrinters(List<PrinterId> list, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPrintManager {
        @Override // android.print.IPrintManager
        public List<PrintJobInfo> getPrintJobInfos(int appId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.print.IPrintManager
        public PrintJobInfo getPrintJobInfo(PrintJobId printJobId, int appId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.print.IPrintManager
        public Bundle print(String printJobName, IPrintDocumentAdapter printAdapter, PrintAttributes attributes, String packageName, int appId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.print.IPrintManager
        public void cancelPrintJob(PrintJobId printJobId, int appId, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void restartPrintJob(PrintJobId printJobId, int appId, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void addPrintJobStateChangeListener(IPrintJobStateChangeListener listener, int appId, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void removePrintJobStateChangeListener(IPrintJobStateChangeListener listener, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void addPrintServicesChangeListener(IPrintServicesChangeListener listener, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void removePrintServicesChangeListener(IPrintServicesChangeListener listener, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public List<PrintServiceInfo> getPrintServices(int selectionFlags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.print.IPrintManager
        public void setPrintServiceEnabled(ComponentName service, boolean isEnabled, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public boolean isPrintServiceEnabled(ComponentName service, int userId) throws RemoteException {
            return false;
        }

        @Override // android.print.IPrintManager
        public void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener listener, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener listener, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public List<RecommendationInfo> getPrintServiceRecommendations(int userId) throws RemoteException {
            return null;
        }

        @Override // android.print.IPrintManager
        public void createPrinterDiscoverySession(IPrinterDiscoveryObserver observer, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void startPrinterDiscovery(IPrinterDiscoveryObserver observer, List<PrinterId> priorityList, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void stopPrinterDiscovery(IPrinterDiscoveryObserver observer, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void validatePrinters(List<PrinterId> printerIds, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void startPrinterStateTracking(PrinterId printerId, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public Icon getCustomPrinterIcon(PrinterId printerId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.print.IPrintManager
        public void stopPrinterStateTracking(PrinterId printerId, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver observer, int userId) throws RemoteException {
        }

        @Override // android.print.IPrintManager
        public boolean getBindInstantServiceAllowed(int userId) throws RemoteException {
            return false;
        }

        @Override // android.print.IPrintManager
        public void setBindInstantServiceAllowed(int userId, boolean allowed) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPrintManager {
        public static final String DESCRIPTOR = "android.print.IPrintManager";
        static final int TRANSACTION_addPrintJobStateChangeListener = 6;
        static final int TRANSACTION_addPrintServiceRecommendationsChangeListener = 13;
        static final int TRANSACTION_addPrintServicesChangeListener = 8;
        static final int TRANSACTION_cancelPrintJob = 4;
        static final int TRANSACTION_createPrinterDiscoverySession = 16;
        static final int TRANSACTION_destroyPrinterDiscoverySession = 23;
        static final int TRANSACTION_getBindInstantServiceAllowed = 24;
        static final int TRANSACTION_getCustomPrinterIcon = 21;
        static final int TRANSACTION_getPrintJobInfo = 2;
        static final int TRANSACTION_getPrintJobInfos = 1;
        static final int TRANSACTION_getPrintServiceRecommendations = 15;
        static final int TRANSACTION_getPrintServices = 10;
        static final int TRANSACTION_isPrintServiceEnabled = 12;
        static final int TRANSACTION_print = 3;
        static final int TRANSACTION_removePrintJobStateChangeListener = 7;
        static final int TRANSACTION_removePrintServiceRecommendationsChangeListener = 14;
        static final int TRANSACTION_removePrintServicesChangeListener = 9;
        static final int TRANSACTION_restartPrintJob = 5;
        static final int TRANSACTION_setBindInstantServiceAllowed = 25;
        static final int TRANSACTION_setPrintServiceEnabled = 11;
        static final int TRANSACTION_startPrinterDiscovery = 17;
        static final int TRANSACTION_startPrinterStateTracking = 20;
        static final int TRANSACTION_stopPrinterDiscovery = 18;
        static final int TRANSACTION_stopPrinterStateTracking = 22;
        static final int TRANSACTION_validatePrinters = 19;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintManager)) {
                return (IPrintManager) iin;
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
                    return Context.PRINT_SERVICE;
                case 4:
                    return "cancelPrintJob";
                case 5:
                    return "restartPrintJob";
                case 6:
                    return "addPrintJobStateChangeListener";
                case 7:
                    return "removePrintJobStateChangeListener";
                case 8:
                    return "addPrintServicesChangeListener";
                case 9:
                    return "removePrintServicesChangeListener";
                case 10:
                    return "getPrintServices";
                case 11:
                    return "setPrintServiceEnabled";
                case 12:
                    return "isPrintServiceEnabled";
                case 13:
                    return "addPrintServiceRecommendationsChangeListener";
                case 14:
                    return "removePrintServiceRecommendationsChangeListener";
                case 15:
                    return "getPrintServiceRecommendations";
                case 16:
                    return "createPrinterDiscoverySession";
                case 17:
                    return "startPrinterDiscovery";
                case 18:
                    return "stopPrinterDiscovery";
                case 19:
                    return "validatePrinters";
                case 20:
                    return "startPrinterStateTracking";
                case 21:
                    return "getCustomPrinterIcon";
                case 22:
                    return "stopPrinterStateTracking";
                case 23:
                    return "destroyPrinterDiscoverySession";
                case 24:
                    return "getBindInstantServiceAllowed";
                case 25:
                    return "setBindInstantServiceAllowed";
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
                            data.enforceNoDataAvail();
                            List<PrintJobInfo> _result = getPrintJobInfos(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            PrintJobId _arg02 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            PrintJobInfo _result2 = getPrintJobInfo(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            IPrintDocumentAdapter _arg13 = IPrintDocumentAdapter.Stub.asInterface(data.readStrongBinder());
                            PrintAttributes _arg22 = (PrintAttributes) data.readTypedObject(PrintAttributes.CREATOR);
                            String _arg3 = data.readString();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result3 = print(_arg03, _arg13, _arg22, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            PrintJobId _arg04 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg14 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelPrintJob(_arg04, _arg14, _arg23);
                            reply.writeNoException();
                            break;
                        case 5:
                            PrintJobId _arg05 = (PrintJobId) data.readTypedObject(PrintJobId.CREATOR);
                            int _arg15 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            restartPrintJob(_arg05, _arg15, _arg24);
                            reply.writeNoException();
                            break;
                        case 6:
                            IPrintJobStateChangeListener _arg06 = IPrintJobStateChangeListener.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            addPrintJobStateChangeListener(_arg06, _arg16, _arg25);
                            reply.writeNoException();
                            break;
                        case 7:
                            IPrintJobStateChangeListener _arg07 = IPrintJobStateChangeListener.Stub.asInterface(data.readStrongBinder());
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            removePrintJobStateChangeListener(_arg07, _arg17);
                            reply.writeNoException();
                            break;
                        case 8:
                            IPrintServicesChangeListener _arg08 = IPrintServicesChangeListener.Stub.asInterface(data.readStrongBinder());
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            addPrintServicesChangeListener(_arg08, _arg18);
                            reply.writeNoException();
                            break;
                        case 9:
                            IPrintServicesChangeListener _arg09 = IPrintServicesChangeListener.Stub.asInterface(data.readStrongBinder());
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            removePrintServicesChangeListener(_arg09, _arg19);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            List<PrintServiceInfo> _result4 = getPrintServices(_arg010, _arg110);
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        case 11:
                            ComponentName _arg011 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg111 = data.readBoolean();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setPrintServiceEnabled(_arg011, _arg111, _arg26);
                            reply.writeNoException();
                            break;
                        case 12:
                            ComponentName _arg012 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = isPrintServiceEnabled(_arg012, _arg112);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 13:
                            IRecommendationsChangeListener _arg013 = IRecommendationsChangeListener.Stub.asInterface(data.readStrongBinder());
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            addPrintServiceRecommendationsChangeListener(_arg013, _arg113);
                            reply.writeNoException();
                            break;
                        case 14:
                            IRecommendationsChangeListener _arg014 = IRecommendationsChangeListener.Stub.asInterface(data.readStrongBinder());
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            removePrintServiceRecommendationsChangeListener(_arg014, _arg114);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            List<RecommendationInfo> _result6 = getPrintServiceRecommendations(_arg015);
                            reply.writeNoException();
                            reply.writeTypedList(_result6, 1);
                            break;
                        case 16:
                            IPrinterDiscoveryObserver _arg016 = IPrinterDiscoveryObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            createPrinterDiscoverySession(_arg016, _arg115);
                            reply.writeNoException();
                            break;
                        case 17:
                            IPrinterDiscoveryObserver _arg017 = IPrinterDiscoveryObserver.Stub.asInterface(data.readStrongBinder());
                            List<PrinterId> _arg116 = data.createTypedArrayList(PrinterId.CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            startPrinterDiscovery(_arg017, _arg116, _arg27);
                            reply.writeNoException();
                            break;
                        case 18:
                            IPrinterDiscoveryObserver _arg018 = IPrinterDiscoveryObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            stopPrinterDiscovery(_arg018, _arg117);
                            reply.writeNoException();
                            break;
                        case 19:
                            List<PrinterId> _arg019 = data.createTypedArrayList(PrinterId.CREATOR);
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            validatePrinters(_arg019, _arg118);
                            reply.writeNoException();
                            break;
                        case 20:
                            PrinterId _arg020 = (PrinterId) data.readTypedObject(PrinterId.CREATOR);
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            startPrinterStateTracking(_arg020, _arg119);
                            reply.writeNoException();
                            break;
                        case 21:
                            PrinterId _arg021 = (PrinterId) data.readTypedObject(PrinterId.CREATOR);
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            Icon _result7 = getCustomPrinterIcon(_arg021, _arg120);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 22:
                            PrinterId _arg022 = (PrinterId) data.readTypedObject(PrinterId.CREATOR);
                            int _arg121 = data.readInt();
                            data.enforceNoDataAvail();
                            stopPrinterStateTracking(_arg022, _arg121);
                            reply.writeNoException();
                            break;
                        case 23:
                            IPrinterDiscoveryObserver _arg023 = IPrinterDiscoveryObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            destroyPrinterDiscoverySession(_arg023, _arg122);
                            reply.writeNoException();
                            break;
                        case 24:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = getBindInstantServiceAllowed(_arg024);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 25:
                            int _arg025 = data.readInt();
                            boolean _arg123 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBindInstantServiceAllowed(_arg025, _arg123);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPrintManager {
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

            @Override // android.print.IPrintManager
            public List<PrintJobInfo> getPrintJobInfos(int appId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<PrintJobInfo> _result = _reply.createTypedArrayList(PrintJobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public PrintJobInfo getPrintJobInfo(PrintJobId printJobId, int appId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    PrintJobInfo _result = (PrintJobInfo) _reply.readTypedObject(PrintJobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public Bundle print(String printJobName, IPrintDocumentAdapter printAdapter, PrintAttributes attributes, String packageName, int appId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(printJobName);
                    _data.writeStrongInterface(printAdapter);
                    _data.writeTypedObject(attributes, 0);
                    _data.writeString(packageName);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void cancelPrintJob(PrintJobId printJobId, int appId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void restartPrintJob(PrintJobId printJobId, int appId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printJobId, 0);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void addPrintJobStateChangeListener(IPrintJobStateChangeListener listener, int appId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(appId);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void removePrintJobStateChangeListener(IPrintJobStateChangeListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void addPrintServicesChangeListener(IPrintServicesChangeListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void removePrintServicesChangeListener(IPrintServicesChangeListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public List<PrintServiceInfo> getPrintServices(int selectionFlags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(selectionFlags);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    List<PrintServiceInfo> _result = _reply.createTypedArrayList(PrintServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void setPrintServiceEnabled(ComponentName service, boolean isEnabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(service, 0);
                    _data.writeBoolean(isEnabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public boolean isPrintServiceEnabled(ComponentName service, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(service, 0);
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

            @Override // android.print.IPrintManager
            public void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public List<RecommendationInfo> getPrintServiceRecommendations(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    List<RecommendationInfo> _result = _reply.createTypedArrayList(RecommendationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void createPrinterDiscoverySession(IPrinterDiscoveryObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void startPrinterDiscovery(IPrinterDiscoveryObserver observer, List<PrinterId> priorityList, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    _data.writeTypedList(priorityList, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void stopPrinterDiscovery(IPrinterDiscoveryObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void validatePrinters(List<PrinterId> printerIds, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(printerIds, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void startPrinterStateTracking(PrinterId printerId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerId, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public Icon getCustomPrinterIcon(PrinterId printerId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerId, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    Icon _result = (Icon) _reply.readTypedObject(Icon.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void stopPrinterStateTracking(PrinterId printerId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(printerId, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public boolean getBindInstantServiceAllowed(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.print.IPrintManager
            public void setBindInstantServiceAllowed(int userId, boolean allowed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(allowed);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 24;
        }
    }
}
