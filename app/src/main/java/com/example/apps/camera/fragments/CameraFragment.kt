package com.example.apps.camera.fragments

import android.os.Bundle
import android.util.Size
import android.view.*
import androidx.camera.core.CameraX
import androidx.camera.core.PreviewConfig
import androidx.fragment.app.Fragment
import com.example.apps.camera.R
import com.example.apps.camera.util.PreviewBuilder
import java.util.concurrent.Executors


class CameraFragment : Fragment() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_camera, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        viewFinder = view.findViewById(R.id.view_finder)
        viewFinder.post { startCamera() }
    }

    private fun startCamera() {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(viewFinder.width, viewFinder.width))
        }.build()

        // Build the viewfinder use case
        val preview = PreviewBuilder.build(previewConfig, viewFinder)

        CameraX.bindToLifecycle(this, preview)
    }


}
