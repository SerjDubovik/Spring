package ru.pufr.FileBrouser;



import ru.pufr.models.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;



public class FileBrouserClass{

    private Map<String, FileView> folders = new TreeMap<>();
    private Map<String, FileView> files = new TreeMap<>();
    private Map<String, FileView> fileExplorer = new LinkedHashMap<>();
    private Map<Integer, FileViewAddressPath> pathLine = new LinkedHashMap<>();     // адресная строка в виде массива для отображения на вьюшке
    private String logo;
    private String name;
    private String type;
    private String size;
    private String path;

    private String nameFolder;


    public Map<String, FileView> getFolders() {
        return folders;
    }

    public void setFolders(Map<String, FileView> folders) {
        this.folders = folders;
    }

    public Map<String, FileView> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileView> files) {
        this.files = files;
    }

    public Map<String, FileView> getFileExplorer() {
        return fileExplorer;
    }

    public void setFileExplorer(Map<String, FileView> fileExplorer) {
        this.fileExplorer = fileExplorer;
    }

    public Map<Integer, FileViewAddressPath> getPathLine() {
        return pathLine;
    }

    public void setPathLine(Map<Integer, FileViewAddressPath> pathLine) {
        this.pathLine = pathLine;
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

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }


    public FileBrouserClass(String path) {
        this.path = path;
    }


    public void FileBrouser() {

        File dir = new File(path);                  // определяем объект для каталога

        if(dir.isDirectory()) {

            for(File item : dir.listFiles())        // получаем все вложенные объекты в каталоге, заполняем две карты сортируя по алвавиту , отдельно для файлов, отдельно для папок
            {
                if(item.isDirectory())
                {
                    folders.put(item.getName(), new FileView("folder.ico", item.getName(),"folder", "", path));
                }else{
                    String nameFile = item.getName();
                    String fileExtension;

                    if(getExtensionByStringHandling(nameFile).isPresent()){     // берем расширение файла в строку
                        fileExtension = getExtensionByStringHandling(nameFile).get();
                    }else{
                        fileExtension = "file extension Not found";
                    }

                    Long fileLength = null;
                    String QnByte = "Б";

                    if((item.length() >= 0) && (item.length() <= 1023)){
                        QnByte = "Б";
                        fileLength = item.length();
                    }
                    if((item.length() >= 1024) && (item.length() <= 1_048_575)) {
                        QnByte = "КБ";
                        fileLength = item.length() / 1000;
                    }
                    if((item.length() >= 1_048_576) && (item.length() <= 1_073_741_823)) {
                        QnByte = "МБ";
                        fileLength = item.length() / 1_000_000;
                    }
                    if((item.length() > 1_073_741_823)) {
                        QnByte = "ГБ";
                        fileLength = item.length() / 1_000_000_000;
                    }

                    files.put(item.getName(), new FileView(getNameIcon(fileExtension), item.getName(),"file", fileLength + " " + QnByte, path));
                }
            }
        }

        for(Map.Entry<String, FileView> entry : folders.entrySet()){    // пробегаем по полученным картам и сливаем в одну с неизменным порядком
            fileExplorer.put(entry.getKey(),entry.getValue());
        }

        for(Map.Entry<String, FileView> entry : files.entrySet()){
            fileExplorer.put(entry.getKey(),entry.getValue());
        }

            String subDirection = path.substring(1);    // обрежем первый слеш в строке, мешает для следующего разбития строки на слова по слеши
            String[] words = subDirection.split("/");       // ну и тут строку на слова по слеши

            String str = "";

            int i = 0;

            for(String word : words){
                str = str + "/" + word;
                pathLine.put(i, new FileViewAddressPath(word, str));
                i++;
            }
    }




    public void FileBrouserDetails() {              // метод, возвращает детальное описание файла или папки

        File dir = new File(path);                  // определяем объект для каталога

        if(dir.isDirectory())
        {
            fileExplorer.put(dir.getName(), new FileView("folder.ico", dir.getName(),"folder", "", path));
        }else{
            String nameFile = dir.getName();
            String fileExtension;

            if(getExtensionByStringHandling(nameFile).isPresent()){     // берем расширение файла в строку
                fileExtension = getExtensionByStringHandling(nameFile).get();
            }else{
                fileExtension = "file extension Not found";
            }

            Long fileLength = null;
            String QnByte = "Б";

            if((dir.length() >= 0) && (dir.length() <= 1023)){
                QnByte = "Б";
                fileLength = dir.length();

            }
            if((dir.length() >= 1024) && (dir.length() <= 1_048_575)) {
                QnByte = "КБ";
                fileLength = dir.length() / 1000;
            }
            if((dir.length() >= 1_048_576) && (dir.length() <= 1_073_741_823)) {
                QnByte = "МБ";
                fileLength = dir.length() / 1_000_000;
            }
            if((dir.length() > 1_073_741_823)) {
                QnByte = "ГБ";
                fileLength = dir.length() / 1_000_000_000;
            }

            fileExplorer.put(dir.getName(), new FileView(getNameIcon(fileExtension), dir.getName(),"file", fileLength + " " + QnByte, ""));
        }
    }


    public Optional<String> getExtensionByStringHandling(String filename) {     // ищет расширение файла в строке
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public String getNameIcon(String name) {
        String nameIcon = "";

        switch (name){
            case "png" : case "PNG" :
            case "jpg" : case "JPG" :
            case "jif" : case "JIF" :
            case "tif" : case "TIF" :
                nameIcon = "icon_image.ico"; break;
            case "txt" : case "TXT" :
                nameIcon = "icon_txt.ico"; break;
            case "pdf" : case "PDF" :
                nameIcon = "icon_pdf.ico"; break;
            case "mkv" : case "MKV" :
                nameIcon = "icon_mkv.ico"; break;
            case "avi" : case "AVI" :
                nameIcon = "icon_avi.ico"; break;
            case "html" : case "HTML" :
                nameIcon = "icon_html.ico"; break;
            case "rar" : case "RAR" :
            case "zip" : case "ZIP" :
            case "war" : case "WAR" :
            case "jar" : case "JAR" :
                nameIcon = "icon_rar.ico"; break;
            case "7z" : case "7Z" :
                nameIcon = "icon_7zip.ico"; break;
            case "psd" : case "PSD" :
                nameIcon = "icon_psd.ico"; break;
            case "ini" : case "INI" :
                nameIcon = "icon_ini.ico"; break;
            case "wmp" : case "WMP" :
                nameIcon = "icon_wmp.ico"; break;
            case "torrent" : case "TORRENT" :
                nameIcon = "icon_uTorrent.ico"; break;
            case "mp3" : case "MP3" :
                nameIcon = "icon_mp3.ico"; break;

            case "xlsx" : case "XLSX" :     // иконки офиса
            case "xlsm" : case "XLSM" :
            case "xlsb" : case "XLSB" :
            case "xltx" : case "XLTX" :
            case "xltm" : case "XLTM" :
            case "xls" : case "XLS" :
            case "xlt" : case "XLT" :
            case "xml" : case "XML" :
            case "xlam" : case "XLAM" :
            case "xla" : case "XLA" :
            case "xlw" : case "XLW" :
            case "xlr" : case "XLR" :
            case "csv" : case "CSV" :
                nameIcon = "icon_exel.ico"; break;
            case "doc" : case "DOC" :
            case "docx" : case "DOCX" :
            case "rtf" : case "RTF" :
                nameIcon = "icon_word.ico"; break;
/*
            case "" : case "" :
                nameIcon = ""; break;
*/
            default:
                nameIcon = "icon_default.png"; break;
        }

        return nameIcon;
    }

}