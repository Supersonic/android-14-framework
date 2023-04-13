package android.security;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes3.dex */
public interface IGenerateRkpKeyService$Status$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "OK" : _aidl_v == 1 ? "NO_NETWORK_CONNECTIVITY" : _aidl_v == 2 ? "NETWORK_COMMUNICATION_ERROR" : _aidl_v == 4 ? "DEVICE_NOT_REGISTERED" : _aidl_v == 5 ? "HTTP_CLIENT_ERROR" : _aidl_v == 6 ? "HTTP_SERVER_ERROR" : _aidl_v == 7 ? "HTTP_UNKNOWN_ERROR" : _aidl_v == 8 ? "INTERNAL_ERROR" : Integer.toString(_aidl_v);
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
