package co.id.scanberry.scanberryv2app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import co.id.scanberry.scanberryv2app.R
import co.id.scanberry.scanberryv2app.ui.components.DetectionOverlay
import co.id.scanberry.scanberryv2app.util.FileUtil
import co.id.scanberry.scanberryv2app.viewmodel.ClassifierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GambarScreen(nav: NavController) {
    val vm: ClassifierViewModel = hiltViewModel()
    val detections by vm.detections.collectAsState()
    val loading by vm.loading.collectAsState()
    var uri by remember { mutableStateOf<Uri?>(null) }
    var imgSize by remember { mutableStateOf(300f to 300f) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(GetContent()) { sel ->
        sel?.let {
            uri = it
            FileUtil.from(context, it).let(vm::classifyImage)
        }
    }

    Scaffold(
        topBar = {
            if (uri != null) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.classification_result)) },
                    navigationIcon = {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        content = { p ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(p)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (uri == null) Arrangement.Center else Arrangement.Top
            ) {
                if (uri == null) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.select_image),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(uri)
                                    .crossfade(true)
                                    .build(),
                                loading = {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                },
                                error = {
                                    Icon(
                                        Icons.Filled.Warning,
                                        "Error loading image",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                contentDescription = "pilih gambar.",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.matchParentSize(),
                                onSuccess = { success ->
                                    imgSize = success.result.drawable.intrinsicWidth.toFloat() to
                                            success.result.drawable.intrinsicHeight.toFloat()
                                }
                            )
                            if (detections.isNotEmpty()) {
                                val (w, h) = imgSize
                                if (w > 0f && h > 0f) {
                                    DetectionOverlay(
                                        detections = detections,
                                        imageWidth = w,
                                        imageHeight = h
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.result_label),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(top = 16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(
                        visible = !loading && detections.isEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.errorContainer,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.no_strawberry_detected),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = stringResource(R.string.try_another_image),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    if (detections.isNotEmpty()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            val counts = detections.groupingBy { it.classId }.eachCount()
                            var totalStrawberries = 0 // Initialize total count

                            @Composable
                            fun label(id: Int) = when (id) {
                                0 -> stringResource(R.string.label_fully_ripe_a)
                                1 -> stringResource(R.string.label_fully_ripe_b)
                                2 -> stringResource(R.string.label_half_ripe_a)
                                3 -> stringResource(R.string.label_half_ripe_b)
                                4 -> stringResource(R.string.label_unripe)
                                else -> stringResource(R.string.label_unknown)
                            }
                            counts.forEach { (id, c) ->
                                totalStrawberries += c // Accumulate the count
                                Text(
                                    "• ${label(id)} = $c ${stringResource(R.string.unit_fruit)}",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            // Display the total number of classified strawberries
                            Text(
                                text = "${stringResource(R.string.total_label)} $totalStrawberries ${stringResource(R.string.unit_fruit)}.",
                                style = MaterialTheme.typography.titleMedium, // Memberikan penekanan pada total
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp) // Padding untuk tampilan
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { nav.popBackStack() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.back),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    )
}
