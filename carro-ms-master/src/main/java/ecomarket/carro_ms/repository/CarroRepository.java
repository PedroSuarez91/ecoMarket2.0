package ecomarket.carro_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecomarket.carro_ms.model.Carro;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {

}
