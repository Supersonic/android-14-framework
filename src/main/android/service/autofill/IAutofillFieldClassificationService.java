package android.service.autofill;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.view.autofill.AutofillValue;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public interface IAutofillFieldClassificationService extends IInterface {
    void calculateScores(RemoteCallback remoteCallback, List<AutofillValue> list, String[] strArr, String[] strArr2, String str, Bundle bundle, Map map, Map map2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAutofillFieldClassificationService {
        @Override // android.service.autofill.IAutofillFieldClassificationService
        public void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAutofillFieldClassificationService {
        public static final String DESCRIPTOR = "android.service.autofill.IAutofillFieldClassificationService";
        static final int TRANSACTION_calculateScores = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAutofillFieldClassificationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAutofillFieldClassificationService)) {
                return (IAutofillFieldClassificationService) iin;
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
                    return "calculateScores";
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
                            RemoteCallback _arg0 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            List<AutofillValue> _arg1 = data.createTypedArrayList(AutofillValue.CREATOR);
                            String[] _arg2 = data.createStringArray();
                            String[] _arg3 = data.createStringArray();
                            String _arg4 = data.readString();
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            ClassLoader cl = getClass().getClassLoader();
                            Map _arg6 = data.readHashMap(cl);
                            Map _arg7 = data.readHashMap(cl);
                            data.enforceNoDataAvail();
                            calculateScores(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAutofillFieldClassificationService {
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

            @Override // android.service.autofill.IAutofillFieldClassificationService
            public void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    _data.writeTypedList(actualValues, 0);
                    _data.writeStringArray(userDataValues);
                    _data.writeStringArray(categoryIds);
                    _data.writeString(defaultAlgorithm);
                    _data.writeTypedObject(defaultArgs, 0);
                    _data.writeMap(algorithms);
                    _data.writeMap(args);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
