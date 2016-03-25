package ru.ok.android.ui.polls;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ProgressBar;
import java.util.ArrayList;
import org.json.JSONArray;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.poll.AppPollPreferences;
import ru.ok.android.services.processors.poll.UploadAppPollAnswerTask;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.polls.fragment.FinalStepAppPollFragment;
import ru.ok.android.ui.polls.fragment.FragmentWithAnswer;
import ru.ok.android.ui.polls.fragment.ListStepAppPollFragment;
import ru.ok.android.ui.polls.fragment.ListStepAppPollFragment.ListStepInteractionListener;
import ru.ok.android.ui.polls.fragment.RatingStepAppPollFragment;
import ru.ok.android.ui.polls.fragment.RatingStepAppPollFragment.RatingStepInteractionListener;
import ru.ok.android.ui.polls.fragment.TableStepAppPollFragment;
import ru.ok.android.ui.polls.fragment.TableStepAppPollFragment.TableStepInteractionListener;
import ru.ok.android.ui.polls.fragment.TextStepAppPollFragment;
import ru.ok.android.ui.polls.fragment.TextStepAppPollFragment.StepTextInteractionListener;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.ViewUtil;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.java.api.json.JsonAppPollParser;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.ListPollQuestion;
import ru.ok.model.poll.PollQuestion;
import ru.ok.model.poll.PollStep;
import ru.ok.model.poll.RatingPollQuestion;
import ru.ok.model.poll.TablePollQuestion;
import ru.ok.model.poll.TextPollQuestion;

public class AppPollsActivity extends BaseActivity implements ListStepInteractionListener, RatingStepInteractionListener, TableStepInteractionListener, StepTextInteractionListener {
    private int buttonHeight;
    private ViewGroup container;
    private String currentAnswerId;
    private Button nextButton;
    private ArrayList<AppPollAnswer> pollAnswers;
    private ArrayList<PollStep> pollSteps;
    private ProgressBar progressBar;

    /* renamed from: ru.ok.android.ui.polls.AppPollsActivity.1 */
    class C11281 implements OnGlobalLayoutListener {
        boolean isOpened;
        final /* synthetic */ View val$activityRootView;

        C11281(View view) {
            this.val$activityRootView = view;
            this.isOpened = false;
        }

        public void onGlobalLayout() {
            if (this.val$activityRootView.getRootView().getHeight() - this.val$activityRootView.getHeight() > 100) {
                if (!this.isOpened) {
                    Fragment fragment = AppPollsActivity.this.getSupportFragmentManager().findFragmentById(2131624506);
                    if (fragment instanceof TableStepAppPollFragment) {
                        ((TableStepAppPollFragment) fragment).onShowKeyBoard();
                    } else if (fragment instanceof ListStepAppPollFragment) {
                        ((ListStepAppPollFragment) fragment).onShowKeyBoard();
                    }
                }
                this.isOpened = true;
            } else if (this.isOpened) {
                this.isOpened = false;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.AppPollsActivity.2 */
    class C11292 implements OnClickListener {
        C11292() {
        }

        public void onClick(View v) {
            AppPollAnswer answer;
            Fragment fragment = AppPollsActivity.this.getSupportFragmentManager().findFragmentById(2131624506);
            if (fragment instanceof FragmentWithAnswer) {
                answer = ((FragmentWithAnswer) fragment).getAnswer();
            } else {
                answer = new AppPollAnswer(true, -1);
            }
            if (answer == null) {
                answer = new AppPollAnswer(true, -1);
            }
            KeyBoardUtils.hideKeyBoard(AppPollsActivity.this);
            AppPollsActivity.this.closeNext();
            AppPollsActivity.this.onNewAnswer(answer);
            if (AppPollsActivity.this.pollAnswers.size() < AppPollsActivity.this.pollSteps.size()) {
                AppPollsActivity.this.nextQuestion();
            } else {
                AppPollsActivity.this.onEndPoll();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.AppPollsActivity.3 */
    class C11303 extends SimpleAnimatorListener {
        public boolean cancel;

        C11303() {
            this.cancel = false;
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.cancel) {
                AppPollsActivity.this.nextButton.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animation) {
            this.cancel = true;
        }
    }

    /* renamed from: ru.ok.android.ui.polls.AppPollsActivity.4 */
    class C11314 extends SimpleAnimatorListener {
        C11314() {
        }

        public void onAnimationEnd(Animator animation) {
            Fragment fragment = AppPollsActivity.this.getSupportFragmentManager().findFragmentById(2131624506);
            if (fragment instanceof TableStepAppPollFragment) {
                ((TableStepAppPollFragment) fragment).show(AppPollsActivity.this.buttonHeight, 400);
            } else if (fragment instanceof ListStepAppPollFragment) {
                ((ListStepAppPollFragment) fragment).show(AppPollsActivity.this.buttonHeight, 400);
            }
            AppPollsActivity.this.nextButton.animate().translationY(0.0f).setListener(null).setDuration(400).start();
        }
    }

    public void setListenerToRootView() {
        View activityRootView = getWindow().getDecorView().findViewById(16908290);
        if (activityRootView != null) {
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new C11281(activityRootView));
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903076);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(AppPollPreferences.getTextByKey(this, "app_poll_actionbar_title"));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(2130837621);
        }
        this.buttonHeight = getResources().getDimensionPixelSize(2131230875);
        this.nextButton = (Button) findViewById(2131624508);
        this.progressBar = (ProgressBar) findViewById(2131624507);
        this.container = (ViewGroup) findViewById(2131624506);
        this.nextButton.setOnClickListener(new C11292());
        if (savedInstanceState != null) {
            ArrayList<AppPollAnswer> answers = savedInstanceState.getParcelableArrayList("app_poll_answers");
            ArrayList<PollStep> steps = savedInstanceState.getParcelableArrayList("app_poll_steps");
            this.currentAnswerId = savedInstanceState.getString("app_poll_answer_id");
            if (steps != null) {
                this.pollSteps = steps;
                this.pollAnswers = answers;
                if (this.currentAnswerId != null) {
                    openNext();
                }
            } else {
                loadData();
            }
        } else {
            loadData();
        }
        setListenerToRootView();
    }

    private void loadData() {
        GlobalBus.send(2131623983, new BusEvent());
    }

    @Subscribe(on = 2131623946, to = 2131624164)
    public void onLoaded(BusEvent event) {
        if (-2 == event.resultCode) {
            finish();
            return;
        }
        this.pollSteps = event.bundleOutput.getParcelableArrayList("app_poll_steps");
        this.pollAnswers = event.bundleOutput.getParcelableArrayList("app_poll_answers");
        AppPollPreferences.setStep(this, (this.pollAnswers != null ? this.pollAnswers.size() : 0) + 1);
        nextQuestion();
        if (this.pollSteps.size() == 0) {
            finish();
        }
    }

    public void onNewAnswer(AppPollAnswer answer) {
        boolean intermediate;
        this.pollAnswers.add(answer);
        AppPollPreferences.setStep(this, this.pollAnswers.size() + 1);
        JSONArray answers = new JSONArray();
        answers.put(JsonAppPollParser.toJson(answer));
        if (this.pollAnswers.size() == this.pollSteps.size()) {
            intermediate = AppPollPreferences.isAppPollRepeatMode(this);
        } else {
            intermediate = true;
            saveAnswers();
        }
        PersistentTaskService.submit(this, new UploadAppPollAnswerTask(OdnoklassnikiApplication.getCurrentUser().getId(), AppPollPreferences.getVersion(this), false, intermediate, answer.getStep(), answers.toString()));
        if (this.pollAnswers.size() == this.pollSteps.size()) {
            AppPollPreferences.clearAppPoll(this);
        }
    }

    private void nextQuestion() {
        PollQuestion question = ((PollStep) this.pollSteps.get(this.pollAnswers.size())).getQuestion(this.pollAnswers);
        this.currentAnswerId = null;
        while (question.isSkip()) {
            onNewAnswer(new AppPollAnswer(true, this.pollAnswers.size() + 1));
            if (this.pollAnswers.size() >= this.pollSteps.size()) {
                onEndPoll();
                return;
            }
            question = ((PollStep) this.pollSteps.get(this.pollAnswers.size())).getQuestion(this.pollAnswers);
        }
        this.progressBar.setProgress((int) ((((float) this.progressBar.getMax()) * (((float) this.pollAnswers.size()) + 1.0f)) / ((float) this.pollSteps.size())));
        Fragment fragment = getFragment(question);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(2131624506, fragment).commit();
        }
    }

    private Fragment getFragment(PollQuestion question) {
        if (question instanceof TablePollQuestion) {
            TablePollQuestion tablePollQuestion = (TablePollQuestion) question;
            tablePollQuestion.shuffle();
            return TableStepAppPollFragment.newInstance(tablePollQuestion);
        } else if (question instanceof ListPollQuestion) {
            ListPollQuestion listPollQuestion = (ListPollQuestion) question;
            listPollQuestion.shuffle();
            return ListStepAppPollFragment.newInstance(listPollQuestion);
        } else if (question instanceof RatingPollQuestion) {
            return RatingStepAppPollFragment.newInstance((RatingPollQuestion) question);
        } else {
            if (question instanceof TextPollQuestion) {
                return TextStepAppPollFragment.newInstance((TextPollQuestion) question);
            }
            return null;
        }
    }

    private void onEndPoll() {
        getSupportFragmentManager().beginTransaction().replace(2131624506, FinalStepAppPollFragment.newInstance()).commit();
        this.currentAnswerId = null;
    }

    private void saveAnswers() {
        Bundle input = new Bundle();
        input.putParcelableArrayList("app_poll_answers", new ArrayList(this.pollAnswers));
        GlobalBus.send(2131624099, new BusEvent(input));
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<PollStep> steps = savedInstanceState.getParcelableArrayList("app_poll_steps");
            if (steps != null) {
                this.pollSteps = steps;
                this.pollAnswers = savedInstanceState.getParcelableArrayList("app_poll_answers");
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.pollSteps != null) {
            outState.putParcelableArrayList("app_poll_answers", this.pollAnswers);
            outState.putParcelableArrayList("app_poll_steps", this.pollSteps);
            outState.putString("app_poll_answer_id", this.currentAnswerId);
        }
    }

    public void onBackPressed() {
        if (this.pollSteps.size() > this.pollAnswers.size()) {
            PersistentTaskService.submit(this, new UploadAppPollAnswerTask(OdnoklassnikiApplication.getCurrentUser().getId(), AppPollPreferences.getVersion(this), !AppPollPreferences.isAppPollRepeatMode(this), true, this.pollAnswers.size(), new JSONArray().toString()));
            AppPollPreferences.clearAppPoll(this);
            KeyBoardUtils.hideKeyBoard(this);
        }
        super.onBackPressed();
    }

    public void onAnswer(String answerId) {
        if (!(answerId == null || answerId.equals(this.currentAnswerId))) {
            if (this.currentAnswerId == null) {
                openNext();
            }
            this.currentAnswerId = answerId;
        }
        if (answerId == null) {
            closeNext();
            this.currentAnswerId = null;
        }
    }

    private void closeNext() {
        this.nextButton.clearAnimation();
        this.container.clearAnimation();
        Fragment fragment = getSupportFragmentManager().findFragmentById(2131624506);
        if (fragment instanceof TableStepAppPollFragment) {
            ((TableStepAppPollFragment) fragment).hide(400);
        } else if (fragment instanceof ListStepAppPollFragment) {
            ((ListStepAppPollFragment) fragment).hide(400);
        }
        this.nextButton.animate().translationY((float) this.buttonHeight).setDuration(400).setListener(new C11303()).start();
        this.container.animate().setListener(null).translationY(0.0f).setDuration(400).start();
        ViewUtil.resetLayoutMargins(this.container, 0, 0, 0, 0);
    }

    private void openNext() {
        String key;
        if (this.pollSteps.size() == this.pollAnswers.size() + 1) {
            key = "app_poll_button_next_final";
        } else {
            key = "app_poll_button_next";
        }
        this.nextButton.setText(AppPollPreferences.getTextByKey(this, key));
        this.nextButton.setVisibility(0);
        this.nextButton.clearAnimation();
        this.container.clearAnimation();
        this.nextButton.animate().translationY((float) this.buttonHeight).setDuration(0).setListener(new C11314()).start();
    }
}
