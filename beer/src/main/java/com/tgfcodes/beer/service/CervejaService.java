package com.tgfcodes.beer.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tgfcodes.beer.dto.CervejaDTO;
import com.tgfcodes.beer.dto.ValorItensEstoque;
import com.tgfcodes.beer.model.Cerveja;
import com.tgfcodes.beer.repository.CervejaRepository;
import com.tgfcodes.beer.repository.filter.CervejaFilter;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.storage.FotoStorage;

@Service
public class CervejaService {

	@Autowired
	private CervejaRepository cervejaRepository;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	
	@Transactional(readOnly = false)
	public void salvar(Cerveja cerveja) {
		this.cervejaRepository.save(cerveja);
	}
	
	@Transactional(readOnly = false)
	public void excluir(Long codigo) {
		try {
			Cerveja cerveja = this.findOne(codigo).get();
			String foto = cerveja.getFoto();
			this.cervejaRepository.delete(cerveja);
			this.cervejaRepository.flush();
			this.fotoStorage.excluir(foto);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi usada em alguma venda.");
		}
	}
	
	@Transactional(readOnly = true)
	public Optional<Cerveja> findOne(Long codigo) {
		return this.cervejaRepository.findById(codigo);
	}
	
	@Transactional(readOnly = true)
	public Page<Cerveja> pesquisar(CervejaFilter cervejaFilter, Pageable pageable){
		return this.cervejaRepository.filtrar(cervejaFilter, pageable);
	}

	@Transactional(readOnly = true)
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		return this.cervejaRepository.porSkuOuNome(skuOuNome);
	}

	@Transactional(readOnly = true)
	public ValorItensEstoque valorItensEstoque() {
		return this.cervejaRepository.valorItensEstoque();
	}
	
}
