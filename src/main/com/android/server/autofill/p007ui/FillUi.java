package com.android.server.autofill.p007ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.text.TextUtils;
import android.util.PluralsMessageFormatter;
import android.util.Slog;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAutofillWindowPresenter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.server.UiThread;
import com.android.server.autofill.AutofillManagerService;
import com.android.server.autofill.Helper;
import com.android.server.autofill.p007ui.FillUi;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/* renamed from: com.android.server.autofill.ui.FillUi */
/* loaded from: classes.dex */
public final class FillUi {
    public static final TypedValue sTempTypedValue = new TypedValue();
    public final ItemsAdapter mAdapter;
    public AnnounceFilterResult mAnnounceFilterResult;
    public final Callback mCallback;
    public int mContentHeight;
    public int mContentWidth;
    public final Context mContext;
    public boolean mDestroyed;
    public String mFilterText;
    public final View mFooter;
    public final boolean mFullScreen;
    public final View mHeader;
    public final ListView mListView;
    public final Point mTempPoint;
    public final int mThemeId;
    public final int mVisibleDatasetsMaxCount;
    public final AnchoredWindow mWindow;
    public final AutofillWindowPresenter mWindowPresenter;

    /* renamed from: com.android.server.autofill.ui.FillUi$Callback */
    /* loaded from: classes.dex */
    public interface Callback {
        void cancelSession();

        void dispatchUnhandledKey(KeyEvent keyEvent);

        void onCanceled();

        void onDatasetPicked(Dataset dataset);

        void onDestroy();

        void onResponsePicked(FillResponse fillResponse);

        void onShown();

        void requestHideFillUi();

        void requestShowFillUi(int i, int i2, IAutofillWindowPresenter iAutofillWindowPresenter);

        void startIntentSender(IntentSender intentSender);
    }

    public static boolean isFullScreen(Context context) {
        if (Helper.sFullScreenMode != null) {
            if (Helper.sVerbose) {
                Slog.v("FillUi", "forcing full-screen mode to " + Helper.sFullScreenMode);
            }
            return Helper.sFullScreenMode.booleanValue();
        }
        return context.getPackageManager().hasSystemFeature("android.software.leanback");
    }

    public FillUi(Context context, final FillResponse fillResponse, AutofillId autofillId, String str, OverlayControl overlayControl, CharSequence charSequence, Drawable drawable, boolean z, Callback callback) {
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        RemoteViews.InteractionHandler interactionHandler;
        int i;
        Pattern pattern;
        boolean z2;
        String str2;
        Point point = new Point();
        this.mTempPoint = point;
        this.mWindowPresenter = new AutofillWindowPresenter();
        if (Helper.sVerbose) {
            Slog.v("FillUi", "nightMode: " + z);
        }
        int i2 = z ? 16974807 : 16974819;
        this.mThemeId = i2;
        this.mCallback = callback;
        boolean isFullScreen = isFullScreen(context);
        this.mFullScreen = isFullScreen;
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, i2);
        this.mContext = contextThemeWrapper;
        LayoutInflater from = LayoutInflater.from(contextThemeWrapper);
        RemoteViews header = fillResponse.getHeader();
        RemoteViews footer = fillResponse.getFooter();
        if (isFullScreen) {
            viewGroup = (ViewGroup) from.inflate(17367107, (ViewGroup) null);
        } else if (header != null || footer != null) {
            viewGroup = (ViewGroup) from.inflate(17367108, (ViewGroup) null);
        } else {
            viewGroup = (ViewGroup) from.inflate(17367106, (ViewGroup) null);
        }
        viewGroup.setClipToOutline(true);
        TextView textView = (TextView) viewGroup.findViewById(16908802);
        if (textView != null) {
            textView.setText(contextThemeWrapper.getString(17039735, charSequence));
        }
        ImageView imageView = (ImageView) viewGroup.findViewById(16908799);
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
        if (isFullScreen) {
            contextThemeWrapper.getDisplayNoVerify().getSize(point);
            this.mContentWidth = -1;
            this.mContentHeight = point.y / 2;
            if (Helper.sVerbose) {
                Slog.v("FillUi", "initialized fillscreen LayoutParams " + this.mContentWidth + "," + this.mContentHeight);
            }
        }
        viewGroup.addOnUnhandledKeyEventListener(new View.OnUnhandledKeyEventListener() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda1
            @Override // android.view.View.OnUnhandledKeyEventListener
            public final boolean onUnhandledKeyEvent(View view, KeyEvent keyEvent) {
                boolean lambda$new$0;
                lambda$new$0 = FillUi.this.lambda$new$0(view, keyEvent);
                return lambda$new$0;
            }
        });
        if (AutofillManagerService.getVisibleDatasetsMaxCount() > 0) {
            int visibleDatasetsMaxCount = AutofillManagerService.getVisibleDatasetsMaxCount();
            this.mVisibleDatasetsMaxCount = visibleDatasetsMaxCount;
            if (Helper.sVerbose) {
                Slog.v("FillUi", "overriding maximum visible datasets to " + visibleDatasetsMaxCount);
            }
        } else {
            this.mVisibleDatasetsMaxCount = contextThemeWrapper.getResources().getInteger(17694726);
        }
        RemoteViews.InteractionHandler interactionHandler2 = new RemoteViews.InteractionHandler() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda2
            public final boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                boolean lambda$new$1;
                lambda$new$1 = FillUi.this.lambda$new$1(view, pendingIntent, remoteResponse);
                return lambda$new$1;
            }
        };
        if (fillResponse.getAuthentication() != null) {
            this.mHeader = null;
            this.mListView = null;
            this.mFooter = null;
            this.mAdapter = null;
            ViewGroup viewGroup3 = (ViewGroup) viewGroup.findViewById(16908801);
            try {
                View applyWithTheme = fillResponse.getPresentation().applyWithTheme(contextThemeWrapper, viewGroup, interactionHandler2, i2);
                viewGroup3.addView(applyWithTheme);
                viewGroup3.setFocusable(true);
                viewGroup3.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        FillUi.this.lambda$new$2(fillResponse, view);
                    }
                });
                if (!isFullScreen) {
                    resolveMaxWindowSize(contextThemeWrapper, point);
                    applyWithTheme.getLayoutParams().width = isFullScreen ? point.x : -2;
                    applyWithTheme.getLayoutParams().height = -2;
                    viewGroup.measure(View.MeasureSpec.makeMeasureSpec(point.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(point.y, Integer.MIN_VALUE));
                    this.mContentWidth = applyWithTheme.getMeasuredWidth();
                    this.mContentHeight = applyWithTheme.getMeasuredHeight();
                }
                this.mWindow = new AnchoredWindow(viewGroup, overlayControl);
                requestShowFillUi();
                return;
            } catch (RuntimeException e) {
                callback.onCanceled();
                Slog.e("FillUi", "Error inflating remote views", e);
                this.mWindow = null;
                return;
            }
        }
        int size = fillResponse.getDatasets().size();
        if (Helper.sVerbose) {
            Slog.v("FillUi", "Number datasets: " + size + " max visible: " + this.mVisibleDatasetsMaxCount);
        }
        if (header != null) {
            interactionHandler = newInteractionBlocker();
            View applyWithTheme2 = header.applyWithTheme(contextThemeWrapper, null, interactionHandler, i2);
            this.mHeader = applyWithTheme2;
            LinearLayout linearLayout = (LinearLayout) viewGroup.findViewById(16908798);
            applyCancelAction(applyWithTheme2, fillResponse.getCancelIds());
            if (Helper.sVerbose) {
                Slog.v("FillUi", "adding header");
            }
            linearLayout.addView(applyWithTheme2);
            linearLayout.setVisibility(0);
            viewGroup2 = null;
        } else {
            viewGroup2 = null;
            this.mHeader = null;
            interactionHandler = null;
        }
        if (footer != null) {
            LinearLayout linearLayout2 = (LinearLayout) viewGroup.findViewById(16908797);
            if (linearLayout2 != null) {
                View applyWithTheme3 = footer.applyWithTheme(contextThemeWrapper, viewGroup2, interactionHandler == null ? newInteractionBlocker() : interactionHandler, i2);
                this.mFooter = applyWithTheme3;
                applyCancelAction(applyWithTheme3, fillResponse.getCancelIds());
                if (Helper.sVerbose) {
                    Slog.v("FillUi", "adding footer");
                }
                linearLayout2.addView(applyWithTheme3);
                linearLayout2.setVisibility(0);
            } else {
                this.mFooter = viewGroup2;
            }
        } else {
            this.mFooter = viewGroup2;
        }
        ArrayList arrayList = new ArrayList(size);
        int i3 = 0;
        while (i3 < size) {
            Dataset dataset = (Dataset) fillResponse.getDatasets().get(i3);
            int indexOf = dataset.getFieldIds().indexOf(autofillId);
            if (indexOf >= 0) {
                RemoteViews fieldPresentation = dataset.getFieldPresentation(indexOf);
                if (fieldPresentation == null) {
                    Slog.w("FillUi", "not displaying UI on field " + autofillId + " because service didn't provide a presentation for it on " + dataset);
                } else {
                    try {
                        if (Helper.sVerbose) {
                            Slog.v("FillUi", "setting remote view for " + autofillId);
                        }
                        i = size;
                    } catch (RuntimeException e2) {
                        e = e2;
                        i = size;
                    }
                    try {
                        View applyWithTheme4 = fieldPresentation.applyWithTheme(this.mContext, null, interactionHandler2, this.mThemeId);
                        Dataset.DatasetFieldFilter filter = dataset.getFilter(indexOf);
                        if (filter == null) {
                            AutofillValue autofillValue = (AutofillValue) dataset.getFieldValues().get(indexOf);
                            str2 = (autofillValue == null || !autofillValue.isText()) ? null : autofillValue.getTextValue().toString().toLowerCase();
                            pattern = null;
                            z2 = true;
                        } else {
                            Pattern pattern2 = filter.pattern;
                            if (pattern2 == null) {
                                if (Helper.sVerbose) {
                                    Slog.v("FillUi", "Explicitly disabling filter at id " + autofillId + " for dataset #" + indexOf);
                                }
                                pattern = pattern2;
                                z2 = false;
                            } else {
                                pattern = pattern2;
                                z2 = true;
                            }
                            str2 = null;
                        }
                        applyCancelAction(applyWithTheme4, fillResponse.getCancelIds());
                        arrayList.add(new ViewItem(dataset, pattern, z2, str2, applyWithTheme4));
                    } catch (RuntimeException e3) {
                        e = e3;
                        Slog.e("FillUi", "Error inflating remote views", e);
                        i3++;
                        size = i;
                    }
                    i3++;
                    size = i;
                }
            }
            i = size;
            i3++;
            size = i;
        }
        ItemsAdapter itemsAdapter = new ItemsAdapter(arrayList);
        this.mAdapter = itemsAdapter;
        ListView listView = (ListView) viewGroup.findViewById(16908800);
        this.mListView = listView;
        listView.setAdapter((ListAdapter) itemsAdapter);
        listView.setVisibility(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda4
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i4, long j) {
                FillUi.this.lambda$new$3(adapterView, view, i4, j);
            }
        });
        if (str == null) {
            this.mFilterText = null;
        } else {
            this.mFilterText = str.toLowerCase();
        }
        applyNewFilterText();
        this.mWindow = new AnchoredWindow(viewGroup, overlayControl);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(View view, KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 4 || keyCode == 66 || keyCode == 111) {
            return false;
        }
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return false;
            default:
                this.mCallback.dispatchUnhandledKey(keyEvent);
                return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        if (pendingIntent != null) {
            this.mCallback.startIntentSender(pendingIntent.getIntentSender());
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(FillResponse fillResponse, View view) {
        this.mCallback.onResponsePicked(fillResponse);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(AdapterView adapterView, View view, int i, long j) {
        this.mCallback.onDatasetPicked(this.mAdapter.getItem(i).dataset);
    }

    public final void applyCancelAction(View view, int[] iArr) {
        if (iArr == null) {
            return;
        }
        if (Helper.sDebug) {
            Slog.d("FillUi", "fill UI has " + iArr.length + " actions");
        }
        if (!(view instanceof ViewGroup)) {
            Slog.w("FillUi", "cannot apply actions because fill UI root is not a ViewGroup: " + view);
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i : iArr) {
            View findViewById = viewGroup.findViewById(i);
            if (findViewById == null) {
                Slog.w("FillUi", "Ignoring cancel action for view " + i + " because it's not on " + viewGroup);
            } else {
                findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda6
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        FillUi.this.lambda$applyCancelAction$4(view2);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCancelAction$4(View view) {
        if (Helper.sVerbose) {
            Slog.v("FillUi", " Cancelling session after " + view + " clicked");
        }
        this.mCallback.cancelSession();
    }

    public void requestShowFillUi() {
        this.mCallback.requestShowFillUi(this.mContentWidth, this.mContentHeight, this.mWindowPresenter);
    }

    public final RemoteViews.InteractionHandler newInteractionBlocker() {
        return new RemoteViews.InteractionHandler() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda5
            public final boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                boolean lambda$newInteractionBlocker$5;
                lambda$newInteractionBlocker$5 = FillUi.lambda$newInteractionBlocker$5(view, pendingIntent, remoteResponse);
                return lambda$newInteractionBlocker$5;
            }
        };
    }

    public static /* synthetic */ boolean lambda$newInteractionBlocker$5(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        if (Helper.sVerbose) {
            Slog.v("FillUi", "Ignoring click on " + view);
            return true;
        }
        return true;
    }

    public final void applyNewFilterText() {
        final int count = this.mAdapter.getCount();
        this.mAdapter.getFilter().filter(this.mFilterText, new Filter.FilterListener() { // from class: com.android.server.autofill.ui.FillUi$$ExternalSyntheticLambda0
            @Override // android.widget.Filter.FilterListener
            public final void onFilterComplete(int i) {
                FillUi.this.lambda$applyNewFilterText$6(count, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyNewFilterText$6(int i, int i2) {
        if (this.mDestroyed) {
            return;
        }
        if (i2 <= 0) {
            if (Helper.sDebug) {
                String str = this.mFilterText;
                int length = str != null ? str.length() : 0;
                Slog.d("FillUi", "No dataset matches filter with " + length + " chars");
            }
            this.mCallback.requestHideFillUi();
            return;
        }
        if (updateContentSize()) {
            requestShowFillUi();
        }
        if (this.mAdapter.getCount() > this.mVisibleDatasetsMaxCount) {
            this.mListView.setVerticalScrollBarEnabled(true);
            this.mListView.onVisibilityAggregated(true);
        } else {
            this.mListView.setVerticalScrollBarEnabled(false);
        }
        if (this.mAdapter.getCount() != i) {
            this.mListView.requestLayout();
        }
    }

    public void setFilterText(String str) {
        throwIfDestroyed();
        if (this.mAdapter == null) {
            if (TextUtils.isEmpty(str)) {
                requestShowFillUi();
                return;
            } else {
                this.mCallback.requestHideFillUi();
                return;
            }
        }
        String lowerCase = str == null ? null : str.toLowerCase();
        if (Objects.equals(this.mFilterText, lowerCase)) {
            return;
        }
        this.mFilterText = lowerCase;
        applyNewFilterText();
    }

    public void destroy(boolean z) {
        throwIfDestroyed();
        AnchoredWindow anchoredWindow = this.mWindow;
        if (anchoredWindow != null) {
            anchoredWindow.hide(false);
        }
        this.mCallback.onDestroy();
        if (z) {
            this.mCallback.requestHideFillUi();
        }
        this.mDestroyed = true;
    }

    public final boolean updateContentSize() {
        boolean z;
        boolean z2;
        ItemsAdapter itemsAdapter = this.mAdapter;
        if (itemsAdapter == null) {
            return false;
        }
        if (this.mFullScreen) {
            return true;
        }
        if (itemsAdapter.getCount() <= 0) {
            if (this.mContentWidth != 0) {
                this.mContentWidth = 0;
                z2 = true;
            } else {
                z2 = false;
            }
            if (this.mContentHeight != 0) {
                this.mContentHeight = 0;
                return true;
            }
            return z2;
        }
        Point point = this.mTempPoint;
        resolveMaxWindowSize(this.mContext, point);
        this.mContentWidth = 0;
        this.mContentHeight = 0;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(point.x, Integer.MIN_VALUE);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(point.y, Integer.MIN_VALUE);
        int count = this.mAdapter.getCount();
        View view = this.mHeader;
        if (view != null) {
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            z = updateWidth(this.mHeader, point) | false | updateHeight(this.mHeader, point);
        } else {
            z = false;
        }
        for (int i = 0; i < count; i++) {
            View view2 = this.mAdapter.getItem(i).view;
            view2.measure(makeMeasureSpec, makeMeasureSpec2);
            z |= updateWidth(view2, point);
            if (i < this.mVisibleDatasetsMaxCount) {
                z |= updateHeight(view2, point);
            }
        }
        View view3 = this.mFooter;
        if (view3 != null) {
            view3.measure(makeMeasureSpec, makeMeasureSpec2);
            return updateWidth(this.mFooter, point) | z | updateHeight(this.mFooter, point);
        }
        return z;
    }

    public final boolean updateWidth(View view, Point point) {
        int max = Math.max(this.mContentWidth, Math.min(view.getMeasuredWidth(), point.x));
        if (max != this.mContentWidth) {
            this.mContentWidth = max;
            return true;
        }
        return false;
    }

    public final boolean updateHeight(View view, Point point) {
        int min = Math.min(view.getMeasuredHeight(), point.y);
        int i = this.mContentHeight;
        int i2 = min + i;
        if (i2 != i) {
            this.mContentHeight = i2;
            return true;
        }
        return false;
    }

    public final void throwIfDestroyed() {
        if (this.mDestroyed) {
            throw new IllegalStateException("cannot interact with a destroyed instance");
        }
    }

    public static void resolveMaxWindowSize(Context context, Point point) {
        context.getDisplayNoVerify().getSize(point);
        TypedValue typedValue = sTempTypedValue;
        context.getTheme().resolveAttribute(17956884, typedValue, true);
        int i = point.x;
        point.x = (int) typedValue.getFraction(i, i);
        context.getTheme().resolveAttribute(17956883, typedValue, true);
        int i2 = point.y;
        point.y = (int) typedValue.getFraction(i2, i2);
    }

    /* renamed from: com.android.server.autofill.ui.FillUi$ViewItem */
    /* loaded from: classes.dex */
    public static class ViewItem {
        public final Dataset dataset;
        public final Pattern filter;
        public final boolean filterable;
        public final String value;
        public final View view;

        public ViewItem(Dataset dataset, Pattern pattern, boolean z, String str, View view) {
            this.dataset = dataset;
            this.value = str;
            this.view = view;
            this.filter = pattern;
            this.filterable = z;
        }

        public boolean matches(CharSequence charSequence) {
            if (TextUtils.isEmpty(charSequence)) {
                return true;
            }
            if (this.filterable) {
                String lowerCase = charSequence.toString().toLowerCase();
                Pattern pattern = this.filter;
                if (pattern != null) {
                    return pattern.matcher(lowerCase).matches();
                }
                String str = this.value;
                if (str == null) {
                    return this.dataset.getAuthentication() == null;
                }
                return str.toLowerCase().startsWith(lowerCase);
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ViewItem:[view=");
            sb.append(this.view.getAutofillId());
            Dataset dataset = this.dataset;
            String id = dataset == null ? null : dataset.getId();
            if (id != null) {
                sb.append(", dataset=");
                sb.append(id);
            }
            if (this.value != null) {
                sb.append(", value=");
                sb.append(this.value.length());
                sb.append("_chars");
            }
            if (this.filterable) {
                sb.append(", filterable");
            }
            if (this.filter != null) {
                sb.append(", filter=");
                sb.append(this.filter.pattern().length());
                sb.append("_chars");
            }
            sb.append(']');
            return sb.toString();
        }
    }

    /* renamed from: com.android.server.autofill.ui.FillUi$AutofillWindowPresenter */
    /* loaded from: classes.dex */
    public final class AutofillWindowPresenter extends IAutofillWindowPresenter.Stub {
        public AutofillWindowPresenter() {
        }

        public void show(final WindowManager.LayoutParams layoutParams, Rect rect, boolean z, int i) {
            if (Helper.sVerbose) {
                Slog.v("FillUi", "AutofillWindowPresenter.show(): fit=" + z + ", params=" + Helper.paramsToString(layoutParams));
            }
            UiThread.getHandler().post(new Runnable() { // from class: com.android.server.autofill.ui.FillUi$AutofillWindowPresenter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FillUi.AutofillWindowPresenter.this.lambda$show$0(layoutParams);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$show$0(WindowManager.LayoutParams layoutParams) {
            FillUi.this.mWindow.show(layoutParams);
        }

        public void hide(Rect rect) {
            Handler handler = UiThread.getHandler();
            final AnchoredWindow anchoredWindow = FillUi.this.mWindow;
            Objects.requireNonNull(anchoredWindow);
            handler.post(new Runnable() { // from class: com.android.server.autofill.ui.FillUi$AutofillWindowPresenter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    FillUi.AnchoredWindow.this.hide();
                }
            });
        }
    }

    /* renamed from: com.android.server.autofill.ui.FillUi$AnchoredWindow */
    /* loaded from: classes.dex */
    public final class AnchoredWindow {
        public final View mContentView;
        public final OverlayControl mOverlayControl;
        public WindowManager.LayoutParams mShowParams;
        public boolean mShowing;
        public final WindowManager mWm;

        public AnchoredWindow(View view, OverlayControl overlayControl) {
            this.mWm = (WindowManager) view.getContext().getSystemService(WindowManager.class);
            this.mContentView = view;
            this.mOverlayControl = overlayControl;
        }

        public void show(WindowManager.LayoutParams layoutParams) {
            this.mShowParams = layoutParams;
            if (Helper.sVerbose) {
                Slog.v("FillUi", "show(): showing=" + this.mShowing + ", params=" + Helper.paramsToString(layoutParams));
            }
            try {
                layoutParams.packageName = PackageManagerShellCommandDataLoader.PACKAGE;
                layoutParams.setTitle("Autofill UI");
                if (!this.mShowing) {
                    layoutParams.accessibilityTitle = this.mContentView.getContext().getString(17039710);
                    this.mWm.addView(this.mContentView, layoutParams);
                    this.mOverlayControl.hideOverlays();
                    this.mShowing = true;
                    FillUi.this.mCallback.onShown();
                    return;
                }
                this.mWm.updateViewLayout(this.mContentView, layoutParams);
            } catch (WindowManager.BadTokenException unused) {
                if (Helper.sDebug) {
                    Slog.d("FillUi", "Filed with with token " + layoutParams.token + " gone.");
                }
                FillUi.this.mCallback.onDestroy();
            } catch (IllegalStateException e) {
                Slog.wtf("FillUi", "Exception showing window " + layoutParams, e);
                FillUi.this.mCallback.onDestroy();
            }
        }

        public void hide() {
            hide(true);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v3, types: [com.android.server.autofill.ui.OverlayControl] */
        public void hide(boolean z) {
            try {
                try {
                    if (this.mShowing) {
                        this.mWm.removeView(this.mContentView);
                        this.mShowing = false;
                    }
                } catch (IllegalStateException e) {
                    Slog.e("FillUi", "Exception hiding window ", e);
                    if (z) {
                        FillUi.this.mCallback.onDestroy();
                    }
                }
            } finally {
                this.mOverlayControl.showOverlays();
            }
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("mCallback: ");
        printWriter.println(this.mCallback != null);
        printWriter.print(str);
        printWriter.print("mFullScreen: ");
        printWriter.println(this.mFullScreen);
        printWriter.print(str);
        printWriter.print("mVisibleDatasetsMaxCount: ");
        printWriter.println(this.mVisibleDatasetsMaxCount);
        if (this.mHeader != null) {
            printWriter.print(str);
            printWriter.print("mHeader: ");
            printWriter.println(this.mHeader);
        }
        if (this.mListView != null) {
            printWriter.print(str);
            printWriter.print("mListView: ");
            printWriter.println(this.mListView);
        }
        if (this.mFooter != null) {
            printWriter.print(str);
            printWriter.print("mFooter: ");
            printWriter.println(this.mFooter);
        }
        if (this.mAdapter != null) {
            printWriter.print(str);
            printWriter.print("mAdapter: ");
            printWriter.println(this.mAdapter);
        }
        if (this.mFilterText != null) {
            printWriter.print(str);
            printWriter.print("mFilterText: ");
            Helper.printlnRedactedText(printWriter, this.mFilterText);
        }
        printWriter.print(str);
        printWriter.print("mContentWidth: ");
        printWriter.println(this.mContentWidth);
        printWriter.print(str);
        printWriter.print("mContentHeight: ");
        printWriter.println(this.mContentHeight);
        printWriter.print(str);
        printWriter.print("mDestroyed: ");
        printWriter.println(this.mDestroyed);
        printWriter.print(str);
        printWriter.print("theme id: ");
        printWriter.print(this.mThemeId);
        int i = this.mThemeId;
        if (i == 16974807) {
            printWriter.println(" (dark)");
        } else if (i == 16974819) {
            printWriter.println(" (light)");
        } else {
            printWriter.println("(UNKNOWN_MODE)");
        }
        if (this.mWindow != null) {
            printWriter.print(str);
            printWriter.print("mWindow: ");
            String str2 = str + "  ";
            printWriter.println();
            printWriter.print(str2);
            printWriter.print("showing: ");
            printWriter.println(this.mWindow.mShowing);
            printWriter.print(str2);
            printWriter.print("view: ");
            printWriter.println(this.mWindow.mContentView);
            if (this.mWindow.mShowParams != null) {
                printWriter.print(str2);
                printWriter.print("params: ");
                printWriter.println(this.mWindow.mShowParams);
            }
            printWriter.print(str2);
            printWriter.print("screen coordinates: ");
            if (this.mWindow.mContentView == null) {
                printWriter.println("N/A");
                return;
            }
            int[] locationOnScreen = this.mWindow.mContentView.getLocationOnScreen();
            printWriter.print(locationOnScreen[0]);
            printWriter.print("x");
            printWriter.println(locationOnScreen[1]);
        }
    }

    public final void announceSearchResultIfNeeded() {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mAnnounceFilterResult == null) {
                this.mAnnounceFilterResult = new AnnounceFilterResult();
            }
            this.mAnnounceFilterResult.post();
        }
    }

    /* renamed from: com.android.server.autofill.ui.FillUi$ItemsAdapter */
    /* loaded from: classes.dex */
    public final class ItemsAdapter extends BaseAdapter implements Filterable {
        public final List<ViewItem> mAllItems;
        public final List<ViewItem> mFilteredItems;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public ItemsAdapter(List<ViewItem> list) {
            ArrayList arrayList = new ArrayList();
            this.mFilteredItems = arrayList;
            this.mAllItems = Collections.unmodifiableList(new ArrayList(list));
            arrayList.addAll(list);
        }

        /* renamed from: com.android.server.autofill.ui.FillUi$ItemsAdapter$1 */
        /* loaded from: classes.dex */
        public class C05451 extends Filter {
            public C05451() {
            }

            @Override // android.widget.Filter
            public Filter.FilterResults performFiltering(final CharSequence charSequence) {
                List list = (List) ItemsAdapter.this.mAllItems.stream().filter(new Predicate() { // from class: com.android.server.autofill.ui.FillUi$ItemsAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean matches;
                        matches = ((FillUi.ViewItem) obj).matches(charSequence);
                        return matches;
                    }
                }).collect(Collectors.toList());
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = list;
                filterResults.count = list.size();
                return filterResults;
            }

            @Override // android.widget.Filter
            public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                int size = ItemsAdapter.this.mFilteredItems.size();
                ItemsAdapter.this.mFilteredItems.clear();
                if (filterResults.count > 0) {
                    ItemsAdapter.this.mFilteredItems.addAll((List) filterResults.values);
                }
                if (size != ItemsAdapter.this.mFilteredItems.size()) {
                    FillUi.this.announceSearchResultIfNeeded();
                }
                ItemsAdapter.this.notifyDataSetChanged();
            }
        }

        @Override // android.widget.Filterable
        public Filter getFilter() {
            return new C05451();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mFilteredItems.size();
        }

        @Override // android.widget.Adapter
        public ViewItem getItem(int i) {
            return this.mFilteredItems.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getItem(i).view;
        }

        public String toString() {
            return "ItemsAdapter: [all=" + this.mAllItems + ", filtered=" + this.mFilteredItems + "]";
        }
    }

    /* renamed from: com.android.server.autofill.ui.FillUi$AnnounceFilterResult */
    /* loaded from: classes.dex */
    public final class AnnounceFilterResult implements Runnable {
        public AnnounceFilterResult() {
        }

        public void post() {
            remove();
            FillUi.this.mListView.postDelayed(this, 1000L);
        }

        public void remove() {
            FillUi.this.mListView.removeCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            String format;
            int count = FillUi.this.mListView.getAdapter().getCount();
            if (count <= 0) {
                format = FillUi.this.mContext.getString(17039711);
            } else {
                HashMap hashMap = new HashMap();
                hashMap.put("count", Integer.valueOf(count));
                format = PluralsMessageFormatter.format(FillUi.this.mContext.getResources(), hashMap, 17039712);
            }
            FillUi.this.mListView.announceForAccessibility(format);
        }
    }
}
