package com.gachon.footprint.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gachon.footprint.UserEnterActivity
import com.gachon.footprint.data.UserDB.Companion.INSTANCE as INSTANCE1

@Database(entities = [EnterUSer::class], version = 1)
abstract class UserDB : RoomDatabase() {
    abstract fun userDao() : UserDao

    companion object{
        private var INSTANCE: UserDB? =null

        fun getInstance(context: UserEnterActivity): UserDB? {
            if(INSTANCE1 == null) {
                synchronized(UserDB::class) {
                    INSTANCE1 = Room.databaseBuilder(
                        context.applicationContext,
                    UserDB::class.java, "enter_user.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE1
        }

        fun destroyInstance() {
            INSTANCE1 = null
        }
    }
}