// app/src/main/java/co/id/scanberry/scanberryv2app/ui/screens/GambarScreen.kt
package co.id.scanberry.scanberryv2app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import co.id.scanberry.scanberryv2app.ui.components.DetectionOverlay
import co.id.scanberry.scanberryv2app.util.FileUtil
import co.id.scanberry.scanberryv2app.viewmodel.ClassifierViewModel
import co.id.scanberry.scanberryv2app.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GambarScreen(nav: NavController) {
    val vm: ClassifierViewModel = hiltViewModel()
    val detections by vm.detections.collectAsState()
    val loading    by vm.loading.collectAsState()
    var uri by remember { mutableStateOf<Uri?>(null) }
    var imgSize by remember { mutableStateOf(300f to 300f) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(GetContent()) { sel ->
        sel?.let {
            uri = it
            FileUtil.from(context, it)?.let(vm::classifyImage)
        }
    }

    Scaffold(
        topBar = {
            if (uri != null) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.classification_result)) },
                    navigationIcon = {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor    = Color(0xFFD32F2F),
                        titleContentColor = Color.White
                    )
                )
            }
        },
        content = { p ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(p)
                    .background(Color(0xFF212121))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement   = if (uri == null) Arrangement.Center else Arrangement.Top
            ) {
                if (uri == null) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(text = stringResource(R.string.select_image), color = Color.White)
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        border = BorderStroke(2.dp, Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(uri)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.matchParentSize(),
                                onSuccess = { success ->
                                    imgSize = success.result.drawable.intrinsicWidth.toFloat() to
                                            success.result.drawable.intrinsicHeight.toFloat()
                                }
                            )
                            if (detections.isNotEmpty()) {
                                val (w,h) = imgSize
                                DetectionOverlay(detections, w, h)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(text = stringResource(R.string.result_label), color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Column(Modifier.fillMaxWidth()) {
                        // hitung per kelas
                        val counts = detections.groupingBy { it.classId }.eachCount()
                        @Composable
                        fun label(id:Int) = when(id){
                            0-> stringResource(R.string.label_unripe);1->stringResource(R.string.label_fully_ripe_a);2->stringResource(R.string.label_fully_ripe_b);3->stringResource(R.string.label_half_ripe_a);4->stringResource(R.string.label_half_ripe_b);else->stringResource(R.string.label_unknown)
                        }
                        counts.forEach { (id,c) ->
                            Text("• ${label(id)} = $c ${stringResource(R.string.unit_fruit)}", color=Color.White)
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { nav.popBackStack() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(text = stringResource(R.string.back), color = Color.White)
                    }
                }
            }
        }
    )
}
