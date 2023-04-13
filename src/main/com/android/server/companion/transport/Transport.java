package com.android.server.companion.transport;

import android.companion.IOnMessageReceivedListener;
import android.content.Context;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public abstract class Transport {
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public final int mAssociationId;
    public final Context mContext;
    public final ParcelFileDescriptor mFd;
    public final InputStream mRemoteIn;
    public final OutputStream mRemoteOut;
    @GuardedBy({"mPendingRequests"})
    public final SparseArray<CompletableFuture<byte[]>> mPendingRequests = new SparseArray<>();
    public final AtomicInteger mNextSequence = new AtomicInteger();
    public final Map<Integer, IOnMessageReceivedListener> mListeners = new HashMap();

    public static boolean isRequest(int i) {
        return (i & (-16777216)) == 1660944384;
    }

    public static boolean isResponse(int i) {
        return (i & (-16777216)) == 855638016;
    }

    public abstract void sendMessage(int i, int i2, byte[] bArr) throws IOException;

    public abstract void start();

    public abstract void stop();

    public Transport(int i, ParcelFileDescriptor parcelFileDescriptor, Context context) {
        this.mAssociationId = i;
        this.mFd = parcelFileDescriptor;
        this.mRemoteIn = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
        this.mRemoteOut = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
        this.mContext = context;
    }

    public void addListener(int i, IOnMessageReceivedListener iOnMessageReceivedListener) {
        this.mListeners.put(Integer.valueOf(i), iOnMessageReceivedListener);
    }

    public int getAssociationId() {
        return this.mAssociationId;
    }

    public ParcelFileDescriptor getFd() {
        return this.mFd;
    }

    public void sendMessage(int i, byte[] bArr) throws IOException {
        sendMessage(i, this.mNextSequence.incrementAndGet(), bArr);
    }

    public Future<byte[]> requestForResponse(int i, byte[] bArr) {
        if (DEBUG) {
            Slog.d("CDM_CompanionTransport", "Requesting for response");
        }
        int incrementAndGet = this.mNextSequence.incrementAndGet();
        CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();
        synchronized (this.mPendingRequests) {
            this.mPendingRequests.put(incrementAndGet, completableFuture);
        }
        try {
            sendMessage(i, incrementAndGet, bArr);
        } catch (IOException e) {
            synchronized (this.mPendingRequests) {
                this.mPendingRequests.remove(incrementAndGet);
                completableFuture.completeExceptionally(e);
            }
        }
        return completableFuture;
    }

    public final void handleMessage(int i, int i2, byte[] bArr) throws IOException {
        if (DEBUG) {
            Slog.d("CDM_CompanionTransport", "Received message 0x" + Integer.toHexString(i) + " sequence " + i2 + " length " + bArr.length + " from association " + this.mAssociationId);
        }
        if (isRequest(i)) {
            try {
                processRequest(i, i2, bArr);
            } catch (IOException e) {
                Slog.w("CDM_CompanionTransport", "Failed to respond to 0x" + Integer.toHexString(i), e);
            }
        } else if (isResponse(i)) {
            processResponse(i, i2, bArr);
        } else {
            Slog.w("CDM_CompanionTransport", "Unknown message 0x" + Integer.toHexString(i));
        }
    }

    public final void processRequest(int i, int i2, byte[] bArr) throws IOException {
        switch (i) {
            case 1667729539:
            case 1669361779:
                callback(i, bArr);
                sendMessage(864257383, i2, EmptyArray.BYTE);
                return;
            case 1669362552:
                sendMessage(864257383, i2, bArr);
                return;
            case 1669491075:
                if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch") && !Build.isDebuggable()) {
                    Slog.w("CDM_CompanionTransport", "Restoring permissions only supported on watches");
                    sendMessage(863004019, i2, EmptyArray.BYTE);
                    return;
                }
                try {
                    callback(i, bArr);
                    sendMessage(864257383, i2, EmptyArray.BYTE);
                    return;
                } catch (Exception unused) {
                    Slog.w("CDM_CompanionTransport", "Failed to restore permissions");
                    sendMessage(863004019, i2, EmptyArray.BYTE);
                    return;
                }
            default:
                Slog.w("CDM_CompanionTransport", "Unknown request 0x" + Integer.toHexString(i));
                sendMessage(863004019, i2, EmptyArray.BYTE);
                return;
        }
    }

    public final void callback(int i, byte[] bArr) {
        if (this.mListeners.containsKey(Integer.valueOf(i))) {
            try {
                this.mListeners.get(Integer.valueOf(i)).onMessageReceived(getAssociationId(), bArr);
                Slog.i("CDM_CompanionTransport", "Message 0x" + Integer.toHexString(i) + " is received from associationId " + this.mAssociationId + ", sending data length " + bArr.length + " to the listener.");
            } catch (RemoteException unused) {
            }
        }
    }

    public final void processResponse(int i, int i2, byte[] bArr) {
        CompletableFuture completableFuture;
        synchronized (this.mPendingRequests) {
            completableFuture = (CompletableFuture) this.mPendingRequests.removeReturnOld(i2);
        }
        if (completableFuture == null) {
            Slog.w("CDM_CompanionTransport", "Ignoring unknown sequence " + i2);
        } else if (i == 863004019) {
            completableFuture.completeExceptionally(new RuntimeException("Remote failure"));
        } else if (i == 864257383) {
            completableFuture.complete(bArr);
        } else {
            Slog.w("CDM_CompanionTransport", "Ignoring unknown response 0x" + Integer.toHexString(i));
        }
    }
}
