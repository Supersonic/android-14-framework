package com.android.server.p011pm;

import android.content.pm.ResolveInfo;
import java.util.List;
/* renamed from: com.android.server.pm.QueryIntentActivitiesResult */
/* loaded from: classes2.dex */
public final class QueryIntentActivitiesResult {
    public boolean addInstant;
    public List<ResolveInfo> answer;
    public List<ResolveInfo> result;
    public boolean sortResult;

    public QueryIntentActivitiesResult(List<ResolveInfo> list) {
        this.sortResult = false;
        this.addInstant = false;
        this.result = null;
        this.answer = list;
    }

    public QueryIntentActivitiesResult(boolean z, boolean z2, List<ResolveInfo> list) {
        this.answer = null;
        this.sortResult = z;
        this.addInstant = z2;
        this.result = list;
    }
}
