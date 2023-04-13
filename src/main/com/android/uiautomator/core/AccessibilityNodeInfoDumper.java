package com.android.uiautomator.core;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class AccessibilityNodeInfoDumper {
    private static final String LOGTAG = AccessibilityNodeInfoDumper.class.getSimpleName();
    private static final String[] NAF_EXCLUDED_CLASSES = {GridView.class.getName(), GridLayout.class.getName(), ListView.class.getName(), TableLayout.class.getName()};

    public static void dumpWindowToFile(AccessibilityNodeInfo root, int rotation, int width, int height) {
        File baseDir = new File(Environment.getDataDirectory(), "local");
        if (!baseDir.exists()) {
            baseDir.mkdir();
            baseDir.setExecutable(true, false);
            baseDir.setWritable(true, false);
            baseDir.setReadable(true, false);
        }
        dumpWindowToFile(root, new File(new File(Environment.getDataDirectory(), "local"), "window_dump.xml"), rotation, width, height);
    }

    public static void dumpWindowToFile(AccessibilityNodeInfo root, File dumpFile, int rotation, int width, int height) {
        if (root == null) {
            return;
        }
        long startTime = SystemClock.uptimeMillis();
        try {
            FileWriter writer = new FileWriter(dumpFile);
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();
            serializer.setOutput(stringWriter);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "hierarchy");
            serializer.attribute("", "rotation", Integer.toString(rotation));
            dumpNodeRec(root, serializer, 0, width, height);
            serializer.endTag("", "hierarchy");
            serializer.endDocument();
            writer.write(stringWriter.toString());
            writer.close();
        } catch (IOException e) {
            Log.e(LOGTAG, "failed to dump window to file", e);
        }
        long endTime = SystemClock.uptimeMillis();
        Log.w(LOGTAG, "Fetch time: " + (endTime - startTime) + "ms");
    }

    public static void dumpWindowsToFile(SparseArray<List<AccessibilityWindowInfo>> allWindows, File dumpFile, DisplayManagerGlobal displayManager) {
        long startTime;
        int d;
        int nd;
        SparseArray<List<AccessibilityWindowInfo>> sparseArray = allWindows;
        if (allWindows.size() == 0) {
            return;
        }
        long startTime2 = SystemClock.uptimeMillis();
        try {
            FileWriter writer = new FileWriter(dumpFile);
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();
            serializer.setOutput(stringWriter);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "displays");
            int nd2 = allWindows.size();
            int d2 = 0;
            while (d2 < nd2) {
                int displayId = sparseArray.keyAt(d2);
                Display display = displayManager.getRealDisplay(displayId);
                if (display == null) {
                    startTime = startTime2;
                    d = d2;
                    nd = nd2;
                } else {
                    List<AccessibilityWindowInfo> windows = sparseArray.valueAt(d2);
                    if (windows.isEmpty()) {
                        startTime = startTime2;
                        d = d2;
                        nd = nd2;
                    } else {
                        serializer.startTag("", "display");
                        serializer.attribute("", "id", Integer.toString(displayId));
                        int rotation = display.getRotation();
                        List<AccessibilityWindowInfo> windows2 = windows;
                        Point size = new Point();
                        display.getRealSize(size);
                        int n = windows2.size();
                        startTime = startTime2;
                        int i = 0;
                        while (true) {
                            int n2 = n;
                            if (i >= n2) {
                                break;
                            }
                            try {
                                int n3 = size.x;
                                dumpWindowRec(windows2.get(i), serializer, i, n3, size.y, rotation);
                                i++;
                                nd2 = nd2;
                                displayId = displayId;
                                windows2 = windows2;
                                display = display;
                                d2 = d2;
                                n = n2;
                            } catch (IOException e) {
                                e = e;
                                Log.e(LOGTAG, "failed to dump window to file", e);
                                long endTime = SystemClock.uptimeMillis();
                                Log.w(LOGTAG, "Fetch time: " + (endTime - startTime) + "ms");
                            }
                        }
                        d = d2;
                        nd = nd2;
                        serializer.endTag("", "display");
                    }
                }
                d2 = d + 1;
                sparseArray = allWindows;
                nd2 = nd;
                startTime2 = startTime;
            }
            startTime = startTime2;
            serializer.endTag("", "displays");
            serializer.endDocument();
            writer.write(stringWriter.toString());
            writer.close();
        } catch (IOException e2) {
            e = e2;
            startTime = startTime2;
        }
        long endTime2 = SystemClock.uptimeMillis();
        Log.w(LOGTAG, "Fetch time: " + (endTime2 - startTime) + "ms");
    }

    private static void dumpWindowRec(AccessibilityWindowInfo winfo, XmlSerializer serializer, int index, int width, int height, int rotation) throws IOException {
        serializer.startTag("", "window");
        serializer.attribute("", "index", Integer.toString(index));
        CharSequence title = winfo.getTitle();
        serializer.attribute("", "title", title != null ? title.toString() : "");
        Rect tmpBounds = new Rect();
        winfo.getBoundsInScreen(tmpBounds);
        serializer.attribute("", "bounds", tmpBounds.toShortString());
        serializer.attribute("", "active", Boolean.toString(winfo.isActive()));
        serializer.attribute("", "focused", Boolean.toString(winfo.isFocused()));
        serializer.attribute("", "accessibility-focused", Boolean.toString(winfo.isAccessibilityFocused()));
        serializer.attribute("", "id", Integer.toString(winfo.getId()));
        serializer.attribute("", "layer", Integer.toString(winfo.getLayer()));
        serializer.attribute("", "type", AccessibilityWindowInfo.typeToString(winfo.getType()));
        int count = winfo.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityWindowInfo child = winfo.getChild(i);
            if (child == null) {
                Log.i(LOGTAG, String.format("Null window child %d/%d, parent: %s", Integer.valueOf(i), Integer.valueOf(count), winfo.getTitle()));
            } else {
                dumpWindowRec(child, serializer, i, width, height, rotation);
                child.recycle();
            }
        }
        AccessibilityNodeInfo root = winfo.getRoot();
        if (root != null) {
            serializer.startTag("", "hierarchy");
            serializer.attribute("", "rotation", Integer.toString(rotation));
            dumpNodeRec(root, serializer, 0, width, height);
            root.recycle();
            serializer.endTag("", "hierarchy");
        }
        serializer.endTag("", "window");
    }

    private static void dumpNodeRec(AccessibilityNodeInfo node, XmlSerializer serializer, int index, int width, int height) throws IOException {
        serializer.startTag("", "node");
        if (!nafExcludedClass(node) && !nafCheck(node)) {
            serializer.attribute("", "NAF", Boolean.toString(true));
        }
        serializer.attribute("", "index", Integer.toString(index));
        serializer.attribute("", "text", safeCharSeqToString(node.getText()));
        serializer.attribute("", "resource-id", safeCharSeqToString(node.getViewIdResourceName()));
        serializer.attribute("", "class", safeCharSeqToString(node.getClassName()));
        serializer.attribute("", "package", safeCharSeqToString(node.getPackageName()));
        serializer.attribute("", "content-desc", safeCharSeqToString(node.getContentDescription()));
        serializer.attribute("", "checkable", Boolean.toString(node.isCheckable()));
        serializer.attribute("", "checked", Boolean.toString(node.isChecked()));
        serializer.attribute("", "clickable", Boolean.toString(node.isClickable()));
        serializer.attribute("", "enabled", Boolean.toString(node.isEnabled()));
        serializer.attribute("", "focusable", Boolean.toString(node.isFocusable()));
        serializer.attribute("", "focused", Boolean.toString(node.isFocused()));
        serializer.attribute("", "scrollable", Boolean.toString(node.isScrollable()));
        serializer.attribute("", "long-clickable", Boolean.toString(node.isLongClickable()));
        serializer.attribute("", "password", Boolean.toString(node.isPassword()));
        serializer.attribute("", "selected", Boolean.toString(node.isSelected()));
        serializer.attribute("", "bounds", AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(node, width, height).toShortString());
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.isVisibleToUser()) {
                    dumpNodeRec(child, serializer, i, width, height);
                    child.recycle();
                } else {
                    Log.i(LOGTAG, String.format("Skipping invisible child: %s", child.toString()));
                }
            } else {
                Log.i(LOGTAG, String.format("Null child %d/%d, parent: %s", Integer.valueOf(i), Integer.valueOf(count), node.toString()));
            }
        }
        serializer.endTag("", "node");
    }

    private static boolean nafExcludedClass(AccessibilityNodeInfo node) {
        String[] strArr;
        String className = safeCharSeqToString(node.getClassName());
        for (String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled() && safeCharSeqToString(node.getContentDescription()).isEmpty() && safeCharSeqToString(node.getText()).isEmpty();
        if (isNaf) {
            return childNafCheck(node);
        }
        return true;
    }

    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty() || !safeCharSeqToString(childNode.getText()).isEmpty() || childNafCheck(childNode)) {
                return true;
            }
        }
        return false;
    }

    private static String safeCharSeqToString(CharSequence cs) {
        if (cs == null) {
            return "";
        }
        return stripInvalidXMLChars(cs);
    }

    private static String stripInvalidXMLChars(CharSequence cs) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < cs.length(); i++) {
            char ch = cs.charAt(i);
            if ((ch >= 1 && ch <= '\b') || ((ch >= 11 && ch <= '\f') || ((ch >= 14 && ch <= 31) || ((ch >= 127 && ch <= 132) || ((ch >= 134 && ch <= 159) || ((ch >= 64976 && ch <= 64991) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || ((ch >= 65534 && ch <= 65535) || (ch >= 65534 && ch <= 65535)))))))))))))))))))))) {
                ret.append(".");
            } else {
                ret.append(ch);
            }
        }
        return ret.toString();
    }
}
