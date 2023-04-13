package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityMetricsEvent;
import android.net.IIpConnectivityMetrics;
import android.net.INetdEventCallback;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkStack;
import android.net.metrics.ApfProgramEvent;
import android.os.Binder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.RingBuffer;
import com.android.internal.util.TokenBucket;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.connectivity.metrics.nano.IpConnectivityLogClass;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public final class IpConnectivityMetrics extends SystemService {
    public static final ToIntFunction<Context> READ_BUFFER_SIZE = new ToIntFunction() { // from class: com.android.server.connectivity.IpConnectivityMetrics$$ExternalSyntheticLambda0
        @Override // java.util.function.ToIntFunction
        public final int applyAsInt(Object obj) {
            int lambda$static$1;
            lambda$static$1 = IpConnectivityMetrics.lambda$static$1((Context) obj);
            return lambda$static$1;
        }
    };
    public static final String TAG = "IpConnectivityMetrics";
    @VisibleForTesting
    public final Impl impl;
    @GuardedBy({"mLock"})
    public final ArrayMap<Class<?>, TokenBucket> mBuckets;
    @GuardedBy({"mLock"})
    public ArrayList<ConnectivityMetricsEvent> mBuffer;
    @GuardedBy({"mLock"})
    public int mCapacity;
    public final ToIntFunction<Context> mCapacityGetter;
    @VisibleForTesting
    final DefaultNetworkMetrics mDefaultNetworkMetrics;
    @GuardedBy({"mLock"})
    public int mDropped;
    @GuardedBy({"mLock"})
    public final RingBuffer<ConnectivityMetricsEvent> mEventLog;
    public final Object mLock;
    @VisibleForTesting
    NetdEventListenerService mNetdListener;

    /* loaded from: classes.dex */
    public interface Logger {
    }

    @Override // com.android.server.SystemService
    public void onStart() {
    }

    public IpConnectivityMetrics(Context context, ToIntFunction<Context> toIntFunction) {
        super(context);
        this.mLock = new Object();
        this.impl = new Impl();
        this.mEventLog = new RingBuffer<>(ConnectivityMetricsEvent.class, 500);
        this.mBuckets = makeRateLimitingBuckets();
        this.mDefaultNetworkMetrics = new DefaultNetworkMetrics();
        this.mCapacityGetter = toIntFunction;
        initBuffer();
    }

    public IpConnectivityMetrics(Context context) {
        this(context, READ_BUFFER_SIZE);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            this.mNetdListener = new NetdEventListenerService(getContext());
            publishBinderService("connmetrics", this.impl);
            publishBinderService("netd_listener", this.mNetdListener);
            LocalServices.addService(Logger.class, new LoggerImpl());
        }
    }

    @VisibleForTesting
    public int bufferCapacity() {
        return this.mCapacityGetter.applyAsInt(getContext());
    }

    public final void initBuffer() {
        synchronized (this.mLock) {
            this.mDropped = 0;
            this.mCapacity = bufferCapacity();
            this.mBuffer = new ArrayList<>(this.mCapacity);
        }
    }

    public final int append(ConnectivityMetricsEvent connectivityMetricsEvent) {
        synchronized (this.mLock) {
            this.mEventLog.append(connectivityMetricsEvent);
            int size = this.mCapacity - this.mBuffer.size();
            if (connectivityMetricsEvent == null) {
                return size;
            }
            if (isRateLimited(connectivityMetricsEvent)) {
                return -1;
            }
            if (size == 0) {
                this.mDropped++;
                return 0;
            }
            this.mBuffer.add(connectivityMetricsEvent);
            return size - 1;
        }
    }

    public final boolean isRateLimited(ConnectivityMetricsEvent connectivityMetricsEvent) {
        TokenBucket tokenBucket = this.mBuckets.get(connectivityMetricsEvent.data.getClass());
        return (tokenBucket == null || tokenBucket.get()) ? false : true;
    }

    public final String flushEncodedOutput() {
        ArrayList<ConnectivityMetricsEvent> arrayList;
        int i;
        synchronized (this.mLock) {
            arrayList = this.mBuffer;
            i = this.mDropped;
            initBuffer();
        }
        List<IpConnectivityLogClass.IpConnectivityEvent> proto = IpConnectivityEventBuilder.toProto(arrayList);
        this.mDefaultNetworkMetrics.flushEvents(proto);
        NetdEventListenerService netdEventListenerService = this.mNetdListener;
        if (netdEventListenerService != null) {
            netdEventListenerService.flushStatistics(proto);
        }
        try {
            return Base64.encodeToString(IpConnectivityEventBuilder.serialize(i, proto), 0);
        } catch (IOException e) {
            Log.e(TAG, "could not serialize events", e);
            return "";
        }
    }

    public final void cmdFlush(PrintWriter printWriter) {
        printWriter.print(flushEncodedOutput());
    }

    public final void cmdList(PrintWriter printWriter) {
        printWriter.println("metrics events:");
        for (ConnectivityMetricsEvent connectivityMetricsEvent : getEvents()) {
            printWriter.println(connectivityMetricsEvent.toString());
        }
        printWriter.println("");
        NetdEventListenerService netdEventListenerService = this.mNetdListener;
        if (netdEventListenerService != null) {
            netdEventListenerService.list(printWriter);
        }
        printWriter.println("");
        this.mDefaultNetworkMetrics.listEvents(printWriter);
    }

    public final List<IpConnectivityLogClass.IpConnectivityEvent> listEventsAsProtos() {
        List<IpConnectivityLogClass.IpConnectivityEvent> proto = IpConnectivityEventBuilder.toProto(getEvents());
        NetdEventListenerService netdEventListenerService = this.mNetdListener;
        if (netdEventListenerService != null) {
            proto.addAll(netdEventListenerService.listAsProtos());
        }
        proto.addAll(this.mDefaultNetworkMetrics.listEventsAsProto());
        return proto;
    }

    public static /* synthetic */ void lambda$cmdListAsTextProto$0(PrintWriter printWriter, IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent) {
        printWriter.print(ipConnectivityEvent.toString());
    }

    public final void cmdListAsTextProto(final PrintWriter printWriter) {
        listEventsAsProtos().forEach(new Consumer() { // from class: com.android.server.connectivity.IpConnectivityMetrics$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                IpConnectivityMetrics.lambda$cmdListAsTextProto$0(printWriter, (IpConnectivityLogClass.IpConnectivityEvent) obj);
            }
        });
    }

    public final void cmdListAsBinaryProto(OutputStream outputStream) {
        int i;
        synchronized (this.mLock) {
            i = this.mDropped;
        }
        try {
            outputStream.write(IpConnectivityEventBuilder.serialize(i, listEventsAsProtos()));
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "could not serialize events", e);
        }
    }

    public final List<ConnectivityMetricsEvent> getEvents() {
        List<ConnectivityMetricsEvent> asList;
        synchronized (this.mLock) {
            asList = Arrays.asList((ConnectivityMetricsEvent[]) this.mEventLog.toArray());
        }
        return asList;
    }

    /* loaded from: classes.dex */
    public final class Impl extends IIpConnectivityMetrics.Stub {
        public Impl() {
        }

        public int logEvent(ConnectivityMetricsEvent connectivityMetricsEvent) {
            NetworkStack.checkNetworkStackPermission(IpConnectivityMetrics.this.getContext());
            return IpConnectivityMetrics.this.append(connectivityMetricsEvent);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:13:0x0027, code lost:
            if (r7.equals("flush") != false) goto L9;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            enforceDumpPermission();
            char c = 0;
            String str = strArr.length > 0 ? strArr[0] : "";
            switch (str.hashCode()) {
                case -1616754616:
                    if (str.equals("--proto")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 3322014:
                    if (str.equals("list")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 97532676:
                    break;
                case 106940904:
                    if (str.equals("proto")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            if (c == 0) {
                IpConnectivityMetrics.this.cmdFlush(printWriter);
            } else if (c == 1) {
                IpConnectivityMetrics.this.cmdListAsTextProto(printWriter);
            } else if (c == 2) {
                IpConnectivityMetrics.this.cmdListAsBinaryProto(new FileOutputStream(fileDescriptor));
            } else {
                IpConnectivityMetrics.this.cmdList(printWriter);
            }
        }

        public final void enforceDumpPermission() {
            enforcePermission("android.permission.DUMP");
        }

        public final void enforcePermission(String str) {
            IpConnectivityMetrics.this.getContext().enforceCallingOrSelfPermission(str, IpConnectivityMetrics.TAG);
        }

        public final void enforceNetdEventListeningPermission() {
            int callingUid = Binder.getCallingUid();
            if (callingUid != 1000) {
                throw new SecurityException(String.format("Uid %d has no permission to listen for netd events.", Integer.valueOf(callingUid)));
            }
        }

        public boolean addNetdEventCallback(int i, INetdEventCallback iNetdEventCallback) {
            enforceNetdEventListeningPermission();
            NetdEventListenerService netdEventListenerService = IpConnectivityMetrics.this.mNetdListener;
            if (netdEventListenerService == null) {
                return false;
            }
            return netdEventListenerService.addNetdEventCallback(i, iNetdEventCallback);
        }

        public boolean removeNetdEventCallback(int i) {
            enforceNetdEventListeningPermission();
            NetdEventListenerService netdEventListenerService = IpConnectivityMetrics.this.mNetdListener;
            if (netdEventListenerService == null) {
                return true;
            }
            return netdEventListenerService.removeNetdEventCallback(i);
        }

        public void logDefaultNetworkValidity(boolean z) {
            NetworkStack.checkNetworkStackPermission(IpConnectivityMetrics.this.getContext());
            IpConnectivityMetrics.this.mDefaultNetworkMetrics.logDefaultNetworkValidity(SystemClock.elapsedRealtime(), z);
        }

        public void logDefaultNetworkEvent(Network network, int i, boolean z, LinkProperties linkProperties, NetworkCapabilities networkCapabilities, Network network2, int i2, LinkProperties linkProperties2, NetworkCapabilities networkCapabilities2) {
            NetworkStack.checkNetworkStackPermission(IpConnectivityMetrics.this.getContext());
            IpConnectivityMetrics.this.mDefaultNetworkMetrics.logDefaultNetworkEvent(SystemClock.elapsedRealtime(), network, i, z, linkProperties, networkCapabilities, network2, i2, linkProperties2, networkCapabilities2);
        }
    }

    public static /* synthetic */ int lambda$static$1(Context context) {
        int i = Settings.Global.getInt(context.getContentResolver(), "connectivity_metrics_buffer_size", 2000);
        if (i <= 0) {
            return 2000;
        }
        return Math.min(i, 20000);
    }

    public static ArrayMap<Class<?>, TokenBucket> makeRateLimitingBuckets() {
        ArrayMap<Class<?>, TokenBucket> arrayMap = new ArrayMap<>();
        arrayMap.put(ApfProgramEvent.class, new TokenBucket(60000, 50));
        return arrayMap;
    }

    /* loaded from: classes.dex */
    public class LoggerImpl implements Logger {
        public LoggerImpl() {
        }
    }
}
