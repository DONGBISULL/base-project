<script setup>

const route = useRoute()
const {public: config} = useRuntimeConfig();

const router = useRouter();

const headers = useRequestHeaders(['cookie'])


const {data, pending, error, refresh} = await useFetch(`${config.API_BASE_URL}/auth/access`, {
    onRequest({request, options}) {
        options.query = options.query || {}
        options.headers = options.headers || {}
        console.log("onRequest")
        console.log(request)
    },
    onRequestError({request, options, error}) {
        console.log("onRequestError")
        console.log(request)
    },
    async onResponse({request, response, options}) {
        // Process the response data
        console.log("onResponse")
        console.log(response)
        console.log(options)
        if (response.status === 200) {
            /* 토큰 발급된 경우 */
            console.log("여기 탐 ")
            const accessToken = response._data?.accessToken;
            localStorage.setItem('accessToken', accessToken);
            const userInfo = await fetchUserInfo(accessToken)
            console.log("==========userInfo=================")
            console.log(userInfo)
        }
    },
    async onResponseError({request, response, options}) {
        // Handle the response errors
        console.log("onResponseError")
        console.log(response)
        console.log("response.error")
        console.log(response.error)
        if (response.status === 401 || response.status === 403) {
            // 새로운 액세스 토큰을 사용하여 요청을 재시도
            if (response._data === "INVALID_ACCESS_TOKEN" || response._data === "EXPIRED_ACCESS_TOKEN") {
                await fetchRefreshToken();
            }
        }
    },
    method: "POST",
    headers: {'Content-Type': 'application/json', ...headers},
    credentials: 'include'
})


async function fetchUserInfo(accessToken) {
    console.log("========= fetchUserInfo ========== ")
    const {
        data: userInfo,
        error: userError,
        status: userStatus
    } = await useAsyncData(() => $fetch(`${config.API_BASE_URL}/auth/user`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
            ...headers
        },
        method: 'POST',
    }));

    if (userError.value) {
        console.log(userError.value.message);
    }

    if (userInfo.value) {
        // 사용자 정보를 화면에 표시 또는 저장 등의 작업 수행
        console.log('User info:', userInfo.value)
        return userInfo
    }
}

async function fetchRefreshToken() {
    console.log(`${config.API_BASE_URL}/auth/reissueToken`)
    const accessTokenData = await $fetch(`${config.API_BASE_URL}/auth/reissueToken`, {
        headers: {
            'Content-Type': 'application/json',
            ...headers
        },
    })
        .catch((error) => error.data)

    return accessTokenData;
    // const {data: accessTokenData, error: refreshError} = await useFetch(`${config.API_BASE_URL}/auth/reissueToken`, {
    //     method: 'GET',
    //     credentials: 'include',
    //     headers: {
    //         'Content-Type': 'application/json',
    //         ...headers
    //     },
    // })
    // console.log(accessTokenData.value)
}

</script>

<template>
    <div>
        로그인 체크
    </div>
</template>
<style lang="scss" scoped>

</style>