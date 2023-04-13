package android.hardware.radio.modem;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface NvItem$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "CDMA_MEID" : _aidl_v == 2 ? "CDMA_MIN" : _aidl_v == 3 ? "CDMA_MDN" : _aidl_v == 4 ? "CDMA_ACCOLC" : _aidl_v == 11 ? "DEVICE_MSL" : _aidl_v == 12 ? "RTN_RECONDITIONED_STATUS" : _aidl_v == 13 ? "RTN_ACTIVATION_DATE" : _aidl_v == 14 ? "RTN_LIFE_TIMER" : _aidl_v == 15 ? "RTN_LIFE_CALLS" : _aidl_v == 16 ? "RTN_LIFE_DATA_TX" : _aidl_v == 17 ? "RTN_LIFE_DATA_RX" : _aidl_v == 18 ? "OMADM_HFA_LEVEL" : _aidl_v == 31 ? "MIP_PROFILE_NAI" : _aidl_v == 32 ? "MIP_PROFILE_HOME_ADDRESS" : _aidl_v == 33 ? "MIP_PROFILE_AAA_AUTH" : _aidl_v == 34 ? "MIP_PROFILE_HA_AUTH" : _aidl_v == 35 ? "MIP_PROFILE_PRI_HA_ADDR" : _aidl_v == 36 ? "MIP_PROFILE_SEC_HA_ADDR" : _aidl_v == 37 ? "MIP_PROFILE_REV_TUN_PREF" : _aidl_v == 38 ? "MIP_PROFILE_HA_SPI" : _aidl_v == 39 ? "MIP_PROFILE_AAA_SPI" : _aidl_v == 40 ? "MIP_PROFILE_MN_HA_SS" : _aidl_v == 41 ? "MIP_PROFILE_MN_AAA_SS" : _aidl_v == 51 ? "CDMA_PRL_VERSION" : _aidl_v == 52 ? "CDMA_BC10" : _aidl_v == 53 ? "CDMA_BC14" : _aidl_v == 54 ? "CDMA_SO68" : _aidl_v == 55 ? "CDMA_SO73_COP0" : _aidl_v == 56 ? "CDMA_SO73_COP1TO7" : _aidl_v == 57 ? "CDMA_1X_ADVANCED_ENABLED" : _aidl_v == 58 ? "CDMA_EHRPD_ENABLED" : _aidl_v == 59 ? "CDMA_EHRPD_FORCED" : _aidl_v == 71 ? "LTE_BAND_ENABLE_25" : _aidl_v == 72 ? "LTE_BAND_ENABLE_26" : _aidl_v == 73 ? "LTE_BAND_ENABLE_41" : _aidl_v == 74 ? "LTE_SCAN_PRIORITY_25" : _aidl_v == 75 ? "LTE_SCAN_PRIORITY_26" : _aidl_v == 76 ? "LTE_SCAN_PRIORITY_41" : _aidl_v == 77 ? "LTE_HIDDEN_BAND_PRIORITY_25" : _aidl_v == 78 ? "LTE_HIDDEN_BAND_PRIORITY_26" : _aidl_v == 79 ? "LTE_HIDDEN_BAND_PRIORITY_41" : Integer.toString(_aidl_v);
    }

    static String arrayToString(Object _aidl_v) {
        int[] iArr;
        if (_aidl_v == null) {
            return "null";
        }
        Class<?> _aidl_cls = _aidl_v.getClass();
        if (!_aidl_cls.isArray()) {
            throw new IllegalArgumentException("not an array: " + _aidl_v);
        }
        Class<?> comp = _aidl_cls.getComponentType();
        StringJoiner _aidl_sj = new StringJoiner(", ", NavigationBarInflaterView.SIZE_MOD_START, NavigationBarInflaterView.SIZE_MOD_END);
        if (comp.isArray()) {
            for (int _aidl_i = 0; _aidl_i < Array.getLength(_aidl_v); _aidl_i++) {
                _aidl_sj.add(arrayToString(Array.get(_aidl_v, _aidl_i)));
            }
        } else if (_aidl_cls != int[].class) {
            throw new IllegalArgumentException("wrong type: " + _aidl_cls);
        } else {
            for (int e : (int[]) _aidl_v) {
                _aidl_sj.add(toString(e));
            }
        }
        return _aidl_sj.toString();
    }
}
