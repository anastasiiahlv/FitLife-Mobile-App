package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlife.data.local.entity.VisitEntity
import kotlinx.coroutines.flow.Flow

data class MonthCount(
    val yearMonth: String,  // "2026-02"
    val count: Int
)

data class DayCount(
    val day: String,   // "2026-03-28"
    val count: Int
)

data class CenterCount(
    val centerId: String,
    val centerName: String,
    val count: Int
)

@Dao
interface VisitsDao {

    // ---- CRUD ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: VisitEntity): Long

    @Update
    suspend fun update(visit: VisitEntity)

    @Delete
    suspend fun delete(visit: VisitEntity)

    @Query("DELETE FROM visits WHERE id = :visitId")
    suspend fun deleteById(visitId: Int)

    // ---- Reads ----
    @Query("SELECT * FROM visits ORDER BY visit_date DESC")
    fun observeAll(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits ORDER BY visit_date DESC")
    suspend fun getAll(): List<VisitEntity>

    @Query("SELECT * FROM visits WHERE center_id = :centerId ORDER BY visit_date DESC")
    fun observeByCenter(centerId: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE center_id = :centerId ORDER BY visit_date DESC")
    suspend fun getByCenter(centerId: String): List<VisitEntity>

    // ---- Simple stats ----
    @Query("SELECT COUNT(*) FROM visits")
    fun observeTotalCount(): Flow<Int>

    // Групування по місяцях: visitDate (millis) -> seconds -> unixepoch
    @Query(
        """
        SELECT 
          strftime('%Y-%m', visit_date / 1000, 'unixepoch') AS yearMonth,
          COUNT(*) AS count
        FROM visits
        GROUP BY yearMonth
        ORDER BY yearMonth ASC
        """
    )
    suspend fun countVisitsByMonth(): List<MonthCount>

    @Query(
        """
        SELECT 
          v.center_id AS centerId,
          fc.name AS centerName,
          COUNT(*) AS count
        FROM visits v
        INNER JOIN fitness_centers fc ON fc.id = v.center_id
        GROUP BY v.center_id
        ORDER BY count DESC
        """
    )
    suspend fun countVisitsByCenter(): List<CenterCount>

    @Query(
        """
        SELECT COUNT(*) 
        FROM visits
        WHERE visit_date BETWEEN :fromMs AND :toMs
        """
    )
    suspend fun countBetween(fromMs: Long, toMs: Long): Int

    @Query("UPDATE visits SET comment = :comment WHERE id = :visitId")
    suspend fun updateComment(visitId: Int, comment: String?)

    @Query(
        """
    SELECT 
      strftime('%Y-%m-%d', visit_date / 1000, 'unixepoch') AS day,
      COUNT(*) AS count
    FROM visits
    WHERE visit_date BETWEEN :fromMs AND :toMs
    GROUP BY day
    ORDER BY day ASC
    """
    )
    suspend fun countVisitsByDay(fromMs: Long, toMs: Long): List<DayCount>
}