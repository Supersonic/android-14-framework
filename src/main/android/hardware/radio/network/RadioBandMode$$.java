package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface RadioBandMode$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "BAND_MODE_UNSPECIFIED" : _aidl_v == 1 ? "BAND_MODE_EURO" : _aidl_v == 2 ? "BAND_MODE_USA" : _aidl_v == 3 ? "BAND_MODE_JPN" : _aidl_v == 4 ? "BAND_MODE_AUS" : _aidl_v == 5 ? "BAND_MODE_AUS_2" : _aidl_v == 6 ? "BAND_MODE_CELL_800" : _aidl_v == 7 ? "BAND_MODE_PCS" : _aidl_v == 8 ? "BAND_MODE_JTACS" : _aidl_v == 9 ? "BAND_MODE_KOREA_PCS" : _aidl_v == 10 ? "BAND_MODE_5_450M" : _aidl_v == 11 ? "BAND_MODE_IMT2000" : _aidl_v == 12 ? "BAND_MODE_7_700M_2" : _aidl_v == 13 ? "BAND_MODE_8_1800M" : _aidl_v == 14 ? "BAND_MODE_9_900M" : _aidl_v == 15 ? "BAND_MODE_10_800M_2" : _aidl_v == 16 ? "BAND_MODE_EURO_PAMR_400M" : _aidl_v == 17 ? "BAND_MODE_AWS" : _aidl_v == 18 ? "BAND_MODE_USA_2500M" : Integer.toString(_aidl_v);
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
