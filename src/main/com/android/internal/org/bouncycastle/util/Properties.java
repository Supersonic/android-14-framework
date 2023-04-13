package com.android.internal.org.bouncycastle.util;

import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
/* loaded from: classes4.dex */
public class Properties {
    private static final ThreadLocal threadProperties = new ThreadLocal();

    private Properties() {
    }

    public static boolean isOverrideSet(String propertyName) {
        try {
            return isSetTrue(getPropertyValue(propertyName));
        } catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean isOverrideSetTo(String propertyName, boolean isTrue) {
        try {
            String propertyValue = getPropertyValue(propertyName);
            if (isTrue) {
                return isSetTrue(propertyValue);
            }
            return isSetFalse(propertyValue);
        } catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean setThreadOverride(String propertyName, boolean enable) {
        boolean isSet = isOverrideSet(propertyName);
        ThreadLocal threadLocal = threadProperties;
        Map localProps = (Map) threadLocal.get();
        if (localProps == null) {
            localProps = new HashMap();
            threadLocal.set(localProps);
        }
        localProps.put(propertyName, enable ? "true" : "false");
        return isSet;
    }

    public static boolean removeThreadOverride(String propertyName) {
        String p;
        ThreadLocal threadLocal = threadProperties;
        Map localProps = (Map) threadLocal.get();
        if (localProps != null && (p = (String) localProps.remove(propertyName)) != null) {
            if (localProps.isEmpty()) {
                threadLocal.remove();
            }
            return "true".equals(Strings.toLowerCase(p));
        }
        return false;
    }

    public static BigInteger asBigInteger(String propertyName) {
        String p = getPropertyValue(propertyName);
        if (p != null) {
            return new BigInteger(p);
        }
        return null;
    }

    public static Set<String> asKeySet(String propertyName) {
        Set<String> set = new HashSet<>();
        String p = getPropertyValue(propertyName);
        if (p != null) {
            StringTokenizer sTok = new StringTokenizer(p, ",");
            while (sTok.hasMoreElements()) {
                set.add(Strings.toLowerCase(sTok.nextToken()).trim());
            }
        }
        return Collections.unmodifiableSet(set);
    }

    public static String getPropertyValue(final String propertyName) {
        String p;
        String val = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.android.internal.org.bouncycastle.util.Properties.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return Security.getProperty(propertyName);
            }
        });
        if (val != null) {
            return val;
        }
        Map localProps = (Map) threadProperties.get();
        if (localProps != null && (p = (String) localProps.get(propertyName)) != null) {
            return p;
        }
        return (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.android.internal.org.bouncycastle.util.Properties.2
            @Override // java.security.PrivilegedAction
            public Object run() {
                return System.getProperty(propertyName);
            }
        });
    }

    private static boolean isSetFalse(String p) {
        if (p == null || p.length() != 5) {
            return false;
        }
        if (p.charAt(0) != 'f' && p.charAt(0) != 'F') {
            return false;
        }
        if (p.charAt(1) != 'a' && p.charAt(1) != 'A') {
            return false;
        }
        if (p.charAt(2) != 'l' && p.charAt(2) != 'L') {
            return false;
        }
        if (p.charAt(3) != 's' && p.charAt(3) != 'S') {
            return false;
        }
        if (p.charAt(4) != 'e' && p.charAt(4) != 'E') {
            return false;
        }
        return true;
    }

    private static boolean isSetTrue(String p) {
        if (p == null || p.length() != 4) {
            return false;
        }
        if (p.charAt(0) != 't' && p.charAt(0) != 'T') {
            return false;
        }
        if (p.charAt(1) != 'r' && p.charAt(1) != 'R') {
            return false;
        }
        if (p.charAt(2) != 'u' && p.charAt(2) != 'U') {
            return false;
        }
        if (p.charAt(3) != 'e' && p.charAt(3) != 'E') {
            return false;
        }
        return true;
    }
}
