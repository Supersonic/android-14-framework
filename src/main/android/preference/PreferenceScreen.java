package android.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes3.dex */
public final class PreferenceScreen extends PreferenceGroup implements AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {
    private Dialog mDialog;
    private Drawable mDividerDrawable;
    private boolean mDividerSpecified;
    private int mLayoutResId;
    private ListView mListView;
    private ListAdapter mRootAdapter;

    public PreferenceScreen(Context context, AttributeSet attrs) {
        super(context, attrs, 16842891);
        this.mLayoutResId = C4057R.layout.preference_list_fragment;
        TypedArray a = context.obtainStyledAttributes(null, C4057R.styleable.PreferenceScreen, 16842891, 0);
        this.mLayoutResId = a.getResourceId(1, this.mLayoutResId);
        if (a.hasValueOrEmpty(0)) {
            this.mDividerDrawable = a.getDrawable(0);
            this.mDividerSpecified = true;
        }
        a.recycle();
    }

    public ListAdapter getRootAdapter() {
        if (this.mRootAdapter == null) {
            this.mRootAdapter = onCreateRootAdapter();
        }
        return this.mRootAdapter;
    }

    protected ListAdapter onCreateRootAdapter() {
        return new PreferenceGroupAdapter(this);
    }

    public void bind(ListView listView) {
        listView.setOnItemClickListener(this);
        listView.setAdapter(getRootAdapter());
        onAttachedToActivity();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onClick() {
        if (getIntent() != null || getFragment() != null || getPreferenceCount() == 0) {
            return;
        }
        showDialog(null);
    }

    private void showDialog(Bundle state) {
        Context context = getContext();
        ListView listView = this.mListView;
        if (listView != null) {
            listView.setAdapter((ListAdapter) null);
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childPrefScreen = inflater.inflate(this.mLayoutResId, (ViewGroup) null);
        View titleView = childPrefScreen.findViewById(16908310);
        ListView listView2 = (ListView) childPrefScreen.findViewById(16908298);
        this.mListView = listView2;
        if (this.mDividerSpecified) {
            listView2.setDivider(this.mDividerDrawable);
        }
        bind(this.mListView);
        CharSequence title = getTitle();
        Dialog dialog = new Dialog(context, context.getThemeResId());
        this.mDialog = dialog;
        if (TextUtils.isEmpty(title)) {
            if (titleView != null) {
                titleView.setVisibility(8);
            }
            dialog.getWindow().requestFeature(1);
        } else if (titleView instanceof TextView) {
            ((TextView) titleView).setText(title);
            titleView.setVisibility(0);
        } else {
            dialog.setTitle(title);
        }
        dialog.setContentView(childPrefScreen);
        dialog.setOnDismissListener(this);
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        getPreferenceManager().addPreferencesScreen(dialog);
        dialog.show();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        this.mDialog = null;
        getPreferenceManager().removePreferencesScreen(dialog);
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        if (parent instanceof ListView) {
            position -= ((ListView) parent).getHeaderViewsCount();
        }
        Object item = getRootAdapter().getItem(position);
        if (item instanceof Preference) {
            Preference preference = (Preference) item;
            preference.performClick(this);
        }
    }

    @Override // android.preference.PreferenceGroup
    protected boolean isOnSameScreenAsChildren() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.preference.PreferenceScreen.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle dialogBundle;
        boolean isDialogShowing;

        public SavedState(Parcel source) {
            super(source);
            this.isDialogShowing = source.readInt() == 1;
            this.dialogBundle = source.readBundle();
        }

        @Override // android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.isDialogShowing ? 1 : 0);
            dest.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }
}
