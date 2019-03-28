package stanislav.kleinikov.exchangerate.domain;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.ArrayList;
import java.util.List;

@Root(strict = false)
public class DailyExRates {

    @Attribute(name = "Date")
    private String mDate;

    @ElementList(inline = true, entry = "Currency")
    private List<Currency> mCurrencyList;

    private DailyExRates() {
        mCurrencyList = new ArrayList<>();
    }

    private static class DailyExRatesHolder {
        private static final DailyExRates INSTANCE = new DailyExRates();
    }

    public static DailyExRates getInstance() {
        return DailyExRatesHolder.INSTANCE;
    }

    public List<Currency> getCurrencyList() {
        return mCurrencyList;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "DailyExRates{" +
                "mDate='" + mDate + '\'' +
                ", mCurrencyList=" + mCurrencyList +
                '}';
    }
}
