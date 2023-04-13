package android.test.suitebuilder;
/* loaded from: classes.dex */
public class SmokeTestSuiteBuilder extends TestSuiteBuilder {
    public SmokeTestSuiteBuilder(Class clazz) {
        this(clazz.getName(), clazz.getClassLoader());
    }

    public SmokeTestSuiteBuilder(String name, ClassLoader classLoader) {
        super(name, classLoader);
        addRequirements(TestPredicates.SELECT_SMOKE);
    }
}
