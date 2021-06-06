package stanislav.kleinikov.exchangerate.domain;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class DailyExRates {

    private Date mDate;

    private final List<Currency> mCurrencyList;

    public DailyExRates(Date date, List<Currency> currencies) {
        mDate = date;
        mCurrencyList = currencies;
    }

    public List<Currency> getCurrencyList() {
        return mCurrencyList;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
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
