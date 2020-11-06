package com.tm.rankme.domain.league

class League(var name: String) {
    var id: String? = null
        private set
    var settings: Settings = Settings()
        private set

    constructor(id: String, name: String) : this(name) {
        this.id = id
    }

    fun setAllowDraws(value: Boolean) {
        settings.allowDraws = value
    }

    fun setMaxScore(value: Int) {
        settings.maxScore = value
    }
}