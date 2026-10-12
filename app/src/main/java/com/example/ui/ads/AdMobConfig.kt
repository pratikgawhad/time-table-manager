package com.example.ui.ads

/**
 * AdMob Configuration for Time Table Manager
 *
 * Configured with REAL AdMob IDs per request.
 * ONLY BANNER ADS are enabled. Interstitial ads are disabled.
 */
object AdMobConfig {

    // ─────────────────────────────────────────────────────────────────────────────
    // ⚙️ TESTING MODE FLAG:
    // When true  -> uses Google Test Ad IDs
    // When false -> uses your REAL AdMob IDs
    // ─────────────────────────────────────────────────────────────────────────────
    var useTestAds: Boolean = false

    // REAL ADMOB IDs
    const val REAL_APP_ID = "ca-app-pub-7527835129661450~4783640462"
    const val REAL_BANNER_AD_UNIT_ID = "ca-app-pub-7527835129661450/9591356425"

    // TEST ADMOB IDs (for debug / preview verification)
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // ONLY BANNER ADS: Interstitial ads are disabled per request
    const val ENABLE_INTERSTITIAL_ADS = false
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Active Banner Unit ID based on test mode flag
    val activeBannerAdUnitId: String
        get() = if (useTestAds) TEST_BANNER_AD_UNIT_ID else REAL_BANNER_AD_UNIT_ID
}
