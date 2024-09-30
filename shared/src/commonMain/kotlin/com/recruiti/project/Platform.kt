package com.recruiti.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform