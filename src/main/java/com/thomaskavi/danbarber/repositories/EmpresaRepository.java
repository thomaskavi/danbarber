
package com.thomaskavi.danbarber.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomaskavi.danbarber.entities.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
