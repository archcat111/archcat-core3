package org.cat.support.security3.generator.spring.validatecode;

public interface IValidateCode {
	
	public void generateCode();
	
	public boolean validate(String calidateCode);
}
