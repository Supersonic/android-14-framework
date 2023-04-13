package android.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.p008os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorFilter;
/* loaded from: classes4.dex */
public abstract class CursorTreeAdapter extends BaseExpandableListAdapter implements Filterable, CursorFilter.CursorFilterClient {
    private boolean mAutoRequery;
    SparseArray<MyCursorHelper> mChildrenCursorHelpers;
    private Context mContext;
    CursorFilter mCursorFilter;
    FilterQueryProvider mFilterQueryProvider;
    MyCursorHelper mGroupCursorHelper;
    private Handler mHandler;

    protected abstract void bindChildView(View view, Context context, Cursor cursor, boolean z);

    protected abstract void bindGroupView(View view, Context context, Cursor cursor, boolean z);

    protected abstract Cursor getChildrenCursor(Cursor cursor);

    protected abstract View newChildView(Context context, Cursor cursor, boolean z, ViewGroup viewGroup);

    protected abstract View newGroupView(Context context, Cursor cursor, boolean z, ViewGroup viewGroup);

    public CursorTreeAdapter(Cursor cursor, Context context) {
        init(cursor, context, true);
    }

    public CursorTreeAdapter(Cursor cursor, Context context, boolean autoRequery) {
        init(cursor, context, autoRequery);
    }

    private void init(Cursor cursor, Context context, boolean autoRequery) {
        this.mContext = context;
        this.mHandler = new Handler();
        this.mAutoRequery = autoRequery;
        this.mGroupCursorHelper = new MyCursorHelper(cursor);
        this.mChildrenCursorHelpers = new SparseArray<>();
    }

    synchronized MyCursorHelper getChildrenCursorHelper(int groupPosition, boolean requestCursor) {
        MyCursorHelper cursorHelper = this.mChildrenCursorHelpers.get(groupPosition);
        if (cursorHelper == null) {
            if (this.mGroupCursorHelper.moveTo(groupPosition) == null) {
                return null;
            }
            Cursor cursor = getChildrenCursor(this.mGroupCursorHelper.getCursor());
            cursorHelper = new MyCursorHelper(cursor);
            this.mChildrenCursorHelpers.put(groupPosition, cursorHelper);
        }
        return cursorHelper;
    }

    public void setGroupCursor(Cursor cursor) {
        this.mGroupCursorHelper.changeCursor(cursor, false);
    }

    public void setChildrenCursor(int groupPosition, Cursor childrenCursor) {
        MyCursorHelper childrenCursorHelper = getChildrenCursorHelper(groupPosition, false);
        childrenCursorHelper.changeCursor(childrenCursor, false);
    }

    @Override // android.widget.ExpandableListAdapter
    public Cursor getChild(int groupPosition, int childPosition) {
        return getChildrenCursorHelper(groupPosition, true).moveTo(childPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int groupPosition, int childPosition) {
        return getChildrenCursorHelper(groupPosition, true).getId(childPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int groupPosition) {
        MyCursorHelper helper = getChildrenCursorHelper(groupPosition, true);
        if (!this.mGroupCursorHelper.isValid() || helper == null) {
            return 0;
        }
        return helper.getCount();
    }

    @Override // android.widget.ExpandableListAdapter
    public Cursor getGroup(int groupPosition) {
        return this.mGroupCursorHelper.moveTo(groupPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.mGroupCursorHelper.getCount();
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int groupPosition) {
        return this.mGroupCursorHelper.getId(groupPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        Cursor cursor = this.mGroupCursorHelper.moveTo(groupPosition);
        if (cursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (convertView == null) {
            v = newGroupView(this.mContext, cursor, isExpanded, parent);
        } else {
            v = convertView;
        }
        bindGroupView(v, this.mContext, cursor, isExpanded);
        return v;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        MyCursorHelper cursorHelper = getChildrenCursorHelper(groupPosition, true);
        Cursor cursor = cursorHelper.moveTo(childPosition);
        if (cursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (convertView == null) {
            v = newChildView(this.mContext, cursor, isLastChild, parent);
        } else {
            v = convertView;
        }
        bindChildView(v, this.mContext, cursor, isLastChild);
        return v;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    private synchronized void releaseCursorHelpers() {
        for (int pos = this.mChildrenCursorHelpers.size() - 1; pos >= 0; pos--) {
            this.mChildrenCursorHelpers.valueAt(pos).deactivate();
        }
        this.mChildrenCursorHelpers.clear();
    }

    @Override // android.widget.BaseExpandableListAdapter
    public void notifyDataSetChanged() {
        notifyDataSetChanged(true);
    }

    public void notifyDataSetChanged(boolean releaseCursors) {
        if (releaseCursors) {
            releaseCursorHelpers();
        }
        super.notifyDataSetChanged();
    }

    @Override // android.widget.BaseExpandableListAdapter
    public void notifyDataSetInvalidated() {
        releaseCursorHelpers();
        super.notifyDataSetInvalidated();
    }

    @Override // android.widget.BaseExpandableListAdapter, android.widget.ExpandableListAdapter
    public void onGroupCollapsed(int groupPosition) {
        deactivateChildrenCursorHelper(groupPosition);
    }

    synchronized void deactivateChildrenCursorHelper(int groupPosition) {
        MyCursorHelper cursorHelper = getChildrenCursorHelper(groupPosition, true);
        this.mChildrenCursorHelpers.remove(groupPosition);
        cursorHelper.deactivate();
    }

    @Override // android.widget.CursorFilter.CursorFilterClient
    public String convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    @Override // android.widget.CursorFilter.CursorFilterClient
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filterQueryProvider = this.mFilterQueryProvider;
        if (filterQueryProvider != null) {
            return filterQueryProvider.runQuery(constraint);
        }
        return this.mGroupCursorHelper.getCursor();
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mCursorFilter == null) {
            this.mCursorFilter = new CursorFilter(this);
        }
        return this.mCursorFilter;
    }

    public FilterQueryProvider getFilterQueryProvider() {
        return this.mFilterQueryProvider;
    }

    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        this.mFilterQueryProvider = filterQueryProvider;
    }

    @Override // android.widget.CursorFilter.CursorFilterClient
    public void changeCursor(Cursor cursor) {
        this.mGroupCursorHelper.changeCursor(cursor, true);
    }

    @Override // android.widget.CursorFilter.CursorFilterClient
    public Cursor getCursor() {
        return this.mGroupCursorHelper.getCursor();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class MyCursorHelper {
        private MyContentObserver mContentObserver;
        private Cursor mCursor;
        private MyDataSetObserver mDataSetObserver;
        private boolean mDataValid;
        private int mRowIDColumn;

        MyCursorHelper(Cursor cursor) {
            boolean cursorPresent = cursor != null;
            this.mCursor = cursor;
            this.mDataValid = cursorPresent;
            this.mRowIDColumn = cursorPresent ? cursor.getColumnIndex("_id") : -1;
            this.mContentObserver = new MyContentObserver();
            this.mDataSetObserver = new MyDataSetObserver();
            if (cursorPresent) {
                cursor.registerContentObserver(this.mContentObserver);
                cursor.registerDataSetObserver(this.mDataSetObserver);
            }
        }

        Cursor getCursor() {
            return this.mCursor;
        }

        int getCount() {
            Cursor cursor;
            if (this.mDataValid && (cursor = this.mCursor) != null) {
                return cursor.getCount();
            }
            return 0;
        }

        long getId(int position) {
            Cursor cursor;
            if (this.mDataValid && (cursor = this.mCursor) != null && cursor.moveToPosition(position)) {
                return this.mCursor.getLong(this.mRowIDColumn);
            }
            return 0L;
        }

        Cursor moveTo(int position) {
            Cursor cursor;
            if (this.mDataValid && (cursor = this.mCursor) != null && cursor.moveToPosition(position)) {
                return this.mCursor;
            }
            return null;
        }

        void changeCursor(Cursor cursor, boolean releaseCursors) {
            if (cursor == this.mCursor) {
                return;
            }
            deactivate();
            this.mCursor = cursor;
            if (cursor != null) {
                cursor.registerContentObserver(this.mContentObserver);
                cursor.registerDataSetObserver(this.mDataSetObserver);
                this.mRowIDColumn = cursor.getColumnIndex("_id");
                this.mDataValid = true;
                CursorTreeAdapter.this.notifyDataSetChanged(releaseCursors);
                return;
            }
            this.mRowIDColumn = -1;
            this.mDataValid = false;
            CursorTreeAdapter.this.notifyDataSetInvalidated();
        }

        void deactivate() {
            Cursor cursor = this.mCursor;
            if (cursor == null) {
                return;
            }
            cursor.unregisterContentObserver(this.mContentObserver);
            this.mCursor.unregisterDataSetObserver(this.mDataSetObserver);
            this.mCursor.close();
            this.mCursor = null;
        }

        boolean isValid() {
            return this.mDataValid && this.mCursor != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public class MyContentObserver extends ContentObserver {
            public MyContentObserver() {
                super(CursorTreeAdapter.this.mHandler);
            }

            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return true;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                if (CursorTreeAdapter.this.mAutoRequery && MyCursorHelper.this.mCursor != null && !MyCursorHelper.this.mCursor.isClosed()) {
                    MyCursorHelper myCursorHelper = MyCursorHelper.this;
                    myCursorHelper.mDataValid = myCursorHelper.mCursor.requery();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public class MyDataSetObserver extends DataSetObserver {
            private MyDataSetObserver() {
            }

            @Override // android.database.DataSetObserver
            public void onChanged() {
                MyCursorHelper.this.mDataValid = true;
                CursorTreeAdapter.this.notifyDataSetChanged();
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                MyCursorHelper.this.mDataValid = false;
                CursorTreeAdapter.this.notifyDataSetInvalidated();
            }
        }
    }
}
