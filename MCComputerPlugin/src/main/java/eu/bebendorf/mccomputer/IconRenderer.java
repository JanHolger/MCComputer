package eu.bebendorf.mccomputer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.bebendorf.mcscreen.api.helper.ImageWrapper;
import lombok.AllArgsConstructor;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IconRenderer {

    private static Map<IconStyle, IconFont> fonts = null;

    private static void load(){
        fonts = new HashMap<>();
        for(IconStyle iconStyle : IconStyle.values()){
            String style = iconStyle.name().toLowerCase(Locale.GERMAN);
            fonts.put(iconStyle, new IconFont(loadTable(style), loadFont(style)));
        }
    }

    private static Font loadFont(String style){
        try {
            return Font.createFont(Font.TRUETYPE_FONT, IconRenderer.class.getClassLoader().getResourceAsStream("fontawesome/"+style+".ttf"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, Character> loadTable(String style){
        Map<String, Character> table = new HashMap<>();
        JsonObject tableJson = new GsonBuilder().create().fromJson(readStream(IconRenderer.class.getClassLoader().getResourceAsStream("fontawesome/"+style+".json")), JsonObject.class);
        for(Map.Entry<String, JsonElement> e : tableJson.entrySet()){
            table.put(e.getKey(), codeToChar(e.getValue().getAsString()));
        }
        return table;
    }

    private static String readStream(InputStream stream){
        StringBuilder sb = new StringBuilder();
        try {
            while (stream.available() > 0){
                byte[] data = new byte[Math.min(stream.available(), 4096)];
                stream.read(data);
                sb.append(new String(data, StandardCharsets.UTF_8));
            }
        }catch (IOException ex){}
        return sb.toString();
    }

    private static char codeToChar(String code) {
        return (char) Integer.parseInt(code, 16);
    }

    public static ImageWrapper render(String style, String icon, int size, int r, int g, int b){
        return render(IconStyle.get(style), icon, size, r, g, b);
    }

    public static ImageWrapper render(IconStyle style, String icon, int size, int r, int g, int b){
        if(fonts == null){
            load();
        }
        IconFont iconFont = fonts.get(style);
        if(iconFont.chars.containsKey(icon)){
            return FontRenderer.render(Character.toString(iconFont.chars.get(icon)), iconFont.font.deriveFont((float) size), r, g, b);
        }
        return null;
    }

    public enum IconStyle {
        SOLID,
        REGULAR,
        BRANDS;
        public static IconStyle get(String value){
            return valueOf(value.toUpperCase(Locale.GERMAN));
        }
    }

    @AllArgsConstructor
    private static class IconFont {
        Map<String, Character> chars;
        Font font;
    }

}
