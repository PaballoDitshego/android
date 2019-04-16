package za.co.moxomo.viewmodel;


import com.google.gson.Gson;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import za.co.moxomo.repository.Repository;


public class ViewModelFactory implements ViewModelProvider.Factory {


    private Repository repository;
    private Gson gson;


    @Inject
    public ViewModelFactory(Repository repository, Gson gson) {
        this.repository =repository;
        this.gson=gson;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(repository);
        }
        if (modelClass.isAssignableFrom(AlertActivityViewModel.class)) {
            return (T) new AlertActivityViewModel(repository, gson);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
