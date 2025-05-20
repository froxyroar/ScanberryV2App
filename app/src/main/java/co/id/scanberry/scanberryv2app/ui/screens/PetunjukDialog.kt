package co.id.scanberry.scanberryv2app.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.id.scanberry.scanberryv2app.R
import co.id.scanberry.scanberryv2app.ui.theme.StrawberryRedDark

@Composable
fun PetunjukDialog(onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onClose,
        shape = MaterialTheme.shapes.medium,
        containerColor = colors.primary,
        tonalElevation = 8.dp,
        title = {
            Text(
                text = stringResource(R.string.dlg_instructions_title),
                color = colors.onPrimary,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dlg_instructions_text),
                color = colors.onPrimary,
                fontSize = 16.sp,
                lineHeight =  20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StrawberryRedDark,
                    contentColor = colors.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.close),
                    fontSize = 16.sp
                )
            }
        }
    )
}
