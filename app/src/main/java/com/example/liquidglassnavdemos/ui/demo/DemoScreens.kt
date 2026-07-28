package com.example.liquidglassnavdemos.ui.demo

import android.graphics.Color as AndroidColor
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.viewinterop.AndroidView
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.matrix.prismal.PrismalFrameLayout
import com.matrix.prismal.PrismalLiquidGlass
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import io.github.alimsrepo.flowtab.BottomNavigation
import io.github.alimsrepo.flowtab.domain.model.BadgeData
import io.github.alimsrepo.flowtab.domain.model.NavColor
import io.github.alimsrepo.flowtab.domain.model.NavConfig
import io.github.alimsrepo.flowtab.domain.model.NavIndicator
import io.github.alimsrepo.flowtab.domain.model.NavItem
import kotlin.math.roundToInt

private data class TabSpec(val label: String, val outlined: ImageVector, val filled: ImageVector)

private val tabs =
  listOf(
    TabSpec("首页", Icons.Outlined.Home, Icons.Filled.Home),
    TabSpec("发现", Icons.Outlined.Explore, Icons.Filled.Explore),
    TabSpec("收藏", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    TabSpec("我的", Icons.Outlined.Person, Icons.Filled.Person),
  )

@Composable
private fun DemoHeader(title: String, detail: String, onBack: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回") }
    Column(Modifier.weight(1f)) {
      Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(detail, style = MaterialTheme.typography.labelMedium, color = Color(0xFF61666D), maxLines = 1)
    }
  }
}

@Composable
private fun ColorfulContent(modifier: Modifier = Modifier, selected: Int) {
  LazyColumn(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFFE9F4FF), Color(0xFFFFF1E5), Color(0xFFE9FBF3))
          )
        ),
    contentPadding = PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 118.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    item {
      Text(tabs[selected].label, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    }
    item {
      Text(
        "上下滚动列表，观察内容穿过底栏时的实时采样变化。",
        color = Color(0xFF555A60),
        style = MaterialTheme.typography.bodyMedium,
      )
    }
    items(20) { index ->
      val colors = listOf(Color(0xFF4C8BF5), Color(0xFFFF7A59), Color(0xFF12A37F), Color(0xFFE2B93B), Color(0xFFB15AC7))
      val color = colors[index % colors.size]
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .height(if (index % 5 == 0) 104.dp else 74.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = if (index % 5 == 0) 0.9f else 0.76f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(Modifier.size(34.dp).background(Color.White.copy(alpha = 0.85f), CircleShape))
        Column(Modifier.padding(start = 12.dp)) {
          Text("Backdrop sample ${index + 1}", color = Color.White, fontWeight = FontWeight.SemiBold)
          Text("颜色与细节会穿过玻璃材质", color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodySmall)
        }
      }
    }
  }
}

@Composable
fun HazeScreen(onBack: () -> Unit) {
  val hazeState = rememberHazeState()
  var selected by remember { mutableIntStateOf(0) }
  Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().hazeSource(hazeState)) {
      DemoHeader("Haze", "Frosted baseline · blur only · no refraction", onBack)
      ColorfulContent(Modifier.weight(1f), selected)
    }
    Row(
      modifier =
        Modifier.align(Alignment.BottomCenter)
          .navigationBarsPadding()
          .padding(horizontal = 18.dp, vertical = 10.dp)
          .fillMaxWidth()
          .height(68.dp)
          .clip(RoundedCornerShape(28.dp))
          .hazeEffect(hazeState) {
            blurRadius = 28.dp
            backgroundColor = Color.White.copy(alpha = 0.42f)
            noiseFactor = 0.08f
          }
          .padding(horizontal = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround,
    ) {
      tabs.forEachIndexed { index, tab ->
        TabButton(tab, selected == index) { selected = index }
      }
    }
  }
}

@Composable
private fun TabButton(tab: TabSpec, selected: Boolean, onClick: () -> Unit) {
  Column(
    modifier = Modifier.size(width = 68.dp, height = 58.dp).clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Icon(
      if (selected) tab.filled else tab.outlined,
      contentDescription = tab.label,
      tint = if (selected) Color(0xFF0B74DE) else Color(0xFF51565C),
      modifier = Modifier.size(23.dp),
    )
    Text(
      tab.label,
      style = MaterialTheme.typography.labelSmall,
      color = if (selected) Color(0xFF0B74DE) else Color(0xFF51565C),
    )
  }
}

@Composable
fun FlowTabScreen(onBack: () -> Unit) {
  val backdrop = rememberLayerBackdrop()
  var selectedId by remember { mutableStateOf("home") }
  val items =
    remember {
      listOf(
        NavItem("home", "首页", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem("explore", "发现", Icons.Outlined.Explore, Icons.Filled.Explore),
        NavItem("favorite", "收藏", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite, badge = BadgeData(count = 3)),
        NavItem("profile", "我的", Icons.Outlined.Person, Icons.Filled.Person),
      )
    }
  val selected = items.indexOfFirst { it.id == selectedId }.coerceAtLeast(0)
  Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
      DemoHeader("FlowTab + Backdrop", "FlowTab interaction · Kyant refraction", onBack)
      ColorfulContent(Modifier.weight(1f), selected)
    }
    BottomNavigation(
      modifier =
        Modifier.align(Alignment.BottomCenter)
          .navigationBarsPadding()
          .padding(horizontal = 18.dp, vertical = 10.dp)
          .drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(28.dp) },
            effects = {
              vibrancy()
              blur(12.dp.toPx())
              lens(
                refractionHeight = 17.dp.toPx(),
                refractionAmount = 26.dp.toPx(),
                chromaticAberration = true,
              )
            },
            onDrawSurface = { drawRect(Color.White.copy(alpha = 0.18f)) },
          ),
      items = items,
      selectedId = selectedId,
      onItemSelected = { selectedId = it.id },
      contentPadding = PaddingValues(0.dp),
      config =
        NavConfig(
          height = 64.dp,
          cornerRadius = 28.dp,
          maxWidth = 460.dp,
          enableBlur = false,
          blurIntensity = 0f,
          showLabels = true,
          showBorder = false,
          navColor =
            NavColor(
              backgroundColor = Color.White,
              borderColor = Color.Transparent,
              selectedIconColor = Color(0xFF7257D9),
              unSelectedIconColor = Color(0xFF54565B),
              selectedTextColor = Color(0xFF7257D9),
              unSelectedTextColor = Color(0xFF54565B),
              selectedRippleColor = Color(0x227257D9),
            ),
          navIndicator = NavIndicator.Ripple(indicatorPadding = 4.dp),
        ),
    )
  }
}

@Composable
fun BackdropScreen(onBack: () -> Unit) {
  val backdrop = rememberLayerBackdrop()
  var selected by remember { mutableIntStateOf(0) }
  var barWidth by remember { mutableIntStateOf(0) }
  var dragX by remember { mutableStateOf<Float?>(null) }
  val tabWidth = if (barWidth == 0) 0f else barWidth / tabs.size.toFloat()
  val settledX by
    animateFloatAsState(
      targetValue = selected * tabWidth,
      animationSpec = spring(dampingRatio = 0.72f, stiffness = 430f),
      label = "liquid-tab-x",
    )
  val indicatorX =
    dragX?.let { (it - tabWidth / 2f).coerceIn(0f, (barWidth - tabWidth).coerceAtLeast(0f)) }
      ?: settledX
  Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
      DemoHeader("Kyant Backdrop", "Compose shader · refraction · dispersion", onBack)
      ColorfulContent(Modifier.weight(1f), selected)
    }
    Box(
      modifier =
        Modifier.align(Alignment.BottomCenter)
          .navigationBarsPadding()
          .padding(horizontal = 18.dp, vertical = 10.dp)
          .fillMaxWidth()
          .height(68.dp)
          .onSizeChanged { barWidth = it.width }
          .pointerInput(barWidth) {
            detectDragGestures(
              onDragStart = { dragX = it.x },
              onDragEnd = {
                val x = dragX
                if (x != null && tabWidth > 0f) {
                  selected = ((x / tabWidth).toInt()).coerceIn(tabs.indices)
                }
                dragX = null
              },
              onDragCancel = { dragX = null },
              onDrag = { change, _ ->
                change.consume()
                dragX = change.position.x
              },
            )
          }
          .drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(30.dp) },
            effects = {
              vibrancy()
              blur(16.dp.toPx())
              lens(
                refractionHeight = 18.dp.toPx(),
                refractionAmount = 28.dp.toPx(),
                chromaticAberration = true,
              )
            },
            onDrawSurface = { drawRect(Color.White.copy(alpha = 0.22f)) },
          ),
    ) {
      if (tabWidth > 0f) {
        Box(
          modifier =
            Modifier.offset { IntOffset(indicatorX.roundToInt(), 0) }
              .width(with(androidx.compose.ui.platform.LocalDensity.current) { tabWidth.toDp() })
              .height(68.dp)
              .padding(5.dp)
              .drawBackdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(25.dp) },
                effects = {
                  lens(
                    refractionHeight = 16.dp.toPx(),
                    refractionAmount = 24.dp.toPx(),
                    chromaticAberration = true,
                  )
                },
                onDrawSurface = { drawRect(Color.White.copy(alpha = 0.16f)) },
              )
        )
      }
      Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
      ) {
        tabs.forEachIndexed { index, tab ->
          TabButton(tab, selected == index) { selected = index }
        }
      }
    }
  }
}

@Composable
fun QmLiquidGlassScreen(onBack: () -> Unit) {
  Column(Modifier.fillMaxSize()) {
    DemoHeader("AndroidLiquidGlassView", "View · RuntimeShader · API 33+", onBack)
    AndroidView(
      modifier = Modifier.weight(1f).fillMaxWidth().navigationBarsPadding(),
      factory = { context -> qmDemoRoot(context) },
    )
  }
}

@Composable
fun PrismalScreen(onBack: () -> Unit) {
  var selected by remember { mutableIntStateOf(0) }
  Column(Modifier.fillMaxSize()) {
    DemoHeader("Prismal", "View · OpenGL ES · Fresnel/refraction", onBack)
    Box(Modifier.weight(1f).fillMaxWidth()) {
      ColorfulContent(Modifier.fillMaxSize(), selected)
      AndroidView(
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(72.dp),
        factory = { context ->
          PrismalFrameLayout(context).apply {
            clipChildren = false
            PrismalLiquidGlass.applyBase(this)
            setCornerRadius(context.dp(30f))
            setThickness(context.dp(9f))
            setBlurRadius(3.5f)
            setIOR(1.62f)
            setChromaticAberration(1.2f)
            addView(nativeTabs(context) { selected = it })
            post { updateBackground() }
          }
        },
        update = { it.post { it.updateBackground() } },
      )
    }
  }
}

private fun nativeTabs(context: android.content.Context, onSelected: (Int) -> Unit): LinearLayout =
  LinearLayout(context).apply {
    orientation = LinearLayout.HORIZONTAL
    gravity = Gravity.CENTER
    layoutParams =
      FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      )
    listOf("首页", "发现", "收藏", "我的").forEachIndexed { index, label ->
      addView(
        TextView(context).apply {
          text = label
          gravity = Gravity.CENTER
          setTextColor(AndroidColor.WHITE)
          textSize = 13f
          setTypeface(typeface, android.graphics.Typeface.BOLD)
          setShadowLayer(4f, 0f, 1f, AndroidColor.argb(110, 0, 0, 0))
          isClickable = true
          isFocusable = true
          setOnClickListener {
            onSelected(index)
            for (i in 0 until childCount) {
              (getChildAt(i) as TextView).alpha = if (i == index) 1f else 0.62f
            }
          }
          alpha = if (index == 0) 1f else 0.62f
          layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        }
      )
    }
  }

private fun qmDemoRoot(context: android.content.Context): FrameLayout {
  val root = FrameLayout(context)
  val nativeList =
    LinearLayout(context).apply {
      orientation = LinearLayout.VERTICAL
      setPadding(context.dp(20f).toInt(), context.dp(18f).toInt(), context.dp(20f).toInt(), context.dp(100f).toInt())
      addView(
        TextView(context).apply {
          text = "RuntimeShader sampling"
          textSize = 26f
          setTextColor(AndroidColor.rgb(30, 34, 38))
          setTypeface(typeface, android.graphics.Typeface.BOLD)
          layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.dp(60f).toInt())
        }
      )
      repeat(20) { index ->
        val color =
          AndroidColor.HSVToColor(
            floatArrayOf((index * 29f) % 360f, 0.62f, 0.92f)
          )
        addView(
          TextView(context).apply {
            text = "  Native backdrop sample ${index + 1}"
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            setTextColor(AndroidColor.WHITE)
            background =
              GradientDrawable().apply {
                setColor(color)
                cornerRadius = context.dp(8f)
              }
            layoutParams =
              LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.dp(if (index == 0) 104f else 74f).toInt()).apply {
                bottomMargin = context.dp(14f).toInt()
              }
          }
        )
      }
    }
  val source =
    ScrollView(context).apply {
      clipToPadding = false
      background =
        GradientDrawable(
          GradientDrawable.Orientation.TL_BR,
          intArrayOf(AndroidColor.rgb(230, 244, 255), AndroidColor.rgb(255, 237, 223), AndroidColor.rgb(226, 250, 240)),
        )
      addView(nativeList, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }
  root.addView(source, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  val glassBackdrop = LiveQmGlassBackdrop(context, nativeList)
  val glass =
    FrameLayout(context).apply {
      clipToOutline = true
      outlineProvider =
        object : android.view.ViewOutlineProvider() {
          override fun getOutline(view: View, outline: android.graphics.Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, context.dp(30f))
          }
        }
      addView(
        glassBackdrop,
        FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        ),
      )
      addView(nativeTabs(context) {})
    }
  root.addView(
    glass,
    FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.dp(72f).toInt(), Gravity.BOTTOM).apply {
      marginStart = context.dp(18f).toInt()
      marginEnd = context.dp(18f).toInt()
      bottomMargin = context.dp(10f).toInt()
    },
  )
  source.setOnScrollChangeListener { _, _, _, _, _ -> glassBackdrop.postInvalidateOnAnimation() }
  return root
}

private class LiveQmGlassBackdrop(
  context: android.content.Context,
  private val source: ViewGroup,
) : View(context) {
  private val shaderSource =
    resources
      .openRawResource(com.qmdeve.liquidglass.R.raw.liquidglass_effect)
      .bufferedReader()
      .use { it.readText() }
  private val runtimeShader = RuntimeShader(shaderSource)
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val clipPath = Path()
  private val sourceLocation = IntArray(2)
  private val hostLocation = IntArray(2)
  private var snapshot: Bitmap? = null
  private var snapshotCanvas: AndroidCanvas? = null
  private val frameRefresh =
    object : Runnable {
      override fun run() {
        if (!isAttachedToWindow) return
        invalidate()
        postOnAnimation(this)
      }
    }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    removeCallbacks(frameRefresh)
    postOnAnimation(frameRefresh)
  }

  override fun onDraw(canvas: AndroidCanvas) {
    super.onDraw(canvas)
    if (width == 0 || height == 0) return
    ensureSnapshot()

    val bitmap = snapshot ?: return
    val bitmapCanvas = snapshotCanvas ?: return
    bitmapCanvas.drawColor(AndroidColor.TRANSPARENT, PorterDuff.Mode.CLEAR)
    source.getLocationInWindow(sourceLocation)
    getLocationInWindow(hostLocation)
    bitmapCanvas.save()
    bitmapCanvas.translate(
      (sourceLocation[0] - hostLocation[0]).toFloat(),
      (sourceLocation[1] - hostLocation[1]).toFloat(),
    )
    source.draw(bitmapCanvas)
    bitmapCanvas.restore()

    runtimeShader.setInputShader(
      "content",
      BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP),
    )
    val radius = context.dp(30f)
    runtimeShader.setFloatUniform("size", width.toFloat(), height.toFloat())
    runtimeShader.setFloatUniform("offset", 0f, 0f)
    runtimeShader.setFloatUniform("cornerRadii", radius, radius, radius, radius)
    runtimeShader.setFloatUniform("refractionHeight", context.dp(18f))
    runtimeShader.setFloatUniform("refractionAmount", -context.dp(44f))
    runtimeShader.setFloatUniform("depthEffect", 0.35f)
    runtimeShader.setFloatUniform("chromaticAberration", 0.42f)
    runtimeShader.setFloatUniform("contrast", 0.04f)
    runtimeShader.setFloatUniform("whitePoint", 0.04f)
    runtimeShader.setFloatUniform("chromaMultiplier", 1.12f)
    runtimeShader.setFloatUniform("tintColor", 1f, 1f, 1f)
    runtimeShader.setFloatUniform("tintAlpha", 0.08f)
    paint.shader = runtimeShader

    clipPath.reset()
    clipPath.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), radius, radius, Path.Direction.CW)
    canvas.save()
    canvas.clipPath(clipPath)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    canvas.restore()
  }

  private fun ensureSnapshot() {
    val current = snapshot
    if (current?.width == width && current.height == height) return
    current?.recycle()
    snapshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    snapshotCanvas = AndroidCanvas(snapshot!!)
  }

  override fun onDetachedFromWindow() {
    removeCallbacks(frameRefresh)
    snapshot?.recycle()
    snapshot = null
    snapshotCanvas = null
    super.onDetachedFromWindow()
  }
}

private fun android.content.Context.dp(value: Float): Float = value * resources.displayMetrics.density
