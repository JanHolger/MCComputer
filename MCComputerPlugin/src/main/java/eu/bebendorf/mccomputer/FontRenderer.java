package eu.bebendorf.mccomputer;

import eu.bebendorf.mcscreen.api.helper.ImageWrapper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FontRenderer {

    public static ImageWrapper render(String text, String font, int size, int r, int g, int b){
        return render(text, new Font(font, Font.PLAIN, size), r, g, b);
    }

    public static ImageWrapper render(String text, File ttfFile, int size, int r, int g, int b){
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ttfFile);
            return render(text, font.deriveFont((float) size), r, g, b);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageWrapper render(String text, InputStream ttfFile, int size, int r, int g, int b){
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ttfFile);
            return render(text, font.deriveFont((float) size), r, g, b);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageWrapper render(String text, Font font, int r, int g, int b){
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(new Color(r, g, b));
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        return new ImageWrapper(img);
    }

}
