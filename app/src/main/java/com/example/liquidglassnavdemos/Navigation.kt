package com.example.liquidglassnavdemos

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.liquidglassnavdemos.ui.demo.BackdropScreen
import com.example.liquidglassnavdemos.ui.demo.FlowTabScreen
import com.example.liquidglassnavdemos.ui.demo.HazeScreen
import com.example.liquidglassnavdemos.ui.demo.PrismalScreen
import com.example.liquidglassnavdemos.ui.demo.QmLiquidGlassScreen
import com.example.liquidglassnavdemos.ui.main.MainScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(onItemClick = { navKey -> backStack.add(navKey) })
        }
        entry<HazeDemo> { HazeScreen(onBack = { backStack.removeLastOrNull() }) }
        entry<FlowTabDemo> { FlowTabScreen(onBack = { backStack.removeLastOrNull() }) }
        entry<BackdropDemo> { BackdropScreen(onBack = { backStack.removeLastOrNull() }) }
        entry<QmLiquidGlassDemo> { QmLiquidGlassScreen(onBack = { backStack.removeLastOrNull() }) }
        entry<PrismalDemo> { PrismalScreen(onBack = { backStack.removeLastOrNull() }) }
      },
  )
}
