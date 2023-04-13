package com.android.server.autofill.p007ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.service.autofill.BatchUpdates;
import android.service.autofill.CustomDescription;
import android.service.autofill.InternalOnClickAction;
import android.service.autofill.InternalTransformation;
import android.service.autofill.InternalValidator;
import android.service.autofill.SaveInfo;
import android.service.autofill.ValueFinder;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ArrayUtils;
import com.android.server.UiThread;
import com.android.server.autofill.Helper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
/* renamed from: com.android.server.autofill.ui.SaveUi */
/* loaded from: classes.dex */
public final class SaveUi {
    public final boolean mCompatMode;
    public final ComponentName mComponentName;
    public boolean mDestroyed;
    public final Dialog mDialog;
    public final OneActionThenDestroyListener mListener;
    public final OverlayControl mOverlayControl;
    public final PendingUi mPendingUi;
    public final String mServicePackageName;
    public final CharSequence mSubTitle;
    public final int mThemeId;
    public final CharSequence mTitle;
    public final int mType;
    public final Handler mHandler = UiThread.getHandler();
    public final MetricsLogger mMetricsLogger = new MetricsLogger();

    /* renamed from: com.android.server.autofill.ui.SaveUi$OnSaveListener */
    /* loaded from: classes.dex */
    public interface OnSaveListener {
        void onCancel(IntentSender intentSender);

        void onDestroy();

        void onSave();

        void startIntentSender(IntentSender intentSender, Intent intent);
    }

    /* renamed from: com.android.server.autofill.ui.SaveUi$OneActionThenDestroyListener */
    /* loaded from: classes.dex */
    public class OneActionThenDestroyListener implements OnSaveListener {
        public boolean mDone;
        public final OnSaveListener mRealListener;

        public OneActionThenDestroyListener(OnSaveListener onSaveListener) {
            this.mRealListener = onSaveListener;
        }

        @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
        public void onSave() {
            if (Helper.sDebug) {
                Slog.d("SaveUi", "OneTimeListener.onSave(): " + this.mDone);
            }
            if (this.mDone) {
                return;
            }
            this.mRealListener.onSave();
        }

        @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
        public void onCancel(IntentSender intentSender) {
            if (Helper.sDebug) {
                Slog.d("SaveUi", "OneTimeListener.onCancel(): " + this.mDone);
            }
            if (this.mDone) {
                return;
            }
            this.mRealListener.onCancel(intentSender);
        }

        @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
        public void onDestroy() {
            if (Helper.sDebug) {
                Slog.d("SaveUi", "OneTimeListener.onDestroy(): " + this.mDone);
            }
            if (this.mDone) {
                return;
            }
            this.mDone = true;
            this.mRealListener.onDestroy();
        }

        @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
        public void startIntentSender(IntentSender intentSender, Intent intent) {
            if (Helper.sDebug) {
                Slog.d("SaveUi", "OneTimeListener.startIntentSender(): " + this.mDone);
            }
            if (this.mDone) {
                return;
            }
            this.mRealListener.startIntentSender(intentSender, intent);
        }
    }

    public SaveUi(Context context, PendingUi pendingUi, CharSequence charSequence, Drawable drawable, String str, ComponentName componentName, final SaveInfo saveInfo, ValueFinder valueFinder, OverlayControl overlayControl, OnSaveListener onSaveListener, boolean z, boolean z2, boolean z3, boolean z4) {
        if (Helper.sVerbose) {
            Slog.v("SaveUi", "nightMode: " + z);
        }
        int i = z ? 16974809 : 16974820;
        this.mThemeId = i;
        this.mPendingUi = pendingUi;
        this.mListener = new OneActionThenDestroyListener(onSaveListener);
        this.mOverlayControl = overlayControl;
        this.mServicePackageName = str;
        this.mComponentName = componentName;
        this.mCompatMode = z3;
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, i) { // from class: com.android.server.autofill.ui.SaveUi.1
            @Override // android.content.ContextWrapper, android.content.Context
            public void startActivity(Intent intent) {
                if (resolveActivity(intent) == null) {
                    if (Helper.sDebug) {
                        Slog.d("SaveUi", "Can not startActivity for save UI with intent=" + intent);
                        return;
                    }
                    return;
                }
                intent.putExtra("android.view.autofill.extra.RESTORE_CROSS_ACTIVITY", true);
                PendingIntent activityAsUser = PendingIntent.getActivityAsUser(this, 0, intent, 50331648, null, UserHandle.CURRENT);
                if (Helper.sDebug) {
                    Slog.d("SaveUi", "startActivity add save UI restored with intent=" + intent);
                }
                SaveUi.this.startIntentSenderWithRestore(activityAsUser, intent);
            }

            public final ComponentName resolveActivity(Intent intent) {
                PackageManager packageManager = getPackageManager();
                ComponentName resolveActivity = intent.resolveActivity(packageManager);
                if (resolveActivity != null) {
                    return resolveActivity;
                }
                intent.addFlags(IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
                ActivityInfo resolveActivityInfo = intent.resolveActivityInfo(packageManager, 8388608);
                if (resolveActivityInfo != null) {
                    return new ComponentName(resolveActivityInfo.applicationInfo.packageName, resolveActivityInfo.name);
                }
                return null;
            }
        };
        View inflate = LayoutInflater.from(contextThemeWrapper).inflate(17367110, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(16908813);
        ArraySet arraySet = new ArraySet(3);
        int type = saveInfo.getType();
        this.mType = type;
        if ((type & 1) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039726));
        }
        if ((type & 2) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039721));
        }
        if (Integer.bitCount(type & 100) > 1 || (type & 128) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039725));
        } else if ((type & 64) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039727));
        } else if ((type & 4) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039722));
        } else if ((type & 32) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039723));
        }
        if ((type & 8) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039728));
        }
        if ((type & 16) != 0) {
            arraySet.add(contextThemeWrapper.getString(17039724));
        }
        int size = arraySet.size();
        if (size == 1) {
            this.mTitle = Html.fromHtml(contextThemeWrapper.getString(z2 ? 17039733 : 17039720, arraySet.valueAt(0), charSequence), 0);
        } else if (size == 2) {
            this.mTitle = Html.fromHtml(contextThemeWrapper.getString(z2 ? 17039731 : 17039718, arraySet.valueAt(0), arraySet.valueAt(1), charSequence), 0);
        } else if (size == 3) {
            this.mTitle = Html.fromHtml(contextThemeWrapper.getString(z2 ? 17039732 : 17039719, arraySet.valueAt(0), arraySet.valueAt(1), arraySet.valueAt(2), charSequence), 0);
        } else {
            this.mTitle = Html.fromHtml(contextThemeWrapper.getString(z2 ? 17039730 : 17039717, charSequence), 0);
        }
        textView.setText(this.mTitle);
        if (z4) {
            setServiceIcon(contextThemeWrapper, inflate, drawable);
        }
        if (applyCustomDescription(contextThemeWrapper, inflate, valueFinder, saveInfo)) {
            this.mSubTitle = null;
            if (Helper.sDebug) {
                Slog.d("SaveUi", "on constructor: applied custom description");
            }
        } else {
            CharSequence description = saveInfo.getDescription();
            this.mSubTitle = description;
            if (description != null) {
                writeLog(1131);
                ViewGroup viewGroup = (ViewGroup) inflate.findViewById(16908810);
                TextView textView2 = new TextView(contextThemeWrapper);
                textView2.setText(description);
                applyMovementMethodIfNeed(textView2);
                viewGroup.addView(textView2, new ViewGroup.LayoutParams(-1, -2));
                viewGroup.setVisibility(0);
                viewGroup.setScrollBarDefaultDelayBeforeFade(500);
            }
            if (Helper.sDebug) {
                Slog.d("SaveUi", "on constructor: title=" + ((Object) this.mTitle) + ", subTitle=" + ((Object) description));
            }
        }
        TextView textView3 = (TextView) inflate.findViewById(16908812);
        int negativeActionStyle = saveInfo.getNegativeActionStyle();
        if (negativeActionStyle == 1) {
            textView3.setText(17039716);
        } else if (negativeActionStyle == 2) {
            textView3.setText(17039714);
        } else {
            textView3.setText(17039715);
        }
        textView3.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.SaveUi$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SaveUi.this.lambda$new$0(saveInfo, view);
            }
        });
        TextView textView4 = (TextView) inflate.findViewById(16908814);
        if (saveInfo.getPositiveActionStyle() == 1) {
            textView4.setText(17039708);
        } else if (z2) {
            textView4.setText(17039734);
        }
        textView4.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.SaveUi$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SaveUi.this.lambda$new$1(view);
            }
        });
        Dialog dialog = new Dialog(contextThemeWrapper, i);
        this.mDialog = dialog;
        dialog.setContentView(inflate);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.server.autofill.ui.SaveUi$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                SaveUi.this.lambda$new$2(dialogInterface);
            }
        });
        Window window = dialog.getWindow();
        window.setType(2038);
        window.addFlags(131074);
        window.setDimAmount(0.6f);
        window.addPrivateFlags(16);
        window.setSoftInputMode(32);
        window.setGravity(81);
        window.setCloseOnTouchOutside(true);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.accessibilityTitle = contextThemeWrapper.getString(17039713);
        attributes.windowAnimations = 16974613;
        show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(SaveInfo saveInfo, View view) {
        this.mListener.onCancel(saveInfo.getNegativeActionListener());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        this.mListener.onSave();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(DialogInterface dialogInterface) {
        this.mListener.onCancel(null);
    }

    public final boolean applyCustomDescription(Context context, View view, ValueFinder valueFinder, SaveInfo saveInfo) {
        CustomDescription customDescription = saveInfo.getCustomDescription();
        if (customDescription == null) {
            return false;
        }
        writeLog(1129);
        RemoteViews presentation = customDescription.getPresentation();
        if (presentation == null) {
            Slog.w("SaveUi", "No remote view on custom description");
            return false;
        }
        ArrayList transformations = customDescription.getTransformations();
        if (Helper.sVerbose) {
            Slog.v("SaveUi", "applyCustomDescription(): transformations = " + transformations);
        }
        if (transformations != null && !InternalTransformation.batchApply(valueFinder, presentation, transformations)) {
            Slog.w("SaveUi", "could not apply main transformations on custom description");
            return false;
        }
        try {
            View applyWithTheme = presentation.applyWithTheme(context, null, new RemoteViews.InteractionHandler() { // from class: com.android.server.autofill.ui.SaveUi$$ExternalSyntheticLambda3
                public final boolean onInteraction(View view2, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                    boolean lambda$applyCustomDescription$3;
                    lambda$applyCustomDescription$3 = SaveUi.this.lambda$applyCustomDescription$3(view2, pendingIntent, remoteResponse);
                    return lambda$applyCustomDescription$3;
                }
            }, this.mThemeId);
            ArrayList updates = customDescription.getUpdates();
            if (Helper.sVerbose) {
                Slog.v("SaveUi", "applyCustomDescription(): view = " + applyWithTheme + " updates=" + updates);
            }
            if (updates != null) {
                int size = updates.size();
                if (Helper.sDebug) {
                    Slog.d("SaveUi", "custom description has " + size + " batch updates");
                }
                for (int i = 0; i < size; i++) {
                    Pair pair = (Pair) updates.get(i);
                    InternalValidator internalValidator = (InternalValidator) pair.first;
                    if (internalValidator != null && internalValidator.isValid(valueFinder)) {
                        BatchUpdates batchUpdates = (BatchUpdates) pair.second;
                        RemoteViews updates2 = batchUpdates.getUpdates();
                        if (updates2 != null) {
                            if (Helper.sDebug) {
                                Slog.d("SaveUi", "Applying template updates for batch update #" + i);
                            }
                            updates2.reapply(context, applyWithTheme);
                        }
                        ArrayList transformations2 = batchUpdates.getTransformations();
                        if (transformations2 == null) {
                            continue;
                        } else {
                            if (Helper.sDebug) {
                                Slog.d("SaveUi", "Applying child transformation for batch update #" + i + ": " + transformations2);
                            }
                            if (!InternalTransformation.batchApply(valueFinder, presentation, transformations2)) {
                                Slog.w("SaveUi", "Could not apply child transformation for batch update #" + i + ": " + transformations2);
                                return false;
                            }
                            presentation.reapply(context, applyWithTheme);
                        }
                    }
                    if (Helper.sDebug) {
                        Slog.d("SaveUi", "Skipping batch update #" + i);
                    }
                }
            }
            SparseArray actions = customDescription.getActions();
            if (actions != null) {
                int size2 = actions.size();
                if (Helper.sDebug) {
                    Slog.d("SaveUi", "custom description has " + size2 + " actions");
                }
                if (applyWithTheme instanceof ViewGroup) {
                    final ViewGroup viewGroup = (ViewGroup) applyWithTheme;
                    for (int i2 = 0; i2 < size2; i2++) {
                        int keyAt = actions.keyAt(i2);
                        final InternalOnClickAction internalOnClickAction = (InternalOnClickAction) actions.valueAt(i2);
                        View findViewById = viewGroup.findViewById(keyAt);
                        if (findViewById == null) {
                            Slog.w("SaveUi", "Ignoring action " + internalOnClickAction + " for view " + keyAt + " because it's not on " + viewGroup);
                        } else {
                            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.SaveUi$$ExternalSyntheticLambda4
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view2) {
                                    SaveUi.lambda$applyCustomDescription$4(internalOnClickAction, viewGroup, view2);
                                }
                            });
                        }
                    }
                } else {
                    Slog.w("SaveUi", "cannot apply actions because custom description root is not a ViewGroup: " + applyWithTheme);
                }
            }
            applyTextViewStyle(applyWithTheme);
            ViewGroup viewGroup2 = (ViewGroup) view.findViewById(16908810);
            viewGroup2.addView(applyWithTheme);
            viewGroup2.setVisibility(0);
            viewGroup2.setScrollBarDefaultDelayBeforeFade(500);
            return true;
        } catch (Exception e) {
            Slog.e("SaveUi", "Error applying custom description. ", e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$applyCustomDescription$3(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        Intent intent = (Intent) remoteResponse.getLaunchOptions(view).first;
        if (!isValidLink(pendingIntent, intent)) {
            LogMaker newLogMaker = newLogMaker(1132, this.mType);
            newLogMaker.setType(0);
            this.mMetricsLogger.write(newLogMaker);
            return false;
        }
        startIntentSenderWithRestore(pendingIntent, intent);
        return true;
    }

    public static /* synthetic */ void lambda$applyCustomDescription$4(InternalOnClickAction internalOnClickAction, ViewGroup viewGroup, View view) {
        if (Helper.sVerbose) {
            Slog.v("SaveUi", "Applying " + internalOnClickAction + " after " + view + " was clicked");
        }
        internalOnClickAction.onClick(viewGroup);
    }

    public final void startIntentSenderWithRestore(PendingIntent pendingIntent, Intent intent) {
        if (Helper.sVerbose) {
            Slog.v("SaveUi", "Intercepting custom description intent");
        }
        IBinder token = this.mPendingUi.getToken();
        intent.putExtra("android.view.autofill.extra.RESTORE_SESSION_TOKEN", token);
        this.mListener.startIntentSender(pendingIntent.getIntentSender(), intent);
        this.mPendingUi.setState(2);
        if (Helper.sDebug) {
            Slog.d("SaveUi", "hiding UI until restored with token " + token);
        }
        hide();
        LogMaker newLogMaker = newLogMaker(1132, this.mType);
        newLogMaker.setType(1);
        this.mMetricsLogger.write(newLogMaker);
    }

    public final void applyTextViewStyle(View view) {
        final ArrayList arrayList = new ArrayList();
        view.findViewByPredicate(new Predicate() { // from class: com.android.server.autofill.ui.SaveUi$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$applyTextViewStyle$5;
                lambda$applyTextViewStyle$5 = SaveUi.lambda$applyTextViewStyle$5(arrayList, (View) obj);
                return lambda$applyTextViewStyle$5;
            }
        });
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            applyMovementMethodIfNeed((TextView) arrayList.get(i));
        }
    }

    public static /* synthetic */ boolean lambda$applyTextViewStyle$5(List list, View view) {
        if (view instanceof TextView) {
            list.add((TextView) view);
            return false;
        }
        return false;
    }

    public final void applyMovementMethodIfNeed(TextView textView) {
        CharSequence text = textView.getText();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        if (ArrayUtils.isEmpty((ClickableSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ClickableSpan.class))) {
            return;
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public final void setServiceIcon(Context context, View view, Drawable drawable) {
        ImageView imageView = (ImageView) view.findViewById(16908811);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(17104958);
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        if (minimumWidth <= dimensionPixelSize && minimumHeight <= dimensionPixelSize) {
            if (Helper.sDebug) {
                Slog.d("SaveUi", "Adding service icon (" + minimumWidth + "x" + minimumHeight + ") as it's less than maximum (" + dimensionPixelSize + "x" + dimensionPixelSize + ").");
            }
            imageView.setImageDrawable(drawable);
            return;
        }
        Slog.w("SaveUi", "Not adding service icon of size (" + minimumWidth + "x" + minimumHeight + ") because maximum is (" + dimensionPixelSize + "x" + dimensionPixelSize + ").");
        ((ViewGroup) imageView.getParent()).removeView(imageView);
    }

    public static boolean isValidLink(PendingIntent pendingIntent, Intent intent) {
        if (pendingIntent == null) {
            Slog.w("SaveUi", "isValidLink(): custom description without pending intent");
            return false;
        } else if (!pendingIntent.isActivity()) {
            Slog.w("SaveUi", "isValidLink(): pending intent not for activity");
            return false;
        } else if (intent == null) {
            Slog.w("SaveUi", "isValidLink(): no intent");
            return false;
        } else {
            return true;
        }
    }

    public final LogMaker newLogMaker(int i, int i2) {
        return newLogMaker(i).addTaggedData(1130, Integer.valueOf(i2));
    }

    public final LogMaker newLogMaker(int i) {
        return Helper.newLogMaker(i, this.mComponentName, this.mServicePackageName, this.mPendingUi.sessionId, this.mCompatMode);
    }

    public final void writeLog(int i) {
        this.mMetricsLogger.write(newLogMaker(i, this.mType));
    }

    public void onPendingUi(int i, IBinder iBinder) {
        if (!this.mPendingUi.matches(iBinder)) {
            Slog.w("SaveUi", "restore(" + i + "): got token " + iBinder + " instead of " + this.mPendingUi.getToken());
            return;
        }
        LogMaker newLogMaker = newLogMaker(1134);
        try {
            if (i == 1) {
                newLogMaker.setType(5);
                if (Helper.sDebug) {
                    Slog.d("SaveUi", "Cancelling pending save dialog for " + iBinder);
                }
                hide();
            } else if (i == 2) {
                if (Helper.sDebug) {
                    Slog.d("SaveUi", "Restoring save dialog for " + iBinder);
                }
                newLogMaker.setType(1);
                show();
            } else {
                newLogMaker.setType(11);
                Slog.w("SaveUi", "restore(): invalid operation " + i);
            }
            this.mMetricsLogger.write(newLogMaker);
            this.mPendingUi.setState(4);
        } catch (Throwable th) {
            this.mMetricsLogger.write(newLogMaker);
            throw th;
        }
    }

    public final void show() {
        Slog.i("SaveUi", "Showing save dialog: " + ((Object) this.mTitle));
        this.mDialog.show();
        this.mOverlayControl.hideOverlays();
    }

    public PendingUi hide() {
        if (Helper.sVerbose) {
            Slog.v("SaveUi", "Hiding save dialog.");
        }
        try {
            this.mDialog.hide();
            this.mOverlayControl.showOverlays();
            return this.mPendingUi;
        } catch (Throwable th) {
            this.mOverlayControl.showOverlays();
            throw th;
        }
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public void destroy() {
        try {
            if (Helper.sDebug) {
                Slog.d("SaveUi", "destroy()");
            }
            throwIfDestroyed();
            this.mListener.onDestroy();
            this.mHandler.removeCallbacksAndMessages(this.mListener);
            this.mDialog.dismiss();
            this.mDestroyed = true;
        } finally {
            this.mOverlayControl.showOverlays();
        }
    }

    public final void throwIfDestroyed() {
        if (this.mDestroyed) {
            throw new IllegalStateException("cannot interact with a destroyed instance");
        }
    }

    public String toString() {
        CharSequence charSequence = this.mTitle;
        return charSequence == null ? "NO TITLE" : charSequence.toString();
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("title: ");
        printWriter.println(this.mTitle);
        printWriter.print(str);
        printWriter.print("subtitle: ");
        printWriter.println(this.mSubTitle);
        printWriter.print(str);
        printWriter.print("pendingUi: ");
        printWriter.println(this.mPendingUi);
        printWriter.print(str);
        printWriter.print("service: ");
        printWriter.println(this.mServicePackageName);
        printWriter.print(str);
        printWriter.print("app: ");
        printWriter.println(this.mComponentName.toShortString());
        printWriter.print(str);
        printWriter.print("compat mode: ");
        printWriter.println(this.mCompatMode);
        printWriter.print(str);
        printWriter.print("theme id: ");
        printWriter.print(this.mThemeId);
        int i = this.mThemeId;
        if (i == 16974809) {
            printWriter.println(" (dark)");
        } else if (i == 16974820) {
            printWriter.println(" (light)");
        } else {
            printWriter.println("(UNKNOWN_MODE)");
        }
        View decorView = this.mDialog.getWindow().getDecorView();
        int[] locationOnScreen = decorView.getLocationOnScreen();
        printWriter.print(str);
        printWriter.print("coordinates: ");
        printWriter.print('(');
        printWriter.print(locationOnScreen[0]);
        printWriter.print(',');
        printWriter.print(locationOnScreen[1]);
        printWriter.print(')');
        printWriter.print('(');
        printWriter.print(locationOnScreen[0] + decorView.getWidth());
        printWriter.print(',');
        printWriter.print(locationOnScreen[1] + decorView.getHeight());
        printWriter.println(')');
        printWriter.print(str);
        printWriter.print("destroyed: ");
        printWriter.println(this.mDestroyed);
    }
}
