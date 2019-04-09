package za.co.moxomo.viewmodel;


import com.google.gson.JsonElement;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.ViewModel;
import za.co.moxomo.model.AlertDTO;
import za.co.moxomo.repository.Repository;

public class ProfileActivityViewModel extends ViewModel {
    private Executor executor;
   /* private Repository repository;

    ProfileActivityViewModel(Repository repository){
        this.repository =repository;
        executor = Executors.newSingleThreadExecutor();
    }


    public void createAlert(AlertDTO alertDTO) {
     repository.createAlert(alertDTO).subscribe(JsonElementresult->{

     });
    }*/


}