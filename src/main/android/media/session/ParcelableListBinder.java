package android.media.session;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class ParcelableListBinder<T extends Parcelable> extends Binder {
    private static final int END_OF_PARCEL = 0;
    private static final int ITEM_CONTINUED = 1;
    private static final int SUGGESTED_MAX_IPC_SIZE = IBinder.getSuggestedMaxIpcSizeBytes();
    private boolean mConsumed;
    private final Consumer<List<T>> mConsumer;
    private int mCount;
    private final Object mLock = new Object();
    private final List<T> mList = new ArrayList();

    public ParcelableListBinder(Consumer<List<T>> consumer) {
        this.mConsumer = consumer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.p008os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        List<T> listToBeConsumed;
        if (code != 1) {
            return super.onTransact(code, data, reply, flags);
        }
        synchronized (this.mLock) {
            if (this.mConsumed) {
                return false;
            }
            int i = this.mList.size();
            if (i == 0) {
                this.mCount = data.readInt();
            }
            while (i < this.mCount && data.readInt() != 0) {
                this.mList.add(data.readParcelable(null));
                i++;
            }
            if (i >= this.mCount) {
                listToBeConsumed = this.mList;
                this.mConsumed = true;
            } else {
                listToBeConsumed = null;
            }
            if (listToBeConsumed != null) {
                this.mConsumer.accept(listToBeConsumed);
            }
            return true;
        }
    }

    public static <T extends Parcelable> void send(IBinder binder, List<T> list) throws RemoteException {
        int count = list.size();
        int i = 0;
        do {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            if (i == 0) {
                data.writeInt(count);
            }
            while (i < count && data.dataSize() < SUGGESTED_MAX_IPC_SIZE) {
                data.writeInt(1);
                data.writeParcelable(list.get(i), 0);
                i++;
            }
            if (i < count) {
                data.writeInt(0);
            }
            binder.transact(1, data, reply, 0);
            reply.recycle();
            data.recycle();
        } while (i < count);
    }
}
