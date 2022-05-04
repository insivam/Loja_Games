package br.com.generation.LojaDeGames.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.com.generation.LojaDeGames.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    public List<Categoria> findAllBygeneroContainingIgnoreCase(String genero);

    public List<Categoria> findAllByGeneroContainingIgnoreCase(@Param("genero") String genero);

}