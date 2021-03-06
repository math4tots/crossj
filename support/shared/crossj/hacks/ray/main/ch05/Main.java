package crossj.hacks.ray.main.ch05;

import crossj.base.IO;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.DeprecatedRay;
import crossj.hacks.ray.geo.DeprecatedSphere;
import crossj.hacks.ray.math.Matrix;

public final class Main {
    public static void main(String[] args) {
        var rayOrigin = Matrix.point(0, 0, -5);
        var wallZ = 10.0;
        var wallSize = 7.0;
        int canvasPixels = 100;
        var pixelSize = wallSize / canvasPixels;
        var half = wallSize / 2;
        var canvas = Bitmap.withDimensions(canvasPixels, canvasPixels);
        var color = Color.RED;
        var shape = DeprecatedSphere.unit();

        for (int y = 0; y < canvasPixels; y++) {
            var worldY = half - pixelSize * y;
            for (int x = 0; x < canvasPixels; x++) {
                var worldX = -half + pixelSize * x;
                var position = Matrix.point(worldX, worldY, wallZ);

                var r = DeprecatedRay.of(rayOrigin, position.subtract(rayOrigin).normalize());
                var xs = shape.intersectRay(r);

                if (xs.getHit().isPresent()) {
                    canvas.setColor(x, y, color);
                }
            }
        }

        IO.writeFileBytes("out/ray/ch05.bmp", canvas.toBMPBytes());
    }
}
