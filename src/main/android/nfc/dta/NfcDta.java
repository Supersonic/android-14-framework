package android.nfc.dta;

import android.content.Context;
import android.nfc.INfcDta;
import android.nfc.NfcAdapter;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.HashMap;
/* loaded from: classes2.dex */
public final class NfcDta {
    private static final String TAG = "NfcDta";
    private static HashMap<Context, NfcDta> sNfcDtas = new HashMap<>();
    private static INfcDta sService;
    private final Context mContext;

    private NfcDta(Context context, INfcDta service) {
        this.mContext = context.getApplicationContext();
        sService = service;
    }

    public static synchronized NfcDta getInstance(NfcAdapter adapter) {
        NfcDta manager;
        synchronized (NfcDta.class) {
            try {
                if (adapter == null) {
                    throw new NullPointerException("NfcAdapter is null");
                }
                Context context = adapter.getContext();
                if (context == null) {
                    Log.m110e(TAG, "NfcAdapter context is null.");
                    throw new UnsupportedOperationException();
                }
                manager = sNfcDtas.get(context);
                if (manager == null) {
                    INfcDta service = adapter.getNfcDtaInterface();
                    if (service == null) {
                        Log.m110e(TAG, "This device does not implement the INfcDta interface.");
                        throw new UnsupportedOperationException();
                    }
                    manager = new NfcDta(context, service);
                    sNfcDtas.put(context, manager);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return manager;
    }

    public boolean enableDta() {
        try {
            sService.enableDta();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean disableDta() {
        try {
            sService.disableDta();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean enableServer(String serviceName, int serviceSap, int miu, int rwSize, int testCaseId) {
        try {
            return sService.enableServer(serviceName, serviceSap, miu, rwSize, testCaseId);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean disableServer() {
        try {
            sService.disableServer();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean enableClient(String serviceName, int miu, int rwSize, int testCaseId) {
        try {
            return sService.enableClient(serviceName, miu, rwSize, testCaseId);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean disableClient() {
        try {
            sService.disableClient();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean registerMessageService(String msgServiceName) {
        try {
            return sService.registerMessageService(msgServiceName);
        } catch (RemoteException e) {
            return false;
        }
    }
}
