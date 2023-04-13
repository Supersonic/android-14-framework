package android.widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
/* loaded from: classes4.dex */
public abstract class SimpleCursorTreeAdapter extends ResourceCursorTreeAdapter {
    private int[] mChildFrom;
    private String[] mChildFromNames;
    private int[] mChildTo;
    private int[] mGroupFrom;
    private String[] mGroupFromNames;
    private int[] mGroupTo;
    private ViewBinder mViewBinder;

    /* loaded from: classes4.dex */
    public interface ViewBinder {
        boolean setViewValue(View view, Cursor cursor, int i);
    }

    public SimpleCursorTreeAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, childLayout, lastChildLayout);
        init(groupFrom, groupTo, childFrom, childTo);
    }

    public SimpleCursorTreeAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, childLayout);
        init(groupFrom, groupTo, childFrom, childTo);
    }

    public SimpleCursorTreeAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, childLayout);
        init(groupFrom, groupTo, childFrom, childTo);
    }

    private void init(String[] groupFromNames, int[] groupTo, String[] childFromNames, int[] childTo) {
        this.mGroupFromNames = groupFromNames;
        this.mGroupTo = groupTo;
        this.mChildFromNames = childFromNames;
        this.mChildTo = childTo;
    }

    public ViewBinder getViewBinder() {
        return this.mViewBinder;
    }

    public void setViewBinder(ViewBinder viewBinder) {
        this.mViewBinder = viewBinder;
    }

    private void bindView(View view, Context context, Cursor cursor, int[] from, int[] to) {
        ViewBinder binder = this.mViewBinder;
        for (int i = 0; i < to.length; i++) {
            View v = view.findViewById(to[i]);
            if (v != null) {
                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, cursor, from[i]);
                }
                if (bound) {
                    continue;
                } else {
                    String text = cursor.getString(from[i]);
                    if (text == null) {
                        text = "";
                    }
                    if (v instanceof TextView) {
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        setViewImage((ImageView) v, text);
                    } else {
                        throw new IllegalStateException("SimpleCursorTreeAdapter can bind values only to TextView and ImageView!");
                    }
                }
            }
        }
    }

    private void initFromColumns(Cursor cursor, String[] fromColumnNames, int[] fromColumns) {
        for (int i = fromColumnNames.length - 1; i >= 0; i--) {
            fromColumns[i] = cursor.getColumnIndexOrThrow(fromColumnNames[i]);
        }
    }

    @Override // android.widget.CursorTreeAdapter
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        if (this.mChildFrom == null) {
            String[] strArr = this.mChildFromNames;
            int[] iArr = new int[strArr.length];
            this.mChildFrom = iArr;
            initFromColumns(cursor, strArr, iArr);
        }
        bindView(view, context, cursor, this.mChildFrom, this.mChildTo);
    }

    @Override // android.widget.CursorTreeAdapter
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        if (this.mGroupFrom == null) {
            String[] strArr = this.mGroupFromNames;
            int[] iArr = new int[strArr.length];
            this.mGroupFrom = iArr;
            initFromColumns(cursor, strArr, iArr);
        }
        bindView(view, context, cursor, this.mGroupFrom, this.mGroupTo);
    }

    protected void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            v.setImageURI(Uri.parse(value));
        }
    }

    public void setViewText(TextView v, String text) {
        v.setText(text);
    }
}
