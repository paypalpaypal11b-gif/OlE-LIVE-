package com.example.service

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class StreamingProvider(val displayName: String, val protocol: String, val latency: String) {
    AGORA_RTC("Agora Interactive Live Streaming", "WebRTC / RTC", "80-150 ms (Ultra Low)"),
    LIVEKIT("LiveKit Cloud RTC", "WebRTC / SFU", "100-200 ms"),
    AWS_IVS("Amazon Interactive Video Service", "LL-HLS / RTMPS", "1.5 - 3.0 s"),
    RTMP_CUSTOM("Self-Hosted SRS / Nginx RTMP", "RTMP / HLS", "2.0 - 5.0 s")
}

data class StreamQualityMetrics(
    val provider: StreamingProvider = StreamingProvider.AGORA_RTC,
    val bitrateKbps: Int = 2450,
    val fps: Int = 60,
    val resolution: String = "1080x1920 (Vertical HD)",
    val packetLossPct: Double = 0.05,
    val latencyMs: Int = 118,
    val isMicrophoneMuted: Boolean = false,
    val isTorchOn: Boolean = false,
    val isBeautyFilterEnabled: Boolean = true,
    val isFrontCamera: Boolean = true
)

class StreamingEngine {

    private val _metrics = MutableStateFlow(StreamQualityMetrics())
    val metrics: StateFlow<StreamQualityMetrics> = _metrics.asStateFlow()

    private var metricsJob: Job? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startBroadcastSession(scope: CoroutineScope, channelId: String) {
        metricsJob?.cancel()
        metricsJob = scope.launch(Dispatchers.Default) {
            while (true) {
                delay(2000)
                // Minor realistic jitter in broadcast telemetry
                val jitterBitrate = (2350..2550).random()
                val jitterLatency = (105..135).random()
                val jitterLoss = (2..8).random() / 100.0
                _metrics.value = _metrics.value.copy(
                    bitrateKbps = jitterBitrate,
                    latencyMs = jitterLatency,
                    packetLossPct = jitterLoss
                )
            }
        }
    }

    fun stopBroadcastSession() {
        metricsJob?.cancel()
        cameraProvider?.unbindAll()
    }

    fun toggleCameraFacing() {
        _metrics.value = _metrics.value.copy(isFrontCamera = !_metrics.value.isFrontCamera)
    }

    fun toggleMicrophone() {
        _metrics.value = _metrics.value.copy(isMicrophoneMuted = !_metrics.value.isMicrophoneMuted)
    }

    fun toggleBeautyFilter() {
        _metrics.value = _metrics.value.copy(isBeautyFilterEnabled = !_metrics.value.isBeautyFilterEnabled)
    }

    fun switchProvider(provider: StreamingProvider) {
        _metrics.value = _metrics.value.copy(provider = provider)
    }

    /**
     * Binds CameraX PreviewView to Android lifecycle.
     */
    fun bindCameraPreview(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onCameraReady: (Boolean) -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val cameraSelector = if (_metrics.value.isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
                onCameraReady(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onCameraReady(false)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    companion object {
        fun generateStreamToken(channel: String, role: String): String {
            return "ole_token_v2_${role.lowercase()}_${UUID.randomUUID().toString().take(12)}"
        }
    }
}
