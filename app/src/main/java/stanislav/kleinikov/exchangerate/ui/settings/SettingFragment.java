package stanislav.kleinikov.exchangerate.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.CurrencyBank;

public class SettingFragment extends Fragment implements OnStartDragListener {

    private Context mContext;
    private ItemTouchHelper mHelper;
    private SharedPreferences mPreferences;
    private CurrencyAdapter mAdapter;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.setting_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CurrencyAdapter(CurrencyBank.getInstance()
                .getDailyExRatesList().get(0).getCurrencyList(), this);
        recyclerView.setAdapter(mAdapter);
        CurrencyItemTouchCallback rateItemTouchCallback = new CurrencyItemTouchCallback(mAdapter);
        mHelper = new ItemTouchHelper(rateItemTouchCallback);
        mHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.save:
                Toast.makeText(mContext, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mHelper.startDrag(viewHolder);
    }

    private class CurrencyAdapter extends RecyclerView.Adapter<CurrencyHolder> {

        private List<Currency> mCurrencyList;
        private final OnStartDragListener mDragStartListener;

        private CurrencyAdapter(List<Currency> CurrencyList, OnStartDragListener dragStartListener) {
            mCurrencyList = CurrencyList;
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

    private class CurrencyHolder extends RecyclerView.ViewHolder {
        private TextView mCharCodeTV;
        private TextView mScaleTV;
        private ImageView imageView;
        private Switch visibilitySW;
        private Currency mCurrency;

        CurrencyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_pref, parent, false));
            mCharCodeTV = itemView.findViewById(R.id.char_code_tv);
            mScaleTV = itemView.findViewById(R.id.scale_tv);
            imageView = itemView.findViewById(R.id.anchor_iv);
            visibilitySW = itemView.findViewById(R.id.visibility_sw);
        }

        void bind(Currency currency) {
            mCurrency = currency;
            visibilitySW.setChecked(mPreferences.getBoolean(mCurrency.getCharCode(), false));
            mCharCodeTV.setText(mCurrency.getName());
            mScaleTV.setText(String.format(getString(R.string.format_scale),
                    currency.getScale(), currency.getName()));

        }
    }

    private class CurrencyItemTouchCallback extends ItemTouchHelper.Callback {

        private CurrencyAdapter mAdapter;

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
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
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
