package android.telephony;

import android.app.settings.SettingsEnums;
import android.telephony.AccessNetworkConstants;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes3.dex */
public class AccessNetworkUtils {
    private static final int FREQUENCY_KHZ = 1000;
    private static final int FREQUENCY_RANGE_HIGH_KHZ = 6000000;
    private static final int FREQUENCY_RANGE_LOW_KHZ = 1000000;
    private static final int FREQUENCY_RANGE_MID_KHZ = 3000000;
    public static final int INVALID_BAND = -1;
    public static final int INVALID_FREQUENCY = -1;
    private static final String JAPAN_ISO_COUNTRY_CODE = "jp";
    private static final String TAG = "AccessNetworkUtils";
    private static final Set<Integer> UARFCN_NOT_GENERAL_BAND;

    private AccessNetworkUtils() {
    }

    static {
        HashSet hashSet = new HashSet();
        UARFCN_NOT_GENERAL_BAND = hashSet;
        hashSet.add(101);
        hashSet.add(102);
        hashSet.add(103);
        hashSet.add(104);
        hashSet.add(105);
        hashSet.add(106);
    }

    public static int getDuplexModeForEutranBand(int band) {
        if (band != -1 && band <= 88) {
            if (band >= 65) {
                return 1;
            }
            if (band >= 33) {
                return 2;
            }
            return band >= 1 ? 1 : 0;
        }
        return 0;
    }

    public static int getOperatingBandForEarfcn(int earfcn) {
        if (earfcn > 70645) {
            return -1;
        }
        if (earfcn >= 70596) {
            return 88;
        }
        if (earfcn >= 70546) {
            return 87;
        }
        if (earfcn >= 70366) {
            return 85;
        }
        if (earfcn > 69465) {
            return -1;
        }
        if (earfcn >= 69036) {
            return 74;
        }
        if (earfcn >= 68986) {
            return 73;
        }
        if (earfcn >= 68936) {
            return 72;
        }
        if (earfcn >= 68586) {
            return 71;
        }
        if (earfcn >= 68336) {
            return 70;
        }
        if (earfcn > 67835) {
            return -1;
        }
        if (earfcn >= 67536) {
            return 68;
        }
        if (earfcn >= 67366) {
            return -1;
        }
        if (earfcn >= 66436) {
            return 66;
        }
        if (earfcn >= 65536) {
            return 65;
        }
        if (earfcn > 60254) {
            return -1;
        }
        if (earfcn >= 60140) {
            return 53;
        }
        if (earfcn >= 59140) {
            return 52;
        }
        if (earfcn >= 59090) {
            return 51;
        }
        if (earfcn >= 58240) {
            return 50;
        }
        if (earfcn >= 56740) {
            return 49;
        }
        if (earfcn >= 55240) {
            return 48;
        }
        if (earfcn >= 54540) {
            return 47;
        }
        if (earfcn >= 46790) {
            return 46;
        }
        if (earfcn >= 46590) {
            return 45;
        }
        if (earfcn >= 45590) {
            return 44;
        }
        if (earfcn >= 43590) {
            return 43;
        }
        if (earfcn >= 41590) {
            return 42;
        }
        if (earfcn >= 39650) {
            return 41;
        }
        if (earfcn >= 38650) {
            return 40;
        }
        if (earfcn >= 38250) {
            return 39;
        }
        if (earfcn >= 37750) {
            return 38;
        }
        if (earfcn >= 37550) {
            return 37;
        }
        if (earfcn >= 36950) {
            return 36;
        }
        if (earfcn >= 36350) {
            return 35;
        }
        if (earfcn >= 36200) {
            return 34;
        }
        if (earfcn >= 36000) {
            return 33;
        }
        if (earfcn <= 10359 && earfcn < 9920) {
            if (earfcn >= 9870) {
                return 31;
            }
            if (earfcn >= 9770) {
                return 30;
            }
            if (earfcn >= 9660) {
                return -1;
            }
            if (earfcn >= 9210) {
                return 28;
            }
            if (earfcn >= 9040) {
                return 27;
            }
            if (earfcn >= 8690) {
                return 26;
            }
            if (earfcn >= 8040) {
                return 25;
            }
            if (earfcn >= 7700) {
                return 24;
            }
            if (earfcn >= 7500) {
                return 23;
            }
            if (earfcn >= 6600) {
                return 22;
            }
            if (earfcn >= 6450) {
                return 21;
            }
            if (earfcn >= 6150) {
                return 20;
            }
            if (earfcn >= 6000) {
                return 19;
            }
            if (earfcn >= 5850) {
                return 18;
            }
            if (earfcn >= 5730) {
                return 17;
            }
            if (earfcn > 5379) {
                return -1;
            }
            if (earfcn >= 5280) {
                return 14;
            }
            if (earfcn >= 5180) {
                return 13;
            }
            if (earfcn >= 5010) {
                return 12;
            }
            if (earfcn >= 4750) {
                return 11;
            }
            if (earfcn >= 4150) {
                return 10;
            }
            if (earfcn >= 3800) {
                return 9;
            }
            if (earfcn >= 3450) {
                return 8;
            }
            if (earfcn >= 2750) {
                return 7;
            }
            if (earfcn >= 2650) {
                return 6;
            }
            if (earfcn >= 2400) {
                return 5;
            }
            if (earfcn >= 1950) {
                return 4;
            }
            if (earfcn >= 1200) {
                return 3;
            }
            if (earfcn >= 600) {
                return 2;
            }
            return earfcn >= 0 ? 1 : -1;
        }
        return -1;
    }

    public static int getOperatingBandForNrarfcn(int nrarfcn) {
        if (nrarfcn >= 422000 && nrarfcn <= 434000) {
            return 1;
        }
        if (nrarfcn >= 386000 && nrarfcn <= 398000) {
            return 2;
        }
        if (nrarfcn >= 361000 && nrarfcn <= 376000) {
            return 3;
        }
        if (nrarfcn >= 173800 && nrarfcn <= 178800) {
            return 5;
        }
        if (nrarfcn >= 524000 && nrarfcn <= 538000) {
            return 7;
        }
        if (nrarfcn >= 185000 && nrarfcn <= 192000) {
            return 8;
        }
        if (nrarfcn >= 145800 && nrarfcn <= 149200) {
            return 12;
        }
        if (nrarfcn >= 151600 && nrarfcn <= 153600) {
            return 14;
        }
        if (nrarfcn >= 172000 && nrarfcn <= 175000) {
            return 18;
        }
        if (nrarfcn < 158200 || nrarfcn > 164200) {
            if (nrarfcn >= 386000 && nrarfcn <= 399000) {
                return 25;
            }
            if (nrarfcn >= 171800 && nrarfcn <= 178800) {
                return 26;
            }
            if (nrarfcn >= 151600 && nrarfcn <= 160600) {
                return 28;
            }
            if (nrarfcn >= 143400 && nrarfcn <= 145600) {
                return 29;
            }
            if (nrarfcn >= 470000 && nrarfcn <= 472000) {
                return 30;
            }
            if (nrarfcn >= 402000 && nrarfcn <= 405000) {
                return 34;
            }
            if (nrarfcn >= 514000 && nrarfcn <= 524000) {
                return 38;
            }
            if (nrarfcn >= 376000 && nrarfcn <= 384000) {
                return 39;
            }
            if (nrarfcn >= 460000 && nrarfcn <= 480000) {
                return 40;
            }
            if (nrarfcn >= 499200 && nrarfcn <= 537999) {
                return 41;
            }
            if (nrarfcn >= 743334 && nrarfcn <= 795000) {
                return 46;
            }
            if (nrarfcn >= 636667 && nrarfcn <= 646666) {
                return 48;
            }
            if (nrarfcn >= 286400 && nrarfcn <= 303400) {
                return 50;
            }
            if (nrarfcn >= 285400 && nrarfcn <= 286400) {
                return 51;
            }
            if (nrarfcn >= 496700 && nrarfcn <= 499000) {
                return 53;
            }
            if (nrarfcn >= 422000 && nrarfcn <= 440000) {
                return 65;
            }
            if (nrarfcn >= 399000 && nrarfcn <= 404000) {
                return 70;
            }
            if (nrarfcn >= 123400 && nrarfcn <= 130400) {
                return 71;
            }
            if (nrarfcn >= 295000 && nrarfcn <= 303600) {
                return 74;
            }
            if (nrarfcn >= 286400 && nrarfcn <= 303400) {
                return 75;
            }
            if (nrarfcn >= 285400 && nrarfcn <= 286400) {
                return 76;
            }
            if (nrarfcn >= 620000 && nrarfcn <= 680000) {
                return 77;
            }
            if (nrarfcn >= 620000 && nrarfcn <= 653333) {
                return 78;
            }
            if (nrarfcn >= 693334 && nrarfcn <= 733333) {
                return 79;
            }
            if (nrarfcn >= 499200 && nrarfcn <= 538000) {
                return 90;
            }
            if (nrarfcn >= 285400 && nrarfcn <= 286400) {
                return 91;
            }
            if (nrarfcn >= 286400 && nrarfcn <= 303400) {
                return 92;
            }
            if (nrarfcn >= 285400 && nrarfcn <= 286400) {
                return 93;
            }
            if (nrarfcn >= 286400 && nrarfcn <= 303400) {
                return 94;
            }
            if (nrarfcn >= 795000 && nrarfcn <= 875000) {
                return 96;
            }
            if (nrarfcn >= 2054166 && nrarfcn <= 2104165) {
                return 257;
            }
            if (nrarfcn >= 2016667 && nrarfcn <= 2070832) {
                return 258;
            }
            if (nrarfcn >= 2229166 && nrarfcn <= 2279165) {
                return 260;
            }
            if (nrarfcn >= 2070833 && nrarfcn <= 2084999) {
                return 261;
            }
            return -1;
        }
        return 20;
    }

    public static int getOperatingBandForArfcn(int arfcn) {
        if (arfcn >= 0 && arfcn <= 124) {
            return 10;
        }
        if (arfcn >= 128 && arfcn <= 251) {
            return 8;
        }
        if (arfcn >= 259 && arfcn <= 293) {
            return 3;
        }
        if (arfcn >= 306 && arfcn <= 340) {
            return 4;
        }
        if (arfcn >= 438 && arfcn <= 511) {
            return 6;
        }
        if (arfcn >= 512 && arfcn <= 885) {
            return 12;
        }
        if (arfcn >= 940 && arfcn <= 974) {
            return 14;
        }
        if (arfcn >= 975 && arfcn <= 1023) {
            return 10;
        }
        return -1;
    }

    public static int getOperatingBandForUarfcn(int uarfcn) {
        int[] addlBand2 = {412, FrameworkStatsLog.BOOT_COMPLETED_BROADCAST_COMPLETION_LATENCY_REPORTED, 462, 487, 512, MetricsProto.MetricsEvent.DIALOG_NO_HOME, 562, 587, MetricsProto.MetricsEvent.PROVISIONING_EXTRA, MetricsProto.MetricsEvent.ACTION_PERMISSION_REVOKE_READ_CALENDAR, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_ACCESS_COARSE_LOCATION, MetricsProto.MetricsEvent.ACTION_PERMISSION_GRANT_ADD_VOICEMAIL};
        int[] addlBand4 = {SettingsEnums.SECURITY_WARNINGS, SettingsEnums.ACCESSIBILITY_TEXT_READING_OPTIONS, 1937, 1962, SettingsEnums.DIALOG_SPECIFIC_DDS_SIM_PICKER, 2012, 2037, 2062, 2087};
        int[] addlBand5 = {1007, 1012, 1032, 1037, RILConstants.RIL_UNSOL_SATELLITE_PROVISION_STATE_CHANGED, 1087};
        int[] addlBand6 = {1037, RILConstants.RIL_UNSOL_SATELLITE_PROVISION_STATE_CHANGED};
        int[] addlBand7 = {2587, 2612, 2637, 2662, 2687, 2712, 2737, 2762, 2787, 2812, 2837, 2862, 2887, 2912};
        int[] addlBand10 = {3412, 3437, 3462, 3487, 3512, 3537, 3562, 3587, 3612, 3637, 3662, 3687};
        int[] addlBand12 = {3932, 3957, 3962, 3987, 3992};
        int[] addlBand13 = {4067, 4092};
        int[] addlBand14 = {4167, 4192};
        int[] addlBand19 = {787, 812, MetricsProto.MetricsEvent.WIFI_NETWORK_RECOMMENDATION_CONNECTION_SUCCESS};
        int[] addlBand25 = {6292, 6317, 6342, 6367, ServiceState.RIL_RADIO_CDMA_TECHNOLOGY_BITMASK, 6417, 6442, 6467, 6492, 6517, 6542, 6567, 6592};
        int[] addlBand26 = {5937, 5962, 5987, 5992, 6012, 6017, 6037, 6042, 6062, 6067, 6087};
        if (uarfcn < 10562 || uarfcn > 10838) {
            if ((uarfcn < 9662 || uarfcn > 9938) && Arrays.binarySearch(addlBand2, uarfcn) < 0) {
                if (uarfcn < 1162 || uarfcn > 1513) {
                    if ((uarfcn < 1537 || uarfcn > 1738) && Arrays.binarySearch(addlBand4, uarfcn) < 0) {
                        if (uarfcn < 4387 || uarfcn > 4413) {
                            if ((uarfcn < 4357 || uarfcn > 4458) && Arrays.binarySearch(addlBand5, uarfcn) < 0) {
                                if (Arrays.binarySearch(addlBand6, uarfcn) < 0) {
                                    if ((uarfcn < 2237 || uarfcn > 2563) && Arrays.binarySearch(addlBand7, uarfcn) < 0) {
                                        if (uarfcn < 2937 || uarfcn > 3088) {
                                            if (uarfcn < 9237 || uarfcn > 9387) {
                                                if ((uarfcn < 3112 || uarfcn > 3388) && Arrays.binarySearch(addlBand10, uarfcn) < 0) {
                                                    if (uarfcn < 3712 || uarfcn > 3787) {
                                                        if ((uarfcn < 3842 || uarfcn > 3903) && Arrays.binarySearch(addlBand12, uarfcn) < 0) {
                                                            if ((uarfcn < 4017 || uarfcn > 4043) && Arrays.binarySearch(addlBand13, uarfcn) < 0) {
                                                                if ((uarfcn < 4117 || uarfcn > 4143) && Arrays.binarySearch(addlBand14, uarfcn) < 0) {
                                                                    if ((uarfcn < 712 || uarfcn > 763) && Arrays.binarySearch(addlBand19, uarfcn) < 0) {
                                                                        if (uarfcn < 4512 || uarfcn > 4638) {
                                                                            if (uarfcn < 862 || uarfcn > 912) {
                                                                                if (uarfcn < 4662 || uarfcn > 5038) {
                                                                                    if ((uarfcn < 5112 || uarfcn > 5413) && Arrays.binarySearch(addlBand25, uarfcn) < 0) {
                                                                                        if ((uarfcn >= 5762 && uarfcn <= 5913) || Arrays.binarySearch(addlBand26, uarfcn) >= 0) {
                                                                                            return 26;
                                                                                        }
                                                                                        return -1;
                                                                                    }
                                                                                    return 25;
                                                                                }
                                                                                return 22;
                                                                            }
                                                                            return 21;
                                                                        }
                                                                        return 20;
                                                                    }
                                                                    return 19;
                                                                }
                                                                return 14;
                                                            }
                                                            return 13;
                                                        }
                                                        return 12;
                                                    }
                                                    return 11;
                                                }
                                                return 10;
                                            }
                                            return 9;
                                        }
                                        return 8;
                                    }
                                    return 7;
                                }
                                return 6;
                            }
                            return 5;
                        }
                        String country = TelephonyManager.getDefault().getNetworkCountryIso();
                        return JAPAN_ISO_COUNTRY_CODE.compareToIgnoreCase(country) == 0 ? 6 : 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public static int getFrequencyRangeGroupFromGeranBand(int band) {
        switch (band) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 14:
                return 1;
            case 12:
            case 13:
                return 2;
            default:
                return 0;
        }
    }

    public static int getFrequencyRangeGroupFromUtranBand(int band) {
        switch (band) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
            case 9:
            case 10:
            case 11:
            case 21:
            case 25:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
                return 2;
            case 5:
            case 6:
            case 8:
            case 12:
            case 13:
            case 14:
            case 19:
            case 20:
            case 26:
                return 1;
            case 22:
                return 3;
            default:
                return 0;
        }
    }

    public static int getFrequencyRangeGroupFromEutranBand(int band) {
        switch (band) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
            case 9:
            case 10:
            case 11:
            case 21:
            case 23:
            case 24:
            case 25:
            case 30:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 45:
            case 53:
            case 65:
            case 66:
            case 70:
            case 74:
                return 2;
            case 5:
            case 6:
            case 8:
            case 12:
            case 13:
            case 14:
            case 17:
            case 18:
            case 19:
            case 20:
            case 26:
            case 27:
            case 28:
            case 31:
            case 44:
            case 50:
            case 51:
            case 68:
            case 71:
            case 72:
            case 73:
            case 85:
            case 87:
            case 88:
                return 1;
            case 15:
            case 16:
            case 29:
            case 32:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 67:
            case 69:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 86:
            default:
                return 0;
            case 22:
            case 42:
            case 43:
            case 46:
            case 47:
            case 48:
            case 49:
            case 52:
                return 3;
        }
    }

    public static int getFrequencyRangeGroupFromNrBand(int band) {
        switch (band) {
            case 1:
            case 2:
            case 3:
            case 7:
            case 25:
            case 30:
            case 34:
            case 38:
            case 39:
            case 40:
            case 41:
            case 50:
            case 51:
            case 53:
            case 65:
            case 66:
            case 70:
            case 74:
            case 75:
            case 76:
            case 80:
            case 84:
            case 86:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
                return 2;
            case 5:
            case 8:
            case 12:
            case 14:
            case 18:
            case 20:
            case 26:
            case 28:
            case 29:
            case 71:
            case 81:
            case 82:
            case 83:
            case 89:
                return 1;
            case 46:
            case 48:
            case 77:
            case 78:
            case 79:
                return 3;
            case 96:
            case 257:
            case 258:
            case 260:
            case 261:
                return 4;
            default:
                return 0;
        }
    }

    public static int getFrequencyFromNrArfcn(int nrArfcn) {
        if (nrArfcn == Integer.MAX_VALUE) {
            return -1;
        }
        int globalKhz = 0;
        int rangeOffset = 0;
        int arfcnOffset = 0;
        AccessNetworkConstants.NgranArfcnFrequency[] values = AccessNetworkConstants.NgranArfcnFrequency.values();
        int length = values.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            AccessNetworkConstants.NgranArfcnFrequency nrArfcnFrequency = values[i];
            if (nrArfcn < nrArfcnFrequency.rangeFirst || nrArfcn > nrArfcnFrequency.rangeLast) {
                i++;
            } else {
                globalKhz = nrArfcnFrequency.globalKhz;
                rangeOffset = nrArfcnFrequency.rangeOffset;
                arfcnOffset = nrArfcnFrequency.arfcnOffset;
                break;
            }
        }
        return ((nrArfcn - arfcnOffset) * globalKhz) + rangeOffset;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x005c, code lost:
        return convertEarfcnToFrequency(r0, r8, r1);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getFrequencyFromEarfcn(int band, int earfcn, boolean isUplink) {
        int low = 0;
        int offset = 0;
        AccessNetworkConstants.EutranBandArfcnFrequency[] values = AccessNetworkConstants.EutranBandArfcnFrequency.values();
        int length = values.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            AccessNetworkConstants.EutranBandArfcnFrequency earfcnFrequency = values[i];
            if (band != earfcnFrequency.band) {
                i++;
            } else if (isInEarfcnRange(earfcn, earfcnFrequency, isUplink)) {
                low = isUplink ? earfcnFrequency.uplinkLowKhz : earfcnFrequency.downlinkLowKhz;
                offset = isUplink ? earfcnFrequency.uplinkOffset : earfcnFrequency.downlinkOffset;
            } else {
                Rlog.m121w(TAG, "Band and the range of EARFCN are not consistent: band = " + band + " ,earfcn = " + earfcn + " ,isUplink = " + isUplink);
                return -1;
            }
        }
    }

    private static int convertEarfcnToFrequency(int low, int earfcn, int offset) {
        return ((earfcn - offset) * 100) + low;
    }

    private static boolean isInEarfcnRange(int earfcn, AccessNetworkConstants.EutranBandArfcnFrequency earfcnFrequency, boolean isUplink) {
        return isUplink ? earfcn >= earfcnFrequency.uplinkOffset && earfcn <= earfcnFrequency.uplinkRange : earfcn >= earfcnFrequency.downlinkOffset && earfcn <= earfcnFrequency.downlinkRange;
    }

    public static int getFrequencyFromUarfcn(int band, int uarfcn, boolean isUplink) {
        if (uarfcn == Integer.MAX_VALUE) {
            return -1;
        }
        int offsetKhz = 0;
        AccessNetworkConstants.UtranBandArfcnFrequency[] values = AccessNetworkConstants.UtranBandArfcnFrequency.values();
        int length = values.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            AccessNetworkConstants.UtranBandArfcnFrequency uarfcnFrequency = values[i];
            if (band != uarfcnFrequency.band) {
                i++;
            } else if (isInUarfcnRange(uarfcn, uarfcnFrequency, isUplink)) {
                offsetKhz = isUplink ? uarfcnFrequency.uplinkOffset : uarfcnFrequency.downlinkOffset;
            } else {
                Rlog.m121w(TAG, "Band and the range of UARFCN are not consistent: band = " + band + " ,uarfcn = " + uarfcn + " ,isUplink = " + isUplink);
                return -1;
            }
        }
        if (!UARFCN_NOT_GENERAL_BAND.contains(Integer.valueOf(band))) {
            return convertUarfcnToFrequency(offsetKhz, uarfcn);
        }
        return convertUarfcnTddToFrequency(band, uarfcn);
    }

    private static int convertUarfcnToFrequency(int offsetKhz, int uarfcn) {
        return (uarfcn * 200) + offsetKhz;
    }

    private static int convertUarfcnTddToFrequency(int band, int uarfcn) {
        if (band != 104) {
            return uarfcn * 5 * 1000;
        }
        return ((uarfcn * 1000) - 2150100) * 5;
    }

    private static boolean isInUarfcnRange(int uarfcn, AccessNetworkConstants.UtranBandArfcnFrequency uarfcnFrequency, boolean isUplink) {
        if (isUplink) {
            return uarfcn >= uarfcnFrequency.uplinkRangeFirst && uarfcn <= uarfcnFrequency.uplinkRangeLast;
        } else if (uarfcnFrequency.downlinkRangeFirst == 0 || uarfcnFrequency.downlinkRangeLast == 0) {
            return true;
        } else {
            return uarfcn >= uarfcnFrequency.downlinkRangeFirst && uarfcn <= uarfcnFrequency.downlinkRangeLast;
        }
    }

    public static int getFrequencyFromArfcn(int band, int arfcn, boolean isUplink) {
        if (arfcn == Integer.MAX_VALUE) {
            return -1;
        }
        int downlinkOffset = 0;
        int frequency = 0;
        AccessNetworkConstants.GeranBandArfcnFrequency[] values = AccessNetworkConstants.GeranBandArfcnFrequency.values();
        int length = values.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            AccessNetworkConstants.GeranBandArfcnFrequency arfcnFrequency = values[i];
            if (band != arfcnFrequency.band) {
                i++;
            } else if (arfcn >= arfcnFrequency.arfcnRangeFirst && arfcn <= arfcnFrequency.arfcnRangeLast) {
                int uplinkFrequencyFirst = arfcnFrequency.uplinkFrequencyFirst;
                downlinkOffset = arfcnFrequency.downlinkOffset;
                int arfcnOffset = arfcnFrequency.arfcnOffset;
                frequency = convertArfcnToFrequency(arfcn, uplinkFrequencyFirst, arfcnOffset);
            } else {
                Rlog.m121w(TAG, "Band and the range of ARFCN are not consistent: band = " + band + " ,arfcn = " + arfcn + " ,isUplink = " + isUplink);
                return -1;
            }
        }
        return isUplink ? frequency : frequency + downlinkOffset;
    }

    private static int convertArfcnToFrequency(int arfcn, int uplinkFrequencyFirstKhz, int arfcnOffset) {
        return ((arfcn - arfcnOffset) * 200) + uplinkFrequencyFirstKhz;
    }

    public static int getFrequencyRangeFromArfcn(int frequency) {
        if (frequency < 1000000) {
            return 1;
        }
        if (frequency < FREQUENCY_RANGE_MID_KHZ && frequency >= 1000000) {
            return 2;
        }
        if (frequency < FREQUENCY_RANGE_HIGH_KHZ && frequency >= FREQUENCY_RANGE_MID_KHZ) {
            return 3;
        }
        return 4;
    }
}
