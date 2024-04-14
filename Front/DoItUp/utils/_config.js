const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';
const KAKAO_LOGIN_PREFIX = process.env.KAKAO_LOGIN_PREFIX || '/oauth2/authorization/kakao';
const GOOGLE_LOGIN_PREFIX = process.env.GOOGLE_LOGIN_PREFIX || '/oauth2/authorization/google';

export {
    API_BASE_URL,
    KAKAO_LOGIN_PREFIX,
    GOOGLE_LOGIN_PREFIX
};