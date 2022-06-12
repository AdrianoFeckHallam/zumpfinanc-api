package com.zump.zumpfinanc.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zump.zumpfinanc.exception.RegraNegocioException;
import com.zump.zumpfinanc.model.entity.Lancamento;
import com.zump.zumpfinanc.model.entity.Usuario;
import com.zump.zumpfinanc.model.enums.StatusLancamento;
import com.zump.zumpfinanc.model.repository.LancamentoRepository;
import com.zump.zumpfinanc.model.repository.LancamentoRepositoryTest;
import com.zump.zumpfinanc.service.impl.LancamentoServiceImpl;

@ActiveProfiles("test") // Pega o application-test.properties
@ExtendWith(SpringExtension.class)
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean // Simular comportamento do repository
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		// Ação/Execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		// Verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(lancamentoSalvo.getStatus());
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		// Ação/Execução
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		// Verificação
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		// Cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		// Ação/Execução
		service.atualizar(lancamentoSalvo);
		
		// Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		// Ação/Execução
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		
		// Verificação
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		// Ação/Execução
		service.deletar(lancamento);
		
		// Verificação
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
				
		// Ação/Execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
				
		// Verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		// Ação/Execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		// Verificação
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento); // Para não chamar o método de verdade
		
		// Ação/Execução
		service.atualizarStatus(lancamento, novoStatus);
		
		// Verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		// Cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		// Ação/Execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		// Verificação
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		// Cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		// Ação/Execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		// Verificação
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		// Cenário
		Lancamento lancamento = new Lancamento();
		
		// Ação/Execução
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		// Ação/Execução
		lancamento.setDescricao("");
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
				
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		// Ação/Execução
		lancamento.setDescricao("Salario");
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		// Ação/Execução
		lancamento.setMes(0);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
				
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
				
		// Ação/Execução
		lancamento.setMes(13);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
				
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		// Ação/Execução
		lancamento.setMes(1);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		// Ação/Execução
		lancamento.setAno(999);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
				
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		// Ação/Execução
		lancamento.setAno(2022);;
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
				
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		// Ação/Execução
		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1l);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
						
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		// Ação/Execução
		lancamento.setValor(BigDecimal.ZERO);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
								
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		// Ação/Execução
		lancamento.setValor(BigDecimal.valueOf(7000));
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
						
		// Verificação
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lancamento.");
	}
	
}
