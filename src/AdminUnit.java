import java.util.List;

public class AdminUnit {
    String name;
    int adminLevel;
    double population;
    double area;
    double density;
    AdminUnit parent;
    BoundingBox bbox = new BoundingBox();
    List<AdminUnit> children;

    @Override
    public String toString() {
        if (this.density == 0 && this.area > 0) {
            this.density = this.population / this.area;
        }

        String parentName = (this.parent != null) ? this.parent.name : "Brak";

        return "AdminUnit {" +
                "\n  name: '" + name + '\'' +
                ",\n  adminLevel: " + adminLevel +
                ",\n  population: " + population +
                ",\n  area: " + String.format("%.2f", area) + " km^2" +
                ",\n  density: " + String.format("%.2f", density) + " os./km^2" +
                ",\n  parent: " + parentName +
                ",\n  bbox: " + bbox.toString() +
                "\n}";
    }


}
