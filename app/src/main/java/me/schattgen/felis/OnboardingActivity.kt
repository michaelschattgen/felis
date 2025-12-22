package me.schattgen.felis

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import me.schattgen.felis.adapters.OnboardingAdapter
import me.schattgen.felis.databinding.ActivityOnboardingBinding
import me.schattgen.felis.models.OnboardingPage

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    private val pages by lazy {
        listOf(
            OnboardingPage(
                imageRes = R.drawable.ic_launcher_background,
                titleRes = R.string.onboarding_share_title,
                descriptionRes = R.string.onboarding_share_desc
            ),
            OnboardingPage(
                imageRes = R.drawable.ic_launcher_background,
                titleRes = R.string.onboarding_selection_title,
                descriptionRes = R.string.onboarding_selection_desc
            ),
            OnboardingPage(
                imageRes = R.drawable.ic_launcher_background,
                titleRes = R.string.onboarding_manual_title,
                descriptionRes = R.string.onboarding_manual_desc
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)



        /*        if (hasCompletedOnboarding()) {
                    showSimpleInfoScreen()
                    return
                }*/

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabDots, binding.viewPager) { _: TabLayout.Tab, _: Int -> }
            .attach()

        updateNextButtonText(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNextButtonText(position)
            }
        })

        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }

        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < pages.lastIndex) {
                binding.viewPager.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                bars.left + v.paddingLeft,
                bars.top + v.paddingTop,
                bars.right + v.paddingRight,
                bars.bottom + v.paddingBottom
            )

            insets
        }
    }

    private fun updateNextButtonText(position: Int) {
        val isLast = position == pages.lastIndex
        binding.btnNext.setText(
            if (isLast) R.string.onboarding_get_started
            else R.string.onboarding_next
        )
    }

    private fun completeOnboarding() {
        getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("completed", true)
            .apply()
        showSimpleInfoScreen()
    }

    private fun hasCompletedOnboarding(): Boolean {
        return getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("completed", false)
    }

    private fun showSimpleInfoScreen() {
        val tv = TextView(this).apply {
            text = getString(R.string.post_onboarding_info)
            setPadding(48, 48, 48, 48)
        }
        setContentView(tv)
    }

}