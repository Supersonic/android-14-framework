package android.view.textclassifier;

import android.app.RemoteAction;
import android.content.Intent;
import android.p008os.Bundle;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public final class ExtrasUtils {
    private static final String ACTIONS_INTENTS = "actions-intents";
    private static final String ACTION_INTENT = "action-intent";
    private static final String ENTITY_TYPE = "entity-type";
    private static final String FOREIGN_LANGUAGE = "foreign-language";
    private static final String MODEL_NAME = "model-name";
    private static final String SCORE = "score";

    private ExtrasUtils() {
    }

    public static Bundle getForeignLanguageExtra(TextClassification classification) {
        if (classification == null) {
            return null;
        }
        return classification.getExtras().getBundle(FOREIGN_LANGUAGE);
    }

    public static Intent getActionIntent(Bundle container) {
        return (Intent) container.getParcelable(ACTION_INTENT, Intent.class);
    }

    public static ArrayList<Intent> getActionsIntents(TextClassification classification) {
        if (classification == null) {
            return null;
        }
        return classification.getExtras().getParcelableArrayList(ACTIONS_INTENTS, Intent.class);
    }

    private static RemoteAction findAction(TextClassification classification, String intentAction) {
        ArrayList<Intent> actionIntents;
        if (classification != null && intentAction != null && (actionIntents = getActionsIntents(classification)) != null) {
            int size = actionIntents.size();
            for (int i = 0; i < size; i++) {
                Intent intent = actionIntents.get(i);
                if (intent != null && intentAction.equals(intent.getAction())) {
                    return classification.getActions().get(i);
                }
            }
        }
        return null;
    }

    public static RemoteAction findTranslateAction(TextClassification classification) {
        return findAction(classification, Intent.ACTION_TRANSLATE);
    }

    public static String getEntityType(Bundle extra) {
        if (extra == null) {
            return null;
        }
        return extra.getString(ENTITY_TYPE);
    }

    public static float getScore(Bundle extra) {
        if (extra == null) {
            return -1.0f;
        }
        return extra.getFloat(SCORE, -1.0f);
    }

    public static String getModelName(Bundle extra) {
        if (extra == null) {
            return null;
        }
        return extra.getString(MODEL_NAME);
    }
}
