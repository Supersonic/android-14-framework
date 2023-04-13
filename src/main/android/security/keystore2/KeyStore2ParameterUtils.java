package android.security.keystore2;

import android.app.AppGlobals;
import android.hardware.biometrics.BiometricManager;
import android.hardware.security.keymint.KeyParameter;
import android.hardware.security.keymint.KeyParameterValue;
import android.hardware.security.keymint.Tag;
import android.security.GateKeeper;
import android.security.keymaster.KeymasterDefs;
import android.security.keystore.UserAuthArgs;
import android.system.keystore2.Authorization;
import java.math.BigInteger;
import java.security.ProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public abstract class KeyStore2ParameterUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeBool(int tag) {
        int type = KeymasterDefs.getTagType(tag);
        if (type != 1879048192) {
            throw new IllegalArgumentException("Not a boolean tag: " + tag);
        }
        KeyParameter p = new KeyParameter();
        p.tag = tag;
        p.value = KeyParameterValue.boolValue(true);
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeEnum(int tag, int v) {
        KeyParameter kp = new KeyParameter();
        kp.tag = tag;
        switch (tag) {
            case 268435458:
                kp.value = KeyParameterValue.algorithm(v);
                break;
            case 268435466:
                kp.value = KeyParameterValue.ecCurve(v);
                break;
            case Tag.HARDWARE_TYPE /* 268435760 */:
                kp.value = KeyParameterValue.securityLevel(v);
                break;
            case 268435960:
                kp.value = KeyParameterValue.hardwareAuthenticatorType(v);
                break;
            case 268436158:
                kp.value = KeyParameterValue.origin(v);
                break;
            case 536870913:
                kp.value = KeyParameterValue.keyPurpose(v);
                break;
            case 536870916:
                kp.value = KeyParameterValue.blockMode(v);
                break;
            case 536870917:
            case 536871115:
                kp.value = KeyParameterValue.digest(v);
                break;
            case 536870918:
                kp.value = KeyParameterValue.paddingMode(v);
                break;
            default:
                throw new IllegalArgumentException("Not an enum or repeatable enum tag: " + tag);
        }
        return kp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeInt(int tag, int v) {
        int type = KeymasterDefs.getTagType(tag);
        if (type != 805306368 && type != 1073741824) {
            throw new IllegalArgumentException("Not an int or repeatable int tag: " + tag);
        }
        KeyParameter p = new KeyParameter();
        p.tag = tag;
        p.value = KeyParameterValue.integer(v);
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeLong(int tag, long v) {
        int type = KeymasterDefs.getTagType(tag);
        if (type != 1342177280 && type != -1610612736) {
            throw new IllegalArgumentException("Not a long or repeatable long tag: " + tag);
        }
        KeyParameter p = new KeyParameter();
        p.tag = tag;
        p.value = KeyParameterValue.longInteger(v);
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeBytes(int tag, byte[] b) {
        if (KeymasterDefs.getTagType(tag) != -1879048192) {
            throw new IllegalArgumentException("Not a bytes tag: " + tag);
        }
        KeyParameter p = new KeyParameter();
        p.tag = tag;
        p.value = KeyParameterValue.blob(b);
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeBignum(int tag, BigInteger b) {
        if (KeymasterDefs.getTagType(tag) != Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Not a bignum tag: " + tag);
        }
        KeyParameter p = new KeyParameter();
        p.tag = tag;
        p.value = KeyParameterValue.blob(b.toByteArray());
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyParameter makeDate(int tag, Date date) {
        if (KeymasterDefs.getTagType(tag) != 1610612736) {
            throw new IllegalArgumentException("Not a date tag: " + tag);
        }
        KeyParameter p = new KeyParameter();
        p.tag = tag;
        p.value = KeyParameterValue.dateTime(date.getTime());
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSecureHardware(int securityLevel) {
        return securityLevel == 1 || securityLevel == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getUnsignedInt(Authorization param) {
        if (KeymasterDefs.getTagType(param.keyParameter.tag) != 805306368) {
            throw new IllegalArgumentException("Not an int tag: " + param.keyParameter.tag);
        }
        return param.keyParameter.value.getInteger() & 4294967295L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Date getDate(Authorization param) {
        if (KeymasterDefs.getTagType(param.keyParameter.tag) != 1610612736) {
            throw new IllegalArgumentException("Not a date tag: " + param.keyParameter.tag);
        }
        if (param.keyParameter.value.getDateTime() < 0) {
            throw new IllegalArgumentException("Date Value too large: " + param.keyParameter.value.getDateTime());
        }
        return new Date(param.keyParameter.value.getDateTime());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void forEachSetFlag(int flags, Consumer<Integer> consumer) {
        int offset = 0;
        while (flags != 0) {
            if ((flags & 1) == 1) {
                consumer.accept(Integer.valueOf(1 << offset));
            }
            offset++;
            flags >>>= 1;
        }
    }

    private static long getRootSid() {
        long rootSid = GateKeeper.getSecureUserId();
        if (rootSid == 0) {
            throw new IllegalStateException("Secure lock screen must be enabled to create keys requiring user authentication");
        }
        return rootSid;
    }

    private static void addSids(List<KeyParameter> params, UserAuthArgs spec) {
        if (spec.getUserAuthenticationType() == 3) {
            if (spec.getBoundToSpecificSecureUserId() != 0) {
                params.add(makeLong(-1610612234, spec.getBoundToSpecificSecureUserId()));
                return;
            } else {
                params.add(makeLong(-1610612234, getRootSid()));
                return;
            }
        }
        List<Long> sids = new ArrayList<>();
        if ((spec.getUserAuthenticationType() & 2) != 0) {
            BiometricManager bm = (BiometricManager) AppGlobals.getInitialApplication().getSystemService(BiometricManager.class);
            long[] biometricSids = bm.getAuthenticatorIds();
            if (biometricSids.length == 0) {
                throw new IllegalStateException("At least one biometric must be enrolled to create keys requiring user authentication for every use");
            }
            if (spec.getBoundToSpecificSecureUserId() != 0) {
                sids.add(Long.valueOf(spec.getBoundToSpecificSecureUserId()));
            } else if (spec.isInvalidatedByBiometricEnrollment()) {
                for (long sid : biometricSids) {
                    sids.add(Long.valueOf(sid));
                }
            } else {
                sids.add(Long.valueOf(getRootSid()));
            }
        } else if ((spec.getUserAuthenticationType() & 1) != 0) {
            sids.add(Long.valueOf(getRootSid()));
        } else {
            throw new IllegalStateException("Invalid or no authentication type specified.");
        }
        for (int i = 0; i < sids.size(); i++) {
            params.add(makeLong(-1610612234, sids.get(i).longValue()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addUserAuthArgs(List<KeyParameter> args, UserAuthArgs spec) {
        if (spec.isUserConfirmationRequired()) {
            args.add(makeBool(1879048700));
        }
        if (spec.isUserPresenceRequired()) {
            args.add(makeBool(1879048699));
        }
        if (spec.isUnlockedDeviceRequired()) {
            args.add(makeBool(1879048701));
        }
        if (!spec.isUserAuthenticationRequired()) {
            args.add(makeBool(1879048695));
        } else if (spec.getUserAuthenticationValidityDurationSeconds() == 0) {
            addSids(args, spec);
            args.add(makeEnum(268435960, spec.getUserAuthenticationType()));
            if (spec.isUserAuthenticationValidWhileOnBody()) {
                throw new ProviderException("Key validity extension while device is on-body is not supported for keys requiring fingerprint authentication");
            }
        } else {
            addSids(args, spec);
            args.add(makeEnum(268435960, spec.getUserAuthenticationType()));
            args.add(makeInt(805306873, spec.getUserAuthenticationValidityDurationSeconds()));
            if (spec.isUserAuthenticationValidWhileOnBody()) {
                args.add(makeBool(1879048698));
            }
        }
    }
}
