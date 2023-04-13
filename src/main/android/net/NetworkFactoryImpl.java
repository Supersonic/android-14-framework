package android.net;

import android.content.Context;
import android.net.NetworkCapabilities;
import android.net.NetworkProvider;
import android.net.NetworkScore;
import android.os.Looper;
import android.os.Message;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class NetworkFactoryImpl extends NetworkFactoryLegacyImpl {
    public static final NetworkScore INVINCIBLE_SCORE = new NetworkScore.Builder().setLegacyInt(1000).build();
    public final Executor mExecutor;
    public final Map<NetworkRequest, NetworkRequestInfo> mNetworkRequests;
    public final NetworkProvider.NetworkOfferCallback mRequestCallback;
    public NetworkScore mScore;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Runnable runnable) {
        post(runnable);
    }

    public NetworkFactoryImpl(NetworkFactory networkFactory, Looper looper, Context context, NetworkCapabilities networkCapabilities) {
        super(networkFactory, looper, context, networkCapabilities == null ? NetworkCapabilities.Builder.withoutDefaultCapabilities().build() : networkCapabilities);
        this.mNetworkRequests = new LinkedHashMap();
        this.mScore = new NetworkScore.Builder().setLegacyInt(0).build();
        this.mRequestCallback = new NetworkProvider.NetworkOfferCallback() { // from class: android.net.NetworkFactoryImpl.1
            public void onNetworkNeeded(NetworkRequest networkRequest) {
                NetworkFactoryImpl.this.handleAddRequest(networkRequest);
            }

            public void onNetworkUnneeded(NetworkRequest networkRequest) {
                NetworkFactoryImpl.this.handleRemoveRequest(networkRequest);
            }
        };
        this.mExecutor = new Executor() { // from class: android.net.NetworkFactoryImpl$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                NetworkFactoryImpl.this.lambda$new$0(runnable);
            }
        };
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    public void register(String str) {
        register(str, false);
    }

    @Override // android.net.NetworkFactoryShim
    public void registerIgnoringScore(String str) {
        register(str, true);
    }

    public final void register(String str, boolean z) {
        if (this.mProvider != null) {
            throw new IllegalStateException("A NetworkFactory must only be registered once");
        }
        this.mParent.log("Registering NetworkFactory");
        this.mProvider = new NetworkProvider(this.mContext, getLooper(), str) { // from class: android.net.NetworkFactoryImpl.2
            public void onNetworkRequested(NetworkRequest networkRequest, int i, int i2) {
                NetworkFactoryImpl.this.handleAddRequest(networkRequest);
            }

            public void onNetworkRequestWithdrawn(NetworkRequest networkRequest) {
                NetworkFactoryImpl.this.handleRemoveRequest(networkRequest);
            }
        };
        ((ConnectivityManager) this.mContext.getSystemService("connectivity")).registerNetworkProvider(this.mProvider);
        if (z) {
            sendMessage(obtainMessage(6));
        } else {
            sendMessage(obtainMessage(5));
        }
    }

    public final void handleOfferNetwork(NetworkScore networkScore) {
        this.mProvider.registerNetworkOffer(networkScore, this.mCapabilityFilter, this.mExecutor, this.mRequestCallback);
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                handleAddRequest((NetworkRequest) message.obj);
                return;
            case 2:
                handleRemoveRequest((NetworkRequest) message.obj);
                return;
            case 3:
                handleSetScore((NetworkScore) message.obj);
                return;
            case 4:
                handleSetFilter((NetworkCapabilities) message.obj);
                return;
            case 5:
                handleOfferNetwork(this.mScore);
                return;
            case 6:
                handleOfferNetwork(INVINCIBLE_SCORE);
                return;
            default:
                return;
        }
    }

    /* loaded from: classes.dex */
    public static class NetworkRequestInfo {
        public final NetworkRequest request;
        public boolean requested = false;

        public NetworkRequestInfo(NetworkRequest networkRequest) {
            this.request = networkRequest;
        }

        public String toString() {
            return "{" + this.request + ", requested=" + this.requested + "}";
        }
    }

    public final void handleAddRequest(NetworkRequest networkRequest) {
        NetworkRequestInfo networkRequestInfo = this.mNetworkRequests.get(networkRequest);
        if (networkRequestInfo == null) {
            NetworkFactory networkFactory = this.mParent;
            networkFactory.log("got request " + networkRequest);
            networkRequestInfo = new NetworkRequestInfo(networkRequest);
            this.mNetworkRequests.put(networkRequestInfo.request, networkRequestInfo);
        }
        if (this.mParent.acceptRequest(networkRequest)) {
            networkRequestInfo.requested = true;
            this.mParent.needNetworkFor(networkRequest);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRemoveRequest(NetworkRequest networkRequest) {
        NetworkRequestInfo networkRequestInfo = this.mNetworkRequests.get(networkRequest);
        if (networkRequestInfo != null) {
            this.mNetworkRequests.remove(networkRequest);
            if (networkRequestInfo.requested) {
                this.mParent.releaseNetworkFor(networkRequestInfo.request);
            }
        }
    }

    public final void handleSetScore(NetworkScore networkScore) {
        if (this.mScore.equals(networkScore)) {
            return;
        }
        this.mScore = networkScore;
        this.mParent.reevaluateAllRequests();
    }

    private void handleSetFilter(NetworkCapabilities networkCapabilities) {
        if (networkCapabilities.equals(this.mCapabilityFilter)) {
            return;
        }
        this.mCapabilityFilter = networkCapabilities;
        this.mParent.reevaluateAllRequests();
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    public final void reevaluateAllRequests() {
        NetworkProvider networkProvider = this.mProvider;
        if (networkProvider == null) {
            return;
        }
        networkProvider.registerNetworkOffer(this.mScore, this.mCapabilityFilter, this.mExecutor, this.mRequestCallback);
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    @Deprecated
    public void setScoreFilter(int i) {
        setScoreFilter(new NetworkScore.Builder().setLegacyInt(i).build());
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    public void setScoreFilter(NetworkScore networkScore) {
        sendMessage(obtainMessage(3, networkScore));
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    public void setCapabilityFilter(NetworkCapabilities networkCapabilities) {
        sendMessage(obtainMessage(4, new NetworkCapabilities(networkCapabilities)));
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    public int getRequestCount() {
        return this.mNetworkRequests.size();
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.net.NetworkFactoryShim
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(toString());
        Iterator<NetworkRequestInfo> it = this.mNetworkRequests.values().iterator();
        while (it.hasNext()) {
            printWriter.println("  " + it.next());
        }
    }

    @Override // android.net.NetworkFactoryLegacyImpl, android.os.Handler
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("providerId=");
        NetworkProvider networkProvider = this.mProvider;
        sb.append(networkProvider != null ? Integer.valueOf(networkProvider.getProviderId()) : "null");
        sb.append(", ScoreFilter=");
        sb.append(this.mScore);
        sb.append(", Filter=");
        sb.append(this.mCapabilityFilter);
        sb.append(", requests=");
        sb.append(this.mNetworkRequests.size());
        return sb.toString();
    }
}
