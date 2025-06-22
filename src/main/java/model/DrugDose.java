package model;

public class DrugDose {
    private String name;
    private int doseMor,doseNoon,doseAfternoon;

    public DrugDose() {
    }

    public DrugDose(String name, int doseMor, int doseNoon, int doseAfternoon) {
        this.name = name;
        this.doseMor = doseMor;
        this.doseNoon = doseNoon;
        this.doseAfternoon = doseAfternoon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDoseMor() {
        return doseMor;
    }

    public void setDoseMor(int doseMor) {
        this.doseMor = doseMor;
    }

    public int getDoseNoon() {
        return doseNoon;
    }

    public void setDoseNoon(int doseNoon) {
        this.doseNoon = doseNoon;
    }

    public int getDoseAfternoon() {
        return doseAfternoon;
    }

    public void setDoseAfternoon(int doseAfternoon) {
        this.doseAfternoon = doseAfternoon;
    }

    @Override
    public String toString() {
        return "DrugDose{" +
                "name='" + name + '\'' +
                ", doseMor=" + doseMor +
                ", doseNoon=" + doseNoon +
                ", doseAfternoon=" + doseAfternoon +
                '}';
    }
}
