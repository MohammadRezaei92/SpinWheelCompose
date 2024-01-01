package com.commandiron.spin_wheel_compose.state

import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.util.Random

data class SpinWheelState(
    internal val pieCount: Int,
    private val durationMillis: Int,
    private val delayMillis: Int,
    private val rotationPerSecond: Float,
    private val easing: Easing,
    private val startDegree: Float,
    private val resultDegree: Float? = null,
    internal val autoSpinDelay: Long? = null,
) {
    internal var rotation by mutableStateOf(Animatable(startDegree))
    private var spinAnimationState by mutableStateOf(SpinAnimationState.STOPPED)
    private val total = (360f * rotationPerSecond * (durationMillis / 1000))

    suspend fun animate(onFinish: (pieIndex: Int) -> Unit = {}) {
        when (spinAnimationState) {
            SpinAnimationState.STOPPED -> {
                randomSpin(onFinish)
            }

            SpinAnimationState.SPINNING -> {
                reset()
            }
        }
    }

    suspend fun randomSpin(onFinish: (pieIndex: Int) -> Unit = {}) {
        if (spinAnimationState == SpinAnimationState.STOPPED) {

            spinAnimationState = SpinAnimationState.SPINNING

            val randomRotationDegree = generateRandomRotationDegree()

            rotation.animateTo(
                targetValue = (360f * rotationPerSecond * (durationMillis / 1000)) + (resultDegree
                    ?: randomRotationDegree),
                animationSpec = tween(
                    durationMillis = durationMillis,
                    delayMillis = delayMillis,
                    easing = easing
                )
            )

            val pieDegree = 360f / pieCount
            val quotient = (resultDegree ?: randomRotationDegree).toInt() / pieDegree.toInt()
            val resultIndex = pieCount - quotient - 1

            onFinish(resultIndex)

            rotation.snapTo(resultDegree ?: randomRotationDegree)

            spinAnimationState = SpinAnimationState.STOPPED

            autoSpinDelay?.let {
                delay(it)
                randomSpin()
            }
        }
    }

    suspend fun infiniteSpin() {
        if (spinAnimationState == SpinAnimationState.STOPPED) {

            spinAnimationState = SpinAnimationState.SPINNING

            rotation.animateTo(
                targetValue = total,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durationMillis,
                        delayMillis = delayMillis,
                        easing = LinearEasing
                    )
                )
            )

            autoSpinDelay?.let {
                delay(it)
                infiniteSpin()
            }
        }
    }

    suspend fun stopOn(itemIndex: Int) {
        if (spinAnimationState == SpinAnimationState.SPINNING) {

            val pieDegree = 360f / pieCount
            val itemDegree = startDegree - (pieDegree * itemIndex).plus(pieDegree.div(2))

            rotation.animateTo(
                initialVelocity = rotation.velocity,
                targetValue = total + itemDegree,
                animationSpec = tween(
                    durationMillis = durationMillis,
                    delayMillis = 0,
                    easing = easing
                )
            )

            rotation.snapTo(itemDegree)

            spinAnimationState = SpinAnimationState.STOPPED
        }
    }

    suspend fun reset() {
        if (spinAnimationState == SpinAnimationState.SPINNING) {

            rotation.snapTo(startDegree)

            spinAnimationState = SpinAnimationState.STOPPED
        }
    }

    private fun generateRandomRotationDegree(): Float {
        return Random().nextInt(360).toFloat()
    }
}

enum class SpinAnimationState {
    STOPPED, SPINNING
}

@Composable
fun rememberSpinWheelState(
    @IntRange(from = 2, to = 8) pieCount: Int = 8,
    durationMillis: Int = 12000,
    delayMillis: Int = 0,
    rotationPerSecond: Float = 1f,
    easing: Easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f),
    startDegree: Float = 0f,
    resultDegree: Float? = null,
    autoSpinDelay: Long? = null
): SpinWheelState {
    return remember {
        SpinWheelState(
            pieCount,
            durationMillis,
            delayMillis,
            rotationPerSecond,
            easing,
            startDegree,
            resultDegree,
            autoSpinDelay
        )
    }
}
