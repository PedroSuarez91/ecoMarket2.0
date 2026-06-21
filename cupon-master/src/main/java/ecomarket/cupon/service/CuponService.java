package ecomarket.cupon.service;

import ecomarket.cupon.model.Cupon;
import ecomarket.cupon.repository.CuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CuponService {
    @Autowired
    private CuponRepository cuponRepository;

    public Cupon guardar(Cupon cupon) {
        // Si no viene el estado, el cupón nace activo
        if (cupon.getActivo() == null) {
            cupon.setActivo(true);
        }
        return cuponRepository.save(cupon);
    }

    public List<Cupon> listar() {
        return cuponRepository.findAll();
    }

    public Optional<Cupon> findById(Long id) {
        return cuponRepository.findById(id);
    }

    public Optional<Cupon> buscarPorCodigo(String codigo) {
        return cuponRepository.findByCodigo(codigo);
    }

    public Cupon actualizar(Long id, Cupon datos) {
        return cuponRepository.findById(id).map(c -> {
            c.setCodigo(datos.getCodigo());
            c.setPorcentajeDescuento(datos.getPorcentajeDescuento());
            c.setActivo(datos.getActivo());
            c.setFechaExpiracion(datos.getFechaExpiracion());
            return cuponRepository.save(c);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        cuponRepository.deleteById(id);
    }

    // Valida si un cupón se puede usar: existe, está activo y no está vencido
    public boolean validar(String codigo) {
        return cuponRepository.findByCodigo(codigo)
                .map(c -> Boolean.TRUE.equals(c.getActivo())
                        && (c.getFechaExpiracion() == null
                            || !c.getFechaExpiracion().isBefore(LocalDate.now())))
                .orElse(false);
    }
}