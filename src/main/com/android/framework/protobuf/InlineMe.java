package com.android.framework.protobuf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
/* loaded from: classes4.dex */
@interface InlineMe {
    String[] imports() default {};

    String replacement();

    String[] staticImports() default {};
}
