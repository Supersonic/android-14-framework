package com.android.internal.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes3.dex */
public @interface DataClass {

    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Each {
    }

    @Target({ElementType.METHOD})
    @Deprecated
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Generated {

        @Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.TYPE})
        @Deprecated
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface Member {
        }

        String codegenVersion();

        String inputSignatures() default "";

        String sourceFile();

        long time();
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MaySetToNull {
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ParcelWith {
        Class<? extends Parcelling> value();
    }

    /* loaded from: classes3.dex */
    public interface PerIntFieldAction<THIS> {
        void acceptInt(THIS r1, String str, int i);
    }

    /* loaded from: classes3.dex */
    public interface PerObjectFieldAction<THIS> {
        void acceptObject(THIS r1, String str, Object obj);
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PluralOf {
        String value();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Suppress {
        String[] value();
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SuppressConstDefsGeneration {
    }

    boolean genAidl() default false;

    boolean genBuilder() default false;

    boolean genConstDefs() default true;

    boolean genConstructor() default true;

    boolean genCopyConstructor() default false;

    boolean genEqualsHashCode() default false;

    boolean genForEachField() default false;

    boolean genGetters() default true;

    boolean genHiddenBuilder() default false;

    boolean genHiddenConstDefs() default false;

    boolean genHiddenConstructor() default false;

    boolean genHiddenCopyConstructor() default false;

    boolean genHiddenGetters() default false;

    boolean genHiddenSetters() default false;

    boolean genParcelable() default false;

    boolean genSetters() default false;

    boolean genToString() default false;
}
