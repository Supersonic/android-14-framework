package com.android.internal.accessibility.dialog;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
/* loaded from: classes4.dex */
abstract class TargetAdapter extends BaseAdapter {

    /* loaded from: classes4.dex */
    static class ViewHolder {
        CheckBox mCheckBoxView;
        ImageView mIconView;
        TextView mLabelView;
        TextView mStatusView;
    }
}
