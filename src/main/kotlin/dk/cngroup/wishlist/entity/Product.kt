package dk.cngroup.wishlist.entity

import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.Description

@Entity
class Product(
    @Description("Unique name of the item")
    @field:NotNull
    var code: String,
    @field:Size(min = 1, max = 255)
    var description: String? = null
) : AuditableEntity()

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findByCodeStartingWithIgnoreCase(code: String): List<Product>
    fun findByDescriptionContaining(description: String): List<Product>
}