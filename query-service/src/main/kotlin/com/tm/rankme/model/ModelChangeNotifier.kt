package com.tm.rankme.model

interface ModelChangeNotifier {
    fun notify(type: String, model: Any)
}