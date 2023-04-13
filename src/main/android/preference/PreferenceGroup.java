package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.p008os.Bundle;
import android.preference.GenericInflater;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Deprecated
/* loaded from: classes3.dex */
public abstract class PreferenceGroup extends Preference implements GenericInflater.Parent<Preference> {
    private boolean mAttachedToActivity;
    private int mCurrentPreferenceOrder;
    private boolean mOrderingAsAdded;
    private List<Preference> mPreferenceList;

    public PreferenceGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mOrderingAsAdded = true;
        this.mCurrentPreferenceOrder = 0;
        this.mAttachedToActivity = false;
        this.mPreferenceList = new ArrayList();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.PreferenceGroup, defStyleAttr, defStyleRes);
        this.mOrderingAsAdded = a.getBoolean(0, this.mOrderingAsAdded);
        a.recycle();
    }

    public PreferenceGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setOrderingAsAdded(boolean orderingAsAdded) {
        this.mOrderingAsAdded = orderingAsAdded;
    }

    public boolean isOrderingAsAdded() {
        return this.mOrderingAsAdded;
    }

    @Override // android.preference.GenericInflater.Parent
    public void addItemFromInflater(Preference preference) {
        addPreference(preference);
    }

    public int getPreferenceCount() {
        return this.mPreferenceList.size();
    }

    public Preference getPreference(int index) {
        return this.mPreferenceList.get(index);
    }

    public boolean addPreference(Preference preference) {
        if (this.mPreferenceList.contains(preference)) {
            return true;
        }
        if (preference.getOrder() == Integer.MAX_VALUE) {
            if (this.mOrderingAsAdded) {
                int i = this.mCurrentPreferenceOrder;
                this.mCurrentPreferenceOrder = i + 1;
                preference.setOrder(i);
            }
            if (preference instanceof PreferenceGroup) {
                ((PreferenceGroup) preference).setOrderingAsAdded(this.mOrderingAsAdded);
            }
        }
        if (!onPrepareAddPreference(preference)) {
            return false;
        }
        synchronized (this) {
            int insertionIndex = Collections.binarySearch(this.mPreferenceList, preference);
            if (insertionIndex < 0) {
                insertionIndex = (insertionIndex * (-1)) - 1;
            }
            this.mPreferenceList.add(insertionIndex, preference);
        }
        preference.onAttachedToHierarchy(getPreferenceManager());
        preference.assignParent(this);
        if (this.mAttachedToActivity) {
            preference.onAttachedToActivity();
        }
        notifyHierarchyChanged();
        return true;
    }

    public boolean removePreference(Preference preference) {
        boolean returnValue = removePreferenceInt(preference);
        notifyHierarchyChanged();
        return returnValue;
    }

    private boolean removePreferenceInt(Preference preference) {
        boolean remove;
        synchronized (this) {
            preference.onPrepareForRemoval();
            if (preference.getParent() == this) {
                preference.assignParent(null);
            }
            remove = this.mPreferenceList.remove(preference);
        }
        return remove;
    }

    public void removeAll() {
        synchronized (this) {
            List<Preference> preferenceList = this.mPreferenceList;
            for (int i = preferenceList.size() - 1; i >= 0; i--) {
                removePreferenceInt(preferenceList.get(0));
            }
        }
        notifyHierarchyChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean onPrepareAddPreference(Preference preference) {
        preference.onParentChanged(this, shouldDisableDependents());
        return true;
    }

    public Preference findPreference(CharSequence key) {
        Preference returnedPreference;
        if (TextUtils.equals(getKey(), key)) {
            return this;
        }
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreference(i);
            String curKey = preference.getKey();
            if (curKey != null && curKey.equals(key)) {
                return preference;
            }
            if ((preference instanceof PreferenceGroup) && (returnedPreference = ((PreferenceGroup) preference).findPreference(key)) != null) {
                return returnedPreference;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isOnSameScreenAsChildren() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        this.mAttachedToActivity = true;
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onAttachedToActivity();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        this.mAttachedToActivity = false;
    }

    @Override // android.preference.Preference
    public void notifyDependencyChange(boolean disableDependents) {
        super.notifyDependencyChange(disableDependents);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onParentChanged(this, disableDependents);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sortPreferences() {
        synchronized (this) {
            Collections.sort(this.mPreferenceList);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void dispatchSaveInstanceState(Bundle container) {
        super.dispatchSaveInstanceState(container);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).dispatchSaveInstanceState(container);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void dispatchRestoreInstanceState(Bundle container) {
        super.dispatchRestoreInstanceState(container);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).dispatchRestoreInstanceState(container);
        }
    }
}
