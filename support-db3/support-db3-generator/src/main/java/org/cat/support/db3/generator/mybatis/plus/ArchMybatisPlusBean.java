package org.cat.support.db3.generator.mybatis.plus;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.cat.core.web3.constants.ArchVersion;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchMybatisPlusBean implements Serializable {

	private static final long serialVersionUID = ArchVersion.V3;
	
	/**
	 * 1：已删除
	 * 0：未删除（默认）
	 */
    @TableLogic
    private Integer del;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTimestamp;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdateTimestamp;

}
