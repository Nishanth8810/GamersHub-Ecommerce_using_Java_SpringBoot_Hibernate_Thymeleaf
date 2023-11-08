package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.OrderItem;
import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.entity.Report;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
@Service
public class ReportService {
    @Autowired
    OrderService orderService;

    public byte[] exportPdfReport(String format) throws JRException {

        List<Orders> filteredUserOrders = orderService.getAllOrders();

        Collections.reverse(filteredUserOrders);

        List<Report> reportList = new ArrayList<>();

        for (Orders order : filteredUserOrders) {
            Report report = new Report();
            report.setOrderId(order.getId());
            report.setOrderDate(order.getLocalDateTime());
            report.setCustomer(order.getUser().getEmail());
            report.setTotalAmount(order.getAmount());
            report.setPaymentMethod(order.getPaymentMethod().getName());
            List<OrderItem> orderItemList = order.getOrderItems();

            for (int i=0; i<orderItemList.size();i++) {
                if (i==0)
                    report.setProducts(orderItemList.get(i).getProduct().getName() + " - " + orderItemList.get(i).getQuantity() + " nos.");
                else
                    report.setProducts(report.getProducts() + ", " + orderItemList.get(i).getProduct().getName() + " - " + orderItemList.get(i).getQuantity() + " nos.");
            }
            report.setOrderStatus(order.getOrderStatus().getStatus());
            reportList.add(report);
        }

        //Load jrxml file and compile it
        InputStream inputStream= getClass().getClassLoader().getResourceAsStream("salesreport.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        //Map resource to file
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportList);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("grandTotal", Math.round(filteredUserOrders.stream().map(Orders::getAmount).reduce(0, Integer::sum)*100.0)/100.0);
//        parameters.put("quantity", filteredUserOrders.stream().map(Orders).reduce(0, Integer::sum));
        parameters.put("totalOrders", filteredUserOrders.size());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        //Export to pdf
        if (format.equalsIgnoreCase("pdf")) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }

        //Export to xls
        if (format.equalsIgnoreCase("xlsx")) {

            JRBeanCollectionDataSource dataSource2 = new JRBeanCollectionDataSource(reportList);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            JasperPrint jasperPrint2 = JasperFillManager.fillReport(jasperReport, parameters, dataSource2);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint2);
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.exportReport();
            return outputStream.toByteArray();
        }

        return null;
    }

    public byte[] exportPdfReportForDate(LocalDate startDate,
                                         LocalDate endDate,
                                         String format) throws FileNotFoundException, JRException {

        List<Orders> filteredUserOrders = orderService.getAllOrders();
//        List<Order> filteredUserOrders = new ArrayList<>(userOrders.stream().filter(order -> order.getOrderStatus().getId() != 6).toList());
        Collections.reverse(filteredUserOrders);
        List<Orders> dateOrders = new ArrayList<>();
        for (Orders order : filteredUserOrders) {
            LocalDate localDate = order.getLocalDateTime().toLocalDate();
            if (localDate.isAfter(startDate.minusDays(1)) && localDate.isBefore(endDate.plusDays(1))) {
                dateOrders.add(order);
            }
        }

        List<Report> reportList = new ArrayList<>();

        for (Orders order : dateOrders) {
            Report report = new Report();
            report.setOrderId(order.getId());
            report.setOrderDate(order.getLocalDateTime());
            report.setCustomer(order.getUser().getEmail());
            report.setTotalAmount(order.getAmount());
            report.setPaymentMethod(order.getPaymentMethod().getName());
            List<OrderItem> orderItemList = order.getOrderItems();


            for (int i = 0; i < orderItemList.size(); i++) {
                if (i == 0)
                    report.setProducts(orderItemList.get(i).getProduct().getName() + " - " + orderItemList.get(i).getQuantity() + " nos.");
                else
                    report.setProducts(report.getProducts() + ", " + orderItemList.get(i).getProduct().getName() + " - " + orderItemList.get(i).getQuantity() + " nos.");
            }
            report.setOrderStatus(order.getOrderStatus().getStatus());
            reportList.add(report);
        }

        //Load jrxml file and compile it
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("salesreport.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        //Map resource to file
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportList);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("grandTotal", Math.round(filteredUserOrders.stream().map(Orders::getAmount).reduce(0, Integer::sum) * 100.0) / 100.0);
//        parameters.put("quantity", filteredUserOrders.stream().map(Orders).reduce(0, Integer::sum));
        parameters.put("totalOrders", filteredUserOrders.size());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        //Export to pdf
        if (format.equalsIgnoreCase("pdf")) {
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return pdfBytes;
        }

        //Export to xls
        if (format.equalsIgnoreCase("xlsx")) {

            JRBeanCollectionDataSource dataSource2 = new JRBeanCollectionDataSource(reportList);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            JasperPrint jasperPrint2 = JasperFillManager.fillReport(jasperReport, parameters, dataSource2);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint2);
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.exportReport();
            return outputStream.toByteArray();
        }

        return null;
    }


}
