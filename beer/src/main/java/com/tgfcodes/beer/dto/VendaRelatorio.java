package com.tgfcodes.beer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VendaRelatorio {

	private Long codigo;
	private String nome_vendedor;
	private String nome_cliente;
	private BigDecimal valor_total;
	private LocalDateTime data_criacao;
	
	public VendaRelatorio(Long codigo, String nome_vendedor, String nome_cliente, BigDecimal valor_total, LocalDateTime data_criacao) {
		this.codigo = codigo;
		this.nome_vendedor = nome_vendedor;
		this.nome_cliente = nome_cliente;
		this.valor_total = valor_total;
		this.data_criacao = data_criacao;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome_vendedor() {
		return nome_vendedor;
	}

	public void setNome_vendedor(String nome_vendedor) {
		this.nome_vendedor = nome_vendedor;
	}

	public String getNome_cliente() {
		return nome_cliente;
	}

	public void setNome_cliente(String nome_cliente) {
		this.nome_cliente = nome_cliente;
	}

	public BigDecimal getValor_total() {
		return valor_total;
	}

	public void setValor_total(BigDecimal valor_total) {
		this.valor_total = valor_total;
	}

	public LocalDateTime getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(LocalDateTime data_criacao) {
		this.data_criacao = data_criacao;
	}
	
}
