package com.android.internal.app;

import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.content.Context;
import android.p008os.UserHandle;
import com.android.internal.C4057R;
import com.android.internal.app.AbstractMultiProfilePagerAdapter;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class WorkProfilePausedEmptyStateProvider implements AbstractMultiProfilePagerAdapter.EmptyStateProvider {
    private final Context mContext;
    private final String mMetricsCategory;
    private final AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener mOnSwitchOnWorkSelectedListener;
    private final AbstractMultiProfilePagerAdapter.QuietModeManager mQuietModeManager;
    private final UserHandle mWorkProfileUserHandle;

    public WorkProfilePausedEmptyStateProvider(Context context, UserHandle workProfileUserHandle, AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager, AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener onSwitchOnWorkSelectedListener, String metricsCategory) {
        this.mContext = context;
        this.mWorkProfileUserHandle = workProfileUserHandle;
        this.mQuietModeManager = quietModeManager;
        this.mMetricsCategory = metricsCategory;
        this.mOnSwitchOnWorkSelectedListener = onSwitchOnWorkSelectedListener;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyStateProvider
    public AbstractMultiProfilePagerAdapter.EmptyState getEmptyState(ResolverListAdapter resolverListAdapter) {
        if (!resolverListAdapter.getUserHandle().equals(this.mWorkProfileUserHandle) || !this.mQuietModeManager.isQuietModeEnabled(this.mWorkProfileUserHandle) || resolverListAdapter.getCount() == 0) {
            return null;
        }
        String title = ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.RESOLVER_WORK_PAUSED_TITLE, new Supplier() { // from class: com.android.internal.app.WorkProfilePausedEmptyStateProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getEmptyState$0;
                lambda$getEmptyState$0 = WorkProfilePausedEmptyStateProvider.this.lambda$getEmptyState$0();
                return lambda$getEmptyState$0;
            }
        });
        return new WorkProfileOffEmptyState(title, new AbstractMultiProfilePagerAdapter.EmptyState.ClickListener() { // from class: com.android.internal.app.WorkProfilePausedEmptyStateProvider$$ExternalSyntheticLambda1
            @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState.ClickListener
            public final void onClick(AbstractMultiProfilePagerAdapter.EmptyState.TabControl tabControl) {
                WorkProfilePausedEmptyStateProvider.this.lambda$getEmptyState$1(tabControl);
            }
        }, this.mMetricsCategory);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEmptyState$0() {
        return this.mContext.getString(C4057R.string.resolver_turn_on_work_apps);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getEmptyState$1(AbstractMultiProfilePagerAdapter.EmptyState.TabControl tab) {
        tab.showSpinner();
        AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener onSwitchOnWorkSelectedListener = this.mOnSwitchOnWorkSelectedListener;
        if (onSwitchOnWorkSelectedListener != null) {
            onSwitchOnWorkSelectedListener.onSwitchOnWorkSelected();
        }
        this.mQuietModeManager.requestQuietModeEnabled(false, this.mWorkProfileUserHandle);
    }

    /* loaded from: classes4.dex */
    public static class WorkProfileOffEmptyState implements AbstractMultiProfilePagerAdapter.EmptyState {
        private final String mMetricsCategory;
        private final AbstractMultiProfilePagerAdapter.EmptyState.ClickListener mOnClick;
        private final String mTitle;

        public WorkProfileOffEmptyState(String title, AbstractMultiProfilePagerAdapter.EmptyState.ClickListener onClick, String metricsCategory) {
            this.mTitle = title;
            this.mOnClick = onClick;
            this.mMetricsCategory = metricsCategory;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public String getTitle() {
            return this.mTitle;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public AbstractMultiProfilePagerAdapter.EmptyState.ClickListener getButtonClickListener() {
            return this.mOnClick;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public void onEmptyStateShown() {
            DevicePolicyEventLogger.createEvent(157).setStrings(this.mMetricsCategory).write();
        }
    }
}
