package me.indian.pl.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import me.indian.pl.OsTag;
import me.indian.pl.utils.ColorUtil;
import me.indian.pl.utils.OtherUtils;

import java.util.HashMap;
import java.util.UUID;

import static me.indian.pl.utils.PlayerInfoUtil.*;

public class Formater implements Listener {

    private HashMap<UUID, Long> cooldown = new HashMap<>();
    private final OsTag plugin;

    public Formater(OsTag plugin) {
        this.plugin = plugin;
    }

    // IndianPL
    //Chat formater for Nukkit
    //https://github.com/IndianBartonka/LuckPermChatFormater


    @EventHandler
    public void playerChatFormat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        Config conf = plugin.getConfig();
        String wiad = e.getMessage();

        String cenzor = plugin.getConfig().getString("censorship.word");

        //conzorship is a experimental option, maybe not good working
        for (String czarnalista : plugin.getConfig().getStringList("BlackWords")) {
            if (e.getMessage().toLowerCase().contains(czarnalista.toLowerCase())) {
                if (e.getMessage().toLowerCase().contains("Huje22".toLowerCase())) {
                    return;
                }
            }
            if (plugin.getConfig().getBoolean("censorship.enable")) {
                if (!(p.isOp())) {
                    msg = e.getMessage().toLowerCase().replace(czarnalista.toLowerCase(), cenzor);
                }
                e.setMessage(msg);
            }
            if (p.hasPermission("ostag.admin") || p.hasPermission("ostag.colors") || conf.getBoolean("and-for-all")) {
                wiad = TextFormat.colorize('&' , e.getMessage());
            } else {
                wiad = e.getMessage();
            }
            e.setMessage(wiad);

            String messageformat = ColorUtil.replaceColorCode(plugin.getConfig().getString("message-format"));

            if(plugin.papKot){
                PlaceholderAPI api = PlaceholderAPI.getInstance();
                messageformat = api.translateString(ColorUtil.replaceColorCode(conf.getString("message-format")), p);
            }
            

            e.setFormat(messageformat
                            .replace("<name>", p.getName())
                            .replace("<suffix>", getLuckPermSufix(p))
                            .replace("<prefix>", getLuckPermPrefix(p))
                            .replace("<msg>", e.getMessage())
                            .replace("<groupDisName>", getGroupDisName(p))
                            .replace("<device>", getDevice(p))
                            .replace("<health>", p.getHealth() + "")
                            .replace("<model>", p.getLoginChainData().getDeviceModel() + "")
                            .replace("<version>", p.getLoginChainData().getGameVersion())
                            .replace("<language>", p.getLoginChainData().getLanguageCode())
                            .replace("<ping>", getPing(p))
                            .replace("<xp>", getXp(p))
                            .replace("<dimension>", getDimension(p))
                            .replace("<unique-description>", getPlayerUnique(p))


                            .replace("\n", " this action not allowed here ")
                    //message.format: "<prefix> <player> <suffix> >> <msg>

            );

        }
    }

    @EventHandler
    public void cooldownMessage(PlayerChatEvent e) {
        //cooldown is a experimental option, maybe not good working
        Player p = (Player) e.getPlayer();
        Config conf = plugin.getConfig();
        Long time = conf.getLong("cooldown.delay") * 100;
        if (conf.getBoolean("cooldown.enable")) {

            if (!cooldown.containsKey(p.getUniqueId()) || System.currentTimeMillis() - cooldown.get(p.getUniqueId()) > time) {
                if (!(p.isOp())) {
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                }
                if (conf.getBoolean("break-between-messages.enable")) {
                    Server.getInstance().getScheduler().scheduleDelayedTask(null, () -> OtherUtils.sendMessageToAll(" "), 1);
                }
            } else {
                long cooldownTime = (time - (System.currentTimeMillis() - cooldown.get(p.getUniqueId()))) / 100;
                e.setCancelled(true);
                if (conf.getBoolean("break-between-messages.enable")) {
                    p.sendMessage(" ");
                }
                p.sendMessage(ColorUtil.replaceColorCode(conf.getString("cooldown.message")
                        .replace("<left>", cooldownTime + "")));
            }
        }
    }
}