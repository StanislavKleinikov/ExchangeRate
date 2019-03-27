package stanislav.kleinikov.exchangerate.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root
public class DailyExRates {
    @Attribute(name = "Date")
    private String mDate;
    @ElementList(inline = true)
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
}
