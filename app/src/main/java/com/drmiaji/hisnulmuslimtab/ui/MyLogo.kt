package com.drmiaji.hisnulmuslimtab.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drmiaji.hisnulmuslimtab.R
import com.drmiaji.hisnulmuslimtab.ui.theme.FontManager
import androidx.compose.ui.draw.clip

@Composable
fun MyLogo(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primary.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.primary.copy(alpha = 0.8f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            // Decorative circles in background (subtle branding element)
            Canvas(modifier = Modifier.matchParentSize()) {
                val circleColor = colorScheme.onPrimary.copy(alpha = 0.07f)
                drawCircle(
                    color = circleColor,
                    radius = size.width * 0.2f,
                    center = Offset(size.width * 0.85f, size.height * 0.2f)
                )
                drawCircle(
                    color = circleColor,
                    radius = size.width * 0.1f,
                    center = Offset(size.width * 0.15f, size.height * 0.8f)
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App icon with border and shadow
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, CircleShape)
                        .background(colorScheme.surface, CircleShape)
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    // App name with slightly enhanced styling
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier.fillMaxWidth(), // Ensures horizontal centering space
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontFamily = FontManager.getSolaimanLipiFontFamily(),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp,
                            textAlign = TextAlign.Center, // Aligns text within its bounds
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        ),
                        color = colorScheme.onPrimary
                    )

                    Spacer(Modifier.height(4.dp))

                    // Author name with slightly different styling
                    Text(
                        text = "ডক্টর আব্দুল বাতেন মিয়াজী",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontManager.getSolaimanLipiFontFamily(),
                            letterSpacing = 0.2.sp
                        ),
                        color = colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}