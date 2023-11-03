package com.example.tgbot.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

public class ImageService {
    public static String getPathImage(String path, String text){
        try {
            BufferedImage image = ImageIO.read(new File(path));

            Graphics2D graphics = image.createGraphics();
            int size = image.getHeight() / 20;
            Font font = new Font("Monospaced", Font.PLAIN, size);
            graphics.setFont(font);
            graphics.setColor(Color.WHITE);

            int x = image.getWidth() / 100;
            int y = image.getHeight() / 15;
            for (String str : text.split("\n")){
                graphics.drawString(str, x, y);
                y += (int) (font.getSize() * 1.2);
            }


            String newPath = path.replace("_", "_" + new Date().toString().replace(":", "-")).replace("images", "weatherimages");
            ImageIO.write(image, "jpg", new File(newPath));
            return newPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
