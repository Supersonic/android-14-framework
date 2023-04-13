package android.net;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.SubscriptionPlan;
/* loaded from: classes2.dex */
public interface INetworkPolicyListener extends IInterface {
    void onBlockedReasonChanged(int i, int i2, int i3) throws RemoteException;

    void onMeteredIfacesChanged(String[] strArr) throws RemoteException;

    void onRestrictBackgroundChanged(boolean z) throws RemoteException;

    void onSubscriptionOverride(int i, int i2, int i3, int[] iArr) throws RemoteException;

    void onSubscriptionPlansChanged(int i, SubscriptionPlan[] subscriptionPlanArr) throws RemoteException;

    void onUidPoliciesChanged(int i, int i2) throws RemoteException;

    void onUidRulesChanged(int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INetworkPolicyListener {
        @Override // android.net.INetworkPolicyListener
        public void onUidRulesChanged(int uid, int uidRules) throws RemoteException {
        }

        @Override // android.net.INetworkPolicyListener
        public void onMeteredIfacesChanged(String[] meteredIfaces) throws RemoteException {
        }

        @Override // android.net.INetworkPolicyListener
        public void onRestrictBackgroundChanged(boolean restrictBackground) throws RemoteException {
        }

        @Override // android.net.INetworkPolicyListener
        public void onUidPoliciesChanged(int uid, int uidPolicies) throws RemoteException {
        }

        @Override // android.net.INetworkPolicyListener
        public void onSubscriptionOverride(int subId, int overrideMask, int overrideValue, int[] networkTypes) throws RemoteException {
        }

        @Override // android.net.INetworkPolicyListener
        public void onSubscriptionPlansChanged(int subId, SubscriptionPlan[] plans) throws RemoteException {
        }

        @Override // android.net.INetworkPolicyListener
        public void onBlockedReasonChanged(int uid, int oldBlockedReason, int newBlockedReason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INetworkPolicyListener {
        public static final String DESCRIPTOR = "android.net.INetworkPolicyListener";
        static final int TRANSACTION_onBlockedReasonChanged = 7;
        static final int TRANSACTION_onMeteredIfacesChanged = 2;
        static final int TRANSACTION_onRestrictBackgroundChanged = 3;
        static final int TRANSACTION_onSubscriptionOverride = 5;
        static final int TRANSACTION_onSubscriptionPlansChanged = 6;
        static final int TRANSACTION_onUidPoliciesChanged = 4;
        static final int TRANSACTION_onUidRulesChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkPolicyListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkPolicyListener)) {
                return (INetworkPolicyListener) iin;
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
                    return "onUidRulesChanged";
                case 2:
                    return "onMeteredIfacesChanged";
                case 3:
                    return "onRestrictBackgroundChanged";
                case 4:
                    return "onUidPoliciesChanged";
                case 5:
                    return "onSubscriptionOverride";
                case 6:
                    return "onSubscriptionPlansChanged";
                case 7:
                    return "onBlockedReasonChanged";
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
                            onUidRulesChanged(_arg0, _arg1);
                            break;
                        case 2:
                            String[] _arg02 = data.createStringArray();
                            data.enforceNoDataAvail();
                            onMeteredIfacesChanged(_arg02);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onRestrictBackgroundChanged(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onUidPoliciesChanged(_arg04, _arg12);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            int[] _arg3 = data.createIntArray();
                            data.enforceNoDataAvail();
                            onSubscriptionOverride(_arg05, _arg13, _arg2, _arg3);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            SubscriptionPlan[] _arg14 = (SubscriptionPlan[]) data.createTypedArray(SubscriptionPlan.CREATOR);
                            data.enforceNoDataAvail();
                            onSubscriptionPlansChanged(_arg06, _arg14);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg15 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            onBlockedReasonChanged(_arg07, _arg15, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INetworkPolicyListener {
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

            @Override // android.net.INetworkPolicyListener
            public void onUidRulesChanged(int uid, int uidRules) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(uidRules);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onMeteredIfacesChanged(String[] meteredIfaces) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(meteredIfaces);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onRestrictBackgroundChanged(boolean restrictBackground) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(restrictBackground);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onUidPoliciesChanged(int uid, int uidPolicies) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(uidPolicies);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onSubscriptionOverride(int subId, int overrideMask, int overrideValue, int[] networkTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(overrideMask);
                    _data.writeInt(overrideValue);
                    _data.writeIntArray(networkTypes);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onSubscriptionPlansChanged(int subId, SubscriptionPlan[] plans) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedArray(plans, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onBlockedReasonChanged(int uid, int oldBlockedReason, int newBlockedReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(oldBlockedReason);
                    _data.writeInt(newBlockedReason);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
