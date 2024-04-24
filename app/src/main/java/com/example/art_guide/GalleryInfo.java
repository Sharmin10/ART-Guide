package com.example.art_guide;

import java.io.Serializable;

public class GalleryInfo implements Serializable {
    String name, image, map, address, open, description;
    public GalleryInfo(String name, String image, String map, String address, String open, String description){
        this.name = name;
        this.image = image;
        this.map = map;
        this.address = address;
        this.open = open;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
