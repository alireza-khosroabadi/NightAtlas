package dev.nightatlas.feature.journal.navigation

import dev.nightatlas.feature.journal.Section
import dev.nightatlas.navigation.feature.FeatureNavigator
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.DestinationType
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.JournalInput

internal fun Section.selectAction() = ActionEntity.Tab(key)

internal class JournalNavigator(
    private val openAction: (ActionEntity) -> Unit,
) : FeatureNavigator<JournalInput, Section?> {
    override fun handleAction(action: ActionEntity): Section? {
        val section = when (action) {
            is ActionEntity.Tab -> Section.match(action.destination)
            is ActionEntity.BottomBar ->
                if (action.destination.equals("journal", true)) Section.match(action.tabScreen) else null
            is ActionEntity.Service -> localSection(action)
            is ActionEntity.Link -> localSection(action.mapped as? ActionEntity.Service)
            is ActionEntity.UnsupportedService ->
                if (action.type == DestinationType.DEEP_LINK) Section.match(action.destination) else null
            else -> null
        }
        if (section == null && action !is ActionEntity.Tab) openAction(action)
        return section
    }

    private fun localSection(action: ActionEntity.Service?): Section? {
        val request = action?.request ?: return null
        val input = AtlasServices.Journal.inputOf(request) ?: return null
        return Section.entries.find { it.serviceInput == input }
    }

    override fun openFromService(input: JournalInput) =
        handleAction(ActionEntity.Service(AtlasServices.Journal(input)))
}
