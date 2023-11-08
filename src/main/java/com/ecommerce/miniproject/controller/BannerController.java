package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.BannerImage;
import com.ecommerce.miniproject.repository.BannerImageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class BannerController {
    public static String uploadDir = System.getProperty("user.dir") +
            "/src/main/resources/static/bannerImages";

    private final BannerImageRepository bannerImageRepository;

    public BannerController(BannerImageRepository bannerImageRepository) {
        this.bannerImageRepository = bannerImageRepository;
    }

    @GetMapping("/admin/banner")
    public String getAllBanner(Model model){
        model.addAttribute("banners",bannerImageRepository.findAll());
        return "banners";

    }
    @GetMapping("/admin/banner/add")
    public String addBanner(){
        return "bannerAdd";
    }
    @PostMapping("/admin/banner/add")
    public  String postAddBanner(@RequestParam("bannerImage")List<MultipartFile> fileList) throws IOException {

        if (fileList.isEmpty()){
            return "redirect:/admin/banner";
        }
        for (MultipartFile file:fileList){
            BannerImage bannerImage=new BannerImage();
            bannerImage.setName(file.getOriginalFilename());
            Path filename= Paths.get(uploadDir, file.getOriginalFilename());
            Files.write(filename,file.getBytes());
            bannerImageRepository.save(bannerImage);
        }
        return "redirect:/admin/banner";

    }
    @GetMapping("/admin/banner/delete/{id}")
    public String deleteBanner(@PathVariable long id){
        bannerImageRepository.deleteById(id);
        return "redirect:/admin/banner";

    }


}
