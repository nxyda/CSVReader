import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();

    public void read(String fileName) {
        CSVReader reader = null;
        Map<Long, AdminUnit> idToUnit = new HashMap<>();
        Map<AdminUnit, Long> unitToParentId = new HashMap<>();

        try {
            reader = new CSVReader(fileName, ",", true);
            while (reader.next()) {
                AdminUnit unit = new AdminUnit();
                unit.name = reader.get("name");

                try {
                    unit.adminLevel = reader.getInt("adminLevel");
                    unit.population = reader.isMissing("population") ? 0.0 : reader.getDouble("population");
                    unit.area = reader.isMissing("area") ? 0.0 : reader.getDouble("area");
                } catch (Exception e) {
                    unit.adminLevel = 0;
                    unit.population = 0.0;
                    unit.area = 0.0;
                }

                double x1 = reader.isMissing("x1") ? Double.NaN : reader.getDouble("x1");
                double y1 = reader.isMissing("y1") ? Double.NaN : reader.getDouble("y1");
                double x2 = reader.isMissing("x2") ? Double.NaN : reader.getDouble("x2");
                double y2 = reader.isMissing("y2") ? Double.NaN : reader.getDouble("y2");

                if (!Double.isNaN(x1) && !Double.isNaN(y1))
                    unit.bbox.addPoint(x1, y1);

                if (!Double.isNaN(x2) && !Double.isNaN(y2))
                    unit.bbox.addPoint(x2, y2);

                unit.children = new ArrayList<>();

                long id = (reader.isMissing("id") || reader.get("id").isEmpty()) ? 0 : reader.getLong("id");
                long parentId = (reader.isMissing("parentId") || reader.get("parentId").isEmpty()) ? 0 : reader.getLong("parentId");


                idToUnit.put(id, unit);
                unitToParentId.put(unit, parentId);

                this.units.add(unit);
            }

            for (Map.Entry<AdminUnit, Long> entry : unitToParentId.entrySet()) {
                AdminUnit unit = entry.getKey();
                Long pid = entry.getValue();
                if (pid == 0) {
                    unit.parent = null;
                } else {
                    AdminUnit parent = idToUnit.get(pid);
                    unit.parent = parent;
                    if (parent != null) {
                        parent.children.add(unit);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku: " + fileName + " - " + e.getMessage());
        }
    }

    void list(PrintStream out) {
        for (AdminUnit unit : this.units) {
            out.println(unit);
        }
    }

    void list(PrintStream out, int offset, int limit) {
        if (offset < 0) offset = 0;
        if (limit < 0) limit = 0;
        int end = Math.min(offset + limit, units.size());
        for (int i = offset; i < end; i++) {
            out.println(units.get(i));
        }
    }

    AdminUnitList selectByName(String pattern, boolean regex) {
        AdminUnitList ret = new AdminUnitList();
        for (AdminUnit unit : this.units) {
            if (regex) {
                if (unit.name.matches(pattern)) {
                    ret.units.add(unit);
                }
            } else {
                if (unit.name.contains(pattern)) {
                    ret.units.add(unit);
                }
            }
        }
        return ret;
    }

    private void fixMissingValues() {
        for (AdminUnit unit : units) {
            fixMissingValues(unit);
        }
    }

    private void fixMissingValues(AdminUnit unit) {
        if (unit.population > 0 && unit.density > 0) {
            return;
        }
        if (unit.parent != null) {
            fixMissingValues(unit.parent);

            if (unit.density <= 0) {
                unit.density = unit.parent.density;
            }
        }
        if (unit.population <= 0) {
            unit.population = unit.area * unit.density;
        }
    }

    public void removeRoot(String pattern,boolean regex) {
        List<AdminUnit> toRemove = new ArrayList<>();

        for (AdminUnit unit : units) {
            if (unit.parent == null) {
                boolean matches = regex ? unit.name.matches(pattern) : unit.name.contains(pattern);
                if (matches) {
                    toRemove.add(unit);
                }
            }
        }

        for (AdminUnit root : toRemove) {
            removeUnitRecursive(root);
        }
    }

    private void removeUnitRecursive(AdminUnit unit) {
        for (AdminUnit child : unit.children) {
            removeUnitRecursive(child);
        }

        if (unit.parent != null) {
            unit.parent.children.remove(unit);
        }
        units.remove(unit);
    }

    AdminUnitList getNeighbors(AdminUnit unit, double maxdistance) {
        int adminlevel = unit.adminLevel;

        if (adminlevel < 4 || adminlevel > 8 || adminlevel == 5)
            throw new IllegalArgumentException("");

        AdminUnitList neighbors = new AdminUnitList();

        if (unit.parent == null) {
            for (AdminUnit u : units) {
                if (u == unit) continue;
                if (u.adminLevel != adminlevel) continue;
                if (!unit.bbox.intersects(u.bbox)) continue;
                if (adminlevel == 8 && unit.bbox.distanceTo(u.parent.bbox) > maxdistance) continue;
                neighbors.units.add(u);
            }
        } else {
            for (AdminUnit u : unit.parent.children) {
                if (u == unit) continue;
                if (u.adminLevel != unit.adminLevel) continue;
                if (unit.bbox.isEmpty() || u.bbox.isEmpty()) continue;
                if (!unit.bbox.intersects(u.bbox)) continue;
                if (adminlevel == 8 && unit.bbox.distanceTo(u.parent.bbox) > maxdistance) continue;
                neighbors.units.add(u);
            }

        }

        return neighbors;
    }


    public AdminUnitList getNeighborsOptimized(AdminUnit unit, double maxdistance) {
        AdminUnitList neighbors = new AdminUnitList();
        if (unit == null || unit.bbox.isEmpty()) return neighbors;

        int lvl = unit.adminLevel;
        if (lvl < 4 || lvl > 8 || lvl == 5)
            throw new IllegalArgumentException("");

        AdminUnit parent = unit.parent;
        if (parent == null) parent = unit;

        for (AdminUnit u : parent.children) {
            if (u == unit || u.adminLevel != unit.adminLevel) continue;
            if (u.bbox.isEmpty()) continue;
            try {
                if (unit.bbox.distanceTo(u.bbox) <= maxdistance)
                    neighbors.units.add(u);
            } catch (IllegalStateException e) {

            }
        }

        return neighbors;
    }

    private void searchNeighbors(AdminUnit unit, AdminUnitList neighbors, AdminUnit node, double maxdistance) {
        if (node != unit && node.adminLevel == unit.adminLevel) {
            boolean isNeighbor = false;
            if (unit.adminLevel <= 3) {
                isNeighbor = unit.bbox.intersects(node.bbox);
            } else {
                try {
                    isNeighbor = unit.bbox.distanceTo(node.bbox) <= maxdistance;
                } catch (IllegalStateException e) {
                    isNeighbor = false;
                }
            }
            if (isNeighbor) neighbors.units.add(node);
        }

        if (unit.adminLevel <= 3) {
            for (AdminUnit child : node.children) {
                if (child.bbox.isEmpty()) continue;
                if (unit.bbox.intersects(child.bbox))
                    searchNeighbors(unit, neighbors, child, maxdistance);
            }
        }
    }




    public AdminUnitList sortInplaceByName() {
        class NameComparator implements Comparator<AdminUnit> {
            @Override
            public int compare(AdminUnit a, AdminUnit b) {
                if (a == null && b == null) return 0;
                if (a == null) return -1;
                if (b == null) return 1;
                return a.name.compareToIgnoreCase(b.name);
            }
        }

        java.util.Collections.sort(units, new NameComparator());
        return this;

    }

    public AdminUnitList sortInplaceByArea() {
        java.util.Collections.sort(this.units, new java.util.Comparator<AdminUnit>() {
            @Override
            public int compare(AdminUnit a, AdminUnit b) {
                if (a == null && b == null) return 0;
                if (a == null) return -1;
                if (b == null) return 1;
                return Double.compare(a.area, b.area);
            }
        });

        return this;
    }

    AdminUnitList sortInplaceByPopulation() {
        this.units.sort((a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return Double.compare(a.population, b.population);
        });

        return this;
    }

    AdminUnitList sortInplace(Comparator<AdminUnit> cmp){
        if (cmp != null) {
            this.units.sort(cmp);
        }
        return this;
    }

    AdminUnitList sort(Comparator<AdminUnit> cmp){
        AdminUnitList sortedList = new AdminUnitList();
        sortedList.units.addAll(this.units);
        sortedList.sortInplace(cmp);
        return sortedList;
    }

    public AdminUnitList filter(Predicate<AdminUnit> pred) {
        AdminUnitList result = new AdminUnitList();
        for (AdminUnit unit : this.units) {
            if (pred.test(unit)) {
                result.units.add(unit);
            }
        }
        return result;
    }

    AdminUnitList filter(Predicate<AdminUnit> pred, int limit) {
        AdminUnitList result = new AdminUnitList();
        if (pred == null || limit <= 0) return result;

        int count = 0;
        for (AdminUnit u : this.units) {
            if (pred.test(u)) {
                result.units.add(u);
                count++;
                if (count >= limit) break;
            }
        }
        return result;
    }

    public AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit) {
        AdminUnitList result = new AdminUnitList();
        if (pred == null || limit <= 0 || offset < 0) return result;

        int count = 0;
        for (AdminUnit u : this.units) {
            if (pred.test(u)) {
                if (count >= offset) {
                    result.units.add(u);
                    if (result.units.size() >= limit) break;
                }
                count++;
            }
        }

        return result;
    }




}
