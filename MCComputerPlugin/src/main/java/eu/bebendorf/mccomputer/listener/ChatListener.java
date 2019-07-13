package eu.bebendorf.mccomputer.listener;

import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.execution.RuntimeEvent;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.ScreenAPI;
import eu.bebendorf.mcscreen.api.helper.ScreenPixel;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@AllArgsConstructor
public class ChatListener implements Listener {

    private MCComputer plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Screen screen = ScreenAPI.getInstance().getScreen(e.getPlayer());
        if(screen == null)
            return;
        e.setCancelled(true);
        ScreenPixel pixel = screen.getPixel(e.getPlayer());
        plugin.getComputerManager().getComputers(screen).forEach(computer -> {
            computer.event(RuntimeEvent.CONSOLE_INPUT)
                    .param("screen", screen.getId())
                    .param("player", e.getPlayer().getUniqueId().toString())
                    .param("x", pixel.getX())
                    .param("y", pixel.getY())
                    .param("message", e.getMessage())
                    .dispatch();
        });
    }

}
