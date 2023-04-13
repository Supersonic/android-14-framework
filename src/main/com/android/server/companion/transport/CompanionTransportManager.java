package com.android.server.companion.transport;

import android.annotation.SuppressLint;
import android.app.ActivityManagerInternal;
import android.companion.AssociationInfo;
import android.companion.IOnMessageReceivedListener;
import android.companion.IOnTransportsChangedListener;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.server.LocalServices;
import com.android.server.companion.AssociationStore;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
@SuppressLint({"LongLogTag"})
/* loaded from: classes.dex */
public class CompanionTransportManager {
    public final AssociationStore mAssociationStore;
    public final Context mContext;
    public Transport mTempTransport;
    public boolean mSecureTransportEnabled = true;
    @GuardedBy({"mTransports"})
    public final SparseArray<Transport> mTransports = new SparseArray<>();
    public final RemoteCallbackList<IOnTransportsChangedListener> mTransportsListeners = new RemoteCallbackList<>();
    public final SparseArray<IOnMessageReceivedListener> mMessageListeners = new SparseArray<>();

    public CompanionTransportManager(Context context, AssociationStore associationStore) {
        this.mContext = context;
        this.mAssociationStore = associationStore;
    }

    @GuardedBy({"mTransports"})
    public void addListener(int i, IOnMessageReceivedListener iOnMessageReceivedListener) {
        this.mMessageListeners.put(i, iOnMessageReceivedListener);
        for (int i2 = 0; i2 < this.mTransports.size(); i2++) {
            this.mTransports.valueAt(i2).addListener(i, iOnMessageReceivedListener);
        }
    }

    @GuardedBy({"mTransports"})
    public void addListener(final IOnTransportsChangedListener iOnTransportsChangedListener) {
        Slog.i("CDM_CompanionTransportManager", "Registering OnTransportsChangedListener");
        this.mTransportsListeners.register(iOnTransportsChangedListener);
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mTransports.size(); i++) {
            AssociationInfo associationById = this.mAssociationStore.getAssociationById(this.mTransports.keyAt(i));
            if (associationById != null) {
                arrayList.add(associationById);
            }
        }
        this.mTransportsListeners.broadcast(new Consumer() { // from class: com.android.server.companion.transport.CompanionTransportManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CompanionTransportManager.lambda$addListener$0(iOnTransportsChangedListener, arrayList, (IOnTransportsChangedListener) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$addListener$0(IOnTransportsChangedListener iOnTransportsChangedListener, List list, IOnTransportsChangedListener iOnTransportsChangedListener2) {
        if (iOnTransportsChangedListener2 == iOnTransportsChangedListener) {
            try {
                iOnTransportsChangedListener.onTransportsChanged(list);
            } catch (RemoteException unused) {
            }
        }
    }

    public void removeListener(IOnTransportsChangedListener iOnTransportsChangedListener) {
        this.mTransportsListeners.unregister(iOnTransportsChangedListener);
    }

    public void removeListener(int i, IOnMessageReceivedListener iOnMessageReceivedListener) {
        this.mMessageListeners.remove(i);
    }

    @GuardedBy({"mTransports"})
    public void sendMessage(int i, byte[] bArr, int[] iArr) {
        Slog.i("CDM_CompanionTransportManager", "Sending message 0x" + Integer.toHexString(i) + " data length " + bArr.length);
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (this.mTransports.contains(iArr[i2])) {
                try {
                    this.mTransports.get(iArr[i2]).sendMessage(i, bArr);
                } catch (IOException unused) {
                    Slog.e("CDM_CompanionTransportManager", "Failed to send message 0x" + Integer.toHexString(i) + " data length " + bArr.length + " to association " + iArr[i2]);
                }
            }
        }
    }

    public final void enforceCallerCanTransportSystemData(String str, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DELIVER_COMPANION_MESSAGES", "CDM_CompanionTransportManager");
        try {
            ApplicationInfo applicationInfoAsUser = this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, i);
            int instrumentationSourceUid = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getInstrumentationSourceUid(Binder.getCallingUid());
            if (!Build.isDebuggable() && !applicationInfoAsUser.isSystemApp() && instrumentationSourceUid == -1) {
                throw new SecurityException("Transporting of system data currently only available to built-in companion apps or tests");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void attachSystemDataTransport(String str, int i, int i2, ParcelFileDescriptor parcelFileDescriptor) {
        enforceCallerCanTransportSystemData(str, i);
        synchronized (this.mTransports) {
            if (this.mTransports.contains(i2)) {
                detachSystemDataTransport(str, i, i2);
            }
            initializeTransport(i2, parcelFileDescriptor);
            notifyOnTransportsChanged();
        }
    }

    public void detachSystemDataTransport(String str, int i, int i2) {
        enforceCallerCanTransportSystemData(str, i);
        synchronized (this.mTransports) {
            Transport transport = this.mTransports.get(i2);
            if (transport != null) {
                this.mTransports.delete(i2);
                transport.stop();
            }
            notifyOnTransportsChanged();
        }
    }

    @GuardedBy({"mTransports"})
    public final void notifyOnTransportsChanged() {
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mTransports.size(); i++) {
            AssociationInfo associationById = this.mAssociationStore.getAssociationById(this.mTransports.keyAt(i));
            if (associationById != null) {
                arrayList.add(associationById);
            }
        }
        this.mTransportsListeners.broadcast(new Consumer() { // from class: com.android.server.companion.transport.CompanionTransportManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CompanionTransportManager.lambda$notifyOnTransportsChanged$1(arrayList, (IOnTransportsChangedListener) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$notifyOnTransportsChanged$1(List list, IOnTransportsChangedListener iOnTransportsChangedListener) {
        try {
            iOnTransportsChangedListener.onTransportsChanged(list);
        } catch (RemoteException unused) {
        }
    }

    @GuardedBy({"mTransports"})
    public final void initializeTransport(int i, ParcelFileDescriptor parcelFileDescriptor) {
        Slog.i("CDM_CompanionTransportManager", "Initializing transport");
        if (!isSecureTransportEnabled()) {
            RawTransport rawTransport = new RawTransport(i, parcelFileDescriptor, this.mContext);
            addMessageListenersToTransport(rawTransport);
            rawTransport.start();
            this.mTransports.put(i, rawTransport);
            Slog.i("CDM_CompanionTransportManager", "RawTransport is created");
            return;
        }
        RawTransport rawTransport2 = new RawTransport(i, parcelFileDescriptor, this.mContext);
        this.mTempTransport = rawTransport2;
        addMessageListenersToTransport(rawTransport2);
        this.mTempTransport.addListener(1669361779, new IOnMessageReceivedListener() { // from class: com.android.server.companion.transport.CompanionTransportManager.1
            public IBinder asBinder() {
                return null;
            }

            public void onMessageReceived(int i2, byte[] bArr) throws RemoteException {
                synchronized (CompanionTransportManager.this.mTransports) {
                    CompanionTransportManager.this.onPlatformInfoReceived(i2, bArr);
                }
            }
        });
        this.mTempTransport.start();
        int i2 = Build.VERSION.SDK_INT;
        String str = Build.VERSION.RELEASE;
        try {
            this.mTempTransport.sendMessage(1669361779, ByteBuffer.allocate(str.getBytes().length + 8).putInt(i2).putInt(str.getBytes().length).put(str.getBytes()).array());
        } catch (IOException unused) {
            Slog.e("CDM_CompanionTransportManager", "Failed to exchange platform info");
        }
    }

    @GuardedBy({"CompanionTransportManager.this.mTransports"})
    public final void onPlatformInfoReceived(int i, byte[] bArr) {
        SecureTransport secureTransport;
        if (this.mTempTransport.getAssociationId() != i) {
            return;
        }
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        int i2 = wrap.getInt();
        byte[] bArr2 = new byte[wrap.getInt()];
        wrap.get(bArr2);
        Slog.i("CDM_CompanionTransportManager", "Remote device SDK: " + i2 + ", release:" + new String(bArr2));
        Transport transport = this.mTempTransport;
        this.mTempTransport = null;
        String str = Build.VERSION.RELEASE;
        if (i2 == -1) {
            secureTransport = new SecureTransport(transport.getAssociationId(), transport.getFd(), this.mContext, null, null);
        } else {
            if (i2 >= 10000) {
                Slog.i("CDM_CompanionTransportManager", "Creating a secure channel");
                secureTransport = new SecureTransport(transport.getAssociationId(), transport.getFd(), this.mContext);
                addMessageListenersToTransport(secureTransport);
                secureTransport.start();
            }
            this.mTransports.put(transport.getAssociationId(), transport);
        }
        transport = secureTransport;
        this.mTransports.put(transport.getAssociationId(), transport);
    }

    public Future<?> requestPermissionRestore(int i, byte[] bArr) {
        synchronized (this.mTransports) {
            Transport transport = this.mTransports.get(i);
            if (transport == null) {
                return CompletableFuture.failedFuture(new IOException("Missing transport"));
            }
            return transport.requestForResponse(1669491075, bArr);
        }
    }

    public void enableSecureTransport(boolean z) {
        this.mSecureTransportEnabled = z;
    }

    public final boolean isSecureTransportEnabled() {
        return !Build.IS_DEBUGGABLE || this.mSecureTransportEnabled;
    }

    public final void addMessageListenersToTransport(Transport transport) {
        for (int i = 0; i < this.mMessageListeners.size(); i++) {
            transport.addListener(this.mMessageListeners.keyAt(i), this.mMessageListeners.valueAt(i));
        }
    }
}
