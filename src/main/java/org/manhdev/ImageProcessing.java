package org.manhdev;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageProcessing {

    public static void main(String[] args) throws Exception {
        // Đọc ảnh
        BufferedImage image = ImageIO.read(new File("/Users/nguyenthemanh/Downloads/anh-phong-canh-66-1.jpg"));

        // Ảnh âm tính
        BufferedImage negativeImage = negative(image);
        ImageIO.write(negativeImage, "jpg", new File("negative.jpg"));

        // Tăng độ tương phản
        BufferedImage contrastImage = increaseContrast(image);
        ImageIO.write(contrastImage, "jpg", new File("contrast.jpg"));

        // Biến đổi log
        BufferedImage logImage = logTransform(image);
        ImageIO.write(logImage, "jpg", new File("log_transform.jpg"));

        // Cân bằng Histogram
        BufferedImage histEqualizedImage = histogramEqualization(image);
        ImageIO.write(histEqualizedImage, "jpg", new File("histogram_equalized.jpg"));
    }

    // Ảnh âm tính
    public static BufferedImage negative(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, img.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int r = 255 - ((rgb >> 16) & 0xff);
                int g = 255 - ((rgb >> 8) & 0xff);
                int b = 255 - (rgb & 0xff);
                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    // Tăng độ tương phản đơn giản
    public static BufferedImage increaseContrast(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, img.getType());
        int factor = 2; // Độ tương phản

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int r = clamp(((rgb >> 16) & 0xff) * factor);
                int g = clamp(((rgb >> 8) & 0xff) * factor);
                int b = clamp((rgb & 0xff) * factor);
                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    // Biến đổi log
    public static BufferedImage logTransform(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, img.getType());
        double c = 255 / Math.log(256);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int r = clamp((int) (c * Math.log(1 + ((rgb >> 16) & 0xff))));
                int g = clamp((int) (c * Math.log(1 + ((rgb >> 8) & 0xff))));
                int b = clamp((int) (c * Math.log(1 + (rgb & 0xff))));
                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    // Cân bằng Histogram (rất cơ bản)
    public static BufferedImage histogramEqualization(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, img.getType());

        int[] histogram = new int[256];
        int[] cumulativeHistogram = new int[256];

        // Tạo histogram
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (int) (0.299 * ((rgb >> 16) & 0xff) +
                        0.587 * ((rgb >> 8) & 0xff) +
                        0.114 * (rgb & 0xff));
                histogram[gray]++;
            }
        }

        // Tạo histogram tích lũy
        cumulativeHistogram[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            cumulativeHistogram[i] = cumulativeHistogram[i - 1] + histogram[i];
        }

        // Áp dụng cân bằng
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (int) (0.299 * ((rgb >> 16) & 0xff) +
                        0.587 * ((rgb >> 8) & 0xff) +
                        0.114 * (rgb & 0xff));
                int newGray = (cumulativeHistogram[gray] * 255) / (width * height);
                int newRgb = (newGray << 16) | (newGray << 8) | newGray;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    // Giới hạn giá trị pixel trong khoảng [0, 255]
    public static int clamp(int value) {
        return Math.min(255, Math.max(0, value));
    }
}
