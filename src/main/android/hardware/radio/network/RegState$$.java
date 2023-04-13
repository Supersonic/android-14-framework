package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface RegState$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "NOT_REG_MT_NOT_SEARCHING_OP" : _aidl_v == 1 ? "REG_HOME" : _aidl_v == 2 ? "NOT_REG_MT_SEARCHING_OP" : _aidl_v == 3 ? "REG_DENIED" : _aidl_v == 4 ? "UNKNOWN" : _aidl_v == 5 ? "REG_ROAMING" : _aidl_v == 10 ? "NOT_REG_MT_NOT_SEARCHING_OP_EM" : _aidl_v == 12 ? "NOT_REG_MT_SEARCHING_OP_EM" : _aidl_v == 13 ? "REG_DENIED_EM" : _aidl_v == 14 ? "UNKNOWN_EM" : _aidl_v == 20 ? "REG_EM" : Integer.toString(_aidl_v);
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
