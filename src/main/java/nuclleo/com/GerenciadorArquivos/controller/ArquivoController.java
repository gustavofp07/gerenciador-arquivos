package nuclleo.com.GerenciadorArquivos.controller;

import nuclleo.com.GerenciadorArquivos.dto.ArquivoResponseDTO;
import nuclleo.com.GerenciadorArquivos.enums.TipoArquivo;
import nuclleo.com.GerenciadorArquivos.service.ArquivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/arquivos")
@Tag(name = "Arquivo")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ArquivoController {
    @Autowired
    private ArquivoService arquivoService;

    /*
    @PostMapping
    @Operation(summary = "Fazer upload de um arquivo")
    public ResponseEntity<ArquivoResponseDTO> inserir(@RequestParam("file") MultipartFile file,
            @RequestParam("tipo") TipoArquivo tipo) {
        if (tipo == null) {
            throw new BusinessException("O tipo de arquivo é obrigatório.");
        }

        ArquivoResponseDTO dto = arquivoService.inserir(file, tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
        */

    @PostMapping
    @Operation(summary = "Fazer upload de um arquivo")
    public ResponseEntity<ArquivoResponseDTO> inserir(
            @RequestParam("file") MultipartFile file) {

        ArquivoResponseDTO dto = arquivoService.inserir(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar arquivos ativos")
    public ResponseEntity<List<ArquivoResponseDTO>> listar(
            @RequestParam(required = false) TipoArquivo tipo) {
        return ResponseEntity.ok(arquivoService.listar(tipo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar um arquivo")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        arquivoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}