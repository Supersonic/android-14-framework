package android.hardware.radio;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.telephony.DctConstants;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface RadioAccessFamily$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "UNKNOWN" : _aidl_v == 2 ? "GPRS" : _aidl_v == 4 ? "EDGE" : _aidl_v == 8 ? "UMTS" : _aidl_v == 16 ? "IS95A" : _aidl_v == 32 ? "IS95B" : _aidl_v == 64 ? "ONE_X_RTT" : _aidl_v == 128 ? "EVDO_0" : _aidl_v == 256 ? "EVDO_A" : _aidl_v == 512 ? "HSDPA" : _aidl_v == 1024 ? "HSUPA" : _aidl_v == 2048 ? "HSPA" : _aidl_v == 4096 ? "EVDO_B" : _aidl_v == 8192 ? "EHRPD" : _aidl_v == 16384 ? DctConstants.RAT_NAME_LTE : _aidl_v == 32768 ? "HSPAP" : _aidl_v == 65536 ? "GSM" : _aidl_v == 131072 ? "TD_SCDMA" : _aidl_v == 262144 ? "IWLAN" : _aidl_v == 524288 ? "LTE_CA" : _aidl_v == 1048576 ? "NR" : Integer.toString(_aidl_v);
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
