package android.p008os;

import android.p008os.Parcelable;
import com.android.internal.p028os.IResultReceiver;
/* renamed from: android.os.ResultReceiver */
/* loaded from: classes3.dex */
public class ResultReceiver implements Parcelable {
    public static final Parcelable.Creator<ResultReceiver> CREATOR = new Parcelable.Creator<ResultReceiver>() { // from class: android.os.ResultReceiver.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResultReceiver createFromParcel(Parcel in) {
            return new ResultReceiver(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResultReceiver[] newArray(int size) {
            return new ResultReceiver[size];
        }
    };
    final Handler mHandler;
    final boolean mLocal;
    IResultReceiver mReceiver;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.ResultReceiver$MyRunnable */
    /* loaded from: classes3.dex */
    public class MyRunnable implements Runnable {
        final int mResultCode;
        final Bundle mResultData;

        MyRunnable(int resultCode, Bundle resultData) {
            this.mResultCode = resultCode;
            this.mResultData = resultData;
        }

        @Override // java.lang.Runnable
        public void run() {
            ResultReceiver.this.onReceiveResult(this.mResultCode, this.mResultData);
        }
    }

    /* renamed from: android.os.ResultReceiver$MyResultReceiver */
    /* loaded from: classes3.dex */
    class MyResultReceiver extends IResultReceiver.Stub {
        MyResultReceiver() {
        }

        @Override // com.android.internal.p028os.IResultReceiver
        public void send(int resultCode, Bundle resultData) {
            if (ResultReceiver.this.mHandler != null) {
                ResultReceiver.this.mHandler.post(new MyRunnable(resultCode, resultData));
            } else {
                ResultReceiver.this.onReceiveResult(resultCode, resultData);
            }
        }
    }

    public ResultReceiver(Handler handler) {
        this.mLocal = true;
        this.mHandler = handler;
    }

    public void send(int resultCode, Bundle resultData) {
        if (this.mLocal) {
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.post(new MyRunnable(resultCode, resultData));
                return;
            } else {
                onReceiveResult(resultCode, resultData);
                return;
            }
        }
        IResultReceiver iResultReceiver = this.mReceiver;
        if (iResultReceiver != null) {
            try {
                iResultReceiver.send(resultCode, resultData);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onReceiveResult(int resultCode, Bundle resultData) {
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        synchronized (this) {
            if (this.mReceiver == null) {
                this.mReceiver = new MyResultReceiver();
            }
            out.writeStrongBinder(this.mReceiver.asBinder());
        }
    }

    ResultReceiver(Parcel in) {
        this.mLocal = false;
        this.mHandler = null;
        this.mReceiver = IResultReceiver.Stub.asInterface(in.readStrongBinder());
    }
}
