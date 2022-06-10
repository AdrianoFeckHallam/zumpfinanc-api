package com.zump.zumpfinanc.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zump.zumpfinanc.exception.ErroAutenticacao;
import com.zump.zumpfinanc.exception.RegraNegocioException;
import com.zump.zumpfinanc.model.entity.Usuario;
import com.zump.zumpfinanc.model.repository.UsuarioRepository;
import com.zump.zumpfinanc.service.impl.UsuarioServiceImpl;

@ActiveProfiles("test") // Pega o application-test.properties
@ExtendWith(SpringExtension.class)
public class UsuarioServiceTest { // Testes Unitários
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() { // Teste do salvarUsuario, com sucesso
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			
			Usuario usuario = criarUsuario();
			
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
			
			// Ação/Execução
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
			
			// Verificação
			Assertions.assertThat(usuarioSalvo).isNotNull();
			Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
			Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("usuario");
			Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
			Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		});
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
			// Cenário
			Usuario usuario = criarUsuario();
			
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());
			
			// Ação/Execução
			service.salvarUsuario(usuario);
		
			// Verificação
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() { // Teste do autenticar, com sucesso
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Usuario usuario = criarUsuario();
			
			Mockito.when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
			
			// Ação/Execução
			Usuario result = service.autenticar(usuario.getEmail(), usuario.getSenha());
			
			// Verificação
			Assertions.assertThat(result).isNotNull();
		});
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() { // Teste do autenticar, com erro de não encontrar usuário
		// Cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
		// Ação/Execução
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("usuario@email.com", "senha"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado.");

	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() { // Teste do autenticar, com erro de senha inválida
		// Cenário
		Usuario usuario = criarUsuario();
			
		Mockito.when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
			
		// Ação/Execução
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar(usuario.getEmail(), "1234"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
	}
	
	@Test
	public void deveValidarEmail() { // Teste do validarEmail, com sucesso
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

			// Ação/Execução
			service.validarEmail("usuario@email.com");
		});
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() { // Teste do validarEmail, com erro de usuário cadastrado.
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
			// Cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
			
			// Ação/Execução
			service.validarEmail("usuario@email.com");
		});
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").id(1l).build();
	}

}
