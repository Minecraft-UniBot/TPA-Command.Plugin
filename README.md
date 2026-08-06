# PlaceholderCommand

为 [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) 插件提供指令访问能力。通过指令即可渲染文本或获取占位符变量值，无需编写额外的插件代码。

支持 **Bukkit / Spigot / Paper** 平台，指令格式与 [TextPlaceholderAPI](https://github.com/Patbox/TextPlaceholderAPI) 模组完全一致。

> **注意**：本插件仅对 PlaceholderAPI 做封装。它不会修改 PlaceholderAPI，也不会注册任何占位符。所有占位符均由 PlaceholderAPI 及其挂载的扩展（Expansion）提供。

## 功能

- **渲染文本**：通过指令将包含占位符的文本解析并输出。
- **获取变量值**：通过指令查询任意占位符对应的变量值。
- **纯文本输出**：无论是玩家还是控制台，指令结果均以纯文本形式返回，不含任何富文本格式。
- **兼容全部占位符**：可直接使用 PlaceholderAPI 及其所有扩展注册的占位符。

## 依赖

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)（必须安装）

## 支持的版本

| 平台 | Minecraft 版本 |
| --- | --- |
| Spigot / Paper | 1.13+ |

> 使用 Java 8 字节码目标编译，可在全部 1.13+ 服务器（Java 8+）上加载运行。

## 多版本支持

本插件仅封装稳定的 Bukkit API，未使用任何版本特有的 API，因此同一个 jar 即可在 1.13 及以上的所有版本上运行。

版本相关参数集中在 `gradle.properties` 中，方便统一调整：

```properties
# 编译所基于的 Minecraft API 版本
mcVersion=1.20.1
# plugin.yml 中的 api-version（决定服务器允许的最低版本）
apiVersion=1.13
# Java 编译目标字节码版本
javaTarget=8
# jar 产物版本号
pluginVersion=1.0.0
```

- **最低版本（1.13）**：`apiVersion=1.13` 是引入 `api-version` 字段的版本，1.8~1.12 服务器会忽略该字段。若要支持 1.8~1.12，需确认所调用的 Bukkit API 在该版本存在（本插件只用稳定 API，一般没问题）。
- **Java 目标**：`javaTarget=8` 使字节码可在所有 1.13+ 服务器（Java 8+）上加载。若编译 JDK 也满足要求，无需改动。
- **编译依据**：`mcVersion=1.20.1` 仅决定编译时引用的 Spigot API 版本，与运行时兼容范围无关。

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

所有指令均需要 `minecraft.command.*` 权限（默认管理员可用）。

### `/placeholdercommand render [as <玩家>] <文本>`

渲染包含占位符的文本，把解析结果发送给命令执行者。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来渲染。

```
/placeholdercommand render 你好，%player_name%，你的等级是 %player_level%
/placeholdercommand render as test 你好，%player_name%，你的等级是 %player_level%
```

### `/placeholdercommand get [as <玩家>] <占位符>`

获取单个占位符的变量值。支持直接输入 `%namespace:name%` 形式，也支持省略 `%` 自动补全。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来查询。

```
/placeholdercommand get %player_name%
/placeholdercommand get player_health
/placeholdercommand get as test %player_name%
```

### `/placeholdercommand list`

列出 PlaceholderAPI 当前已注册的所有占位符。

## 说明

- 控制台（Console）执行 `render` / `get` 时，若未通过 `as <玩家>` 指定上下文，则仅解析服务器级占位符（无玩家上下文的占位符将返回空值）。
- 本插件不提供任何内置占位符，也不注册 Expansion。所有占位符均来自 PlaceholderAPI 及其扩展。

## 许可

MIT
