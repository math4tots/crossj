package crossj.hacks.ray3.geo;

import crossj.Assert;
import crossj.M;
import crossj.Optional;
import crossj.Rand;
import crossj.hacks.ray.Matrix;

public final class Sphere implements Surface {
    private Matrix transform;
    private Matrix transformInverse = null;
    private Matrix transformInverseTranspose = null;

    private Sphere(Matrix transform) {
        this.transform = transform;
    }

    /**
     * Create a unit sphere centered at the origin
     */
    public static Sphere unit() {
        return withTransform(Matrix.identity(4));
    }

    /**
     * Returns a the surface created by applying the given transform to a unit sphere.
     */
    public static Sphere withTransform(Matrix transform) {
        Assert.withMessage(transform.getC() == 4 && transform.getR() == 4,
                "Sphere.withTransform expects a 4x4 transformation matrix");
        return new Sphere(transform);
    }

    public Matrix getTransform() {
        return transform;
    }

    public void setTransform(Matrix transform) {
        this.transform = transform;
        transformInverse = null;
        transformInverseTranspose = null;
    }

    private Matrix getTransformInverse() {
        if (transformInverse == null) {
            transformInverse = transform.inverse();
        }
        return transformInverse;
    }

    private Matrix getTransformInverseTranspose() {
        if (transformInverseTranspose == null) {
            transformInverseTranspose = getTransformInverse().transpose();
        }
        return transformInverseTranspose;
    }

    /**
     * Select a random point from a unit sphere
     */
    public static Matrix randomPointOnUnitSphere() {
        var rng = Rand.getDefault();
        var x = rng.nextGaussian();
        var y = rng.nextGaussian();
        var z = rng.nextGaussian();
        var len = M.sqrt(x * x + y * y + z * z);
        return Matrix.point(x / len, y / len, z / len);
    }

    /**
     * Select a random point from this sphere
     */
    public Matrix getRandomPoint() {
        return transform.multiply(randomPointOnUnitSphere());
    }

    @Override
    public Optional<Hit> hitRayInRange(Ray ray, double min, double max) {

        Ray adjustedRay = ray.transform(getTransformInverse());

        // vector from the sphere's center, to the ray origin
        Matrix sphereToRay = adjustedRay.getOrigin().subtract(Matrix.point(0, 0, 0));
        double a = adjustedRay.getDirection().dot(adjustedRay.getDirection());
        double b = 2 * adjustedRay.getDirection().dot(sphereToRay);
        double c = sphereToRay.dot(sphereToRay) - 1;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return Optional.empty();
        } else {
            // since sqrt must be non-negative, t1 <= t2
            double t1 = (-b - M.sqrt(discriminant)) / (2 * a);
            double t2 = (-b + M.sqrt(discriminant)) / (2 * a);

            if (t1 > max || t2 < min) {
                return Optional.empty();
            }

            // if t1 is not in [min, max], pick t2, but otherwise prefer t1 because it's smaller
            // (the intersection at t2 is occluded by t1)
            double t = t1 < min ? t2 : t1;

            Matrix p = ray.position(t);
            Matrix normal = normalAt(p);

            return Optional.of(Hit.of(t, p, normal));
        }
    }

    /**
     * Returns the normal vector given a point on a sphere.
     * Assumes that the given point is on the given sphere.
     */
    public Matrix normalAt(Matrix point) {
        Assert.withMessage(point.isPoint(), "Sphere.normalAt requires a point");

        // objectPoint the given point but using a coordinate system where the given sphere
        // is unit size and centered on the origin
        var objectPoint = getTransformInverse().multiply(point);

        // objectNormal is the normal for the point objectPoint for the unit sphere at the
        // origin
        var objectNormal = objectPoint.subtract(Matrix.point(0, 0, 0));

        // worldNormal is objectNormal translated back into 'world coordinates'.
        var worldNormal = getTransformInverseTranspose().multiply(objectNormal).withW(0);

        return worldNormal.normalize();
    }
}
