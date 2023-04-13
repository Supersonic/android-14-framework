package android.provider;

import android.annotation.SystemApi;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
@SystemApi
/* loaded from: classes3.dex */
public class SearchIndexableResource extends SearchIndexableData {
    public int xmlResId;

    public SearchIndexableResource(int rank, int xmlResId, String className, int iconResId) {
        this.rank = rank;
        this.xmlResId = xmlResId;
        this.className = className;
        this.iconResId = iconResId;
    }

    public SearchIndexableResource(Context context) {
        super(context);
    }

    @Override // android.provider.SearchIndexableData
    public String toString() {
        return "SearchIndexableResource[" + super.toString() + ", xmlResId: " + this.xmlResId + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
