package com.android.server.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.SystemService;
import com.android.server.app.GameServiceConfiguration;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class GameServiceController {
    @GuardedBy({"mLock"})
    public volatile GameServiceConfiguration.GameServiceComponentConfiguration mActiveGameServiceComponentConfiguration;
    @GuardedBy({"mLock"})
    public volatile String mActiveGameServiceProviderPackage;
    public final Executor mBackgroundExecutor;
    public final Context mContext;
    public volatile SystemService.TargetUser mCurrentForegroundUser;
    public BroadcastReceiver mGameServicePackageChangedReceiver;
    @GuardedBy({"mLock"})
    public volatile GameServiceProviderInstance mGameServiceProviderInstance;
    public final GameServiceProviderInstanceFactory mGameServiceProviderInstanceFactory;
    public volatile String mGameServiceProviderOverride;
    public final GameServiceProviderSelector mGameServiceProviderSelector;
    public volatile boolean mHasBootCompleted;
    public final Object mLock = new Object();

    public GameServiceController(Context context, Executor executor, GameServiceProviderSelector gameServiceProviderSelector, GameServiceProviderInstanceFactory gameServiceProviderInstanceFactory) {
        this.mContext = context;
        this.mGameServiceProviderInstanceFactory = gameServiceProviderInstanceFactory;
        this.mBackgroundExecutor = executor;
        this.mGameServiceProviderSelector = gameServiceProviderSelector;
    }

    public void onBootComplete() {
        if (this.mHasBootCompleted) {
            return;
        }
        this.mHasBootCompleted = true;
        this.mBackgroundExecutor.execute(new GameServiceController$$ExternalSyntheticLambda0(this));
    }

    public void notifyUserStarted(SystemService.TargetUser targetUser) {
        if (this.mCurrentForegroundUser != null) {
            return;
        }
        setCurrentForegroundUserAndEvaluateProvider(targetUser);
    }

    public void notifyNewForegroundUser(SystemService.TargetUser targetUser) {
        setCurrentForegroundUserAndEvaluateProvider(targetUser);
    }

    public void notifyUserUnlocking(SystemService.TargetUser targetUser) {
        if (this.mCurrentForegroundUser != null && this.mCurrentForegroundUser.getUserIdentifier() == targetUser.getUserIdentifier()) {
            this.mBackgroundExecutor.execute(new GameServiceController$$ExternalSyntheticLambda0(this));
        }
    }

    public void notifyUserStopped(SystemService.TargetUser targetUser) {
        if (this.mCurrentForegroundUser != null && this.mCurrentForegroundUser.getUserIdentifier() == targetUser.getUserIdentifier()) {
            setCurrentForegroundUserAndEvaluateProvider(null);
        }
    }

    public void setGameServiceProvider(String str) {
        if (!Objects.equals(this.mGameServiceProviderOverride, str)) {
            this.mGameServiceProviderOverride = str;
            this.mBackgroundExecutor.execute(new GameServiceController$$ExternalSyntheticLambda0(this));
        }
    }

    public final void setCurrentForegroundUserAndEvaluateProvider(SystemService.TargetUser targetUser) {
        if (!Objects.equals(this.mCurrentForegroundUser, targetUser)) {
            this.mCurrentForegroundUser = targetUser;
            this.mBackgroundExecutor.execute(new GameServiceController$$ExternalSyntheticLambda0(this));
        }
    }

    public final void evaluateActiveGameServiceProvider() {
        if (this.mHasBootCompleted) {
            synchronized (this.mLock) {
                GameServiceConfiguration gameServiceConfiguration = this.mGameServiceProviderSelector.get(this.mCurrentForegroundUser, this.mGameServiceProviderOverride);
                String packageName = gameServiceConfiguration == null ? null : gameServiceConfiguration.getPackageName();
                GameServiceConfiguration.GameServiceComponentConfiguration gameServiceComponentConfiguration = gameServiceConfiguration == null ? null : gameServiceConfiguration.getGameServiceComponentConfiguration();
                evaluateGameServiceProviderPackageChangedListenerLocked(packageName);
                if (!Objects.equals(gameServiceComponentConfiguration, this.mActiveGameServiceComponentConfiguration)) {
                    if (this.mGameServiceProviderInstance != null) {
                        Slog.i("GameServiceController", "Stopping Game Service provider: " + this.mActiveGameServiceComponentConfiguration);
                        this.mGameServiceProviderInstance.stop();
                        this.mGameServiceProviderInstance = null;
                    }
                    this.mActiveGameServiceComponentConfiguration = gameServiceComponentConfiguration;
                    if (this.mActiveGameServiceComponentConfiguration == null) {
                        return;
                    }
                    Slog.i("GameServiceController", "Starting Game Service provider: " + this.mActiveGameServiceComponentConfiguration);
                    this.mGameServiceProviderInstance = this.mGameServiceProviderInstanceFactory.create(this.mActiveGameServiceComponentConfiguration);
                    this.mGameServiceProviderInstance.start();
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void evaluateGameServiceProviderPackageChangedListenerLocked(String str) {
        if (TextUtils.equals(this.mActiveGameServiceProviderPackage, str)) {
            return;
        }
        BroadcastReceiver broadcastReceiver = this.mGameServicePackageChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mGameServicePackageChangedReceiver = null;
        }
        this.mActiveGameServiceProviderPackage = str;
        if (TextUtils.isEmpty(this.mActiveGameServiceProviderPackage)) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart(str, 0);
        PackageChangedBroadcastReceiver packageChangedBroadcastReceiver = new PackageChangedBroadcastReceiver(str);
        this.mGameServicePackageChangedReceiver = packageChangedBroadcastReceiver;
        this.mContext.registerReceiver(packageChangedBroadcastReceiver, intentFilter);
    }

    /* loaded from: classes.dex */
    public final class PackageChangedBroadcastReceiver extends BroadcastReceiver {
        public final String mPackageName;

        public PackageChangedBroadcastReceiver(String str) {
            GameServiceController.this = r1;
            this.mPackageName = str;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getData().getSchemeSpecificPart(), this.mPackageName)) {
                Executor executor = GameServiceController.this.mBackgroundExecutor;
                final GameServiceController gameServiceController = GameServiceController.this;
                executor.execute(new Runnable() { // from class: com.android.server.app.GameServiceController$PackageChangedBroadcastReceiver$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        GameServiceController.this.evaluateActiveGameServiceProvider();
                    }
                });
            }
        }
    }
}
