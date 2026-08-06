package com.placeholdercommand;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * PlaceholderCommand 主类。
 * <p>
 * 仅封装 PlaceholderAPI，为 Bukkit/Paper 服务器提供与 TextPlaceholderAPI 模组
 * 指令格式一致的指令访问能力。本插件不修改 PlaceholderAPI，也不注册任何占位符。
 */
public final class PlaceholderCommandPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PlaceholderCommandCommand command = new PlaceholderCommandCommand();
        getCommand("placeholdercommand").setExecutor(command);
        getCommand("placeholdercommand").setTabCompleter(command);
        getLogger().info("PlaceholderCommand 已启用。使用 /placeholdercommand 开始。");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlaceholderCommand 已禁用。");
    }
}