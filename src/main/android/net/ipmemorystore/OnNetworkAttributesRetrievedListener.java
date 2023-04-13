package android.net.ipmemorystore;

import android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener;
/* loaded from: classes.dex */
public interface OnNetworkAttributesRetrievedListener {
    void onNetworkAttributesRetrieved(Status status, String str, NetworkAttributes networkAttributes);

    static IOnNetworkAttributesRetrievedListener toAIDL(OnNetworkAttributesRetrievedListener onNetworkAttributesRetrievedListener) {
        return new IOnNetworkAttributesRetrievedListener.Stub() { // from class: android.net.ipmemorystore.OnNetworkAttributesRetrievedListener.1
            @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
            public String getInterfaceHash() {
                return "d5ea5eb3ddbdaa9a986ce6ba70b0804ca3e39b0c";
            }

            @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
            public int getInterfaceVersion() {
                return 10;
            }

            @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
            public void onNetworkAttributesRetrieved(StatusParcelable statusParcelable, String str, NetworkAttributesParcelable networkAttributesParcelable) {
                OnNetworkAttributesRetrievedListener onNetworkAttributesRetrievedListener2 = OnNetworkAttributesRetrievedListener.this;
                if (onNetworkAttributesRetrievedListener2 != null) {
                    onNetworkAttributesRetrievedListener2.onNetworkAttributesRetrieved(new Status(statusParcelable), str, networkAttributesParcelable == null ? null : new NetworkAttributes(networkAttributesParcelable));
                }
            }
        };
    }
}
