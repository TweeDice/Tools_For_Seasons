package com.mwaldmeier.toolsforseasons;

/**
 * Created by michael on 12/13/2015.
 */
public enum ElementType {
    FIRE("fireImg"),
    WATER("waterImg"),
    WIND("windImg"),
    EARTH("earthImg");

    private final String imgValue;
    ElementType(String imgValue) {this.imgValue = imgValue;}

    public String getImgValue() {
        return imgValue;
    }
}
