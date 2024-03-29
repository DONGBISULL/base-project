package com.demo.modules.enums;

import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum CustomOAuth2Provider {
    GOOGLE {
        private static final String REDIRECT_URL = "{baseUrl}/login/social/{registrationId}";
//        private static final String REDIRECT_URL = "http://localhost:8080/login/social/google";

        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            return CommonOAuth2Provider.GOOGLE
                    .getBuilder(registrationId)
                    .redirectUri(REDIRECT_URL);
        }

    },
    KAKAO {
        private static final String AUTHORIZATION_URI = "https://kauth.kakao.com/oauth/authorize";
        private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
        private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
        private static final String USER_NAME_ATTRIBUTE_NAME = "id";
        //        private static final String REDIRECT_URL = "http://localhost:8080/login/social/kakao";
        private static final String REDIRECT_URL = "{baseUrl}/login/social/{registrationId}";

        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId,
                    ClientAuthenticationMethod.POST, REDIRECT_URL);
            builder.scope("profile", "account_email");
            builder.authorizationUri(AUTHORIZATION_URI);
            builder.tokenUri(TOKEN_URI);
            builder.userInfoUri(USER_INFO_URI);
            builder.userNameAttributeName(USER_NAME_ATTRIBUTE_NAME);
            builder.clientName("Kakao");
            return builder;
        }
    },
    NAVER {
        public static final String AUTHORIZATON_URI = "https://nid.naver.com/oauth2.0/authorize";
        private static final String TOKEN_URI = "https://nid.naver.com/oauth2.0/token";
        private static final String USER_INFO_URI = "https://openapi.naver.com/v1/nid/me";
        private static final String USER_NAME_ATTRIBUTE_NAME = "response";
        //        private static final String REDIRECT_URL = "http://localhost:8080/login/social/naver";
        private static final String REDIRECT_URL = "{baseUrl}/login/social/{registrationId}";

        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId,
                    ClientAuthenticationMethod.POST, REDIRECT_URL);
            builder.scope("profile");
            builder.authorizationUri(AUTHORIZATON_URI);
            builder.tokenUri(TOKEN_URI);
            builder.userInfoUri(USER_INFO_URI);
            builder.userNameAttributeName(USER_NAME_ATTRIBUTE_NAME);
            builder.clientName("Naver");
            return builder;
        }
    };

    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method,
                                                          String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUri(redirectUri);
        return builder;
    }

    public abstract ClientRegistration.Builder getBuilder(String registrationId);
}
