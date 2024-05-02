package com.example.criconnect.DependencyInjection

import com.example.criconnect.Repositories.TeamRepository
import com.example.criconnect.Repositories.TournamentRepository
import com.example.criconnect.Repositories.UserRepository
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.ViewModels.TournamentViewModel
import com.example.criconnect.ViewModels.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mainModule= module {

    single { TeamRepository(get()) }
    viewModel { TeamViewModel(get()) }

    single { UserRepository(get()) }
    viewModel { UserViewModel(get()) }

    single { TournamentRepository(get()) }
    viewModel { TournamentViewModel(get()) }

}