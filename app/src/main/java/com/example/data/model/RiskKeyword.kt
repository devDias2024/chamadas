package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "risk_keywords")
data class RiskKeyword(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val targetType: String // "EMBARQUE", "DESTINO", "MERCADOS"
)
