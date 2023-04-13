package android.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class ChooseAccountTypeActivity extends Activity {
    private static final String TAG = "AccountChooser";
    private ArrayList<AuthInfo> mAuthenticatorInfosToDisplay;
    private HashMap<String, AuthInfo> mTypeToAuthenticatorInfo = new HashMap<>();

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addSystemFlags(524288);
        if (Log.isLoggable(TAG, 2)) {
            Log.m106v(TAG, "ChooseAccountTypeActivity.onCreate(savedInstanceState=" + savedInstanceState + NavigationBarInflaterView.KEY_CODE_END);
        }
        Set<String> setOfAllowableAccountTypes = null;
        String[] validAccountTypes = getIntent().getStringArrayExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY);
        if (validAccountTypes != null) {
            setOfAllowableAccountTypes = new HashSet<>(validAccountTypes.length);
            for (String type : validAccountTypes) {
                setOfAllowableAccountTypes.add(type);
            }
        }
        buildTypeToAuthDescriptionMap();
        this.mAuthenticatorInfosToDisplay = new ArrayList<>(this.mTypeToAuthenticatorInfo.size());
        for (Map.Entry<String, AuthInfo> entry : this.mTypeToAuthenticatorInfo.entrySet()) {
            String type2 = entry.getKey();
            AuthInfo info = entry.getValue();
            if (setOfAllowableAccountTypes == null || setOfAllowableAccountTypes.contains(type2)) {
                this.mAuthenticatorInfosToDisplay.add(info);
            }
        }
        if (this.mAuthenticatorInfosToDisplay.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "no allowable account types");
            setResult(-1, new Intent().putExtras(bundle));
            finish();
        } else if (this.mAuthenticatorInfosToDisplay.size() == 1) {
            setResultAndFinish(this.mAuthenticatorInfosToDisplay.get(0).desc.type);
        } else {
            setContentView(C4057R.layout.choose_account_type);
            ListView list = (ListView) findViewById(16908298);
            list.setAdapter((ListAdapter) new AccountArrayAdapter(this, 17367043, this.mAuthenticatorInfosToDisplay));
            list.setChoiceMode(0);
            list.setTextFilterEnabled(false);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.accounts.ChooseAccountTypeActivity.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ChooseAccountTypeActivity chooseAccountTypeActivity = ChooseAccountTypeActivity.this;
                    chooseAccountTypeActivity.setResultAndFinish(((AuthInfo) chooseAccountTypeActivity.mAuthenticatorInfosToDisplay.get(position)).desc.type);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setResultAndFinish(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("accountType", type);
        setResult(-1, new Intent().putExtras(bundle));
        if (Log.isLoggable(TAG, 2)) {
            Log.m106v(TAG, "ChooseAccountTypeActivity.setResultAndFinish: selected account type " + type);
        }
        finish();
    }

    private void buildTypeToAuthDescriptionMap() {
        AuthenticatorDescription[] authenticatorTypes;
        for (AuthenticatorDescription desc : AccountManager.get(this).getAuthenticatorTypes()) {
            String name = null;
            Drawable icon = null;
            try {
                Context authContext = createPackageContext(desc.packageName, 0);
                icon = authContext.getDrawable(desc.iconId);
                CharSequence sequence = authContext.getResources().getText(desc.labelId);
                if (sequence != null) {
                    name = sequence.toString();
                }
            } catch (PackageManager.NameNotFoundException e) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.m104w(TAG, "No icon name for account type " + desc.type);
                }
            } catch (Resources.NotFoundException e2) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.m104w(TAG, "No icon resource for account type " + desc.type);
                }
            }
            AuthInfo authInfo = new AuthInfo(desc, name, icon);
            this.mTypeToAuthenticatorInfo.put(desc.type, authInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AuthInfo {
        final AuthenticatorDescription desc;
        final Drawable drawable;
        final String name;

        AuthInfo(AuthenticatorDescription desc, String name, Drawable drawable) {
            this.desc = desc;
            this.name = name;
            this.drawable = drawable;
        }
    }

    /* loaded from: classes.dex */
    private static class ViewHolder {
        ImageView icon;
        TextView text;

        private ViewHolder() {
        }
    }

    /* loaded from: classes.dex */
    private static class AccountArrayAdapter extends ArrayAdapter<AuthInfo> {
        private ArrayList<AuthInfo> mInfos;
        private LayoutInflater mLayoutInflater;

        public AccountArrayAdapter(Context context, int textViewResourceId, ArrayList<AuthInfo> infos) {
            super(context, textViewResourceId, infos);
            this.mInfos = infos;
            this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = this.mLayoutInflater.inflate(C4057R.layout.choose_account_row, (ViewGroup) null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(C4057R.C4059id.account_row_text);
                holder.icon = (ImageView) convertView.findViewById(C4057R.C4059id.account_row_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(this.mInfos.get(position).name);
            holder.icon.setImageDrawable(this.mInfos.get(position).drawable);
            return convertView;
        }
    }
}
