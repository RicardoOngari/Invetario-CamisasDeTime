package com.camisastime.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.camisastime.model.ImagemProduto;
import com.camisastime.service.ImagemService;

@RestController
@RequestMapping("/api/imagens")
public class ImagemController {
    
    @Autowired
    private ImagemService imagemService;
    
    @PostMapping("/produto/{produtoId}")
    public ResponseEntity<Map<String, Object>> uploadImagem(
            @PathVariable Long produtoId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "principal", required = false) Boolean principal) {
        
        try {
            ImagemProduto imagem = imagemService.salvarImagem(produtoId, arquivo, principal);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", imagem.getId());
            response.put("nomeOriginal", imagem.getNomeOriginal());
            response.put("nomeArquivo", imagem.getNomeArquivo());
            response.put("tipoArquivo", imagem.getTipoArquivo());
            response.put("tamanhoArquivo", imagem.getTamanhoArquivo());
            response.put("principal", imagem.getPrincipal());
            response.put("message", "Imagem carregada com sucesso");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao processar arquivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<ImagemProduto>> listarImagensProduto(@PathVariable Long produtoId) {
        try {
            List<ImagemProduto> imagens = imagemService.listarImagensProduto(produtoId);
            return ResponseEntity.ok(imagens);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/produto/{produtoId}/principal")
    public ResponseEntity<ImagemProduto> obterImagemPrincipal(@PathVariable Long produtoId) {
        try {
            return imagemService.obterImagemPrincipal(produtoId)
                .map(imagem -> ResponseEntity.ok(imagem))
                .orElse(ResponseEntity.notFound().build());
                
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{imagemId}/arquivo")
    public ResponseEntity<byte[]> obterArquivoImagem(@PathVariable Long imagemId) {
        try {
            byte[] arquivo = imagemService.obterArquivoImagem(imagemId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Pode ser ajustado dinamicamente
            headers.setContentLength(arquivo.length);
            
            return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{imagemId}/principal")
    public ResponseEntity<Map<String, Object>> definirImagemPrincipal(@PathVariable Long imagemId) {
        try {
            imagemService.definirImagemPrincipal(imagemId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Imagem definida como principal com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/{imagemId}")
    public ResponseEntity<Map<String, Object>> deletarImagem(@PathVariable Long imagemId) {
        try {
            imagemService.deletarImagem(imagemId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Imagem deletada com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao deletar arquivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}