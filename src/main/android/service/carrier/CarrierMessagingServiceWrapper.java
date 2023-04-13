package android.service.carrier;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.service.carrier.CarrierMessagingServiceWrapper;
import android.service.carrier.ICarrierMessagingCallback;
import android.service.carrier.ICarrierMessagingService;
import com.android.internal.util.Preconditions;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public final class CarrierMessagingServiceWrapper implements AutoCloseable {
    private volatile CarrierMessagingServiceConnection mCarrierMessagingServiceConnection;
    private Context mContext;
    private volatile ICarrierMessagingService mICarrierMessagingService;
    private Runnable mOnServiceReadyCallback;
    private Executor mServiceReadyCallbackExecutor;

    @SystemApi
    public boolean bindToCarrierMessagingService(Context context, String carrierPackageName, Executor executor, Runnable onServiceReadyCallback) {
        Preconditions.checkState(this.mCarrierMessagingServiceConnection == null);
        Objects.requireNonNull(context);
        Objects.requireNonNull(carrierPackageName);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(onServiceReadyCallback);
        Intent intent = new Intent(CarrierMessagingService.SERVICE_INTERFACE);
        intent.setPackage(carrierPackageName);
        this.mCarrierMessagingServiceConnection = new CarrierMessagingServiceConnection();
        this.mOnServiceReadyCallback = onServiceReadyCallback;
        this.mServiceReadyCallbackExecutor = executor;
        this.mContext = context;
        return context.bindService(intent, this.mCarrierMessagingServiceConnection, 1);
    }

    @SystemApi
    public void disconnect() {
        Preconditions.checkNotNull(this.mCarrierMessagingServiceConnection);
        this.mContext.unbindService(this.mCarrierMessagingServiceConnection);
        this.mCarrierMessagingServiceConnection = null;
        this.mOnServiceReadyCallback = null;
        this.mServiceReadyCallbackExecutor = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onServiceReady(ICarrierMessagingService carrierMessagingService) {
        this.mICarrierMessagingService = carrierMessagingService;
        if (this.mOnServiceReadyCallback != null && this.mServiceReadyCallbackExecutor != null) {
            long identity = Binder.clearCallingIdentity();
            try {
                this.mServiceReadyCallbackExecutor.execute(this.mOnServiceReadyCallback);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    @SystemApi
    public void receiveSms(MessagePdu pdu, String format, int destPort, int subId, Executor executor, CarrierMessagingCallback callback) {
        if (this.mICarrierMessagingService != null) {
            try {
                this.mICarrierMessagingService.filterSms(pdu, format, destPort, subId, new CarrierMessagingCallbackInternal(callback, executor));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SystemApi
    public void sendTextSms(String text, int subId, String destAddress, int sendSmsFlag, Executor executor, CarrierMessagingCallback callback) {
        Objects.requireNonNull(this.mICarrierMessagingService);
        try {
            this.mICarrierMessagingService.sendTextSms(text, subId, destAddress, sendSmsFlag, new CarrierMessagingCallbackInternal(callback, executor));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public void sendDataSms(byte[] data, int subId, String destAddress, int destPort, int sendSmsFlag, Executor executor, CarrierMessagingCallback callback) {
        Objects.requireNonNull(this.mICarrierMessagingService);
        try {
            this.mICarrierMessagingService.sendDataSms(data, subId, destAddress, destPort, sendSmsFlag, new CarrierMessagingCallbackInternal(callback, executor));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public void sendMultipartTextSms(List<String> parts, int subId, String destAddress, int sendSmsFlag, Executor executor, CarrierMessagingCallback callback) {
        Objects.requireNonNull(this.mICarrierMessagingService);
        try {
            this.mICarrierMessagingService.sendMultipartTextSms(parts, subId, destAddress, sendSmsFlag, new CarrierMessagingCallbackInternal(callback, executor));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public void sendMms(Uri pduUri, int subId, Uri location, Executor executor, CarrierMessagingCallback callback) {
        Objects.requireNonNull(this.mICarrierMessagingService);
        try {
            this.mICarrierMessagingService.sendMms(pduUri, subId, location, new CarrierMessagingCallbackInternal(callback, executor));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public void downloadMms(Uri pduUri, int subId, Uri location, Executor executor, CarrierMessagingCallback callback) {
        Objects.requireNonNull(this.mICarrierMessagingService);
        try {
            this.mICarrierMessagingService.downloadMms(pduUri, subId, location, new CarrierMessagingCallbackInternal(callback, executor));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        disconnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class CarrierMessagingServiceConnection implements ServiceConnection {
        private CarrierMessagingServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            CarrierMessagingServiceWrapper.this.onServiceReady(ICarrierMessagingService.Stub.asInterface(service));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public interface CarrierMessagingCallback {
        default void onReceiveSmsComplete(int result) {
        }

        default void onSendSmsComplete(int result, int messageRef) {
        }

        default void onSendMultipartSmsComplete(int result, int[] messageRefs) {
        }

        default void onSendMmsComplete(int result, byte[] sendConfPdu) {
        }

        default void onDownloadMmsComplete(int result) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class CarrierMessagingCallbackInternal extends ICarrierMessagingCallback.Stub {
        final CarrierMessagingCallback mCarrierMessagingCallback;
        final Executor mExecutor;

        CarrierMessagingCallbackInternal(CarrierMessagingCallback callback, Executor executor) {
            this.mCarrierMessagingCallback = callback;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFilterComplete$0(int result) {
            this.mCarrierMessagingCallback.onReceiveSmsComplete(result);
        }

        @Override // android.service.carrier.ICarrierMessagingCallback
        public void onFilterComplete(final int result) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.service.carrier.CarrierMessagingServiceWrapper$CarrierMessagingCallbackInternal$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CarrierMessagingServiceWrapper.CarrierMessagingCallbackInternal.this.lambda$onFilterComplete$0(result);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSendSmsComplete$1(int result, int messageRef) {
            this.mCarrierMessagingCallback.onSendSmsComplete(result, messageRef);
        }

        @Override // android.service.carrier.ICarrierMessagingCallback
        public void onSendSmsComplete(final int result, final int messageRef) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.service.carrier.CarrierMessagingServiceWrapper$CarrierMessagingCallbackInternal$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CarrierMessagingServiceWrapper.CarrierMessagingCallbackInternal.this.lambda$onSendSmsComplete$1(result, messageRef);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSendMultipartSmsComplete$2(int result, int[] messageRefs) {
            this.mCarrierMessagingCallback.onSendMultipartSmsComplete(result, messageRefs);
        }

        @Override // android.service.carrier.ICarrierMessagingCallback
        public void onSendMultipartSmsComplete(final int result, final int[] messageRefs) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.service.carrier.CarrierMessagingServiceWrapper$CarrierMessagingCallbackInternal$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    CarrierMessagingServiceWrapper.CarrierMessagingCallbackInternal.this.lambda$onSendMultipartSmsComplete$2(result, messageRefs);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSendMmsComplete$3(int result, byte[] sendConfPdu) {
            this.mCarrierMessagingCallback.onSendMmsComplete(result, sendConfPdu);
        }

        @Override // android.service.carrier.ICarrierMessagingCallback
        public void onSendMmsComplete(final int result, final byte[] sendConfPdu) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.service.carrier.CarrierMessagingServiceWrapper$CarrierMessagingCallbackInternal$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    CarrierMessagingServiceWrapper.CarrierMessagingCallbackInternal.this.lambda$onSendMmsComplete$3(result, sendConfPdu);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDownloadMmsComplete$4(int result) {
            this.mCarrierMessagingCallback.onDownloadMmsComplete(result);
        }

        @Override // android.service.carrier.ICarrierMessagingCallback
        public void onDownloadMmsComplete(final int result) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.service.carrier.CarrierMessagingServiceWrapper$CarrierMessagingCallbackInternal$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    CarrierMessagingServiceWrapper.CarrierMessagingCallbackInternal.this.lambda$onDownloadMmsComplete$4(result);
                }
            });
        }
    }
}
