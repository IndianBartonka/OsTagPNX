package me.indian.ostag.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.utils.Config;
import me.indian.ostag.OsTag;
import me.indian.ostag.util.GithubUtil;
import me.indian.ostag.util.Permissions;

public class PlayerJoinListener implements Listener {

    private final OsTag plugin;

    public PlayerJoinListener(OsTag plugin) {
        this.plugin = plugin;
    }


    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Config config = plugin.getConfig();
        final String compatibility = GithubUtil.checkTagCompatibility();

        if (player.hasPermission(Permissions.ADMIN) || player.isOp()) {
            if (config.getBoolean("UpdateChecker")) {
                player.sendMessage(plugin.pluginPrefix + compatibility);
            }
        }
    }
}
