@file:Suppress("RemoveExplicitTypeArguments")

package stanislav.kleinikov.exchangerate.di

import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import stanislav.kleinikov.exchangerate.data.PreferenceRepositoryImpl
import stanislav.kleinikov.exchangerate.data.RatesRepositoryImpl
import stanislav.kleinikov.exchangerate.domain.GetRatesUseCaseImpl
import stanislav.kleinikov.exchangerate.domain.PreferenceRepository
import stanislav.kleinikov.exchangerate.domain.RatesRepository
import stanislav.kleinikov.exchangerate.presentation.main.MainViewModel
import stanislav.kleinikov.exchangerate.presentation.main.RatesMapperImpl
import stanislav.kleinikov.exchangerate.presentation.settings.SettingsMapper
import stanislav.kleinikov.exchangerate.presentation.settings.SettingsMapperImpl
import stanislav.kleinikov.exchangerate.presentation.settings.SettingsViewModel

val appModule = module {

    viewModel<MainViewModel> {
        MainViewModel(
            getRatesUseCase = GetRatesUseCaseImpl(
                ratesRepository = get<RatesRepository>(),
                preferenceRepository = get<PreferenceRepository>(),
                subscribeScheduler = Schedulers.io(),
                observeScheduler = Schedulers.computation()
            ),
            ratesMapper = RatesMapperImpl(),
        )
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(
            preferenceRepository = get<PreferenceRepository>(),
            settingsMapper = SettingsMapperImpl()
        )
    }

    single<RatesRepository> { RatesRepositoryImpl() }
    single<PreferenceRepository> { PreferenceRepositoryImpl(androidContext()) }
}