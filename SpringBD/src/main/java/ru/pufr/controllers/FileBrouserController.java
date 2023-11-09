package ru.pufr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.pufr.FileBrouser.FileBrouserClass;
import ru.pufr.models.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import ru.pufr.repo.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class FileBrouserController {

    @Autowired
    private UserRepository userRepository;

    private static String videoPath = "";

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/fileBrouser")
    public String fileBrouser(Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе
        System.out.println("User path: " + pathCut + " Direction: /");

        FileBrouserClass fb = new FileBrouserClass("/", pathCut);
        fb.FileBrouser();

        model.addAttribute("direction", "/");
        //model.addAttribute("message", fb.getMessage());
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }


    @PreAuthorize("hasAuthority('developers:read')")
    @PostMapping("/fileBrouser")
    public String fileBrouser(@RequestParam String direction, Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе

        System.out.println("first Direction: " + direction);

        if(!Objects.equals(direction, "/")){
            direction += "/";
        }

        System.out.println("User path: " + pathCut + " Direction: " + direction);

        FileBrouserClass fb = new FileBrouserClass(direction, pathCut);
        fb.FileBrouser();

        model.addAttribute("direction", direction);
        //model.addAttribute("message", fb.getMessage());
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }


    @PreAuthorize("hasAuthority('developers:read')")           // передаёт страницу для редактирования и просмотра файлов, если поддерживается
    @PostMapping("/fileBrouser_file")
    public String fileBrouserFile(@RequestParam String direction, Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе
        String typeView = "null";


        File dir = new File(pathCut + direction);                  // определяем объект для каталога

        String nameFile = dir.getName();
        String fileExtension;

        FileBrouserClass fb = new FileBrouserClass(direction, pathCut);

            if(fb.getExtensionByStringHandling(nameFile).isPresent()){     // берем расширение файла в строку
                fileExtension = fb.getExtensionByStringHandling(nameFile).get();
            }else{
                fileExtension = "file extension Not found";
            }

                if(fileExtension.equals("jpg") || fileExtension.equals("JPG") ||
                   fileExtension.equals("png") || fileExtension.equals("PNG") ||
                   fileExtension.equals("jif") || fileExtension.equals("JIF") ||
                   fileExtension.equals("tif") || fileExtension.equals("TIF")) {

                    try {
                        byte[] test = Files.readAllBytes(Path.of(pathCut + direction));
                        byte[] encodeBase64 = Base64.getEncoder().encode(test);

                        String base64Encoded = new String(encodeBase64, "UTF-8");
                        String colum = "data:image/" + fileExtension + ";base64,";

                        model.addAttribute("image", base64Encoded );
                        model.addAttribute("colum", colum);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    typeView = "image";
                }

                if(fileExtension.equals("txt") || fileExtension.equals("TXT") ||
                   fileExtension.equals("doc") || fileExtension.equals("DOC") ||
                   fileExtension.equals("html") || fileExtension.equals("HTML") ||
                   fileExtension.equals("c") || fileExtension.equals("C") ||
                   fileExtension.equals("java") || fileExtension.equals("JAVA") ||
                   fileExtension.equals("xml") || fileExtension.equals("XML") ) {

                    try {

                        BufferedReader reader = new BufferedReader(new FileReader(pathCut + direction));
                        String textStr = reader.readLine();
                        textStr += "\r";
                        String buff = "";
                        while (buff != null) {
                            buff = reader.readLine();
                            textStr = textStr + buff + "\r";
                        }

                        model.addAttribute("textStr", textStr );
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    typeView = "text";
                }

      if(fileExtension.equals("mp4") || fileExtension.equals("MP4")) {

         //videoPath = "/test/admin/Guide for words.mp4";     // дописать формирование строки от выбора пользователя
          videoPath = (pathCut + direction);
          typeView = "video";
      }

        int index = direction.lastIndexOf('/');
        String directionSub = direction.substring(0,index);

        fb.setPath(directionSub);
        fb.FileBrouser();

        model.addAttribute("typeView", typeView);
        model.addAttribute("pathLine", fb.getPathLine());
        return "file-brouserFile";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/videosrc")
    //@GetMapping(value = "/videosrc", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource videoSource() {       // метод отдаёт выбранный видеофайл в видеопроигрыватель браузера
        return new FileSystemResource(new File(videoPath));
    }


    @PreAuthorize("hasAuthority('developers:read')")           // отдает страницу с детальным описанием выбраного каталога
    @PostMapping("/fileBrouser_folder")
    public String fileBrouser_deteils(@RequestParam String direction, Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе

        if(!Objects.equals(direction, "/")){
            direction += "/";
        }

        System.out.println("User path: " + pathCut + " Direction: " + direction);

        FileBrouserClass fb = new FileBrouserClass(direction, pathCut);
        fb.FileBrouser();

        fb.FileBrouserDetails();

        model.addAttribute("direction", direction);
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());

        return "file-brouser-details";
    }



    @PreAuthorize("hasAuthority('developers:read')")
    @PostMapping("/fileBrouser_delete")
    public String fileBrouser_delete(@RequestParam String direction, Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе

        System.out.println("User path: " + pathCut + " Direction: " + direction);


        File deleteDir = new File(pathCut + direction);
        deleteDir.delete();

        String directionSub = "";

        FileBrouserClass fb = new FileBrouserClass();

        if(!Objects.equals(direction, "/")) {
            int index = direction.lastIndexOf('/');
            directionSub = direction.substring(0,index);

            fb.setPath(directionSub);
            fb.setPathCut(pathCut);
            fb.FileBrouser();

            model.addAttribute("direction", directionSub);
            model.addAttribute("pathLine", fb.getPathLine());
            model.addAttribute("brouser", fb.getFileExplorer());
            return "file-brouser";

        }else{

            fb.setPath("/");
            fb.setPathCut(pathCut);
            fb.FileBrouser();

            model.addAttribute("direction", "/");
            model.addAttribute("pathLine", fb.getPathLine());
            model.addAttribute("brouser", fb.getFileExplorer());
            return "file-brouser";
        }

    }




    @PreAuthorize("hasAuthority('developers:read')")           // метод скачивает указанный файл
    @PostMapping("/fileBrouser_save")
    public ResponseEntity<Object> fileBrouser_save(@RequestParam String direction, Model model) throws IOException {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе

        System.out.println("User path: " + pathCut + " Direction: " + direction);

        File file = new File(pathCut + direction);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
//String base64Encoded = new String(encodeBase64, "UTF-8");
        String str = new String(file.getName().getBytes("UTF-8"),"UTF-8");

        System.out.println(str);

        headers.add("Content-Disposition", "attachment; filename= " + str);   //String.format("attachment; filename=\"%s\"", file.getName())
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ResponseEntity<Object>
                responseEntity = ResponseEntity.ok().headers(headers).contentLength(
                file.length()).contentType(MediaType.parseMediaType("application/txt")).body(resource);

        return responseEntity;
    }



    @PreAuthorize("hasAuthority('developers:read')")
    @PostMapping("/fileBrouser_save_archiv")
    public String fileBrouser_save_archiv(@RequestParam String direction, Model model) {


        return "redirect:/fileBrouser";
    }




    @PreAuthorize("hasAuthority('developers:read')")
    @PostMapping("/fileBrouser_create_folder")
    public String fileBrouser_create_folder(@RequestParam String direction, @RequestParam String nameFolder, Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе

        System.out.println("User path: " + pathCut + " Direction: " + direction);

        FileBrouserClass fb = new FileBrouserClass(direction, pathCut);
        fb.FileBrouser();
        File dir = new File(pathCut + direction);             // создаем объект с текущим адресом, где мы хотим создать новую папку
        List<String> strDir = new ArrayList<>();    // список всех папок по этому адресу

        if(dir.isDirectory()) {                     // заполняем список строками перечислением
            for(File item : dir.listFiles())
            {
                if(item.isDirectory())
                {
                    strDir.add(item.getName());
                }
            }
        }


        if(strDir.stream().anyMatch(lang -> lang.equals(nameFolder))){      // если в папке есть папка с именем как хотим создать, выходим

            fb.msgCreate(MsgType.DANGER.toString(),"err", "Папка с таким именем уже существует");

        }else{
            File dir1 = new File(pathCut + direction + "/" + nameFolder);

            boolean created = dir1.mkdir();          // создает новый каталог и при удачном создании возвращает значение true
            if(created){
                //System.out.println("Folder has been created");
                //fb.msgCreate(MsgType.SUCCESS.toString(),"err1", "Папка успешно создана");
                fb.FileBrouser();                   // если папка успешно создана, то обновим списки для отображения
            }else{
                fb.msgCreate(MsgType.WARNING.toString(),"err1", "Не удалось создать папку");
            }
        }

        model.addAttribute("direction", direction);
        model.addAttribute("message", fb.getMessage());
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }


    @PreAuthorize("hasAuthority('developers:read')")
    @PostMapping("/fileBrouser_rename_folder")
    public String fileBrouser_rename_folder(@RequestParam String direction, @RequestParam String nameFolder, Model model) {

        // boolean renameTo(File dest): переименовывает файл или каталог


        return "file-brouser";
    }



    @PreAuthorize("hasAuthority('developers:read')")
    @PostMapping("/addFile") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @RequestParam String direction, Model model) {

        String pathCut = getPath();        // возвращает разрешённый для использования адрес в файловой системе

        System.out.println("User path: " + pathCut + " Direction: " + direction);

        FileBrouserClass fb = new FileBrouserClass(direction, pathCut);

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Выберите файлы для загрузки");
            //return "redirect:uploadStatus";
        }

        try {
            byte[] bytes = file.getBytes();             // Get the file and save it somewhere

            Path path = Paths.get(pathCut + direction + "/" + file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
            fb.msgCreate(MsgType.DANGER.toString(),"err", "Файл не загружен");
        }

        fb.FileBrouser();

        model.addAttribute("direction", direction);
        model.addAttribute("message", fb.getMessage());
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }


    public String getPath(){                                        // метод достаёт из базы адрес папки, разрешённой для редактирования пользователю

        String email = getCurrentUsername();                        // тут мы узнаём кто авторизовался. дальше нужно пробежатся по списку

        Optional<User> user = userRepository.findByEmail(email);    // находим пользователя в базе
        User pathStr = user.get();                                  // преобразуем в экземпляр класса из Optional

        return pathStr.getPathline();
    }

    public String getCurrentUsername() {        // метод показывает ник того, кто сейчас авторизовался в сессии
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

}
