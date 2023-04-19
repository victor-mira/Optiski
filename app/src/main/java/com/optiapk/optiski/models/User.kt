package com.optiapk.optiski.models

import com.google.firebase.auth.FirebaseUser
import com.optiapk.optiski.enums.SkiLevelEnum

class User(
    var userId: String? = null,
    var userName: String? = null,
    var userLevel: String? = null
) {
    constructor(user: FirebaseUser, userLevel: String) : this() {
        this.userId = user.uid
        this.userName = user.displayName
        this.userLevel = userLevel
    }
}