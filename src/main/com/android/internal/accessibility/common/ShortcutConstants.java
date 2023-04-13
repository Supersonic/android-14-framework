package com.android.internal.accessibility.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class ShortcutConstants {
    public static final String CHOOSER_PACKAGE_NAME = "android";
    public static final char SERVICES_SEPARATOR = ':';

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AccessibilityFragmentType {
        public static final int INVISIBLE_TOGGLE = 1;
        public static final int LAUNCH_ACTIVITY = 3;
        public static final int TOGGLE = 2;
        public static final int VOLUME_SHORTCUT_TOGGLE = 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ShortcutMenuMode {
        public static final int EDIT = 1;
        public static final int LAUNCH = 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface UserShortcutType {
        public static final int DEFAULT = 0;
        public static final int HARDWARE = 2;
        public static final int SOFTWARE = 1;
        public static final int TRIPLETAP = 4;
    }

    private ShortcutConstants() {
    }
}
