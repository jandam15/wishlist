package dk.cngroup.wishlist.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.hibernate.annotations.Formula
import org.hibernate.annotations.Where
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RestResource

@Entity
@Where(clause = "active = true")  // all SELECT statements will be enhanced by where condition; cannot be inherited
class Client(
    var active: Boolean = true,
    var firstName: String,
    var lastName: String,
    @JsonManagedReference
    @OneToMany(mappedBy = "client", cascade = [CascadeType.ALL])
    @OrderColumn
    var wishes: MutableList<Wishlist> = mutableListOf(),

    @Transient
    var productCode: String? = null
) : AuditableEntity() {
    @Formula("upper(concat(first_name, '_', last_name))")
    val userName: String? = null

    fun addWishlist(wishlist: Wishlist) {
        wishes += wishlist
        wishlist.client = this
    }
}

interface ClientRepository : JpaRepository<Client, Long> {
    @RestResource(exported = false)
    fun getByUserName(userName: String): Client?

    @RestResource(exported = false)
    @EntityGraph(attributePaths = ["wishes"])
    fun findByUserName(userName: String): Client?

    @EntityGraph(attributePaths = ["wishes.products"])
    fun findClientByUserName(userName: String): Client

    fun findByWishesProductsCodeStartingWithIgnoreCaseOrderByLastName(code: String, pagination: Pageable): Page<Client>

    @Query(
        """
        SELECT DISTINCT c, p.code FROM Client c
        JOIN c.wishes w
        JOIN w.products p
        WHERE LOWER(p.code) LIKE LOWER(:code)
        ORDER BY c.lastName
    """
    )
    fun getClientsByProduct(@Param("code") code: String, pagination: Pageable): Page<Client>
}