package android.location;

import android.location.IGeocodeListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IGeocodeProvider extends IInterface {
    void getFromLocation(double d, double d2, int i, GeocoderParams geocoderParams, IGeocodeListener iGeocodeListener) throws RemoteException;

    void getFromLocationName(String str, double d, double d2, double d3, double d4, int i, GeocoderParams geocoderParams, IGeocodeListener iGeocodeListener) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IGeocodeProvider {
        @Override // android.location.IGeocodeProvider
        public void getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
        }

        @Override // android.location.IGeocodeProvider
        public void getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IGeocodeProvider {
        public static final String DESCRIPTOR = "android.location.IGeocodeProvider";
        static final int TRANSACTION_getFromLocation = 1;
        static final int TRANSACTION_getFromLocationName = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IGeocodeProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGeocodeProvider)) {
                return (IGeocodeProvider) iin;
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
                    return "getFromLocation";
                case 2:
                    return "getFromLocationName";
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
                            double _arg0 = data.readDouble();
                            double _arg1 = data.readDouble();
                            int _arg2 = data.readInt();
                            GeocoderParams _arg3 = (GeocoderParams) data.readTypedObject(GeocoderParams.CREATOR);
                            IGeocodeListener _arg4 = IGeocodeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getFromLocation(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            double _arg12 = data.readDouble();
                            double _arg22 = data.readDouble();
                            double _arg32 = data.readDouble();
                            double _arg42 = data.readDouble();
                            int _arg5 = data.readInt();
                            GeocoderParams _arg6 = (GeocoderParams) data.readTypedObject(GeocoderParams.CREATOR);
                            IGeocodeListener _arg7 = IGeocodeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getFromLocationName(_arg02, _arg12, _arg22, _arg32, _arg42, _arg5, _arg6, _arg7);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IGeocodeProvider {
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

            @Override // android.location.IGeocodeProvider
            public void getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeDouble(latitude);
                    _data.writeDouble(longitude);
                    _data.writeInt(maxResults);
                    _data.writeTypedObject(params, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.location.IGeocodeProvider
            public void getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(locationName);
                    _data.writeDouble(lowerLeftLatitude);
                    try {
                        _data.writeDouble(lowerLeftLongitude);
                        try {
                            _data.writeDouble(upperRightLatitude);
                            try {
                                _data.writeDouble(upperRightLongitude);
                            } catch (Throwable th) {
                                th = th;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(maxResults);
                        try {
                            _data.writeTypedObject(params, 0);
                            try {
                                _data.writeStrongInterface(listener);
                                try {
                                    this.mRemote.transact(2, _data, null, 1);
                                    _data.recycle();
                                } catch (Throwable th4) {
                                    th = th4;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th8) {
                    th = th8;
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
