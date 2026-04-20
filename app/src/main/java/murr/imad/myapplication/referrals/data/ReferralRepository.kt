package murr.imad.myapplication.referrals.data

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import murr.imad.myapplication.shared.ui.view.MainActivity
import murr.imad.myapplication.fcm.PostRequestForNotifications
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.utils.Constants

class ReferralRepository {

    // Create a instance of Firebase Firestore
    private val firestore = Firebase.firestore

    private val currentUserId = UserRepository().getCurrentUserID()


    /**
     * A function to search for the invite code in users collection
     *  returns a querySnapshot containing the document where code is found
     */
    private suspend fun searchForCodeInDB(inviteCode : String)
    : QuerySnapshot?{
        return try{
            val data = firestore
                .collection("users")
                .whereEqualTo("inviteCode" , inviteCode)
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }


    /**
     * A function to extract the referral user from the querySnapshot
     * and getting it as a User Model
     */
    private suspend fun getReferralUser(inviteCode: String)
    : User? {
        try {
            val querySnapshot = searchForCodeInDB(inviteCode)
            if (querySnapshot != null) {
                var referralUser: User? = null
                for (doc in querySnapshot.documents) {
                    referralUser = doc.toObject<User>()
                }
                return referralUser
            }
        }catch (e : Exception){
            return null
        }
        return null
    }


    /**
    * A function to add bonus to the referral user
    */
    private suspend fun addBonusToReferralUser(childName : String,
                                               hashMap: HashMap<String,Any>)
    : Boolean{
        return try{
            firestore
                .collection("users")
                .document(childName)
                .update(hashMap)
                .await()
            true
        }catch (e : Exception){
            false
        }
    }


    /**
     * A function to add bonus to the referred user
     */
    private suspend fun addBonusToReferredUser(hashMap: HashMap<String,Any>)
    : Boolean{
        return try{
            firestore
                .collection("users")
                .document(currentUserId)
                .update(hashMap)
                .await()
            true
        }catch (e : Exception){
            false
        }
    }


    /**
     * A function to call coroutines functions to search and reward users in DB
     */
    fun redeemInviteCode(activity: MainActivity,
                         userDetails: User,
                         inviteCode : String) {

        activity.lifecycleScope.launch(Dispatchers.IO) {

            val referralUser = ReferralRepository().getReferralUser(inviteCode)

            if (referralUser!= null && referralUser.id != userDetails.id) {

                activity.runOnUiThread {
                    activity.showProgressDialog("Rewarding..")
                }

                val referralUserHashMap = java.util.HashMap<String, Any>()
                referralUserHashMap[Constants.POINTS] =
                    referralUser.points + Constants.REFERRAL_REWARD

                if (ReferralRepository().addBonusToReferralUser(referralUser.id, referralUserHashMap)){

                    val referredUserHashMap = java.util.HashMap<String, Any>()
                    referredUserHashMap[Constants.POINTS] =
                        userDetails.points + Constants.REFERRAL_REWARD

                    val rewardSuccess =
                        ReferralRepository().addBonusToReferredUser(referredUserHashMap)

                    if(rewardSuccess){
                        activity.runOnUiThread {
                            activity.hideProgressDialog()
                            Toast.makeText(
                                activity,
                                "Reward granted!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //Send notification to the referral user
                        PostRequestForNotifications(
                            userDetails.name,
                            referralUser.fcmToken,
                            activity,"New referral",
                            "joined the app using your invite code").startApiCall()

                        val user = java.util.HashMap<String, Any>()
                        user[Constants.DIALOG_SHOWED_REDEEM] = true
                        //FirestoreUsers().updateUserData(activity,user)

                        //FirestoreUsers().readUserData(activity)
                    }
                }
            }else{
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        "Invalid code!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}