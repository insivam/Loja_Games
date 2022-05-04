package br.com.generation.LojaDeGames.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.generation.LojaDeGames.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    public List<Produto> findAllByNomeContainingIgnoreCase(@Param("nome") String nome);

    public List<Produto> findByPrecoGreaterThan(Double preco);

    public List<Produto> findAllByPrecoLessThan(Double preco);

    public List<Produto> findByPrecoBetween(Double preco_inicial, Double preco_final);

    public List<Produto> findAllByOrderByPrecoAsc();

    public List<Produto> findAllByOrderByPrecoDesc();

}
