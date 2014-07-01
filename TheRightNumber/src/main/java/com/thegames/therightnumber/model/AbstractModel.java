package com.thegames.therightnumber.model;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by gyljean-lambert on 20/03/2014.
 */
public abstract class AbstractModel extends SugarRecord {

    public AbstractModel(Context context) {
        super(context);
    }
}
