package com.tm.rankme.api.query.player

import com.tm.rankme.api.query.Query
import java.util.*

data class GetPlayerQuery(val id: UUID) : Query()
