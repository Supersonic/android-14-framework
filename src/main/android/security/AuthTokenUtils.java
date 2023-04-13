package android.security;

import android.hardware.security.keymint.HardwareAuthToken;
import android.hardware.security.secureclock.Timestamp;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes3.dex */
public class AuthTokenUtils {
    private AuthTokenUtils() {
    }

    public static HardwareAuthToken toHardwareAuthToken(byte[] array) {
        HardwareAuthToken hardwareAuthToken = new HardwareAuthToken();
        hardwareAuthToken.challenge = ByteBuffer.wrap(array, 1, 8).order(ByteOrder.nativeOrder()).getLong();
        hardwareAuthToken.userId = ByteBuffer.wrap(array, 9, 8).order(ByteOrder.nativeOrder()).getLong();
        hardwareAuthToken.authenticatorId = ByteBuffer.wrap(array, 17, 8).order(ByteOrder.nativeOrder()).getLong();
        hardwareAuthToken.authenticatorType = ByteBuffer.wrap(array, 25, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        Timestamp timestamp = new Timestamp();
        timestamp.milliSeconds = ByteBuffer.wrap(array, 29, 8).order(ByteOrder.BIG_ENDIAN).getLong();
        hardwareAuthToken.timestamp = timestamp;
        hardwareAuthToken.mac = new byte[32];
        System.arraycopy(array, 37, hardwareAuthToken.mac, 0, 32);
        return hardwareAuthToken;
    }
}
