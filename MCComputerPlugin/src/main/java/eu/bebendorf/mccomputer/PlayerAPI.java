package eu.bebendorf.mccomputer;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
public class PlayerAPI {

    private Player player;

    public void sendMessage(String message){
        player.sendMessage(message);
    }

    public void sendChat(int type, String json){
        sendPacket(player, packetV1(type, json));
    }

    private Object packetV1(int type, String json){
        try {
            Object chatComponent = getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, json);
            return getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class).newInstance(chatComponent, (byte) type);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendPacket(Player player, Object packet) {
        if(packet == null)
            return;
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
