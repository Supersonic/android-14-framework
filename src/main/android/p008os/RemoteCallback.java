package android.p008os;

import android.annotation.SystemApi;
import android.p008os.IRemoteCallback;
import android.p008os.Parcelable;
@SystemApi
/* renamed from: android.os.RemoteCallback */
/* loaded from: classes3.dex */
public final class RemoteCallback implements Parcelable {
    public static final Parcelable.Creator<RemoteCallback> CREATOR = new Parcelable.Creator<RemoteCallback>() { // from class: android.os.RemoteCallback.3
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteCallback createFromParcel(Parcel parcel) {
            return new RemoteCallback(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteCallback[] newArray(int size) {
            return new RemoteCallback[size];
        }
    };
    private final IRemoteCallback mCallback;
    private final Handler mHandler;
    private final OnResultListener mListener;

    /* renamed from: android.os.RemoteCallback$OnResultListener */
    /* loaded from: classes3.dex */
    public interface OnResultListener {
        void onResult(Bundle bundle);
    }

    public RemoteCallback(OnResultListener listener) {
        this(listener, null);
    }

    public RemoteCallback(OnResultListener listener, Handler handler) {
        if (listener == null) {
            throw new NullPointerException("listener cannot be null");
        }
        this.mListener = listener;
        this.mHandler = handler;
        this.mCallback = new IRemoteCallback.Stub() { // from class: android.os.RemoteCallback.1
            @Override // android.p008os.IRemoteCallback
            public void sendResult(Bundle data) {
                RemoteCallback.this.sendResult(data);
            }
        };
    }

    RemoteCallback(Parcel parcel) {
        this.mListener = null;
        this.mHandler = null;
        this.mCallback = IRemoteCallback.Stub.asInterface(parcel.readStrongBinder());
    }

    public void sendResult(final Bundle result) {
        OnResultListener onResultListener = this.mListener;
        if (onResultListener != null) {
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: android.os.RemoteCallback.2
                    @Override // java.lang.Runnable
                    public void run() {
                        RemoteCallback.this.mListener.onResult(result);
                    }
                });
                return;
            } else {
                onResultListener.onResult(result);
                return;
            }
        }
        try {
            this.mCallback.sendResult(result);
        } catch (RemoteException e) {
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStrongBinder(this.mCallback.asBinder());
    }
}
