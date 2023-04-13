package android.service.autofill;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Pair;
import android.view.autofill.Helper;
import android.widget.RemoteViews;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class BatchUpdates implements Parcelable {
    public static final Parcelable.Creator<BatchUpdates> CREATOR = new Parcelable.Creator<BatchUpdates>() { // from class: android.service.autofill.BatchUpdates.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BatchUpdates createFromParcel(Parcel parcel) {
            Builder builder = new Builder();
            int[] ids = parcel.createIntArray();
            if (ids != null) {
                InternalTransformation[] values = (InternalTransformation[]) parcel.readParcelableArray(null, InternalTransformation.class);
                int size = ids.length;
                for (int i = 0; i < size; i++) {
                    builder.transformChild(ids[i], values[i]);
                }
            }
            RemoteViews updates = (RemoteViews) parcel.readParcelable(null, RemoteViews.class);
            if (updates != null) {
                builder.updateTemplate(updates);
            }
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BatchUpdates[] newArray(int size) {
            return new BatchUpdates[size];
        }
    };
    private final ArrayList<Pair<Integer, InternalTransformation>> mTransformations;
    private final RemoteViews mUpdates;

    private BatchUpdates(Builder builder) {
        this.mTransformations = builder.mTransformations;
        this.mUpdates = builder.mUpdates;
    }

    public ArrayList<Pair<Integer, InternalTransformation>> getTransformations() {
        return this.mTransformations;
    }

    public RemoteViews getUpdates() {
        return this.mUpdates;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private boolean mDestroyed;
        private ArrayList<Pair<Integer, InternalTransformation>> mTransformations;
        private RemoteViews mUpdates;

        public Builder updateTemplate(RemoteViews updates) {
            throwIfDestroyed();
            this.mUpdates = (RemoteViews) Objects.requireNonNull(updates);
            return this;
        }

        public Builder transformChild(int id, Transformation transformation) {
            throwIfDestroyed();
            Preconditions.checkArgument(transformation instanceof InternalTransformation, "not provided by Android System: %s", transformation);
            if (this.mTransformations == null) {
                this.mTransformations = new ArrayList<>();
            }
            this.mTransformations.add(new Pair<>(Integer.valueOf(id), (InternalTransformation) transformation));
            return this;
        }

        public BatchUpdates build() {
            throwIfDestroyed();
            Preconditions.checkState((this.mUpdates == null && this.mTransformations == null) ? false : true, "must call either updateTemplate() or transformChild() at least once");
            this.mDestroyed = true;
            return new BatchUpdates(this);
        }

        private void throwIfDestroyed() {
            if (this.mDestroyed) {
                throw new IllegalStateException("Already called #build()");
            }
        }
    }

    public String toString() {
        if (Helper.sDebug) {
            StringBuilder append = new StringBuilder("BatchUpdates: [").append(", transformations=");
            ArrayList<Pair<Integer, InternalTransformation>> arrayList = this.mTransformations;
            return append.append(arrayList == null ? "N/A" : Integer.valueOf(arrayList.size())).append(", updates=").append(this.mUpdates).append(NavigationBarInflaterView.SIZE_MOD_END).toString();
        }
        return super.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ArrayList<Pair<Integer, InternalTransformation>> arrayList = this.mTransformations;
        if (arrayList == null) {
            dest.writeIntArray(null);
        } else {
            int size = arrayList.size();
            int[] ids = new int[size];
            InternalTransformation[] values = new InternalTransformation[size];
            for (int i = 0; i < size; i++) {
                Pair<Integer, InternalTransformation> pair = this.mTransformations.get(i);
                ids[i] = pair.first.intValue();
                values[i] = pair.second;
            }
            dest.writeIntArray(ids);
            dest.writeParcelableArray(values, flags);
        }
        dest.writeParcelable(this.mUpdates, flags);
    }
}
