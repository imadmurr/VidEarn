package murr.imad.videarn.referrals.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.auth.data.repository.UserRepository
import murr.imad.videarn.utils.Constants

/**
 * Possible outcomes when a user attempts to redeem an invite code.
 */
sealed class RedeemResult {
    /** Both users were rewarded successfully. */
    data class Success(val referralUserName: String, val referralUserFcmToken: String) : RedeemResult()
    /** The invite code was not found in the database. */
    object InvalidCode : RedeemResult()
    /** The user tried to redeem their own invite code. */
    object SelfReferral : RedeemResult()
}

/**
 * Handles all Firestore operations related to the referral system.
 *
 * This class is a pure data layer — it has no dependency on Activity, Fragment,
 * or any Android UI component. Orchestration and UI state live in [ReferralViewModel].
 */
class ReferralRepository {

    private val firestore = Firebase.firestore
    private val currentUserId = UserRepository().getCurrentUserID()

    /**
     * Looks up a user document by invite code.
     */
    private suspend fun findUserByInviteCode(inviteCode: String): User? {
        return try {
            val snapshot = firestore
                .collection(Constants.USERS)
                .whereEqualTo(Constants.FIELD_INVITE_CODE, inviteCode)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.toObject<User>()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Redeems an invite code, rewarding both the referrer and the referred user atomically.
     *
     * Uses a Firestore transaction so that both point updates succeed or both fail together —
     * there is no partial state where only one user receives a reward.
     *
     * @param inviteCode The code entered by the current user.
     * @param currentUser The currently signed-in user.
     * @return A [Result] wrapping a [RedeemResult] describing the outcome.
     */
    suspend fun redeemInviteCode(inviteCode: String, currentUser: User): Result<RedeemResult> {
        return try {
            val referralUser = findUserByInviteCode(inviteCode)
                ?: return Result.success(RedeemResult.InvalidCode)

            if (referralUser.id == currentUser.id) {
                return Result.success(RedeemResult.SelfReferral)
            }

            val referralUserRef = firestore.collection(Constants.USERS).document(referralUser.id)
            val currentUserRef = firestore.collection(Constants.USERS).document(currentUserId)

            // Atomic transaction: both updates commit together or neither does.
            firestore.runTransaction { transaction ->
                transaction.update(referralUserRef, Constants.POINTS, referralUser.points + Constants.REFERRAL_REWARD)
                transaction.update(currentUserRef, Constants.POINTS, currentUser.points + Constants.REFERRAL_REWARD)
            }.await()

            Result.success(
                RedeemResult.Success(
                    referralUserName = referralUser.name,
                    referralUserFcmToken = referralUser.fcmToken
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}