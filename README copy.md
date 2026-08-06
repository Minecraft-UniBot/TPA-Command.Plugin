# Placeholder Command Mods

为 [TextPlaceholderAPI](https://github.com/Patbox/TextPlaceholderAPI) 模组提供指令访问能力。通过指令即可渲染文本或获取变量值，无需编写额外的模组代码。

支持 **Fabric** 平台，面向 **Minecraft 26.1.2 / 26.2**（mojmap，Java 25）。

## 功能

- **渲染文本**：通过指令将包含占位符的文本解析并输出。
- **获取变量值**：通过指令查询任意占位符对应的变量值。
- **纯文本输出**：无论是玩家还是控制台，指令结果均以纯文本形式返回，不含任何富文本格式。
- **内置占位符**：提供一组开箱即用的服务器/玩家占位符。

## 依赖

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [TextPlaceholderAPI](https://modrinth.com/mod/text_placeholder_api)（已通过 Jar-in-Jar 打包，无需单独安装）

## 支持的版本

| Minecraft 版本 | Java | 映射 |
| --- | --- | --- |
| 26.1.2 | 25 | mojmap（官方命名） |
| 26.2 | 25 | mojmap（官方命名） |

## 构建

需要 **Java 25**（例如 HMCL 的 `mojang-java-runtime-epsilon`）。

项目采用多版本子项目结构，每个 Minecraft 版本对应 `versions/<版本>/` 下的一个子项目，共享源码位于根项目 `src/main/java`（mojmap 命名）。

```sh
JAVA_HOME="/path/to/jdk25/Contents/Home" ./gradlew build --no-daemon
```

构建产物示例：
- `versions/26.1/build/libs/placeholder-command-mods-26.1.2-1.0.0.jar`
- `versions/26.2/build/libs/placeholder-command-mods-26.2-1.0.0.jar`

## 指令

所有指令均需要 `minecraft.command.*` 权限（默认管理员可用）。

### `/placeholdercommand render [as <玩家>] <文本>`

渲染包含占位符的文本，把解析结果发送给命令执行者。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来渲染。

```
/placeholdercommand render 你好，%placeholder:player_name%，你的等级是 %placeholder:player_level%
/placeholdercommand render as test 你好，%placeholder:player_name%，你的等级是 %placeholder:player_level%
```

### `/placeholdercommand get [as <玩家>] <占位符>`

获取单个占位符的变量值。支持直接输入 `%namespace:name%` 形式，也支持省略 `%` 自动补全。默认使用命令执行者自身的上下文；可通过 `as <玩家>` 指定以某个玩家的上下文来查询。

```
/placeholdercommand get %placeholder:player_health%
/placeholdercommand get placeholder:server_tps
/placeholdercommand get as test %placeholder:player_health%
```

### `/placeholdercommand list`

列出 TextPlaceholderAPI 当前已注册的所有占位符。

## 内置占位符

本模组使用 `placeholder` 命名空间，提供以下占位符：

| 占位符 | 说明 |
| --- | --- |
| `%placeholder:player_name%` | 玩家显示名称 |
| `%placeholder:player_uuid%` | 玩家 UUID |
| `%placeholder:player_level%` | 玩家游戏等级 |
| `%placeholder:player_health%` | 玩家生命值（保留一位小数） |
| `%placeholder:server_tps%` | 服务器 TPS |
| `%placeholder:server_player_count%` | 服务器在线人数 |
| `%placeholder:server_max_players%` | 服务器最大玩家数 |

> 除了上述占位符，你还可以使用 TextPlaceholderAPI 提供的所有内置占位符（如 `%player:name%`、`%server:tps%` 等），以及其它模组注册的占位符。

## 构建

项目采用多版本多子项目结构，每个 Minecraft 版本对应 `versions/<版本>/` 下的一个子项目，共享源码位于根项目 `src/main/java`（mojmap 命名）。

### 构建某个版本

```bash
./gradlew :26.1:build
```

将 `26.1` 替换为目标版本（如 `:26.2:build`）即可。

### 构建全部版本

```bash
./gradlew build
```

> **注意**：并行构建全部版本时内存占用较高，官方配置已增加 Gradle JVM 内存（`org.gradle.jvmargs=-Xmx4G`）。若仍遇到内存不足，建议分批构建。

构建产物位于对应子项目的 `versions/<version>/build/libs/` 目录下。

## 许可

LGPL-3.0-or-later
