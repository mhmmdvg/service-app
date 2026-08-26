package com.cashierserviceapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cashierserviceapp.domain.models.UserRole

/**
 * The cached `GET /me`, field-for-field.
 *
 * One row, always: only one cashier is signed in at a time, so [id] is fixed rather than the user's
 * own — a new sign-in overwrites the row instead of settling beside the last one's.
 */
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val id: Int = SINGLE_ROW,
    /** The user's own id, which the server may leave off. */
    val userId: String?,
    val name: String,
    val email: String,
    val role: UserRole,
    val phone: String?,
    val createdAt: String?,
) {
    companion object {
        const val SINGLE_ROW = 1
    }
}
