package com.thomaskavi.danbarber.security;

import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.enums.Modulo;
import com.thomaskavi.danbarber.services.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModuloGuard {

    private final AutenticacaoService autenticacaoService;

    public void exigirModulo(Modulo moduloEsperado) {
        Empresa empresa = autenticacaoService.obterUsuarioLogado().getEmpresa();
        if (empresa == null || !empresa.getModulosAtivos().contains(moduloEsperado)) {
            throw new AccessDeniedException("Módulo '" + moduloEsperado + "' não habilitado para esta empresa");
        }
    }
}