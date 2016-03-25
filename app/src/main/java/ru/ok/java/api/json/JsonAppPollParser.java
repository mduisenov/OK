package ru.ok.java.api.json;

import android.support.annotation.NonNull;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.GraphPollStep;
import ru.ok.model.poll.ListPollQuestion;
import ru.ok.model.poll.ListPollQuestion.ListPollItem;
import ru.ok.model.poll.PollStep;
import ru.ok.model.poll.RatingPollQuestion;
import ru.ok.model.poll.SimplePollStep;
import ru.ok.model.poll.SkipPollQuestion;
import ru.ok.model.poll.TablePollQuestion;
import ru.ok.model.poll.TablePollQuestion.TablePollItem;
import ru.ok.model.poll.TextPollQuestion;

public final class JsonAppPollParser {
    public static JSONObject toJson(AppPollAnswer pollAnswer) {
        JSONObject result = new JSONObject();
        try {
            result.put("step", pollAnswer.getStep());
            result.put("skip", pollAnswer.isSkip());
            if (pollAnswer.getAnswer() != null) {
                result.put("answer", pollAnswer.getAnswer());
            }
            if (pollAnswer.getAnswerIndex() != null) {
                result.put("answerIndex", pollAnswer.getAnswerIndex());
            }
            if (pollAnswer.getAnswerText() != null) {
                result.put("answerText", pollAnswer.getAnswerText());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<PollStep> getSteps(JSONObject object, List<TablePollItem> defaultTableItems) throws JSONException {
        ArrayList<PollStep> steps = new ArrayList();
        int i = 1;
        while (true) {
            JSONObject questionObject = object.optJSONObject("step" + i);
            if (questionObject == null) {
                return steps;
            }
            steps.add(parseStep(questionObject, i, defaultTableItems));
            i++;
        }
    }

    public static boolean isNewPoll(JSONObject object) {
        return "notStarted".equals(object.optString("state"));
    }

    @NonNull
    public static String getStepsString(JSONObject responseJson, List<TablePollItem> defaultTableItems) throws JSONException {
        String questions = responseJson.optString("questions");
        if (questions == null) {
            throw new IllegalStateException("No field questions");
        }
        getSteps(new JSONObject(questions), defaultTableItems);
        return questions;
    }

    public static Settings getSettings(JSONObject responseJson) throws JSONException {
        String questions = responseJson.optString("questions");
        if (questions == null) {
            throw new IllegalStateException("No field questions");
        }
        JSONObject object = new JSONObject(questions);
        Settings settings = new Settings();
        settings.finalString = object.optString("final");
        settings.actionBarTitle = object.optString("actionbar_title");
        settings.buttonNext = object.optString("button_next");
        settings.buttonNextFinal = object.optString("button_next_final");
        settings.language = object.optString("language");
        settings.streamStart = object.optString("stream_start");
        settings.streamCancel = object.optString("stream_cancel");
        settings.streamTitle = object.optString("stream_title");
        settings.streamDescription = object.optString("stream_description");
        settings.streamResume = object.optString("stream_resume");
        settings.other = object.optString("other");
        return settings;
    }

    private static PollStep parseStep(JSONObject questionObject, int step, List<TablePollItem> defaultTableItems) throws JSONException {
        if (questionObject.has("step")) {
            if (questionObject.has("values")) {
                JSONObject values = questionObject.getJSONObject("values");
                Iterator<String> iterator = values.keys();
                HashMap<String, PollStep> questionHashMap = new HashMap();
                Logger.m172d("GraphAppPoll { ");
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    questionHashMap.put(key, parseStep(values.getJSONObject(key), step, defaultTableItems));
                }
                int linkedStep = getStep(questionObject.getString("step"));
                Logger.m172d("} GraphAppPoll");
                return new GraphPollStep(questionHashMap, linkedStep);
            }
        }
        if (questionObject.has(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE)) {
            if (questionObject.has("type")) {
                String type = questionObject.getString("type");
                String title = questionObject.getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
                Logger.m172d("SimpleAppPoll: " + type);
                Object obj = -1;
                switch (type.hashCode()) {
                    case 3322014:
                        if (type.equals("list")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 3492908:
                        if (type.equals("rank")) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 3556653:
                        if (type.equals(Stanza.TEXT)) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 110115790:
                        if (type.equals("table")) {
                            obj = null;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case RECEIVED_VALUE:
                        return new SimplePollStep(new TablePollQuestion(new ArrayList(defaultTableItems), title, step));
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        JSONArray values2 = questionObject.getJSONArray("values");
                        ArrayList<ListPollItem> items = new ArrayList(values2.length());
                        for (int i = 0; i < values2.length(); i++) {
                            JSONObject item = values2.getJSONObject(i);
                            items.add(new ListPollItem(item.getString("id"), item.getString("name"), item.optString("other")));
                        }
                        return new SimplePollStep(new ListPollQuestion(items, title, step));
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        return new SimplePollStep(new RatingPollQuestion(title, step));
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        return new SimplePollStep(new TextPollQuestion(title, step));
                }
            }
        }
        return new SimplePollStep(new SkipPollQuestion(step));
    }

    private static int getStep(String step) throws NumberFormatException {
        return Integer.parseInt(step.substring("step".length()));
    }

    public static int getVersion(JSONObject responseJson) throws JSONException {
        return responseJson.getInt("version");
    }

    public static AppPollAnswer answerByJson(JSONObject object) throws JSONException {
        int step = object.getInt("step");
        boolean skip = object.getBoolean("skip");
        String answer = object.optString("answer");
        String answerIndex = object.optString("answerIndex");
        String answerText = object.optString("answerText");
        if (answer == null) {
            return new AppPollAnswer(skip, step);
        }
        if (answerText != null) {
            return new AppPollAnswer(answer, answerText, answerIndex, step);
        }
        return new AppPollAnswer(answer, answerIndex, step);
    }
}
