package com.android.nfc_extras;

import android.content.Context;
import android.nfc.INfcAdapterExtras;
import android.nfc.NfcAdapter;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
/* loaded from: classes.dex */
public final class NfcAdapterExtras {
    public static final String ACTION_RF_FIELD_OFF_DETECTED = "com.android.nfc_extras.action.RF_FIELD_OFF_DETECTED";
    public static final String ACTION_RF_FIELD_ON_DETECTED = "com.android.nfc_extras.action.RF_FIELD_ON_DETECTED";
    private static final String TAG = "NfcAdapterExtras";
    private static INfcAdapterExtras sService;
    private final NfcAdapter mAdapter;
    private final NfcExecutionEnvironment mEmbeddedEe;
    final String mPackageName;
    private final CardEmulationRoute mRouteOnWhenScreenOn;
    private static final CardEmulationRoute ROUTE_OFF = new CardEmulationRoute(1, null);
    private static final HashMap<NfcAdapter, NfcAdapterExtras> sNfcExtras = new HashMap<>();

    private static void initService(NfcAdapter adapter) {
        INfcAdapterExtras service = adapter.getNfcAdapterExtrasInterface();
        if (service != null) {
            sService = service;
        }
    }

    public static NfcAdapterExtras get(NfcAdapter adapter) {
        NfcAdapterExtras extras;
        Context context = adapter.getContext();
        if (context == null) {
            throw new UnsupportedOperationException("You must pass a context to your NfcAdapter to use the NFC extras APIs");
        }
        synchronized (NfcAdapterExtras.class) {
            if (sService == null) {
                initService(adapter);
            }
            HashMap<NfcAdapter, NfcAdapterExtras> hashMap = sNfcExtras;
            extras = hashMap.get(adapter);
            if (extras == null) {
                extras = new NfcAdapterExtras(adapter);
                hashMap.put(adapter, extras);
            }
        }
        return extras;
    }

    private NfcAdapterExtras(NfcAdapter adapter) {
        this.mAdapter = adapter;
        this.mPackageName = adapter.getContext().getPackageName();
        NfcExecutionEnvironment nfcExecutionEnvironment = new NfcExecutionEnvironment(this);
        this.mEmbeddedEe = nfcExecutionEnvironment;
        this.mRouteOnWhenScreenOn = new CardEmulationRoute(2, nfcExecutionEnvironment);
    }

    /* loaded from: classes.dex */
    public static final class CardEmulationRoute {
        public static final int ROUTE_OFF = 1;
        public static final int ROUTE_ON_WHEN_SCREEN_ON = 2;
        public final NfcExecutionEnvironment nfcEe;
        public final int route;

        public CardEmulationRoute(int route, NfcExecutionEnvironment nfcEe) {
            if (route == 1 && nfcEe != null) {
                throw new IllegalArgumentException("must not specifiy a NFC-EE with ROUTE_OFF");
            }
            if (route != 1 && nfcEe == null) {
                throw new IllegalArgumentException("must specifiy a NFC-EE for this route");
            }
            this.route = route;
            this.nfcEe = nfcEe;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attemptDeadServiceRecovery(Exception e) {
        Log.e(TAG, "NFC Adapter Extras dead - attempting to recover");
        this.mAdapter.attemptDeadServiceRecovery(e);
        initService(this.mAdapter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public INfcAdapterExtras getService() {
        return sService;
    }

    public CardEmulationRoute getCardEmulationRoute() {
        try {
            int route = sService.getCardEmulationRoute(this.mPackageName);
            if (route == 1) {
                return ROUTE_OFF;
            }
            return this.mRouteOnWhenScreenOn;
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            return ROUTE_OFF;
        }
    }

    public void setCardEmulationRoute(CardEmulationRoute route) {
        try {
            sService.setCardEmulationRoute(this.mPackageName, route.route);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
        }
    }

    public NfcExecutionEnvironment getEmbeddedExecutionEnvironment() {
        return this.mEmbeddedEe;
    }

    public void authenticate(byte[] token) {
        try {
            sService.authenticate(this.mPackageName, token);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
        }
    }

    public String getDriverName() {
        try {
            return sService.getDriverName(this.mPackageName);
        } catch (RemoteException e) {
            attemptDeadServiceRecovery(e);
            return "";
        }
    }
}
