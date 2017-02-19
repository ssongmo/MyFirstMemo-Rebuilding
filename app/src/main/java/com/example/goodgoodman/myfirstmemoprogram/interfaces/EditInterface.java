package com.example.goodgoodman.myfirstmemoprogram.interfaces;

import com.example.goodgoodman.myfirstmemoprogram.Memo;

import java.sql.SQLException;

/**
 * Created by GoodGoodMan on 2017. 2. 15..
 */

public interface EditInterface {
    public void backToList();
    public void saveToList(Memo memo) throws SQLException;
}
