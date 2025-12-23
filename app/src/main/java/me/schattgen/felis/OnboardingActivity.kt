package me.schattgen.felis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
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

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = OnboardingAdapter(pages)

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

        val initialLeft = binding.root.paddingLeft
        val initialTop = binding.root.paddingTop
        val initialRight = binding.root.paddingRight
        val initialBottom = binding.root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())

            v.setPadding(
                initialLeft,
                initialTop + bars.top + 20,
                initialRight,
                initialBottom
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
        OnboardingPrefs.setCompleted(this, true)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }
}