package ru.pufr.models;

public class FileViewAddressPath{

    private String nameFolder;
    private String path;

    public FileViewAddressPath() {
    }

    public FileViewAddressPath(String nameFolder, String path) {
        this.nameFolder = nameFolder;
        this.path = path;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
