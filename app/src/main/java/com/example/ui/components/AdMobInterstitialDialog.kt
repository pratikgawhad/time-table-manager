package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.ads.AdMobConfig
import kotlinx.coroutines.delay

/**
 * Full-screen Interstitial Ad simulator triggered every 3rd lecture save.
 * Uses official AdMob Test Unit ID: ca-app-pub-3940256099942544/1033173712
 */
@Composable
fun AdMobInterstitialDialog(
    onDismiss: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Dialog(
        onDismissRequest = {
            if (countdown <= 0) onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = countdown <= 0,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111827))
                .testTag("admob_interstitial_dialog")
        ) {
            // Top Ad Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEAB308))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Ad • Test Interstitial",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "(Every 3rd Save)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp
                    )
                }

                // Close Button / Countdown
                if (countdown > 0) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF374151)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$countdown",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .testTag("close_interstitial_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Ad",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Creative Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Google AdMob Test Unit",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ID: ${AdMobConfig.INTERSTITIAL_AD_UNIT_ID}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFF1F2937),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4.9 (1.2M reviews)",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This interstitial ad appeared because you saved 3 timetable entries. Replace with your live AdMob IDs before publishing.",
                            color = Color(0xFFD1D5DB),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (countdown <= 0) onDismiss()
                    },
                    enabled = countdown <= 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        disabledContainerColor = Color(0xFF374151)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (countdown > 0) "Reward in $countdown s..." else "Continue to Timetable",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
