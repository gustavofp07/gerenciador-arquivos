package nuclleo.com.GerenciadorArquivos.dto;

import nuclleo.com.GerenciadorArquivos.entity.Arquivo;
import nuclleo.com.GerenciadorArquivos.enums.TipoArquivo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ArquivoResponseDTO {
    private Long id;
    private String nomeOriginal;
    private TipoArquivo tipo;
    private String url;
    private Boolean ativo;
    private LocalDateTime dtCriacao;

    public ArquivoResponseDTO(Arquivo arquivo) {
        this.id = arquivo.getId();
        this.nomeOriginal = arquivo.getNomeOriginal();
        this.tipo = arquivo.getTipo();
        this.url = arquivo.getUrl();
        this.ativo = arquivo.getAtivo();
        this.dtCriacao = arquivo.getDtCriacao();
    }
}
