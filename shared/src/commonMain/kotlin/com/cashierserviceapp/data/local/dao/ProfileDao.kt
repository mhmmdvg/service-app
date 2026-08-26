package com.cashierserviceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cashierserviceapp.data.local.entities.ProfileEntity

@Dao
interface ProfileDao {
    @Upsert
    suspend fun upsertProfile(profile: ProfileEntity)

    /** `LIMIT 1` rather than a lookup by id — there is only ever the one row. */
    @Query("SELECT * FROM profile LIMIT 1")
    suspend fun getProfile(): ProfileEntity?

    @Query("DELETE FROM profile")
    suspend fun clear()
}
