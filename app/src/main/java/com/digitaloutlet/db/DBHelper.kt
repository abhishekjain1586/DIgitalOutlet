package com.digitaloutlet.db

import androidx.room.Room
import com.digitaloutlet.application.DOApplication

object DBHelper {

    private var dbInstance_ : DigitalOutletDatabase? = null

    fun getInstance() : DigitalOutletDatabase {
        if (dbInstance_ == null) {
            dbInstance_ = Room.databaseBuilder(DOApplication._INSTANCE.applicationContext,
                DigitalOutletDatabase::class.java,
                DigitalOutletDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
        }
        return dbInstance_ as DigitalOutletDatabase
    }
}