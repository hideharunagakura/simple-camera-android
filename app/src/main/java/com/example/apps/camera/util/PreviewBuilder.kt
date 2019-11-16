package com.example.apps.camera.util

import android.graphics.Matrix
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import java.lang.ref.WeakReference


class PreviewBuilder private constructor(config: PreviewConfig, viewFinderRef: WeakReference<TextureView>) {

    val useCase: Preview

    init {
        // Make sure that the view finder reference is valid
        val viewFinder = viewFinderRef.get() ?: throw IllegalArgumentException("Invalid reference to view finder used")

        // Initialize public use-case with the given config
        useCase = Preview(config)

        // Every time the viewfinder is updated, recompute layout
        useCase.setOnPreviewOutputUpdateListener {
            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            // Update internal texture
            viewFinder.surfaceTexture = it.surfaceTexture

            // Apply relevant transformations
            updateTransform(viewFinder)
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform(viewFinder)
        }
    }

    private fun updateTransform(textureView: TextureView) {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        textureView.setTransform(matrix)
    }

    companion object {

        fun build(config: PreviewConfig, viewFinder: TextureView) =
                PreviewBuilder(config, WeakReference(viewFinder)).useCase
    }


}
