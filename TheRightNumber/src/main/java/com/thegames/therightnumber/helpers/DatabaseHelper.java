package com.thegames.therightnumber.helpers;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by gyljean-lambert on 25/05/2014.
 *
 * CRUD Database helper class for db operations
 */
public class DatabaseHelper<T extends SugarRecord> {

    protected Class<T> mToken;

    public DatabaseHelper(Class<T> token) {
        mToken = token;
    }

    /**
     * Fetch all entities of type T from the DB
     * @return A List with all the entities of type T
     */
    public List<T> fetchAll() {
        return T.listAll(mToken);
    }


    public T getById(long id) {
        return T.findById(mToken, id);
    }

    public void insertOrUpdate(T obj) {
        if(obj != null) {
            obj.save();
        }
    }

    public T find(String queryParams, String... params) {
        List<T> result = T.find(mToken, queryParams, params);
        if(result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public List<T> findAll(String queryParams, String... params) {
        return T.find(mToken, queryParams, params);
    }


}
