package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.InvoiceDataset;
import com.ecommerce.miniproject.entity.OrderItem;
import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class InvoiceController {
    @Autowired
    OrderService orderService;


    @GetMapping("/invoice/{id}")
    public void generateInvoice(@PathVariable Long id,
                                HttpServletResponse response) throws IOException, JRException {

        Orders order = orderService.getOrderById(id).orElseThrow();

        List<InvoiceDataset> invoiceDatasetList = new ArrayList<>();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("customerName", order.getUser().getFirstName() + " " + order.getUser().getLastName());
        parameters.put("customerEmail", order.getUser().getEmail());
        parameters.put("customerAddress", order.getAddress().getFullAddress());
        parameters.put("orderId", order.getId());
        parameters.put("orderDate", order.getLocalDateTime());
        parameters.put("orderStatus", order.getOrderStatus().getStatus());
        parameters.put("paymentMethod", order.getPaymentMethod().getName());
        parameters.put("total", order.getAmount());

        List<OrderItem> orderItemList = order.getOrderItems();

            for (OrderItem orderItem : orderItemList) {
            InvoiceDataset invoiceDataset = new InvoiceDataset();
            invoiceDataset.setProductName(orderItem.getProduct().getName());
            invoiceDataset.setQuantity(orderItem.getQuantity());
            invoiceDataset.setUnitPrice(orderItem.getProduct().getPrice());
            invoiceDataset.setStotal(orderItem.getSubtotal());

            invoiceDatasetList.add(invoiceDataset);

        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoiceDatasetList);

        parameters.put("invoiceDataset", dataSource);

        //Load jrxml file and compile it
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("invoice.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");
        response.setContentType("application/pdf");

        response.getOutputStream().write(pdfBytes);

    }

}
