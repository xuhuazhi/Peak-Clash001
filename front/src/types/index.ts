export interface User {
    id: string
    username: string
    password: string
    status: boolean
}

export interface Skills {
    name: string
    damage: number
    MB: number
    description: string
}

export interface Character {
    name: string
    HP: number
    maxHP: number
    attack: number
    defense: number
    MB: number
    maxMB: number
    wins: number
    level: number
    skillsList: Skills[]
}

export interface HeroCharacter extends Character { }

export interface EnemyCharacter extends Character {
    defending: boolean
}

export interface BattleAction {
    type: 'normal' | 'skill1' | 'skill2' | 'item'
}

export interface BattleState {
    hero: HeroCharacter
    enemy: EnemyCharacter
    round: number
    battleCount: number
    isPlayerTurn: boolean
    log: string[]
}