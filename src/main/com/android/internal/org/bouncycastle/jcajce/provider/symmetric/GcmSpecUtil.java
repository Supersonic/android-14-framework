package com.android.internal.org.bouncycastle.jcajce.provider.symmetric;

import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.cms.GCMParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import com.android.internal.org.bouncycastle.util.Integers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
/* loaded from: classes4.dex */
class GcmSpecUtil {
    static final Class gcmSpecClass = ClassUtil.loadClass(GcmSpecUtil.class, "javax.crypto.spec.GCMParameterSpec");

    GcmSpecUtil() {
    }

    static boolean gcmSpecExists() {
        return gcmSpecClass != null;
    }

    static boolean isGcmSpec(AlgorithmParameterSpec paramSpec) {
        Class cls = gcmSpecClass;
        return cls != null && cls.isInstance(paramSpec);
    }

    static boolean isGcmSpec(Class paramSpecClass) {
        return gcmSpecClass == paramSpecClass;
    }

    static AlgorithmParameterSpec extractGcmSpec(ASN1Primitive spec) throws InvalidParameterSpecException {
        try {
            GCMParameters gcmParams = GCMParameters.getInstance(spec);
            Constructor constructor = gcmSpecClass.getConstructor(Integer.TYPE, byte[].class);
            return (AlgorithmParameterSpec) constructor.newInstance(Integers.valueOf(gcmParams.getIcvLen() * 8), gcmParams.getNonce());
        } catch (NoSuchMethodException e) {
            throw new InvalidParameterSpecException("No constructor found!");
        } catch (Exception e2) {
            throw new InvalidParameterSpecException("Construction failed: " + e2.getMessage());
        }
    }

    static GCMParameters extractGcmParameters(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        try {
            Class cls = gcmSpecClass;
            Method tLen = cls.getDeclaredMethod("getTLen", new Class[0]);
            Method iv = cls.getDeclaredMethod("getIV", new Class[0]);
            return new GCMParameters((byte[]) iv.invoke(paramSpec, new Object[0]), ((Integer) tLen.invoke(paramSpec, new Object[0])).intValue() / 8);
        } catch (Exception e) {
            throw new InvalidParameterSpecException("Cannot process GCMParameterSpec");
        }
    }
}
