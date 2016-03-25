package ru.ok.model.places;

public enum ComplaintPlaceType {
    ADVERTISING("Advertising"),
    DUPLICATE("PlaceDuplicate"),
    CLOSE("PlaceClosed"),
    NON_EXISTENT("PlaceDoesNotExist");
    
    private String value;

    public String getValue() {
        return this.value;
    }

    private ComplaintPlaceType(String value) {
        this.value = value;
    }
}
