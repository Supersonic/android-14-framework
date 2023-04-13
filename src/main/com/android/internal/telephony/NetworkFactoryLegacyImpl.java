package com.android.internal.telephony;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkProvider;
import android.net.NetworkRequest;
import android.net.NetworkScore;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.annotations.VisibleForTesting;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class NetworkFactoryLegacyImpl extends Handler implements NetworkFactoryShim {
    public static final int CMD_CANCEL_REQUEST = 2;
    public static final int CMD_REQUEST_NETWORK = 1;
    NetworkCapabilities mCapabilityFilter;
    final Context mContext;
    private final Map<NetworkRequest, NetworkRequestInfo> mNetworkRequests;
    final NetworkFactory mParent;
    NetworkProvider mProvider;
    private int mScore;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NetworkFactoryLegacyImpl(NetworkFactory networkFactory, Looper looper, Context context, NetworkCapabilities networkCapabilities) {
        super(looper);
        this.mNetworkRequests = new LinkedHashMap();
        this.mProvider = null;
        this.mParent = networkFactory;
        this.mContext = context;
        this.mCapabilityFilter = networkCapabilities;
    }

    public void register(String str) {
        if (this.mProvider != null) {
            throw new IllegalStateException("A NetworkFactory must only be registered once");
        }
        this.mParent.log("Registering NetworkFactory");
        this.mProvider = new NetworkProvider(this.mContext, getLooper(), str) { // from class: com.android.internal.telephony.NetworkFactoryLegacyImpl.1
            public void onNetworkRequested(NetworkRequest networkRequest, int i, int i2) {
                NetworkFactoryLegacyImpl.this.handleAddRequest(networkRequest, i, i2);
            }

            public void onNetworkRequestWithdrawn(NetworkRequest networkRequest) {
                NetworkFactoryLegacyImpl.this.handleRemoveRequest(networkRequest);
            }
        };
        ((ConnectivityManager) this.mContext.getSystemService("connectivity")).registerNetworkProvider(this.mProvider);
    }

    @Override // com.android.internal.telephony.NetworkFactoryShim
    public void terminate() {
        if (this.mProvider == null) {
            throw new IllegalStateException("This NetworkFactory was never registered");
        }
        this.mParent.log("Unregistering NetworkFactory");
        ((ConnectivityManager) this.mContext.getSystemService("connectivity")).unregisterNetworkProvider(this.mProvider);
        removeCallbacksAndMessages(null);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            handleAddRequest((NetworkRequest) message.obj, message.arg1, message.arg2);
        } else if (i == 2) {
            handleRemoveRequest((NetworkRequest) message.obj);
        } else if (i == 3) {
            handleSetScore(message.arg1);
        } else if (i != 4) {
        } else {
            handleSetFilter((NetworkCapabilities) message.obj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NetworkRequestInfo {
        public int providerId;
        public final NetworkRequest request;
        public boolean requested = false;
        public int score;

        NetworkRequestInfo(NetworkRequest networkRequest, int i, int i2) {
            this.request = networkRequest;
            this.score = i;
            this.providerId = i2;
        }

        public String toString() {
            return "{" + this.request + ", score=" + this.score + ", requested=" + this.requested + "}";
        }
    }

    @VisibleForTesting
    protected void handleAddRequest(NetworkRequest networkRequest, int i, int i2) {
        NetworkRequestInfo networkRequestInfo = this.mNetworkRequests.get(networkRequest);
        if (networkRequestInfo == null) {
            NetworkFactory networkFactory = this.mParent;
            networkFactory.log("got request " + networkRequest + " with score " + i + " and providerId " + i2);
            networkRequestInfo = new NetworkRequestInfo(networkRequest, i, i2);
            this.mNetworkRequests.put(networkRequestInfo.request, networkRequestInfo);
        } else {
            networkRequestInfo.score = i;
            networkRequestInfo.providerId = i2;
        }
        evalRequest(networkRequestInfo);
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

    private void handleSetScore(int i) {
        this.mScore = i;
        evalRequests();
    }

    private void handleSetFilter(NetworkCapabilities networkCapabilities) {
        this.mCapabilityFilter = networkCapabilities;
        evalRequests();
    }

    public boolean acceptRequest(NetworkRequest networkRequest) {
        return this.mParent.acceptRequest(networkRequest);
    }

    private void evalRequest(NetworkRequestInfo networkRequestInfo) {
        if (shouldNeedNetworkFor(networkRequestInfo)) {
            this.mParent.needNetworkFor(networkRequestInfo.request);
            networkRequestInfo.requested = true;
        } else if (shouldReleaseNetworkFor(networkRequestInfo)) {
            this.mParent.releaseNetworkFor(networkRequestInfo.request);
            networkRequestInfo.requested = false;
        }
    }

    private boolean shouldNeedNetworkFor(NetworkRequestInfo networkRequestInfo) {
        return !networkRequestInfo.requested && (networkRequestInfo.score < this.mScore || networkRequestInfo.providerId == this.mProvider.getProviderId()) && networkRequestInfo.request.canBeSatisfiedBy(this.mCapabilityFilter) && acceptRequest(networkRequestInfo.request);
    }

    private boolean shouldReleaseNetworkFor(NetworkRequestInfo networkRequestInfo) {
        return networkRequestInfo.requested && !((networkRequestInfo.score <= this.mScore || networkRequestInfo.providerId == this.mProvider.getProviderId()) && networkRequestInfo.request.canBeSatisfiedBy(this.mCapabilityFilter) && acceptRequest(networkRequestInfo.request));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void evalRequests() {
        for (NetworkRequestInfo networkRequestInfo : this.mNetworkRequests.values()) {
            evalRequest(networkRequestInfo);
        }
    }

    public void reevaluateAllRequests() {
        post(new Runnable() { // from class: com.android.internal.telephony.NetworkFactoryLegacyImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NetworkFactoryLegacyImpl.this.evalRequests();
            }
        });
    }

    @Override // com.android.internal.telephony.NetworkFactoryShim
    public void releaseRequestAsUnfulfillableByAnyFactory(final NetworkRequest networkRequest) {
        post(new Runnable() { // from class: com.android.internal.telephony.NetworkFactoryLegacyImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                NetworkFactoryLegacyImpl.this.lambda$releaseRequestAsUnfulfillableByAnyFactory$0(networkRequest);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$releaseRequestAsUnfulfillableByAnyFactory$0(NetworkRequest networkRequest) {
        NetworkFactory networkFactory = this.mParent;
        networkFactory.log("releaseRequestAsUnfulfillableByAnyFactory: " + networkRequest);
        NetworkProvider networkProvider = this.mProvider;
        if (networkProvider == null) {
            this.mParent.log("Ignoring attempt to release unregistered request as unfulfillable");
        } else {
            networkProvider.declareNetworkRequestUnfulfillable(networkRequest);
        }
    }

    public void setScoreFilter(int i) {
        sendMessage(obtainMessage(3, i, 0));
    }

    public void setScoreFilter(NetworkScore networkScore) {
        setScoreFilter(networkScore.getLegacyInt());
    }

    public void setCapabilityFilter(NetworkCapabilities networkCapabilities) {
        sendMessage(obtainMessage(4, new NetworkCapabilities(networkCapabilities)));
    }

    public int getRequestCount() {
        return this.mNetworkRequests.size();
    }

    @Override // com.android.internal.telephony.NetworkFactoryShim
    public int getSerialNumber() {
        return this.mProvider.getProviderId();
    }

    @Override // com.android.internal.telephony.NetworkFactoryShim
    public NetworkProvider getProvider() {
        return this.mProvider;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(toString());
        Iterator<NetworkRequestInfo> it = this.mNetworkRequests.values().iterator();
        while (it.hasNext()) {
            printWriter.println("  " + it.next());
        }
    }

    @Override // android.os.Handler
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
