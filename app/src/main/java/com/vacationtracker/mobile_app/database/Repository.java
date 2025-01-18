package com.vacationtracker.mobile_app.database;

import android.app.Application;

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

    private List<Vacation> mAllVacation;

    private  List<Excursion> mAllExcursion;

    private static int NUMBER_OF_THREADS=4;

    static final ExecutorService databaseExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(DatabaseBuilder db) {
        mVacationDAO = db.vacationDAO();
        mExcursionDAO = db.excusionDAO();
    }

    public Repository(Application application) {

        DatabaseBuilder db=DatabaseBuilder.getDatabase(application);


        mVacationDAO = db.vacationDAO();
        mExcursionDAO = db.excusionDAO();

    }

     public List<Vacation> getAllVacation(){
        databaseExecutor.execute(()-> mAllVacation=mVacationDAO.getAllVacation());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return mAllVacation;
     }

    public void insert(Vacation vacation){
        databaseExecutor.execute(()-> mVacationDAO.insert(vacation));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void update(Vacation vacation){
        databaseExecutor.execute(()-> mVacationDAO.update(vacation));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void delete(Vacation vacation){
        databaseExecutor.execute(()-> mVacationDAO.delete(vacation));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Excursion> getAllExcursion(){
        databaseExecutor.execute(()-> mAllExcursion=mExcursionDAO.getAllExcursion());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
           e.printStackTrace();
        }

        return mAllExcursion;
    }
    public void insert(Excursion excursion){
        databaseExecutor.execute(()-> mExcursionDAO.insert(excursion));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void update(Excursion excursion){
        databaseExecutor.execute(()-> mExcursionDAO.update(excursion));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void delete(Excursion excursion){
        databaseExecutor.execute(()-> mExcursionDAO.delete(excursion));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
