package android.window;

import android.graphics.Rect;
import android.p008os.IBinder;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.InputWindowHandle;
import android.window.WindowInfosListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class WindowInfosListenerForTest {
    private static final String TAG = "WindowInfosListenerForTest";
    private ArrayMap<Consumer<List<WindowInfo>>, WindowInfosListener> mListeners = new ArrayMap<>();

    /* loaded from: classes4.dex */
    public static class WindowInfo {
        public final Rect bounds;
        public final String name;
        public final IBinder windowToken;

        WindowInfo(IBinder windowToken, String name, Rect bounds) {
            this.windowToken = windowToken;
            this.name = name;
            this.bounds = bounds;
        }
    }

    public void addWindowInfosListener(final Consumer<List<WindowInfo>> consumer) {
        final CountDownLatch calledWithInitialState = new CountDownLatch(1);
        WindowInfosListener windowInfosListener = new WindowInfosListener() { // from class: android.window.WindowInfosListenerForTest.1
            @Override // android.window.WindowInfosListener
            public void onWindowInfosChanged(InputWindowHandle[] windowHandles, WindowInfosListener.DisplayInfo[] displayInfos) {
                try {
                    calledWithInitialState.await();
                } catch (InterruptedException e) {
                    Log.m110e(WindowInfosListenerForTest.TAG, "Exception thrown while waiting for listener to be called with initial state");
                }
                consumer.accept(WindowInfosListenerForTest.buildWindowInfos(windowHandles));
            }
        };
        this.mListeners.put(consumer, windowInfosListener);
        Pair<InputWindowHandle[], WindowInfosListener.DisplayInfo[]> initialState = windowInfosListener.register();
        consumer.accept(buildWindowInfos(initialState.first));
        calledWithInitialState.countDown();
    }

    public void removeWindowInfosListener(Consumer<List<WindowInfo>> consumer) {
        WindowInfosListener listener = this.mListeners.remove(consumer);
        if (listener == null) {
            return;
        }
        listener.unregister();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<WindowInfo> buildWindowInfos(InputWindowHandle[] windowHandles) {
        ArrayList<WindowInfo> windowInfos = new ArrayList<>(windowHandles.length);
        for (InputWindowHandle handle : windowHandles) {
            if ((handle.inputConfig & 2) == 0) {
                Rect bounds = new Rect(handle.frameLeft, handle.frameTop, handle.frameRight, handle.frameBottom);
                windowInfos.add(new WindowInfo(handle.getWindowToken(), handle.name, bounds));
            }
        }
        return windowInfos;
    }
}
