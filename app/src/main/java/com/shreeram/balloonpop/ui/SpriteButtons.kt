package com.shreeram.balloonpop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shreeram.balloonpop.R

enum class SpriteButtonType(val resId: Int) {
    PLAY(R.drawable.button_play_icon),
    PAUSE(R.drawable.button_pause_icon),
    RESUME(R.drawable.button_arrow_loop),
    EXIT(R.drawable.button_exit),
    HOME(R.drawable.button_home_icon),
    BLANK_LONG(R.drawable.button_blank_large),
    CIRCLE_GEAR(R.drawable.button_blank_circle),
    CIRCLE_LEADERBOARD(R.drawable.button_blank_circle),
    VOLUME_ON(R.drawable.button_volume),
    VOLUME_OFF(R.drawable.button_mute),
    CLOSE(R.drawable.button_close),
    CHECK(R.drawable.button_check)
}

@Composable
fun SpriteButton(
    type: SpriteButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    width: Int = 160,
    height: Int = 56,
    iconResId: Int? = null,
    iconVector: ImageVector? = null
) {
    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = type.resId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (iconResId != null) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size((height * 0.5).dp)
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((height * 0.5).dp)
            )
        }
        else if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = (height / 2.6).sp
                ),
                modifier = Modifier.padding(bottom = (height * 0.08).dp)
            )
        }
    }
}
