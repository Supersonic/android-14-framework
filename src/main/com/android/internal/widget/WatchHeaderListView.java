package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.function.Predicate;
/* loaded from: classes5.dex */
public class WatchHeaderListView extends ListView {
    private View mTopPanel;

    public WatchHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchHeaderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WatchHeaderListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.ListView
    protected HeaderViewListAdapter wrapHeaderListAdapterInternal(ArrayList<ListView.FixedViewInfo> headerViewInfos, ArrayList<ListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
        return new WatchHeaderListAdapter(headerViewInfos, footerViewInfos, adapter);
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.ViewManager
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (this.mTopPanel == null) {
            setTopPanel(child);
            return;
        }
        throw new IllegalStateException("WatchHeaderListView can host only one header");
    }

    public void setTopPanel(View v) {
        this.mTopPanel = v;
        wrapAdapterIfNecessary();
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        wrapAdapterIfNecessary();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public View findViewTraversal(int id) {
        View view;
        View v = super.findViewTraversal(id);
        if (v == null && (view = this.mTopPanel) != null && !view.isRootNamespace()) {
            return this.mTopPanel.findViewById(id);
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public View findViewWithTagTraversal(Object tag) {
        View view;
        View v = super.findViewWithTagTraversal(tag);
        if (v == null && (view = this.mTopPanel) != null && !view.isRootNamespace()) {
            return this.mTopPanel.findViewWithTag(tag);
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public <T extends View> T findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
        View view;
        T t = (T) super.findViewByPredicateTraversal(predicate, childToSkip);
        if (t == null && (view = this.mTopPanel) != null && view != childToSkip && !view.isRootNamespace()) {
            return (T) this.mTopPanel.findViewByPredicate(predicate);
        }
        return t;
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    public int getHeaderViewsCount() {
        if (this.mTopPanel == null) {
            return super.getHeaderViewsCount();
        }
        return super.getHeaderViewsCount() + (this.mTopPanel.getVisibility() == 8 ? 0 : 1);
    }

    private void wrapAdapterIfNecessary() {
        ListAdapter adapter = getAdapter();
        if (adapter != null && this.mTopPanel != null) {
            if (!(adapter instanceof WatchHeaderListAdapter)) {
                wrapHeaderListAdapterInternal();
            }
            ((WatchHeaderListAdapter) getAdapter()).setTopPanel(this.mTopPanel);
            dispatchDataSetObserverOnChangedInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class WatchHeaderListAdapter extends HeaderViewListAdapter {
        private View mTopPanel;

        public WatchHeaderListAdapter(ArrayList<ListView.FixedViewInfo> headerViewInfos, ArrayList<ListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
            super(headerViewInfos, footerViewInfos, adapter);
        }

        public void setTopPanel(View v) {
            this.mTopPanel = v;
        }

        private int getTopPanelCount() {
            View view = this.mTopPanel;
            return (view == null || view.getVisibility() == 8) ? 0 : 1;
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.Adapter
        public int getCount() {
            return super.getCount() + getTopPanelCount();
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return getTopPanelCount() == 0 && super.areAllItemsEnabled();
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            int topPanelCount = getTopPanelCount();
            if (position < topPanelCount) {
                return false;
            }
            return super.isEnabled(position - topPanelCount);
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.Adapter
        public Object getItem(int position) {
            int topPanelCount = getTopPanelCount();
            if (position < topPanelCount) {
                return null;
            }
            return super.getItem(position - topPanelCount);
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.Adapter
        public long getItemId(int position) {
            int numHeaders = getHeadersCount() + getTopPanelCount();
            if (getWrappedAdapter() != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = getWrappedAdapter().getCount();
                if (adjPosition < adapterCount) {
                    return getWrappedAdapter().getItemId(adjPosition);
                }
                return -1L;
            }
            return -1L;
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            int topPanelCount = getTopPanelCount();
            return position < topPanelCount ? this.mTopPanel : super.getView(position - topPanelCount, convertView, parent);
        }

        @Override // android.widget.HeaderViewListAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount() + getTopPanelCount();
            if (getWrappedAdapter() != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = getWrappedAdapter().getCount();
                if (adjPosition < adapterCount) {
                    return getWrappedAdapter().getItemViewType(adjPosition);
                }
                return -2;
            }
            return -2;
        }
    }
}
