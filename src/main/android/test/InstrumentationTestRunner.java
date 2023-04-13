package android.test;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Debug;
import android.os.Looper;
import android.test.suitebuilder.TestMethod;
import android.test.suitebuilder.TestPredicates;
import android.test.suitebuilder.TestSuiteBuilder;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import com.android.internal.util.Predicate;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;
import junit.textui.ResultPrinter;
@Deprecated
/* loaded from: classes.dex */
public class InstrumentationTestRunner extends Instrumentation implements TestSuiteProvider {
    static final String ARGUMENT_ANNOTATION = "annotation";
    static final String ARGUMENT_DELAY_MSEC = "delay_msec";
    private static final String ARGUMENT_LOG_ONLY = "log";
    static final String ARGUMENT_NOT_ANNOTATION = "notAnnotation";
    static final String ARGUMENT_TEST_CLASS = "class";
    private static final String ARGUMENT_TEST_PACKAGE = "package";
    private static final String ARGUMENT_TEST_SIZE_PREDICATE = "size";
    private static final String DEFAULT_COVERAGE_FILE_NAME = "coverage.ec";
    private static final String LARGE_SUITE = "large";
    private static final String LOG_TAG = "InstrumentationTestRunner";
    private static final String MEDIUM_SUITE = "medium";
    private static final float MEDIUM_SUITE_MAX_RUNTIME = 1000.0f;
    private static final String REPORT_KEY_COVERAGE_PATH = "coverageFilePath";
    public static final String REPORT_KEY_NAME_CLASS = "class";
    public static final String REPORT_KEY_NAME_TEST = "test";
    public static final String REPORT_KEY_NUM_CURRENT = "current";
    private static final String REPORT_KEY_NUM_ITERATIONS = "numiterations";
    public static final String REPORT_KEY_NUM_TOTAL = "numtests";
    private static final String REPORT_KEY_RUN_TIME = "runtime";
    public static final String REPORT_KEY_STACK = "stack";
    private static final String REPORT_KEY_SUITE_ASSIGNMENT = "suiteassignment";
    public static final String REPORT_VALUE_ID = "InstrumentationTestRunner";
    public static final int REPORT_VALUE_RESULT_ERROR = -1;
    public static final int REPORT_VALUE_RESULT_FAILURE = -2;
    public static final int REPORT_VALUE_RESULT_OK = 0;
    public static final int REPORT_VALUE_RESULT_START = 1;
    private static final String SMALL_SUITE = "small";
    private static final float SMALL_SUITE_MAX_RUNTIME = 100.0f;
    private Bundle mArguments;
    private boolean mCoverage;
    private String mCoverageFilePath;
    private boolean mDebug;
    private int mDelayMsec;
    private boolean mJustCount;
    private String mPackageOfTests;
    private final Bundle mResults = new Bundle();
    private boolean mSuiteAssignmentMode;
    private int mTestCount;
    private AndroidTestRunner mTestRunner;
    private static final Predicate<TestMethod> SELECT_SMALL = TestPredicates.hasAnnotation(SmallTest.class);
    private static final Predicate<TestMethod> SELECT_MEDIUM = TestPredicates.hasAnnotation(MediumTest.class);
    private static final Predicate<TestMethod> SELECT_LARGE = TestPredicates.hasAnnotation(LargeTest.class);

    @Override // android.app.Instrumentation
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        this.mArguments = arguments;
        String[] apkPaths = {getTargetContext().getPackageCodePath(), getContext().getPackageCodePath()};
        ClassPathPackageInfoSource.setApkPaths(apkPaths);
        Predicate<TestMethod> testSizePredicate = null;
        Predicate<TestMethod> testAnnotationPredicate = null;
        Predicate<TestMethod> testNotAnnotationPredicate = null;
        String testClassesArg = null;
        boolean logOnly = false;
        if (arguments != null) {
            testClassesArg = arguments.getString("class");
            this.mDebug = getBooleanArgument(arguments, "debug");
            this.mJustCount = getBooleanArgument(arguments, "count");
            this.mSuiteAssignmentMode = getBooleanArgument(arguments, "suiteAssignment");
            this.mPackageOfTests = arguments.getString(ARGUMENT_TEST_PACKAGE);
            testSizePredicate = getSizePredicateFromArg(arguments.getString(ARGUMENT_TEST_SIZE_PREDICATE));
            testAnnotationPredicate = getAnnotationPredicate(arguments.getString(ARGUMENT_ANNOTATION));
            testNotAnnotationPredicate = getNotAnnotationPredicate(arguments.getString(ARGUMENT_NOT_ANNOTATION));
            logOnly = getBooleanArgument(arguments, ARGUMENT_LOG_ONLY);
            this.mCoverage = getBooleanArgument(arguments, "coverage");
            this.mCoverageFilePath = arguments.getString("coverageFile");
            try {
                Object delay = arguments.get(ARGUMENT_DELAY_MSEC);
                if (delay != null) {
                    this.mDelayMsec = Integer.parseInt(delay.toString());
                }
            } catch (NumberFormatException e) {
                Log.e("InstrumentationTestRunner", "Invalid delay_msec parameter", e);
            }
        }
        TestSuiteBuilder testSuiteBuilder = new TestSuiteBuilder(getClass().getName(), getTargetContext().getClassLoader());
        if (testSizePredicate != null) {
            testSuiteBuilder.addRequirements(testSizePredicate);
        }
        if (testAnnotationPredicate != null) {
            testSuiteBuilder.addRequirements(testAnnotationPredicate);
        }
        if (testNotAnnotationPredicate != null) {
            testSuiteBuilder.addRequirements(testNotAnnotationPredicate);
        }
        if (testClassesArg == null) {
            String str = this.mPackageOfTests;
            if (str != null) {
                testSuiteBuilder.includePackages(str);
            } else {
                TestSuite testSuite = getTestSuite();
                if (testSuite != null) {
                    testSuiteBuilder.addTestSuite(testSuite);
                } else {
                    testSuiteBuilder.includePackages("");
                }
            }
        } else {
            parseTestClasses(testClassesArg, testSuiteBuilder);
        }
        testSuiteBuilder.addRequirements(getBuilderRequirements());
        AndroidTestRunner androidTestRunner = getAndroidTestRunner();
        this.mTestRunner = androidTestRunner;
        androidTestRunner.setContext(getTargetContext());
        this.mTestRunner.setInstrumentation(this);
        this.mTestRunner.setSkipExecution(logOnly);
        this.mTestRunner.setTest(testSuiteBuilder.build());
        int size = this.mTestRunner.getTestCases().size();
        this.mTestCount = size;
        if (this.mSuiteAssignmentMode) {
            this.mTestRunner.addTestListener(new SuiteAssignmentPrinter());
        } else {
            WatcherResultPrinter resultPrinter = new WatcherResultPrinter(size);
            this.mTestRunner.addTestListener(new TestPrinter("TestRunner", false));
            this.mTestRunner.addTestListener(resultPrinter);
        }
        start();
    }

    public Bundle getArguments() {
        return this.mArguments;
    }

    protected void addTestListener(TestListener listener) {
        AndroidTestRunner androidTestRunner = this.mTestRunner;
        if (androidTestRunner != null && listener != null) {
            androidTestRunner.addTestListener(listener);
        }
    }

    List<Predicate<TestMethod>> getBuilderRequirements() {
        return new ArrayList();
    }

    private void parseTestClasses(String testClassArg, TestSuiteBuilder testSuiteBuilder) {
        String[] testClasses = testClassArg.split(",");
        for (String testClass : testClasses) {
            parseTestClass(testClass, testSuiteBuilder);
        }
    }

    private void parseTestClass(String testClassName, TestSuiteBuilder testSuiteBuilder) {
        int methodSeparatorIndex = testClassName.indexOf(35);
        String testMethodName = null;
        if (methodSeparatorIndex > 0) {
            testMethodName = testClassName.substring(methodSeparatorIndex + 1);
            testClassName = testClassName.substring(0, methodSeparatorIndex);
        }
        testSuiteBuilder.addTestClassByName(testClassName, testMethodName, getTargetContext());
    }

    protected AndroidTestRunner getAndroidTestRunner() {
        return new AndroidTestRunner();
    }

    private boolean getBooleanArgument(Bundle arguments, String tag) {
        String tagString = arguments.getString(tag);
        return tagString != null && Boolean.parseBoolean(tagString);
    }

    private Predicate<TestMethod> getSizePredicateFromArg(String sizeArg) {
        if (SMALL_SUITE.equals(sizeArg)) {
            return SELECT_SMALL;
        }
        if (MEDIUM_SUITE.equals(sizeArg)) {
            return SELECT_MEDIUM;
        }
        if (LARGE_SUITE.equals(sizeArg)) {
            return SELECT_LARGE;
        }
        return null;
    }

    private Predicate<TestMethod> getAnnotationPredicate(String annotationClassName) {
        Class<? extends Annotation> annotationClass = getAnnotationClass(annotationClassName);
        if (annotationClass != null) {
            return TestPredicates.hasAnnotation(annotationClass);
        }
        return null;
    }

    private Predicate<TestMethod> getNotAnnotationPredicate(String annotationClassName) {
        Class<? extends Annotation> annotationClass = getAnnotationClass(annotationClassName);
        if (annotationClass != null) {
            return TestPredicates.not(TestPredicates.hasAnnotation(annotationClass));
        }
        return null;
    }

    private Class<? extends Annotation> getAnnotationClass(String annotationClassName) {
        Class cls;
        if (annotationClassName == null) {
            return null;
        }
        try {
            cls = Class.forName(annotationClassName);
        } catch (ClassNotFoundException e) {
            Log.e("InstrumentationTestRunner", String.format("Could not find class for specified annotation %s", annotationClassName));
        }
        if (!cls.isAnnotation()) {
            Log.e("InstrumentationTestRunner", String.format("Provided annotation value %s is not an Annotation", annotationClassName));
            return null;
        }
        return cls;
    }

    void prepareLooper() {
        Looper.prepare();
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0070, code lost:
        if (r11.mCoverage != false) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x00b1, code lost:
        return;
     */
    @Override // android.app.Instrumentation
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onStart() {
        prepareLooper();
        if (this.mJustCount) {
            this.mResults.putString("id", "InstrumentationTestRunner");
            this.mResults.putInt(REPORT_KEY_NUM_TOTAL, this.mTestCount);
            finish(-1, this.mResults);
            return;
        }
        if (this.mDebug) {
            Debug.waitForDebugger();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(byteArrayOutputStream);
        try {
            StringResultPrinter resultPrinter = new StringResultPrinter(writer);
            this.mTestRunner.addTestListener(resultPrinter);
            long startTime = System.currentTimeMillis();
            this.mTestRunner.runTest();
            long runTime = System.currentTimeMillis() - startTime;
            resultPrinter.printResult(this.mTestRunner.getTestResult(), runTime);
            this.mResults.putString("stream", String.format("\nTest results for %s=%s", this.mTestRunner.getTestClassName(), byteArrayOutputStream.toString()));
        } catch (Throwable t) {
            try {
                writer.println(String.format("Test run aborted due to unexpected exception: %s", t.getMessage()));
                t.printStackTrace(writer);
            } finally {
                this.mResults.putString("stream", String.format("\nTest results for %s=%s", this.mTestRunner.getTestClassName(), byteArrayOutputStream.toString()));
                if (this.mCoverage) {
                    generateCoverageReport();
                }
                writer.close();
                finish(-1, this.mResults);
            }
        }
    }

    @Override // android.test.TestSuiteProvider
    public TestSuite getTestSuite() {
        return getAllTests();
    }

    public TestSuite getAllTests() {
        return null;
    }

    public ClassLoader getLoader() {
        return null;
    }

    private void generateCoverageReport() {
        String coverageFilePath = getCoverageFilePath();
        File coverageFile = new File(coverageFilePath);
        try {
            Class<?> emmaRTClass = Class.forName("com.vladium.emma.rt.RT");
            Method dumpCoverageMethod = emmaRTClass.getMethod("dumpCoverageData", coverageFile.getClass(), Boolean.TYPE, Boolean.TYPE);
            dumpCoverageMethod.invoke(null, coverageFile, false, false);
            this.mResults.putString(REPORT_KEY_COVERAGE_PATH, coverageFilePath);
            String currentStream = this.mResults.getString("stream");
            this.mResults.putString("stream", String.format("%s\nGenerated code coverage data to %s", currentStream, coverageFilePath));
        } catch (ClassNotFoundException e) {
            reportEmmaError("Is emma jar on classpath?", e);
        } catch (IllegalAccessException e2) {
            reportEmmaError(e2);
        } catch (IllegalArgumentException e3) {
            reportEmmaError(e3);
        } catch (NoSuchMethodException e4) {
            reportEmmaError(e4);
        } catch (SecurityException e5) {
            reportEmmaError(e5);
        } catch (InvocationTargetException e6) {
            reportEmmaError(e6);
        }
    }

    private String getCoverageFilePath() {
        String str = this.mCoverageFilePath;
        if (str == null) {
            return getTargetContext().getFilesDir().getAbsolutePath() + File.separator + DEFAULT_COVERAGE_FILE_NAME;
        }
        return str;
    }

    private void reportEmmaError(Exception e) {
        reportEmmaError("", e);
    }

    private void reportEmmaError(String hint, Exception e) {
        String msg = "Failed to generate emma coverage. " + hint;
        Log.e("InstrumentationTestRunner", msg, e);
        this.mResults.putString("stream", "\nError: " + msg);
    }

    /* loaded from: classes.dex */
    private class StringResultPrinter extends ResultPrinter {
        public StringResultPrinter(PrintStream writer) {
            super(writer);
        }

        public synchronized void printResult(TestResult result, long runTime) {
            printHeader(runTime);
            printFooter(result);
        }
    }

    /* loaded from: classes.dex */
    private class SuiteAssignmentPrinter implements TestListener {
        private long mEndTime;
        private long mStartTime;
        private Bundle mTestResult;
        private boolean mTimingValid;

        public SuiteAssignmentPrinter() {
        }

        public void startTest(Test test) {
            this.mTimingValid = true;
            this.mStartTime = System.currentTimeMillis();
        }

        public void addError(Test test, Throwable t) {
            this.mTimingValid = false;
        }

        public void addFailure(Test test, junit.framework.AssertionFailedError t) {
            this.mTimingValid = false;
        }

        public void endTest(Test test) {
            String assignmentSuite;
            float runTime;
            this.mEndTime = System.currentTimeMillis();
            this.mTestResult = new Bundle();
            if (this.mTimingValid) {
                long j = this.mStartTime;
                if (j >= 0) {
                    runTime = (float) (this.mEndTime - j);
                    if (runTime < InstrumentationTestRunner.SMALL_SUITE_MAX_RUNTIME && !InstrumentationTestCase.class.isAssignableFrom(test.getClass())) {
                        assignmentSuite = InstrumentationTestRunner.SMALL_SUITE;
                    } else if (runTime < InstrumentationTestRunner.MEDIUM_SUITE_MAX_RUNTIME) {
                        assignmentSuite = InstrumentationTestRunner.MEDIUM_SUITE;
                    } else {
                        assignmentSuite = InstrumentationTestRunner.LARGE_SUITE;
                    }
                    this.mStartTime = -1L;
                    this.mTestResult.putString("stream", test.getClass().getName() + "#" + ((TestCase) test).getName() + "\nin " + assignmentSuite + " suite\nrunTime: " + String.valueOf(runTime) + "\n");
                    this.mTestResult.putFloat(InstrumentationTestRunner.REPORT_KEY_RUN_TIME, runTime);
                    this.mTestResult.putString(InstrumentationTestRunner.REPORT_KEY_SUITE_ASSIGNMENT, assignmentSuite);
                    InstrumentationTestRunner.this.sendStatus(0, this.mTestResult);
                }
            }
            assignmentSuite = "NA";
            runTime = -1.0f;
            this.mStartTime = -1L;
            this.mTestResult.putString("stream", test.getClass().getName() + "#" + ((TestCase) test).getName() + "\nin " + assignmentSuite + " suite\nrunTime: " + String.valueOf(runTime) + "\n");
            this.mTestResult.putFloat(InstrumentationTestRunner.REPORT_KEY_RUN_TIME, runTime);
            this.mTestResult.putString(InstrumentationTestRunner.REPORT_KEY_SUITE_ASSIGNMENT, assignmentSuite);
            InstrumentationTestRunner.this.sendStatus(0, this.mTestResult);
        }
    }

    /* loaded from: classes.dex */
    private class WatcherResultPrinter implements TestListener {
        private final Bundle mResultTemplate;
        Bundle mTestResult;
        int mTestNum = 0;
        int mTestResultCode = 0;
        String mTestClass = null;

        public WatcherResultPrinter(int numTests) {
            Bundle bundle = new Bundle();
            this.mResultTemplate = bundle;
            bundle.putString("id", "InstrumentationTestRunner");
            bundle.putInt(InstrumentationTestRunner.REPORT_KEY_NUM_TOTAL, numTests);
        }

        public void startTest(Test test) {
            String testClass = test.getClass().getName();
            String testName = ((TestCase) test).getName();
            Bundle bundle = new Bundle(this.mResultTemplate);
            this.mTestResult = bundle;
            bundle.putString("class", testClass);
            this.mTestResult.putString(InstrumentationTestRunner.REPORT_KEY_NAME_TEST, testName);
            Bundle bundle2 = this.mTestResult;
            int i = this.mTestNum + 1;
            this.mTestNum = i;
            bundle2.putInt(InstrumentationTestRunner.REPORT_KEY_NUM_CURRENT, i);
            if (testClass == null || testClass.equals(this.mTestClass)) {
                this.mTestResult.putString("stream", "");
            } else {
                this.mTestResult.putString("stream", String.format("\n%s:", testClass));
                this.mTestClass = testClass;
            }
            try {
                Method testMethod = test.getClass().getMethod(testName, new Class[0]);
                if (testMethod.isAnnotationPresent(RepetitiveTest.class)) {
                    int numIterations = testMethod.getAnnotation(RepetitiveTest.class).numIterations();
                    this.mTestResult.putInt(InstrumentationTestRunner.REPORT_KEY_NUM_ITERATIONS, numIterations);
                }
            } catch (NoSuchMethodException e) {
            }
            try {
                if (this.mTestNum == 1) {
                    Thread.sleep(InstrumentationTestRunner.this.mDelayMsec);
                }
                InstrumentationTestRunner.this.sendStatus(1, this.mTestResult);
                this.mTestResultCode = 0;
            } catch (InterruptedException e2) {
                throw new IllegalStateException(e2);
            }
        }

        public void addError(Test test, Throwable t) {
            this.mTestResult.putString(InstrumentationTestRunner.REPORT_KEY_STACK, BaseTestRunner.getFilteredTrace(t));
            this.mTestResultCode = -1;
            this.mTestResult.putString("stream", String.format("\nError in %s:\n%s", ((TestCase) test).getName(), BaseTestRunner.getFilteredTrace(t)));
        }

        public void addFailure(Test test, junit.framework.AssertionFailedError t) {
            this.mTestResult.putString(InstrumentationTestRunner.REPORT_KEY_STACK, BaseTestRunner.getFilteredTrace((Throwable) t));
            this.mTestResultCode = -2;
            this.mTestResult.putString("stream", String.format("\nFailure in %s:\n%s", ((TestCase) test).getName(), BaseTestRunner.getFilteredTrace((Throwable) t)));
        }

        public void endTest(Test test) {
            if (this.mTestResultCode == 0) {
                this.mTestResult.putString("stream", ".");
            }
            InstrumentationTestRunner.this.sendStatus(this.mTestResultCode, this.mTestResult);
            try {
                Thread.sleep(InstrumentationTestRunner.this.mDelayMsec);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
