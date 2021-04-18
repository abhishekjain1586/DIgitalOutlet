package com.digitaloutlet.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.digitaloutlet.db.DigitalOutletDatabase.Companion.DATABASE_VERSION
import com.digitaloutlet.db.dao.ProductsDao
import com.digitaloutlet.db.entities.ProductsEntity

@Database(entities = arrayOf(ProductsEntity::class), version = DATABASE_VERSION, exportSchema = false)
abstract class DigitalOutletDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "digital_outlet.db"
        const val DATABASE_VERSION: Int = 1
    }

    abstract fun getProductsDao() : ProductsDao
}