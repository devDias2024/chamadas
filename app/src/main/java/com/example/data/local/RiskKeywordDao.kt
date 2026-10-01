package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RiskKeyword
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskKeywordDao {
    @Query("SELECT * FROM risk_keywords ORDER BY id DESC")
    fun getAllKeywords(): Flow<List<RiskKeyword>>

    @Query("SELECT * FROM risk_keywords WHERE targetType = :type ORDER BY id DESC")
    fun getKeywordsByType(type: String): Flow<List<RiskKeyword>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: RiskKeyword): Long

    @Delete
    suspend fun deleteKeyword(keyword: RiskKeyword)
}
