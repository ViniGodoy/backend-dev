package br.pucpr.authserver.users

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidSortDirException : IllegalArgumentException("Invalid sort dir")

enum class SortDir {
    ASC, DESC;

    companion object {
        fun findOrNull(sortDir: String) = entries.find { it.name == sortDir.uppercase() }
        fun find(sortDir: String) = SortDir.findOrNull(sortDir) ?: throw InvalidSortDirException()
    }
}