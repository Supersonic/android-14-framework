package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.BluetoothCallQualityReport;
import android.telecom.CallAudioState;
import android.telecom.DisconnectCause;
import android.telecom.ParcelableCall;
import android.telephony.CallQuality;
import com.android.internal.telecom.ICallDiagnosticServiceAdapter;
/* loaded from: classes2.dex */
public interface ICallDiagnosticService extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.ICallDiagnosticService";

    void callQualityChanged(String str, CallQuality callQuality) throws RemoteException;

    void initializeDiagnosticCall(ParcelableCall parcelableCall) throws RemoteException;

    void notifyCallDisconnected(String str, DisconnectCause disconnectCause) throws RemoteException;

    void receiveBluetoothCallQualityReport(BluetoothCallQualityReport bluetoothCallQualityReport) throws RemoteException;

    void receiveDeviceToDeviceMessage(String str, int i, int i2) throws RemoteException;

    void removeDiagnosticCall(String str) throws RemoteException;

    void setAdapter(ICallDiagnosticServiceAdapter iCallDiagnosticServiceAdapter) throws RemoteException;

    void updateCall(ParcelableCall parcelableCall) throws RemoteException;

    void updateCallAudioState(CallAudioState callAudioState) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICallDiagnosticService {
        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void setAdapter(ICallDiagnosticServiceAdapter adapter) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void initializeDiagnosticCall(ParcelableCall call) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void updateCall(ParcelableCall call) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void updateCallAudioState(CallAudioState callAudioState) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void removeDiagnosticCall(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void receiveDeviceToDeviceMessage(String callId, int message, int value) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void callQualityChanged(String callId, CallQuality callQuality) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void receiveBluetoothCallQualityReport(BluetoothCallQualityReport qualityReport) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticService
        public void notifyCallDisconnected(String callId, DisconnectCause disconnectCause) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallDiagnosticService {
        static final int TRANSACTION_callQualityChanged = 7;
        static final int TRANSACTION_initializeDiagnosticCall = 2;
        static final int TRANSACTION_notifyCallDisconnected = 9;
        static final int TRANSACTION_receiveBluetoothCallQualityReport = 8;
        static final int TRANSACTION_receiveDeviceToDeviceMessage = 6;
        static final int TRANSACTION_removeDiagnosticCall = 5;
        static final int TRANSACTION_setAdapter = 1;
        static final int TRANSACTION_updateCall = 3;
        static final int TRANSACTION_updateCallAudioState = 4;

        public Stub() {
            attachInterface(this, ICallDiagnosticService.DESCRIPTOR);
        }

        public static ICallDiagnosticService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICallDiagnosticService.DESCRIPTOR);
            if (iin != null && (iin instanceof ICallDiagnosticService)) {
                return (ICallDiagnosticService) iin;
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
                    return "setAdapter";
                case 2:
                    return "initializeDiagnosticCall";
                case 3:
                    return "updateCall";
                case 4:
                    return "updateCallAudioState";
                case 5:
                    return "removeDiagnosticCall";
                case 6:
                    return "receiveDeviceToDeviceMessage";
                case 7:
                    return "callQualityChanged";
                case 8:
                    return "receiveBluetoothCallQualityReport";
                case 9:
                    return "notifyCallDisconnected";
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
                data.enforceInterface(ICallDiagnosticService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICallDiagnosticService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ICallDiagnosticServiceAdapter _arg0 = ICallDiagnosticServiceAdapter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setAdapter(_arg0);
                            break;
                        case 2:
                            ParcelableCall _arg02 = (ParcelableCall) data.readTypedObject(ParcelableCall.CREATOR);
                            data.enforceNoDataAvail();
                            initializeDiagnosticCall(_arg02);
                            break;
                        case 3:
                            ParcelableCall _arg03 = (ParcelableCall) data.readTypedObject(ParcelableCall.CREATOR);
                            data.enforceNoDataAvail();
                            updateCall(_arg03);
                            break;
                        case 4:
                            CallAudioState _arg04 = (CallAudioState) data.readTypedObject(CallAudioState.CREATOR);
                            data.enforceNoDataAvail();
                            updateCallAudioState(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            removeDiagnosticCall(_arg05);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            receiveDeviceToDeviceMessage(_arg06, _arg1, _arg2);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            CallQuality _arg12 = (CallQuality) data.readTypedObject(CallQuality.CREATOR);
                            data.enforceNoDataAvail();
                            callQualityChanged(_arg07, _arg12);
                            break;
                        case 8:
                            BluetoothCallQualityReport _arg08 = (BluetoothCallQualityReport) data.readTypedObject(BluetoothCallQualityReport.CREATOR);
                            data.enforceNoDataAvail();
                            receiveBluetoothCallQualityReport(_arg08);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            DisconnectCause _arg13 = (DisconnectCause) data.readTypedObject(DisconnectCause.CREATOR);
                            data.enforceNoDataAvail();
                            notifyCallDisconnected(_arg09, _arg13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ICallDiagnosticService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICallDiagnosticService.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void setAdapter(ICallDiagnosticServiceAdapter adapter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeStrongInterface(adapter);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void initializeDiagnosticCall(ParcelableCall call) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeTypedObject(call, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void updateCall(ParcelableCall call) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeTypedObject(call, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void updateCallAudioState(CallAudioState callAudioState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeTypedObject(callAudioState, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void removeDiagnosticCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void receiveDeviceToDeviceMessage(String callId, int message, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(message);
                    _data.writeInt(value);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void callQualityChanged(String callId, CallQuality callQuality) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(callQuality, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void receiveBluetoothCallQualityReport(BluetoothCallQualityReport qualityReport) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeTypedObject(qualityReport, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticService
            public void notifyCallDisconnected(String callId, DisconnectCause disconnectCause) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticService.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(disconnectCause, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
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
