package tech.soc.soar.shared.data.response.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertResponseRequests(
        requests: List<ResponseRequestEntity>
    )

    @Query(
        """
        SELECT *
        FROM response_requests
        WHERE alert_id = :alertId
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :skip
        """
    )
    suspend fun getResponseRequestsByAlertId(
        alertId: Long,
        skip: Int,
        limit: Int
    ): List<ResponseRequestEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM response_requests
        WHERE alert_id = :alertId
        """
    )
    suspend fun countResponseRequestsByAlertId(
        alertId: Long
    ): Int

    @Query(
        """
    UPDATE response_requests
    SET status = :status,
        updated_at = :updatedAt,
        cached_at = :cachedAt
    WHERE id = :responseRequestId
    """
    )
    suspend fun updateResponseRequestStatus(
        responseRequestId: Long,
        status: String,
        updatedAt: String,
        cachedAt: Long
    )
}