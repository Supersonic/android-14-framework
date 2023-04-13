package com.android.internal.app;

import android.hardware.soundtrigger.SoundTrigger;
import android.media.permission.Identity;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.app.ISoundTriggerSession;
import java.util.List;
/* loaded from: classes4.dex */
public interface ISoundTriggerService extends IInterface {
    ISoundTriggerSession attachAsMiddleman(Identity identity, Identity identity2, SoundTrigger.ModuleProperties moduleProperties, IBinder iBinder) throws RemoteException;

    ISoundTriggerSession attachAsOriginator(Identity identity, SoundTrigger.ModuleProperties moduleProperties, IBinder iBinder) throws RemoteException;

    List<SoundTrigger.ModuleProperties> listModuleProperties(Identity identity) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ISoundTriggerService {
        @Override // com.android.internal.app.ISoundTriggerService
        public ISoundTriggerSession attachAsOriginator(Identity originatorIdentity, SoundTrigger.ModuleProperties moduleProperties, IBinder client) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.ISoundTriggerService
        public ISoundTriggerSession attachAsMiddleman(Identity middlemanIdentity, Identity originatorIdentity, SoundTrigger.ModuleProperties moduleProperties, IBinder client) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.ISoundTriggerService
        public List<SoundTrigger.ModuleProperties> listModuleProperties(Identity originatorIdentity) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISoundTriggerService {
        public static final String DESCRIPTOR = "com.android.internal.app.ISoundTriggerService";
        static final int TRANSACTION_attachAsMiddleman = 2;
        static final int TRANSACTION_attachAsOriginator = 1;
        static final int TRANSACTION_listModuleProperties = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISoundTriggerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundTriggerService)) {
                return (ISoundTriggerService) iin;
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
                    return "attachAsOriginator";
                case 2:
                    return "attachAsMiddleman";
                case 3:
                    return "listModuleProperties";
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
                            Identity _arg0 = (Identity) data.readTypedObject(Identity.CREATOR);
                            SoundTrigger.ModuleProperties _arg1 = (SoundTrigger.ModuleProperties) data.readTypedObject(SoundTrigger.ModuleProperties.CREATOR);
                            IBinder _arg2 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            ISoundTriggerSession _result = attachAsOriginator(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            Identity _arg02 = (Identity) data.readTypedObject(Identity.CREATOR);
                            Identity _arg12 = (Identity) data.readTypedObject(Identity.CREATOR);
                            SoundTrigger.ModuleProperties _arg22 = (SoundTrigger.ModuleProperties) data.readTypedObject(SoundTrigger.ModuleProperties.CREATOR);
                            IBinder _arg3 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            ISoundTriggerSession _result2 = attachAsMiddleman(_arg02, _arg12, _arg22, _arg3);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            Identity _arg03 = (Identity) data.readTypedObject(Identity.CREATOR);
                            data.enforceNoDataAvail();
                            List<SoundTrigger.ModuleProperties> _result3 = listModuleProperties(_arg03);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ISoundTriggerService {
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

            @Override // com.android.internal.app.ISoundTriggerService
            public ISoundTriggerSession attachAsOriginator(Identity originatorIdentity, SoundTrigger.ModuleProperties moduleProperties, IBinder client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(originatorIdentity, 0);
                    _data.writeTypedObject(moduleProperties, 0);
                    _data.writeStrongBinder(client);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ISoundTriggerSession _result = ISoundTriggerSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerService
            public ISoundTriggerSession attachAsMiddleman(Identity middlemanIdentity, Identity originatorIdentity, SoundTrigger.ModuleProperties moduleProperties, IBinder client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(middlemanIdentity, 0);
                    _data.writeTypedObject(originatorIdentity, 0);
                    _data.writeTypedObject(moduleProperties, 0);
                    _data.writeStrongBinder(client);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ISoundTriggerSession _result = ISoundTriggerSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerService
            public List<SoundTrigger.ModuleProperties> listModuleProperties(Identity originatorIdentity) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(originatorIdentity, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    List<SoundTrigger.ModuleProperties> _result = _reply.createTypedArrayList(SoundTrigger.ModuleProperties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
