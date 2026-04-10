package br.pucpr.authserver.users

import br.pucpr.authserver.users.requests.CreateUserRequest
import br.pucpr.authserver.users.responses.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(val service: UserService) {
    @GetMapping
    fun list(
        @RequestParam sortDir: String? = null,
        @RequestParam role: String? = null
    ): ResponseEntity<List<UserResponse>> {
        val users = if (role != null) service.findByRole(role)
        else service.findAll(SortDir.find(sortDir ?: "ASC"))
        return users
            .map { UserResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    fun insert(
        @Valid @RequestBody user: CreateUserRequest
    ) = service.insert(user.toUser())
            .let { UserResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }


    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ) = service.findById(id)
        .let { UserResponse(it) }
        .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ) = service.delete(id)

    @PutMapping("/{id}/roles/{role}")
    fun grant(
        @PathVariable id: Long,
        @PathVariable role: String
    ): ResponseEntity<Void> =
        service.addRole(id, role)
            .let { if (it) ResponseEntity.ok().build() else ResponseEntity.noContent().build() }
}