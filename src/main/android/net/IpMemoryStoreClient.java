package android.net;

import android.content.Context;
import android.net.ipmemorystore.Blob;
import android.net.ipmemorystore.NetworkAttributes;
import android.net.ipmemorystore.OnBlobRetrievedListener;
import android.net.ipmemorystore.OnDeleteStatusListener;
import android.net.ipmemorystore.OnL2KeyResponseListener;
import android.net.ipmemorystore.OnNetworkAttributesRetrievedListener;
import android.net.ipmemorystore.OnSameL3NetworkResponseListener;
import android.net.ipmemorystore.OnStatusListener;
import android.net.ipmemorystore.Status;
import android.os.RemoteException;
import android.util.Log;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public abstract class IpMemoryStoreClient {
    private static final String TAG = "IpMemoryStoreClient";
    private final Context mContext;

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface ThrowingRunnable {
        void run() throws RemoteException;
    }

    public abstract void runWhenServiceReady(Consumer<IIpMemoryStore> consumer) throws ExecutionException;

    public IpMemoryStoreClient(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("missing context");
        }
        this.mContext = context;
    }

    private void ignoringRemoteException(ThrowingRunnable throwingRunnable) {
        ignoringRemoteException("Failed to execute remote procedure call", throwingRunnable);
    }

    private void ignoringRemoteException(String str, ThrowingRunnable throwingRunnable) {
        try {
            throwingRunnable.run();
        } catch (RemoteException e) {
            Log.e(TAG, str, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$storeNetworkAttributes$1(final String str, final NetworkAttributes networkAttributes, final OnStatusListener onStatusListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda25
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$storeNetworkAttributes$0(IIpMemoryStore.this, str, networkAttributes, onStatusListener);
            }
        });
    }

    public void storeNetworkAttributes(final String str, final NetworkAttributes networkAttributes, final OnStatusListener onStatusListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda21
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$storeNetworkAttributes$1(str, networkAttributes, onStatusListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            if (onStatusListener == null) {
                return;
            }
            ignoringRemoteException("Error storing network attributes", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda22
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$storeNetworkAttributes$2(OnStatusListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$storeNetworkAttributes$0(IIpMemoryStore iIpMemoryStore, String str, NetworkAttributes networkAttributes, OnStatusListener onStatusListener) throws RemoteException {
        iIpMemoryStore.storeNetworkAttributes(str, networkAttributes.toParcelable(), OnStatusListener.toAIDL(onStatusListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$storeNetworkAttributes$2(OnStatusListener onStatusListener) throws RemoteException {
        onStatusListener.onComplete(new Status(-5));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$storeBlob$4(final String str, final String str2, final String str3, final Blob blob, final OnStatusListener onStatusListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda20
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$storeBlob$3(IIpMemoryStore.this, str, str2, str3, blob, onStatusListener);
            }
        });
    }

    public void storeBlob(final String str, final String str2, final String str3, final Blob blob, final OnStatusListener onStatusListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda17
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$storeBlob$4(str, str2, str3, blob, onStatusListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            if (onStatusListener == null) {
                return;
            }
            ignoringRemoteException("Error storing blob", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda18
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$storeBlob$5(OnStatusListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$storeBlob$3(IIpMemoryStore iIpMemoryStore, String str, String str2, String str3, Blob blob, OnStatusListener onStatusListener) throws RemoteException {
        iIpMemoryStore.storeBlob(str, str2, str3, blob, OnStatusListener.toAIDL(onStatusListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$storeBlob$5(OnStatusListener onStatusListener) throws RemoteException {
        onStatusListener.onComplete(new Status(-5));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$findL2Key$7(final NetworkAttributes networkAttributes, final OnL2KeyResponseListener onL2KeyResponseListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda15
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$findL2Key$6(IIpMemoryStore.this, networkAttributes, onL2KeyResponseListener);
            }
        });
    }

    public void findL2Key(final NetworkAttributes networkAttributes, final OnL2KeyResponseListener onL2KeyResponseListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$findL2Key$7(networkAttributes, onL2KeyResponseListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            ignoringRemoteException("Error finding L2 Key", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda5
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$findL2Key$8(OnL2KeyResponseListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$findL2Key$6(IIpMemoryStore iIpMemoryStore, NetworkAttributes networkAttributes, OnL2KeyResponseListener onL2KeyResponseListener) throws RemoteException {
        iIpMemoryStore.findL2Key(networkAttributes.toParcelable(), OnL2KeyResponseListener.toAIDL(onL2KeyResponseListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$findL2Key$8(OnL2KeyResponseListener onL2KeyResponseListener) throws RemoteException {
        onL2KeyResponseListener.onL2KeyResponse(new Status(-5), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$isSameNetwork$10(final String str, final String str2, final OnSameL3NetworkResponseListener onSameL3NetworkResponseListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda6
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$isSameNetwork$9(IIpMemoryStore.this, str, str2, onSameL3NetworkResponseListener);
            }
        });
    }

    public void isSameNetwork(final String str, final String str2, final OnSameL3NetworkResponseListener onSameL3NetworkResponseListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$isSameNetwork$10(str, str2, onSameL3NetworkResponseListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            ignoringRemoteException("Error checking for network sameness", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda10
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$isSameNetwork$11(OnSameL3NetworkResponseListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$isSameNetwork$9(IIpMemoryStore iIpMemoryStore, String str, String str2, OnSameL3NetworkResponseListener onSameL3NetworkResponseListener) throws RemoteException {
        iIpMemoryStore.isSameNetwork(str, str2, OnSameL3NetworkResponseListener.toAIDL(onSameL3NetworkResponseListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$isSameNetwork$11(OnSameL3NetworkResponseListener onSameL3NetworkResponseListener) throws RemoteException {
        onSameL3NetworkResponseListener.onSameL3NetworkResponse(new Status(-5), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$retrieveNetworkAttributes$13(final String str, final OnNetworkAttributesRetrievedListener onNetworkAttributesRetrievedListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda3
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$retrieveNetworkAttributes$12(IIpMemoryStore.this, str, onNetworkAttributesRetrievedListener);
            }
        });
    }

    public void retrieveNetworkAttributes(final String str, final OnNetworkAttributesRetrievedListener onNetworkAttributesRetrievedListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$retrieveNetworkAttributes$13(str, onNetworkAttributesRetrievedListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            ignoringRemoteException("Error retrieving network attributes", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda8
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$retrieveNetworkAttributes$14(OnNetworkAttributesRetrievedListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$retrieveNetworkAttributes$12(IIpMemoryStore iIpMemoryStore, String str, OnNetworkAttributesRetrievedListener onNetworkAttributesRetrievedListener) throws RemoteException {
        iIpMemoryStore.retrieveNetworkAttributes(str, OnNetworkAttributesRetrievedListener.toAIDL(onNetworkAttributesRetrievedListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$retrieveNetworkAttributes$14(OnNetworkAttributesRetrievedListener onNetworkAttributesRetrievedListener) throws RemoteException {
        onNetworkAttributesRetrievedListener.onNetworkAttributesRetrieved(new Status(-5), null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$retrieveBlob$16(final String str, final String str2, final String str3, final OnBlobRetrievedListener onBlobRetrievedListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda11
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$retrieveBlob$15(IIpMemoryStore.this, str, str2, str3, onBlobRetrievedListener);
            }
        });
    }

    public void retrieveBlob(final String str, final String str2, final String str3, final OnBlobRetrievedListener onBlobRetrievedListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda23
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$retrieveBlob$16(str, str2, str3, onBlobRetrievedListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            ignoringRemoteException("Error retrieving blob", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda24
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$retrieveBlob$17(OnBlobRetrievedListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$retrieveBlob$15(IIpMemoryStore iIpMemoryStore, String str, String str2, String str3, OnBlobRetrievedListener onBlobRetrievedListener) throws RemoteException {
        iIpMemoryStore.retrieveBlob(str, str2, str3, OnBlobRetrievedListener.toAIDL(onBlobRetrievedListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$retrieveBlob$17(OnBlobRetrievedListener onBlobRetrievedListener) throws RemoteException {
        onBlobRetrievedListener.onBlobRetrieved(new Status(-5), null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$delete$19(final String str, final boolean z, final OnDeleteStatusListener onDeleteStatusListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda0
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$delete$18(IIpMemoryStore.this, str, z, onDeleteStatusListener);
            }
        });
    }

    public void delete(final String str, final boolean z, final OnDeleteStatusListener onDeleteStatusListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda12
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$delete$19(str, z, onDeleteStatusListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            if (onDeleteStatusListener == null) {
                return;
            }
            ignoringRemoteException("Error deleting from the memory store", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda13
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$delete$20(OnDeleteStatusListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$delete$18(IIpMemoryStore iIpMemoryStore, String str, boolean z, OnDeleteStatusListener onDeleteStatusListener) throws RemoteException {
        iIpMemoryStore.delete(str, z, OnDeleteStatusListener.toAIDL(onDeleteStatusListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$delete$20(OnDeleteStatusListener onDeleteStatusListener) throws RemoteException {
        onDeleteStatusListener.onComplete(new Status(-5), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteCluster$22(final String str, final boolean z, final OnDeleteStatusListener onDeleteStatusListener, final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda16
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IpMemoryStoreClient.lambda$deleteCluster$21(IIpMemoryStore.this, str, z, onDeleteStatusListener);
            }
        });
    }

    public void deleteCluster(final String str, final boolean z, final OnDeleteStatusListener onDeleteStatusListener) {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$deleteCluster$22(str, z, onDeleteStatusListener, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException unused) {
            if (onDeleteStatusListener == null) {
                return;
            }
            ignoringRemoteException("Error deleting from the memory store", new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda2
                @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
                public final void run() {
                    IpMemoryStoreClient.lambda$deleteCluster$23(OnDeleteStatusListener.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$deleteCluster$21(IIpMemoryStore iIpMemoryStore, String str, boolean z, OnDeleteStatusListener onDeleteStatusListener) throws RemoteException {
        iIpMemoryStore.deleteCluster(str, z, OnDeleteStatusListener.toAIDL(onDeleteStatusListener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$deleteCluster$23(OnDeleteStatusListener onDeleteStatusListener) throws RemoteException {
        onDeleteStatusListener.onComplete(new Status(-5), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$factoryReset$25(final IIpMemoryStore iIpMemoryStore) {
        ignoringRemoteException(new ThrowingRunnable() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda19
            @Override // android.net.IpMemoryStoreClient.ThrowingRunnable
            public final void run() {
                IIpMemoryStore.this.factoryReset();
            }
        });
    }

    public void factoryReset() {
        try {
            runWhenServiceReady(new Consumer() { // from class: android.net.IpMemoryStoreClient$$ExternalSyntheticLambda14
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$factoryReset$25((IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            Log.e(TAG, "Error executing factory reset", e);
        }
    }
}
