package dev.nightatlas.app.fieldkit

import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.nightatlas.app.Shelf
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

internal enum class FieldKitExample(val shelf: Shelf, val label: String) {
    RouteEditor(Shelf.Routes, "Try a route"),
    Lyra(Shelf.Routes, "Open Lyra by service ID"),
    OrionCard(Shelf.Routes, "Open Orion from a JSON card"),
    CassiopeiaAlias(Shelf.Routes, "Open Cassiopeia by its legacy alias"),
    GuideHub(Shelf.Routes, "Open the guide through the hub"),
    LogObservation(Shelf.Routes, "Log an observation via deep link"),
    LegacyStar(Shelf.Routes, "Try a legacy service link"),
    Tonight(Shelf.Routes, "Open tonight’s target by service"),
    GuideTab(Shelf.Routes, "Open the guide tab"),
    UnknownService(Shelf.Routes, "Try an unknown service"),
    OfflineGuide(Shelf.Android, "Read the offline guide"),
    NasaWebsite(Shelf.Android, "Read about stars on NASA’s website"),
    BrowserService(Shelf.Android, "Open a browser by service ID"),
    GuideService(Shelf.Android, "Open the offline guide by service ID"),
    Dialer(Shelf.Android, "Open the dialer with a sample number"),
    AppSettings(Shelf.Android, "App settings"),
    BiometricSettings(Shelf.Android, "Biometric settings"),
    PendingRequests(Shelf.Android, "Pending requests"),
    GuideIntent(Shelf.Android, "Open the guide via intent"),
    StartTrail(Shelf.Stack, "Start at stop 1"),
}

@Composable
internal fun FieldKitScreen(onBack: () -> Unit, onExampleSelected: (FieldKitExample) -> Unit) {
    var shelf by rememberSaveable { mutableStateOf(Shelf.Routes) }
    Page("Field kit", onBack) {
        PrimaryTabRow(selectedTabIndex = shelf.ordinal) {
            Shelf.entries.forEach { tab ->
                Tab(selected = tab == shelf, onClick = { shelf = tab }, text = { Text(tab.title) })
            }
        }
        if (shelf == Shelf.Stack) {
            Text(
                "Visit the three stops in order. At the third stop, choose a back stack action and see where you land."
            )
        }
        FieldKitExample.entries.filter { it.shelf == shelf }.forEach { example ->
            ActionRow(example.label, { onExampleSelected(example) })
        }
    }
}
