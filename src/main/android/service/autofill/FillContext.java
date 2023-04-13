package android.service.autofill;

import android.annotation.NonNull;
import android.app.assist.AssistStructure;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.SparseIntArray;
import android.view.autofill.AutofillId;
import android.view.autofill.Helper;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayDeque;
/* loaded from: classes3.dex */
public final class FillContext implements Parcelable {
    public static final Parcelable.Creator<FillContext> CREATOR = new Parcelable.Creator<FillContext>() { // from class: android.service.autofill.FillContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FillContext[] newArray(int size) {
            return new FillContext[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FillContext createFromParcel(Parcel in) {
            int requestId = in.readInt();
            AssistStructure structure = (AssistStructure) in.readTypedObject(AssistStructure.CREATOR);
            AutofillId focusedId = (AutofillId) in.readTypedObject(AutofillId.CREATOR);
            return new FillContext(requestId, structure, focusedId);
        }
    };
    private final AutofillId mFocusedId;
    private final int mRequestId;
    private final AssistStructure mStructure;
    private transient ArrayMap<AutofillId, AssistStructure.ViewNode> mViewNodeLookupTable;

    public String toString() {
        return !Helper.sDebug ? super.toString() : "FillContext [reqId=" + this.mRequestId + ", focusedId=" + this.mFocusedId + NavigationBarInflaterView.SIZE_MOD_END;
    }

    /* JADX WARN: Incorrect condition in loop: B:18:0x0051 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public AssistStructure.ViewNode[] findViewNodesByAutofillIds(AutofillId[] ids) {
        ArrayDeque<AssistStructure.ViewNode> nodesToProcess = new ArrayDeque<>();
        AssistStructure.ViewNode[] foundNodes = new AssistStructure.ViewNode[ids.length];
        SparseIntArray missingNodeIndexes = new SparseIntArray(ids.length);
        for (int i = 0; i < ids.length; i++) {
            ArrayMap<AutofillId, AssistStructure.ViewNode> arrayMap = this.mViewNodeLookupTable;
            if (arrayMap != null) {
                int lookupTableIndex = arrayMap.indexOfKey(ids[i]);
                if (lookupTableIndex >= 0) {
                    foundNodes[i] = this.mViewNodeLookupTable.valueAt(lookupTableIndex);
                } else {
                    missingNodeIndexes.put(i, 0);
                }
            } else {
                missingNodeIndexes.put(i, 0);
            }
        }
        int numWindowNodes = this.mStructure.getWindowNodeCount();
        for (int i2 = 0; i2 < numWindowNodes; i2++) {
            nodesToProcess.add(this.mStructure.getWindowNodeAt(i2).getRootViewNode());
        }
        while (i > 0 && !nodesToProcess.isEmpty()) {
            AssistStructure.ViewNode node = nodesToProcess.removeFirst();
            int i3 = 0;
            while (true) {
                if (i3 >= missingNodeIndexes.size()) {
                    break;
                }
                int index = missingNodeIndexes.keyAt(i3);
                AutofillId id = ids[index];
                if (!id.equals(node.getAutofillId())) {
                    i3++;
                } else {
                    foundNodes[index] = node;
                    if (this.mViewNodeLookupTable == null) {
                        this.mViewNodeLookupTable = new ArrayMap<>(ids.length);
                    }
                    this.mViewNodeLookupTable.put(id, node);
                    missingNodeIndexes.removeAt(i3);
                }
            }
            for (int i4 = 0; i4 < node.getChildCount(); i4++) {
                nodesToProcess.addLast(node.getChildAt(i4));
            }
        }
        for (int i5 = 0; i5 < missingNodeIndexes.size(); i5++) {
            if (this.mViewNodeLookupTable == null) {
                this.mViewNodeLookupTable = new ArrayMap<>(missingNodeIndexes.size());
            }
            this.mViewNodeLookupTable.put(ids[missingNodeIndexes.keyAt(i5)], null);
        }
        return foundNodes;
    }

    public FillContext(int requestId, AssistStructure structure, AutofillId focusedId) {
        this.mRequestId = requestId;
        this.mStructure = structure;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) structure);
        this.mFocusedId = focusedId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) focusedId);
    }

    public int getRequestId() {
        return this.mRequestId;
    }

    public AssistStructure getStructure() {
        return this.mStructure;
    }

    public AutofillId getFocusedId() {
        return this.mFocusedId;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRequestId);
        dest.writeTypedObject(this.mStructure, flags);
        dest.writeTypedObject(this.mFocusedId, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Deprecated
    private void __metadata() {
    }
}
