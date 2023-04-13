package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.app.PictureInPictureUiState;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class PipStateTransactionItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<PipStateTransactionItem> CREATOR = new Parcelable.Creator<PipStateTransactionItem>() { // from class: android.app.servertransaction.PipStateTransactionItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PipStateTransactionItem createFromParcel(Parcel in) {
            return new PipStateTransactionItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PipStateTransactionItem[] newArray(int size) {
            return new PipStateTransactionItem[size];
        }
    };
    private PictureInPictureUiState mPipState;

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        client.handlePictureInPictureStateChanged(r, this.mPipState);
    }

    private PipStateTransactionItem() {
    }

    public static PipStateTransactionItem obtain(PictureInPictureUiState pipState) {
        PipStateTransactionItem instance = (PipStateTransactionItem) ObjectPool.obtain(PipStateTransactionItem.class);
        if (instance == null) {
            instance = new PipStateTransactionItem();
        }
        instance.mPipState = pipState;
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mPipState = null;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mPipState.writeToParcel(dest, flags);
    }

    private PipStateTransactionItem(Parcel in) {
        this.mPipState = PictureInPictureUiState.CREATOR.createFromParcel(in);
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public String toString() {
        return "PipStateTransactionItem{}";
    }
}
