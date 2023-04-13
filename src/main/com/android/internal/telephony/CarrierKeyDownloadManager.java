package com.android.internal.telephony;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class CarrierKeyDownloadManager extends Handler {
    private static final int[] CARRIER_KEY_TYPES = {1, 2};
    private final BroadcastReceiver mBroadcastReceiver;
    public int mCarrierId;
    private final Context mContext;
    @VisibleForTesting
    public long mDownloadId;
    public final DownloadManager mDownloadManager;
    @VisibleForTesting
    public String mMccMncForDownload;
    private final Phone mPhone;
    private TelephonyManager mTelephonyManager;
    private String mURL;
    @VisibleForTesting
    public int mKeyAvailability = 0;
    private boolean mAllowedOverMeteredNetwork = false;
    private boolean mDeleteOldKeyAfterDownload = false;
    private final BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.CarrierKeyDownloadManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.DOWNLOAD_COMPLETE")) {
                Log.d("CarrierKeyDownloadManager", "Download Complete");
                CarrierKeyDownloadManager carrierKeyDownloadManager = CarrierKeyDownloadManager.this;
                carrierKeyDownloadManager.sendMessage(carrierKeyDownloadManager.obtainMessage(1, Long.valueOf(intent.getLongExtra("extra_download_id", 0L))));
            }
        }
    };

    public static boolean isKeyEnabled(int i, int i2) {
        return ((i2 >> (i - 1)) & 1) == 1;
    }

    public CarrierKeyDownloadManager(Phone phone) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.CarrierKeyDownloadManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                int slotIndex = SubscriptionManager.getSlotIndex(CarrierKeyDownloadManager.this.mPhone.getSubId());
                int phoneId = CarrierKeyDownloadManager.this.mPhone.getPhoneId();
                if (action.equals("com.android.internal.telephony.carrier_key_download_alarm")) {
                    if (intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1) == slotIndex) {
                        Log.d("CarrierKeyDownloadManager", "Handling key renewal alarm: " + action);
                        CarrierKeyDownloadManager.this.sendEmptyMessage(0);
                    }
                } else if (action.equals("com.android.internal.telephony.ACTION_CARRIER_CERTIFICATE_DOWNLOAD") && phoneId == intent.getIntExtra("phone", -1)) {
                    Log.d("CarrierKeyDownloadManager", "Handling reset intent: " + action);
                    CarrierKeyDownloadManager.this.sendEmptyMessage(0);
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mPhone = phone;
        Context context = phone.getContext();
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.internal.telephony.carrier_key_download_alarm");
        intentFilter.addAction("com.android.internal.telephony.ACTION_CARRIER_CERTIFICATE_DOWNLOAD");
        context.registerReceiver(broadcastReceiver, intentFilter, null, phone);
        this.mDownloadManager = (DownloadManager) context.getSystemService("download");
        this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(phone.getSubId());
        ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.CarrierKeyDownloadManager$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                CarrierKeyDownloadManager.this.post(runnable);
            }
        }, new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.CarrierKeyDownloadManager$$ExternalSyntheticLambda1
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                CarrierKeyDownloadManager.this.lambda$new$0(i, i2, i3, i4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        if (i == this.mPhone.getPhoneId()) {
            Log.d("CarrierKeyDownloadManager", "Carrier Config changed: slotIndex=" + i);
            handleAlarmOrConfigChange();
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            handleAlarmOrConfigChange();
        } else if (i != 1) {
        } else {
            long longValue = ((Long) message.obj).longValue();
            String simOperator = getSimOperator();
            int simCarrierId = getSimCarrierId();
            if (isValidDownload(simOperator, longValue, simCarrierId)) {
                onDownloadComplete(longValue, simOperator, simCarrierId);
                onPostDownloadProcessing(longValue);
            }
        }
    }

    private void onPostDownloadProcessing(long j) {
        resetRenewalAlarm();
        cleanupDownloadInfo();
        this.mContext.unregisterReceiver(this.mDownloadReceiver);
    }

    private void handleAlarmOrConfigChange() {
        if (carrierUsesKeys()) {
            if (!areCarrierKeysAbsentOrExpiring() || downloadKey()) {
                return;
            }
            resetRenewalAlarm();
            return;
        }
        cleanupRenewalAlarms();
        this.mPhone.deleteCarrierInfoForImsiEncryption(getSimCarrierId());
    }

    private void cleanupDownloadInfo() {
        Log.d("CarrierKeyDownloadManager", "Cleaning up download info");
        this.mDownloadId = -1L;
        this.mMccMncForDownload = null;
        this.mCarrierId = -1;
    }

    private void cleanupRenewalAlarms() {
        Log.d("CarrierKeyDownloadManager", "Cleaning up existing renewal alarms");
        int slotIndex = SubscriptionManager.getSlotIndex(this.mPhone.getSubId());
        Intent intent = new Intent("com.android.internal.telephony.carrier_key_download_alarm");
        intent.putExtra("android.telephony.extra.SLOT_INDEX", slotIndex);
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(this.mContext, 0, intent, 201326592));
    }

    @VisibleForTesting
    public long getExpirationDate() {
        int[] iArr;
        ImsiEncryptionInfo carrierInfoForImsiEncryption;
        long j = Long.MAX_VALUE;
        for (int i : CARRIER_KEY_TYPES) {
            if (isKeyEnabled(i) && (carrierInfoForImsiEncryption = this.mPhone.getCarrierInfoForImsiEncryption(i, false)) != null && carrierInfoForImsiEncryption.getExpirationTime() != null && j > carrierInfoForImsiEncryption.getExpirationTime().getTime()) {
                j = carrierInfoForImsiEncryption.getExpirationTime().getTime();
            }
        }
        if (j == Long.MAX_VALUE || j < System.currentTimeMillis() + 604800000) {
            return System.currentTimeMillis() + 86400000;
        }
        return j - (new Random().nextInt(1209600000) + 604800000);
    }

    @VisibleForTesting
    public void resetRenewalAlarm() {
        cleanupRenewalAlarms();
        int slotIndex = SubscriptionManager.getSlotIndex(this.mPhone.getSubId());
        long expirationDate = getExpirationDate();
        Log.d("CarrierKeyDownloadManager", "minExpirationDate: " + new Date(expirationDate));
        Intent intent = new Intent("com.android.internal.telephony.carrier_key_download_alarm");
        intent.putExtra("android.telephony.extra.SLOT_INDEX", slotIndex);
        ((AlarmManager) this.mContext.getSystemService("alarm")).set(0, expirationDate, PendingIntent.getBroadcast(this.mContext, 0, intent, 201326592));
        Log.d("CarrierKeyDownloadManager", "setRenewalAlarm: action=" + intent.getAction() + " time=" + new Date(expirationDate));
    }

    @VisibleForTesting
    public String getSimOperator() {
        return this.mTelephonyManager.getSimOperator(this.mPhone.getSubId());
    }

    @VisibleForTesting
    public int getSimCarrierId() {
        return this.mTelephonyManager.getSimCarrierId();
    }

    @VisibleForTesting
    public boolean isValidDownload(String str, long j, int i) {
        if (j != this.mDownloadId) {
            Log.e("CarrierKeyDownloadManager", "download ID=" + j + " for completed download does not match stored id=" + this.mDownloadId);
            return false;
        } else if (TextUtils.isEmpty(str) || TextUtils.isEmpty(this.mMccMncForDownload) || !TextUtils.equals(str, this.mMccMncForDownload) || this.mCarrierId != i) {
            Log.e("CarrierKeyDownloadManager", "currentMccMnc=" + str + " storedMccMnc =" + this.mMccMncForDownload + "currentCarrierId = " + i + "  storedCarrierId = " + this.mCarrierId);
            return false;
        } else {
            Log.d("CarrierKeyDownloadManager", "Matched MccMnc =  " + str + ", carrierId = " + i + ", downloadId: " + j);
            return true;
        }
    }

    private void onDownloadComplete(long j, String str, int i) {
        Log.d("CarrierKeyDownloadManager", "onDownloadComplete: " + j);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(j);
        Cursor query2 = this.mDownloadManager.query(query);
        if (query2 == null) {
            return;
        }
        if (query2.moveToFirst()) {
            try {
                if (8 == query2.getInt(query2.getColumnIndex("status"))) {
                    try {
                        String convertToString = convertToString(this.mDownloadManager, j);
                        if (TextUtils.isEmpty(convertToString)) {
                            Log.d("CarrierKeyDownloadManager", "fallback to no gzip");
                            convertToString = convertToStringNoGZip(this.mDownloadManager, j);
                        }
                        parseJsonAndPersistKey(convertToString, str, i);
                        this.mDownloadManager.remove(j);
                    } catch (Exception e) {
                        Log.e("CarrierKeyDownloadManager", "Error in download:" + j + ". " + e);
                        this.mDownloadManager.remove(j);
                    }
                }
                Log.d("CarrierKeyDownloadManager", "Completed downloading keys");
            } catch (Throwable th) {
                this.mDownloadManager.remove(j);
                throw th;
            }
        }
        query2.close();
    }

    private boolean carrierUsesKeys() {
        PersistableBundle persistableBundle;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        if (carrierConfigManager == null) {
            return false;
        }
        try {
            persistableBundle = carrierConfigManager.getConfigForSubId(this.mPhone.getSubId(), new String[]{"imsi_key_availability_int", "imsi_key_download_url_string", "allow_metered_network_for_cert_download_bool"});
        } catch (RuntimeException unused) {
            Log.e("CarrierKeyDownloadManager", "CarrierConfigLoader is not available.");
            persistableBundle = null;
        }
        if (persistableBundle != null && !persistableBundle.isEmpty()) {
            this.mKeyAvailability = persistableBundle.getInt("imsi_key_availability_int");
            this.mURL = persistableBundle.getString("imsi_key_download_url_string");
            this.mAllowedOverMeteredNetwork = persistableBundle.getBoolean("allow_metered_network_for_cert_download_bool");
            if (this.mKeyAvailability != 0 && !TextUtils.isEmpty(this.mURL)) {
                for (int i : CARRIER_KEY_TYPES) {
                    if (isKeyEnabled(i)) {
                        return true;
                    }
                }
                return false;
            }
            Log.d("CarrierKeyDownloadManager", "Carrier not enabled or invalid values. mKeyAvailability=" + this.mKeyAvailability + " mURL=" + this.mURL);
        }
        return false;
    }

    private static String convertToStringNoGZip(DownloadManager downloadManager, long j) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(downloadManager.openDownloadedFile(j).getFileDescriptor());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                    sb.append('\n');
                } else {
                    fileInputStream.close();
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String convertToString(DownloadManager downloadManager, long j) {
        try {
            FileInputStream fileInputStream = new FileInputStream(downloadManager.openDownloadedFile(j).getFileDescriptor());
            GZIPInputStream gZIPInputStream = new GZIPInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gZIPInputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                    sb.append('\n');
                } else {
                    String sb2 = sb.toString();
                    gZIPInputStream.close();
                    fileInputStream.close();
                    return sb2;
                }
            }
        } catch (ZipException e) {
            Log.d("CarrierKeyDownloadManager", "Stream is not gzipped e=" + e);
            return null;
        } catch (IOException e2) {
            Log.e("CarrierKeyDownloadManager", "Unexpected exception in convertToString e=" + e2);
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0097 A[Catch: Exception -> 0x00bf, JSONException -> 0x00d5, TryCatch #2 {JSONException -> 0x00d5, Exception -> 0x00bf, blocks: (B:10:0x001f, B:11:0x0035, B:13:0x003b, B:15:0x0045, B:17:0x0050, B:19:0x0056, B:26:0x0081, B:28:0x0097, B:29:0x009e, B:22:0x0064, B:24:0x006c, B:16:0x004a), top: B:38:0x001f }] */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void parseJsonAndPersistKey(String str, String str2, int i) {
        String string;
        int i2;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || i == -1) {
            Log.e("CarrierKeyDownloadManager", "jsonStr or mcc, mnc: is empty or carrierId is UNKNOWN_CARRIER_ID");
            return;
        }
        try {
            String substring = str2.substring(0, 3);
            String substring2 = str2.substring(3);
            JSONArray jSONArray = new JSONObject(str).getJSONArray("carrier-keys");
            int i3 = 0;
            while (i3 < jSONArray.length()) {
                JSONObject jSONObject = jSONArray.getJSONObject(i3);
                if (jSONObject.has("certificate")) {
                    string = jSONObject.getString("certificate");
                } else {
                    string = jSONObject.getString("public-key");
                }
                if (jSONObject.has("key-type")) {
                    String string2 = jSONObject.getString("key-type");
                    if (!string2.equals("EPDG")) {
                        if (!string2.equals("WLAN")) {
                            Log.e("CarrierKeyDownloadManager", "Invalid key-type specified: " + string2);
                        }
                    } else {
                        i2 = 1;
                        String string3 = jSONObject.getString("key-identifier");
                        Pair<PublicKey, Long> keyInformation = getKeyInformation(cleanCertString(string).getBytes());
                        if (this.mDeleteOldKeyAfterDownload) {
                            this.mPhone.deleteCarrierInfoForImsiEncryption(-1);
                            this.mDeleteOldKeyAfterDownload = false;
                        }
                        int i4 = i3;
                        JSONArray jSONArray2 = jSONArray;
                        savePublicKey((PublicKey) keyInformation.first, i2, string3, ((Long) keyInformation.second).longValue(), substring, substring2, i);
                        i3 = i4 + 1;
                        jSONArray = jSONArray2;
                    }
                }
                i2 = 2;
                String string32 = jSONObject.getString("key-identifier");
                Pair<PublicKey, Long> keyInformation2 = getKeyInformation(cleanCertString(string).getBytes());
                if (this.mDeleteOldKeyAfterDownload) {
                }
                int i42 = i3;
                JSONArray jSONArray22 = jSONArray;
                savePublicKey((PublicKey) keyInformation2.first, i2, string32, ((Long) keyInformation2.second).longValue(), substring, substring2, i);
                i3 = i42 + 1;
                jSONArray = jSONArray22;
            }
        } catch (JSONException e) {
            Log.e("CarrierKeyDownloadManager", "Json parsing error: " + e.getMessage());
        } catch (Exception e2) {
            Log.e("CarrierKeyDownloadManager", "Exception getting certificate: " + e2);
        }
    }

    @VisibleForTesting
    public boolean isKeyEnabled(int i) {
        return isKeyEnabled(i, this.mKeyAvailability);
    }

    @VisibleForTesting
    public boolean areCarrierKeysAbsentOrExpiring() {
        int[] iArr;
        for (int i : CARRIER_KEY_TYPES) {
            if (isKeyEnabled(i)) {
                ImsiEncryptionInfo carrierInfoForImsiEncryption = this.mPhone.getCarrierInfoForImsiEncryption(i, false);
                if (carrierInfoForImsiEncryption == null) {
                    Log.d("CarrierKeyDownloadManager", "Key not found for: " + i);
                    return true;
                } else if (carrierInfoForImsiEncryption.getCarrierId() != -1) {
                    return carrierInfoForImsiEncryption.getExpirationTime().getTime() - System.currentTimeMillis() < 1814400000;
                } else {
                    Log.d("CarrierKeyDownloadManager", "carrier key is unknown carrier, so prefer to reDownload");
                    this.mDeleteOldKeyAfterDownload = true;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean downloadKey() {
        Log.d("CarrierKeyDownloadManager", "starting download from: " + this.mURL);
        String simOperator = getSimOperator();
        int simCarrierId = getSimCarrierId();
        if (!TextUtils.isEmpty(simOperator) || simCarrierId != -1) {
            Log.d("CarrierKeyDownloadManager", "downloading key for mccmnc : " + simOperator + ", carrierId : " + simCarrierId);
            try {
                this.mContext.registerReceiver(this.mDownloadReceiver, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"), null, this.mPhone, 2);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.mURL));
                request.setAllowedOverMetered(this.mAllowedOverMeteredNetwork);
                request.setNotificationVisibility(2);
                request.addRequestHeader("Accept-Encoding", "gzip");
                Long valueOf = Long.valueOf(this.mDownloadManager.enqueue(request));
                Log.d("CarrierKeyDownloadManager", "saving values mccmnc: " + simOperator + ", downloadId: " + valueOf + ", carrierId: " + simCarrierId);
                this.mMccMncForDownload = simOperator;
                this.mCarrierId = simCarrierId;
                this.mDownloadId = valueOf.longValue();
                return true;
            } catch (Exception unused) {
                Log.e("CarrierKeyDownloadManager", "exception trying to download key from url: " + this.mURL);
                return false;
            }
        }
        Log.e("CarrierKeyDownloadManager", "mccmnc or carrierId is UnKnown");
        return false;
    }

    @VisibleForTesting
    public static Pair<PublicKey, Long> getKeyInformation(byte[] bArr) throws Exception {
        X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
        return new Pair<>(x509Certificate.getPublicKey(), Long.valueOf(x509Certificate.getNotAfter().getTime()));
    }

    @VisibleForTesting
    public void savePublicKey(PublicKey publicKey, int i, String str, long j, String str2, String str3, int i2) {
        this.mPhone.setCarrierInfoForImsiEncryption(new ImsiEncryptionInfo(str2, str3, i, str, publicKey, new Date(j), i2));
    }

    @VisibleForTesting
    public static String cleanCertString(String str) {
        return str.substring(str.indexOf("-----BEGIN CERTIFICATE-----"), str.indexOf("-----END CERTIFICATE-----") + 25);
    }
}
