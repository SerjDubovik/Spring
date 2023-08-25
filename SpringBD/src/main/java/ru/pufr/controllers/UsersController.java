package ru.pufr.controllers;

import ru.pufr.models.*;
import ru.pufr.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.FileInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;




@Controller
public class UsersController {

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/static/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/users")
    public String usersList (Model model){
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users-list";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/users/add")
    public String userListAdd(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String role,
                              @RequestParam String status,
                              Model model){



        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode(password);

        String str = new String();          // !!!!!! тестовый код !!!! проверка добавления новой колонки в базу

        User user = new User(email, hashedPassword, firstName, lastName, str, role, status);
        userRepository.save(user);
        return "redirect:/users";
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/adminka")
    public String adminkaAdmin(Model model){

        String email = getCurrentUsername();         // тут мы узнаём кто авторизовался. дальше нужно пробежатся по списку

        Optional<User> user = userRepository.findByEmail(email);
        ArrayList<User> nik = new ArrayList<>();
        user.ifPresent(nik::add);

        model.addAttribute("nik", nik);
        return "adminkaAdmin";
    }


    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/cabinet")
    public String Cabinet(Model model){

        String username = getCurrentUsername();         // тут мы узнаём кто авторизовался. дальше нужно пробежатся по списку
        model.addAttribute("username", username);
        return "adminkaUser";
    }

    /*
    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/addFile")
    public String addFiles(@RequestParam String file, Model model){

        String test = new String(file + "<p>Абзац</p><h1>Заголовок h1</h1>");

        model.addAttribute("test", test);
        return "demonstration";
    }
*/


    //@RequestMapping(value = "/download", method = RequestMethod.GET)
    @GetMapping("/download")
    public ResponseEntity<Object> downloadFile() throws IOException  {
        String filename = UPLOADED_FOLDER + "test.txt";
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ResponseEntity<Object>
                responseEntity = ResponseEntity.ok().headers(headers).contentLength(
                file.length()).contentType(MediaType.parseMediaType("application/txt")).body(resource);

        return responseEntity;
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/fileBrouser")
    public String fileBrouser(Model model) {            // @RequestParam String direction,

        String path = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/static/";

        /*
        String path = new String();
        if(direction.isEmpty())
        {
            path  = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/static/";
        }
        else
        {
            path = direction;
        }
        */

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

        //String path = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/static/";

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

        //String path = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/static/";

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




    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }



    @GetMapping("/registration")
    public String getRegistration() {

        return "registration";
    }



    @PostMapping("/registration")
    public String registration(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              Model model){


        Iterable<User> users = userRepository.findAll();

        for (User user : users) {

            if (user.getEmail().equals(email)) {             // проверим, есть ли в базе такой эмаил.
                String message = "Пользователь с такой почтой уже есть.";

                model.addAttribute("message", message);
                return "registration";                  // вернём пользователю ту же страницу, только с сообщением, что такое уже занято
            }
        }


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode(password);

        String role = "USER";
        String status = "BANNED";

        String uuid = UUID.randomUUID().toString();                     // создаём уникальный номер, для активации
        lastName = uuid;                                                // !!!!!! тестово закидываем созданный нами код в поле фамилии, потом пофиксить !!!!!!

        String str = new String();                                      // !!!!!! тестовый код !!!! проверка добавления новой колонки в базу

        User user = new User(email, hashedPassword, firstName, lastName, str, role, status);
        userRepository.save(user);

        // так почему-то не работает
        //MailController mailController = new MailController();       // вызываем метод отправки писем из контроллера по обслуживанию писем
        //mailController.sendEmail();

        sendEmailRegister("mail", uuid);

        return "redirect:/blog";                         // при успешной добавке просто переведём на стартовую страницу. !!!!! тестово !!!!!
    }




    @GetMapping("/RegistrationNewUser/{uuid}")
    public String blogEdit (@PathVariable(value = "uuid") String uuid, Model model){

            Iterable<User> users = userRepository.findAll();

            for (User user : users) {
                if (user.getLastName().equals(uuid)) {             // проверим, есть ли в базе такой активационный номер, и если найдём, то активируем эту учётную запись  !!! тестово используем ячейку LastName !!!!!


                    user.setRole("ACTIVE");
                    userRepository.save(user);

                    String message = user.getEmail() + "Активация прошла успешно!";

                    model.addAttribute("message", message);
                    return "registration";                  // вернём пользователю ту же страницу, только с сообщением, что активация прошла успешно
                }
            }



            String message = "Что-то пошло не так, активация не завершена. Ваша активационная ссылка не действительна.";
            model.addAttribute("message", message);
            return "registration";                  // вернём пользователю ту же страницу, только с сообщением, что такое уже занято
        }




    public String getCurrentUsername() {        // метод показывает ник того, кто сейчас авторизовался в сессии
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }


    void sendEmailRegister(String mail, String uuidString) {            // метод, для отправки письма для подтверждения аккаунта

        SimpleMailMessage msg = new SimpleMailMessage();

        String rnd =  "RegistrationNewUser/" + uuidString;
        //
        //http://localhost:59055/

        msg.setFrom("mail@pufr.ru");
        msg.setTo("mail@mail.com");          //, mail
        msg.setSubject("Активация аккаунта на PUFR.RU");
        msg.setText("Это сообщение сформировано автоматически, не отвечайте на это письмо.\n " +
                    "Если Вы не проходили регистрацию на сайте PUFR.RU, игнорируйте это письмо.\n" +
                    "Для активации аккаунта, перейдите по ссылке: " + "http://pufr.ru/" + rnd +" регистрация завершится автоматически.\n"+
                    "Ссылка будет активна не более суток с момента отправки сообщения");

        javaMailSender.send(msg);

    }




}
