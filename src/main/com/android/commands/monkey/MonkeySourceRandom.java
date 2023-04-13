package com.android.commands.monkey;

import android.content.ComponentName;
import android.graphics.PointF;
import android.hardware.display.DisplayManagerGlobal;
import android.os.SystemClock;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
/* loaded from: classes.dex */
public class MonkeySourceRandom implements MonkeyEventSource {
    public static final int FACTORZ_COUNT = 12;
    public static final int FACTOR_ANYTHING = 11;
    public static final int FACTOR_APPSWITCH = 9;
    public static final int FACTOR_FLIP = 10;
    public static final int FACTOR_MAJORNAV = 7;
    public static final int FACTOR_MOTION = 1;
    public static final int FACTOR_NAV = 6;
    public static final int FACTOR_PERMISSION = 5;
    public static final int FACTOR_PINCHZOOM = 2;
    public static final int FACTOR_ROTATION = 4;
    public static final int FACTOR_SYSOPS = 8;
    public static final int FACTOR_TOUCH = 0;
    public static final int FACTOR_TRACKBALL = 3;
    private static final int GESTURE_DRAG = 1;
    private static final int GESTURE_PINCH_OR_ZOOM = 2;
    private static final int GESTURE_TAP = 0;
    private static final int[] SCREEN_ROTATION_DEGREES;
    private float[] mFactors;
    private HashMap<ComponentName, String> mMainApps;
    private MonkeyPermissionUtil mPermissionUtil;

    /* renamed from: mQ */
    private MonkeyEventQueue f1mQ;
    private Random mRandom;
    private static final int[] NAV_KEYS = {19, 20, 21, 22};
    private static final int[] MAJOR_NAV_KEYS = {82, 23};
    private static final int[] SYS_KEYS = {3, 4, 5, 6, 24, 25, 164, 91};
    private static final boolean[] PHYSICAL_KEY_EXISTS = new boolean[KeyEvent.getMaxKeyCode() + 1];
    private int mEventCount = 0;
    private int mVerbose = 0;
    private long mThrottle = 0;
    private boolean mKeyboardOpen = false;

    static {
        int i = 0;
        while (true) {
            boolean[] zArr = PHYSICAL_KEY_EXISTS;
            if (i >= zArr.length) {
                break;
            }
            zArr[i] = true;
            i++;
        }
        int i2 = 0;
        while (true) {
            int[] iArr = SYS_KEYS;
            if (i2 < iArr.length) {
                boolean[] zArr2 = PHYSICAL_KEY_EXISTS;
                int i3 = iArr[i2];
                zArr2[i3] = KeyCharacterMap.deviceHasKey(i3);
                i2++;
            } else {
                SCREEN_ROTATION_DEGREES = new int[]{0, 1, 2, 3};
                return;
            }
        }
    }

    public static String getKeyName(int keycode) {
        return KeyEvent.keyCodeToString(keycode);
    }

    public static int getKeyCode(String keyName) {
        return KeyEvent.keyCodeFromString(keyName);
    }

    public MonkeySourceRandom(Random random, HashMap<ComponentName, String> MainApps, long throttle, boolean randomizeThrottle, boolean permissionTargetSystem) {
        this.mFactors = r0;
        float[] fArr = {15.0f, 10.0f, 2.0f, 15.0f, 0.0f, 0.0f, 25.0f, 15.0f, 2.0f, 2.0f, 1.0f, 13.0f};
        this.mRandom = random;
        this.mMainApps = MainApps;
        this.f1mQ = new MonkeyEventQueue(random, throttle, randomizeThrottle);
        MonkeyPermissionUtil monkeyPermissionUtil = new MonkeyPermissionUtil();
        this.mPermissionUtil = monkeyPermissionUtil;
        monkeyPermissionUtil.setTargetSystemPackages(permissionTargetSystem);
    }

    private boolean adjustEventFactors() {
        float userSum = 0.0f;
        float defaultSum = 0.0f;
        int defaultCount = 0;
        for (int i = 0; i < 12; i++) {
            float f = this.mFactors[i];
            if (f <= 0.0f) {
                userSum -= f;
            } else {
                defaultSum += f;
                defaultCount++;
            }
        }
        if (userSum > 100.0f) {
            Logger.err.println("** Event weights > 100%");
            return false;
        } else if (defaultCount == 0 && (userSum < 99.9f || userSum > 100.1f)) {
            Logger.err.println("** Event weights != 100%");
            return false;
        } else {
            float defaultsTarget = 100.0f - userSum;
            float defaultsAdjustment = defaultsTarget / defaultSum;
            for (int i2 = 0; i2 < 12; i2++) {
                float[] fArr = this.mFactors;
                float f2 = fArr[i2];
                if (f2 <= 0.0f) {
                    fArr[i2] = -f2;
                } else {
                    fArr[i2] = f2 * defaultsAdjustment;
                }
            }
            if (this.mVerbose > 0) {
                Logger.out.println("// Event percentages:");
                for (int i3 = 0; i3 < 12; i3++) {
                    Logger.out.println("//   " + i3 + ": " + this.mFactors[i3] + "%");
                }
            }
            if (validateKeys()) {
                float sum = 0.0f;
                for (int i4 = 0; i4 < 12; i4++) {
                    float[] fArr2 = this.mFactors;
                    sum += fArr2[i4] / 100.0f;
                    fArr2[i4] = sum;
                }
                return true;
            }
            return false;
        }
    }

    private static boolean validateKeyCategory(String catName, int[] keys, float factor) {
        if (factor < 0.1f) {
            return true;
        }
        for (int i : keys) {
            if (PHYSICAL_KEY_EXISTS[i]) {
                return true;
            }
        }
        Logger.err.println("** " + catName + " has no physical keys but with factor " + factor + "%.");
        return false;
    }

    private boolean validateKeys() {
        return validateKeyCategory("NAV_KEYS", NAV_KEYS, this.mFactors[6]) && validateKeyCategory("MAJOR_NAV_KEYS", MAJOR_NAV_KEYS, this.mFactors[7]) && validateKeyCategory("SYS_KEYS", SYS_KEYS, this.mFactors[8]);
    }

    public void setFactors(float[] factors) {
        int c = 12;
        if (factors.length < 12) {
            c = factors.length;
        }
        for (int i = 0; i < c; i++) {
            this.mFactors[i] = factors[i];
        }
    }

    public void setFactors(int index, float v) {
        this.mFactors[index] = v;
    }

    private void generatePointerEvent(Random random, int gesture) {
        int i;
        int i2 = 0;
        Display display = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        PointF p1 = randomPoint(random, display);
        PointF v1 = randomVector(random);
        long downAt = SystemClock.uptimeMillis();
        this.f1mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(0).setDownTime(downAt).addPointer(0, p1.x, p1.y).setIntermediateNote(false));
        int i3 = 2;
        if (gesture == 1) {
            int count = random.nextInt(10);
            int i4 = 0;
            while (i4 < count) {
                randomWalk(random, display, p1, v1);
                this.f1mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(i3).setDownTime(downAt).addPointer(0, p1.x, p1.y).setIntermediateNote(true));
                i4++;
                i3 = 2;
            }
            i = 1;
        } else if (gesture != 2) {
            i = 1;
        } else {
            PointF p2 = randomPoint(random, display);
            PointF v2 = randomVector(random);
            randomWalk(random, display, p1, v1);
            this.f1mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(261).setDownTime(downAt).addPointer(0, p1.x, p1.y).addPointer(1, p2.x, p2.y).setIntermediateNote(true));
            int count2 = random.nextInt(10);
            int i5 = 0;
            while (i5 < count2) {
                randomWalk(random, display, p1, v1);
                randomWalk(random, display, p2, v2);
                this.f1mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(2).setDownTime(downAt).addPointer(i2, p1.x, p1.y).addPointer(1, p2.x, p2.y).setIntermediateNote(true));
                i5++;
                i2 = 0;
            }
            randomWalk(random, display, p1, v1);
            randomWalk(random, display, p2, v2);
            i = 1;
            this.f1mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(262).setDownTime(downAt).addPointer(0, p1.x, p1.y).addPointer(1, p2.x, p2.y).setIntermediateNote(true));
        }
        randomWalk(random, display, p1, v1);
        this.f1mQ.addLast((MonkeyEvent) new MonkeyTouchEvent(i).setDownTime(downAt).addPointer(0, p1.x, p1.y).setIntermediateNote(false));
    }

    private PointF randomPoint(Random random, Display display) {
        return new PointF(random.nextInt(display.getWidth()), random.nextInt(display.getHeight()));
    }

    private PointF randomVector(Random random) {
        return new PointF((random.nextFloat() - 0.5f) * 50.0f, (random.nextFloat() - 0.5f) * 50.0f);
    }

    private void randomWalk(Random random, Display display, PointF point, PointF vector) {
        point.x = Math.max(Math.min(point.x + (random.nextFloat() * vector.x), display.getWidth()), 0.0f);
        point.y = Math.max(Math.min(point.y + (random.nextFloat() * vector.y), display.getHeight()), 0.0f);
    }

    private void generateTrackballEvent(Random random) {
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= 10) {
                break;
            }
            int dX = random.nextInt(10) - 5;
            int dY = random.nextInt(10) - 5;
            MonkeyEventQueue monkeyEventQueue = this.f1mQ;
            MonkeyMotionEvent addPointer = new MonkeyTrackballEvent(2).addPointer(0, dX, dY);
            if (i <= 0) {
                z = false;
            }
            monkeyEventQueue.addLast((MonkeyEvent) addPointer.setIntermediateNote(z));
            i++;
        }
        int i2 = random.nextInt(10);
        if (i2 == 0) {
            long downAt = SystemClock.uptimeMillis();
            this.f1mQ.addLast((MonkeyEvent) new MonkeyTrackballEvent(0).setDownTime(downAt).addPointer(0, 0.0f, 0.0f).setIntermediateNote(true));
            this.f1mQ.addLast((MonkeyEvent) new MonkeyTrackballEvent(1).setDownTime(downAt).addPointer(0, 0.0f, 0.0f).setIntermediateNote(false));
        }
    }

    private void generateRotationEvent(Random random) {
        MonkeyEventQueue monkeyEventQueue = this.f1mQ;
        int[] iArr = SCREEN_ROTATION_DEGREES;
        monkeyEventQueue.addLast((MonkeyEvent) new MonkeyRotationEvent(iArr[random.nextInt(iArr.length)], random.nextBoolean()));
    }

    private void generateEvents() {
        int lastKey;
        float cls = this.mRandom.nextFloat();
        float[] fArr = this.mFactors;
        if (cls < fArr[0]) {
            generatePointerEvent(this.mRandom, 0);
        } else if (cls < fArr[1]) {
            generatePointerEvent(this.mRandom, 1);
        } else if (cls < fArr[2]) {
            generatePointerEvent(this.mRandom, 2);
        } else if (cls < fArr[3]) {
            generateTrackballEvent(this.mRandom);
        } else if (cls < fArr[4]) {
            generateRotationEvent(this.mRandom);
        } else if (cls < fArr[5]) {
            this.f1mQ.add(this.mPermissionUtil.generateRandomPermissionEvent(this.mRandom));
        } else {
            while (true) {
                float[] fArr2 = this.mFactors;
                if (cls < fArr2[6]) {
                    int[] iArr = NAV_KEYS;
                    lastKey = iArr[this.mRandom.nextInt(iArr.length)];
                } else if (cls < fArr2[7]) {
                    int[] iArr2 = MAJOR_NAV_KEYS;
                    lastKey = iArr2[this.mRandom.nextInt(iArr2.length)];
                } else if (cls < fArr2[8]) {
                    int[] iArr3 = SYS_KEYS;
                    lastKey = iArr3[this.mRandom.nextInt(iArr3.length)];
                } else if (cls < fArr2[9]) {
                    MonkeyActivityEvent e = new MonkeyActivityEvent((ComponentName) new ArrayList(this.mMainApps.keySet()).get(this.mRandom.nextInt(this.mMainApps.size())), this.mMainApps);
                    this.f1mQ.addLast((MonkeyEvent) e);
                    return;
                } else if (cls >= fArr2[10]) {
                    lastKey = this.mRandom.nextInt(KeyEvent.getMaxKeyCode() - 1) + 1;
                } else {
                    MonkeyFlipEvent e2 = new MonkeyFlipEvent(this.mKeyboardOpen);
                    this.mKeyboardOpen = !this.mKeyboardOpen;
                    this.f1mQ.addLast((MonkeyEvent) e2);
                    return;
                }
                if (lastKey != 26 && lastKey != 6 && lastKey != 223 && lastKey != 276 && PHYSICAL_KEY_EXISTS[lastKey]) {
                    MonkeyKeyEvent e3 = new MonkeyKeyEvent(0, lastKey);
                    this.f1mQ.addLast((MonkeyEvent) e3);
                    MonkeyKeyEvent e4 = new MonkeyKeyEvent(1, lastKey);
                    this.f1mQ.addLast((MonkeyEvent) e4);
                    return;
                }
            }
        }
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public boolean validate() {
        boolean ret = true;
        if (this.mFactors[5] != 0.0f && ((ret = true & this.mPermissionUtil.populatePermissionsMapping())) && this.mVerbose >= 2) {
            this.mPermissionUtil.dump();
        }
        return adjustEventFactors() & ret;
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public void setVerbose(int verbose) {
        this.mVerbose = verbose;
    }

    public void generateActivity() {
        MonkeyActivityEvent e = new MonkeyActivityEvent((ComponentName) new ArrayList(this.mMainApps.keySet()).get(this.mRandom.nextInt(this.mMainApps.size())), this.mMainApps);
        this.f1mQ.addLast((MonkeyEvent) e);
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public MonkeyEvent getNextEvent() {
        if (this.f1mQ.isEmpty()) {
            generateEvents();
        }
        this.mEventCount++;
        MonkeyEvent e = this.f1mQ.getFirst();
        this.f1mQ.removeFirst();
        return e;
    }
}
