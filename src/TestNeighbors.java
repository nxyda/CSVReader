import java.io.IOException;
import java.util.Locale;
public class TestNeighbors {
    public static void main(String[] args) throws IOException {
        AdminUnitList list = new AdminUnitList();
        System.out.println("Przed");
        list.read("admin_units_full.csv");
        System.out.println("Po");

        String targetName = "Kraków";
        int targetLevel = 6;
        AdminUnit target = null;

        for (AdminUnit u : list.units) {
            if (u.adminLevel == targetLevel && u.name.equals(targetName)) {
                target = u;
                break;
            }
        }

        if (target == null) {
            System.out.println("Nie znaleziono jednostki " + targetName);
            return;
        }

        System.out.println("Wybrana jednostka:");
        System.out.println(target);

        double maxDistance = 15.0;
        double t1 = System.nanoTime() / 1e6;

        AdminUnitList neighbors = list.getNeighborsOptimized(target, maxDistance);

        double t2 = System.nanoTime() / 1e6;
        System.out.printf(Locale.US, "Czas wyszukiwania sąsiadów: %.3f ms\n", t2 - t1);

        System.out.println("Sąsiedzi:");
        neighbors.list(System.out);

        System.out.println("\nDla wybranej jednostki:");
        System.out.printf(Locale.US,
                "LINESTRING(%f %f, %f %f, %f %f, %f %f, %f %f)\n",
                target.bbox.xmin, target.bbox.ymin,
                target.bbox.xmin, target.bbox.ymax,
                target.bbox.xmax, target.bbox.ymax,
                target.bbox.xmax, target.bbox.ymin,
                target.bbox.xmin, target.bbox.ymin
        );
    }
}
