package android.view;

import android.util.SparseArray;
import java.util.function.IntFunction;
/* compiled from: D8$$SyntheticClass */
/* loaded from: classes4.dex */
public final /* synthetic */ class View$InspectionCompanion$$ExternalSyntheticLambda0 implements IntFunction {
    public final /* synthetic */ SparseArray f$0;

    public /* synthetic */ View$InspectionCompanion$$ExternalSyntheticLambda0(SparseArray sparseArray) {
        this.f$0 = sparseArray;
    }

    @Override // java.util.function.IntFunction
    public final Object apply(int i) {
        return (String) this.f$0.get(i);
    }
}
