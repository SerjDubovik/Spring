package ru.pufr.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class FileBrouserController {


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/fileBrouser")
    public String fileBrouser(Model model) {
        // Метод startsWith() позволяют определить начинается ли строка с определенной подстроки
        // тут должна быть проверка пути на валидность
        // String path = "/opt/test/";
       String path = "/";

        FileBrouserClass fb = new FileBrouserClass(path);
        fb.FileBrouser();

        model.addAttribute("direction", path);
        //model.addAttribute("message", fb.getMessage());
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser")
    public String fileBrouser(@RequestParam String direction, Model model) {

        // тут должна быть проверка пути на валидность

        direction = direction + "/";
        FileBrouserClass fb = new FileBrouserClass(direction);
        fb.FileBrouser();

        model.addAttribute("direction", direction);
        //model.addAttribute("message", fb.getMessage());
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }


    @PreAuthorize("hasAuthority('developers:write')")           // передаёт страницу для редактирования и просмотра файлов, если поддерживается
    @PostMapping("/fileBrouser_file")
    public String fileBrouserFile(@RequestParam String direction, Model model) {

        // тут должна быть проверка пути на валидность

        File dir = new File(direction);                  // определяем объект для каталога

        String nameFile = dir.getName();
        String fileExtension;

        FileBrouserClass fb = new FileBrouserClass(direction);

            if(fb.getExtensionByStringHandling(nameFile).isPresent()){     // берем расширение файла в строку
                fileExtension = fb.getExtensionByStringHandling(nameFile).get();
            }else{
                fileExtension = "file extension Not found";
            }

            int lastSlesh = direction.lastIndexOf('/');     // находим номер по порядку последнего слеша в адресе
            String subDirection = direction.substring(lastSlesh);   // обрезаем из пути имя выбранного объекта
            direction = direction.replace(subDirection, "");    // заменяем в пути имя выбранного объекта пустотой. (поискать решение по красивее)


        fb.setPath(direction);
        fb.FileBrouser();

        model.addAttribute("nameFile", nameFile);
        model.addAttribute("fileExtension", fileExtension);
        model.addAttribute("pathLine", fb.getPathLine());
        return "file-brouserFile";
    }


    @PreAuthorize("hasAuthority('developers:write')")           // отдает страницу с детальным описанием выбраного каталога
    @PostMapping("/fileBrouser_folder")
    public String fileBrouser_deteils(@RequestParam String direction, Model model) {

        // тут должна быть проверка пути на валидность

        direction = direction + "/";
        FileBrouserClass fb = new FileBrouserClass(direction);
        fb.FileBrouser();

        fb.FileBrouserDetails();

        model.addAttribute("direction", direction);
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());

        return "file-brouser-details";
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser_delete")
    public String fileBrouser_delete(@RequestParam String direction, Model model) {

        // написать механизм для подтверждения удаления

        File deleteDir = new File(direction);
        deleteDir.delete();


        int index = direction.lastIndexOf('/');
        String directionSub = direction.substring(0,index);

        directionSub = directionSub + "/";

        FileBrouserClass fb = new FileBrouserClass(directionSub);
        fb.FileBrouser();

        model.addAttribute("direction", directionSub);
        model.addAttribute("pathLine", fb.getPathLine());
        model.addAttribute("brouser", fb.getFileExplorer());
        return "file-brouser";
    }




    @PreAuthorize("hasAuthority('developers:write')")           // метод скачивает указанный файл
    @PostMapping("/fileBrouser_save")
    public ResponseEntity<Object> fileBrouser_save(@RequestParam String direction, Model model) throws IOException {

        File file = new File(direction);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();

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



    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser_save_archiv")
    public String fileBrouser_save_archiv(@RequestParam String direction, Model model) {


        return "redirect:/fileBrouser";
    }




    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser_create_folder")
    public String fileBrouser_create_folder(@RequestParam String direction, @RequestParam String nameFolder, Model model) {

        // direction проверить на валидность пути пользователя

        FileBrouserClass fb = new FileBrouserClass(direction);
        fb.FileBrouser();
        File dir = new File(direction);             // создаем объект с текущим адресом, где мы хотим создать новую папку
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
            File dir1 = new File(direction + "/" + nameFolder);

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


    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser_rename_folder")
    public String fileBrouser_rename_folder(@RequestParam String direction, @RequestParam String nameFolder, Model model) {

        // boolean renameTo(File dest): переименовывает файл или каталог


        return "file-brouser";
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/addFile") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @RequestParam String direction, Model model) {

        FileBrouserClass fb = new FileBrouserClass(direction);

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Выберите файлы для загрузки");
            //return "redirect:uploadStatus";
        }

        try {
            byte[] bytes = file.getBytes();             // Get the file and save it somewhere

            Path path = Paths.get(direction + "/" + file.getOriginalFilename());
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

}
