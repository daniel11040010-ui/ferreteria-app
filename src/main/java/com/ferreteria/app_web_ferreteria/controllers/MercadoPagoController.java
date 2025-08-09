package com.ferreteria.app_web_ferreteria.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/mercadopago")
@CrossOrigin(origins = "*")
public class MercadoPagoController {

    @Value("${myapp.public-url}")
    private String publicUrl;

    // TESTUSER2021313455 - F8dJxAD1tP
    // 8950538817078921

    //private static final String ACCESS_TOKEN = "APP_USR-1541387343191117-052412-799a4fc19b3f3389ce5c73a349569df6-548243961";
    // ESTE ES TU TOKEN DE PRODUCCIÓN, ÚSALO CUANDO LA APP ESTÉ LIVE.
    // private static final String ACCESS_TOKEN = "APP_USR-8950538817078921-052420-e506d452c793d98030039251368ccedb-2454927501";

    // USA ESTE TOKEN DE PRUEBA PARA DESARROLLO. Reemplázalo con tu Access Token de Prueba.
    
    private static final String ACCESS_TOKEN = "APP_USR-8950538817078921-052420-e506d452c793d98030039251368ccedb-2454927501";
    private static final String API_URL = "https://api.mercadopago.com/checkout/preferences";

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody Map<String, Object> datos) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(ACCESS_TOKEN);

            List<Map<String, Object>> items = new ArrayList<>();
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) datos.get("items");
            for (Map<String, Object> itemData : itemsData) {
                Map<String, Object> item = new HashMap<>();
                item.put("title", itemData.get("title"));
                item.put("quantity", itemData.get("quantity"));
                item.put("unit_price", itemData.get("unit_price"));
                items.add(item);
            }

            // Configuramos los métodos de pago
            Map<String, Object> paymentMethods = new HashMap<>();
            List<Map<String, String>> excludedPaymentTypes = new ArrayList<>();
            excludedPaymentTypes.add(Collections.singletonMap("id", "credit_card"));
            excludedPaymentTypes.add(Collections.singletonMap("id", "debit_card"));
            excludedPaymentTypes.add(Collections.singletonMap("id", "ticket")); // Excluye PagoEfectivo y otros en efectivo
            paymentMethods.put("excluded_payment_types", excludedPaymentTypes);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("items", items);
            requestBody.put("payment_methods", paymentMethods);

            // 1. Usamos la URL pública configurada manualmente desde application.properties
            if (publicUrl == null || publicUrl.contains("pon-aqui-tu-url")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "La URL pública no está configurada correctamente en application.properties."));
            }

            Map<String, String> backUrls = new HashMap<>();
            backUrls.put("success", publicUrl + "/pedidos/pedidos");
            backUrls.put("failure", publicUrl + "/carrito");
            backUrls.put("pending", publicUrl + "/mis-pedidos");
            requestBody.put("back_urls", backUrls);

            // 2. Habilitamos la redirección automática
            requestBody.put("auto_return", "approved");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(API_URL, request, Map.class);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error completo en los logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al crear preferencia: " + e.getMessage()));
        }
    }
}
