package android.telephony.satellite;

import android.content.Context;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.ICancellationSignal;
import android.p008os.OutcomeReceiver;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.satellite.SatelliteManager;
import com.android.internal.telephony.IIntegerConsumer;
import com.android.internal.telephony.ITelephony;
import com.android.internal.util.FunctionalUtils;
import com.android.telephony.Rlog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class SatelliteManager {
    public static final int DATAGRAM_TYPE_LOCATION_SHARING = 2;
    public static final int DATAGRAM_TYPE_SOS_MESSAGE = 1;
    public static final int DATAGRAM_TYPE_UNKNOWN = 0;
    public static final String KEY_DEMO_MODE_ENABLED = "demo_mode_enabled";
    public static final String KEY_MAX_CHARACTERS_PER_SATELLITE_TEXT = "max_characters_per_satellite_text";
    public static final String KEY_SATELLITE_CAPABILITIES = "satellite_capabilities";
    public static final String KEY_SATELLITE_COMMUNICATION_ALLOWED = "satellite_communication_allowed";
    public static final String KEY_SATELLITE_ENABLED = "satellite_enabled";
    public static final String KEY_SATELLITE_NEXT_VISIBILITY = "satellite_next_visibility";
    public static final String KEY_SATELLITE_PROVISIONED = "satellite_provisioned";
    public static final String KEY_SATELLITE_SUPPORTED = "satellite_supported";
    public static final int NT_RADIO_TECHNOLOGY_EMTC_NTN = 2;
    public static final int NT_RADIO_TECHNOLOGY_NB_IOT_NTN = 0;
    public static final int NT_RADIO_TECHNOLOGY_NR_NTN = 1;
    public static final int NT_RADIO_TECHNOLOGY_PROPRIETARY = 3;
    public static final int NT_RADIO_TECHNOLOGY_UNKNOWN = -1;
    public static final int SATELLITE_ACCESS_BARRED = 16;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_IDLE = 0;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_RECEIVE_FAILED = 7;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_RECEIVE_NONE = 6;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_RECEIVE_SUCCESS = 5;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_RECEIVING = 4;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_SENDING = 1;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_SEND_FAILED = 3;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_SEND_SUCCESS = 2;
    public static final int SATELLITE_DATAGRAM_TRANSFER_STATE_UNKNOWN = -1;
    public static final int SATELLITE_ERROR = 1;
    public static final int SATELLITE_ERROR_NONE = 0;
    public static final int SATELLITE_INVALID_ARGUMENTS = 8;
    public static final int SATELLITE_INVALID_MODEM_STATE = 7;
    public static final int SATELLITE_INVALID_TELEPHONY_STATE = 6;
    public static final int SATELLITE_MODEM_ERROR = 4;
    public static final int SATELLITE_MODEM_STATE_DATAGRAM_RETRYING = 3;
    public static final int SATELLITE_MODEM_STATE_DATAGRAM_TRANSFERRING = 2;
    public static final int SATELLITE_MODEM_STATE_IDLE = 0;
    public static final int SATELLITE_MODEM_STATE_LISTENING = 1;
    public static final int SATELLITE_MODEM_STATE_OFF = 4;
    public static final int SATELLITE_MODEM_STATE_UNAVAILABLE = 5;
    public static final int SATELLITE_MODEM_STATE_UNKNOWN = -1;
    public static final int SATELLITE_NETWORK_ERROR = 5;
    public static final int SATELLITE_NETWORK_TIMEOUT = 17;
    public static final int SATELLITE_NOT_AUTHORIZED = 19;
    public static final int SATELLITE_NOT_REACHABLE = 18;
    public static final int SATELLITE_NOT_SUPPORTED = 20;
    public static final int SATELLITE_NO_RESOURCES = 12;
    public static final int SATELLITE_RADIO_NOT_AVAILABLE = 10;
    public static final int SATELLITE_REQUEST_ABORTED = 15;
    public static final int SATELLITE_REQUEST_FAILED = 9;
    public static final int SATELLITE_REQUEST_NOT_SUPPORTED = 11;
    public static final int SATELLITE_SERVER_ERROR = 2;
    public static final int SATELLITE_SERVICE_ERROR = 3;
    public static final int SATELLITE_SERVICE_NOT_PROVISIONED = 13;
    public static final int SATELLITE_SERVICE_PROVISION_IN_PROGRESS = 14;
    private static final String TAG = "SatelliteManager";
    private final Context mContext;
    private final int mSubId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DatagramType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface NTRadioTechnology {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SatelliteDatagramTransferState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SatelliteError {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SatelliteModemState {
    }

    public SatelliteManager(Context context) {
        this(context, Integer.MAX_VALUE);
    }

    private SatelliteManager(Context context, int subId) {
        this.mContext = context;
        this.mSubId = subId;
    }

    /* loaded from: classes3.dex */
    public static class SatelliteException extends Exception {
        private final int mErrorCode;

        public SatelliteException(int errorCode) {
            this.mErrorCode = errorCode;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }
    }

    public void requestSatelliteEnabled(boolean enable, Executor executor, Consumer<Integer> errorCodeListener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(errorCodeListener);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                IIntegerConsumer errorCallback = new BinderC33541(executor, errorCodeListener);
                telephony.requestSatelliteEnabled(this.mSubId, enable, errorCallback);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            Rlog.m7e(TAG, "requestSatelliteEnabled() RemoteException: ", ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$1 */
    /* loaded from: classes3.dex */
    class BinderC33541 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$errorCodeListener;
        final /* synthetic */ Executor val$executor;

        BinderC33541(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$errorCodeListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$errorCodeListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$1$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestIsSatelliteEnabled(Executor executor, OutcomeReceiver<Boolean, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC33622(null, executor, callback);
                telephony.requestIsSatelliteEnabled(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestIsSatelliteEnabled() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$2 */
    /* loaded from: classes3.dex */
    class ResultReceiverC33622 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC33622(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey("satellite_enabled")) {
                    final boolean isSatelliteEnabled = resultData.getBoolean("satellite_enabled");
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$2$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$2$$ExternalSyntheticLambda0
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Boolean.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_SATELLITE_ENABLED does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$2$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$2$$ExternalSyntheticLambda5
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$2$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$2$$ExternalSyntheticLambda4
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestSatelliteDemoModeEnabled(boolean enable, Executor executor, Consumer<Integer> errorCodeListener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(errorCodeListener);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                IIntegerConsumer errorCallback = new BinderC33633(executor, errorCodeListener);
                telephony.requestSatelliteDemoModeEnabled(this.mSubId, enable, errorCallback);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            Rlog.m7e(TAG, "requestSatelliteDemoModeEnabled() RemoteException: ", ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$3 */
    /* loaded from: classes3.dex */
    class BinderC33633 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$errorCodeListener;
        final /* synthetic */ Executor val$executor;

        BinderC33633(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$errorCodeListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$errorCodeListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$3$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$3$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestIsSatelliteDemoModeEnabled(Executor executor, OutcomeReceiver<Boolean, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC33644(null, executor, callback);
                telephony.requestIsSatelliteDemoModeEnabled(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestIsSatelliteDemoModeEnabled() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$4 */
    /* loaded from: classes3.dex */
    class ResultReceiverC33644 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC33644(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_DEMO_MODE_ENABLED)) {
                    final boolean isDemoModeEnabled = resultData.getBoolean(SatelliteManager.KEY_DEMO_MODE_ENABLED);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$4$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$4$$ExternalSyntheticLambda5
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Boolean.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_DEMO_MODE_ENABLED does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$4$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$4$$ExternalSyntheticLambda0
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$4$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$4$$ExternalSyntheticLambda1
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestIsSatelliteSupported(Executor executor, OutcomeReceiver<Boolean, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC33655(null, executor, callback);
                telephony.requestIsSatelliteSupported(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestIsSatelliteSupported() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$5 */
    /* loaded from: classes3.dex */
    class ResultReceiverC33655 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC33655(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_SATELLITE_SUPPORTED)) {
                    final boolean isSatelliteSupported = resultData.getBoolean(SatelliteManager.KEY_SATELLITE_SUPPORTED);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$5$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$5$$ExternalSyntheticLambda0
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Boolean.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_SATELLITE_SUPPORTED does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$5$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$5$$ExternalSyntheticLambda5
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$5$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$5$$ExternalSyntheticLambda4
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestSatelliteCapabilities(Executor executor, OutcomeReceiver<SatelliteCapabilities, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC33666(null, executor, callback);
                telephony.requestSatelliteCapabilities(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestSatelliteCapabilities() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$6 */
    /* loaded from: classes3.dex */
    class ResultReceiverC33666 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC33666(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_SATELLITE_CAPABILITIES)) {
                    final SatelliteCapabilities capabilities = (SatelliteCapabilities) resultData.getParcelable(SatelliteManager.KEY_SATELLITE_CAPABILITIES, SatelliteCapabilities.class);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$6$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$6$$ExternalSyntheticLambda3
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(r2);
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_SATELLITE_CAPABILITIES does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$6$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$6$$ExternalSyntheticLambda4
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$6$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$6$$ExternalSyntheticLambda5
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public void startSatellitePositionUpdates(Executor executor, Consumer<Integer> errorCodeListener, SatellitePositionUpdateCallback callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(errorCodeListener);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                callback.setExecutor(executor);
                IIntegerConsumer errorCallback = new BinderC33677(executor, errorCodeListener);
                telephony.startSatellitePositionUpdates(this.mSubId, errorCallback, callback.getBinder());
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("startSatellitePositionUpdates() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$7 */
    /* loaded from: classes3.dex */
    class BinderC33677 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$errorCodeListener;
        final /* synthetic */ Executor val$executor;

        BinderC33677(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$errorCodeListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$errorCodeListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$7$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$7$$ExternalSyntheticLambda1
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void stopSatellitePositionUpdates(SatellitePositionUpdateCallback callback, Executor executor, Consumer<Integer> errorCodeListener) {
        Objects.requireNonNull(callback);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(errorCodeListener);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                IIntegerConsumer errorCallback = new BinderC33688(executor, errorCodeListener);
                telephony.stopSatellitePositionUpdates(this.mSubId, errorCallback, callback.getBinder());
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("stopSatellitePositionUpdates() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$8 */
    /* loaded from: classes3.dex */
    class BinderC33688 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$errorCodeListener;
        final /* synthetic */ Executor val$executor;

        BinderC33688(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$errorCodeListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$errorCodeListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$8$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$8$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestMaxSizePerSendingDatagram(Executor executor, OutcomeReceiver<Integer, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC33699(null, executor, callback);
                telephony.requestMaxSizePerSendingDatagram(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestMaxCharactersPerSatelliteTextMessage() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$9 */
    /* loaded from: classes3.dex */
    class ResultReceiverC33699 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC33699(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_MAX_CHARACTERS_PER_SATELLITE_TEXT)) {
                    final int maxCharacters = resultData.getInt(SatelliteManager.KEY_MAX_CHARACTERS_PER_SATELLITE_TEXT);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$9$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$9$$ExternalSyntheticLambda4
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Integer.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_MAX_CHARACTERS_PER_SATELLITE_TEXT does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$9$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$9$$ExternalSyntheticLambda3
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$9$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$9$$ExternalSyntheticLambda5
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public void provisionSatelliteService(String token, CancellationSignal cancellationSignal, Executor executor, Consumer<Integer> errorCodeListener) {
        ITelephony telephony;
        Objects.requireNonNull(token);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(errorCodeListener);
        ICancellationSignal cancelRemote = null;
        try {
            telephony = getITelephony();
        } catch (RemoteException ex) {
            loge("provisionSatelliteService() RemoteException=" + ex);
            ex.rethrowFromSystemServer();
        }
        if (telephony != null) {
            IIntegerConsumer errorCallback = new BinderC335510(executor, errorCodeListener);
            cancelRemote = telephony.provisionSatelliteService(this.mSubId, token, errorCallback);
            if (cancellationSignal != null) {
                cancellationSignal.setRemote(cancelRemote);
                return;
            }
            return;
        }
        throw new IllegalStateException("telephony service is null.");
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$10 */
    /* loaded from: classes3.dex */
    class BinderC335510 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$errorCodeListener;
        final /* synthetic */ Executor val$executor;

        BinderC335510(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$errorCodeListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$errorCodeListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$10$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$10$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void deprovisionSatelliteService(String token, Executor executor, Consumer<Integer> errorCodeListener) {
        Objects.requireNonNull(token);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(errorCodeListener);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                IIntegerConsumer errorCallback = new BinderC335611(executor, errorCodeListener);
                telephony.deprovisionSatelliteService(this.mSubId, token, errorCallback);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("deprovisionSatelliteService() RemoteException=" + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$11 */
    /* loaded from: classes3.dex */
    class BinderC335611 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$errorCodeListener;
        final /* synthetic */ Executor val$executor;

        BinderC335611(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$errorCodeListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$errorCodeListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$11$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$11$$ExternalSyntheticLambda1
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public int registerForSatelliteProvisionStateChanged(Executor executor, SatelliteProvisionStateCallback callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                callback.setExecutor(executor);
                return telephony.registerForSatelliteProvisionStateChanged(this.mSubId, callback.getBinder());
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("registerForSatelliteProvisionStateChanged() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
            return 9;
        }
    }

    public void unregisterForSatelliteProvisionStateChanged(SatelliteProvisionStateCallback callback) {
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.unregisterForSatelliteProvisionStateChanged(this.mSubId, callback.getBinder());
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("unregisterForSatelliteProvisionStateChanged() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    public void requestIsSatelliteProvisioned(Executor executor, OutcomeReceiver<Boolean, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC335712(null, executor, callback);
                telephony.requestIsSatelliteProvisioned(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestIsSatelliteProvisioned() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$12 */
    /* loaded from: classes3.dex */
    class ResultReceiverC335712 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC335712(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_SATELLITE_PROVISIONED)) {
                    final boolean isSatelliteProvisioned = resultData.getBoolean(SatelliteManager.KEY_SATELLITE_PROVISIONED);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$12$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$12$$ExternalSyntheticLambda5
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Boolean.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_SATELLITE_PROVISIONED does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$12$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$12$$ExternalSyntheticLambda0
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$12$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$12$$ExternalSyntheticLambda4
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public int registerForSatelliteModemStateChanged(Executor executor, SatelliteStateCallback callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                callback.setExecutor(executor);
                return telephony.registerForSatelliteModemStateChanged(this.mSubId, callback.getBinder());
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("registerForSatelliteModemStateChanged() RemoteException:" + ex);
            ex.rethrowFromSystemServer();
            return 9;
        }
    }

    public void unregisterForSatelliteModemStateChanged(SatelliteStateCallback callback) {
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.unregisterForSatelliteModemStateChanged(this.mSubId, callback.getBinder());
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("unregisterForSatelliteModemStateChanged() RemoteException:" + ex);
            ex.rethrowFromSystemServer();
        }
    }

    public int registerForSatelliteDatagram(int datagramType, Executor executor, SatelliteDatagramCallback callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                callback.setExecutor(executor);
                return telephony.registerForSatelliteDatagram(this.mSubId, datagramType, callback.getBinder());
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("registerForSatelliteDatagram() RemoteException:" + ex);
            ex.rethrowFromSystemServer();
            return 9;
        }
    }

    public void unregisterForSatelliteDatagram(SatelliteDatagramCallback callback) {
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.unregisterForSatelliteDatagram(this.mSubId, callback.getBinder());
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("unregisterForSatelliteDatagram() RemoteException:" + ex);
            ex.rethrowFromSystemServer();
        }
    }

    public void pollPendingSatelliteDatagrams(Executor executor, Consumer<Integer> resultListener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(resultListener);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                IIntegerConsumer internalCallback = new BinderC335813(executor, resultListener);
                telephony.pollPendingSatelliteDatagrams(this.mSubId, internalCallback);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("pollPendingSatelliteDatagrams() RemoteException:" + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$13 */
    /* loaded from: classes3.dex */
    class BinderC335813 extends IIntegerConsumer.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ Consumer val$resultListener;

        BinderC335813(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$resultListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$resultListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$13$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$13$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void sendSatelliteDatagram(int datagramType, SatelliteDatagram datagram, boolean needFullScreenPointingUI, Executor executor, Consumer<Integer> resultListener) {
        Objects.requireNonNull(datagram);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(resultListener);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                IIntegerConsumer internalCallback = new BinderC335914(executor, resultListener);
                telephony.sendSatelliteDatagram(this.mSubId, datagramType, datagram, needFullScreenPointingUI, internalCallback);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("sendSatelliteDatagram() RemoteException:" + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$14 */
    /* loaded from: classes3.dex */
    class BinderC335914 extends IIntegerConsumer.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ Consumer val$resultListener;

        BinderC335914(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$resultListener = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            Executor executor = this.val$executor;
            final Consumer consumer = this.val$resultListener;
            executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$14$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$14$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            r1.accept(Integer.valueOf(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestIsSatelliteCommunicationAllowedForCurrentLocation(Executor executor, OutcomeReceiver<Boolean, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC336015(null, executor, callback);
                telephony.requestIsSatelliteCommunicationAllowedForCurrentLocation(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestIsSatelliteCommunicationAllowedForCurrentLocation() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$15 */
    /* loaded from: classes3.dex */
    class ResultReceiverC336015 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC336015(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_SATELLITE_COMMUNICATION_ALLOWED)) {
                    final boolean isSatelliteCommunicationAllowed = resultData.getBoolean(SatelliteManager.KEY_SATELLITE_COMMUNICATION_ALLOWED);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$15$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$15$$ExternalSyntheticLambda1
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Boolean.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_SATELLITE_COMMUNICATION_ALLOWED does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$15$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$15$$ExternalSyntheticLambda0
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$15$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$15$$ExternalSyntheticLambda5
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    public void requestTimeForNextSatelliteVisibility(Executor executor, OutcomeReceiver<Integer, SatelliteException> callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                ResultReceiver receiver = new ResultReceiverC336116(null, executor, callback);
                telephony.requestTimeForNextSatelliteVisibility(this.mSubId, receiver);
                return;
            }
            throw new IllegalStateException("telephony service is null.");
        } catch (RemoteException ex) {
            loge("requestTimeForNextSatelliteVisibility() RemoteException: " + ex);
            ex.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.telephony.satellite.SatelliteManager$16 */
    /* loaded from: classes3.dex */
    class ResultReceiverC336116 extends ResultReceiver {
        final /* synthetic */ OutcomeReceiver val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ResultReceiverC336116(Handler handler, Executor executor, OutcomeReceiver outcomeReceiver) {
            super(handler);
            this.val$executor = executor;
            this.val$callback = outcomeReceiver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(final int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                if (resultData.containsKey(SatelliteManager.KEY_SATELLITE_NEXT_VISIBILITY)) {
                    final int nextVisibilityDuration = resultData.getInt(SatelliteManager.KEY_SATELLITE_NEXT_VISIBILITY);
                    Executor executor = this.val$executor;
                    final OutcomeReceiver outcomeReceiver = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$16$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$16$$ExternalSyntheticLambda4
                                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                                public final void runOrThrow() {
                                    OutcomeReceiver.this.onResult(Integer.valueOf(r2));
                                }
                            });
                        }
                    });
                    return;
                }
                SatelliteManager.loge("KEY_SATELLITE_NEXT_VISIBILITY does not exist.");
                Executor executor2 = this.val$executor;
                final OutcomeReceiver outcomeReceiver2 = this.val$callback;
                executor2.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$16$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$16$$ExternalSyntheticLambda5
                            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                            public final void runOrThrow() {
                                OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(9));
                            }
                        });
                    }
                });
                return;
            }
            Executor executor3 = this.val$executor;
            final OutcomeReceiver outcomeReceiver3 = this.val$callback;
            executor3.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteManager$16$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.satellite.SatelliteManager$16$$ExternalSyntheticLambda0
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            OutcomeReceiver.this.onError(new SatelliteManager.SatelliteException(r2));
                        }
                    });
                }
            });
        }
    }

    private static ITelephony getITelephony() {
        ITelephony binder = ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
        if (binder == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        return binder;
    }

    private static void logd(String log) {
        Rlog.m10d(TAG, log);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String log) {
        Rlog.m8e(TAG, log);
    }
}
