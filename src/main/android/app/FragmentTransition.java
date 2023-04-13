package android.app;

import android.app.BackStackRecord;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.view.OneShotPreDrawListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FragmentTransition {
    private static final int[] INVERSE_OPS = {0, 3, 0, 1, 5, 4, 7, 6, 9, 8};

    /* loaded from: classes.dex */
    public static class FragmentContainerTransition {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;
    }

    FragmentTransition() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startTransitions(FragmentManagerImpl fragmentManager, ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex, boolean isReordered) {
        if (fragmentManager.mCurState < 1) {
            return;
        }
        SparseArray<FragmentContainerTransition> transitioningFragments = new SparseArray<>();
        for (int i = startIndex; i < endIndex; i++) {
            BackStackRecord record = records.get(i);
            boolean isPop = isRecordPop.get(i).booleanValue();
            if (isPop) {
                calculatePopFragments(record, transitioningFragments, isReordered);
            } else {
                calculateFragments(record, transitioningFragments, isReordered);
            }
        }
        int i2 = transitioningFragments.size();
        if (i2 != 0) {
            View nonExistentView = new View(fragmentManager.mHost.getContext());
            int numContainers = transitioningFragments.size();
            for (int i3 = 0; i3 < numContainers; i3++) {
                int containerId = transitioningFragments.keyAt(i3);
                ArrayMap<String, String> nameOverrides = calculateNameOverrides(containerId, records, isRecordPop, startIndex, endIndex);
                FragmentContainerTransition containerTransition = transitioningFragments.valueAt(i3);
                if (isReordered) {
                    configureTransitionsReordered(fragmentManager, containerId, containerTransition, nonExistentView, nameOverrides);
                } else {
                    configureTransitionsOrdered(fragmentManager, containerId, containerTransition, nonExistentView, nameOverrides);
                }
            }
        }
    }

    private static ArrayMap<String, String> calculateNameOverrides(int containerId, ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
        ArrayList<String> sources;
        ArrayList<String> targets;
        ArrayMap<String, String> nameOverrides = new ArrayMap<>();
        for (int recordNum = endIndex - 1; recordNum >= startIndex; recordNum--) {
            BackStackRecord record = records.get(recordNum);
            if (record.interactsWith(containerId)) {
                boolean isPop = isRecordPop.get(recordNum).booleanValue();
                if (record.mSharedElementSourceNames != null) {
                    int numSharedElements = record.mSharedElementSourceNames.size();
                    if (isPop) {
                        targets = record.mSharedElementSourceNames;
                        sources = record.mSharedElementTargetNames;
                    } else {
                        sources = record.mSharedElementSourceNames;
                        targets = record.mSharedElementTargetNames;
                    }
                    for (int i = 0; i < numSharedElements; i++) {
                        String sourceName = sources.get(i);
                        String targetName = targets.get(i);
                        String previousTarget = nameOverrides.remove(targetName);
                        if (previousTarget != null) {
                            nameOverrides.put(sourceName, previousTarget);
                        } else {
                            nameOverrides.put(sourceName, targetName);
                        }
                    }
                }
            }
        }
        return nameOverrides;
    }

    private static void configureTransitionsReordered(FragmentManagerImpl fragmentManager, int containerId, FragmentContainerTransition fragments, View nonExistentView, ArrayMap<String, String> nameOverrides) {
        ViewGroup sceneRoot;
        Transition exitTransition;
        if (fragmentManager.mContainer.onHasView()) {
            ViewGroup sceneRoot2 = (ViewGroup) fragmentManager.mContainer.onFindViewById(containerId);
            sceneRoot = sceneRoot2;
        } else {
            sceneRoot = null;
        }
        if (sceneRoot == null) {
            return;
        }
        Fragment inFragment = fragments.lastIn;
        Fragment outFragment = fragments.firstOut;
        boolean inIsPop = fragments.lastInIsPop;
        boolean outIsPop = fragments.firstOutIsPop;
        ArrayList<View> sharedElementsIn = new ArrayList<>();
        ArrayList<View> sharedElementsOut = new ArrayList<>();
        Transition enterTransition = getEnterTransition(inFragment, inIsPop);
        Transition exitTransition2 = getExitTransition(outFragment, outIsPop);
        TransitionSet sharedElementTransition = configureSharedElementsReordered(sceneRoot, nonExistentView, nameOverrides, fragments, sharedElementsOut, sharedElementsIn, enterTransition, exitTransition2);
        if (enterTransition == null && sharedElementTransition == null) {
            exitTransition = exitTransition2;
            if (exitTransition == null) {
                return;
            }
        } else {
            exitTransition = exitTransition2;
        }
        ArrayList<View> exitingViews = configureEnteringExitingViews(exitTransition, outFragment, sharedElementsOut, nonExistentView);
        ArrayList<View> enteringViews = configureEnteringExitingViews(enterTransition, inFragment, sharedElementsIn, nonExistentView);
        setViewVisibility(enteringViews, 4);
        Transition transition = mergeTransitions(enterTransition, exitTransition, sharedElementTransition, inFragment, inIsPop);
        if (transition != null) {
            replaceHide(exitTransition, outFragment, exitingViews);
            transition.setNameOverrides(nameOverrides);
            scheduleRemoveTargets(transition, enterTransition, enteringViews, exitTransition, exitingViews, sharedElementTransition, sharedElementsIn);
            TransitionManager.beginDelayedTransition(sceneRoot, transition);
            setViewVisibility(enteringViews, 0);
            if (sharedElementTransition != null) {
                sharedElementTransition.getTargets().clear();
                sharedElementTransition.getTargets().addAll(sharedElementsIn);
                replaceTargets(sharedElementTransition, sharedElementsOut, sharedElementsIn);
            }
        }
    }

    private static void configureTransitionsOrdered(FragmentManagerImpl fragmentManager, int containerId, FragmentContainerTransition fragments, View nonExistentView, ArrayMap<String, String> nameOverrides) {
        ViewGroup sceneRoot;
        Transition exitTransition;
        if (fragmentManager.mContainer.onHasView()) {
            ViewGroup sceneRoot2 = (ViewGroup) fragmentManager.mContainer.onFindViewById(containerId);
            sceneRoot = sceneRoot2;
        } else {
            sceneRoot = null;
        }
        if (sceneRoot == null) {
            return;
        }
        Fragment inFragment = fragments.lastIn;
        Fragment outFragment = fragments.firstOut;
        boolean inIsPop = fragments.lastInIsPop;
        boolean outIsPop = fragments.firstOutIsPop;
        Transition enterTransition = getEnterTransition(inFragment, inIsPop);
        Transition exitTransition2 = getExitTransition(outFragment, outIsPop);
        ArrayList<View> sharedElementsOut = new ArrayList<>();
        ArrayList<View> sharedElementsIn = new ArrayList<>();
        TransitionSet sharedElementTransition = configureSharedElementsOrdered(sceneRoot, nonExistentView, nameOverrides, fragments, sharedElementsOut, sharedElementsIn, enterTransition, exitTransition2);
        if (enterTransition == null && sharedElementTransition == null) {
            exitTransition = exitTransition2;
            if (exitTransition == null) {
                return;
            }
        } else {
            exitTransition = exitTransition2;
        }
        ArrayList<View> exitingViews = configureEnteringExitingViews(exitTransition, outFragment, sharedElementsOut, nonExistentView);
        exitTransition = (exitingViews == null || exitingViews.isEmpty()) ? null : null;
        if (enterTransition != null) {
            enterTransition.addTarget(nonExistentView);
        }
        Transition transition = mergeTransitions(enterTransition, exitTransition, sharedElementTransition, inFragment, fragments.lastInIsPop);
        if (transition != null) {
            transition.setNameOverrides(nameOverrides);
            ArrayList<View> enteringViews = new ArrayList<>();
            scheduleRemoveTargets(transition, enterTransition, enteringViews, exitTransition, exitingViews, sharedElementTransition, sharedElementsIn);
            scheduleTargetChange(sceneRoot, inFragment, nonExistentView, sharedElementsIn, enterTransition, enteringViews, exitTransition, exitingViews);
            TransitionManager.beginDelayedTransition(sceneRoot, transition);
        }
    }

    private static void replaceHide(Transition exitTransition, Fragment exitingFragment, final ArrayList<View> exitingViews) {
        if (exitingFragment != null && exitTransition != null && exitingFragment.mAdded && exitingFragment.mHidden && exitingFragment.mHiddenChanged) {
            exitingFragment.setHideReplaced(true);
            final View fragmentView = exitingFragment.getView();
            OneShotPreDrawListener.add(exitingFragment.mContainer, new Runnable() { // from class: android.app.FragmentTransition$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    FragmentTransition.setViewVisibility(exitingViews, 4);
                }
            });
            exitTransition.addListener(new TransitionListenerAdapter() { // from class: android.app.FragmentTransition.1
                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    View.this.setVisibility(8);
                    FragmentTransition.setViewVisibility(exitingViews, 0);
                }
            });
        }
    }

    private static void scheduleTargetChange(ViewGroup sceneRoot, final Fragment inFragment, final View nonExistentView, final ArrayList<View> sharedElementsIn, final Transition enterTransition, final ArrayList<View> enteringViews, final Transition exitTransition, final ArrayList<View> exitingViews) {
        OneShotPreDrawListener.add(sceneRoot, new Runnable() { // from class: android.app.FragmentTransition$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FragmentTransition.lambda$scheduleTargetChange$1(Transition.this, nonExistentView, inFragment, sharedElementsIn, enteringViews, exitingViews, exitTransition);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$scheduleTargetChange$1(Transition enterTransition, View nonExistentView, Fragment inFragment, ArrayList sharedElementsIn, ArrayList enteringViews, ArrayList exitingViews, Transition exitTransition) {
        if (enterTransition != null) {
            enterTransition.removeTarget(nonExistentView);
            ArrayList<View> views = configureEnteringExitingViews(enterTransition, inFragment, sharedElementsIn, nonExistentView);
            enteringViews.addAll(views);
        }
        if (exitingViews != null) {
            if (exitTransition != null) {
                ArrayList<View> tempExiting = new ArrayList<>();
                tempExiting.add(nonExistentView);
                replaceTargets(exitTransition, exitingViews, tempExiting);
            }
            exitingViews.clear();
            exitingViews.add(nonExistentView);
        }
    }

    private static TransitionSet getSharedElementTransition(Fragment inFragment, Fragment outFragment, boolean isPop) {
        Transition sharedElementEnterTransition;
        if (inFragment == null || outFragment == null) {
            return null;
        }
        if (isPop) {
            sharedElementEnterTransition = outFragment.getSharedElementReturnTransition();
        } else {
            sharedElementEnterTransition = inFragment.getSharedElementEnterTransition();
        }
        Transition transition = cloneTransition(sharedElementEnterTransition);
        if (transition == null) {
            return null;
        }
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(transition);
        return transitionSet;
    }

    private static Transition getEnterTransition(Fragment inFragment, boolean isPop) {
        if (inFragment == null) {
            return null;
        }
        return cloneTransition(isPop ? inFragment.getReenterTransition() : inFragment.getEnterTransition());
    }

    private static Transition getExitTransition(Fragment outFragment, boolean isPop) {
        if (outFragment == null) {
            return null;
        }
        return cloneTransition(isPop ? outFragment.getReturnTransition() : outFragment.getExitTransition());
    }

    private static Transition cloneTransition(Transition transition) {
        if (transition != null) {
            return transition.mo4799clone();
        }
        return transition;
    }

    private static TransitionSet configureSharedElementsReordered(ViewGroup sceneRoot, View nonExistentView, ArrayMap<String, String> nameOverrides, FragmentContainerTransition fragments, ArrayList<View> sharedElementsOut, ArrayList<View> sharedElementsIn, Transition enterTransition, Transition exitTransition) {
        TransitionSet sharedElementTransition;
        TransitionSet sharedElementTransition2;
        View epicenterView;
        Rect epicenter;
        final Fragment inFragment = fragments.lastIn;
        final Fragment outFragment = fragments.firstOut;
        if (inFragment != null) {
            inFragment.getView().setVisibility(0);
        }
        if (inFragment != null && outFragment != null) {
            final boolean inIsPop = fragments.lastInIsPop;
            if (nameOverrides.isEmpty()) {
                sharedElementTransition = null;
            } else {
                sharedElementTransition = getSharedElementTransition(inFragment, outFragment, inIsPop);
            }
            ArrayMap<String, View> outSharedElements = captureOutSharedElements(nameOverrides, sharedElementTransition, fragments);
            final ArrayMap<String, View> inSharedElements = captureInSharedElements(nameOverrides, sharedElementTransition, fragments);
            if (nameOverrides.isEmpty()) {
                if (outSharedElements != null) {
                    outSharedElements.clear();
                }
                if (inSharedElements != null) {
                    inSharedElements.clear();
                }
                sharedElementTransition2 = null;
            } else {
                addSharedElementsWithMatchingNames(sharedElementsOut, outSharedElements, nameOverrides.keySet());
                addSharedElementsWithMatchingNames(sharedElementsIn, inSharedElements, nameOverrides.values());
                sharedElementTransition2 = sharedElementTransition;
            }
            if (enterTransition == null && exitTransition == null && sharedElementTransition2 == null) {
                return null;
            }
            callSharedElementStartEnd(inFragment, outFragment, inIsPop, outSharedElements, true);
            if (sharedElementTransition2 != null) {
                sharedElementsIn.add(nonExistentView);
                setSharedElementTargets(sharedElementTransition2, nonExistentView, sharedElementsOut);
                boolean outIsPop = fragments.firstOutIsPop;
                BackStackRecord outTransaction = fragments.firstOutTransaction;
                setOutEpicenter(sharedElementTransition2, exitTransition, outSharedElements, outIsPop, outTransaction);
                final Rect epicenter2 = new Rect();
                epicenterView = getInEpicenterView(inSharedElements, fragments, enterTransition, inIsPop);
                if (epicenterView != null) {
                    enterTransition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.app.FragmentTransition.2
                        @Override // android.transition.Transition.EpicenterCallback
                        public Rect onGetEpicenter(Transition transition) {
                            return Rect.this;
                        }
                    });
                }
                epicenter = epicenter2;
            } else {
                epicenterView = null;
                epicenter = null;
            }
            TransitionSet sharedElementTransition3 = sharedElementTransition2;
            final View view = epicenterView;
            final Rect rect = epicenter;
            OneShotPreDrawListener.add(sceneRoot, new Runnable() { // from class: android.app.FragmentTransition$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FragmentTransition.lambda$configureSharedElementsReordered$2(Fragment.this, outFragment, inIsPop, inSharedElements, view, rect);
                }
            });
            return sharedElementTransition3;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$configureSharedElementsReordered$2(Fragment inFragment, Fragment outFragment, boolean inIsPop, ArrayMap inSharedElements, View epicenterView, Rect epicenter) {
        callSharedElementStartEnd(inFragment, outFragment, inIsPop, inSharedElements, false);
        if (epicenterView != null) {
            epicenterView.getBoundsOnScreen(epicenter);
        }
    }

    private static void addSharedElementsWithMatchingNames(ArrayList<View> views, ArrayMap<String, View> sharedElements, Collection<String> nameOverridesSet) {
        for (int i = sharedElements.size() - 1; i >= 0; i--) {
            View view = sharedElements.valueAt(i);
            if (view != null && nameOverridesSet.contains(view.getTransitionName())) {
                views.add(view);
            }
        }
    }

    private static TransitionSet configureSharedElementsOrdered(ViewGroup sceneRoot, final View nonExistentView, final ArrayMap<String, String> nameOverrides, final FragmentContainerTransition fragments, final ArrayList<View> sharedElementsOut, final ArrayList<View> sharedElementsIn, final Transition enterTransition, Transition exitTransition) {
        TransitionSet sharedElementTransition;
        TransitionSet sharedElementTransition2;
        Rect inEpicenter;
        final Fragment inFragment = fragments.lastIn;
        final Fragment outFragment = fragments.firstOut;
        if (inFragment != null && outFragment != null) {
            final boolean inIsPop = fragments.lastInIsPop;
            if (nameOverrides.isEmpty()) {
                sharedElementTransition = null;
            } else {
                sharedElementTransition = getSharedElementTransition(inFragment, outFragment, inIsPop);
            }
            ArrayMap<String, View> outSharedElements = captureOutSharedElements(nameOverrides, sharedElementTransition, fragments);
            if (nameOverrides.isEmpty()) {
                sharedElementTransition2 = null;
            } else {
                sharedElementsOut.addAll(outSharedElements.values());
                sharedElementTransition2 = sharedElementTransition;
            }
            if (enterTransition == null && exitTransition == null && sharedElementTransition2 == null) {
                return null;
            }
            callSharedElementStartEnd(inFragment, outFragment, inIsPop, outSharedElements, true);
            if (sharedElementTransition2 != null) {
                final Rect inEpicenter2 = new Rect();
                setSharedElementTargets(sharedElementTransition2, nonExistentView, sharedElementsOut);
                boolean outIsPop = fragments.firstOutIsPop;
                BackStackRecord outTransaction = fragments.firstOutTransaction;
                setOutEpicenter(sharedElementTransition2, exitTransition, outSharedElements, outIsPop, outTransaction);
                if (enterTransition != null) {
                    enterTransition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.app.FragmentTransition.3
                        @Override // android.transition.Transition.EpicenterCallback
                        public Rect onGetEpicenter(Transition transition) {
                            if (Rect.this.isEmpty()) {
                                return null;
                            }
                            return Rect.this;
                        }
                    });
                }
                inEpicenter = inEpicenter2;
            } else {
                inEpicenter = null;
            }
            final TransitionSet finalSharedElementTransition = sharedElementTransition2;
            TransitionSet sharedElementTransition3 = sharedElementTransition2;
            final Rect rect = inEpicenter;
            OneShotPreDrawListener.add(sceneRoot, new Runnable() { // from class: android.app.FragmentTransition$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    FragmentTransition.lambda$configureSharedElementsOrdered$3(ArrayMap.this, finalSharedElementTransition, fragments, sharedElementsIn, nonExistentView, inFragment, outFragment, inIsPop, sharedElementsOut, enterTransition, rect);
                }
            });
            return sharedElementTransition3;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$configureSharedElementsOrdered$3(ArrayMap nameOverrides, TransitionSet finalSharedElementTransition, FragmentContainerTransition fragments, ArrayList sharedElementsIn, View nonExistentView, Fragment inFragment, Fragment outFragment, boolean inIsPop, ArrayList sharedElementsOut, Transition enterTransition, Rect inEpicenter) {
        ArrayMap<String, View> inSharedElements = captureInSharedElements(nameOverrides, finalSharedElementTransition, fragments);
        if (inSharedElements != null) {
            sharedElementsIn.addAll(inSharedElements.values());
            sharedElementsIn.add(nonExistentView);
        }
        callSharedElementStartEnd(inFragment, outFragment, inIsPop, inSharedElements, false);
        if (finalSharedElementTransition != null) {
            finalSharedElementTransition.getTargets().clear();
            finalSharedElementTransition.getTargets().addAll(sharedElementsIn);
            replaceTargets(finalSharedElementTransition, sharedElementsOut, sharedElementsIn);
            View inEpicenterView = getInEpicenterView(inSharedElements, fragments, enterTransition, inIsPop);
            if (inEpicenterView != null) {
                inEpicenterView.getBoundsOnScreen(inEpicenter);
            }
        }
    }

    private static ArrayMap<String, View> captureOutSharedElements(ArrayMap<String, String> nameOverrides, TransitionSet sharedElementTransition, FragmentContainerTransition fragments) {
        SharedElementCallback sharedElementCallback;
        ArrayList<String> names;
        if (nameOverrides.isEmpty() || sharedElementTransition == null) {
            nameOverrides.clear();
            return null;
        }
        Fragment outFragment = fragments.firstOut;
        ArrayMap<String, View> outSharedElements = new ArrayMap<>();
        outFragment.getView().findNamedViews(outSharedElements);
        BackStackRecord outTransaction = fragments.firstOutTransaction;
        if (fragments.firstOutIsPop) {
            sharedElementCallback = outFragment.getEnterTransitionCallback();
            names = outTransaction.mSharedElementTargetNames;
        } else {
            sharedElementCallback = outFragment.getExitTransitionCallback();
            names = outTransaction.mSharedElementSourceNames;
        }
        outSharedElements.retainAll(names);
        if (sharedElementCallback != null) {
            sharedElementCallback.onMapSharedElements(names, outSharedElements);
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = names.get(i);
                View view = outSharedElements.get(name);
                if (view == null) {
                    nameOverrides.remove(name);
                } else if (!name.equals(view.getTransitionName())) {
                    String targetValue = nameOverrides.remove(name);
                    nameOverrides.put(view.getTransitionName(), targetValue);
                }
            }
        } else {
            nameOverrides.retainAll(outSharedElements.keySet());
        }
        return outSharedElements;
    }

    private static ArrayMap<String, View> captureInSharedElements(ArrayMap<String, String> nameOverrides, TransitionSet sharedElementTransition, FragmentContainerTransition fragments) {
        SharedElementCallback sharedElementCallback;
        ArrayList<String> names;
        String key;
        Fragment inFragment = fragments.lastIn;
        View fragmentView = inFragment.getView();
        if (nameOverrides.isEmpty() || sharedElementTransition == null || fragmentView == null) {
            nameOverrides.clear();
            return null;
        }
        ArrayMap<String, View> inSharedElements = new ArrayMap<>();
        fragmentView.findNamedViews(inSharedElements);
        BackStackRecord inTransaction = fragments.lastInTransaction;
        if (fragments.lastInIsPop) {
            sharedElementCallback = inFragment.getExitTransitionCallback();
            names = inTransaction.mSharedElementSourceNames;
        } else {
            sharedElementCallback = inFragment.getEnterTransitionCallback();
            names = inTransaction.mSharedElementTargetNames;
        }
        if (names != null) {
            inSharedElements.retainAll(names);
        }
        if (names != null && sharedElementCallback != null) {
            sharedElementCallback.onMapSharedElements(names, inSharedElements);
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = names.get(i);
                View view = inSharedElements.get(name);
                if (view == null) {
                    String key2 = findKeyForValue(nameOverrides, name);
                    if (key2 != null) {
                        nameOverrides.remove(key2);
                    }
                } else if (!name.equals(view.getTransitionName()) && (key = findKeyForValue(nameOverrides, name)) != null) {
                    nameOverrides.put(key, view.getTransitionName());
                }
            }
        } else {
            retainValues(nameOverrides, inSharedElements);
        }
        return inSharedElements;
    }

    private static String findKeyForValue(ArrayMap<String, String> map, String value) {
        int numElements = map.size();
        for (int i = 0; i < numElements; i++) {
            if (value.equals(map.valueAt(i))) {
                return map.keyAt(i);
            }
        }
        return null;
    }

    private static View getInEpicenterView(ArrayMap<String, View> inSharedElements, FragmentContainerTransition fragments, Transition enterTransition, boolean inIsPop) {
        BackStackRecord inTransaction = fragments.lastInTransaction;
        if (enterTransition != null && inSharedElements != null && inTransaction.mSharedElementSourceNames != null && !inTransaction.mSharedElementSourceNames.isEmpty()) {
            String targetName = inIsPop ? inTransaction.mSharedElementSourceNames.get(0) : inTransaction.mSharedElementTargetNames.get(0);
            return inSharedElements.get(targetName);
        }
        return null;
    }

    private static void setOutEpicenter(TransitionSet sharedElementTransition, Transition exitTransition, ArrayMap<String, View> outSharedElements, boolean outIsPop, BackStackRecord outTransaction) {
        if (outTransaction.mSharedElementSourceNames != null && !outTransaction.mSharedElementSourceNames.isEmpty()) {
            String sourceName = outIsPop ? outTransaction.mSharedElementTargetNames.get(0) : outTransaction.mSharedElementSourceNames.get(0);
            View outEpicenterView = outSharedElements.get(sourceName);
            setEpicenter(sharedElementTransition, outEpicenterView);
            if (exitTransition != null) {
                setEpicenter(exitTransition, outEpicenterView);
            }
        }
    }

    private static void setEpicenter(Transition transition, View view) {
        if (view != null) {
            final Rect epicenter = new Rect();
            view.getBoundsOnScreen(epicenter);
            transition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.app.FragmentTransition.4
                @Override // android.transition.Transition.EpicenterCallback
                public Rect onGetEpicenter(Transition transition2) {
                    return Rect.this;
                }
            });
        }
    }

    private static void retainValues(ArrayMap<String, String> nameOverrides, ArrayMap<String, View> namedViews) {
        for (int i = nameOverrides.size() - 1; i >= 0; i--) {
            String targetName = nameOverrides.valueAt(i);
            if (!namedViews.containsKey(targetName)) {
                nameOverrides.removeAt(i);
            }
        }
    }

    private static void callSharedElementStartEnd(Fragment inFragment, Fragment outFragment, boolean isPop, ArrayMap<String, View> sharedElements, boolean isStart) {
        SharedElementCallback sharedElementCallback;
        if (isPop) {
            sharedElementCallback = outFragment.getEnterTransitionCallback();
        } else {
            sharedElementCallback = inFragment.getEnterTransitionCallback();
        }
        if (sharedElementCallback != null) {
            ArrayList<View> views = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            int count = sharedElements == null ? 0 : sharedElements.size();
            for (int i = 0; i < count; i++) {
                names.add(sharedElements.keyAt(i));
                views.add(sharedElements.valueAt(i));
            }
            if (isStart) {
                sharedElementCallback.onSharedElementStart(names, views, null);
            } else {
                sharedElementCallback.onSharedElementEnd(names, views, null);
            }
        }
    }

    private static void setSharedElementTargets(TransitionSet transition, View nonExistentView, ArrayList<View> sharedViews) {
        List<View> views = transition.getTargets();
        views.clear();
        int count = sharedViews.size();
        for (int i = 0; i < count; i++) {
            View view = sharedViews.get(i);
            bfsAddViewChildren(views, view);
        }
        views.add(nonExistentView);
        sharedViews.add(nonExistentView);
        addTargets(transition, sharedViews);
    }

    private static void bfsAddViewChildren(List<View> views, View startView) {
        int startIndex = views.size();
        if (containedBeforeIndex(views, startView, startIndex)) {
            return;
        }
        views.add(startView);
        for (int index = startIndex; index < views.size(); index++) {
            View view = views.get(index);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int childIndex = 0; childIndex < childCount; childIndex++) {
                    View child = viewGroup.getChildAt(childIndex);
                    if (!containedBeforeIndex(views, child, startIndex)) {
                        views.add(child);
                    }
                }
            }
        }
    }

    private static boolean containedBeforeIndex(List<View> views, View view, int maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            if (views.get(i) == view) {
                return true;
            }
        }
        return false;
    }

    private static void scheduleRemoveTargets(Transition overalTransition, final Transition enterTransition, final ArrayList<View> enteringViews, final Transition exitTransition, final ArrayList<View> exitingViews, final TransitionSet sharedElementTransition, final ArrayList<View> sharedElementsIn) {
        overalTransition.addListener(new TransitionListenerAdapter() { // from class: android.app.FragmentTransition.5
            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionStart(Transition transition) {
                Transition transition2 = Transition.this;
                if (transition2 != null) {
                    FragmentTransition.replaceTargets(transition2, enteringViews, null);
                }
                Transition transition3 = exitTransition;
                if (transition3 != null) {
                    FragmentTransition.replaceTargets(transition3, exitingViews, null);
                }
                TransitionSet transitionSet = sharedElementTransition;
                if (transitionSet != null) {
                    FragmentTransition.replaceTargets(transitionSet, sharedElementsIn, null);
                }
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }
        });
    }

    public static void replaceTargets(Transition transition, ArrayList<View> oldTargets, ArrayList<View> newTargets) {
        List<View> targets;
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (int i = 0; i < numTransitions; i++) {
                Transition child = set.getTransitionAt(i);
                replaceTargets(child, oldTargets, newTargets);
            }
        } else if (!hasSimpleTarget(transition) && (targets = transition.getTargets()) != null && targets.size() == oldTargets.size() && targets.containsAll(oldTargets)) {
            int targetCount = newTargets == null ? 0 : newTargets.size();
            for (int i2 = 0; i2 < targetCount; i2++) {
                transition.addTarget(newTargets.get(i2));
            }
            int i3 = oldTargets.size();
            for (int i4 = i3 - 1; i4 >= 0; i4--) {
                transition.removeTarget(oldTargets.get(i4));
            }
        }
    }

    public static void addTargets(Transition transition, ArrayList<View> views) {
        if (transition == null) {
            return;
        }
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (int i = 0; i < numTransitions; i++) {
                Transition child = set.getTransitionAt(i);
                addTargets(child, views);
            }
        } else if (!hasSimpleTarget(transition)) {
            List<View> targets = transition.getTargets();
            if (isNullOrEmpty(targets)) {
                int numViews = views.size();
                for (int i2 = 0; i2 < numViews; i2++) {
                    transition.addTarget(views.get(i2));
                }
            }
        }
    }

    private static boolean hasSimpleTarget(Transition transition) {
        return (isNullOrEmpty(transition.getTargetIds()) && isNullOrEmpty(transition.getTargetNames()) && isNullOrEmpty(transition.getTargetTypes())) ? false : true;
    }

    private static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    private static ArrayList<View> configureEnteringExitingViews(Transition transition, Fragment fragment, ArrayList<View> sharedElements, View nonExistentView) {
        ArrayList<View> viewList = null;
        if (transition != null) {
            viewList = new ArrayList<>();
            View root = fragment.getView();
            if (root != null) {
                root.captureTransitioningViews(viewList);
            }
            if (sharedElements != null) {
                viewList.removeAll(sharedElements);
            }
            if (!viewList.isEmpty()) {
                viewList.add(nonExistentView);
                addTargets(transition, viewList);
            }
        }
        return viewList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setViewVisibility(ArrayList<View> views, int visibility) {
        if (views == null) {
            return;
        }
        for (int i = views.size() - 1; i >= 0; i--) {
            View view = views.get(i);
            view.setVisibility(visibility);
        }
    }

    private static Transition mergeTransitions(Transition enterTransition, Transition exitTransition, Transition sharedElementTransition, Fragment inFragment, boolean isPop) {
        boolean overlap = true;
        if (enterTransition != null && exitTransition != null && inFragment != null) {
            overlap = isPop ? inFragment.getAllowReturnTransitionOverlap() : inFragment.getAllowEnterTransitionOverlap();
        }
        if (overlap) {
            TransitionSet transitionSet = new TransitionSet();
            if (enterTransition != null) {
                transitionSet.addTransition(enterTransition);
            }
            if (exitTransition != null) {
                transitionSet.addTransition(exitTransition);
            }
            if (sharedElementTransition != null) {
                transitionSet.addTransition(sharedElementTransition);
                return transitionSet;
            }
            return transitionSet;
        }
        Transition staggered = null;
        if (exitTransition != null && enterTransition != null) {
            staggered = new TransitionSet().addTransition(exitTransition).addTransition(enterTransition).setOrdering(1);
        } else if (exitTransition != null) {
            staggered = exitTransition;
        } else if (enterTransition != null) {
            staggered = enterTransition;
        }
        if (sharedElementTransition != null) {
            TransitionSet together = new TransitionSet();
            if (staggered != null) {
                together.addTransition(staggered);
            }
            together.addTransition(sharedElementTransition);
            return together;
        }
        return staggered;
    }

    public static void calculateFragments(BackStackRecord transaction, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isReordered) {
        int numOps = transaction.mOps.size();
        for (int opNum = 0; opNum < numOps; opNum++) {
            BackStackRecord.C0154Op op = transaction.mOps.get(opNum);
            addToFirstInLastOut(transaction, op, transitioningFragments, false, isReordered);
        }
    }

    public static void calculatePopFragments(BackStackRecord transaction, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isReordered) {
        if (!transaction.mManager.mContainer.onHasView()) {
            return;
        }
        int numOps = transaction.mOps.size();
        for (int opNum = numOps - 1; opNum >= 0; opNum--) {
            BackStackRecord.C0154Op op = transaction.mOps.get(opNum);
            addToFirstInLastOut(transaction, op, transitioningFragments, true, isReordered);
        }
    }

    private static void addToFirstInLastOut(BackStackRecord transaction, BackStackRecord.C0154Op op, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isPop, boolean isReorderedTransaction) {
        int containerId;
        boolean setLastIn;
        boolean setLastIn2;
        boolean wasRemoved;
        boolean setFirstOut;
        boolean wasAdded;
        boolean setFirstOut2;
        boolean setFirstOut3;
        boolean setLastIn3;
        FragmentContainerTransition containerTransition;
        Fragment fragment;
        FragmentContainerTransition containerTransition2;
        FragmentContainerTransition containerTransition3;
        FragmentContainerTransition containerTransition4;
        Fragment fragment2 = op.fragment;
        if (fragment2 == null || (containerId = fragment2.mContainerId) == 0) {
            return;
        }
        int command = isPop ? INVERSE_OPS[op.cmd] : op.cmd;
        boolean z = false;
        switch (command) {
            case 1:
            case 7:
                if (isReorderedTransaction) {
                    setLastIn = fragment2.mIsNewlyAdded;
                } else {
                    if (!fragment2.mAdded && !fragment2.mHidden) {
                        z = true;
                    }
                    setLastIn = z;
                }
                setLastIn2 = setLastIn;
                wasRemoved = false;
                setFirstOut = false;
                wasAdded = true;
                break;
            case 2:
            default:
                setLastIn2 = false;
                wasRemoved = false;
                setFirstOut = false;
                wasAdded = false;
                break;
            case 3:
            case 6:
                if (isReorderedTransaction) {
                    if (!fragment2.mAdded && fragment2.mView != null && fragment2.mView.getVisibility() == 0 && fragment2.mView.getTransitionAlpha() > 0.0f) {
                        z = true;
                    }
                    setFirstOut2 = z;
                } else {
                    if (fragment2.mAdded && !fragment2.mHidden) {
                        z = true;
                    }
                    setFirstOut2 = z;
                }
                setLastIn2 = false;
                wasRemoved = true;
                setFirstOut = setFirstOut2;
                wasAdded = false;
                break;
            case 4:
                if (isReorderedTransaction) {
                    if (fragment2.mHiddenChanged && fragment2.mAdded && fragment2.mHidden) {
                        z = true;
                    }
                    setFirstOut3 = z;
                } else {
                    if (fragment2.mAdded && !fragment2.mHidden) {
                        z = true;
                    }
                    setFirstOut3 = z;
                }
                setLastIn2 = false;
                wasRemoved = true;
                setFirstOut = setFirstOut3;
                wasAdded = false;
                break;
            case 5:
                if (isReorderedTransaction) {
                    if (fragment2.mHiddenChanged && !fragment2.mHidden && fragment2.mAdded) {
                        z = true;
                    }
                    setLastIn3 = z;
                } else {
                    setLastIn3 = fragment2.mHidden;
                }
                setLastIn2 = setLastIn3;
                wasRemoved = false;
                setFirstOut = false;
                wasAdded = true;
                break;
        }
        FragmentContainerTransition containerTransition5 = transitioningFragments.get(containerId);
        if (!setLastIn2) {
            containerTransition = containerTransition5;
        } else {
            FragmentContainerTransition containerTransition6 = ensureContainer(containerTransition5, transitioningFragments, containerId);
            containerTransition6.lastIn = fragment2;
            containerTransition6.lastInIsPop = isPop;
            containerTransition6.lastInTransaction = transaction;
            containerTransition = containerTransition6;
        }
        if (isReorderedTransaction || !wasAdded) {
            fragment = null;
            containerTransition2 = containerTransition;
        } else {
            if (containerTransition != null && containerTransition.firstOut == fragment2) {
                containerTransition.firstOut = null;
            }
            FragmentManagerImpl manager = transaction.mManager;
            if (fragment2.mState >= 1 || manager.mCurState < 1) {
                fragment = null;
                containerTransition2 = containerTransition;
            } else if (manager.mHost.getContext().getApplicationInfo().targetSdkVersion < 24 || transaction.mReorderingAllowed) {
                fragment = null;
                containerTransition2 = containerTransition;
            } else {
                manager.makeActive(fragment2);
                containerTransition2 = containerTransition;
                fragment = null;
                manager.moveToState(fragment2, 1, 0, 0, false);
            }
        }
        if (setFirstOut) {
            containerTransition3 = containerTransition2;
            if (containerTransition3 == null || containerTransition3.firstOut == null) {
                containerTransition4 = ensureContainer(containerTransition3, transitioningFragments, containerId);
                containerTransition4.firstOut = fragment2;
                containerTransition4.firstOutIsPop = isPop;
                containerTransition4.firstOutTransaction = transaction;
                if (isReorderedTransaction && wasRemoved && containerTransition4 != null && containerTransition4.lastIn == fragment2) {
                    containerTransition4.lastIn = fragment;
                    return;
                }
                return;
            }
        } else {
            containerTransition3 = containerTransition2;
        }
        containerTransition4 = containerTransition3;
        if (isReorderedTransaction) {
        }
    }

    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition containerTransition, SparseArray<FragmentContainerTransition> transitioningFragments, int containerId) {
        if (containerTransition == null) {
            FragmentContainerTransition containerTransition2 = new FragmentContainerTransition();
            transitioningFragments.put(containerId, containerTransition2);
            return containerTransition2;
        }
        return containerTransition;
    }
}
