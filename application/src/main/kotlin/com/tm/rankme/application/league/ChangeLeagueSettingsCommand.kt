package com.tm.rankme.application.league

import com.tm.rankme.application.cqrs.Command
import java.util.*

data class ChangeLeagueSettingsCommand(
    val id: UUID,
    val allowDraws: Boolean,
    val maxScore: Int
) : Command()