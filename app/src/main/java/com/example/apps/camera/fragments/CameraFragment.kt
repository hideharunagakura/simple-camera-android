package com.example.apps.camera.fragments

import android.os.Bundle
import android.util.Size
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.PreviewConfig
import androidx.fragment.app.Fragment
import com.example.apps.camera.R
import com.example.apps.camera.util.PreviewBuilder
import java.io.File
import java.util.concurrent.Executors


class CameraFragment : Fragment() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    private var imageCapture: ImageCapture? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_camera, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        viewFinder = view.findViewById(R.id.view_finder)
        viewFinder.post { startCamera() }

        view.findViewById<ImageButton>(R.id.capture_button).setOnClickListener {
            takePicture()
        }
    }

    private fun startCamera() {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(viewFinder.width, viewFinder.width))
        }.build()

        // Build the viewfinder use case
        val preview = PreviewBuilder.build(previewConfig, viewFinder)

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        imageCapture = ImageCapture(imageCaptureConfig)

        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun takePicture() {
        val file = File(requireContext().externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

        imageCapture?.takePicture(file, executor, object : ImageCapture.OnImageSavedListener {
            override fun onError(imageCaptureError: ImageCapture.ImageCaptureError, message: String, exc: Throwable?) {
                val msg = "Photo capture failed: $message"
                viewFinder.post {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onImageSaved(file: File) {
                val msg = "Photo capture succeeded: ${file.absolutePath}"
                viewFinder.post {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}
