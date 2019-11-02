package com.elorating.domain.league

class League(var name: String) {
    var id: String? = null

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