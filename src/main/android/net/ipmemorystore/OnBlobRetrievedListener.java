package android.net.ipmemorystore;

import android.net.ipmemorystore.IOnBlobRetrievedListener;
/* loaded from: classes.dex */
public interface OnBlobRetrievedListener {
    void onBlobRetrieved(Status status, String str, String str2, Blob blob);

    static IOnBlobRetrievedListener toAIDL(OnBlobRetrievedListener onBlobRetrievedListener) {
        return new IOnBlobRetrievedListener.Stub() { // from class: android.net.ipmemorystore.OnBlobRetrievedListener.1
            @Override // android.net.ipmemorystore.IOnBlobRetrievedListener
            public String getInterfaceHash() {
                return "d5ea5eb3ddbdaa9a986ce6ba70b0804ca3e39b0c";
            }

            @Override // android.net.ipmemorystore.IOnBlobRetrievedListener
            public int getInterfaceVersion() {
                return 10;
            }

            @Override // android.net.ipmemorystore.IOnBlobRetrievedListener
            public void onBlobRetrieved(StatusParcelable statusParcelable, String str, String str2, Blob blob) {
                OnBlobRetrievedListener onBlobRetrievedListener2 = OnBlobRetrievedListener.this;
                if (onBlobRetrievedListener2 != null) {
                    onBlobRetrievedListener2.onBlobRetrieved(new Status(statusParcelable), str, str2, blob);
                }
            }
        };
    }
}
