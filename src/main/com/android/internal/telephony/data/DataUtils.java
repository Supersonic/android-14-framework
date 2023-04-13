package com.android.internal.telephony.data;

import android.os.SystemClock;
import android.telephony.data.DataProfile;
import android.util.ArrayMap;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.telephony.Rlog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class DataUtils {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    public static int apnTypeToNetworkCapability(int i) {
        switch (i) {
            case 2:
                return 0;
            case 4:
                return 1;
            case 8:
                return 2;
            case 17:
                return 12;
            case 32:
                return 3;
            case 64:
                return 4;
            case 128:
                return 5;
            case CallFailCause.RADIO_UPLINK_FAILURE /* 256 */:
                return 7;
            case 512:
                return 10;
            case 1024:
                return 23;
            case 2048:
                return 9;
            case 4096:
                return 30;
            case NetworkStackConstants.IPV4_FLAG_MF /* 8192 */:
                return 31;
            case 16384:
                return 29;
            default:
                return -1;
        }
    }

    public static int getSourceTransport(int i) {
        return i == 1 ? 2 : 1;
    }

    public static int getTargetTransport(int i) {
        return i == 1 ? 2 : 1;
    }

    public static boolean isValidAccessNetwork(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public static int networkCapabilityToApnType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                if (i != 7) {
                                    if (i != 12) {
                                        if (i != 23) {
                                            if (i != 9) {
                                                if (i != 10) {
                                                    switch (i) {
                                                        case 29:
                                                            return 16384;
                                                        case 30:
                                                            return 4096;
                                                        case 31:
                                                            return NetworkStackConstants.IPV4_FLAG_MF;
                                                        default:
                                                            return 0;
                                                    }
                                                }
                                                return 512;
                                            }
                                            return 2048;
                                        }
                                        return 1024;
                                    }
                                    return 17;
                                }
                                return CallFailCause.RADIO_UPLINK_FAILURE;
                            }
                            return 128;
                        }
                        return 64;
                    }
                    return 32;
                }
                return 8;
            }
            return 4;
        }
        return 2;
    }

    public static int networkTypeToAccessNetworkType(int i) {
        switch (i) {
            case 1:
            case 2:
            case 16:
                return 1;
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
            case 17:
                return 2;
            case 4:
            case 5:
            case 6:
            case 7:
            case 12:
            case 14:
                return 4;
            case 11:
            default:
                return 0;
            case 13:
            case 19:
                return 3;
            case 18:
                return 5;
            case 20:
                return 6;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getNetworkCapabilityFromString(String str) {
        char c;
        String upperCase = str.toUpperCase(Locale.ROOT);
        upperCase.hashCode();
        switch (upperCase.hashCode()) {
            case -471939055:
                if (upperCase.equals("PRIORITIZE_BANDWIDTH")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -317644959:
                if (upperCase.equals("ENTERPRISE")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 65769:
                if (upperCase.equals("BIP")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 66516:
                if (upperCase.equals("CBS")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 68061:
                if (upperCase.equals("DUN")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 72623:
                if (upperCase.equals("IMS")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 76162:
                if (upperCase.equals("MCX")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 76467:
                if (upperCase.equals("MMS")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 2128202:
                if (upperCase.equals("EIMS")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 2163958:
                if (upperCase.equals("FOTA")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 2556894:
                if (upperCase.equals("SUPL")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 2644129:
                if (upperCase.equals("VSIM")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 2688090:
                if (upperCase.equals("XCAP")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 906932318:
                if (upperCase.equals("PRIORITIZE_LATENCY")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 1353037633:
                if (upperCase.equals("INTERNET")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 35;
            case 1:
                return 29;
            case 2:
                return 31;
            case 3:
                return 5;
            case 4:
                return 2;
            case 5:
                return 4;
            case 6:
                return 23;
            case 7:
                return 0;
            case '\b':
                return 10;
            case '\t':
                return 3;
            case '\n':
                return 1;
            case 11:
                return 30;
            case '\f':
                return 9;
            case '\r':
                return 34;
            case 14:
                return 12;
            default:
                return -1;
        }
    }

    public static Set<Integer> getNetworkCapabilitiesFromString(String str) {
        if (!str.matches("(\\s*[a-zA-Z]+\\s*)(\\|\\s*[a-zA-Z]+\\s*)*")) {
            return Collections.singleton(-1);
        }
        return (Set) Arrays.stream(str.split("\\s*\\|\\s*")).map(new DataNetworkController$HandoverRule$$ExternalSyntheticLambda0()).map(new Function() { // from class: com.android.internal.telephony.data.DataUtils$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(DataUtils.getNetworkCapabilityFromString((String) obj));
            }
        }).collect(Collectors.toSet());
    }

    public static String networkCapabilityToString(int i) {
        switch (i) {
            case 0:
                return "MMS";
            case 1:
                return "SUPL";
            case 2:
                return "DUN";
            case 3:
                return "FOTA";
            case 4:
                return "IMS";
            case 5:
                return "CBS";
            case 6:
                return "WIFI_P2P";
            case 7:
                return "IA";
            case 8:
                return "RCS";
            case 9:
                return "XCAP";
            case 10:
                return "EIMS";
            case 11:
                return "NOT_METERED";
            case 12:
                return "INTERNET";
            case 13:
                return "NOT_RESTRICTED";
            case 14:
                return "TRUSTED";
            case 15:
                return "NOT_VPN";
            case 16:
                return "VALIDATED";
            case 17:
                return "CAPTIVE_PORTAL";
            case 18:
                return "NOT_ROAMING";
            case 19:
                return "FOREGROUND";
            case 20:
                return "NOT_CONGESTED";
            case 21:
                return "NOT_SUSPENDED";
            case 22:
                return "OEM_PAID";
            case 23:
                return "MCX";
            case 24:
                return "PARTIAL_CONNECTIVITY";
            case 25:
                return "TEMPORARILY_NOT_METERED";
            case 26:
                return "OEM_PRIVATE";
            case 27:
                return "VEHICLE_INTERNAL";
            case 28:
                return "NOT_VCN_MANAGED";
            case 29:
                return "ENTERPRISE";
            case 30:
                return "VSIM";
            case 31:
                return "BIP";
            case 32:
                return "HEAD_UNIT";
            case 33:
                return "MMTEL";
            case 34:
                return "PRIORITIZE_LATENCY";
            case 35:
                return "PRIORITIZE_BANDWIDTH";
            default:
                loge("Unknown network capability(" + i + ")");
                return "Unknown(" + i + ")";
        }
    }

    public static String networkCapabilitiesToString(Collection<Integer> collection) {
        if (collection == null || collection.isEmpty()) {
            return PhoneConfigurationManager.SSSS;
        }
        return "[" + ((String) collection.stream().map(new DataConfigManager$$ExternalSyntheticLambda9()).collect(Collectors.joining("|"))) + "]";
    }

    public static String networkCapabilitiesToString(int[] iArr) {
        if (iArr == null) {
            return PhoneConfigurationManager.SSSS;
        }
        return "[" + ((String) Arrays.stream(iArr).mapToObj(new IntFunction() { // from class: com.android.internal.telephony.data.DataUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return DataUtils.networkCapabilityToString(i);
            }
        }).collect(Collectors.joining("|"))) + "]";
    }

    public static String validationStatusToString(int i) {
        if (i != 1) {
            if (i != 2) {
                loge("Unknown validation status(" + i + ")");
                return "UNKNOWN(" + i + ")";
            }
            return "INVALID";
        }
        return "VALID";
    }

    public static String elapsedTimeToString(long j) {
        return j != 0 ? systemTimeToString((System.currentTimeMillis() - SystemClock.elapsedRealtime()) + j) : "never";
    }

    public static String systemTimeToString(long j) {
        return j != 0 ? TIME_FORMAT.format(Long.valueOf(j)) : "never";
    }

    public static String imsFeatureToString(int i) {
        if (i != 1) {
            if (i != 2) {
                loge("Unknown IMS feature(" + i + ")");
                return "Unknown(" + i + ")";
            }
            return "RCS";
        }
        return "MMTEL";
    }

    public static int getHighestPriorityNetworkCapabilityFromDataProfile(DataConfigManager dataConfigManager, DataProfile dataProfile) {
        if (dataProfile.getApnSetting() == null || dataProfile.getApnSetting().getApnTypes().isEmpty()) {
            return -1;
        }
        Stream map = dataProfile.getApnSetting().getApnTypes().stream().map(new DataConfigManager$$ExternalSyntheticLambda15());
        Objects.requireNonNull(dataConfigManager);
        return ((Integer) ((List) map.sorted(Comparator.comparing(new DataNetwork$$ExternalSyntheticLambda0(dataConfigManager)).reversed()).collect(Collectors.toList())).get(0)).intValue();
    }

    public static List<DataNetworkController.NetworkRequestList> getGroupedNetworkRequestList(DataNetworkController.NetworkRequestList networkRequestList) {
        ArrayMap arrayMap = new ArrayMap();
        Iterator<TelephonyNetworkRequest> it = networkRequestList.iterator();
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            ((DataNetworkController.NetworkRequestList) arrayMap.computeIfAbsent((Set) Arrays.stream(next.getCapabilities()).boxed().collect(Collectors.toSet()), new Function() { // from class: com.android.internal.telephony.data.DataUtils$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    DataNetworkController.NetworkRequestList lambda$getGroupedNetworkRequestList$0;
                    lambda$getGroupedNetworkRequestList$0 = DataUtils.lambda$getGroupedNetworkRequestList$0((Set) obj);
                    return lambda$getGroupedNetworkRequestList$0;
                }
            })).add(next);
        }
        ArrayList arrayList = new ArrayList();
        for (DataNetworkController.NetworkRequestList networkRequestList2 : arrayMap.values()) {
            List<TelephonyNetworkRequest> list = (List) networkRequestList2.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataUtils$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean hasCapability;
                    hasCapability = ((TelephonyNetworkRequest) obj).hasCapability(29);
                    return hasCapability;
                }
            }).collect(Collectors.toList());
            if (list.isEmpty()) {
                arrayList.add(networkRequestList2);
            } else {
                ArrayMap arrayMap2 = new ArrayMap();
                for (TelephonyNetworkRequest telephonyNetworkRequest : list) {
                    ((DataNetworkController.NetworkRequestList) arrayMap2.computeIfAbsent(Integer.valueOf(telephonyNetworkRequest.getCapabilityDifferentiator()), new Function() { // from class: com.android.internal.telephony.data.DataUtils$$ExternalSyntheticLambda3
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            DataNetworkController.NetworkRequestList lambda$getGroupedNetworkRequestList$2;
                            lambda$getGroupedNetworkRequestList$2 = DataUtils.lambda$getGroupedNetworkRequestList$2((Integer) obj);
                            return lambda$getGroupedNetworkRequestList$2;
                        }
                    })).add(telephonyNetworkRequest);
                }
                arrayList.addAll(arrayMap2.values());
            }
        }
        return (List) arrayList.stream().sorted(new Comparator() { // from class: com.android.internal.telephony.data.DataUtils$$ExternalSyntheticLambda4
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$getGroupedNetworkRequestList$3;
                lambda$getGroupedNetworkRequestList$3 = DataUtils.lambda$getGroupedNetworkRequestList$3((DataNetworkController.NetworkRequestList) obj, (DataNetworkController.NetworkRequestList) obj2);
                return lambda$getGroupedNetworkRequestList$3;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ DataNetworkController.NetworkRequestList lambda$getGroupedNetworkRequestList$0(Set set) {
        return new DataNetworkController.NetworkRequestList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ DataNetworkController.NetworkRequestList lambda$getGroupedNetworkRequestList$2(Integer num) {
        return new DataNetworkController.NetworkRequestList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getGroupedNetworkRequestList$3(DataNetworkController.NetworkRequestList networkRequestList, DataNetworkController.NetworkRequestList networkRequestList2) {
        return Integer.compare(networkRequestList2.get(0).getPriority(), networkRequestList.get(0).getPriority());
    }

    public static String linkStatusToString(int i) {
        if (i != -1) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        loge("Unknown link status(" + i + ")");
                        return "UNKNOWN(" + i + ")";
                    }
                    return "ACTIVE";
                }
                return "DORMANT";
            }
            return "INACTIVE";
        }
        return "UNKNOWN";
    }

    public static String dataActivityToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            loge("Unknown data activity(" + i + ")");
                            return "UNKNOWN(" + i + ")";
                        }
                        return "DORMANT";
                    }
                    return "INOUT";
                }
                return "OUT";
            }
            return "IN";
        }
        return "NONE";
    }

    private static void loge(String str) {
        Rlog.e("DataUtils", str);
    }
}
