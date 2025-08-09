package com.ferreteria.app_web_ferreteria.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    @Value("${openai.api.key}")
    private String apiKey;

    private static final String STORE_CONTEXT = "Matizados Cris es una tienda de pinturas ubicada en Lima, Perú. " +
            "UBICACIÓN: Avenida Próceres de la Independencia 2078, rejas negras, casa color naranja. Cerca de la estación Los Postes de la Línea 1 del Metro de Lima. " +
            "HORARIO: Lunes a domingo de 9:00 AM a 8:00 PM. " +
            "CONTACTO: WhatsApp 942671817 para consultas y pedidos. " +
            "PRODUCTOS: Pinturas para paredes, madera, metal y productos especializados. " +
            "MARCAS: Anypsa, CPP, Losaro, Vencedor, Multicolor, Diamante, Jhomeron, Paracas, Tekno y Majestad. " +
            "TIPOS: Esmalte sintético, base automotriz, laca piroxilina, masilla fina, barniz poliuretano y selladores. " +
            "COLORES: Blanco, gris, marfil, azul, verde, naranja, rojo, amarillo, turquesa, violeta, melón, fucsia, marrón, coral, esmeralda, acuarela, sábila, rosado, blanco humo y miel.";

    private static final String SYSTEM_MESSAGE = "Eres el asistente virtual de Matizados Cris, una tienda de pinturas en Lima, Perú. " +
            "IMPORTANTE: Solo responde preguntas relacionadas con la tienda, sus productos, servicios, ubicación, contacto y horarios. " +
            "Si te preguntan algo fuera del contexto de la tienda (matemáticas, fecha actual, clima, otros negocios, etc.), responde: " +
            "'Soy el asistente de Matizados Cris. Te puedo ayudar con información sobre pinturas, ubicación, horarios y contacto. ¿En qué puedo asistirte sobre nuestra tienda?' " +
            "INSTRUCCIONES ESPECÍFICAS: " +
            "1. Para preguntas sobre UBICACIÓN/DIRECCIÓN: Responde 'Estamos ubicados en la Avenida Próceres de la Independencia 2078, rejas negras, casa color naranja. Cerca de la estación Los Postes de la Línea 1 del Metro.' " +
            "2. Para preguntas sobre CONTACTO/REDES SOCIALES/COMUNICACIÓN: Responde 'Puedes escribirnos o llamarnos por WhatsApp al 942671817 para consultas y pedidos.' " +
            "3. Para preguntas sobre HORARIOS: Responde 'Atendemos de lunes a domingo de 9:00 AM a 8:00 PM.' " +
            "4. Para PRODUCTOS/MARCAS/COLORES: Usa la información del contexto. " +
            "5. Para PRECIOS: Di 'Consulta los precios actualizados en nuestro catálogo o escríbenos al WhatsApp 942671817.' " +
            "6. Sé amable, conciso y profesional. " +
            "7. Mantente siempre en el contexto de la tienda de pinturas. " +
            "CONTEXTO DE LA TIENDA: " + STORE_CONTEXT;

    private final Map<String, List<Map<String, String>>> conversationMemory = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String userMessage = (String) body.getOrDefault("message", "");

        if (userMessage.isEmpty()) {
            return ResponseEntity.badRequest().body("Mensaje requerido");
        }

        String clientIP = request.getRemoteAddr();

        if (userMessage.trim().equalsIgnoreCase("/reset")) {
            conversationMemory.remove(clientIP);
            return ResponseEntity.ok(Map.of("respuesta", "Historial de conversación reiniciado."));
        }

        // Verificar respuestas directas para preguntas específicas
        String directResponse = getDirectResponse(userMessage);
        if (directResponse != null) {
            return ResponseEntity.ok(Map.of("respuesta", directResponse));
        }

        conversationMemory.putIfAbsent(clientIP, new ArrayList<>());
        List<Map<String, String>> fullMessages = conversationMemory.get(clientIP);

        if (fullMessages.isEmpty()) {
            fullMessages.add(Map.of("role", "system", "content", SYSTEM_MESSAGE));
        }

        fullMessages.add(Map.of("role", "user", "content", userMessage));

        // Preparar mensajes recientes (solo los últimos 10: 5 pares usuario/asistente)
        List<Map<String, String>> recentMessages = new ArrayList<>();
        recentMessages.add(fullMessages.get(0)); // system message

        int start = Math.max(1, fullMessages.size() - 10);
        recentMessages.addAll(fullMessages.subList(start, fullMessages.size()));

        // Debug: Verificar si la clave API está disponible
        System.out.println("DEBUG - API Key disponible: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("DEBUG - API Key length: " + (apiKey != null ? apiKey.length() : 0));
        System.out.println("DEBUG - API Key starts with: " + (apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "null"));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", recentMessages);
        requestBody.put("max_tokens", 150); // Aumentado para respuestas más completas
        requestBody.put("temperature", 0.3); // Menor aleatoriedad para mayor consistencia y precisión

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            Map response = restTemplate.postForObject(OPENAI_API_URL, entity, Map.class);
            String respuesta = "";
            if (response != null && response.containsKey("choices")) {
                List choices = (List) response.get("choices");
                if (!choices.isEmpty()) {
                    Map choice = (Map) choices.get(0);
                    Map message = (Map) choice.get("message");
                    respuesta = (String) message.get("content");
                    fullMessages.add(Map.of("role", "assistant", "content", respuesta));
                }
            }
            return ResponseEntity.ok(Map.of("respuesta", respuesta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("error", "Error al contactar con OpenAI: " + e.getMessage()));
        }
    }

    private String getDirectResponse(String userMessage) {
        String message = userMessage.toLowerCase().trim();
        
        // Detectar preguntas fuera del contexto de la tienda
        if (isOutOfContext(message)) {
            return "Soy el asistente de Matizados Cris, una tienda de pinturas. Te puedo ayudar con información sobre nuestros productos, ubicación, horarios y contacto. ¿En qué puedo asistirte sobre nuestra tienda?";
        }
        
        // Preguntas sobre ubicación/dirección
        if (message.contains("ubicación") || message.contains("ubicacion") || 
            message.contains("dirección") || message.contains("direccion") || 
            message.contains("donde") || message.contains("dónde") ||
            message.contains("están ubicados") || message.contains("como llegar") ||
            message.contains("como llego") || message.contains("encontrarlos")) {
            return "Estamos ubicados en la Avenida Próceres de la Independencia 2078, rejas negras, casa color naranja. Cerca de la estación Los Postes de la Línea 1 del Metro.";
        }
        
        // Preguntas sobre contacto/redes sociales
        if (message.contains("redes sociales") || message.contains("contacto") || 
            message.contains("whatsapp") || message.contains("teléfono") || 
            message.contains("telefono") || message.contains("comunicar") ||
            message.contains("llamar") || message.contains("escribir") ||
            message.contains("número") || message.contains("numero")) {
            return "Puedes escribirnos o llamarnos por WhatsApp al 942671817 para consultas y pedidos.";
        }
        
        // Preguntas sobre horarios
        if (message.contains("horario") || message.contains("hora") || 
            message.contains("abierto") || message.contains("cerrado") ||
            message.contains("atienden") || message.contains("abren")) {
            return "Atendemos de lunes a domingo de 9:00 AM a 8:00 PM.";
        }
        
        return null; // No hay respuesta directa, usar OpenAI
    }

    private boolean isOutOfContext(String message) {
        // Preguntas matemáticas
        if (message.matches(".*\\d+\\s*[+\\-*/]\\s*\\d+.*") || 
            message.contains("cuanto es") || message.contains("cuánto es") ||
            message.contains("suma") || message.contains("resta") ||
            message.contains("multiplicación") || message.contains("división")) {
            return true;
        }
        
        // Preguntas sobre fecha/tiempo actual
        if (message.contains("qué día es") || message.contains("que dia es") ||
            message.contains("qué fecha") || message.contains("que fecha") ||
            message.contains("qué hora") || message.contains("que hora") ||
            message.contains("hoy es") || message.contains("fecha actual")) {
            return true;
        }
        
        // Preguntas generales fuera del contexto
        if (message.contains("clima") || message.contains("tiempo") ||
            message.contains("noticias") || message.contains("futbol") ||
            message.contains("política") || message.contains("politica") ||
            message.contains("chistes") || message.contains("recetas") ||
            message.contains("medicina") || message.contains("salud") ||
            message.contains("universidad") || message.contains("estudios")) {
            return true;
        }
        
        // Preguntas sobre otros negocios o tiendas
        if (message.contains("otras tiendas") || message.contains("competencia") ||
            message.contains("supermercado") || message.contains("farmacia") ||
            message.contains("restaurant") || message.contains("restaurante")) {
            return true;
        }
        
        return false;
    }
}