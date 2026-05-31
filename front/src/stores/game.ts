import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { HeroCharacter, EnemyCharacter, BattleState } from '../types'

export const useGameStore = defineStore('game', () => {
    const hero = ref<HeroCharacter | null>(null)
    const enemy = ref<EnemyCharacter | null>(null)
    const round = ref(1)
    const battleCount = ref(1)
    const isPlayerTurn = ref(true)
    const battleLog = ref<string[]>([])

    function setHero(h: HeroCharacter) {
        hero.value = h
    }

    function setEnemy(e: EnemyCharacter) {
        enemy.value = e
        round.value = 1
    }

    function addLog(msg: string) {
        battleLog.value.push(msg)
    }

    function resetBattle() {
        round.value = 1
        battleLog.value = []
        isPlayerTurn.value = true
    }

    function nextBattle() {
        battleCount.value++
        resetBattle()
    }

    return {
        hero, enemy, round, battleCount,
        isPlayerTurn, battleLog,
        setHero, setEnemy, addLog, resetBattle, nextBattle
    }
})