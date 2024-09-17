package com.zufar.urlshortener.service

import com.zufar.urlshortener.common.entity.UrlMapping
import com.zufar.urlshortener.repository.UrlRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UrlShortener(
    private val urlRepository: UrlRepository,
    private val urlValidator: UrlValidator,
    private val urlCreator: UrlCreator
) {
    private val log = LoggerFactory.getLogger(UrlShortener::class.java)

    fun shortenUrl(originalUrl: String): Mono<String> {
        log.info("Received request to shorten originalURL='{}'", originalUrl)

        return Mono.fromCallable {
            urlValidator.validateUrl(originalUrl)
            log.debug("OriginalURL='{}' passed validation", originalUrl)
        }.flatMap {
            val encodedUrl = StringEncoder.encode(originalUrl)
            log.debug("OriginalURL='{}' was encoded to '{}'", originalUrl, encodedUrl)

            val urlMapping = UrlMapping(encodedUrl, originalUrl)

            urlRepository.save(urlMapping)
                .map { savedMapping ->
                    val shortUrl = savedMapping.shortUrl
                    val newShortURL = urlCreator.create(shortUrl)
                    log.info("New shortURL='{}' was created for originalURL='{}'", newShortURL, originalUrl)
                    newShortURL
                }
        }.doOnError { ex ->
            log.error("Failed to shorten originalURL='{}'", originalUrl, ex)
        }
    }
}
