package com.example.liquidglassnavdemos.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.liquidglassnavdemos.BackdropDemo
import com.example.liquidglassnavdemos.FlowTabDemo
import com.example.liquidglassnavdemos.HazeDemo
import com.example.liquidglassnavdemos.PrismalDemo
import com.example.liquidglassnavdemos.QmLiquidGlassDemo

private data class DemoItem(
  val title: String,
  val subtitle: String,
  val tag: String,
  val icon: ImageVector,
  val accent: Color,
  val destination: NavKey,
)

private val demos =
  listOf(
    DemoItem("Haze", "磨砂玻璃对照组：只有模糊，没有折射", "Frosted", Icons.Outlined.BlurOn, Color(0xFF0C8CE9), HazeDemo),
    DemoItem("FlowTab + Backdrop", "FlowTab 交互组件 + Kyant 液态折射材质", "Hybrid", Icons.Outlined.Layers, Color(0xFF7257D9), FlowTabDemo),
    DemoItem("Kyant Backdrop", "折射、色散与可拖拽液态选中胶囊", "Shader", Icons.Outlined.WaterDrop, Color(0xFF00A67E), BackdropDemo),
    DemoItem("AndroidLiquidGlassView", "View RuntimeShader 真实折射 · API 33+", "View", Icons.Outlined.LightMode, Color(0xFFE4572E), QmLiquidGlassDemo),
    DemoItem("Prismal", "OpenGL ES 折射、Fresnel 与高光", "OpenGL", Icons.Outlined.AutoAwesome, Color(0xFFCF3F74), PrismalDemo),
  )

@Composable
fun MainScreen(onItemClick: (NavKey) -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFFF5F8FF), Color(0xFFF7F7F4), Color(0xFFFFF6F2))
          )
        )
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().safeDrawingPadding(),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      item {
        Text(
          text = "Liquid Glass Lab",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF15171A),
        )
        Spacer(Modifier.height(6.dp))
        Text(
          text = "4 种液态实现 + 1 个磨砂对照组",
          style = MaterialTheme.typography.bodyLarge,
          color = Color(0xFF5F6368),
        )
        Spacer(Modifier.height(18.dp))
      }
      items(demos) { demo ->
        Surface(
          modifier = Modifier.fillMaxWidth().clickable { onItemClick(demo.destination) },
          shape = RoundedCornerShape(8.dp),
          color = Color.White.copy(alpha = 0.92f),
          shadowElevation = 1.dp,
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Box(
              modifier = Modifier.size(44.dp).background(demo.accent.copy(alpha = 0.12f), CircleShape),
              contentAlignment = Alignment.Center,
            ) {
              Icon(demo.icon, null, tint = demo.accent, modifier = Modifier.size(23.dp))
            }
            Column(Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  demo.title,
                  modifier = Modifier.weight(1f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF202124),
                )
                Text(
                  demo.tag,
                  maxLines = 1,
                  style = MaterialTheme.typography.labelSmall,
                  color = demo.accent,
                  modifier = Modifier.background(demo.accent.copy(alpha = 0.09f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp),
                )
              }
              Spacer(Modifier.height(4.dp))
              Text(demo.subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF696D72))
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Color(0xFF9AA0A6))
          }
        }
      }
    }
  }
}
