package com.thomaskavi.danbarber.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.AjusteEstoqueRequestDTO;
import com.thomaskavi.danbarber.dtos.ProdutoRequestDTO;
import com.thomaskavi.danbarber.dtos.ProdutoResponseDTO;
import com.thomaskavi.danbarber.dtos.TipoAjusteEstoque;
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.entities.Produto;
import com.thomaskavi.danbarber.enums.Modulo;
import com.thomaskavi.danbarber.repositories.ProdutoRepository;
import com.thomaskavi.danbarber.security.ModuloGuard;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final AutenticacaoService autenticacaoService;
    private final ModuloGuard moduloGuard;

    public ProdutoResponseDTO registrar(ProdutoRequestDTO dto) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);

        Empresa empresa = autenticacaoService.obterUsuarioLogado().getEmpresa();

        if (produtoRepository.existsByNomeIgnoreCaseAndEmpresaId(dto.nome(), empresa.getId())) {
            throw new IllegalArgumentException("Já existe um produto com esse nome");
        }

        Produto produto = Produto.builder()
                .nome(dto.nome())
                .preco(dto.preco())
                .quantidadeEstoque(dto.quantidadeEstoque())
                .empresa(empresa)
                .ativo(true)
                .build();

        return toResponseDTO(produtoRepository.save(produto));
    }

    public List<ProdutoResponseDTO> listarAtivos() {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return produtoRepository.findByAtivoTrueAndEmpresaId(empresaId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ProdutoResponseDTO> listarInativos() {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return produtoRepository.findByAtivoFalseAndEmpresaId(empresaId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Produto produto = buscarOuFalhar(id);
        produto.setNome(dto.nome());
        produto.setPreco(dto.preco());
        return toResponseDTO(produtoRepository.save(produto));
    }

    // Ajuste manual de estoque (entrada de mercadoria, perda, correção de inventário)
    // Separado da venda, que faz baixa automática — ver VendaService
    @Transactional
    public ProdutoResponseDTO ajustarEstoque(Long id, AjusteEstoqueRequestDTO dto) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Produto produto = buscarOuFalhar(id);

        if (dto.tipo() == TipoAjusteEstoque.ENTRADA) {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + dto.quantidade());
        } else {
            if (produto.getQuantidadeEstoque() < dto.quantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para essa saída");
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - dto.quantidade());
        }

        return toResponseDTO(produtoRepository.save(produto));
    }

    public void desativar(Long id) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Produto produto = buscarOuFalhar(id);
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    public ProdutoResponseDTO reativar(Long id) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Produto produto = buscarOuFalhar(id);
        produto.setAtivo(true);
        return toResponseDTO(produtoRepository.save(produto));
    }

    private Produto buscarOuFalhar(Long id) {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return produtoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: id " + id));
    }

    private ProdutoResponseDTO toResponseDTO(Produto p) {
        return new ProdutoResponseDTO(p.getId(), p.getNome(), p.getPreco(), p.getQuantidadeEstoque(), p.isAtivo());
    }
}