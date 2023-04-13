package android.webkit;

import android.content.p001pm.PackageInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IWebViewUpdateService extends IInterface {
    String changeProviderAndSetting(String str) throws RemoteException;

    void enableMultiProcess(boolean z) throws RemoteException;

    WebViewProviderInfo[] getAllWebViewPackages() throws RemoteException;

    PackageInfo getCurrentWebViewPackage() throws RemoteException;

    String getCurrentWebViewPackageName() throws RemoteException;

    WebViewProviderInfo[] getValidWebViewPackages() throws RemoteException;

    boolean isMultiProcessEnabled() throws RemoteException;

    void notifyRelroCreationCompleted() throws RemoteException;

    WebViewProviderResponse waitForAndGetProvider() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IWebViewUpdateService {
        @Override // android.webkit.IWebViewUpdateService
        public void notifyRelroCreationCompleted() throws RemoteException {
        }

        @Override // android.webkit.IWebViewUpdateService
        public WebViewProviderResponse waitForAndGetProvider() throws RemoteException {
            return null;
        }

        @Override // android.webkit.IWebViewUpdateService
        public String changeProviderAndSetting(String newProvider) throws RemoteException {
            return null;
        }

        @Override // android.webkit.IWebViewUpdateService
        public WebViewProviderInfo[] getValidWebViewPackages() throws RemoteException {
            return null;
        }

        @Override // android.webkit.IWebViewUpdateService
        public WebViewProviderInfo[] getAllWebViewPackages() throws RemoteException {
            return null;
        }

        @Override // android.webkit.IWebViewUpdateService
        public String getCurrentWebViewPackageName() throws RemoteException {
            return null;
        }

        @Override // android.webkit.IWebViewUpdateService
        public PackageInfo getCurrentWebViewPackage() throws RemoteException {
            return null;
        }

        @Override // android.webkit.IWebViewUpdateService
        public boolean isMultiProcessEnabled() throws RemoteException {
            return false;
        }

        @Override // android.webkit.IWebViewUpdateService
        public void enableMultiProcess(boolean enable) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IWebViewUpdateService {
        public static final String DESCRIPTOR = "android.webkit.IWebViewUpdateService";
        static final int TRANSACTION_changeProviderAndSetting = 3;
        static final int TRANSACTION_enableMultiProcess = 9;
        static final int TRANSACTION_getAllWebViewPackages = 5;
        static final int TRANSACTION_getCurrentWebViewPackage = 7;
        static final int TRANSACTION_getCurrentWebViewPackageName = 6;
        static final int TRANSACTION_getValidWebViewPackages = 4;
        static final int TRANSACTION_isMultiProcessEnabled = 8;
        static final int TRANSACTION_notifyRelroCreationCompleted = 1;
        static final int TRANSACTION_waitForAndGetProvider = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWebViewUpdateService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWebViewUpdateService)) {
                return (IWebViewUpdateService) iin;
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
                    return "notifyRelroCreationCompleted";
                case 2:
                    return "waitForAndGetProvider";
                case 3:
                    return "changeProviderAndSetting";
                case 4:
                    return "getValidWebViewPackages";
                case 5:
                    return "getAllWebViewPackages";
                case 6:
                    return "getCurrentWebViewPackageName";
                case 7:
                    return "getCurrentWebViewPackage";
                case 8:
                    return "isMultiProcessEnabled";
                case 9:
                    return "enableMultiProcess";
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
                            notifyRelroCreationCompleted();
                            reply.writeNoException();
                            break;
                        case 2:
                            WebViewProviderResponse _result = waitForAndGetProvider();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 3:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            String _result2 = changeProviderAndSetting(_arg0);
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 4:
                            WebViewProviderInfo[] _result3 = getValidWebViewPackages();
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 5:
                            WebViewProviderInfo[] _result4 = getAllWebViewPackages();
                            reply.writeNoException();
                            reply.writeTypedArray(_result4, 1);
                            break;
                        case 6:
                            String _result5 = getCurrentWebViewPackageName();
                            reply.writeNoException();
                            reply.writeString(_result5);
                            break;
                        case 7:
                            PackageInfo _result6 = getCurrentWebViewPackage();
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 8:
                            boolean _result7 = isMultiProcessEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 9:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            enableMultiProcess(_arg02);
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
        public static class Proxy implements IWebViewUpdateService {
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

            @Override // android.webkit.IWebViewUpdateService
            public void notifyRelroCreationCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public WebViewProviderResponse waitForAndGetProvider() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    WebViewProviderResponse _result = (WebViewProviderResponse) _reply.readTypedObject(WebViewProviderResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public String changeProviderAndSetting(String newProvider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(newProvider);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public WebViewProviderInfo[] getValidWebViewPackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    WebViewProviderInfo[] _result = (WebViewProviderInfo[]) _reply.createTypedArray(WebViewProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public WebViewProviderInfo[] getAllWebViewPackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    WebViewProviderInfo[] _result = (WebViewProviderInfo[]) _reply.createTypedArray(WebViewProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public String getCurrentWebViewPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public PackageInfo getCurrentWebViewPackage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    PackageInfo _result = (PackageInfo) _reply.readTypedObject(PackageInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public boolean isMultiProcessEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.webkit.IWebViewUpdateService
            public void enableMultiProcess(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
