package android.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
@Deprecated
/* loaded from: classes.dex */
public class InstrumentationTestCase extends TestCase {
    private Instrumentation mInstrumentation;

    public void injectInstrumentation(Instrumentation instrumentation) {
        this.mInstrumentation = instrumentation;
    }

    @Deprecated
    public void injectInsrumentation(Instrumentation instrumentation) {
        injectInstrumentation(instrumentation);
    }

    public Instrumentation getInstrumentation() {
        return this.mInstrumentation;
    }

    public final <T extends Activity> T launchActivity(String pkg, Class<T> activityCls, Bundle extras) {
        Intent intent = new Intent("android.intent.action.MAIN");
        if (extras != null) {
            intent.putExtras(extras);
        }
        return (T) launchActivityWithIntent(pkg, activityCls, intent);
    }

    public final <T extends Activity> T launchActivityWithIntent(String pkg, Class<T> activityCls, Intent intent) {
        intent.setClassName(pkg, activityCls.getName());
        intent.addFlags(268435456);
        T activity = (T) getInstrumentation().startActivitySync(intent);
        getInstrumentation().waitForIdleSync();
        return activity;
    }

    public void runTestOnUiThread(final Runnable r) throws Throwable {
        final Throwable[] exceptions = new Throwable[1];
        getInstrumentation().runOnMainSync(new Runnable() { // from class: android.test.InstrumentationTestCase.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    r.run();
                } catch (Throwable throwable) {
                    exceptions[0] = throwable;
                }
            }
        });
        if (exceptions[0] != null) {
            throw exceptions[0];
        }
    }

    @Override // junit.framework.TestCase
    protected void runTest() throws Throwable {
        String fName = getName();
        assertNotNull(fName);
        Method method = null;
        try {
            Class[] clsArr = null;
            method = getClass().getMethod(fName, null);
        } catch (NoSuchMethodException e) {
            fail("Method \"" + fName + "\" not found");
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            fail("Method \"" + fName + "\" should be public");
        }
        int runCount = 1;
        boolean isRepetitive = false;
        if (method.isAnnotationPresent(FlakyTest.class)) {
            runCount = ((FlakyTest) method.getAnnotation(FlakyTest.class)).tolerance();
        } else if (method.isAnnotationPresent(RepetitiveTest.class)) {
            runCount = ((RepetitiveTest) method.getAnnotation(RepetitiveTest.class)).numIterations();
            isRepetitive = true;
        }
        if (method.isAnnotationPresent(UiThreadTest.class)) {
            final int tolerance = runCount;
            final boolean repetitive = isRepetitive;
            final Method testMethod = method;
            final Throwable[] exceptions = new Throwable[1];
            getInstrumentation().runOnMainSync(new Runnable() { // from class: android.test.InstrumentationTestCase.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        InstrumentationTestCase.this.runMethod(testMethod, tolerance, repetitive);
                    } catch (Throwable throwable) {
                        exceptions[0] = throwable;
                    }
                }
            });
            if (exceptions[0] != null) {
                throw exceptions[0];
            }
            return;
        }
        runMethod(method, runCount, isRepetitive);
    }

    private void runMethod(Method runMethod, int tolerance) throws Throwable {
        runMethod(runMethod, tolerance, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runMethod(Method runMethod, int tolerance, boolean isRepetitive) throws Throwable {
        Throwable exception;
        Bundle iterations;
        int runCount = 0;
        while (true) {
            try {
                try {
                    Object[] objArr = null;
                    runMethod.invoke(this, null);
                    exception = null;
                    runCount++;
                } catch (IllegalAccessException e) {
                    e.fillInStackTrace();
                    exception = e;
                    runCount++;
                    if (isRepetitive) {
                        iterations = new Bundle();
                    }
                } catch (InvocationTargetException e2) {
                    e2.fillInStackTrace();
                    exception = e2.getTargetException();
                    runCount++;
                    if (isRepetitive) {
                        iterations = new Bundle();
                    }
                }
                if (isRepetitive) {
                    iterations = new Bundle();
                    iterations.putInt("currentiterations", runCount);
                    getInstrumentation().sendStatus(2, iterations);
                }
                if (runCount >= tolerance || (!isRepetitive && exception == null)) {
                    break;
                }
            } catch (Throwable th) {
                int runCount2 = runCount + 1;
                if (isRepetitive) {
                    Bundle iterations2 = new Bundle();
                    iterations2.putInt("currentiterations", runCount2);
                    getInstrumentation().sendStatus(2, iterations2);
                }
                throw th;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    public void sendKeys(String keysSequence) {
        int keyCount;
        String[] keys = keysSequence.split(" ");
        Instrumentation instrumentation = getInstrumentation();
        for (String key : keys) {
            int repeater = key.indexOf(42);
            if (repeater == -1) {
                keyCount = 1;
            } else {
                try {
                    keyCount = Integer.parseInt(key.substring(0, repeater));
                } catch (NumberFormatException e) {
                    Log.w("ActivityTestCase", "Invalid repeat count: " + key);
                }
            }
            if (repeater != -1) {
                key = key.substring(repeater + 1);
            }
            for (int j = 0; j < keyCount; j++) {
                try {
                    Field keyCodeField = KeyEvent.class.getField("KEYCODE_" + key);
                    int keyCode = keyCodeField.getInt(null);
                    try {
                        instrumentation.sendKeyDownUpSync(keyCode);
                    } catch (SecurityException e2) {
                    }
                } catch (IllegalAccessException e3) {
                    Log.w("ActivityTestCase", "Unknown keycode: KEYCODE_" + key);
                } catch (NoSuchFieldException e4) {
                    Log.w("ActivityTestCase", "Unknown keycode: KEYCODE_" + key);
                }
            }
        }
        instrumentation.waitForIdleSync();
    }

    public void sendKeys(int... keys) {
        Instrumentation instrumentation = getInstrumentation();
        for (int i : keys) {
            try {
                instrumentation.sendKeyDownUpSync(i);
            } catch (SecurityException e) {
            }
        }
        instrumentation.waitForIdleSync();
    }

    public void sendRepeatedKeys(int... keys) {
        int count = keys.length;
        if ((count & 1) == 1) {
            throw new IllegalArgumentException("The size of the keys array must be a multiple of 2");
        }
        Instrumentation instrumentation = getInstrumentation();
        for (int i = 0; i < count; i += 2) {
            int keyCount = keys[i];
            int keyCode = keys[i + 1];
            for (int j = 0; j < keyCount; j++) {
                try {
                    instrumentation.sendKeyDownUpSync(keyCode);
                } catch (SecurityException e) {
                }
            }
        }
        instrumentation.waitForIdleSync();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // junit.framework.TestCase
    public void tearDown() throws Exception {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        super.tearDown();
    }
}
