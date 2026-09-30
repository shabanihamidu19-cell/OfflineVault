package com.offlinevault.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.offlinevault.presentation.home.HomeScreen
import com.offlinevault.presentation.library.LibraryScreen
import com.offlinevault.presentation.player.PlayerScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val PLAYER = "player/{path}/{title}"

    fun player(path: String, title: String): String {
        val encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
        val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
        return "player/$encodedPath/$encodedTitle"
    }
}

@Composable
fun OfflineVaultNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute == Routes.HOME || currentRoute == Routes.LIBRARY

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Download, contentDescription = "Download") },
                        label = { Text("Download") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.LIBRARY,
                        onClick = {
                            navController.navigate(Routes.LIBRARY) {
                                popUpTo(Routes.HOME)
                            }
                        },
                        icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Library") },
                        label = { Text("Library") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen()
            }
            composable(Routes.LIBRARY) {
                LibraryScreen(
                    onPlayVideo = { file ->
                        navController.navigate(
                            Routes.player(file.absolutePath, file.nameWithoutExtension)
                        )
                    }
                )
            }
            composable(
                route = Routes.PLAYER,
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val path = URLDecoder.decode(
                    backStackEntry.arguments?.getString("path") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val title = URLDecoder.decode(
                    backStackEntry.arguments?.getString("title") ?: "Video",
                    StandardCharsets.UTF_8.toString()
                )
                PlayerScreen(
                    videoPath = path,
                    title = title,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
