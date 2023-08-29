package ru.pufr.controllers;

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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileBrouserController {



    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/fileBrouser")
    public String fileBrouser(Model model) {            // @RequestParam String direction,

        String path = "/opt/test/";

        Map<Integer, FileView> brouser = new HashMap<Integer, FileView>();

        File dir = new File(path);                  // определяем объект для каталога

        if(dir.isDirectory())
        {

            int i = 0;

            for(File item : dir.listFiles())       // получаем все вложенные объекты в каталоге
            {
                if(item.isDirectory())
                {
                    brouser.put(i, new FileView("logo",item.getName(),"folder", "", path));

                    i++;
                }
                else{

                    brouser.put(i, new FileView("logo",item.getName(),"file", Long.toString(item.length()) + " Б", ""));

                    i++;
                }
            }
        }

        Map<Integer, FileViewAddressPath> pathLine = new HashMap<Integer, FileViewAddressPath>();


        String subВirection = path.substring(1);               // обрежем первый слеш в строке, мешает для следующего разбития строки на слова по слеши
        String[] words = subВirection.split("/");       // ну и тут строку на слова по слеши

        String str = new String();

        int i = 0;

        for(String word : words){
            str = str + "/" + word;
            pathLine.put(i, new FileViewAddressPath(word, str));
            i++;
        }

        model.addAttribute("direction", path);
        model.addAttribute("pathLine", pathLine);
        model.addAttribute("brouser", brouser);
        return "file-brouser";
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser_delete")
    public String fileBrouser_delete(@RequestParam String direction, Model model) {

        Map<Integer, FileView> brouser = new HashMap<Integer, FileView>();


        File deleteDir = new File(direction);
        deleteDir.delete();


        int index = direction.lastIndexOf('/');
        String directionSub = direction.substring(0,index);

        File dir = new File(directionSub);                  // определяем объект для каталога

        directionSub = directionSub + "/";

        if(dir.isDirectory())
        {

            int i = 0;

            for(File item : dir.listFiles())       // получаем все вложенные объекты в каталоге
            {
                if(item.isDirectory())
                {
                    brouser.put(i, new FileView("logo",item.getName(),"folder", "", directionSub));

                    i++;
                }
                else{

                    brouser.put(i, new FileView("logo",item.getName(),"file", Long.toString(item.length()) + " Б", directionSub));

                    i++;
                }
            }
        }

        Map<Integer, FileViewAddressPath> pathLine = new HashMap<Integer, FileViewAddressPath>();


        String subВirection = directionSub.substring(1);            // обрежем первый слеш в строке, мешает для следующего разбития строки на слова по слеши
        String[] words = subВirection.split("/");       // ну и тут строку на слова по слеши

        String str = new String();

        int i = 0;

        for(String word : words){
            str = str + "/" + word;
            pathLine.put(i, new FileViewAddressPath(word, str));
            i++;
        }

        model.addAttribute("direction", subВirection);
        model.addAttribute("pathLine", pathLine);
        model.addAttribute("brouser", brouser);
        return "file-brouser";
    }




    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/fileBrouser")
    public String fileBrouser_edit(@RequestParam String direction, Model model) {

        Map<Integer, FileView> brouser = new HashMap<Integer, FileView>();

        File dir = new File(direction);                  // определяем объект для каталога

        direction = direction + "/";

        if(dir.isDirectory())
        {

            int i = 0;

            for(File item : dir.listFiles())       // получаем все вложенные объекты в каталоге
            {
                if(item.isDirectory())
                {
                    brouser.put(i, new FileView("logo",item.getName(),"folder", "", direction));

                    i++;
                }
                else{

                    brouser.put(i, new FileView("logo",item.getName(),"file", Long.toString(item.length()) + " Б", direction));

                    i++;
                }
            }
        }

        Map<Integer, FileViewAddressPath> pathLine = new HashMap<Integer, FileViewAddressPath>();


        String subВirection = direction.substring(1);            // обрежем первый слеш в строке, мешает для следующего разбития строки на слова по слеши
        String[] words = subВirection.split("/");       // ну и тут строку на слова по слеши

        String str = new String();

        int i = 0;

        for(String word : words){
            str = str + "/" + word;
            pathLine.put(i, new FileViewAddressPath(word, str));
            i++;
        }

        model.addAttribute("direction", direction);
        model.addAttribute("pathLine", pathLine);
        model.addAttribute("brouser", brouser);
        return "file-brouser";
    }




    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/addFile") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @RequestParam String direction, Model model) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {


            byte[] bytes = file.getBytes();             // Get the file and save it somewhere


            Path path = Paths.get(direction + "/" + file.getOriginalFilename());
            Files.write(path, bytes);


            /*
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

            String test = new String(bytes, "UTF-8");           // преобразует массив байт в строку
            model.addAttribute("test", test);
            return "demonstration";
            */

        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("direction", direction);
        return "redirect:/fileBrouser";
        //return "file-brouser";
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }


}
