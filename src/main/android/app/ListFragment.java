package android.app;

import android.p008os.Bundle;
import android.p008os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes.dex */
public class ListFragment extends Fragment {
    ListAdapter mAdapter;
    CharSequence mEmptyText;
    View mEmptyView;
    ListView mList;
    View mListContainer;
    boolean mListShown;
    View mProgressContainer;
    TextView mStandardEmptyView;
    private final Handler mHandler = new Handler();
    private final Runnable mRequestFocus = new Runnable() { // from class: android.app.ListFragment.1
        @Override // java.lang.Runnable
        public void run() {
            ListFragment.this.mList.focusableViewAvailable(ListFragment.this.mList);
        }
    };
    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() { // from class: android.app.ListFragment.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            ListFragment.this.onListItemClick((ListView) parent, v, position, id);
        }
    };

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(17367060, container, false);
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureList();
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mList = null;
        this.mListShown = false;
        this.mListContainer = null;
        this.mProgressContainer = null;
        this.mEmptyView = null;
        this.mStandardEmptyView = null;
        super.onDestroyView();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    public void setListAdapter(ListAdapter adapter) {
        boolean hadAdapter = this.mAdapter != null;
        this.mAdapter = adapter;
        ListView listView = this.mList;
        if (listView != null) {
            listView.setAdapter(adapter);
            if (!this.mListShown && !hadAdapter) {
                setListShown(true, getView().getWindowToken() != null);
            }
        }
    }

    public void setSelection(int position) {
        ensureList();
        this.mList.setSelection(position);
    }

    public int getSelectedItemPosition() {
        ensureList();
        return this.mList.getSelectedItemPosition();
    }

    public long getSelectedItemId() {
        ensureList();
        return this.mList.getSelectedItemId();
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    public void setEmptyText(CharSequence text) {
        ensureList();
        TextView textView = this.mStandardEmptyView;
        if (textView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        textView.setText(text);
        if (this.mEmptyText == null) {
            this.mList.setEmptyView(this.mStandardEmptyView);
        }
        this.mEmptyText = text;
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    private void setListShown(boolean shown, boolean animate) {
        ensureList();
        View view = this.mProgressContainer;
        if (view == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (this.mListShown == shown) {
            return;
        }
        this.mListShown = shown;
        if (shown) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432577));
                this.mListContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432576));
            } else {
                view.clearAnimation();
                this.mListContainer.clearAnimation();
            }
            this.mProgressContainer.setVisibility(8);
            this.mListContainer.setVisibility(0);
            return;
        }
        if (animate) {
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432576));
            this.mListContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432577));
        } else {
            view.clearAnimation();
            this.mListContainer.clearAnimation();
        }
        this.mProgressContainer.setVisibility(0);
        this.mListContainer.setVisibility(8);
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    private void ensureList() {
        if (this.mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ListView) {
            this.mList = (ListView) root;
        } else {
            TextView textView = (TextView) root.findViewById(C4057R.C4059id.internalEmpty);
            this.mStandardEmptyView = textView;
            if (textView == null) {
                this.mEmptyView = root.findViewById(16908292);
            } else {
                textView.setVisibility(8);
            }
            this.mProgressContainer = root.findViewById(C4057R.C4059id.progressContainer);
            this.mListContainer = root.findViewById(C4057R.C4059id.listContainer);
            View rawListView = root.findViewById(16908298);
            if (!(rawListView instanceof ListView)) {
                throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
            }
            ListView listView = (ListView) rawListView;
            this.mList = listView;
            if (listView == null) {
                throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
            }
            View view = this.mEmptyView;
            if (view != null) {
                listView.setEmptyView(view);
            } else {
                CharSequence charSequence = this.mEmptyText;
                if (charSequence != null) {
                    this.mStandardEmptyView.setText(charSequence);
                    this.mList.setEmptyView(this.mStandardEmptyView);
                }
            }
        }
        this.mListShown = true;
        this.mList.setOnItemClickListener(this.mOnClickListener);
        if (this.mAdapter != null) {
            ListAdapter adapter = this.mAdapter;
            this.mAdapter = null;
            setListAdapter(adapter);
        } else if (this.mProgressContainer != null) {
            setListShown(false, false);
        }
        this.mHandler.post(this.mRequestFocus);
    }
}
