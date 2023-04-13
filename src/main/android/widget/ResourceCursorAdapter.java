package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes4.dex */
public abstract class ResourceCursorAdapter extends CursorAdapter {
    private LayoutInflater mDropDownInflater;
    private int mDropDownLayout;
    private LayoutInflater mInflater;
    private int mLayout;

    @Deprecated
    public ResourceCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.mDropDownLayout = layout;
        this.mLayout = layout;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mInflater = layoutInflater;
        this.mDropDownInflater = layoutInflater;
    }

    public ResourceCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.mDropDownLayout = layout;
        this.mLayout = layout;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mInflater = layoutInflater;
        this.mDropDownInflater = layoutInflater;
    }

    public ResourceCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, c, flags);
        this.mDropDownLayout = layout;
        this.mLayout = layout;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mInflater = layoutInflater;
        this.mDropDownInflater = layoutInflater;
    }

    @Override // android.widget.CursorAdapter, android.widget.ThemedSpinnerAdapter
    public void setDropDownViewTheme(Resources.Theme theme) {
        super.setDropDownViewTheme(theme);
        if (theme == null) {
            this.mDropDownInflater = null;
        } else if (theme == this.mInflater.getContext().getTheme()) {
            this.mDropDownInflater = this.mInflater;
        } else {
            Context context = new ContextThemeWrapper(this.mContext, theme);
            this.mDropDownInflater = LayoutInflater.from(context);
        }
    }

    @Override // android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.mInflater.inflate(this.mLayout, parent, false);
    }

    @Override // android.widget.CursorAdapter
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        return this.mDropDownInflater.inflate(this.mDropDownLayout, parent, false);
    }

    public void setViewResource(int layout) {
        this.mLayout = layout;
    }

    public void setDropDownViewResource(int dropDownLayout) {
        this.mDropDownLayout = dropDownLayout;
    }
}
