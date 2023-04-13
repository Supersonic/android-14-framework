package android.service.contentsuggestions;

import android.app.contentsuggestions.ClassificationsRequest;
import android.app.contentsuggestions.IClassificationsCallback;
import android.app.contentsuggestions.ISelectionsCallback;
import android.app.contentsuggestions.SelectionsRequest;
import android.hardware.HardwareBuffer;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IContentSuggestionsService extends IInterface {
    public static final String DESCRIPTOR = "android.service.contentsuggestions.IContentSuggestionsService";

    void classifyContentSelections(ClassificationsRequest classificationsRequest, IClassificationsCallback iClassificationsCallback) throws RemoteException;

    void notifyInteraction(String str, Bundle bundle) throws RemoteException;

    void provideContextImage(int i, HardwareBuffer hardwareBuffer, int i2, Bundle bundle) throws RemoteException;

    void suggestContentSelections(SelectionsRequest selectionsRequest, ISelectionsCallback iSelectionsCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IContentSuggestionsService {
        @Override // android.service.contentsuggestions.IContentSuggestionsService
        public void provideContextImage(int taskId, HardwareBuffer contextImage, int colorSpaceId, Bundle imageContextRequestExtras) throws RemoteException {
        }

        @Override // android.service.contentsuggestions.IContentSuggestionsService
        public void suggestContentSelections(SelectionsRequest request, ISelectionsCallback callback) throws RemoteException {
        }

        @Override // android.service.contentsuggestions.IContentSuggestionsService
        public void classifyContentSelections(ClassificationsRequest request, IClassificationsCallback callback) throws RemoteException {
        }

        @Override // android.service.contentsuggestions.IContentSuggestionsService
        public void notifyInteraction(String requestId, Bundle interaction) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IContentSuggestionsService {
        static final int TRANSACTION_classifyContentSelections = 3;
        static final int TRANSACTION_notifyInteraction = 4;
        static final int TRANSACTION_provideContextImage = 1;
        static final int TRANSACTION_suggestContentSelections = 2;

        public Stub() {
            attachInterface(this, IContentSuggestionsService.DESCRIPTOR);
        }

        public static IContentSuggestionsService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IContentSuggestionsService.DESCRIPTOR);
            if (iin != null && (iin instanceof IContentSuggestionsService)) {
                return (IContentSuggestionsService) iin;
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
                    return "provideContextImage";
                case 2:
                    return "suggestContentSelections";
                case 3:
                    return "classifyContentSelections";
                case 4:
                    return "notifyInteraction";
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
                data.enforceInterface(IContentSuggestionsService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IContentSuggestionsService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            HardwareBuffer _arg1 = (HardwareBuffer) data.readTypedObject(HardwareBuffer.CREATOR);
                            int _arg2 = data.readInt();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            provideContextImage(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            SelectionsRequest _arg02 = (SelectionsRequest) data.readTypedObject(SelectionsRequest.CREATOR);
                            ISelectionsCallback _arg12 = ISelectionsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            suggestContentSelections(_arg02, _arg12);
                            break;
                        case 3:
                            ClassificationsRequest _arg03 = (ClassificationsRequest) data.readTypedObject(ClassificationsRequest.CREATOR);
                            IClassificationsCallback _arg13 = IClassificationsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            classifyContentSelections(_arg03, _arg13);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            Bundle _arg14 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyInteraction(_arg04, _arg14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IContentSuggestionsService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IContentSuggestionsService.DESCRIPTOR;
            }

            @Override // android.service.contentsuggestions.IContentSuggestionsService
            public void provideContextImage(int taskId, HardwareBuffer contextImage, int colorSpaceId, Bundle imageContextRequestExtras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsService.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(contextImage, 0);
                    _data.writeInt(colorSpaceId);
                    _data.writeTypedObject(imageContextRequestExtras, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentsuggestions.IContentSuggestionsService
            public void suggestContentSelections(SelectionsRequest request, ISelectionsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentsuggestions.IContentSuggestionsService
            public void classifyContentSelections(ClassificationsRequest request, IClassificationsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentsuggestions.IContentSuggestionsService
            public void notifyInteraction(String requestId, Bundle interaction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsService.DESCRIPTOR);
                    _data.writeString(requestId);
                    _data.writeTypedObject(interaction, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
