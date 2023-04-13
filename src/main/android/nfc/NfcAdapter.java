package android.nfc;

import android.annotation.SystemApi;
import android.app.Activity;
import android.app.ActivityThread;
import android.app.OnActivityPausedListener;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.p001pm.PackageManager;
import android.net.Uri;
import android.nfc.INfcAdapter;
import android.nfc.INfcUnlockHandler;
import android.nfc.ITagRemovedCallback;
import android.nfc.NfcServiceManager;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public final class NfcAdapter {
    public static final String ACTION_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED";
    public static final String ACTION_HANDOVER_TRANSFER_DONE = "android.nfc.action.HANDOVER_TRANSFER_DONE";
    public static final String ACTION_HANDOVER_TRANSFER_STARTED = "android.nfc.action.HANDOVER_TRANSFER_STARTED";
    public static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED";
    public static final String ACTION_PREFERRED_PAYMENT_CHANGED = "android.nfc.action.PREFERRED_PAYMENT_CHANGED";
    public static final String ACTION_REQUIRE_UNLOCK_FOR_NFC = "android.nfc.action.REQUIRE_UNLOCK_FOR_NFC";
    public static final String ACTION_TAG_DISCOVERED = "android.nfc.action.TAG_DISCOVERED";
    public static final String ACTION_TAG_LEFT_FIELD = "android.nfc.action.TAG_LOST";
    public static final String ACTION_TECH_DISCOVERED = "android.nfc.action.TECH_DISCOVERED";
    public static final String ACTION_TRANSACTION_DETECTED = "android.nfc.action.TRANSACTION_DETECTED";
    public static final String EXTRA_ADAPTER_STATE = "android.nfc.extra.ADAPTER_STATE";
    public static final String EXTRA_AID = "android.nfc.extra.AID";
    public static final String EXTRA_DATA = "android.nfc.extra.DATA";
    public static final String EXTRA_HANDOVER_TRANSFER_STATUS = "android.nfc.extra.HANDOVER_TRANSFER_STATUS";
    public static final String EXTRA_HANDOVER_TRANSFER_URI = "android.nfc.extra.HANDOVER_TRANSFER_URI";
    public static final String EXTRA_ID = "android.nfc.extra.ID";
    public static final String EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES";
    public static final String EXTRA_PREFERRED_PAYMENT_CHANGED_REASON = "android.nfc.extra.PREFERRED_PAYMENT_CHANGED_REASON";
    public static final String EXTRA_READER_PRESENCE_CHECK_DELAY = "presence";
    public static final String EXTRA_SECURE_ELEMENT_NAME = "android.nfc.extra.SECURE_ELEMENT_NAME";
    public static final String EXTRA_TAG = "android.nfc.extra.TAG";
    @SystemApi
    public static final int FLAG_NDEF_PUSH_NO_CONFIRM = 1;
    public static final int FLAG_READER_NFC_A = 1;
    public static final int FLAG_READER_NFC_B = 2;
    public static final int FLAG_READER_NFC_BARCODE = 16;
    public static final int FLAG_READER_NFC_F = 4;
    public static final int FLAG_READER_NFC_V = 8;
    public static final int FLAG_READER_NO_PLATFORM_SOUNDS = 256;
    public static final int FLAG_READER_SKIP_NDEF_CHECK = 128;
    public static final int HANDOVER_TRANSFER_STATUS_FAILURE = 1;
    public static final int HANDOVER_TRANSFER_STATUS_SUCCESS = 0;
    public static final int PREFERRED_PAYMENT_CHANGED = 2;
    public static final int PREFERRED_PAYMENT_LOADED = 1;
    public static final int PREFERRED_PAYMENT_UPDATED = 3;
    public static final int STATE_OFF = 1;
    public static final int STATE_ON = 3;
    public static final int STATE_TURNING_OFF = 4;
    public static final int STATE_TURNING_ON = 2;
    static final String TAG = "NFC";
    @SystemApi
    public static final int TAG_INTENT_APP_PREF_RESULT_PACKAGE_NOT_FOUND = -1;
    @SystemApi
    public static final int TAG_INTENT_APP_PREF_RESULT_SUCCESS = 0;
    @SystemApi
    public static final int TAG_INTENT_APP_PREF_RESULT_UNAVAILABLE = -2;
    static INfcCardEmulation sCardEmulationService;
    static boolean sHasCeFeature;
    static boolean sHasNfcFeature;
    static boolean sIsInitialized = false;
    static HashMap<Context, NfcAdapter> sNfcAdapters = new HashMap<>();
    static INfcFCardEmulation sNfcFCardEmulationService;
    static NfcAdapter sNullContextNfcAdapter;
    static INfcAdapter sService;
    static NfcServiceManager.ServiceRegisterer sServiceRegisterer;
    static INfcTag sTagService;
    final Context mContext;
    OnActivityPausedListener mForegroundDispatchListener = new OnActivityPausedListener() { // from class: android.nfc.NfcAdapter.1
        @Override // android.app.OnActivityPausedListener
        public void onPaused(Activity activity) {
            NfcAdapter.this.disableForegroundDispatchInternal(activity, true);
        }
    };
    final NfcActivityManager mNfcActivityManager = new NfcActivityManager(this);
    final HashMap<NfcUnlockHandler, INfcUnlockHandler> mNfcUnlockHandlers = new HashMap<>();
    ITagRemovedCallback mTagRemovedListener = null;
    final Object mLock = new Object();
    private final NfcControllerAlwaysOnListener mControllerAlwaysOnListener = new NfcControllerAlwaysOnListener(getService());

    @SystemApi
    /* loaded from: classes2.dex */
    public interface ControllerAlwaysOnListener {
        void onControllerAlwaysOnChanged(boolean z);
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public interface CreateBeamUrisCallback {
        Uri[] createBeamUris(NfcEvent nfcEvent);
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public interface CreateNdefMessageCallback {
        NdefMessage createNdefMessage(NfcEvent nfcEvent);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public interface NfcUnlockHandler {
        boolean onUnlockAttempted(Tag tag);
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public interface OnNdefPushCompleteCallback {
        void onNdefPushComplete(NfcEvent nfcEvent);
    }

    /* loaded from: classes2.dex */
    public interface OnTagRemovedListener {
        void onTagRemoved();
    }

    /* loaded from: classes2.dex */
    public interface ReaderCallback {
        void onTagDiscovered(Tag tag);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface TagIntentAppPreferenceResult {
    }

    public List<String> getSupportedOffHostSecureElements() {
        if (this.mContext == null) {
            throw new UnsupportedOperationException("You need a context on NfcAdapter to use the  getSupportedOffHostSecureElements APIs");
        }
        List<String> offHostSE = new ArrayList<>();
        PackageManager pm = this.mContext.getPackageManager();
        if (pm == null) {
            Log.m110e(TAG, "Cannot get package manager, assuming no off-host CE feature");
            return offHostSE;
        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_NFC_OFF_HOST_CARD_EMULATION_UICC)) {
            offHostSE.add("SIM");
        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_NFC_OFF_HOST_CARD_EMULATION_ESE)) {
            offHostSE.add("eSE");
        }
        return offHostSE;
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0066 A[Catch: all -> 0x00f3, TryCatch #2 {, blocks: (B:5:0x0005, B:7:0x0009, B:8:0x0011, B:11:0x0015, B:13:0x0019, B:15:0x002e, B:17:0x0036, B:19:0x003e, B:24:0x004a, B:28:0x0053, B:29:0x005f, B:30:0x0060, B:32:0x0066, B:34:0x0074, B:36:0x0078, B:41:0x008d, B:43:0x0091, B:44:0x009a, B:47:0x00a4, B:48:0x00b0, B:52:0x00bf, B:50:0x00b2, B:51:0x00be, B:39:0x0080, B:40:0x008c, B:53:0x00c2, B:54:0x00ce, B:55:0x00cf, B:56:0x00db, B:57:0x00dc, B:59:0x00e6), top: B:69:0x0003, inners: #0, #1, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00cf A[Catch: all -> 0x00f3, TryCatch #2 {, blocks: (B:5:0x0005, B:7:0x0009, B:8:0x0011, B:11:0x0015, B:13:0x0019, B:15:0x002e, B:17:0x0036, B:19:0x003e, B:24:0x004a, B:28:0x0053, B:29:0x005f, B:30:0x0060, B:32:0x0066, B:34:0x0074, B:36:0x0078, B:41:0x008d, B:43:0x0091, B:44:0x009a, B:47:0x00a4, B:48:0x00b0, B:52:0x00bf, B:50:0x00b2, B:51:0x00be, B:39:0x0080, B:40:0x008c, B:53:0x00c2, B:54:0x00ce, B:55:0x00cf, B:56:0x00db, B:57:0x00dc, B:59:0x00e6), top: B:69:0x0003, inners: #0, #1, #3 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static synchronized NfcAdapter getNfcAdapter(Context context) {
        boolean z;
        NfcServiceManager manager;
        synchronized (NfcAdapter.class) {
            if (context == null) {
                if (sNullContextNfcAdapter == null) {
                    sNullContextNfcAdapter = new NfcAdapter(null);
                }
                return sNullContextNfcAdapter;
            }
            if (!sIsInitialized) {
                PackageManager pm = context.getPackageManager();
                sHasNfcFeature = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
                if (!pm.hasSystemFeature("android.hardware.nfc.hce") && !pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF) && !pm.hasSystemFeature(PackageManager.FEATURE_NFC_OFF_HOST_CARD_EMULATION_UICC) && !pm.hasSystemFeature(PackageManager.FEATURE_NFC_OFF_HOST_CARD_EMULATION_ESE)) {
                    z = false;
                    sHasCeFeature = z;
                    if (!sHasNfcFeature && !z) {
                        Log.m106v(TAG, "this device does not have NFC support");
                        throw new UnsupportedOperationException();
                    }
                    manager = NfcFrameworkInitializer.getNfcServiceManager();
                    if (manager != null) {
                        Log.m110e(TAG, "NfcServiceManager is null");
                        throw new UnsupportedOperationException();
                    }
                    sServiceRegisterer = manager.getNfcManagerServiceRegisterer();
                    INfcAdapter serviceInterface = getServiceInterface();
                    sService = serviceInterface;
                    if (serviceInterface == null) {
                        Log.m110e(TAG, "could not retrieve NFC service");
                        throw new UnsupportedOperationException();
                    }
                    if (sHasNfcFeature) {
                        try {
                            sTagService = serviceInterface.getNfcTagInterface();
                        } catch (RemoteException e) {
                            Log.m110e(TAG, "could not retrieve NFC Tag service");
                            throw new UnsupportedOperationException();
                        }
                    }
                    if (sHasCeFeature) {
                        try {
                            sNfcFCardEmulationService = sService.getNfcFCardEmulationInterface();
                            try {
                                sCardEmulationService = sService.getNfcCardEmulationInterface();
                            } catch (RemoteException e2) {
                                Log.m110e(TAG, "could not retrieve card emulation service");
                                throw new UnsupportedOperationException();
                            }
                        } catch (RemoteException e3) {
                            Log.m110e(TAG, "could not retrieve NFC-F card emulation service");
                            throw new UnsupportedOperationException();
                        }
                    }
                    sIsInitialized = true;
                }
                z = true;
                sHasCeFeature = z;
                if (!sHasNfcFeature) {
                    Log.m106v(TAG, "this device does not have NFC support");
                    throw new UnsupportedOperationException();
                }
                manager = NfcFrameworkInitializer.getNfcServiceManager();
                if (manager != null) {
                }
            }
            NfcAdapter adapter = sNfcAdapters.get(context);
            if (adapter == null) {
                adapter = new NfcAdapter(context);
                sNfcAdapters.put(context, adapter);
            }
            return adapter;
        }
    }

    private static INfcAdapter getServiceInterface() {
        IBinder b = sServiceRegisterer.get();
        if (b == null) {
            return null;
        }
        return INfcAdapter.Stub.asInterface(b);
    }

    public static NfcAdapter getDefaultAdapter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        Context context2 = context.getApplicationContext();
        if (context2 == null) {
            throw new IllegalArgumentException("context not associated with any application (using a mock context?)");
        }
        if (sIsInitialized && sServiceRegisterer.tryGet() == null) {
            synchronized (NfcAdapter.class) {
                if (sIsInitialized) {
                    sIsInitialized = false;
                }
            }
        }
        NfcManager manager = (NfcManager) context2.getSystemService("nfc");
        if (manager == null) {
            return null;
        }
        return manager.getDefaultAdapter();
    }

    @Deprecated
    public static NfcAdapter getDefaultAdapter() {
        Log.m103w(TAG, "WARNING: NfcAdapter.getDefaultAdapter() is deprecated, use NfcAdapter.getDefaultAdapter(Context) instead", new Exception());
        return getNfcAdapter(null);
    }

    NfcAdapter(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return this.mContext;
    }

    public INfcAdapter getService() {
        isEnabled();
        return sService;
    }

    public INfcTag getTagService() {
        isEnabled();
        return sTagService;
    }

    public INfcCardEmulation getCardEmulationService() {
        isEnabled();
        return sCardEmulationService;
    }

    public INfcFCardEmulation getNfcFCardEmulationService() {
        isEnabled();
        return sNfcFCardEmulationService;
    }

    public INfcDta getNfcDtaInterface() {
        Context context = this.mContext;
        if (context == null) {
            throw new UnsupportedOperationException("You need a context on NfcAdapter to use the  NFC extras APIs");
        }
        try {
            return sService.getNfcDtaInterface(context.getPackageName());
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return null;
            }
            try {
                return iNfcAdapter.getNfcDtaInterface(this.mContext.getPackageName());
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return null;
            }
        }
    }

    public void attemptDeadServiceRecovery(Exception e) {
        Log.m109e(TAG, "NFC service dead - attempting to recover", e);
        INfcAdapter service = getServiceInterface();
        if (service == null) {
            Log.m110e(TAG, "could not retrieve NFC service during service recovery");
            return;
        }
        sService = service;
        try {
            sTagService = service.getNfcTagInterface();
            try {
                sCardEmulationService = service.getNfcCardEmulationInterface();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "could not retrieve NFC card emulation service during service recovery");
            }
            try {
                sNfcFCardEmulationService = service.getNfcFCardEmulationInterface();
            } catch (RemoteException e3) {
                Log.m110e(TAG, "could not retrieve NFC-F card emulation service during service recovery");
            }
        } catch (RemoteException e4) {
            Log.m110e(TAG, "could not retrieve NFC tag service during service recovery");
        }
    }

    public boolean isEnabled() {
        try {
            return sService.getState() == 3;
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.getState() == 3;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    public int getAdapterState() {
        try {
            return sService.getState();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return 1;
            }
            try {
                return iNfcAdapter.getState();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return 1;
            }
        }
    }

    @SystemApi
    public boolean enable() {
        try {
            return sService.enable();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.enable();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    @SystemApi
    public boolean disable() {
        try {
            return sService.disable(true);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.disable(true);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    @SystemApi
    public boolean disable(boolean persist) {
        try {
            return sService.disable(persist);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.disable(persist);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    public void pausePolling(int timeoutInMs) {
        try {
            sService.pausePolling(timeoutInMs);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
        }
    }

    public void resumePolling() {
        try {
            sService.resumePolling();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
        }
    }

    @Deprecated
    public void setBeamPushUris(Uri[] uris, Activity activity) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Deprecated
    public void setBeamPushUrisCallback(CreateBeamUrisCallback callback, Activity activity) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Deprecated
    public void setNdefPushMessage(NdefMessage message, Activity activity, Activity... activities) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @SystemApi
    public void setNdefPushMessage(NdefMessage message, Activity activity, int flags) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Deprecated
    public void setNdefPushMessageCallback(CreateNdefMessageCallback callback, Activity activity, Activity... activities) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Deprecated
    public void setOnNdefPushCompleteCallback(OnNdefPushCompleteCallback callback, Activity activity, Activity... activities) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void enableForegroundDispatch(Activity activity, PendingIntent intent, IntentFilter[] filters, String[][] techLists) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        if (activity == null || intent == null) {
            throw new NullPointerException();
        }
        if (!activity.isResumed()) {
            throw new IllegalStateException("Foreground dispatch can only be enabled when your activity is resumed");
        }
        TechListParcel parcel = null;
        if (techLists != null) {
            try {
                if (techLists.length > 0) {
                    parcel = new TechListParcel(techLists);
                }
            } catch (RemoteException e) {
                attemptDeadServiceRecovery(e);
                return;
            }
        }
        ActivityThread.currentActivityThread().registerOnActivityPausedListener(activity, this.mForegroundDispatchListener);
        sService.setForegroundDispatch(intent, filters, parcel);
    }

    public void disableForegroundDispatch(Activity activity) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        ActivityThread.currentActivityThread().unregisterOnActivityPausedListener(activity, this.mForegroundDispatchListener);
        disableForegroundDispatchInternal(activity, false);
    }

    void disableForegroundDispatchInternal(Activity activity, boolean force) {
        try {
            sService.setForegroundDispatch(null, null, null);
            if (!force && !activity.isResumed()) {
                throw new IllegalStateException("You must disable foreground dispatching while your activity is still resumed");
            }
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
        }
    }

    public void enableReaderMode(Activity activity, ReaderCallback callback, int flags, Bundle extras) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        this.mNfcActivityManager.enableReaderMode(activity, callback, flags, extras);
    }

    public void disableReaderMode(Activity activity) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        this.mNfcActivityManager.disableReaderMode(activity);
    }

    @Deprecated
    public boolean invokeBeam(Activity activity) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        return false;
    }

    @Deprecated
    public void enableForegroundNdefPush(Activity activity, NdefMessage message) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Deprecated
    public void disableForegroundNdefPush(Activity activity) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @SystemApi
    public boolean enableSecureNfc(boolean enable) {
        if (!sHasNfcFeature && !sHasCeFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.setNfcSecure(enable);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.setNfcSecure(enable);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    public boolean isSecureNfcSupported() {
        if (!sHasNfcFeature && !sHasCeFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.deviceSupportsNfcSecure();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.deviceSupportsNfcSecure();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    public NfcAntennaInfo getNfcAntennaInfo() {
        if (!sHasNfcFeature && !sHasCeFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.getNfcAntennaInfo();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return null;
            }
            try {
                return iNfcAdapter.getNfcAntennaInfo();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return null;
            }
        }
    }

    public boolean isSecureNfcEnabled() {
        if (!sHasNfcFeature && !sHasCeFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.isNfcSecureEnabled();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.isNfcSecureEnabled();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    @SystemApi
    public boolean enableNdefPush() {
        return false;
    }

    @SystemApi
    public boolean disableNdefPush() {
        return false;
    }

    @Deprecated
    public boolean isNdefPushEnabled() {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        return false;
    }

    public boolean ignore(Tag tag, int debounceMs, final OnTagRemovedListener tagRemovedListener, final Handler handler) {
        ITagRemovedCallback.Stub iListener = null;
        if (tagRemovedListener != null) {
            iListener = new ITagRemovedCallback.Stub() { // from class: android.nfc.NfcAdapter.2
                @Override // android.nfc.ITagRemovedCallback
                public void onTagRemoved() throws RemoteException {
                    Handler handler2 = handler;
                    if (handler2 != null) {
                        handler2.post(new Runnable() { // from class: android.nfc.NfcAdapter.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                tagRemovedListener.onTagRemoved();
                            }
                        });
                    } else {
                        tagRemovedListener.onTagRemoved();
                    }
                    synchronized (NfcAdapter.this.mLock) {
                        NfcAdapter.this.mTagRemovedListener = null;
                    }
                }
            };
        }
        synchronized (this.mLock) {
            this.mTagRemovedListener = iListener;
        }
        try {
            return sService.ignore(tag.getServiceHandle(), debounceMs, iListener);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void dispatch(Tag tag) {
        if (tag == null) {
            throw new NullPointerException("tag cannot be null");
        }
        try {
            sService.dispatch(tag);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
        }
    }

    @SystemApi
    public boolean addNfcUnlockHandler(final NfcUnlockHandler unlockHandler, String[] tagTechnologies) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        if (tagTechnologies.length == 0) {
            return false;
        }
        try {
            synchronized (this.mLock) {
                if (this.mNfcUnlockHandlers.containsKey(unlockHandler)) {
                    sService.removeNfcUnlockHandler(this.mNfcUnlockHandlers.get(unlockHandler));
                    this.mNfcUnlockHandlers.remove(unlockHandler);
                }
                INfcUnlockHandler.Stub iHandler = new INfcUnlockHandler.Stub() { // from class: android.nfc.NfcAdapter.3
                    @Override // android.nfc.INfcUnlockHandler
                    public boolean onUnlockAttempted(Tag tag) throws RemoteException {
                        return unlockHandler.onUnlockAttempted(tag);
                    }
                };
                sService.addNfcUnlockHandler(iHandler, Tag.getTechCodesFromStrings(tagTechnologies));
                this.mNfcUnlockHandlers.put(unlockHandler, iHandler);
            }
            return true;
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            return false;
        } catch (IllegalArgumentException e2) {
            Log.m109e(TAG, "Unable to register LockscreenDispatch", e2);
            return false;
        }
    }

    @SystemApi
    public boolean removeNfcUnlockHandler(NfcUnlockHandler unlockHandler) {
        synchronized (NfcAdapter.class) {
            if (!sHasNfcFeature) {
                throw new UnsupportedOperationException();
            }
        }
        try {
            synchronized (this.mLock) {
                if (this.mNfcUnlockHandlers.containsKey(unlockHandler)) {
                    sService.removeNfcUnlockHandler(this.mNfcUnlockHandlers.remove(unlockHandler));
                }
            }
            return true;
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            return false;
        }
    }

    public INfcAdapterExtras getNfcAdapterExtrasInterface() {
        Context context = this.mContext;
        if (context == null) {
            throw new UnsupportedOperationException("You need a context on NfcAdapter to use the  NFC extras APIs");
        }
        try {
            return sService.getNfcAdapterExtrasInterface(context.getPackageName());
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return null;
            }
            try {
                return iNfcAdapter.getNfcAdapterExtrasInterface(this.mContext.getPackageName());
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return null;
            }
        }
    }

    void enforceResumed(Activity activity) {
        if (!activity.isResumed()) {
            throw new IllegalStateException("API cannot be called while activity is paused");
        }
    }

    int getSdkVersion() {
        Context context = this.mContext;
        if (context == null) {
            return 9;
        }
        return context.getApplicationInfo().targetSdkVersion;
    }

    @SystemApi
    public boolean setControllerAlwaysOn(boolean value) {
        if (!sHasNfcFeature && !sHasCeFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.setControllerAlwaysOn(value);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.setControllerAlwaysOn(value);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    @SystemApi
    public boolean isControllerAlwaysOn() {
        try {
            return sService.isControllerAlwaysOn();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.isControllerAlwaysOn();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    @SystemApi
    public boolean isControllerAlwaysOnSupported() {
        if (!sHasNfcFeature && !sHasCeFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.isControllerAlwaysOnSupported();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.isControllerAlwaysOnSupported();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }

    @SystemApi
    public void registerControllerAlwaysOnListener(Executor executor, ControllerAlwaysOnListener listener) {
        this.mControllerAlwaysOnListener.register(executor, listener);
    }

    @SystemApi
    public void unregisterControllerAlwaysOnListener(ControllerAlwaysOnListener listener) {
        this.mControllerAlwaysOnListener.unregister(listener);
    }

    @SystemApi
    public int setTagIntentAppPreferenceForUser(int userId, String pkg, boolean allow) {
        Objects.requireNonNull(pkg, "pkg cannot be null");
        if (!isTagIntentAppPreferenceSupported()) {
            Log.m110e(TAG, "TagIntentAppPreference is not supported");
            throw new UnsupportedOperationException();
        }
        try {
            return sService.setTagIntentAppPreferenceForUser(userId, pkg, allow);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            if (sService == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
            }
            try {
                return sService.setTagIntentAppPreferenceForUser(userId, pkg, allow);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return -2;
            }
        }
    }

    @SystemApi
    public Map<String, Boolean> getTagIntentAppPreferenceForUser(int userId) {
        if (!isTagIntentAppPreferenceSupported()) {
            Log.m110e(TAG, "TagIntentAppPreference is not supported");
            throw new UnsupportedOperationException();
        }
        try {
            Map<String, Boolean> result = sService.getTagIntentAppPreferenceForUser(userId);
            return result;
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return Collections.emptyMap();
            }
            try {
                Map<String, Boolean> result2 = iNfcAdapter.getTagIntentAppPreferenceForUser(userId);
                return result2;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return Collections.emptyMap();
            }
        }
    }

    @SystemApi
    public boolean isTagIntentAppPreferenceSupported() {
        if (!sHasNfcFeature) {
            throw new UnsupportedOperationException();
        }
        try {
            return sService.isTagIntentAppPreferenceSupported();
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            INfcAdapter iNfcAdapter = sService;
            if (iNfcAdapter == null) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
            try {
                return iNfcAdapter.isTagIntentAppPreferenceSupported();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover NFC Service.");
                return false;
            }
        }
    }
}
