package ru.ok.android.ui.search.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.plus.PlusShare;
import java.util.Calendar;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.custom.SearchAutocompleteTextView;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.search.adapters.CitiesAutoCompleteAdapter;
import ru.ok.android.ui.search.adapters.CommunityAutoCompleteAdapter;
import ru.ok.android.ui.search.adapters.SearchBaseAdapter.SearchErrorListener;
import ru.ok.android.ui.search.util.AutoCompleteSearchHandler;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GroupInfo;
import ru.ok.model.search.SearchCityResult;
import ru.ok.model.search.SearchResultCommunity;
import ru.ok.model.search.SearchResultCommunity.CommunityType;
import ru.ok.onelog.search.SearchByCommunityEvent;
import ru.ok.onelog.search.SearchByCommunityUsageFactory;

public class SearchByCommunityFragment extends BaseFragment implements SearchErrorListener {
    private CitiesAutoCompleteAdapter citiesAutoCompleteAdapter;
    private AutoCompleteTextViewHolder citiesViewHolder;
    private CommunityAutoCompleteAdapter communityAutoCompleteAdapter;
    private AutoCompleteTextViewHolder communityViewHolder;
    private SearchCityResult currentCity;
    private SearchResultCommunity currentCommunity;
    private View membersCountContainer;
    private TextView membersCountTextView;
    private View membersNextIcon;
    private YearsFieldsHolder studyYearsHolder;
    private ViewGroup view;

    /* renamed from: ru.ok.android.ui.search.fragment.SearchByCommunityFragment.1 */
    class C11901 implements OnItemClickListener {
        C11901() {
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SearchByCommunityFragment.this.currentCity = (SearchCityResult) parent.getItemAtPosition(position);
            SearchByCommunityFragment.this.currentCommunity = null;
            SearchByCommunityFragment.this.updateFields();
            SearchByCommunityFragment.this.communityAutoCompleteAdapter.setCity(SearchByCommunityFragment.this.currentCity);
            SearchByCommunityFragment.this.communityViewHolder.container.setVisibility(0);
            SearchByCommunityFragment.this.checkUsersForCommunity(null);
        }
    }

    /* renamed from: ru.ok.android.ui.search.fragment.SearchByCommunityFragment.2 */
    class C11912 implements OnItemClickListener {
        C11912() {
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SearchByCommunityFragment.this.currentCommunity = (SearchResultCommunity) parent.getItemAtPosition(position);
            SearchByCommunityFragment.this.updateFields();
            SearchByCommunityFragment.this.studyYearsHolder.container.setVisibility(0);
            SearchByCommunityFragment.this.checkUsersForCommunity(SearchByCommunityEvent.select_community);
        }
    }

    /* renamed from: ru.ok.android.ui.search.fragment.SearchByCommunityFragment.3 */
    class C11923 implements OnItemSelectedListener {
        C11923() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            SearchByCommunityFragment.this.checkUsersForCommunity(SearchByCommunityEvent.select_correct_years);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: ru.ok.android.ui.search.fragment.SearchByCommunityFragment.4 */
    class C11934 implements OnClickListener {
        final /* synthetic */ GroupInfo val$groupInfo;
        final /* synthetic */ String val$membersCountText;

        C11934(GroupInfo groupInfo, String str) {
            this.val$groupInfo = groupInfo;
            this.val$membersCountText = str;
        }

        public void onClick(View v) {
            SearchByCommunityFragment.this.joinCommunity();
            NavigationHelper.showCommunityUsersFragment(SearchByCommunityFragment.this.getActivity(), this.val$groupInfo.getId(), SearchByCommunityFragment.this.studyYearsHolder.getStartYear(), SearchByCommunityFragment.this.studyYearsHolder.getEndYear(), this.val$membersCountText);
        }
    }

    protected static class AutoCompleteTextViewHolder {
        private final ViewGroup autoCompleteContainer;
        public final ViewGroup container;
        public final SearchAutocompleteTextView searchAutoCompleteTextView;

        /* renamed from: ru.ok.android.ui.search.fragment.SearchByCommunityFragment.AutoCompleteTextViewHolder.1 */
        class C11941 implements Runnable {
            C11941() {
            }

            public void run() {
                AutoCompleteTextViewHolder.this.searchAutoCompleteTextView.dismissDropDown();
            }
        }

        public void measureProgressBar() {
            ProgressBar progressBar = (ProgressBar) this.container.findViewById(2131625306);
            this.container.measure(0, 0);
            int size = this.searchAutoCompleteTextView.getMeasuredHeight() / 2;
            LayoutParams layoutParams = new LayoutParams(size, size);
            layoutParams.gravity = 21;
            progressBar.setLayoutParams(layoutParams);
            this.searchAutoCompleteTextView.setProgressBar(progressBar);
        }

        public void updateValue(String value) {
            this.searchAutoCompleteTextView.setText(value);
            this.searchAutoCompleteTextView.post(new C11941());
        }

        public AutoCompleteTextViewHolder(View container, View autoCompleteContainer) {
            this.container = (ViewGroup) container;
            this.autoCompleteContainer = (ViewGroup) autoCompleteContainer;
            this.searchAutoCompleteTextView = (SearchAutocompleteTextView) this.autoCompleteContainer.findViewById(2131625305);
            this.searchAutoCompleteTextView.setDropDownBackgroundResource(2131493208);
            measureProgressBar();
        }
    }

    protected static class YearsFieldsHolder {
        public final ViewGroup container;
        public final Spinner endYearSpinner;
        public final Spinner startYearSpinner;

        public YearsFieldsHolder(View container) {
            this.container = (ViewGroup) container;
            this.startYearSpinner = (Spinner) container.findViewById(2131625307);
            this.endYearSpinner = (Spinner) container.findViewById(2131625308);
        }

        public int getStartYear() {
            return Integer.valueOf(this.startYearSpinner.getSelectedItem().toString()).intValue();
        }

        public int getEndYear() {
            return Integer.valueOf(this.endYearSpinner.getSelectedItem().toString()).intValue();
        }
    }

    protected CharSequence getTitle() {
        String title = getArguments().getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
        return title == null ? super.getTitle() : title;
    }

    protected int getLayoutId() {
        return 2130903423;
    }

    public int getType() {
        Bundle args = getArguments();
        if (args == null) {
            return 0;
        }
        return args.getInt("type", 0);
    }

    protected int getTypeStringId() {
        switch (getType()) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 2131165616;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 2131165618;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return 2131165622;
            default:
                return 2131165620;
        }
    }

    public CommunityType getCommunityType() {
        switch (getType()) {
            case RECEIVED_VALUE:
                return CommunityType.SCHOOL;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return CommunityType.COLLEAGUE;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return CommunityType.UNIVERSITY;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return CommunityType.WORKPLACE;
            default:
                return CommunityType.UNKNOWN;
        }
    }

    public void prepareCityField() {
        this.citiesAutoCompleteAdapter = new CitiesAutoCompleteAdapter(getContext());
        this.citiesAutoCompleteAdapter.setSearchErrorListener(this);
        SearchAutocompleteTextView citiesAutoCompleteTextView = this.citiesViewHolder.searchAutoCompleteTextView;
        citiesAutoCompleteTextView.setAdapter(this.citiesAutoCompleteAdapter);
        citiesAutoCompleteTextView.setSearchHandler(new AutoCompleteSearchHandler(this.citiesAutoCompleteAdapter, citiesAutoCompleteTextView.getProgressBar()));
        citiesAutoCompleteTextView.setOnItemClickListener(new C11901());
    }

    public void prepareCommunityField() {
        ((TextView) this.view.findViewById(2131625298)).setText(LocalizationManager.getString(getContext(), getTypeStringId()));
        SearchAutocompleteTextView communityAutoCompleteTextView = this.communityViewHolder.searchAutoCompleteTextView;
        this.communityAutoCompleteAdapter = new CommunityAutoCompleteAdapter(getContext(), getCommunityType());
        this.communityAutoCompleteAdapter.setSearchErrorListener(this);
        communityAutoCompleteTextView.setAdapter(this.communityAutoCompleteAdapter);
        communityAutoCompleteTextView.setSearchHandler(new AutoCompleteSearchHandler(this.communityAutoCompleteAdapter, communityAutoCompleteTextView.getProgressBar()));
        communityAutoCompleteTextView.setOnItemClickListener(new C11912());
    }

    public void checkUsersForCommunity(SearchByCommunityEvent logEvent) {
        if (this.currentCommunity == null || this.studyYearsHolder.getStartYear() > this.studyYearsHolder.getEndYear()) {
            this.membersCountContainer.setVisibility(8);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("start_year", this.studyYearsHolder.getStartYear());
        bundle.putInt("end_year", this.studyYearsHolder.getEndYear());
        bundle.putString("GROUP_ID", this.currentCommunity.getGroupInfo().getId());
        bundle.putInt("total_count", 1);
        GlobalBus.send(2131623955, new BusEvent(bundle));
        if (logEvent != null) {
            OneLog.log(SearchByCommunityUsageFactory.get(logEvent));
        }
    }

    public void prepareYearsFields() {
        int END_YEAR = Calendar.getInstance().get(1);
        ((TextView) this.view.findViewById(2131625301)).setText(LocalizationManager.getString(getContext(), getType() == 3 ? 2131166877 : 2131166657));
        this.studyYearsHolder.startYearSpinner.setAdapter(createSpinnerAdapter(1900, END_YEAR));
        this.studyYearsHolder.endYearSpinner.setAdapter(createSpinnerAdapter(1900, END_YEAR));
        int endPosition = END_YEAR - 1900;
        this.studyYearsHolder.endYearSpinner.setSelection(endPosition);
        this.studyYearsHolder.startYearSpinner.setSelection(endPosition - 10);
        OnItemSelectedListener onItemSelectedListener = new C11923();
        this.studyYearsHolder.startYearSpinner.setOnItemSelectedListener(onItemSelectedListener);
        this.studyYearsHolder.endYearSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    private ArrayAdapter<String> createSpinnerAdapter(int startYear, int endYear) {
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), 2130903442);
        for (int i = startYear; i <= endYear; i++) {
            adapter.add(String.valueOf(i));
        }
        adapter.setDropDownViewResource(2130903441);
        return adapter;
    }

    public CharSequence getPageTitle() {
        return LocalizationManager.getString(getContext(), getTypeStringId());
    }

    private void updateFields() {
        if (this.currentCity != null) {
            this.citiesViewHolder.updateValue(this.currentCity.getCitySummary());
            this.communityViewHolder.container.setVisibility(0);
            if (this.currentCommunity != null) {
                this.communityViewHolder.updateValue(this.currentCommunity.getText());
                this.studyYearsHolder.container.setVisibility(0);
                return;
            }
            this.communityViewHolder.searchAutoCompleteTextView.setText("");
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = (ViewGroup) LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        this.membersCountContainer = this.view.findViewById(2131624664);
        this.membersCountTextView = (TextView) this.view.findViewById(2131625304);
        this.membersNextIcon = this.view.findViewById(2131625303);
        this.citiesViewHolder = new AutoCompleteTextViewHolder(this.view.findViewById(2131625294), this.view.findViewById(2131625296));
        this.communityViewHolder = new AutoCompleteTextViewHolder(this.view.findViewById(2131625297), this.view.findViewById(2131625299));
        this.studyYearsHolder = new YearsFieldsHolder(this.view.findViewById(2131625300));
        prepareCityField();
        prepareCommunityField();
        prepareYearsFields();
        return this.view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFields();
    }

    private void showPeopleFoundButton(GroupInfo groupInfo, int foundCount) {
        this.membersCountContainer.setVisibility(0);
        if (foundCount == 0) {
            this.membersCountContainer.setVisibility(0);
            this.membersCountContainer.setOnClickListener(null);
            this.membersNextIcon.setVisibility(4);
            this.membersCountTextView.setText(LocalizationManager.getString(getContext(), 2131166336));
            return;
        }
        int countOfPeopleId = StringUtils.plural((long) foundCount, 2131165883, 2131165881, 2131165882);
        String membersCountText = LocalizationManager.getString(getContext(), countOfPeopleId, Integer.valueOf(foundCount));
        this.membersCountTextView.setText(membersCountText);
        this.membersNextIcon.setVisibility(0);
        this.membersCountContainer.setOnClickListener(new C11934(groupInfo, membersCountText));
    }

    public void joinCommunity() {
        if (this.currentCommunity != null || this.studyYearsHolder != null) {
            Bundle bundle = new Bundle();
            bundle.putString("GROUP_ID", this.currentCommunity.getGroupInfo().getId());
            int startYear = this.studyYearsHolder.getStartYear();
            int endYear = this.studyYearsHolder.getEndYear();
            bundle.putInt("COMMUNITY_START_YEAR", startYear);
            bundle.putInt("COMMUNITY_NED_YEAR", endYear);
            GlobalBus.send(2131623990, new BusEvent(bundle));
        }
    }

    public void onSearchError(ErrorType errorType) {
        Toast.makeText(getContext(), errorType == ErrorType.NO_INTERNET ? 2131165984 : 2131166539, 1).show();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Subscribe(on = 2131623946, to = 2131624135)
    public void receiveCommunityParticipants(BusEvent event) {
        Bundle result = event.bundleOutput;
        if (event.resultCode != -2) {
            String groupId = event.bundleInput.getString("GROUP_ID");
            if (this.currentCommunity != null && this.currentCommunity.getGroupInfo().getId().equals(groupId)) {
                showPeopleFoundButton(this.currentCommunity.getGroupInfo(), result.getInt("total_count"));
                return;
            }
            return;
        }
        onSearchError(ErrorType.from(result));
    }
}
