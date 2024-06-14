package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

//classic Spring MVC controller
@RestController
class ProductController(private val repository: ProductRepository) {

    @GetMapping("/products/search/findByCode")
    fun getProductsByCode(
        @RequestParam(defaultValue = "") code: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "5") size: Int,
        @RequestParam(defaultValue = "id") sort: String
    ): ResponseEntity<out Any>? {
        val result = repository.findByCodeStartingWithIgnoreCase(
            code = code,
            pagination = PageRequest.of(
                page - 1,
                size,
                Sort.by(sort)
            )
        )
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with desired code not found")
        }
        return ResponseEntity.ok(result)
    }

    @GetMapping("/products/search/findByDescription")
    fun getProductsByDescription(
        @RequestParam(defaultValue = "") description: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "5") size: Int,
        @RequestParam(defaultValue = "id") sort: String
    ): ResponseEntity<out Any>? {
        val result = repository.findByDescriptionContaining(
            description = description,
            pagination = PageRequest.of(
                page - 1,
                size,
                Sort.by(sort)
            )
        )
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with desired description not found")
        }
        return ResponseEntity.ok(result)
    }


    @PostMapping("/product")
    fun saveProduct(@Validated @RequestBody product: Product): Product {
        return repository.save(product)
    }
}