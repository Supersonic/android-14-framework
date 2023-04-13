package android.database;

import android.database.IContentObserver;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public abstract class BulkCursorNative extends Binder implements IBulkCursor {
    public BulkCursorNative() {
        attachInterface(this, IBulkCursor.descriptor);
    }

    public static IBulkCursor asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IBulkCursor in = (IBulkCursor) obj.queryLocalInterface(IBulkCursor.descriptor);
        if (in != null) {
            return in;
        }
        return new BulkCursorProxy(obj);
    }

    @Override // android.p008os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            switch (code) {
                case 1:
                    data.enforceInterface(IBulkCursor.descriptor);
                    int startPos = data.readInt();
                    CursorWindow window = getWindow(startPos);
                    reply.writeNoException();
                    if (window == null) {
                        reply.writeInt(0);
                    } else {
                        reply.writeInt(1);
                        window.writeToParcel(reply, 1);
                    }
                    return true;
                case 2:
                    data.enforceInterface(IBulkCursor.descriptor);
                    deactivate();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(IBulkCursor.descriptor);
                    IContentObserver observer = IContentObserver.Stub.asInterface(data.readStrongBinder());
                    int count = requery(observer);
                    reply.writeNoException();
                    reply.writeInt(count);
                    reply.writeBundle(getExtras());
                    return true;
                case 4:
                    data.enforceInterface(IBulkCursor.descriptor);
                    int position = data.readInt();
                    onMove(position);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(IBulkCursor.descriptor);
                    Bundle extras = getExtras();
                    reply.writeNoException();
                    reply.writeBundle(extras);
                    return true;
                case 6:
                    data.enforceInterface(IBulkCursor.descriptor);
                    Bundle extras2 = data.readBundle();
                    Bundle returnExtras = respond(extras2);
                    reply.writeNoException();
                    reply.writeBundle(returnExtras);
                    return true;
                case 7:
                    data.enforceInterface(IBulkCursor.descriptor);
                    close();
                    reply.writeNoException();
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        } catch (Exception e) {
            DatabaseUtils.writeExceptionToParcel(reply, e);
            return true;
        }
    }

    @Override // android.p008os.IInterface
    public IBinder asBinder() {
        return this;
    }
}
