<script setup>

// const route = useRoute()

// const router = useRouter();

// const headers = useRequestHeaders(['cookie'])


const {
  data: userInfo,
  error: userError,
  status: userStatus
} = await useCustomFetch(`${API_BASE_URL}/auth/user` ,{method : 'POST'})


// const {public: config} = useRuntimeConfig();
// const {data, pending, error, refresh} = await useCustomFetch(`${config.API_BASE_URL}/auth/access`)
//
// watchEffect(async () => {
//   if (data.value?.accessToken) {
//     const {
//       data: userInfo,
//       error: userError,
//       status: userStatus
//     } = await useCustomFetch(`${config.API_BASE_URL}/auth/user`)
//
//     console.log(userInfo.value)
//     //   fetchUserInfo(data.value.accessToken).then(userInfo => {
//     //     console.log(userInfo)
//     // }).catch(err => {
//     //   console.error('Failed to fetch user info', err)
//     // })
//   }
// })


// const {data, pending, error, refresh} = await useFetch(`${config.API_BASE_URL}/auth/access`, {
//   onRequest({request, options}) {
//     options.query = options.query || {}
//     options.headers = options.headers || {}
//     console.log("onRequest")
//     console.log(request)
//   },
//   onRequestError({request, options, error}) {
//     console.log("onRequestError")
//     console.log(request)
//   },
//   async onResponse({request, response, options}) {
//     // Process the response data
//     console.log("onResponse")
//     console.log(response)
//     console.log(options)
//   },
//   async onResponseError({request, response, options}) {
//     // Handle the response errors
//     console.log("onResponseError")
//     console.log(response)
//     console.log("response.error")
//     console.log(response.error)
//     if (response.status === 401 || response.status === 403) {
//       // 새로운 액세스 토큰을 사용하여 요청을 재시도
//       if (response._data === "INVALID_ACCESS_TOKEN" || response._data === "EXPIRED_ACCESS_TOKEN") {
//         const retryResult = await retryWithRefreshedToken(request, options);
//         console.log('Retry result:', retryResult);
//       }
//     }
//   },
//   method: "POST",
//   headers: {'Content-Type': 'application/json', ...headers},
//   credentials: 'include'
// })
//
// watchEffect(() => {
//   if (data.value?.accessToken) {
//     fetchUserInfo(data.value.accessToken).then(userInfo => {
//       console.log(userInfo)
//     }).catch(err => {
//       console.error('Failed to fetch user info', err)
//     })
//   }
// })
//
//
// async function fetchUserInfo(accessToken) {
//   console.log("========= fetchUserInfo ========== ")
//   const {
//     data: userInfo,
//     error: userError,
//     status: userStatus
//   } = await useAsyncData(() => useFetch(`${config.API_BASE_URL}/auth/user`, {
//     headers: {
//       'Content-Type': 'application/json',
//       'Authorization': `Bearer ${accessToken}`,
//       ...headers
//     },
//     method: 'POST',
//   }));
//
//   if (userError.value) {
//     console.log(userError.value.message);
//   }
//
//   if (userInfo.value) {
//     // 사용자 정보를 화면에 표시 또는 저장 등의 작업 수행
//     console.log('User info:', userInfo.value)
//     return userInfo
//   }
// }
//
// async function retryWithRefreshedToken(request, options) {
//   // 새로운 토큰을 요청
//   const newAccessToken = await fetchRefreshToken();
//
//   if (newAccessToken) {
//     // 헤더에 새로운 액세스 토큰 설정
//     options.headers['Authorization'] = `Bearer ${newAccessToken}`;
//
//     // 요청 재시도
//     return useFetch(request.url, {
//       ...options,
//       method: request.method
//     });
//   } else {
//     console.error('Failed to refresh the token, redirecting to login.');
//     // 토큰 갱신 실패 시 로그인 페이지로 리다이렉트
//     // 로그인 페이지 경로는 프로젝트 설정에 따라 다름
//   }
// }
//
// async function fetchRefreshToken() {
//   console.log(`${config.API_BASE_URL}/auth/reissueToken`)
//   const {
//     data: tokenInfo,
//     error: tokenError,
//     status: tokenStatus
//   } = await useFetch(`${config.API_BASE_URL}/auth/reissueToken`, {
//     headers: {
//       'Content-Type': 'application/json',
//       ...headers,
//     },
//     method: "GET",
//     credentials: 'include'
//   });
//
//   if (tokenError.value) {
//     console.log(tokenError.value.message);
//     return tokenError;
//   }
//
//   if (tokenInfo.value) {
//     // 사용자 정보를 화면에 표시 또는 저장 등의 작업 수행
//     localStorage.setItem('accessToken', tokenInfo.value.accessToken);
//     console.log('User info:', tokenInfo.value)
//     return tokenInfo
//   }
// }

</script>

<template>
  <div>
    로그인 체크
    {{userInfo}}
  </div>
</template>
<style lang="scss" scoped>

</style>