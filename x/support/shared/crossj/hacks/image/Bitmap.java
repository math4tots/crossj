package crossj.hacks.image;

import crossj.Assert;
import crossj.Bytes;
import crossj.IntArray;

/**
 * Quick and dirty class for playing with bitmaps
 *
 * Coordinates start from (0, 0) in the upper-left corner
 *
 * (NOTE, this is different from the BMP file format which starts from (0, 0) in
 * the lower-left corner).
 *
 */
public final class Bitmap {
    private final int width;
    private final IntArray data; // array of 32-bit RGBA data

    private Bitmap(int width, IntArray data) {
        Assert.divides(width, data.size());
        this.width = width;
        this.data = data;
    }

    public static Bitmap withDimensions(int width, int height) {
        IntArray data = IntArray.withSize(width * height);
        return new Bitmap(width, data);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return data.size() / width;
    }

    public void setPixel(int x, int y, Pixel pixel) {
        int i = y * width + x;
        data.set(i, pixel.toI32RGBA());
    }

    public Pixel getPixel(int x, int y) {
        int i = y * width + x;
        return Pixel.fromI32RGBA(data.get(i));
    }

    /**
     * Converts out the bitmap data into 32bpp BMP data, and returns
     * the contents as a Bytes value.
     * This Bytes object can be directly written out to a file and
     * be a valid *.bmp.
     *
     * Reference:
     * https://en.wikipedia.org/wiki/BMP_file_format
     *
     * @return
     */
    public Bytes toBMPBytes() {
        // 14                (file header)
        // 40                (dib header (Windows BITMAPINFOHEADER))
        // 4 * data.size()   (pixel data)
        int bytesize = 14 + 40 + 4 * data.size();
        int width = getWidth();
        int height = getHeight();

        Bytes out = Bytes.withCapacity(bytesize);

        // -- file header --
        out.addBytes(Bytes.fromASCII("BM")); // magic
        out.addI32(bytesize); // size of BMP file in bytes
        out.addU16(0); // reserved
        out.addU16(0); // reserved
        out.addI32(14 + 40); // offset to start of pixel data
        Assert.equals(out.size(), 14);

        // -- dib header (Windows BITMAPINFOHEADER) --
        out.addI32(40); // size of this header in bytes
        out.addI32(width); // width in pixels
        out.addI32(height); // height in pixels
        out.addU16(1); // # color planes (must be 1?)
        out.addU16(32); // bits per pixel
        out.addI32(0); // compression (none)
        out.addI32(0); // uncompressed image size (if compressed)
        out.addI32(2835); // horizontal resolution (2835 used in wiki example)
        out.addI32(2835); // vertical resolution (2835 used in wiki example)
        out.addI32(0); // # colors in palette (palette is not used here)
        out.addI32(0); // # important colors (generally ignored)
        Assert.equals(out.size(), 14 + 40);

        // -- pixel data --
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                out.addI32(getPixel(x, y).toI32ARGB());
            }
        }
        Assert.equals(out.size(), bytesize);

        return out;
    }
}
