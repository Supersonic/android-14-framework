package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface EutranBands$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "BAND_1" : _aidl_v == 2 ? "BAND_2" : _aidl_v == 3 ? "BAND_3" : _aidl_v == 4 ? "BAND_4" : _aidl_v == 5 ? "BAND_5" : _aidl_v == 6 ? "BAND_6" : _aidl_v == 7 ? "BAND_7" : _aidl_v == 8 ? "BAND_8" : _aidl_v == 9 ? "BAND_9" : _aidl_v == 10 ? "BAND_10" : _aidl_v == 11 ? "BAND_11" : _aidl_v == 12 ? "BAND_12" : _aidl_v == 13 ? "BAND_13" : _aidl_v == 14 ? "BAND_14" : _aidl_v == 17 ? "BAND_17" : _aidl_v == 18 ? "BAND_18" : _aidl_v == 19 ? "BAND_19" : _aidl_v == 20 ? "BAND_20" : _aidl_v == 21 ? "BAND_21" : _aidl_v == 22 ? "BAND_22" : _aidl_v == 23 ? "BAND_23" : _aidl_v == 24 ? "BAND_24" : _aidl_v == 25 ? "BAND_25" : _aidl_v == 26 ? "BAND_26" : _aidl_v == 27 ? "BAND_27" : _aidl_v == 28 ? "BAND_28" : _aidl_v == 30 ? "BAND_30" : _aidl_v == 31 ? "BAND_31" : _aidl_v == 33 ? "BAND_33" : _aidl_v == 34 ? "BAND_34" : _aidl_v == 35 ? "BAND_35" : _aidl_v == 36 ? "BAND_36" : _aidl_v == 37 ? "BAND_37" : _aidl_v == 38 ? "BAND_38" : _aidl_v == 39 ? "BAND_39" : _aidl_v == 40 ? "BAND_40" : _aidl_v == 41 ? "BAND_41" : _aidl_v == 42 ? "BAND_42" : _aidl_v == 43 ? "BAND_43" : _aidl_v == 44 ? "BAND_44" : _aidl_v == 45 ? "BAND_45" : _aidl_v == 46 ? "BAND_46" : _aidl_v == 47 ? "BAND_47" : _aidl_v == 48 ? "BAND_48" : _aidl_v == 65 ? "BAND_65" : _aidl_v == 66 ? "BAND_66" : _aidl_v == 68 ? "BAND_68" : _aidl_v == 70 ? "BAND_70" : _aidl_v == 49 ? "BAND_49" : _aidl_v == 50 ? "BAND_50" : _aidl_v == 51 ? "BAND_51" : _aidl_v == 52 ? "BAND_52" : _aidl_v == 53 ? "BAND_53" : _aidl_v == 71 ? "BAND_71" : _aidl_v == 72 ? "BAND_72" : _aidl_v == 73 ? "BAND_73" : _aidl_v == 74 ? "BAND_74" : _aidl_v == 85 ? "BAND_85" : _aidl_v == 87 ? "BAND_87" : _aidl_v == 88 ? "BAND_88" : Integer.toString(_aidl_v);
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
