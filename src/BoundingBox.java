public class BoundingBox {
    double xmin = Double.POSITIVE_INFINITY;
    double ymin = Double.POSITIVE_INFINITY;
    double xmax = Double.NEGATIVE_INFINITY;
    double ymax = Double.NEGATIVE_INFINITY;

    void addPoint(double x, double y) {
        if (x < xmin)  xmin = x;
        if (x > xmax) xmax = x;
        if (y < ymin)  ymin = y;
        if (y > ymax) ymax = y;
    }

    boolean contains(double x, double y){
        if (xmin > xmax || ymin > ymax)  return false;
        return x >  xmin && x < xmax && y > ymin && y < ymax;
    }

    boolean contains(BoundingBox bb){
        if (this.xmin > this.xmax || this.ymin > this.ymax)  return false;
        if (bb.xmin > bb.xmax || bb.ymin > bb.ymax)  return false;

        return bb.xmin >= this.xmin && bb.xmax <= this.xmax && bb.ymin >= this.ymin && bb.ymax <= this.ymax;
    }

    boolean intersects(BoundingBox bb){
        if (this.xmin > this.xmax || this.ymin > this.ymax)  return false;
        if (bb.xmin > bb.xmax || bb.ymin > bb.ymax)  return false;

        return !(bb.xmax < this.xmin || bb.ymax < this.ymin || bb.xmin >  this.xmax ||  bb.ymin >  this.ymax);
    }

    BoundingBox add(BoundingBox bb){
        if (bb.xmin > bb.xmax || bb.ymin > bb.ymax) {
            return this;
        }

        if (this.xmin > this.xmax || this.ymin > this.ymax) {
            this.xmin = bb.xmin;
            this.ymin = bb.ymin;
            this.xmax = bb.xmax;
            this.ymax = bb.ymax;
            return this;
        }

        if (bb.xmin < this.xmin) this.xmin = bb.xmin;
        if (bb.ymin < this.ymin) this.ymin = bb.ymin;
        if (bb.xmax > this.xmax) this.xmax = bb.xmax;
        if (bb.ymax > this.ymax) this.ymax = bb.ymax;

        return this;
    }

    boolean isEmpty() {
        return xmin > xmax || ymin > ymax;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundingBox)) return false;

        BoundingBox bb =  (BoundingBox) o;

        return Double.compare(xmin, bb.xmin) == 0 && Double.compare(xmax, bb.xmax) == 0 &&
                Double.compare(ymin, bb.ymin) == 0 && Double.compare(ymax, bb.ymax) == 0;

    }

    double getCenterX() {
        if (isEmpty()) {
            throw new IllegalStateException("BoundingBox is empty");
        }
        return (xmin + xmax) / 2;
    }

    double getCenterY() {
        if (isEmpty()) {
            throw new IllegalStateException("BoundingBox is empty");
        }
        return (ymin + ymax) / 2;
    }

    double distanceTo(BoundingBox bbx){
        if (this.isEmpty() || bbx.isEmpty()) {
            throw new IllegalStateException("One of the bounding boxes is empty");
        }
        double lat1 = this.getCenterY();
        double lon1 = this.getCenterX();
        double lat2 = bbx.getCenterY();
        double lon2 = bbx.getCenterX();

        double R = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rLat1) * Math.cos(rLat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "BoundingBox(empty)";
        return String.format("[xmin=%.6f, ymin=%.6f, xmax=%.6f, ymax=%.6f]", xmin, ymin, xmax, ymax);
    }


}
