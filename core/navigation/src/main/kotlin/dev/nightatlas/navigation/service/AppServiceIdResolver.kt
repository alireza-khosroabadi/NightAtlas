package dev.nightatlas.navigation.service

interface AppServiceIdResolver {
    fun resolveServiceId(appServiceKey: String, path: String? = null): AppServiceId?
}
