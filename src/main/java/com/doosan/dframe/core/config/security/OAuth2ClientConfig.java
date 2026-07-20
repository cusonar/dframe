package com.doosan.dframe.core.config.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.function.Function;

/**
 * Azure AD 인증서 기반(private_key_jwt) 인증 설정.
 * <p>
 * client-secret 대신 PKCS12(.p12/.pfx) 키스토어에 저장된 인증서의 개인키로
 * JWT를 서명하여 Azure AD 토큰 엔드포인트에 전달합니다.
 */
@Configuration
public class OAuth2ClientConfig {

    @Value("${azure.certificate.key-store}")
    private Resource keyStoreResource;

    @Value("${azure.certificate.key-store-password}")
    private String keyStorePassword;

    @Value("${azure.certificate.key-alias}")
    private String keyAlias;

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        RestClientAuthorizationCodeTokenResponseClient client =
                new RestClientAuthorizationCodeTokenResponseClient();

        NimbusJwtClientAuthenticationParametersConverter<OAuth2AuthorizationCodeGrantRequest> converter =
                new NimbusJwtClientAuthenticationParametersConverter<>(jwkResolver());

        client.addParametersConverter(converter);
        return client;
    }

    private Function<ClientRegistration, JWK> jwkResolver() {
        return clientRegistration -> {
            if (!"azure".equals(clientRegistration.getRegistrationId())) {
                return null;
            }
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                try (InputStream is = keyStoreResource.getInputStream()) {
                    keyStore.load(is, keyStorePassword.toCharArray());
                }

                RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(
                        keyAlias, keyStorePassword.toCharArray());

                // PKCS12에서는 인증서가 키 항목의 체인에 포함되어 있으므로
                // getCertificateChain()을 우선 사용
                X509Certificate certificate = null;
                java.security.cert.Certificate[] chain = keyStore.getCertificateChain(keyAlias);
                if (chain != null && chain.length > 0) {
                    certificate = (X509Certificate) chain[0];
                } else {
                    certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
                }

                if (certificate == null) {
                    throw new IllegalStateException("키스토어에서 alias '" + keyAlias + "'에 해당하는 인증서를 찾을 수 없습니다.");
                }

                RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();

                // Azure AD가 인증서를 인식할 수 있도록 SHA-1 지문(Thumbprint)을 구해 kid 및 x5t 헤더로 설정하고 x5c 체인을 추가합니다.
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
                byte[] der = certificate.getEncoded();
                byte[] digest = md.digest(der);
                com.nimbusds.jose.util.Base64URL thumbprint = com.nimbusds.jose.util.Base64URL.encode(digest);
                
                java.util.List<com.nimbusds.jose.util.Base64> x5c = java.util.Collections.singletonList(
                        com.nimbusds.jose.util.Base64.encode(der)
                );

                return new RSAKey.Builder(publicKey)
                        .privateKey(privateKey)
                        .keyID(thumbprint.toString())
                        .x509CertThumbprint(thumbprint)
                        .x509CertChain(x5c)
                        .build();
            } catch (Exception e) {
                throw new IllegalStateException("Azure 인증서 키스토어 로드 실패: " + e.getMessage(), e);
            }
        };
    }
}
