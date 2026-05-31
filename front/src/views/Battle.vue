<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import type { HeroCharacter, EnemyCharacter } from '../types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const hero = reactive<HeroCharacter>({
  name: '', HP: 0, maxHP: 0,
  attack: 0, defense: 0,
  MB: 0, maxMB: 0,
  wins: 0, level: 1,
  skillsList: []
})

const enemy = reactive<EnemyCharacter>({
  name: '', HP: 0, maxHP: 0,
  attack: 0, defense: 0,
  MB: 0, maxMB: 0,
  wins: 0, level: 1,
  skillsList: [],
  defending: false
})

const round = ref(1)
const battleCount = ref(1)
const isPlayerTurn = ref(true)
const isBattling = ref(false)
const battleLog = ref<string[]>([])
const waitingForInput = ref(false)
const battleEnded = ref(false)
const playerWon = ref(false)

const heroData: HeroCharacter[] = [
  {
    name: '弓箭女皇', HP: 725, maxHP: 725, attack: 160, defense: 10, MB: 200, maxMB: 200, wins: 0, level: 1,
    skillsList: [
      { name: '皇室披风', damage: 200, MB: 80, description: '隐身3.6秒，回血150' },
      { name: '巨型箭矢', damage: 500, MB: 100, description: '单发高伤箭，点杀关键防御' }
    ]
  },
  {
    name: '野蛮人之王', HP: 1445, maxHP: 1445, attack: 102, defense: 20, MB: 180, maxMB: 180, wins: 0, level: 1,
    skillsList: [
      { name: '铁拳', damage: 150, MB: 80, description: '回血200，增伤提速' },
      { name: '震地金靴', damage: 300, MB: 100, description: '重击地面，小范围震晕' }
    ]
  },
  {
    name: '大守护者', HP: 850, maxHP: 850, attack: 43, defense: 15, MB: 300, maxMB: 300, wins: 0, level: 1,
    skillsList: [
      { name: '永恒书卷', damage: 0, MB: 120, description: '友军无敌3秒，回血90' },
      { name: '狂暴宝石', damage: 80, MB: 60, description: '光环友军增伤' }
    ]
  },
  {
    name: '飞盾战神', HP: 2508, maxHP: 2508, attack: 340, defense: 12, MB: 220, maxMB: 220, wins: 0, level: 1,
    skillsList: [
      { name: '飞盾寻踪', damage: 1250, MB: 100, description: '掷盾打4个防御' },
      { name: '火箭长矛', damage: 200, MB: 60, description: '附加额外伤害' }
    ]
  },
  {
    name: '亡灵王子', HP: 2600, maxHP: 2600, attack: 320, defense: 10, MB: 250, maxMB: 250, wins: 0, level: 1,
    skillsList: [
      { name: '护卫木偶', damage: 150, MB: 70, description: '召2个护卫，回血200' },
      { name: '暗黑魔球', damage: 250, MB: 90, description: '范围减速，小幅增伤' }
    ]
  },
  {
    name: '飞龙公爵', HP: 9100, maxHP: 9100, attack: 304, defense: 18, MB: 220, maxMB: 220, wins: 0, level: 1,
    skillsList: [
      { name: '烈焰之心', damage: 200, MB: 70, description: '被动回血，小范围爆炸' },
      { name: '赤焰风鼓', damage: 350, MB: 90, description: '锥形火焰，AOE' }
    ]
  }
]

const enemyTemplates = [
  { name: '哥布林', HP: 200, attack: 80, defense: 20, MB: 60, skills: [{ name: '偷袭', damage: 50, MB: 20, description: '快速偷袭' }] },
  { name: '骷髅战士', HP: 450, attack: 100, defense: 40, MB: 90, skills: [{ name: '骨盾', damage: 0, MB: 40, description: '进入防御' }, { name: '亡者斩击', damage: 120, MB: 50, description: '奋力一击' }] },
  { name: '暗黑骑士', HP: 700, attack: 130, defense: 60, MB: 110, skills: [{ name: '暗影冲锋', damage: 100, MB: 50, description: '暗影冲锋' }, { name: '黑暗波动', damage: 180, MB: 80, description: '释放黑暗波动' }] },
  { name: '石像鬼', HP: 350, attack: 90, defense: 30, MB: 80, skills: [{ name: '石化凝视', damage: 80, MB: 40, description: '石化凝视' }, { name: '俯冲攻击', damage: 140, MB: 60, description: '俯冲攻击' }] },
  { name: '巨型蜘蛛', HP: 550, attack: 110, defense: 40, MB: 100, skills: [{ name: '毒液喷射', damage: 100, MB: 40, description: '喷射毒液' }, { name: '蛛网束缚', damage: 80, MB: 50, description: '蛛网束缚' }] },
  { name: '火龙', HP: 900, attack: 160, defense: 70, MB: 140, skills: [{ name: '火焰吐息', damage: 180, MB: 60, description: '喷吐火焰' }, { name: '龙之怒', damage: 250, MB: 90, description: '龙之怒' }] }
]

let difficultyLevel = 0

function log(msg: string) {
  battleLog.value.push(msg)
}

function calcDamage(attacker: { attack: number }, skillBonus: number, defender: { defense: number }): number {
  const dmg = attacker.attack + skillBonus - defender.defense
  return Math.max(dmg, 1)
}

function renderBar(current: number, max: number, len: number = 20): string {
  const filled = max > 0 ? Math.round((current / max) * len) : 0
  return '█'.repeat(filled) + '░'.repeat(len - filled)
}

const heroBar = computed(() => renderBar(hero.HP, hero.maxHP))
const heroMbBar = computed(() => renderBar(hero.MB, hero.maxMB))
const enemyBar = computed(() => renderBar(enemy.HP, enemy.maxHP))
const enemyMbBar = computed(() => renderBar(enemy.MB, enemy.maxMB))

function generateEnemy() {
  const tpl = enemyTemplates[Math.floor(Math.random() * enemyTemplates.length)]
  enemy.name = tpl.name
  enemy.HP = tpl.HP
  enemy.maxHP = tpl.HP
  enemy.attack = tpl.attack
  enemy.defense = tpl.defense
  enemy.MB = tpl.MB
  enemy.maxMB = tpl.MB
  enemy.skillsList = [...tpl.skills.map(s => ({ ...s }))]
  enemy.defending = false

  if (difficultyLevel > 0) {
    const scale = 1 + difficultyLevel * 0.1
    enemy.HP = Math.floor(enemy.HP * scale)
    enemy.maxHP = enemy.HP
    enemy.attack = Math.floor(enemy.attack * scale)
    enemy.defense = Math.floor(enemy.defense * scale)
  }
  enemy.maxMB = enemy.MB
}

function startBattle() {
  isBattling.value = true
  battleEnded.value = false
  playerWon.value = false
  round.value = 1
  isPlayerTurn.value = true
  battleLog.value = []
  generateEnemy()

  log(`⚔️ 第 ${battleCount.value} 场战斗开始！对手: ${enemy.name}`)
  log(`---------------------------------------`)
  log(`⚔️ 第 ${round.value} 回合开始！`)
  showStatus()
  waitingForInput.value = true
}

function showStatus() {
  log(`${hero.name}: [${heroBar.value}] ${hero.HP}/${hero.maxHP} HP`)
  log(`${hero.name}: [${heroMbBar.value}] ${hero.MB}/${hero.maxMB} MB`)
  log(`${enemy.name}: [${enemyBar.value}] ${enemy.HP}/${enemy.maxHP} HP`)
  log(`${enemy.name}: [${enemyMbBar.value}] ${enemy.MB}/${enemy.maxMB} MB`)
}

function playerAction(action: 'normal' | 'skill1' | 'skill2') {
  if (!waitingForInput.value || !isBattling.value || battleEnded.value) return
  waitingForInput.value = false

  const skill1 = hero.skillsList[0]
  const skill2 = hero.skillsList[1]

  if (action === 'normal') {
    const dmg = calcDamage(hero, 0, enemy)
    enemy.HP = Math.max(0, enemy.HP - dmg)
    log(`🔹 ${hero.name} 普通攻击，造成 ${dmg} 点伤害！`)
  } else if (action === 'skill1') {
    if (hero.MB < skill1.MB) {
      log(`⚠️ 蓝量不足，无法使用【${skill1.name}】！`)
      log(`自动执行普通攻击`)
      const dmg = calcDamage(hero, 0, enemy)
      enemy.HP = Math.max(0, enemy.HP - dmg)
      log(`🔹 ${hero.name} 普通攻击，造成 ${dmg} 点伤害！`)
    } else {
      hero.MB -= skill1.MB
      const dmg = calcDamage(hero, skill1.damage, enemy)
      enemy.HP = Math.max(0, enemy.HP - dmg)
      log(`💥 ${hero.name} 使用【${skill1.name}】，造成 ${dmg} 点伤害！(蓝耗${skill1.MB})`)
    }
  } else if (action === 'skill2') {
    if (hero.MB < skill2.MB) {
      log(`⚠️ 蓝量不足，无法使用【${skill2.name}】！`)
      log(`自动执行普通攻击`)
      const dmg = calcDamage(hero, 0, enemy)
      enemy.HP = Math.max(0, enemy.HP - dmg)
      log(`🔹 ${hero.name} 普通攻击，造成 ${dmg} 点伤害！`)
    } else {
      hero.MB -= skill2.MB
      const dmg = calcDamage(hero, skill2.damage, enemy)
      enemy.HP = Math.max(0, enemy.HP - dmg)
      log(`💥 ${hero.name} 使用【${skill2.name}】，造成 ${dmg} 点伤害！(蓝耗${skill2.MB})`)
    }
  }

  if (enemy.HP <= 0) {
    endBattle(true)
    return
  }

  isPlayerTurn.value = false
  setTimeout(enemyAction, 800)
}

function enemyAction() {
  if (battleEnded.value) return

  const skillCount = enemy.skillsList.length
  const action = Math.floor(Math.random() * (1 + skillCount))

  if (action === 0) {
    const dmg = calcDamage(enemy, 0, hero)
    hero.HP = Math.max(0, hero.HP - dmg)
    log(`🔸 ${enemy.name} 普通攻击，造成 ${dmg} 点伤害！`)
  } else if (action === 1 && skillCount >= 1) {
    const s = enemy.skillsList[0]
    if (enemy.MB >= s.MB) {
      enemy.MB -= s.MB
      const dmg = calcDamage(enemy, s.damage, hero)
      hero.HP = Math.max(0, hero.HP - dmg)
      log(`💢 ${enemy.name} 使用【${s.name}】，造成 ${dmg} 点伤害！`)
    } else {
      const dmg = calcDamage(enemy, 0, hero)
      hero.HP = Math.max(0, hero.HP - dmg)
      log(`🔸 ${enemy.name} 普通攻击，造成 ${dmg} 点伤害！`)
    }
  } else if (action === 2 && skillCount >= 2) {
    const s = enemy.skillsList[1]
    if (enemy.MB >= s.MB) {
      enemy.MB -= s.MB
      const dmg = calcDamage(enemy, s.damage, hero)
      hero.HP = Math.max(0, hero.HP - dmg)
      log(`💢 ${enemy.name} 使用【${s.name}】，造成 ${dmg} 点伤害！`)
    } else {
      const dmg = calcDamage(enemy, 0, hero)
      hero.HP = Math.max(0, hero.HP - dmg)
      log(`🔸 ${enemy.name} 普通攻击，造成 ${dmg} 点伤害！`)
    }
  } else {
    const dmg = calcDamage(enemy, 0, hero)
    hero.HP = Math.max(0, hero.HP - dmg)
    log(`🔸 ${enemy.name} 普通攻击，造成 ${dmg} 点伤害！`)
  }

  if (hero.HP <= 0) {
    endBattle(false)
    return
  }

  round.value++
  isPlayerTurn.value = true
  log(`---------------------------------------`)
  log(`⚔️ 第 ${round.value} 回合开始！`)
  showStatus()
  waitingForInput.value = true
}

function endBattle(won: boolean) {
  battleEnded.value = true
  isBattling.value = false
  playerWon.value = won

  if (won) {
    log(`🎉 战斗胜利！${hero.name} 击败了 ${enemy.name}！`)
    const recoverHP = 20 + Math.floor(Math.random() * 21)
    const recoverMB = Math.floor(hero.maxMB * 0.3)
    hero.HP = Math.min(hero.HP + recoverHP, hero.maxHP)
    hero.MB = Math.min(hero.MB + recoverMB, hero.maxMB)
    log(`💚 战斗结束！恢复了 ${recoverHP} 点生命值和 ${recoverMB} 点蓝量`)
    hero.wins++

    if (hero.wins % 3 === 0) {
      hero.maxHP += 30
      hero.attack += 5
      hero.defense += 3
      hero.level++
      log(`🌟 属性提升！当前等级: ${hero.level}`)
      log(`   生命+30  攻击+5  防御+3`)
    }

    difficultyLevel++
    log(`⚠ 敌人势力增强了！当前难度等级: ${difficultyLevel}`)
    log(`🏆 当前胜场: ${hero.wins}`)
  } else {
    log(`💀 ${hero.name} 倒下了...`)
    log(`游戏结束，最终胜场: ${hero.wins}，等级: ${hero.level}`)
    ElMessage.error('你被击败了！')
    setTimeout(() => {
      router.push({ path: '/game-over', query: { wins: String(hero.wins), level: String(hero.level) } })
    }, 1500)
  }
}

function continueBattle() {
  battleCount.value++
  hero.HP = Math.min(hero.HP + 100, hero.maxHP)
  hero.MB = Math.min(hero.MB + 50, hero.maxMB)
  log(`💚 每场战斗后自动恢复 100 HP + 50 MB`)
  startBattle()
}

function quitToMain() {
  router.push({
    path: '/game-over',
    query: { wins: String(hero.wins), level: String(hero.level) }
  })
}

onMounted(() => {
  if (!userStore.currentUser) {
    router.push('/login')
    return
  }
  const heroIndex = Number(route.query.heroIndex) || 0
  const data = heroData[heroIndex]
  Object.assign(hero, {
    ...data,
    skillsList: data.skillsList.map(s => ({ ...s }))
  })
  startBattle()
})
</script>

<template>
  <div class="battle-page">
    <div class="bg-particles"></div>
    <div class="top-bar">
      <div class="top-hero">
        <span class="top-icon">🦸</span>
        <span class="top-name">{{ hero.name }}</span>
        <span class="top-level">Lv.{{ hero.level }}</span>
      </div>
      <div class="top-center">
        <span class="battle-round">⚔ 第 {{ battleCount }} 场</span>
        <span class="battle-wins">🏆 {{ hero.wins }} 胜</span>
      </div>
      <div class="top-enemy">
        <span class="top-icon">👹</span>
        <span class="top-name enemy">{{ enemy.name }}</span>
      </div>
    </div>

    <div class="battle-area">
      <div class="fighter hero-side">
        <div class="fighter-avatar">🦸</div>
        <div class="fighter-panel">
          <div class="fighter-name">{{ hero.name }}</div>
          <div class="bar-row">
            <span class="bar-label">HP</span>
            <div class="bar-track">
              <div class="bar-fill hp" :style="{ width: (hero.maxHP > 0 ? (hero.HP / hero.maxHP * 100) : 0) + '%' }">
                <div class="bar-shimmer"></div>
              </div>
            </div>
            <span class="bar-num">{{ hero.HP }}/{{ hero.maxHP }}</span>
          </div>
          <div class="bar-row">
            <span class="bar-label">MP</span>
            <div class="bar-track">
              <div class="bar-fill mp" :style="{ width: (hero.maxMB > 0 ? (hero.MB / hero.maxMB * 100) : 0) + '%' }">
                <div class="bar-shimmer"></div>
              </div>
            </div>
            <span class="bar-num">{{ hero.MB }}/{{ hero.maxMB }}</span>
          </div>
        </div>
      </div>

      <div class="vs-zone">
        <div class="vs-ring">
          <span class="vs-text">VS</span>
        </div>
      </div>

      <div class="fighter enemy-side">
        <div class="fighter-avatar">👹</div>
        <div class="fighter-panel">
          <div class="fighter-name enemy">{{ enemy.name }}</div>
          <div class="bar-row">
            <span class="bar-label">HP</span>
            <div class="bar-track">
              <div class="bar-fill enemy-hp" :style="{ width: (enemy.maxHP > 0 ? (enemy.HP / enemy.maxHP * 100) : 0) + '%' }">
                <div class="bar-shimmer"></div>
              </div>
            </div>
            <span class="bar-num">{{ enemy.HP }}/{{ enemy.maxHP }}</span>
          </div>
          <div class="bar-row">
            <span class="bar-label">MP</span>
            <div class="bar-track">
              <div class="bar-fill enemy-mp" :style="{ width: (enemy.maxMB > 0 ? (enemy.MB / enemy.maxMB * 100) : 0) + '%' }">
                <div class="bar-shimmer"></div>
              </div>
            </div>
            <span class="bar-num">{{ enemy.MB }}/{{ enemy.maxMB }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="action-panel" v-if="waitingForInput && !battleEnded">
      <div class="action-header">
        <span class="action-glow-left"></span>
        <span class="action-title">你的回合</span>
        <span class="action-glow-right"></span>
      </div>
      <div class="btn-group">
        <button class="skill-btn btn-normal" @click="playerAction('normal')">
          <span class="skill-icon">⚔️</span>
          <span class="skill-label">普通攻击</span>
        </button>
        <button
          class="skill-btn btn-skill1"
          @click="playerAction('skill1')"
          :disabled="hero.MB < hero.skillsList[0]?.MB"
        >
          <span class="skill-icon">💥</span>
          <span class="skill-label">{{ hero.skillsList[0]?.name }}</span>
          <span class="skill-cost">MP {{ hero.skillsList[0]?.MB }}</span>
        </button>
        <button
          class="skill-btn btn-skill2"
          @click="playerAction('skill2')"
          :disabled="hero.MB < hero.skillsList[1]?.MB"
        >
          <span class="skill-icon">🔥</span>
          <span class="skill-label">{{ hero.skillsList[1]?.name }}</span>
          <span class="skill-cost">MP {{ hero.skillsList[1]?.MB }}</span>
        </button>
      </div>
    </div>

    <div class="result-panel win-panel" v-if="battleEnded && playerWon">
      <div class="result-glow green"></div>
      <div class="result-icon">🏆</div>
      <h2>战斗胜利</h2>
      <p>{{ hero.name }} 击败了 {{ enemy.name }}！</p>
      <div class="btn-group">
        <el-button type="primary" size="large" @click="continueBattle">继续下一场</el-button>
        <el-button size="large" @click="quitToMain">结束游戏</el-button>
      </div>
    </div>

    <div class="result-panel lose-panel" v-if="battleEnded && !playerWon">
      <div class="result-glow red"></div>
      <div class="result-icon">💀</div>
      <h2>你被击败了</h2>
      <p>即将跳转到结算页面...</p>
    </div>

    <div class="log-panel">
      <div class="log-header">
        <span class="log-dot live"></span>
        <span>战斗日志</span>
      </div>
      <div class="log-content" ref="logContainer">
        <div v-for="(msg, i) in battleLog" :key="i" class="log-line" :style="{ animationDelay: '0s' }">{{ msg }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.battle-page {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 14px 24px;
  background: radial-gradient(ellipse at 50% 40%, #0f1a2e 0%, #080c14 70%);
  color: #e0e0e0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.top-bar {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  margin-bottom: 16px;
}

.top-hero, .top-enemy {
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-icon { font-size: 20px; }

.top-name {
  font-size: 16px;
  font-weight: 800;
  color: #4facfe;
}

.top-name.enemy { color: #f5576c; }

.top-level {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  padding: 2px 8px;
  background: rgba(79, 172, 254, 0.1);
  border-radius: 10px;
}

.top-center {
  display: flex;
  gap: 16px;
}

.battle-round {
  font-size: 15px;
  color: #c8a84e;
  font-weight: 700;
}

.battle-wins {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
}

.battle-area {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  margin-bottom: 16px;
  padding: 24px 20px;
  background: rgba(255, 255, 255, 0.015);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.fighter {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  max-width: 380px;
}

.fighter-avatar {
  font-size: 56px;
  flex-shrink: 0;
  filter: drop-shadow(0 0 12px rgba(0, 0, 0, 0.5));
}

.fighter-panel {
  flex: 1;
  min-width: 0;
}

.fighter-name {
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 10px;
}

.hero-side .fighter-name { color: #4facfe; }
.enemy-side .fighter-name { color: #f5576c; }

.vs-zone {
  flex-shrink: 0;
  padding: 0 24px;
}

.vs-ring {
  width: 60px;
  height: 60px;
  border: 2px solid rgba(200, 168, 78, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulseGlowGold 2s ease-in-out infinite;
}

.vs-text {
  font-size: 22px;
  font-weight: 900;
  color: #c8a84e;
  text-shadow: 0 0 16px rgba(200, 168, 78, 0.4);
}

.bar-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.bar-label {
  width: 24px;
  font-size: 10px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.25);
  letter-spacing: 1px;
}

.bar-track {
  flex: 1;
  height: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.4s ease;
  position: relative;
  overflow: hidden;
}

.bar-fill.hp { background: linear-gradient(90deg, #e74c3c, #ff5e5e); box-shadow: 0 0 8px rgba(231, 76, 60, 0.3); }
.bar-fill.mp { background: linear-gradient(90deg, #8e44ad, #be90d4); box-shadow: 0 0 8px rgba(142, 68, 173, 0.3); }
.bar-fill.enemy-hp { background: linear-gradient(90deg, #c0392b, #ff5e5e); box-shadow: 0 0 8px rgba(192, 57, 43, 0.3); }
.bar-fill.enemy-mp { background: linear-gradient(90deg, #7d3c98, #be90d4); box-shadow: 0 0 8px rgba(125, 60, 152, 0.3); }

.bar-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
  background-size: 200% 100%;
  animation: shimmer 2.5s ease-in-out infinite;
}

.bar-num {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.4);
  width: 72px;
  text-align: right;
  font-family: 'Courier New', monospace;
  font-weight: 700;
}

.action-panel {
  position: relative;
  z-index: 1;
  text-align: center;
  margin-bottom: 16px;
  padding: 18px 24px;
  background: rgba(200, 168, 78, 0.03);
  border: 1px solid rgba(200, 168, 78, 0.15);
  border-radius: 10px;
  animation: pulseGlowGold 3s ease-in-out infinite;
}

.action-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 14px;
}

.action-glow-left, .action-glow-right {
  width: 40px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(200, 168, 78, 0.4));
}
.action-glow-right {
  background: linear-gradient(90deg, rgba(200, 168, 78, 0.4), transparent);
}

.action-title {
  font-size: 18px;
  color: #c8a84e;
  font-weight: 800;
  letter-spacing: 4px;
}

.btn-group {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.skill-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.03);
  color: #e0e0e0;
  transition: all 0.25s ease;
  min-width: 130px;
}

.skill-btn:hover:not(:disabled) { transform: translateY(-2px); }
.skill-btn:disabled { opacity: 0.35; cursor: not-allowed; }

.btn-normal:hover:not(:disabled) {
  background: rgba(80, 180, 255, 0.1);
  border-color: rgba(80, 180, 255, 0.4);
  box-shadow: 0 0 20px rgba(80, 180, 255, 0.12);
}

.btn-skill1:hover:not(:disabled) {
  background: rgba(255, 180, 40, 0.1);
  border-color: rgba(255, 180, 40, 0.4);
  box-shadow: 0 0 20px rgba(255, 180, 40, 0.12);
}

.btn-skill2:hover:not(:disabled) {
  background: rgba(255, 80, 60, 0.1);
  border-color: rgba(255, 80, 60, 0.4);
  box-shadow: 0 0 20px rgba(255, 80, 60, 0.12);
}

.skill-icon { font-size: 20px; }
.skill-label { font-size: 14px; font-weight: 700; }
.skill-cost { font-size: 10px; color: rgba(255, 255, 255, 0.35); }

.result-panel {
  position: relative;
  z-index: 1;
  text-align: center;
  margin-bottom: 16px;
  padding: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.result-glow {
  position: absolute;
  inset: 0;
  opacity: 0.3;
  pointer-events: none;
}
.result-glow.green { background: radial-gradient(ellipse at 50% 0%, rgba(46, 204, 113, 0.3), transparent 70%); }
.result-glow.red { background: radial-gradient(ellipse at 50% 0%, rgba(231, 76, 60, 0.3), transparent 70%); }

.win-panel {
  background: rgba(46, 204, 113, 0.06);
  border: 1px solid rgba(46, 204, 113, 0.15);
}
.win-panel h2 { color: #2ecc71; font-size: 22px; margin: 8px 0 4px; animation: victoriousGlow 2s ease-in-out infinite; }
.win-panel p { color: rgba(255, 255, 255, 0.4); font-size: 14px; margin-bottom: 16px; }

.lose-panel {
  background: rgba(231, 76, 60, 0.06);
  border: 1px solid rgba(231, 76, 60, 0.15);
}
.lose-panel h2 { color: #e74c3c; font-size: 22px; margin: 8px 0 4px; }
.lose-panel p { color: rgba(255, 255, 255, 0.3); font-size: 14px; }

.result-icon { font-size: 44px; }

.log-panel {
  position: relative;
  z-index: 1;
  flex: 1;
  background: rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  min-height: 140px;
  max-height: 300px;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
  font-weight: 700;
  letter-spacing: 2px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}

.log-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}
.log-dot.live { background: #2ecc71; box-shadow: 0 0 6px rgba(46, 204, 113, 0.5); }

.log-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px 16px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.7;
}

.log-line {
  color: rgba(255, 255, 255, 0.45);
  white-space: pre-wrap;
  padding: 2px 0;
}

@media (max-width: 768px) {
  .battle-page { padding: 8px 10px; }
  .top-bar { padding: 8px 12px; }
  .top-name { font-size: 13px; }
  .battle-area { flex-direction: column; gap: 14px; padding: 14px; }
  .fighter { max-width: 100%; }
  .fighter-avatar { font-size: 40px; }
  .fighter-name { font-size: 15px; }
  .vs-ring { width: 44px; height: 44px; }
  .vs-text { font-size: 16px; }
  .bar-track { height: 8px; }
  .bar-num { font-size: 9px; width: 56px; }
  .action-panel { padding: 12px 14px; }
  .action-title { font-size: 15px; }
  .skill-btn { padding: 10px 14px; min-width: 100px; }
  .skill-label { font-size: 12px; }
  .log-panel { min-height: 100px; max-height: 180px; }
  .log-line { font-size: 10px; }
}

@media (max-width: 480px) {
  .top-bar { font-size: 11px; }
  .top-name { font-size: 12px; }
  .battle-area { padding: 10px; }
  .fighter-avatar { font-size: 32px; }
  .vs-ring { width: 36px; height: 36px; }
  .btn-group { flex-direction: column; }
  .skill-btn { width: 100%; }
}
</style>
