package org.cat.support.springboot3.starter.actuator.props;

import org.cat.support.springboot3.actuator.ActuatorConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorInfo2Properties {
	private boolean enabled = true;
	//info2接口返回的是系统默认的info信息还是arch框架扩展的info信息
	private String activation = ActuatorConstants.Info.ARCH; 
}
