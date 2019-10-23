package com.shallin.code;

import com.sun.deploy.net.HttpResponse;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptcahCode {

    public static char randomChar(){
        String string="QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random = new Random();
        return string.charAt(random.nextInt(string.length()));
    }

    public static String drawImage(HttpServletResponse response){
        StringBuilder builder= new StringBuilder();
        for (int i=0;i<4;i++){
            builder.append(randomChar());
        }
        String code = builder.toString();
        //定义图片的宽度和高度
        int width =70;
        int height= 25;
        //建立bufferedImage对象，制定图片的长度和宽度以及色彩
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        //3:获取到 Graphics2D 绘制对象，开始绘制验证码
        Graphics2D graphics2D=bi.createGraphics();
        //设置文字的字体和大小
        Font font = new Font("微软雅黑",Font.PLAIN,20);
        //设置字体的颜色
        Color color =new Color(0,0,0);
        //设置字体
        graphics2D.setFont(font);
        //设置颜色
        graphics2D.setColor(color);
        //设置背景
        graphics2D.setBackground(new Color(226,226,240));
        //开始绘制
        graphics2D.clearRect(0,0,width,height);
        //绘制形状，获取矩形对象
        FontRenderContext context = graphics2D.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(code,context);
        //计算文件的坐标和间距
        double x = (width - bounds.getWidth())/2;
        double y = (height - bounds.getHeight())/2;
        double ascent = bounds.getY();
        double baseY = y - ascent;
        graphics2D.drawString(code,(int)x,(int)baseY);
        //结束绘制
        graphics2D.dispose();
        try {
            ImageIO.write(bi,"jpg",response.getOutputStream());
            //刷新响应流
            response.flushBuffer();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return code;
    }

}
