package ru.ok.android.ui.polls.fragment;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.gridlayout.C0028R;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
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
import android.widget.TextView;
import java.util.List;
import ru.ok.android.ui.polls.choice.SingleChoiceRecycleViewAdapter;
import ru.ok.android.ui.polls.choice.SingleChoiceRecycleViewAdapter.ViewHolderFinder;
import ru.ok.android.ui.toolbar.ViewMarginTranslationListener;
import ru.ok.android.ui.utils.AdapterItemViewTypeMaxValueProvider;
import ru.ok.android.ui.utils.RecyclerMergeAdapter;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.ListPollQuestion;
import ru.ok.model.poll.ListPollQuestion.ListPollItem;

public class ListStepAppPollFragment extends Fragment implements FragmentWithAnswer {
    private ListStepAdapter adapter;
    private View bottom;
    private LinearLayoutManager linearManager;
    private ListStepInteractionListener listener;
    private RecyclerMergeAdapter mergeAdapter;
    private View otherLayout;
    private EditText otherText;
    private ListPollQuestion question;
    private TextWatcher textWatcher;
    private ViewMarginTranslationListener translationListener;
    private Runnable updateRecyclerPosition;

    public interface ListStepInteractionListener {
        void onAnswer(String str);
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.ListStepAppPollFragment.1 */
    class C11321 extends ItemDecoration {
        final /* synthetic */ int val$dividerSize;
        final /* synthetic */ Paint val$paint;

        C11321(int i, Paint paint) {
            this.val$dividerSize = i;
            this.val$paint = paint;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom += this.val$dividerSize;
        }

        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            super.onDrawOver(c, parent, state);
            LayoutManager manager = parent.getLayoutManager();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                c.drawLine((float) manager.getDecoratedLeft(child), ((float) manager.getDecoratedBottom(child)) - (((float) this.val$dividerSize) / 2.0f), (float) manager.getDecoratedRight(child), ((float) manager.getDecoratedBottom(child)) - (((float) this.val$dividerSize) / 2.0f), this.val$paint);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.ListStepAppPollFragment.2 */
    class C11332 implements Runnable {
        C11332() {
        }

        public void run() {
            int lastVisiblePosition = ListStepAppPollFragment.this.linearManager.findLastCompletelyVisibleItemPosition();
            int checkedPosition = ListStepAppPollFragment.this.mergeAdapter.getRecyclerPositionByAdapterPosition(ListStepAppPollFragment.this.adapter, ListStepAppPollFragment.this.adapter.getCheckedPosition());
            if (lastVisiblePosition < checkedPosition) {
                ListStepAppPollFragment.this.linearManager.scrollToPositionWithOffset(checkedPosition, 20);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.ListStepAppPollFragment.3 */
    class C11343 implements TextWatcher {
        C11343() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (ListStepAppPollFragment.this.otherText.getText().toString().isEmpty()) {
                ListStepAppPollFragment.this.listener.onAnswer(null);
                return;
            }
            ListPollItem item = ListStepAppPollFragment.this.adapter.getCheckedItem();
            if (item != null) {
                ListStepAppPollFragment.this.listener.onAnswer(item.getId());
            }
        }
    }

    private static class HeaderListAdapter extends Adapter<HeaderViewHolder> implements AdapterItemViewTypeMaxValueProvider {
        private final HeaderViewHolder headerHolder;

        public HeaderListAdapter(View headerView) {
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

    class ListStepAdapter extends SingleChoiceRecycleViewAdapter<ListViewHolder> implements AdapterItemViewTypeMaxValueProvider {
        private final List<ListPollItem> items;

        /* renamed from: ru.ok.android.ui.polls.fragment.ListStepAppPollFragment.ListStepAdapter.1 */
        class C11351 implements ViewHolderFinder {
            final /* synthetic */ ListStepAppPollFragment val$this$0;

            C11351(ListStepAppPollFragment listStepAppPollFragment) {
                this.val$this$0 = listStepAppPollFragment;
            }

            @Nullable
            public ViewHolder findViewHolderForPosition(RecyclerView recyclerView, int adapterPosition) {
                return recyclerView.findViewHolderForPosition(this.val$this$0.mergeAdapter.getRecyclerPositionByAdapterPosition(this.val$this$0.adapter, adapterPosition));
            }
        }

        public ListStepAdapter(RecyclerView recyclerView, List<ListPollItem> items) {
            super(recyclerView, new C11351(ListStepAppPollFragment.this));
            this.items = items;
        }

        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(2130903100, parent, false));
        }

        public void onBindViewHolder(ListViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.bind(position);
        }

        public void updateEditText(ListPollItem listPollItem, boolean isChecked, boolean clearEditText) {
            if (listPollItem.isOther()) {
                if (isChecked) {
                    ListStepAppPollFragment.this.otherLayout.setVisibility(0);
                    KeyBoardUtils.showKeyBoard(ListStepAppPollFragment.this.getActivity(), ListStepAppPollFragment.this.otherText);
                    if (clearEditText) {
                        ListStepAppPollFragment.this.otherText.setText("");
                        return;
                    }
                    return;
                }
                ListStepAppPollFragment.this.otherLayout.setVisibility(8);
                KeyBoardUtils.hideKeyBoard(ListStepAppPollFragment.this.getActivity());
            } else if (isChecked) {
                ListStepAppPollFragment.this.otherLayout.setVisibility(8);
                KeyBoardUtils.hideKeyBoard(ListStepAppPollFragment.this.getActivity());
            }
        }

        public void onRestoreInstanceState(Bundle bundle) {
            super.onRestoreInstanceState(bundle);
            if (getCheckedPosition() > -1) {
                updateEditText((ListPollItem) this.items.get(getCheckedPosition()), true, false);
            }
        }

        public int getItemCount() {
            return this.items.size();
        }

        public int getItemViewType(int position) {
            return 1;
        }

        public int getItemViewTypeMaxValue() {
            return 1;
        }

        @Nullable
        public ListPollItem getCheckedItem() {
            int position = getCheckedPosition();
            if (position >= 0) {
                return (ListPollItem) this.items.get(position);
            }
            return null;
        }
    }

    public class ListViewHolder extends ViewHolder implements Checkable {
        private boolean checked;
        private final View mark;
        private final TextView text;

        /* renamed from: ru.ok.android.ui.polls.fragment.ListStepAppPollFragment.ListViewHolder.1 */
        class C11361 implements OnClickListener {
            final /* synthetic */ ListStepAppPollFragment val$this$0;

            C11361(ListStepAppPollFragment listStepAppPollFragment) {
                this.val$this$0 = listStepAppPollFragment;
            }

            public void onClick(View v) {
                ListStepAppPollFragment.this.adapter.onChecked(ListViewHolder.this.getMergeAdapterPosition());
                ListPollItem listPollItem = ListStepAppPollFragment.this.adapter.getCheckedItem();
                if (listPollItem != null) {
                    String otherId = ListStepAppPollFragment.this.otherText.getText().toString().isEmpty() ? null : listPollItem.getId();
                    ListStepInteractionListener access$400 = ListStepAppPollFragment.this.listener;
                    if (!listPollItem.isOther()) {
                        otherId = listPollItem.getId();
                    }
                    access$400.onAnswer(otherId);
                }
            }
        }

        public ListViewHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(2131624615);
            this.mark = itemView.findViewById(2131624616);
            itemView.setOnClickListener(new C11361(ListStepAppPollFragment.this));
        }

        private int getMergeAdapterPosition() {
            return ListStepAppPollFragment.this.mergeAdapter.getAdapterPositionByRecyclerPosition(ListStepAppPollFragment.this.adapter, getPosition());
        }

        public void setChecked(boolean checked) {
            int position = getMergeAdapterPosition();
            if (position >= 0) {
                bind((ListPollItem) ListStepAppPollFragment.this.adapter.items.get(position), checked);
                ListStepAppPollFragment.this.adapter.updateEditText((ListPollItem) ListStepAppPollFragment.this.adapter.items.get(position), checked, false);
                this.checked = checked;
            }
        }

        public boolean isChecked() {
            return this.checked;
        }

        public void toggle() {
        }

        public void bind(ListPollItem item, boolean isChecked) {
            if (isChecked) {
                this.text.setTextColor(ListStepAppPollFragment.this.getResources().getColor(2131493225));
                this.mark.setVisibility(0);
                if (item.isOther()) {
                    ListStepAppPollFragment.this.otherLayout.setVisibility(0);
                }
            } else {
                this.text.setTextColor(ListStepAppPollFragment.this.getResources().getColor(2131493226));
                this.mark.setVisibility(8);
                if (item.isOther()) {
                    ListStepAppPollFragment.this.otherLayout.setVisibility(8);
                }
            }
            this.checked = isChecked;
        }

        public void bind(int position) {
            ListPollItem listPollItem = (ListPollItem) ListStepAppPollFragment.this.adapter.items.get(position);
            this.text.setText(listPollItem.getTitle());
            bind(listPollItem, ListStepAppPollFragment.this.adapter.isChecked(position));
        }
    }

    public static ListStepAppPollFragment newInstance(ListPollQuestion question) {
        ListStepAppPollFragment fragment = new ListStepAppPollFragment();
        Bundle args = new Bundle();
        args.putParcelable("arg_list_question", question);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.question = (ListPollQuestion) getArguments().getParcelable("arg_list_question");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(2130903190, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(2131624822);
        this.otherLayout = v.findViewById(2131624823);
        this.otherText = (EditText) v.findViewById(2131624825);
        this.bottom = v.findViewById(C0028R.id.bottom);
        TextView otherTitle = (TextView) v.findViewById(2131624824);
        int dividerSize = getResources().getDimensionPixelSize(2131230873);
        int dividerColor = getResources().getColor(2131492899);
        Paint paint = new Paint();
        this.linearManager = new LinearLayoutManager(getActivity());
        paint.setColor(dividerColor);
        paint.setStrokeWidth((float) dividerSize);
        recyclerView.setLayoutManager(this.linearManager);
        recyclerView.addItemDecoration(new C11321(dividerSize, paint));
        TextView questionText = (TextView) inflater.inflate(2130903099, recyclerView, false);
        this.mergeAdapter = new RecyclerMergeAdapter();
        this.adapter = new ListStepAdapter(recyclerView, this.question.getItems());
        this.mergeAdapter.addAdapter(new HeaderListAdapter(questionText));
        this.mergeAdapter.addAdapter(this.adapter);
        String text = ((ListPollItem) this.question.getItems().get(this.question.getItems().size() - 1)).getOtherText();
        if (text != null) {
            otherTitle.setText(text);
        }
        questionText.setText(this.question.getTitle());
        recyclerView.setAdapter(this.mergeAdapter);
        this.updateRecyclerPosition = new C11332();
        this.textWatcher = new C11343();
        return v;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (ListStepInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public void onStart() {
        super.onStart();
        this.otherText.addTextChangedListener(this.textWatcher);
    }

    public void onStop() {
        super.onStop();
        this.otherText.removeTextChangedListener(this.textWatcher);
    }

    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.adapter.onSaveInstanceState(outState);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.adapter.onRestoreInstanceState(savedInstanceState);
    }

    public AppPollAnswer getAnswer() {
        ListPollItem pollItem = this.adapter.getCheckedItem();
        String realPosition = Integer.toString(this.adapter.getCheckedPosition() + 1);
        if (pollItem != null && pollItem.isOther()) {
            return new AppPollAnswer(pollItem.getId(), this.otherText.getText().toString(), realPosition, this.question.getStep());
        }
        if (pollItem != null) {
            return new AppPollAnswer(pollItem.getId(), realPosition, this.question.getStep());
        }
        return null;
    }

    public void show(int size, int duration) {
        if (this.translationListener == null) {
            this.translationListener = new ViewMarginTranslationListener(this.bottom);
            this.translationListener.setTranslationSize((float) size);
            this.translationListener.setTranslation(0.0f);
        }
        this.translationListener.animateShow(duration);
        this.bottom.postDelayed(this.updateRecyclerPosition, (long) duration);
    }

    public void hide(int duration) {
        if (this.translationListener != null) {
            this.translationListener.animateHide(duration);
        }
    }

    public void onShowKeyBoard() {
        this.updateRecyclerPosition.run();
    }
}
