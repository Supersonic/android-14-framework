package com.google.protobuf;

import java.lang.reflect.Field;
@CheckReturnValue
/* loaded from: classes.dex */
final class OneofInfo {
    private final Field caseField;

    /* renamed from: id */
    private final int f1id;
    private final Field valueField;

    public OneofInfo(int id, Field caseField, Field valueField) {
        this.f1id = id;
        this.caseField = caseField;
        this.valueField = valueField;
    }

    public int getId() {
        return this.f1id;
    }

    public Field getCaseField() {
        return this.caseField;
    }

    public Field getValueField() {
        return this.valueField;
    }
}
