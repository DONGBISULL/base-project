<script setup>
const route = useRoute()
const {public: config} = useRuntimeConfig();

const router = useRouter();

const headers = useRequestHeaders(['cookie'])

const {data, pending, error, refresh} = await useFetch(`${config.API_BASE_URL}/auth/user`, {
    onRequest({request, options}) {
        options.query = options.query || {}
        options.headers = options.headers || {}
        console.log("onRequest")
        console.log(request)
        // options.headers = options.headers || {}
    },
    onRequestError({request, options, error}) {
        console.log("onRequestError")
        console.log(request)
    },
    onResponse({request, response, options}) {
        // Process the response data
        console.log("onResponse")
        console.log(response)
        console.log(options)
    },
    onResponseError({request, response, options}) {
        // Handle the response errors
        console.log("onResponseError")
        console.log(response)
        console.log("response.error")
        console.log(response.error)
        // if (response.status === 401 || response.status === 403) {
        //     // 새로운 액세스 토큰을 사용하여 요청을 재시도
        //     // const newAccessToken = localStorage.getItem('AccessToken');
        //     if (newAccessToken) {
        //         console.log("재시도 로직")
        //         // 헤더에 새 토큰을 설정
        //         options.headers.Authorization = `Bearer ${newAccessToken}`;
        //         // 요청을 다시 보내는 로직
        //         refresh();
        //     }
        // }
    },
    method: "GET",
    headers: headers,
    credentials: 'include'
})

async function callApiWithAutoRefresh(url, options) {
    const {data : refreshToken, pending: tokenPending, error: tokenError, refresh: tokenRefresh} =  await useFetch(`${config.API_BASE_URL}/auth/reissueToken`, {
        onRequest({request, options}) {
            options.query = options.query || {}
            options.headers = options.headers || {}
            console.log("callApiWithAutoRefresh onRequest")
            console.log(request)
            // options.headers = options.headers || {}
        },
        onRequestError({request, options, error}) {
            console.log("callApiWithAutoRefresh onRequestError")
            console.log(request)
        },
        onResponse({request, response, options}) {
            // Process the response data
            console.log("callApiWithAutoRefresh onResponse")
            console.log(response)
            console.log(options)
        },
        onResponseError({request, response, options}) {
            // Handle the response errors
            console.log("callApiWithAutoRefresh onResponseError")
            console.log(response)
            console.log(response.error)

        },
        method: "POST",
        headers: headers,
        credentials: 'include'
    });
}


</script>

<template>
    <div>
        로그인 체크
    </div>
</template>
<style lang="scss" scoped>

</style>