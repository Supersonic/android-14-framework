package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.cms.AttributeTable;
import java.util.Map;
/* loaded from: classes4.dex */
public class SimpleAttributeTableGenerator implements CMSAttributeTableGenerator {
    private final AttributeTable attributes;

    public SimpleAttributeTableGenerator(AttributeTable attributes) {
        this.attributes = attributes;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSAttributeTableGenerator
    public AttributeTable getAttributes(Map parameters) {
        return this.attributes;
    }
}
