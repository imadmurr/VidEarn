package com.app.admin.data.repository

import com.app.admin.Constants
import com.app.admin.data.model.User
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository class for handling user data in Firestore, using coroutines and Flow.
 */
class UserRepository {

}
