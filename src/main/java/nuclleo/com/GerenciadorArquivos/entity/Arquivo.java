package nuclleo.com.GerenciadorArquivos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nuclleo.com.GerenciadorArquivos.enums.TipoArquivo;


import java.time.LocalDateTime;

@Entity
@Table(name = "arquivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nomeOriginal;

    @Column(nullable = false, unique = true, length = 255)
    private String nomeNoServidor;

    // @Column(nullable = false, length = 50)
    // private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoArquivo tipo;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private LocalDateTime dtCriacao = LocalDateTime.now();
}