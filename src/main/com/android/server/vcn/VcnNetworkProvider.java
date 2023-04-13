package com.android.server.vcn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkProvider;
import android.net.NetworkRequest;
import android.net.NetworkScore;
import android.net.vcn.VcnGatewayConnectionConfig;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public class VcnNetworkProvider extends NetworkProvider {
    public static final String TAG = VcnNetworkProvider.class.getSimpleName();
    public final Context mContext;
    public final Dependencies mDeps;
    public final Handler mHandler;
    public final Set<NetworkRequestListener> mListeners;
    public final Set<NetworkRequest> mRequests;

    /* loaded from: classes2.dex */
    public interface NetworkRequestListener {
        void onNetworkRequested(NetworkRequest networkRequest);
    }

    public VcnNetworkProvider(Context context, Looper looper) {
        this(context, looper, new Dependencies());
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public VcnNetworkProvider(Context context, Looper looper, Dependencies dependencies) {
        super(context, looper, TAG);
        Objects.requireNonNull(context, "Missing context");
        Objects.requireNonNull(looper, "Missing looper");
        this.mListeners = new ArraySet();
        this.mRequests = new ArraySet();
        this.mContext = context;
        this.mHandler = new Handler(looper);
        Objects.requireNonNull(dependencies, "Missing dependencies");
        this.mDeps = dependencies;
    }

    public void register() {
        ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).registerNetworkProvider(this);
        this.mDeps.registerNetworkOffer(this, Vcn.getNetworkScore(), buildCapabilityFilter(), new HandlerExecutor(this.mHandler), new NetworkProvider.NetworkOfferCallback() { // from class: com.android.server.vcn.VcnNetworkProvider.1
            public void onNetworkNeeded(NetworkRequest networkRequest) {
                VcnNetworkProvider.this.handleNetworkRequested(networkRequest);
            }

            public void onNetworkUnneeded(NetworkRequest networkRequest) {
                VcnNetworkProvider.this.handleNetworkRequestWithdrawn(networkRequest);
            }
        });
    }

    public final NetworkCapabilities buildCapabilityFilter() {
        NetworkCapabilities.Builder addCapability = new NetworkCapabilities.Builder().addTransportType(0).addCapability(14).addCapability(13).addCapability(15).addCapability(28);
        for (Integer num : VcnGatewayConnectionConfig.ALLOWED_CAPABILITIES) {
            addCapability.addCapability(num.intValue());
        }
        return addCapability.build();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void registerListener(NetworkRequestListener networkRequestListener) {
        this.mListeners.add(networkRequestListener);
        resendAllRequests(networkRequestListener);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void unregisterListener(NetworkRequestListener networkRequestListener) {
        this.mListeners.remove(networkRequestListener);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void resendAllRequests(NetworkRequestListener networkRequestListener) {
        for (NetworkRequest networkRequest : this.mRequests) {
            notifyListenerForEvent(networkRequestListener, networkRequest);
        }
    }

    public final void notifyListenerForEvent(NetworkRequestListener networkRequestListener, NetworkRequest networkRequest) {
        networkRequestListener.onNetworkRequested(networkRequest);
    }

    public final void handleNetworkRequested(NetworkRequest networkRequest) {
        this.mRequests.add(networkRequest);
        for (NetworkRequestListener networkRequestListener : this.mListeners) {
            notifyListenerForEvent(networkRequestListener, networkRequest);
        }
    }

    public final void handleNetworkRequestWithdrawn(NetworkRequest networkRequest) {
        this.mRequests.remove(networkRequest);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("VcnNetworkProvider:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mListeners:");
        indentingPrintWriter.increaseIndent();
        for (NetworkRequestListener networkRequestListener : this.mListeners) {
            indentingPrintWriter.println(networkRequestListener);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.println("mRequests:");
        indentingPrintWriter.increaseIndent();
        for (NetworkRequest networkRequest : this.mRequests) {
            indentingPrintWriter.println(networkRequest);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes2.dex */
    public static class Dependencies {
        public void registerNetworkOffer(VcnNetworkProvider vcnNetworkProvider, NetworkScore networkScore, NetworkCapabilities networkCapabilities, Executor executor, NetworkProvider.NetworkOfferCallback networkOfferCallback) {
            vcnNetworkProvider.registerNetworkOffer(networkScore, networkCapabilities, executor, networkOfferCallback);
        }
    }
}
