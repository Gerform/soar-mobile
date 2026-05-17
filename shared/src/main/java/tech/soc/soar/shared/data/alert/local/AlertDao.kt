package tech.soc.soar.shared.data.alert.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlerts(alerts: List<AlertEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlertViews(views: List<AlertViewEntity>)

    @Query(
        """
        SELECT 
            alerts.id,
            alerts.date,
            alerts.status,
            alerts.space_name,
            alerts.reason,
            alert_views.is_viewed
        FROM alerts
        LEFT JOIN alert_views 
            ON alerts.id = alert_views.alert_id
            AND alert_views.user_id = :userId
        WHERE alerts.space_name = :spaceName
        ORDER BY alerts.date DESC
        LIMIT :limit OFFSET :skip
        """
    )
    suspend fun getAlertsForSpace(
        spaceName: String,
        userId: Int,
        skip: Int,
        limit: Int
    ): List<AlertWithViewEntity>
}