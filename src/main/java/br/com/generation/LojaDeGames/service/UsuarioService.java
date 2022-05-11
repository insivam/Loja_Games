package br.com.generation.LojaDeGames.service;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.generation.LojaDeGames.model.Usuario;
import br.com.generation.LojaDeGames.model.UsuarioLogin;
import br.com.generation.LojaDeGames.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();

		if (calculaIdade(usuario.getDataNascimento()) < 18)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proibido menores de 18 anos", null);

		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		return Optional.ofNullable(usuarioRepository.save(usuario));
	}

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		Optional<Usuario> buscarUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

		if (usuarioRepository.findById(usuario.getId()).isPresent()) {

			if (calculaIdade(usuario.getDataNascimento()) < 18)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proibido menores de 18 anos", null);

			if (buscarUsuario.isPresent() && usuario.getId() != buscarUsuario.get().getId())
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já em uso", null);

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuario));

		}

		return Optional.empty();

	}

	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		Optional<Usuario> buscarUsuarioL = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

		if (buscarUsuarioL.isPresent()) {

			if (compararSenhas(usuarioLogin.get().getSenha(), buscarUsuarioL.get().getSenha())) {

				usuarioLogin.get().setId(buscarUsuarioL.get().getId());
				usuarioLogin.get().setNome(buscarUsuarioL.get().getNome());
				usuarioLogin.get().setFoto(buscarUsuarioL.get().getFoto());
				usuarioLogin.get().setDataNascimento(buscarUsuarioL.get().getDataNascimento());
				usuarioLogin.get()
						.setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(buscarUsuarioL.get().getSenha());

				return usuarioLogin;
			}

		}

		return Optional.empty();

	}

	private int calculaIdade(LocalDate dataNascimento) {
		
		return Period.between(dataNascimento, LocalDate.now()).getYears();
	}

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(senha);
	}

	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.matches(senhaDigitada, senhaBanco);
	}

	private String gerarBasicToken(String usuario, String senha) {

		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));

		return "Basic " + new String(tokenBase64);

	}

}