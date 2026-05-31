<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import type { HeroCharacter } from '../types'

const router = useRouter()
const userStore = useUserStore()

const heroes = ref<HeroCharacter[]>([
  {
    name: '弓箭女皇',
    HP: 725, maxHP: 725,
    attack: 160, defense: 10,
    MB: 200, maxMB: 200,
    wins: 0, level: 1,
    skillsList: [
      { name: '皇室披风', damage: 200, MB: 80, description: '隐身3.6秒，回血150，召5个弓箭手' },
      { name: '巨型箭矢', damage: 500, MB: 100, description: '单发高伤箭，点杀关键防御' }
    ]
  },
  {
    name: '野蛮人之王',
    HP: 1445, maxHP: 1445,
    attack: 102, defense: 20,
    MB: 180, maxMB: 180,
    wins: 0, level: 1,
    skillsList: [
      { name: '铁拳', damage: 150, MB: 80, description: '回血200，增伤提速，召6个野蛮人' },
      { name: '震地金靴', damage: 300, MB: 100, description: '重击地面，伤建筑、小范围震晕' }
    ]
  },
  {
    name: '大守护者',
    HP: 850, maxHP: 850,
    attack: 43, defense: 15,
    MB: 300, maxMB: 300,
    wins: 0, level: 1,
    skillsList: [
      { name: '永恒书卷', damage: 0, MB: 120, description: '范围内友军无敌3秒，自身回血90' },
      { name: '狂暴宝石', damage: 80, MB: 60, description: '光环友军小幅度增伤' }
    ]
  },
  {
    name: '飞盾战神',
    HP: 2508, maxHP: 2508,
    attack: 340, defense: 12,
    MB: 220, maxMB: 220,
    wins: 0, level: 1,
    skillsList: [
      { name: '飞盾寻踪', damage: 1250, MB: 100, description: '掷盾打4个防御，每目标1250伤害' },
      { name: '火箭长矛', damage: 200, MB: 60, description: '前几次攻击附加额外伤害' }
    ]
  },
  {
    name: '亡灵王子',
    HP: 2600, maxHP: 2600,
    attack: 320, defense: 10,
    MB: 250, maxMB: 250,
    wins: 0, level: 1,
    skillsList: [
      { name: '护卫木偶', damage: 150, MB: 70, description: '召2个护卫，隐身1秒，回血200' },
      { name: '暗黑魔球', damage: 250, MB: 90, description: '范围减速防御攻速，小幅增伤' }
    ]
  },
  {
    name: '飞龙公爵',
    HP: 9100, maxHP: 9100,
    attack: 304, defense: 18,
    MB: 220, maxMB: 220,
    wins: 0, level: 1,
    skillsList: [
      { name: '烈焰之心', damage: 200, MB: 70, description: '被动回血，阵亡小范围爆炸' },
      { name: '赤焰风鼓', damage: 350, MB: 90, description: '锥形火焰，12格小范围AOE' }
    ]
  }
])

const gradientColors = [
  'linear-gradient(135deg, #43e97b, #38f9d7)',
  'linear-gradient(135deg, #f7971e, #ffd200)',
  'linear-gradient(135deg, #a18cd1, #fbc2eb)',
  'linear-gradient(135deg, #f5576c, #fd746c)',
  'linear-gradient(135deg, #4facfe, #00f2fe)',
  'linear-gradient(135deg, #fa709a, #fee140)'
]

const selectedHero = ref<number | null>(null)

function selectHero(index: number) {
  selectedHero.value = index
}

function confirmSelection() {
  if (selectedHero.value === null) return
  router.push({
    path: '/battle',
    query: { heroIndex: selectedHero.value }
  })
}

function statPercent(value: number, max: number): string {
  return Math.min((value / max) * 100, 100) + '%'
}
</script>

<template>
  <div class="select-page">
    <div class="bg-particles"></div>
    <div class="header">
      <div class="header-line"></div>
      <h1>选择你的英雄</h1>
      <p class="welcome">勇者 <span class="username">{{ userStore.currentUser?.username }}</span>，请选择一位英雄出战</p>
    </div>

    <div class="hero-grid">
      <div
        v-for="(hero, index) in heroes"
        :key="hero.name"
        class="hero-card"
        :class="{ selected: selectedHero === index }"
        :style="{ animationDelay: (index * 0.08) + 's' }"
        @click="selectHero(index)"
      >
        <div class="card-glow"></div>
        <div class="card-gradient" :style="{ background: gradientColors[index] }">
          <span class="hero-emoji">
            {{ ['👸', '🪓', '🧙', '🛡️', '💀', '🐉'][index] }}
          </span>
          <h3 class="hero-name">{{ hero.name }}</h3>
          <div class="hero-tag">{{ ['射手', '战士', '辅助', '坦克', '刺客', '龙族'][index] }}</div>
        </div>

        <div class="card-body">
          <div class="stat-row">
            <span class="stat-label">HP</span>
            <div class="stat-track">
              <div class="stat-fill hp" :style="{ width: statPercent(hero.HP, 9100) }">
                <div class="stat-shimmer"></div>
              </div>
            </div>
            <span class="stat-num">{{ hero.HP }}</span>
          </div>

          <div class="stat-row">
            <span class="stat-label">ATK</span>
            <div class="stat-track">
              <div class="stat-fill atk" :style="{ width: statPercent(hero.attack, 340) }">
                <div class="stat-shimmer"></div>
              </div>
            </div>
            <span class="stat-num">{{ hero.attack }}</span>
          </div>

          <div class="stat-row">
            <span class="stat-label">DEF</span>
            <div class="stat-track">
              <div class="stat-fill def" :style="{ width: statPercent(hero.defense, 20) }">
                <div class="stat-shimmer"></div>
              </div>
            </div>
            <span class="stat-num">{{ hero.defense }}</span>
          </div>

          <div class="stat-row">
            <span class="stat-label">MP</span>
            <div class="stat-track">
              <div class="stat-fill mp" :style="{ width: statPercent(hero.MB, 300) }">
                <div class="stat-shimmer"></div>
              </div>
            </div>
            <span class="stat-num">{{ hero.MB }}</span>
          </div>

          <div class="skills-section">
            <div
              v-for="skill in hero.skillsList"
              :key="skill.name"
              class="skill-tag"
            >
              <span class="skill-name">{{ skill.name }}</span>
              <span class="skill-info">+{{ skill.damage }} DMG · {{ skill.MB }} MP</span>
            </div>
          </div>
        </div>

        <div class="select-indicator" v-if="selectedHero === index">
          <span>◆ 已选择 ◆</span>
        </div>
      </div>
    </div>

    <div class="bottom-bar">
      <el-button
        type="primary"
        size="large"
        :disabled="selectedHero === null"
        @click="confirmSelection"
      >
        {{ selectedHero !== null ? `确认选择: ${heroes[selectedHero].name}` : '请先选择一位英雄' }}
      </el-button>
      <el-button size="large" @click="router.push('/login')">返回</el-button>
    </div>
  </div>
</template>

<style scoped>
.select-page {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 32px 48px;
  background: radial-gradient(ellipse at 50% 20%, #0f1a2e 0%, #080c14 70%);
  box-sizing: border-box;
  overflow-y: auto;
}

.header {
  position: relative;
  z-index: 1;
  text-align: center;
  margin-bottom: 32px;
  animation: fadeInDown 0.6s ease-out;
}

.header-line {
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255, 200, 60, 0.5), transparent);
  margin: 0 auto 16px;
}

.header h1 {
  font-size: 36px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 4px;
  margin-bottom: 8px;
  text-shadow: 0 0 20px rgba(255, 200, 60, 0.2);
}

.welcome {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 1px;
}

.username {
  color: #c8a84e;
  font-weight: 700;
}

.hero-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 80px;
}

.hero-card {
  position: relative;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.5s ease-out both;
}

.card-glow {
  position: absolute;
  inset: -1px;
  border-radius: 10px;
  opacity: 0;
  background: transparent;
  transition: opacity 0.35s ease;
  z-index: 0;
  pointer-events: none;
}

.hero-card:hover .card-glow {
  opacity: 1;
  box-shadow:
    0 0 0 1px rgba(255, 200, 60, 0.3),
    0 0 30px rgba(255, 200, 60, 0.1);
}

.hero-card:hover {
  transform: translateY(-6px);
  border-color: rgba(255, 255, 255, 0.15);
}

.hero-card.selected {
  border-color: #c8a84e;
  transform: translateY(-6px);
}

.hero-card.selected .card-glow {
  opacity: 1;
  box-shadow:
    0 0 0 2px rgba(200, 168, 78, 0.5),
    0 0 40px rgba(200, 168, 78, 0.2),
    inset 0 0 60px rgba(200, 168, 78, 0.04);
  animation: pulseGlowGold 2s ease-in-out infinite;
}

.card-gradient {
  position: relative;
  z-index: 1;
  padding: 28px 20px 18px;
  text-align: center;
}

.hero-emoji {
  font-size: 48px;
  display: block;
  margin-bottom: 6px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
}

.hero-name {
  font-size: 22px;
  font-weight: 800;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  margin: 0 0 4px;
}

.hero-tag {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 4px;
  text-transform: uppercase;
}

.card-body {
  position: relative;
  z-index: 1;
  padding: 16px 18px 18px;
}

.stat-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.stat-label {
  width: 32px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  font-weight: 700;
  letter-spacing: 1px;
  text-align: right;
  flex-shrink: 0;
}

.stat-track {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
  overflow: hidden;
}

.stat-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s ease;
  position: relative;
  overflow: hidden;
}

.stat-fill.hp { background: linear-gradient(90deg, #e74c3c, #ff5e5e); box-shadow: 0 0 6px rgba(231, 76, 60, 0.4); }
.stat-fill.atk { background: linear-gradient(90deg, #f39c12, #fbb040); box-shadow: 0 0 6px rgba(243, 156, 18, 0.4); }
.stat-fill.def { background: linear-gradient(90deg, #3498db, #5dade2); box-shadow: 0 0 6px rgba(52, 152, 219, 0.4); }
.stat-fill.mp { background: linear-gradient(90deg, #9b59b6, #be90d4); box-shadow: 0 0 6px rgba(155, 89, 182, 0.4); }

.stat-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  background-size: 200% 100%;
  animation: shimmer 2s ease-in-out infinite;
}

.stat-num {
  width: 46px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 700;
  text-align: right;
  font-family: 'Courier New', monospace;
}

.skills-section {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.skill-tag {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-radius: 4px;
  font-size: 12px;
}

.skill-name {
  color: #c8a84e;
  font-weight: 700;
  font-size: 12px;
}

.skill-info {
  color: rgba(255, 255, 255, 0.3);
  font-size: 10px;
  letter-spacing: 0.5px;
}

.select-indicator {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  font-size: 11px;
  color: #c8a84e;
  font-weight: 700;
  letter-spacing: 2px;
  animation: pulseGlowGold 2s ease-in-out infinite;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 14px 48px;
  background: rgba(6, 8, 16, 0.95);
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  display: flex;
  justify-content: center;
  gap: 16px;
  z-index: 10;
}

@media (max-width: 1024px) {
  .hero-grid { grid-template-columns: repeat(2, 1fr); gap: 16px; }
  .select-page { padding: 20px 24px; }
}

@media (max-width: 768px) {
  .select-page { padding: 16px 16px 80px 16px; }
  .header h1 { font-size: 24px; }
  .hero-grid { gap: 12px; padding-bottom: 80px; }
  .hero-emoji { font-size: 36px; }
  .hero-name { font-size: 17px; }
  .card-body { padding: 12px 14px 14px; }
  .card-gradient { padding: 18px 12px 12px; }
  .skill-tag { font-size: 10px; }
  .bottom-bar { padding: 12px 16px; gap: 10px; }
}

@media (max-width: 480px) {
  .hero-grid { grid-template-columns: 1fr; gap: 12px; }
  .header h1 { font-size: 20px; }
  .welcome { font-size: 13px; }
  .hero-card { border-radius: 8px; }
}
</style>
