package android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes.dex */
public @interface UserHandleAware {
    int enabledSinceTargetSdkVersion() default 0;

    String[] requiresAnyOfPermissionsIfNotCaller() default {};

    String[] requiresAnyOfPermissionsIfNotCallerProfileGroup() default {};

    String requiresPermissionIfNotCaller() default "";
}
