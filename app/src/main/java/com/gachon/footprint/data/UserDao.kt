package com.gachon.footprint.data

import androidx.room.*

@Dao
public interface UserDao  {
    @Query("SELECT * FROM user")
    fun getAllInfo(): List<CurrentUser>

    @Insert
    fun insert(user: CurrentUser)


    @Update
    fun update( user: CurrentUser)

    @Delete
    fun delete( user: CurrentUser)

}