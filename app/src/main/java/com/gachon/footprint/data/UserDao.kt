package com.gachon.footprint.data

import androidx.room.*

@Dao
public interface UserDao  {
    @Query("SELECT * FROM EnterUSer")
    fun getAll(): List<EnterUSer>

    @Insert
    fun insert(user: EnterUSer)

    @Update
    fun update( user: EnterUSer)

    @Delete
    fun delete( user: EnterUSer)

}