package ru.pufr.models;

public class FileView {

    private String logo;
    private String name;
    private String type;
    private String size;
    private String path;

    public FileView() {
    }

    public FileView(String logo, String name, String type, String size, String path) {
        this.logo = logo;
        this.name = name;
        this.type = type;
        this.size = size;
        this.path = path;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
