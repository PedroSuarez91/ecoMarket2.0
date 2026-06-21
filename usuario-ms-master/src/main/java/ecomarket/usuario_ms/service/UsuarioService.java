package ecomarket.usuario_ms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecomarket.usuario_ms.model.Usuario;
import ecomarket.usuario_ms.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario findByEmailUsuario(String emailUsuario) {
        Usuario buscado = usuarioRepository.findByEmailUsuario(emailUsuario).orElse(null);
        if (buscado == null)
            return null;

        Usuario dto = new Usuario();
        dto.setIdUsuario(buscado.getIdUsuario());
        dto.setNombre(buscado.getNombre());
        dto.setApellido(buscado.getApellido());
        dto.setEmailUsuario(buscado.getEmailUsuario());
        dto.setPassword(buscado.getPassword());
        dto.setRol(buscado.getRol());

        return dto;
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: "));

        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setEmailUsuario(usuario.getEmailUsuario());
        existente.setPassword(usuario.getPassword());
        existente.setRol(usuario.getRol());

        return usuarioRepository.save(existente);
    }

}
