package com.android.uiautomator.core;

import android.app.UiAutomation;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
/* loaded from: classes.dex */
class QueryController {
    private static final boolean DEBUG;
    private static final String LOG_TAG;
    private static final boolean VERBOSE;
    private final UiAutomatorBridge mUiAutomatorBridge;
    private final Object mLock = new Object();
    private String mLastActivityName = null;
    private int mPatternCounter = 0;
    private int mPatternIndexer = 0;
    private int mLogIndent = 0;
    private int mLogParentIndent = 0;
    private String mLastTraversedText = "";

    static {
        String simpleName = QueryController.class.getSimpleName();
        LOG_TAG = simpleName;
        DEBUG = Log.isLoggable(simpleName, 3);
        VERBOSE = Log.isLoggable(simpleName, 2);
    }

    public QueryController(UiAutomatorBridge bridge) {
        this.mUiAutomatorBridge = bridge;
        bridge.setOnAccessibilityEventListener(new UiAutomation.OnAccessibilityEventListener() { // from class: com.android.uiautomator.core.QueryController.1
            @Override // android.app.UiAutomation.OnAccessibilityEventListener
            public void onAccessibilityEvent(AccessibilityEvent event) {
                synchronized (QueryController.this.mLock) {
                    switch (event.getEventType()) {
                        case 32:
                            if (event.getText() != null && event.getText().size() > 0 && event.getText().get(0) != null) {
                                QueryController.this.mLastActivityName = event.getText().get(0).toString();
                                break;
                            }
                            break;
                        case 131072:
                            if (event.getText() != null && event.getText().size() > 0 && event.getText().get(0) != null) {
                                QueryController.this.mLastTraversedText = event.getText().get(0).toString();
                            }
                            if (QueryController.DEBUG) {
                                Log.d(QueryController.LOG_TAG, "Last text selection reported: " + QueryController.this.mLastTraversedText);
                                break;
                            }
                            break;
                    }
                    QueryController.this.mLock.notifyAll();
                }
            }
        });
    }

    public String getLastTraversedText() {
        this.mUiAutomatorBridge.waitForIdle();
        synchronized (this.mLock) {
            if (this.mLastTraversedText.length() > 0) {
                return this.mLastTraversedText;
            }
            return null;
        }
    }

    public void clearLastTraversedText() {
        this.mUiAutomatorBridge.waitForIdle();
        synchronized (this.mLock) {
            this.mLastTraversedText = "";
        }
    }

    private void initializeNewSearch() {
        this.mPatternCounter = 0;
        this.mPatternIndexer = 0;
        this.mLogIndent = 0;
        this.mLogParentIndent = 0;
    }

    public int getPatternCount(UiSelector selector) {
        findAccessibilityNodeInfo(selector, true);
        return this.mPatternCounter;
    }

    public AccessibilityNodeInfo findAccessibilityNodeInfo(UiSelector selector) {
        return findAccessibilityNodeInfo(selector, false);
    }

    protected AccessibilityNodeInfo findAccessibilityNodeInfo(UiSelector selector, boolean isCounting) {
        this.mUiAutomatorBridge.waitForIdle();
        initializeNewSearch();
        if (DEBUG) {
            Log.d(LOG_TAG, "Searching: " + selector);
        }
        synchronized (this.mLock) {
            AccessibilityNodeInfo rootNode = getRootNode();
            if (rootNode == null) {
                Log.e(LOG_TAG, "Cannot proceed when root node is null. Aborted search");
                return null;
            }
            UiSelector uiSelector = new UiSelector(selector);
            return translateCompoundSelector(uiSelector, rootNode, isCounting);
        }
    }

    protected AccessibilityNodeInfo getRootNode() {
        AccessibilityNodeInfo rootNode = null;
        for (int x = 0; x < 4; x++) {
            rootNode = this.mUiAutomatorBridge.getRootInActiveWindow();
            if (rootNode != null) {
                return rootNode;
            }
            if (x < 3) {
                Log.e(LOG_TAG, "Got null root node from accessibility - Retrying...");
                SystemClock.sleep(250L);
            }
        }
        return rootNode;
    }

    private AccessibilityNodeInfo translateCompoundSelector(UiSelector selector, AccessibilityNodeInfo fromNode, boolean isCounting) {
        AccessibilityNodeInfo fromNode2;
        if (selector.hasContainerSelector()) {
            if (selector.getContainerSelector().hasContainerSelector()) {
                fromNode2 = translateCompoundSelector(selector.getContainerSelector(), fromNode, false);
                initializeNewSearch();
            } else {
                fromNode2 = translateReqularSelector(selector.getContainerSelector(), fromNode);
            }
        } else {
            fromNode2 = translateReqularSelector(selector, fromNode);
        }
        if (fromNode2 == null) {
            if (DEBUG) {
                Log.d(LOG_TAG, "Container selector not found: " + selector.dumpToString(false));
            }
            return null;
        }
        if (selector.hasPatternSelector()) {
            fromNode2 = translatePatternSelector(selector.getPatternSelector(), fromNode2, isCounting);
            if (isCounting) {
                Log.i(LOG_TAG, String.format("Counted %d instances of: %s", Integer.valueOf(this.mPatternCounter), selector));
                return null;
            } else if (fromNode2 == null) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "Pattern selector not found: " + selector.dumpToString(false));
                }
                return null;
            }
        }
        if ((selector.hasContainerSelector() || selector.hasPatternSelector()) && (selector.hasChildSelector() || selector.hasParentSelector())) {
            fromNode2 = translateReqularSelector(selector, fromNode2);
        }
        if (fromNode2 == null) {
            if (DEBUG) {
                Log.d(LOG_TAG, "Object Not Found for selector " + selector);
            }
            return null;
        }
        Log.i(LOG_TAG, String.format("Matched selector: %s <<==>> [%s]", selector, fromNode2));
        return fromNode2;
    }

    private AccessibilityNodeInfo translateReqularSelector(UiSelector selector, AccessibilityNodeInfo fromNode) {
        return findNodeRegularRecursive(selector, fromNode, 0);
    }

    private AccessibilityNodeInfo findNodeRegularRecursive(UiSelector subSelector, AccessibilityNodeInfo fromNode, int index) {
        if (subSelector.isMatchFor(fromNode, index)) {
            if (DEBUG) {
                Log.d(LOG_TAG, formatLog(String.format("%s", subSelector.dumpToString(false))));
            }
            if (subSelector.isLeaf()) {
                return fromNode;
            }
            if (subSelector.hasChildSelector()) {
                this.mLogIndent++;
                subSelector = subSelector.getChildSelector();
                if (subSelector == null) {
                    Log.e(LOG_TAG, "Error: A child selector without content");
                    return null;
                }
            } else if (subSelector.hasParentSelector()) {
                this.mLogIndent++;
                subSelector = subSelector.getParentSelector();
                if (subSelector == null) {
                    Log.e(LOG_TAG, "Error: A parent selector without content");
                    return null;
                }
                fromNode = fromNode.getParent();
                if (fromNode == null) {
                    return null;
                }
            }
        }
        int childCount = fromNode.getChildCount();
        boolean hasNullChild = false;
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = fromNode.getChild(i);
            if (childNode == null) {
                String str = LOG_TAG;
                Log.w(str, String.format("AccessibilityNodeInfo returned a null child (%d of %d)", Integer.valueOf(i), Integer.valueOf(childCount)));
                if (!hasNullChild) {
                    Log.w(str, String.format("parent = %s", fromNode.toString()));
                }
                hasNullChild = true;
            } else if (!childNode.isVisibleToUser()) {
                if (VERBOSE) {
                    Log.v(LOG_TAG, String.format("Skipping invisible child: %s", childNode.toString()));
                }
            } else {
                AccessibilityNodeInfo retNode = findNodeRegularRecursive(subSelector, childNode, i);
                if (retNode != null) {
                    return retNode;
                }
            }
        }
        return null;
    }

    private AccessibilityNodeInfo translatePatternSelector(UiSelector subSelector, AccessibilityNodeInfo fromNode, boolean isCounting) {
        if (subSelector.hasPatternSelector()) {
            if (isCounting) {
                this.mPatternIndexer = -1;
            } else {
                this.mPatternIndexer = subSelector.getInstance();
            }
            UiSelector subSelector2 = subSelector.getPatternSelector();
            if (subSelector2 == null) {
                Log.e(LOG_TAG, "Pattern portion of the selector is null or not defined");
                return null;
            }
            int i = this.mLogIndent + 1;
            this.mLogIndent = i;
            this.mLogParentIndent = i;
            return findNodePatternRecursive(subSelector2, fromNode, 0, subSelector2);
        }
        Log.e(LOG_TAG, "Selector must have a pattern selector defined");
        return null;
    }

    private AccessibilityNodeInfo findNodePatternRecursive(UiSelector subSelector, AccessibilityNodeInfo fromNode, int index, UiSelector originalPattern) {
        if (subSelector.isMatchFor(fromNode, index)) {
            if (subSelector.isLeaf()) {
                if (this.mPatternIndexer == 0) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, formatLog(String.format("%s", subSelector.dumpToString(false))));
                    }
                    return fromNode;
                }
                if (DEBUG) {
                    Log.d(LOG_TAG, formatLog(String.format("%s", subSelector.dumpToString(false))));
                }
                this.mPatternCounter++;
                this.mPatternIndexer--;
                subSelector = originalPattern;
                this.mLogIndent = this.mLogParentIndent;
            } else {
                if (DEBUG) {
                    Log.d(LOG_TAG, formatLog(String.format("%s", subSelector.dumpToString(false))));
                }
                if (subSelector.hasChildSelector()) {
                    this.mLogIndent++;
                    subSelector = subSelector.getChildSelector();
                    if (subSelector == null) {
                        Log.e(LOG_TAG, "Error: A child selector without content");
                        return null;
                    }
                } else if (subSelector.hasParentSelector()) {
                    this.mLogIndent++;
                    subSelector = subSelector.getParentSelector();
                    if (subSelector == null) {
                        Log.e(LOG_TAG, "Error: A parent selector without content");
                        return null;
                    }
                    fromNode = fromNode.getParent();
                    if (fromNode == null) {
                        return null;
                    }
                }
            }
        }
        int childCount = fromNode.getChildCount();
        boolean hasNullChild = false;
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = fromNode.getChild(i);
            if (childNode == null) {
                String str = LOG_TAG;
                Log.w(str, String.format("AccessibilityNodeInfo returned a null child (%d of %d)", Integer.valueOf(i), Integer.valueOf(childCount)));
                if (!hasNullChild) {
                    Log.w(str, String.format("parent = %s", fromNode.toString()));
                }
                hasNullChild = true;
            } else if (!childNode.isVisibleToUser()) {
                if (DEBUG) {
                    Log.d(LOG_TAG, String.format("Skipping invisible child: %s", childNode.toString()));
                }
            } else {
                AccessibilityNodeInfo retNode = findNodePatternRecursive(subSelector, childNode, i, originalPattern);
                if (retNode != null) {
                    return retNode;
                }
            }
        }
        return null;
    }

    public AccessibilityNodeInfo getAccessibilityRootNode() {
        return this.mUiAutomatorBridge.getRootInActiveWindow();
    }

    @Deprecated
    public String getCurrentActivityName() {
        String str;
        this.mUiAutomatorBridge.waitForIdle();
        synchronized (this.mLock) {
            str = this.mLastActivityName;
        }
        return str;
    }

    public String getCurrentPackageName() {
        this.mUiAutomatorBridge.waitForIdle();
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null || rootNode.getPackageName() == null) {
            return null;
        }
        return rootNode.getPackageName().toString();
    }

    private String formatLog(String str) {
        int i;
        StringBuilder l = new StringBuilder();
        int space = 0;
        while (true) {
            i = this.mLogIndent;
            if (space >= i) {
                break;
            }
            l.append(". . ");
            space++;
        }
        if (i > 0) {
            l.append(String.format(". . [%d]: %s", Integer.valueOf(this.mPatternCounter), str));
        } else {
            l.append(String.format(". . [%d]: %s", Integer.valueOf(this.mPatternCounter), str));
        }
        return l.toString();
    }
}
