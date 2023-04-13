package android.util;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public class DebugUtils {
    public static boolean isObjectSelected(Object object) {
        Method declaredMethod;
        boolean match = false;
        String s = System.getenv("ANDROID_OBJECT_FILTER");
        if (s != null && s.length() > 0) {
            String[] selectors = s.split("@");
            if (object.getClass().getSimpleName().matches(selectors[0])) {
                for (int i = 1; i < selectors.length; i++) {
                    String[] pair = selectors[i].split("=");
                    Class<?> klass = object.getClass();
                    Class<?> parent = klass;
                    do {
                        try {
                            Class[] clsArr = null;
                            declaredMethod = parent.getDeclaredMethod("get" + pair[0].substring(0, 1).toUpperCase(Locale.ROOT) + pair[0].substring(1), null);
                            Class<?> superclass = klass.getSuperclass();
                            parent = superclass;
                            if (superclass == null) {
                                break;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e2) {
                            e2.printStackTrace();
                        } catch (InvocationTargetException e3) {
                            e3.printStackTrace();
                        }
                    } while (declaredMethod == null);
                    if (declaredMethod != null) {
                        Object[] objArr = null;
                        Object value = declaredMethod.invoke(object, null);
                        match |= (value != null ? value.toString() : "null").matches(pair[1]);
                    }
                }
            }
        }
        return match;
    }

    public static void buildShortClassTag(Object cls, StringBuilder out) {
        int end;
        if (cls == null) {
            out.append("null");
            return;
        }
        String simpleName = cls.getClass().getSimpleName();
        if ((simpleName == null || simpleName.isEmpty()) && (end = (simpleName = cls.getClass().getName()).lastIndexOf(46)) > 0) {
            simpleName = simpleName.substring(end + 1);
        }
        out.append(simpleName);
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(cls)));
    }

    public static void printSizeValue(PrintWriter pw, long number) {
        String value;
        float result = (float) number;
        String suffix = "";
        if (result > 900.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        if (result < 1.0f) {
            value = String.format("%.2f", Float.valueOf(result));
        } else if (result < 10.0f) {
            value = String.format("%.1f", Float.valueOf(result));
        } else if (result < 100.0f) {
            value = String.format("%.0f", Float.valueOf(result));
        } else {
            value = String.format("%.0f", Float.valueOf(result));
        }
        pw.print(value);
        pw.print(suffix);
    }

    public static String sizeValueToString(long number, StringBuilder outBuilder) {
        String value;
        if (outBuilder == null) {
            outBuilder = new StringBuilder(32);
        }
        float result = (float) number;
        String suffix = "";
        if (result > 900.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        if (result < 1.0f) {
            value = String.format("%.2f", Float.valueOf(result));
        } else if (result < 10.0f) {
            value = String.format("%.1f", Float.valueOf(result));
        } else if (result < 100.0f) {
            value = String.format("%.0f", Float.valueOf(result));
        } else {
            value = String.format("%.0f", Float.valueOf(result));
        }
        outBuilder.append(value);
        outBuilder.append(suffix);
        return outBuilder.toString();
    }

    public static String valueToString(Class<?> clazz, String prefix, int value) {
        Field[] declaredFields = clazz.getDeclaredFields();
        int length = declaredFields.length;
        for (int i = 0; i < length; i++) {
            Field field = declaredFields[i];
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && field.getType().equals(Integer.TYPE) && field.getName().startsWith(prefix)) {
                try {
                    if (value == field.getInt(null)) {
                        return constNameWithoutPrefix(prefix, field);
                    }
                    continue;
                } catch (IllegalAccessException e) {
                }
            }
        }
        return Integer.toString(value);
    }

    public static String flagsToString(Class<?> clazz, String prefix, long flags) {
        Field[] fieldArr;
        StringBuilder res = new StringBuilder();
        int i = 0;
        boolean flagsWasZero = flags == 0;
        Field[] declaredFields = clazz.getDeclaredFields();
        int length = declaredFields.length;
        long flags2 = flags;
        while (i < length) {
            Field field = declaredFields[i];
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                fieldArr = declaredFields;
            } else if (!field.getType().equals(Integer.TYPE) && !field.getType().equals(Long.TYPE)) {
                fieldArr = declaredFields;
            } else if (!field.getName().startsWith(prefix)) {
                fieldArr = declaredFields;
            } else {
                long value = getFieldValue(field);
                if (value == 0 && flagsWasZero) {
                    return constNameWithoutPrefix(prefix, field);
                }
                if (value == 0 || (flags2 & value) != value) {
                    fieldArr = declaredFields;
                } else {
                    fieldArr = declaredFields;
                    long flags3 = (~value) & flags2;
                    res.append(constNameWithoutPrefix(prefix, field)).append('|');
                    flags2 = flags3;
                }
            }
            i++;
            declaredFields = fieldArr;
        }
        if (flags2 != 0 || res.length() == 0) {
            res.append(Long.toHexString(flags2));
        } else {
            res.deleteCharAt(res.length() - 1);
        }
        return res.toString();
    }

    private static long getFieldValue(Field field) {
        long longValue;
        try {
            longValue = field.getLong(null);
        } catch (IllegalAccessException e) {
        }
        if (longValue != 0) {
            return longValue;
        }
        int intValue = field.getInt(null);
        if (intValue != 0) {
            return intValue;
        }
        return 0L;
    }

    public static String constantToString(Class<?> clazz, String prefix, int value) {
        Field[] declaredFields = clazz.getDeclaredFields();
        int length = declaredFields.length;
        for (int i = 0; i < length; i++) {
            Field field = declaredFields[i];
            int modifiers = field.getModifiers();
            try {
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && field.getType().equals(Integer.TYPE) && field.getName().startsWith(prefix) && field.getInt(null) == value) {
                    return constNameWithoutPrefix(prefix, field);
                }
            } catch (IllegalAccessException e) {
            }
        }
        return prefix + Integer.toString(value);
    }

    private static String constNameWithoutPrefix(String prefix, Field field) {
        return field.getName().substring(prefix.length());
    }

    public static List<String> callersWithin(final Class<?> cls, int offset) {
        List<String> result = (List) Arrays.stream(Thread.currentThread().getStackTrace()).skip(offset + 3).filter(new Predicate() { // from class: android.util.DebugUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean startsWith;
                startsWith = ((StackTraceElement) obj).getClassName().startsWith(cls.getName());
                return startsWith;
            }
        }).map(new Function() { // from class: android.util.DebugUtils$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((StackTraceElement) obj).getMethodName();
            }
        }).collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }
}
