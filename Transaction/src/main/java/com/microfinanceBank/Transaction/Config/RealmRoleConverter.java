package com.microfinanceBank.Transaction.Config;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Keycloak JWT의 realm_access.roles 클레임을 Spring Security GrantedAuthority 목록으로 변환하는 컨버터.
 * 각 역할에 "ROLE_" 접두사를 추가하여 Spring Security 역할 규칙을 준수한다.
 */
public class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    /**
     * JWT 토큰의 realm_access.roles 클레임을 GrantedAuthority 컬렉션으로 변환한다.
     *
     * @param jwt Keycloak 발급 JWT 토큰
     * @return Spring Security 권한 목록
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
        return realmAccess.get("roles")
                .stream()
                .map(roleName -> "ROLE_" + roleName) // prefix required by Spring Security for roles.
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}