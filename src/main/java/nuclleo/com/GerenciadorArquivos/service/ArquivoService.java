package nuclleo.com.GerenciadorArquivos.service;

import jakarta.annotation.PostConstruct;
import nuclleo.com.GerenciadorArquivos.dto.ArquivoResponseDTO;
import nuclleo.com.GerenciadorArquivos.entity.Arquivo;
import nuclleo.com.GerenciadorArquivos.exception.BusinessException;
import nuclleo.com.GerenciadorArquivos.exception.ResourceNotFoundException;
import nuclleo.com.GerenciadorArquivos.enums.TipoArquivo;
import nuclleo.com.GerenciadorArquivos.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ArquivoService {
    // Define as extensões de arquivo permitidas para fotos e documentos
    // Será utilizado para validar o tipo de arquivo enviado pelo usuário
    private static final Set<String> EXTENSOES_FOTO = Set.of(".jpg", ".jpeg", ".png");

    private static final Set<String> EXTENSOES_DOCUMENTO = Set.of(".pdf", ".doc", ".docx", ".txt");

    private static final Set<String> MIME_FOTO = Set.of(
            "image/jpeg",
            "image/png");

    private static final Set<String> MIME_DOCUMENTO = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain");

    @Autowired
    private ArquivoRepository arquivoRepository;

    // configuração do arquivo yaml para definir o local de armazenamento dos
    // arquivos
    @Value("${storage.location}")
    private String storageLocation;

    private Path pastaRaiz;

    // executa o método init() após a injeção de dependências, criando a pasta de
    // uploads se não existir
    @PostConstruct
    public void init() {
        this.pastaRaiz = Paths.get(storageLocation);
        try {
            Files.createDirectories(pastaRaiz);
        } catch (IOException e) {
            throw new BusinessException("Não foi possível criar a pasta de uploads.");
        }
    }

    @Transactional
    public ArquivoResponseDTO inserir(MultipartFile file) {
        validarArquivo(file);

        String extensao = obterExtensao(file.getOriginalFilename()).toLowerCase();
        TipoArquivo tipo = identificarTipoArquivo(extensao);

        validarMimeType(file.getContentType(), tipo);

        String nomeNoServidor = UUID.randomUUID().toString() + extensao;

        try {
            Files.copy(file.getInputStream(),
                    pastaRaiz.resolve(nomeNoServidor),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Falha ao salvar o arquivo no servidor.");
        }

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(nomeNoServidor)
                .toUriString();

        Arquivo arquivo = new Arquivo();
        arquivo.setNomeOriginal(file.getOriginalFilename());
        arquivo.setNomeNoServidor(nomeNoServidor);
        arquivo.setTipo(tipo);
        arquivo.setUrl(url);

        return new ArquivoResponseDTO(arquivoRepository.save(arquivo));
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponseDTO> listar(TipoArquivo tipo) {
        List<Arquivo> arquivos = (tipo != null)
                ? arquivoRepository.findByTipoAndAtivoTrueOrderByDtCriacaoDesc(tipo)
                : arquivoRepository.findByAtivoTrueOrderByDtCriacaoDesc();

        return arquivos.stream().map(ArquivoResponseDTO::new).toList();
    }

    @Transactional
    public void inativar(Long id) {
        Arquivo arquivo = arquivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado. Id: " + id));
        arquivo.setAtivo(false);
        arquivoRepository.save(arquivo);
    }

    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains("."))
            return "";
        return nomeArquivo.substring(nomeArquivo.lastIndexOf("."));
    }

    private void validarArquivo(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo foi enviado.");
        }

        // Olhar o arquivo resources/application.yaml para ver o tamanho máximo
        // permitido
        // O tamanho máximo permitido é de 100 MB
        long tamanhoMaximo = 100 * 1024 * 1024;

        if (file.getSize() > tamanhoMaximo) {
            throw new BusinessException("O arquivo excede o tamanho máximo permitido de 100 MB.");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new BusinessException("Nome do arquivo inválido.");
        }
    }

    private TipoArquivo identificarTipoArquivo(String extensao) {
        if (EXTENSOES_FOTO.contains(extensao)) {
            return TipoArquivo.FOTO;
        }

        if (EXTENSOES_DOCUMENTO.contains(extensao)) {
            return TipoArquivo.DOCUMENTO;
        }

        throw new BusinessException(
                "Formato de arquivo não permitido. São aceitos apenas JPG, JPEG, PNG, PDF, DOC, DOCX e TXT.");
    }

    private void validarMimeType(String contentType, TipoArquivo tipo) {
        if (contentType == null || contentType.isBlank()) {
            throw new BusinessException("Não foi possível identificar o tipo do arquivo.");
        }

        if (tipo == TipoArquivo.FOTO && !MIME_FOTO.contains(contentType)) {
            throw new BusinessException("O arquivo enviado não corresponde a uma imagem válida.");
        }

        if (tipo == TipoArquivo.DOCUMENTO && !MIME_DOCUMENTO.contains(contentType)) {
            throw new BusinessException("O arquivo enviado não corresponde a um documento válido.");
        }
    }

}