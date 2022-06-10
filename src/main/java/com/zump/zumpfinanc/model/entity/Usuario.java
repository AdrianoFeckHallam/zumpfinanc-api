package com.zump.zumpfinanc.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // = @Getter, @Setter, @ToString, @EqualsAndHashCode.
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario", schema = "financas")
public class Usuario {

	@Id
	@Column(name = "id") // * Pode omitir a annotation pois o nome da propriedade é o mesmo da coluna da base de dados
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome") // *
	private String nome;
	
	@Column(name = "email") // *
	private String email;
	
	@Column(name = "senha") // *
	private String senha;
	
}
