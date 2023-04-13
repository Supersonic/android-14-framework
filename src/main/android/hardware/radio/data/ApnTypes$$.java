package android.hardware.radio.data;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.security.keystore.KeyProperties;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface ApnTypes$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? KeyProperties.DIGEST_NONE : _aidl_v == 1 ? "DEFAULT" : _aidl_v == 2 ? "MMS" : _aidl_v == 4 ? "SUPL" : _aidl_v == 8 ? "DUN" : _aidl_v == 16 ? "HIPRI" : _aidl_v == 32 ? "FOTA" : _aidl_v == 64 ? "IMS" : _aidl_v == 128 ? "CBS" : _aidl_v == 256 ? "IA" : _aidl_v == 512 ? "EMERGENCY" : _aidl_v == 1024 ? "MCX" : _aidl_v == 2048 ? "XCAP" : _aidl_v == 4096 ? "VSIM" : _aidl_v == 8192 ? "BIP" : _aidl_v == 16384 ? "ENTERPRISE" : Integer.toString(_aidl_v);
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
