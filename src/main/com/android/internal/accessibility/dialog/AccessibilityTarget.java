package com.android.internal.accessibility.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import com.android.internal.accessibility.dialog.TargetAdapter;
import com.android.internal.accessibility.util.ShortcutUtils;
/* loaded from: classes4.dex */
public abstract class AccessibilityTarget implements TargetOperations, OnTargetSelectedListener, OnTargetCheckedChangeListener {
    private Context mContext;
    private int mFragmentType;
    private Drawable mIcon;
    private String mId;
    private String mKey;
    private CharSequence mLabel;
    private boolean mShortcutEnabled;
    private int mShortcutType;
    private CharSequence mStateDescription;

    public AccessibilityTarget(Context context, int shortcutType, int fragmentType, boolean isShortcutSwitched, String id, CharSequence label, Drawable icon, String key) {
        this.mContext = context;
        this.mShortcutType = shortcutType;
        this.mFragmentType = fragmentType;
        this.mShortcutEnabled = isShortcutSwitched;
        this.mId = id;
        this.mLabel = label;
        this.mIcon = icon;
        this.mKey = key;
    }

    @Override // com.android.internal.accessibility.dialog.TargetOperations
    public void updateActionItem(TargetAdapter.ViewHolder holder, int shortcutMenuMode) {
        boolean z = true;
        boolean isEditMenuMode = shortcutMenuMode == 1;
        CheckBox checkBox = holder.mCheckBoxView;
        if (!isEditMenuMode || !isShortcutEnabled()) {
            z = false;
        }
        checkBox.setChecked(z);
        holder.mCheckBoxView.setVisibility(isEditMenuMode ? 0 : 8);
        holder.mIconView.setImageDrawable(getIcon());
        holder.mLabelView.setText(getLabel());
        holder.mStatusView.setVisibility(8);
    }

    @Override // com.android.internal.accessibility.dialog.OnTargetSelectedListener
    public void onSelected() {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(AccessibilityManager.class);
        switch (getShortcutType()) {
            case 0:
                am.notifyAccessibilityButtonClicked(getContext().getDisplayId(), getId());
                return;
            case 1:
                am.performAccessibilityShortcut(getId());
                return;
            default:
                throw new IllegalStateException("Unexpected shortcut type");
        }
    }

    @Override // com.android.internal.accessibility.dialog.OnTargetCheckedChangeListener
    public void onCheckedChanged(boolean isChecked) {
        setShortcutEnabled(isChecked);
        if (isChecked) {
            ShortcutUtils.optInValueToSettings(getContext(), ShortcutUtils.convertToUserType(getShortcutType()), getId());
        } else {
            ShortcutUtils.optOutValueFromSettings(getContext(), ShortcutUtils.convertToUserType(getShortcutType()), getId());
        }
    }

    public void setStateDescription(CharSequence stateDescription) {
        this.mStateDescription = stateDescription;
    }

    public CharSequence getStateDescription() {
        return this.mStateDescription;
    }

    public void setShortcutEnabled(boolean enabled) {
        this.mShortcutEnabled = enabled;
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getShortcutType() {
        return this.mShortcutType;
    }

    public int getFragmentType() {
        return this.mFragmentType;
    }

    public boolean isShortcutEnabled() {
        return this.mShortcutEnabled;
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public String getKey() {
        return this.mKey;
    }
}
