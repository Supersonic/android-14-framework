package android.hardware.radio.voice;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface CdmaOtaProvisionStatus$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "SPL_UNLOCKED" : _aidl_v == 1 ? "SPC_RETRIES_EXCEEDED" : _aidl_v == 2 ? "A_KEY_EXCHANGED" : _aidl_v == 3 ? "SSD_UPDATED" : _aidl_v == 4 ? "NAM_DOWNLOADED" : _aidl_v == 5 ? "MDN_DOWNLOADED" : _aidl_v == 6 ? "IMSI_DOWNLOADED" : _aidl_v == 7 ? "PRL_DOWNLOADED" : _aidl_v == 8 ? "COMMITTED" : _aidl_v == 9 ? "OTAPA_STARTED" : _aidl_v == 10 ? "OTAPA_STOPPED" : _aidl_v == 11 ? "OTAPA_ABORTED" : Integer.toString(_aidl_v);
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
