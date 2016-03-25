package ru.ok.android.ui.polls.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.gridlayout.C0028R;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import ru.ok.android.ui.polls.choice.SingleChoiceRecycleViewAdapter;
import ru.ok.android.ui.polls.choice.SingleChoiceRecycleViewAdapter.ViewHolderFinder;
import ru.ok.android.ui.toolbar.ViewMarginTranslationListener;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.RecyclerMergeAdapter;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.TablePollQuestion;
import ru.ok.model.poll.TablePollQuestion.TablePollItem;

public class TableStepAppPollFragment extends Fragment implements FragmentWithAnswer {
    private AppPollAdapter adapter;
    private View bottom;
    private GridLayoutManager layoutManager;
    private RecyclerMergeAdapter mergeAdapter;
    private View otherLayout;
    private EditText otherText;
    private TextWatcher otherTextWatcher;
    private TablePollQuestion question;
    private TableStepInteractionListener tableStepInteractionListener;
    private ViewMarginTranslationListener translationListener;
    private Runnable updateRecyclerRunnable;

    public interface TableStepInteractionListener {
        void onAnswer(@Nullable String str);
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.TableStepAppPollFragment.1 */
    class C11391 extends SpanSizeLookup {
        C11391() {
        }

        public int getSpanSize(int position) {
            return position == 0 ? 3 : 1;
        }
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.TableStepAppPollFragment.2 */
    class C11402 extends ItemDecoration {
        final /* synthetic */ int val$space;

        C11402(int i) {
            this.val$space = i;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.left = this.val$space / 2;
            outRect.right = this.val$space / 2;
            if (parent.getChildPosition(view) >= 4) {
                outRect.top = 30;
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.TableStepAppPollFragment.3 */
    class C11413 implements Runnable {
        C11413() {
        }

        public void run() {
            int position = TableStepAppPollFragment.this.mergeAdapter.getRecyclerPositionByAdapterPosition(TableStepAppPollFragment.this.adapter, TableStepAppPollFragment.this.adapter.getCheckedPosition());
            if (position >= 0) {
                TableStepAppPollFragment.this.layoutManager.scrollToPositionWithOffset(position, 20);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.TableStepAppPollFragment.4 */
    class C11424 implements TextWatcher {
        C11424() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (TableStepAppPollFragment.this.otherText.getText().toString().isEmpty()) {
                TableStepAppPollFragment.this.tableStepInteractionListener.onAnswer(null);
                return;
            }
            int position = TableStepAppPollFragment.this.adapter.getCheckedPosition();
            if (position >= 0) {
                TableStepAppPollFragment.this.tableStepInteractionListener.onAnswer(((TablePollItem) TableStepAppPollFragment.this.adapter.items.get(position)).getId());
            }
        }
    }

    private class AppPollAdapter extends SingleChoiceRecycleViewAdapter<AppPollViewHolder> {
        private final List<TablePollItem> items;

        /* renamed from: ru.ok.android.ui.polls.fragment.TableStepAppPollFragment.AppPollAdapter.1 */
        class C11431 implements ViewHolderFinder {
            final /* synthetic */ TableStepAppPollFragment val$this$0;

            C11431(TableStepAppPollFragment tableStepAppPollFragment) {
                this.val$this$0 = tableStepAppPollFragment;
            }

            public ViewHolder findViewHolderForPosition(RecyclerView recyclerView, int adapterPosition) {
                return recyclerView.findViewHolderForPosition(this.val$this$0.mergeAdapter.getRecyclerPositionByAdapterPosition(this.val$this$0.adapter, adapterPosition));
            }
        }

        AppPollAdapter(RecyclerView recyclerView, List<TablePollItem> items) {
            super(recyclerView, new C11431(TableStepAppPollFragment.this));
            this.items = items;
        }

        public AppPollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AppPollViewHolder(LayoutInflater.from(parent.getContext()).inflate(2130903101, parent, false));
        }

        public void onBindViewHolder(AppPollViewHolder holder, int position) {
            holder.bind(position);
        }

        public int getItemViewType(int position) {
            return 1;
        }

        public int getItemCount() {
            return this.items.size();
        }

        public void onRestoreInstanceState(Bundle bundle) {
            super.onRestoreInstanceState(bundle);
            if (getCheckedPosition() > -1) {
                updateEditText((TablePollItem) this.items.get(getCheckedPosition()), true);
            }
        }

        public void updateEditText(TablePollItem tablePollItem, boolean isChecked) {
            if (tablePollItem.isOther()) {
                if (isChecked) {
                    TableStepAppPollFragment.this.otherLayout.setVisibility(0);
                    KeyBoardUtils.showKeyBoard(TableStepAppPollFragment.this.getActivity(), TableStepAppPollFragment.this.otherText);
                    TableStepAppPollFragment.this.bottom.postDelayed(TableStepAppPollFragment.this.updateRecyclerRunnable, 600);
                    return;
                }
                TableStepAppPollFragment.this.otherLayout.setVisibility(8);
                KeyBoardUtils.hideKeyBoard(TableStepAppPollFragment.this.getActivity());
            } else if (isChecked) {
                TableStepAppPollFragment.this.otherLayout.setVisibility(8);
                KeyBoardUtils.hideKeyBoard(TableStepAppPollFragment.this.getActivity());
            }
        }
    }

    private class AppPollViewHolder extends ViewHolder implements OnClickListener, Checkable {
        private final ImageView imageView;
        private final ImageView markView;
        private final TextView textView;

        public AppPollViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.imageView = (ImageView) itemView.findViewById(2131624617);
            this.textView = (TextView) itemView.findViewById(2131624615);
            this.markView = (ImageView) itemView.findViewById(2131624616);
        }

        public void setChecked(boolean checked) {
            bind((TablePollItem) TableStepAppPollFragment.this.adapter.items.get(getMergeAdapterPosition()), checked);
            TableStepAppPollFragment.this.adapter.updateEditText((TablePollItem) TableStepAppPollFragment.this.adapter.items.get(getMergeAdapterPosition()), checked);
        }

        public boolean isChecked() {
            return TableStepAppPollFragment.this.adapter.isChecked(getMergeAdapterPosition());
        }

        private int getMergeAdapterPosition() {
            return TableStepAppPollFragment.this.mergeAdapter.getAdapterPositionByRecyclerPosition(TableStepAppPollFragment.this.adapter, getPosition());
        }

        public void toggle() {
        }

        public void bind(int adapterPosition) {
            TablePollItem tablePollItem = (TablePollItem) TableStepAppPollFragment.this.adapter.items.get(adapterPosition);
            this.textView.setText(tablePollItem.getTitle());
            bind(tablePollItem, TableStepAppPollFragment.this.adapter.isChecked(adapterPosition));
        }

        private void bind(TablePollItem tablePollItem, boolean isChecked) {
            if (isChecked) {
                this.imageView.setImageResource(tablePollItem.getSelectedResId());
                this.textView.setTextColor(this.textView.getContext().getResources().getColor(2131492902));
                this.markView.setVisibility(0);
                this.itemView.setBackgroundResource(2130837645);
                return;
            }
            this.imageView.setImageResource(tablePollItem.getResId());
            this.textView.setTextColor(this.textView.getContext().getResources().getColor(2131493224));
            this.markView.setVisibility(8);
            this.itemView.setBackgroundResource(2130837644);
        }

        public void onClick(View v) {
            TableStepAppPollFragment.this.adapter.onChecked(getMergeAdapterPosition());
            TablePollItem item = (TablePollItem) TableStepAppPollFragment.this.question.getItems().get(getMergeAdapterPosition());
            String otherId = TableStepAppPollFragment.this.otherText.getText().toString().isEmpty() ? null : item.getId();
            TableStepInteractionListener access$400 = TableStepAppPollFragment.this.tableStepInteractionListener;
            if (!item.isOther()) {
                otherId = item.getId();
            }
            access$400.onAnswer(otherId);
        }
    }

    private static class HeaderTableAdapter extends Adapter<HeaderViewHolder> implements AdapterItemViewTypeMaxValueProvider {
        private final HeaderViewHolder headerHolder;

        public HeaderTableAdapter(View headerView) {
            this.headerHolder = new HeaderViewHolder(headerView);
        }

        public HeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return this.headerHolder;
        }

        public void onBindViewHolder(HeaderViewHolder holder, int position) {
        }

        public int getItemViewType(int position) {
            return 1;
        }

        public int getItemViewTypeMaxValue() {
            return 1;
        }

        public int getItemCount() {
            return 1;
        }
    }

    private static class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static TableStepAppPollFragment newInstance(TablePollQuestion question) {
        TableStepAppPollFragment fragment = new TableStepAppPollFragment();
        Bundle args = new Bundle();
        args.putParcelable("table_question", question);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.question = (TablePollQuestion) getArguments().getParcelable("table_question");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(2130903192, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(2131624822);
        TextView otherTitle = (TextView) v.findViewById(2131624824);
        this.otherText = (EditText) v.findViewById(2131624825);
        this.otherLayout = v.findViewById(2131624823);
        this.bottom = v.findViewById(C0028R.id.bottom);
        int space = getActivity().getResources().getDimensionPixelOffset(2131230879);
        this.layoutManager = new GridLayoutManager(getActivity(), 3);
        this.layoutManager.setSpanSizeLookup(new C11391());
        recyclerView.setLayoutManager(this.layoutManager);
        recyclerView.addItemDecoration(new C11402(space));
        this.mergeAdapter = new RecyclerMergeAdapter();
        this.adapter = new AppPollAdapter(recyclerView, this.question.getItems());
        View headerView = inflater.inflate(2130903102, recyclerView, false);
        this.mergeAdapter.addAdapter(new HeaderTableAdapter(headerView));
        this.mergeAdapter.addAdapter(this.adapter);
        recyclerView.setAdapter(this.mergeAdapter);
        String text = ((TablePollItem) this.question.getItems().get(this.question.getItems().size() - 1)).getOtherText();
        if (text != null) {
            otherTitle.setText(text);
        }
        ((TextView) headerView.findViewById(2131624614)).setText(this.question.getTitle());
        this.updateRecyclerRunnable = new C11413();
        this.otherTextWatcher = new C11424();
        return v;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.adapter.onSaveInstanceState(outState);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.adapter.onRestoreInstanceState(savedInstanceState);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.tableStepInteractionListener = (TableStepInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TableStepInteractionListener");
        }
    }

    public void onStart() {
        super.onStart();
        this.otherText.addTextChangedListener(this.otherTextWatcher);
    }

    public void onStop() {
        super.onStop();
        this.otherText.removeTextChangedListener(this.otherTextWatcher);
    }

    public void onDetach() {
        super.onDetach();
        this.tableStepInteractionListener = null;
    }

    public AppPollAnswer getAnswer() {
        TablePollItem pollItem = (TablePollItem) this.adapter.items.get(this.adapter.getCheckedPosition());
        String realPosition = Integer.toString(this.adapter.getCheckedPosition() + 1);
        if (pollItem.isOther()) {
            return new AppPollAnswer(pollItem.getId(), this.otherText.getText().toString(), realPosition, this.question.getStep());
        }
        return new AppPollAnswer(pollItem.getId(), realPosition, this.question.getStep());
    }

    public void show(int size, int duration) {
        if (this.translationListener == null) {
            this.translationListener = new ViewMarginTranslationListener(this.bottom);
            this.translationListener.setTranslationSize((float) size);
            this.translationListener.setTranslation(0.0f);
        }
        this.translationListener.animateShow(duration);
        this.bottom.postDelayed(this.updateRecyclerRunnable, (long) (duration + 300));
    }

    public void hide(int duration) {
        if (this.translationListener != null) {
            this.translationListener.animateHide(duration);
        }
    }

    public void onShowKeyBoard() {
        this.updateRecyclerRunnable.run();
    }
}
