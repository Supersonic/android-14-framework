package android.view.inspector;
/* loaded from: classes4.dex */
public class StaticInspectionCompanionProvider implements InspectionCompanionProvider {
    private static final String COMPANION_SUFFIX = "$InspectionCompanion";

    @Override // android.view.inspector.InspectionCompanionProvider
    public <T> InspectionCompanion<T> provide(Class<T> cls) {
        String companionName = cls.getName() + COMPANION_SUFFIX;
        try {
            Class<?> loadClass = cls.getClassLoader().loadClass(companionName);
            if (!InspectionCompanion.class.isAssignableFrom(loadClass)) {
                return null;
            }
            return (InspectionCompanion) loadClass.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        } catch (InstantiationException e3) {
            Throwable cause = e3.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            if (cause instanceof Error) {
                throw ((Error) cause);
            }
            throw new RuntimeException(cause);
        }
    }
}
