package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.cms.GCMParameters;
import com.android.internal.org.bouncycastle.crypto.params.AEADParameters;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.util.Integers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
/* loaded from: classes4.dex */
public class GcmSpecUtil {
    static final Class gcmSpecClass;

    /* renamed from: iv */
    static final Method f813iv;
    static final Method tLen;

    static {
        Class loadClass = ClassUtil.loadClass(GcmSpecUtil.class, "javax.crypto.spec.GCMParameterSpec");
        gcmSpecClass = loadClass;
        if (loadClass != null) {
            tLen = extractMethod("getTLen");
            f813iv = extractMethod("getIV");
            return;
        }
        tLen = null;
        f813iv = null;
    }

    private static Method extractMethod(final String name) {
        try {
            return (Method) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil.1
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    return GcmSpecUtil.gcmSpecClass.getDeclaredMethod(name, new Class[0]);
                }
            });
        } catch (PrivilegedActionException e) {
            return null;
        }
    }

    public static boolean gcmSpecExists() {
        return gcmSpecClass != null;
    }

    public static boolean isGcmSpec(AlgorithmParameterSpec paramSpec) {
        Class cls = gcmSpecClass;
        return cls != null && cls.isInstance(paramSpec);
    }

    public static boolean isGcmSpec(Class paramSpecClass) {
        return gcmSpecClass == paramSpecClass;
    }

    public static AlgorithmParameterSpec extractGcmSpec(ASN1Primitive spec) throws InvalidParameterSpecException {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AEADParameters extractAeadParameters(final KeyParameter keyParam, final AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        try {
            return (AEADParameters) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil.2
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    return new AEADParameters(KeyParameter.this, ((Integer) GcmSpecUtil.tLen.invoke(params, new Object[0])).intValue(), (byte[]) GcmSpecUtil.f813iv.invoke(params, new Object[0]));
                }
            });
        } catch (Exception e) {
            throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
        }
    }

    public static GCMParameters extractGcmParameters(final AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        try {
            return (GCMParameters) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil.3
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    return new GCMParameters((byte[]) GcmSpecUtil.f813iv.invoke(paramSpec, new Object[0]), ((Integer) GcmSpecUtil.tLen.invoke(paramSpec, new Object[0])).intValue() / 8);
                }
            });
        } catch (Exception e) {
            throw new InvalidParameterSpecException("Cannot process GCMParameterSpec");
        }
    }
}
