package za.co.moxomo.viewmodel;


import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import za.co.moxomo.repository.Repository;


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
