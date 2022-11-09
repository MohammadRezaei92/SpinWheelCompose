package com.commandiron.spin_wheel_compose.state

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import java.util.*

data class SpinWheelState(
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

    suspend fun spinToReset() {
        when(spinAnimationState) {
            SpinAnimationState.STOPPED -> {
                spin()
            }
            SpinAnimationState.SPINNING -> {
                reset()
            }
        }
    }

    suspend fun spin() {
        if(spinAnimationState == SpinAnimationState.STOPPED) {

            spinAnimationState = SpinAnimationState.SPINNING

            val randomRotationDegree = generateRandomRotationDegree()

            rotation.animateTo(
                targetValue = (360f * rotationPerSecond * (durationMillis / 1000)) + (resultDegree ?:  randomRotationDegree),
                animationSpec = tween(
                    durationMillis = durationMillis,
                    delayMillis = delayMillis,
                    easing = easing
                )
            )
            rotation.snapTo(randomRotationDegree)

            spinAnimationState = SpinAnimationState.STOPPED

            autoSpinDelay?.let {
                delay(it)
                spin()
            }
        }
    }

    suspend fun reset() {
        if(spinAnimationState == SpinAnimationState.SPINNING) {

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
