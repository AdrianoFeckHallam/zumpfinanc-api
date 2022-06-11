package com.zump.zumpfinanc.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.zump.zumpfinanc.model.enums.StatusLancamento;
import com.zump.zumpfinanc.model.enums.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // = @Getter, @Setter, @ToString, @EqualsAndHashCode e Contrutores
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lancamento", schema = "financas")
public class Lancamento {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "mes")
	private Integer mes;
	
	@Column(name = "ano")
	private Integer ano;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario") // * Neste caso não teria como omitir pois o nomes da propriedade e da coluna são diferentes
	private Usuario usuario;
	
	@Column(name = "valor")
	private BigDecimal valor;
	
	@Column(name = "data_cadastro") // *
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)// ** Converte LocalDate para a coluna do BD
	private LocalDate dataCadastro;
	
	@Column(name = "tipo")
	@Enumerated(value = EnumType.STRING) // *** Pega o Enum como string e não por ordinal(0,1,2...)
	private TipoLancamento tipo;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING) // ***
	private StatusLancamento status;
	
}
