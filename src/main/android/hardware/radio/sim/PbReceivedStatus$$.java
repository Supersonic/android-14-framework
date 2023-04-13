package android.hardware.radio.sim;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface PbReceivedStatus$$ {
    static String toString(byte _aidl_v) {
        return _aidl_v == 1 ? "PB_RECEIVED_OK" : _aidl_v == 2 ? "PB_RECEIVED_ERROR" : _aidl_v == 3 ? "PB_RECEIVED_ABORT" : _aidl_v == 4 ? "PB_RECEIVED_FINAL" : Byte.toString(_aidl_v);
    }

    static String arrayToString(Object _aidl_v) {
        byte[] bArr;
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
        } else if (_aidl_cls != byte[].class) {
            throw new IllegalArgumentException("wrong type: " + _aidl_cls);
        } else {
            for (byte e : (byte[]) _aidl_v) {
                _aidl_sj.add(toString(e));
            }
        }
        return _aidl_sj.toString();
    }
}
