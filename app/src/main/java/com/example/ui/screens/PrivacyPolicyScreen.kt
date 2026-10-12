package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top App Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Privacy Policy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Privacy Policy for Time Table Manager", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text(text = "Last updated: October 2026", fontSize = 12.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(16.dp))

                    PolicySection(
                        title = "1. Information We Collect",
                        body = "Time Table Manager stores your scheduled lectures, class timings, and room details locally on your device using SQLite. We do not transmit or sell your personal schedule data to external servers."
                    )

                    PolicySection(
                        title = "2. How We Use Information",
                        body = "Your timetable data is used exclusively to trigger precise background alarm wake-ups, display full-screen alerts, and vibrate your phone at class times."
                    )

                    PolicySection(
                        title = "3. Third-Party Services (AdMob)",
                        body = "Our app displays advertising banners provided by Google AdMob. AdMob may collect and process pseudonymous device identifiers and diagnostics in accordance with Google's Privacy Policy to serve relevant advertising."
                    )

                    PolicySection(
                        title = "4. Data Storage & Security",
                        body = "All your timetable data is stored locally on your device. Clearing app storage or uninstalling the app will remove your locally saved records."
                    )

                    PolicySection(
                        title = "5. User Rights & Data Deletion",
                        body = "You have full control over your data. You can edit, delete, or reset your lectures at any time directly through the app interface."
                    )

                    PolicySection(
                        title = "6. Contact Us",
                        body = "If you have questions or concerns regarding this Privacy Policy, please contact our support team at support@example.com."
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = body, fontSize = 12.sp, color = Color(0xFF475569), lineHeight = 18.sp)
    }
}
