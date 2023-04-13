package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface GeranBands$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "BAND_T380" : _aidl_v == 2 ? "BAND_T410" : _aidl_v == 3 ? "BAND_450" : _aidl_v == 4 ? "BAND_480" : _aidl_v == 5 ? "BAND_710" : _aidl_v == 6 ? "BAND_750" : _aidl_v == 7 ? "BAND_T810" : _aidl_v == 8 ? "BAND_850" : _aidl_v == 9 ? "BAND_P900" : _aidl_v == 10 ? "BAND_E900" : _aidl_v == 11 ? "BAND_R900" : _aidl_v == 12 ? "BAND_DCS1800" : _aidl_v == 13 ? "BAND_PCS1900" : _aidl_v == 14 ? "BAND_ER900" : Integer.toString(_aidl_v);
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
