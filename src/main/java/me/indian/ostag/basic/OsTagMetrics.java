package me.indian.ostag.basic;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import me.indian.ostag.OsTag;
import me.indian.ostag.util.MessageUtil;
import me.indian.ostag.util.ThreadUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OsTagMetrics {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadUtil("Ostag Metrics Thread"));
    private final OsTag plugin = OsTag.getInstance();
    private final Server server = plugin.getServer();
    private final Config config = this.plugin.getConfig();
    private final PluginLogger logger = this.plugin.getLogger();
    private final Metrics metrics = new Metrics(this.plugin);
    public boolean enabled = this.metrics.isEnabled();

    public void run() {
        executorService.execute(() -> {
            try {
                if (!this.enabled) {
                    this.logger.info(MessageUtil.colorize("&aMetrics is disabled"));
                    Thread.currentThread().interrupt();
                    return;
                }
                this.customMetrics();
                this.logger.info(MessageUtil.colorize("&aLoaded Metrics"));
            } catch (final Exception e) {
                this.logger.info(MessageUtil.colorize("&cCan't load metrics"));
                if (this.plugin.debug) {
                    e.printStackTrace();
                }
                Thread.currentThread().interrupt();
            }
        });
    }

    private void customMetrics() {
        this.metrics.addCustomChart(new Metrics.SimplePie("server_movement", () -> String.valueOf(this.plugin.serverMovement)));
        this.metrics.addCustomChart(new Metrics.SimplePie("nukkit_version", () -> server.getNukkitVersion() + " (MC: " + server.getVersion() + " Nukkit API: " + server.getApiVersion() + ")"));
        this.metrics.addCustomChart(new Metrics.SimplePie("refresh_time", () -> {
            if (this.plugin.osTag) {
                return this.plugin.getOsTimer().getRefreshTime() + " ticks";
            } else {
                return "";
            }
        }));


        this.metrics.addCustomChart(new Metrics.AdvancedPie("functions", () -> {
            final Map<String, Integer> functionMap = new HashMap<>();

            final boolean ostag = plugin.osTag;
            final boolean formater = plugin.chatFormatter;
            final boolean cpsLimiter = plugin.cpsLimiter;
            final boolean nametag = this.plugin.nametag;
            final boolean scoreTag = this.plugin.scoreTag;
            final boolean update = this.plugin.upDatechecker;
            final boolean auto = this.config.getBoolean("AutoUpdate");
            final boolean debug = this.plugin.debug;
            final boolean censor = this.config.getBoolean("censorship.enable");
            final boolean breaks = this.config.getBoolean("break-between-messages.enable");
            final boolean cooldown = this.config.getBoolean("cooldown.enable");
            final boolean andForAll = this.config.getBoolean("And-for-all");
            final boolean formsDebug = this.config.getBoolean("FormsDebug");

            if (ostag) {
                functionMap.put("OsTag", 1);
            }
            if (formater) {
                functionMap.put("ChatFormatter", 1);
            }
            if (cpsLimiter) {
                functionMap.put("CpsLimiter", 1);
            }
            if (scoreTag) {
                functionMap.put("ScoreTag", 1);
            }
            if (nametag) {
                functionMap.put("NmaeTag", 1);
            }
            if (update) {
                functionMap.put("UpdateChecker", 1);
            }
            if (auto) {
                functionMap.put("AutoUpdate", 1);
            }
            if (debug) {
                functionMap.put("Debug", 1);
            }
            if (censor) {
                functionMap.put("Censorship", 1);
            }
            if (breaks) {
                functionMap.put("Breaks Between Messages", 1);
            }
            if (cooldown) {
                functionMap.put("Cooldown", 1);
            }
            if (andForAll) {
                functionMap.put("And For All", 1);
            }
            if(formsDebug){
                functionMap.put("Froms Debug" , 1);
            }

            return functionMap;
        }));

        this.metrics.addCustomChart(new Metrics.AdvancedPie("plugins", () -> {
            final Map<String, Plugin> serverPluginsMap = this.server.getPluginManager().getPlugins();
            final Map<String, Integer> pluginMap = new HashMap<>();

            for (Map.Entry<String, Plugin> entry : serverPluginsMap.entrySet()) {
                final String key = entry.getKey();
                if (key.equalsIgnoreCase("LuckPerms") || key.equalsIgnoreCase("PlaceholderAPI") || key.equalsIgnoreCase("KotlinLib") || key.equalsIgnoreCase("FormConstructor")) {
                    if (!pluginMap.containsKey(key)) {
                        pluginMap.put(key, 1);
                    }
                }
            }
            return pluginMap;
        }));

        /*
        Code from https://github.com/CloudburstMC/Nukkit/blob/master/src/main/java/cn/nukkit/metrics/NukkitMetrics.java#L47
         */
        this.metrics.addCustomChart(new Metrics.SimplePie("xbox_auth", () -> this.server.getPropertyBoolean("xbox-auth") ? "Required" : "Not required"));
        this.metrics.addCustomChart(new Metrics.AdvancedPie("player_platform", () -> {
            final Map<String, Integer> valueMap = new HashMap<>();
            this.server.getOnlinePlayers().forEach((uuid, player) -> {
                final String deviceOS = this.mapDeviceOSToString(player.getLoginChainData().getDeviceOS());
                if (!valueMap.containsKey(deviceOS)) {
                    valueMap.put(deviceOS, 1);
                } else {
                    valueMap.put(deviceOS, valueMap.get(deviceOS) + 1);
                }
            });
            return valueMap;
        }));

        this.metrics.addCustomChart(new Metrics.AdvancedPie("player_game_version", () -> {
            final Map<String, Integer> valueMap = new HashMap<>();
            this.server.getOnlinePlayers().forEach((uuid, player) -> {
                final String gameVersion = player.getLoginChainData().getGameVersion();
                if (!valueMap.containsKey(gameVersion)) {
                    valueMap.put(gameVersion, 1);
                } else {
                    valueMap.put(gameVersion, valueMap.get(gameVersion) + 1);
                }
            });
            return valueMap;
        }));
    }

    private String mapDeviceOSToString(final int os) {
        switch (os) {
            case 1:
                return "Android";
            case 2:
                return "iOS";
            case 3:
                return "macOS";
            case 4:
                return "FireOS";
            case 5:
                return "Gear VR";
            case 6:
                return "Hololens";
            case 7:
            case 8:
                return "Windows";
            case 9:
                return "Dedicated";
            case 10:
                return "tvos";
            case 11:
                return "PlayStation";
            case 12:
                return "Switch";
            case 13:
                return "Xbox One";
            case 14:
                return "Windows Phone";
            case 15:
                return "Linux";
        }
        return "Unknown";
    }
}
