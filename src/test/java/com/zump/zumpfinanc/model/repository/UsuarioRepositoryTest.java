package com.zump.zumpfinanc.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
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
	public void deveVerificarAExistenciaDeUmEmail() {  // Teste existsByEmail
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario); //repositorty.save(usuario);
		
		// Ação/Execução
		boolean result = repository.existsByEmail(usuario.getEmail());
		
		// Verificação
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() { // Teste not existsByEmail
		// Ação/Execução
		boolean result = repository.existsByEmail("usuario@email.com");
				
		// Verificação
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() { // Teste do Save
		// Cenário
		Usuario usuario = criarUsuario();
		
		// Ação/Execução
		Usuario usuarioSalvo = repository.save(usuario);
		
		// Verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() { // Teste do findByEmail
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// Verificação
		Optional<Usuario> result = repository.findByEmail(usuario.getEmail());
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExistenaBase() { // Teste do findByEmail		
		// Verificação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").senha("usuario@email.com").build();
	}

}
