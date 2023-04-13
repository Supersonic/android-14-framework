package android.app;

import android.p008os.Bundle;
import android.p008os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes.dex */
public class ListActivity extends Activity {
    protected ListAdapter mAdapter;
    protected ListView mList;
    private Handler mHandler = new Handler();
    private boolean mFinishedStart = false;
    private Runnable mRequestFocus = new Runnable() { // from class: android.app.ListActivity.1
        @Override // java.lang.Runnable
        public void run() {
            ListActivity.this.mList.focusableViewAvailable(ListActivity.this.mList);
        }
    };
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() { // from class: android.app.ListActivity.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            ListActivity.this.onListItemClick((ListView) parent, v, position, id);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle state) {
        ensureList();
        super.onRestoreInstanceState(state);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(16908292);
        ListView listView = (ListView) findViewById(16908298);
        this.mList = listView;
        if (listView == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
        }
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        this.mList.setOnItemClickListener(this.mOnClickListener);
        if (this.mFinishedStart) {
            setListAdapter(this.mAdapter);
        }
        this.mHandler.post(this.mRequestFocus);
        this.mFinishedStart = true;
    }

    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            ensureList();
            this.mAdapter = adapter;
            this.mList.setAdapter(adapter);
        }
    }

    public void setSelection(int position) {
        this.mList.setSelection(position);
    }

    public int getSelectedItemPosition() {
        return this.mList.getSelectedItemPosition();
    }

    public long getSelectedItemId() {
        return this.mList.getSelectedItemId();
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    private void ensureList() {
        if (this.mList != null) {
            return;
        }
        setContentView(C4057R.layout.list_content_simple);
    }
}
