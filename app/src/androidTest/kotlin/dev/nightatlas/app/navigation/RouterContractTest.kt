package dev.nightatlas.app.navigation

import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.nightatlas.app.MainActivity
import dev.nightatlas.app.fieldkit.LEGACY_KIT_LINK
import dev.nightatlas.app.fieldkit.navigation.KitEntry
import dev.nightatlas.app.fieldkit.navigation.TrailMarker
import dev.nightatlas.app.fieldkit.navigation.Unavailable
import dev.nightatlas.navigation.deeplink.AtlasLinkMapper
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.DestinationType
import dev.nightatlas.navigation.model.FieldKitRoute
import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.registry.handler.ModelRouteHandler
import dev.nightatlas.navigation.registry.handler.RouteHandler
import dev.nightatlas.navigation.registry.handler.TypedRouteHandler
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.routing.NavigationContainer
import dev.nightatlas.navigation.service.invoke
import dev.nightatlas.navigation.service.serviceContract
import dev.nightatlas.navigation.stack.StackAction
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouterContractTest {
    @get:Rule val activity = ActivityScenarioRule(MainActivity::class.java)

    private fun withRouter(
        registry: NavigationRegistry =
            NavigationRegistry().apply { KitEntry().registerNavigation(this) },
        check: (NavHostController, NavigationContainer) -> Unit,
    ) {
        activity.scenario.onActivity { host ->
            val controller = NavHostController(host)
            controller.navigatorProvider.addNavigator(ComposeNavigator())
            controller.graph =
                controller.createGraph(startDestination = CatalogTab) {
                    composable<CatalogTab> {}
                    composable<TrailMarker> {}
                    composable<Unavailable> {}
                    composable(
                        FieldKitRoute.name,
                        deepLinks = listOf(navDeepLink { uriPattern = LEGACY_KIT_LINK }),
                    ) {}
                }
            check(controller, createContainer(host, controller, registry))
        }
    }

    @Test
    fun popCountReportsHowFarItCouldActuallyGoAndPreservesTheRoot() =
        withRouter { controller, nav ->
            val root = controller.currentBackStackEntry
            nav.open(TrailMarker(1))
            nav.open(TrailMarker(2))
            assertEquals(NavigationResult.Unhandled, nav.open(StackAction.PopCount(0)))
            assertEquals(NavigationResult.Unhandled, nav.open(StackAction.PopCount(-1)))
            assertEquals(TrailMarker(2), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            assertEquals(NavigationResult.Handled, nav.open(StackAction.PopCount(1)))
            assertEquals(TrailMarker(1), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            assertFalse(nav.tryNavigate(StackAction.PopCount(2)))
            assertSame(root, controller.currentBackStackEntry)
            assertFalse(nav.tryNavigate(StackAction.PopCount(1)))
            assertSame(root, controller.currentBackStackEntry)
        }

    @Test
    fun replaceCurrentRecreatesOnlyTheTopEntryWithItsArguments() = withRouter { controller, nav ->
        nav.open(TrailMarker(1))
        val below = controller.currentBackStackEntry
        nav.open(TrailMarker(7))
        val old = controller.currentBackStackEntry
        assertEquals(NavigationResult.Handled, nav.open(StackAction.ReplaceCurrent))
        assertNotSame(old, controller.currentBackStackEntry)
        assertEquals(TrailMarker(7), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
        assertSame(below, controller.previousBackStackEntry)
        assertTrue(nav.tryNavigate(StackAction.Back))
        assertSame(below, controller.currentBackStackEntry)
    }

    @Test
    fun graphNavigatorCanReplaceNamedAndTypedEntriesInTheSameStack() = withRouter { controller, nav ->
        val graph =
            GraphNavigator(
                nav,
                onUnhandled = { fail("Unexpected destination failure") },
                onServiceUnhandled = { fail("Unexpected service failure") },
            )
        graph.openRoute(FieldKitRoute)
        graph.openRoute(TrailMarker(2))
        graph.execute(StackAction.popUpToAndNavigate(FieldKitRoute, TrailMarker(3), inclusive = true))
        assertEquals(TrailMarker(3), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
        assertEquals(CatalogTab, controller.previousBackStackEntry!!.toRoute<CatalogTab>())

        graph.execute(StackAction.popUpToAndNavigate<TrailMarker>(FieldKitRoute, inclusive = true))
        assertEquals(FieldKitRoute.name, controller.currentDestination!!.route)
        assertEquals(CatalogTab, controller.previousBackStackEntry!!.toRoute<CatalogTab>())
    }

    @Test
    fun registeredModelKeepsItsHandlerAndArgumentThroughGraphAndService() {
        val visit = ModelRouteHandler<Int> { TrailMarker(it) }
        val nextVisit = ModelRouteHandler<Int> { TrailMarker(it + 1) }
        val unknown = ModelRouteHandler<Int> { throw AssertionError("Unregistered mapper must not run") }
        val service = serviceContract<Int>("NEXT_VISIT")
        val registry = NavigationRegistry().apply {
            register(visit)
            register(nextVisit)
            registerService(service, decode = { it["visit"]?.toIntOrNull() }) { nextVisit.input(it) }
        }
        withRouter(registry) { controller, nav ->
            val graph = GraphNavigator(nav, { fail("Model route failed") }, { fail("Service failed") })
            graph.openRegistered(visit.input(3))
            assertEquals(TrailMarker(3), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            graph.openService(service(7))
            assertEquals(TrailMarker(8), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            val top = controller.currentBackStackEntry
            assertEquals(NavigationResult.Unhandled, nav.open(unknown.input(9)))
            assertSame(top, controller.currentBackStackEntry)
        }
    }

    @Test
    fun exactRouteRegistrationTakesPriorityOverMatchingCustomHandler() {
        val registry = NavigationRegistry().apply {
            register(object : RouteHandler {
                override fun canHandle(route: NavigationRoute) =
                    route is TrailMarker || route is Unavailable

                override fun navigate(navController: NavController, route: NavigationRoute) {
                    navController.navigate(TrailMarker(42))
                }
            })
            register(TypedRouteHandler(TrailMarker::class))
        }
        withRouter(registry) { controller, nav ->
            assertEquals(NavigationResult.Handled, nav.open(TrailMarker(5)))
            assertEquals(TrailMarker(5), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            assertEquals(NavigationResult.Handled, nav.open(Unavailable("x", "SERVICE")))
            assertEquals(TrailMarker(42), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
        }
    }

    @Test
    fun namedServiceTargetsUseRegisteredHandlersOrTheGraphRoute() {
        val shortcut = NamedRoute("shortcut")
        val service = serviceContract<Boolean>("SHORTCUT")
        val registry = NavigationRegistry().apply {
            registerString(shortcut) { controller, _ -> controller.navigate(TrailMarker(6)) }
            registerService(service, decode = { it["direct"] == "yes" }) { if (it) FieldKitRoute else shortcut }
        }
        withRouter(registry) { controller, nav ->
            assertEquals(NavigationResult.Handled, nav.openService(service(false)))
            assertEquals(TrailMarker(6), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            assertEquals(NavigationResult.Handled, nav.openService(service(true)))
            assertEquals(FieldKitRoute.name, controller.currentDestination!!.route)
            val top = controller.currentBackStackEntry
            assertEquals(NavigationResult.Unhandled, nav.open(NamedRoute("unknown")))
            assertEquals(NavigationResult.Unhandled, nav.open(TrailMarker(9)))
            assertSame(top, controller.currentBackStackEntry)
        }
    }

    @Test
    fun requestedDestinationFallbackAndUnhandledAreDistinct() = withRouter { controller, nav ->
        val missing = serviceContract<Unit>("MISSING")()
        assertEquals(NavigationResult.Handled, nav.open(TrailMarker(3)))
        val initial = controller.currentBackStackEntry
        assertEquals(NavigationResult.Unhandled, nav.openService(missing))
        val wireRegistry = NavigationRegistry().apply { KitEntry().registerNavigation(this) }
        val missingUrl = ActionDto("", "WEB_VIEW", "SERVICE").toDomainModel(wireRegistry)
        assertEquals(ActionEntity.Unhandled, missingUrl)
        assertEquals(NavigationResult.Unhandled, nav.openAction(missingUrl))
        assertEquals(NavigationResult.Unhandled, nav.openLink("https://example.org".toUri()))
        assertSame(initial, controller.currentBackStackEntry)
        assertFalse(nav.tryNavigate(missing))
        val unknown = ActionDto("", "MISSING", "SERVICE").toDomainModel(NavigationRegistry())
        assertEquals(NavigationResult.FallbackOpened, nav.openAction(unknown))
        assertEquals(
            Unavailable("MISSING", "SERVICE"),
            controller.currentBackStackEntry!!.toRoute<Unavailable>(),
        )
        assertTrue(nav.tryNavigate(unknown))
        assertEquals(NavigationResult.Handled, nav.openLink(LEGACY_KIT_LINK.toUri()))
        assertEquals(FieldKitRoute.name, controller.currentDestination!!.route)
    }

    @Test
    fun deepLinkAndJsonDecodeAtIngressAndDispatchTheirTypedValuesExactlyOnce() {
        val service = serviceContract<Int>("VISITS")
        var reads = 0
        val registry = NavigationRegistry().apply {
            register(TypedRouteHandler(TrailMarker::class))
            registerService(service, setOf("walk"), decode = {
                reads++
                it["visit"]?.toIntOrNull()
            }) { TrailMarker(it) }
        }
        val uri = "nightatlas://walk?visit=3".toUri()
        val link = AtlasLinkMapper(registry).decode(uri)
        val json = ActionDto("", "walk", "SERVICE", mapOf("visit" to "4")).toDomainModel(registry)
        assertEquals(ActionEntity.Service(service(3)), link.mapped)
        assertEquals(ActionEntity.Service(service(4)), json)
        assertEquals(2, reads)
        withRouter(registry) { controller, nav ->
            assertEquals(NavigationResult.Handled, nav.openAction(link))
            assertEquals(TrailMarker(3), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            assertEquals(NavigationResult.Handled, nav.openAction(json))
            assertEquals(TrailMarker(4), controller.currentBackStackEntry!!.toRoute<TrailMarker>())
            assertEquals(NavigationResult.Handled, nav.openService(service(5)))
            assertEquals(2, reads)
        }
    }

    @Test
    fun fallbackOutcomeSurvivesNestedServicesActionsAndDeepLinks() {
        val forward = serviceContract<Unit>("FORWARD")
        val link = serviceContract<Unit>("LINK")
        val missing = ActionEntity.UnsupportedService("MISSING", DestinationType.SERVICE)
        val registry =
            NavigationRegistry().apply {
                KitEntry().registerNavigation(this)
                registerService(forward) {
                    missing
                }
                registerService(link) { NavigationTarget.Link("nightatlas://forward".toUri()) }
            }
        withRouter(registry) { _, nav ->
            assertEquals(NavigationResult.FallbackOpened, nav.openLink("nightatlas://link".toUri()))
            assertEquals(NavigationResult.FallbackOpened, nav.openService(link()))
            assertEquals(
                NavigationResult.FallbackOpened,
                nav.openAction(ActionDto("", "nightatlas://link", "DEEP_LINK").toDomainModel(registry)),
            )
        }
        withRouter(NavigationRegistry()) { controller, nav ->
            val root = controller.currentBackStackEntry
            assertEquals(
                NavigationResult.Unhandled,
                nav.openAction(missing),
            )
            assertSame(root, controller.currentBackStackEntry)
        }
    }
}
