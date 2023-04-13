package android.test;

import android.content.Loader;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.ArrayBlockingQueue;
/* loaded from: classes.dex */
public class LoaderTestCase extends AndroidTestCase {
    static {
        new AsyncTask<Void, Void, Void>() { // from class: android.test.LoaderTestCase.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... args) {
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void result) {
            }
        };
    }

    public <T> T getLoaderResultSynchronously(final Loader<T> loader) {
        final ArrayBlockingQueue<T> queue = new ArrayBlockingQueue<>(1);
        final Loader.OnLoadCompleteListener<T> listener = new Loader.OnLoadCompleteListener<T>() { // from class: android.test.LoaderTestCase.2
            @Override // android.content.Loader.OnLoadCompleteListener
            public void onLoadComplete(Loader<T> completedLoader, T data) {
                completedLoader.unregisterListener(this);
                completedLoader.stopLoading();
                completedLoader.reset();
                queue.add(data);
            }
        };
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) { // from class: android.test.LoaderTestCase.3
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                loader.registerListener(0, listener);
                loader.startLoading();
            }
        };
        mainThreadHandler.sendEmptyMessage(0);
        try {
            T result = queue.take();
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException("waiting thread interrupted", e);
        }
    }
}
