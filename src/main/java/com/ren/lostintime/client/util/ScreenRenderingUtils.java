package com.ren.lostintime.client.util;

import com.ren.lostintime.client.screen.book.PageComponent;

import java.awt.*;

public class ScreenRenderingUtils {

    public static void centerHorizontally(Rectangle centerBounds, PageComponent target){
        target.setX(centerBounds.x + (centerBounds.width - target.getBounds().width) / 2);
    }
    public static void centerVertically(Rectangle centerBounds, PageComponent target){
        target.setY(centerBounds.y + (centerBounds.height - target.getBounds().height) / 2);
    }

    public static void center(Rectangle centerBounds, PageComponent target){
        centerHorizontally(centerBounds, target);
        centerVertically(centerBounds, target);
    }
}
