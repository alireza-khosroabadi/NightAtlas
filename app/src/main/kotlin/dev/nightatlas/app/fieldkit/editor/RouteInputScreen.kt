package dev.nightatlas.app.fieldkit.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nightatlas.app.InputKind
import dev.nightatlas.ui.components.Page

@Composable
internal fun RouteInputScreen(
    onSubmit: (SubmittedInput) -> Unit,
    onBack: () -> Unit,
    viewModel: RouteInputViewModel = hiltViewModel(key = RouteInputViewModel.SAVED_STATE_KEY),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Page("Try a route", onBack) {
        PrimaryTabRow(selectedTabIndex = state.kind.ordinal) {
            InputKind.entries.forEach { item ->
                Tab(
                    selected = item == state.kind,
                    onClick = { viewModel.selectKind(item) },
                    text = { Text(item.title) },
                )
            }
        }
        OutlinedTextField(
            value = state.value,
            onValueChange = viewModel::changeValue,
            label = { Text("Input") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 6,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            isError = state.invalidJson && state.kind == InputKind.Action,
            supportingText = jsonError(state.invalidJson && state.kind == InputKind.Action),
        )
        if (state.kind == InputKind.Service)
            OutlinedTextField(
                value = state.parameters,
                onValueChange = viewModel::changeParameters,
                label = { Text("Service parameters (JSON)") },
                isError = state.invalidJson,
                supportingText = jsonError(state.invalidJson),
                modifier = Modifier.fillMaxWidth(),
            )
        Button(enabled = state.canSubmit, onClick = { viewModel.submit()?.let(onSubmit) }) {
            Text("Open destination")
        }
    }
}

private fun jsonError(visible: Boolean): (@Composable () -> Unit)? =
    if (visible) {
        { Text("Enter valid JSON for this request.") }
    } else null
