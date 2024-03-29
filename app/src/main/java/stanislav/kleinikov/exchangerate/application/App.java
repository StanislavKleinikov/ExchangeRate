package stanislav.kleinikov.exchangerate.application;

import android.app.Application;
import android.preference.PreferenceManager;

import stanislav.kleinikov.exchangerate.R;

public class App extends Application {

    public static final String PREFERENCES_CURRENCY_ORDER = "currency_order_pref";

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.currency_pref, false);
    }
}
