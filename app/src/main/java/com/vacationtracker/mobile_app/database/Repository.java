package com.vacationtracker.mobile_app.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.vacationtracker.mobile_app.dao.ExcusionDAO;
import com.vacationtracker.mobile_app.dao.VacationDAO;
import com.vacationtracker.mobile_app.entities.Excursion;
import com.vacationtracker.mobile_app.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private VacationDAO mVacationDAO;
    private ExcusionDAO mExcursionDAO;

    private LiveData<List<Vacation>> mAllVacation;

    private LiveData<List<Excursion>> mAllExcursion;

    private static int NUMBER_OF_THREADS=4;

    static final ExecutorService databaseExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(DatabaseBuilder db) {
        mVacationDAO = db.vacationDAO();
        mExcursionDAO = db.excusionDAO();
        mAllVacation = mVacationDAO.getAllVacation();
        mAllExcursion = mExcursionDAO.getAllExcursion();
    }

    public Repository(Application application) {

        DatabaseBuilder db=DatabaseBuilder.getDatabase(application);


        mVacationDAO = db.vacationDAO();
        mExcursionDAO = db.excusionDAO();
        mAllVacation = mVacationDAO.getAllVacation();
        mAllExcursion = mExcursionDAO.getAllExcursion();

    }

     public LiveData<List<Vacation>> getAllVacation(){
        return mAllVacation;
     }

    public void insert(Vacation vacation){
        databaseExecutor.execute(()-> mVacationDAO.insert(vacation));
    }
    public void update(Vacation vacation){
        databaseExecutor.execute(()-> mVacationDAO.update(vacation));
    }
    public void delete(Vacation vacation){
        databaseExecutor.execute(()-> mVacationDAO.delete(vacation));
    }

    public LiveData<List<Excursion>> getAllExcursion(){
        return mAllExcursion;
    }
    public void insert(Excursion excursion){
        databaseExecutor.execute(()-> mExcursionDAO.insert(excursion));
    }
    public void update(Excursion excursion){
        databaseExecutor.execute(()-> mExcursionDAO.update(excursion));
    }
    public void delete(Excursion excursion){
        databaseExecutor.execute(()-> mExcursionDAO.delete(excursion));
    }

}
