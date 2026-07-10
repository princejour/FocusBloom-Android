package com.walhero.focusbloom

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import com.walhero.focusbloom.privacy.ConsentManager
import com.walhero.focusbloom.ui.FocusBloomRoot
import com.walhero.focusbloom.ui.FocusBloomViewModel
import com.walhero.focusbloom.ui.collectAsStateWithLifecycleCompat
import com.walhero.focusbloom.ui.theme.FocusBloomTheme

class MainActivity : AppCompatActivity() {
    private lateinit var consentManager: ConsentManager
    private var adsReady by mutableStateOf(false)
    private var privacyOptionsRequired by mutableStateOf(false)
    private var mobileAdsStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        consentManager = ConsentManager(this)
        val selectedLanguage = AppCompatDelegate.getApplicationLocales()[0]?.language.orEmpty()

        setContent {
            val viewModel: FocusBloomViewModel = viewModel()
            val appState by viewModel.appState.collectAsStateWithLifecycleCompat()

            FocusBloomTheme(themeMode = appState.themeMode) {
                FocusBloomRoot(
                    viewModel = viewModel,
                    adsReady = adsReady,
                    privacyOptionsRequired = privacyOptionsRequired,
                    selectedLanguage = selectedLanguage,
                    onPrivacyOptions = {
                        consentManager.showPrivacyOptions(this) {
                            privacyOptionsRequired = consentManager.isPrivacyOptionsRequired
                            startMobileAdsIfAllowed()
                        }
                    },
                    onLanguageSelected = ::setAppLanguage,
                )
            }
        }

        consentManager.gatherConsent(this) {
            privacyOptionsRequired = consentManager.isPrivacyOptionsRequired
            startMobileAdsIfAllowed()
        }
    }

    private fun startMobileAdsIfAllowed() {
        if (!consentManager.canRequestAds || mobileAdsStarted) return
        mobileAdsStarted = true
        MobileAds.initialize(this) {
            adsReady = true
        }
    }

    private fun setAppLanguage(languageTag: String) {
        val locales = if (languageTag.isBlank()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
