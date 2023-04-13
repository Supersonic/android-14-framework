package android.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import com.android.internal.C4057R;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public class TransitionInflater {
    private static final Class<?>[] sConstructorSignature = {Context.class, AttributeSet.class};
    private static final ArrayMap<String, Constructor> sConstructors = new ArrayMap<>();
    private Context mContext;

    private TransitionInflater(Context context) {
        this.mContext = context;
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    public Transition inflateTransition(int resource) {
        XmlResourceParser parser = this.mContext.getResources().getXml(resource);
        try {
            try {
                return createTransitionFromXml(parser, Xml.asAttributeSet(parser), null);
            } catch (IOException e) {
                InflateException ex = new InflateException(parser.getPositionDescription() + ": " + e.getMessage());
                ex.initCause(e);
                throw ex;
            } catch (XmlPullParserException e2) {
                InflateException ex2 = new InflateException(e2.getMessage());
                ex2.initCause(e2);
                throw ex2;
            }
        } finally {
            parser.close();
        }
    }

    public TransitionManager inflateTransitionManager(int resource, ViewGroup sceneRoot) {
        XmlResourceParser parser = this.mContext.getResources().getXml(resource);
        try {
            try {
                return createTransitionManagerFromXml(parser, Xml.asAttributeSet(parser), sceneRoot);
            } catch (IOException e) {
                InflateException ex = new InflateException(parser.getPositionDescription() + ": " + e.getMessage());
                ex.initCause(e);
                throw ex;
            } catch (XmlPullParserException e2) {
                InflateException ex2 = new InflateException(e2.getMessage());
                ex2.initCause(e2);
                throw ex2;
            }
        } finally {
            parser.close();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:76:0x0185, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private Transition createTransitionFromXml(XmlPullParser parser, AttributeSet attrs, Transition parent) throws XmlPullParserException, IOException {
        Transition transition = null;
        int depth = parser.getDepth();
        TransitionSet transitionSet = parent instanceof TransitionSet ? (TransitionSet) parent : null;
        while (true) {
            int type = parser.next();
            if ((type != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if ("fade".equals(name)) {
                        transition = new Fade(this.mContext, attrs);
                    } else if ("changeBounds".equals(name)) {
                        transition = new ChangeBounds(this.mContext, attrs);
                    } else if ("slide".equals(name)) {
                        transition = new Slide(this.mContext, attrs);
                    } else if ("explode".equals(name)) {
                        transition = new Explode(this.mContext, attrs);
                    } else if ("changeImageTransform".equals(name)) {
                        transition = new ChangeImageTransform(this.mContext, attrs);
                    } else if ("changeTransform".equals(name)) {
                        transition = new ChangeTransform(this.mContext, attrs);
                    } else if ("changeClipBounds".equals(name)) {
                        transition = new ChangeClipBounds(this.mContext, attrs);
                    } else if ("autoTransition".equals(name)) {
                        transition = new AutoTransition(this.mContext, attrs);
                    } else if ("recolor".equals(name)) {
                        transition = new Recolor(this.mContext, attrs);
                    } else if ("changeScroll".equals(name)) {
                        transition = new ChangeScroll(this.mContext, attrs);
                    } else if ("transitionSet".equals(name)) {
                        transition = new TransitionSet(this.mContext, attrs);
                    } else if ("transition".equals(name)) {
                        transition = (Transition) createCustom(attrs, Transition.class, "transition");
                    } else if ("targets".equals(name)) {
                        getTargetIds(parser, attrs, parent);
                    } else if ("arcMotion".equals(name)) {
                        parent.setPathMotion(new ArcMotion(this.mContext, attrs));
                    } else if ("pathMotion".equals(name)) {
                        parent.setPathMotion((PathMotion) createCustom(attrs, PathMotion.class, "pathMotion"));
                    } else if ("patternPathMotion".equals(name)) {
                        parent.setPathMotion(new PatternPathMotion(this.mContext, attrs));
                    } else {
                        throw new RuntimeException("Unknown scene name: " + parser.getName());
                    }
                    if (transition == null) {
                        continue;
                    } else {
                        if (!parser.isEmptyElementTag()) {
                            createTransitionFromXml(parser, attrs, transition);
                        }
                        if (transitionSet != null) {
                            transitionSet.addTransition(transition);
                            transition = null;
                        } else if (parent != null) {
                            throw new InflateException("Could not add transition to another transition.");
                        }
                    }
                }
            }
        }
    }

    private Object createCustom(AttributeSet attrs, Class expectedType, String tag) {
        Object newInstance;
        Class c;
        String className = attrs.getAttributeValue(null, "class");
        if (className == null) {
            throw new InflateException(tag + " tag must have a 'class' attribute");
        }
        try {
            ArrayMap<String, Constructor> arrayMap = sConstructors;
            synchronized (arrayMap) {
                Constructor constructor = arrayMap.get(className);
                if (constructor == null && (c = this.mContext.getClassLoader().loadClass(className).asSubclass(expectedType)) != null) {
                    constructor = c.getConstructor(sConstructorSignature);
                    constructor.setAccessible(true);
                    arrayMap.put(className, constructor);
                }
                newInstance = constructor.newInstance(this.mContext, attrs);
            }
            return newInstance;
        } catch (ClassNotFoundException e) {
            throw new InflateException("Could not instantiate " + expectedType + " class " + className, e);
        } catch (IllegalAccessException e2) {
            throw new InflateException("Could not instantiate " + expectedType + " class " + className, e2);
        } catch (InstantiationException e3) {
            throw new InflateException("Could not instantiate " + expectedType + " class " + className, e3);
        } catch (NoSuchMethodException e4) {
            throw new InflateException("Could not instantiate " + expectedType + " class " + className, e4);
        } catch (InvocationTargetException e5) {
            throw new InflateException("Could not instantiate " + expectedType + " class " + className, e5);
        }
    }

    private void getTargetIds(XmlPullParser parser, AttributeSet attrs, Transition transition) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if ((type != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if (name.equals("target")) {
                        TypedArray a = this.mContext.obtainStyledAttributes(attrs, C4057R.styleable.TransitionTarget);
                        int id = a.getResourceId(1, 0);
                        if (id != 0) {
                            transition.addTarget(id);
                        } else {
                            int id2 = a.getResourceId(2, 0);
                            if (id2 != 0) {
                                transition.excludeTarget(id2, true);
                            } else {
                                String transitionName = a.getString(4);
                                if (transitionName != null) {
                                    transition.addTarget(transitionName);
                                } else {
                                    String transitionName2 = a.getString(5);
                                    if (transitionName2 != null) {
                                        transition.excludeTarget(transitionName2, true);
                                    } else {
                                        String className = a.getString(3);
                                        if (className != null) {
                                            try {
                                                Class clazz = Class.forName(className);
                                                transition.excludeTarget(clazz, true);
                                            } catch (ClassNotFoundException e) {
                                                a.recycle();
                                                throw new RuntimeException("Could not create " + className, e);
                                            }
                                        } else {
                                            String className2 = a.getString(0);
                                            if (className2 != null) {
                                                Class clazz2 = Class.forName(className2);
                                                transition.addTarget(clazz2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        a.recycle();
                    } else {
                        throw new RuntimeException("Unknown scene name: " + parser.getName());
                    }
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x005a, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private TransitionManager createTransitionManagerFromXml(XmlPullParser parser, AttributeSet attrs, ViewGroup sceneRoot) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        TransitionManager transitionManager = null;
        while (true) {
            int type = parser.next();
            if ((type != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if (name.equals("transitionManager")) {
                        transitionManager = new TransitionManager();
                    } else if (!name.equals("transition") || transitionManager == null) {
                        break;
                    } else {
                        loadTransition(attrs, sceneRoot, transitionManager);
                    }
                }
            }
        }
        throw new RuntimeException("Unknown scene name: " + parser.getName());
    }

    private void loadTransition(AttributeSet attrs, ViewGroup sceneRoot, TransitionManager transitionManager) throws Resources.NotFoundException {
        Transition transition;
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, C4057R.styleable.TransitionManager);
        int transitionId = a.getResourceId(2, -1);
        int fromId = a.getResourceId(0, -1);
        Scene fromScene = fromId < 0 ? null : Scene.getSceneForLayout(sceneRoot, fromId, this.mContext);
        int toId = a.getResourceId(1, -1);
        Scene toScene = toId >= 0 ? Scene.getSceneForLayout(sceneRoot, toId, this.mContext) : null;
        if (transitionId >= 0 && (transition = inflateTransition(transitionId)) != null) {
            if (toScene == null) {
                throw new RuntimeException("No toScene for transition ID " + transitionId);
            }
            if (fromScene == null) {
                transitionManager.setTransition(toScene, transition);
            } else {
                transitionManager.setTransition(fromScene, toScene, transition);
            }
        }
        a.recycle();
    }
}
