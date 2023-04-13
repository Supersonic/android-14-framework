package android.service.contentcapture;

import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.contentcapture.ContentCaptureCondition;
import java.util.List;
/* loaded from: classes3.dex */
public interface IContentCaptureServiceCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.contentcapture.IContentCaptureServiceCallback";

    void disableSelf() throws RemoteException;

    void setContentCaptureConditions(String str, List<ContentCaptureCondition> list) throws RemoteException;

    void setContentCaptureWhitelist(List<String> list, List<ComponentName> list2) throws RemoteException;

    void writeSessionFlush(int i, ComponentName componentName, FlushMetrics flushMetrics, ContentCaptureOptions contentCaptureOptions, int i2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IContentCaptureServiceCallback {
        @Override // android.service.contentcapture.IContentCaptureServiceCallback
        public void setContentCaptureWhitelist(List<String> packages, List<ComponentName> activities) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureServiceCallback
        public void setContentCaptureConditions(String packageName, List<ContentCaptureCondition> conditions) throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureServiceCallback
        public void disableSelf() throws RemoteException {
        }

        @Override // android.service.contentcapture.IContentCaptureServiceCallback
        public void writeSessionFlush(int sessionId, ComponentName app, FlushMetrics flushMetrics, ContentCaptureOptions options, int flushReason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IContentCaptureServiceCallback {
        static final int TRANSACTION_disableSelf = 3;
        static final int TRANSACTION_setContentCaptureConditions = 2;
        static final int TRANSACTION_setContentCaptureWhitelist = 1;
        static final int TRANSACTION_writeSessionFlush = 4;

        public Stub() {
            attachInterface(this, IContentCaptureServiceCallback.DESCRIPTOR);
        }

        public static IContentCaptureServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IContentCaptureServiceCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IContentCaptureServiceCallback)) {
                return (IContentCaptureServiceCallback) iin;
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
                    return "setContentCaptureWhitelist";
                case 2:
                    return "setContentCaptureConditions";
                case 3:
                    return "disableSelf";
                case 4:
                    return "writeSessionFlush";
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
                data.enforceInterface(IContentCaptureServiceCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IContentCaptureServiceCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<String> _arg0 = data.createStringArrayList();
                            List<ComponentName> _arg1 = data.createTypedArrayList(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setContentCaptureWhitelist(_arg0, _arg1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            List<ContentCaptureCondition> _arg12 = data.createTypedArrayList(ContentCaptureCondition.CREATOR);
                            data.enforceNoDataAvail();
                            setContentCaptureConditions(_arg02, _arg12);
                            break;
                        case 3:
                            disableSelf();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            ComponentName _arg13 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            FlushMetrics _arg2 = (FlushMetrics) data.readTypedObject(FlushMetrics.CREATOR);
                            ContentCaptureOptions _arg3 = (ContentCaptureOptions) data.readTypedObject(ContentCaptureOptions.CREATOR);
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            writeSessionFlush(_arg03, _arg13, _arg2, _arg3, _arg4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IContentCaptureServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IContentCaptureServiceCallback.DESCRIPTOR;
            }

            @Override // android.service.contentcapture.IContentCaptureServiceCallback
            public void setContentCaptureWhitelist(List<String> packages, List<ComponentName> activities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureServiceCallback.DESCRIPTOR);
                    _data.writeStringList(packages);
                    _data.writeTypedList(activities, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureServiceCallback
            public void setContentCaptureConditions(String packageName, List<ContentCaptureCondition> conditions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureServiceCallback.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedList(conditions, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureServiceCallback
            public void disableSelf() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureServiceCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IContentCaptureServiceCallback
            public void writeSessionFlush(int sessionId, ComponentName app, FlushMetrics flushMetrics, ContentCaptureOptions options, int flushReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureServiceCallback.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    _data.writeTypedObject(app, 0);
                    _data.writeTypedObject(flushMetrics, 0);
                    _data.writeTypedObject(options, 0);
                    _data.writeInt(flushReason);
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
