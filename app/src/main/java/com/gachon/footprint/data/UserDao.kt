package com.gachon.footprint.data

import androidx.room.*

@Dao
public interface UserDao  {
    @Query("SELECT * FROM ModelUser")
    fun getAll(): List<ModelUser>

    @Insert
    fun insert( user: ModelUser)

    @Update
    fun update( user: ModelUser)

    @Delete
    fun delete( user: ModelUser)
}