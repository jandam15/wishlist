package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

//classic Spring MVC controller
@RestController
class ProductController(private val repository: ProductRepository) {


    @GetMapping("/products/search/findByDescription")
    fun getProductsByDescription(
        @RequestParam description: String
    ): ResponseEntity<out Any>? {
        val result = repository.findByDescriptionContaining(description)
        if(result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with desired description not found")
        }
        return ResponseEntity.ok(result)
    }


    @PostMapping("/product")
    fun saveProduct(@Validated @RequestBody product: Product): Product {
        return repository.save(product)
    }
}