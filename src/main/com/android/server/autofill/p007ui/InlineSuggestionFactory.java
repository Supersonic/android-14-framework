package com.android.server.autofill.p007ui;

import android.content.IntentSender;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.service.autofill.InlinePresentation;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.inputmethod.InlineSuggestion;
import android.view.inputmethod.InlineSuggestionInfo;
import android.view.inputmethod.InlineSuggestionsRequest;
import android.widget.inline.InlinePresentationSpec;
import com.android.internal.view.inline.IInlineContentProvider;
import com.android.server.autofill.Helper;
import com.android.server.autofill.p007ui.InlineFillUi;
import com.android.server.location.gnss.hal.GnssNative;
import java.util.List;
/* renamed from: com.android.server.autofill.ui.InlineSuggestionFactory */
/* loaded from: classes.dex */
public final class InlineSuggestionFactory {
    public static /* synthetic */ void lambda$createInlineSuggestionTooltip$2() {
    }

    public static InlineSuggestion createInlineAuthentication(InlineFillUi.InlineFillUiInfo inlineFillUiInfo, FillResponse fillResponse, final InlineFillUi.InlineSuggestionUiCallback inlineSuggestionUiCallback) {
        InlinePresentation inlinePresentation = fillResponse.getInlinePresentation();
        final int requestId = fillResponse.getRequestId();
        return createInlineSuggestion(inlineFillUiInfo, "android:autofill", "android:autofill:action", new Runnable() { // from class: com.android.server.autofill.ui.InlineSuggestionFactory$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                InlineFillUi.InlineSuggestionUiCallback.this.authenticate(requestId, GnssNative.GNSS_AIDING_TYPE_ALL);
            }
        }, mergedInlinePresentation(inlineFillUiInfo.mInlineRequest, 0, inlinePresentation), createInlineSuggestionTooltip(inlineFillUiInfo.mInlineRequest, inlineFillUiInfo, "android:autofill", fillResponse.getInlineTooltipPresentation()), inlineSuggestionUiCallback);
    }

    public static SparseArray<Pair<Dataset, InlineSuggestion>> createInlineSuggestions(InlineFillUi.InlineFillUiInfo inlineFillUiInfo, String str, List<Dataset> list, final InlineFillUi.InlineSuggestionUiCallback inlineSuggestionUiCallback) {
        InlineSuggestion inlineSuggestion;
        if (Helper.sDebug) {
            Slog.d("InlineSuggestionFactory", "createInlineSuggestions(source=" + str + ") called");
        }
        InlineSuggestionsRequest inlineSuggestionsRequest = inlineFillUiInfo.mInlineRequest;
        SparseArray<Pair<Dataset, InlineSuggestion>> sparseArray = new SparseArray<>(list.size());
        boolean z = false;
        for (final int i = 0; i < list.size(); i++) {
            final Dataset dataset = list.get(i);
            int indexOf = dataset.getFieldIds().indexOf(inlineFillUiInfo.mFocusedId);
            if (indexOf < 0) {
                Slog.w("InlineSuggestionFactory", "AutofillId=" + inlineFillUiInfo.mFocusedId + " not found in dataset");
            } else {
                InlinePresentation fieldInlinePresentation = dataset.getFieldInlinePresentation(indexOf);
                if (fieldInlinePresentation == null) {
                    Slog.w("InlineSuggestionFactory", "InlinePresentation not found in dataset");
                } else {
                    String str2 = dataset.getAuthentication() == null ? "android:autofill:suggestion" : "android:autofill:action";
                    if (z) {
                        inlineSuggestion = null;
                    } else {
                        inlineSuggestion = createInlineSuggestionTooltip(inlineSuggestionsRequest, inlineFillUiInfo, str, dataset.getFieldInlineTooltipPresentation(indexOf));
                        if (inlineSuggestion != null) {
                            z = true;
                        }
                    }
                    sparseArray.append(i, Pair.create(dataset, createInlineSuggestion(inlineFillUiInfo, str, str2, new Runnable() { // from class: com.android.server.autofill.ui.InlineSuggestionFactory$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            InlineFillUi.InlineSuggestionUiCallback.this.autofill(dataset, i);
                        }
                    }, mergedInlinePresentation(inlineSuggestionsRequest, i, fieldInlinePresentation), inlineSuggestion, inlineSuggestionUiCallback)));
                    z = z;
                }
            }
        }
        return sparseArray;
    }

    public static InlineSuggestion createInlineSuggestion(InlineFillUi.InlineFillUiInfo inlineFillUiInfo, String str, String str2, Runnable runnable, InlinePresentation inlinePresentation, InlineSuggestion inlineSuggestion, InlineFillUi.InlineSuggestionUiCallback inlineSuggestionUiCallback) {
        return new InlineSuggestion(new InlineSuggestionInfo(inlinePresentation.getInlinePresentationSpec(), str, inlinePresentation.getAutofillHints(), str2, inlinePresentation.isPinned(), inlineSuggestion), createInlineContentProvider(inlineFillUiInfo, inlinePresentation, runnable, inlineSuggestionUiCallback));
    }

    public static InlinePresentation mergedInlinePresentation(InlineSuggestionsRequest inlineSuggestionsRequest, int i, InlinePresentation inlinePresentation) {
        List<InlinePresentationSpec> inlinePresentationSpecs = inlineSuggestionsRequest.getInlinePresentationSpecs();
        if (inlinePresentationSpecs.isEmpty()) {
            return inlinePresentation;
        }
        return new InlinePresentation(inlinePresentation.getSlice(), new InlinePresentationSpec.Builder(inlinePresentation.getInlinePresentationSpec().getMinSize(), inlinePresentation.getInlinePresentationSpec().getMaxSize()).setStyle(inlinePresentationSpecs.get(Math.min(inlinePresentationSpecs.size() - 1, i)).getStyle()).build(), inlinePresentation.isPinned());
    }

    public static InlineSuggestion createInlineSuggestionTooltip(InlineSuggestionsRequest inlineSuggestionsRequest, InlineFillUi.InlineFillUiInfo inlineFillUiInfo, String str, InlinePresentation inlinePresentation) {
        InlinePresentationSpec build;
        if (inlinePresentation == null) {
            return null;
        }
        InlinePresentationSpec inlineTooltipPresentationSpec = inlineSuggestionsRequest.getInlineTooltipPresentationSpec();
        if (inlineTooltipPresentationSpec == null) {
            build = inlinePresentation.getInlinePresentationSpec();
        } else {
            build = new InlinePresentationSpec.Builder(inlinePresentation.getInlinePresentationSpec().getMinSize(), inlinePresentation.getInlinePresentationSpec().getMaxSize()).setStyle(inlineTooltipPresentationSpec.getStyle()).build();
        }
        InlinePresentationSpec inlinePresentationSpec = build;
        return new InlineSuggestion(new InlineSuggestionInfo(inlinePresentationSpec, str, null, "android:autofill:suggestion", false, null), createInlineContentProvider(inlineFillUiInfo, new InlinePresentation(inlinePresentation.getSlice(), inlinePresentationSpec, false), new Runnable() { // from class: com.android.server.autofill.ui.InlineSuggestionFactory$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                InlineSuggestionFactory.lambda$createInlineSuggestionTooltip$2();
            }
        }, new InlineFillUi.InlineSuggestionUiCallback() { // from class: com.android.server.autofill.ui.InlineSuggestionFactory.1
            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void authenticate(int i, int i2) {
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void autofill(Dataset dataset, int i) {
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void onInflate() {
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void startIntentSender(IntentSender intentSender) {
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void onError() {
                Slog.w("InlineSuggestionFactory", "An error happened on the tooltip");
            }
        }));
    }

    public static IInlineContentProvider createInlineContentProvider(InlineFillUi.InlineFillUiInfo inlineFillUiInfo, InlinePresentation inlinePresentation, Runnable runnable, InlineFillUi.InlineSuggestionUiCallback inlineSuggestionUiCallback) {
        return new InlineContentProviderImpl(new RemoteInlineSuggestionViewConnector(inlineFillUiInfo, inlinePresentation, runnable, inlineSuggestionUiCallback), null);
    }
}
