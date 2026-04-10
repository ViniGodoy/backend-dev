package br.pucpr.authserver.users

import br.pucpr.authserver.roles.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository
) {
    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }
        val user = repository.save(user)
        log.info("Inserted new user {}", user.id)
        return user
    }

    fun findAll(dir: SortDir = SortDir.ASC) = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("name").descending())
    }

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)
    fun findById(id: Long) =
        repository.findByIdOrNull(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User $id not found")

    fun delete(id: Long) {
        val user = findById(id)
        if (user.isAdmin() && repository.findByRole("ADMIN").size == 1) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete the last admin")
        }
        repository.delete(user)
        log.info("User {} deleted", user.id)
    }

    fun findByRole(role: String) = repository.findByRole(role.uppercase())

    fun addRole(id: Long, roleName: String): Boolean {
        val upperRole = roleName.uppercase()
        val user = findById(id)
        val role = roleRepository.findByName(upperRole) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Role $upperRole not found"
        )

        user.roles.add(role)
        repository.save(user)
        log.info("Added role {} to user {}", upperRole, user.id)
        return true
    }

    fun update(id: Long, name: String): User? {
        val user = findById(id)
        if (user.name == name) {
            return null
        }
        user.name = name
        repository.save(user)
        return user
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}