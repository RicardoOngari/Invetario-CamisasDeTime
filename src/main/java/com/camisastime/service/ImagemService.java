package com.camisastime.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.camisastime.model.ImagemProduto;
import com.camisastime.model.Produto;
import com.camisastime.repository.ImagemProdutoRepository;

@Service
@Transactional
public class ImagemService {
    
    @Value("${app.upload.dir:uploads/produtos}")
    private String uploadDir;
    
    @Value("${app.upload.max-file-size:5242880}") // 5MB
    private long maxFileSize;
    
    @Autowired
    private ImagemProdutoRepository imagemRepository;
    
    @Autowired
    private ProdutoService produtoService;
    
    private static final List<String> TIPOS_PERMITIDOS = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    public ImagemProduto salvarImagem(Long produtoId, MultipartFile arquivo, Boolean principal) throws IOException {
        
        validarArquivo(arquivo);
        
        Produto produto = produtoService.buscarPorId(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
       
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
       
        String extensao = obterExtensao(arquivo.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID().toString() + "." + extensao;
        Path caminhoCompleto = uploadPath.resolve(nomeArquivo);
        
       
        Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
        
      
        if (principal != null && principal) {
            imagemRepository.removerImagemPrincipalDoProduto(produtoId);
        } else if (principal == null) {
            
            long totalImagens = imagemRepository.countByProdutoId(produtoId);
            principal = totalImagens == 0;
        }
        
     
        ImagemProduto imagem = new ImagemProduto(
            produto,
            arquivo.getOriginalFilename(),
            nomeArquivo,
            caminhoCompleto.toString(),
            arquivo.getContentType(),
            arquivo.getSize()
        );
        imagem.setPrincipal(principal);
        
        return imagemRepository.save(imagem);
    }
    
    public List<ImagemProduto> listarImagensProduto(Long produtoId) {
        return imagemRepository.findByProdutoId(produtoId);
    }
    
    public Optional<ImagemProduto> obterImagemPrincipal(Long produtoId) {
        return imagemRepository.findByProdutoIdAndPrincipalTrue(produtoId);
    }
    
    public void definirImagemPrincipal(Long imagemId) {
        ImagemProduto imagem = imagemRepository.findById(imagemId)
            .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
        
        // Remover flag principal das outras imagens do produto
        imagemRepository.removerImagemPrincipalDoProduto(imagem.getProduto().getId());
        
        // Definir esta imagem como principal
        imagem.setPrincipal(true);
        imagemRepository.save(imagem);
    }
    
    public void deletarImagem(Long imagemId) throws IOException {
        ImagemProduto imagem = imagemRepository.findById(imagemId)
            .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
        
        // Deletar arquivo do sistema
        Path arquivo = Paths.get(imagem.getCaminhoArquivo());
        if (Files.exists(arquivo)) {
            Files.delete(arquivo);
        }
        
        // Se era principal, definir outra como principal
        if (imagem.getPrincipal()) {
            List<ImagemProduto> outrasImagens = imagemRepository.findByProdutoId(imagem.getProduto().getId());
            outrasImagens.stream()
                .filter(img -> !img.getId().equals(imagemId))
                .findFirst()
                .ifPresent(img -> {
                    img.setPrincipal(true);
                    imagemRepository.save(img);
                });
        }
        
        // Deletar registro do banco
        imagemRepository.delete(imagem);
    }
    
    public byte[] obterArquivoImagem(Long imagemId) throws IOException {
        ImagemProduto imagem = imagemRepository.findById(imagemId)
            .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
        
        Path arquivo = Paths.get(imagem.getCaminhoArquivo());
        if (!Files.exists(arquivo)) {
            throw new RuntimeException("Arquivo de imagem não encontrado no sistema");
        }
        
        return Files.readAllBytes(arquivo);
    }
    
    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo não pode estar vazio");
        }
        
        if (arquivo.getSize() > maxFileSize) {
            throw new RuntimeException("Arquivo muito grande. Máximo permitido: " + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new RuntimeException("Tipo de arquivo não permitido. Tipos aceitos: JPG, PNG, GIF, WEBP");
        }
    }
    
    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "jpg";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();
    }
}