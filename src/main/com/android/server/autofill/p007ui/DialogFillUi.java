package com.android.server.autofill.p007ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.text.TextUtils;
import android.util.PluralsMessageFormatter;
import android.util.Slog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.server.autofill.AutofillManagerService;
import com.android.server.autofill.Helper;
import com.android.server.autofill.p007ui.DialogFillUi;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/* renamed from: com.android.server.autofill.ui.DialogFillUi */
/* loaded from: classes.dex */
public final class DialogFillUi {
    public final ItemsAdapter mAdapter;
    public AnnounceFilterResult mAnnounceFilterResult;
    public final UiCallback mCallback;
    public final ComponentName mComponentName;
    public final Context mContext;
    public boolean mDestroyed;
    public final Dialog mDialog;
    public String mFilterText;
    public final ListView mListView;
    public final OverlayControl mOverlayControl;
    public final String mServicePackageName;
    public final int mThemeId;
    public final int mVisibleDatasetsMaxCount;

    /* renamed from: com.android.server.autofill.ui.DialogFillUi$UiCallback */
    /* loaded from: classes.dex */
    public interface UiCallback {
        void onCanceled();

        void onDatasetPicked(Dataset dataset);

        void onDismissed();

        void onResponsePicked(FillResponse fillResponse);

        void onShown();

        void startIntentSender(IntentSender intentSender);
    }

    public String toString() {
        return "NO TITLE";
    }

    public DialogFillUi(Context context, FillResponse fillResponse, AutofillId autofillId, String str, Drawable drawable, String str2, ComponentName componentName, OverlayControl overlayControl, boolean z, UiCallback uiCallback) {
        if (Helper.sVerbose) {
            Slog.v("DialogFillUi", "nightMode: " + z);
        }
        int i = z ? 16974809 : 16974820;
        this.mThemeId = i;
        this.mCallback = uiCallback;
        this.mOverlayControl = overlayControl;
        this.mServicePackageName = str2;
        this.mComponentName = componentName;
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, i);
        this.mContext = contextThemeWrapper;
        View inflate = LayoutInflater.from(contextThemeWrapper).inflate(17367109, (ViewGroup) null);
        if (fillResponse.getShowFillDialogIcon()) {
            setServiceIcon(inflate, drawable);
        }
        setHeader(inflate, fillResponse);
        this.mVisibleDatasetsMaxCount = getVisibleDatasetsMaxCount();
        if (fillResponse.getAuthentication() != null) {
            this.mListView = null;
            this.mAdapter = null;
            try {
                initialAuthenticationLayout(inflate, fillResponse);
            } catch (RuntimeException e) {
                uiCallback.onCanceled();
                Slog.e("DialogFillUi", "Error inflating remote views", e);
                this.mDialog = null;
                return;
            }
        } else {
            this.mAdapter = new ItemsAdapter(createDatasetItems(fillResponse, autofillId));
            this.mListView = (ListView) inflate.findViewById(16908805);
            initialDatasetLayout(inflate, str);
        }
        setDismissButton(inflate);
        Dialog dialog = new Dialog(contextThemeWrapper, i);
        this.mDialog = dialog;
        dialog.setContentView(inflate);
        setDialogParamsAsBottomSheet();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                DialogFillUi.this.lambda$new$0(dialogInterface);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                DialogFillUi.this.lambda$new$1(dialogInterface);
            }
        });
        show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface) {
        this.mCallback.onCanceled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface) {
        this.mCallback.onShown();
    }

    public final int getVisibleDatasetsMaxCount() {
        if (AutofillManagerService.getVisibleDatasetsMaxCount() > 0) {
            int visibleDatasetsMaxCount = AutofillManagerService.getVisibleDatasetsMaxCount();
            if (Helper.sVerbose) {
                Slog.v("DialogFillUi", "overriding maximum visible datasets to " + visibleDatasetsMaxCount);
            }
            return visibleDatasetsMaxCount;
        }
        return this.mContext.getResources().getInteger(17694726);
    }

    public final void setDialogParamsAsBottomSheet() {
        Window window = this.mDialog.getWindow();
        window.setType(2038);
        window.addFlags(131074);
        window.setDimAmount(0.6f);
        window.addPrivateFlags(16);
        window.setSoftInputMode(32);
        window.setGravity(81);
        window.setCloseOnTouchOutside(true);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.accessibilityTitle = this.mContext.getString(17039710);
        attributes.windowAnimations = 16974613;
    }

    public final void setServiceIcon(View view, Drawable drawable) {
        if (drawable == null) {
            return;
        }
        ImageView imageView = (ImageView) view.findViewById(16908815);
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        if (Helper.sDebug) {
            Slog.d("DialogFillUi", "Adding service icon (" + minimumWidth + "x" + minimumHeight + ")");
        }
        imageView.setImageDrawable(drawable);
        imageView.setVisibility(0);
    }

    public final void setHeader(View view, FillResponse fillResponse) {
        RemoteViews dialogHeader = fillResponse.getDialogHeader();
        if (dialogHeader == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view.findViewById(16908804);
        viewGroup.addView(dialogHeader.applyWithTheme(this.mContext, (ViewGroup) view, new RemoteViews.InteractionHandler() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda6
            public final boolean onInteraction(View view2, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                boolean lambda$setHeader$2;
                lambda$setHeader$2 = DialogFillUi.this.lambda$setHeader$2(view2, pendingIntent, remoteResponse);
                return lambda$setHeader$2;
            }
        }, this.mThemeId));
        viewGroup.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setHeader$2(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        if (pendingIntent != null) {
            this.mCallback.startIntentSender(pendingIntent.getIntentSender());
            return true;
        }
        return true;
    }

    public final void setDismissButton(View view) {
        TextView textView = (TextView) view.findViewById(16908806);
        textView.setText(17039715);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DialogFillUi.this.lambda$setDismissButton$3(view2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDismissButton$3(View view) {
        this.mCallback.onDismissed();
    }

    public final void setContinueButton(View view, View.OnClickListener onClickListener) {
        TextView textView = (TextView) view.findViewById(16908808);
        textView.setText(17039708);
        textView.setOnClickListener(onClickListener);
        textView.setVisibility(0);
    }

    public final void initialAuthenticationLayout(View view, final FillResponse fillResponse) {
        RemoteViews dialogPresentation = fillResponse.getDialogPresentation();
        if (dialogPresentation == null) {
            dialogPresentation = fillResponse.getPresentation();
        }
        if (dialogPresentation == null) {
            throw new RuntimeException("No presentation for fill dialog authentication");
        }
        ViewGroup viewGroup = (ViewGroup) view.findViewById(16908803);
        viewGroup.addView(dialogPresentation.applyWithTheme(this.mContext, (ViewGroup) view, new RemoteViews.InteractionHandler() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda2
            public final boolean onInteraction(View view2, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                boolean lambda$initialAuthenticationLayout$4;
                lambda$initialAuthenticationLayout$4 = DialogFillUi.this.lambda$initialAuthenticationLayout$4(view2, pendingIntent, remoteResponse);
                return lambda$initialAuthenticationLayout$4;
            }
        }, this.mThemeId));
        viewGroup.setVisibility(0);
        viewGroup.setFocusable(true);
        viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DialogFillUi.this.lambda$initialAuthenticationLayout$5(fillResponse, view2);
            }
        });
        setContinueButton(view, new View.OnClickListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DialogFillUi.this.lambda$initialAuthenticationLayout$6(fillResponse, view2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initialAuthenticationLayout$4(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        if (pendingIntent != null) {
            this.mCallback.startIntentSender(pendingIntent.getIntentSender());
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialAuthenticationLayout$5(FillResponse fillResponse, View view) {
        this.mCallback.onResponsePicked(fillResponse);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialAuthenticationLayout$6(FillResponse fillResponse, View view) {
        this.mCallback.onResponsePicked(fillResponse);
    }

    public final ArrayList<ViewItem> createDatasetItems(FillResponse fillResponse, AutofillId autofillId) {
        boolean z;
        Pattern pattern;
        int size = fillResponse.getDatasets().size();
        if (Helper.sVerbose) {
            Slog.v("DialogFillUi", "Number datasets: " + size + " max visible: " + this.mVisibleDatasetsMaxCount);
        }
        RemoteViews.InteractionHandler interactionHandler = new RemoteViews.InteractionHandler() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda10
            public final boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                boolean lambda$createDatasetItems$7;
                lambda$createDatasetItems$7 = DialogFillUi.this.lambda$createDatasetItems$7(view, pendingIntent, remoteResponse);
                return lambda$createDatasetItems$7;
            }
        };
        ArrayList<ViewItem> arrayList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Dataset dataset = (Dataset) fillResponse.getDatasets().get(i);
            int indexOf = dataset.getFieldIds().indexOf(autofillId);
            if (indexOf >= 0) {
                RemoteViews fieldDialogPresentation = dataset.getFieldDialogPresentation(indexOf);
                if (fieldDialogPresentation == null) {
                    if (Helper.sDebug) {
                        Slog.w("DialogFillUi", "not displaying UI on field " + autofillId + " because service didn't provide a presentation for it on " + dataset);
                    }
                } else {
                    try {
                        if (Helper.sVerbose) {
                            Slog.v("DialogFillUi", "setting remote view for " + autofillId);
                        }
                        String str = null;
                        View applyWithTheme = fieldDialogPresentation.applyWithTheme(this.mContext, null, interactionHandler, this.mThemeId);
                        Dataset.DatasetFieldFilter filter = dataset.getFilter(indexOf);
                        if (filter == null) {
                            AutofillValue autofillValue = (AutofillValue) dataset.getFieldValues().get(indexOf);
                            z = true;
                            pattern = null;
                            str = (autofillValue == null || !autofillValue.isText()) ? null : autofillValue.getTextValue().toString().toLowerCase();
                        } else {
                            Pattern pattern2 = filter.pattern;
                            if (pattern2 == null) {
                                if (Helper.sVerbose) {
                                    Slog.v("DialogFillUi", "Explicitly disabling filter at id " + autofillId + " for dataset #" + indexOf);
                                }
                                z = false;
                            } else {
                                z = true;
                            }
                            pattern = pattern2;
                        }
                        arrayList.add(new ViewItem(dataset, pattern, z, str, applyWithTheme));
                    } catch (RuntimeException e) {
                        Slog.e("DialogFillUi", "Error inflating remote views", e);
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createDatasetItems$7(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        if (pendingIntent != null) {
            this.mCallback.startIntentSender(pendingIntent.getIntentSender());
            return true;
        }
        return true;
    }

    public final void initialDatasetLayout(View view, String str) {
        final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda7
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                DialogFillUi.this.lambda$initialDatasetLayout$8(adapterView, view2, i, j);
            }
        };
        this.mListView.setAdapter((ListAdapter) this.mAdapter);
        this.mListView.setVisibility(0);
        this.mListView.setOnItemClickListener(onItemClickListener);
        if (this.mAdapter.getCount() == 1) {
            setContinueButton(view, new View.OnClickListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    onItemClickListener.onItemClick(null, null, 0, 0L);
                }
            });
        }
        if (str == null) {
            this.mFilterText = null;
        } else {
            this.mFilterText = str.toLowerCase();
        }
        final int count = this.mAdapter.getCount();
        this.mAdapter.getFilter().filter(this.mFilterText, new Filter.FilterListener() { // from class: com.android.server.autofill.ui.DialogFillUi$$ExternalSyntheticLambda9
            @Override // android.widget.Filter.FilterListener
            public final void onFilterComplete(int i) {
                DialogFillUi.this.lambda$initialDatasetLayout$10(count, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialDatasetLayout$8(AdapterView adapterView, View view, int i, long j) {
        this.mCallback.onDatasetPicked(this.mAdapter.getItem(i).dataset);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialDatasetLayout$10(int i, int i2) {
        if (this.mDestroyed) {
            return;
        }
        if (i2 <= 0) {
            if (Helper.sDebug) {
                String str = this.mFilterText;
                int length = str != null ? str.length() : 0;
                Slog.d("DialogFillUi", "No dataset matches filter with " + length + " chars");
            }
            this.mCallback.onCanceled();
            return;
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

    public final void show() {
        Slog.i("DialogFillUi", "Showing fill dialog");
        this.mDialog.show();
        this.mOverlayControl.hideOverlays();
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public void destroy() {
        try {
            if (Helper.sDebug) {
                Slog.d("DialogFillUi", "destroy()");
            }
            throwIfDestroyed();
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

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("service: ");
        printWriter.println(this.mServicePackageName);
        printWriter.print(str);
        printWriter.print("app: ");
        printWriter.println(this.mComponentName.toShortString());
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

    public final void announceSearchResultIfNeeded() {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mAnnounceFilterResult == null) {
                this.mAnnounceFilterResult = new AnnounceFilterResult();
            }
            this.mAnnounceFilterResult.post();
        }
    }

    /* renamed from: com.android.server.autofill.ui.DialogFillUi$AnnounceFilterResult */
    /* loaded from: classes.dex */
    public final class AnnounceFilterResult implements Runnable {
        public AnnounceFilterResult() {
        }

        public void post() {
            remove();
            DialogFillUi.this.mListView.postDelayed(this, 1000L);
        }

        public void remove() {
            DialogFillUi.this.mListView.removeCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            String format;
            int count = DialogFillUi.this.mListView.getAdapter().getCount();
            if (count <= 0) {
                format = DialogFillUi.this.mContext.getString(17039711);
            } else {
                HashMap hashMap = new HashMap();
                hashMap.put("count", Integer.valueOf(count));
                format = PluralsMessageFormatter.format(DialogFillUi.this.mContext.getResources(), hashMap, 17039712);
            }
            DialogFillUi.this.mListView.announceForAccessibility(format);
        }
    }

    /* renamed from: com.android.server.autofill.ui.DialogFillUi$ItemsAdapter */
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

        /* renamed from: com.android.server.autofill.ui.DialogFillUi$ItemsAdapter$1 */
        /* loaded from: classes.dex */
        public class C05441 extends Filter {
            public C05441() {
            }

            @Override // android.widget.Filter
            public Filter.FilterResults performFiltering(final CharSequence charSequence) {
                List list = (List) ItemsAdapter.this.mAllItems.stream().filter(new Predicate() { // from class: com.android.server.autofill.ui.DialogFillUi$ItemsAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean matches;
                        matches = ((DialogFillUi.ViewItem) obj).matches(charSequence);
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
                    DialogFillUi.this.announceSearchResultIfNeeded();
                }
                ItemsAdapter.this.notifyDataSetChanged();
            }
        }

        @Override // android.widget.Filterable
        public Filter getFilter() {
            return new C05441();
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

    /* renamed from: com.android.server.autofill.ui.DialogFillUi$ViewItem */
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
}
