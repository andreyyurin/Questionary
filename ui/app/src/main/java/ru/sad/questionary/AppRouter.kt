package ru.sad.questionary

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.base.BaseRouterImpl
import ru.sad.base.navigation.NavigationKey
import ru.sad.base.simple.SimpleListBottomSheet
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.quiz.QuizBottomSheet
import java.lang.Exception
import java.lang.IllegalStateException

internal class AppRouter(
    private val navController: NavController,
    private val openDialog: (BaseBottomSheetDialogFragment<*>) -> Unit
) : BaseRouterImpl {

    override val enterAnimation: Int = android.R.anim.fade_in

    override val exitAnimation: Int = android.R.anim.fade_out

    override fun navigate(key: NavigationKey, bundles: Bundle) {
        when (key) {
            NavigationKey.LOGIN_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)
                    .setPopUpTo(R.id.nav_graph, false)

                navController.navigate(
                    R.id.LoginFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.SPLASH_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)
                    .setPopUpTo(R.id.nav_graph, false)

                navController.navigate(
                    R.id.SplashFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.PROFILE_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)
                    .setPopUpTo(R.id.nav_graph, false)

                navController.navigate(
                    R.id.ProfileFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.CREATE_QUIZ_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)

                navController.navigate(
                    R.id.CreateQuizFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.QUIZ_RESULT_SCREEN -> {
                val navOptions = NavOptions.Builder()

                navController.navigate(
                    R.id.QuizResultFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.ONBOARDING_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, false)

                navController.navigate(
                    R.id.OnBoardingFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.CREATE_QUIZ_FINAL_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)

                navController.navigate(
                    R.id.CreateQuizFinalFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.QUIZ_SCREEN -> {
                val dialog = QuizBottomSheet
                    .Builder()
                    .build(bundles)

                openDialog.invoke(dialog as BaseBottomSheetDialogFragment<*>)
//                val navOptions = NavOptions.Builder()
//                    .setEnterAnim(enterAnimation)
//                    .setExitAnim(exitAnimation)
//
//                navController.navigate(
//                    R.id.QuizFragment,
//                    bundles,
//                    navOptions.build()
//                )
            }

            NavigationKey.TOP_QUIZ_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)
                    .setPopUpTo(R.id.nav_graph, false)

                navController.navigate(
                    R.id.TopQuizFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.SUBSCRIPTION_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)
                    .setPopUpTo(R.id.nav_graph, false)

                navController.navigate(
                    R.id.SubscriptionFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.CAMERA_SCREEN -> {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(enterAnimation)
                    .setExitAnim(exitAnimation)

                navController.navigate(
                    R.id.CameraFragment,
                    bundles,
                    navOptions.build()
                )
            }

            NavigationKey.EXIT -> {
                navController.popBackStack()
            }

            else -> throw (IllegalStateException("Not found screen key: $key"))
        }
    }
}