package com.dailygoal.reminder.ui.animation

import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dailygoal.reminder.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

/**
 * Aimly Motion System Specs and Utilities.
 * Enforces standardized motion rules:
 * - Fast interactions: 150-250ms
 * - Content/State transitions: 250-450ms
 * - Hero/Spring interactions: Medium-bouncy physics
 * - Accessibility: Reduced motion check support
 */
object AimlyMotionSpecs {
    const val FastDuration = 180
    const val ContentDuration = 320
    const val HeroDuration = 450

    fun <T> fastTween(): TweenSpec<T> = tween(durationMillis = FastDuration, easing = FastOutSlowInEasing)
    fun <T> contentTween(): TweenSpec<T> = tween(durationMillis = ContentDuration, easing = FastOutSlowInEasing)

    val PressSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    val ChipSelectSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val HeroSpringFloat: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val HeroSpringDp: SpringSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val CheckmarkSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val ProgressSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * Checks system accessibility setting for reduced motion preference.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Helper to return zero-duration snap if reduced motion is preferred.
 */
@Composable
fun <T> specOrSnap(spec: FiniteAnimationSpec<T>): FiniteAnimationSpec<T> {
    return if (rememberReducedMotion()) snap() else spec
}

/**
 * Reusable press scale feedback modifier.
 */
fun Modifier.aimlyPressFeedback(
    enabled: Boolean = true,
    pressedScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    val isReducedMotion = rememberReducedMotion()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isReducedMotion) pressedScale else 1.0f,
        animationSpec = AimlyMotionSpecs.PressSpring,
        label = "AimlyPressScale"
    )

    this
        .scale(scale)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
            } else Modifier
        )
}

/**
 * Smooth numeric text counter that animates count updates with vertical slide transitions.
 */
@Composable
fun AnimatedProgressCounter(
    valueText: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.Unspecified
) {
    val isReducedMotion = rememberReducedMotion()

    if (isReducedMotion) {
        Text(
            text = valueText,
            style = textStyle,
            fontWeight = fontWeight,
            color = color,
            modifier = modifier
        )
        return
    }

    AnimatedContent(
        targetState = valueText,
        transitionSpec = {
            (slideInVertically { height -> height / 2 } + fadeIn(AimlyMotionSpecs.fastTween())) togetherWith
                    (slideOutVertically { height -> -height / 2 } + fadeOut(AimlyMotionSpecs.fastTween())) using
                    SizeTransform(clip = false)
        },
        label = "AnimatedCounter"
    ) { target ->
        Text(
            text = target,
            style = textStyle,
            fontWeight = fontWeight,
            color = color,
            modifier = modifier
        )
    }
}

/**
 * Reusable bouncy goal checkmark icon component.
 */
@Composable
fun AnimatedCheckmarkIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = SuccessGreen
) {
    val isReducedMotion = rememberReducedMotion()

    val scale by animateFloatAsState(
        targetValue = if (checked) 1.0f else 0.0f,
        animationSpec = if (isReducedMotion) snap() else AimlyMotionSpecs.CheckmarkSpring,
        label = "CheckmarkScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(AimlyMotionSpecs.fastTween()),
            exit = fadeOut(AimlyMotionSpecs.fastTween())
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = tint,
                modifier = Modifier
                    .size(size)
                    .scale(scale)
            )
        }
    }
}

/**
 * Staggered Entrance animation container for list and grid items.
 */
@Composable
fun StaggeredItemEntrance(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isReducedMotion = rememberReducedMotion()
    var isVisible by remember { mutableStateOf(isReducedMotion) }

    LaunchedEffect(Unit) {
        if (!isReducedMotion) {
            val delayMs = (index * 45L).coerceAtMost(300L)
            delay(delayMs)
            isVisible = true
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = AimlyMotionSpecs.contentTween(),
        label = "ItemAlpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 24f,
        animationSpec = AimlyMotionSpecs.contentTween(),
        label = "ItemOffsetY"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = offsetY
        }
    ) {
        content()
    }
}

/**
 * Reusable streak badge pulse animation container (infinite subtle pulse for active streak).
 */
@Composable
fun StreakBadgePulseContainer(
    isHighStreak: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    val isReducedMotion = rememberReducedMotion()

    if (!isHighStreak || isReducedMotion) {
        content(modifier)
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "StreakPulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScaleAnim"
    )

    content(modifier.scale(pulseScale))
}
