import java.util.function.Predicate;

public class TestFilter {

    public static void main(String[] args) {
        AdminUnitList list = new AdminUnitList();
        list.read("src/admin_units.csv");

        AdminUnitList result = list.filter(u -> u.name.startsWith("K"));
        System.out.println("\nJednostki zaczynające się na 'K':");
        result.sortInplaceByArea().list(System.out);

        result = list.filter(u -> u.name.startsWith("K"), 5, 10);
        System.out.println("\n10 jednostek zaczynających się na 'K', od 5-tej:");
        result.sortInplaceByArea().list(System.out);

        result = list.filter(u -> u.adminLevel == 2
                && u.parent != null
                && "małopolskie".equalsIgnoreCase(u.parent.name));
        System.out.println("\nPowiaty w województwie Małopolskim:");
        result.sortInplaceByName().list(System.out);

        Predicate<AdminUnit> smallPop = u -> u.population < 5000;
        Predicate<AdminUnit> smallArea = u -> u.area < 10;
        result = list.filter(smallPop.or(smallArea));
        System.out.println("\nMałe miejscowości lub o małej powierzchni:");
        result.sortInplaceByPopulation().list(System.out);

        Predicate<AdminUnit> isPowiat = u -> u.adminLevel == 2;
        Predicate<AdminUnit> inMalopolska = u -> u.parent != null && "małopolskie".equalsIgnoreCase(u.parent.name);
        Predicate<AdminUnit> notKrakow = u -> !"Kraków".equalsIgnoreCase(u.name);
        result = list.filter(isPowiat.and(inMalopolska).and(notKrakow));
        System.out.println("\nPowiaty w Małopolsce, oprócz Krakowa:");
        result.sortInplaceByName().list(System.out);

        result = list.filter(u -> u.name.startsWith("Ż"));
        System.out.println("\nJednostki zaczynające się na 'Ż'");
        result.sortInplaceByArea().list(System.out);
    }
}
