package com.tgfcodes.beer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.tgfcodes.beer.model.Grupo;

public class GrupoConverter implements Converter<String, Grupo> {

	@Override
	public Grupo convert(String codigo) {
		if(!StringUtils.isEmpty(codigo)){
			Grupo grupo = new Grupo();
			grupo.setCodigo(Long.parseLong(codigo));
			return grupo;
		}
		return null;
	}

}
