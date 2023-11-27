package org.cat.support.springboot3.starter.storage.minio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.storage3.generator.minio.MinioClientHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

@Configuration
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(prefix = "cat.support3.storage.minio", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({MinioProperties.class})
public class MinioAutoConfiguration {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "Minio初始化：";
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private MinioProperties minioProperties;
	
	@Bean
	public MinioClientHolder minioClientHolder() {
		MinioClientHolder minioClientHolder = new MinioClientHolder();
		
		Map<String, MinioAccountProperties> accountsProperties = minioProperties.getAccounts();
		
		for (Entry<String, MinioAccountProperties> entry : accountsProperties.entrySet()) {
			String accountName = entry.getKey();
			MinioAccountProperties minioAccountProperties = entry.getValue();
			
			String url = minioAccountProperties.getUrl();
			String accessKey = minioAccountProperties.getAccessKey();
			String secretKey = minioAccountProperties.getSecretKey();
			
			MinioClient minioClient = MinioClient.builder()
					.endpoint(url)
					.credentials(accessKey, secretKey)
					.build();
			
			//验证账户下的bucket是否有效
			List<String> validateBuckets = minioAccountProperties.getValidateBuckets();
			validateBuckets(accountName, minioClient, validateBuckets);
			
			ArchSpringBeanUtil.registerBean(applicationContext, accountName, minioClient);
			this.coreLogger.info(this.logPrefix+"已完成：将MinioClient["+accountName+"]注入到Spring容器中");
			minioClientHolder.put(accountName, minioClient);
		}
		
		return minioClientHolder;
	}
	
	private void validateBuckets(String accountName, MinioClient minioClient, List<String> validateBuckets) {
		validateBuckets.forEach(bucketName -> {
			BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
			
			boolean avatarBucketExists = false;
			try {
				avatarBucketExists = minioClient.bucketExists(bucketExistsArgs);
			} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
					| InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
					| IOException e) {
				throw new IllegalArgumentException(this.logPrefix + "Account["+accountName+"]验证Bucket["+bucketName+"]失败，排除异常如下：", e);
			}
			if(!avatarBucketExists) {
				throw new IllegalArgumentException(this.logPrefix + "Account["+accountName+"]验证Bucket["+bucketName+"]失败，该bucketName不存在");
			}else {
				coreLogger.info(logPrefix+"Account["+accountName+"]验证bucket["+bucketName+"]成功");
			}
		});
	}

}
