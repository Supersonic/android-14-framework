package com.android.internal.app;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ShortcutInfo;
import android.content.p001pm.ShortcutManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.p008os.UserHandle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.app.ChooserTargetActionsDialogFragment;
import com.android.internal.app.ResolverListAdapter;
import com.android.internal.app.chooser.DisplayResolveInfo;
import com.android.internal.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes4.dex */
public class ChooserTargetActionsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String INTENT_FILTER_KEY = "intent_filter";
    public static final String IS_SHORTCUT_PINNED_KEY = "is_shortcut_pinned";
    public static final String SHORTCUT_ID_KEY = "shortcut_id";
    public static final String SHORTCUT_TITLE_KEY = "shortcut_title";
    public static final String TARGET_INFOS_KEY = "target_infos";
    public static final String USER_HANDLE_KEY = "user_handle";
    protected IntentFilter mIntentFilter;
    protected boolean mIsShortcutPinned;
    protected String mShortcutId;
    protected String mShortcutTitle;
    protected ArrayList<DisplayResolveInfo> mTargetInfos = new ArrayList<>();
    protected UserHandle mUserHandle;

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            setStateFromBundle(savedInstanceState);
        } else {
            setStateFromBundle(getArguments());
        }
    }

    void setStateFromBundle(Bundle b) {
        this.mTargetInfos = (ArrayList) b.get(TARGET_INFOS_KEY);
        this.mUserHandle = (UserHandle) b.get("user_handle");
        this.mShortcutId = b.getString(SHORTCUT_ID_KEY);
        this.mShortcutTitle = b.getString(SHORTCUT_TITLE_KEY);
        this.mIsShortcutPinned = b.getBoolean(IS_SHORTCUT_PINNED_KEY);
        this.mIntentFilter = (IntentFilter) b.get("intent_filter");
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("user_handle", this.mUserHandle);
        outState.putParcelableArrayList(TARGET_INFOS_KEY, this.mTargetInfos);
        outState.putString(SHORTCUT_ID_KEY, this.mShortcutId);
        outState.putBoolean(IS_SHORTCUT_PINNED_KEY, this.mIsShortcutPinned);
        outState.putString(SHORTCUT_TITLE_KEY, this.mShortcutTitle);
        outState.putParcelable("intent_filter", this.mIntentFilter);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setStateFromBundle(savedInstanceState);
        } else {
            setStateFromBundle(getArguments());
        }
        Optional.of(getDialog()).map(new Function() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Dialog) obj).getWindow();
            }
        }).ifPresent(new Consumer() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Window) obj).setBackgroundDrawable(new ColorDrawable(0));
            }
        });
        List<Pair<Drawable, CharSequence>> items = (List) this.mTargetInfos.stream().map(new Function() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Pair lambda$onCreateView$1;
                lambda$onCreateView$1 = ChooserTargetActionsDialogFragment.this.lambda$onCreateView$1((DisplayResolveInfo) obj);
                return lambda$onCreateView$1;
            }
        }).collect(Collectors.toList());
        View v = inflater.inflate(C4057R.layout.chooser_dialog, container, false);
        TextView title = (TextView) v.findViewById(16908310);
        ImageView icon = (ImageView) v.findViewById(16908294);
        RecyclerView rv = (RecyclerView) v.findViewById(C4057R.C4059id.listContainer);
        ResolverListAdapter.ResolveInfoPresentationGetter pg = getProvidingAppPresentationGetter();
        title.setText(isShortcutTarget() ? this.mShortcutTitle : pg.getLabel());
        icon.setImageDrawable(pg.getIcon(this.mUserHandle));
        rv.setAdapter(new VHAdapter(items));
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Pair lambda$onCreateView$1(DisplayResolveInfo dri) {
        return new Pair(getItemIcon(dri), getItemLabel(dri));
    }

    /* loaded from: classes4.dex */
    class VHAdapter extends RecyclerView.Adapter<C4094VH> {
        List<Pair<Drawable, CharSequence>> mItems;

        VHAdapter(List<Pair<Drawable, CharSequence>> items) {
            this.mItems = items;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.widget.RecyclerView.Adapter
        public C4094VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new C4094VH(LayoutInflater.from(parent.getContext()).inflate(C4057R.layout.chooser_dialog_item, parent, false));
        }

        @Override // com.android.internal.widget.RecyclerView.Adapter
        public void onBindViewHolder(C4094VH holder, int position) {
            holder.bind(this.mItems.get(position), position);
        }

        @Override // com.android.internal.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mItems.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.app.ChooserTargetActionsDialogFragment$VH */
    /* loaded from: classes4.dex */
    public class C4094VH extends RecyclerView.ViewHolder {
        ImageView mIcon;
        TextView mLabel;

        C4094VH(View itemView) {
            super(itemView);
            this.mLabel = (TextView) itemView.findViewById(C4057R.C4059id.text);
            this.mIcon = (ImageView) itemView.findViewById(16908294);
        }

        public void bind(Pair<Drawable, CharSequence> item, final int position) {
            this.mLabel.setText(item.second);
            if (item.first == null) {
                this.mIcon.setVisibility(8);
            } else {
                this.mIcon.setVisibility(0);
                this.mIcon.setImageDrawable(item.first);
            }
            this.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$VH$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChooserTargetActionsDialogFragment.C4094VH.this.lambda$bind$0(position, view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$bind$0(int position, View v) {
            ChooserTargetActionsDialogFragment chooserTargetActionsDialogFragment = ChooserTargetActionsDialogFragment.this;
            chooserTargetActionsDialogFragment.onClick(chooserTargetActionsDialogFragment.getDialog(), position);
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        if (isShortcutTarget()) {
            toggleShortcutPinned(this.mTargetInfos.get(which).getResolvedComponentName());
        } else {
            pinComponent(this.mTargetInfos.get(which).getResolvedComponentName());
        }
        ((ChooserActivity) getActivity()).handlePackagesChanged();
        dismiss();
    }

    private void toggleShortcutPinned(ComponentName name) {
        if (this.mIntentFilter == null) {
            return;
        }
        List<String> pinnedShortcuts = getPinnedShortcutsFromPackageAsUser(getContext(), this.mUserHandle, this.mIntentFilter, name.getPackageName());
        if (this.mIsShortcutPinned) {
            pinnedShortcuts.remove(this.mShortcutId);
        } else {
            pinnedShortcuts.add(this.mShortcutId);
        }
        ((LauncherApps) getContext().getSystemService(LauncherApps.class)).pinShortcuts(name.getPackageName(), pinnedShortcuts, this.mUserHandle);
    }

    private static List<String> getPinnedShortcutsFromPackageAsUser(Context context, UserHandle user, IntentFilter filter, final String packageName) {
        Context contextAsUser = context.createContextAsUser(user, 0);
        List<ShortcutManager.ShareShortcutInfo> targets = ((ShortcutManager) contextAsUser.getSystemService(ShortcutManager.class)).getShareTargets(filter);
        return (List) targets.stream().map(new Function() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ShortcutManager.ShareShortcutInfo) obj).getShortcutInfo();
            }
        }).filter(new Predicate() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ChooserTargetActionsDialogFragment.lambda$getPinnedShortcutsFromPackageAsUser$2(packageName, (ShortcutInfo) obj);
            }
        }).map(new Function() { // from class: com.android.internal.app.ChooserTargetActionsDialogFragment$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ShortcutInfo) obj).getId();
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getPinnedShortcutsFromPackageAsUser$2(String packageName, ShortcutInfo s) {
        return s.isPinned() && s.getPackage().equals(packageName);
    }

    private void pinComponent(ComponentName name) {
        SharedPreferences sp = ChooserActivity.getPinnedSharedPrefs(getContext());
        String key = name.flattenToString();
        boolean currentVal = sp.getBoolean(name.flattenToString(), false);
        if (currentVal) {
            sp.edit().remove(key).apply();
        } else {
            sp.edit().putBoolean(key, true).apply();
        }
    }

    private Drawable getPinIcon(boolean isPinned) {
        if (isPinned) {
            return getContext().getDrawable(C4057R.C4058drawable.ic_close);
        }
        return getContext().getDrawable(C4057R.C4058drawable.ic_chooser_pin_dialog);
    }

    private CharSequence getPinLabel(boolean isPinned, CharSequence targetLabel) {
        if (isPinned) {
            return getResources().getString(C4057R.string.unpin_specific_target, targetLabel);
        }
        return getResources().getString(C4057R.string.pin_specific_target, targetLabel);
    }

    protected CharSequence getItemLabel(DisplayResolveInfo dri) {
        PackageManager pm = getContext().getPackageManager();
        return getPinLabel(isPinned(dri), isShortcutTarget() ? this.mShortcutTitle : dri.getResolveInfo().loadLabel(pm));
    }

    protected Drawable getItemIcon(DisplayResolveInfo dri) {
        return getPinIcon(isPinned(dri));
    }

    private ResolverListAdapter.ResolveInfoPresentationGetter getProvidingAppPresentationGetter() {
        ActivityManager am = (ActivityManager) getContext().getSystemService("activity");
        int iconDpi = am.getLauncherLargeIconDensity();
        return new ResolverListAdapter.ResolveInfoPresentationGetter(getContext(), iconDpi, this.mTargetInfos.get(0).getResolveInfo());
    }

    private boolean isPinned(DisplayResolveInfo dri) {
        return isShortcutTarget() ? this.mIsShortcutPinned : dri.isPinned();
    }

    private boolean isShortcutTarget() {
        return this.mShortcutId != null;
    }
}
