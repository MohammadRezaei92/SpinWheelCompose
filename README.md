# SpinWheelCompose
Spinwheel in Android using Jetpack Compose.

## How it looks
<img src="https://user-images.githubusercontent.com/50905347/178328848-39908123-1128-479c-8f5a-56f5643050da.gif" width="250" height="530">

## Usage
```kotlin  
var isSpinning by remember { mutableStateOf(false)}
DefaultSpinWheel(
    isSpinning = isSpinning,
    resultDegree = resultDegree,
    onClick = {
        if(!isSpinning){
            resultDegree = Random().nextInt(360).toFloat()
        }
        isSpinning = !isSpinning
    },
    onFinish = {
        isSpinning = false
    }
)
```

## Setup