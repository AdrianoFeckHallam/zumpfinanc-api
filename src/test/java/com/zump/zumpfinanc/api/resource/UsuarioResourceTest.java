package com.zump.zumpfinanc.api.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zump.zumpfinanc.api.dto.UsuarioDTO;
import com.zump.zumpfinanc.exception.ErroAutenticacao;
import com.zump.zumpfinanc.exception.RegraNegocioException;
import com.zump.zumpfinanc.model.entity.Usuario;
import com.zump.zumpfinanc.service.LancamentoService;
import com.zump.zumpfinanc.service.UsuarioService;

@AutoConfigureMockMvc
@ActiveProfiles("test") // Pega o application-test.properties
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UsuarioResource.class)
public class UsuarioResourceTest {
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		// Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Ação/Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON).contentType(JSON).content(json);
		
		// Verificação
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome())).andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		// Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Ação/Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON).contentType(JSON).content(json);
		
		// Verificação
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		// Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Ação/Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON).content(json);
		
		// Verificação
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome())).andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
		// Cenário
		UsuarioDTO dto = UsuarioDTO.builder().email("usuario@email.com").senha("123").build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Ação/Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON).content(json);
		
		// Verificação
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	
	}

}
