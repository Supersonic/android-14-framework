package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
/* loaded from: classes3.dex */
public abstract class Transition implements Cloneable {
    static final boolean DBG = false;
    private static final String LOG_TAG = "Transition";
    private static final int MATCH_FIRST = 1;
    public static final int MATCH_ID = 3;
    private static final String MATCH_ID_STR = "id";
    public static final int MATCH_INSTANCE = 1;
    private static final String MATCH_INSTANCE_STR = "instance";
    public static final int MATCH_ITEM_ID = 4;
    private static final String MATCH_ITEM_ID_STR = "itemId";
    private static final int MATCH_LAST = 4;
    public static final int MATCH_NAME = 2;
    private static final String MATCH_NAME_STR = "name";
    private static final String MATCH_VIEW_NAME_STR = "viewName";
    ArrayList<TransitionValues> mEndValuesList;
    EpicenterCallback mEpicenterCallback;
    ArrayMap<String, String> mNameOverrides;
    TransitionPropagation mPropagation;
    ArrayList<TransitionValues> mStartValuesList;
    private static final int[] DEFAULT_MATCH_ORDER = {2, 1, 3, 4};
    private static final PathMotion STRAIGHT_PATH_MOTION = new PathMotion() { // from class: android.transition.Transition.1
        @Override // android.transition.PathMotion
        public Path getPath(float startX, float startY, float endX, float endY) {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(endX, endY);
            return path;
        }
    };
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal<>();
    private String mName = getClass().getName();
    long mStartDelay = -1;
    long mDuration = -1;
    TimeInterpolator mInterpolator = null;
    ArrayList<Integer> mTargetIds = new ArrayList<>();
    ArrayList<View> mTargets = new ArrayList<>();
    ArrayList<String> mTargetNames = null;
    ArrayList<Class> mTargetTypes = null;
    ArrayList<Integer> mTargetIdExcludes = null;
    ArrayList<View> mTargetExcludes = null;
    ArrayList<Class> mTargetTypeExcludes = null;
    ArrayList<String> mTargetNameExcludes = null;
    ArrayList<Integer> mTargetIdChildExcludes = null;
    ArrayList<View> mTargetChildExcludes = null;
    ArrayList<Class> mTargetTypeChildExcludes = null;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    TransitionSet mParent = null;
    int[] mMatchOrder = DEFAULT_MATCH_ORDER;
    ViewGroup mSceneRoot = null;
    boolean mCanRemoveViews = false;
    private ArrayList<Animator> mCurrentAnimators = new ArrayList<>();
    int mNumInstances = 0;
    boolean mPaused = false;
    private boolean mEnded = false;
    ArrayList<TransitionListener> mListeners = null;
    ArrayList<Animator> mAnimators = new ArrayList<>();
    PathMotion mPathMotion = STRAIGHT_PATH_MOTION;

    /* loaded from: classes3.dex */
    public static abstract class EpicenterCallback {
        public abstract Rect onGetEpicenter(Transition transition);
    }

    /* loaded from: classes3.dex */
    public interface TransitionListener {
        void onTransitionCancel(Transition transition);

        void onTransitionEnd(Transition transition);

        void onTransitionPause(Transition transition);

        void onTransitionResume(Transition transition);

        void onTransitionStart(Transition transition);
    }

    public abstract void captureEndValues(TransitionValues transitionValues);

    public abstract void captureStartValues(TransitionValues transitionValues);

    public Transition() {
    }

    public Transition(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.Transition);
        long duration = a.getInt(1, -1);
        if (duration >= 0) {
            setDuration(duration);
        }
        long startDelay = a.getInt(2, -1);
        if (startDelay > 0) {
            setStartDelay(startDelay);
        }
        int resID = a.getResourceId(0, 0);
        if (resID > 0) {
            setInterpolator(AnimationUtils.loadInterpolator(context, resID));
        }
        String matchOrder = a.getString(3);
        if (matchOrder != null) {
            setMatchOrder(parseMatchOrder(matchOrder));
        }
        a.recycle();
    }

    private static int[] parseMatchOrder(String matchOrderString) {
        StringTokenizer st = new StringTokenizer(matchOrderString, ",");
        int[] matches = new int[st.countTokens()];
        int index = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if ("id".equalsIgnoreCase(token)) {
                matches[index] = 3;
            } else if (MATCH_INSTANCE_STR.equalsIgnoreCase(token)) {
                matches[index] = 1;
            } else if ("name".equalsIgnoreCase(token)) {
                matches[index] = 2;
            } else if (MATCH_VIEW_NAME_STR.equalsIgnoreCase(token)) {
                matches[index] = 2;
            } else if (MATCH_ITEM_ID_STR.equalsIgnoreCase(token)) {
                matches[index] = 4;
            } else if (token.isEmpty()) {
                int[] smallerMatches = new int[matches.length - 1];
                System.arraycopy(matches, 0, smallerMatches, 0, index);
                matches = smallerMatches;
                index--;
            } else {
                throw new InflateException("Unknown match type in matchOrder: '" + token + "'");
            }
            index++;
        }
        return matches;
    }

    public Transition setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public Transition setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
        return this;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public Transition setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public String[] getTransitionProperties() {
        return null;
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    public void setMatchOrder(int... matches) {
        if (matches == null || matches.length == 0) {
            this.mMatchOrder = DEFAULT_MATCH_ORDER;
            return;
        }
        for (int i = 0; i < matches.length; i++) {
            int match = matches[i];
            if (!isValidMatch(match)) {
                throw new IllegalArgumentException("matches contains invalid value");
            }
            if (alreadyContains(matches, i)) {
                throw new IllegalArgumentException("matches contains a duplicate value");
            }
        }
        this.mMatchOrder = (int[]) matches.clone();
    }

    private static boolean isValidMatch(int match) {
        return match >= 1 && match <= 4;
    }

    private static boolean alreadyContains(int[] array, int searchIndex) {
        int value = array[searchIndex];
        for (int i = 0; i < searchIndex; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

    private void matchInstances(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd) {
        TransitionValues end;
        for (int i = unmatchedStart.size() - 1; i >= 0; i--) {
            View view = unmatchedStart.keyAt(i);
            if (view != null && isValidTarget(view) && (end = unmatchedEnd.remove(view)) != null && isValidTarget(end.view)) {
                TransitionValues start = unmatchedStart.removeAt(i);
                this.mStartValuesList.add(start);
                this.mEndValuesList.add(end);
            }
        }
    }

    private void matchItemIds(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd, LongSparseArray<View> startItemIds, LongSparseArray<View> endItemIds) {
        View endView;
        int numStartIds = startItemIds.size();
        for (int i = 0; i < numStartIds; i++) {
            View startView = startItemIds.valueAt(i);
            if (startView != null && isValidTarget(startView) && (endView = endItemIds.get(startItemIds.keyAt(i))) != null && isValidTarget(endView)) {
                TransitionValues startValues = unmatchedStart.get(startView);
                TransitionValues endValues = unmatchedEnd.get(endView);
                if (startValues != null && endValues != null) {
                    this.mStartValuesList.add(startValues);
                    this.mEndValuesList.add(endValues);
                    unmatchedStart.remove(startView);
                    unmatchedEnd.remove(endView);
                }
            }
        }
    }

    private void matchIds(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd, SparseArray<View> startIds, SparseArray<View> endIds) {
        View endView;
        int numStartIds = startIds.size();
        for (int i = 0; i < numStartIds; i++) {
            View startView = startIds.valueAt(i);
            if (startView != null && isValidTarget(startView) && (endView = endIds.get(startIds.keyAt(i))) != null && isValidTarget(endView)) {
                TransitionValues startValues = unmatchedStart.get(startView);
                TransitionValues endValues = unmatchedEnd.get(endView);
                if (startValues != null && endValues != null) {
                    this.mStartValuesList.add(startValues);
                    this.mEndValuesList.add(endValues);
                    unmatchedStart.remove(startView);
                    unmatchedEnd.remove(endView);
                }
            }
        }
    }

    private void matchNames(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd, ArrayMap<String, View> startNames, ArrayMap<String, View> endNames) {
        View endView;
        int numStartNames = startNames.size();
        for (int i = 0; i < numStartNames; i++) {
            View startView = startNames.valueAt(i);
            if (startView != null && isValidTarget(startView) && (endView = endNames.get(startNames.keyAt(i))) != null && isValidTarget(endView)) {
                TransitionValues startValues = unmatchedStart.get(startView);
                TransitionValues endValues = unmatchedEnd.get(endView);
                if (startValues != null && endValues != null) {
                    this.mStartValuesList.add(startValues);
                    this.mEndValuesList.add(endValues);
                    unmatchedStart.remove(startView);
                    unmatchedEnd.remove(endView);
                }
            }
        }
    }

    private void addUnmatched(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd) {
        for (int i = 0; i < unmatchedStart.size(); i++) {
            TransitionValues start = unmatchedStart.valueAt(i);
            if (isValidTarget(start.view)) {
                this.mStartValuesList.add(start);
                this.mEndValuesList.add(null);
            }
        }
        for (int i2 = 0; i2 < unmatchedEnd.size(); i2++) {
            TransitionValues end = unmatchedEnd.valueAt(i2);
            if (isValidTarget(end.view)) {
                this.mEndValuesList.add(end);
                this.mStartValuesList.add(null);
            }
        }
    }

    private void matchStartAndEnd(TransitionValuesMaps startValues, TransitionValuesMaps endValues) {
        ArrayMap<View, TransitionValues> unmatchedStart = new ArrayMap<>(startValues.viewValues);
        ArrayMap<View, TransitionValues> unmatchedEnd = new ArrayMap<>(endValues.viewValues);
        int i = 0;
        while (true) {
            int[] iArr = this.mMatchOrder;
            if (i < iArr.length) {
                switch (iArr[i]) {
                    case 1:
                        matchInstances(unmatchedStart, unmatchedEnd);
                        break;
                    case 2:
                        matchNames(unmatchedStart, unmatchedEnd, startValues.nameValues, endValues.nameValues);
                        break;
                    case 3:
                        matchIds(unmatchedStart, unmatchedEnd, startValues.idValues, endValues.idValues);
                        break;
                    case 4:
                        matchItemIds(unmatchedStart, unmatchedEnd, startValues.itemIdValues, endValues.itemIdValues);
                        break;
                }
                i++;
            } else {
                addUnmatched(unmatchedStart, unmatchedEnd);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues, ArrayList<TransitionValues> startValuesList, ArrayList<TransitionValues> endValuesList) {
        TransitionValues start;
        TransitionValues end;
        int minAnimator;
        int startValuesListCount;
        int i;
        TransitionValues infoValues;
        View view;
        Animator animator;
        long minStartDelay;
        TransitionValues infoValues2;
        Animator animator2;
        TransitionValues infoValues3;
        String[] properties;
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        long minStartDelay2 = Long.MAX_VALUE;
        int minAnimator2 = this.mAnimators.size();
        SparseLongArray startDelays = new SparseLongArray();
        int startValuesListCount2 = startValuesList.size();
        int i2 = 0;
        while (i2 < startValuesListCount2) {
            TransitionValues start2 = startValuesList.get(i2);
            TransitionValues end2 = endValuesList.get(i2);
            if (start2 != null && !start2.targetedTransitions.contains(this)) {
                start = null;
            } else {
                start = start2;
            }
            if (end2 != null && !end2.targetedTransitions.contains(this)) {
                end = null;
            } else {
                end = end2;
            }
            if (start == null && end == null) {
                minAnimator = minAnimator2;
                startValuesListCount = startValuesListCount2;
                i = i2;
            } else {
                boolean isChanged = start == null || end == null || isTransitionRequired(start, end);
                if (isChanged) {
                    Animator animator3 = createAnimator(sceneRoot, start, end);
                    if (animator3 != null) {
                        if (end == null) {
                            infoValues = null;
                            minAnimator = minAnimator2;
                            startValuesListCount = startValuesListCount2;
                            i = i2;
                            View view2 = start != null ? start.view : null;
                            view = view2;
                            animator = animator3;
                        } else {
                            View view3 = end.view;
                            String[] properties2 = getTransitionProperties();
                            if (properties2 != null) {
                                infoValues2 = null;
                                if (properties2.length > 0) {
                                    infoValues3 = new TransitionValues(view3);
                                    minAnimator = minAnimator2;
                                    startValuesListCount = startValuesListCount2;
                                    TransitionValues newValues = endValues.viewValues.get(view3);
                                    if (newValues != null) {
                                        int j = 0;
                                        while (j < properties2.length) {
                                            infoValues3.values.put(properties2[j], newValues.values.get(properties2[j]));
                                            j++;
                                            newValues = newValues;
                                            i2 = i2;
                                        }
                                        i = i2;
                                    } else {
                                        i = i2;
                                    }
                                    int numExistingAnims = runningAnimators.size();
                                    int j2 = 0;
                                    while (true) {
                                        if (j2 >= numExistingAnims) {
                                            animator2 = animator3;
                                            break;
                                        }
                                        Animator anim = runningAnimators.keyAt(j2);
                                        AnimationInfo info = runningAnimators.get(anim);
                                        if (info.values == null || info.view != view3) {
                                            properties = properties2;
                                        } else {
                                            if (info.name == null && getName() == null) {
                                                properties = properties2;
                                            } else {
                                                properties = properties2;
                                                if (!info.name.equals(getName())) {
                                                    continue;
                                                }
                                            }
                                            if (info.values.equals(infoValues3)) {
                                                animator2 = null;
                                                break;
                                            }
                                        }
                                        j2++;
                                        properties2 = properties;
                                    }
                                    animator = animator2;
                                    view = view3;
                                    infoValues = infoValues3;
                                } else {
                                    minAnimator = minAnimator2;
                                    startValuesListCount = startValuesListCount2;
                                    i = i2;
                                }
                            } else {
                                infoValues2 = null;
                                minAnimator = minAnimator2;
                                startValuesListCount = startValuesListCount2;
                                i = i2;
                            }
                            animator2 = animator3;
                            infoValues3 = infoValues2;
                            animator = animator2;
                            view = view3;
                            infoValues = infoValues3;
                        }
                        if (animator != null) {
                            TransitionPropagation transitionPropagation = this.mPropagation;
                            if (transitionPropagation == null) {
                                minStartDelay = minStartDelay2;
                            } else {
                                long delay = transitionPropagation.getStartDelay(sceneRoot, this, start, end);
                                startDelays.put(this.mAnimators.size(), delay);
                                minStartDelay = Math.min(delay, minStartDelay2);
                            }
                            runningAnimators.put(animator, new AnimationInfo(view, getName(), this, sceneRoot.getWindowId(), infoValues));
                            this.mAnimators.add(animator);
                            minStartDelay2 = minStartDelay;
                        }
                    } else {
                        minAnimator = minAnimator2;
                        startValuesListCount = startValuesListCount2;
                        i = i2;
                    }
                } else {
                    minAnimator = minAnimator2;
                    startValuesListCount = startValuesListCount2;
                    i = i2;
                }
            }
            i2 = i + 1;
            minAnimator2 = minAnimator;
            startValuesListCount2 = startValuesListCount;
        }
        if (startDelays.size() != 0) {
            for (int i3 = 0; i3 < startDelays.size(); i3++) {
                int index = startDelays.keyAt(i3);
                Animator animator4 = this.mAnimators.get(index);
                animator4.setStartDelay((startDelays.valueAt(i3) - minStartDelay2) + animator4.getStartDelay());
            }
        }
    }

    public boolean isValidTarget(View target) {
        ArrayList<Class> arrayList;
        ArrayList<String> arrayList2;
        if (target == null) {
            return false;
        }
        int targetId = target.getId();
        ArrayList<Integer> arrayList3 = this.mTargetIdExcludes;
        if (arrayList3 != null && arrayList3.contains(Integer.valueOf(targetId))) {
            return false;
        }
        ArrayList<View> arrayList4 = this.mTargetExcludes;
        if (arrayList4 != null && arrayList4.contains(target)) {
            return false;
        }
        ArrayList<Class> arrayList5 = this.mTargetTypeExcludes;
        if (arrayList5 != null && target != null) {
            int numTypes = arrayList5.size();
            for (int i = 0; i < numTypes; i++) {
                Class type = this.mTargetTypeExcludes.get(i);
                if (type.isInstance(target)) {
                    return false;
                }
            }
        }
        if (this.mTargetNameExcludes != null && target != null && target.getTransitionName() != null && this.mTargetNameExcludes.contains(target.getTransitionName())) {
            return false;
        }
        if ((this.mTargetIds.size() == 0 && this.mTargets.size() == 0 && (((arrayList = this.mTargetTypes) == null || arrayList.isEmpty()) && ((arrayList2 = this.mTargetNames) == null || arrayList2.isEmpty()))) || this.mTargetIds.contains(Integer.valueOf(targetId)) || this.mTargets.contains(target)) {
            return true;
        }
        ArrayList<String> arrayList6 = this.mTargetNames;
        if (arrayList6 == null || !arrayList6.contains(target.getTransitionName())) {
            if (this.mTargetTypes != null) {
                for (int i2 = 0; i2 < this.mTargetTypes.size(); i2++) {
                    if (this.mTargetTypes.get(i2).isInstance(target)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap<Animator, AnimationInfo> runningAnimators = sRunningAnimators.get();
        if (runningAnimators == null) {
            ArrayMap<Animator, AnimationInfo> runningAnimators2 = new ArrayMap<>();
            sRunningAnimators.set(runningAnimators2);
            return runningAnimators2;
        }
        return runningAnimators;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void runAnimators() {
        start();
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        Iterator<Animator> it = this.mAnimators.iterator();
        while (it.hasNext()) {
            Animator anim = it.next();
            if (runningAnimators.containsKey(anim)) {
                start();
                runAnimator(anim, runningAnimators);
            }
        }
        this.mAnimators.clear();
        end();
    }

    private void runAnimator(Animator animator, final ArrayMap<Animator, AnimationInfo> runningAnimators) {
        if (animator != null) {
            animator.addListener(new AnimatorListenerAdapter() { // from class: android.transition.Transition.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    Transition.this.mCurrentAnimators.add(animation);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    runningAnimators.remove(animation);
                    Transition.this.mCurrentAnimators.remove(animation);
                }
            });
            animate(animator);
        }
    }

    public Transition addTarget(int targetId) {
        if (targetId > 0) {
            this.mTargetIds.add(Integer.valueOf(targetId));
        }
        return this;
    }

    public Transition addTarget(String targetName) {
        if (targetName != null) {
            if (this.mTargetNames == null) {
                this.mTargetNames = new ArrayList<>();
            }
            this.mTargetNames.add(targetName);
        }
        return this;
    }

    public Transition addTarget(Class targetType) {
        if (targetType != null) {
            if (this.mTargetTypes == null) {
                this.mTargetTypes = new ArrayList<>();
            }
            this.mTargetTypes.add(targetType);
        }
        return this;
    }

    public Transition removeTarget(int targetId) {
        if (targetId > 0) {
            this.mTargetIds.remove(Integer.valueOf(targetId));
        }
        return this;
    }

    public Transition removeTarget(String targetName) {
        ArrayList<String> arrayList;
        if (targetName != null && (arrayList = this.mTargetNames) != null) {
            arrayList.remove(targetName);
        }
        return this;
    }

    public Transition excludeTarget(int targetId, boolean exclude) {
        if (targetId >= 0) {
            this.mTargetIdExcludes = excludeObject(this.mTargetIdExcludes, Integer.valueOf(targetId), exclude);
        }
        return this;
    }

    public Transition excludeTarget(String targetName, boolean exclude) {
        this.mTargetNameExcludes = excludeObject(this.mTargetNameExcludes, targetName, exclude);
        return this;
    }

    public Transition excludeChildren(int targetId, boolean exclude) {
        if (targetId >= 0) {
            this.mTargetIdChildExcludes = excludeObject(this.mTargetIdChildExcludes, Integer.valueOf(targetId), exclude);
        }
        return this;
    }

    public Transition excludeTarget(View target, boolean exclude) {
        this.mTargetExcludes = excludeObject(this.mTargetExcludes, target, exclude);
        return this;
    }

    public Transition excludeChildren(View target, boolean exclude) {
        this.mTargetChildExcludes = excludeObject(this.mTargetChildExcludes, target, exclude);
        return this;
    }

    private static <T> ArrayList<T> excludeObject(ArrayList<T> list, T target, boolean exclude) {
        if (target != null) {
            if (exclude) {
                return ArrayListManager.add(list, target);
            }
            return ArrayListManager.remove(list, target);
        }
        return list;
    }

    public Transition excludeTarget(Class type, boolean exclude) {
        this.mTargetTypeExcludes = excludeObject(this.mTargetTypeExcludes, type, exclude);
        return this;
    }

    public Transition excludeChildren(Class type, boolean exclude) {
        this.mTargetTypeChildExcludes = excludeObject(this.mTargetTypeChildExcludes, type, exclude);
        return this;
    }

    public Transition addTarget(View target) {
        this.mTargets.add(target);
        return this;
    }

    public Transition removeTarget(View target) {
        if (target != null) {
            this.mTargets.remove(target);
        }
        return this;
    }

    public Transition removeTarget(Class target) {
        if (target != null) {
            this.mTargetTypes.remove(target);
        }
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    public List<View> getTargets() {
        return this.mTargets;
    }

    public List<String> getTargetNames() {
        return this.mTargetNames;
    }

    public List<String> getTargetViewNames() {
        return this.mTargetNames;
    }

    public List<Class> getTargetTypes() {
        return this.mTargetTypes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void captureValues(ViewGroup sceneRoot, boolean start) {
        ArrayList<String> arrayList;
        ArrayList<Class> arrayList2;
        ArrayMap<String, String> arrayMap;
        clearValues(start);
        if ((this.mTargetIds.size() > 0 || this.mTargets.size() > 0) && (((arrayList = this.mTargetNames) == null || arrayList.isEmpty()) && ((arrayList2 = this.mTargetTypes) == null || arrayList2.isEmpty()))) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                int id = this.mTargetIds.get(i).intValue();
                View view = sceneRoot.findViewById(id);
                if (view != null) {
                    TransitionValues values = new TransitionValues(view);
                    if (start) {
                        captureStartValues(values);
                    } else {
                        captureEndValues(values);
                    }
                    values.targetedTransitions.add(this);
                    capturePropagationValues(values);
                    if (start) {
                        addViewValues(this.mStartValues, view, values);
                    } else {
                        addViewValues(this.mEndValues, view, values);
                    }
                }
            }
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                View view2 = this.mTargets.get(i2);
                TransitionValues values2 = new TransitionValues(view2);
                if (start) {
                    captureStartValues(values2);
                } else {
                    captureEndValues(values2);
                }
                values2.targetedTransitions.add(this);
                capturePropagationValues(values2);
                if (start) {
                    addViewValues(this.mStartValues, view2, values2);
                } else {
                    addViewValues(this.mEndValues, view2, values2);
                }
            }
        } else {
            captureHierarchy(sceneRoot, start);
        }
        if (!start && (arrayMap = this.mNameOverrides) != null) {
            int numOverrides = arrayMap.size();
            ArrayList<View> overriddenViews = new ArrayList<>(numOverrides);
            for (int i3 = 0; i3 < numOverrides; i3++) {
                String fromName = this.mNameOverrides.keyAt(i3);
                overriddenViews.add(this.mStartValues.nameValues.remove(fromName));
            }
            for (int i4 = 0; i4 < numOverrides; i4++) {
                View view3 = overriddenViews.get(i4);
                if (view3 != null) {
                    String toName = this.mNameOverrides.valueAt(i4);
                    this.mStartValues.nameValues.put(toName, view3);
                }
            }
        }
    }

    static void addViewValues(TransitionValuesMaps transitionValuesMaps, View view, TransitionValues transitionValues) {
        transitionValuesMaps.viewValues.put(view, transitionValues);
        int id = view.getId();
        if (id >= 0) {
            if (transitionValuesMaps.idValues.indexOfKey(id) >= 0) {
                transitionValuesMaps.idValues.put(id, null);
            } else {
                transitionValuesMaps.idValues.put(id, view);
            }
        }
        String name = view.getTransitionName();
        if (name != null) {
            if (transitionValuesMaps.nameValues.containsKey(name)) {
                transitionValuesMaps.nameValues.put(name, null);
            } else {
                transitionValuesMaps.nameValues.put(name, view);
            }
        }
        if (view.getParent() instanceof ListView) {
            ListView listview = (ListView) view.getParent();
            if (listview.getAdapter().hasStableIds()) {
                int position = listview.getPositionForView(view);
                long itemId = listview.getItemIdAtPosition(position);
                if (transitionValuesMaps.itemIdValues.indexOfKey(itemId) >= 0) {
                    View alreadyMatched = transitionValuesMaps.itemIdValues.get(itemId);
                    if (alreadyMatched != null) {
                        alreadyMatched.setHasTransientState(false);
                        transitionValuesMaps.itemIdValues.put(itemId, null);
                        return;
                    }
                    return;
                }
                view.setHasTransientState(true);
                transitionValuesMaps.itemIdValues.put(itemId, view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearValues(boolean start) {
        if (start) {
            this.mStartValues.viewValues.clear();
            this.mStartValues.idValues.clear();
            this.mStartValues.itemIdValues.clear();
            this.mStartValues.nameValues.clear();
            this.mStartValuesList = null;
            return;
        }
        this.mEndValues.viewValues.clear();
        this.mEndValues.idValues.clear();
        this.mEndValues.itemIdValues.clear();
        this.mEndValues.nameValues.clear();
        this.mEndValuesList = null;
    }

    private void captureHierarchy(View view, boolean start) {
        if (view == null) {
            return;
        }
        int id = view.getId();
        ArrayList<Integer> arrayList = this.mTargetIdExcludes;
        if (arrayList != null && arrayList.contains(Integer.valueOf(id))) {
            return;
        }
        ArrayList<View> arrayList2 = this.mTargetExcludes;
        if (arrayList2 != null && arrayList2.contains(view)) {
            return;
        }
        ArrayList<Class> arrayList3 = this.mTargetTypeExcludes;
        if (arrayList3 != null && view != null) {
            int numTypes = arrayList3.size();
            for (int i = 0; i < numTypes; i++) {
                if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                    return;
                }
            }
        }
        if (view.getParent() instanceof ViewGroup) {
            TransitionValues values = new TransitionValues(view);
            if (start) {
                captureStartValues(values);
            } else {
                captureEndValues(values);
            }
            values.targetedTransitions.add(this);
            capturePropagationValues(values);
            if (start) {
                addViewValues(this.mStartValues, view, values);
            } else {
                addViewValues(this.mEndValues, view, values);
            }
        }
        if (view instanceof ViewGroup) {
            ArrayList<Integer> arrayList4 = this.mTargetIdChildExcludes;
            if (arrayList4 != null && arrayList4.contains(Integer.valueOf(id))) {
                return;
            }
            ArrayList<View> arrayList5 = this.mTargetChildExcludes;
            if (arrayList5 != null && arrayList5.contains(view)) {
                return;
            }
            ArrayList<Class> arrayList6 = this.mTargetTypeChildExcludes;
            if (arrayList6 != null) {
                int numTypes2 = arrayList6.size();
                for (int i2 = 0; i2 < numTypes2; i2++) {
                    if (this.mTargetTypeChildExcludes.get(i2).isInstance(view)) {
                        return;
                    }
                }
            }
            ViewGroup parent = (ViewGroup) view;
            for (int i3 = 0; i3 < parent.getChildCount(); i3++) {
                captureHierarchy(parent.getChildAt(i3), start);
            }
        }
    }

    public TransitionValues getTransitionValues(View view, boolean start) {
        TransitionSet transitionSet = this.mParent;
        if (transitionSet != null) {
            return transitionSet.getTransitionValues(view, start);
        }
        TransitionValuesMaps valuesMaps = start ? this.mStartValues : this.mEndValues;
        return valuesMaps.viewValues.get(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransitionValues getMatchedTransitionValues(View view, boolean viewInStart) {
        TransitionSet transitionSet = this.mParent;
        if (transitionSet != null) {
            return transitionSet.getMatchedTransitionValues(view, viewInStart);
        }
        ArrayList<TransitionValues> lookIn = viewInStart ? this.mStartValuesList : this.mEndValuesList;
        if (lookIn == null) {
            return null;
        }
        int count = lookIn.size();
        int index = -1;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            TransitionValues values = lookIn.get(i);
            if (values == null) {
                return null;
            }
            if (values.view != view) {
                i++;
            } else {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return null;
        }
        ArrayList<TransitionValues> matchIn = viewInStart ? this.mEndValuesList : this.mStartValuesList;
        return matchIn.get(index);
    }

    public void pause(View sceneRoot) {
        if (!this.mEnded) {
            ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
            int numOldAnims = runningAnimators.size();
            if (sceneRoot != null) {
                WindowId windowId = sceneRoot.getWindowId();
                for (int i = numOldAnims - 1; i >= 0; i--) {
                    AnimationInfo info = runningAnimators.valueAt(i);
                    if (info.view != null && windowId != null && windowId.equals(info.windowId)) {
                        Animator anim = runningAnimators.keyAt(i);
                        anim.pause();
                    }
                }
            }
            ArrayList<TransitionListener> arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i2 = 0; i2 < numListeners; i2++) {
                    tmpListeners.get(i2).onTransitionPause(this);
                }
            }
            this.mPaused = true;
        }
    }

    public void resume(View sceneRoot) {
        if (this.mPaused) {
            if (!this.mEnded) {
                ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
                int numOldAnims = runningAnimators.size();
                WindowId windowId = sceneRoot.getWindowId();
                for (int i = numOldAnims - 1; i >= 0; i--) {
                    AnimationInfo info = runningAnimators.valueAt(i);
                    if (info.view != null && windowId != null && windowId.equals(info.windowId)) {
                        Animator anim = runningAnimators.keyAt(i);
                        anim.resume();
                    }
                }
                ArrayList<TransitionListener> arrayList = this.mListeners;
                if (arrayList != null && arrayList.size() > 0) {
                    ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                    int numListeners = tmpListeners.size();
                    for (int i2 = 0; i2 < numListeners; i2++) {
                        tmpListeners.get(i2).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playTransition(ViewGroup sceneRoot) {
        AnimationInfo oldInfo;
        this.mStartValuesList = new ArrayList<>();
        this.mEndValuesList = new ArrayList<>();
        matchStartAndEnd(this.mStartValues, this.mEndValues);
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        int numOldAnims = runningAnimators.size();
        WindowId windowId = sceneRoot.getWindowId();
        for (int i = numOldAnims - 1; i >= 0; i--) {
            Animator anim = runningAnimators.keyAt(i);
            if (anim != null && (oldInfo = runningAnimators.get(anim)) != null && oldInfo.view != null && oldInfo.windowId == windowId) {
                TransitionValues oldValues = oldInfo.values;
                View oldView = oldInfo.view;
                TransitionValues startValues = getTransitionValues(oldView, true);
                TransitionValues endValues = getMatchedTransitionValues(oldView, true);
                if (startValues == null && endValues == null) {
                    endValues = this.mEndValues.viewValues.get(oldView);
                }
                boolean cancel = !(startValues == null && endValues == null) && oldInfo.transition.isTransitionRequired(oldValues, endValues);
                if (cancel) {
                    if (anim.isRunning() || anim.isStarted()) {
                        anim.cancel();
                    } else {
                        runningAnimators.remove(anim);
                    }
                }
            }
        }
        createAnimators(sceneRoot, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
        runAnimators();
    }

    public boolean isTransitionRequired(TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return false;
        }
        String[] properties = getTransitionProperties();
        if (properties != null) {
            for (String str : properties) {
                if (isValueChanged(startValues, endValues, str)) {
                    return true;
                }
            }
            return false;
        }
        for (String key : startValues.values.keySet()) {
            if (isValueChanged(startValues, endValues, key)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValueChanged(TransitionValues oldValues, TransitionValues newValues, String key) {
        if (oldValues.values.containsKey(key) != newValues.values.containsKey(key)) {
            return false;
        }
        Object oldValue = oldValues.values.get(key);
        Object newValue = newValues.values.get(key);
        if (oldValue == null && newValue == null) {
            return false;
        }
        if (oldValue == null || newValue == null) {
            return true;
        }
        boolean changed = !oldValue.equals(newValue);
        return changed;
    }

    protected void animate(Animator animator) {
        if (animator == null) {
            end();
            return;
        }
        if (getDuration() >= 0) {
            animator.setDuration(getDuration());
        }
        if (getStartDelay() >= 0) {
            animator.setStartDelay(getStartDelay() + animator.getStartDelay());
        }
        if (getInterpolator() != null) {
            animator.setInterpolator(getInterpolator());
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: android.transition.Transition.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Transition.this.end();
                animation.removeListener(this);
            }
        });
        animator.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start() {
        if (this.mNumInstances == 0) {
            ArrayList<TransitionListener> arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    tmpListeners.get(i).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        this.mNumInstances++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void end() {
        int i = this.mNumInstances - 1;
        this.mNumInstances = i;
        if (i == 0) {
            ArrayList<TransitionListener> arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i2 = 0; i2 < numListeners; i2++) {
                    tmpListeners.get(i2).onTransitionEnd(this);
                }
            }
            for (int i3 = 0; i3 < this.mStartValues.itemIdValues.size(); i3++) {
                View view = this.mStartValues.itemIdValues.valueAt(i3);
                if (view != null) {
                    view.setHasTransientState(false);
                }
            }
            for (int i4 = 0; i4 < this.mEndValues.itemIdValues.size(); i4++) {
                View view2 = this.mEndValues.itemIdValues.valueAt(i4);
                if (view2 != null) {
                    view2.setHasTransientState(false);
                }
            }
            this.mEnded = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void forceToEnd(ViewGroup sceneRoot) {
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        int numOldAnims = runningAnimators.size();
        if (sceneRoot == null || numOldAnims == 0) {
            return;
        }
        WindowId windowId = sceneRoot.getWindowId();
        ArrayMap<Animator, AnimationInfo> oldAnimators = new ArrayMap<>(runningAnimators);
        runningAnimators.clear();
        for (int i = numOldAnims - 1; i >= 0; i--) {
            AnimationInfo info = oldAnimators.valueAt(i);
            if (info.view != null && windowId != null && windowId.equals(info.windowId)) {
                Animator anim = oldAnimators.keyAt(i);
                anim.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancel() {
        int numAnimators = this.mCurrentAnimators.size();
        for (int i = numAnimators - 1; i >= 0; i--) {
            Animator animator = this.mCurrentAnimators.get(i);
            animator.cancel();
        }
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList != null && arrayList.size() > 0) {
            ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i2 = 0; i2 < numListeners; i2++) {
                tmpListeners.get(i2).onTransitionCancel(this);
            }
        }
    }

    public Transition addListener(TransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(listener);
        return this;
    }

    public Transition removeListener(TransitionListener listener) {
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return this;
        }
        arrayList.remove(listener);
        if (this.mListeners.size() == 0) {
            this.mListeners = null;
        }
        return this;
    }

    public void setEpicenterCallback(EpicenterCallback epicenterCallback) {
        this.mEpicenterCallback = epicenterCallback;
    }

    public EpicenterCallback getEpicenterCallback() {
        return this.mEpicenterCallback;
    }

    public Rect getEpicenter() {
        EpicenterCallback epicenterCallback = this.mEpicenterCallback;
        if (epicenterCallback == null) {
            return null;
        }
        return epicenterCallback.onGetEpicenter(this);
    }

    public void setPathMotion(PathMotion pathMotion) {
        if (pathMotion == null) {
            this.mPathMotion = STRAIGHT_PATH_MOTION;
        } else {
            this.mPathMotion = pathMotion;
        }
    }

    public PathMotion getPathMotion() {
        return this.mPathMotion;
    }

    public void setPropagation(TransitionPropagation transitionPropagation) {
        this.mPropagation = transitionPropagation;
    }

    public TransitionPropagation getPropagation() {
        return this.mPropagation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void capturePropagationValues(TransitionValues transitionValues) {
        String[] propertyNames;
        if (this.mPropagation == null || transitionValues.values.isEmpty() || (propertyNames = this.mPropagation.getPropagationProperties()) == null) {
            return;
        }
        boolean containsAll = true;
        int i = 0;
        while (true) {
            if (i >= propertyNames.length) {
                break;
            } else if (transitionValues.values.containsKey(propertyNames[i])) {
                i++;
            } else {
                containsAll = false;
                break;
            }
        }
        if (!containsAll) {
            this.mPropagation.captureValues(transitionValues);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Transition setSceneRoot(ViewGroup sceneRoot) {
        this.mSceneRoot = sceneRoot;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCanRemoveViews(boolean canRemoveViews) {
        this.mCanRemoveViews = canRemoveViews;
    }

    public boolean canRemoveViews() {
        return this.mCanRemoveViews;
    }

    public void setNameOverrides(ArrayMap<String, String> overrides) {
        this.mNameOverrides = overrides;
    }

    public ArrayMap<String, String> getNameOverrides() {
        return this.mNameOverrides;
    }

    public String toString() {
        return toString("");
    }

    @Override // 
    /* renamed from: clone */
    public Transition mo4799clone() {
        Transition clone = null;
        try {
            clone = (Transition) super.clone();
            clone.mAnimators = new ArrayList<>();
            clone.mStartValues = new TransitionValuesMaps();
            clone.mEndValues = new TransitionValuesMaps();
            clone.mStartValuesList = null;
            clone.mEndValuesList = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String toString(String indent) {
        String result = indent + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
        if (this.mDuration != -1) {
            result = result + "dur(" + this.mDuration + ") ";
        }
        if (this.mStartDelay != -1) {
            result = result + "dly(" + this.mStartDelay + ") ";
        }
        if (this.mInterpolator != null) {
            result = result + "interp(" + this.mInterpolator + ") ";
        }
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            String result2 = result + "tgts(";
            if (this.mTargetIds.size() > 0) {
                for (int i = 0; i < this.mTargetIds.size(); i++) {
                    if (i > 0) {
                        result2 = result2 + ", ";
                    }
                    result2 = result2 + this.mTargetIds.get(i);
                }
            }
            if (this.mTargets.size() > 0) {
                for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                    if (i2 > 0) {
                        result2 = result2 + ", ";
                    }
                    result2 = result2 + this.mTargets.get(i2);
                }
            }
            return result2 + NavigationBarInflaterView.KEY_CODE_END;
        }
        return result;
    }

    /* loaded from: classes3.dex */
    public static class AnimationInfo {
        String name;
        Transition transition;
        TransitionValues values;
        public View view;
        WindowId windowId;

        AnimationInfo(View view, String name, Transition transition, WindowId windowId, TransitionValues values) {
            this.view = view;
            this.name = name;
            this.values = values;
            this.windowId = windowId;
            this.transition = transition;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ArrayListManager {
        private ArrayListManager() {
        }

        static <T> ArrayList<T> add(ArrayList<T> list, T item) {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (!list.contains(item)) {
                list.add(item);
            }
            return list;
        }

        static <T> ArrayList<T> remove(ArrayList<T> list, T item) {
            if (list != null) {
                list.remove(item);
                if (list.isEmpty()) {
                    return null;
                }
                return list;
            }
            return list;
        }
    }
}
