# Liquid Glass Lab

一个用于比较 Android 底部液态玻璃导航实现的实验项目。项目重点区分普通高斯模糊与真正的折射、色散、Fresnel 和动态背景采样。

![Liquid Glass Lab 首页](docs/images/home.png)

## Demo 方案

| 方案 | 渲染方式 | 定位 |
| --- | --- | --- |
| Haze | Compose 高斯模糊 | 磨砂玻璃对照组，不包含折射 |
| FlowTab + Backdrop | FlowTab 交互 + Kyant Backdrop Shader | 通用导航组件与液态材质的组合方案 |
| Kyant Backdrop | Compose RuntimeShader | 折射、色散和可拖拽液态选中胶囊 |
| AndroidLiquidGlassView | AGSL RuntimeShader + 实时 Bitmap 采样 | View 体系的动态液态折射，API 33+ |
| Prismal | OpenGL ES | 折射、Fresnel 与高光 |

## 方案详解

### 1. Haze：磨砂玻璃对照组

<img src="docs/images/haze.png" width="360" alt="Haze Demo">

Haze 的职责是采集 Compose 内容并做背景模糊。Demo 在内容容器上使用 `hazeSource`，在底栏上使用 `hazeEffect`，参数为 `28.dp` 模糊半径、42% 白色表面和少量噪声。列表滚动时，底栏会实时显示经过模糊的背景颜色。

这是一种典型的 frosted glass，而不是 liquid glass：

- 有实时背景采样和高斯模糊；
- 没有边缘折射、透镜位移、色散或 Fresnel 高光；
- 点击导航项只改变图标和文字状态，玻璃材质本身不会发生形变。

它适合工具栏、浮层和阅读界面等强调可读性的场景，也适合作为判断其他方案是否真正产生折射的视觉基准。优点是实现成熟、性能稳定、Compose 集成自然；局限是无法单独还原 iOS Liquid Glass 的透镜感。

### 2. FlowTab + Backdrop：组件与材质组合

<img src="docs/images/flowtab-backdrop.png" width="360" alt="FlowTab + Backdrop Demo">

这个方案将职责拆成两层：

- FlowTab-CMP 负责四个导航项、选中状态、图标切换、文字、收藏徽标和 Ripple 指示器；
- Kyant Backdrop 负责底栏背后的实时采样、模糊、透镜折射和色散。

Demo 主动关闭 FlowTab 自带的模糊和边框，再通过 `drawBackdrop` 添加液态材质。当前参数包含 `12.dp` 模糊、`17.dp` 折射区域高度、`26.dp` 折射量、色差开关和 18% 白色表面。

这种组合适合已有成熟导航组件，但希望替换视觉材质的项目。它保留了 FlowTab 的 API、徽标和交互模型，同时获得真正的折射效果。需要注意的是，FlowTab 本身不是液态玻璃库，也不负责拖拽切换；液态渲染来自 Backdrop，交互仍是常规点击和 Ripple。

### 3. Kyant Backdrop：Compose 液态玻璃主体方案

<img src="docs/images/kyant-backdrop.png" width="360" alt="Kyant Backdrop Demo">

这是项目中最接近 iOS 底部液态导航交互的纯 Compose 方案。页面通过 `layerBackdrop` 建立可采样内容层，底栏和选中胶囊分别使用 `drawBackdrop`，形成两层折射：

- 外层底栏使用 vibrancy、`16.dp` 模糊、`18.dp` 折射高度和 `28.dp` 折射量；
- 内层选中胶囊再次进行透镜采样，使用 `16.dp` 折射高度和 `24.dp` 折射量；
- 两层都启用 chromatic aberration，使边缘出现轻微 RGB 色散。

整个底栏监听 `detectDragGestures`。拖动时，胶囊中心跟随手指并限制在底栏范围内；松手后根据位置选择最近的导航项，再通过阻尼比 `0.72`、刚度 `430` 的 spring 动画回弹到槽位。点击导航项同样可以切换。

优点是折射、色散、拖拽和弹性动画都在 Compose 中完成，状态管理直观，最适合新 Compose 项目。代价是 Shader、模糊和双层采样比普通导航栏更重，低端设备需要降低模糊半径、色散强度或渲染层数，并补充 TalkBack 语义和键盘焦点支持。

### 4. AndroidLiquidGlassView：View + AGSL 实时采样修正版

<img src="docs/images/android-liquid-glass-view.png" width="360" alt="AndroidLiquidGlassView Demo">

AndroidLiquidGlassView 基于 Android 13 引入的 `RuntimeShader` 和 AGSL。着色器根据圆角矩形的 signed distance 计算边缘法线，再偏移背景采样坐标；RGB 通道使用不同偏移量采样，从而得到透镜折射和色散。

原版组件通过 RenderNode 录制目标 View。目标是 `ScrollView` 时，硬件显示列表可能持续复用初始内容，出现“列表已经滚动，但导航栏仍保持第一帧”的问题。因此 Demo 保留库提供的 `liquidglass_effect.agsl` 着色器，并实现了 `LiveQmGlassBackdrop`：

- 直接采样 `ScrollView` 内部的内容容器，绕过 ScrollView 缓存的显示列表；
- 使用 `getLocationInWindow()` 计算内容与玻璃底栏的实时相对位置；
- 每帧将当前区域绘制到复用的 Bitmap，再把 `BitmapShader` 交给原版 AGSL；
- 导航文字放在独立的上层，避免和背景采样互相递归。

当前 Demo 使用 `18.dp` 折射高度、`44.dp` 折射量、0.42 色散强度和轻量白色 tint。它可以真实反映滚动内容变化，但每帧 Bitmap 采样的成本高于 RenderNode，更适合验证、特殊局部控件或需要兼容传统 View 的场景。生产环境应按滚动状态启停刷新、限制采样尺寸并进行性能测试。该方案需要 API 33+，也不是原库组件的原样调用。

### 5. Prismal：OpenGL ES 物理质感方案

<img src="docs/images/prismal.png" width="360" alt="Prismal Demo">

Prismal 使用 OpenGL ES 渲染玻璃表面，重点模拟有厚度的透明介质。Demo 将 `PrismalFrameLayout` 作为底栏容器，并设置：

- `30.dp` 圆角；
- `9.dp` 玻璃厚度；
- `3.5f` 模糊半径；
- `1.62f` 折射率（IOR）；
- `1.2f` 色差强度。

折射率、厚度、Fresnel 边缘反射和高光共同产生比普通模糊更强的实体玻璃感。导航文字作为原生 View 子元素叠加，选择状态由 TextView 的透明度表示；内容或状态更新后通过 `updateBackground()` 重新采样。

它适合强调物理质感、需要 OpenGL 管线或仍以 View 为主的项目。优势是折射边缘、高光和深度感明显；局限是与 Compose 状态和生命周期的衔接更复杂，背景变化需要显式同步，GPU 兼容性、功耗和低端设备表现也需要单独验证。

## 如何选择

| 需求 | 推荐方案 |
| --- | --- |
| 只需要稳定的背景模糊 | Haze |
| 保留现成导航组件，同时增加折射材质 | FlowTab + Backdrop |
| Compose 项目，需要拖拽液态胶囊 | Kyant Backdrop |
| 传统 View 项目，需要 AGSL 且设备为 API 33+ | AndroidLiquidGlassView 修正版 |
| 需要更明显的厚度、Fresnel 和 OpenGL 质感 | Prismal |

所有实现集中在 [`DemoScreens.kt`](app/src/main/java/com/example/liquidglassnavdemos/ui/demo/DemoScreens.kt)，可以直接对照参数和交互代码。

## 环境要求

- Android Studio 或 Android SDK 命令行工具
- JDK 17
- Android SDK 37
- 最低 Android 7.1（API 25）
- AndroidLiquidGlassView Demo 需要 Android 13（API 33）或更高版本

## 构建运行

```bash
git clone https://github.com/caochuankuan/liquid-glass-lab.git
cd liquid-glass-lab
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

也可以直接使用 Android Studio 打开项目并运行 `app` 配置。

## 主要依赖

- [Haze](https://github.com/chrisbanes/haze)
- [FlowTab-CMP](https://github.com/alims-repo/FlowTab-CMP)
- [Backdrop](https://github.com/Kyant0/Backdrop)
- [AndroidLiquidGlassView](https://github.com/QmDeve/AndroidLiquidGlassView)
- [Prismal](https://github.com/styropyr0/Prismal)
- Jetpack Compose、Material 3、Navigation 3

具体版本统一维护在 [`gradle/libs.versions.toml`](gradle/libs.versions.toml)。

## 许可证

项目采用 [MIT License](LICENSE)。
