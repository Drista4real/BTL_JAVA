package service;

import model.Drug;

public interface IDrugService {
    Drug getDrugDetail(int id);
    boolean updateDrug(int id, String name, String description, double price, int stock);
    boolean insertDrug(String name, String description, double price, int stock);
}
