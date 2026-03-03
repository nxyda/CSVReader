public class TestAdminUnitList {
    public static void main(String[] args) {
        AdminUnitList aul = new AdminUnitList();
        aul.read("src/admin_units.csv");

        AdminUnitList selected = aul.selectByName("małop", false);
        System.out.println("Jednostki zawierające 'małop':");
        selected.list(System.out, 0, selected.units.size());

        AdminUnitList regexSelected = aul.selectByName("^wojew.*", true);
        System.out.println("\nJednostki pasujące do regex '^wojew.*':");
        regexSelected.list(System.out, 0, regexSelected.units.size());

        AdminUnitList regexEnd = aul.selectByName(".*skie", true);
        System.out.println("\nJednostki pasujące do regex '.*skie':");
        regexEnd.list(System.out, 0, regexEnd.units.size());
    }
}
