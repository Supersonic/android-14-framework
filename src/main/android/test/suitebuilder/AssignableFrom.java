package android.test.suitebuilder;

import com.android.internal.util.Predicate;
/* loaded from: classes.dex */
class AssignableFrom implements Predicate<TestMethod> {
    private final Class<?> root;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AssignableFrom(Class<?> root) {
        this.root = root;
    }

    public boolean apply(TestMethod testMethod) {
        return this.root.isAssignableFrom(testMethod.getEnclosingClass());
    }
}
