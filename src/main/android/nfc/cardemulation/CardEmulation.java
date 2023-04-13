package android.nfc.cardemulation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.nfc.INfcCardEmulation;
import android.nfc.NfcAdapter;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public final class CardEmulation {
    public static final String ACTION_CHANGE_DEFAULT = "android.nfc.cardemulation.action.ACTION_CHANGE_DEFAULT";
    public static final String CATEGORY_OTHER = "other";
    public static final String CATEGORY_PAYMENT = "payment";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_SERVICE_COMPONENT = "component";
    public static final int SELECTION_MODE_ALWAYS_ASK = 1;
    public static final int SELECTION_MODE_ASK_IF_CONFLICT = 2;
    public static final int SELECTION_MODE_PREFER_DEFAULT = 0;
    static final String TAG = "CardEmulation";
    static INfcCardEmulation sService;
    final Context mContext;
    private static final Pattern AID_PATTERN = Pattern.compile("[0-9A-Fa-f]{10,32}\\*?\\#?");
    static boolean sIsInitialized = false;
    static HashMap<Context, CardEmulation> sCardEmus = new HashMap<>();

    private CardEmulation(Context context, INfcCardEmulation service) {
        this.mContext = context.getApplicationContext();
        sService = service;
    }

    public static synchronized CardEmulation getInstance(NfcAdapter adapter) {
        CardEmulation manager;
        synchronized (CardEmulation.class) {
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
                    } else if (!pm.hasSystemFeature("android.hardware.nfc.hce")) {
                        Log.m110e(TAG, "This device does not support card emulation");
                        throw new UnsupportedOperationException();
                    } else {
                        sIsInitialized = true;
                    }
                }
                manager = sCardEmus.get(context);
                if (manager == null) {
                    INfcCardEmulation service = adapter.getCardEmulationService();
                    if (service == null) {
                        Log.m110e(TAG, "This device does not implement the INfcCardEmulation interface.");
                        throw new UnsupportedOperationException();
                    }
                    manager = new CardEmulation(context, service);
                    sCardEmus.put(context, manager);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return manager;
    }

    public boolean isDefaultServiceForCategory(ComponentName service, String category) {
        try {
            return sService.isDefaultServiceForCategory(this.mContext.getUser().getIdentifier(), service, category);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.isDefaultServiceForCategory(this.mContext.getUser().getIdentifier(), service, category);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
        }
    }

    public boolean isDefaultServiceForAid(ComponentName service, String aid) {
        try {
            return sService.isDefaultServiceForAid(this.mContext.getUser().getIdentifier(), service, aid);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.isDefaultServiceForAid(this.mContext.getUser().getIdentifier(), service, aid);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean categoryAllowsForegroundPreference(String category) {
        if (CATEGORY_PAYMENT.equals(category)) {
            Context contextAsUser = this.mContext.createContextAsUser(UserHandle.m145of(UserHandle.myUserId()), 0);
            try {
                boolean preferForeground = Settings.Secure.getInt(contextAsUser.getContentResolver(), Settings.Secure.NFC_PAYMENT_FOREGROUND) != 0;
                return preferForeground;
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    public int getSelectionModeForCategory(String category) {
        boolean paymentRegistered;
        if (CATEGORY_PAYMENT.equals(category)) {
            try {
                paymentRegistered = sService.isDefaultPaymentRegistered();
            } catch (RemoteException e) {
                recoverService();
                INfcCardEmulation iNfcCardEmulation = sService;
                if (iNfcCardEmulation == null) {
                    Log.m110e(TAG, "Failed to recover CardEmulationService.");
                    return 1;
                }
                try {
                    paymentRegistered = iNfcCardEmulation.isDefaultPaymentRegistered();
                } catch (RemoteException e2) {
                    Log.m110e(TAG, "Failed to reach CardEmulationService.");
                    return 1;
                }
            }
            if (!paymentRegistered) {
                return 1;
            }
            return 0;
        }
        return 2;
    }

    public boolean registerAidsForService(ComponentName service, String category, List<String> aids) {
        AidGroup aidGroup = new AidGroup(aids, category);
        try {
            return sService.registerAidGroupForService(this.mContext.getUser().getIdentifier(), service, aidGroup);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.registerAidGroupForService(this.mContext.getUser().getIdentifier(), service, aidGroup);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean unsetOffHostForService(ComponentName service) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.mContext);
        if (adapter == null) {
            return false;
        }
        try {
            return sService.unsetOffHostForService(this.mContext.getUser().getIdentifier(), service);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.unsetOffHostForService(this.mContext.getUser().getIdentifier(), service);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean setOffHostForService(ComponentName service, String offHostSecureElement) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.mContext);
        if (adapter == null || offHostSecureElement == null) {
            return false;
        }
        List<String> validSE = adapter.getSupportedOffHostSecureElements();
        if ((offHostSecureElement.startsWith("eSE") && !validSE.contains("eSE")) || (offHostSecureElement.startsWith("SIM") && !validSE.contains("SIM"))) {
            return false;
        }
        if (!offHostSecureElement.startsWith("eSE") && !offHostSecureElement.startsWith("SIM")) {
            return false;
        }
        if (offHostSecureElement.equals("eSE")) {
            offHostSecureElement = "eSE1";
        } else if (offHostSecureElement.equals("SIM")) {
            offHostSecureElement = "SIM1";
        }
        try {
            return sService.setOffHostForService(this.mContext.getUser().getIdentifier(), service, offHostSecureElement);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setOffHostForService(this.mContext.getUser().getIdentifier(), service, offHostSecureElement);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public List<String> getAidsForService(ComponentName service, String category) {
        try {
            AidGroup group = sService.getAidGroupForService(this.mContext.getUser().getIdentifier(), service, category);
            if (group != null) {
                return group.getAids();
            }
            return null;
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                AidGroup group2 = iNfcCardEmulation.getAidGroupForService(this.mContext.getUser().getIdentifier(), service, category);
                if (group2 != null) {
                    return group2.getAids();
                }
                return null;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
        }
    }

    public boolean removeAidsForService(ComponentName service, String category) {
        try {
            return sService.removeAidGroupForService(this.mContext.getUser().getIdentifier(), service, category);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.removeAidGroupForService(this.mContext.getUser().getIdentifier(), service, category);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean setPreferredService(Activity activity, ComponentName service) {
        if (activity == null || service == null) {
            throw new NullPointerException("activity or service or category is null");
        }
        if (!activity.isResumed()) {
            throw new IllegalArgumentException("Activity must be resumed.");
        }
        try {
            return sService.setPreferredService(service);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setPreferredService(service);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean unsetPreferredService(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity is null");
        }
        if (!activity.isResumed()) {
            throw new IllegalArgumentException("Activity must be resumed.");
        }
        try {
            return sService.unsetPreferredService();
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.unsetPreferredService();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean supportsAidPrefixRegistration() {
        try {
            return sService.supportsAidPrefixRegistration();
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.supportsAidPrefixRegistration();
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public List<String> getAidsForPreferredPaymentService() {
        try {
            ApduServiceInfo serviceInfo = sService.getPreferredPaymentService(this.mContext.getUser().getIdentifier());
            if (serviceInfo != null) {
                return serviceInfo.getAids();
            }
            return null;
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                throw e.rethrowFromSystemServer();
            }
            try {
                ApduServiceInfo serviceInfo2 = iNfcCardEmulation.getPreferredPaymentService(this.mContext.getUser().getIdentifier());
                if (serviceInfo2 != null) {
                    return serviceInfo2.getAids();
                }
                return null;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String getRouteDestinationForPreferredPaymentService() {
        try {
            ApduServiceInfo serviceInfo = sService.getPreferredPaymentService(this.mContext.getUser().getIdentifier());
            if (serviceInfo != null) {
                if (serviceInfo.isOnHost()) {
                    return "Host";
                }
                return serviceInfo.getOffHostSecureElement() == null ? "OffHost" : serviceInfo.getOffHostSecureElement();
            }
            return null;
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                throw e.rethrowFromSystemServer();
            }
            try {
                ApduServiceInfo serviceInfo2 = iNfcCardEmulation.getPreferredPaymentService(this.mContext.getUser().getIdentifier());
                if (serviceInfo2 != null) {
                    if (serviceInfo2.isOnHost()) {
                        return "Host";
                    }
                    return serviceInfo2.getOffHostSecureElement() == null ? "Offhost" : serviceInfo2.getOffHostSecureElement();
                }
                return null;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public CharSequence getDescriptionForPreferredPaymentService() {
        try {
            ApduServiceInfo serviceInfo = sService.getPreferredPaymentService(this.mContext.getUser().getIdentifier());
            if (serviceInfo != null) {
                return serviceInfo.getDescription();
            }
            return null;
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                throw e.rethrowFromSystemServer();
            }
            try {
                ApduServiceInfo serviceInfo2 = iNfcCardEmulation.getPreferredPaymentService(this.mContext.getUser().getIdentifier());
                if (serviceInfo2 != null) {
                    return serviceInfo2.getDescription();
                }
                return null;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean setDefaultServiceForCategory(ComponentName service, String category) {
        try {
            return sService.setDefaultServiceForCategory(this.mContext.getUser().getIdentifier(), service, category);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setDefaultServiceForCategory(this.mContext.getUser().getIdentifier(), service, category);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean setDefaultForNextTap(ComponentName service) {
        try {
            return sService.setDefaultForNextTap(this.mContext.getUser().getIdentifier(), service);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setDefaultForNextTap(this.mContext.getUser().getIdentifier(), service);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean setDefaultForNextTap(int userId, ComponentName service) {
        try {
            return sService.setDefaultForNextTap(userId, service);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return iNfcCardEmulation.setDefaultForNextTap(userId, service);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public List<ApduServiceInfo> getServices(String category) {
        try {
            return sService.getServices(this.mContext.getUser().getIdentifier(), category);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return iNfcCardEmulation.getServices(this.mContext.getUser().getIdentifier(), category);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return null;
            }
        }
    }

    public List<ApduServiceInfo> getServices(String category, int userId) {
        try {
            return sService.getServices(userId, category);
        } catch (RemoteException e) {
            recoverService();
            INfcCardEmulation iNfcCardEmulation = sService;
            if (iNfcCardEmulation == null) {
                Log.m110e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return iNfcCardEmulation.getServices(userId, category);
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to reach CardEmulationService.");
                return null;
            }
        }
    }

    public static boolean isValidAid(String aid) {
        if (aid == null) {
            return false;
        }
        if ((aid.endsWith("*") || aid.endsWith("#")) && aid.length() % 2 == 0) {
            Log.m110e(TAG, "AID " + aid + " is not a valid AID.");
            return false;
        } else if (!aid.endsWith("*") && !aid.endsWith("#") && aid.length() % 2 != 0) {
            Log.m110e(TAG, "AID " + aid + " is not a valid AID.");
            return false;
        } else if (!AID_PATTERN.matcher(aid).matches()) {
            Log.m110e(TAG, "AID " + aid + " is not a valid AID.");
            return false;
        } else {
            return true;
        }
    }

    void recoverService() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.mContext);
        sService = adapter.getCardEmulationService();
    }
}
