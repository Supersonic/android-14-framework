package com.android.internal.app;

import android.content.Context;
import android.p008os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.internal.C4057R;
import com.android.internal.app.AbstractMultiProfilePagerAdapter;
/* loaded from: classes4.dex */
public class ResolverMultiProfilePagerAdapter extends AbstractMultiProfilePagerAdapter {
    private final ResolverProfileDescriptor[] mItems;
    private boolean mUseLayoutWithDefault;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResolverMultiProfilePagerAdapter(Context context, ResolverListAdapter adapter, AbstractMultiProfilePagerAdapter.EmptyStateProvider emptyStateProvider, AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager, UserHandle workProfileUserHandle, UserHandle cloneUserHandle) {
        super(context, 0, emptyStateProvider, quietModeManager, workProfileUserHandle, cloneUserHandle);
        this.mItems = new ResolverProfileDescriptor[]{createProfileDescriptor(adapter)};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResolverMultiProfilePagerAdapter(Context context, ResolverListAdapter personalAdapter, ResolverListAdapter workAdapter, AbstractMultiProfilePagerAdapter.EmptyStateProvider emptyStateProvider, AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager, int defaultProfile, UserHandle workProfileUserHandle, UserHandle cloneUserHandle) {
        super(context, defaultProfile, emptyStateProvider, quietModeManager, workProfileUserHandle, cloneUserHandle);
        this.mItems = new ResolverProfileDescriptor[]{createProfileDescriptor(personalAdapter), createProfileDescriptor(workAdapter)};
    }

    private ResolverProfileDescriptor createProfileDescriptor(ResolverListAdapter adapter) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup rootView = (ViewGroup) inflater.inflate(C4057R.layout.resolver_list_per_profile, (ViewGroup) null, false);
        return new ResolverProfileDescriptor(rootView, adapter);
    }

    ListView getListViewForIndex(int index) {
        return getItem(index).listView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverProfileDescriptor getItem(int pageIndex) {
        return this.mItems[pageIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public int getItemCount() {
        return this.mItems.length;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    void setupListAdapter(int pageIndex) {
        ListView listView = getItem(pageIndex).listView;
        listView.setAdapter((ListAdapter) getItem(pageIndex).resolverListAdapter);
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getAdapterForIndex(int pageIndex) {
        return this.mItems[pageIndex].resolverListAdapter;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter, com.android.internal.widget.PagerAdapter
    public ViewGroup instantiateItem(ViewGroup container, int position) {
        setupListAdapter(position);
        return super.instantiateItem(container, position);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getListAdapterForUserHandle(UserHandle userHandle) {
        if (getPersonalListAdapter().getUserHandle().equals(userHandle) || userHandle.equals(getCloneUserHandle())) {
            return getPersonalListAdapter();
        }
        if (getWorkListAdapter() != null && getWorkListAdapter().getUserHandle().equals(userHandle)) {
            return getWorkListAdapter();
        }
        return null;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getActiveListAdapter() {
        return getAdapterForIndex(getCurrentPage());
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getInactiveListAdapter() {
        if (getCount() == 1) {
            return null;
        }
        return getAdapterForIndex(1 - getCurrentPage());
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getPersonalListAdapter() {
        return getAdapterForIndex(0);
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getWorkListAdapter() {
        if (getCount() == 1) {
            return null;
        }
        return getAdapterForIndex(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ResolverListAdapter getCurrentRootAdapter() {
        return getActiveListAdapter();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ListView getActiveAdapterView() {
        return getListViewForIndex(getCurrentPage());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ViewGroup getInactiveAdapterView() {
        if (getCount() == 1) {
            return null;
        }
        return getListViewForIndex(1 - getCurrentPage());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUseLayoutWithDefault(boolean useLayoutWithDefault) {
        this.mUseLayoutWithDefault = useLayoutWithDefault;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    protected void setupContainerPadding(View container) {
        int bottom = this.mUseLayoutWithDefault ? container.getPaddingBottom() : 0;
        container.setPadding(container.getPaddingLeft(), container.getPaddingTop(), container.getPaddingRight(), bottom);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class ResolverProfileDescriptor extends AbstractMultiProfilePagerAdapter.ProfileDescriptor {
        final ListView listView;
        private ResolverListAdapter resolverListAdapter;

        ResolverProfileDescriptor(ViewGroup rootView, ResolverListAdapter adapter) {
            super(rootView);
            this.resolverListAdapter = adapter;
            this.listView = (ListView) rootView.findViewById(C4057R.C4059id.resolver_list);
        }
    }
}
