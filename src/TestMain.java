import java.io.IOException;
import java.util.Locale;

public class TestMain {
    public static void main(String[] args) throws IOException {
        CSVReader reader = new CSVReader("src/titanic-part.csv", ",", true);

        while (reader.next()) {
            int id = reader.getInt("PassengerId");
            String name = reader.get("Name");
            double fare = reader.getDouble("Fare");

            System.out.printf(Locale.US, "%d %s %.2f%n", id, name, fare);
        }

        AdminUnitList aul = new AdminUnitList();
        aul.read("src/admin_units.csv");

        aul.removeRoot("województwo.*", true);

        aul.removeRoot("Urząd.*", true);
        aul.removeRoot("Metropolia katowicka", false);

        aul.list(System.out, 0, 50);


    }
}