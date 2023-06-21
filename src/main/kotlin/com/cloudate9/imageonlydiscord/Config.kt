package com.cloudate9.imageonlydiscord

import kotlinx.serialization.Serializable

@Serializable
data class Config(val botToken: String, val channelRegex: String, val rejectionMessage: String)