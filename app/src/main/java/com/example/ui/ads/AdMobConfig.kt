package com.example.ui.ads

/**
 * AdMob Configuration for Time Table Manager
 *
 * Current IDs are official Google AdMob TEST unit IDs.
 * Replace with your real AdMob App ID and Ad Unit IDs from https://apps.admob.com before publishing.
 */
object AdMobConfig {

    // ─────────────────────────────────────────────────────────────────────────────
    // ⚙️ WHERE TO PUT YOUR REAL ADMOB IDS:
    // ─────────────────────────────────────────────────────────────────────────────
    // 1. In AndroidManifest.xml:
    //    <meta-data
    //        android:name="com.google.android.gms.ads.APPLICATION_ID"
    //        android:value="ca-app-pub-YOUR_ADMOB_ACCOUNT_ID~YOUR_APP_ID"/>
    //
    // 2. Replace the IDs below with your real unit IDs:
    // ─────────────────────────────────────────────────────────────────────────────

    // Official Google Test App ID
    const val ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    // Official Google Test Banner ID (Standard 320x50 / Adaptive Banner)
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Official Google Test Interstitial ID (Triggered every 3rd lecture save)
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // When true, test badge & mock creatives are active
    const val IS_TEST_MODE = true
}
