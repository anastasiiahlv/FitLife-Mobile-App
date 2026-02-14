package com.example.fitlife.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fitlife.db"
            )
                // Для лабораторної можна дозволити це,
                // але в реальному проєкті краще НЕ робити.
                // .allowMainThreadQueries()

                // Якщо міняєш схему і не хочеш робити міграції на етапі ЛР:
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}
