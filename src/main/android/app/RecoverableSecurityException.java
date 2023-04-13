package android.app;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.C4057R;
import java.util.Objects;
/* loaded from: classes.dex */
public final class RecoverableSecurityException extends SecurityException implements Parcelable {
    public static final Parcelable.Creator<RecoverableSecurityException> CREATOR = new Parcelable.Creator<RecoverableSecurityException>() { // from class: android.app.RecoverableSecurityException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecoverableSecurityException createFromParcel(Parcel source) {
            return new RecoverableSecurityException(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecoverableSecurityException[] newArray(int size) {
            return new RecoverableSecurityException[size];
        }
    };
    private static final String TAG = "RecoverableSecurityException";
    private final RemoteAction mUserAction;
    private final CharSequence mUserMessage;

    public RecoverableSecurityException(Parcel in) {
        this(new SecurityException(in.readString()), in.readCharSequence(), RemoteAction.CREATOR.createFromParcel(in));
    }

    public RecoverableSecurityException(Throwable cause, CharSequence userMessage, RemoteAction userAction) {
        super(cause.getMessage());
        this.mUserMessage = (CharSequence) Objects.requireNonNull(userMessage);
        this.mUserAction = (RemoteAction) Objects.requireNonNull(userAction);
    }

    public CharSequence getUserMessage() {
        return this.mUserMessage;
    }

    public RemoteAction getUserAction() {
        return this.mUserAction;
    }

    public void showAsNotification(Context context, String channelId) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NotificationManager.class);
        Notification.Builder builder = new Notification.Builder(context, channelId).setSmallIcon(C4057R.C4058drawable.ic_print_error).setContentTitle(this.mUserAction.getTitle()).setContentText(this.mUserMessage).setContentIntent(this.mUserAction.getActionIntent()).setCategory(Notification.CATEGORY_ERROR);
        nm.notify(TAG, this.mUserAction.getActionIntent().getCreatorUid(), builder.build());
    }

    public void showAsDialog(Activity activity) {
        LocalDialog dialog = new LocalDialog();
        Bundle args = new Bundle();
        args.putParcelable(TAG, this);
        dialog.setArguments(args);
        String tag = "RecoverableSecurityException_" + this.mUserAction.getActionIntent().getCreatorUid();
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment old = fm.findFragmentByTag(tag);
        if (old != null) {
            ft.remove(old);
        }
        ft.add(dialog, tag);
        ft.commitAllowingStateLoss();
    }

    /* loaded from: classes.dex */
    public static class LocalDialog extends DialogFragment {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final RecoverableSecurityException e = (RecoverableSecurityException) getArguments().getParcelable(RecoverableSecurityException.TAG, RecoverableSecurityException.class);
            return new AlertDialog.Builder(getActivity()).setMessage(e.mUserMessage).setPositiveButton(e.mUserAction.getTitle(), new DialogInterface.OnClickListener() { // from class: android.app.RecoverableSecurityException$LocalDialog$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    RecoverableSecurityException.this.mUserAction.getActionIntent().send();
                }
            }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
        dest.writeCharSequence(this.mUserMessage);
        this.mUserAction.writeToParcel(dest, flags);
    }
}
