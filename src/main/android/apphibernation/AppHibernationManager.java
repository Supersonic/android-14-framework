package android.apphibernation;

import android.annotation.SystemApi;
import android.apphibernation.IAppHibernationService;
import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
@SystemApi
/* loaded from: classes.dex */
public class AppHibernationManager {
    private static final String TAG = "AppHibernationManager";
    private final Context mContext;
    private final IAppHibernationService mIAppHibernationService = IAppHibernationService.Stub.asInterface(ServiceManager.getService(Context.APP_HIBERNATION_SERVICE));

    public AppHibernationManager(Context context) {
        this.mContext = context;
    }

    @SystemApi
    public boolean isHibernatingForUser(String packageName) {
        try {
            return this.mIAppHibernationService.isHibernatingForUser(packageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setHibernatingForUser(String packageName, boolean isHibernating) {
        try {
            this.mIAppHibernationService.setHibernatingForUser(packageName, this.mContext.getUserId(), isHibernating);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isHibernatingGlobally(String packageName) {
        try {
            return this.mIAppHibernationService.isHibernatingGlobally(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setHibernatingGlobally(String packageName, boolean isHibernating) {
        try {
            this.mIAppHibernationService.setHibernatingGlobally(packageName, isHibernating);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<String> getHibernatingPackagesForUser() {
        try {
            return this.mIAppHibernationService.getHibernatingPackagesForUser(this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public Map<String, HibernationStats> getHibernationStatsForUser(Set<String> packageNames) {
        try {
            return this.mIAppHibernationService.getHibernationStatsForUser(new ArrayList(packageNames), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public Map<String, HibernationStats> getHibernationStatsForUser() {
        try {
            return this.mIAppHibernationService.getHibernationStatsForUser(null, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isOatArtifactDeletionEnabled() {
        try {
            return this.mIAppHibernationService.isOatArtifactDeletionEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
