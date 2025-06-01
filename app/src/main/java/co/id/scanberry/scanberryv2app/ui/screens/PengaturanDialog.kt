package co.id.scanberry.scanberryv2app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.id.scanberry.scanberryv2app.R
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel

@Composable
fun PengaturanDialog(
    vm: SettingsViewModel,
    onClose: () -> Unit
) {
    val currentDark by vm.isDarkMode.collectAsState()
    val currentLang by vm.language.collectAsState()

    var tempDark by remember { mutableStateOf(currentDark) }
    var tempLang by remember { mutableStateOf(currentLang) }

    LaunchedEffect(currentDark, currentLang) {
        tempDark = currentDark
        tempLang = currentLang
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onClose)
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFe74c3c))
                .padding(24.dp)
                .clickable(enabled = false) { /* Consume click */ }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onClose)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconToggleButton(
                            checked = tempLang == "en",
                            onCheckedChange = { isChecked -> if (isChecked) tempLang = "en" },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_english),
                                contentDescription = stringResource(R.string.language_english),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                        IconToggleButton(
                            checked = tempLang == "id",
                            onCheckedChange = { isChecked -> if (isChecked) tempLang = "id" },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_indonesia),
                                contentDescription = stringResource(R.string.language_indonesia),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (tempLang == "id")
                            stringResource(R.string.language_indonesia)
                        else
                            stringResource(R.string.language_english),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.dark_light_mode),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                    Switch(
                        checked = tempDark,
                        onCheckedChange = { tempDark = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White,
                            uncheckedThumbColor = Color.LightGray,
                            checkedTrackColor   = Color(0xFF2c3e50),
                            uncheckedTrackColor = Color(0xFF95a5a6)
                        )
                    )
                }
                Button(
                    onClick = {
                        vm.setLanguage(tempLang)
                        vm.setDarkMode(tempDark)
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFc0392b)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        color = Color.White
                    )
                }
            }
        }
    }
}