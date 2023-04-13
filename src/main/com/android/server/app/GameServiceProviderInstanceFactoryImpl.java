package com.android.server.app;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.games.IGameService;
import android.service.games.IGameSessionService;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ScreenshotHelper;
import com.android.server.LocalServices;
import com.android.server.app.GameServiceConfiguration;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.p014wm.WindowManagerService;
import java.util.concurrent.Executor;
import java.util.function.Function;
/* loaded from: classes.dex */
public final class GameServiceProviderInstanceFactoryImpl implements GameServiceProviderInstanceFactory {
    public final Context mContext;

    public GameServiceProviderInstanceFactoryImpl(Context context) {
        this.mContext = context;
    }

    @Override // com.android.server.app.GameServiceProviderInstanceFactory
    public GameServiceProviderInstance create(GameServiceConfiguration.GameServiceComponentConfiguration gameServiceComponentConfiguration) {
        UserHandle userHandle = gameServiceComponentConfiguration.getUserHandle();
        IActivityTaskManager service = ActivityTaskManager.getService();
        Executor executor = BackgroundThread.getExecutor();
        Context context = this.mContext;
        return new GameServiceProviderInstanceImpl(userHandle, executor, context, new GameTaskInfoProvider(userHandle, service, new GameClassifierImpl(context.getPackageManager())), ActivityManager.getService(), (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class), service, (WindowManagerService) ServiceManager.getService("window"), (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class), (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class), new GameServiceConnector(this.mContext, gameServiceComponentConfiguration), new GameSessionServiceConnector(this.mContext, gameServiceComponentConfiguration), new ScreenshotHelper(this.mContext));
    }

    /* loaded from: classes.dex */
    public static final class GameServiceConnector extends ServiceConnector.Impl<IGameService> {
        public long getAutoDisconnectTimeoutMs() {
            return 0L;
        }

        public GameServiceConnector(Context context, GameServiceConfiguration.GameServiceComponentConfiguration gameServiceComponentConfiguration) {
            super(context, new Intent("android.service.games.action.GAME_SERVICE").setComponent(gameServiceComponentConfiguration.getGameServiceComponentName()), 1048576, gameServiceComponentConfiguration.getUserHandle().getIdentifier(), new Function() { // from class: com.android.server.app.GameServiceProviderInstanceFactoryImpl$GameServiceConnector$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return IGameService.Stub.asInterface((IBinder) obj);
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public static final class GameSessionServiceConnector extends ServiceConnector.Impl<IGameSessionService> {
        public long getAutoDisconnectTimeoutMs() {
            return 0L;
        }

        public GameSessionServiceConnector(Context context, GameServiceConfiguration.GameServiceComponentConfiguration gameServiceComponentConfiguration) {
            super(context, new Intent("android.service.games.action.GAME_SESSION_SERVICE").setComponent(gameServiceComponentConfiguration.getGameSessionServiceComponentName()), 135790592, gameServiceComponentConfiguration.getUserHandle().getIdentifier(), new Function() { // from class: com.android.server.app.GameServiceProviderInstanceFactoryImpl$GameSessionServiceConnector$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return IGameSessionService.Stub.asInterface((IBinder) obj);
                }
            });
        }
    }
}
