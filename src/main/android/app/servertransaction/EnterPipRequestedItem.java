package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class EnterPipRequestedItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<EnterPipRequestedItem> CREATOR = new Parcelable.Creator<EnterPipRequestedItem>() { // from class: android.app.servertransaction.EnterPipRequestedItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EnterPipRequestedItem createFromParcel(Parcel in) {
            return new EnterPipRequestedItem();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EnterPipRequestedItem[] newArray(int size) {
            return new EnterPipRequestedItem[size];
        }
    };

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        client.handlePictureInPictureRequested(r);
    }

    private EnterPipRequestedItem() {
    }

    public static EnterPipRequestedItem obtain() {
        EnterPipRequestedItem instance = (EnterPipRequestedItem) ObjectPool.obtain(EnterPipRequestedItem.class);
        if (instance == null) {
            return new EnterPipRequestedItem();
        }
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public String toString() {
        return "EnterPipRequestedItem{}";
    }
}
