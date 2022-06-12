package com.zump.zumpfinanc.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zump.zumpfinanc.model.entity.Lancamento;
import com.zump.zumpfinanc.model.enums.StatusLancamento;
import com.zump.zumpfinanc.model.enums.TipoLancamento;

@DataJpaTest // Faz com que cada método de teste seja isolado dos outros, fazendo com que antes de executar o teste o banco seja limpo, entao vc pode criar seu cenario sem influenciar nos outros testes.
@ActiveProfiles("test") // Pega o application-test.properties
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	private void deveSalvarUmLancamento() {
		// Cenário
		Lancamento lancamento = criarLancamento();
				
		// Ação/Execução
		lancamento = repository.save(lancamento);
				
		// Verificação
		assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	private void deveDeletarUmLancamento() {
		// Cenário
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		// Ação/Execução
		repository.delete(lancamento);
		
		// Verificação
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull();
	}
	
	@Test
	private void deveAtualizarUmLancamento() {
		// Cenário
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		// Ação/Execução
		lancamento.setAno(2021);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		// Verificação
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2021);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		
	}
	
	@Test
	private void deveBuscarUmLancamentoPorId() {
		// Cenário
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		// Ação/Execução
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		// Verificação
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
		
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder().ano(2022).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(10)).tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();
	}
	
	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
}
