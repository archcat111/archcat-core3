package org.cat.support.id3.generator.service;

import org.cat.support.db3.generator.service.AbsArchBaseMyBatisService;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbsArchMyBatisService extends AbsArchBaseMyBatisService {
	
	@Autowired(required = false)
	private IdGeneratorHolder idGeneratorHolder;

}
