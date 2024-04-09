<script setup>
const { public: config } = useRuntimeConfig();
const kakaoKey = process.env.KAKAO_API_SECRET_KEY;
const googleKey = process.env.GOOGLE_API_SECRET_KEY;

async function socialLogin(type) {
    const {data, pending, error, refresh} = await useFetch(`${config.API_BASE_URL}/oauth2/authorization/${type}`, {
        onRequest({request, options}) {
            // Set the request headers
            console.log("onRequest")
            options.headers = options.headers || {}
        },
        onRequestError({request, options, error}) {
            // Handle the request errors
            console.log("onRequestError")
            console.log(request)
        },
        onResponse({request, response, options}) {
            // Process the response data
            console.log("onResponse")
            console.log(response)
            console.log(options)
            // localStorage.setItem('token', response._data.token)
        },
        onResponseError({request, response, options}) {
            // Handle the response errors
            console.log("onResponseError")
            console.log(response)
        }
    })
}
</script>

<template>
    <div>
<!--        <button @click="socialLogin('kakao')">-->
<!--            카카오-->
<!--        </button>-->
<!--        <br>-->
<!--        <a :href="config.public?.API_BASE_URL+config.public?.KAKAO_LOGIN_PREFIX+'?redirect_uri=http://localhost:3000/oauth/redirected/kakao'">-->
<!--            스프링에서 카카오 로그인-->
<!--        </a>-->


                <a class="p-2"
                   :href="config?.API_BASE_URL+config?.KAKAO_LOGIN_PREFIX+'?redirect_uri=http://localhost:8080/login/kakao'"
                >
                    카카오로그인
                </a>
                <br>
                <a class="p-2"
                   :href="config?.API_BASE_URL+config?.GOOGLE_LOGIN_PREFIX+'?redirect_uri=http://localhost:8080/login/google'"
                >
                    구글 로그인
                </a>
    </div>
</template>
<style lang="scss" scoped>
</style>
