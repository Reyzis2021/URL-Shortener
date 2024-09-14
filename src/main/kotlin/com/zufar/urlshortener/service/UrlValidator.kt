package com.zufar.urlshortener.service

import org.springframework.stereotype.Service
import java.net.URI
import java.net.URL

@Service
class UrlValidator {

    private val allowedProtocols = setOf("http", "https")
    private val loopbackAddresses = setOf("localhost", "127.0.0.1", "::1", "short-link.zufargroup.com")

    fun validateUrl(url: String) {
        require(url.isNotEmpty() && url.isNotBlank()) {
            "URL must not be empty or blank"
        }

        require(!url.contains(" ")) {
            "URL must not contain spaces"
        }

        val parsedUrl: URL = URI(url).toURL()

        require(!parsedUrl.host.isNullOrBlank()) {
            "URL must have a valid host"
        }

        require(parsedUrl.protocol in allowedProtocols) {
            "URL must use HTTP or HTTPS protocol"
        }

        require(parsedUrl.host !in loopbackAddresses) {
            "URL cannot point to a loopback address"
        }

        require(url.length <= 2000) {
            "URL is too long"
        }
    }
}
