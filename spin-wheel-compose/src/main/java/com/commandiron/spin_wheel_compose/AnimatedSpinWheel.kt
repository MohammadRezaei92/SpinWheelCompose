package com.commandiron.spin_wheel_compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.commandiron.spin_wheel_compose.state.SpinWheelState
import com.commandiron.spin_wheel_compose.state.rememberSpinWheelState

@Composable
internal fun AnimatedSpinWheel(
    modifier: Modifier,
    state: SpinWheelState = rememberSpinWheelState(),
    onClick: () -> Unit,
    onFinish: (resultIndex: Int) -> Unit,
    content: @Composable BoxScope.(pieIndex: Int) -> Unit
){

    SpinWheelSelector(
        modifier = modifier,
        frameSize = state.size,
        pieCount = state.pieCount,
        selectorWidth = state.selectorWidth,
        selectorColor = state.selectorColor,
        rotationDegree = 0f
    ) {
        SpinWheelFrame(
            modifier = modifier,
            frameSize = state.size - state.selectorWidth,
            pieCount = state.pieCount,
            frameWidth = state.frameWidth,
            frameColor = state.frameColor,
            dividerColor =  state.dividerColor,
            rotationDegree = 0f,
            onClick = onClick,
        ) {
            SpinWheelPies(
                modifier = modifier,
                spinSize = state.size - state.frameWidth - state.selectorWidth,
                pieCount = state.pieCount,
                pieColors = state.pieColors,
                rotationDegree = 0f,
                onClick = onClick
            ){
                SpinWheelContent(
                    modifier = modifier,
                    spinSize = state.size - state.frameWidth - state.selectorWidth,
                    pieCount = state.pieCount,
                    rotationDegree = 0f
                ){
                    content(it)
                }
            }
        }
    }
}
