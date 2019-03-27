package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.domain.DailyExRates;
import stanislav.kleinikov.exchangerate.domain.NetworkService;
import stanislav.kleinikov.exchangerate.ui.settings.SettingActivity;

import static stanislav.kleinikov.exchangerate.domain.NbrbApi.PATTERN_DATE;

public class MainFragment extends Fragment {

    private Context mContext;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        NetworkService.getInstance()
                .getNbrbApi()
                .loadDailyRates(new SimpleDateFormat(PATTERN_DATE).format(new Date()))
                .enqueue(new Callback<DailyExRates>() {
                    @Override
                    public void onResponse(@NonNull Call<DailyExRates> call, @NonNull Response<DailyExRates> response) {
                        Log.e(MainActivity.DEBUG_TAG, response.body().getDate());
                    }

                    @Override
                    public void onFailure(@NonNull Call<DailyExRates> call, @NonNull Throwable t) {
                        Log.e(MainActivity.DEBUG_TAG, "error");
                        t.printStackTrace();
                    }
                });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.settings:
                Intent intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
