package android.window;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.content.Context;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.util.Singleton;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes4.dex */
public interface SplashScreen {
    public static final int SPLASH_SCREEN_STYLE_ICON = 1;
    public static final int SPLASH_SCREEN_STYLE_SOLID_COLOR = 0;
    public static final int SPLASH_SCREEN_STYLE_UNDEFINED = -1;

    /* loaded from: classes4.dex */
    public interface OnExitAnimationListener {
        void onSplashScreenExit(SplashScreenView splashScreenView);
    }

    /* loaded from: classes4.dex */
    public @interface SplashScreenStyle {
    }

    void clearOnExitAnimationListener();

    void setOnExitAnimationListener(OnExitAnimationListener onExitAnimationListener);

    void setSplashScreenTheme(int i);

    /* loaded from: classes4.dex */
    public static class SplashScreenImpl implements SplashScreen {
        private static final String TAG = "SplashScreenImpl";
        private final IBinder mActivityToken;
        private OnExitAnimationListener mExitAnimationListener;
        private final SplashScreenManagerGlobal mGlobal = SplashScreenManagerGlobal.getInstance();

        public SplashScreenImpl(Context context) {
            this.mActivityToken = context.getActivityToken();
        }

        @Override // android.window.SplashScreen
        public void setOnExitAnimationListener(OnExitAnimationListener listener) {
            if (this.mActivityToken == null) {
                return;
            }
            synchronized (this.mGlobal.mGlobalLock) {
                if (listener != null) {
                    this.mExitAnimationListener = listener;
                    this.mGlobal.addImpl(this);
                }
            }
        }

        @Override // android.window.SplashScreen
        public void clearOnExitAnimationListener() {
            if (this.mActivityToken == null) {
                return;
            }
            synchronized (this.mGlobal.mGlobalLock) {
                this.mExitAnimationListener = null;
                this.mGlobal.removeImpl(this);
            }
        }

        @Override // android.window.SplashScreen
        public void setSplashScreenTheme(int themeId) {
            if (this.mActivityToken == null) {
                Log.m104w(TAG, "Couldn't persist the starting theme. This instance is not an Activity");
                return;
            }
            Activity activity = ActivityThread.currentActivityThread().getActivity(this.mActivityToken);
            if (activity == null) {
                return;
            }
            String themeName = themeId != 0 ? activity.getResources().getResourceName(themeId) : null;
            try {
                AppGlobals.getPackageManager().setSplashScreenTheme(activity.getComponentName().getPackageName(), themeName, activity.getUserId());
            } catch (RemoteException e) {
                Log.m103w(TAG, "Couldn't persist the starting theme", e);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class SplashScreenManagerGlobal {
        private static final String TAG = SplashScreen.class.getSimpleName();
        private static final Singleton<SplashScreenManagerGlobal> sInstance = new Singleton<SplashScreenManagerGlobal>() { // from class: android.window.SplashScreen.SplashScreenManagerGlobal.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.util.Singleton
            public SplashScreenManagerGlobal create() {
                return new SplashScreenManagerGlobal();
            }
        };
        private final Object mGlobalLock;
        private final ArrayList<SplashScreenImpl> mImpls;

        private SplashScreenManagerGlobal() {
            this.mGlobalLock = new Object();
            this.mImpls = new ArrayList<>();
            ActivityThread.currentActivityThread().registerSplashScreenManager(this);
        }

        public static SplashScreenManagerGlobal getInstance() {
            return sInstance.get();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addImpl(SplashScreenImpl impl) {
            synchronized (this.mGlobalLock) {
                this.mImpls.add(impl);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeImpl(SplashScreenImpl impl) {
            synchronized (this.mGlobalLock) {
                this.mImpls.remove(impl);
            }
        }

        private SplashScreenImpl findImpl(IBinder token) {
            synchronized (this.mGlobalLock) {
                Iterator<SplashScreenImpl> it = this.mImpls.iterator();
                while (it.hasNext()) {
                    SplashScreenImpl impl = it.next();
                    if (impl.mActivityToken == token) {
                        return impl;
                    }
                }
                return null;
            }
        }

        public void tokenDestroyed(IBinder token) {
            synchronized (this.mGlobalLock) {
                SplashScreenImpl impl = findImpl(token);
                if (impl != null) {
                    removeImpl(impl);
                }
            }
        }

        public void handOverSplashScreenView(IBinder token, SplashScreenView splashScreenView) {
            dispatchOnExitAnimation(token, splashScreenView);
        }

        private void dispatchOnExitAnimation(IBinder token, SplashScreenView view) {
            synchronized (this.mGlobalLock) {
                SplashScreenImpl impl = findImpl(token);
                if (impl == null) {
                    return;
                }
                if (impl.mExitAnimationListener == null) {
                    Slog.m96e(TAG, "cannot dispatch onExitAnimation to listener " + token);
                } else {
                    impl.mExitAnimationListener.onSplashScreenExit(view);
                }
            }
        }

        public boolean containsExitListener(IBinder token) {
            boolean z;
            synchronized (this.mGlobalLock) {
                SplashScreenImpl impl = findImpl(token);
                z = (impl == null || impl.mExitAnimationListener == null) ? false : true;
            }
            return z;
        }
    }
}
