package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface UtranBands$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "BAND_1" : _aidl_v == 2 ? "BAND_2" : _aidl_v == 3 ? "BAND_3" : _aidl_v == 4 ? "BAND_4" : _aidl_v == 5 ? "BAND_5" : _aidl_v == 6 ? "BAND_6" : _aidl_v == 7 ? "BAND_7" : _aidl_v == 8 ? "BAND_8" : _aidl_v == 9 ? "BAND_9" : _aidl_v == 10 ? "BAND_10" : _aidl_v == 11 ? "BAND_11" : _aidl_v == 12 ? "BAND_12" : _aidl_v == 13 ? "BAND_13" : _aidl_v == 14 ? "BAND_14" : _aidl_v == 19 ? "BAND_19" : _aidl_v == 20 ? "BAND_20" : _aidl_v == 21 ? "BAND_21" : _aidl_v == 22 ? "BAND_22" : _aidl_v == 25 ? "BAND_25" : _aidl_v == 26 ? "BAND_26" : _aidl_v == 101 ? "BAND_A" : _aidl_v == 102 ? "BAND_B" : _aidl_v == 103 ? "BAND_C" : _aidl_v == 104 ? "BAND_D" : _aidl_v == 105 ? "BAND_E" : _aidl_v == 106 ? "BAND_F" : Integer.toString(_aidl_v);
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
