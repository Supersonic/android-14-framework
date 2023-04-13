package android.service.autofill;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telecom.Logging.Session;
import android.util.Log;
import android.util.Pair;
import android.view.autofill.AutofillId;
import android.view.autofill.Helper;
import android.widget.RemoteViews;
import com.android.internal.util.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class CharSequenceTransformation extends InternalTransformation implements Transformation, Parcelable {
    public static final Parcelable.Creator<CharSequenceTransformation> CREATOR = new Parcelable.Creator<CharSequenceTransformation>() { // from class: android.service.autofill.CharSequenceTransformation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CharSequenceTransformation createFromParcel(Parcel parcel) {
            AutofillId[] ids = (AutofillId[]) parcel.readParcelableArray(null, AutofillId.class);
            Pattern[] regexs = (Pattern[]) parcel.readSerializable();
            String[] substs = parcel.createStringArray();
            Builder builder = new Builder(ids[0], regexs[0], substs[0]);
            int size = ids.length;
            for (int i = 1; i < size; i++) {
                builder.addField(ids[i], regexs[i], substs[i]);
            }
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CharSequenceTransformation[] newArray(int size) {
            return new CharSequenceTransformation[size];
        }
    };
    private static final String TAG = "CharSequenceTransformation";
    private final LinkedHashMap<AutofillId, Pair<Pattern, String>> mFields;

    private CharSequenceTransformation(Builder builder) {
        this.mFields = builder.mFields;
    }

    @Override // android.service.autofill.InternalTransformation
    public void apply(ValueFinder finder, RemoteViews parentTemplate, int childViewId) throws Exception {
        StringBuilder converted = new StringBuilder();
        int size = this.mFields.size();
        if (Helper.sDebug) {
            Log.m112d(TAG, size + " fields on id " + childViewId);
        }
        for (Map.Entry<AutofillId, Pair<Pattern, String>> entry : this.mFields.entrySet()) {
            AutofillId id = entry.getKey();
            Pair<Pattern, String> field = entry.getValue();
            String value = finder.findByAutofillId(id);
            if (value == null) {
                Log.m104w(TAG, "No value for id " + id);
                return;
            }
            try {
                Matcher matcher = field.first.matcher(value);
                if (!matcher.find()) {
                    if (Helper.sDebug) {
                        Log.m112d(TAG, "Match for " + field.first + " failed on id " + id);
                        return;
                    }
                    return;
                }
                String convertedValue = matcher.replaceAll(field.second);
                converted.append(convertedValue);
            } catch (Exception e) {
                Log.m104w(TAG, "Cannot apply " + field.first.pattern() + Session.SUBSESSION_SEPARATION_CHAR + field.second + " to field with autofill id" + id + ": " + e.getClass());
                throw e;
            }
        }
        Log.m112d(TAG, "Converting text on child " + childViewId + " to " + converted.length() + "_chars");
        parentTemplate.setCharSequence(childViewId, "setText", converted);
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private boolean mDestroyed;
        private final LinkedHashMap<AutofillId, Pair<Pattern, String>> mFields = new LinkedHashMap<>();

        public Builder(AutofillId id, Pattern regex, String subst) {
            addField(id, regex, subst);
        }

        public Builder addField(AutofillId id, Pattern regex, String subst) {
            throwIfDestroyed();
            Objects.requireNonNull(id);
            Objects.requireNonNull(regex);
            Objects.requireNonNull(subst);
            this.mFields.put(id, new Pair<>(regex, subst));
            return this;
        }

        public CharSequenceTransformation build() {
            throwIfDestroyed();
            this.mDestroyed = true;
            return new CharSequenceTransformation(this);
        }

        private void throwIfDestroyed() {
            Preconditions.checkState(!this.mDestroyed, "Already called build()");
        }
    }

    public String toString() {
        return !Helper.sDebug ? super.toString() : "MultipleViewsCharSequenceTransformation: [fields=" + this.mFields + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.util.regex.Pattern[], java.io.Serializable] */
    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        int size = this.mFields.size();
        AutofillId[] ids = new AutofillId[size];
        ?? r2 = new Pattern[size];
        String[] substs = new String[size];
        int i = 0;
        for (Map.Entry<AutofillId, Pair<Pattern, String>> entry : this.mFields.entrySet()) {
            ids[i] = entry.getKey();
            Pair<Pattern, String> pair = entry.getValue();
            r2[i] = pair.first;
            substs[i] = pair.second;
            i++;
        }
        parcel.writeParcelableArray(ids, flags);
        parcel.writeSerializable(r2);
        parcel.writeStringArray(substs);
    }
}
