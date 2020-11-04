package com.tgfcodes.beer.relatorio;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil<E> implements Serializable{

	private static final long serialVersionUID = 1L;
	@Autowired
	private ResourceLoader resourceLoader;
	
	public byte[] gerarRelatorio(List<E> dados, String relatorio) throws Exception{
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(dados);
		Resource resourceLocal = (Resource) resourceLoader.getResource("classpath:/relatorio/" + relatorio + ".jasper");
		String caminhoJasper =  resourceLocal.getURI().getPath();
		return JasperRunManager.runReportToPdf(caminhoJasper, new HashMap<String, Object>(), beanCollectionDataSource);
	}
	
}