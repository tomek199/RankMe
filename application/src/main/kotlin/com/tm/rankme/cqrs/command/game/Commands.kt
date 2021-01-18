package com.tm.rankme.cqrs.command.game

import com.tm.rankme.cqrs.command.Command
import java.util.*

data class PlayGameCommand(
    val playerOneId: UUID,
    val playerTwoId: UUID,
    val playerOneScore: Int,
    val playerTwoScore: Int
) : Command()
