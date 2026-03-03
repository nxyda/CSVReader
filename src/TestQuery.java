import java.io.PrintStream;

public class TestQuery {
    public static void main(String[] args) {
        PrintStream out = System.out;
        AdminUnitList list = new AdminUnitList();
        list.read("src/admin_units.csv");

        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.area > 1000)
                .or(a -> a.name.startsWith("Sz"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(100);
        query.execute().list(out);

        new AdminUnitQuery()
                .selectFrom(list)
                .where(u -> u.adminLevel == 2)
                .sort((a, b) -> Double.compare(b.population, a.population))
                .limit(50)
                .execute()
                .list(out);

        new AdminUnitQuery()
                .selectFrom(list)
                .where(u -> u.name.startsWith("K"))
                .and(u -> u.area > 100)
                .sort((a, b) -> a.name.compareTo(b.name))
                .execute()
                .list(out);

        new AdminUnitQuery()
                .selectFrom(list)
                .where(u -> true)
                .sort((a, b) -> {
                    int c1 = Integer.compare(a.adminLevel, b.adminLevel);
                    if (c1 != 0) return c1;
                    return Double.compare(b.area, a.area);
                })
                .limit(20)
                .execute()
                .list(out);

    }
}
