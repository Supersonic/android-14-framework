package android.test.suitebuilder;
@Deprecated
/* loaded from: classes.dex */
public class UnitTestSuiteBuilder extends TestSuiteBuilder {
    public UnitTestSuiteBuilder(Class clazz) {
        this(clazz.getName(), clazz.getClassLoader());
    }

    public UnitTestSuiteBuilder(String name, ClassLoader classLoader) {
        super(name, classLoader);
        addRequirements(TestPredicates.REJECT_INSTRUMENTATION);
    }
}
