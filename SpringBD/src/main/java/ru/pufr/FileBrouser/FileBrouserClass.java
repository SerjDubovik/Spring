package ru.pufr.FileBrouser;



import ru.pufr.models.*;

import java.io.File;
import java.util.*;


public class FileBrouserClass{

    private Map<String, FileView> folders = new TreeMap<>();
    private Map<String, FileView> files = new TreeMap<>();
    private Map<String, FileView> fileExplorer = new LinkedHashMap<>();
    private Map<Integer, FileViewAddressPath> pathLine = new LinkedHashMap<>();     // адресная строка в виде массива для отображения на вьюшке
    private Map<String, MsgUser> message = new LinkedHashMap<>();       // покажет на отображении сообщения при выполнении команд пользователя. ошибки и т.д
    private String logo;
    private String name;
    private String type;
    private String size;
    private String path;

    private String pathCut;

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

    public Map<String, MsgUser> getMessage() {
        return message;
    }

    public void setMessage(Map<String, MsgUser> message) {
        this.message = message;
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

    public String getPathCut() {
        return pathCut;
    }

    public void setPathCut(String pathCut) {
        this.pathCut = pathCut;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }


    public FileBrouserClass() {
    }

    public FileBrouserClass(String path, String pathCut) {
        this.path = path;
        this.pathCut = pathCut;
    }


    public void FileBrouser() {

        if (!(folders.isEmpty())){      // если вызываем не в первый раз, то чистим списки перед новым наполнением
            folders.clear();
        }
        if (!(files.isEmpty())){
            files.clear();
        }
        if (!(pathLine.isEmpty())){
            pathLine.clear();
        }
        if (!(fileExplorer.isEmpty())){
            fileExplorer.clear();
        }


        File dir = new File(pathCut + path);                  // определяем объект для каталога

        if(dir.isDirectory()) {

            for(File item : dir.listFiles())        // получаем все вложенные объекты в каталоге, заполняем две карты сортируя по алфавиту, отдельно для файлов, отдельно для папок
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

        System.out.println("path in class: " + path);

        if(!Objects.equals(path, "/")) {

            String subDirection = "";

            if(!path.isEmpty()){
                subDirection = path.substring(1);    // обрежем первый слеш в строке, мешает для следующего разбития строки на слова по слеши
            }

            System.out.println("subDirection: " + subDirection);
            System.out.println("---------");

            String[] words = subDirection.split("/");       // ну и тут строку на слова по слеши
            String str = "";

            pathLine.put(0, new FileViewAddressPath("home", "/"));

            int i = 1;

            for(String word : words){
                str = str + "/" + word;
                pathLine.put(i, new FileViewAddressPath(word, str));
                i++;
            }


            //System.out.print("Mass words: ");
            //for(int count = 0; count < words.length; count++){
            //    System.out.print(" " + words[count].toString());
            //}
            //System.out.println("");
            //System.out.println("---------");

        }
        if(Objects.equals(path, "/")){
            pathLine.put(0, new FileViewAddressPath("home", "/"));
            System.out.println("defolt string");
        }
    }




    public void FileBrouserDetails() {              // метод, возвращает детальное описание файла или папки

        if (!(fileExplorer.isEmpty())){
            fileExplorer.clear();
        }

        File dir = new File(pathCut + path);                  // определяем объект для каталога

        if(dir.isDirectory())
        {
            fileExplorer.put(dir.getName(), new FileView("folder.ico", dir.getName(),"folder", "", path));
        }

    }


    public void msgCreate(String typeOfMsg, String name, String msg){

        message.put(name, new MsgUser(name, typeOfMsg, msg));
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
