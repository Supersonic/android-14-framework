package com.android.internal.telephony.satellite;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.Rlog;
import android.telephony.satellite.SatelliteManager;
import android.telephony.satellite.stub.ISatellite;
import android.telephony.satellite.stub.ISatelliteCapabilitiesConsumer;
import android.telephony.satellite.stub.ISatelliteListener;
import android.telephony.satellite.stub.PointingInfo;
import android.telephony.satellite.stub.SatelliteCapabilities;
import android.telephony.satellite.stub.SatelliteDatagram;
import android.text.TextUtils;
import android.util.Pair;
import com.android.internal.telephony.ExponentialBackoff;
import com.android.internal.telephony.IBooleanConsumer;
import com.android.internal.telephony.IIntegerConsumer;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.satellite.SatelliteModemInterface;
import com.android.internal.util.FunctionalUtils;
import java.util.Arrays;
/* loaded from: classes.dex */
public class SatelliteModemInterface {
    private static SatelliteModemInterface sInstance;
    private final Context mContext;
    private final ExponentialBackoff mExponentialBackoff;
    private boolean mIsBinding;
    private boolean mIsBound;
    private ISatellite mSatelliteService;
    private SatelliteServiceConnection mSatelliteServiceConnection;
    private final Object mLock = new Object();
    private final boolean mIsSatelliteServiceSupported = false;
    private final RegistrantList mSatelliteProvisionStateChangedRegistrants = new RegistrantList();
    private final RegistrantList mSatellitePositionInfoChangedRegistrants = new RegistrantList();
    private final RegistrantList mDatagramTransferStateChangedRegistrants = new RegistrantList();
    private final RegistrantList mSatelliteModemStateChangedRegistrants = new RegistrantList();
    private final RegistrantList mPendingDatagramCountRegistrants = new RegistrantList();
    private final RegistrantList mSatelliteDatagramsReceivedRegistrants = new RegistrantList();
    private final RegistrantList mSatelliteRadioTechnologyChangedRegistrants = new RegistrantList();
    private final ISatelliteListener mListener = new ISatelliteListener.Stub() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface.1
        public void onSatelliteProvisionStateChanged(boolean z) {
            SatelliteModemInterface.this.mSatelliteProvisionStateChangedRegistrants.notifyResult(Boolean.valueOf(z));
        }

        public void onSatelliteDatagramReceived(SatelliteDatagram satelliteDatagram, int i) {
            SatelliteModemInterface.this.mSatelliteDatagramsReceivedRegistrants.notifyResult(new Pair(SatelliteServiceUtils.fromSatelliteDatagram(satelliteDatagram), Integer.valueOf(i)));
        }

        public void onPendingDatagramCount(int i) {
            SatelliteModemInterface.this.mPendingDatagramCountRegistrants.notifyResult(Integer.valueOf(i));
        }

        public void onSatellitePositionChanged(PointingInfo pointingInfo) {
            SatelliteModemInterface.this.mSatellitePositionInfoChangedRegistrants.notifyResult(SatelliteServiceUtils.fromPointingInfo(pointingInfo));
        }

        public void onSatelliteModemStateChanged(int i) {
            int i2;
            SatelliteModemInterface.this.mSatelliteModemStateChangedRegistrants.notifyResult(Integer.valueOf(SatelliteServiceUtils.fromSatelliteModemState(i)));
            if (i != 0) {
                i2 = 1;
                if (i == 1) {
                    i2 = 4;
                } else if (i != 2) {
                    i2 = -1;
                }
            } else {
                i2 = 0;
            }
            SatelliteModemInterface.this.mDatagramTransferStateChangedRegistrants.notifyResult(Integer.valueOf(i2));
        }

        public void onSatelliteRadioTechnologyChanged(int i) {
            SatelliteModemInterface.this.mSatelliteRadioTechnologyChangedRegistrants.notifyResult(Integer.valueOf(SatelliteServiceUtils.fromSatelliteRadioTechnology(i)));
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: -$$Nest$smsendMessageWithResult  reason: not valid java name */
    public static /* bridge */ /* synthetic */ void m1019$$Nest$smsendMessageWithResult(Message message, Object obj, int i) {
        sendMessageWithResult(message, obj, i);
    }

    public boolean isSatelliteServiceSupported() {
        return false;
    }

    public static SatelliteModemInterface getInstance() {
        if (sInstance == null) {
            loge("SatelliteModemInterface was not yet initialized.");
        }
        return sInstance;
    }

    public static SatelliteModemInterface make(Context context) {
        if (sInstance == null) {
            sInstance = new SatelliteModemInterface(context, Looper.getMainLooper());
        }
        return sInstance;
    }

    private SatelliteModemInterface(Context context, Looper looper) {
        this.mContext = context;
        ExponentialBackoff exponentialBackoff = new ExponentialBackoff(2000L, 64000L, 2, looper, new Runnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SatelliteModemInterface.this.lambda$new$0();
            }
        });
        this.mExponentialBackoff = exponentialBackoff;
        exponentialBackoff.start();
        logd("Created SatelliteModemInterface. Attempting to bind to SatelliteService.");
        bindService();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        synchronized (this.mLock) {
            if ((!this.mIsBound || this.mSatelliteService == null) && !this.mIsBinding) {
                if (this.mSatelliteServiceConnection != null) {
                    synchronized (this.mLock) {
                        this.mIsBound = false;
                        this.mIsBinding = false;
                    }
                    unbindService();
                }
                bindService();
            }
        }
    }

    public ISatellite getService() {
        return this.mSatelliteService;
    }

    private String getSatellitePackageName() {
        return TextUtils.emptyIfNull(this.mContext.getResources().getString(17039991));
    }

    private void bindService() {
        synchronized (this.mLock) {
            if (!this.mIsBinding && !this.mIsBound) {
                this.mIsBinding = true;
                String satellitePackageName = getSatellitePackageName();
                if (TextUtils.isEmpty(satellitePackageName)) {
                    loge("Unable to bind to the satellite service because the package is undefined.");
                    synchronized (this.mLock) {
                        this.mIsBinding = false;
                    }
                    this.mExponentialBackoff.stop();
                    return;
                }
                Intent intent = new Intent("android.telephony.satellite.SatelliteService");
                intent.setPackage(satellitePackageName);
                SatelliteServiceConnection satelliteServiceConnection = new SatelliteServiceConnection();
                this.mSatelliteServiceConnection = satelliteServiceConnection;
                try {
                    if (this.mContext.bindService(intent, satelliteServiceConnection, 1)) {
                        logd("Successfully bound to the satellite service.");
                    } else {
                        synchronized (this.mLock) {
                            this.mIsBinding = false;
                        }
                        this.mExponentialBackoff.notifyFailed();
                        loge("Error binding to the satellite service. Retrying in " + this.mExponentialBackoff.getCurrentDelay() + " ms.");
                    }
                } catch (Exception e) {
                    synchronized (this.mLock) {
                        this.mIsBinding = false;
                        this.mExponentialBackoff.notifyFailed();
                        loge("Exception binding to the satellite service. Retrying in " + this.mExponentialBackoff.getCurrentDelay() + " ms. Exception: " + e);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService() {
        disconnectSatelliteService();
        this.mContext.unbindService(this.mSatelliteServiceConnection);
        this.mSatelliteServiceConnection = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnectSatelliteService() {
        this.mSatelliteService = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SatelliteServiceConnection implements ServiceConnection {
        private SatelliteServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SatelliteModemInterface.logd("onServiceConnected: ComponentName=" + componentName);
            synchronized (SatelliteModemInterface.this.mLock) {
                SatelliteModemInterface.this.mIsBound = true;
                SatelliteModemInterface.this.mIsBinding = false;
            }
            SatelliteModemInterface.this.mSatelliteService = ISatellite.Stub.asInterface(iBinder);
            SatelliteModemInterface.this.mExponentialBackoff.stop();
            try {
                SatelliteModemInterface.this.mSatelliteService.setSatelliteListener(SatelliteModemInterface.this.mListener);
            } catch (RemoteException e) {
                SatelliteModemInterface.logd("setSatelliteListener: RemoteException " + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            SatelliteModemInterface.loge("onServiceDisconnected: Waiting for reconnect.");
            synchronized (SatelliteModemInterface.this.mLock) {
                SatelliteModemInterface.this.mIsBinding = false;
            }
            SatelliteModemInterface.this.disconnectSatelliteService();
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            SatelliteModemInterface.loge("onBindingDied: Unbinding and rebinding service.");
            synchronized (SatelliteModemInterface.this.mLock) {
                SatelliteModemInterface.this.mIsBound = false;
                SatelliteModemInterface.this.mIsBinding = false;
            }
            SatelliteModemInterface.this.unbindService();
            SatelliteModemInterface.this.mExponentialBackoff.start();
        }
    }

    public void registerForSatelliteProvisionStateChanged(Handler handler, int i, Object obj) {
        this.mSatelliteProvisionStateChangedRegistrants.add(handler, i, obj);
    }

    public void unregisterForSatelliteProvisionStateChanged(Handler handler) {
        this.mSatelliteProvisionStateChangedRegistrants.remove(handler);
    }

    public void registerForSatellitePositionInfoChanged(Handler handler, int i, Object obj) {
        this.mSatellitePositionInfoChangedRegistrants.add(handler, i, obj);
    }

    public void unregisterForSatellitePositionInfoChanged(Handler handler) {
        this.mSatellitePositionInfoChangedRegistrants.remove(handler);
    }

    public void registerForDatagramTransferStateChanged(Handler handler, int i, Object obj) {
        this.mDatagramTransferStateChangedRegistrants.add(handler, i, obj);
    }

    public void unregisterForDatagramTransferStateChanged(Handler handler) {
        this.mDatagramTransferStateChangedRegistrants.remove(handler);
    }

    public void registerForSatelliteModemStateChanged(Handler handler, int i, Object obj) {
        this.mSatelliteModemStateChangedRegistrants.add(handler, i, obj);
    }

    public void unregisterForSatelliteModemStateChanged(Handler handler) {
        this.mSatelliteModemStateChangedRegistrants.remove(handler);
    }

    public void registerForPendingDatagramCount(Handler handler, int i, Object obj) {
        this.mPendingDatagramCountRegistrants.add(handler, i, obj);
    }

    public void unregisterForPendingDatagramCount(Handler handler) {
        this.mPendingDatagramCountRegistrants.remove(handler);
    }

    public void registerForSatelliteDatagramsReceived(Handler handler, int i, Object obj) {
        this.mSatelliteDatagramsReceivedRegistrants.add(handler, i, obj);
    }

    public void unregisterForSatelliteDatagramsReceived(Handler handler) {
        this.mSatelliteDatagramsReceivedRegistrants.remove(handler);
    }

    public void registerForSatelliteRadioTechnologyChanged(Handler handler, int i, Object obj) {
        this.mSatelliteRadioTechnologyChangedRegistrants.add(handler, i, obj);
    }

    public void unregisterForSatelliteRadioTechnologyChanged(Handler handler) {
        this.mSatelliteRadioTechnologyChangedRegistrants.remove(handler);
    }

    public void requestSatelliteListeningEnabled(boolean z, int i, boolean z2, Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestSatelliteListeningEnabled(z, z2, i, new C03022(message));
                return;
            } catch (RemoteException e) {
                loge("requestSatelliteListeningEnabled: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestSatelliteListeningEnabled: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$2 */
    /* loaded from: classes.dex */
    class C03022 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03022(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestSatelliteListeningEnabled: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$2$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void requestSatelliteEnabled(boolean z, Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestSatelliteEnabled(z, new C03093(message));
                return;
            } catch (RemoteException e) {
                loge("setSatelliteEnabled: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("setSatelliteEnabled: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$3 */
    /* loaded from: classes.dex */
    class C03093 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03093(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("setSatelliteEnabled: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$3$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void requestIsSatelliteEnabled(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestIsSatelliteEnabled(new C03104(message), new C03115(message));
                return;
            } catch (RemoteException e) {
                loge("requestIsSatelliteEnabled: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestIsSatelliteEnabled: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$4 */
    /* loaded from: classes.dex */
    class C03104 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03104(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestIsSatelliteEnabled: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$4$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$5 */
    /* loaded from: classes.dex */
    class C03115 extends IBooleanConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03115(Message message) {
            this.val$message = message;
        }

        public void accept(boolean z) {
            final int[] iArr = {z ? 1 : 0};
            SatelliteModemInterface.logd("requestIsSatelliteEnabled: " + Arrays.toString(iArr));
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$5$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, iArr, 0);
                }
            });
        }
    }

    public void requestIsSatelliteSupported(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestIsSatelliteSupported(new C03126(message), new C03137(message));
                return;
            } catch (RemoteException e) {
                loge("requestIsSatelliteSupported: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestIsSatelliteSupported: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$6 */
    /* loaded from: classes.dex */
    class C03126 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03126(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestIsSatelliteSupported: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$6$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$7 */
    /* loaded from: classes.dex */
    class C03137 extends IBooleanConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03137(Message message) {
            this.val$message = message;
        }

        public void accept(final boolean z) {
            SatelliteModemInterface.logd("requestIsSatelliteSupported: " + z);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$7$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.C03137.lambda$accept$0(message, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$accept$0(Message message, boolean z) throws Exception {
            SatelliteModemInterface.sendMessageWithResult(message, Boolean.valueOf(z), 0);
        }
    }

    public void requestSatelliteCapabilities(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestSatelliteCapabilities(new C03148(message), new C03159(message));
                return;
            } catch (RemoteException e) {
                loge("requestSatelliteCapabilities: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestSatelliteCapabilities: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$8 */
    /* loaded from: classes.dex */
    class C03148 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03148(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestSatelliteCapabilities: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$8$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$9 */
    /* loaded from: classes.dex */
    class C03159 extends ISatelliteCapabilitiesConsumer.Stub {
        final /* synthetic */ Message val$message;

        C03159(Message message) {
            this.val$message = message;
        }

        public void accept(SatelliteCapabilities satelliteCapabilities) {
            final android.telephony.satellite.SatelliteCapabilities fromSatelliteCapabilities = SatelliteServiceUtils.fromSatelliteCapabilities(satelliteCapabilities);
            SatelliteModemInterface.logd("requestSatelliteCapabilities: " + fromSatelliteCapabilities);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$9$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, fromSatelliteCapabilities, 0);
                }
            });
        }
    }

    public void startSendingSatellitePointingInfo(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.startSendingSatellitePointingInfo(new C029210(message));
                return;
            } catch (RemoteException e) {
                loge("startSendingSatellitePointingInfo: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("startSendingSatellitePointingInfo: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$10 */
    /* loaded from: classes.dex */
    class C029210 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029210(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("startSendingSatellitePointingInfo: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$10$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void stopSendingSatellitePointingInfo(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.stopSendingSatellitePointingInfo(new C029311(message));
                return;
            } catch (RemoteException e) {
                loge("stopSendingSatellitePointingInfo: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("stopSendingSatellitePointingInfo: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$11 */
    /* loaded from: classes.dex */
    class C029311 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029311(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("stopSendingSatellitePointingInfo: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$11$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void requestMaxCharactersPerMOTextMessage(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestMaxCharactersPerMOTextMessage(new C029412(message), new C029513(message));
                return;
            } catch (RemoteException e) {
                loge("requestMaxCharactersPerMOTextMessage: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestMaxCharactersPerMOTextMessage: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$12 */
    /* loaded from: classes.dex */
    class C029412 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029412(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestMaxCharactersPerMOTextMessage: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$12$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$13 */
    /* loaded from: classes.dex */
    class C029513 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029513(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int[] iArr = {i};
            SatelliteModemInterface.logd("requestMaxCharactersPerMOTextMessage: " + Arrays.toString(iArr));
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$13$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, iArr, 0);
                }
            });
        }
    }

    public void provisionSatelliteService(String str, Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.provisionSatelliteService(str, new C029614(message));
                return;
            } catch (RemoteException e) {
                loge("provisionSatelliteService: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("provisionSatelliteService: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$14 */
    /* loaded from: classes.dex */
    class C029614 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029614(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("provisionSatelliteService: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$14$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void deprovisionSatelliteService(String str, Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.deprovisionSatelliteService(str, new C029715(message));
                return;
            } catch (RemoteException e) {
                loge("deprovisionSatelliteService: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("deprovisionSatelliteService: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$15 */
    /* loaded from: classes.dex */
    class C029715 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029715(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("deprovisionSatelliteService: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$15$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void requestIsSatelliteProvisioned(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestIsSatelliteProvisioned(new C029816(message), new C029917(message));
                return;
            } catch (RemoteException e) {
                loge("requestIsSatelliteProvisioned: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestIsSatelliteProvisioned: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$16 */
    /* loaded from: classes.dex */
    class C029816 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029816(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestIsSatelliteProvisioned: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$16$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$17 */
    /* loaded from: classes.dex */
    class C029917 extends IBooleanConsumer.Stub {
        final /* synthetic */ Message val$message;

        C029917(Message message) {
            this.val$message = message;
        }

        public void accept(boolean z) {
            final int[] iArr = {z ? 1 : 0};
            SatelliteModemInterface.logd("requestIsSatelliteProvisioned: " + Arrays.toString(iArr));
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$17$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, iArr, 0);
                }
            });
        }
    }

    public void pollPendingSatelliteDatagrams(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.pollPendingSatelliteDatagrams(new C030018(message));
                return;
            } catch (RemoteException e) {
                loge("pollPendingSatelliteDatagrams: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("pollPendingSatelliteDatagrams: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$18 */
    /* loaded from: classes.dex */
    class C030018 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030018(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("pollPendingSatelliteDatagrams: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$18$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void sendSatelliteDatagram(android.telephony.satellite.SatelliteDatagram satelliteDatagram, boolean z, boolean z2, boolean z3, Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.sendSatelliteDatagram(SatelliteServiceUtils.toSatelliteDatagram(satelliteDatagram), z3, z, new C030119(message));
                return;
            } catch (RemoteException e) {
                loge("sendSatelliteDatagram: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("sendSatelliteDatagram: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$19 */
    /* loaded from: classes.dex */
    class C030119 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030119(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("sendSatelliteDatagram: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$19$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    public void requestSatelliteModemState(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestSatelliteModemState(new C030320(message), new C030421(message));
                return;
            } catch (RemoteException e) {
                loge("requestSatelliteModemState: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestSatelliteModemState: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$20 */
    /* loaded from: classes.dex */
    class C030320 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030320(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestSatelliteModemState: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$20$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$21 */
    /* loaded from: classes.dex */
    class C030421 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030421(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteModemState = SatelliteServiceUtils.fromSatelliteModemState(i);
            SatelliteModemInterface.logd("requestSatelliteModemState: " + fromSatelliteModemState);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$21$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.C030421.lambda$accept$0(message, fromSatelliteModemState);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$accept$0(Message message, int i) throws Exception {
            SatelliteModemInterface.sendMessageWithResult(message, Integer.valueOf(i), 0);
        }
    }

    public void requestIsSatelliteCommunicationAllowedForCurrentLocation(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestIsSatelliteCommunicationAllowedForCurrentLocation(new C030522(message), new C030623(message));
                return;
            } catch (RemoteException e) {
                loge("requestIsSatelliteCommunicationAllowedForCurrentLocation: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestIsSatelliteCommunicationAllowedForCurrentLocation: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$22 */
    /* loaded from: classes.dex */
    class C030522 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030522(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestIsSatelliteCommunicationAllowedForCurrentLocation: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$22$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$23 */
    /* loaded from: classes.dex */
    class C030623 extends IBooleanConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030623(Message message) {
            this.val$message = message;
        }

        public void accept(final boolean z) {
            SatelliteModemInterface.logd("requestIsSatelliteCommunicationAllowedForCurrentLocation: " + z);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$23$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.C030623.lambda$accept$0(message, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$accept$0(Message message, boolean z) throws Exception {
            SatelliteModemInterface.sendMessageWithResult(message, Boolean.valueOf(z), 0);
        }
    }

    public void requestTimeForNextSatelliteVisibility(Message message) {
        ISatellite iSatellite = this.mSatelliteService;
        if (iSatellite != null) {
            try {
                iSatellite.requestTimeForNextSatelliteVisibility(new C030724(message), new C030825(message));
                return;
            } catch (RemoteException e) {
                loge("requestTimeForNextSatelliteVisibility: RemoteException " + e);
                sendMessageWithResult(message, null, 3);
                return;
            }
        }
        loge("requestTimeForNextSatelliteVisibility: Satellite service is unavailable.");
        sendMessageWithResult(message, null, 11);
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$24 */
    /* loaded from: classes.dex */
    class C030724 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030724(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int fromSatelliteError = SatelliteServiceUtils.fromSatelliteError(i);
            SatelliteModemInterface.logd("requestTimeForNextSatelliteVisibility: " + fromSatelliteError);
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$24$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, null, fromSatelliteError);
                }
            });
        }
    }

    /* renamed from: com.android.internal.telephony.satellite.SatelliteModemInterface$25 */
    /* loaded from: classes.dex */
    class C030825 extends IIntegerConsumer.Stub {
        final /* synthetic */ Message val$message;

        C030825(Message message) {
            this.val$message = message;
        }

        public void accept(int i) {
            final int[] iArr = {i};
            SatelliteModemInterface.logd("requestTimeForNextSatelliteVisibility: " + Arrays.toString(iArr));
            final Message message = this.val$message;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.internal.telephony.satellite.SatelliteModemInterface$25$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    SatelliteModemInterface.m1019$$Nest$smsendMessageWithResult(message, iArr, 0);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendMessageWithResult(Message message, Object obj, int i) {
        AsyncResult.forMessage(message, obj, i == 0 ? null : new SatelliteManager.SatelliteException(i));
        message.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d("SatelliteModemInterface", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e("SatelliteModemInterface", str);
    }
}
