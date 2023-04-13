package com.android.internal.app.chooser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.p008os.Bundle;
import android.p008os.UserHandle;
import android.service.chooser.ChooserTarget;
import android.text.SpannableStringBuilder;
import android.util.Log;
import com.android.internal.app.ResolverActivity;
import com.android.internal.app.ResolverListAdapter;
import com.android.internal.app.SimpleIconFactory;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public final class SelectableTargetInfo implements ChooserTargetInfo {
    private static final String TAG = "SelectableTargetInfo";
    private final ResolveInfo mBackupResolveInfo;
    private CharSequence mBadgeContentDescription;
    private Drawable mBadgeIcon;
    private final ChooserTarget mChooserTarget;
    private final Context mContext;
    private Drawable mDisplayIcon;
    private final String mDisplayLabel;
    private final int mFillInFlags;
    private final Intent mFillInIntent;
    private final boolean mIsPinned;
    private boolean mIsSuspended;
    private final float mModifiedScore;
    private final PackageManager mPm;
    private final SelectableTargetInfoCommunicator mSelectableTargetInfoCommunicator;
    private ShortcutInfo mShortcutInfo;
    private final DisplayResolveInfo mSourceInfo;

    /* loaded from: classes4.dex */
    public interface SelectableTargetInfoCommunicator {
        Intent getReferrerFillInIntent();

        Intent getTargetIntent();

        ResolverListAdapter.ActivityInfoPresentationGetter makePresentationGetter(ActivityInfo activityInfo);
    }

    public SelectableTargetInfo(Context context, DisplayResolveInfo sourceInfo, ChooserTarget chooserTarget, float modifiedScore, SelectableTargetInfoCommunicator selectableTargetInfoComunicator, ShortcutInfo shortcutInfo) {
        boolean z;
        ResolveInfo ri;
        ActivityInfo ai;
        this.mBadgeIcon = null;
        this.mIsSuspended = false;
        this.mContext = context;
        this.mSourceInfo = sourceInfo;
        this.mChooserTarget = chooserTarget;
        this.mModifiedScore = modifiedScore;
        this.mPm = context.getPackageManager();
        this.mSelectableTargetInfoCommunicator = selectableTargetInfoComunicator;
        this.mShortcutInfo = shortcutInfo;
        if (shortcutInfo == null || !shortcutInfo.isPinned()) {
            z = false;
        } else {
            z = true;
        }
        this.mIsPinned = z;
        if (sourceInfo != null && (ri = sourceInfo.getResolveInfo()) != null && (ai = ri.activityInfo) != null && ai.applicationInfo != null) {
            PackageManager pm = context.getPackageManager();
            this.mBadgeIcon = pm.getApplicationIcon(ai.applicationInfo);
            this.mBadgeContentDescription = pm.getApplicationLabel(ai.applicationInfo);
            this.mIsSuspended = (ai.applicationInfo.flags & 1073741824) != 0;
        }
        if (sourceInfo == null) {
            this.mBackupResolveInfo = context.getPackageManager().resolveActivity(getResolvedIntent(), 0);
        } else {
            this.mBackupResolveInfo = null;
        }
        this.mFillInIntent = null;
        this.mFillInFlags = 0;
        this.mDisplayLabel = sanitizeDisplayLabel(chooserTarget.getTitle());
    }

    private SelectableTargetInfo(SelectableTargetInfo other, Intent fillInIntent, int flags) {
        this.mBadgeIcon = null;
        this.mIsSuspended = false;
        this.mContext = other.mContext;
        this.mPm = other.mPm;
        this.mSelectableTargetInfoCommunicator = other.mSelectableTargetInfoCommunicator;
        this.mSourceInfo = other.mSourceInfo;
        this.mBackupResolveInfo = other.mBackupResolveInfo;
        ChooserTarget chooserTarget = other.mChooserTarget;
        this.mChooserTarget = chooserTarget;
        this.mBadgeIcon = other.mBadgeIcon;
        this.mBadgeContentDescription = other.mBadgeContentDescription;
        synchronized (other) {
            this.mShortcutInfo = other.mShortcutInfo;
            this.mDisplayIcon = other.mDisplayIcon;
        }
        this.mFillInIntent = fillInIntent;
        this.mFillInFlags = flags;
        this.mModifiedScore = other.mModifiedScore;
        this.mIsPinned = other.mIsPinned;
        this.mDisplayLabel = sanitizeDisplayLabel(chooserTarget.getTitle());
    }

    private String sanitizeDisplayLabel(CharSequence label) {
        SpannableStringBuilder sb = new SpannableStringBuilder(label);
        sb.clearSpans();
        return sb.toString();
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean isSuspended() {
        return this.mIsSuspended;
    }

    public DisplayResolveInfo getDisplayResolveInfo() {
        return this.mSourceInfo;
    }

    public boolean loadIcon() {
        ShortcutInfo shortcutInfo;
        Drawable icon;
        synchronized (this) {
            shortcutInfo = this.mShortcutInfo;
            icon = this.mDisplayIcon;
        }
        boolean shouldLoadIcon = icon == null && shortcutInfo != null;
        if (shouldLoadIcon) {
            Drawable icon2 = getChooserTargetIconDrawable(this.mChooserTarget, shortcutInfo);
            synchronized (this) {
                this.mDisplayIcon = icon2;
                this.mShortcutInfo = null;
            }
        }
        return shouldLoadIcon;
    }

    private Drawable getChooserTargetIconDrawable(ChooserTarget target, ShortcutInfo shortcutInfo) {
        Drawable directShareIcon = null;
        Icon icon = target.getIcon();
        if (icon != null) {
            directShareIcon = icon.loadDrawable(this.mContext);
        } else if (shortcutInfo != null) {
            LauncherApps launcherApps = (LauncherApps) this.mContext.getSystemService(Context.LAUNCHER_APPS_SERVICE);
            directShareIcon = launcherApps.getShortcutIconDrawable(shortcutInfo, 0);
        }
        if (directShareIcon == null) {
            return null;
        }
        ActivityInfo info = null;
        try {
            info = this.mPm.getActivityInfo(target.getComponentName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(TAG, "Could not find activity associated with ChooserTarget");
        }
        if (info == null) {
            return null;
        }
        Bitmap appIcon = this.mSelectableTargetInfoCommunicator.makePresentationGetter(info).getIconBitmap(null);
        SimpleIconFactory sif = SimpleIconFactory.obtain(this.mContext);
        Bitmap directShareBadgedIcon = sif.createAppBadgedIconBitmap(directShareIcon, appIcon);
        sif.recycle();
        return new BitmapDrawable(this.mContext.getResources(), directShareBadgedIcon);
    }

    @Override // com.android.internal.app.chooser.ChooserTargetInfo
    public float getModifiedScore() {
        return this.mModifiedScore;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public Intent getResolvedIntent() {
        DisplayResolveInfo displayResolveInfo = this.mSourceInfo;
        if (displayResolveInfo != null) {
            return displayResolveInfo.getResolvedIntent();
        }
        Intent targetIntent = new Intent(this.mSelectableTargetInfoCommunicator.getTargetIntent());
        targetIntent.setComponent(this.mChooserTarget.getComponentName());
        targetIntent.putExtras(this.mChooserTarget.getIntentExtras());
        return targetIntent;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public ComponentName getResolvedComponentName() {
        DisplayResolveInfo displayResolveInfo = this.mSourceInfo;
        if (displayResolveInfo != null) {
            return displayResolveInfo.getResolvedComponentName();
        }
        if (this.mBackupResolveInfo != null) {
            return new ComponentName(this.mBackupResolveInfo.activityInfo.packageName, this.mBackupResolveInfo.activityInfo.name);
        }
        return null;
    }

    private Intent getBaseIntentToSend() {
        Intent result = getResolvedIntent();
        if (result == null) {
            Log.m110e(TAG, "ChooserTargetInfo: no base intent available to send");
        } else {
            result = new Intent(result);
            Intent intent = this.mFillInIntent;
            if (intent != null) {
                result.fillIn(intent, this.mFillInFlags);
            }
            result.fillIn(this.mSelectableTargetInfoCommunicator.getReferrerFillInIntent(), 0);
        }
        return result;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean start(Activity activity, Bundle options) {
        throw new RuntimeException("ChooserTargets should be started as caller.");
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean startAsCaller(ResolverActivity activity, Bundle options, int userId) {
        Intent intent = getBaseIntentToSend();
        boolean ignoreTargetSecurity = false;
        if (intent == null) {
            return false;
        }
        intent.setComponent(this.mChooserTarget.getComponentName());
        intent.putExtras(this.mChooserTarget.getIntentExtras());
        TargetInfo.prepareIntentForCrossProfileLaunch(intent, userId);
        DisplayResolveInfo displayResolveInfo = this.mSourceInfo;
        if (displayResolveInfo != null && displayResolveInfo.getResolvedComponentName().getPackageName().equals(this.mChooserTarget.getComponentName().getPackageName())) {
            ignoreTargetSecurity = true;
        }
        activity.startActivityAsCaller(intent, options, ignoreTargetSecurity, userId);
        return true;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean startAsUser(Activity activity, Bundle options, UserHandle user) {
        throw new RuntimeException("ChooserTargets should be started as caller.");
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public ResolveInfo getResolveInfo() {
        DisplayResolveInfo displayResolveInfo = this.mSourceInfo;
        return displayResolveInfo != null ? displayResolveInfo.getResolveInfo() : this.mBackupResolveInfo;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public CharSequence getDisplayLabel() {
        return this.mDisplayLabel;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public CharSequence getExtendedInfo() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public synchronized Drawable getDisplayIcon(Context context) {
        return this.mDisplayIcon;
    }

    public synchronized boolean hasDisplayIcon() {
        return this.mDisplayIcon != null;
    }

    @Override // com.android.internal.app.chooser.ChooserTargetInfo
    public ChooserTarget getChooserTarget() {
        return this.mChooserTarget;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public TargetInfo cloneFilledIn(Intent fillInIntent, int flags) {
        return new SelectableTargetInfo(this, fillInIntent, flags);
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public List<Intent> getAllSourceIntents() {
        List<Intent> results = new ArrayList<>();
        DisplayResolveInfo displayResolveInfo = this.mSourceInfo;
        if (displayResolveInfo != null) {
            results.add(displayResolveInfo.getAllSourceIntents().get(0));
        }
        return results;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean isPinned() {
        return this.mIsPinned;
    }
}
