package org.manhdev;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Scalar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

public class ImageProcessingApp extends JFrame {
    private JLabel imageLabel;
    private Mat originalImage;

    public ImageProcessingApp() {
        setTitle("Image Processing Application");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // JLabel để hiển thị ảnh
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        // Nút chọn file
        JButton loadButton = new JButton("Load Image");
        loadButton.addActionListener(e -> loadImage());

        // Nút xử lý ảnh âm tính
        JButton negativeButton = new JButton("Negative");
        negativeButton.addActionListener(e -> processNegative());

        // Nút tăng độ tương phản
        JButton contrastButton = new JButton("Increase Contrast");
        contrastButton.addActionListener(e -> processContrast());

        // Nút biến đổi log
        JButton logButton = new JButton("Log Transform");
        logButton.addActionListener(e -> processLogTransform());

        // Nút cân bằng histogram
        JButton histButton = new JButton("Equalize Histogram");
        histButton.addActionListener(e -> processHistogramEqualization());

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        buttonPanel.add(negativeButton);
        buttonPanel.add(contrastButton);
        buttonPanel.add(logButton);
        buttonPanel.add(histButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Hàm tải ảnh từ file
    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            originalImage = opencv_imgcodecs.imread(file.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
            if (!originalImage.empty()) {
                ImageIcon icon = new ImageIcon(matToBufferedImage(originalImage));
                imageLabel.setIcon(icon);
            }
        }
    }

    // Hàm xử lý ảnh âm tính
    private void processNegative() {
        if (originalImage != null) {
            Mat negativeImage = new Mat();
            opencv_core.subtract(opencv_core.Scalar.all(255), originalImage, negativeImage);
            displayImage(negativeImage);
        }
    }

    // Hàm tăng độ tương phản
    private void processContrast() {
        if (originalImage != null) {
            Mat contrastImage = new Mat();
            originalImage.convertTo(contrastImage, -1, 1.5, 20); // Alpha = 1.5, Beta = 20
            displayImage(contrastImage);
        }
    }

    // Hàm biến đổi log
    private void processLogTransform() {
        if (originalImage != null) {
            Mat logImage = new Mat(originalImage.size(), originalImage.type());
            originalImage.convertTo(logImage, opencv_core.CV_32F);
            opencv_core.add(logImage, opencv_core.Scalar.all(1), logImage);  // Tránh log(0)
            opencv_core.log(logImage, logImage);
            opencv_core.multiply(logImage, opencv_core.Scalar.all(255 / Math.log(256)), logImage);
            logImage.convertTo(logImage, opencv_core.CV_8U);
            displayImage(logImage);
        }
    }

    // Hàm cân bằng Histogram
    private void processHistogramEqualization() {
        if (originalImage != null) {
            Mat histImage = new Mat();
            opencv_imgproc.equalizeHist(originalImage, histImage);
            displayImage(histImage);
        }
    }

    // Hiển thị ảnh trên JLabel
    private void displayImage(Mat img) {
        ImageIcon icon = new ImageIcon(matToBufferedImage(img));
        imageLabel.setIcon(icon);
    }

    // Chuyển đổi Mat sang BufferedImage để hiển thị trong Swing
    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage img = new BufferedImage(mat.cols(), mat.rows(), type);
        ByteBuffer buffer = ByteBuffer.allocate(mat.channels() * mat.cols() * mat.rows());
        mat.data().get(buffer.array());
        img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), buffer.array());
        return img;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageProcessingApp app = new ImageProcessingApp();
            app.setVisible(true);
        });
    }
}