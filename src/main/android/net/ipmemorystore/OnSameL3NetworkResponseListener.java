package android.net.ipmemorystore;

import android.net.ipmemorystore.IOnSameL3NetworkResponseListener;
/* loaded from: classes.dex */
public interface OnSameL3NetworkResponseListener {
    void onSameL3NetworkResponse(Status status, SameL3NetworkResponse sameL3NetworkResponse);

    static IOnSameL3NetworkResponseListener toAIDL(OnSameL3NetworkResponseListener onSameL3NetworkResponseListener) {
        return new IOnSameL3NetworkResponseListener.Stub() { // from class: android.net.ipmemorystore.OnSameL3NetworkResponseListener.1
            @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
            public String getInterfaceHash() {
                return "d5ea5eb3ddbdaa9a986ce6ba70b0804ca3e39b0c";
            }

            @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
            public int getInterfaceVersion() {
                return 10;
            }

            @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
            public void onSameL3NetworkResponse(StatusParcelable statusParcelable, SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable) {
                OnSameL3NetworkResponseListener onSameL3NetworkResponseListener2 = OnSameL3NetworkResponseListener.this;
                if (onSameL3NetworkResponseListener2 != null) {
                    onSameL3NetworkResponseListener2.onSameL3NetworkResponse(new Status(statusParcelable), new SameL3NetworkResponse(sameL3NetworkResponseParcelable));
                }
            }
        };
    }
}
