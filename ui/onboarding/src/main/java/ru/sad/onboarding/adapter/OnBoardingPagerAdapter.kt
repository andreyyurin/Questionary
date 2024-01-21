package ru.sad.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.sad.base.base.AnimationScrollListener
import ru.sad.base.base.BaseFragment
import ru.sad.onboarding.pages.OnBoardingFirstPageFragment
import ru.sad.onboarding.pages.OnBoardingFourthPageFragment
import ru.sad.onboarding.pages.OnBoardingSecondPageFragment
import ru.sad.onboarding.pages.OnBoardingThirdPageFragment

class OnBoardingPagerAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    private var fragments = listOf(
        OnBoardingFirstPageFragment(),
        OnBoardingSecondPageFragment(),
        OnBoardingThirdPageFragment(),
        OnBoardingFourthPageFragment()
    )

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): BaseFragment<*> = fragments[position]

    fun getFragment(position: Int): BaseFragment<*> = fragments[position]
}