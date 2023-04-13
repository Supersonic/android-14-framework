package com.android.server.p014wm;

import android.annotation.SystemApi;
import android.app.ActivityOptions;
import android.app.TaskInfo;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* renamed from: com.android.server.wm.ActivityInterceptorCallback */
/* loaded from: classes2.dex */
public interface ActivityInterceptorCallback {
    public static final int DREAM_MANAGER_ORDERED_ID = 4;
    public static final int MAINLINE_FIRST_ORDERED_ID = 1000;
    public static final int MAINLINE_LAST_ORDERED_ID = 1001;
    public static final int MAINLINE_SDK_SANDBOX_ORDER_ID = 1001;
    public static final int PERMISSION_POLICY_ORDERED_ID = 1;
    public static final int SYSTEM_FIRST_ORDERED_ID = 0;
    public static final int SYSTEM_LAST_ORDERED_ID = 4;
    public static final int VIRTUAL_DEVICE_SERVICE_ORDERED_ID = 3;

    static boolean isValidMainlineOrderId(int i) {
        return i >= 1000 && i <= 1001;
    }

    default void onActivityLaunched(TaskInfo taskInfo, ActivityInfo activityInfo, ActivityInterceptorInfo activityInterceptorInfo) {
    }

    ActivityInterceptResult onInterceptActivityLaunch(ActivityInterceptorInfo activityInterceptorInfo);

    static boolean isValidOrderId(int i) {
        return isValidMainlineOrderId(i) || (i >= 0 && i <= 4);
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    /* renamed from: com.android.server.wm.ActivityInterceptorCallback$ActivityInterceptorInfo */
    /* loaded from: classes2.dex */
    public static final class ActivityInterceptorInfo {
        public final ActivityInfo mActivityInfo;
        public final String mCallingFeatureId;
        public final String mCallingPackage;
        public final int mCallingPid;
        public final int mCallingUid;
        public final ActivityOptions mCheckedOptions;
        public final Runnable mClearOptionsAnimation;
        public final Intent mIntent;
        public final int mRealCallingPid;
        public final int mRealCallingUid;
        public final ResolveInfo mResolveInfo;
        public final String mResolvedType;
        public final int mUserId;

        public ActivityInterceptorInfo(Builder builder) {
            this.mCallingUid = builder.mCallingUid;
            this.mCallingPid = builder.mCallingPid;
            this.mRealCallingUid = builder.mRealCallingUid;
            this.mRealCallingPid = builder.mRealCallingPid;
            this.mUserId = builder.mUserId;
            this.mIntent = builder.mIntent;
            this.mResolveInfo = builder.mResolveInfo;
            this.mActivityInfo = builder.mActivityInfo;
            this.mResolvedType = builder.mResolvedType;
            this.mCallingPackage = builder.mCallingPackage;
            this.mCallingFeatureId = builder.mCallingFeatureId;
            this.mCheckedOptions = builder.mCheckedOptions;
            this.mClearOptionsAnimation = builder.mClearOptionsAnimation;
        }

        /* renamed from: com.android.server.wm.ActivityInterceptorCallback$ActivityInterceptorInfo$Builder */
        /* loaded from: classes2.dex */
        public static final class Builder {
            public final ActivityInfo mActivityInfo;
            public final int mCallingPid;
            public final int mCallingUid;
            public final Intent mIntent;
            public final int mRealCallingPid;
            public final int mRealCallingUid;
            public final ResolveInfo mResolveInfo;
            public String mResolvedType;
            public final int mUserId;
            public String mCallingPackage = null;
            public String mCallingFeatureId = null;
            public ActivityOptions mCheckedOptions = null;
            public Runnable mClearOptionsAnimation = null;

            public Builder(int i, int i2, int i3, int i4, int i5, Intent intent, ResolveInfo resolveInfo, ActivityInfo activityInfo) {
                this.mCallingUid = i;
                this.mCallingPid = i2;
                this.mRealCallingUid = i3;
                this.mRealCallingPid = i4;
                this.mUserId = i5;
                this.mIntent = intent;
                this.mResolveInfo = resolveInfo;
                this.mActivityInfo = activityInfo;
            }

            public ActivityInterceptorInfo build() {
                return new ActivityInterceptorInfo(this);
            }

            public Builder setResolvedType(String str) {
                this.mResolvedType = str;
                return this;
            }

            public Builder setCallingPackage(String str) {
                this.mCallingPackage = str;
                return this;
            }

            public Builder setCallingFeatureId(String str) {
                this.mCallingFeatureId = str;
                return this;
            }

            public Builder setCheckedOptions(ActivityOptions activityOptions) {
                this.mCheckedOptions = activityOptions;
                return this;
            }

            public Builder setClearOptionsAnimationRunnable(Runnable runnable) {
                this.mClearOptionsAnimation = runnable;
                return this;
            }
        }

        public int getCallingUid() {
            return this.mCallingUid;
        }

        public int getCallingPid() {
            return this.mCallingPid;
        }

        public int getRealCallingUid() {
            return this.mRealCallingUid;
        }

        public int getRealCallingPid() {
            return this.mRealCallingPid;
        }

        public int getUserId() {
            return this.mUserId;
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public ResolveInfo getResolveInfo() {
            return this.mResolveInfo;
        }

        public ActivityInfo getActivityInfo() {
            return this.mActivityInfo;
        }

        public String getResolvedType() {
            return this.mResolvedType;
        }

        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        public String getCallingFeatureId() {
            return this.mCallingFeatureId;
        }

        public ActivityOptions getCheckedOptions() {
            return this.mCheckedOptions;
        }

        public Runnable getClearOptionsAnimationRunnable() {
            return this.mClearOptionsAnimation;
        }
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    /* renamed from: com.android.server.wm.ActivityInterceptorCallback$ActivityInterceptResult */
    /* loaded from: classes2.dex */
    public static final class ActivityInterceptResult {
        public final ActivityOptions mActivityOptions;
        public final boolean mActivityResolved;
        public final Intent mIntent;

        public ActivityInterceptResult(Intent intent, ActivityOptions activityOptions) {
            this(intent, activityOptions, false);
        }

        public ActivityInterceptResult(Intent intent, ActivityOptions activityOptions, boolean z) {
            this.mIntent = intent;
            this.mActivityOptions = activityOptions;
            this.mActivityResolved = z;
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public ActivityOptions getActivityOptions() {
            return this.mActivityOptions;
        }

        public boolean isActivityResolved() {
            return this.mActivityResolved;
        }
    }
}
