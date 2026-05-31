<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const wins = computed(() => Number(route.query.wins) || 0)
const level = computed(() => Number(route.query.level) || 1)

function playAgain() {
  router.push('/character-select')
}

function backToLogin() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="gameover-page">
    <div class="bg-particles"></div>
    <div class="overlay-glow" :class="wins > 0 ? 'win-glow' : 'lose-glow'"></div>

    <div class="result-card">
      <div class="card-ring"></div>

      <div class="result-icon" :class="wins > 0 ? 'icon-win' : 'icon-lose'">
        {{ wins > 0 ? '🏆' : '💀' }}
      </div>

      <h1 :class="wins > 0 ? 'title-win' : 'title-lose'">
        {{ wins > 0 ? '战斗结束' : '你被击败了' }}
      </h1>

      <div class="stat-trio">
        <div class="stat-block">
          <span class="stat-name">玩家</span>
          <span class="stat-val">{{ userStore.currentUser?.username }}</span>
        </div>
        <div class="stat-block">
          <span class="stat-name">胜场</span>
          <span class="stat-val gold">{{ wins }} 场</span>
        </div>
        <div class="stat-block">
          <span class="stat-name">等级</span>
          <span class="stat-val cyan">Lv.{{ level }}</span>
        </div>
      </div>

      <div class="rating-block" v-if="wins > 0">
        <div class="rating-line"></div>
        <p class="rating-text" v-if="wins >= 12">🏅 传奇战士 · 无人能敌</p>
        <p class="rating-text" v-else-if="wins >= 9">🥇 精英勇士 · 战无不胜</p>
        <p class="rating-text" v-else-if="wins >= 6">🥈 优秀斗士 · 实力强劲</p>
        <p class="rating-text" v-else-if="wins >= 3">🥉 初露锋芒 · 继续加油</p>
        <p class="rating-text" v-else>🔰 刚刚起步 · 再接再厉</p>
        <div class="rating-line"></div>
      </div>

      <div class="btn-group">
        <button class="btn-restart" @click="playAgain">
          <span class="btn-icon">🔄</span>
          <span>再来一局</span>
        </button>
        <button class="btn-exit" @click="backToLogin">
          <span class="btn-icon">🏠</span>
          <span>返回登录</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.gameover-page {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(ellipse at 50% 40%, #0f1a2e 0%, #080c14 70%);
  overflow: hidden;
}

.overlay-glow {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  pointer-events: none;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.08;
}

.win-glow { background: radial-gradient(circle, rgba(46, 204, 113, 0.6), transparent 70%); }
.lose-glow { background: radial-gradient(circle, rgba(231, 76, 60, 0.6), transparent 70%); }

.result-card {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 48px 56px;
  background: rgba(8, 12, 24, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 14px;
  backdrop-filter: blur(20px);
  max-width: 460px;
  width: 100%;
  animation: scaleIn 0.6s ease-out;
}

.card-ring {
  position: absolute;
  inset: -4px;
  border-radius: 16px;
  border: 1px solid rgba(200, 168, 78, 0.08);
  pointer-events: none;
}

.result-icon {
  font-size: 72px;
  margin-bottom: 8px;
  display: inline-block;
}

.icon-win { animation: bounceIcon 1.5s ease-in-out infinite; }
.icon-lose { opacity: 0.6; }

@keyframes bounceIcon {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

.title-win {
  font-size: 32px;
  font-weight: 900;
  color: #fff;
  margin: 0 0 28px;
  letter-spacing: 4px;
}

.title-lose {
  font-size: 32px;
  font-weight: 900;
  color: #e74c3c;
  margin: 0 0 28px;
  letter-spacing: 4px;
}

.stat-trio {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 28px;
}

.stat-block {
  display: flex;
  flex-direction: column;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 8px;
}

.stat-name {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.25);
  letter-spacing: 2px;
  margin-bottom: 6px;
  text-transform: uppercase;
}

.stat-val {
  font-size: 20px;
  color: #e0e0e0;
  font-weight: 800;
}

.stat-val.gold { color: #c8a84e; }
.stat-val.cyan { color: #4facfe; }

.rating-block {
  margin-bottom: 32px;
}

.rating-line {
  width: 100px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(200, 168, 78, 0.3), transparent);
  margin: 0 auto;
}

.rating-text {
  font-size: 19px;
  color: #c8a84e;
  font-weight: 800;
  margin: 16px 0;
  letter-spacing: 1px;
}

.btn-group {
  display: flex;
  gap: 14px;
  justify-content: center;
}

.btn-restart, .btn-exit {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 2px;
  transition: all 0.25s ease;
}

.btn-restart {
  background: rgba(46, 204, 113, 0.1);
  color: #2ecc71;
  border: 1px solid rgba(46, 204, 113, 0.25);
}

.btn-restart:hover {
  background: rgba(46, 204, 113, 0.2);
  border-color: rgba(46, 204, 113, 0.5);
  box-shadow: 0 0 24px rgba(46, 204, 113, 0.15);
}

.btn-exit {
  background: rgba(255, 255, 255, 0.03);
  color: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-exit:hover {
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.8);
  border-color: rgba(255, 255, 255, 0.2);
}

.btn-icon { font-size: 16px; }

@media (max-width: 768px) {
  .result-card { padding: 32px 24px; }
  .result-icon { font-size: 52px; }
  .title-win, .title-lose { font-size: 24px; margin-bottom: 20px; }
  .stat-trio { gap: 8px; }
  .stat-block { padding: 10px 14px; }
  .stat-val { font-size: 17px; }
  .rating-text { font-size: 16px; }
  .btn-group { flex-direction: column; align-items: center; }
  .btn-restart, .btn-exit { width: 200px; justify-content: center; }
}

@media (max-width: 480px) {
  .result-card { padding: 24px 16px; border-radius: 10px; }
  .result-icon { font-size: 40px; }
  .title-win, .title-lose { font-size: 20px; }
  .stat-trio { flex-wrap: wrap; gap: 6px; }
  .stat-block { padding: 8px 10px; }
  .stat-val { font-size: 15px; }
  .rating-text { font-size: 14px; }
}
</style>
