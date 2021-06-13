package stanislav.kleinikov.exchangerate.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import stanislav.kleinikov.exchangerate.domain.GetRatesUseCase
import stanislav.kleinikov.exchangerate.domain.Resource
import stanislav.kleinikov.exchangerate.presentation.AppViewModel
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseListItem

class MainViewModel(
    private val getRatesUseCase: GetRatesUseCase,
    private val ratesMapper: RatesMapper
) : AppViewModel() {

    private val _rates: MutableLiveData<Resource<List<BaseListItem>>> = MutableLiveData()

    val rates: LiveData<Resource<List<BaseListItem>>> = _rates

    init {
        updateRates()
    }

    fun updateRates() {
        getRatesUseCase.getRates()
            .doOnSubscribe { _rates.postValue(Resource.Loading()) }
            .map { ratesMapper.getUiModel(it) }
            .subscribeBy(
                onSuccess = { _rates.postValue(Resource.Success(it)) },
                onError = { _rates.postValue(Resource.Error(it)) }
            ).addTo(compositeDisposable)
    }
}