package android.hardware.radio;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.telephony.DctConstants;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface RadioTechnology$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "UNKNOWN" : _aidl_v == 1 ? "GPRS" : _aidl_v == 2 ? "EDGE" : _aidl_v == 3 ? "UMTS" : _aidl_v == 4 ? "IS95A" : _aidl_v == 5 ? "IS95B" : _aidl_v == 6 ? "ONE_X_RTT" : _aidl_v == 7 ? "EVDO_0" : _aidl_v == 8 ? "EVDO_A" : _aidl_v == 9 ? "HSDPA" : _aidl_v == 10 ? "HSUPA" : _aidl_v == 11 ? "HSPA" : _aidl_v == 12 ? "EVDO_B" : _aidl_v == 13 ? "EHRPD" : _aidl_v == 14 ? DctConstants.RAT_NAME_LTE : _aidl_v == 15 ? "HSPAP" : _aidl_v == 16 ? "GSM" : _aidl_v == 17 ? "TD_SCDMA" : _aidl_v == 18 ? "IWLAN" : _aidl_v == 19 ? "LTE_CA" : _aidl_v == 20 ? "NR" : Integer.toString(_aidl_v);
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
