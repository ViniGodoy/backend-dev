package br.pucpr.authserver.roles

import br.pucpr.authserver.roles.requests.CreateRoleRequest
import br.pucpr.authserver.roles.responses.RoleResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roles")
class RoleController(val service: RoleService) {
    @PostMapping
    fun insert(
        @RequestBody @Valid role: CreateRoleRequest
    ) = service.insert(role.toRole())
        .let { RoleResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    fun list() = service.findAll()
        .map { RoleResponse(it) }
        .let { ResponseEntity.ok(it) }
}