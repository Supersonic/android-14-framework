package android.test;

import android.app.Activity;
@Deprecated
/* loaded from: classes.dex */
public abstract class ActivityInstrumentationTestCase<T extends Activity> extends ActivityTestCase {
    Class<T> mActivityClass;
    boolean mInitialTouchMode;
    String mPackage;

    public ActivityInstrumentationTestCase(String pkg, Class<T> activityClass) {
        this(pkg, activityClass, false);
    }

    public ActivityInstrumentationTestCase(String pkg, Class<T> activityClass, boolean initialTouchMode) {
        this.mInitialTouchMode = false;
        this.mActivityClass = activityClass;
        this.mInitialTouchMode = initialTouchMode;
    }

    @Override // android.test.ActivityTestCase
    public T getActivity() {
        return (T) super.getActivity();
    }

    protected void setUp() throws Exception {
        super.setUp();
        getInstrumentation().setInTouchMode(this.mInitialTouchMode);
        String targetPackageName = getInstrumentation().getTargetContext().getPackageName();
        setActivity(launchActivity(targetPackageName, this.mActivityClass, null));
    }

    protected void tearDown() throws Exception {
        getActivity().finish();
        setActivity(null);
        scrubClass(ActivityInstrumentationTestCase.class);
        super.tearDown();
    }

    public void testActivityTestCaseSetUpProperly() throws Exception {
        assertNotNull("activity should be launched successfully", getActivity());
    }
}
