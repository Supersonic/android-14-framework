package android.nfc.cardemulation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.nfc.INfcFCardEmulation;
import android.nfc.NfcAdapter;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes2.dex */
public final class NfcFCardEmulation {
    static final String TAG = "NfcFCardEmulation";
    static INfcFCardEmulation sService;
    final Context mContext;
    static boolean sIsInitialized = false;
    static HashMap<Context, NfcFCardEmulation> sCardEmus = new HashMap<>();

    private NfcFCardEmulation(Context context, INfcFCardEmulation service) {
        this.mContext = context.getApplicationContext();
        sService = service;
    }

    public static synchronized NfcFCardEmulation getInstance(NfcAdapter adapter) {
        NfcFCardEmulation manager;
        synchronized (NfcFCardEmulation.class) {
            try {
                if (adapter == null) {
                    throw new NullPointerException("NfcAdapter is null");
                }
                Context context = adapter.getContext();
                if (context == null) {
                    Log.m110e(TAG, "NfcAdapter context is null.");
                    throw new UnsupportedOperationException();
                }
                if (!sIsInitialized) {
                    PackageManager pm = context.getPackageManager();
                    if (pm == null) {
                        Log.m110e(TAG, "Cannot get PackageManager");
                        throw new UnsupportedOperationException();
                    } else if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)) {
                        Log.m110e(TAG, "This device does not support NFC-F card emulation");
                        throw new UnsupportedOperationException();
                    } else {
                        sIsInitialized = true;
                    }
                }
                manager = sCardEmus.get(context);
                if (manager == null) {
                    INfcFCardEmulation service = adapter.getNfcFCardEmulationService();
                    if (service == null) {
                        Log.m110e(TAG, "This device does not implement the INfcFCardEmulation interface.");
                        throw new UnsupportedOperationException();
                    }
                    manager = new NfcFCardEmulation(context, service);
                    sCardEmus.put(context, manager);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return manager;
    }

    public String getSystemCodeForService(ComponentName service) throws RuntimeException {
        if (service == null) {
            throw new NullPointerException("service is null");
        }
        try {
            return sService.getSystemCodeForService(this.mContext.getUser().getIdentifier(), service);
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return iNfcFCardEmulation.getSystemCodeForService(this.mContext.getUser().getIdentifier(), service);
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return null;
            }
        }
    }

    public boolean registerSystemCodeForService(ComponentName service, String systemCode) throws RuntimeException {
        if (service == null || systemCode == null) {
            throw new NullPointerException("service or systemCode is null");
        }
        try {
            return sService.registerSystemCodeForService(this.mContext.getUser().getIdentifier(), service, systemCode);
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcFCardEmulation.registerSystemCodeForService(this.mContext.getUser().getIdentifier(), service, systemCode);
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return false;
            }
        }
    }

    public boolean unregisterSystemCodeForService(ComponentName service) throws RuntimeException {
        if (service == null) {
            throw new NullPointerException("service is null");
        }
        try {
            return sService.removeSystemCodeForService(this.mContext.getUser().getIdentifier(), service);
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcFCardEmulation.removeSystemCodeForService(this.mContext.getUser().getIdentifier(), service);
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return false;
            }
        }
    }

    public String getNfcid2ForService(ComponentName service) throws RuntimeException {
        if (service == null) {
            throw new NullPointerException("service is null");
        }
        try {
            return sService.getNfcid2ForService(this.mContext.getUser().getIdentifier(), service);
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return iNfcFCardEmulation.getNfcid2ForService(this.mContext.getUser().getIdentifier(), service);
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return null;
            }
        }
    }

    public boolean setNfcid2ForService(ComponentName service, String nfcid2) throws RuntimeException {
        if (service == null || nfcid2 == null) {
            throw new NullPointerException("service or nfcid2 is null");
        }
        try {
            return sService.setNfcid2ForService(this.mContext.getUser().getIdentifier(), service, nfcid2);
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcFCardEmulation.setNfcid2ForService(this.mContext.getUser().getIdentifier(), service, nfcid2);
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return false;
            }
        }
    }

    public boolean enableService(Activity activity, ComponentName service) throws RuntimeException {
        if (activity == null || service == null) {
            throw new NullPointerException("activity or service is null");
        }
        if (!activity.isResumed()) {
            throw new IllegalArgumentException("Activity must be resumed.");
        }
        try {
            return sService.enableNfcFForegroundService(service);
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcFCardEmulation.enableNfcFForegroundService(service);
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return false;
            }
        }
    }

    public boolean disableService(Activity activity) throws RuntimeException {
        if (activity == null) {
            throw new NullPointerException("activity is null");
        }
        if (!activity.isResumed()) {
            throw new IllegalArgumentException("Activity must be resumed.");
        }
        try {
            return sService.disableNfcFForegroundService();
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcFCardEmulation.disableNfcFForegroundService();
            } catch (RemoteException ee) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                ee.rethrowAsRuntimeException();
                return false;
            }
        }
    }

    public List<NfcFServiceInfo> getNfcFServices() {
        try {
            return sService.getNfcFServices(this.mContext.getUser().getIdentifier());
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return iNfcFCardEmulation.getNfcFServices(this.mContext.getUser().getIdentifier());
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return null;
            }
        }
    }

    public int getMaxNumOfRegisterableSystemCodes() {
        try {
            return sService.getMaxNumOfRegisterableSystemCodes();
        } catch (RemoteException e) {
            recoverService();
            INfcFCardEmulation iNfcFCardEmulation = sService;
            if (iNfcFCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return -1;
            }
            try {
                return iNfcFCardEmulation.getMaxNumOfRegisterableSystemCodes();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return -1;
            }
        }
    }

    public static boolean isValidSystemCode(String systemCode) {
        if (systemCode == null) {
            return false;
        }
        if (systemCode.length() != 4) {
            Log.m110e(TAG, "System Code " + systemCode + " is not a valid System Code.");
            return false;
        } else if (!systemCode.startsWith("4") || systemCode.toUpperCase().endsWith("FF")) {
            Log.m110e(TAG, "System Code " + systemCode + " is not a valid System Code.");
            return false;
        } else {
            try {
                Integer.parseInt(systemCode, 16);
                return true;
            } catch (NumberFormatException e) {
                Log.m110e(TAG, "System Code " + systemCode + " is not a valid System Code.");
                return false;
            }
        }
    }

    public static boolean isValidNfcid2(String nfcid2) {
        if (nfcid2 == null) {
            return false;
        }
        if (nfcid2.length() != 16) {
            Log.m110e(TAG, "NFCID2 " + nfcid2 + " is not a valid NFCID2.");
            return false;
        } else if (!nfcid2.toUpperCase().startsWith("02FE")) {
            Log.m110e(TAG, "NFCID2 " + nfcid2 + " is not a valid NFCID2.");
            return false;
        } else {
            try {
                Long.parseLong(nfcid2, 16);
                return true;
            } catch (NumberFormatException e) {
                Log.m110e(TAG, "NFCID2 " + nfcid2 + " is not a valid NFCID2.");
                return false;
            }
        }
    }

    void recoverService() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.mContext);
        sService = adapter.getNfcFCardEmulationService();
    }
}
