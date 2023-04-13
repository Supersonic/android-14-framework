package android.hardware.radio.voice;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface AudioQuality$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "UNSPECIFIED" : _aidl_v == 1 ? "AMR" : _aidl_v == 2 ? "AMR_WB" : _aidl_v == 3 ? "GSM_EFR" : _aidl_v == 4 ? "GSM_FR" : _aidl_v == 5 ? "GSM_HR" : _aidl_v == 6 ? "EVRC" : _aidl_v == 7 ? "EVRC_B" : _aidl_v == 8 ? "EVRC_WB" : _aidl_v == 9 ? "EVRC_NW" : Integer.toString(_aidl_v);
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
