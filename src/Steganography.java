import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Steganography {

    public static void main(String[] args) {

        if (args.length < 2 || args.length > 3) {
            System.out.println("steganography <input image> <output image> [payload file]");
            return;
        }

        Stopwatch stopwatch = new Stopwatch();

        try {
            if (args.length == 3) {
                try {
                    ImageIO.write(encode(ImageIO.read(new File(args[0])), new BitInputStream(new File(args[2]))), "PNG", new File(args[1]));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } else if (args.length == 2) {
                decode(ImageIO.read(new File(args[0])), new BitOutputStream(new FileOutputStream(args[1])));
            }

            System.out.println(String.format("done %sms", stopwatch.getTime() / 1000000));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static BufferedImage encode(BufferedImage carrier, BitInputStream payload) throws Exception {
        for (int y = 0; y < carrier.getHeight(); y++) {
            for (int x = 0; x < carrier.getWidth(); x++) {
                int pixel = carrier.getRGB(x, y) & 0xFFFCFCFC;
                for (int offset = 16; offset >= 0; offset -= 8) {
                    int bits = payload.readBits(2);
                    if (bits == -1)
                        return carrier;
                    pixel |= bits << offset;
                }
                carrier.setRGB(x, y, pixel);
            }
        }
        throw new Exception("not enough space");
    }

    public static void decode(BufferedImage carrier, BitOutputStream payload) throws IOException {
        for (int y = 0; y < carrier.getHeight(); y++) {
            for (int x = 0; x < carrier.getWidth(); x++) {
                for (int offset = 16; offset >= 0; offset -= 8) {
                    payload.write(2, (carrier.getRGB(x, y) >> offset) & 0x3);
                }
            }
        }
        payload.close();
    }
}