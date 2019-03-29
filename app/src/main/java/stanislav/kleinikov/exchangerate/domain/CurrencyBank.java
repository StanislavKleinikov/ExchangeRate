package stanislav.kleinikov.exchangerate.domain;


import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyBank {

    private List<Currency> mCurrencyList = new ArrayList<>();
    private SparseArray<Map<String, BigDecimal>> mRates;

    private CurrencyBank() {

    }

    private static final class CurrencyBankHolder {
        private static final CurrencyBank INSTANCE = new CurrencyBank();
    }

    public static CurrencyBank getInstance() {
        return CurrencyBankHolder.INSTANCE;
    }


    public List<Currency> getCurrencyList() {
        return mCurrencyList;
    }

    public void setCurrencyList(List<Currency> currencyList) {
        mCurrencyList = currencyList;
    }

    public void setRates(SparseArray<Map<String, BigDecimal>> rates) {
        mRates = rates;
    }

    public Map<String, BigDecimal> getRatesById(int currencyId) {
        return mRates.get(currencyId);
    }

}
