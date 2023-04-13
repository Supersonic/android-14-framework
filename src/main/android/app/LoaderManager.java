package android.app;

import android.content.Loader;
import android.p008os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
@Deprecated
/* loaded from: classes.dex */
public abstract class LoaderManager {

    @Deprecated
    /* loaded from: classes.dex */
    public interface LoaderCallbacks<D> {
        Loader<D> onCreateLoader(int i, Bundle bundle);

        void onLoadFinished(Loader<D> loader, D d);

        void onLoaderReset(Loader<D> loader);
    }

    public abstract void destroyLoader(int i);

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public abstract <D> Loader<D> getLoader(int i);

    public abstract <D> Loader<D> initLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks);

    public abstract <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks);

    public static void enableDebugLogging(boolean enabled) {
        LoaderManagerImpl.DEBUG = enabled;
    }

    public FragmentHostCallback getFragmentHostCallback() {
        return null;
    }
}
