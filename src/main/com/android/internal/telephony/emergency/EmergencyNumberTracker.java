package com.android.internal.telephony.emergency;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.PhoneNumberUtils;
import android.telephony.emergency.EmergencyNumber;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.HalVersion;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.LocaleTracker;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.phonenumbers.ShortNumberInfo;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.phone.ecc.nano.ProtobufEccData$AllInfo;
import com.android.phone.ecc.nano.ProtobufEccData$CountryInfo;
import com.android.phone.ecc.nano.ProtobufEccData$EccInfo;
import com.android.telephony.Rlog;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;
/* loaded from: classes.dex */
public class EmergencyNumberTracker extends Handler {
    public static final int ADD_EMERGENCY_NUMBER_TEST_MODE = 1;
    @VisibleForTesting
    public static final int EVENT_OVERRIDE_OTA_EMERGENCY_NUMBER_DB_FILE_PATH = 6;
    @VisibleForTesting
    public static final int EVENT_UPDATE_OTA_EMERGENCY_NUMBER_DB = 5;
    public static final int REMOVE_EMERGENCY_NUMBER_TEST_MODE = 2;
    public static final int RESET_EMERGENCY_NUMBER_TEST_MODE = 3;
    private final CommandsInterface mCi;
    private String mCountryIso;
    private String[] mEmergencyNumberPrefix;
    private boolean mIsHalVersionLessThan1Dot4;
    private final Phone mPhone;
    private int mPhoneId;
    private Resources mResources;
    private static final String TAG = EmergencyNumberTracker.class.getSimpleName();
    public static boolean DBG = false;
    private ParcelFileDescriptor mOverridedOtaDbParcelFileDescriptor = null;
    private String mLastKnownEmergencyCountryIso = PhoneConfigurationManager.SSSS;
    private int mCurrentDatabaseVersion = -1;
    private Map<String, Set<String>> mNormalRoutedNumbers = new ArrayMap();
    public boolean mIsCountrySetByAnotherSub = false;
    private List<EmergencyNumber> mEmergencyNumberListFromDatabase = new ArrayList();
    private List<EmergencyNumber> mEmergencyNumberListFromRadio = new ArrayList();
    private List<EmergencyNumber> mEmergencyNumberListWithPrefix = new ArrayList();
    private List<EmergencyNumber> mEmergencyNumberListFromTestMode = new ArrayList();
    private List<EmergencyNumber> mEmergencyNumberList = new ArrayList();
    private final LocalLog mEmergencyNumberListDatabaseLocalLog = new LocalLog(16);
    private final LocalLog mEmergencyNumberListRadioLocalLog = new LocalLog(16);
    private final LocalLog mEmergencyNumberListPrefixLocalLog = new LocalLog(16);
    private final LocalLog mEmergencyNumberListTestModeLocalLog = new LocalLog(16);
    private final LocalLog mEmergencyNumberListLocalLog = new LocalLog(16);
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.emergency.EmergencyNumberTracker.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra;
            if (intent.getAction().equals("android.telephony.action.NETWORK_COUNTRY_CHANGED") && (intExtra = intent.getIntExtra("phone", -1)) == EmergencyNumberTracker.this.mPhone.getPhoneId()) {
                String stringExtra = intent.getStringExtra("android.telephony.extra.NETWORK_COUNTRY");
                EmergencyNumberTracker emergencyNumberTracker = EmergencyNumberTracker.this;
                emergencyNumberTracker.logd("ACTION_NETWORK_COUNTRY_CHANGED: PhoneId: " + intExtra + " CountryIso: " + stringExtra);
                EmergencyNumberTracker emergencyNumberTracker2 = EmergencyNumberTracker.this;
                if (stringExtra == null) {
                    stringExtra = PhoneConfigurationManager.SSSS;
                }
                emergencyNumberTracker2.updateEmergencyCountryIsoAllPhones(stringExtra);
            }
        }
    };

    @VisibleForTesting
    public boolean shouldDeterminingOfUrnsAndCategoriesWhileMergingIgnored() {
        return false;
    }

    public EmergencyNumberTracker(Phone phone, CommandsInterface commandsInterface) {
        this.mIsHalVersionLessThan1Dot4 = false;
        this.mResources = null;
        this.mEmergencyNumberPrefix = new String[0];
        this.mPhone = phone;
        this.mCi = commandsInterface;
        this.mResources = phone.getContext().getResources();
        if (phone != null) {
            this.mPhoneId = phone.getPhoneId();
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService("carrier_config");
            if (carrierConfigManager != null) {
                PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(phone.getContext(), phone.getSubId(), new String[]{"emergency_number_prefix_string_array"});
                if (!carrierConfigSubset.isEmpty()) {
                    this.mEmergencyNumberPrefix = carrierConfigSubset.getStringArray("emergency_number_prefix_string_array");
                }
                carrierConfigManager.registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.emergency.EmergencyNumberTracker$$ExternalSyntheticLambda0
                    @Override // java.util.concurrent.Executor
                    public final void execute(Runnable runnable) {
                        EmergencyNumberTracker.this.post(runnable);
                    }
                }, new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.emergency.EmergencyNumberTracker$$ExternalSyntheticLambda1
                    public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                        EmergencyNumberTracker.this.lambda$new$0(i, i2, i3, i4);
                    }
                });
                phone.getContext().registerReceiver(this.mIntentReceiver, new IntentFilter("android.telephony.action.NETWORK_COUNTRY_CHANGED"));
            } else {
                loge("CarrierConfigManager is null.");
            }
            this.mIsHalVersionLessThan1Dot4 = phone.getHalVersion(6).lessOrEqual(new HalVersion(1, 3));
        } else {
            loge("mPhone is null.");
        }
        initializeDatabaseEmergencyNumberList();
        commandsInterface.registerForEmergencyNumberList(this, 1, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        onCarrierConfigUpdated(i);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Object obj = asyncResult.result;
                if (obj == null) {
                    loge("EVENT_UNSOL_EMERGENCY_NUMBER_LIST: Result from RIL is null.");
                    return;
                } else if (obj != null && asyncResult.exception == null) {
                    updateRadioEmergencyNumberListAndNotify((List) obj);
                    return;
                } else {
                    loge("EVENT_UNSOL_EMERGENCY_NUMBER_LIST: Exception from RIL : " + asyncResult.exception);
                    return;
                }
            case 2:
                Object obj2 = message.obj;
                if (obj2 == null) {
                    loge("EVENT_UPDATE_DB_COUNTRY_ISO_CHANGED: Result from UpdateCountryIso is null.");
                    return;
                } else {
                    updateEmergencyNumberListDatabaseAndNotify((String) obj2);
                    return;
                }
            case 3:
                Object obj3 = message.obj;
                if (obj3 == null && message.arg1 != 3) {
                    loge("EVENT_UPDATE_EMERGENCY_NUMBER_TEST_MODE: Result from executeEmergencyNumberTestModeCommand is null.");
                    return;
                } else {
                    updateEmergencyNumberListTestModeAndNotify(message.arg1, (EmergencyNumber) obj3);
                    return;
                }
            case 4:
                Object obj4 = message.obj;
                if (obj4 == null) {
                    loge("EVENT_UPDATE_EMERGENCY_NUMBER_PREFIX: Result from onCarrierConfigChanged is null.");
                    return;
                } else {
                    updateEmergencyNumberPrefixAndNotify((String[]) obj4);
                    return;
                }
            case 5:
                updateOtaEmergencyNumberListDatabaseAndNotify();
                return;
            case 6:
                Object obj5 = message.obj;
                if (obj5 == null) {
                    overrideOtaEmergencyNumberDbFilePath(null);
                    return;
                } else {
                    overrideOtaEmergencyNumberDbFilePath((ParcelFileDescriptor) obj5);
                    return;
                }
            default:
                return;
        }
    }

    private boolean isAirplaneModeEnabled() {
        ServiceStateTracker serviceStateTracker = this.mPhone.getServiceStateTracker();
        return serviceStateTracker != null && serviceStateTracker.getServiceState().getState() == 3;
    }

    @VisibleForTesting
    public boolean isSimAbsent() {
        Phone[] phones;
        int slotIndex;
        for (Phone phone : PhoneFactory.getPhones()) {
            if (phone.isSubscriptionManagerServiceEnabled()) {
                slotIndex = SubscriptionManagerService.getInstance().getSlotIndex(phone.getSubId());
            } else {
                slotIndex = SubscriptionController.getInstance().getSlotIndex(phone.getSubId());
            }
            if (slotIndex != -1) {
                logd("found sim in slotId: " + slotIndex + " subid: " + phone.getSubId());
                return false;
            }
        }
        return true;
    }

    private void initializeDatabaseEmergencyNumberList() {
        if (this.mCountryIso == null) {
            String lowerCase = getInitialCountryIso().toLowerCase(Locale.ROOT);
            updateEmergencyCountryIso(lowerCase);
            if (TextUtils.isEmpty(lowerCase) && isAirplaneModeEnabled()) {
                lowerCase = getCountryIsoForCachingDatabase();
            }
            cacheEmergencyDatabaseByCountry(lowerCase);
        }
    }

    @VisibleForTesting
    public void updateEmergencyCountryIsoAllPhones(String str) {
        Phone[] phones;
        this.mIsCountrySetByAnotherSub = false;
        updateEmergencyNumberDatabaseCountryChange(str);
        for (Phone phone : PhoneFactory.getPhones()) {
            if (phone.getPhoneId() != this.mPhone.getPhoneId() && phone.getEmergencyNumberTracker() != null) {
                EmergencyNumberTracker emergencyNumberTracker = phone.getEmergencyNumberTracker();
                if (!TextUtils.isEmpty(str) && (TextUtils.isEmpty(emergencyNumberTracker.getEmergencyCountryIso()) || emergencyNumberTracker.mIsCountrySetByAnotherSub)) {
                    emergencyNumberTracker.mIsCountrySetByAnotherSub = true;
                    emergencyNumberTracker.updateEmergencyNumberDatabaseCountryChange(str);
                }
            }
        }
    }

    private void onCarrierConfigUpdated(int i) {
        Phone phone = this.mPhone;
        if (phone != null) {
            if (i != phone.getPhoneId()) {
                return;
            }
            PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{"emergency_number_prefix_string_array"});
            if (carrierConfigSubset.isEmpty()) {
                return;
            }
            String[] stringArray = carrierConfigSubset.getStringArray("emergency_number_prefix_string_array");
            if (Arrays.equals(this.mEmergencyNumberPrefix, stringArray)) {
                return;
            }
            obtainMessage(4, stringArray).sendToTarget();
            return;
        }
        loge("onCarrierConfigurationChanged mPhone is null.");
    }

    private String getInitialCountryIso() {
        LocaleTracker localeTracker;
        Phone phone = this.mPhone;
        if (phone != null) {
            ServiceStateTracker serviceStateTracker = phone.getServiceStateTracker();
            return (serviceStateTracker == null || (localeTracker = serviceStateTracker.getLocaleTracker()) == null) ? PhoneConfigurationManager.SSSS : localeTracker.getCurrentCountry();
        }
        loge("getInitialCountryIso mPhone is null.");
        return PhoneConfigurationManager.SSSS;
    }

    public void updateEmergencyNumberDatabaseCountryChange(String str) {
        obtainMessage(2, str).sendToTarget();
    }

    public void updateOtaEmergencyNumberDatabase() {
        obtainMessage(5).sendToTarget();
    }

    public void updateOtaEmergencyNumberDbFilePath(ParcelFileDescriptor parcelFileDescriptor) {
        obtainMessage(6, parcelFileDescriptor).sendToTarget();
    }

    public void resetOtaEmergencyNumberDbFilePath() {
        obtainMessage(6, null).sendToTarget();
    }

    private EmergencyNumber convertEmergencyNumberFromEccInfo(ProtobufEccData$EccInfo protobufEccData$EccInfo, String str, int i) {
        String trim = protobufEccData$EccInfo.phoneNumber.trim();
        if (trim.isEmpty()) {
            loge("EccInfo has empty phone number.");
            return null;
        }
        int i2 = 0;
        for (int i3 : protobufEccData$EccInfo.types) {
            switch (i3) {
                case 1:
                    i2 |= 1;
                    break;
                case 2:
                    i2 |= 2;
                    break;
                case 3:
                    i2 |= 4;
                    break;
                case 4:
                    i2 |= 8;
                    break;
                case 5:
                    i2 |= 16;
                    break;
                case 6:
                    i2 |= 32;
                    break;
                case 7:
                    i2 |= 64;
                    break;
            }
        }
        return new EmergencyNumber(trim, str, PhoneConfigurationManager.SSSS, i2, new ArrayList(), 16, i);
    }

    private int getRoutingInfoFromDB(ProtobufEccData$EccInfo protobufEccData$EccInfo, Map<String, Set<String>> map) {
        String[] strArr;
        int i = protobufEccData$EccInfo.routing;
        int i2 = i != 1 ? i != 2 ? 0 : 2 : 1;
        String trim = protobufEccData$EccInfo.phoneNumber.trim();
        if (trim.isEmpty()) {
            loge("EccInfo has empty phone number.");
            return i2;
        } else if (protobufEccData$EccInfo.routing == 2) {
            String[] strArr2 = protobufEccData$EccInfo.normalRoutingMncs;
            if (strArr2.length == 0 || strArr2[0].length() <= 0) {
                return 2;
            }
            for (String str : protobufEccData$EccInfo.normalRoutingMncs) {
                if (!map.containsKey(str)) {
                    ArraySet arraySet = new ArraySet();
                    arraySet.add(trim);
                    map.put(str, arraySet);
                } else {
                    Set<String> set = map.get(str);
                    if (!set.contains(trim)) {
                        set.add(trim);
                    }
                }
            }
            logd("Normal routed mncs with phoneNumbers:" + map);
            return 0;
        } else {
            return i2;
        }
    }

    private void cacheEmergencyDatabaseByCountry(String str) {
        ProtobufEccData$EccInfo[] protobufEccData$EccInfoArr;
        ArrayMap arrayMap = new ArrayMap();
        ArrayList arrayList = new ArrayList();
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(this.mPhone.getContext().getAssets().open("eccdata"));
            GZIPInputStream gZIPInputStream = new GZIPInputStream(bufferedInputStream);
            ProtobufEccData$AllInfo parseFrom = ProtobufEccData$AllInfo.parseFrom(readInputStreamToByteArray(gZIPInputStream));
            int i = parseFrom.revision;
            logd(str + " asset emergency database is loaded. Ver: " + i + " Phone Id: " + this.mPhone.getPhoneId() + " countryIso: " + str);
            ProtobufEccData$CountryInfo[] protobufEccData$CountryInfoArr = parseFrom.countries;
            int length = protobufEccData$CountryInfoArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                ProtobufEccData$CountryInfo protobufEccData$CountryInfo = protobufEccData$CountryInfoArr[i2];
                if (protobufEccData$CountryInfo.isoCode.equals(str.toUpperCase(Locale.ROOT))) {
                    for (ProtobufEccData$EccInfo protobufEccData$EccInfo : protobufEccData$CountryInfo.eccs) {
                        arrayList.add(convertEmergencyNumberFromEccInfo(protobufEccData$EccInfo, str, !shouldEmergencyNumberRoutingFromDbBeIgnored() ? getRoutingInfoFromDB(protobufEccData$EccInfo, arrayMap) : 0));
                    }
                }
            }
            EmergencyNumber.mergeSameNumbersInEmergencyNumberList(arrayList);
            gZIPInputStream.close();
            bufferedInputStream.close();
            int cacheOtaEmergencyNumberDatabase = cacheOtaEmergencyNumberDatabase();
            if (cacheOtaEmergencyNumberDatabase == -1 && i == -1) {
                loge("No database available. Phone Id: " + this.mPhone.getPhoneId());
            } else if (i > cacheOtaEmergencyNumberDatabase) {
                logd("Using Asset Emergency database. Version: " + i);
                this.mCurrentDatabaseVersion = i;
                this.mEmergencyNumberListFromDatabase = arrayList;
                this.mNormalRoutedNumbers.clear();
                this.mNormalRoutedNumbers = arrayMap;
            } else {
                logd("Using Ota Emergency database. Version: " + cacheOtaEmergencyNumberDatabase);
            }
        } catch (IOException e) {
            logw("Cache asset emergency database failure: " + e);
        }
    }

    private int cacheOtaEmergencyNumberDatabase() {
        File absoluteFile;
        ProtobufEccData$CountryInfo[] protobufEccData$CountryInfoArr;
        int i;
        ArrayMap arrayMap = new ArrayMap();
        ArrayList arrayList = new ArrayList();
        ParcelFileDescriptor parcelFileDescriptor = this.mOverridedOtaDbParcelFileDescriptor;
        if (parcelFileDescriptor == null) {
            absoluteFile = new File(Environment.getDataDirectory(), "misc/emergencynumberdb/emergency_number_db");
        } else {
            try {
                absoluteFile = ParcelFileDescriptor.getFile(parcelFileDescriptor.getFileDescriptor()).getAbsoluteFile();
            } catch (IOException e) {
                loge("Cache ota emergency database IOException: " + e);
                return -1;
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(absoluteFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            try {
                GZIPInputStream gZIPInputStream = new GZIPInputStream(bufferedInputStream);
                ProtobufEccData$AllInfo parseFrom = ProtobufEccData$AllInfo.parseFrom(readInputStreamToByteArray(gZIPInputStream));
                String lastKnownEmergencyCountryIso = getLastKnownEmergencyCountryIso();
                logd(lastKnownEmergencyCountryIso + " ota emergency database is loaded. Ver: -1");
                int i2 = parseFrom.revision;
                ProtobufEccData$CountryInfo[] protobufEccData$CountryInfoArr2 = parseFrom.countries;
                int length = protobufEccData$CountryInfoArr2.length;
                int i3 = 0;
                while (i3 < length) {
                    ProtobufEccData$CountryInfo protobufEccData$CountryInfo = protobufEccData$CountryInfoArr2[i3];
                    if (protobufEccData$CountryInfo.isoCode.equals(lastKnownEmergencyCountryIso.toUpperCase(Locale.ROOT))) {
                        ProtobufEccData$EccInfo[] protobufEccData$EccInfoArr = protobufEccData$CountryInfo.eccs;
                        int length2 = protobufEccData$EccInfoArr.length;
                        int i4 = 0;
                        while (i4 < length2) {
                            ProtobufEccData$EccInfo protobufEccData$EccInfo = protobufEccData$EccInfoArr[i4];
                            if (shouldEmergencyNumberRoutingFromDbBeIgnored()) {
                                protobufEccData$CountryInfoArr = protobufEccData$CountryInfoArr2;
                                i = 0;
                            } else {
                                protobufEccData$CountryInfoArr = protobufEccData$CountryInfoArr2;
                                i = getRoutingInfoFromDB(protobufEccData$EccInfo, arrayMap);
                            }
                            arrayList.add(convertEmergencyNumberFromEccInfo(protobufEccData$EccInfo, lastKnownEmergencyCountryIso, i));
                            i4++;
                            protobufEccData$CountryInfoArr2 = protobufEccData$CountryInfoArr;
                        }
                    }
                    i3++;
                    protobufEccData$CountryInfoArr2 = protobufEccData$CountryInfoArr2;
                }
                EmergencyNumber.mergeSameNumbersInEmergencyNumberList(arrayList);
                gZIPInputStream.close();
                bufferedInputStream.close();
                fileInputStream.close();
                if (i2 != -1 && this.mCurrentDatabaseVersion < i2) {
                    this.mCurrentDatabaseVersion = i2;
                    this.mEmergencyNumberListFromDatabase = arrayList;
                    this.mNormalRoutedNumbers.clear();
                    this.mNormalRoutedNumbers = arrayMap;
                }
                return i2;
            } catch (Throwable th) {
                try {
                    bufferedInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e2) {
            loge("Cache ota emergency database IOException: " + e2);
            return -1;
        }
    }

    private static byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[16384];
        while (true) {
            int read = inputStream.read(bArr, 0, 16384);
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                byteArrayOutputStream.flush();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    private void updateRadioEmergencyNumberListAndNotify(List<EmergencyNumber> list) {
        Collections.sort(list);
        logd("updateRadioEmergencyNumberListAndNotify(): receiving " + list);
        if (list.equals(this.mEmergencyNumberListFromRadio)) {
            return;
        }
        try {
            EmergencyNumber.mergeSameNumbersInEmergencyNumberList(list);
            writeUpdatedEmergencyNumberListMetrics(list);
            this.mEmergencyNumberListFromRadio = list;
            if (!DBG) {
                LocalLog localLog = this.mEmergencyNumberListRadioLocalLog;
                localLog.log("updateRadioEmergencyNumberList:" + list);
            }
            updateEmergencyNumberList();
            if (!DBG) {
                LocalLog localLog2 = this.mEmergencyNumberListLocalLog;
                localLog2.log("updateRadioEmergencyNumberListAndNotify:" + this.mEmergencyNumberList);
            }
            notifyEmergencyNumberList();
        } catch (NullPointerException e) {
            loge("updateRadioEmergencyNumberListAndNotify() Phone already destroyed: " + e + " EmergencyNumberList not notified");
        }
    }

    private void updateEmergencyNumberListDatabaseAndNotify(String str) {
        logd("updateEmergencyNumberListDatabaseAndNotify(): receiving countryIso: " + str);
        updateEmergencyCountryIso(str.toLowerCase(Locale.ROOT));
        if (TextUtils.isEmpty(str) && isAirplaneModeEnabled()) {
            str = getCountryIsoForCachingDatabase();
            logd("updateEmergencyNumberListDatabaseAndNotify(): using cached APM country " + str);
        }
        cacheEmergencyDatabaseByCountry(str);
        writeUpdatedEmergencyNumberListMetrics(this.mEmergencyNumberListFromDatabase);
        if (!DBG) {
            LocalLog localLog = this.mEmergencyNumberListDatabaseLocalLog;
            localLog.log("updateEmergencyNumberListDatabaseAndNotify:" + this.mEmergencyNumberListFromDatabase);
        }
        updateEmergencyNumberList();
        if (!DBG) {
            LocalLog localLog2 = this.mEmergencyNumberListLocalLog;
            localLog2.log("updateEmergencyNumberListDatabaseAndNotify:" + this.mEmergencyNumberList);
        }
        notifyEmergencyNumberList();
    }

    private void overrideOtaEmergencyNumberDbFilePath(ParcelFileDescriptor parcelFileDescriptor) {
        logd("overrideOtaEmergencyNumberDbFilePath:" + parcelFileDescriptor);
        this.mOverridedOtaDbParcelFileDescriptor = parcelFileDescriptor;
    }

    private void updateOtaEmergencyNumberListDatabaseAndNotify() {
        logd("updateOtaEmergencyNumberListDatabaseAndNotify(): receiving Emegency Number database OTA update");
        if (cacheOtaEmergencyNumberDatabase() != -1) {
            writeUpdatedEmergencyNumberListMetrics(this.mEmergencyNumberListFromDatabase);
            if (!DBG) {
                LocalLog localLog = this.mEmergencyNumberListDatabaseLocalLog;
                localLog.log("updateOtaEmergencyNumberListDatabaseAndNotify:" + this.mEmergencyNumberListFromDatabase);
            }
            updateEmergencyNumberList();
            if (!DBG) {
                LocalLog localLog2 = this.mEmergencyNumberListLocalLog;
                localLog2.log("updateOtaEmergencyNumberListDatabaseAndNotify:" + this.mEmergencyNumberList);
            }
            notifyEmergencyNumberList();
        }
    }

    private void updateEmergencyNumberPrefixAndNotify(String[] strArr) {
        logd("updateEmergencyNumberPrefixAndNotify(): receiving emergencyNumberPrefix: " + Arrays.toString(strArr));
        this.mEmergencyNumberPrefix = strArr;
        updateEmergencyNumberList();
        if (!DBG) {
            LocalLog localLog = this.mEmergencyNumberListLocalLog;
            localLog.log("updateEmergencyNumberPrefixAndNotify:" + this.mEmergencyNumberList);
        }
        notifyEmergencyNumberList();
    }

    private void notifyEmergencyNumberList() {
        try {
            if (getEmergencyNumberList() != null) {
                this.mPhone.notifyEmergencyNumberList();
                logd("notifyEmergencyNumberList(): notified");
            }
        } catch (NullPointerException e) {
            loge("notifyEmergencyNumberList(): failure: Phone already destroyed: " + e);
        }
    }

    private void updateEmergencyNumberList() {
        ArrayList arrayList = new ArrayList(this.mEmergencyNumberListFromDatabase);
        arrayList.addAll(this.mEmergencyNumberListFromRadio);
        this.mEmergencyNumberListWithPrefix.clear();
        if (this.mEmergencyNumberPrefix.length != 0) {
            this.mEmergencyNumberListWithPrefix.addAll(getEmergencyNumberListWithPrefix(this.mEmergencyNumberListFromRadio));
            this.mEmergencyNumberListWithPrefix.addAll(getEmergencyNumberListWithPrefix(this.mEmergencyNumberListFromDatabase));
        }
        if (!DBG) {
            LocalLog localLog = this.mEmergencyNumberListPrefixLocalLog;
            localLog.log("updateEmergencyNumberList:" + this.mEmergencyNumberListWithPrefix);
        }
        arrayList.addAll(this.mEmergencyNumberListWithPrefix);
        arrayList.addAll(this.mEmergencyNumberListFromTestMode);
        if (shouldDeterminingOfUrnsAndCategoriesWhileMergingIgnored()) {
            EmergencyNumber.mergeSameNumbersInEmergencyNumberList(arrayList);
        } else {
            EmergencyNumber.mergeSameNumbersInEmergencyNumberList(arrayList, true);
        }
        this.mEmergencyNumberList = arrayList;
    }

    public List<EmergencyNumber> getEmergencyNumberList() {
        List<EmergencyNumber> emergencyNumberListFromEccListDatabaseAndTest;
        if (!this.mEmergencyNumberListFromRadio.isEmpty()) {
            emergencyNumberListFromEccListDatabaseAndTest = Collections.unmodifiableList(this.mEmergencyNumberList);
        } else {
            emergencyNumberListFromEccListDatabaseAndTest = getEmergencyNumberListFromEccListDatabaseAndTest();
        }
        return shouldAdjustForRouting() ? adjustRoutingForEmergencyNumbers(emergencyNumberListFromEccListDatabaseAndTest) : emergencyNumberListFromEccListDatabaseAndTest;
    }

    private boolean shouldAdjustForRouting() {
        return (shouldEmergencyNumberRoutingFromDbBeIgnored() || this.mNormalRoutedNumbers.isEmpty()) ? false : true;
    }

    private List<EmergencyNumber> adjustRoutingForEmergencyNumbers(List<EmergencyNumber> list) {
        int i;
        String str;
        CellIdentity currentCellIdentity = this.mPhone.getCurrentCellIdentity();
        if (currentCellIdentity != null) {
            String mncString = currentCellIdentity.getMncString();
            Set<String> set = this.mNormalRoutedNumbers.get(mncString);
            ArraySet arraySet = new ArraySet();
            if (set != null && !set.isEmpty()) {
                for (String str2 : set) {
                    Set<String> addPrefixToEmergencyNumber = addPrefixToEmergencyNumber(str2);
                    if (addPrefixToEmergencyNumber != null && !addPrefixToEmergencyNumber.isEmpty()) {
                        arraySet.addAll(addPrefixToEmergencyNumber);
                    }
                }
            }
            ArrayList arrayList = new ArrayList();
            for (EmergencyNumber emergencyNumber : list) {
                int emergencyCallRouting = emergencyNumber.getEmergencyCallRouting();
                String mnc = emergencyNumber.getMnc();
                if (emergencyNumber.isFromSources(16)) {
                    if ((set != null && set.contains(emergencyNumber.getNumber())) || arraySet.contains(emergencyNumber.getNumber())) {
                        logd("adjustRoutingForEmergencyNumbers for number" + emergencyNumber.getNumber());
                        str = mncString;
                        i = 2;
                        arrayList.add(new EmergencyNumber(emergencyNumber.getNumber(), emergencyNumber.getCountryIso(), str, emergencyNumber.getEmergencyServiceCategoryBitmask(), emergencyNumber.getEmergencyUrns(), emergencyNumber.getEmergencyNumberSourceBitmask(), i));
                    } else if (emergencyCallRouting == 0) {
                        emergencyCallRouting = 1;
                    }
                }
                i = emergencyCallRouting;
                str = mnc;
                arrayList.add(new EmergencyNumber(emergencyNumber.getNumber(), emergencyNumber.getCountryIso(), str, emergencyNumber.getEmergencyServiceCategoryBitmask(), emergencyNumber.getEmergencyUrns(), emergencyNumber.getEmergencyNumberSourceBitmask(), i));
            }
            return arrayList;
        }
        return list;
    }

    private Set<String> addPrefixToEmergencyNumber(String str) {
        String[] strArr;
        ArraySet arraySet = new ArraySet();
        for (String str2 : this.mEmergencyNumberPrefix) {
            if (!str.startsWith(str2)) {
                arraySet.add(str2 + str);
            }
        }
        return arraySet;
    }

    public boolean isEmergencyNumber(String str) {
        if (str == null || PhoneNumberUtils.isUriNumber(str)) {
            return false;
        }
        String extractNetworkPortionAlt = PhoneNumberUtils.extractNetworkPortionAlt(str);
        if (!this.mEmergencyNumberListFromRadio.isEmpty()) {
            for (EmergencyNumber emergencyNumber : this.mEmergencyNumberList) {
                if (emergencyNumber.getNumber().equals(extractNetworkPortionAlt)) {
                    logd("Found in mEmergencyNumberList");
                    return true;
                }
            }
            return false;
        }
        boolean isEmergencyNumberFromEccList = isEmergencyNumberFromEccList(extractNetworkPortionAlt);
        boolean isEmergencyNumberFromDatabase = isEmergencyNumberFromDatabase(extractNetworkPortionAlt);
        boolean isEmergencyNumberForTest = isEmergencyNumberForTest(extractNetworkPortionAlt);
        logd("Search results - inRilEccList:" + isEmergencyNumberFromEccList + " inEmergencyNumberDb:" + isEmergencyNumberFromDatabase + " inEmergencyNumberTestList: " + isEmergencyNumberForTest);
        return isEmergencyNumberFromEccList || isEmergencyNumberFromDatabase || isEmergencyNumberForTest;
    }

    public EmergencyNumber getEmergencyNumber(String str) {
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        for (EmergencyNumber emergencyNumber : getEmergencyNumberList()) {
            if (emergencyNumber.getNumber().equals(stripSeparators)) {
                return emergencyNumber;
            }
        }
        return null;
    }

    public int getEmergencyServiceCategories(String str) {
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        for (EmergencyNumber emergencyNumber : getEmergencyNumberList()) {
            if (emergencyNumber.getNumber().equals(stripSeparators) && (emergencyNumber.isFromSources(1) || emergencyNumber.isFromSources(2))) {
                return emergencyNumber.getEmergencyServiceCategoryBitmask();
            }
        }
        return 0;
    }

    public int getEmergencyCallRouting(String str) {
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        for (EmergencyNumber emergencyNumber : getEmergencyNumberList()) {
            if (emergencyNumber.getNumber().equals(stripSeparators) && emergencyNumber.isFromSources(16)) {
                return emergencyNumber.getEmergencyCallRouting();
            }
        }
        return 0;
    }

    public String getEmergencyCountryIso() {
        return this.mCountryIso;
    }

    public String getLastKnownEmergencyCountryIso() {
        return this.mLastKnownEmergencyCountryIso;
    }

    private String getCountryIsoForCachingDatabase() {
        LocaleTracker localeTracker;
        ServiceStateTracker serviceStateTracker = this.mPhone.getServiceStateTracker();
        return (serviceStateTracker == null || (localeTracker = serviceStateTracker.getLocaleTracker()) == null) ? PhoneConfigurationManager.SSSS : localeTracker.getLastKnownCountryIso();
    }

    public int getEmergencyNumberDbVersion() {
        return this.mCurrentDatabaseVersion;
    }

    private synchronized void updateEmergencyCountryIso(String str) {
        this.mCountryIso = str;
        if (!TextUtils.isEmpty(str)) {
            this.mLastKnownEmergencyCountryIso = this.mCountryIso;
        }
        this.mCurrentDatabaseVersion = -1;
    }

    private List<EmergencyNumber> getEmergencyNumberListFromEccList() {
        ArrayList arrayList = new ArrayList();
        if (this.mIsHalVersionLessThan1Dot4) {
            arrayList.addAll(getEmergencyNumberListFromEccListForHalv1_3());
        }
        for (String str : (isSimAbsent() ? "112,911,000,08,110,118,119,999" : "112,911").split(",")) {
            arrayList.add(getLabeledEmergencyNumberForEcclist(str));
        }
        if (this.mEmergencyNumberPrefix.length != 0) {
            arrayList.addAll(getEmergencyNumberListWithPrefix(arrayList));
        }
        EmergencyNumber.mergeSameNumbersInEmergencyNumberList(arrayList);
        return arrayList;
    }

    private String getEmergencyNumberListForHalv1_3() {
        int slotIndex;
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            slotIndex = SubscriptionManagerService.getInstance().getSlotIndex(this.mPhone.getSubId());
        } else {
            slotIndex = SubscriptionController.getInstance().getSlotIndex(this.mPhone.getSubId());
        }
        String str = "ril.ecclist";
        if (slotIndex > 0) {
            str = "ril.ecclist" + slotIndex;
        }
        String str2 = SystemProperties.get(str, PhoneConfigurationManager.SSSS);
        if (TextUtils.isEmpty(str2)) {
            str2 = SystemProperties.get("ro.ril.ecclist");
        }
        logd(str + " emergencyNumbers: " + str2);
        return str2;
    }

    private List<EmergencyNumber> getEmergencyNumberListFromEccListForHalv1_3() {
        ArrayList arrayList = new ArrayList();
        String emergencyNumberListForHalv1_3 = getEmergencyNumberListForHalv1_3();
        if (!TextUtils.isEmpty(emergencyNumberListForHalv1_3)) {
            for (String str : emergencyNumberListForHalv1_3.split(",")) {
                arrayList.add(getLabeledEmergencyNumberForEcclist(str));
            }
        }
        return arrayList;
    }

    private List<EmergencyNumber> getEmergencyNumberListWithPrefix(List<EmergencyNumber> list) {
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            for (EmergencyNumber emergencyNumber : list) {
                Set<String> addPrefixToEmergencyNumber = addPrefixToEmergencyNumber(emergencyNumber.getNumber());
                if (addPrefixToEmergencyNumber != null && !addPrefixToEmergencyNumber.isEmpty()) {
                    for (String str : addPrefixToEmergencyNumber) {
                        arrayList.add(new EmergencyNumber(str, emergencyNumber.getCountryIso(), emergencyNumber.getMnc(), emergencyNumber.getEmergencyServiceCategoryBitmask(), emergencyNumber.getEmergencyUrns(), emergencyNumber.getEmergencyNumberSourceBitmask(), emergencyNumber.getEmergencyCallRouting()));
                    }
                }
            }
        }
        return arrayList;
    }

    private boolean isEmergencyNumberForTest(String str) {
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        for (EmergencyNumber emergencyNumber : this.mEmergencyNumberListFromTestMode) {
            if (emergencyNumber.getNumber().equals(stripSeparators)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmergencyNumberFromDatabase(String str) {
        if (this.mEmergencyNumberListFromDatabase.isEmpty()) {
            return false;
        }
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        for (EmergencyNumber emergencyNumber : this.mEmergencyNumberListFromDatabase) {
            if (emergencyNumber.getNumber().equals(stripSeparators)) {
                return true;
            }
        }
        for (EmergencyNumber emergencyNumber2 : getEmergencyNumberListWithPrefix(this.mEmergencyNumberListFromDatabase)) {
            if (emergencyNumber2.getNumber().equals(stripSeparators)) {
                return true;
            }
        }
        return false;
    }

    private EmergencyNumber getLabeledEmergencyNumberForEcclist(String str) {
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        for (EmergencyNumber emergencyNumber : this.mEmergencyNumberListFromDatabase) {
            if (emergencyNumber.getNumber().equals(stripSeparators)) {
                return new EmergencyNumber(stripSeparators, getLastKnownEmergencyCountryIso().toLowerCase(Locale.ROOT), PhoneConfigurationManager.SSSS, emergencyNumber.getEmergencyServiceCategoryBitmask(), new ArrayList(), 16, emergencyNumber.getEmergencyCallRouting());
            }
        }
        return new EmergencyNumber(stripSeparators, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, 0, new ArrayList(), 0, 0);
    }

    private boolean isEmergencyNumberFromEccList(String str) {
        String[] split;
        if (str == null) {
            return false;
        }
        String lastKnownEmergencyCountryIso = getLastKnownEmergencyCountryIso();
        logd("country:" + lastKnownEmergencyCountryIso);
        if (this.mIsHalVersionLessThan1Dot4) {
            String emergencyNumberListForHalv1_3 = getEmergencyNumberListForHalv1_3();
            if (!TextUtils.isEmpty(emergencyNumberListForHalv1_3)) {
                return isEmergencyNumberFromEccListForHalv1_3(str, emergencyNumberListForHalv1_3);
            }
        }
        logd("System property doesn't provide any emergency numbers. Use embedded logic for determining ones.");
        for (String str2 : (isSimAbsent() ? "112,911,000,08,110,118,119,999" : "112,911").split(",")) {
            if (str.equals(str2)) {
                return true;
            }
            for (String str3 : this.mEmergencyNumberPrefix) {
                if (str.equals(str3 + str2)) {
                    return true;
                }
            }
        }
        if (isSimAbsent() && lastKnownEmergencyCountryIso != null) {
            ShortNumberInfo shortNumberInfo = ShortNumberInfo.getInstance();
            if (shortNumberInfo.isEmergencyNumber(str, lastKnownEmergencyCountryIso.toUpperCase(Locale.ROOT))) {
                return true;
            }
            for (String str4 : this.mEmergencyNumberPrefix) {
                if (shortNumberInfo.isEmergencyNumber(str4 + str, lastKnownEmergencyCountryIso.toUpperCase(Locale.ROOT))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEmergencyNumberFromEccListForHalv1_3(String str, String str2) {
        String[] split;
        for (String str3 : str2.split(",")) {
            if (str.equals(str3)) {
                return true;
            }
            for (String str4 : this.mEmergencyNumberPrefix) {
                if (str.equals(str4 + str3)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void executeEmergencyNumberTestModeCommand(int i, EmergencyNumber emergencyNumber) {
        obtainMessage(3, i, 0, emergencyNumber).sendToTarget();
    }

    private void updateEmergencyNumberListTestModeAndNotify(int i, EmergencyNumber emergencyNumber) {
        if (i == 1) {
            if (!isEmergencyNumber(emergencyNumber.getNumber())) {
                this.mEmergencyNumberListFromTestMode.add(emergencyNumber);
            }
        } else if (i == 3) {
            this.mEmergencyNumberListFromTestMode.clear();
        } else if (i == 2) {
            this.mEmergencyNumberListFromTestMode.remove(emergencyNumber);
        } else {
            loge("updateEmergencyNumberListTestModeAndNotify: Unexpected action in test mode.");
            return;
        }
        if (!DBG) {
            LocalLog localLog = this.mEmergencyNumberListTestModeLocalLog;
            localLog.log("updateEmergencyNumberListTestModeAndNotify:" + this.mEmergencyNumberListFromTestMode);
        }
        updateEmergencyNumberList();
        if (!DBG) {
            LocalLog localLog2 = this.mEmergencyNumberListLocalLog;
            localLog2.log("updateEmergencyNumberListTestModeAndNotify:" + this.mEmergencyNumberList);
        }
        notifyEmergencyNumberList();
    }

    private List<EmergencyNumber> getEmergencyNumberListFromEccListDatabaseAndTest() {
        List<EmergencyNumber> emergencyNumberListFromEccList = getEmergencyNumberListFromEccList();
        if (!this.mEmergencyNumberListFromDatabase.isEmpty()) {
            loge("getEmergencyNumberListFromEccListDatabaseAndTest: radio indication is unavailable in 1.4 HAL.");
            emergencyNumberListFromEccList.addAll(this.mEmergencyNumberListFromDatabase);
            emergencyNumberListFromEccList.addAll(getEmergencyNumberListWithPrefix(this.mEmergencyNumberListFromDatabase));
        }
        emergencyNumberListFromEccList.addAll(getEmergencyNumberListTestMode());
        if (shouldDeterminingOfUrnsAndCategoriesWhileMergingIgnored()) {
            EmergencyNumber.mergeSameNumbersInEmergencyNumberList(emergencyNumberListFromEccList);
        } else {
            EmergencyNumber.mergeSameNumbersInEmergencyNumberList(emergencyNumberListFromEccList, true);
        }
        return emergencyNumberListFromEccList;
    }

    public List<EmergencyNumber> getEmergencyNumberListTestMode() {
        return Collections.unmodifiableList(this.mEmergencyNumberListFromTestMode);
    }

    @VisibleForTesting
    public List<EmergencyNumber> getRadioEmergencyNumberList() {
        return new ArrayList(this.mEmergencyNumberListFromRadio);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String str) {
        String str2 = TAG;
        Rlog.d(str2, "[" + this.mPhoneId + "]" + str);
    }

    private void logw(String str) {
        String str2 = TAG;
        Rlog.w(str2, "[" + this.mPhoneId + "]" + str);
    }

    private void loge(String str) {
        String str2 = TAG;
        Rlog.e(str2, "[" + this.mPhoneId + "]" + str);
    }

    private void writeUpdatedEmergencyNumberListMetrics(List<EmergencyNumber> list) {
        if (list == null) {
            return;
        }
        for (EmergencyNumber emergencyNumber : list) {
            TelephonyMetrics.getInstance().writeEmergencyNumberUpdateEvent(this.mPhone.getPhoneId(), emergencyNumber, getEmergencyNumberDbVersion());
        }
    }

    @VisibleForTesting
    public boolean shouldModemConfigEmergencyNumbersBeIgnored() {
        return this.mResources.getBoolean(17891901);
    }

    @VisibleForTesting
    public boolean shouldEmergencyNumberRoutingFromDbBeIgnored() {
        return this.mResources.getBoolean(17891900);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println(" Hal Version:" + this.mPhone.getHalVersion(6));
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println(" Country Iso:" + getEmergencyCountryIso());
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println(" Database Version:" + getEmergencyNumberDbVersion());
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println("mEmergencyNumberListDatabaseLocalLog:");
        indentingPrintWriter.increaseIndent();
        this.mEmergencyNumberListDatabaseLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println("mEmergencyNumberListRadioLocalLog:");
        indentingPrintWriter.increaseIndent();
        this.mEmergencyNumberListRadioLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println("mEmergencyNumberListPrefixLocalLog:");
        indentingPrintWriter.increaseIndent();
        this.mEmergencyNumberListPrefixLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println("mEmergencyNumberListTestModeLocalLog:");
        indentingPrintWriter.increaseIndent();
        this.mEmergencyNumberListTestModeLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.println("mEmergencyNumberListLocalLog (valid >= 1.4 HAL):");
        indentingPrintWriter.increaseIndent();
        this.mEmergencyNumberListLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" ========================================= ");
        if (this.mIsHalVersionLessThan1Dot4) {
            getEmergencyNumberListForHalv1_3();
            indentingPrintWriter.println(" ========================================= ");
        }
        indentingPrintWriter.println("Emergency Number List for Phone(" + this.mPhone.getPhoneId() + ")");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println(getEmergencyNumberList());
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" ========================================= ");
        indentingPrintWriter.flush();
    }
}
