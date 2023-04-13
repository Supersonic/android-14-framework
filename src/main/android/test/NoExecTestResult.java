package android.test;

import junit.framework.TestCase;
import junit.framework.TestResult;
@Deprecated
/* loaded from: classes.dex */
class NoExecTestResult extends TestResult {
    protected void run(TestCase test) {
        startTest(test);
        endTest(test);
    }
}
