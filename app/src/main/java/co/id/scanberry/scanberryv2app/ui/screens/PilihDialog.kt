package co.id.scanberry.scanberryv2app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.id.scanberry.scanberryv2app.R

@Composable
fun PilihDialog(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.dlg_choose_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Realtime Detection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCamera)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_realtime),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = stringResource(R.string.dlg_realtime), style = MaterialTheme.typography.bodyLarge)
                }

                Divider()

                // Upload Gambar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onGallery)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_image),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = stringResource(R.string.dlg_upload), style = MaterialTheme.typography.bodyLarge)
                }

                Divider(modifier = Modifier.padding(top = 16.dp))

                // Tombol Kembali
                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.back))
                }
            }
        }
    }
}
