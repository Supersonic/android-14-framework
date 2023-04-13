package android.hardware.broadcastradio;

import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public interface IdentifierType$$ {
    static String toString(int i) {
        return i == 1000 ? "VENDOR_START" : i == 1999 ? "VENDOR_END" : i == 0 ? "INVALID" : i == 1 ? "AMFM_FREQUENCY_KHZ" : i == 2 ? "RDS_PI" : i == 3 ? "HD_STATION_ID_EXT" : i == 4 ? "HD_STATION_NAME" : i == 5 ? "DAB_SID_EXT" : i == 6 ? "DAB_ENSEMBLE" : i == 7 ? "DAB_SCID" : i == 8 ? "DAB_FREQUENCY_KHZ" : i == 9 ? "DRMO_SERVICE_ID" : i == 10 ? "DRMO_FREQUENCY_KHZ" : i == 12 ? "SXM_SERVICE_ID" : i == 13 ? "SXM_CHANNEL" : Integer.toString(i);
    }

    static String arrayToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class<?> cls = obj.getClass();
        if (!cls.isArray()) {
            throw new IllegalArgumentException("not an array: " + obj);
        }
        Class<?> componentType = cls.getComponentType();
        StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
        int i = 0;
        if (componentType.isArray()) {
            while (i < Array.getLength(obj)) {
                stringJoiner.add(arrayToString(Array.get(obj, i)));
                i++;
            }
        } else if (cls != int[].class) {
            throw new IllegalArgumentException("wrong type: " + cls);
        } else {
            int[] iArr = (int[]) obj;
            int length = iArr.length;
            while (i < length) {
                stringJoiner.add(toString(iArr[i]));
                i++;
            }
        }
        return stringJoiner.toString();
    }
}
