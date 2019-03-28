package stanislav.kleinikov.exchangerate.domain;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Root(strict = false)
public class DailyExRates {

    @Attribute(name = "Date", required = false)
    private String mDate;

    @ElementList(inline = true, entry = "Currency", required = false)
    private List<Currency> mCurrencyList;

    private DailyExRates() {
        mCurrencyList = new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyExRates)) return false;
        DailyExRates that = (DailyExRates) o;
        return Objects.equals(mDate, that.mDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDate);
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
