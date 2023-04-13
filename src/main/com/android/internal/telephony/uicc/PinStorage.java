package com.android.internal.telephony.uicc;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.WorkSource;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.nano.StoredPinProto$EncryptedPin;
import com.android.internal.telephony.nano.StoredPinProto$StoredPin;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
/* loaded from: classes.dex */
public class PinStorage extends Handler {
    private final int mBootCount;
    private final BroadcastReceiver mBroadcastReceiver;
    private final Context mContext;
    private boolean mIsDeviceLocked;
    private boolean mIsDeviceSecure;
    private final KeyStore mKeyStore;
    private boolean mLastCommitResult = true;
    private SecretKey mLongTermSecretKey;
    private final SparseArray<byte[]> mRamStorage;
    private SecretKey mShortTermSecretKey;
    @VisibleForTesting
    public int mShortTermSecretKeyDurationMinutes;

    private static void logv(String str, Object... objArr) {
    }

    public PinStorage(Context context) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.uicc.PinStorage.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if ("android.telephony.action.SIM_CARD_STATE_CHANGED".equals(action) || "android.telephony.action.SIM_APPLICATION_STATE_CHANGED".equals(action)) {
                    int intExtra = intent.getIntExtra("phone", -1);
                    int intExtra2 = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                    if (PinStorage.this.validateSlotId(intExtra)) {
                        PinStorage pinStorage = PinStorage.this;
                        pinStorage.sendMessage(pinStorage.obtainMessage(1, intExtra, intExtra2));
                    }
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    PinStorage pinStorage2 = PinStorage.this;
                    pinStorage2.sendMessage(pinStorage2.obtainMessage(4));
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mContext = context;
        this.mBootCount = getBootCount();
        this.mKeyStore = initializeKeyStore();
        this.mShortTermSecretKeyDurationMinutes = 15;
        boolean isDeviceSecure = isDeviceSecure();
        this.mIsDeviceSecure = isDeviceSecure;
        this.mIsDeviceLocked = isDeviceSecure ? isDeviceLocked() : false;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.SIM_CARD_STATE_CHANGED");
        intentFilter.addAction("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        context.registerReceiver(broadcastReceiver, intentFilter);
        ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.uicc.PinStorage$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                PinStorage.this.post(runnable);
            }
        }, new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.uicc.PinStorage$$ExternalSyntheticLambda1
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                PinStorage.this.lambda$new$0(i, i2, i3, i4);
            }
        });
        this.mLongTermSecretKey = initializeSecretKey((!this.mIsDeviceSecure || this.mIsDeviceLocked) ? "PinStorage_longTerm_always_key" : "PinStorage_longTerm_ua_key", true);
        if (!this.mIsDeviceSecure || !this.mIsDeviceLocked) {
            this.mRamStorage = null;
            onDeviceReady();
            return;
        }
        logd("Device is locked - Postponing initialization", new Object[0]);
        this.mRamStorage = new SparseArray<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        onCarrierConfigurationChanged(i);
    }

    public synchronized void storePin(String str, int i) {
        String iccid = getIccid(i);
        if (validatePin(str) && validateIccid(iccid) && validateSlotId(i)) {
            if (!isCacheAllowed(i)) {
                logd("storePin[%d]: caching it not allowed", Integer.valueOf(i));
                return;
            }
            logd("storePin[%d]", Integer.valueOf(i));
            StoredPinProto$StoredPin storedPinProto$StoredPin = new StoredPinProto$StoredPin();
            storedPinProto$StoredPin.iccid = iccid;
            storedPinProto$StoredPin.pin = str;
            storedPinProto$StoredPin.slotId = i;
            storedPinProto$StoredPin.status = 1;
            savePinInformation(i, storedPinProto$StoredPin);
            return;
        }
        loge("storePin[%d] - Invalid PIN, slotId or ICCID", Integer.valueOf(i));
        clearPin(i);
    }

    public synchronized void clearPin(int i) {
        logd("clearPin[%d]", Integer.valueOf(i));
        if (validateSlotId(i)) {
            savePinInformation(i, null);
        }
    }

    public synchronized String getPin(int i, String str) {
        if (validateSlotId(i) && validateIccid(str)) {
            StoredPinProto$StoredPin loadPinInformation = loadPinInformation(i);
            if (loadPinInformation != null) {
                if (!loadPinInformation.iccid.equals(str)) {
                    savePinInformation(i, null);
                    TelephonyStatsLog.write(336, 6, 1, PhoneConfigurationManager.SSSS);
                } else if (loadPinInformation.status == 3) {
                    logd("getPin[%d] - Found PIN ready for verification", Integer.valueOf(i));
                    loadPinInformation.status = 1;
                    savePinInformation(i, loadPinInformation);
                    return loadPinInformation.pin;
                }
            }
            return PhoneConfigurationManager.SSSS;
        }
        return PhoneConfigurationManager.SSSS;
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x0083 A[Catch: all -> 0x00ad, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0007, B:9:0x0012, B:13:0x001a, B:15:0x0031, B:18:0x003f, B:20:0x0047, B:30:0x0071, B:33:0x0078, B:37:0x0083, B:40:0x00a8, B:39:0x0097, B:23:0x0050, B:27:0x006b, B:24:0x0053, B:26:0x005b), top: B:46:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0095  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized int prepareUnattendedReboot(WorkSource workSource) {
        String str;
        int i = 2;
        if (this.mIsDeviceLocked) {
            loge("prepareUnattendedReboot - Device is locked", new Object[0]);
            return 2;
        } else if (startTimer(20000)) {
            int slotCount = getSlotCount();
            SparseArray<StoredPinProto$StoredPin> loadPinInformation = loadPinInformation();
            deleteSecretKey("PinStorage_shortTerm_key");
            this.mShortTermSecretKey = null;
            if (loadPinInformation.size() > 0) {
                this.mShortTermSecretKey = initializeSecretKey("PinStorage_shortTerm_key", true);
            }
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            while (true) {
                if (i2 >= slotCount) {
                    i = i4;
                    break;
                }
                StoredPinProto$StoredPin storedPinProto$StoredPin = loadPinInformation.get(i2);
                if (storedPinProto$StoredPin != null) {
                    storedPinProto$StoredPin.status = 2;
                    if (!savePinInformation(i2, storedPinProto$StoredPin)) {
                        break;
                    }
                    i3++;
                } else if (isPinState(i2, IccCardStatus.PinState.PINSTATE_ENABLED_VERIFIED)) {
                    loge("Slot %d requires PIN and is not cached", Integer.valueOf(i2));
                    i5++;
                    i4 = 1;
                }
                i2++;
            }
            if (workSource != null && workSource.size() != 0) {
                str = workSource.getPackageName(0);
                if (i != 0) {
                    logd("prepareUnattendedReboot - Stored %d PINs", Integer.valueOf(i3));
                    TelephonyStatsLog.write(336, 4, i3, str);
                } else if (i == 1) {
                    logd("prepareUnattendedReboot - Required %d PINs after reboot", Integer.valueOf(i5));
                    TelephonyStatsLog.write(336, 5, i5, str);
                }
                saveNumberOfCachedPins(i3);
                return i;
            }
            str = PhoneConfigurationManager.SSSS;
            if (i != 0) {
            }
            saveNumberOfCachedPins(i3);
            return i;
        } else {
            return 2;
        }
    }

    private synchronized void onUserUnlocked() {
        if (this.mIsDeviceLocked) {
            logd("onUserUnlocked - Device is unlocked", new Object[0]);
            SparseArray<StoredPinProto$StoredPin> loadPinInformation = loadPinInformation();
            cleanRamStorage();
            this.mIsDeviceLocked = false;
            this.mLongTermSecretKey = initializeSecretKey("PinStorage_longTerm_ua_key", true);
            for (int i = 0; i < loadPinInformation.size(); i++) {
                savePinInformation(loadPinInformation.keyAt(i), loadPinInformation.valueAt(i));
            }
            onDeviceReady();
            verifyPendingPins();
        }
    }

    private void onDeviceReady() {
        logd("onDeviceReady", new Object[0]);
        this.mShortTermSecretKey = initializeSecretKey("PinStorage_shortTerm_key", false);
        int slotCount = getSlotCount();
        int i = 0;
        for (int i2 = 0; i2 < slotCount; i2++) {
            StoredPinProto$StoredPin loadPinInformation = loadPinInformation(i2);
            if (loadPinInformation != null) {
                if (loadPinInformation.status == 1) {
                    if (loadPinInformation.bootCount != this.mBootCount) {
                        logd("Boot count [%d] does not match - remove PIN", Integer.valueOf(i2));
                        savePinInformation(i2, null);
                    } else {
                        logd("Boot count [%d] matches - keep stored PIN", Integer.valueOf(i2));
                    }
                }
                if (loadPinInformation.status == 2) {
                    loadPinInformation.status = 3;
                    savePinInformation(i2, loadPinInformation);
                    i++;
                }
            }
        }
        if (i > 0) {
            startTimer(20000);
        }
        int saveNumberOfCachedPins = saveNumberOfCachedPins(0);
        if (saveNumberOfCachedPins > i) {
            TelephonyStatsLog.write(336, 7, saveNumberOfCachedPins - i, PhoneConfigurationManager.SSSS);
        }
    }

    private synchronized void onTimerExpiration() {
        logd("onTimerExpiration", new Object[0]);
        int slotCount = getSlotCount();
        int i = 0;
        for (int i2 = 0; i2 < slotCount; i2++) {
            StoredPinProto$StoredPin loadPinInformation = loadPinInformation(i2);
            if (loadPinInformation != null) {
                int i3 = loadPinInformation.status;
                if (i3 == 3) {
                    logd("onTimerExpiration - Discarding PIN in slot %d", Integer.valueOf(i2));
                    savePinInformation(i2, null);
                    i++;
                } else if (i3 == 2) {
                    logd("onTimerExpiration - Moving PIN in slot %d back to AVAILABLE", Integer.valueOf(i2));
                    loadPinInformation.status = 1;
                    savePinInformation(i2, loadPinInformation);
                }
            }
        }
        deleteSecretKey("PinStorage_shortTerm_key");
        this.mShortTermSecretKey = null;
        saveNumberOfCachedPins(0);
        if (i > 0) {
            TelephonyStatsLog.write(336, 3, i, PhoneConfigurationManager.SSSS);
        }
    }

    private synchronized void onSimStatusChange(int i, @TelephonyManager.SimState int i2) {
        logd("SIM card/application changed[%d]: %s", Integer.valueOf(i), TelephonyManager.simStateToString(i2));
        switch (i2) {
            case 1:
            case 2:
                StoredPinProto$StoredPin loadPinInformation = loadPinInformation(i);
                if (loadPinInformation != null && loadPinInformation.status != 3) {
                    savePinInformation(i, null);
                    break;
                }
                break;
            case 3:
            case 7:
            case 8:
                clearPin(i);
                break;
            case 4:
            case 5:
            case 9:
            case 10:
                StoredPinProto$StoredPin loadPinInformation2 = loadPinInformation(i);
                if (loadPinInformation2 != null && loadPinInformation2.status != 1) {
                    savePinInformation(i, null);
                    break;
                }
                break;
        }
    }

    private void onCarrierConfigurationChanged(int i) {
        logv("onCarrierConfigChanged[%d]", Integer.valueOf(i));
        if (isCacheAllowed(i)) {
            return;
        }
        logd("onCarrierConfigChanged[%d] - PIN caching not allowed", Integer.valueOf(i));
        clearPin(i);
    }

    private void onSupplyPinComplete(int i, boolean z) {
        logd("onSupplyPinComplete[%d] - success: %s", Integer.valueOf(i), Boolean.valueOf(z));
        if (!z) {
            clearPin(i);
        }
        TelephonyStatsLog.write(336, z ? 1 : 2, 1, PhoneConfigurationManager.SSSS);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        boolean z = true;
        if (i == 1) {
            onSimStatusChange(message.arg1, message.arg2);
        } else if (i == 3) {
            onTimerExpiration();
        } else if (i == 4) {
            onUserUnlocked();
        } else if (i != 5) {
        } else {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            onSupplyPinComplete(message.arg2, (asyncResult == null || asyncResult.exception != null) ? false : false);
        }
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) this.mContext.getSystemService(KeyguardManager.class);
        if (keyguardManager != null) {
            return keyguardManager.isDeviceSecure();
        }
        return false;
    }

    private boolean isDeviceLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) this.mContext.getSystemService(KeyguardManager.class);
        return keyguardManager != null && keyguardManager.isDeviceSecure() && keyguardManager.isDeviceLocked();
    }

    private SparseArray<StoredPinProto$StoredPin> loadPinInformation() {
        SparseArray<StoredPinProto$StoredPin> sparseArray = new SparseArray<>();
        int slotCount = getSlotCount();
        for (int i = 0; i < slotCount; i++) {
            StoredPinProto$StoredPin loadPinInformation = loadPinInformation(i);
            if (loadPinInformation != null) {
                sparseArray.put(i, loadPinInformation);
            }
        }
        return sparseArray;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0043, code lost:
        if (r4 != null) goto L13;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private StoredPinProto$StoredPin loadPinInformation(int i) {
        StoredPinProto$StoredPin loadPinInformationFromDisk;
        int i2;
        StoredPinProto$StoredPin storedPinProto$StoredPin = null;
        if (!this.mLastCommitResult) {
            loge("Last commit failed - returning empty values", new Object[0]);
            return null;
        }
        if (this.mIsDeviceLocked) {
            SparseArray<byte[]> sparseArray = this.mRamStorage;
            if (sparseArray != null && sparseArray.get(i) != null) {
                loadPinInformationFromDisk = decryptStoredPin(this.mRamStorage.get(i), this.mLongTermSecretKey);
            }
            loadPinInformationFromDisk = null;
        } else {
            StoredPinProto$StoredPin loadPinInformationFromDisk2 = loadPinInformationFromDisk(i, "encrypted_pin_available_", this.mLongTermSecretKey);
            loadPinInformationFromDisk = loadPinInformationFromDisk(i, "encrypted_pin_reboot_", this.mShortTermSecretKey);
            if (loadPinInformationFromDisk2 == null || loadPinInformationFromDisk != null) {
                if (loadPinInformationFromDisk2 == null) {
                }
                loadPinInformationFromDisk = null;
            } else {
                loadPinInformationFromDisk = loadPinInformationFromDisk2;
            }
        }
        if (loadPinInformationFromDisk == null || (i2 = loadPinInformationFromDisk.slotId) == i) {
            storedPinProto$StoredPin = loadPinInformationFromDisk;
        } else {
            loge("Load PIN: slot ID does not match (%d != %d)", Integer.valueOf(i2), Integer.valueOf(i));
        }
        if (storedPinProto$StoredPin != null) {
            logv("Load PIN: %s", storedPinProto$StoredPin.toString());
        } else {
            logv("Load PIN for slot %d: null", Integer.valueOf(i));
        }
        return storedPinProto$StoredPin;
    }

    private StoredPinProto$StoredPin loadPinInformationFromDisk(int i, String str, SecretKey secretKey) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("pinstorage_prefs", 0);
        String string = sharedPreferences.getString(str + i, PhoneConfigurationManager.SSSS);
        if (string.isEmpty()) {
            return null;
        }
        try {
            return decryptStoredPin(Base64.decode(string, 0), secretKey);
        } catch (Exception unused) {
            return null;
        }
    }

    private StoredPinProto$StoredPin decryptStoredPin(byte[] bArr, SecretKey secretKey) {
        if (secretKey == null) {
            TelephonyStatsLog.write(336, 10, 1, PhoneConfigurationManager.SSSS);
            return null;
        }
        try {
            byte[] decrypt = decrypt(secretKey, bArr);
            if (decrypt.length > 0) {
                return StoredPinProto$StoredPin.parseFrom(decrypt);
            }
            return null;
        } catch (Exception e) {
            loge("cannot decrypt/parse PIN information", e);
            return null;
        }
    }

    private boolean savePinInformation(int i, StoredPinProto$StoredPin storedPinProto$StoredPin) {
        boolean z;
        if (storedPinProto$StoredPin != null) {
            storedPinProto$StoredPin.bootCount = this.mBootCount;
        }
        if (this.mIsDeviceLocked) {
            return savePinInformationToRam(i, storedPinProto$StoredPin);
        }
        boolean z2 = false;
        SharedPreferences.Editor remove = this.mContext.getSharedPreferences("pinstorage_prefs", 0).edit().remove("encrypted_pin_available_" + i).remove("encrypted_pin_reboot_" + i);
        if (storedPinProto$StoredPin != null) {
            logd("Saving PIN for slot %d", Integer.valueOf(i));
            if (storedPinProto$StoredPin.status == 1) {
                z = savePinInformation(remove, i, storedPinProto$StoredPin, "encrypted_pin_available_", this.mLongTermSecretKey);
            } else {
                z = savePinInformation(remove, i, storedPinProto$StoredPin, "encrypted_pin_reboot_", this.mShortTermSecretKey);
            }
        } else {
            logv("Deleting PIN for slot %d (if existed)", Integer.valueOf(i));
            z = true;
        }
        if (remove.commit() && z) {
            z2 = true;
        }
        this.mLastCommitResult = z2;
        return z2;
    }

    private boolean savePinInformation(SharedPreferences.Editor editor, int i, StoredPinProto$StoredPin storedPinProto$StoredPin, String str, SecretKey secretKey) {
        if (secretKey == null) {
            return false;
        }
        if (i != storedPinProto$StoredPin.slotId) {
            loge("Save PIN: the slotId does not match (%d != %d)", Integer.valueOf(i), Integer.valueOf(storedPinProto$StoredPin.slotId));
            return false;
        }
        logv("Save PIN: %s", storedPinProto$StoredPin.toString());
        byte[] encrypt = encrypt(secretKey, MessageNano.toByteArray(storedPinProto$StoredPin));
        if (encrypt.length > 0) {
            editor.putString(str + i, Base64.encodeToString(encrypt, 0));
            return true;
        }
        return false;
    }

    private boolean savePinInformationToRam(int i, StoredPinProto$StoredPin storedPinProto$StoredPin) {
        byte[] encrypt;
        cleanRamStorage(i);
        if (storedPinProto$StoredPin == null) {
            return true;
        }
        if (storedPinProto$StoredPin.status != 1 || (encrypt = encrypt(this.mLongTermSecretKey, MessageNano.toByteArray(storedPinProto$StoredPin))) == null || encrypt.length <= 0) {
            return false;
        }
        logd("Saving PIN for slot %d in RAM", Integer.valueOf(i));
        this.mRamStorage.put(i, encrypt);
        return true;
    }

    private void cleanRamStorage() {
        int slotCount = getSlotCount();
        for (int i = 0; i < slotCount; i++) {
            cleanRamStorage(i);
        }
    }

    private void cleanRamStorage(int i) {
        SparseArray<byte[]> sparseArray = this.mRamStorage;
        if (sparseArray != null) {
            byte[] bArr = sparseArray.get(i);
            if (bArr != null) {
                Arrays.fill(bArr, (byte) 0);
            }
            this.mRamStorage.delete(i);
        }
    }

    private void verifyPendingPins() {
        int slotCount = getSlotCount();
        for (int i = 0; i < slotCount; i++) {
            if (isPinState(i, IccCardStatus.PinState.PINSTATE_ENABLED_NOT_VERIFIED)) {
                verifyPendingPin(i);
            }
        }
    }

    private void verifyPendingPin(int i) {
        String pin = getPin(i, getIccid(i));
        if (pin.isEmpty()) {
            return;
        }
        logd("Perform automatic verification of PIN in slot %d", Integer.valueOf(i));
        UiccProfile uiccProfileForPhone = UiccController.getInstance().getUiccProfileForPhone(i);
        if (uiccProfileForPhone != null) {
            Message obtainMessage = obtainMessage(5);
            obtainMessage.arg2 = i;
            uiccProfileForPhone.supplyPin(pin, obtainMessage);
            return;
        }
        logd("Perform automatic verification of PIN in slot %d not possible", Integer.valueOf(i));
    }

    private int getBootCount() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "boot_count", -1);
    }

    private int getSlotCount() {
        try {
            return PhoneFactory.getPhones().length;
        } catch (Exception unused) {
            return TelephonyManager.getDefault().getActiveModemCount();
        }
    }

    private int saveNumberOfCachedPins(int i) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("pinstorage_prefs", 0);
        int i2 = sharedPreferences.getInt("stored_pins", 0);
        sharedPreferences.edit().putInt("stored_pins", i).commit();
        return i2;
    }

    private boolean startTimer(int i) {
        removeMessages(3);
        if (i > 0) {
            return sendEmptyMessageDelayed(3, i);
        }
        return true;
    }

    private String getIccid(int i) {
        Phone phone = PhoneFactory.getPhone(i);
        return phone != null ? phone.getFullIccSerialNumber() : PhoneConfigurationManager.SSSS;
    }

    private boolean validatePin(String str) {
        return str != null && str.length() >= 4 && str.length() <= 8;
    }

    private boolean validateIccid(String str) {
        return str != null && str.length() >= 12;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean validateSlotId(int i) {
        return i >= 0 && i < getSlotCount();
    }

    private boolean isPinState(int i, IccCardStatus.PinState pinState) {
        UiccProfile uiccProfileForPhone = UiccController.getInstance().getUiccProfileForPhone(i);
        if (uiccProfileForPhone != null) {
            for (int i2 = 0; i2 < 3; i2++) {
                UiccCardApplication application = uiccProfileForPhone.getApplication(i2);
                if (application != null) {
                    return application.getPin1State() == pinState;
                }
            }
        }
        return false;
    }

    private boolean isCacheAllowed(int i) {
        return isCacheAllowedByDevice() && isCacheAllowedByCarrier(i);
    }

    private boolean isCacheAllowedByDevice() {
        if (this.mContext.getResources().getBoolean(17891363)) {
            return true;
        }
        logv("Pin caching disabled in resources", new Object[0]);
        return false;
    }

    private boolean isCacheAllowedByCarrier(int i) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        Phone phone = PhoneFactory.getPhone(i);
        PersistableBundle carrierConfigSubset = (carrierConfigManager == null || phone == null) ? null : CarrierConfigManager.getCarrierConfigSubset(this.mContext, phone.getSubId(), new String[]{"store_sim_pin_for_unattended_reboot_bool"});
        if (carrierConfigSubset == null || carrierConfigSubset.isEmpty()) {
            carrierConfigSubset = CarrierConfigManager.getDefaultConfig();
        }
        return carrierConfigSubset.getBoolean("store_sim_pin_for_unattended_reboot_bool", true);
    }

    private static KeyStore initializeKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            logv("KeyStore ready", new Object[0]);
            return keyStore;
        } catch (Exception e) {
            loge("Error loading KeyStore", e);
            return null;
        }
    }

    private SecretKey initializeSecretKey(String str, boolean z) {
        if (this.mKeyStore == null) {
            return null;
        }
        SecretKey secretKey = getSecretKey(str);
        if (secretKey != null) {
            logd("KeyStore: alias %s exists", str);
            return secretKey;
        } else if (z) {
            Date shortLivedKeyValidityEnd = "PinStorage_shortTerm_key".equals(str) ? getShortLivedKeyValidityEnd() : null;
            boolean z2 = !"PinStorage_longTerm_always_key".equals(str) && isDeviceSecure();
            Object[] objArr = new Object[3];
            objArr[0] = str;
            objArr[1] = shortLivedKeyValidityEnd != null ? shortLivedKeyValidityEnd.toString() : PhoneConfigurationManager.SSSS;
            objArr[2] = Boolean.valueOf(z2);
            logd("KeyStore: alias %s does not exist - Creating (exp=%s, auth=%s)", objArr);
            return createSecretKey(str, shortLivedKeyValidityEnd, z2);
        } else {
            logd("KeyStore: alias %s does not exist - Nothing to do", str);
            return null;
        }
    }

    private SecretKey getSecretKey(String str) {
        try {
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) this.mKeyStore.getEntry(str, null);
            if (secretKeyEntry != null) {
                return secretKeyEntry.getSecretKey();
            }
        } catch (Exception e) {
            loge("Exception with getting the key " + str, e);
            this.deleteSecretKey(str);
        }
        return null;
    }

    private SecretKey createSecretKey(String str, Date date, boolean z) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            KeyGenParameterSpec.Builder encryptionPaddings = new KeyGenParameterSpec.Builder(str, 3).setBlockModes("GCM").setEncryptionPaddings("NoPadding");
            if (date != null) {
                encryptionPaddings = encryptionPaddings.setKeyValidityEnd(date);
            }
            if (z) {
                encryptionPaddings = encryptionPaddings.setUserAuthenticationRequired(true).setUserAuthenticationParameters(KeepaliveStatus.INVALID_HANDLE, 1);
            }
            keyGenerator.init(encryptionPaddings.build());
            return keyGenerator.generateKey();
        } catch (Exception e) {
            loge("Create key exception", e);
            return null;
        }
    }

    private Date getShortLivedKeyValidityEnd() {
        if (this.mShortTermSecretKeyDurationMinutes > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(12, this.mShortTermSecretKeyDurationMinutes);
            return calendar.getTime();
        }
        return null;
    }

    private void deleteSecretKey(String str) {
        if (this.mKeyStore != null) {
            logd("Delete key: %s", str);
            try {
                this.mKeyStore.deleteEntry(str);
            } catch (Exception unused) {
                loge("Delete key exception", new Object[0]);
            }
        }
    }

    private byte[] encrypt(SecretKey secretKey, byte[] bArr) {
        if (secretKey == null) {
            loge("Encrypt: Secret key is null", new Object[0]);
            return new byte[0];
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(1, secretKey);
            StoredPinProto$EncryptedPin storedPinProto$EncryptedPin = new StoredPinProto$EncryptedPin();
            storedPinProto$EncryptedPin.f13iv = cipher.getIV();
            storedPinProto$EncryptedPin.encryptedStoredPin = cipher.doFinal(bArr);
            return MessageNano.toByteArray(storedPinProto$EncryptedPin);
        } catch (Exception e) {
            loge("Encrypt exception", e);
            TelephonyStatsLog.write(336, 9, 1, PhoneConfigurationManager.SSSS);
            return new byte[0];
        }
    }

    private byte[] decrypt(SecretKey secretKey, byte[] bArr) {
        if (secretKey == null) {
            loge("Decrypt: Secret key is null", new Object[0]);
            return new byte[0];
        }
        try {
            StoredPinProto$EncryptedPin parseFrom = StoredPinProto$EncryptedPin.parseFrom(bArr);
            if (!ArrayUtils.isEmpty(parseFrom.encryptedStoredPin) && !ArrayUtils.isEmpty(parseFrom.f13iv)) {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(2, secretKey, new GCMParameterSpec(128, parseFrom.f13iv));
                return cipher.doFinal(parseFrom.encryptedStoredPin);
            }
        } catch (Exception e) {
            loge("Decrypt exception", e);
            TelephonyStatsLog.write(336, 8, 1, PhoneConfigurationManager.SSSS);
        }
        return new byte[0];
    }

    private static void logd(String str, Object... objArr) {
        Rlog.d("PinStorage", String.format(str, objArr));
    }

    private static void loge(String str, Object... objArr) {
        Rlog.e("PinStorage", String.format(str, objArr));
    }

    private static void loge(String str, Throwable th) {
        Rlog.e("PinStorage", str, th);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("PinStorage:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mIsDeviceSecure=" + this.mIsDeviceSecure);
        androidUtilIndentingPrintWriter.println("mIsDeviceLocked=" + this.mIsDeviceLocked);
        StringBuilder sb = new StringBuilder();
        sb.append("isLongTermSecretKey=");
        sb.append(this.mLongTermSecretKey != null);
        androidUtilIndentingPrintWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("isShortTermSecretKey=");
        sb2.append(this.mShortTermSecretKey != null);
        androidUtilIndentingPrintWriter.println(sb2.toString());
        androidUtilIndentingPrintWriter.println("isCacheAllowedByDevice=" + isCacheAllowedByDevice());
        int slotCount = getSlotCount();
        for (int i = 0; i < slotCount; i++) {
            androidUtilIndentingPrintWriter.println("isCacheAllowedByCarrier[" + i + "]=" + isCacheAllowedByCarrier(i));
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
