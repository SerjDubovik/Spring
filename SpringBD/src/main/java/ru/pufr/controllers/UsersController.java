package ru.pufr.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;

import org.springframework.web.multipart.MultipartFile;

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
    private JavaMailSender javaMailSender;



    @GetMapping("/users")
    public String usersList (Model model){

        return "users-list";
    }





    @GetMapping("/adminka")
    public String adminkaAdmin(Model model){

        return "adminkaAdmin";
    }



    @GetMapping("/cabinet")
    public String Cabinet(Model model){

        return "adminkaUser";
    }


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
