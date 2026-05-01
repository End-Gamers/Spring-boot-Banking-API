package com.microfinanceBank.Employee.service;

import com.microfinanceBank.Employee.Config.KeycloakConfiguration;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.keycloak.admin.client.KeycloakBuilder.builder;


/**
 * Keycloak Admin Client 인스턴스를 제공하는 싱글턴 클래스.
 * Client Credentials 방식으로 Keycloak 서버에 연결한다.
 */
@Configuration
@Slf4j
@AllArgsConstructor
public class KeycloakProvider {

    private final KeycloakConfiguration keycloakData;

    private static Keycloak keycloak = null;

    /**
     * Keycloak 인스턴스를 반환한다. 최초 호출 시 인스턴스를 생성한다.
     * @return Keycloak Admin Client 인스턴스
     */
    public Keycloak getInstance() {
        if (keycloak == null) {
            return
                    builder()
                            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                            .serverUrl(keycloakData.getServerURL())
                            .realm(keycloakData.getRealm())
                            .clientId("idm-client")
                            .clientSecret(keycloakData.getClientSecret())
                            .build();
        }
        return keycloak;
    }

//    public JsonNode refreshToken(String refreshToken) throws UnirestException {
//        String url = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";
//        return Unirest.post(url)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .field("client_id", clientID)
////                .field("client_secret", clientSecret)
//                .field("refresh_token", refreshToken)
//                .field("grant_type", "refresh_token")
//                .asJson().getBody();
//    }
}