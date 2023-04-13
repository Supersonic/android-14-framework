package com.android.internal.telephony;

import android.app.ActivityManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.timezone.TelephonyLookup;
import android.timezone.TelephonyNetwork;
import android.timezone.TelephonyNetworkFinder;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.cat.BerTlv;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class MccTable {
    public static final Map<Locale, Locale> FALLBACKS;
    static ArrayList<MccEntry> sTable;
    @GuardedBy({"MccTable.class"})
    private static TelephonyNetworkFinder sTelephonyNetworkFinder;

    /* loaded from: classes.dex */
    public static class MccEntry implements Comparable<MccEntry> {
        @UnsupportedAppUsage(maxTargetSdk = 29, publicAlternatives = "There is no alternative for {@code MccTable.MccEntry.mIso}, but it was included in hidden APIs due to a static analysis false positive and has been made max Q. Please file a bug if you still require this API.")
        public final String mIso;
        final int mMcc;
        final int mSmallestDigitsMnc;

        MccEntry(int i, String str, int i2) {
            str.getClass();
            this.mMcc = i;
            this.mIso = str;
            this.mSmallestDigitsMnc = i2;
        }

        @Override // java.lang.Comparable
        public int compareTo(MccEntry mccEntry) {
            return this.mMcc - mccEntry.mMcc;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class MccMnc {
        public final String mcc;
        public final String mnc;

        public static MccMnc fromOperatorNumeric(String str) {
            Objects.requireNonNull(str);
            String str2 = null;
            try {
                String substring = str.substring(0, 3);
                try {
                    str2 = str.substring(3);
                } catch (StringIndexOutOfBoundsException unused) {
                }
                return new MccMnc(substring, str2);
            } catch (StringIndexOutOfBoundsException unused2) {
                return null;
            }
        }

        public MccMnc(String str, String str2) {
            Objects.requireNonNull(str);
            this.mcc = str;
            this.mnc = str2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MccMnc mccMnc = (MccMnc) obj;
            return this.mcc.equals(mccMnc.mcc) && Objects.equals(this.mnc, mccMnc.mnc);
        }

        public int hashCode() {
            return Objects.hash(this.mcc, this.mnc);
        }

        public String toString() {
            return "MccMnc{mcc='" + this.mcc + "', mnc='" + this.mnc + "'}";
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 29, publicAlternatives = "There is no alternative for {@code MccTable.entryForMcc}, but it was included in hidden APIs due to a static analysis false positive and has been made greylist-max-q. Please file a bug if you still require this API.")
    public static MccEntry entryForMcc(int i) {
        int binarySearch = Collections.binarySearch(sTable, new MccEntry(i, PhoneConfigurationManager.SSSS, 0));
        if (binarySearch < 0) {
            return null;
        }
        return sTable.get(binarySearch);
    }

    @UnsupportedAppUsage
    public static String countryCodeForMcc(int i) {
        MccEntry entryForMcc = entryForMcc(i);
        return entryForMcc == null ? PhoneConfigurationManager.SSSS : entryForMcc.mIso;
    }

    public static String countryCodeForMcc(String str) {
        try {
            return countryCodeForMcc(Integer.parseInt(str));
        } catch (NumberFormatException unused) {
            return PhoneConfigurationManager.SSSS;
        }
    }

    public static String geoCountryCodeForMccMnc(MccMnc mccMnc) {
        String countryCodeForMccMncNoFallback = mccMnc.mnc != null ? countryCodeForMccMncNoFallback(mccMnc) : null;
        return TextUtils.isEmpty(countryCodeForMccMncNoFallback) ? countryCodeForMcc(mccMnc.mcc) : countryCodeForMccMncNoFallback;
    }

    private static String countryCodeForMccMncNoFallback(MccMnc mccMnc) {
        TelephonyNetwork findNetworkByMccMnc;
        synchronized (MccTable.class) {
            if (sTelephonyNetworkFinder == null) {
                sTelephonyNetworkFinder = TelephonyLookup.getInstance().getTelephonyNetworkFinder();
            }
        }
        TelephonyNetworkFinder telephonyNetworkFinder = sTelephonyNetworkFinder;
        if (telephonyNetworkFinder == null || (findNetworkByMccMnc = telephonyNetworkFinder.findNetworkByMccMnc(mccMnc.mcc, mccMnc.mnc)) == null) {
            return null;
        }
        return findNetworkByMccMnc.getCountryIsoCode();
    }

    @UnsupportedAppUsage(maxTargetSdk = 29, publicAlternatives = "There is no alternative for {@code MccTable.smallestDigitsMccForMnc}, but it was included in hidden APIs due to a static analysis false positive and has been made max Q. Please file a bug if you still require this API.")
    public static int smallestDigitsMccForMnc(int i) {
        MccEntry entryForMcc = entryForMcc(i);
        if (entryForMcc == null) {
            return 2;
        }
        return entryForMcc.mSmallestDigitsMnc;
    }

    public static void updateMccMncConfiguration(Context context, String str) {
        Rlog.d("MccTable", "updateMccMncConfiguration mccmnc='" + str);
        if (TelephonyUtils.IS_DEBUGGABLE) {
            String str2 = SystemProperties.get("persist.sys.override_mcc");
            if (!TextUtils.isEmpty(str2)) {
                Rlog.d("MccTable", "updateMccMncConfiguration overriding mccmnc='" + str2 + "'");
                str = str2;
            }
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            if (Integer.parseInt(str.substring(0, 3)) != 0) {
                if (!((ActivityManager) context.getSystemService("activity")).updateMccMncConfiguration(str.substring(0, 3), str.substring(3))) {
                    Rlog.d("MccTable", "updateMccMncConfiguration: update mccmnc=" + str + " failure");
                    return;
                }
                Rlog.d("MccTable", "updateMccMncConfiguration: update mccmnc=" + str + " success");
                return;
            }
            Rlog.d("MccTable", "updateMccMncConfiguration nothing to update");
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            Rlog.e("MccTable", "Error parsing mccmnc: " + str + ". ex=" + e);
        }
    }

    static {
        HashMap hashMap = new HashMap();
        FALLBACKS = hashMap;
        hashMap.put(Locale.ENGLISH, Locale.US);
        ArrayList<MccEntry> arrayList = new ArrayList<>(240);
        sTable = arrayList;
        arrayList.add(new MccEntry(202, "gr", 2));
        sTable.add(new MccEntry(204, "nl", 2));
        sTable.add(new MccEntry(206, "be", 2));
        sTable.add(new MccEntry(BerTlv.BER_PROACTIVE_COMMAND_TAG, "fr", 2));
        sTable.add(new MccEntry(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_APP_TOOLKIT_BUSY, "mc", 2));
        sTable.add(new MccEntry(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR, "ad", 2));
        sTable.add(new MccEntry(BerTlv.BER_EVENT_DOWNLOAD_TAG, "es", 2));
        sTable.add(new MccEntry(216, "hu", 2));
        sTable.add(new MccEntry(218, "ba", 2));
        sTable.add(new MccEntry(219, "hr", 2));
        sTable.add(new MccEntry(220, "rs", 2));
        sTable.add(new MccEntry(NetworkStackConstants.VENDOR_SPECIFIC_IE_ID, "xk", 2));
        sTable.add(new MccEntry(222, "it", 2));
        sTable.add(new MccEntry(225, "va", 2));
        sTable.add(new MccEntry(226, "ro", 2));
        sTable.add(new MccEntry(228, "ch", 2));
        sTable.add(new MccEntry(230, "cz", 2));
        sTable.add(new MccEntry(231, "sk", 2));
        sTable.add(new MccEntry(232, "at", 2));
        sTable.add(new MccEntry(234, "gb", 2));
        sTable.add(new MccEntry(235, "gb", 2));
        sTable.add(new MccEntry(238, "dk", 2));
        sTable.add(new MccEntry(240, "se", 2));
        sTable.add(new MccEntry(242, "no", 2));
        sTable.add(new MccEntry(CallFailCause.DIAL_MODIFIED_TO_USSD, "fi", 2));
        sTable.add(new MccEntry(CallFailCause.DIAL_MODIFIED_TO_DIAL, "lt", 2));
        sTable.add(new MccEntry(CallFailCause.RADIO_OFF, "lv", 2));
        sTable.add(new MccEntry(248, "ee", 2));
        sTable.add(new MccEntry(CallFailCause.RADIO_INTERNAL_ERROR, "ru", 2));
        sTable.add(new MccEntry(255, "ua", 2));
        sTable.add(new MccEntry(CallFailCause.RADIO_SETUP_FAILURE, "by", 2));
        sTable.add(new MccEntry(CallFailCause.RADIO_RELEASE_ABNORMAL, "md", 2));
        sTable.add(new MccEntry(CallFailCause.ACCESS_CLASS_BLOCKED, "pl", 2));
        sTable.add(new MccEntry(262, "de", 2));
        sTable.add(new MccEntry(266, "gi", 2));
        sTable.add(new MccEntry(268, "pt", 2));
        sTable.add(new MccEntry(270, "lu", 2));
        sTable.add(new MccEntry(272, "ie", 2));
        sTable.add(new MccEntry(274, "is", 2));
        sTable.add(new MccEntry(276, "al", 2));
        sTable.add(new MccEntry(278, "mt", 2));
        sTable.add(new MccEntry(280, "cy", 2));
        sTable.add(new MccEntry(282, "ge", 2));
        sTable.add(new MccEntry(283, "am", 2));
        sTable.add(new MccEntry(284, "bg", 2));
        sTable.add(new MccEntry(286, "tr", 2));
        sTable.add(new MccEntry(288, "fo", 2));
        sTable.add(new MccEntry(289, "ge", 2));
        sTable.add(new MccEntry(290, "gl", 2));
        sTable.add(new MccEntry(292, "sm", 2));
        sTable.add(new MccEntry(293, "si", 2));
        sTable.add(new MccEntry(294, "mk", 2));
        sTable.add(new MccEntry(295, "li", 2));
        sTable.add(new MccEntry(297, "me", 2));
        sTable.add(new MccEntry(302, "ca", 3));
        sTable.add(new MccEntry(308, "pm", 2));
        sTable.add(new MccEntry(310, "us", 3));
        sTable.add(new MccEntry(311, "us", 3));
        sTable.add(new MccEntry(312, "us", 3));
        sTable.add(new MccEntry(313, "us", 3));
        sTable.add(new MccEntry(314, "us", 3));
        sTable.add(new MccEntry(315, "us", 3));
        sTable.add(new MccEntry(316, "us", 3));
        sTable.add(new MccEntry(330, "pr", 2));
        sTable.add(new MccEntry(332, "vi", 2));
        sTable.add(new MccEntry(334, "mx", 3));
        sTable.add(new MccEntry(338, "jm", 3));
        sTable.add(new MccEntry(340, "gp", 2));
        sTable.add(new MccEntry(342, "bb", 3));
        sTable.add(new MccEntry(344, "ag", 3));
        sTable.add(new MccEntry(346, "ky", 3));
        sTable.add(new MccEntry(348, "vg", 3));
        sTable.add(new MccEntry(350, "bm", 2));
        sTable.add(new MccEntry(352, "gd", 2));
        sTable.add(new MccEntry(354, "ms", 2));
        sTable.add(new MccEntry(356, "kn", 2));
        sTable.add(new MccEntry(358, "lc", 2));
        sTable.add(new MccEntry(360, "vc", 2));
        sTable.add(new MccEntry(362, "cw", 2));
        sTable.add(new MccEntry(363, "aw", 2));
        sTable.add(new MccEntry(364, "bs", 2));
        sTable.add(new MccEntry(365, "ai", 3));
        sTable.add(new MccEntry(366, "dm", 2));
        sTable.add(new MccEntry(368, "cu", 2));
        sTable.add(new MccEntry(370, "do", 2));
        sTable.add(new MccEntry(372, "ht", 2));
        sTable.add(new MccEntry(374, "tt", 2));
        sTable.add(new MccEntry(376, "tc", 2));
        sTable.add(new MccEntry(400, "az", 2));
        sTable.add(new MccEntry(401, "kz", 2));
        sTable.add(new MccEntry(402, "bt", 2));
        sTable.add(new MccEntry(404, "in", 2));
        sTable.add(new MccEntry(405, "in", 2));
        sTable.add(new MccEntry(406, "in", 2));
        sTable.add(new MccEntry(410, "pk", 2));
        sTable.add(new MccEntry(412, "af", 2));
        sTable.add(new MccEntry(413, "lk", 2));
        sTable.add(new MccEntry(414, "mm", 2));
        sTable.add(new MccEntry(415, "lb", 2));
        sTable.add(new MccEntry(416, "jo", 2));
        sTable.add(new MccEntry(417, "sy", 2));
        sTable.add(new MccEntry(418, "iq", 2));
        sTable.add(new MccEntry(419, "kw", 2));
        sTable.add(new MccEntry(420, "sa", 2));
        sTable.add(new MccEntry(421, "ye", 2));
        sTable.add(new MccEntry(422, "om", 2));
        sTable.add(new MccEntry(423, "ps", 2));
        sTable.add(new MccEntry(424, "ae", 2));
        sTable.add(new MccEntry(425, "il", 2));
        sTable.add(new MccEntry(426, "bh", 2));
        sTable.add(new MccEntry(427, "qa", 2));
        sTable.add(new MccEntry(428, "mn", 2));
        sTable.add(new MccEntry(429, "np", 2));
        sTable.add(new MccEntry(430, "ae", 2));
        sTable.add(new MccEntry(431, "ae", 2));
        sTable.add(new MccEntry(432, "ir", 2));
        sTable.add(new MccEntry(434, "uz", 2));
        sTable.add(new MccEntry(436, "tj", 2));
        sTable.add(new MccEntry(437, "kg", 2));
        sTable.add(new MccEntry(438, "tm", 2));
        sTable.add(new MccEntry(440, "jp", 2));
        sTable.add(new MccEntry(441, "jp", 2));
        sTable.add(new MccEntry(450, "kr", 2));
        sTable.add(new MccEntry(452, "vn", 2));
        sTable.add(new MccEntry(454, "hk", 2));
        sTable.add(new MccEntry(455, "mo", 2));
        sTable.add(new MccEntry(456, "kh", 2));
        sTable.add(new MccEntry(457, "la", 2));
        sTable.add(new MccEntry(460, "cn", 2));
        sTable.add(new MccEntry(461, "cn", 2));
        sTable.add(new MccEntry(466, "tw", 2));
        sTable.add(new MccEntry(467, "kp", 2));
        sTable.add(new MccEntry(470, "bd", 2));
        sTable.add(new MccEntry(472, "mv", 2));
        sTable.add(new MccEntry(502, "my", 2));
        sTable.add(new MccEntry(505, "au", 2));
        sTable.add(new MccEntry(510, "id", 2));
        sTable.add(new MccEntry(514, "tl", 2));
        sTable.add(new MccEntry(515, "ph", 2));
        sTable.add(new MccEntry(520, "th", 2));
        sTable.add(new MccEntry(525, "sg", 2));
        sTable.add(new MccEntry(528, "bn", 2));
        sTable.add(new MccEntry(530, "nz", 2));
        sTable.add(new MccEntry(534, "mp", 2));
        sTable.add(new MccEntry(535, "gu", 2));
        sTable.add(new MccEntry(536, "nr", 2));
        sTable.add(new MccEntry(537, "pg", 2));
        sTable.add(new MccEntry(539, "to", 2));
        sTable.add(new MccEntry(540, "sb", 2));
        sTable.add(new MccEntry(541, "vu", 2));
        sTable.add(new MccEntry(542, "fj", 2));
        sTable.add(new MccEntry(543, "wf", 2));
        sTable.add(new MccEntry(544, "as", 2));
        sTable.add(new MccEntry(545, "ki", 2));
        sTable.add(new MccEntry(NetworkStackConstants.DHCP6_CLIENT_PORT, "nc", 2));
        sTable.add(new MccEntry(NetworkStackConstants.DHCP6_SERVER_PORT, "pf", 2));
        sTable.add(new MccEntry(548, "ck", 2));
        sTable.add(new MccEntry(549, "ws", 2));
        sTable.add(new MccEntry(550, "fm", 2));
        sTable.add(new MccEntry(551, "mh", 2));
        sTable.add(new MccEntry(552, "pw", 2));
        sTable.add(new MccEntry(553, "tv", 2));
        sTable.add(new MccEntry(554, "tk", 2));
        sTable.add(new MccEntry(555, "nu", 2));
        sTable.add(new MccEntry(602, "eg", 2));
        sTable.add(new MccEntry(603, "dz", 2));
        sTable.add(new MccEntry(604, "ma", 2));
        sTable.add(new MccEntry(605, "tn", 2));
        sTable.add(new MccEntry(606, "ly", 2));
        sTable.add(new MccEntry(607, "gm", 2));
        sTable.add(new MccEntry(608, "sn", 2));
        sTable.add(new MccEntry(609, "mr", 2));
        sTable.add(new MccEntry(610, "ml", 2));
        sTable.add(new MccEntry(611, "gn", 2));
        sTable.add(new MccEntry(612, "ci", 2));
        sTable.add(new MccEntry(613, "bf", 2));
        sTable.add(new MccEntry(614, "ne", 2));
        sTable.add(new MccEntry(615, "tg", 2));
        sTable.add(new MccEntry(616, "bj", 2));
        sTable.add(new MccEntry(617, "mu", 2));
        sTable.add(new MccEntry(618, "lr", 2));
        sTable.add(new MccEntry(619, "sl", 2));
        sTable.add(new MccEntry(620, "gh", 2));
        sTable.add(new MccEntry(621, "ng", 2));
        sTable.add(new MccEntry(622, "td", 2));
        sTable.add(new MccEntry(623, "cf", 2));
        sTable.add(new MccEntry(624, "cm", 2));
        sTable.add(new MccEntry(625, "cv", 2));
        sTable.add(new MccEntry(626, "st", 2));
        sTable.add(new MccEntry(627, "gq", 2));
        sTable.add(new MccEntry(628, "ga", 2));
        sTable.add(new MccEntry(629, "cg", 2));
        sTable.add(new MccEntry(630, "cd", 2));
        sTable.add(new MccEntry(631, "ao", 2));
        sTable.add(new MccEntry(632, "gw", 2));
        sTable.add(new MccEntry(633, "sc", 2));
        sTable.add(new MccEntry(634, "sd", 2));
        sTable.add(new MccEntry(635, "rw", 2));
        sTable.add(new MccEntry(636, "et", 2));
        sTable.add(new MccEntry(637, "so", 2));
        sTable.add(new MccEntry(638, "dj", 2));
        sTable.add(new MccEntry(639, "ke", 2));
        sTable.add(new MccEntry(640, "tz", 2));
        sTable.add(new MccEntry(641, "ug", 2));
        sTable.add(new MccEntry(642, "bi", 2));
        sTable.add(new MccEntry(643, "mz", 2));
        sTable.add(new MccEntry(645, "zm", 2));
        sTable.add(new MccEntry(646, "mg", 2));
        sTable.add(new MccEntry(647, "re", 2));
        sTable.add(new MccEntry(648, "zw", 2));
        sTable.add(new MccEntry(649, "na", 2));
        sTable.add(new MccEntry(650, "mw", 2));
        sTable.add(new MccEntry(651, "ls", 2));
        sTable.add(new MccEntry(652, "bw", 2));
        sTable.add(new MccEntry(653, "sz", 2));
        sTable.add(new MccEntry(654, "km", 2));
        sTable.add(new MccEntry(655, "za", 2));
        sTable.add(new MccEntry(657, "er", 2));
        sTable.add(new MccEntry(658, "sh", 2));
        sTable.add(new MccEntry(659, "ss", 2));
        sTable.add(new MccEntry(702, "bz", 2));
        sTable.add(new MccEntry(704, "gt", 2));
        sTable.add(new MccEntry(706, "sv", 2));
        sTable.add(new MccEntry(708, "hn", 3));
        sTable.add(new MccEntry(710, "ni", 2));
        sTable.add(new MccEntry(712, "cr", 2));
        sTable.add(new MccEntry(714, "pa", 2));
        sTable.add(new MccEntry(716, "pe", 2));
        sTable.add(new MccEntry(722, "ar", 3));
        sTable.add(new MccEntry(724, "br", 2));
        sTable.add(new MccEntry(730, "cl", 2));
        sTable.add(new MccEntry(732, "co", 3));
        sTable.add(new MccEntry(734, "ve", 2));
        sTable.add(new MccEntry(736, "bo", 2));
        sTable.add(new MccEntry(738, "gy", 2));
        sTable.add(new MccEntry(740, "ec", 2));
        sTable.add(new MccEntry(742, "gf", 2));
        sTable.add(new MccEntry(744, "py", 2));
        sTable.add(new MccEntry(746, "sr", 2));
        sTable.add(new MccEntry(748, "uy", 2));
        sTable.add(new MccEntry(750, "fk", 2));
        Collections.sort(sTable);
    }
}
