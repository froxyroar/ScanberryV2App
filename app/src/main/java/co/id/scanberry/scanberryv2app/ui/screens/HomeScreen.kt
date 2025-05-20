package co.id.scanberry.scanberryv2app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.id.scanberry.scanberryv2app.R
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    vm: SettingsViewModel? = null,
    isDarkMode: Boolean,
) {
    val isPreview = LocalInspectionMode.current
    val viewModel: SettingsViewModel? =
        if (isPreview) vm else vm ?: hiltViewModel()

    var showPetunjuk by remember { mutableStateOf(false) }
    var showPengaturan by remember { mutableStateOf(false) }
    var showPilih by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.app_name),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    val logo = if (isDarkMode) R.drawable.ic_strawberry_night else R.drawable.ic_strawberry
                    IconButton(onClick = {}) {
                        Image(
                            painter = painterResource(logo),
                            contentDescription = "Logo"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showPengaturan = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_setting),
                            contentDescription = "Pengaturan"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .clickable { showPilih = true },
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera),
                            contentDescription = "Mulai Klasifikasi",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showPetunjuk = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(32.dp)
                        .fillMaxWidth(0.6f)
                        .height(48.dp),
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = stringResource(R.string.instructions),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )

    if (showPetunjuk) {
        PetunjukDialog { showPetunjuk = false }
    }
    if (showPengaturan && viewModel != null) {
        PengaturanDialog(viewModel) { showPengaturan = false }
    }
    if (showPilih) {
        PilihDialog(
            onCamera = {
                showPilih = false
                nav.navigate("realtime")
            },
            onGallery = {
                showPilih = false
                nav.navigate("gambar")
            },
            onClose = { showPilih = false }
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF0F0F0,
    name = "HomeScreen Preview"
)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        nav = rememberNavController(),
        vm = null,
        isDarkMode = false
    )
}