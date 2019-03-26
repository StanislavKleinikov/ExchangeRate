package stanislav.kleinikov.exchangerate.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.domain.Rate;
import stanislav.kleinikov.exchangerate.domain.RateBank;
import stanislav.kleinikov.exchangerate.ui.main.MainActivity;

public class SettingFragment extends Fragment implements OnStartDragListener {

    private Context mContext;
    private ItemTouchHelper mHelper;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.setting_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RateAdapter adapter = new RateAdapter(RateBank.getInstance().getRates(), this);
        recyclerView.setAdapter(adapter);
        RateItemTouchCallback rateItemTouchCallback = new RateItemTouchCallback(adapter);
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
                Toast.makeText(mContext, "saved", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mHelper.startDrag(viewHolder);
    }

    private class RateAdapter extends RecyclerView.Adapter<RateHolder> {

        private List<Rate> mRates;
        private final OnStartDragListener mDragStartListener;

        private RateAdapter(List<Rate> rates, OnStartDragListener dragStartListener) {
            mRates = rates;
            mDragStartListener = dragStartListener;
        }


        @NonNull
        @Override
        public RateHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new RateHolder(layoutInflater, viewGroup);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull RateHolder rateHolder, int i) {
            Rate rate = mRates.get(i);
            rateHolder.bind(rate);
            rateHolder.imageView.setOnTouchListener((v, event) -> {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(rateHolder);
                    Log.e(MainActivity.DEBUG_TAG, "clicked");

                }
                return false;
            });
        }

        @Override
        public int getItemCount() {
            return mRates.size();
        }

        void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mRates, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mRates, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    private class RateHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mNumberTextView;
        ImageView imageView;
        private Rate mRate;

        RateHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_rate, parent, false));
            mNameTextView = itemView.findViewById(R.id.name);
            mNumberTextView = itemView.findViewById(R.id.number);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(Rate rate) {
            mRate = rate;
            mNameTextView.setText(mRate.getName());
            mNumberTextView.setText(mRate.getNumber());
        }
    }

    private class RateItemTouchCallback extends ItemTouchHelper.Callback {

        private RateAdapter mAdapter;

        private RateItemTouchCallback(RateAdapter adapter) {
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
