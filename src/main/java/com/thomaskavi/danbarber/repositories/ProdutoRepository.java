package com.thomaskavi.danbarber.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.thomaskavi.danbarber.entities.Produto;

import jakarta.persistence.LockModeType;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrueAndEmpresaId(Long empresaId);

    List<Produto> findByAtivoFalseAndEmpresaId(Long empresaId);

    Optional<Produto> findByIdAndEmpresaId(Long id, Long empresaId);

    boolean existsByNomeIgnoreCaseAndEmpresaId(String nome, Long empresaId);

    // Trava a linha no banco até a transação terminar — evita duas vendas
    // simultâneas decrementarem o mesmo estoque de forma inconsistente
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Produto p WHERE p.id = :id AND p.empresa.id = :empresaId")
    Optional<Produto> buscarComLockPorIdEEmpresa(Long id, Long empresaId);
}