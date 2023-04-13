package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.HardwareRenderer;
import android.graphics.Picture;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Debug;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import libcore.util.HexEncoding;
/* loaded from: classes4.dex */
public class ViewDebug {
    private static final int CAPTURE_TIMEOUT = 6000;
    public static final boolean DEBUG_DRAG = false;
    public static final boolean DEBUG_POSITIONING = false;
    private static final String REMOTE_COMMAND_CAPTURE = "CAPTURE";
    private static final String REMOTE_COMMAND_CAPTURE_LAYERS = "CAPTURE_LAYERS";
    private static final String REMOTE_COMMAND_DUMP = "DUMP";
    public static final String REMOTE_COMMAND_DUMP_ENCODED = "DUMP_ENCODED";
    private static final String REMOTE_COMMAND_DUMP_THEME = "DUMP_THEME";
    private static final String REMOTE_COMMAND_INVALIDATE = "INVALIDATE";
    private static final String REMOTE_COMMAND_OUTPUT_DISPLAYLIST = "OUTPUT_DISPLAYLIST";
    private static final String REMOTE_COMMAND_REQUEST_LAYOUT = "REQUEST_LAYOUT";
    private static final String REMOTE_PROFILE = "PROFILE";
    @Deprecated
    public static final boolean TRACE_HIERARCHY = false;
    @Deprecated
    public static final boolean TRACE_RECYCLER = false;
    private static HashMap<Class<?>, PropertyInfo<CapturedViewProperty, ?>[]> sCapturedViewProperties;
    private static HashMap<Class<?>, PropertyInfo<ExportedProperty, ?>[]> sExportProperties;

    /* loaded from: classes4.dex */
    public interface CanvasProvider {
        Bitmap createBitmap();

        Canvas getCanvas(View view, int i, int i2);
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: classes4.dex */
    public @interface CapturedViewProperty {
        boolean retrieveReturn() default false;
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: classes4.dex */
    public @interface ExportedProperty {
        String category() default "";

        boolean deepExport() default false;

        FlagToString[] flagMapping() default {};

        boolean formatToHexString() default false;

        boolean hasAdjacentMapping() default false;

        IntToString[] indexMapping() default {};

        IntToString[] mapping() default {};

        String prefix() default "";

        boolean resolveId() default false;
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: classes4.dex */
    public @interface FlagToString {
        int equals();

        int mask();

        String name();

        boolean outputIf() default true;
    }

    /* loaded from: classes4.dex */
    public interface HierarchyHandler {
        void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i);

        View findHierarchyView(String str, int i);
    }

    @Deprecated
    /* loaded from: classes4.dex */
    public enum HierarchyTraceType {
        INVALIDATE,
        INVALIDATE_CHILD,
        INVALIDATE_CHILD_IN_PARENT,
        REQUEST_LAYOUT,
        ON_LAYOUT,
        ON_MEASURE,
        DRAW,
        BUILD_CACHE
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: classes4.dex */
    public @interface IntToString {
        int from();

        /* renamed from: to */
        String m86to();
    }

    @Deprecated
    /* loaded from: classes4.dex */
    public enum RecyclerTraceType {
        NEW_VIEW,
        BIND_VIEW,
        RECYCLE_FROM_ACTIVE_HEAP,
        RECYCLE_FROM_SCRAP_HEAP,
        MOVE_TO_SCRAP_HEAP,
        MOVE_FROM_ACTIVE_TO_SCRAP_HEAP
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static abstract class PropertyInfo<T extends Annotation, R extends AccessibleObject & Member> {
        public final R member;
        public final String name;
        public final T property;
        public final Class<?> returnType;
        public String entrySuffix = "";
        public String valueSuffix = "";

        public abstract Object invoke(Object obj) throws Exception;

        PropertyInfo(Class<T> property, R member, Class<?> returnType) {
            this.member = member;
            this.name = member.getName();
            this.property = (T) member.getAnnotation(property);
            this.returnType = returnType;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static <T extends Annotation> PropertyInfo<T, ?> forMethod(Method method, Class<T> property) {
            try {
                if (method.getReturnType() != Void.class) {
                    if (method.getParameterTypes().length != 0 || !method.isAnnotationPresent(property)) {
                        return null;
                    }
                    method.setAccessible(true);
                    PropertyInfo info = new MethodPI(method, property);
                    info.entrySuffix = "()";
                    info.valueSuffix = NavigationBarInflaterView.GRAVITY_SEPARATOR;
                    return info;
                }
                return null;
            } catch (NoClassDefFoundError e) {
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static <T extends Annotation> PropertyInfo<T, ?> forField(Field field, Class<T> property) {
            if (!field.isAnnotationPresent(property)) {
                return null;
            }
            field.setAccessible(true);
            return new FieldPI(field, property);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class MethodPI<T extends Annotation> extends PropertyInfo<T, Method> {
        MethodPI(Method method, Class<T> property) {
            super(property, method, method.getReturnType());
        }

        @Override // android.view.ViewDebug.PropertyInfo
        public Object invoke(Object target) throws Exception {
            return ((Method) this.member).invoke(target, new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class FieldPI<T extends Annotation> extends PropertyInfo<T, Field> {
        FieldPI(Field field, Class<T> property) {
            super(property, field, field.getType());
        }

        @Override // android.view.ViewDebug.PropertyInfo
        public Object invoke(Object target) throws Exception {
            return ((Field) this.member).get(target);
        }
    }

    public static long getViewInstanceCount() {
        return Debug.countInstancesOfClass(View.class);
    }

    public static long getViewRootImplCount() {
        return Debug.countInstancesOfClass(ViewRootImpl.class);
    }

    @Deprecated
    public static void trace(View view, RecyclerTraceType type, int... parameters) {
    }

    @Deprecated
    public static void startRecyclerTracing(String prefix, View view) {
    }

    @Deprecated
    public static void stopRecyclerTracing() {
    }

    @Deprecated
    public static void trace(View view, HierarchyTraceType type) {
    }

    @Deprecated
    public static void startHierarchyTracing(String prefix, View view) {
    }

    @Deprecated
    public static void stopHierarchyTracing() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dispatchCommand(View view, String command, String parameters, OutputStream clientStream) throws IOException {
        View view2 = view.getRootView();
        if (REMOTE_COMMAND_DUMP.equalsIgnoreCase(command)) {
            dump(view2, false, true, clientStream);
        } else if (REMOTE_COMMAND_DUMP_THEME.equalsIgnoreCase(command)) {
            dumpTheme(view2, clientStream);
        } else if (REMOTE_COMMAND_DUMP_ENCODED.equalsIgnoreCase(command)) {
            dumpEncoded(view2, clientStream);
        } else if (REMOTE_COMMAND_CAPTURE_LAYERS.equalsIgnoreCase(command)) {
            captureLayers(view2, new DataOutputStream(clientStream));
        } else {
            String[] params = parameters.split(" ");
            if (REMOTE_COMMAND_CAPTURE.equalsIgnoreCase(command)) {
                capture(view2, clientStream, params[0]);
            } else if (REMOTE_COMMAND_OUTPUT_DISPLAYLIST.equalsIgnoreCase(command)) {
                outputDisplayList(view2, params[0]);
            } else if (REMOTE_COMMAND_INVALIDATE.equalsIgnoreCase(command)) {
                invalidate(view2, params[0]);
            } else if (REMOTE_COMMAND_REQUEST_LAYOUT.equalsIgnoreCase(command)) {
                requestLayout(view2, params[0]);
            } else if (REMOTE_PROFILE.equalsIgnoreCase(command)) {
                profile(view2, clientStream, params[0]);
            }
        }
    }

    public static View findView(View root, String parameter) {
        if (parameter.indexOf(64) != -1) {
            String[] ids = parameter.split("@");
            String className = ids[0];
            int hashCode = (int) Long.parseLong(ids[1], 16);
            View view = root.getRootView();
            if (view instanceof ViewGroup) {
                return findView((ViewGroup) view, className, hashCode);
            }
            return null;
        }
        int id = root.getResources().getIdentifier(parameter, null, null);
        return root.getRootView().findViewById(id);
    }

    private static void invalidate(View root, String parameter) {
        View view = findView(root, parameter);
        if (view != null) {
            view.postInvalidate();
        }
    }

    private static void requestLayout(View root, String parameter) {
        final View view = findView(root, parameter);
        if (view != null) {
            root.post(new Runnable() { // from class: android.view.ViewDebug.1
                @Override // java.lang.Runnable
                public void run() {
                    View.this.requestLayout();
                }
            });
        }
    }

    private static void profile(View root, OutputStream clientStream, String parameter) throws IOException {
        View view = findView(root, parameter);
        BufferedWriter out = null;
        try {
            try {
                out = new BufferedWriter(new OutputStreamWriter(clientStream), 32768);
                if (view != null) {
                    profileViewAndChildren(view, out);
                } else {
                    out.write("-1 -1 -1");
                    out.newLine();
                }
                out.write("DONE.");
                out.newLine();
            } catch (Exception e) {
                Log.m103w("View", "Problem profiling the view:", e);
                if (out == null) {
                    return;
                }
            }
            out.close();
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static void profileViewAndChildren(View view, BufferedWriter out) throws IOException {
        RenderNode node = RenderNode.create("ViewDebug", null);
        profileViewAndChildren(view, node, out, true);
    }

    private static void profileViewAndChildren(View view, RenderNode node, BufferedWriter out, boolean root) throws IOException {
        long durationDraw = 0;
        long durationMeasure = (root || (view.mPrivateFlags & 2048) != 0) ? profileViewMeasure(view) : 0L;
        long durationLayout = (root || (view.mPrivateFlags & 8192) != 0) ? profileViewLayout(view) : 0L;
        if (root || !view.willNotDraw() || (view.mPrivateFlags & 32) != 0) {
            durationDraw = profileViewDraw(view, node);
        }
        out.write(String.valueOf(durationMeasure));
        out.write(32);
        out.write(String.valueOf(durationLayout));
        out.write(32);
        out.write(String.valueOf(durationDraw));
        out.newLine();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                profileViewAndChildren(group.getChildAt(i), node, out, false);
            }
        }
    }

    private static long profileViewMeasure(final View view) {
        return profileViewOperation(view, new ViewOperation() { // from class: android.view.ViewDebug.2
            @Override // android.view.ViewDebug.ViewOperation
            public void pre() {
                forceLayout(View.this);
            }

            private void forceLayout(View view2) {
                view2.forceLayout();
                if (view2 instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view2;
                    int count = group.getChildCount();
                    for (int i = 0; i < count; i++) {
                        forceLayout(group.getChildAt(i));
                    }
                }
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run() {
                View view2 = View.this;
                view2.measure(view2.mOldWidthMeasureSpec, View.this.mOldHeightMeasureSpec);
            }
        });
    }

    private static long profileViewLayout(final View view) {
        return profileViewOperation(view, new ViewOperation() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda7
            @Override // android.view.ViewDebug.ViewOperation
            public final void run() {
                r0.layout(r0.mLeft, r0.mTop, r0.mRight, View.this.mBottom);
            }
        });
    }

    private static long profileViewDraw(final View view, RenderNode node) {
        DisplayMetrics dm = view.getResources().getDisplayMetrics();
        if (dm == null) {
            return 0L;
        }
        if (view.isHardwareAccelerated()) {
            final RecordingCanvas canvas = node.beginRecording(dm.widthPixels, dm.heightPixels);
            try {
                return profileViewOperation(view, new ViewOperation() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda5
                    @Override // android.view.ViewDebug.ViewOperation
                    public final void run() {
                        View.this.draw(canvas);
                    }
                });
            } finally {
                node.endRecording();
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(dm, dm.widthPixels, dm.heightPixels, Bitmap.Config.RGB_565);
        final Canvas canvas2 = new Canvas(bitmap);
        try {
            return profileViewOperation(view, new ViewOperation() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda6
                @Override // android.view.ViewDebug.ViewOperation
                public final void run() {
                    View.this.draw(canvas2);
                }
            });
        } finally {
            canvas2.setBitmap(null);
            bitmap.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public interface ViewOperation {
        void run();

        default void pre() {
        }
    }

    private static long profileViewOperation(View view, final ViewOperation operation) {
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] duration = new long[1];
        view.post(new Runnable() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ViewDebug.lambda$profileViewOperation$3(ViewDebug.ViewOperation.this, duration, latch);
            }
        });
        try {
            if (!latch.await(6000L, TimeUnit.MILLISECONDS)) {
                Log.m104w("View", "Could not complete the profiling of the view " + view);
                return -1L;
            }
            return duration[0];
        } catch (InterruptedException e) {
            Log.m104w("View", "Could not complete the profiling of the view " + view);
            Thread.currentThread().interrupt();
            return -1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$profileViewOperation$3(ViewOperation operation, long[] duration, CountDownLatch latch) {
        try {
            operation.pre();
            long start = Debug.threadCpuTimeNanos();
            operation.run();
            duration[0] = Debug.threadCpuTimeNanos() - start;
        } finally {
            latch.countDown();
        }
    }

    public static void captureLayers(View root, DataOutputStream clientStream) throws IOException {
        try {
            Rect outRect = new Rect();
            root.mAttachInfo.mViewRootImpl.getDisplayFrame(outRect);
            clientStream.writeInt(outRect.width());
            clientStream.writeInt(outRect.height());
            captureViewLayer(root, clientStream, true);
            clientStream.write(2);
        } finally {
            clientStream.close();
        }
    }

    private static void captureViewLayer(View view, DataOutputStream clientStream, boolean visible) throws IOException {
        boolean localVisible = view.getVisibility() == 0 && visible;
        if ((view.mPrivateFlags & 128) != 128) {
            int id = view.getId();
            String name = view.getClass().getSimpleName();
            if (id != -1) {
                name = resolveId(view.getContext(), id).toString();
            }
            clientStream.write(1);
            clientStream.writeUTF(name);
            clientStream.writeByte(localVisible ? 1 : 0);
            int[] position = new int[2];
            view.getLocationInWindow(position);
            clientStream.writeInt(position[0]);
            clientStream.writeInt(position[1]);
            clientStream.flush();
            Bitmap b = performViewCapture(view, true);
            if (b != null) {
                ByteArrayOutputStream arrayOut = new ByteArrayOutputStream(b.getWidth() * b.getHeight() * 2);
                b.compress(Bitmap.CompressFormat.PNG, 100, arrayOut);
                clientStream.writeInt(arrayOut.size());
                arrayOut.writeTo(clientStream);
            }
            clientStream.flush();
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                captureViewLayer(group.getChildAt(i), clientStream, localVisible);
            }
        }
        if (view.mOverlay != null) {
            ViewGroup overlayContainer = view.getOverlay().mOverlayViewGroup;
            captureViewLayer(overlayContainer, clientStream, localVisible);
        }
    }

    private static void outputDisplayList(View root, String parameter) throws IOException {
        View view = findView(root, parameter);
        view.getViewRootImpl().outputDisplayList(view);
    }

    public static void outputDisplayList(View root, View target) {
        root.getViewRootImpl().outputDisplayList(target);
    }

    /* loaded from: classes4.dex */
    private static class PictureCallbackHandler implements AutoCloseable, HardwareRenderer.PictureCapturedCallback, Runnable {
        private final Function<Picture, Boolean> mCallback;
        private final Executor mExecutor;
        private final ReentrantLock mLock;
        private final ArrayDeque<Picture> mQueue;
        private Thread mRenderThread;
        private final HardwareRenderer mRenderer;
        private boolean mStopListening;

        private PictureCallbackHandler(HardwareRenderer renderer, Function<Picture, Boolean> callback, Executor executor) {
            this.mLock = new ReentrantLock(false);
            this.mQueue = new ArrayDeque<>(3);
            this.mRenderer = renderer;
            this.mCallback = callback;
            this.mExecutor = executor;
            renderer.setPictureCaptureCallback(this);
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.mLock.lock();
            this.mStopListening = true;
            this.mLock.unlock();
            this.mRenderer.setPictureCaptureCallback(null);
        }

        @Override // android.graphics.HardwareRenderer.PictureCapturedCallback
        public void onPictureCaptured(Picture picture) {
            this.mLock.lock();
            if (this.mStopListening) {
                this.mLock.unlock();
                this.mRenderer.setPictureCaptureCallback(null);
                return;
            }
            if (this.mRenderThread == null) {
                this.mRenderThread = Thread.currentThread();
            }
            Picture toDestroy = null;
            if (this.mQueue.size() == 3) {
                Picture toDestroy2 = this.mQueue.removeLast();
                toDestroy = toDestroy2;
            }
            this.mQueue.add(picture);
            this.mLock.unlock();
            if (toDestroy == null) {
                this.mExecutor.execute(this);
            } else {
                toDestroy.close();
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mLock.lock();
            Picture picture = this.mQueue.poll();
            boolean isStopped = this.mStopListening;
            this.mLock.unlock();
            if (Thread.currentThread() == this.mRenderThread) {
                close();
                throw new IllegalStateException("ViewDebug#startRenderingCommandsCapture must be given an executor that invokes asynchronously");
            } else if (isStopped) {
                picture.close();
            } else {
                boolean keepReceiving = this.mCallback.apply(picture).booleanValue();
                if (!keepReceiving) {
                    close();
                }
            }
        }
    }

    @Deprecated
    public static AutoCloseable startRenderingCommandsCapture(View tree, Executor executor, Function<Picture, Boolean> callback) {
        View.AttachInfo attachInfo = tree.mAttachInfo;
        if (attachInfo == null) {
            throw new IllegalArgumentException("Given view isn't attached");
        }
        if (attachInfo.mHandler.getLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Called on the wrong thread. Must be called on the thread that owns the given View");
        }
        HardwareRenderer renderer = attachInfo.mThreadedRenderer;
        if (renderer == null) {
            return null;
        }
        return new PictureCallbackHandler(renderer, callback, executor);
    }

    /* loaded from: classes4.dex */
    private static class StreamingPictureCallbackHandler implements AutoCloseable, HardwareRenderer.PictureCapturedCallback, Runnable {
        private final Callable<OutputStream> mCallback;
        private final Executor mExecutor;
        private final ReentrantLock mLock;
        private final ArrayDeque<Picture> mQueue;
        private Thread mRenderThread;
        private final HardwareRenderer mRenderer;
        private boolean mStopListening;

        private StreamingPictureCallbackHandler(HardwareRenderer renderer, Callable<OutputStream> callback, Executor executor) {
            this.mLock = new ReentrantLock(false);
            this.mQueue = new ArrayDeque<>(3);
            this.mRenderer = renderer;
            this.mCallback = callback;
            this.mExecutor = executor;
            renderer.setPictureCaptureCallback(this);
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.mLock.lock();
            this.mStopListening = true;
            this.mLock.unlock();
            this.mRenderer.setPictureCaptureCallback(null);
        }

        @Override // android.graphics.HardwareRenderer.PictureCapturedCallback
        public void onPictureCaptured(Picture picture) {
            this.mLock.lock();
            if (this.mStopListening) {
                this.mLock.unlock();
                this.mRenderer.setPictureCaptureCallback(null);
                return;
            }
            if (this.mRenderThread == null) {
                this.mRenderThread = Thread.currentThread();
            }
            boolean needsInvoke = true;
            if (this.mQueue.size() == 3) {
                this.mQueue.removeLast();
                needsInvoke = false;
            }
            this.mQueue.add(picture);
            this.mLock.unlock();
            if (needsInvoke) {
                this.mExecutor.execute(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mLock.lock();
            Picture picture = this.mQueue.poll();
            boolean isStopped = this.mStopListening;
            this.mLock.unlock();
            if (Thread.currentThread() == this.mRenderThread) {
                close();
                throw new IllegalStateException("ViewDebug#startRenderingCommandsCapture must be given an executor that invokes asynchronously");
            } else if (isStopped) {
            } else {
                OutputStream stream = null;
                try {
                    stream = this.mCallback.call();
                } catch (Exception ex) {
                    Log.m103w("ViewDebug", "Aborting rendering commands capture because callback threw exception", ex);
                }
                if (stream != null) {
                    try {
                        picture.writeToStream(stream);
                        stream.flush();
                        return;
                    } catch (IOException ex2) {
                        Log.m103w("ViewDebug", "Aborting rendering commands capture due to IOException writing to output stream", ex2);
                        return;
                    }
                }
                close();
            }
        }
    }

    public static AutoCloseable startRenderingCommandsCapture(View tree, Executor executor, Callable<OutputStream> callback) {
        View.AttachInfo attachInfo = tree.mAttachInfo;
        if (attachInfo == null) {
            throw new IllegalArgumentException("Given view isn't attached");
        }
        if (attachInfo.mHandler.getLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Called on the wrong thread. Must be called on the thread that owns the given View");
        }
        HardwareRenderer renderer = attachInfo.mThreadedRenderer;
        if (renderer == null) {
            return null;
        }
        return new StreamingPictureCallbackHandler(renderer, callback, executor);
    }

    private static void capture(View root, OutputStream clientStream, String parameter) throws IOException {
        View captureView = findView(root, parameter);
        capture(root, clientStream, captureView);
    }

    public static void capture(View root, OutputStream clientStream, View captureView) throws IOException {
        Bitmap b = performViewCapture(captureView, false);
        if (b == null) {
            Log.m104w("View", "Failed to create capture bitmap!");
            b = Bitmap.createBitmap(root.getResources().getDisplayMetrics(), 1, 1, Bitmap.Config.ARGB_8888);
        }
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(clientStream, 32768);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            b.recycle();
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            b.recycle();
            throw th;
        }
    }

    private static Bitmap performViewCapture(final View captureView, final boolean skipChildren) {
        if (captureView != null) {
            final CountDownLatch latch = new CountDownLatch(1);
            final Bitmap[] cache = new Bitmap[1];
            captureView.post(new Runnable() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ViewDebug.lambda$performViewCapture$4(View.this, cache, skipChildren, latch);
                }
            });
            try {
                latch.await(6000L, TimeUnit.MILLISECONDS);
                return cache[0];
            } catch (InterruptedException e) {
                Log.m104w("View", "Could not complete the capture of the view " + captureView);
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$performViewCapture$4(View captureView, Bitmap[] cache, boolean skipChildren, CountDownLatch latch) {
        try {
            try {
                CanvasProvider provider = captureView.isHardwareAccelerated() ? new HardwareCanvasProvider() : new SoftwareCanvasProvider();
                cache[0] = captureView.createSnapshot(provider, skipChildren);
            } catch (OutOfMemoryError e) {
                Log.m104w("View", "Out of memory for bitmap");
            }
        } finally {
            latch.countDown();
        }
    }

    @Deprecated
    public static void dump(View root, boolean skipChildren, boolean includeProperties, OutputStream clientStream) throws IOException {
        BufferedWriter out = null;
        try {
            try {
                out = new BufferedWriter(new OutputStreamWriter(clientStream, "utf-8"), 32768);
                View view = root.getRootView();
                if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    dumpViewHierarchy(group.getContext(), group, out, 0, skipChildren, includeProperties);
                }
                out.write("DONE.");
                out.newLine();
            } catch (Exception e) {
                Log.m103w("View", "Problem dumping the view:", e);
                if (out == null) {
                    return;
                }
            }
            out.close();
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static void dumpv2(final View view, ByteArrayOutputStream out) throws InterruptedException {
        final ViewHierarchyEncoder encoder = new ViewHierarchyEncoder(out);
        final CountDownLatch latch = new CountDownLatch(1);
        view.post(new Runnable() { // from class: android.view.ViewDebug.3
            @Override // java.lang.Runnable
            public void run() {
                ViewHierarchyEncoder.this.addProperty("window:left", view.mAttachInfo.mWindowLeft);
                ViewHierarchyEncoder.this.addProperty("window:top", view.mAttachInfo.mWindowTop);
                view.encode(ViewHierarchyEncoder.this);
                latch.countDown();
            }
        });
        latch.await(2L, TimeUnit.SECONDS);
        encoder.endStream();
    }

    private static void dumpEncoded(View view, OutputStream out) throws IOException {
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        ViewHierarchyEncoder encoder = new ViewHierarchyEncoder(baOut);
        encoder.setUserPropertiesEnabled(false);
        encoder.addProperty("window:left", view.mAttachInfo.mWindowLeft);
        encoder.addProperty("window:top", view.mAttachInfo.mWindowTop);
        view.encode(encoder);
        encoder.endStream();
        out.write(baOut.toByteArray());
    }

    public static void dumpTheme(View view, OutputStream clientStream) throws IOException {
        BufferedWriter out = null;
        try {
            try {
                out = new BufferedWriter(new OutputStreamWriter(clientStream, "utf-8"), 32768);
                String[] attributes = getStyleAttributesDump(view.getContext().getResources(), view.getContext().getTheme());
                if (attributes != null) {
                    for (int i = 0; i < attributes.length; i += 2) {
                        if (attributes[i] != null) {
                            out.write(attributes[i] + "\n");
                            out.write(attributes[i + 1] + "\n");
                        }
                    }
                }
                out.write("DONE.");
                out.newLine();
            } catch (Exception e) {
                Log.m103w("View", "Problem dumping View Theme:", e);
                if (out == null) {
                    return;
                }
            }
            out.close();
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    private static String[] getStyleAttributesDump(Resources resources, Resources.Theme theme) {
        String str;
        TypedValue outValue = new TypedValue();
        int i = 0;
        int[] attributes = theme.getAllAttributes();
        String[] data = new String[attributes.length * 2];
        for (int attributeId : attributes) {
            try {
                data[i] = resources.getResourceName(attributeId);
                int i2 = i + 1;
                if (!theme.resolveAttribute(attributeId, outValue, true)) {
                    str = "null";
                } else {
                    str = outValue.coerceToString().toString();
                }
                data[i2] = str;
                i += 2;
                if (outValue.type == 1) {
                    data[i - 1] = resources.getResourceName(outValue.resourceId);
                }
            } catch (Resources.NotFoundException e) {
            }
        }
        return data;
    }

    private static View findView(ViewGroup group, String className, int hashCode) {
        View found;
        View found2;
        if (isRequestedView(group, className, hashCode)) {
            return group;
        }
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                View found3 = findView((ViewGroup) view, className, hashCode);
                if (found3 != null) {
                    return found3;
                }
            } else if (isRequestedView(view, className, hashCode)) {
                return view;
            }
            if (view.mOverlay != null && (found2 = findView(view.mOverlay.mOverlayViewGroup, className, hashCode)) != null) {
                return found2;
            }
            if ((view instanceof HierarchyHandler) && (found = ((HierarchyHandler) view).findHierarchyView(className, hashCode)) != null) {
                return found;
            }
        }
        return null;
    }

    private static boolean isRequestedView(View view, String className, int hashCode) {
        if (view.hashCode() == hashCode) {
            String viewClassName = view.getClass().getName();
            if (className.equals("ViewOverlay")) {
                return viewClassName.equals("android.view.ViewOverlay$OverlayViewGroup");
            }
            return className.equals(viewClassName);
        }
        return false;
    }

    private static void dumpViewHierarchy(final Context context, final ViewGroup group, final BufferedWriter out, final int level, final boolean skipChildren, final boolean includeProperties) {
        cacheExportedProperties(group.getClass());
        if (!skipChildren) {
            cacheExportedPropertiesForChildren(group);
        }
        Handler handler = group.getHandler();
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (handler.getLooper() == Looper.myLooper()) {
            dumpViewHierarchyOnUIThread(context, group, out, level, skipChildren, includeProperties);
            return;
        }
        FutureTask task = new FutureTask(new Runnable() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ViewDebug.dumpViewHierarchyOnUIThread(Context.this, group, out, level, skipChildren, includeProperties);
            }
        }, null);
        Message msg = Message.obtain(handler, task);
        msg.setAsynchronous(true);
        handler.sendMessage(msg);
        while (true) {
            try {
                task.get(6000L, TimeUnit.MILLISECONDS);
                return;
            } catch (InterruptedException e) {
            } catch (ExecutionException | TimeoutException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    private static void cacheExportedPropertiesForChildren(ViewGroup group) {
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = group.getChildAt(i);
            cacheExportedProperties(view.getClass());
            if (view instanceof ViewGroup) {
                cacheExportedPropertiesForChildren((ViewGroup) view);
            }
        }
    }

    private static void cacheExportedProperties(Class<?> klass) {
        PropertyInfo<ExportedProperty, ?>[] exportedProperties;
        HashMap<Class<?>, PropertyInfo<ExportedProperty, ?>[]> hashMap = sExportProperties;
        if (hashMap != null && hashMap.containsKey(klass)) {
            return;
        }
        do {
            for (PropertyInfo<ExportedProperty, ?> info : getExportedProperties(klass)) {
                if (!info.returnType.isPrimitive() && info.property.deepExport()) {
                    cacheExportedProperties(info.returnType);
                }
            }
            klass = klass.getSuperclass();
        } while (klass != Object.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void dumpViewHierarchyOnUIThread(Context context, ViewGroup group, BufferedWriter out, int level, boolean skipChildren, boolean includeProperties) {
        if (!dumpView(context, group, out, level, includeProperties) || skipChildren) {
            return;
        }
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = group.getChildAt(i);
            if (!(view instanceof ViewGroup)) {
                dumpView(context, view, out, level + 1, includeProperties);
            } else {
                dumpViewHierarchyOnUIThread(context, (ViewGroup) view, out, level + 1, skipChildren, includeProperties);
            }
            if (view.mOverlay != null) {
                ViewOverlay overlay = view.getOverlay();
                ViewGroup overlayContainer = overlay.mOverlayViewGroup;
                dumpViewHierarchyOnUIThread(context, overlayContainer, out, level + 2, skipChildren, includeProperties);
            }
        }
        if (group instanceof HierarchyHandler) {
            ((HierarchyHandler) group).dumpViewHierarchyWithProperties(out, level + 1);
        }
    }

    private static boolean dumpView(Context context, View view, BufferedWriter out, int level, boolean includeProperties) {
        for (int i = 0; i < level; i++) {
            try {
                out.write(32);
            } catch (IOException e) {
                Log.m104w("View", "Error while dumping hierarchy tree");
                return false;
            }
        }
        String className = view.getClass().getName();
        if (className.equals("android.view.ViewOverlay$OverlayViewGroup")) {
            className = "ViewOverlay";
        }
        out.write(className);
        out.write(64);
        out.write(Integer.toHexString(view.hashCode()));
        out.write(32);
        if (includeProperties) {
            dumpViewProperties(context, view, out);
        }
        out.newLine();
        return true;
    }

    private static <T extends Annotation> PropertyInfo<T, ?>[] convertToPropertyInfos(Method[] methods, Field[] fields, final Class<T> property) {
        return (PropertyInfo[]) Stream.of((Object[]) new Stream[]{Arrays.stream(methods).map(new Function() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ViewDebug.PropertyInfo forMethod;
                forMethod = ViewDebug.PropertyInfo.forMethod((Method) obj, property);
                return forMethod;
            }
        }), Arrays.stream(fields).map(new Function() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ViewDebug.PropertyInfo forField;
                forField = ViewDebug.PropertyInfo.forField((Field) obj, property);
                return forField;
            }
        })}).flatMap(Function.identity()).filter(new Predicate() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ViewDebug.lambda$convertToPropertyInfos$8(obj);
            }
        }).toArray(new IntFunction() { // from class: android.view.ViewDebug$$ExternalSyntheticLambda4
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return ViewDebug.lambda$convertToPropertyInfos$9(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$convertToPropertyInfos$8(Object i) {
        return i != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ PropertyInfo[] lambda$convertToPropertyInfos$9(int x$0) {
        return new PropertyInfo[x$0];
    }

    private static PropertyInfo<ExportedProperty, ?>[] getExportedProperties(Class<?> klass) {
        if (sExportProperties == null) {
            sExportProperties = new HashMap<>();
        }
        HashMap<Class<?>, PropertyInfo<ExportedProperty, ?>[]> map = sExportProperties;
        PropertyInfo<ExportedProperty, ?>[] properties = sExportProperties.get(klass);
        if (properties == null) {
            PropertyInfo<ExportedProperty, ?>[] properties2 = convertToPropertyInfos(klass.getDeclaredMethods(), klass.getDeclaredFields(), ExportedProperty.class);
            map.put(klass, properties2);
            return properties2;
        }
        return properties;
    }

    private static void dumpViewProperties(Context context, Object view, BufferedWriter out) throws IOException {
        dumpViewProperties(context, view, out, "");
    }

    private static void dumpViewProperties(Context context, Object view, BufferedWriter out, String prefix) throws IOException {
        if (view == null) {
            out.write(prefix + "=4,null ");
            return;
        }
        Class<?> klass = view.getClass();
        do {
            writeExportedProperties(context, view, out, klass, prefix);
            klass = klass.getSuperclass();
        } while (klass != Object.class);
    }

    private static String formatIntToHexString(int value) {
        return "0x" + Integer.toHexString(value).toUpperCase();
    }

    private static void writeExportedProperties(Context context, Object view, BufferedWriter out, Class<?> klass, String prefix) throws IOException {
        PropertyInfo<ExportedProperty, ?>[] exportedProperties;
        Object value;
        String categoryPrefix;
        for (PropertyInfo<ExportedProperty, ?> info : getExportedProperties(klass)) {
            try {
                value = info.invoke(view);
                categoryPrefix = info.property.category().length() != 0 ? info.property.category() + ":" : "";
            } catch (Exception e) {
            }
            if (info.returnType == Integer.TYPE || info.returnType == Byte.TYPE) {
                if (info.property.resolveId() && context != null) {
                    int id = ((Integer) value).intValue();
                    value = resolveId(context, id);
                } else if (info.property.formatToHexString()) {
                    if (info.returnType == Integer.TYPE) {
                        value = formatIntToHexString(((Integer) value).intValue());
                    } else if (info.returnType == Byte.TYPE) {
                        value = "0x" + HexEncoding.encodeToString(((Byte) value).byteValue(), true);
                    }
                } else {
                    FlagToString[] flagsMapping = info.property.flagMapping();
                    if (flagsMapping.length > 0) {
                        String valuePrefix = categoryPrefix + prefix + info.name + '_';
                        exportUnrolledFlags(out, flagsMapping, ((Integer) value).intValue(), valuePrefix);
                    }
                    IntToString[] mapping = info.property.mapping();
                    if (mapping.length > 0) {
                        int intValue = ((Integer) value).intValue();
                        boolean mapped = false;
                        int mappingCount = mapping.length;
                        int j = 0;
                        while (true) {
                            if (j >= mappingCount) {
                                break;
                            }
                            IntToString mapper = mapping[j];
                            Object value2 = value;
                            if (mapper.from() != intValue) {
                                j++;
                                value = value2;
                            } else {
                                value = mapper.m86to();
                                mapped = true;
                                break;
                            }
                        }
                        if (!mapped) {
                            value = Integer.valueOf(intValue);
                        }
                    }
                }
            } else {
                if (info.returnType == int[].class) {
                    String valuePrefix2 = categoryPrefix + prefix + info.name + '_';
                    exportUnrolledArray(context, out, info.property, (int[]) value, valuePrefix2, info.entrySuffix);
                } else if (info.returnType == String[].class) {
                    String[] array = (String[]) value;
                    if (info.property.hasAdjacentMapping() && array != null) {
                        for (int j2 = 0; j2 < array.length; j2 += 2) {
                            if (array[j2] != null) {
                                writeEntry(out, categoryPrefix + prefix, array[j2], info.entrySuffix, array[j2 + 1] == null ? "null" : array[j2 + 1]);
                            }
                        }
                    }
                } else if (!info.returnType.isPrimitive() && info.property.deepExport()) {
                    dumpViewProperties(context, value, out, prefix + info.property.prefix());
                }
            }
            writeEntry(out, categoryPrefix + prefix, info.name, info.entrySuffix, value);
        }
    }

    private static void writeEntry(BufferedWriter out, String prefix, String name, String suffix, Object value) throws IOException {
        out.write(prefix);
        out.write(name);
        out.write(suffix);
        out.write("=");
        writeValue(out, value);
        out.write(32);
    }

    private static void exportUnrolledFlags(BufferedWriter out, FlagToString[] mapping, int intValue, String prefix) throws IOException {
        for (FlagToString flagMapping : mapping) {
            boolean ifTrue = flagMapping.outputIf();
            int maskResult = flagMapping.mask() & intValue;
            boolean test = maskResult == flagMapping.equals();
            if ((test && ifTrue) || (!test && !ifTrue)) {
                String name = flagMapping.name();
                String value = formatIntToHexString(maskResult);
                writeEntry(out, prefix, name, "", value);
            }
        }
    }

    public static String intToString(Class<?> clazz, String field, int integer) {
        IntToString[] mapping = getMapping(clazz, field);
        if (mapping == null) {
            return Integer.toString(integer);
        }
        for (IntToString map : mapping) {
            if (map.from() == integer) {
                return map.m86to();
            }
        }
        return Integer.toString(integer);
    }

    public static String flagsToString(Class<?> clazz, String field, int flags) {
        FlagToString[] mapping = getFlagMapping(clazz, field);
        if (mapping == null) {
            return Integer.toHexString(flags);
        }
        StringBuilder result = new StringBuilder();
        int count = mapping.length;
        int j = 0;
        while (true) {
            if (j >= count) {
                break;
            }
            FlagToString flagMapping = mapping[j];
            boolean ifTrue = flagMapping.outputIf();
            int maskResult = flagMapping.mask() & flags;
            boolean test = maskResult == flagMapping.equals();
            if (test && ifTrue) {
                String name = flagMapping.name();
                result.append(name).append(' ');
            }
            j++;
        }
        int j2 = result.length();
        if (j2 > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    private static FlagToString[] getFlagMapping(Class<?> clazz, String field) {
        try {
            return ((ExportedProperty) clazz.getDeclaredField(field).getAnnotation(ExportedProperty.class)).flagMapping();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static IntToString[] getMapping(Class<?> clazz, String field) {
        try {
            return ((ExportedProperty) clazz.getDeclaredField(field).getAnnotation(ExportedProperty.class)).mapping();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static void exportUnrolledArray(Context context, BufferedWriter out, ExportedProperty property, int[] array, String prefix, String suffix) throws IOException {
        IntToString[] indexMapping = property.indexMapping();
        boolean resolveId = false;
        boolean hasIndexMapping = indexMapping.length > 0;
        IntToString[] mapping = property.mapping();
        boolean hasMapping = mapping.length > 0;
        if (property.resolveId() && context != null) {
            resolveId = true;
        }
        int valuesCount = array.length;
        for (int j = 0; j < valuesCount; j++) {
            String value = null;
            int intValue = array[j];
            String name = String.valueOf(j);
            if (hasIndexMapping) {
                int mappingCount = indexMapping.length;
                int k = 0;
                while (true) {
                    if (k >= mappingCount) {
                        break;
                    }
                    IntToString mapped = indexMapping[k];
                    if (mapped.from() != j) {
                        k++;
                    } else {
                        name = mapped.m86to();
                        break;
                    }
                }
            }
            if (hasMapping) {
                int mappingCount2 = mapping.length;
                int k2 = 0;
                while (true) {
                    if (k2 >= mappingCount2) {
                        break;
                    }
                    IntToString mapped2 = mapping[k2];
                    if (mapped2.from() != intValue) {
                        k2++;
                    } else {
                        value = mapped2.m86to();
                        break;
                    }
                }
            }
            if (resolveId) {
                if (value == null) {
                    value = (String) resolveId(context, intValue);
                }
            } else {
                value = String.valueOf(intValue);
            }
            writeEntry(out, prefix, name, suffix, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object resolveId(Context context, int id) {
        Resources resources = context.getResources();
        if (id >= 0) {
            try {
                Object fieldValue = resources.getResourceTypeName(id) + '/' + resources.getResourceEntryName(id);
                return fieldValue;
            } catch (Resources.NotFoundException e) {
                Object fieldValue2 = "id/" + formatIntToHexString(id);
                return fieldValue2;
            }
        }
        return "NO_ID";
    }

    private static void writeValue(BufferedWriter out, Object value) throws IOException {
        if (value != null) {
            String output = "[EXCEPTION]";
            try {
                output = value.toString().replace("\n", "\\n");
                return;
            } finally {
                out.write(String.valueOf(output.length()));
                out.write(",");
                out.write(output);
            }
        }
        out.write("4,null");
    }

    private static PropertyInfo<CapturedViewProperty, ?>[] getCapturedViewProperties(Class<?> klass) {
        if (sCapturedViewProperties == null) {
            sCapturedViewProperties = new HashMap<>();
        }
        HashMap<Class<?>, PropertyInfo<CapturedViewProperty, ?>[]> map = sCapturedViewProperties;
        PropertyInfo<CapturedViewProperty, ?>[] infos = map.get(klass);
        if (infos == null) {
            PropertyInfo<CapturedViewProperty, ?>[] infos2 = convertToPropertyInfos(klass.getMethods(), klass.getFields(), CapturedViewProperty.class);
            map.put(klass, infos2);
            return infos2;
        }
        return infos;
    }

    private static String exportCapturedViewProperties(Object obj, Class<?> klass, String prefix) {
        PropertyInfo<CapturedViewProperty, ?>[] capturedViewProperties;
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (PropertyInfo<CapturedViewProperty, ?> pi : getCapturedViewProperties(klass)) {
            try {
                Object methodValue = pi.invoke(obj);
                if (pi.property.retrieveReturn()) {
                    sb.append(exportCapturedViewProperties(methodValue, pi.returnType, pi.name + "#"));
                } else {
                    sb.append(prefix).append(pi.name).append(pi.entrySuffix).append("=");
                    if (methodValue != null) {
                        String value = methodValue.toString().replace("\n", "\\n");
                        sb.append(value);
                    } else {
                        sb.append("null");
                    }
                    sb.append(pi.valueSuffix).append(" ");
                }
            } catch (Exception e) {
            }
        }
        return sb.toString();
    }

    public static void dumpCapturedView(String tag, Object view) {
        Class<?> klass = view.getClass();
        Log.m112d(tag, (klass.getName() + ": ") + exportCapturedViewProperties(view, klass, ""));
    }

    public static Object invokeViewMethod(final View view, final Method method, final Object[] args) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        view.post(new Runnable() { // from class: android.view.ViewDebug.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    result.set(method.invoke(view, args));
                } catch (InvocationTargetException e) {
                    exception.set(e.getCause());
                } catch (Exception e2) {
                    exception.set(e2);
                }
                latch.countDown();
            }
        });
        try {
            latch.await();
            if (exception.get() != null) {
                throw new RuntimeException(exception.get());
            }
            return result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLayoutParameter(final View view, String param, int value) throws NoSuchFieldException, IllegalAccessException {
        final ViewGroup.LayoutParams p = view.getLayoutParams();
        Field f = p.getClass().getField(param);
        if (f.getType() != Integer.TYPE) {
            throw new RuntimeException("Only integer layout parameters can be set. Field " + param + " is of type " + f.getType().getSimpleName());
        }
        f.set(p, Integer.valueOf(value));
        view.post(new Runnable() { // from class: android.view.ViewDebug.5
            @Override // java.lang.Runnable
            public void run() {
                View.this.setLayoutParams(p);
            }
        });
    }

    /* loaded from: classes4.dex */
    public static class SoftwareCanvasProvider implements CanvasProvider {
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private boolean mEnabledHwFeaturesInSwMode;

        @Override // android.view.ViewDebug.CanvasProvider
        public Canvas getCanvas(View view, int width, int height) {
            Bitmap createBitmap = Bitmap.createBitmap(view.getResources().getDisplayMetrics(), width, height, Bitmap.Config.ARGB_8888);
            this.mBitmap = createBitmap;
            if (createBitmap == null) {
                throw new OutOfMemoryError();
            }
            createBitmap.setDensity(view.getResources().getDisplayMetrics().densityDpi);
            if (view.mAttachInfo != null) {
                this.mCanvas = view.mAttachInfo.mCanvas;
            }
            if (this.mCanvas == null) {
                this.mCanvas = new Canvas();
            }
            this.mEnabledHwFeaturesInSwMode = this.mCanvas.isHwFeaturesInSwModeEnabled();
            this.mCanvas.setBitmap(this.mBitmap);
            return this.mCanvas;
        }

        @Override // android.view.ViewDebug.CanvasProvider
        public Bitmap createBitmap() {
            this.mCanvas.setBitmap(null);
            this.mCanvas.setHwFeaturesInSwModeEnabled(this.mEnabledHwFeaturesInSwMode);
            return this.mBitmap;
        }
    }

    /* loaded from: classes4.dex */
    public static class HardwareCanvasProvider implements CanvasProvider {
        private Picture mPicture;

        @Override // android.view.ViewDebug.CanvasProvider
        public Canvas getCanvas(View view, int width, int height) {
            Picture picture = new Picture();
            this.mPicture = picture;
            return picture.beginRecording(width, height);
        }

        @Override // android.view.ViewDebug.CanvasProvider
        public Bitmap createBitmap() {
            this.mPicture.endRecording();
            return Bitmap.createBitmap(this.mPicture);
        }
    }
}
