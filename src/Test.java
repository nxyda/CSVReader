import java.io.StringReader;

public class Test {
    public static void main(String[] args) throws Exception {

        System.out.println("\nTest 1");
        CSVReader reader1 = new CSVReader("src/titanic-part.csv", ",", true);

        while (reader1.next()) {
            for (int i = 0; i < reader1.getRecordLength(); i++) {
                System.out.print("<" + reader1.get(i) + "> ");
            }
            System.out.println();

            try {
                int id = reader1.getInt("PassengerId");
                double fare = reader1.getDouble("Fare");
                System.out.println("ID=" + id + " Fare=" + fare);
            } catch (RuntimeException e) {
                System.out.println("Błąd: " + e.getMessage());
            }

            for (int i = 0; i < reader1.getRecordLength(); i++) {
                if (reader1.isMissing(i)) System.out.println("Kolumna " + i + " jest pusta");
            }
        }


        System.out.println("\nTest 2");
        CSVReader reader2 = new CSVReader("src/titanic-part.csv", ",", true);
        while (reader2.next()) {
            try {
                System.out.println("Nieistniejąca kolumna: " + reader2.get("NieMaTakiej"));
            } catch (RuntimeException e) {
                System.out.println("Błąd przy nieistniejącej kolumnie: " + e.getMessage());
            }

            try {
                System.out.println("Nieistniejąca kolumna indeks: " + reader2.get(999));
            } catch (RuntimeException e) {
                System.out.println("Błąd przy nieistniejącym indeksie: " + e.getMessage());
            }
        }


        System.out.println("\nTest 3");
        String text = "a,b,c\n123,456,789\n,,\n10,20,30";
        CSVReader reader3 = new CSVReader(new StringReader(text), ",", true);

        while (reader3.next()) {
            for (int i = 0; i < reader3.getRecordLength(); i++) {
                String val = reader3.get(i);
                System.out.print("<" + val + "> ");
            }
            System.out.println();

            for (int i = 0; i < reader3.getRecordLength(); i++) {
                if (!reader3.isMissing(i)) {
                    try {
                        int v = reader3.getInt(i);
                        System.out.print("[" + v + "] ");
                    } catch (RuntimeException e) {
                        System.out.print("[Błąd] ");
                    }
                } else {
                    System.out.print("[Brak] ");
                }
            }
            System.out.println("\n---");
        }

        System.out.println("\nTest 4");
        String text2 = """
                x,y,z
                1,2,3
                4,,6
                """;
        CSVReader reader4 = new CSVReader(new StringReader(text2), ",", true);

        while (reader4.next()) {
            for (int i = 0; i < reader4.getRecordLength(); i++) {
                System.out.print("<" + reader4.get(i) + "> ");
            }
            System.out.println();

            for (int i = 0; i < reader4.getRecordLength(); i++) {
                if (!reader4.isMissing(i)) {
                    try {
                        double d = reader4.getDouble(i);
                        System.out.print("[" + d + "] ");
                    } catch (RuntimeException e) {
                        System.out.print("[Błąd] ");
                    }
                } else {
                    System.out.print("[Brak] ");
                }
            }
            System.out.println("\n---");
        }

    }
}
