package stanislav.kleinikov.exchangerate.presentation.settings;

import androidx.lifecycle.ViewModel;

import java.util.List;

import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.CurrencyBank;

public class SettingViewModel extends ViewModel {

    public List<Currency> currencyList = CurrencyBank.getInstance().getCurrencyList();

}