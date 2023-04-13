package android.text.method;

import android.app.Dialog;
import android.content.Context;
import android.p008os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public class CharacterPickerDialog extends Dialog implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Button mCancelButton;
    private LayoutInflater mInflater;
    private boolean mInsert;
    private String mOptions;
    private Editable mText;
    private View mView;

    public CharacterPickerDialog(Context context, View view, Editable text, String options, boolean insert) {
        super(context, 16973913);
        this.mView = view;
        this.mText = text;
        this.mOptions = options;
        this.mInsert = insert;
        this.mInflater = LayoutInflater.from(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.token = this.mView.getApplicationWindowToken();
        params.type = 1003;
        params.flags |= 1;
        setContentView(C4057R.layout.character_picker);
        GridView grid = (GridView) findViewById(C4057R.C4059id.characterPicker);
        grid.setAdapter((ListAdapter) new OptionsAdapter(getContext()));
        grid.setOnItemClickListener(this);
        Button button = (Button) findViewById(C4057R.C4059id.cancel);
        this.mCancelButton = button;
        button.setOnClickListener(this);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        String result = String.valueOf(this.mOptions.charAt(position));
        replaceCharacterAndClose(result);
    }

    private void replaceCharacterAndClose(CharSequence replace) {
        int selEnd = Selection.getSelectionEnd(this.mText);
        if (this.mInsert || selEnd == 0) {
            this.mText.insert(selEnd, replace);
        } else {
            this.mText.replace(selEnd - 1, selEnd, replace);
        }
        dismiss();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mCancelButton) {
            dismiss();
        } else if (v instanceof Button) {
            CharSequence result = ((Button) v).getText();
            replaceCharacterAndClose(result);
        }
    }

    /* loaded from: classes3.dex */
    private class OptionsAdapter extends BaseAdapter {
        public OptionsAdapter(Context context) {
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            Button b = (Button) CharacterPickerDialog.this.mInflater.inflate(C4057R.layout.character_picker_button, (ViewGroup) null);
            b.setText(String.valueOf(CharacterPickerDialog.this.mOptions.charAt(position)));
            b.setOnClickListener(CharacterPickerDialog.this);
            return b;
        }

        @Override // android.widget.Adapter
        public final int getCount() {
            return CharacterPickerDialog.this.mOptions.length();
        }

        @Override // android.widget.Adapter
        public final Object getItem(int position) {
            return String.valueOf(CharacterPickerDialog.this.mOptions.charAt(position));
        }

        @Override // android.widget.Adapter
        public final long getItemId(int position) {
            return position;
        }
    }
}
