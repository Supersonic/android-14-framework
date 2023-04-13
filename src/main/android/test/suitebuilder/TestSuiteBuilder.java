package android.test.suitebuilder;

import android.content.Context;
import android.test.AndroidTestRunner;
import android.test.TestCaseUtil;
import android.util.Log;
import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
@Deprecated
/* loaded from: classes.dex */
public class TestSuiteBuilder {
    private String currentClassname;
    private final Set<Predicate<TestMethod>> predicates;
    private TestSuite rootSuite;
    private TestSuite suiteForCurrentClass;
    private String suiteName;
    private List<TestCase> testCases;
    private final TestGrouping testGrouping;

    public TestSuiteBuilder(Class clazz) {
        this(clazz.getName(), clazz.getClassLoader());
    }

    public TestSuiteBuilder(String name, ClassLoader classLoader) {
        this.predicates = new HashSet();
        this.suiteName = name;
        this.testGrouping = new TestGrouping(TestGrouping.SORT_BY_FULLY_QUALIFIED_NAME, classLoader);
        this.testCases = new ArrayList();
        addRequirements(TestPredicates.REJECT_SUPPRESSED);
    }

    public TestSuiteBuilder addTestClassByName(String testClassName, String testMethodName, Context context) {
        AndroidTestRunner atr = new AndroidTestRunner();
        atr.setContext(context);
        atr.setTestClassName(testClassName, testMethodName);
        this.testCases.addAll(atr.getTestCases());
        return this;
    }

    public TestSuiteBuilder addTestSuite(TestSuite testSuite) {
        for (TestCase testCase : TestCaseUtil.getTests(testSuite, true)) {
            this.testCases.add(testCase);
        }
        return this;
    }

    public TestSuiteBuilder includePackages(String... packageNames) {
        this.testGrouping.addPackagesRecursive(packageNames);
        return this;
    }

    public TestSuiteBuilder excludePackages(String... packageNames) {
        this.testGrouping.removePackagesRecursive(packageNames);
        return this;
    }

    public TestSuiteBuilder addRequirements(List<Predicate<TestMethod>> predicates) {
        this.predicates.addAll(predicates);
        return this;
    }

    public final TestSuiteBuilder includeAllPackagesUnderHere() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callingClassName = null;
        String thisClassName = TestSuiteBuilder.class.getName();
        int i = 0;
        while (true) {
            if (i >= stackTraceElements.length) {
                break;
            }
            StackTraceElement element = stackTraceElements[i];
            if (!thisClassName.equals(element.getClassName()) || !"includeAllPackagesUnderHere".equals(element.getMethodName())) {
                i++;
            } else {
                callingClassName = stackTraceElements[i + 1].getClassName();
                break;
            }
        }
        String packageName = parsePackageNameFromClassName(callingClassName);
        return includePackages(packageName);
    }

    public TestSuiteBuilder named(String newSuiteName) {
        this.suiteName = newSuiteName;
        return this;
    }

    public final TestSuite build() {
        this.rootSuite = new TestSuite(getSuiteName());
        this.currentClassname = null;
        try {
            for (TestMethod test : this.testGrouping.getTests()) {
                if (satisfiesAllPredicates(test)) {
                    addTest(test);
                }
            }
            if (this.testCases.size() > 0) {
                for (TestCase testCase : this.testCases) {
                    if (satisfiesAllPredicates(new TestMethod(testCase))) {
                        addTest((Test) testCase);
                    }
                }
            }
            return this.rootSuite;
        } catch (Exception exception) {
            Log.i("TestSuiteBuilder", "Failed to create test.", exception);
            TestSuite suite = new TestSuite(getSuiteName());
            suite.addTest(new FailedToCreateTests(exception));
            return suite;
        }
    }

    protected String getSuiteName() {
        return this.suiteName;
    }

    public final TestSuiteBuilder addRequirements(Predicate<TestMethod>... predicates) {
        ArrayList<Predicate<TestMethod>> list = new ArrayList<>();
        Collections.addAll(list, predicates);
        return addRequirements(list);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class FailedToCreateTests extends TestCase {
        private final Exception exception;

        public FailedToCreateTests(Exception exception) {
            super("testSuiteConstructionFailed");
            this.exception = exception;
        }

        public void testSuiteConstructionFailed() {
            throw new RuntimeException("Exception during suite construction", this.exception);
        }
    }

    private boolean satisfiesAllPredicates(TestMethod test) {
        for (Predicate<TestMethod> predicate : this.predicates) {
            if (!predicate.apply(test)) {
                return false;
            }
        }
        return true;
    }

    private void addTest(TestMethod testMethod) throws Exception {
        addSuiteIfNecessary(testMethod.getEnclosingClassname());
        this.suiteForCurrentClass.addTest(testMethod.createTest());
    }

    private void addTest(Test test) {
        addSuiteIfNecessary(test.getClass().getName());
        this.suiteForCurrentClass.addTest(test);
    }

    private void addSuiteIfNecessary(String parentClassname) {
        if (!parentClassname.equals(this.currentClassname)) {
            this.currentClassname = parentClassname;
            TestSuite testSuite = new TestSuite(parentClassname);
            this.suiteForCurrentClass = testSuite;
            this.rootSuite.addTest(testSuite);
        }
    }

    private static String parsePackageNameFromClassName(String className) {
        return className.substring(0, className.lastIndexOf(46));
    }
}
