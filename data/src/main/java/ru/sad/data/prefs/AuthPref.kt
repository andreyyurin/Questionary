package ru.sad.data.prefs

import com.chibatching.kotpref.KotprefModel

object AuthPref : KotprefModel() {

    override val kotprefName: String
        get() = "AUTH_SETTINGS"

    var isAuthorized by booleanPref(key = "isAuthorized", default = false)

    var authToken by nullableStringPref(key = "token")

    var userId by intPref(-1, key = "user_id")

    var isOnBoardingShown by booleanPref(key = "isOnBoardingShow", default = false)
}