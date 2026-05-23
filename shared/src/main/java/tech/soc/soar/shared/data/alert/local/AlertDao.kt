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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlertDetail(detail: AlertDetailsEntity)

    @Query(
        """
    SELECT *
    FROM alert_details
    WHERE id = :alertId
      AND space_name = :spaceName
    LIMIT 1
    """
    )
    suspend fun getAlertDetail(
        alertId: Long,
        spaceName: String
    ): AlertDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlertView(view: AlertViewEntity)

    @Query(
        """
    UPDATE alerts
    SET status = :status
    WHERE id = :alertId
    """
    )
    suspend fun updateAlertStatus(
        alertId: Long,
        status: String
    )

    @Query(
        """
    UPDATE alert_details
    SET status = :status,
        updated_at = :updatedAt
    WHERE id = :alertId
    """
    )
    suspend fun updateAlertDetailsStatus(
        alertId: Long,
        status: String,
        updatedAt: Long
    )
}