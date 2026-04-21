package murr.imad.videarn.payouts.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import murr.imad.videarn.payouts.data.model.CryptoPayout
import murr.imad.videarn.payouts.data.model.GiftCardPayout
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.utils.Constants

/**
 * Repository for managing Firebase Firestore operations related to payouts.
 *
 * @property firestore The instance of Firebase Firestore.
 */
class PayoutsRepository {

    private val firestore = Firebase.firestore

    /**
     * Adds a cryptocurrency payout to Firestore.
     *
     * @param user The current user making the payout.
     * @param cryptoPayout The cryptocurrency payout to be added.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun addCryptoPayout(user: User, cryptoPayout: CryptoPayout): Result<Unit> {
        return try {
            firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.CRYPTO_PAYMENTS)
                .document()
                .set(cryptoPayout, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PayoutsRepository", "Error adding crypto payout", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a list of pending cryptocurrency payouts for a user.
     *
     * @param user The current user whose payouts are to be fetched.
     * @return A [Result] containing a list of [CryptoPayout] or an error.
     */
    suspend fun getAllCryptoPayouts(user: User): Result<List<CryptoPayout>> {
        return try {
            val snapshot = firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.CRYPTO_PAYMENTS)
                .orderBy(Constants.FIELD_ADDED_DATE, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.FIELD_ID, user.id)
                .get()
                .await()

            val payoutsList = snapshot.documents.mapNotNull { it.toObject(CryptoPayout::class.java) }
            Result.success(payoutsList)
        } catch (e: Exception) {
            Log.e("PayoutsRepository", "Error fetching crypto payouts", e)
            Result.failure(e)
        }
    }

    /**
     * Cancels a specific cryptocurrency payout.
     *
     * @param user The current user making the request.
     * @param cryptoPayout The cryptocurrency payout to be cancelled.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun cancelCryptoPayout(user: User, cryptoPayout: CryptoPayout): Result<Unit> {
        return try {
            val snapshot = firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.CRYPTO_PAYMENTS)
                .whereEqualTo(Constants.FIELD_ID, cryptoPayout.id)
                .whereEqualTo(Constants.FIELD_AMOUNT, cryptoPayout.amount)
                .whereEqualTo(Constants.FIELD_ADDED_DATE, cryptoPayout.added_date)
                .get()
                .await()

            if (snapshot.isEmpty) return Result.failure(Exception("Payout not found"))

            val documentId = snapshot.documents[0].id
            firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.CRYPTO_PAYMENTS)
                .document(documentId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PayoutsRepository", "Error cancelling crypto payout", e)
            Result.failure(e)
        }
    }

    /**
     * Adds a gift card payout to Firestore.
     *
     * @param user The current user making the payout.
     * @param giftCardPayout The gift card payout to be added.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun addGiftCardPayout(user: User, giftCardPayout: GiftCardPayout): Result<Unit> {
        return try {
            firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.GIFT_CARDS_PAYMENTS)
                .document()
                .set(giftCardPayout, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PayoutsRepository", "Error adding gift card payout", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a list of pending gift card payouts for a user.
     *
     * @param user The current user whose payouts are to be fetched.
     * @return A [Result] containing a list of [GiftCardPayout] or an error.
     */
    suspend fun getAllGiftCardPayouts(user: User): Result<List<GiftCardPayout>> {
        return try {
            val snapshot = firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.GIFT_CARDS_PAYMENTS)
                .orderBy(Constants.FIELD_ADDED_DATE, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.FIELD_ID, user.id)
                .get()
                .await()

            val payoutsList = snapshot.documents.mapNotNull { it.toObject(GiftCardPayout::class.java) }
            Result.success(payoutsList)
        } catch (e: Exception) {
            Log.e("PayoutsRepository", "Error fetching gift card payouts", e)
            Result.failure(e)
        }
    }

    /**
     * Cancels a specific gift card payout.
     *
     * @param user The current user making the request.
     * @param giftCardPayout The gift card payout to be cancelled.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun cancelGiftCardPayout(user: User, giftCardPayout: GiftCardPayout): Result<Unit> {
        return try {
            val snapshot = firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.GIFT_CARDS_PAYMENTS)
                .whereEqualTo(Constants.FIELD_ID, giftCardPayout.id)
                .whereEqualTo(Constants.FIELD_AMOUNT, giftCardPayout.ptsAmount)
                .whereEqualTo(Constants.FIELD_ADDED_DATE, giftCardPayout.added_date)
                .get()
                .await()

            if (snapshot.isEmpty) return Result.failure(Exception("Payout not found"))

            val documentId = snapshot.documents[0].id
            firestore.collection(Constants.PAYMENTS)
                .document(user.id)
                .collection(Constants.GIFT_CARDS_PAYMENTS)
                .document(documentId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PayoutsRepository", "Error cancelling gift card payout", e)
            Result.failure(e)
        }
    }

    /**
     * Updates user points in the Firestore database using increment.
     *
     * @param user The user whose points need to be updated.
     * @param pointsUpdate The points to increment (can be positive or negative).
     * @return A result indicating success or failure.
     */
    suspend fun updateUserPoints(user: User, pointsUpdate: Long): Result<Unit> {
        return try {
            val userRef = firestore.collection(Constants.USERS).document(user.id)
            userRef.update(Constants.POINTS, FieldValue.increment(pointsUpdate)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}