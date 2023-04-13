package android.window;

import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.util.Log;
import android.view.ViewRootImpl;
import android.window.IOnBackInvokedCallback;
import android.window.WindowOnBackInvokedDispatcher;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes4.dex */
public class ImeOnBackInvokedDispatcher implements OnBackInvokedDispatcher, Parcelable {
    public static final Parcelable.Creator<ImeOnBackInvokedDispatcher> CREATOR = new Parcelable.Creator<ImeOnBackInvokedDispatcher>() { // from class: android.window.ImeOnBackInvokedDispatcher.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImeOnBackInvokedDispatcher createFromParcel(Parcel in) {
            return new ImeOnBackInvokedDispatcher(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImeOnBackInvokedDispatcher[] newArray(int size) {
            return new ImeOnBackInvokedDispatcher[size];
        }
    };
    static final int RESULT_CODE_REGISTER = 0;
    static final int RESULT_CODE_UNREGISTER = 1;
    static final String RESULT_KEY_CALLBACK = "callback";
    static final String RESULT_KEY_ID = "id";
    static final String RESULT_KEY_PRIORITY = "priority";
    private static final String TAG = "ImeBackDispatcher";
    private final ArrayList<ImeOnBackInvokedCallback> mImeCallbacks = new ArrayList<>();
    private final ResultReceiver mResultReceiver;

    public ImeOnBackInvokedDispatcher(Handler handler) {
        this.mResultReceiver = new ResultReceiver(handler) { // from class: android.window.ImeOnBackInvokedDispatcher.1
            @Override // android.p008os.ResultReceiver
            public void onReceiveResult(int resultCode, Bundle resultData) {
                WindowOnBackInvokedDispatcher dispatcher = ImeOnBackInvokedDispatcher.this.getReceivingDispatcher();
                if (dispatcher != null) {
                    ImeOnBackInvokedDispatcher.this.receive(resultCode, resultData, dispatcher);
                }
            }
        };
    }

    protected WindowOnBackInvokedDispatcher getReceivingDispatcher() {
        return null;
    }

    ImeOnBackInvokedDispatcher(Parcel in) {
        this.mResultReceiver = (ResultReceiver) in.readTypedObject(ResultReceiver.CREATOR);
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void registerOnBackInvokedCallback(int priority, OnBackInvokedCallback callback) {
        Bundle bundle = new Bundle();
        IOnBackInvokedCallback iCallback = new WindowOnBackInvokedDispatcher.OnBackInvokedCallbackWrapper(callback, false);
        bundle.putBinder(RESULT_KEY_CALLBACK, iCallback.asBinder());
        bundle.putInt("priority", priority);
        bundle.putInt("id", callback.hashCode());
        this.mResultReceiver.send(0, bundle);
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void unregisterOnBackInvokedCallback(OnBackInvokedCallback callback) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", callback.hashCode());
        this.mResultReceiver.send(1, bundle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mResultReceiver, flags);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void receive(int resultCode, Bundle resultData, WindowOnBackInvokedDispatcher receivingDispatcher) {
        int callbackId = resultData.getInt("id");
        if (resultCode == 0) {
            int priority = resultData.getInt("priority");
            IOnBackInvokedCallback callback = IOnBackInvokedCallback.Stub.asInterface(resultData.getBinder(RESULT_KEY_CALLBACK));
            registerReceivedCallback(callback, priority, callbackId, receivingDispatcher);
        } else if (resultCode == 1) {
            unregisterReceivedCallback(callbackId, receivingDispatcher);
        }
    }

    private void registerReceivedCallback(IOnBackInvokedCallback iCallback, int priority, int callbackId, WindowOnBackInvokedDispatcher receivingDispatcher) {
        ImeOnBackInvokedCallback imeCallback = new ImeOnBackInvokedCallback(iCallback, callbackId, priority);
        this.mImeCallbacks.add(imeCallback);
        receivingDispatcher.registerOnBackInvokedCallbackUnchecked(imeCallback, priority);
    }

    private void unregisterReceivedCallback(int callbackId, OnBackInvokedDispatcher receivingDispatcher) {
        ImeOnBackInvokedCallback callback = null;
        Iterator<ImeOnBackInvokedCallback> it = this.mImeCallbacks.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ImeOnBackInvokedCallback imeCallback = it.next();
            if (imeCallback.getId() == callbackId) {
                callback = imeCallback;
                break;
            }
        }
        if (callback == null) {
            Log.m110e(TAG, "Ime callback not found. Ignoring unregisterReceivedCallback. callbackId: " + callbackId);
            return;
        }
        receivingDispatcher.unregisterOnBackInvokedCallback(callback);
        this.mImeCallbacks.remove(callback);
    }

    public void clear() {
        if (getReceivingDispatcher() != null) {
            Iterator<ImeOnBackInvokedCallback> it = this.mImeCallbacks.iterator();
            while (it.hasNext()) {
                ImeOnBackInvokedCallback callback = it.next();
                getReceivingDispatcher().unregisterOnBackInvokedCallback(callback);
            }
        }
        this.mImeCallbacks.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ImeOnBackInvokedCallback implements OnBackInvokedCallback {
        private final IOnBackInvokedCallback mIOnBackInvokedCallback;
        private final int mId;
        private final int mPriority;

        ImeOnBackInvokedCallback(IOnBackInvokedCallback iCallback, int id, int priority) {
            this.mIOnBackInvokedCallback = iCallback;
            this.mId = id;
            this.mPriority = priority;
        }

        @Override // android.window.OnBackInvokedCallback
        public void onBackInvoked() {
            try {
                IOnBackInvokedCallback iOnBackInvokedCallback = this.mIOnBackInvokedCallback;
                if (iOnBackInvokedCallback != null) {
                    iOnBackInvokedCallback.onBackInvoked();
                }
            } catch (RemoteException e) {
                Log.m109e(ImeOnBackInvokedDispatcher.TAG, "Exception when invoking forwarded callback. e: ", e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getId() {
            return this.mId;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public IOnBackInvokedCallback getIOnBackInvokedCallback() {
            return this.mIOnBackInvokedCallback;
        }

        public String toString() {
            return "ImeCallback=ImeOnBackInvokedCallback@" + this.mId + " Callback=" + this.mIOnBackInvokedCallback;
        }
    }

    public void switchRootView(ViewRootImpl previous, ViewRootImpl current) {
        Iterator<ImeOnBackInvokedCallback> it = this.mImeCallbacks.iterator();
        while (it.hasNext()) {
            ImeOnBackInvokedCallback imeCallback = it.next();
            if (previous != null) {
                previous.getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(imeCallback);
            }
            if (current != null) {
                current.getOnBackInvokedDispatcher().registerOnBackInvokedCallbackUnchecked(imeCallback, imeCallback.mPriority);
            }
        }
    }
}
