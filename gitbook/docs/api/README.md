# API 参考

本部分覆盖 MULLION SDK 的**全部公开符号**：函数、Composable、类、枚举、伴生对象与属性。每个条目给出：

- **Kotlin 签名**（含 `@Composable` 等注解与默认值）；
- **释义**（取自源码 KDoc，含 `@param` / `@return`）；无 KDoc 的符号标注「以签名为准」。

## 模块

| 页面 | 模块 | 内容 | 符号数 |
| --- | --- | --- | --- |
| [mullion-core API](mullion-core.md) | `mullion-core` | 断点 `Bp`、分栏 `PaneStrategy`、折叠几何 `Foldables`、窗口状态 `MullionWindowState`、标尺 `MullionMetrics`、`MullionViewport` | 94 |
| [mullion-compose API](mullion-compose.md) | `mullion-compose` | 主题与 token、窗口 provider、14 个自适应组件、12 个基础件 | 130 |
| [mullion-view API](mullion-view.md) | `mullion-view` | `MullionBreakpointLayout`、`MullionPaneLayout`、玻璃 View、`ResourceScaler`、`MullionEdgeToEdge` | 46 |

## 阅读方式

- 页面按 **包 → 源文件 → 符号** 三级组织，标题层级与源码目录一一对应；
- 签名即契约：默认值、参数顺序、返回类型都可直接复制使用；
- Composable 带 `@Composable` 注解；扩展函数的接收者类型在签名中可见；
- View 层的 XML 属性见 [View 层文档](../view-tier.md)。

## 生成方式

```bash
python3 gitbook/scripts/gen_api_docs.py
```

脚本遍历三个模块的 `src/main/kotlin`，提取 `public` 声明及其 KDoc。API 页面是构建产物的一部分，改源码注释后重新运行脚本即可同步。
