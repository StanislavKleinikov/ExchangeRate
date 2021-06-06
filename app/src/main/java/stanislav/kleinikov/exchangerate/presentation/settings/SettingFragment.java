package stanislav.kleinikov.exchangerate.presentation.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.application.App;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.CurrencyBank;

public class SettingFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper mHelper;
    private SharedPreferences mPreferences;
    private SparseBooleanArray mVisibilityList;
    private List<Currency> mCurrencyList;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrencyList = new ViewModelProvider(this).get(SettingViewModel.class).currencyList;

        mVisibilityList = new SparseBooleanArray(mCurrencyList.size());
        for (Currency currency : mCurrencyList) {
            mVisibilityList.put(currency.getId(), mPreferences.getBoolean(currency.getCharCode(), false));
        }

        RecyclerView recyclerView = view.findViewById(R.id.settings_recycler_view);
        CurrencyAdapter mAdapter = new CurrencyAdapter(this);
        CurrencyItemTouchCallback rateItemTouchCallback = new CurrencyItemTouchCallback(mAdapter);
        mHelper = new ItemTouchHelper(rateItemTouchCallback);
        mHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save) {
            SharedPreferences.Editor visibilityEditor = mPreferences.edit();
            SharedPreferences orderPreferences = requireContext().getSharedPreferences(App.PREFERENCES_CURRENCY_ORDER, Context.MODE_PRIVATE);
            SharedPreferences.Editor orderEditor = orderPreferences.edit();
            for (int i = 0; i < mCurrencyList.size(); i++) {
                Currency currency = mCurrencyList.get(i);
                visibilityEditor.putBoolean(currency.getCharCode(), mVisibilityList.get(currency.getId()));
                orderEditor.putInt(currency.getCharCode(), i);
            }

            visibilityEditor.apply();
            orderEditor.apply();
            CurrencyBank.getInstance().setCurrencyList(mCurrencyList);
            Toast.makeText(requireContext(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
            ((SettingActivity) requireContext()).setResult(Activity.RESULT_OK);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mHelper.startDrag(viewHolder);
    }

    private class CurrencyAdapter extends RecyclerView.Adapter<CurrencyHolder> {
        private final OnStartDragListener mDragStartListener;

        private CurrencyAdapter(OnStartDragListener dragStartListener) {
            mDragStartListener = dragStartListener;
        }

        @NonNull
        @Override
        public CurrencyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CurrencyHolder(layoutInflater, viewGroup);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull CurrencyHolder currencyHolder, int i) {
            Currency currency = mCurrencyList.get(i);
            currencyHolder.bind(currency);
            currencyHolder.imageView.setOnTouchListener((v, event) -> {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(currencyHolder);
                }
                return false;
            });
        }

        @Override
        public int getItemCount() {
            return mCurrencyList.size();
        }

        void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mCurrencyList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mCurrencyList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    private class CurrencyHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private final TextView mCharCodeTV;
        private final TextView mScaleTV;
        private final ImageView imageView;
        private final SwitchCompat visibilitySW;
        private Currency mCurrency;

        CurrencyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_pref, parent, false));
            mCharCodeTV = itemView.findViewById(R.id.char_code_tv);
            mScaleTV = itemView.findViewById(R.id.scale_tv);
            imageView = itemView.findViewById(R.id.anchor_iv);
            visibilitySW = itemView.findViewById(R.id.visibility_sw);
            visibilitySW.setOnCheckedChangeListener(this);
        }

        void bind(Currency currency) {
            mCurrency = currency;
            visibilitySW.setChecked(mVisibilityList.get(mCurrency.getId()));
            mCharCodeTV.setText(mCurrency.getCharCode());
            mScaleTV.setText(String.format(getString(R.string.format_scale),
                    currency.getScale(), currency.getName()));

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mVisibilityList.put(mCurrency.getId(), isChecked);
        }
    }

    private class CurrencyItemTouchCallback extends ItemTouchHelper.Callback {

        private final CurrencyAdapter mAdapter;

        private CurrencyItemTouchCallback(CurrencyAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView
                                            recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
                viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            mAdapter.onItemMove(viewHolder.getBindingAdapterPosition(), viewHolder1.getBindingAdapterPosition());
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }


        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        }
    }

}
