package com.digitaloutlet.db.dao

import androidx.room.*
import com.digitaloutlet.utils.Constants.type
import java.util.ArrayList

@Dao
abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(type: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(type: List<T>): List<Long>

    @Update
    abstract fun update(type: T)

    @Update
    abstract fun updateAll(type: List<T>)

    @Transaction
    open fun insertOrUpdate(type: T) {
        val rowId: Long = insert(type)
        if (rowId == -1L) {
            update(type)
        }
    }

    @Transaction
    open fun insertOrUpdateAll(lst: List<T>) {
        val insertResult = insertAll(lst)
        val updateList = ArrayList<T>()

        for (i in 0 until insertResult.size) {
            if (insertResult.get(i) == -1L) {
                updateList.add(lst.get(i))
            }
        }

        if (!updateList.isEmpty()) {
            updateAll(updateList)
        }
    }

    @Delete
    abstract fun delete(type: T)
}