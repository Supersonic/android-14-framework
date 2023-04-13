package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface NgranBands$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "BAND_1" : _aidl_v == 2 ? "BAND_2" : _aidl_v == 3 ? "BAND_3" : _aidl_v == 5 ? "BAND_5" : _aidl_v == 7 ? "BAND_7" : _aidl_v == 8 ? "BAND_8" : _aidl_v == 12 ? "BAND_12" : _aidl_v == 14 ? "BAND_14" : _aidl_v == 18 ? "BAND_18" : _aidl_v == 20 ? "BAND_20" : _aidl_v == 25 ? "BAND_25" : _aidl_v == 26 ? "BAND_26" : _aidl_v == 28 ? "BAND_28" : _aidl_v == 29 ? "BAND_29" : _aidl_v == 30 ? "BAND_30" : _aidl_v == 34 ? "BAND_34" : _aidl_v == 38 ? "BAND_38" : _aidl_v == 39 ? "BAND_39" : _aidl_v == 40 ? "BAND_40" : _aidl_v == 41 ? "BAND_41" : _aidl_v == 46 ? "BAND_46" : _aidl_v == 48 ? "BAND_48" : _aidl_v == 50 ? "BAND_50" : _aidl_v == 51 ? "BAND_51" : _aidl_v == 53 ? "BAND_53" : _aidl_v == 65 ? "BAND_65" : _aidl_v == 66 ? "BAND_66" : _aidl_v == 70 ? "BAND_70" : _aidl_v == 71 ? "BAND_71" : _aidl_v == 74 ? "BAND_74" : _aidl_v == 75 ? "BAND_75" : _aidl_v == 76 ? "BAND_76" : _aidl_v == 77 ? "BAND_77" : _aidl_v == 78 ? "BAND_78" : _aidl_v == 79 ? "BAND_79" : _aidl_v == 80 ? "BAND_80" : _aidl_v == 81 ? "BAND_81" : _aidl_v == 82 ? "BAND_82" : _aidl_v == 83 ? "BAND_83" : _aidl_v == 84 ? "BAND_84" : _aidl_v == 86 ? "BAND_86" : _aidl_v == 89 ? "BAND_89" : _aidl_v == 90 ? "BAND_90" : _aidl_v == 91 ? "BAND_91" : _aidl_v == 92 ? "BAND_92" : _aidl_v == 93 ? "BAND_93" : _aidl_v == 94 ? "BAND_94" : _aidl_v == 95 ? "BAND_95" : _aidl_v == 96 ? "BAND_96" : _aidl_v == 257 ? "BAND_257" : _aidl_v == 258 ? "BAND_258" : _aidl_v == 260 ? "BAND_260" : _aidl_v == 261 ? "BAND_261" : Integer.toString(_aidl_v);
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
