package com.android.server.autofill;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.service.autofill.Dataset;
import android.text.TextUtils;
import android.util.Slog;
import android.view.autofill.AutofillId;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.autofill.PresentationStatsEventLogger;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class PresentationStatsEventLogger {
    public Optional<PresentationStatsEventInternal> mEventInternal = Optional.empty();
    public final int mSessionId;

    public static int getDisplayPresentationType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    return 0;
                }
            }
        }
        return i2;
    }

    public static int getNoPresentationEventReason(int i) {
        if (i != 1) {
            if (i != 2) {
                return i != 4 ? 0 : 3;
            }
            return 6;
        }
        return 4;
    }

    public PresentationStatsEventLogger(int i) {
        this.mSessionId = i;
    }

    public static PresentationStatsEventLogger forSessionId(int i) {
        return new PresentationStatsEventLogger(i);
    }

    public void startNewEvent() {
        if (this.mEventInternal.isPresent()) {
            Slog.e("PresentationStatsEventLogger", "Failed to start new event because already have active event.");
        } else {
            this.mEventInternal = Optional.of(new PresentationStatsEventInternal());
        }
    }

    public void maybeSetRequestId(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda11
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mRequestId = i;
            }
        });
    }

    public void maybeSetNoPresentationEventReason(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PresentationStatsEventLogger.lambda$maybeSetNoPresentationEventReason$1(i, (PresentationStatsEventLogger.PresentationStatsEventInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$maybeSetNoPresentationEventReason$1(int i, PresentationStatsEventInternal presentationStatsEventInternal) {
        if (presentationStatsEventInternal.mCountShown == 0) {
            presentationStatsEventInternal.mNoPresentationReason = i;
        }
    }

    public void maybeSetNoPresentationEventReasonIfNoReasonExists(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PresentationStatsEventLogger.lambda$maybeSetNoPresentationEventReasonIfNoReasonExists$2(i, (PresentationStatsEventLogger.PresentationStatsEventInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$maybeSetNoPresentationEventReasonIfNoReasonExists$2(int i, PresentationStatsEventInternal presentationStatsEventInternal) {
        if (presentationStatsEventInternal.mCountShown == 0 && presentationStatsEventInternal.mNoPresentationReason == 0) {
            presentationStatsEventInternal.mNoPresentationReason = i;
        }
    }

    public void maybeSetAvailableCount(final List<Dataset> list, final AutofillId autofillId) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PresentationStatsEventLogger.lambda$maybeSetAvailableCount$3(list, autofillId, (PresentationStatsEventLogger.PresentationStatsEventInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$maybeSetAvailableCount$3(List list, AutofillId autofillId, PresentationStatsEventInternal presentationStatsEventInternal) {
        int datasetCountForAutofillId = getDatasetCountForAutofillId(list, autofillId);
        presentationStatsEventInternal.mAvailableCount = datasetCountForAutofillId;
        presentationStatsEventInternal.mIsDatasetAvailable = datasetCountForAutofillId > 0;
    }

    public void maybeSetCountShown(final List<Dataset> list, final AutofillId autofillId) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PresentationStatsEventLogger.lambda$maybeSetCountShown$4(list, autofillId, (PresentationStatsEventLogger.PresentationStatsEventInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$maybeSetCountShown$4(List list, AutofillId autofillId, PresentationStatsEventInternal presentationStatsEventInternal) {
        int datasetCountForAutofillId = getDatasetCountForAutofillId(list, autofillId);
        presentationStatsEventInternal.mCountShown = datasetCountForAutofillId;
        if (datasetCountForAutofillId > 0) {
            presentationStatsEventInternal.mNoPresentationReason = 1;
        }
    }

    public static int getDatasetCountForAutofillId(List<Dataset> list, AutofillId autofillId) {
        if (list != null) {
            int i = 0;
            for (int i2 = 0; i2 < list.size(); i2++) {
                Dataset dataset = list.get(i2);
                if (dataset != null && dataset.getFieldIds() != null && dataset.getFieldIds().contains(autofillId)) {
                    i++;
                }
            }
            return i;
        }
        return 0;
    }

    public void maybeSetDisplayPresentationType(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PresentationStatsEventLogger.lambda$maybeSetDisplayPresentationType$8(i, (PresentationStatsEventLogger.PresentationStatsEventInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$maybeSetDisplayPresentationType$8(int i, PresentationStatsEventInternal presentationStatsEventInternal) {
        presentationStatsEventInternal.mDisplayPresentationType = getDisplayPresentationType(i);
    }

    public void maybeSetFillRequestSentTimestampMs(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mFillRequestSentTimestampMs = i;
            }
        });
    }

    public void maybeSetFillResponseReceivedTimestampMs(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mFillResponseReceivedTimestampMs = i;
            }
        });
    }

    public void maybeSetSuggestionSentTimestampMs(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mSuggestionSentTimestampMs = i;
            }
        });
    }

    public void maybeSetSuggestionPresentedTimestampMs(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mSuggestionPresentedTimestampMs = i;
            }
        });
    }

    public void maybeSetInlinePresentationAndSuggestionHostUid(final Context context, final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PresentationStatsEventLogger.lambda$maybeSetInlinePresentationAndSuggestionHostUid$13(context, i, (PresentationStatsEventLogger.PresentationStatsEventInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$maybeSetInlinePresentationAndSuggestionHostUid$13(Context context, int i, PresentationStatsEventInternal presentationStatsEventInternal) {
        presentationStatsEventInternal.mDisplayPresentationType = 2;
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "default_input_method", i);
        if (TextUtils.isEmpty(stringForUser)) {
            Slog.w("PresentationStatsEventLogger", "No default IME found");
            return;
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(stringForUser);
        if (unflattenFromString == null) {
            Slog.w("PresentationStatsEventLogger", "No default IME found");
            return;
        }
        String packageName = unflattenFromString.getPackageName();
        try {
            presentationStatsEventInternal.mInlineSuggestionHostUid = context.getPackageManager().getApplicationInfoAsUser(packageName, PackageManager.ApplicationInfoFlags.of(0L), i).uid;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w("PresentationStatsEventLogger", "Couldn't find packageName: " + packageName);
        }
    }

    public void maybeSetAutofillServiceUid(final int i) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mAutofillServiceUid = i;
            }
        });
    }

    public void maybeSetIsNewRequest(final boolean z) {
        this.mEventInternal.ifPresent(new Consumer() { // from class: com.android.server.autofill.PresentationStatsEventLogger$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((PresentationStatsEventLogger.PresentationStatsEventInternal) obj).mIsRequestTriggered = z;
            }
        });
    }

    public void logAndEndEvent() {
        if (!this.mEventInternal.isPresent()) {
            Slog.w("PresentationStatsEventLogger", "Shouldn't be logging AutofillPresentationEventReported again for same event");
            return;
        }
        PresentationStatsEventInternal presentationStatsEventInternal = this.mEventInternal.get();
        if (Helper.sVerbose) {
            Slog.v("PresentationStatsEventLogger", "Log AutofillPresentationEventReported: requestId=" + presentationStatsEventInternal.mRequestId + " sessionId=" + this.mSessionId + " mNoPresentationEventReason=" + presentationStatsEventInternal.mNoPresentationReason + " mAvailableCount=" + presentationStatsEventInternal.mAvailableCount + " mCountShown=" + presentationStatsEventInternal.mCountShown + " mCountFilteredUserTyping=" + presentationStatsEventInternal.mCountFilteredUserTyping + " mCountNotShownImePresentationNotDrawn=" + presentationStatsEventInternal.mCountNotShownImePresentationNotDrawn + " mCountNotShownImeUserNotSeen=" + presentationStatsEventInternal.mCountNotShownImeUserNotSeen + " mDisplayPresentationType=" + presentationStatsEventInternal.mDisplayPresentationType + " mAutofillServiceUid=" + presentationStatsEventInternal.mAutofillServiceUid + " mInlineSuggestionHostUid=" + presentationStatsEventInternal.mInlineSuggestionHostUid + " mIsRequestTriggered=" + presentationStatsEventInternal.mIsRequestTriggered + " mFillRequestSentTimestampMs=" + presentationStatsEventInternal.mFillRequestSentTimestampMs + " mFillResponseReceivedTimestampMs=" + presentationStatsEventInternal.mFillResponseReceivedTimestampMs + " mSuggestionSentTimestampMs=" + presentationStatsEventInternal.mSuggestionSentTimestampMs + " mSuggestionPresentedTimestampMs=" + presentationStatsEventInternal.mSuggestionPresentedTimestampMs);
        }
        if (!presentationStatsEventInternal.mIsDatasetAvailable) {
            this.mEventInternal = Optional.empty();
            return;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.AUTOFILL_PRESENTATION_EVENT_REPORTED, presentationStatsEventInternal.mRequestId, this.mSessionId, presentationStatsEventInternal.mNoPresentationReason, presentationStatsEventInternal.mAvailableCount, presentationStatsEventInternal.mCountShown, presentationStatsEventInternal.mCountFilteredUserTyping, presentationStatsEventInternal.mCountNotShownImePresentationNotDrawn, presentationStatsEventInternal.mCountNotShownImeUserNotSeen, presentationStatsEventInternal.mDisplayPresentationType, presentationStatsEventInternal.mAutofillServiceUid, presentationStatsEventInternal.mInlineSuggestionHostUid, presentationStatsEventInternal.mIsRequestTriggered, presentationStatsEventInternal.mFillRequestSentTimestampMs, presentationStatsEventInternal.mFillResponseReceivedTimestampMs, presentationStatsEventInternal.mSuggestionSentTimestampMs, presentationStatsEventInternal.mSuggestionPresentedTimestampMs, 0, false, false, false);
        this.mEventInternal = Optional.empty();
    }

    /* loaded from: classes.dex */
    public final class PresentationStatsEventInternal {
        public int mAvailableCount;
        public int mCountFilteredUserTyping;
        public int mCountNotShownImePresentationNotDrawn;
        public int mCountNotShownImeUserNotSeen;
        public int mCountShown;
        public int mFillRequestSentTimestampMs;
        public int mFillResponseReceivedTimestampMs;
        public boolean mIsDatasetAvailable;
        public boolean mIsRequestTriggered;
        public int mRequestId;
        public int mSuggestionPresentedTimestampMs;
        public int mSuggestionSentTimestampMs;
        public int mNoPresentationReason = 0;
        public int mDisplayPresentationType = 0;
        public int mAutofillServiceUid = -1;
        public int mInlineSuggestionHostUid = -1;

        public PresentationStatsEventInternal() {
        }
    }
}
