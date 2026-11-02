# 设计风格

MULLION 的视觉语言是「窗棂」，不是「霓虹」：**浅色优先、蓝色语义主色、可复现的玻璃配方、一切尺寸来自上下文标尺**。本页是设计规范总览；每个值都能在源码 token 中找到唯一出处。

## 1. 设计原则

1. **结构先于装饰** — 分格由 `Bp`、`PaneStrategy`、`HingeInfo` 决定，样式只负责呈现，不参与适配判断。
2. **浅色优先** — 参考应用默认浅色企业工具风格；深色是同一套语义 token 的另一种解析，不是另一套 UI。
3. **语义色，不写死颜色** — 组件只读 `MullionColors` / `MullionPalette`，业务代码不出现 `Color(0xFF...)`。
4. **尺寸来自上下文** — `mullionDimens()` 由 `Bp + fontScale` 派生；业务 Screen 不写 `padding = 24.dp`。
5. **可复现** — 玻璃是确定性渐变配方，API 21 上与 API 35 上像素一致，截图测试不会漂移。

## 2. 配色（语义 token）

| Token | 浅色 | 深色 | 用途 |
| --- | --- | --- | --- |
| `Background` | `#F5F7FA` | `#0F141B` | 页面底色 |
| `BackgroundLift` | `#FFFFFF` | `#171D26` | 卡片 / 浮层 |
| `BackgroundDeep` | `#E9EEF4` | `#202936` | 次级表面 |
| `Volt`（主色） | `#2563EB` | `#6EA8FE` | 选中、焦点、主按钮 |
| `VoltHigh` | `#60A5FA` | `#A7C4FF` | 主色渐变高光 |
| `Signal` | `#D9534F` | `#E07A5F` | 铰链 / 严重告警 / 破坏性 |
| `Mint` | `#0E9F6E` | `#55C7A3` | 成功 / 在线 |
| `Amber` | `#B45309` | `#D6A84B` | 警告 / 提醒 |
| `Ink` | `#111827` | `#E9EEF5` | 主文本 |
| `Muted` | `#4B5563` | `#A6B0BE` | 次级文本 |
| `Faint` | `#8A94A3` | `#75808F` | 辅助文本 / 标签 |
| `OnVolt` | `#FFFFFF` | `#0B1220` | 主色填充上的文字 |

规则：

- **Volt 只用于「当前 pane、焦点环、主操作」**，不做装饰色；
- **Signal 只用于铰链与危险操作**，告警严重级别复用它；
- 状态色（Mint / Amber / Signal）永远搭配文字或形状，不单独靠颜色传达信息。

## 3. 字阶

| 场景 | 字体 | 尺寸 | 行高 |
| --- | --- | --- | --- |
| 页面标题 | Sans Bold | 22–28sp | 30–36 |
| 大数字 / KPI | Sans SemiBold | 24sp | 30 |
| 正文 `Body` | Sans | 15sp（Expanded 17 / Large 19） | 25–34 |
| 辅助 `UISmall` | Sans | 11sp | 16 |
| 标签 `UILabel` | Sans Medium | 11sp | 16 |
| 日志 / 序列号 | Mono | 10.5–12sp | 16–18 |

- **业务文案一律 Sans**；`Mono` 只留给日志、序列号、设备 ID 等技术值。
- 字号不是窗口宽度的线性函数，而是 `TypeScale` 步进（15 → 17 → 19sp），`SizeClassText` 还带 `maxMeasureCh` 控制行长。

## 4. 圆角与玻璃

| 层级 | 圆角 | 用途 |
| --- | --- | --- |
| Chip / Pill | 999dp | 徽标、分段器、按钮 |
| Card | 12dp | 卡片 |
| Panel | 18dp | 面板、输入区 |
| Dialog | 22dp | 弹窗 / 侧滑面板 |
| Window | 28dp | 橱窗 / 大容器 |

玻璃配方（`Modifier.glass`）：

1. 按形状裁切；
2. 三层渐变填充 —— 浅色用墨色低透明度，深色用白色低透明度（`glassTint × glassAlphaScale`）；
3. 1dp 语义边框（`Edge`）；
4. 顶部 1dp 发丝高光。

> 玻璃是**确定性渐变**，不是实时背景模糊：Compose 在 API 31 以下无法采样背景，`RenderEffect` 会让截图测试分叉，这与 MULLION「先解决适配」的目标相悖。

## 5. 间距与标尺

dp 只存在于标尺层，业务代码用 `mullionDimens()` 读取：

| Token | Compact | Expanded | Large / XL |
| --- | --- | --- | --- |
| `screenPadding` | 16 | 24 | 32 |
| `cardPadding` | 14 | 16 | 18 |
| `sectionGap` | 16 | 20 | 24 |
| `gutter`（栏间距） | 12 | 24 | 24 |
| `pageMaxWidth` | 720 | 960 | 1180 |
| `railItemHeight` / `drawerItemHeight` | 62 / 44 | 62 / 44 | 62 / 44 |

控件固定量（无障碍底线）：触摸目标 **≥48dp**、控件高 44 / 52dp、输入框 42dp、分栏最小详情宽度 280–320dp。

## 6. 导航形态

| 宽度 | 形态 | 位置 |
| --- | --- | --- |
| <600dp | BottomBar | 底部半透明浮层，内容从其后滚过，浮层接收系统导航 inset |
| 600–839dp | Rail | 左侧 80dp，背景铺满全高 |
| ≥840dp | Drawer | 左侧 216dp 展开，背景铺满全高 |

三者共享 `OrbitNav` 的同一份 `items` 与 `selectedIndex`，切换宽度不丢状态。

## 7. 动效

| 时长 | 用途 |
| --- | --- |
| 120ms | 按压缩放 |
| 180ms | 状态色 / 分段切换 |
| 240ms | pane 共享轴切换 |
| 340ms | 弹窗、面板进出 |
| 380ms | Docking / 重停靠 |

缓动统一为 `cubic-bezier(.22,1,.36,1)`（Settle），动效表达「重新分格」，不是「炫技」。

## 8. Do / Don't

```kotlin
// ✅ 从上下文取尺
Row(Modifier.padding(mullionDimens().screenPadding))

// ❌ 业务代码手写适配数值
Row(Modifier.padding(24.dp))
```

```kotlin
// ✅ 语义色
Text(color = if (online) MullionColors.Mint else MullionColors.Faint)

// ❌ 写死颜色，深色模式必然失真
Text(color = Color(0xFF00FF00))
```

```kotlin
// ✅ 名义化预览宽度
MullionViewport.Phone / Fold / Desktop

// ❌ 到处散落 375 / 760 / 1180
```

## 9. 相关源码

- 配色：`mullion-compose/theme/MullionTokens.kt`（`MullionPalette`、`LightMullionPalette`、`DarkMullionPalette`）
- 尺寸：同文件 `MullionDimens.forBp()`
- 字阶：同文件 `MullionTextStyles`、`mullion-core/Metrics.kt` 的 `TypeScale`
- 玻璃：`mullion-compose/theme/Glass.kt`
- 运动：`MullionMotion`
