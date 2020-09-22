package graphics.core.io;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageData {
    private BufferedImage bufferedImage;
    private ByteBuffer byteBuffer;
    private int width, height;

    ImageData(BufferedImage bufferedImage, ByteBuffer byteBuffer){
        this.bufferedImage = bufferedImage;
        this.byteBuffer = byteBuffer;
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
