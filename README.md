# PlaceholderCommand

为 [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) 插件提供指令访问能力。通过指令即可渲染文本或获取占位符变量值，无需编写额外的插件代码。

支持 **Bukkit / Spigot / Paper** 平台，指令格式与 [TextPlaceholderAPI](https://github.com/Patbox/TextPlaceholderAPI) 模组完全一致。

> **注意**：本插件仅对 PlaceholderAPI 做封装。它不会修改 PlaceholderAPI，也不会注册任何占位符。所有占位符均由 PlaceholderAPI 及其挂载的扩展（Expansion）提供。

## 功能

- **渲染文本**：通过指令将包含占位符的文本解析并输出。
- **获取变量值**：通过指令查询任意占位符对应的变量值。
- **批量获取**：通过 `gets` 一次查询多个占位符的值。
- **兼容全部占位符**：可直接使用 PlaceholderAPI 及其所有扩展注册的占位符。
- **指令补全**：支持 `Tab` 键补全子命令与在线玩家名。

## 依赖

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)（必须安装）

## 支持的版本

| 平台 | Minecraft 版本 |
| --- | --- |
| Spigot / Paper | 1.13+ |

> 使用 Java 8 字节码目标编译，可在全部 1.13+ 服务器（Java 8+）上加载运行。

## 构建

需要 **JDK 17+** 与 **Gradle 8+**（本项目使用 wrapper 自带 Gradle 8.14.3）。

```sh
./gradlew build
```

构建产物位于 `build/libs/PlaceholderCommand-1.0.0.jar`。

## 安装

1. 将生成的 jar 放入服务器的 `plugins/` 目录。
2. 同时安装 [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)。
3. 重启服务器。

## 指令

所有指令均需要 `placeholdercommand.use` 权限（默认 OP 可用）。命令名 `placeholdercommand`，别名 `phc`。

子命令：`render`（渲染文本）、`get`（获取占位符值）、`gets`（批量获取占位符值）、`list`（列出占位符）。以下 `<label>` 指实际使用的命令名（`placeholdercommand` 或 `phc`）。

### 通用

**无参数或未知子命令** → 输出用法：

```
用法:
  /<label> render [as <玩家>] <文本>
  /<label> get [as <玩家>] <占位符>
  /<label> gets [as <玩家>] <占位符1> <占位符2> ...
  /<label> list
```

### `/placeholdercommand render [as <玩家>] <文本>`

渲染文本，把解析结果发送给命令执行者。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来渲染。

**注意**：`render` 保留文本中的颜色/格式代码（如 `&a`、`§b`），不做任何清理。

| 输入 | 输出 |
| --- | --- |
| `render as <不在线的玩家>` | `[ No Player ]` |
| `render as <在线玩家> <文本>` | 以该玩家上下文渲染后的文本 |
| `render <文本>`（玩家执行） | 以执行者自身上下文渲染后的文本 |
| `render <文本>`（控制台执行） | 无玩家上下文，仅解析服务器级占位符 |

示例：

```
/placeholdercommand render 你好，%player_name%，你的等级是 %player_level%
/placeholdercommand render as test 你好，%player_name%，你的等级是 %player_level%
```

### `/placeholdercommand get [as <玩家>] <占位符>`

获取单个占位符的变量值。支持直接输入 `%namespace:name%` 形式，也支持省略 `%` 自动补全。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来查询。

**注意**：`get` 会对结果做**颜色/格式代码剥离**（移除 `&` 与 `§` 格式码），保证纯文本输出。

| 输入 | 输出 |
| --- | --- |
| `get as <不在线的玩家> <占位符>` | `[ No Player ]` |
| `get as <在线玩家> <占位符>` | 以该玩家上下文解析出的值（剥离颜色码） |
| `get <占位符>`（玩家执行） | 以执行者自身上下文解析出的值（剥离颜色码） |
| `get <占位符>`（控制台执行） | 无玩家上下文，仅解析服务器级占位符（剥离颜色码） |

示例：

```
/placeholdercommand get %player_name%
/placeholdercommand get player_health
/placeholdercommand get as test %player_name%
```

### `/placeholdercommand gets [as <玩家>] <占位符1> <占位符2> ...`

一次性获取多个占位符的值，逐行输出。每个占位符支持直接输入 `%namespace:name%` 形式，也支持省略 `%` 自动补全。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来查询。

**注意**：`gets` 会对每个结果做**颜色/格式代码剥离**（移除 `&` 与 `§` 格式码），保证纯文本输出。

| 输入 | 输出 |
| --- | --- |
| `gets as <不在线的玩家> <占位符>` | `[ No Player ]` |
| `gets as <在线玩家> <占位符1> <占位符2>` | 每行输出 `%占位符% = 值`，以该玩家上下文解析 |
| `gets <占位符1> <占位符2>`（玩家执行） | 每行输出 `%占位符% = 值`，以执行者自身上下文解析 |
| `gets <占位符1> <占位符2>`（控制台执行） | 每行输出 `%占位符% = 值`，仅解析服务器级占位符 |

示例：

```
/placeholdercommand gets %player_name% %player_health% %server_tps%
/placeholdercommand gets player_health server_tps
/placeholdercommand gets as test %player_name% %player_level%
```

输出示例：

```
%player_name% = test
%player_health% = 20.0
%server_tps% = 20.0
```

### `/placeholdercommand list`

列出 PlaceholderAPI 当前已注册的所有占位符。

| 情况 | 输出 |
| --- | --- |
| PlaceholderAPI 未加载 | `PlaceholderAPI 未加载，无法列出占位符。` |
| 没有任何已注册占位符 | `[ No Placeholder ]` |
| 正常 | 逐行输出 `%<标识符>%`（按字母序） |

示例输出：

```
%player_name%
%player_health%
%server_tps%
```

## 说明

- **控制台（Console）** 执行 `render` / `get` / `gets` 时，若未通过 `as <玩家>` 指定上下文，则仅解析服务器级占位符（无玩家上下文的占位符将返回空值）。
- **`render` 与 `get` / `gets` 的区别**：`render` 用于渲染包含占位符的整段文本且保留颜色，可拼接多个单词（保留空格）；`get` / `gets` 用于查询占位符的值且剥离颜色码——`get` 只取第一个参数，`gets` 可一次查询多个。
- 本插件不提供任何内置占位符，也不注册 Expansion。所有占位符均来自 PlaceholderAPI 及其扩展。

## 许可

MIT
