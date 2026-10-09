package com.example.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingSlide(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val badge: String
)

private val SLIDES = listOf(
    OnboardingSlide(
        emoji = "⏰",
        title = "Never Miss a Lecture Again",
        subtitle = "Organize all your daily class timetables with exact background wake alarms that trigger even when your phone is locked.",
        badge = "TIMETABLE ASSISTANT"
    ),
    OnboardingSlide(
        emoji = "🎨",
        title = "Add Classes in Seconds",
        subtitle = "Pick your start time, customizable repeat rules (Daily, Weekdays, Weekends, Custom), room numbers, and 12 subject emojis.",
        badge = "FLEXIBLE REPEAT RULES"
    ),
    OnboardingSlide(
        emoji = "🔔",
        title = "Full-Screen Wake Alerts",
        subtitle = "Experience our clean solid white full-screen alert overlay with 3-second vibration, lockscreen wake-up, and quick snooze options.",
        badge = "EXACT WAKE & SNOOZE"
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val currentSlide = SLIDES[currentPage]
    val isLastPage = currentPage == SLIDES.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEEF2FF), Color(0xFFF8FAFC), Color.White)
                )
            )
            .padding(24.dp)
            .testTag("onboarding_screen")
    ) {
        // TOP SKIP BUTTON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (!isLastPage) {
                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // CENTER CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF6366F1).copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = currentSlide.badge,
                    color = Color(0xFF6366F1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Emoji circle container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(12.dp, CircleShape, ambientColor = Color(0x336366F1))
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentSlide.emoji,
                    fontSize = 58.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title
            Text(
                text = currentSlide.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = currentSlide.subtitle,
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // BOTTOM CONTROLS: DOTS & NEXT/GET STARTED
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(SLIDES.size) { index ->
                    val isSelected = index == currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) Color(0xFF6366F1) else Color(0xFFCBD5E1)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Next / Get Started button
            Button(
                onClick = {
                    if (isLastPage) onFinish()
                    else currentPage++
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x336366F1))
                    .testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
