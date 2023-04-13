package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.telecom.CallEndpoint;
import android.telecom.CallException;
import android.telecom.DisconnectCause;
import com.android.internal.telecom.ICallControl;
import java.util.List;
/* loaded from: classes2.dex */
public interface ICallEventCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.ICallEventCallback";

    void onAddCallControl(String str, int i, ICallControl iCallControl, CallException callException) throws RemoteException;

    void onAnswer(String str, int i, ResultReceiver resultReceiver) throws RemoteException;

    void onAvailableCallEndpointsChanged(String str, List<CallEndpoint> list) throws RemoteException;

    void onCallEndpointChanged(String str, CallEndpoint callEndpoint) throws RemoteException;

    void onCallStreamingFailed(String str, int i) throws RemoteException;

    void onCallStreamingStarted(String str, ResultReceiver resultReceiver) throws RemoteException;

    void onDisconnect(String str, DisconnectCause disconnectCause, ResultReceiver resultReceiver) throws RemoteException;

    void onEvent(String str, String str2, Bundle bundle) throws RemoteException;

    void onMuteStateChanged(String str, boolean z) throws RemoteException;

    void onSetActive(String str, ResultReceiver resultReceiver) throws RemoteException;

    void onSetInactive(String str, ResultReceiver resultReceiver) throws RemoteException;

    void removeCallFromTransactionalServiceWrapper(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICallEventCallback {
        @Override // com.android.internal.telecom.ICallEventCallback
        public void onAddCallControl(String callId, int resultCode, ICallControl callControl, CallException exception) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onSetActive(String callId, ResultReceiver callback) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onSetInactive(String callId, ResultReceiver callback) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onAnswer(String callId, int videoState, ResultReceiver callback) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onDisconnect(String callId, DisconnectCause cause, ResultReceiver callback) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onCallStreamingStarted(String callId, ResultReceiver callback) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onCallStreamingFailed(String callId, int reason) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onCallEndpointChanged(String callId, CallEndpoint endpoint) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onAvailableCallEndpointsChanged(String callId, List<CallEndpoint> endpoint) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onMuteStateChanged(String callId, boolean isMuted) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onEvent(String callId, String event, Bundle extras) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void removeCallFromTransactionalServiceWrapper(String callId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallEventCallback {
        static final int TRANSACTION_onAddCallControl = 1;
        static final int TRANSACTION_onAnswer = 4;
        static final int TRANSACTION_onAvailableCallEndpointsChanged = 9;
        static final int TRANSACTION_onCallEndpointChanged = 8;
        static final int TRANSACTION_onCallStreamingFailed = 7;
        static final int TRANSACTION_onCallStreamingStarted = 6;
        static final int TRANSACTION_onDisconnect = 5;
        static final int TRANSACTION_onEvent = 11;
        static final int TRANSACTION_onMuteStateChanged = 10;
        static final int TRANSACTION_onSetActive = 2;
        static final int TRANSACTION_onSetInactive = 3;
        static final int TRANSACTION_removeCallFromTransactionalServiceWrapper = 12;

        public Stub() {
            attachInterface(this, ICallEventCallback.DESCRIPTOR);
        }

        public static ICallEventCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICallEventCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ICallEventCallback)) {
                return (ICallEventCallback) iin;
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
                    return "onAddCallControl";
                case 2:
                    return "onSetActive";
                case 3:
                    return "onSetInactive";
                case 4:
                    return "onAnswer";
                case 5:
                    return "onDisconnect";
                case 6:
                    return "onCallStreamingStarted";
                case 7:
                    return "onCallStreamingFailed";
                case 8:
                    return "onCallEndpointChanged";
                case 9:
                    return "onAvailableCallEndpointsChanged";
                case 10:
                    return "onMuteStateChanged";
                case 11:
                    return "onEvent";
                case 12:
                    return "removeCallFromTransactionalServiceWrapper";
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
                data.enforceInterface(ICallEventCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICallEventCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            ICallControl _arg2 = ICallControl.Stub.asInterface(data.readStrongBinder());
                            CallException _arg3 = (CallException) data.readTypedObject(CallException.CREATOR);
                            data.enforceNoDataAvail();
                            onAddCallControl(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            ResultReceiver _arg12 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onSetActive(_arg02, _arg12);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            ResultReceiver _arg13 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onSetInactive(_arg03, _arg13);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            ResultReceiver _arg22 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onAnswer(_arg04, _arg14, _arg22);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            DisconnectCause _arg15 = (DisconnectCause) data.readTypedObject(DisconnectCause.CREATOR);
                            ResultReceiver _arg23 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onDisconnect(_arg05, _arg15, _arg23);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            ResultReceiver _arg16 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onCallStreamingStarted(_arg06, _arg16);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            onCallStreamingFailed(_arg07, _arg17);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            CallEndpoint _arg18 = (CallEndpoint) data.readTypedObject(CallEndpoint.CREATOR);
                            data.enforceNoDataAvail();
                            onCallEndpointChanged(_arg08, _arg18);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            List<CallEndpoint> _arg19 = data.createTypedArrayList(CallEndpoint.CREATOR);
                            data.enforceNoDataAvail();
                            onAvailableCallEndpointsChanged(_arg09, _arg19);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            boolean _arg110 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onMuteStateChanged(_arg010, _arg110);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            String _arg111 = data.readString();
                            Bundle _arg24 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onEvent(_arg011, _arg111, _arg24);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            data.enforceNoDataAvail();
                            removeCallFromTransactionalServiceWrapper(_arg012);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ICallEventCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICallEventCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onAddCallControl(String callId, int resultCode, ICallControl callControl, CallException exception) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(resultCode);
                    _data.writeStrongInterface(callControl);
                    _data.writeTypedObject(exception, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onSetActive(String callId, ResultReceiver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onSetInactive(String callId, ResultReceiver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onAnswer(String callId, int videoState, ResultReceiver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(videoState);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onDisconnect(String callId, DisconnectCause cause, ResultReceiver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(cause, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onCallStreamingStarted(String callId, ResultReceiver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onCallStreamingFailed(String callId, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(reason);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onCallEndpointChanged(String callId, CallEndpoint endpoint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(endpoint, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onAvailableCallEndpointsChanged(String callId, List<CallEndpoint> endpoint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedList(endpoint, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onMuteStateChanged(String callId, boolean isMuted) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(isMuted);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void onEvent(String callId, String event, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(event);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallEventCallback
            public void removeCallFromTransactionalServiceWrapper(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallEventCallback.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
