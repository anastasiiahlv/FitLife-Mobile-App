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
    val yearMonth: String,
    val count: Int
)

data class DayCount(
    val day: String,
    val count: Int
)

data class CenterCount(
    val centerId: String,
    val centerName: String,
    val count: Int
)

@Dao
interface VisitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: VisitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visits: List<VisitEntity>)

    @Update
    suspend fun update(visit: VisitEntity)

    @Delete
    suspend fun delete(visit: VisitEntity)

    @Query("DELETE FROM visits WHERE id = :visitId")
    suspend fun deleteById(visitId: Int)

    @Query("DELETE FROM visits")
    suspend fun deleteAll()

    @Query("SELECT * FROM visits ORDER BY visit_date DESC")
    fun observeAll(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits ORDER BY visit_date DESC")
    suspend fun getAll(): List<VisitEntity>

    @Query("SELECT * FROM visits WHERE center_id = :centerId ORDER BY visit_date DESC")
    fun observeByCenter(centerId: String): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE center_id = :centerId ORDER BY visit_date DESC")
    suspend fun getByCenter(centerId: String): List<VisitEntity>

    @Query("SELECT COUNT(*) FROM visits")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM fitness_centers WHERE id = :centerId)")
    suspend fun centerExists(centerId: String): Boolean

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