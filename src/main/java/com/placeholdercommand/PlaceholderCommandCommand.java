package com.placeholdercommand;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 指令执行器，实现与 TextPlaceholderAPI 模组完全一致的指令格式。
 * <p>
 * 指令：
 * <ul>
 *   <li>{@code /placeholdercommand render [as <玩家>] <文本>}</li>
 *   <li>{@code /placeholdercommand get [as <玩家>] <占位符>}</li>
 *   <li>{@code /placeholdercommand gets [as <玩家>] <占位符1> <占位符2> ...}</li>
 *   <li>{@code /placeholdercommand list}</li>
 * </ul>
 */
public final class PlaceholderCommandCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = Collections.unmodifiableList(
            java.util.Arrays.asList("render", "get", "gets", "list"));

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "render":
                onRender(sender, args);
                break;
            case "get":
                onGet(sender, args);
                break;
            case "gets":
                onGets(sender, args);
                break;
            case "list":
                onList(sender);
                break;
            default:
                sendUsage(sender, label);
                break;
        }
        return true;
    }

    // ------------------------------------------------------------------
    // render
    // ------------------------------------------------------------------

    private void onRender(CommandSender sender, String[] args) {
        // render [as <玩家>] <文本...>
        int index = 1;
        Player target = null;

        if (index < args.length && args[index].equalsIgnoreCase("as")) {
            if (index + 1 >= args.length) {
                sender.sendMessage("用法：/placeholdercommand render [as <玩家>] <文本>");
                return;
            }
            target = Bukkit.getPlayerExact(args[index + 1]);
            if (target == null) {
                sender.sendMessage("[ No Player ]");
                return;
            }
            index += 2;
        }

        if (index >= args.length) {
            sender.sendMessage("用法：/placeholdercommand render [as <玩家>] <文本>");
            return;
        }

        // 拼接文本（保留空格）
        StringBuilder sb = new StringBuilder(args[index]);
        for (int i = index + 1; i < args.length; i++) {
            sb.append(' ').append(args[i]);
        }

        String text = sb.toString();
        String result = renderFor(sender, target, text);

        sender.sendMessage(result);
    }

    // ------------------------------------------------------------------
    // get
    // ------------------------------------------------------------------

    private void onGet(CommandSender sender, String[] args) {
        // get [as <玩家>] <占位符>
        int index = 1;
        Player target = null;

        if (index < args.length && args[index].equalsIgnoreCase("as")) {
            if (index + 1 >= args.length) {
                sender.sendMessage("用法：/placeholdercommand get [as <玩家>] <占位符>");
                return;
            }
            target = Bukkit.getPlayerExact(args[index + 1]);
            if (target == null) {
                sender.sendMessage("[ No Player ]");
                return;
            }
            index += 2;
        }

        if (index >= args.length) {
            sender.sendMessage("用法：/placeholdercommand get [as <玩家>] <占位符>");
            return;
        }

        String placeholder = normalizePlaceholder(args[index]);

        String result = renderFor(sender, target, placeholder);

        // 纯文本输出：剥离联邦颜色/格式代码
        sender.sendMessage(strip(result));
    }

    // ------------------------------------------------------------------
    // gets
    // ------------------------------------------------------------------

    private void onGets(CommandSender sender, String[] args) {
        // gets [as <玩家>] <占位符1> <占位符2> ...
        int index = 1;
        Player target = null;

        if (index < args.length && args[index].equalsIgnoreCase("as")) {
            if (index + 1 >= args.length) {
                sender.sendMessage("用法：/placeholdercommand gets [as <玩家>] <占位符1> <占位符2> ...");
                return;
            }
            target = Bukkit.getPlayerExact(args[index + 1]);
            if (target == null) {
                sender.sendMessage("[ No Player ]");
                return;
            }
            index += 2;
        }

        if (index >= args.length) {
            sender.sendMessage("用法：/placeholdercommand gets [as <玩家>] <占位符1> <占位符2> ...");
            return;
        }

        // 逐个解析占位符并逐行输出
        for (int i = index; i < args.length; i++) {
            String raw = args[i];
            String placeholder = normalizePlaceholder(raw);
            String value = renderFor(sender, target, placeholder);
            // 剥离颜色/格式代码，保证纯文本输出
            sender.sendMessage(placeholder + " = " + strip(value));
        }
    }

    // ------------------------------------------------------------------
    // list
    // ------------------------------------------------------------------

    private void onList(CommandSender sender) {
        // 判断 PlaceholderAPI 插件本体是否已加载。
        // 注意：不能用 PlaceholderAPI.isRegistered(String) —— 它检查的是
        // 某个占位符标识符（如 "player"）是否注册，而非插件是否加载。
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            sender.sendMessage("PlaceholderAPI 未加载，无法列出占位符。");
            return;
        }

        List<String> placeholders = new ArrayList<>(PlaceholderAPI.getRegisteredIdentifiers());
        placeholders.sort(String.CASE_INSENSITIVE_ORDER);

        if (placeholders.isEmpty()) {
            sender.sendMessage("[ No Placeholder ]");
            return;
        }

        for (String id : placeholders) {
            sender.sendMessage("%" + id + "%");
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    /**
     * 以指定上下文（target 若为 null 则使用 sender 自身）渲染文本。
     * 若 sender 是玩家，自动使用其上下文；否则仅当明确指定 target 时使用玩家上下文。
     */
    private String renderFor(CommandSender sender, Player target, String text) {
        Player context = target;
        if (context == null && sender instanceof Player) {
            context = (Player) sender;
        }

        if (context != null)
            return PlaceholderAPI.setPlaceholders(context, text);

        // 控制台且未指定玩家：仅解析服务器级占位符
        return PlaceholderAPI.setPlaceholders(null, text);
    }

    /** 去除联邦颜色/格式代码，保证纯文本输出。 */
    private String strip(String input) {
        if (input == null) return "";
        return input.replaceAll("(?i)&[0-9a-fk-orx]", "")
                .replace('\u00A7', ' ');
    }

    /**
     * 将用户输入规范化为占位符格式。
     * <p>如果输入已包含 {@code %...%} 形式则原样返回；否则自动补全为 {@code %input%}。</p>
     */
    private String normalizePlaceholder(String input) {
        String trimmed = input.trim();
        if (trimmed.startsWith("%") && trimmed.endsWith("%"))
            return trimmed;
        return "%" + trimmed + "%";
    }

    // ------------------------------------------------------------------
    // tab completion
    // ------------------------------------------------------------------

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);

        // 第一级：render / get / gets / list
        if (args.length == 1) {
            return filter(SUBCOMMANDS, prefix);
        }

        // 第二级：render / get / gets 后可选 "as"，或直接是 <文本>/<占位符>
        if (args.length == 2) {
            List<String> candidates = new ArrayList<>();
            candidates.add("as");
            candidates.addAll(playerNames());
            return filter(candidates, prefix);
        }

        // 第三级：render as <玩家> / get as <玩家> / gets as <玩家> —— 补全玩家名
        if (args.length == 3 && args[1].equalsIgnoreCase("as")) {
            return filter(playerNames(), prefix);
        }

        return Collections.emptyList();
    }

    /** 返回以指定前缀开头的候选列表（忽略大小写）。 */
    private List<String> filter(List<String> candidates, String prefix) {
        List<String> result = new ArrayList<>();
        for (String s : candidates) {
            if (s.toLowerCase(Locale.ROOT).startsWith(prefix))
                result.add(s);
        }
        return result;
    }

    /** 收集当前在线玩家名（Java 8 兼容：getOnlinePlayers() 返回 Collection）。 */
    private List<String> playerNames() {
        List<String> names = new ArrayList<>();
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();
        for (Player p : online) {
            names.add(p.getName());
        }
        return names;
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage("用法:");
        sender.sendMessage("  /" + label + " render [as <玩家>] <文本>");
        sender.sendMessage("  /" + label + " get [as <玩家>] <占位符>");
        sender.sendMessage("  /" + label + " gets [as <玩家>] <占位符1> <占位符2> ...");
        sender.sendMessage("  /" + label + " list");
    }
}