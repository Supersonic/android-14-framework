package android.hardware.radio.sim;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface PersoSubstate$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? "UNKNOWN" : _aidl_v == 1 ? "IN_PROGRESS" : _aidl_v == 2 ? "READY" : _aidl_v == 3 ? "SIM_NETWORK" : _aidl_v == 4 ? "SIM_NETWORK_SUBSET" : _aidl_v == 5 ? "SIM_CORPORATE" : _aidl_v == 6 ? "SIM_SERVICE_PROVIDER" : _aidl_v == 7 ? "SIM_SIM" : _aidl_v == 8 ? "SIM_NETWORK_PUK" : _aidl_v == 9 ? "SIM_NETWORK_SUBSET_PUK" : _aidl_v == 10 ? "SIM_CORPORATE_PUK" : _aidl_v == 11 ? "SIM_SERVICE_PROVIDER_PUK" : _aidl_v == 12 ? "SIM_SIM_PUK" : _aidl_v == 13 ? "RUIM_NETWORK1" : _aidl_v == 14 ? "RUIM_NETWORK2" : _aidl_v == 15 ? "RUIM_HRPD" : _aidl_v == 16 ? "RUIM_CORPORATE" : _aidl_v == 17 ? "RUIM_SERVICE_PROVIDER" : _aidl_v == 18 ? "RUIM_RUIM" : _aidl_v == 19 ? "RUIM_NETWORK1_PUK" : _aidl_v == 20 ? "RUIM_NETWORK2_PUK" : _aidl_v == 21 ? "RUIM_HRPD_PUK" : _aidl_v == 22 ? "RUIM_CORPORATE_PUK" : _aidl_v == 23 ? "RUIM_SERVICE_PROVIDER_PUK" : _aidl_v == 24 ? "RUIM_RUIM_PUK" : _aidl_v == 25 ? "SIM_SPN" : _aidl_v == 26 ? "SIM_SPN_PUK" : _aidl_v == 27 ? "SIM_SP_EHPLMN" : _aidl_v == 28 ? "SIM_SP_EHPLMN_PUK" : _aidl_v == 29 ? "SIM_ICCID" : _aidl_v == 30 ? "SIM_ICCID_PUK" : _aidl_v == 31 ? "SIM_IMPI" : _aidl_v == 32 ? "SIM_IMPI_PUK" : _aidl_v == 33 ? "SIM_NS_SP" : _aidl_v == 34 ? "SIM_NS_SP_PUK" : Integer.toString(_aidl_v);
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
