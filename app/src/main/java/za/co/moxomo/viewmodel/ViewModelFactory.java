package za.co.moxomo.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import za.co.moxomo.repository.Repository;
import za.co.moxomo.viewmodel.MainActivityViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private Repository repository;

    @Inject
    public ViewModelFactory(Repository repository) {
        this.repository =repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
