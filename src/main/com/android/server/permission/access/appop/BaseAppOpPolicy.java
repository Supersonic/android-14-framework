package com.android.server.permission.access.appop;

import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.server.permission.access.AccessState;
import com.android.server.permission.access.SchemePolicy;
/* compiled from: BaseAppOpPolicy.kt */
/* loaded from: classes2.dex */
public abstract class BaseAppOpPolicy extends SchemePolicy {
    public final BaseAppOpPersistence persistence;

    @Override // com.android.server.permission.access.SchemePolicy
    public String getObjectScheme() {
        return "app-op";
    }

    public BaseAppOpPolicy(BaseAppOpPersistence baseAppOpPersistence) {
        this.persistence = baseAppOpPersistence;
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public void parseUserState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
        this.persistence.parseUserState(binaryXmlPullParser, accessState, i);
    }

    @Override // com.android.server.permission.access.SchemePolicy
    public void serializeUserState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState, int i) {
        this.persistence.serializeUserState(binaryXmlSerializer, accessState, i);
    }
}
