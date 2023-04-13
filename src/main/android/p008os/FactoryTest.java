package android.p008os;

import com.android.internal.p028os.RoSystemProperties;
/* renamed from: android.os.FactoryTest */
/* loaded from: classes3.dex */
public final class FactoryTest {
    public static final int FACTORY_TEST_HIGH_LEVEL = 2;
    public static final int FACTORY_TEST_LOW_LEVEL = 1;
    public static final int FACTORY_TEST_OFF = 0;

    public static int getMode() {
        return RoSystemProperties.FACTORYTEST;
    }

    public static boolean isLongPressOnPowerOffEnabled() {
        return SystemProperties.getInt("factory.long_press_power_off", 0) != 0;
    }
}
