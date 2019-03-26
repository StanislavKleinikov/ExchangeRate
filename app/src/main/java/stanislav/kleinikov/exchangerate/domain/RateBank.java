package stanislav.kleinikov.exchangerate.domain;

import java.util.ArrayList;
import java.util.List;

public class RateBank {

    private List<Rate> mRates;

    private RateBank() {
        mRates = new ArrayList<>();
        mRates.add(new Rate("USD", "1 доллар США"));
        mRates.add(new Rate("RUB", "100 российских  рублей"));
        mRates.add(new Rate("USD", "1 евро"));
    }

    private static class RateBankHolder {
        private static final RateBank INSTANCE = new RateBank();
    }

    public static RateBank getInstance() {
        return RateBankHolder.INSTANCE;
    }

    public List<Rate> getRates() {
        return mRates;
    }
}
