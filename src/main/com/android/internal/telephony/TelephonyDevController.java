package com.android.internal.telephony;

import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class TelephonyDevController extends Handler {
    private static final Object mLock = new Object();
    private static ArrayList<HardwareConfig> mModems = new ArrayList<>();
    private static ArrayList<HardwareConfig> mSims = new ArrayList<>();
    private static Message sRilHardwareConfig;
    private static TelephonyDevController sTelephonyDevController;

    private static void logd(String str) {
        Rlog.d("TDC", str);
    }

    private static void loge(String str) {
        Rlog.e("TDC", str);
    }

    public static TelephonyDevController create() {
        TelephonyDevController telephonyDevController;
        synchronized (mLock) {
            if (sTelephonyDevController != null) {
                throw new RuntimeException("TelephonyDevController already created!?!");
            }
            telephonyDevController = new TelephonyDevController();
            sTelephonyDevController = telephonyDevController;
        }
        return telephonyDevController;
    }

    public static TelephonyDevController getInstance() {
        TelephonyDevController telephonyDevController;
        synchronized (mLock) {
            telephonyDevController = sTelephonyDevController;
            if (telephonyDevController == null) {
                throw new RuntimeException("TelephonyDevController not yet created!?!");
            }
        }
        return telephonyDevController;
    }

    private void initFromResource() {
        String[] stringArray = Resources.getSystem().getStringArray(17236148);
        if (stringArray != null) {
            for (String str : stringArray) {
                HardwareConfig hardwareConfig = new HardwareConfig(str);
                int i = hardwareConfig.type;
                if (i == 0) {
                    updateOrInsert(hardwareConfig, mModems);
                } else if (i == 1) {
                    updateOrInsert(hardwareConfig, mSims);
                }
            }
        }
    }

    private TelephonyDevController() {
        initFromResource();
        mModems.trimToSize();
        mSims.trimToSize();
    }

    public static void registerRIL(CommandsInterface commandsInterface) {
        commandsInterface.getHardwareConfig(sRilHardwareConfig);
        Message message = sRilHardwareConfig;
        if (message != null) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult.exception == null) {
                handleGetHardwareConfigChanged(asyncResult);
            }
        }
        commandsInterface.registerForHardwareConfigChanged(sTelephonyDevController, 1, null);
    }

    public static void unregisterRIL(CommandsInterface commandsInterface) {
        commandsInterface.unregisterForHardwareConfigChanged(sTelephonyDevController);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what == 1) {
            logd("handleMessage: received EVENT_HARDWARE_CONFIG_CHANGED");
            handleGetHardwareConfigChanged((AsyncResult) message.obj);
            return;
        }
        loge("handleMessage: Unknown Event " + message.what);
    }

    private static void updateOrInsert(HardwareConfig hardwareConfig, ArrayList<HardwareConfig> arrayList) {
        synchronized (mLock) {
            int size = arrayList.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                HardwareConfig hardwareConfig2 = arrayList.get(i);
                if (hardwareConfig2.uuid.compareTo(hardwareConfig.uuid) == 0) {
                    logd("updateOrInsert: removing: " + hardwareConfig2);
                    arrayList.remove(i);
                    break;
                }
                i++;
            }
            logd("updateOrInsert: inserting: " + hardwareConfig);
            arrayList.add(hardwareConfig);
        }
    }

    private static void handleGetHardwareConfigChanged(AsyncResult asyncResult) {
        Object obj;
        if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
            List list = (List) obj;
            for (int i = 0; i < list.size(); i++) {
                HardwareConfig hardwareConfig = (HardwareConfig) list.get(i);
                if (hardwareConfig != null) {
                    int i2 = hardwareConfig.type;
                    if (i2 == 0) {
                        updateOrInsert(hardwareConfig, mModems);
                    } else if (i2 == 1) {
                        updateOrInsert(hardwareConfig, mSims);
                    }
                }
            }
            return;
        }
        loge("handleGetHardwareConfigChanged - returned an error.");
    }

    public static int getModemCount() {
        int size;
        synchronized (mLock) {
            size = mModems.size();
            logd("getModemCount: " + size);
        }
        return size;
    }

    public HardwareConfig getModem(int i) {
        synchronized (mLock) {
            if (mModems.isEmpty()) {
                loge("getModem: no registered modem device?!?");
                return null;
            } else if (i > getModemCount()) {
                loge("getModem: out-of-bounds access for modem device " + i + " max: " + getModemCount());
                return null;
            } else {
                logd("getModem: " + i);
                return mModems.get(i);
            }
        }
    }

    public int getSimCount() {
        int size;
        synchronized (mLock) {
            size = mSims.size();
            logd("getSimCount: " + size);
        }
        return size;
    }

    public HardwareConfig getSim(int i) {
        synchronized (mLock) {
            if (mSims.isEmpty()) {
                loge("getSim: no registered sim device?!?");
                return null;
            } else if (i > getSimCount()) {
                loge("getSim: out-of-bounds access for sim device " + i + " max: " + getSimCount());
                return null;
            } else {
                logd("getSim: " + i);
                return mSims.get(i);
            }
        }
    }

    public HardwareConfig getModemForSim(int i) {
        synchronized (mLock) {
            if (!mModems.isEmpty() && !mSims.isEmpty()) {
                if (i > getSimCount()) {
                    loge("getModemForSim: out-of-bounds access for sim device " + i + " max: " + getSimCount());
                    return null;
                }
                logd("getModemForSim " + i);
                HardwareConfig sim = getSim(i);
                Iterator<HardwareConfig> it = mModems.iterator();
                while (it.hasNext()) {
                    HardwareConfig next = it.next();
                    if (next.uuid.equals(sim.modemUuid)) {
                        return next;
                    }
                }
                return null;
            }
            loge("getModemForSim: no registered modem/sim device?!?");
            return null;
        }
    }

    public ArrayList<HardwareConfig> getAllSimsForModem(int i) {
        synchronized (mLock) {
            if (mSims.isEmpty()) {
                loge("getAllSimsForModem: no registered sim device?!?");
                return null;
            } else if (i > getModemCount()) {
                loge("getAllSimsForModem: out-of-bounds access for modem device " + i + " max: " + getModemCount());
                return null;
            } else {
                logd("getAllSimsForModem " + i);
                ArrayList<HardwareConfig> arrayList = new ArrayList<>();
                HardwareConfig modem = getModem(i);
                Iterator<HardwareConfig> it = mSims.iterator();
                while (it.hasNext()) {
                    HardwareConfig next = it.next();
                    if (next.modemUuid.equals(modem.uuid)) {
                        arrayList.add(next);
                    }
                }
                return arrayList;
            }
        }
    }

    public ArrayList<HardwareConfig> getAllModems() {
        ArrayList<HardwareConfig> arrayList;
        synchronized (mLock) {
            arrayList = new ArrayList<>();
            if (mModems.isEmpty()) {
                logd("getAllModems: empty list.");
            } else {
                Iterator<HardwareConfig> it = mModems.iterator();
                while (it.hasNext()) {
                    arrayList.add(it.next());
                }
            }
        }
        return arrayList;
    }

    public ArrayList<HardwareConfig> getAllSims() {
        ArrayList<HardwareConfig> arrayList;
        synchronized (mLock) {
            arrayList = new ArrayList<>();
            if (mSims.isEmpty()) {
                logd("getAllSims: empty list.");
            } else {
                Iterator<HardwareConfig> it = mSims.iterator();
                while (it.hasNext()) {
                    arrayList.add(it.next());
                }
            }
        }
        return arrayList;
    }
}
