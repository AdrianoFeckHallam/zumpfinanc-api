package com.zump.zumpfinanc.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zump.zumpfinanc.model.entity.Usuario;

@DataJpaTest // Faz com que cada método de teste seja isolado dos outros, fazendo com que antes de executar o teste o banco seja limpo, entao vc pode criar seu cenario sem influenciar nos outros testes.
@ActiveProfiles("test") // Pega o application-test.properties
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest { // Teste de Integração - Teste que utiliza BD
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	private void deveVerificarAExistenciaDeUmEmail() {  // Teste existsByEmail
		// Cenário
		Usuario usuario = criarEPersistirUmUsuario();
		
		// Ação/Execução
		boolean result = repository.existsByEmail(usuario.getEmail());
		
		// Verificação
		assertThat(result).isTrue();
	}
	
	@Test
	private void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() { // Teste not existsByEmail
		// Ação/Execução
		boolean result = repository.existsByEmail("usuario@email.com");
				
		// Verificação
		assertThat(result).isFalse();
	}
	
	@Test
	private void devePersistirUmUsuarioNaBaseDeDados() { // Teste do Save
		// Cenário
		Usuario usuario = criarUsuario();
		
		// Ação/Execução
		Usuario usuarioSalvo = repository.save(usuario);
		
		// Verificação
		assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	private void deveBuscarUmUsuarioPorEmail() { // Teste do findByEmail
		// Cenário
		Usuario usuario = criarEPersistirUmUsuario();
		
		// Verificação
		Optional<Usuario> result = repository.findByEmail(usuario.getEmail());
		
		assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	private void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExistenaBase() { // Teste do findByEmail		
		// Verificação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		assertThat(result.isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").senha("usuario@email.com").build();
	}
	
	private Usuario criarEPersistirUmUsuario() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		return usuario;
	}

}
