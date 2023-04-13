package com.android.internal.util;

import android.util.Log;
import android.util.SparseArray;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/* loaded from: classes3.dex */
public class MessageUtils {
    private static final boolean DBG = false;
    private static final String TAG = MessageUtils.class.getSimpleName();
    public static final String[] DEFAULT_PREFIXES = {"CMD_", "EVENT_"};

    /* loaded from: classes3.dex */
    public static class DuplicateConstantError extends Error {
        private DuplicateConstantError() {
        }

        public DuplicateConstantError(String name1, String name2, int value) {
            super(String.format("Duplicate constant value: both %s and %s = %d", name1, name2, Integer.valueOf(value)));
        }
    }

    public static SparseArray<String> findMessageNames(Class[] classes, String[] prefixes) {
        Class[] clsArr = classes;
        String[] strArr = prefixes;
        SparseArray<String> messageNames = new SparseArray<>();
        int length = clsArr.length;
        int i = 0;
        loop0: while (i < length) {
            Class c = clsArr[i];
            String className = c.getName();
            try {
                Field[] fields = c.getDeclaredFields();
                int length2 = fields.length;
                int i2 = 0;
                while (i2 < length2) {
                    Field field = fields[i2];
                    int modifiers = field.getModifiers();
                    if (!((!Modifier.isStatic(modifiers)) | (!Modifier.isFinal(modifiers)))) {
                        String name = field.getName();
                        int length3 = strArr.length;
                        int i3 = 0;
                        while (i3 < length3) {
                            String prefix = strArr[i3];
                            if (name.startsWith(prefix)) {
                                try {
                                    field.setAccessible(true);
                                    try {
                                        int value = field.getInt(null);
                                        String previousName = messageNames.get(value);
                                        if (previousName != null && !previousName.equals(name)) {
                                            throw new DuplicateConstantError(name, previousName, value);
                                            break loop0;
                                        }
                                        messageNames.put(value, name);
                                    } catch (ExceptionInInitializerError | IllegalArgumentException e) {
                                    }
                                } catch (IllegalAccessException | SecurityException e2) {
                                }
                            }
                            i3++;
                            strArr = prefixes;
                        }
                        continue;
                    }
                    i2++;
                    strArr = prefixes;
                }
                continue;
            } catch (SecurityException e3) {
                Log.m110e(TAG, "Can't list fields of class " + className);
            }
            i++;
            clsArr = classes;
            strArr = prefixes;
        }
        return messageNames;
    }

    public static SparseArray<String> findMessageNames(Class[] classNames) {
        return findMessageNames(classNames, DEFAULT_PREFIXES);
    }
}
