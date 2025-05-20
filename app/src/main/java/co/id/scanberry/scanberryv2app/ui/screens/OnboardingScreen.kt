package co.id.scanberry.scanberryv2app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import co.id.scanberry.scanberryv2app.R
import co.id.scanberry.scanberryv2app.viewmodel.SettingsViewModel

@Composable
fun OnboardingScreen(
    nav: NavController,
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    // Data class dengan stringResource
    data class Page(
        val titleRes: Int,
        val descRes: Int,
        val buttonRes: Int
    )

    val pages = listOf(
        Page(
            titleRes = R.string.onb_title_1,
            descRes = R.string.onb_desc_1,
            buttonRes = R.string.next
        ),
        Page(
            titleRes = R.string.onb_title_2,
            descRes = R.string.onb_desc_2,
            buttonRes = R.string.next
        ),
        Page(
            titleRes = R.string.onb_title_3,
            descRes = R.string.onb_desc_3,
            buttonRes = R.string.start
        )
    )
    var idx by remember { mutableIntStateOf(0) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Konten utama di-center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = pages[idx].titleRes),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(id = pages[idx].descRes),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Indikator titik
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.forEachIndexed { i, _ ->
                Box(
                    Modifier
                        .size(if (i == idx) 12.dp else 8.dp)
                        .background(
                            color = if (i == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.3f
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
                if (i < pages.lastIndex) Spacer(Modifier.width(8.dp))
            }
        }

        // Tombol navigasi
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (idx > 0) {
                TextButton(onClick = { idx-- }) {
                    Text(stringResource(R.string.back))
                }
            } else {
                Spacer(Modifier.width(64.dp))
            }

            Button(
                onClick = {
                    if (idx < pages.lastIndex) idx++ else {
                        settingsVm.setFirstRun(false)
                        nav.navigate("home") { popUpTo("onboarding") { inclusive = true } }
                    }
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(id = pages[idx].buttonRes))
            }
        }
    }
}
