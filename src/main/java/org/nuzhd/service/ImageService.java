package org.nuzhd.service;

import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

@Service
public class ImageService {

    public void corruptImage(String chatId, float corruptionLevel) throws IOException {

        File input = new File("src/main/resources/photos/" + chatId + "/image.jpg");
        BufferedImage image = ImageIO.read(input);

        File compressedImageFile = new File("src/main/resources/photos/" + chatId + "/compressed_image.jpg");
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(corruptionLevel);  // Change the quality value you prefer
        writer.write(null, new IIOImage(image, null, null), param);

        os.close();
        ios.close();
        writer.dispose();
    }

    public void saveImage(String chatId, BufferedImage image) throws IOException {

        File outputFile = new File("src/main/resources/photos/" + chatId + "/image.jpg");
        ImageIO.write(image, "jpg", outputFile);
    }
}
