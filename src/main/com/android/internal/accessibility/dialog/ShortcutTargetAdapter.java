package com.android.internal.accessibility.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.accessibility.dialog.TargetAdapter;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ShortcutTargetAdapter extends TargetAdapter {
    private int mShortcutMenuMode = 0;
    private final List<AccessibilityTarget> mTargets;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ShortcutTargetAdapter(List<AccessibilityTarget> targets) {
        this.mTargets = targets;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mTargets.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.mTargets.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TargetAdapter.ViewHolder holder;
        Context context = parent.getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(C4057R.layout.accessibility_shortcut_chooser_item, parent, false);
            holder = new TargetAdapter.ViewHolder();
            holder.mCheckBoxView = (CheckBox) convertView.findViewById(C4057R.C4059id.accessibility_shortcut_target_checkbox);
            holder.mIconView = (ImageView) convertView.findViewById(C4057R.C4059id.accessibility_shortcut_target_icon);
            holder.mLabelView = (TextView) convertView.findViewById(C4057R.C4059id.accessibility_shortcut_target_label);
            holder.mStatusView = (TextView) convertView.findViewById(C4057R.C4059id.accessibility_shortcut_target_status);
            convertView.setTag(holder);
        } else {
            holder = (TargetAdapter.ViewHolder) convertView.getTag();
        }
        AccessibilityTarget target = this.mTargets.get(position);
        target.updateActionItem(holder, this.mShortcutMenuMode);
        return convertView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShortcutMenuMode(int shortcutMenuMode) {
        this.mShortcutMenuMode = shortcutMenuMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getShortcutMenuMode() {
        return this.mShortcutMenuMode;
    }
}
