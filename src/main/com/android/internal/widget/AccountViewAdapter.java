package com.android.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;
/* loaded from: classes5.dex */
public class AccountViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<AccountElements> mData;

    public AccountViewAdapter(Context context, List<AccountElements> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mData.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.mData.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<AccountElements> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountItemView view;
        if (convertView == null) {
            view = new AccountItemView(this.mContext);
        } else {
            view = (AccountItemView) convertView;
        }
        AccountElements elements = (AccountElements) getItem(position);
        view.setViewItem(elements);
        return view;
    }

    /* loaded from: classes5.dex */
    public static class AccountElements {
        private Drawable mDrawable;
        private int mIcon;
        private String mName;
        private String mNumber;

        public AccountElements(int icon, String name, String number) {
            this(icon, null, name, number);
        }

        public AccountElements(Drawable drawable, String name, String number) {
            this(0, drawable, name, number);
        }

        private AccountElements(int icon, Drawable drawable, String name, String number) {
            this.mIcon = icon;
            this.mDrawable = drawable;
            this.mName = name;
            this.mNumber = number;
        }

        public int getIcon() {
            return this.mIcon;
        }

        public String getName() {
            return this.mName;
        }

        public String getNumber() {
            return this.mNumber;
        }

        public Drawable getDrawable() {
            return this.mDrawable;
        }
    }
}
