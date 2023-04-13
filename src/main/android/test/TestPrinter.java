package android.test;

import android.util.Log;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestListener;
@Deprecated
/* loaded from: classes.dex */
class TestPrinter implements TestListener {
    private Set<String> mFailedTests = new HashSet();
    private boolean mOnlyFailures;
    private String mTag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TestPrinter(String tag, boolean onlyFailures) {
        this.mTag = tag;
        this.mOnlyFailures = onlyFailures;
    }

    private void started(String className) {
        if (!this.mOnlyFailures) {
            Log.i(this.mTag, "started: " + className);
        }
    }

    private void finished(String className) {
        if (!this.mOnlyFailures) {
            Log.i(this.mTag, "finished: " + className);
        }
    }

    private void passed(String className) {
        if (!this.mOnlyFailures) {
            Log.i(this.mTag, "passed: " + className);
        }
    }

    private void failed(String className, Throwable exception) {
        Log.i(this.mTag, "failed: " + className);
        Log.i(this.mTag, "----- begin exception -----");
        Log.i(this.mTag, "", exception);
        Log.i(this.mTag, "----- end exception -----");
    }

    private void failed(Test test, Throwable t) {
        this.mFailedTests.add(test.toString());
        failed(test.toString(), t);
    }

    public void addError(Test test, Throwable t) {
        failed(test, t);
    }

    public void addFailure(Test test, junit.framework.AssertionFailedError t) {
        failed(test, (Throwable) t);
    }

    public void endTest(Test test) {
        finished(test.toString());
        if (!this.mFailedTests.contains(test.toString())) {
            passed(test.toString());
        }
        this.mFailedTests.remove(test.toString());
    }

    public void startTest(Test test) {
        started(test.toString());
    }
}
