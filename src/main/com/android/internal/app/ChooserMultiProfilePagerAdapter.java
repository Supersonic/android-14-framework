package com.android.internal.app;

import android.content.Context;
import android.p008os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.C4057R;
import com.android.internal.app.AbstractMultiProfilePagerAdapter;
import com.android.internal.app.ChooserActivity;
import com.android.internal.widget.GridLayoutManager;
import com.android.internal.widget.RecyclerView;
/* loaded from: classes4.dex */
public class ChooserMultiProfilePagerAdapter extends AbstractMultiProfilePagerAdapter {
    private static final int SINGLE_CELL_SPAN_SIZE = 1;
    private int mBottomOffset;
    private final ChooserProfileDescriptor[] mItems;
    private int mMaxTargetsPerRow;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChooserMultiProfilePagerAdapter(Context context, ChooserActivity.ChooserGridAdapter adapter, AbstractMultiProfilePagerAdapter.EmptyStateProvider emptyStateProvider, AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager, UserHandle workProfileUserHandle, UserHandle cloneUserHandle, int maxTargetsPerRow) {
        super(context, 0, emptyStateProvider, quietModeManager, workProfileUserHandle, cloneUserHandle);
        this.mItems = new ChooserProfileDescriptor[]{createProfileDescriptor(adapter)};
        this.mMaxTargetsPerRow = maxTargetsPerRow;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChooserMultiProfilePagerAdapter(Context context, ChooserActivity.ChooserGridAdapter personalAdapter, ChooserActivity.ChooserGridAdapter workAdapter, AbstractMultiProfilePagerAdapter.EmptyStateProvider emptyStateProvider, AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager, int defaultProfile, UserHandle workProfileUserHandle, UserHandle cloneUserHandle, int maxTargetsPerRow) {
        super(context, defaultProfile, emptyStateProvider, quietModeManager, workProfileUserHandle, cloneUserHandle);
        this.mItems = new ChooserProfileDescriptor[]{createProfileDescriptor(personalAdapter), createProfileDescriptor(workAdapter)};
        this.mMaxTargetsPerRow = maxTargetsPerRow;
    }

    private ChooserProfileDescriptor createProfileDescriptor(ChooserActivity.ChooserGridAdapter adapter) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup rootView = (ViewGroup) inflater.inflate(C4057R.layout.chooser_list_per_profile, (ViewGroup) null, false);
        ChooserProfileDescriptor profileDescriptor = new ChooserProfileDescriptor(rootView, adapter);
        profileDescriptor.recyclerView.setAccessibilityDelegateCompat(new ChooserRecyclerViewAccessibilityDelegate(profileDescriptor.recyclerView));
        return profileDescriptor;
    }

    public void setMaxTargetsPerRow(int maxTargetsPerRow) {
        this.mMaxTargetsPerRow = maxTargetsPerRow;
    }

    RecyclerView getListViewForIndex(int index) {
        return getItem(index).recyclerView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserProfileDescriptor getItem(int pageIndex) {
        return this.mItems[pageIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public int getItemCount() {
        return this.mItems.length;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserActivity.ChooserGridAdapter getAdapterForIndex(int pageIndex) {
        return this.mItems[pageIndex].chooserGridAdapter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserListAdapter getListAdapterForUserHandle(UserHandle userHandle) {
        if (getPersonalListAdapter().getUserHandle().equals(userHandle) || userHandle.equals(getCloneUserHandle())) {
            return getPersonalListAdapter();
        }
        if (getWorkListAdapter() != null && getWorkListAdapter().getUserHandle().equals(userHandle)) {
            return getWorkListAdapter();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public void setupListAdapter(int pageIndex) {
        RecyclerView recyclerView = getItem(pageIndex).recyclerView;
        final ChooserActivity.ChooserGridAdapter chooserGridAdapter = getItem(pageIndex).chooserGridAdapter;
        final GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
        glm.setSpanCount(this.mMaxTargetsPerRow);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.android.internal.app.ChooserMultiProfilePagerAdapter.1
            @Override // com.android.internal.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (chooserGridAdapter.shouldCellSpan(position)) {
                    return 1;
                }
                return glm.getSpanCount();
            }
        });
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserListAdapter getActiveListAdapter() {
        return getAdapterForIndex(getCurrentPage()).getListAdapter();
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserListAdapter getInactiveListAdapter() {
        if (getCount() == 1) {
            return null;
        }
        return getAdapterForIndex(1 - getCurrentPage()).getListAdapter();
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserListAdapter getPersonalListAdapter() {
        return getAdapterForIndex(0).getListAdapter();
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserListAdapter getWorkListAdapter() {
        if (getCount() == 1) {
            return null;
        }
        return getAdapterForIndex(1).getListAdapter();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public ChooserActivity.ChooserGridAdapter getCurrentRootAdapter() {
        return getAdapterForIndex(getCurrentPage());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public RecyclerView getActiveAdapterView() {
        return getListViewForIndex(getCurrentPage());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public RecyclerView getInactiveAdapterView() {
        if (getCount() == 1) {
            return null;
        }
        return getListViewForIndex(1 - getCurrentPage());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEmptyStateBottomOffset(int bottomOffset) {
        this.mBottomOffset = bottomOffset;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter
    public void setupContainerPadding(View container) {
        int initialBottomPadding = getContext().getResources().getDimensionPixelSize(C4057R.dimen.resolver_empty_state_container_padding_bottom);
        container.setPadding(container.getPaddingLeft(), container.getPaddingTop(), container.getPaddingRight(), this.mBottomOffset + initialBottomPadding);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class ChooserProfileDescriptor extends AbstractMultiProfilePagerAdapter.ProfileDescriptor {
        private ChooserActivity.ChooserGridAdapter chooserGridAdapter;
        private RecyclerView recyclerView;

        ChooserProfileDescriptor(ViewGroup rootView, ChooserActivity.ChooserGridAdapter adapter) {
            super(rootView);
            this.chooserGridAdapter = adapter;
            this.recyclerView = (RecyclerView) rootView.findViewById(C4057R.C4059id.resolver_list);
        }
    }
}
