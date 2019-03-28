package stanislav.kleinikov.exchangerate.domain;


import java.util.List;

public class CurrencyBank {

    private List<DailyExRates> mDailyExRatesList;

    private CurrencyBank() {

    }

    private static final class CurrencyBankHolder {
        private static final CurrencyBank INSTANCE = new CurrencyBank();
    }

    public static CurrencyBank getInstance() {
        return CurrencyBankHolder.INSTANCE;
    }

    public List<DailyExRates> getDailyExRatesList() {
        return mDailyExRatesList;
    }

    public void setDailyExRatesList(List<DailyExRates> dailyExRatesList) {
        mDailyExRatesList = dailyExRatesList;
    }
}
