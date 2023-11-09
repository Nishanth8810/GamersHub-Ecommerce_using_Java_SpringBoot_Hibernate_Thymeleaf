package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.repository.OrderRepository;
import com.ecommerce.miniproject.service.OrderService;
import com.ecommerce.miniproject.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

@Controller

public class AdminSalesController {
    @Autowired
    OrderService orderService;
    @Autowired
    ReportService reportService;

    public AdminSalesController(OrderRepository orderRepository) {

    }
    @GetMapping("/admin/salesReport")
    public String salesReport(Model model){
        model.addAttribute("userOrder",orderService.getAllOrders());
        model.addAttribute("totalOrders",orderService.getAllOrders().size());
        model.addAttribute("totalSales",orderService.getAllOrders().stream().mapToDouble(Orders::getAmount).sum());

        return "salesReport";
    }
    @PostMapping("/admin/salesReport")
    public String  postSalesReport(@RequestParam("dateFilter")String dateFilter){

        return "redirect:/admin/salesReport";
    }

    @GetMapping("/generateReport/{format}")
    public void generateReport(@PathVariable String format, HttpServletResponse response) throws JRException, FileNotFoundException {

        byte[] reportData = reportService.exportPdfReport(format);

        response.setHeader("Content-Disposition", "attachment; filename=salesreport." + format);
        response.setContentType("application/" + format);

        try (OutputStream os = response.getOutputStream()) {
            os.write(reportData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/downloadReportDate")
    public void downloadReportDate(@ModelAttribute("startDate") LocalDate startDate,
                                   @ModelAttribute("endDate") LocalDate endDate,
                                   @ModelAttribute("format") String format,
                                   HttpServletResponse response) throws JRException, FileNotFoundException {

        byte[] reportData = reportService.exportPdfReportForDate(startDate, endDate, format);

        response.setHeader("Content-Disposition", "attachment; filename=salesreport." + format);
        response.setContentType("application/" + format);

        try (OutputStream os = response.getOutputStream()) {
            os.write(reportData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
