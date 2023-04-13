package com.android.uiautomator.core;
@Deprecated
/* loaded from: classes.dex */
public class UiCollection extends UiObject {
    public UiCollection(UiSelector selector) {
        super(selector);
    }

    public UiObject getChildByDescription(UiSelector childPattern, String text) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text);
        if (text != null) {
            int count = getChildCount(childPattern);
            for (int x = 0; x < count; x++) {
                UiObject row = getChildByInstance(childPattern, x);
                String nodeDesc = row.getContentDescription();
                if (nodeDesc != null && nodeDesc.contains(text)) {
                    return row;
                }
                UiObject item = row.getChild(new UiSelector().descriptionContains(text));
                if (item.exists()) {
                    return row;
                }
            }
        }
        throw new UiObjectNotFoundException("for description= \"" + text + "\"");
    }

    public UiObject getChildByInstance(UiSelector childPattern, int instance) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, Integer.valueOf(instance));
        UiSelector patternSelector = UiSelector.patternBuilder(getSelector(), UiSelector.patternBuilder(childPattern).instance(instance));
        return new UiObject(patternSelector);
    }

    public UiObject getChildByText(UiSelector childPattern, String text) throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text);
        if (text != null) {
            int count = getChildCount(childPattern);
            for (int x = 0; x < count; x++) {
                UiObject row = getChildByInstance(childPattern, x);
                String nodeText = row.getText();
                if (text.equals(nodeText)) {
                    return row;
                }
                UiObject item = row.getChild(new UiSelector().text(text));
                if (item.exists()) {
                    return row;
                }
            }
        }
        throw new UiObjectNotFoundException("for text= \"" + text + "\"");
    }

    public int getChildCount(UiSelector childPattern) {
        Tracer.trace(childPattern);
        UiSelector patternSelector = UiSelector.patternBuilder(getSelector(), UiSelector.patternBuilder(childPattern));
        return getQueryController().getPatternCount(patternSelector);
    }
}
