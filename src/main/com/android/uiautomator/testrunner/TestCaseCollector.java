package com.android.uiautomator.testrunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
/* loaded from: classes.dex */
public class TestCaseCollector {
    private ClassLoader mClassLoader;
    private TestCaseFilter mFilter;
    private List<TestCase> mTestCases = new ArrayList();

    /* loaded from: classes.dex */
    public interface TestCaseFilter {
        boolean accept(Class<?> cls);

        boolean accept(Method method);
    }

    public TestCaseCollector(ClassLoader classLoader, TestCaseFilter filter) {
        this.mClassLoader = classLoader;
        this.mFilter = filter;
    }

    public void addTestClasses(List<String> classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            addTestClass(className);
        }
    }

    public void addTestClass(String className) throws ClassNotFoundException {
        int hashPos = className.indexOf(35);
        String methodName = null;
        if (hashPos != -1) {
            methodName = className.substring(hashPos + 1);
            className = className.substring(0, hashPos);
        }
        addTestClass(className, methodName);
    }

    public void addTestClass(String className, String methodName) throws ClassNotFoundException {
        Class<?> clazz = this.mClassLoader.loadClass(className);
        if (methodName != null) {
            addSingleTestMethod(clazz, methodName);
            return;
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (this.mFilter.accept(method)) {
                addSingleTestMethod(clazz, method.getName());
            }
        }
    }

    public List<TestCase> getTestCases() {
        return Collections.unmodifiableList(this.mTestCases);
    }

    protected void addSingleTestMethod(Class<?> clazz, String method) {
        if (!this.mFilter.accept(clazz)) {
            throw new RuntimeException("Test class must be derived from UiAutomatorTestCase");
        }
        try {
            TestCase testCase = (TestCase) clazz.newInstance();
            testCase.setName(method);
            this.mTestCases.add(testCase);
        } catch (IllegalAccessException e) {
            this.mTestCases.add(error(clazz, "IllegalAccessException: could not instantiate test class. Class: " + clazz.getName()));
        } catch (InstantiationException e2) {
            this.mTestCases.add(error(clazz, "InstantiationException: could not instantiate test class. Class: " + clazz.getName()));
        }
    }

    private UiAutomatorTestCase error(Class<?> clazz, final String message) {
        UiAutomatorTestCase warning = new UiAutomatorTestCase() { // from class: com.android.uiautomator.testrunner.TestCaseCollector.1
            @Override // junit.framework.TestCase
            protected void runTest() {
                fail(message);
            }
        };
        warning.setName(clazz.getName());
        return warning;
    }
}
