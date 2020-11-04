package com.tgfcodes.beer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.dto.VendaMes;
import com.tgfcodes.beer.dto.VendaOrigem;
import com.tgfcodes.beer.model.Venda;
import com.tgfcodes.beer.model.enumeration.StatusVenda;
import com.tgfcodes.beer.repository.VendaRepository;
import com.tgfcodes.beer.repository.filter.VendaFilter;

@Service
public class VendaService {
	
	@Autowired
	private VendaRepository vendaRepository;
	
	@Transactional(readOnly = false)
	public Venda salvar(Venda venda) {
		if (venda.isSalvarProibido()) {
			throw new RuntimeException("Esta venda não pode ser modificada.");
		}
		if (venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		}else {
			Venda vendaExistente = this.findOne(venda.getCodigo()).get();
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
		if (venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
		}
		return this.vendaRepository.saveAndFlush(venda);
	}
	
	@Transactional(readOnly = false)
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		this.salvar(venda);
	}

	@Transactional(readOnly = true)
	public Page<Venda> filtrar(VendaFilter vendaFilter, Pageable pageable) {
		return this.vendaRepository.filtrar(vendaFilter, pageable);
	}
	
	@Transactional(readOnly = true)
	public Optional<Venda> findOne(Long codigo){
		return this.vendaRepository.findById(codigo);
	}
	
	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')")
	@Transactional(readOnly = false)
	public void cancelar(Venda venda) {
		Venda vendaExistente = this.findOne(venda.getCodigo()).get();
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		this.vendaRepository.save(vendaExistente);
	}

	@Transactional(readOnly = true)
	public BigDecimal valorTotalNoAno() {
		return this.vendaRepository.valorTotalNoAno();
	}

	@Transactional(readOnly = true)
	public BigDecimal valorTotalNoMes() {
		return this.vendaRepository.valorTotalNoMes();
	}

	@Transactional(readOnly = true)
	public BigDecimal valorTicketMedioNoAno() {
		return this.vendaRepository.valorTicketMedioNoAno();
	}

	@Transactional(readOnly = true)
	public List<VendaMes> totalPorMes() {
		return this.vendaRepository.totalPorMes();
	}

	@Transactional(readOnly = true)
	public List<VendaOrigem> totalPorOrigem() {
		return this.vendaRepository.totalPorOrigem();
	}
}
