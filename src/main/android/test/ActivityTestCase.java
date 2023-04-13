package android.test;

import android.app.Activity;
import android.util.Log;
import java.lang.reflect.Field;
@Deprecated
/* loaded from: classes.dex */
public abstract class ActivityTestCase extends InstrumentationTestCase {
    private Activity mActivity;

    /* JADX INFO: Access modifiers changed from: protected */
    public Activity getActivity() {
        return this.mActivity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setActivity(Activity testActivity) {
        this.mActivity = testActivity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void scrubClass(Class<?> testCaseClass) throws IllegalAccessException {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldClass = field.getDeclaringClass();
            if (testCaseClass.isAssignableFrom(fieldClass) && !field.getType().isPrimitive() && (field.getModifiers() & 16) == 0) {
                try {
                    field.setAccessible(true);
                    field.set(this, null);
                } catch (Exception e) {
                    Log.d("TestCase", "Error: Could not nullify field!");
                }
                if (field.get(this) != null) {
                    Log.d("TestCase", "Error: Could not nullify field!");
                }
            }
        }
    }
}
