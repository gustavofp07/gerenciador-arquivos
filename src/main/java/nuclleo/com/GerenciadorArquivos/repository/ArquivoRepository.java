package nuclleo.com.GerenciadorArquivos.repository;

import nuclleo.com.GerenciadorArquivos.entity.Arquivo;
import nuclleo.com.GerenciadorArquivos.enums.TipoArquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {
    List<Arquivo> findByAtivoTrueOrderByDtCriacaoDesc();

    List<Arquivo> findByTipoAndAtivoTrueOrderByDtCriacaoDesc(TipoArquivo tipo);
}