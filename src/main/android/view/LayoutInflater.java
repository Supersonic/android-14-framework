package android.view;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.StrictMode;
import android.p008os.Trace;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.internal.C4057R;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public abstract class LayoutInflater {
    private static final String ATTR_LAYOUT = "layout";
    private static final String COMPILED_VIEW_DEX_FILE_NAME = "/compiled_view.dex";
    private static final boolean DEBUG = false;
    private static final String TAG_1995 = "blink";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_MERGE = "merge";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";
    private static final String TAG_TAG = "tag";
    private static final String USE_PRECOMPILED_LAYOUT = "view.precompiled_layout_enabled";
    final Object[] mConstructorArgs;
    protected final Context mContext;
    private Factory mFactory;
    private Factory2 mFactory2;
    private boolean mFactorySet;
    private Filter mFilter;
    private HashMap<String, Boolean> mFilterMap;
    private ClassLoader mPrecompiledClassLoader;
    private Factory2 mPrivateFactory;
    private TypedValue mTempValue;
    private boolean mUseCompiledView;
    private static final String TAG = LayoutInflater.class.getSimpleName();
    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
    static final Class<?>[] mConstructorSignature = {Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();
    private static final int[] ATTRS_THEME = {16842752};
    private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();

    /* loaded from: classes4.dex */
    public interface Factory {
        View onCreateView(String str, Context context, AttributeSet attributeSet);
    }

    /* loaded from: classes4.dex */
    public interface Factory2 extends Factory {
        View onCreateView(View view, String str, Context context, AttributeSet attributeSet);
    }

    /* loaded from: classes4.dex */
    public interface Filter {
        boolean onLoadClass(Class cls);
    }

    public abstract LayoutInflater cloneInContext(Context context);

    /* loaded from: classes4.dex */
    private static class FactoryMerger implements Factory2 {
        private final Factory mF1;
        private final Factory2 mF12;
        private final Factory mF2;
        private final Factory2 mF22;

        FactoryMerger(Factory f1, Factory2 f12, Factory f2, Factory2 f22) {
            this.mF1 = f1;
            this.mF2 = f2;
            this.mF12 = f12;
            this.mF22 = f22;
        }

        @Override // android.view.LayoutInflater.Factory
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View v = this.mF1.onCreateView(name, context, attrs);
            return v != null ? v : this.mF2.onCreateView(name, context, attrs);
        }

        @Override // android.view.LayoutInflater.Factory2
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            Factory2 factory2 = this.mF12;
            View v = factory2 != null ? factory2.onCreateView(parent, name, context, attrs) : this.mF1.onCreateView(name, context, attrs);
            if (v != null) {
                return v;
            }
            Factory2 factory22 = this.mF22;
            return factory22 != null ? factory22.onCreateView(parent, name, context, attrs) : this.mF2.onCreateView(name, context, attrs);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public LayoutInflater(Context context) {
        this.mConstructorArgs = new Object[2];
        StrictMode.assertConfigurationContext(context, "LayoutInflater");
        this.mContext = context;
        initPrecompiledViews();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public LayoutInflater(LayoutInflater original, Context newContext) {
        this.mConstructorArgs = new Object[2];
        StrictMode.assertConfigurationContext(newContext, "LayoutInflater");
        this.mContext = newContext;
        this.mFactory = original.mFactory;
        this.mFactory2 = original.mFactory2;
        this.mPrivateFactory = original.mPrivateFactory;
        setFilter(original.mFilter);
        initPrecompiledViews();
    }

    public static LayoutInflater from(Context context) {
        LayoutInflater LayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }

    public Context getContext() {
        return this.mContext;
    }

    public final Factory getFactory() {
        return this.mFactory;
    }

    public final Factory2 getFactory2() {
        return this.mFactory2;
    }

    public void setFactory(Factory factory) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        this.mFactorySet = true;
        Factory factory2 = this.mFactory;
        if (factory2 == null) {
            this.mFactory = factory;
        } else {
            this.mFactory = new FactoryMerger(factory, null, factory2, this.mFactory2);
        }
    }

    public void setFactory2(Factory2 factory) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        this.mFactorySet = true;
        Factory factory2 = this.mFactory;
        if (factory2 == null) {
            this.mFactory2 = factory;
            this.mFactory = factory;
            return;
        }
        FactoryMerger factoryMerger = new FactoryMerger(factory, factory, factory2, this.mFactory2);
        this.mFactory2 = factoryMerger;
        this.mFactory = factoryMerger;
    }

    public void setPrivateFactory(Factory2 factory) {
        Factory2 factory2 = this.mPrivateFactory;
        if (factory2 == null) {
            this.mPrivateFactory = factory;
        } else {
            this.mPrivateFactory = new FactoryMerger(factory, factory, factory2, factory2);
        }
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public void setFilter(Filter filter) {
        this.mFilter = filter;
        if (filter != null) {
            this.mFilterMap = new HashMap<>();
        }
    }

    private void initPrecompiledViews() {
        initPrecompiledViews(false);
    }

    private void initPrecompiledViews(boolean enablePrecompiledViews) {
        this.mUseCompiledView = enablePrecompiledViews;
        if (!enablePrecompiledViews) {
            this.mPrecompiledClassLoader = null;
            return;
        }
        ApplicationInfo appInfo = this.mContext.getApplicationInfo();
        if (appInfo.isEmbeddedDexUsed() || appInfo.isPrivilegedApp()) {
            this.mUseCompiledView = false;
            return;
        }
        try {
            this.mPrecompiledClassLoader = this.mContext.getClassLoader();
            String dexFile = this.mContext.getCodeCacheDir() + COMPILED_VIEW_DEX_FILE_NAME;
            if (new File(dexFile).exists()) {
                this.mPrecompiledClassLoader = new PathClassLoader(dexFile, this.mPrecompiledClassLoader);
            } else {
                this.mUseCompiledView = false;
            }
        } catch (Throwable th) {
            this.mUseCompiledView = false;
        }
        if (!this.mUseCompiledView) {
            this.mPrecompiledClassLoader = null;
        }
    }

    public void setPrecompiledLayoutsEnabledForTesting(boolean enablePrecompiledLayouts) {
        initPrecompiledViews(enablePrecompiledLayouts);
    }

    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        Resources res = getContext().getResources();
        View view = tryInflatePrecompiled(resource, res, root, attachToRoot);
        if (view != null) {
            return view;
        }
        XmlResourceParser parser = res.getLayout(resource);
        try {
            return inflate(parser, root, attachToRoot);
        } finally {
            parser.close();
        }
    }

    private View tryInflatePrecompiled(int resource, Resources res, ViewGroup root, boolean attachToRoot) {
        if (this.mUseCompiledView) {
            Trace.traceBegin(8L, "inflate (precompiled)");
            String pkg = res.getResourcePackageName(resource);
            String layout = res.getResourceEntryName(resource);
            try {
                Class clazz = Class.forName("" + pkg + ".CompiledView", false, this.mPrecompiledClassLoader);
                Method inflater = clazz.getMethod(layout, Context.class, Integer.TYPE);
                View view = (View) inflater.invoke(null, this.mContext, Integer.valueOf(resource));
                if (view != null && root != null) {
                    XmlResourceParser parser = res.getLayout(resource);
                    AttributeSet attrs = Xml.asAttributeSet(parser);
                    advanceToRootNode(parser);
                    ViewGroup.LayoutParams params = root.generateLayoutParams(attrs);
                    if (attachToRoot) {
                        root.addView(view, params);
                    } else {
                        view.setLayoutParams(params);
                    }
                    parser.close();
                }
                Trace.traceEnd(8L);
                return view;
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                return null;
            }
        }
        return null;
    }

    private void advanceToRootNode(XmlPullParser parser) throws InflateException, IOException, XmlPullParserException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new InflateException(parser.getPositionDescription() + ": No start tag found!");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v4 */
    /* JADX WARN: Type inference failed for: r10v5 */
    /* JADX WARN: Type inference failed for: r10v6 */
    /* JADX WARN: Type inference failed for: r10v8 */
    /* JADX WARN: Type inference failed for: r10v9 */
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        String name;
        ?? r10;
        synchronized (this.mConstructorArgs) {
            try {
                try {
                    ?? r102 = 8;
                    r102 = 8;
                    Trace.traceBegin(8L, "inflate");
                    Context inflaterContext = this.mContext;
                    AttributeSet attrs = Xml.asAttributeSet(parser);
                    Object[] objArr = this.mConstructorArgs;
                    Context lastContext = (Context) objArr[0];
                    objArr[0] = inflaterContext;
                    View result = root;
                    if (root != null && root.getViewRootImpl() != null) {
                        root.getViewRootImpl().notifyRendererOfExpensiveFrame();
                    }
                    try {
                        try {
                            advanceToRootNode(parser);
                            name = parser.getName();
                        } catch (Throwable th) {
                            e = th;
                        }
                    } catch (XmlPullParserException e) {
                        e = e;
                    } catch (Exception e2) {
                        e = e2;
                    } catch (Throwable th2) {
                        e = th2;
                        r102 = 1;
                    }
                    try {
                        if (!TAG_MERGE.equals(name)) {
                            r10 = 1;
                            View temp = createViewFromTag(root, name, inflaterContext, attrs);
                            if (root == null && temp != null && temp.getViewRootImpl() != null) {
                                temp.getViewRootImpl().notifyRendererOfExpensiveFrame();
                            }
                            ViewGroup.LayoutParams params = null;
                            if (root != null) {
                                params = root.generateLayoutParams(attrs);
                                if (!attachToRoot) {
                                    temp.setLayoutParams(params);
                                }
                            }
                            try {
                                rInflateChildren(parser, temp, attrs, true);
                                if (root != null && attachToRoot) {
                                    root.addView(temp, params);
                                }
                                if (root == null || !attachToRoot) {
                                    result = temp;
                                }
                            } catch (XmlPullParserException e3) {
                                e = e3;
                                InflateException ie = new InflateException(e.getMessage(), e);
                                ie.setStackTrace(EMPTY_STACK_TRACE);
                                throw ie;
                            } catch (Exception e4) {
                                e = e4;
                                InflateException ie2 = new InflateException(getParserStateDescription(inflaterContext, attrs) + ": " + e.getMessage(), e);
                                ie2.setStackTrace(EMPTY_STACK_TRACE);
                                throw ie2;
                            }
                        } else if (root == null || !attachToRoot) {
                            throw new InflateException("<merge /> can be used only with a valid ViewGroup root and attachToRoot=true");
                        } else {
                            r10 = 1;
                            rInflate(parser, root, inflaterContext, attrs, false);
                        }
                        Object[] objArr2 = this.mConstructorArgs;
                        objArr2[0] = lastContext;
                        objArr2[r10] = null;
                        Trace.traceEnd(8L);
                        return result;
                    } catch (XmlPullParserException e5) {
                        e = e5;
                    } catch (Exception e6) {
                        e = e6;
                    } catch (Throwable th3) {
                        e = th3;
                        Object[] objArr3 = this.mConstructorArgs;
                        objArr3[0] = lastContext;
                        objArr3[r102] = null;
                        Trace.traceEnd(8L);
                        throw e;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th;
            }
        }
    }

    private static String getParserStateDescription(Context context, AttributeSet attrs) {
        int sourceResId = Resources.getAttributeSetSourceResId(attrs);
        if (sourceResId == 0) {
            return attrs.getPositionDescription();
        }
        return attrs.getPositionDescription() + " in " + context.getResources().getResourceName(sourceResId);
    }

    private final boolean verifyClassLoader(Constructor<? extends View> constructor) {
        ClassLoader constructorLoader = constructor.getDeclaringClass().getClassLoader();
        if (constructorLoader == BOOT_CLASS_LOADER) {
            return true;
        }
        ClassLoader cl = this.mContext.getClassLoader();
        while (constructorLoader != cl) {
            cl = cl.getParent();
            if (cl == null) {
                return false;
            }
        }
        return true;
    }

    public final View createView(String name, String prefix, AttributeSet attrs) throws ClassNotFoundException, InflateException {
        Context context = (Context) this.mConstructorArgs[0];
        if (context == null) {
            context = this.mContext;
        }
        return createView(context, name, prefix, attrs);
    }

    public final View createView(Context viewContext, String name, String prefix, AttributeSet attrs) throws ClassNotFoundException, InflateException {
        Objects.requireNonNull(viewContext);
        Objects.requireNonNull(name);
        HashMap<String, Constructor<? extends View>> hashMap = sConstructorMap;
        Constructor<? extends View> constructor = hashMap.get(name);
        if (constructor != null && !verifyClassLoader(constructor)) {
            constructor = null;
            hashMap.remove(name);
        }
        Class<? extends View> clazz = null;
        try {
            try {
                try {
                    try {
                        Trace.traceBegin(8L, name);
                        if (constructor == null) {
                            Class asSubclass = Class.forName(prefix != null ? prefix + name : name, false, this.mContext.getClassLoader()).asSubclass(View.class);
                            Filter filter = this.mFilter;
                            if (filter != null && asSubclass != null) {
                                if (!filter.onLoadClass(asSubclass)) {
                                    failNotAllowed(name, prefix, viewContext, attrs);
                                }
                            }
                            constructor = asSubclass.getConstructor(mConstructorSignature);
                            constructor.setAccessible(true);
                            hashMap.put(name, constructor);
                        } else if (this.mFilter != null) {
                            Boolean allowedState = this.mFilterMap.get(name);
                            if (allowedState == null) {
                                Class asSubclass2 = Class.forName(prefix != null ? prefix + name : name, false, this.mContext.getClassLoader()).asSubclass(View.class);
                                boolean allowed = asSubclass2 != null && this.mFilter.onLoadClass(asSubclass2);
                                this.mFilterMap.put(name, Boolean.valueOf(allowed));
                                if (!allowed) {
                                    failNotAllowed(name, prefix, viewContext, attrs);
                                }
                            } else if (allowedState.equals(Boolean.FALSE)) {
                                failNotAllowed(name, prefix, viewContext, attrs);
                            }
                        }
                        Object[] args = this.mConstructorArgs;
                        Object lastContext = args[0];
                        args[0] = viewContext;
                        args[1] = attrs;
                        try {
                            View view = constructor.newInstance(args);
                            if (view instanceof ViewStub) {
                                ViewStub viewStub = (ViewStub) view;
                                viewStub.setLayoutInflater(cloneInContext((Context) args[0]));
                            }
                            return view;
                        } finally {
                            this.mConstructorArgs[0] = lastContext;
                        }
                    } catch (Exception e) {
                        InflateException ie = new InflateException(getParserStateDescription(viewContext, attrs) + ": Error inflating class " + (0 == 0 ? "<unknown>" : clazz.getName()), e);
                        ie.setStackTrace(EMPTY_STACK_TRACE);
                        throw ie;
                    }
                } catch (ClassCastException e2) {
                    InflateException ie2 = new InflateException(getParserStateDescription(viewContext, attrs) + ": Class is not a View " + (prefix != null ? prefix + name : name), e2);
                    ie2.setStackTrace(EMPTY_STACK_TRACE);
                    throw ie2;
                }
            } catch (ClassNotFoundException e3) {
                throw e3;
            } catch (NoSuchMethodException e4) {
                InflateException ie3 = new InflateException(getParserStateDescription(viewContext, attrs) + ": Error inflating class " + (prefix != null ? prefix + name : name), e4);
                ie3.setStackTrace(EMPTY_STACK_TRACE);
                throw ie3;
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private void failNotAllowed(String name, String prefix, Context context, AttributeSet attrs) {
        throw new InflateException(getParserStateDescription(context, attrs) + ": Class not allowed to be inflated " + (prefix != null ? prefix + name : name));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        return createView(name, "android.view.", attrs);
    }

    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return onCreateView(name, attrs);
    }

    public View onCreateView(Context viewContext, View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return onCreateView(parent, name, attrs);
    }

    private View createViewFromTag(View parent, String name, Context context, AttributeSet attrs) {
        return createViewFromTag(parent, name, context, attrs, false);
    }

    View createViewFromTag(View parent, String name, Context context, AttributeSet attrs, boolean ignoreThemeAttr) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        if (!ignoreThemeAttr) {
            TypedArray ta = context.obtainStyledAttributes(attrs, ATTRS_THEME);
            int themeResId = ta.getResourceId(0, 0);
            if (themeResId != 0) {
                context = new ContextThemeWrapper(context, themeResId);
            }
            ta.recycle();
        }
        try {
            View view = tryCreateView(parent, name, context, attrs);
            if (view == null) {
                Object[] objArr = this.mConstructorArgs;
                Object lastContext = objArr[0];
                objArr[0] = context;
                try {
                    if (-1 == name.indexOf(46)) {
                        view = onCreateView(context, parent, name, attrs);
                    } else {
                        view = createView(context, name, null, attrs);
                    }
                    this.mConstructorArgs[0] = lastContext;
                } catch (Throwable th) {
                    this.mConstructorArgs[0] = lastContext;
                    throw th;
                }
            }
            return view;
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException ie = new InflateException(getParserStateDescription(context, attrs) + ": Error inflating class " + name, e2);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;
        } catch (Exception e3) {
            InflateException ie2 = new InflateException(getParserStateDescription(context, attrs) + ": Error inflating class " + name, e3);
            ie2.setStackTrace(EMPTY_STACK_TRACE);
            throw ie2;
        }
    }

    public final View tryCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view;
        Factory2 factory2;
        if (name.equals(TAG_1995)) {
            return new BlinkLayout(context, attrs);
        }
        Factory2 factory22 = this.mFactory2;
        if (factory22 != null) {
            view = factory22.onCreateView(parent, name, context, attrs);
        } else {
            Factory factory = this.mFactory;
            if (factory != null) {
                view = factory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }
        }
        if (view == null && (factory2 = this.mPrivateFactory) != null) {
            View view2 = factory2.onCreateView(parent, name, context, attrs);
            return view2;
        }
        return view;
    }

    final void rInflateChildren(XmlPullParser parser, View parent, AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
        rInflate(parser, parent, parent.getContext(), attrs, finishInflate);
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0076, code lost:
        if (r1 == false) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0078, code lost:
        r10.restoreDefaultFocus();
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x007b, code lost:
        if (r13 == false) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x007d, code lost:
        r10.onFinishInflate();
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0080, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    void rInflate(XmlPullParser parser, View parent, Context context, AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        boolean pendingRequestFocus = false;
        while (true) {
            int type = parser.next();
            if ((type != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if (TAG_REQUEST_FOCUS.equals(name)) {
                        pendingRequestFocus = true;
                        consumeChildElements(parser);
                    } else if ("tag".equals(name)) {
                        parseViewTag(parser, parent, attrs);
                    } else if (TAG_INCLUDE.equals(name)) {
                        if (parser.getDepth() == 0) {
                            throw new InflateException("<include /> cannot be the root element");
                        }
                        parseInclude(parser, context, parent, attrs);
                    } else if (TAG_MERGE.equals(name)) {
                        throw new InflateException("<merge /> must be the root element");
                    } else {
                        View view = createViewFromTag(parent, name, context, attrs);
                        ViewGroup viewGroup = (ViewGroup) parent;
                        ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                        rInflateChildren(parser, view, attrs, true);
                        viewGroup.addView(view, params);
                    }
                }
            }
        }
    }

    private void parseViewTag(XmlPullParser parser, View view, AttributeSet attrs) throws XmlPullParserException, IOException {
        Context context = view.getContext();
        TypedArray ta = context.obtainStyledAttributes(attrs, C4057R.styleable.ViewTag);
        int key = ta.getResourceId(1, 0);
        CharSequence value = ta.getText(0);
        view.setTag(key, value);
        ta.recycle();
        consumeChildElements(parser);
    }

    /* JADX WARN: Code restructure failed: missing block: B:38:0x00a3, code lost:
        if (r0 != 2) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00a5, code lost:
        r0 = r5.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b1, code lost:
        if (android.view.LayoutInflater.TAG_MERGE.equals(r0) == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00c4, code lost:
        rInflate(r5, r25, r15, r4, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00c7, code lost:
        r4 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00ca, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00cb, code lost:
        r4 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00e3, code lost:
        r0 = createViewFromTag(r25, r0, r15, r4, r14);
        r0 = (android.view.ViewGroup) r25;
        r0 = r15.obtainStyledAttributes(r26, com.android.internal.C4057R.styleable.Include);
        r0 = r0.getResourceId(0, -1);
        r0 = r0.getInt(1, -1);
        r0.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0101, code lost:
        r21 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0107, code lost:
        r21 = r0.generateLayoutParams(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x010a, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x010b, code lost:
        r4 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0148, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0149, code lost:
        r4 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x016e, code lost:
        throw new android.view.InflateException(getParserStateDescription(r15, r4) + ": No start tag found!");
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x0175, code lost:
        r4.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0178, code lost:
        throw r0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v9, types: [android.util.AttributeSet] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void parseInclude(XmlPullParser parser, Context context, View parent, AttributeSet attrs) throws XmlPullParserException, IOException {
        XmlResourceParser childParser;
        XmlResourceParser childParser2;
        View view;
        ViewGroup group;
        int id;
        int visibility;
        ViewGroup.LayoutParams params;
        AttributeSet childAttrs;
        ViewGroup.LayoutParams params2;
        if (!(parent instanceof ViewGroup)) {
            throw new InflateException("<include /> can only be used inside of a ViewGroup");
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, ATTRS_THEME);
        int themeResId = ta.getResourceId(0, 0);
        boolean hasThemeOverride = themeResId != 0;
        Context context2 = hasThemeOverride ? new ContextThemeWrapper(context, themeResId) : context;
        ta.recycle();
        int layout = attrs.getAttributeResourceValue(null, "layout", 0);
        if (layout == 0) {
            String value = attrs.getAttributeValue(null, "layout");
            if (value == null || value.length() <= 0) {
                throw new InflateException("You must specify a layout in the include tag: <include layout=\"@layout/layoutID\" />");
            }
            layout = context2.getResources().getIdentifier(value.substring(1), "attr", context2.getPackageName());
        }
        if (this.mTempValue == null) {
            this.mTempValue = new TypedValue();
        }
        int layout2 = (layout == 0 || !context2.getTheme().resolveAttribute(layout, this.mTempValue, true)) ? layout : this.mTempValue.resourceId;
        if (layout2 == 0) {
            throw new InflateException("You must specify a valid layout reference. The layout ID " + attrs.getAttributeValue(null, "layout") + " is not valid.");
        }
        View precompiled = tryInflatePrecompiled(layout2, context2.getResources(), (ViewGroup) parent, true);
        if (precompiled == null) {
            childParser = context2.getResources().getLayout(layout2);
            try {
                childParser2 = Xml.asAttributeSet(childParser);
                while (true) {
                    int type = childParser.next();
                    if (type == 2 || type == 1) {
                        try {
                            break;
                        } catch (Throwable th) {
                            th = th;
                        }
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                childParser2 = childParser;
            }
        }
        consumeChildElements(parser);
        if (params == null) {
            childAttrs = childParser2;
            params2 = group.generateLayoutParams(childAttrs);
        } else {
            childAttrs = childParser2;
            params2 = params;
        }
        view.setLayoutParams(params2);
        XmlResourceParser childParser3 = childParser;
        rInflateChildren(childParser3, view, childAttrs, true);
        if (id != -1) {
            view.setId(id);
        }
        switch (visibility) {
            case 0:
                view.setVisibility(0);
                break;
            case 1:
                view.setVisibility(4);
                break;
            case 2:
                view.setVisibility(8);
                break;
        }
        group.addView(view);
        childParser3.close();
        consumeChildElements(parser);
    }

    static final void consumeChildElements(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        int currentDepth = parser.getDepth();
        do {
            type = parser.next();
            if (type == 3 && parser.getDepth() <= currentDepth) {
                return;
            }
        } while (type != 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class BlinkLayout extends FrameLayout {
        private static final int BLINK_DELAY = 500;
        private static final int MESSAGE_BLINK = 66;
        private boolean mBlink;
        private boolean mBlinkState;
        private final Handler mHandler;

        public BlinkLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mHandler = new Handler(new Handler.Callback() { // from class: android.view.LayoutInflater.BlinkLayout.1
                @Override // android.p008os.Handler.Callback
                public boolean handleMessage(Message msg) {
                    if (msg.what == 66) {
                        if (BlinkLayout.this.mBlink) {
                            BlinkLayout blinkLayout = BlinkLayout.this;
                            blinkLayout.mBlinkState = !blinkLayout.mBlinkState;
                            BlinkLayout.this.makeBlink();
                        }
                        BlinkLayout.this.invalidate();
                        return true;
                    }
                    return false;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void makeBlink() {
            Message message = this.mHandler.obtainMessage(66);
            this.mHandler.sendMessageDelayed(message, 500L);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.mBlink = true;
            this.mBlinkState = true;
            makeBlink();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.mBlink = false;
            this.mBlinkState = true;
            this.mHandler.removeMessages(66);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.mBlinkState) {
                super.dispatchDraw(canvas);
            }
        }
    }
}
