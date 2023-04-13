package com.android.internal.app;

import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.p008os.UserHandle;
import com.android.internal.app.AbstractMultiProfilePagerAdapter;
import com.android.internal.app.NoCrossProfileEmptyStateProvider;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class NoCrossProfileEmptyStateProvider implements AbstractMultiProfilePagerAdapter.EmptyStateProvider {
    private final AbstractMultiProfilePagerAdapter.CrossProfileIntentsChecker mCrossProfileIntentsChecker;
    private final AbstractMultiProfilePagerAdapter.EmptyState mNoPersonalToWorkEmptyState;
    private final AbstractMultiProfilePagerAdapter.EmptyState mNoWorkToPersonalEmptyState;
    private final UserHandle mPersonalProfileUserHandle;
    private final UserHandle mTabOwnerUserHandleForLaunch;

    public NoCrossProfileEmptyStateProvider(UserHandle personalUserHandle, AbstractMultiProfilePagerAdapter.EmptyState noWorkToPersonalEmptyState, AbstractMultiProfilePagerAdapter.EmptyState noPersonalToWorkEmptyState, AbstractMultiProfilePagerAdapter.CrossProfileIntentsChecker crossProfileIntentsChecker, UserHandle preselectedTabOwnerUserHandle) {
        this.mPersonalProfileUserHandle = personalUserHandle;
        this.mNoWorkToPersonalEmptyState = noWorkToPersonalEmptyState;
        this.mNoPersonalToWorkEmptyState = noPersonalToWorkEmptyState;
        this.mCrossProfileIntentsChecker = crossProfileIntentsChecker;
        this.mTabOwnerUserHandleForLaunch = preselectedTabOwnerUserHandle;
    }

    @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyStateProvider
    public AbstractMultiProfilePagerAdapter.EmptyState getEmptyState(ResolverListAdapter resolverListAdapter) {
        boolean shouldShowBlocker = (this.mTabOwnerUserHandleForLaunch.equals(resolverListAdapter.getUserHandle()) || this.mCrossProfileIntentsChecker.hasCrossProfileIntents(resolverListAdapter.getIntents(), this.mTabOwnerUserHandleForLaunch.getIdentifier(), resolverListAdapter.getUserHandle().getIdentifier())) ? false : true;
        if (!shouldShowBlocker) {
            return null;
        }
        if (resolverListAdapter.getUserHandle().equals(this.mPersonalProfileUserHandle)) {
            return this.mNoWorkToPersonalEmptyState;
        }
        return this.mNoPersonalToWorkEmptyState;
    }

    /* loaded from: classes4.dex */
    public static class DevicePolicyBlockerEmptyState implements AbstractMultiProfilePagerAdapter.EmptyState {
        private final Context mContext;
        private final int mDefaultSubtitleResource;
        private final int mDefaultTitleResource;
        private final String mDevicePolicyStringSubtitleId;
        private final String mDevicePolicyStringTitleId;
        private final String mEventCategory;
        private final int mEventId;

        public DevicePolicyBlockerEmptyState(Context context, String devicePolicyStringTitleId, int defaultTitleResource, String devicePolicyStringSubtitleId, int defaultSubtitleResource, int devicePolicyEventId, String devicePolicyEventCategory) {
            this.mContext = context;
            this.mDevicePolicyStringTitleId = devicePolicyStringTitleId;
            this.mDefaultTitleResource = defaultTitleResource;
            this.mDevicePolicyStringSubtitleId = devicePolicyStringSubtitleId;
            this.mDefaultSubtitleResource = defaultSubtitleResource;
            this.mEventId = devicePolicyEventId;
            this.mEventCategory = devicePolicyEventCategory;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public String getTitle() {
            return ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getResources().getString(this.mDevicePolicyStringTitleId, new Supplier() { // from class: com.android.internal.app.NoCrossProfileEmptyStateProvider$DevicePolicyBlockerEmptyState$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getTitle$0;
                    lambda$getTitle$0 = NoCrossProfileEmptyStateProvider.DevicePolicyBlockerEmptyState.this.lambda$getTitle$0();
                    return lambda$getTitle$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getTitle$0() {
            return this.mContext.getString(this.mDefaultTitleResource);
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public String getSubtitle() {
            return ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getResources().getString(this.mDevicePolicyStringSubtitleId, new Supplier() { // from class: com.android.internal.app.NoCrossProfileEmptyStateProvider$DevicePolicyBlockerEmptyState$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getSubtitle$1;
                    lambda$getSubtitle$1 = NoCrossProfileEmptyStateProvider.DevicePolicyBlockerEmptyState.this.lambda$getSubtitle$1();
                    return lambda$getSubtitle$1;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getSubtitle$1() {
            return this.mContext.getString(this.mDefaultSubtitleResource);
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public void onEmptyStateShown() {
            DevicePolicyEventLogger.createEvent(this.mEventId).setStrings(this.mEventCategory).write();
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.EmptyState
        public boolean shouldSkipDataRebuild() {
            return true;
        }
    }
}
