<template>
  <div class="login-page">
    <section class="login-brand">
      <div class="brand-top">
        <div class="brand-logo">Z</div>
        <div>
          <strong>智汇 HRMS</strong>
          <span>人力资源管理平台</span>
        </div>
      </div>

      <div class="brand-main">
        <span class="brand-eyebrow">SMART PEOPLE OPERATIONS</span>
        <h1>让每一次人才决策<br><em>更清晰、更高效</em></h1>
        <p>从员工档案到人事异动，把分散的 HR 信息收进一个干净、可靠的工作台。</p>
      </div>

      <div class="feature-list">
        <div class="feature-item">
          <div class="feature-icon"><el-icon><UserFilled/></el-icon></div>
          <div>
            <strong>员工档案</strong>
            <span>一处维护基础信息、岗位与合同</span>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon"><el-icon><Document/></el-icon></div>
          <div>
            <strong>人事记录</strong>
            <span>奖惩、培训、调动、调薪全程留痕</span>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon"><el-icon><TrendCharts/></el-icon></div>
          <div>
            <strong>数据看板</strong>
            <span>快速掌握团队结构与人员动态</span>
          </div>
        </div>
      </div>

      <div class="brand-footer">ZhiHui Human Resource Management System</div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <div class="login-heading">
          <span class="eyebrow">WELCOME BACK</span>
          <h2>登录工作台</h2>
          <p>请输入账号和密码，继续管理你的团队。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" size="large" placeholder="请输入账号" :prefix-icon="User"
                      autocomplete="username" @keyup.enter="loginHandle"/>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" size="large" type="password" show-password
                      placeholder="请输入密码" :prefix-icon="Lock" autocomplete="current-password"
                      @keyup.enter="loginHandle"/>
          </el-form-item>
        </el-form>

        <el-button class="login-button" type="primary" size="large" :loading="loading" @click="loginHandle">
          进入工作台
          <el-icon class="button-arrow"><ArrowRight/></el-icon>
        </el-button>

        <div class="demo-hint">
          <span>演示账号</span>
          <b>admin</b>
          <span class="slash">/</span>
          <b>123</b>
        </div>
      </div>

      <p class="copyright">© 2026 智汇 HRMS · 软件工程实践课程项目</p>
    </section>
  </div>
</template>

<script setup>
import {reactive, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {ArrowRight, Document, Lock, TrendCharts, User, UserFilled} from '@element-plus/icons-vue';
import {login} from '@/api/login.js';

const router = useRouter();
const route = useRoute();
const formRef = ref();
const loading = ref(false);

const form = reactive({
  username: 'admin',
  password: '123'
});

const rules = {
  username: [{required: true, message: '请输入账号', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}]
};

function loginHandle() {
  if (loading.value) return;
  formRef.value.validate(valid => {
    if (!valid) return;
    loading.value = true;
    login(form).then(res => {
      window.sessionStorage.setItem('hr', JSON.stringify(res.data));
      const redirect = route.query.redirect;
      router.replace(redirect ? redirect : '/home');
    }).catch(() => {
      // 错误提示由 request 拦截器统一处理
    }).finally(() => {
      loading.value = false;
    });
  });
}
</script>

<style scoped>
.login-page {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(420px, 0.85fr);
  width: 100%;
  min-height: 100vh;
  background: #f7f9fd;
}

.login-brand {
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 42px 58px;
  overflow: hidden;
  color: #fff;
  background:
      radial-gradient(circle at 14% 20%, rgba(79, 110, 247, 0.38), transparent 32%),
      radial-gradient(circle at 88% 78%, rgba(14, 165, 233, 0.2), transparent 32%),
      linear-gradient(145deg, #101a30 0%, #0f172a 60%, #0b1324 100%);
}

.login-brand::after {
  position: absolute;
  right: -110px;
  bottom: -160px;
  width: 430px;
  height: 430px;
  border: 1px solid rgba(255, 255, 255, 0.09);
  border-radius: 50%;
  box-shadow: 0 0 0 60px rgba(255, 255, 255, 0.025), 0 0 0 120px rgba(255, 255, 255, 0.018);
  content: "";
}

.brand-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 13px;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #5b7cfa, #3e59d4);
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 13px;
  box-shadow: 0 10px 24px rgba(79, 110, 247, 0.35);
  font-size: 23px;
  font-weight: 800;
  letter-spacing: -0.06em;
}

.brand-top strong {
  display: block;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.brand-top span {
  display: block;
  margin-top: 3px;
  color: #8e9bb5;
  font-size: 10px;
  letter-spacing: 0.08em;
}

.brand-main {
  position: relative;
  z-index: 1;
  max-width: 570px;
  margin: auto 0;
  padding: 55px 0 40px;
}

.brand-eyebrow {
  color: #8fa6ff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
}

.brand-main h1 {
  margin: 18px 0 20px;
  color: #f8faff;
  font-size: clamp(34px, 4vw, 54px);
  font-weight: 700;
  line-height: 1.28;
  letter-spacing: -0.05em;
}

.brand-main h1 em {
  color: #91a8ff;
  font-style: normal;
}

.brand-main p {
  max-width: 470px;
  margin: 0;
  color: #9aa8c2;
  font-size: 15px;
  line-height: 1.9;
}

.feature-list {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  max-width: 690px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 12px;
}

.feature-icon {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  color: #b9c7ff;
  background: rgba(79, 110, 247, 0.2);
  border-radius: 8px;
}

.feature-item strong {
  display: block;
  color: #e9eeff;
  font-size: 12px;
  font-weight: 650;
}

.feature-item span {
  display: block;
  margin-top: 4px;
  color: #7f8da8;
  font-size: 10px;
  line-height: 1.5;
}

.brand-footer {
  position: relative;
  z-index: 1;
  margin-top: auto;
  padding-top: 30px;
  color: #53627c;
  font-size: 10px;
  letter-spacing: 0.08em;
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px 40px 32px;
  background: #fff;
  border: 1px solid #edf0f7;
  border-radius: 20px;
  box-shadow: 0 24px 70px rgba(23, 32, 51, 0.08);
}

.login-heading {
  margin-bottom: 26px;
}

.eyebrow {
  color: #4f6ef7;
  font-size: 10px;
  font-weight: 750;
  letter-spacing: 0.16em;
}

.login-heading h2 {
  margin: 10px 0 8px;
  color: #172033;
  font-size: 27px;
  font-weight: 750;
  letter-spacing: -0.04em;
}

.login-heading p {
  margin: 0;
  color: #929caf;
  font-size: 12px;
}

.login-card :deep(.el-form-item__label) {
  padding-bottom: 7px;
  color: #5b6679;
  font-size: 12px;
  font-weight: 600;
}

.login-card :deep(.el-input__wrapper) {
  height: 46px;
  border-radius: 10px;
  background: #fafbff;
}

.login-button {
  width: 100%;
  height: 46px;
  margin-top: 4px;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.button-arrow {
  margin-left: 7px;
}

.demo-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  margin-top: 22px;
  padding: 10px;
  color: #8d98aa;
  background: #f8faff;
  border: 1px dashed #e0e6f3;
  border-radius: 9px;
  font-size: 11px;
}

.demo-hint b {
  color: #4c5b74;
  font-weight: 650;
}

.slash {
  color: #c3cad8;
}

.copyright {
  margin: 26px 0 0;
  color: #aeb7c6;
  font-size: 10px;
}

@media (max-width: 1000px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-brand {
    min-height: auto;
    padding: 28px 26px 34px;
  }

  .brand-main {
    padding: 40px 0 28px;
  }

  .feature-list {
    display: none;
  }

  .login-panel {
    padding: 30px 18px;
  }
}
</style>
