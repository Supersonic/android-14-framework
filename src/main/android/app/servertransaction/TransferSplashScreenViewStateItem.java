package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.SurfaceControl;
import android.window.SplashScreenView;
/* loaded from: classes.dex */
public class TransferSplashScreenViewStateItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<TransferSplashScreenViewStateItem> CREATOR = new Parcelable.Creator<TransferSplashScreenViewStateItem>() { // from class: android.app.servertransaction.TransferSplashScreenViewStateItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TransferSplashScreenViewStateItem createFromParcel(Parcel in) {
            return new TransferSplashScreenViewStateItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TransferSplashScreenViewStateItem[] newArray(int size) {
            return new TransferSplashScreenViewStateItem[size];
        }
    };
    private SplashScreenView.SplashScreenViewParcelable mSplashScreenViewParcelable;
    private SurfaceControl mStartingWindowLeash;

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        client.handleAttachSplashScreenView(r, this.mSplashScreenViewParcelable, this.mStartingWindowLeash);
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mSplashScreenViewParcelable, flags);
        dest.writeTypedObject(this.mStartingWindowLeash, flags);
    }

    private TransferSplashScreenViewStateItem() {
    }

    private TransferSplashScreenViewStateItem(Parcel in) {
        this.mSplashScreenViewParcelable = (SplashScreenView.SplashScreenViewParcelable) in.readTypedObject(SplashScreenView.SplashScreenViewParcelable.CREATOR);
        this.mStartingWindowLeash = (SurfaceControl) in.readTypedObject(SurfaceControl.CREATOR);
    }

    public static TransferSplashScreenViewStateItem obtain(SplashScreenView.SplashScreenViewParcelable parcelable, SurfaceControl startingWindowLeash) {
        TransferSplashScreenViewStateItem instance = (TransferSplashScreenViewStateItem) ObjectPool.obtain(TransferSplashScreenViewStateItem.class);
        if (instance == null) {
            instance = new TransferSplashScreenViewStateItem();
        }
        instance.mSplashScreenViewParcelable = parcelable;
        instance.mStartingWindowLeash = startingWindowLeash;
        return instance;
    }
}
