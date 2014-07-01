package com.thegames.therightnumber.helpers;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.thegames.therightnumber.model.Question;

import java.util.List;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class QuestionsDBHelper extends DatabaseHelper<Question> {

    public QuestionsDBHelper(Class token) {
        super(token);
    }

    public List<Question> fetchUnplayedOnly() {
       return Select.from(Question.class)
                .where(Condition.prop("played").eq("false"))
                .orderBy("level ASC")
                .list();
    }

}
