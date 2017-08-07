package com.parser;

import java.util.Objects;

public class SingleRow {
    
    private String idKontrahenta, kodTowarowy;
    
    SingleRow(String idKontrahenta, String kodTowarowy) {
    
        this.idKontrahenta = idKontrahenta;
        this.kodTowarowy = kodTowarowy;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SingleRow)) {
            return false;
        }
    
        SingleRow singleRow = (SingleRow) o;
        
        return Objects.equals(idKontrahenta, singleRow.idKontrahenta) && Objects.equals(kodTowarowy, singleRow.kodTowarowy);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idKontrahenta, kodTowarowy);
    }
    
}
