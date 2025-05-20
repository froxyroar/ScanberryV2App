package co.id.scanberry.scanberryv2app.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import co.id.scanberry.scanberryv2app.R
import co.id.scanberry.scanberryv2app.ui.components.DetectionOverlay
import co.id.scanberry.scanberryv2app.util.BitmapUtils
import co.id.scanberry.scanberryv2app.viewmodel.ClassifierViewModel
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealtimeScreen(nav: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val vm: ClassifierViewModel = hiltViewModel()
    val detections by vm.detections.collectAsState()
    val loading by vm.loading.collectAsState()
    val scope = rememberCoroutineScope()
    val rootView = LocalView.current

    // PreviewView must be remembered in a composable context
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    var inferenceTime by remember { mutableStateOf(0L) }
    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(cameraPermissionGranted) {
        if (cameraPermissionGranted) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()

            val previewUseCase = Preview.Builder()
                .build()
                .apply { setSurfaceProvider(previewView.surfaceProvider) }

            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context)
                    ) { imageProxy ->
                        val part = BitmapUtils.toMultipartPart(context, imageProxy, "file")
                        if (part != null && !loading) {
                            scope.launch {
                                val start = System.currentTimeMillis()
                                vm.classifyPart(part, imageProxy.imageInfo.rotationDegrees)
                                val end = System.currentTimeMillis()
                                inferenceTime = end - start
                            }
                        }
                        imageProxy.close()
                    }
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                previewUseCase,
                analysisUseCase
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.realtime_title)) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFD32F2F),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFFD32F2F))
            ) {
                Box(Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.matchParentSize()
                    )
                    if (detections.isNotEmpty()) {
                        DetectionOverlay(
                            detections = detections,
                            imageWidth = 640f,
                            imageHeight = 640f,
                            contentScaleFit = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.label_counts),
                style = MaterialTheme.typography.titleMedium
            )

            Column(Modifier.fillMaxWidth()) {
                val counts = detections.groupingBy { it.classId }.eachCount()
                listOf(0, 1, 2, 3, 4).forEach { id ->
                    val label = when (id) {
                        0 -> stringResource(R.string.label_unripe)
                        1 -> stringResource(R.string.label_fully_ripe_a)
                        2 -> stringResource(R.string.label_fully_ripe_b)
                        3 -> stringResource(R.string.label_half_ripe_a)
                        4 -> stringResource(R.string.label_half_ripe_b)
                        else -> stringResource(R.string.label_unknown)
                    }
                    val count = counts[id] ?: 0
                    Text(
                        "• $label = $count ${stringResource(R.string.unit_fruit)}",
                        Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Inference time: ${inferenceTime} ms",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val bmp: Bitmap = rootView.drawToBitmap()
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val filename = "ScanBerryV2_$timestamp.jpg"
                    val cv = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ScanBerryV2")
                        }
                    }
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cv
                    )!!
                    context.contentResolver.openOutputStream(uri).use { out: OutputStream? ->
                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out!!)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text(text = stringResource(R.string.snapshot_save), color = Color.White)
            }
        }
    }

    BackHandler { nav.popBackStack() }
}
