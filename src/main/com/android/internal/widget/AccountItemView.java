package com.android.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.widget.AccountViewAdapter;
/* loaded from: classes5.dex */
public class AccountItemView extends LinearLayout {
    private ImageView mAccountIcon;
    private TextView mAccountName;
    private TextView mAccountNumber;

    public AccountItemView(Context context) {
        this(context, null);
    }

    public AccountItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(C4057R.layout.simple_account_item, (ViewGroup) null);
        addView(view);
        initViewItem(view);
    }

    private void initViewItem(View view) {
        this.mAccountIcon = (ImageView) view.findViewById(16908294);
        this.mAccountName = (TextView) view.findViewById(16908310);
        this.mAccountNumber = (TextView) view.findViewById(16908304);
    }

    public void setViewItem(AccountViewAdapter.AccountElements element) {
        Drawable drawable = element.getDrawable();
        if (drawable != null) {
            setAccountIcon(drawable);
        } else {
            setAccountIcon(element.getIcon());
        }
        setAccountName(element.getName());
        setAccountNumber(element.getNumber());
    }

    public void setAccountIcon(int resId) {
        this.mAccountIcon.setImageResource(resId);
    }

    public void setAccountIcon(Drawable drawable) {
        this.mAccountIcon.setBackgroundDrawable(drawable);
    }

    public void setAccountName(String name) {
        setText(this.mAccountName, name);
    }

    public void setAccountNumber(String number) {
        setText(this.mAccountNumber, number);
    }

    private void setText(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(8);
            return;
        }
        view.setText(text);
        view.setVisibility(0);
    }
}
