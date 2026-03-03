import java.util.Comparator;
import java.util.function.Predicate;

public class AdminUnitQuery {
    AdminUnitList src;
    Predicate<AdminUnit> p = a->true;
    Comparator<AdminUnit> cmp;
    int limit = Integer.MAX_VALUE;
    int offset = 0;

    AdminUnitQuery selectFrom(AdminUnitList src){
        this.src = src;
        return this;
    }

    AdminUnitQuery where(Predicate<AdminUnit> pred){
        this.p = pred;
        return this;
    }

    AdminUnitQuery and(Predicate<AdminUnit> pred){
        this.p = this.p.and(pred);
        return this;
    }

    AdminUnitQuery or(Predicate<AdminUnit> pred){
        this.p = this.p.or(pred);
        return this;
    }

    AdminUnitQuery sort(Comparator<AdminUnit> cmp){
        this.cmp = cmp;
        return this;
    }

    AdminUnitQuery limit(int limit){
        this.limit = limit;
        return this;
    }

    AdminUnitQuery offset(int offset){
        this.offset = offset;
        return this;
    }

    AdminUnitList execute(){
        AdminUnitList result = new AdminUnitList();

        for (AdminUnit u : src.units) {
            if(p.test(u)){
                result.units.add(u);
            }
        }

        if (cmp != null) {
            result.units.sort(cmp);
        }

        AdminUnitList finalList = new AdminUnitList();

        int start = Math.max(0,offset);
        int end = Math.min(result.units.size(), start + limit);

        for (int i = start; i < end; i++) {
            finalList.units.add(result.units.get(i));
        }

        return finalList;
    }

}
