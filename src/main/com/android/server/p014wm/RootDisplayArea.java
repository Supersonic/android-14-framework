package com.android.server.p014wm;

import android.util.Slog;
import com.android.server.p014wm.DisplayArea;
import com.android.server.p014wm.DisplayAreaPolicyBuilder;
import com.android.server.policy.WindowManagerPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* renamed from: com.android.server.wm.RootDisplayArea */
/* loaded from: classes2.dex */
public class RootDisplayArea extends DisplayArea.Dimmable {
    private DisplayArea.Tokens[] mAreaForLayer;
    Map<DisplayAreaPolicyBuilder.Feature, List<DisplayArea<WindowContainer>>> mFeatureToDisplayAreas;
    List<DisplayAreaPolicyBuilder.Feature> mFeatures;
    private boolean mHasBuiltHierarchy;

    @Override // com.android.server.p014wm.WindowContainer
    public RootDisplayArea asRootDisplayArea() {
        return this;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public RootDisplayArea getRootDisplayArea() {
        return this;
    }

    public boolean isOrientationDifferentFromDisplay() {
        return false;
    }

    public RootDisplayArea(WindowManagerService windowManagerService, String str, int i) {
        super(windowManagerService, DisplayArea.Type.ANY, str, i);
    }

    public boolean placeImeContainer(DisplayArea.Tokens tokens) {
        RootDisplayArea rootDisplayArea = tokens.getRootDisplayArea();
        List<DisplayAreaPolicyBuilder.Feature> list = this.mFeatures;
        for (int i = 0; i < list.size(); i++) {
            DisplayAreaPolicyBuilder.Feature feature = list.get(i);
            if (feature.getId() == 7) {
                List<DisplayArea<WindowContainer>> list2 = this.mFeatureToDisplayAreas.get(feature);
                if (list2.size() != 1) {
                    throw new IllegalStateException("There must be exactly one DisplayArea for the FEATURE_IME_PLACEHOLDER");
                }
                rootDisplayArea.updateImeContainerForLayers(null);
                tokens.reparent(list2.get(0), Integer.MAX_VALUE);
                updateImeContainerForLayers(tokens);
                return true;
            }
        }
        if (!isDescendantOf(rootDisplayArea)) {
            Slog.w(StartingSurfaceController.TAG, "The IME target is not in the same root as the IME container, but there is no DisplayArea of FEATURE_IME_PLACEHOLDER in the target RootDisplayArea");
        }
        return false;
    }

    public DisplayArea.Tokens findAreaForTokenInLayer(WindowToken windowToken) {
        return findAreaForWindowTypeInLayer(windowToken.windowType, windowToken.mOwnerCanManageAppTokens, windowToken.mRoundedCornerOverlay);
    }

    public DisplayArea.Tokens findAreaForWindowTypeInLayer(int i, boolean z, boolean z2) {
        int windowLayerFromTypeLw = this.mWmService.mPolicy.getWindowLayerFromTypeLw(i, z, z2);
        if (windowLayerFromTypeLw == 2) {
            throw new IllegalArgumentException("There shouldn't be WindowToken on APPLICATION_LAYER");
        }
        return this.mAreaForLayer[windowLayerFromTypeLw];
    }

    public void onHierarchyBuilt(ArrayList<DisplayAreaPolicyBuilder.Feature> arrayList, DisplayArea.Tokens[] tokensArr, Map<DisplayAreaPolicyBuilder.Feature, List<DisplayArea<WindowContainer>>> map) {
        if (this.mHasBuiltHierarchy) {
            throw new IllegalStateException("Root should only build the hierarchy once");
        }
        this.mHasBuiltHierarchy = true;
        this.mFeatures = Collections.unmodifiableList(arrayList);
        this.mAreaForLayer = tokensArr;
        this.mFeatureToDisplayAreas = map;
    }

    private void updateImeContainerForLayers(DisplayArea.Tokens tokens) {
        WindowManagerPolicy windowManagerPolicy = this.mWmService.mPolicy;
        this.mAreaForLayer[windowManagerPolicy.getWindowLayerFromTypeLw(2011)] = tokens;
        this.mAreaForLayer[windowManagerPolicy.getWindowLayerFromTypeLw(2012)] = tokens;
    }
}
