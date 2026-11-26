<template>
  <div class="login">
    <section class="login-brand">
      <div class="hex-bg" aria-hidden="true">
        <span v-for="n in 12" :key="n" :style="hexStyle(n)"></span>
      </div>
      <div class="brand-inner">
        <div class="brand-row">
          <svg viewBox="0 0 32 32" width="34" height="34" aria-hidden="true">
            <path d="M16 2 29 9.5v13L16 30 3 22.5v-13z" fill="rgba(255,255,255,.16)" />
            <path d="M16 7.5 24 12v8l-8 4.5L8 20v-8z" fill="#8FD3AE" />
          </svg>
          <div>
            <h1>综合能源管控基础平台集中部署服务项目</h1>
            <p>Green Factory Energy &amp; Carbon Management Platform</p>
          </div>
        </div>

        <ul class="brand-points">
          <li><b>能耗采集</b>电 / 水 / 油 / 气多源数据，20 秒刷新</li>
          <li><b>碳排核算</b>依据 GB/T 2589、GB/T 32150 标准口径</li>
          <li><b>碳资产管理</b>配额测算、行情监控与履约预警</li>
        </ul>

        <div class="brand-foot">
          <span>v1.0</span>
        </div>
      </div>
    </section>

    <section class="login-form">
      <div class="form-card">
        <h2>账号登录</h2>
        <p class="sub">请使用平台分配的账号进入能碳管理中心</p>

        <form @submit.prevent="submit">
          <label class="field">
            <span class="field-label">用户名</span>
            <input v-model="form.username" class="input" type="text" autocomplete="username" placeholder="请输入用户名" />
          </label>

          <label class="field">
            <span class="field-label">密码</span>
            <input v-model="form.password" class="input" type="password" autocomplete="current-password" placeholder="请输入密码" />
          </label>

          <label class="field">
            <span class="field-label">验证码</span>
            <div class="captcha-row">
              <input v-model="form.captcha" class="input" type="text" maxlength="4" placeholder="请输入右侧验证码" />
              <button type="button" class="captcha" :title="'点击刷新验证码'" @click="newCaptcha">
                <span v-for="(ch, i) in captcha" :key="i" :style="{ transform: `rotate(${(i % 2 ? 8 : -7)}deg)` }">{{ ch }}</span>
              </button>
            </div>
          </label>

          <p v-if="error" class="error">{{ error }}</p>

          <button class="btn btn-primary btn-block login-btn" type="submit" :disabled="loading">
            {{ loading ? '登录中…' : '登 录' }}
          </button>
        </form>

        <div class="demo-tip">
          <span class="tag">演示账号</span>
          <code>admin / 123456</code>
        </div>
      </div>

      <p class="copyright">© 2025 综合能源管控基础平台集中部署服务项目</p>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api'

const router = useRouter()
const route = useRoute()

const form = reactive({ username: 'admin', password: '123456', captcha: '' })
const captcha = ref('')
const loading = ref(false)
const error = ref('')

function newCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXY3456789'
  captcha.value = Array.from({ length: 4 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
}
newCaptcha()

function hexStyle(n) {
  const row = Math.floor((n - 1) / 4)
  const col = (n - 1) % 4
  return {
    left: `${col * 22 + (row % 2 ? 11 : 0) - 6}%`,
    top: `${row * 15 + 4}%`,
    opacity: 0.06 + ((n * 7) % 5) * 0.012
  }
}

async function submit() {
  error.value = ''
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码'
    return
  }
  if (form.captcha.toUpperCase() !== captcha.value.toUpperCase()) {
    error.value = '验证码不正确'
    newCaptcha()
    form.captcha = ''
    return
  }
  loading.value = true
  try {
    const user = await login({ ...form })
    localStorage.setItem('gfecmp_token', user.token)
    localStorage.setItem('gfecmp_user', JSON.stringify(user))
    router.replace(route.query.redirect || '/home')
  } catch (e) {
    error.value = e.message || '登录失败'
    newCaptcha()
    form.captcha = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 0.95fr);
  background: var(--bg);
}

.login-brand {
  position: relative;
  overflow: hidden;
  background: linear-gradient(150deg, #17513a 0%, #123f2e 55%, #0d3124 100%);
  color: #fff;
  padding: 56px 56px 44px;
  display: flex;
  align-items: center;
}

.hex-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hex-bg span {
  position: absolute;
  width: 150px;
  height: 132px;
  background: #7fd0a4;
  clip-path: polygon(25% 4%, 75% 4%, 100% 50%, 75% 96%, 25% 96%, 0 50%);
}

.brand-inner {
  position: relative;
  z-index: 1;
  max-width: 460px;
}

.brand-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-row h1 {
  font-size: 25px;
  letter-spacing: 1px;
}

.brand-row p {
  margin-top: 5px;
  font-size: 11px;
  letter-spacing: 0.5px;
  color: rgba(255, 255, 255, 0.55);
}

.brand-points {
  list-style: none;
  padding: 0;
  margin: 40px 0 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.brand-points li {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
  padding-left: 16px;
  position: relative;
  line-height: 1.7;
}

.brand-points li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 9px;
  width: 6px;
  height: 6px;
  border-radius: 2px;
  background: var(--amber);
}

.brand-points b {
  display: block;
  color: #fff;
  font-size: 14px;
  margin-bottom: 2px;
}

.brand-foot {
  margin-top: 52px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.42);
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  padding-top: 16px;
}

.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 24px;
}

.form-card {
  width: 100%;
  max-width: 372px;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: 18px;
  box-shadow: var(--shadow);
  padding: 30px 28px 26px;
}

.form-card h2 {
  font-size: 20px;
  letter-spacing: 0.4px;
}

.form-card .sub {
  margin: 8px 0 22px;
  font-size: 12.5px;
  color: var(--muted);
}

form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 104px;
  gap: 8px;
}

.captcha {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  height: 34px;
  border: 1px dashed var(--gf-200);
  border-radius: 8px;
  background: var(--gf-50);
  color: var(--gf-700);
  font-family: var(--mono);
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 3px;
  cursor: pointer;
  user-select: none;
}

.captcha span {
  display: inline-block;
}

.error {
  font-size: 12px;
  color: var(--red);
  background: var(--red-soft);
  border-radius: 6px;
  padding: 7px 10px;
}

.login-btn {
  height: 40px;
  margin-top: 4px;
  font-size: 14px;
  letter-spacing: 2px;
}

.demo-tip {
  margin-top: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--muted);
}

.demo-tip code {
  font-family: var(--mono);
  background: var(--gf-50);
  color: var(--gf-700);
  padding: 3px 8px;
  border-radius: 6px;
}

.copyright {
  margin-top: 26px;
  font-size: 11px;
  color: var(--muted);
}

@media (max-width: 1023px) {
  .login {
    grid-template-columns: minmax(0, 1fr);
  }
  .login-brand {
    display: none;
  }
  .login-form {
    padding: 40px 18px;
    background:
      radial-gradient(circle at 50% 0%, rgba(47, 158, 99, 0.14), transparent 55%),
      var(--bg);
  }
}
</style>
