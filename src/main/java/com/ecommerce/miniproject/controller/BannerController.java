package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.aws.StorageService;
import com.ecommerce.miniproject.entity.BannerImage;
import com.ecommerce.miniproject.repository.BannerImageRepository;
import com.ecommerce.miniproject.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class BannerController {
    public static String uploadDir = System.getProperty("user.dir") +
            "/src/main/resources/static/bannerImages";

    @Autowired
    BannerService bannerService;
    @Autowired
    StorageService storageService;

    @GetMapping("/admin/banner")
    public String getAllBanner(Model model){
        model.addAttribute("banners",bannerService.findAllBanners());
        model.addAttribute("urlList",storageService.getUrlListBanner(bannerService.findAllBanners()));
        return "banners";

    }
    @GetMapping("/admin/banner/add")
    public String addBanner(){
        return "bannerAdd";
    }
    @PostMapping("/admin/banner/add")
    public  String postAddBanner(@RequestParam("bannerImage")
                                     List<MultipartFile> fileList) throws IOException {

        if (fileList.isEmpty()){
            return "redirect:/admin/banner";
        }
        for (MultipartFile file:fileList){
            BannerImage bannerImage=new BannerImage();
            String imageUUID = storageService.uploadFile(fileList.get(0));
                bannerImage.setName(imageUUID);
            bannerService.saveBanner(bannerImage);
        }
        return "redirect:/admin/banner";

    }
    @GetMapping("/admin/banner/delete/{id}")
    public String deleteBanner(@PathVariable long id, RedirectAttributes redirectAttributes){
        if (bannerService.findAllBanners().size()==1){
            redirectAttributes.addFlashAttribute("deleteError","At least one banner is required on the page.");
            return "redirect:/admin/banner";
        }
        bannerService.deleteBannerById(id);
        redirectAttributes.addFlashAttribute("deleteSuccess");
        return "redirect:/admin/banner";

    }


}
