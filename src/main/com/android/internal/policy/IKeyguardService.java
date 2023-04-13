package com.android.internal.policy;

import android.content.Intent;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardStateCallback;
/* loaded from: classes4.dex */
public interface IKeyguardService extends IInterface {
    void addStateMonitorCallback(IKeyguardStateCallback iKeyguardStateCallback) throws RemoteException;

    void dismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) throws RemoteException;

    void dismissKeyguardToLaunch(Intent intent) throws RemoteException;

    void doKeyguardTimeout(Bundle bundle) throws RemoteException;

    void onBootCompleted() throws RemoteException;

    void onDreamingStarted() throws RemoteException;

    void onDreamingStopped() throws RemoteException;

    void onFinishedGoingToSleep(int i, boolean z) throws RemoteException;

    void onFinishedWakingUp() throws RemoteException;

    void onScreenTurnedOff() throws RemoteException;

    void onScreenTurnedOn() throws RemoteException;

    void onScreenTurningOff() throws RemoteException;

    void onScreenTurningOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) throws RemoteException;

    void onShortPowerPressedGoHome() throws RemoteException;

    void onStartedGoingToSleep(int i) throws RemoteException;

    void onStartedWakingUp(int i, boolean z) throws RemoteException;

    void onSystemKeyPressed(int i) throws RemoteException;

    void onSystemReady() throws RemoteException;

    void setCurrentUser(int i) throws RemoteException;

    void setKeyguardEnabled(boolean z) throws RemoteException;

    void setOccluded(boolean z, boolean z2) throws RemoteException;

    void setSwitchingUser(boolean z) throws RemoteException;

    void startKeyguardExitAnimation(long j, long j2) throws RemoteException;

    void verifyUnlock(IKeyguardExitCallback iKeyguardExitCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IKeyguardService {
        @Override // com.android.internal.policy.IKeyguardService
        public void setOccluded(boolean isOccluded, boolean animate) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void addStateMonitorCallback(IKeyguardStateCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void verifyUnlock(IKeyguardExitCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void dismiss(IKeyguardDismissCallback callback, CharSequence message) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onDreamingStarted() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onDreamingStopped() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onStartedGoingToSleep(int pmSleepReason) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onFinishedGoingToSleep(int pmSleepReason, boolean cameraGestureTriggered) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onStartedWakingUp(int pmWakeReason, boolean cameraGestureTriggered) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onFinishedWakingUp() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onScreenTurningOn(IKeyguardDrawnCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onScreenTurnedOn() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onScreenTurningOff() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onScreenTurnedOff() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void setKeyguardEnabled(boolean enabled) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onSystemReady() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void doKeyguardTimeout(Bundle options) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void setSwitchingUser(boolean switching) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void setCurrentUser(int userId) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onBootCompleted() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void startKeyguardExitAnimation(long startTime, long fadeoutDuration) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onShortPowerPressedGoHome() throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void dismissKeyguardToLaunch(Intent intentToLaunch) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardService
        public void onSystemKeyPressed(int keycode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IKeyguardService {
        public static final String DESCRIPTOR = "com.android.internal.policy.IKeyguardService";
        static final int TRANSACTION_addStateMonitorCallback = 2;
        static final int TRANSACTION_dismiss = 4;
        static final int TRANSACTION_dismissKeyguardToLaunch = 23;
        static final int TRANSACTION_doKeyguardTimeout = 17;
        static final int TRANSACTION_onBootCompleted = 20;
        static final int TRANSACTION_onDreamingStarted = 5;
        static final int TRANSACTION_onDreamingStopped = 6;
        static final int TRANSACTION_onFinishedGoingToSleep = 8;
        static final int TRANSACTION_onFinishedWakingUp = 10;
        static final int TRANSACTION_onScreenTurnedOff = 14;
        static final int TRANSACTION_onScreenTurnedOn = 12;
        static final int TRANSACTION_onScreenTurningOff = 13;
        static final int TRANSACTION_onScreenTurningOn = 11;
        static final int TRANSACTION_onShortPowerPressedGoHome = 22;
        static final int TRANSACTION_onStartedGoingToSleep = 7;
        static final int TRANSACTION_onStartedWakingUp = 9;
        static final int TRANSACTION_onSystemKeyPressed = 24;
        static final int TRANSACTION_onSystemReady = 16;
        static final int TRANSACTION_setCurrentUser = 19;
        static final int TRANSACTION_setKeyguardEnabled = 15;
        static final int TRANSACTION_setOccluded = 1;
        static final int TRANSACTION_setSwitchingUser = 18;
        static final int TRANSACTION_startKeyguardExitAnimation = 21;
        static final int TRANSACTION_verifyUnlock = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyguardService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyguardService)) {
                return (IKeyguardService) iin;
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
                    return "setOccluded";
                case 2:
                    return "addStateMonitorCallback";
                case 3:
                    return "verifyUnlock";
                case 4:
                    return "dismiss";
                case 5:
                    return "onDreamingStarted";
                case 6:
                    return "onDreamingStopped";
                case 7:
                    return "onStartedGoingToSleep";
                case 8:
                    return "onFinishedGoingToSleep";
                case 9:
                    return "onStartedWakingUp";
                case 10:
                    return "onFinishedWakingUp";
                case 11:
                    return "onScreenTurningOn";
                case 12:
                    return "onScreenTurnedOn";
                case 13:
                    return "onScreenTurningOff";
                case 14:
                    return "onScreenTurnedOff";
                case 15:
                    return "setKeyguardEnabled";
                case 16:
                    return "onSystemReady";
                case 17:
                    return "doKeyguardTimeout";
                case 18:
                    return "setSwitchingUser";
                case 19:
                    return "setCurrentUser";
                case 20:
                    return "onBootCompleted";
                case 21:
                    return "startKeyguardExitAnimation";
                case 22:
                    return "onShortPowerPressedGoHome";
                case 23:
                    return "dismissKeyguardToLaunch";
                case 24:
                    return "onSystemKeyPressed";
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
                            boolean _arg0 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setOccluded(_arg0, _arg1);
                            break;
                        case 2:
                            IKeyguardStateCallback _arg02 = IKeyguardStateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addStateMonitorCallback(_arg02);
                            break;
                        case 3:
                            IKeyguardExitCallback _arg03 = IKeyguardExitCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            verifyUnlock(_arg03);
                            break;
                        case 4:
                            IKeyguardDismissCallback _arg04 = IKeyguardDismissCallback.Stub.asInterface(data.readStrongBinder());
                            CharSequence _arg12 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            dismiss(_arg04, _arg12);
                            break;
                        case 5:
                            onDreamingStarted();
                            break;
                        case 6:
                            onDreamingStopped();
                            break;
                        case 7:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onStartedGoingToSleep(_arg05);
                            break;
                        case 8:
                            int _arg06 = data.readInt();
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onFinishedGoingToSleep(_arg06, _arg13);
                            break;
                        case 9:
                            int _arg07 = data.readInt();
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onStartedWakingUp(_arg07, _arg14);
                            break;
                        case 10:
                            onFinishedWakingUp();
                            break;
                        case 11:
                            IKeyguardDrawnCallback _arg08 = IKeyguardDrawnCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onScreenTurningOn(_arg08);
                            break;
                        case 12:
                            onScreenTurnedOn();
                            break;
                        case 13:
                            onScreenTurningOff();
                            break;
                        case 14:
                            onScreenTurnedOff();
                            break;
                        case 15:
                            boolean _arg09 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setKeyguardEnabled(_arg09);
                            break;
                        case 16:
                            onSystemReady();
                            break;
                        case 17:
                            Bundle _arg010 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            doKeyguardTimeout(_arg010);
                            break;
                        case 18:
                            boolean _arg011 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSwitchingUser(_arg011);
                            break;
                        case 19:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            setCurrentUser(_arg012);
                            break;
                        case 20:
                            onBootCompleted();
                            break;
                        case 21:
                            long _arg013 = data.readLong();
                            long _arg15 = data.readLong();
                            data.enforceNoDataAvail();
                            startKeyguardExitAnimation(_arg013, _arg15);
                            break;
                        case 22:
                            onShortPowerPressedGoHome();
                            break;
                        case 23:
                            Intent _arg014 = (Intent) data.readTypedObject(Intent.CREATOR);
                            data.enforceNoDataAvail();
                            dismissKeyguardToLaunch(_arg014);
                            break;
                        case 24:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            onSystemKeyPressed(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IKeyguardService {
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

            @Override // com.android.internal.policy.IKeyguardService
            public void setOccluded(boolean isOccluded, boolean animate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isOccluded);
                    _data.writeBoolean(animate);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void addStateMonitorCallback(IKeyguardStateCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void verifyUnlock(IKeyguardExitCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void dismiss(IKeyguardDismissCallback callback, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onDreamingStarted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onDreamingStopped() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onStartedGoingToSleep(int pmSleepReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pmSleepReason);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onFinishedGoingToSleep(int pmSleepReason, boolean cameraGestureTriggered) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pmSleepReason);
                    _data.writeBoolean(cameraGestureTriggered);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onStartedWakingUp(int pmWakeReason, boolean cameraGestureTriggered) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pmWakeReason);
                    _data.writeBoolean(cameraGestureTriggered);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onFinishedWakingUp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onScreenTurningOn(IKeyguardDrawnCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onScreenTurnedOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onScreenTurningOff() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onScreenTurnedOff() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void setKeyguardEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onSystemReady() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void doKeyguardTimeout(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void setSwitchingUser(boolean switching) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(switching);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void setCurrentUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onBootCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void startKeyguardExitAnimation(long startTime, long fadeoutDuration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(startTime);
                    _data.writeLong(fadeoutDuration);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onShortPowerPressedGoHome() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void dismissKeyguardToLaunch(Intent intentToLaunch) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intentToLaunch, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onSystemKeyPressed(int keycode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keycode);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 23;
        }
    }
}
